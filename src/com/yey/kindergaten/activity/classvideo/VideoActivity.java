package com.yey.kindergaten.activity.classvideo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;


public class VideoActivity extends BaseActivity {
	/**视频控件*/
	private VideoView videoview;
	/**传过来的路径*/
	private String path;
	/**播放按钮*/
	private ImageView img_start;
	/**容器*/
	private RelativeLayout relative;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(getContentViewId());
        findViews();
        initGetData();
        init();
        widgetListener();
        super.onCreate(savedInstanceState);
    }

    protected int getContentViewId() {
		return R.layout.video;
	}

	protected void findViews() {
		videoview = (VideoView) findViewById(R.id.videoView);
		img_start = (ImageView) findViewById(R.id.img_start);
		relative = (RelativeLayout) findViewById(R.id.relative);
	}

	protected void initGetData() {
		if (getIntent().getExtras()!=null) {
			path = getIntent().getExtras().getString("path");
		}
	}

	protected void init() {
		videoview.setVideoPath(path);
		videoview.requestFocus();
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {


                return false;
            }
        });


	}



	protected void widgetListener() {
		relative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (videoview.isPlaying()) {
					videoview.pause();
					img_start.setVisibility(View.VISIBLE);
				}else{
					videoview.start();
					img_start.setVisibility(View.GONE);
				}
			}
		});
	}
}
