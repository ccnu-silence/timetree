package com.yey.kindergaten.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.activity.ClassPhotoDetialManager;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.Upload;
import com.yey.kindergaten.db.UploadDB;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.upyun.UpYunException;
import com.yey.kindergaten.upyun.UpYunUtils;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.UpLoadManager;
import com.yey.kindergaten.util.UtilsLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

/**
 * Created by zy on 2015/6/9.
 */
public class UploadPhotosService extends Service {

    /** 保存上传数据对象 */
    private UploadDB uploadDB = UploadDB.getInstnce();
    /** 保存数据，在界面上传服务器 */
    public List<Upload> uploadList = new ArrayList<Upload>();
    /** 图片路径，用于上传 */
    private List<Photo> photos = new ArrayList<Photo>();
    /** 上传方式 普通或高清 */
    private String imageType;
    /** 相册id */
    private String albumid;
    /** 表示第一次上传还是再次上传 1：表示第一次上传，2：表示第二次上传 */
    private String type;

    public static final String First_Start_Action = "1";
    public static final String Retry_Start_Action = "2";
    private ExecutorService singleThreadExecutor;
    public static String TAG = "UploadPhotosService";

    public List<Upload> getUploadList() {
        return uploadList;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public void onCreate() {
        if (singleThreadExecutor == null) {
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        Log.i(TAG, "start UploadPhotosService");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        loadFailFlag = false;
        if (intent.getExtras()!=null) {
            UtilsLog.i(TAG, "start UploadThread");
            imageType = intent.getStringExtra(AppConstants.INTENT_IMAGE_TYPE);
            type = intent.getStringExtra("type");
            if (type!=null && type.equals(First_Start_Action)) {
                photos = intent.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
                // 开启线程上传
                uploadThread(photos,imageType);
            } else if (type!=null && type.equals(Retry_Start_Action)) {
                List<Upload> list = uploadDB.getFileList(AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                if (list!=null && list.size()!=0) {
//                    photos.clear();
                    for (Upload upload : list) {
                        Photo photo = new Photo();
                        photo.imgPath = upload.getUploadfilepath();
                        photos.add(photo);
                        albumid = upload.getParam();
                    }
                }
                uploadThread(photos,"retry");
            }
        }
        return new MyBinder();
    }

    /**
     * 线程池--单个线程启动
     * @param list
     */
    private void uploadThread(final List<Photo> list, final String imageType) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            final int index = i;
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (imageType != null && imageType.equals(AppConstants.COMMON_QUALITY_FOR_PHOTO)) { // 普通
                        sendRequest(index, FileUtils.saveAsCommon(list.get(index).imgPath, AppConfig.UPLOAD_PATH));
                        UtilsLog.i(TAG, "saveAsCommon UploadThread index = " + index);
                    } else if (imageType != null && imageType.equals(AppConstants.HD_QUALITY_FOR_PHOTO)) { // 高清
                        sendRequest(index, FileUtils.saveAsHd(AppContext.getInstance(), list.get(index).imgPath, AppConfig.UPLOAD_PATH));
                        UtilsLog.i(TAG, "saveAsHd UploadThread index = " + index);
                    } else {
                        // 重新上传图片
                        sendRequest(index, list.get(index).imgPath);
                    }
                }

            });
        }
    }

    public static boolean loadFailFlag = false;
    /**
     * 保存上传失败数据
     * @param url 图片上传路径
     */
    private void saveUploadToDB(String url) {
        UtilsLog.i(TAG, "保存失败图片 ： " + url);
        Upload upload = new Upload();
        upload.setUploadSize(0);
        upload.setUploadfilepath(url);
        upload.setFileId(uploadDB.getId());
        upload.setModule(AppConstants.PARAM_UPLOAD_CLASSPHOTO);
        upload.setCompress(imageType);
        upload.setParam(albumid);
        uploadDB.saveUpload(upload);
    }

    /**
     * 上传
     * @param photo_url
     */
    public void sendRequest(final int position, final String photo_url) {
        UtilsLog.i(TAG,"sendRequest postion = " + position + "and the url = " + photo_url);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configTimeout(60000);
        httpUtils.configSoTimeout(60000);
        RequestParams requestParams = null;
        try {
            requestParams = getRequestParams(photo_url);
        } catch (Exception e) {
            UpLoadManager.isupload = false;
            Message msg = new Message();
            msg.what = UpLoadManager.UPLOAD_FAILL;
            msg.arg1 = position;
            UpLoadManager.isupload = false;
            postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, msg, position);

            Upload upload = new Upload();
            upload.setUploadfilepath("");
            upload.setPosition(position);
            upload.setModule("Fail");
            uploadList.add(upload);
            UtilsLog.i(TAG, "Exception getRequestParams(photo_url), e : " + e.getCause() + e.getMessage());
            return;
        }
        String url = getUrl(UpYunUtils.ClASSPHOTO_BUCKET);

        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<Object>() {
            public void onStart() {
                UpLoadManager.isupload = true;
            }
            @Override
            public void onSuccess(ResponseInfo<Object> objectResponseInfo) {
                UtilsLog.i(TAG, "上传成功 ： position " + position);
                String url;
                String result = (String) objectResponseInfo.result;
                UtilsLog.i(TAG, "sendRequest postion = " + position + "onSuccess");
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    url = jsonObject.getString("url");
                    Upload upload = new Upload();
                    upload.setUploadfilepath(ClassPhotoDetialManager.UP_URL + url);
                    upload.setPosition(position);
                    upload.setModule("Success");
                    uploadList.add(upload);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UtilsLog.i(TAG, "delete photo_url = " + photo_url);
                FileUtils.deleteFile(photo_url);
                UtilsLog.i(TAG, "delete photo_url = " + photo_url + "  done");

                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_SUCCESS;
                msg.arg1 = position;
                if (photos.size() == position) { // AppContext.checkList.size() == position
                    UpLoadManager.isupload = false;
                }
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, msg, position);
                UtilsLog.i(TAG, "postEvent position = " + position + " msg.what: " + msg.what);
            }
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
//                UtilsLog.i(TAG, "上传中 ： position : " + position);
                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_UPDATE;
                msg.obj = current;
                msg.arg1 = position;
                UpLoadManager.isupload = true;
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, msg, position);
            }
            @Override
            public void onFailure(HttpException e, String s) {
                UtilsLog.i(TAG, "上传失败 ： position : " + position);
                UtilsLog.i(TAG, "sendRequest postion = " + position + "onFailure");
                UtilsLog.i(TAG, "Failure e:" + e.getMessage() + "cause:" + s);
                UpLoadManager.isupload = false;
                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_FAILL;
                msg.arg1 = position;
                UpLoadManager.isupload = false;
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, msg, position);

                Upload upload = new Upload();
                upload.setUploadfilepath("");
                upload.setPosition(position);
                upload.setModule("Fail");
                uploadList.add(upload);

                if (getUploadUrlByModule(photo_url)!=null && !getUploadUrlByModule(photo_url).equals("")) {
                    saveUploadToDB(getUploadUrlByModule(photo_url));
                }
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                loadFailFlag = true;
                UtilsLog.i(TAG, "上传取消！！！ ");
            }

        });

    }

    /**
     * 获取url
     * @param bucket
     * @return
     */
    private static String getUrl(String bucket) {
        String host = "http://v0.api.upyun.com/";
        return host + bucket + "/";
    }

    /**
     * 根据原路径获得压缩过的图片路径
     * @param path
     * @return
     */
   public String getUploadUrlByModule(String path) {
       if (imageType != null) {
           if (imageType.equals(AppConstants.COMMON_QUALITY_FOR_PHOTO)) {
               if (path != null && path.length() > 0 ) {
                   String baos = FileUtils.saveAsCommon(path, AppConfig.UPLOAD_PATH);
                   if (baos != null) {
                       return baos;
                   } else {
                       return "";
                   }
               }
           } else if (imageType.equals(AppConstants.HD_QUALITY_FOR_PHOTO)) {
               if (path != null && path.length() > 0) {
                   String baos = FileUtils.saveAsHd(AppContext.getInstance(), path, AppConfig.UPLOAD_PATH);
                   if (baos != null) {
                       return baos;
                   } else {
                       return "";
                   }
               }
           }
       }
       return "";
   }

    /**
     * 获取post提交参数
     * @param url
     * @return
     */
    public RequestParams getRequestParams(String url){
        RequestParams requestParams = new RequestParams();
        File file = new File(url);
        String SAVE_KEY = "/{year}/{mon}/{day}/{filemd5}{.suffix}";

        // 取得base64编码后的policy
        String policy = null;
        try {
            policy = UpYunUtils.makePolicy(SAVE_KEY, UpYunUtils.EXPIRATION, UpYunUtils.ClASSPHOTO_BUCKET);
        } catch (UpYunException e) {
            e.printStackTrace();
        }

        // 根据表单api签名密钥对policy进行签名
        String signature = UpYunUtils.signature(policy + "&" + UpYunUtils.CLASSPHOTO_API_KEY);
        requestParams.addBodyParameter(AppConstants.POLICY, policy);
        requestParams.addBodyParameter(AppConstants.SIGNATURE, signature);
        requestParams.addBodyParameter("file", file);
        return requestParams;
    }

    public class MyBinder extends Binder {
        public UploadPhotosService getService() {
            return UploadPhotosService.this;
        }
    }

    /**
     * 通知刷新
     * @param type
     * @param msg
     * @param position
     */
    public static void postEvent(final int type,final Message msg,final int position) {
        EventBus.getDefault().post(new AppEvent(type, msg, position));
    }

}
