package com.yey.kindergaten.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.bokecc.sdk.mobile.exception.DreamwinException;
import com.bokecc.sdk.mobile.upload.UploadListener;
import com.bokecc.sdk.mobile.upload.Uploader;
import com.bokecc.sdk.mobile.upload.VideoInfo;
import com.yey.kindergaten.activity.classvideo.ShowLocalVideoActivity;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.UtilsLog;

/**
 * Created by zy on 2015/7/15.
 */
public class UploadVideoService extends Service {



    /**
     * cc视频上传
     */
    private VideoInfo videoInfo;
    private Uploader uploader;
    private String TAG = "UploadVideoService";


    private boolean stop;
    private int progress;



    private UploadBinder binder = new UploadBinder();

    public class UploadBinder extends Binder {


        public void upload() {
            if (uploader == null) {
                return;
            } else if (uploader.getStatus() == Uploader.WAIT) {
                uploader.start();
            } else if (uploader.getStatus() == Uploader.PAUSE) {
                uploader.resume();
            }
        }

        public void pause() {
            if (uploader == null) {
                return;
            }
            uploader.pause();
        }

        public void cancle() {
            if (uploader == null) {
                return;
            }
            uploader.cancel();
            stop = true;
        }

        public boolean isStop(){
            return stop;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {

        if(intent == null){
            return binder;
        }
        videoInfo = new VideoInfo();
        videoInfo.setTitle(intent.getStringExtra("title"));
        videoInfo.setTags(intent.getStringExtra("tag"));
        videoInfo.setDescription(intent.getStringExtra("desc"));
        videoInfo.setFilePath(intent.getStringExtra("filePath"));
        videoInfo.setUserId(AppConstants.CC_USER_ID);
        //视频回调地址，暂时填写默认
        videoInfo.setNotifyUrl("http://www.example.com");
        uploader = new Uploader(videoInfo, AppConstants.CC_API_KEY);
        uploader.setUploadListener(uploadListener);
//        uploader.start();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//
//    }


    /**
     * 上传过程处理回调
     */
    public UploadListener uploadListener = new UploadListener() {

        Intent broadCastIntent = new Intent(AppConstants.UPLOAD_VIDEO_ACTION);
        @Override
        public void handleProcess(long range, long size, String s) {

            /**
             * 这个回调显示的p有问题，会重复
             */
            int p = (int) ((double) range / size * 100);
            if (progress != p||p==0) {
                progress = p;
                broadCastIntent.putExtra("status", ShowLocalVideoActivity.uploading_what);
                broadCastIntent.putExtra("progress",progress);
//                UtilsLog.i(TAG,"progress  is:"+ progress);
                sendBroadcast(broadCastIntent);
            }

        }

        //处理异常情况
        @Override
        public void handleException(DreamwinException e, int i) {

            broadCastIntent.putExtra("status", ShowLocalVideoActivity.exception_what);
            sendBroadcast(broadCastIntent);

        }

        //处理上传状态
        @Override
        public void handleStatus(VideoInfo videoInfo, int i) {

            if(i == Uploader.FINISH){

                UtilsLog.i(TAG,"Uploader.FINISH");
                broadCastIntent.putExtra("status", ShowLocalVideoActivity.finish_what);
                sendBroadcast(broadCastIntent);

            }else if(i == Uploader.UPLOAD){

                UtilsLog.i(TAG,"Uploader.UPLOAD");
                broadCastIntent.putExtra("status", ShowLocalVideoActivity.start_what);
                sendBroadcast(broadCastIntent);

            }

        }

        //处理取消操作
        @Override
        public void handleCancel(String s) {

        }
    };

}
