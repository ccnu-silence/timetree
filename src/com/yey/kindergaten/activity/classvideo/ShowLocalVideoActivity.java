package com.yey.kindergaten.activity.classvideo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bokecc.sdk.mobile.upload.Uploader;
import com.bokecc.sdk.mobile.upload.VideoInfo;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.PreMediaPlayerActivity;
import com.yey.kindergaten.adapter.ClassVideoAdapter;
import com.yey.kindergaten.bean.ClassVideo;
import com.yey.kindergaten.huanxin.vedio.util.AsyncTask;
import com.yey.kindergaten.service.UploadVideoService;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.xlist.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2015/7/14.
 * 已作废
 */
public class ShowLocalVideoActivity extends BaseActivity {

    public static final int start_what = 100;//开始下载
    public static final int uploading_what = 200;
    public static final int finish_what = 300;
    public static final int exception_what = 400;

    private List<ClassVideo> videos = new ArrayList<ClassVideo>();
    private ClassVideoAdapter adapter;

    private TextView header_tv;
    private String TAG = "ShowLocalVideoActivity";
    private boolean isUploading = false;
    private int progress = 0;

    /**
     * cc视频上传
     */
    private VideoInfo videoInfo;
    private Uploader uploader;

    boolean isClick = false;
    private ProgressBar progressBar;//显示上传进度
    private TextView progressTv;//显示上传百分比
    private DialogTips dialog;

    private ServiceConnection serviceConnection;

    private UploadVideoService.UploadBinder binder;

   private XListView messageLv = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        messageLv = (XListView)findViewById(R.id.home_Message_lv);

        messageLv.setPullLoadEnable(false);

        messageLv.setPullRefreshEnable(false);

        messageLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                Uri data = Uri.parse(videos.get(i-1).getPath());
//                intent.setDataAndType(data, "video/mp4");
//                startActivity(intent);

                Intent intent = new Intent(ShowLocalVideoActivity.this, PreMediaPlayerActivity.class);
                intent.putExtra("videoId",videos.get(i-1).getPath());
                intent.putExtra("isLocalPlay",true);
                startActivity(intent);
            }
        });
        initTitleText();

        IntentFilter filter = new IntentFilter(AppConstants.UPLOAD_VIDEO_ACTION);
        filter.setPriority(5);
        registerReceiver(broadcastReceiver,filter);

        new VideoAsyncTask(this).execute();
    }





    private void initTitleText() {
        header_tv = (TextView)findViewById(R.id.header_title);
        header_tv.setText("本地视频");
        header_tv.setVisibility(View.VISIBLE);
    }


    /**
     * 查询视频异步线程
     */
    class VideoAsyncTask extends AsyncTask<Void,Void,Void> implements ClassVideoAdapter.onClickListener {

        private  Context context;

        private Cursor videoCursor = null;

        VideoAsyncTask(Context context) {

            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            CursorLoader loader;

            String[] columns = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.TITLE,
                                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATA,
                                MediaStore.Video.Media.SIZE};

            String orderBy = MediaStore.Video.Media.DATE_ADDED;

            loader = new CursorLoader(context,  MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        columns,null, null, orderBy+" DESC");

            videoCursor = loader.loadInBackground();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(videoCursor==null){return null;}

             videoCursor.moveToPosition(-1);
             while (videoCursor.moveToNext()){

               int _id =  videoCursor.getInt(videoCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

               String date = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

               String title = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));

               String content = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));

               long duration = videoCursor.getLong(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

               String path = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

               long size = videoCursor.getLong(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

               ClassVideo video = new ClassVideo(_id,size,path,duration,content,title,date);

               videos.add(video);

             }

            return null;
        }



        @Override
        protected void onPostExecute(Void result) {

            if(videos==null)return ;
              adapter = new ClassVideoAdapter(ShowLocalVideoActivity.this, videos);
              adapter.setOnClickListener(this);
              messageLv.setAdapter(adapter);
              videoCursor.close();

            super.onPostExecute(result);
        }



        @Override
        public void onClick(View view,ClassVideo video) {
            showPreUploadDialog(video);
        }


    }


    /**
     * 点击列表上传按钮显示对话框，完善视频信息
     * @param video
     */
    private void showPreUploadDialog(final ClassVideo video){

        DialogTips dialog = new DialogTips(this,"视频信息","", "开始上传",false,true);

        View view = LayoutInflater.from(this).inflate(R.layout.classvideo_upload_inflater,null);

        final EditText titile_et = (EditText) view.findViewById(R.id.title_tv);
        titile_et.setText(video.getContent());
        titile_et.setSelection(video.getContent().length());

        final EditText type_et = (EditText) view.findViewById(R.id.type_et);
        final EditText jianjie_et = (EditText) view.findViewById(R.id.jianjie_et);

        dialog.setView(view);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                isClick = true;
                Intent intent = new Intent(ShowLocalVideoActivity.this, UploadVideoService.class);
                intent.putExtra("title", titile_et.getText().toString() + "");
                intent.putExtra("tag", type_et.getText().toString() + "");
                intent.putExtra("desc", jianjie_et.getText().toString() + "");
                intent.putExtra("filePath", video.getPath());
//                startService(intent);
                binderService(intent);
                if(binder!=null){
                    binder.upload();
                }
                UtilsLog.i(TAG,"click to start upload");
                dialogInterface.dismiss();}

        });
        dialog.show();
    }



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            abortBroadcast();
            if (intent == null)return;

            int status = intent.getIntExtra("status",0);
            int progress = intent.getIntExtra("progress",0);


            if(status == start_what){
                UtilsLog.i(TAG,"broadcastReceiver : "+" status is:" + status);
                showUploadingDialog();
                progressTv.setText("0%");
            }else if(status == uploading_what){
                progressBar.setProgress(progress);
                UtilsLog.i(TAG,"broadcastReceiver : "+" progress is:" +progress);
                if(progress<100){
                    progressTv.setText(progress+"%");
                }else {
                    progressTv.setText("处理中...");
                    isClick = false;
                }
            }else if(status == finish_what){
                UtilsLog.i(TAG,"broadcastReceiver : "+" status is:" + status);
                dialog.dismiss();
                binder.cancle();
                showToast("上传完成");
            }else if(status == exception_what){
                UtilsLog.i(TAG,"broadcastReceiver : Exception");
                dialog.dismiss();
                binder.cancle();
                showToast("错误");
            }
        }
    };


    private void binderService(Intent intent) {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (UploadVideoService.UploadBinder) service;
            }
        };

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }



    /**
     * 显示上传进度对话框
     */
    public  void showUploadingDialog(){
      if(dialog == null){
        dialog  = new DialogTips(this,"","", "取消上传",false,false);
        View view = LayoutInflater.from(this).inflate(R.layout.progress_bar_horizontal,null);

        ImageView close_iv = (ImageView) view.findViewById(R.id.close_iv);
        progressBar = (ProgressBar) view.findViewById(R.id.show_progress_pb);
        progressTv = (TextView) view.findViewById(R.id.show_progress_tv);

        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isUploading = false;
                        progress = 0;
                        binder.cancle();
                        showToast("任务已取消,上传中断");
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isUploading = false;
                        progress = 0;
                        binder.cancle();
                        showToast("任务已取消,上传中断");
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.setView(view);
        dialog.show();}
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        if(serviceConnection!=null)
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
