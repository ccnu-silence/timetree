package com.yey.kindergaten.activity.classvideo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.sdk.mobile.upload.Uploader;
import com.bokecc.sdk.mobile.upload.VideoInfo;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;

/**
 * 设置视频信息界面
 */
public class InputInfoActivity extends Activity implements OnClickListener{
	
	private String filePath = "/storage/sdcard0/1.MP4";

    private EditText titile_et;
    private EditText type_et;
    private EditText jianjie_et;

    private Button uploadButton;

    private TextView title_tv;

    private UploadService.UploadBinder binder;
	private Intent service;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (UploadService.UploadBinder) service;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.upload_info_activity);
        initView();

        service = new Intent(this, UploadService.class);
        bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
		
        String path = getIntent().getStringExtra("filePath");
        if (path != null) {
        	filePath = path;
		}
	}


    private void initView(){

        titile_et    = (EditText) findViewById(R.id.title_tv);

        type_et = (EditText)findViewById(R.id.type_et);

        jianjie_et = (EditText)findViewById(R.id.jianjie_et);

        uploadButton = (Button) findViewById(R.id.inflater_to_upload);

        uploadButton.setOnClickListener(this);

        title_tv = (TextView) findViewById(R.id.header_title);
        title_tv.setText("完善视频信息");
    }


    @Override
	protected void onDestroy() {
		
		unbindService(serviceConnection);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		String title = titile_et.getText().toString();
		if (title == null || "".equals(title.trim())) {
			Toast.makeText(getApplicationContext(), "请填写视频标题", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String uploadId = UploadInfo.UPLOAD_PRE.concat(System.currentTimeMillis() + "");
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.setTitle(title);//视频标题
		videoInfo.setTags(type_et.getText().toString());
		videoInfo.setDescription(jianjie_et.getText().toString());
		videoInfo.setFilePath(filePath);
		
		DataSet.addUploadInfo(new UploadInfo(uploadId, videoInfo, Uploader.WAIT, 0, null));
		sendBroadcast(new Intent(AppConstants.ACTION_UPLOAD));

		if (binder.isStop()) {
			Intent service = new Intent(getApplicationContext(), UploadService.class);
			service.putExtra("title", titile_et.getText().toString());
			service.putExtra("tag", type_et.getText().toString());
			service.putExtra("desc", jianjie_et.getText().toString());
			service.putExtra("filePath", filePath);
			service.putExtra("uploadId", uploadId);
			
			startService(service);
		}
		
		finish();
	}

}
