package com.yey.kindergaten.util;

import android.os.Handler;
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
import com.yey.kindergaten.bean.Upload;
import com.yey.kindergaten.db.UploadDB;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.task.TaskExecutor;
import com.yey.kindergaten.upyun.UpYunException;
import com.yey.kindergaten.upyun.UpYunUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.greenrobot.event.EventBus;



/**
 * 上传的线程
 * 
 * @author chaowen
 * 
 */
public class UploadThread{

    public static  final List<String> new_photos = new ArrayList<String>();

    private final static String TAG = "UploadThread";
	     // 上传失败状态
			public final static int UPLOAD_FAILL = -1;
			// 上传成功状态
			public final static int UPLOAD_SUCCESS = 1;
			// 上传更新状态
			public final static int UPLOAD_UPDATE = 2;
			// 上传暂停
			public final static int UPLOAD_PAUSE = 3;
			public final static boolean network = true;
	private Socket socket;

	private File file;

	public final static int BUFFER = 1024;

	private UploadDB dbManager;

	private AppContext appcontext;

	public  boolean start = true;

	private Handler handler;

	private String savePath;
    private String param = null;
    private int position = 0;
    private Boolean  isupload=false;
	public UploadThread(AppContext appcontext, UploadDB dbManager,
			Handler handler, File file, String savePath,String param,int position) {
		this.appcontext = appcontext;
		this.dbManager = dbManager;
		this.file = file;
		this.handler = handler;
		this.savePath = savePath;
		this.param = param;
		this.position = position;
	}
	
	

	public UploadThread() {
		super();
		
	}



	/*
	 * 服务器提出上传请求
	 */
	private String request(String sourceid) throws IOException {

		DataInputStream in = new DataInputStream(socket.getInputStream());

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		String params = "length=" + file.length() + ";filename="
				+ file.getName() + ";sourceid=" + sourceid + ";filePath="
				+ savePath;
		;

		// 发出上传请求
		out.writeUTF(params);

		out.flush();

		// 返回文件字符
		return in.readUTF();

	}
	  private static final int TIME_OUT = 10 * 100000000; // 超时时间
	  private static final String CHARSET = "utf-8"; // 设置编码
	  private HttpURLConnection conn = null;
	  private long tempSize = 0;

    long expiration = Calendar.getInstance().getTimeInMillis() + 60*1000; // 60s


	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start,Long id) {
		this.start = start;
		//保存中断文件到数据库
		if(start==false){
			dbManager.updateUpload(id, tempSize);
		}
		
		EventBus.getDefault().post(new AppEvent(AppEvent.UPLOAD_PAUSE));
		//postEvent(new AppEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING));
	}
	
	public boolean getStart(){
		System.out.println(this.start);
		return this.start;
	}
	

	public void setFile(File file) {
		this.file = file;
	}

	public void setDbManager(UploadDB dbManager) {
		this.dbManager = dbManager;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public void setParam(String param) {
		this.param = param;
	}
	
	public void upload(int uploadposition,Upload upload,String param,String type,final TaskExecutor.UpPhotoCallback upPhotoCallback){
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String RequestURL=null;
        if(type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)){
            RequestURL = "http://v0.api.upyun.com/";
           // RequestURL = "http://192.168.0.138:8077/class/UploadClassPhotoStream.ashx?";
        }else{
            RequestURL = AppServer.getInstance().getAccountInfo().getUploadurl()+"/hb/UploadHBPhotoLifeWork.ashx";
           // RequestURL = "http://192.168.0.138:8077//hb/UploadHBPhotoLifeWork.ashx";
        }
        if(upload.getMap()!=null){
            upload(uploadposition,upload,upload.getMap(),type,upPhotoCallback);
        }else{
            try {
                URL url = new URL(RequestURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoInput(true); // 允许输入流
                conn.setDoOutput(true); // 允许输出流
                conn.setUseCaches(false); // 不允许使用缓存
                conn.setRequestMethod("PUT"); // 请求方式


                // 设置时间
//                conn.setRequestProperty(UpYun.DATE, TimeUtil.getGMTDate());
                // 设置签名
//                conn.setRequestProperty(UpYun.AUTHORIZATION,
//                        UpYun.sign(conn, upload.getUploadfilepath(), is.available()));
                conn.setChunkedStreamingMode(4096*2);//上传内容过大，选择分块上传
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                String uploadPath =null;


                long pos = dbManager.getFileCurrentSize(upload.getSourcepath());
                file = new File(upload.getUploadfilepath());
                tempSize = pos;
                int totalSize = param.length();
                dos.write(StringUtils.intToBytes(totalSize));
                dos.write(param.getBytes());
                RandomAccessFile fileOutStream = new RandomAccessFile(file, "r");

                // 移动到断点处继续读取
                fileOutStream.seek(Long.valueOf(pos));

                byte[] buffer = new byte[BUFFER];

                int len = -1;

                long length = Long.valueOf(pos);
                System.out.println("start:"+start);
                Log.v("UploadThread", start+"");
                while (getStart()&& (len = fileOutStream.read(buffer)) != -1) {
                    dos.write(buffer, 0, len);
                    dos.flush();
                    length += len;
                    tempSize = length;
                    Message msg = new Message();
                    msg.what = UpLoadManager.UPLOAD_UPDATE;
                    msg.obj = length;
                    msg.arg1 = uploadposition;
                    postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,uploadposition);
                }
                InputStream stream = conn.getInputStream();
                int ch;
                StringBuilder b = new StringBuilder();
                while( (ch = stream.read()) != -1 ){
                    b.append((char)ch);}
                String res=b.toString();
                System.out.println("resresresresresresresresresresres----->"+res);
                JSONObject jObj = new JSONObject(res);
                int code = Integer.valueOf(jObj.getString(AppServer.TAG_CODE));
                if(code == AppServer.REQUEST_SUCCESS){
                    if (file.length() == length) {
                        Message msg = new Message();
                        msg.what = UpLoadManager.UPLOAD_SUCCESS;
                        msg.obj = length;
                        msg.arg1 = uploadposition;
                        postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,uploadposition);
                        System.out.println(file.getAbsolutePath()+",length="+length);
                    }
                }else if(code ==-1){
                    Message msg = new Message();
                    msg.what = UpLoadManager.UPLOAD_FAILL;
                    msg.obj = tempSize;
                    msg.arg1 = uploadposition;
                    //handler.sendMessage(msg);
                    postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,uploadposition);
                }

                stream.close();
                fileOutStream.close();
                dos.close();
            } catch (Exception e) {
                this.start = false;
                UpLoadManager.isupload = false;
                upload.setParam(param);
                dbManager.updateUploadParam(upload);
                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_FAILL;
                msg.obj = tempSize;
                msg.arg1 = uploadposition;
                //handler.sendMessage(msg);
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,uploadposition);
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();

                }
            }
        }

	}


    /**
     * 上传
     * @param upload
     */
    public void sendRequest(final int position,Upload upload){

        HttpUtils httpUtils=new HttpUtils();
        RequestParams requestParams = getRequestParams(upload);
        String url = getUrl(UpYunUtils.ClASSPHOTO_BUCKET);

        if(position == 0){
            new_photos.clear();
        }

        httpUtils.send(HttpRequest.HttpMethod.POST,url ,requestParams,new RequestCallBack<Object>() {
            public void onStart() {
                UpLoadManager.isupload = true;
            }

            @Override
            public void onSuccess(ResponseInfo<Object> objectResponseInfo) {
                String url;
                String result = (String) objectResponseInfo.result;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    url = jsonObject.getString("url");
                    new_photos.add(ClassPhotoDetialManager.UP_URL+url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_SUCCESS;
                msg.arg1 = position;
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, msg, position);

            }
            @Override
            public void onLoading(long total, long current, boolean isUploading) {

                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_UPDATE;
                msg.obj = current;
                msg.arg1 = position;
                UpLoadManager.isupload = true;
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,position);
            }

            @Override
            public void onFailure(HttpException e, String s) {

                UtilsLog.i(TAG,"faild cause by:"+e.getMessage()+"   result: "+s);
                UpLoadManager.isupload = false;
                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_FAILL;
                msg.obj = tempSize;
                msg.arg1 = position;
                UpLoadManager.isupload = false;
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,position);
            }
        });

    }


    /**
     * 班级相册又拍云使用
     * @param position
     * @param upload
     */
     public void  upload(final int position,Upload upload){
         sendRequest(position,upload);
     }

    /**
     * 旧版使用上传图片
     * @param position
     * @param upload
     * @param map
     * @param type
     * @param upPhotoCallback
     */
    public void upload(final int position,Upload upload,Map<String,String> map,String type,final TaskExecutor.UpPhotoCallback upPhotoCallback){

        String  RequestURL = AppServer.getInstance().getAccountInfo().getUploadurl()+"/hb/UploadHBPhotoLifeWorkFile.ashx?";
//      RequestURL = "http://192.168.0.138:8077/hb/UploadHBPhotoLifeWorkFile.ashx?";

        RequestParams requestParams = new RequestParams();
        for (Map.Entry<String, String> entry : map.entrySet()) {//构建表单字段内容
            requestParams.addBodyParameter(entry.getKey().toString(), entry.getValue().toString());
        }
        requestParams.addBodyParameter("file", new File(upload.getUploadfilepath()));
        HttpUtils httpUtils=new HttpUtils();
        httpUtils.configSoTimeout(TIME_OUT);
        httpUtils.send(HttpRequest.HttpMethod.POST,RequestURL ,requestParams,new RequestCallBack<Object>() {
            public void onStart() {
                UpLoadManager.isupload = true;
            }

            @Override
            public void onSuccess(ResponseInfo<Object> objectResponseInfo) {
                if(upPhotoCallback!=null){
                    upPhotoCallback.upphotoback(position+"");

                }

                String result = (String) objectResponseInfo.result;

                if(result.contains("-2")){
                    Message msg = new Message();
                    msg.what = UpLoadManager.UPLOAD_FAILL;
                    msg.arg1 = position;
                    postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,position);
                }else if(result.contains("0")){
                    Message msg = new Message();
                    msg.what = UpLoadManager.UPLOAD_SUCCESS;
                    msg.arg1 = position;
                    postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,position);
                }else{
                    Message msg = new Message();
                    msg.what = UpLoadManager.UPLOAD_FAILL;
                    msg.arg1 = position;
                    postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,position);
                }

            }
            @Override
            public void onLoading(long total, long current, boolean isUploading) {

                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_UPDATE;
                msg.obj = current;
                msg.arg1 = position;
                UpLoadManager.isupload = true;
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,position);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if(upPhotoCallback!=null){
                    upPhotoCallback.upphotoback("success");
                }
                System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzz"+position);
                UpLoadManager.isupload = false;
                Message msg = new Message();
                msg.what = UpLoadManager.UPLOAD_FAILL;
                msg.obj = tempSize;
                msg.arg1 = position;
                UpLoadManager.isupload = false;
                postEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING,msg,position);
            }
        });

    }

    private String getUrl(String bucket) {
         String host = "http://v0.api.upyun.com/";
        return host + bucket + "/";
    }


    /**
     * 获取post提交参数
     * @param upload
     * @return
     */
    public RequestParams getRequestParams(Upload upload){
        RequestParams requestParams = new RequestParams();
        File file = new File(upload.getUploadfilepath());
        String SAVE_KEY = "/{year}/{mon}/{day}/{filemd5}{.suffix}";

        //取得base64编码后的policy
        String policy = null;
        try {
            policy = UpYunUtils.makePolicy(SAVE_KEY, UpYunUtils.EXPIRATION, UpYunUtils.ClASSPHOTO_BUCKET);
        } catch (UpYunException e) {
            e.printStackTrace();
        }
        //根据表单api签名密钥对policy进行签名

        String signature = UpYunUtils.signature(policy + "&" + UpYunUtils.CLASSPHOTO_API_KEY);
        requestParams.addBodyParameter(AppConstants.POLICY,policy);
        requestParams.addBodyParameter(AppConstants.SIGNATURE,signature);
        requestParams.addBodyParameter("file", file);
        return requestParams;
    }
	
	public void postEvent(final int type,final Message msg,final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type, msg, position));

               }
             }).start();
    }

	public void setPosition(int position) {
		this.position = position;
	}
	
	
}
