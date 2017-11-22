package com.yey.kindergaten.activity.classvideo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.PreMediaPlayerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 录制视频类
 */
public class VideoNewActivity extends BaseActivity implements SurfaceHolder.Callback {

	/** 视频最大支持15秒 */
	public static final int VIDEO_TIME_END = 15;
	/** 视频最少必须5秒 */
	public static final int VIDEO_TIME = 5;
	/** 最少得录制多少秒 */
	private ImageView img_at_last;
	/** 闪现光标图片 */
	private ImageView img_shan;
	/** 删除录制 */
	private ImageView img_delete;
	/** 开始录制 */
	private ImageButton img_start;
	/** 确认 */
	private ImageView img_enter;

	/** 选择录像 */
	private ImageView img_video;

	/** 计时器 */
	private TimeCount timeCount;
	/** 录制了多少秒 */
	private int now;
	/** 每次录制结束时是多少秒 */
	private int old;

	/** 录制进度控件 */
	private LinearLayout linear_seekbar;
	/** 屏幕宽度 */
	private int width;
	/** 偶数才执行 */
	private int even;
	/** 是否点击删除了一次 */
	private boolean isOnclick = false;
	/** 录制视频集合 */
	private ArrayList<VideoNewBean> list;
	/** 录制bean */
	private VideoNewBean bean;
	/** 为了能保存到bundler 录制bean */
	private VideoNewParentBean parent_bean;
	/** 录制视频保存文件 */
	private String vedioPath;
	/** 合并之后的视频文件 */
	private String videoPath_merge;
	/** 是否满足视频的最少播放时长 */
	private boolean isMeet = false;

	/** 录制视频的类 */
	private MediaRecorder mMediaRecorder;
	/** 摄像头对象 */
	private Camera mCamera;
	/** 显示的view */
	private SurfaceView surfaceView;
	/** 摄像头参数 */
	private Parameters mParameters;
	// /** 视频输出质量 */
	private CamcorderProfile mProfile;
	/** 文本属性获取器 */
	private SharedPreferences mPreferences;
	/** 刷新界面的回调 */
	private SurfaceHolder mHolder;
	/** 1表示后置，0表示前置 */
	private int cameraPosition = 1;
	/** 路径 */
	private String Ppath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videoTest/";


	
	private boolean isRecoding = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_video_new);
        findViews();
        init();
        widgetListener();
        super.onCreate(savedInstanceState);
    }


	protected void findViews() {

		surfaceView = (SurfaceView) findViewById(R.id.video_new_surfaceview);
		img_at_last = (ImageView) findViewById(R.id.video_new_img_time_atlast);
		img_shan = (ImageView) findViewById(R.id.video_new_img_time_start);
		img_delete = (ImageView) findViewById(R.id.video_new_img_delete);
		img_start = (ImageButton) findViewById(R.id.video_new_img_start);
		img_enter = (ImageView) findViewById(R.id.video_new_img_enter);
		img_video = (ImageView) findViewById(R.id.video_new_img_video);
		linear_seekbar = (LinearLayout) findViewById(R.id.video_new_seekbar);

		width = getWindowManager().getDefaultDisplay().getWidth();

		LayoutParams layoutParam = (LayoutParams) surfaceView.getLayoutParams();
		// 高：宽 4 : 3
		layoutParam.height = width / 3 * 4;
		// 隐藏多少dp才能让屏幕显示正常像素
		layoutParam.topMargin = -(width / 3 * 4 - width - DisplayUtil.dip2px(VideoNewActivity.this, 44));
		surfaceView.setLayoutParams(layoutParam);

		LayoutParams layoutParams = (LayoutParams) img_at_last.getLayoutParams();
		layoutParams.leftMargin = width / VIDEO_TIME_END * VIDEO_TIME;
		img_at_last.setLayoutParams(layoutParams);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		parent_bean.setList(list);
		outState.putSerializable("parent_bean", parent_bean);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		parent_bean = (VideoNewParentBean) savedInstanceState.getSerializable("parent_bean");
		list = parent_bean.getList();

		super.onRestoreInstanceState(savedInstanceState);
	}


	protected void init() {
		handler.postDelayed(runnable, 0);
		even = 0;
		old = 0;
		// 创建文件夹
		File file = new File(Ppath);
		if (!file.exists()) {
			file.mkdir();
		}
		list = new ArrayList<VideoNewBean>();
		parent_bean = new VideoNewParentBean();
		// 安装一个SurfaceHolder.Callback
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		// 针对低于3.0的Android
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		readVideoPreferences();

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 获取Camera实例
		mCamera = getCamera();
		if (mCamera != null) {
			// 因为android不支持竖屏录制，所以需要顺时针转90度，让其游览器显示正常
			mCamera.setDisplayOrientation(90);
			mCamera.lock();
			initCameraParameters();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	/**
	 * 获取摄像头实例
	 *
	 * @return
	 */
	private Camera getCamera() {
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			camera = null;
		}
		return camera;
	}

	private Handler handler = new Handler();

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (img_shan.isShown()) {
				img_shan.setVisibility(View.GONE);
			} else {
				img_shan.setVisibility(View.VISIBLE);
			}
			handler.postDelayed(runnable, 500);
		}
	};

	protected void widgetListener() {
		img_start.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

		           if (isOnclick) {
						(linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 2)).setBackgroundColor(
                                getResources().getColor(R.color.ff1f8fe4));
					}
			
					even = 1;
					if(!isRecoding){
				    isRecoding = true;
                    img_start.setImageResource(R.drawable.video_recorder_stop_btn);
					img_delete.setVisibility(View.VISIBLE);
					img_enter.setVisibility(View.VISIBLE);
					img_video.setVisibility(View.GONE);


					addView_Red();

					// 构造CountDownTimer对象
					timeCount = new TimeCount(30000 - old, 50);
					timeCount.start();// 开始计时

					startRecord();
		   		 }else{
		   			    isRecoding = false;
						old = now + old;

						if (old >= VIDEO_TIME * 1000) {
							isMeet = true;
						}

						timeCount.cancel();

						addView_black();
                        img_start.setImageResource(R.drawable.video_recorder_start_btn);
						stopRecord();
					}
					break;
				}
				return false;
			}
		});
		/** 删除按钮 */
		img_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isOnclick = false;
				if (even % 2 == 0) {
					if (linear_seekbar.getChildCount() > 1) {
						linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
						linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
					}
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							File file = new File(list.get(list.size() - 1).getPath());
							if (file.exists()) {
								file.delete();
							}
						}
						old -= list.get(list.size() - 1).getTime();
						list.remove(list.size() - 1);
						if (old < VIDEO_TIME * 1000) {
							isMeet = false;
						}
						if (list.size() <= 0) {
							img_delete.setVisibility(View.GONE);
							img_enter.setVisibility(View.GONE);
							img_video.setVisibility(View.VISIBLE);
						}
					}
				} else {
					if (linear_seekbar.getChildCount() > 1) {
						isOnclick = true;
						( linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 2)).setBackgroundColor(getResources().getColor(
								R.color.ff135689));
					}
				}
				even++;
			}
		});



		/** 确认按钮 */
		img_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isMeet) {

					int size = list.size();
					String[] strs = new String[size];
					videoPath_merge = Ppath + System.currentTimeMillis() + ".mp4";
					for (int i = 0; i < size; i++) {
						strs[i] = list.get(i).getPath();
					}
					try {
						VideoUtils.appendVideo(strs, videoPath_merge);

						for (int i = size - 1; i >= 0; i--) {
							File file = new File(list.get(i).getPath());
							if (file.exists()) {
								file.delete();
							}
							list.remove(i);
						}

//                        Intent intent = new Intent();
//                        intent.setAction(Intent.ACTION_VIEW);
//                        Uri data = Uri.parse(videoPath_merge);
//                        intent.setDataAndType(data, "video/mp4");
//                        startActivity(intent);

                        Intent intent = new Intent(VideoNewActivity.this, PreMediaPlayerActivity.class);
                        intent.putExtra("videoId",videoPath_merge);
                        intent.putExtra("isLocalPlay",true);
                        startActivity(intent);

//						Intent it = new Intent(VideoNewActivity.this,VideoActivity.class);
//						it.putExtra("path", videoPath_merge);
//						startActivity(it);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(VideoNewActivity.this, "视频最少必须录制5秒以上才能用！", Toast.LENGTH_LONG).show();
				}
			}
		});


		surfaceView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
 				if (mParameters != null && mCamera != null) {
					mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					try {
						mCamera.setParameters(mParameters);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
				if (list.size() > 0) {
					exitVideoNewDialog();
				} else {
					releaseCamera();
					finish();
				}
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}




	/**
	 * 弹出对话框
	 *
	 */
	private void exitVideoNewDialog() {

		AlertDialog.Builder builder = new Builder(VideoNewActivity.this);
		builder.setMessage("确定放弃这段视频吗？");
		builder.setTitle("温馨提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				for (int i = 0; i < list.size(); i++) {
					File file = new File(list.get(i).getPath());
					if (file.exists()) {
						file.delete();
					}
				}
				finish();
			}

		});
		builder.create().show();
	}


	/**
	 * 定义一个倒计时的内部类
	 */
	private class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发

		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			now = (int) (30000 - millisUntilFinished - old);
			if ((old > 0 && old > VIDEO_TIME * 1000) || (old == 0 && now > VIDEO_TIME * 1000)) {
				img_enter.setEnabled(true);
			}
			if (linear_seekbar.getChildCount() > 0) {
				ImageView img = (ImageView) linear_seekbar.getChildAt(linear_seekbar.getChildCount() - 1);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img.getLayoutParams();
				layoutParams.width = (int) (((float) now / 1000f) * (width / VIDEO_TIME_END)) + 1;
				img.setLayoutParams(layoutParams);
			}
		}
	}

	/**
	 * 初始化摄像头参数
	 */
	@SuppressWarnings("deprecation")
	private void initCameraParameters() {
		// 初始化摄像头参数
		mParameters = mCamera.getParameters();

		mParameters.setPreviewSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
		mParameters.setPreviewFrameRate(mProfile.videoFrameRate);

		mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);

		// 设置白平衡参数。
		String whiteBalance = mPreferences.getString("pref_camera_whitebalance_key", "auto");
		if (isSupported(whiteBalance, mParameters.getSupportedWhiteBalance())) {
			mParameters.setWhiteBalance(whiteBalance);
		}

		// 参数设置颜色效果。
		String colorEffect = mPreferences.getString("pref_camera_coloreffect_key", "none");
		if (isSupported(colorEffect, mParameters.getSupportedColorEffects())) {
			mParameters.setColorEffect(colorEffect);
		}

		try {
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始录制
	 */
	@SuppressLint("NewApi")
	private void startRecord() {
		try {
			bean = new VideoNewBean();
			vedioPath = Ppath + System.currentTimeMillis() + ".mp4";
			bean.setPath(vedioPath);

			mCamera.unlock();
			mMediaRecorder = new MediaRecorder();// 创建mediaRecorder对象
			mMediaRecorder.setCamera(mCamera);

			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setProfile(mProfile);

			// mMediaRecorder.setVideoSize(560,560);//设置视频大小（分辨率）

			mMediaRecorder.setVideoEncodingBitRate(1024 * 1024);// 设置视频一次写多少字节(可调节视频空间大小)

			// 最大期限
			mMediaRecorder.setMaxDuration(35 * 1000);

			// 第4步:指定输出文件 ， 设置视频文件输出的路径

			mMediaRecorder.setOutputFile(vedioPath);

			mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

			// // 设置保存录像方向
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (cameraPosition == 1) {
					//由于不支持竖屏录制，后置摄像头需要把视频顺时针旋转90度、、但是视频本身在电脑上看还是逆时针旋转了90度
					mMediaRecorder.setOrientationHint(90);
				} else if (cameraPosition == 0) {
					//由于不支持竖屏录制，前置摄像头需要把视频顺时针旋转270度、、而前置摄像头在电脑上则是顺时针旋转了90度
					mMediaRecorder.setOrientationHint(270);
				}
			}

			mMediaRecorder.setOnInfoListener(new OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {

				}
			});

			mMediaRecorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					recodError();
				}
			});

			// 第6步:根据以上配置准备MediaRecorder

			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			recodError();
		} catch (IOException e) {
			e.printStackTrace();
			recodError();
		} catch (RuntimeException e) {
			e.printStackTrace();
			recodError();
		} catch (Exception e){
            e.printStackTrace();
            recodError();
        }

	}





	/**
	 * 异常处理
	 *
	 */
	private void recodError() {
		AlertDialog.Builder builder = new Builder(VideoNewActivity.this);
		builder.setMessage("该设备暂不支持视频录制");
		builder.setTitle("出错啦");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}

		});
		builder.create().show();

	}

	/**
	 * 结束录制
	 *
	 */
	private void stopRecord() {

		if (bean != null) {
			if (list.size() > 0) {
				bean.setTime(now - list.get(list.size() - 1).getTime());
			} else {
				bean.setTime(now);
			}
			bean.setCameraPosition(cameraPosition);
			list.add(bean);
		}

		if (mMediaRecorder != null) {
			try {
				// 停止录像，释放camera
				mMediaRecorder.setOnErrorListener(new OnErrorListener() {
                    @Override
                    public void onError(MediaRecorder mediaRecorder, int i, int i2) {
                        recodError();
                    }
                });
				mMediaRecorder.setOnInfoListener(null);
				mMediaRecorder.stop();
				// 清除recorder配置
				mMediaRecorder.reset();
				// 释放recorder对象
				mMediaRecorder.release();
				mMediaRecorder = null;
				// 没超过3秒就删除录制所有数据
				if (old < 3000) {
					clearList();
				}
			} catch (Exception e) {
				clearList();
			}
		}
	}

	private void clearList() {
		Toast.makeText(VideoNewActivity.this, "单次录制视频最少3秒", Toast.LENGTH_LONG).show();
		if (linear_seekbar.getChildCount() > 1) {
			linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
			linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				File file = new File(list.get(list.size() - 1).getPath());
				if (file.exists()) {
					file.delete();
				}
			}
			list.remove(list.size() - 1);
			if (list.size() <= 0) {
				img_delete.setVisibility(View.GONE);
				img_enter.setVisibility(View.GONE);
				img_video.setVisibility(View.VISIBLE);
			}
		}
	}

	private static boolean isSupported(String value, List<String> supported) {
		return supported == null ? false : supported.indexOf(value) >= 0;
	}

	public static boolean getVideoQuality(String quality) {
		return "youtube".equals(quality) || "high".equals(quality);
	}

	/**
	 * 设置摄像头参数
	 *
	 */
	private void readVideoPreferences() {
		String quality = mPreferences.getString("pref_video_quality_key", "high");

		boolean videoQualityHigh = getVideoQuality(quality);

		// 设置视频质量。
		Intent intent = getIntent();
		if (intent.hasExtra(MediaStore.EXTRA_VIDEO_QUALITY)) {
			int extraVideoQuality = intent.getIntExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			videoQualityHigh = (extraVideoQuality > 0);
		}

		videoQualityHigh = false;
		
		mProfile = CamcorderProfile.get(videoQualityHigh ? CamcorderProfile.QUALITY_HIGH : CamcorderProfile.QUALITY_LOW);
		mProfile.videoFrameWidth = (int) (mProfile.videoFrameWidth * 2.0f);
		mProfile.videoFrameHeight = (int) (mProfile.videoFrameHeight * 2.0f);
		mProfile.videoBitRate = 256000 * 3;

		CamcorderProfile highProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		mProfile.videoCodec = highProfile.videoCodec;
		mProfile.audioCodec = highProfile.audioCodec;
		mProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
	}

	/**
	 * 添加红色进度条
	 *
	 */
	private void addView_Red() {
		ImageView img = new ImageView(VideoNewActivity.this);
		img.setBackgroundColor(getResources().getColor(R.color.ff1f8fe4));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(VideoNewActivity.this, 1), LinearLayout.LayoutParams.MATCH_PARENT);
		img.setLayoutParams(layoutParams);
		linear_seekbar.addView(img);
	}

	/**
	 * 添加黑色断条
	 *
	 */
	private void addView_black() {
		ImageView img = new ImageView(VideoNewActivity.this);
		img.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(VideoNewActivity.this, 2), LinearLayout.LayoutParams.MATCH_PARENT);
		img.setLayoutParams(layoutParams);
		linear_seekbar.addView(img);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setStartPreview(holder);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		// 先开启在关闭 先开启录制在关闭可以 解决游览的时候比较卡顿的现象，但是会有视频开启时声音。打开这个功能时较慢
		// startRecord();
		// stopRecord();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

	/**
	 * 设置camera显示取景画面,并预览
	 * @param holder
	 */
	private void setStartPreview(SurfaceHolder holder) {
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}
		} catch (IOException e) {

		}
	}

	/**
	 * 释放Camera
	 *
	 */
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();// 停掉原来摄像头的预览
			mCamera.release();
			mCamera = null;
		}
	}

}
