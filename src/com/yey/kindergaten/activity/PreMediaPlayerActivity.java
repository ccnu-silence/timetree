package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.ParamsUtil;
import com.yey.kindergaten.widget.DialogTips;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zy on 2015/6/15.
 * 录制视频预览展示类
 */
public class PreMediaPlayerActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private RelativeLayout playerTopLayout;//顶部布局
    private TextView videoIdText;
    private LinearLayout right_ll;//点击跳转界面

    private RelativeLayout playerBottomLayout;//底部布局
    private ImageView playOp;//暂停按钮
    private SeekBar skbProgress;
    private TextView videoDuration;
    private Button definitionBtn;

    private RelativeLayout loading_rl;//加载界面时显示

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private DWMediaPlayer player;
    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private GestureDetector detector;
    private float scrollTotalDistance, scrollCurrentPosition;

    private boolean isPrepared;
    private boolean isLocalPlay;
    // 控制播放器面板显示
    private boolean isDisplay = false;
    String path;
    int currentPosition;
    private Boolean isPlaying;
    // 当player未准备好，并且当前activity经过onPause()生命周期时，此值为true
    private boolean isFreeze = false;
    private boolean isSurfaceDestroy = false;
    String videoId;
    private ImageView iv_delete;
    private LinearLayout ll_upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_mediaplay_activity);

        detector = new GestureDetector(this, new MyGesture());

        initView();

        initPlayHander();

        initPlayInfo();
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.pre_video_surfaceview);

        playOp = (ImageView) findViewById(R.id.btnPlay);

        videoIdText = (TextView) findViewById(R.id.videoIdText);
        right_ll = (LinearLayout) findViewById(R.id.click_right_ll);


        videoDuration = (TextView) findViewById(R.id.videoDuration);
//        playDuration.setText(ParamsUtil.millsecondsToStr(0));
        videoDuration.setText(ParamsUtil.millsecondsToStr(0));
        definitionBtn = (Button) findViewById(R.id.playScreenSizeBtn);


        skbProgress = (SeekBar) findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener);

        playerTopLayout = (RelativeLayout) findViewById(R.id.playerTopLayout);
        playerBottomLayout = (RelativeLayout) findViewById(R.id.playerBottomLayout);
        loading_rl = (RelativeLayout) findViewById(R.id.pre_vedio_loading_rl);

        playOp.setOnClickListener(onClickListener);
        definitionBtn.setOnClickListener(onClickListener);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //2.3及以下使用，不然出现只有声音没有图像的问题
        surfaceHolder.addCallback(this);

        iv_delete = (ImageView) findViewById(R.id.video_new_img_delete);
        iv_delete.setOnClickListener(onClickListener);
        ll_upload = (LinearLayout) findViewById(R.id.video_makesure_to_upload);
        ll_upload.setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnPlay:
                    if (!isPrepared) {
                        return;
                    }

                    if (isLocalPlay && !player.isPlaying()) {
                        try {
                            player.prepare();

                        } catch (IllegalArgumentException e) {
                            Log.e("player error", e.getMessage());
                        } catch (SecurityException e) {
                            Log.e("player error", e.getMessage());
                        } catch (IllegalStateException e) {
                            Log.e("player error", e + "");
                        } catch (IOException e) {
                            Log.e("player error", e + "");
                        }
                    }
                    changePlayStatus();
                    break;
                case R.id.video_new_img_delete:
                     if(videoId!=null){
                      DialogTips dialogTips =new  DialogTips(PreMediaPlayerActivity.this,"您确定要删除该视频吗？","确定",false,"取消",true);
                         dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                 dialogInterface.dismiss();
                                 FileUtils.deleteFileWithPath(videoId);
                                 finish();
                             }
                         });
                         dialogTips.show();
                     }
                    break;
                case R.id.video_makesure_to_upload:

                    break;
            }
        }
    };





    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int progress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            this.progress = progress * player.getDuration() / seekBar.getMax();

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.seekTo(progress);
        }
    };


    private void initPlayHander() {
        playerHandler = new Handler() {
            public void handleMessage(Message msg) {

                if (player == null) {
                    return;
                }


                // 更新播放进度
                int position = player.getCurrentPosition();
                int duration = player.getDuration();

                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    videoDuration.setText(ParamsUtil.millsecondsToStr(player
                            .getCurrentPosition()));
                    skbProgress.setProgress((int) pos);

                }
            };
        };
        Log.i("vedioplay"," MediaPlayActivity initPlayHander...");

        // 通过定时器和Handler来更新进度
        timerTask = new TimerTask() {
            @Override
            public void run() {

                if (!isPrepared) {
                    return;
                }

                playerHandler.sendEmptyMessage(0);
            }
        };

    }

    private void initPlayInfo() {
        timer.schedule(timerTask, 0, 1000);
        isPrepared = false;
        player = new DWMediaPlayer();
        player.reset();
        player.setOnErrorListener(this);
        Log.i("vedioplay"," MediaPlayActivity initPlayInfo...");

        videoId   = getIntent().getStringExtra("videoId");
        videoIdText.setText(videoId);
        isLocalPlay = getIntent().getBooleanExtra("isLocalPlay", false);
        try {

            if (!isLocalPlay) {// 播放线上视频

                player.setVideoPlayInfo(videoId, AppConstants.CC_USER_ID,
                        AppConstants.CC_API_KEY, this);
                player.setDefaultDefinition(DWMediaPlayer.NORMAL_DEFINITION);

            } else {// 播放本地已下载视频

                if (android.os.Environment.MEDIA_MOUNTED.equals(Environment
                        .getExternalStorageState())) {
//                    path = Environment.getExternalStorageDirectory()
//                            + "/".concat(AppConstants.DOWNLOAD_DIR).concat("/")
//                            .concat(videoId).concat(".mp4");
//                    path = Environment.getDownloadCacheDirectory()+""
                    path = videoId;
                    if (!new File(path).exists()) {
                        return;
                    }

                    player.setDataSource(path);
                }
            }

            player.prepareAsync();
            Log.i("vedioplay"," MediaPlayActivity initPlayInfo  player.prepareAsync();...");
        } catch (IllegalArgumentException e) {
            Log.e("player error", e.getMessage());
        } catch (SecurityException e) {
            Log.e("player error", e.getMessage());
        } catch (IllegalStateException e) {
            Log.e("player error", e + "");
        } catch (IOException e) {
            Log.e("player error", e.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);
            player.setDisplay(holder);
            Log.i("vedioplay"," MediaPlayActivity surfaceCreated...");
            if (isSurfaceDestroy) {
                if (isLocalPlay) {
                    player.setDataSource(path);
                }
                player.prepareAsync();
            }
        } catch (Exception e) {
            Log.e("videoPlayer", "error", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int width, int height) {
                holder.setFixedSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (player == null) {
            return;
        }
        if (isPrepared) {
            currentPosition = player.getCurrentPosition();
        }

        isPrepared = false;
        isSurfaceDestroy = true;

        player.stop();
        player.reset();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        loading_rl.setVisibility(View.GONE);
        isPrepared = true;
        Log.i("vedioplay"," MediaPlayActivity onPrepared...");
        if (!isFreeze) {
            if(isPlaying == null || isPlaying.booleanValue()){
                player.start();
                playOp.setImageResource(R.drawable.btn_pause);
            }
        }

        if (currentPosition > 0) {
            player.seekTo(currentPosition);
        }
        videoDuration.setText(ParamsUtil.millsecondsToStr(player.getDuration()));

    }

    // 手势监听器类
    private class MyGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            scrollTotalDistance += distanceX;

            float duration = (float) player.getDuration();

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            float width = wm.getDefaultDisplay().getWidth() * 0.75f; // 设定总长度是多少，此处根据实际调整

            float currentPosition = scrollCurrentPosition - (float) duration
                    * scrollTotalDistance / width;

            if (currentPosition < 0) {
                currentPosition = 0;
            } else if (currentPosition > duration) {
                currentPosition = duration;
            }

            player.seekTo((int) currentPosition);

            videoDuration.setText(ParamsUtil
                    .millsecondsToStr((int) currentPosition));
            int pos = (int) (skbProgress.getMax() * currentPosition / duration);
            skbProgress.setProgress(pos);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            scrollTotalDistance = 0f;

            scrollCurrentPosition = (float) player.getCurrentPosition();

            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            changePlayStatus();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isDisplay) {
                setLayoutVisibility(View.GONE, false);
            } else {
                setLayoutVisibility(View.VISIBLE, true);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void setLayoutVisibility(int gone, boolean b) {



    }

    @Override
    public void onPause() {
        if (isPrepared) {
            // 如果播放器prepare完成，则对播放器进行暂停操作，并记录状态
            if (player.isPlaying()) {
                isPlaying = true;
            } else {
                isPlaying = false;
            }
            player.pause();
        } else {
            // 如果播放器没有prepare完成，则设置isFreeze为true
            isFreeze = true;
        }

        super.onPause();
    }


    @Override
    public void onResume() {

        if (isFreeze) {
            isFreeze = false;
            if (isPrepared) {
                player.start();
            }
        } else {
            if (isPlaying != null && isPlaying.booleanValue() && isPrepared) {
                player.start();
            }
        }
        super.onResume();
    }

    private void changePlayStatus() {
        if (player.isPlaying()) {
            player.pause();
            playOp.setImageResource(R.drawable.btn_play);

        } else {
            player.start();
            playOp.setImageResource(R.drawable.btn_pause);
        }
    }
}
