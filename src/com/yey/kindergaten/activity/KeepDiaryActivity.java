package com.yey.kindergaten.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceGridviewAdapter;
import com.yey.kindergaten.bean.DiaryHomeInfo;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.cropimage.CropImage;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.RecordManager;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.EmoticonsEditText;
import com.yey.kindergaten.widget.GenerateProcessButton;
import com.yey.kindergaten.widget.MyGridviewWithScrollView;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class KeepDiaryActivity extends BaseActivity implements  OnClickListener {

	MyGridviewWithScrollView gridview ;	
	ServiceGridviewAdapter serviceGridviewAdapter;
	ArrayList<String> list = new ArrayList<String>();
	RecordManager recordManager = null;
	Button btn_record;
	ProgressBar pro_keepdariy;
	private Handler messagehandler;
	private ImageView iv_voice;
	private int indexcount;
	private boolean isrun = true;
	public static int RECORD_NO = 0;    // 不在录音
	public static int RECORD_ING = 1;   // 正在录音
	public static int RECODE_ED = 2;    // 完成录音
	private Thread recordThread;
	private boolean flag;
	private String file = "";
	private String name;
	private ImageView btn_left;
	private TextView btn_right;
	private TextView tv_title;
	private RelativeLayout ll_recording;
	EmoticonsEditText editText;
	private SimpleDateFormat mSdf;
	private String mDate;
	private TextView  tv_time;
	private PopupWindow popwindow,inpopwindow;
	private LinearLayout recording_play;
	private Boolean isshow = true;
	private Boolean isshow_small = true;
	private AnimationDrawable animationDrawable;
    private static double voiceValue = 0.0;
    private Timer timer;

    @ViewInject(R.id.network_listener_ll)RelativeLayout netCheckRL;
    @ViewInject(R.id.network_listener_tv)TextView netCheckTv;

    CharSequence[] items = { "相册","拍照" };
	private static final String PATH = Environment
			.getExternalStorageDirectory() + "/yey/kindergaten/uploadimg/";
	private static final String PATHA = Environment
			.getExternalStorageDirectory() + "/yey/kindergaten/readyuoload/";
	private ArrayList<Photo> alist = new ArrayList<Photo>();
	private MediaPlayer mediaPlayer = null;
    public boolean isPlaying = false;
    private int play_statue = 1;
    private int play_begin = 1;
    private int play_ing = 2;
    private int play_end = 3;
    private ImageView iv_add_photo, iv_add_voice;
    private ImageView ll_add;
    private LinearLayout ll_add_voice;
    private TextView tv_add_photo;
    private LinearLayout ll_add_all;
    private GenerateProcessButton generateProcessButton;
    private ImageView iv_show_voice;
    private TextView tv_add_voice;
    private static float recodeTime = 0.0f;
    private int voice_force;
    private ImageView iv_delet_voice;
    private static final int CAMERA_SUCCESS = 2;
    private static final int PHOTO_SUCCESS = 1;
    private static final int PHOTO_CROP = 9;
    private static final int PHOTO_PACK = 8;
    NetWorkStateReceive mReceiver;
    private final static String TAG = "KeepDiaryActivity";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keepdiary_main);
        ViewUtils.inject(this);

        mReceiver = new NetWorkStateReceive();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);

		initView();
	}

    public class NetWorkStateReceive extends BroadcastReceiver {
        private ConnectivityManager connectivityManager;
        private NetworkInfo info;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    netCheckRL.setVisibility(View.GONE);
                } else {
                    netCheckRL.setVisibility(View.VISIBLE);
                    netCheckTv.setText("网络不可用，请检查您的网络设置。");
                }
            }
        }
    }

	private void initView() {
		iv_voice = (ImageView) findViewById(R.id.iv_voice);
		recordManager = RecordManager.getInstance(this);
//		gridview = (MyGridviewWithScrollView) findViewById(R.id.service_publishspeak_gv);
//      gridview.setOnItemClickListener(this);
//		ll_recording = (RelativeLayout) findViewById(R.id.recording_voice);
//		ll_recording.setOnClickListener(this);
//		btn_record = (Button) findViewById(R.id.btn_keepdairy);
//		btn_record.setOnClickListener(this);
//		pro_keepdariy = (ProgressBar) findViewById(R.id.pro_keepdairy);
		AppContext.checkList.clear();
    	Photo photo = new Photo();
    	photo.imgPath = "local";
    	alist.add(photo);
		messagehandler = new MessagHandler();
		btn_left = (ImageView)findViewById(R.id.left_btn);
		btn_left.setVisibility(View.VISIBLE);
		btn_left.setOnClickListener(this);
		btn_right = (TextView) findViewById(R.id.right_tv);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setText("保存");
		btn_right.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.header_title);
		tv_title.setText("写日记");
		editText = (EmoticonsEditText) findViewById(R.id.service_keepdairy_ed);
        iv_add_photo = (ImageView) findViewById(R.id.iv_add_grow_photo);
        iv_add_photo.setOnClickListener(this);
        iv_add_voice = (ImageView) findViewById(R.id.iv_add_grow_voice);
        iv_add_voice.setOnClickListener(this);
        ll_add = (ImageView) findViewById(R.id.iv_full_add);
        ll_add.setOnClickListener(this);
        ll_add_voice = (LinearLayout) findViewById(R.id.ll_add_photo);
        tv_add_photo = (TextView) findViewById(R.id.tv_add_photo);
        ll_add_all = (LinearLayout) findViewById(R.id.ll_add_all);
        iv_show_voice = (ImageView) findViewById(R.id.iv_show_voice);
        iv_show_voice.setOnClickListener(this);
        tv_add_voice = (TextView) findViewById(R.id.tv_add_voice);
        iv_delet_voice = (ImageView) findViewById(R.id.iv_delet_voice);
        iv_delet_voice.setOnClickListener(this);
        netCheckRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                UtilsLog.i(TAG, "wifiSettingIntent to settings.WIFI_SETTINGS");
            }
        });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == CAMERA_SUCCESS) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                showToast("SD不可用");
                return;
            }
            startCropImage(PATH + name);
		} else if (requestCode == PHOTO_CROP) {
            if (intent == null) {
                return;
            } else {
                String croppath = intent.getStringExtra(CropImage.IMAGE_PATH);
                Bitmap newBitmap = BitmapUtil.getImageByPath(croppath, false);
                BitmapUtil.savePhotoToSDCard(newBitmap, PATHA, name);
                alist.clear();
                list.add(PATHA + name);
                Photo photo = new Photo();
                photo.imgPath = PATHA + name;
                alist.add(photo);
                String path = alist.get(0).imgPath;
                ImageLoader.getInstance().displayImage("file:///" + path, ll_add, ImageLoadOptions.getOptions());
                tv_add_photo.setVisibility(View.GONE);
                iv_add_photo.setVisibility(View.GONE);
                ll_add.setVisibility(View.VISIBLE);
            }
        }
        if (20 == resultCode) {
            recording_play.setVisibility(View.VISIBLE);
            file = intent.getExtras().getString("file");
        }
    }

    private void startCropImage(String path) {

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, path);
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);

        startActivityForResult(intent, PHOTO_CROP);
    }


    private void startImageAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Configuration o = newConfig;
            o.orientation = Configuration.ORIENTATION_PORTRAIT;
            newConfig.setTo(o);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }
        super.onConfigurationChanged(newConfig);
    }
	
	@Override
	protected void onNewIntent(Intent intent){
		if (intent.getExtras()!=null) {
		    ArrayList<Photo> list = intent.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
			alist.clear();
            if (list != null) {
            	alist.addAll(list);
            }
            if (alist.size() > 0) {
                String path = alist.get(0).imgPath;
                ImageLoader.getInstance().displayImage("file:///" + path, ll_add, ImageLoadOptions.getOptions());
            }
            tv_add_photo.setVisibility(View.GONE);
            iv_add_photo.setVisibility(View.GONE);
            ll_add.setVisibility(View.VISIBLE);
		}
			//serviceGridviewAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.right_tv:
                if (editText.getText() == null || editText.getText().toString().trim().equals("")) {
                    showToast("请输入成长日记内容");
                    return;
                }
                if (!AppUtils.isNetworkAvailable(KeepDiaryActivity.this)) {
                    showToast("现在网络不给力，等会儿呗");
                    return;
                }
                Intent a = new Intent(KeepDiaryActivity.this, GrowthDiaryActivity.class);
                DiaryHomeInfo newValues = new DiaryHomeInfo();
                mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mDate = mSdf.format(new Date(System.currentTimeMillis()));
                newValues.setCon(editText.getText().toString());
                newValues.setDate(mDate);
                newValues.setDiaryid("-1");
                if (alist.size() > 0) {
                    a.putExtra("img", alist.get(0).imgPath);
                    newValues.setImg(alist.get(0).imgPath);
                } else {
                    a.putExtra("img", "");
                    newValues.setImg("");
                }
                if (!file.equals("") && file!=null) {
                    a.putExtra("recording", file);
                    newValues.setSnd(file);
                } else {
                    a.putExtra("recording", "");
                    newValues.setSnd("");
                }
                try {
                    DbHelper.getDB(KeepDiaryActivity.this).save(newValues);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                a.putExtra("content", editText.getText().toString().trim());
                a.putExtra("date", mDate);
                startActivity(a);
                hideSoftInput(editText);
                this.finish();
                break;
            case R.id.iv_show_voice:
                if (!isPlaying) {
                    iv_show_voice.setImageResource(R.anim.animotion_voice_playing);
                    animationDrawable = (AnimationDrawable) iv_show_voice.getDrawable();
                    animationDrawable.start();
                    startPlayRecord(file, true);
                }
                break;
            case R.id.left_btn:
                Intent i = new Intent(this, GrowthDiaryActivity.class);
                startActivity(i);
                break;
            case R.id.iv_add_grow_photo:
                showDiaglog();
                break;
            case R.id.iv_add_grow_voice:
                showpopwindow();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                isshow = false;
                break;
            case R.id.iv_full_add:
                showDiaglog();
                break;
            case R.id.iv_delet_voice:
                showDialog("友情提示：", "你确定要删除录音吗？", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        stopPlayRecord();
                        iv_delet_voice.setVisibility(View.GONE);
                        iv_add_voice.setVisibility(View.VISIBLE);
                        iv_show_voice.setVisibility(View.GONE);
                        play_statue=play_begin;
                        tv_add_voice.setText("添加语音");
                        iv_show_voice.setImageResource(R.drawable.grow_voice_show);
                        file = "";
                    }
                });
                break;
            default:
                break;
		}
	}

    private void  showDiaglog() {
        showDialogItems(items, "选择照片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 1) {   // 拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    name = DateFormat.format("yyyyMMddhhmmss",
                            Calendar.getInstance(Locale.CHINA))
                            + ".jpg";
                    File file = new File(PATH + "takephoto/");
                    if (!file.exists()) {
                        file.mkdirs(); // 创建文件夹
                    }
                    Uri imageUri = Uri.fromFile(new File(PATH, name));
                    System.out.println("imageUri----" + imageUri.toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_SUCCESS);
                } else { 		   // 相册
                    Intent intent = new Intent(KeepDiaryActivity.this, GalleryActivity.class);
                    intent.putExtra("typefrom", AppConstants.FROMDAIRY);
                    startActivity(intent);
                }
            }
        });
    }

	private void showpopwindow() {
	    if (isshow) {
            LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_view_voice, null);
            popwindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    //		popwindow.showAsDropDown(ll_add_all,0,0, Gravity.CENTER_HORIZONTAL);
            popwindow.showAsDropDown(ll_add_all);
            ImageView iv_delete = (ImageView) layout.findViewById(R.id.delete);
            final ImageView iv_play = (ImageView) layout.findViewById(R.id.voice_play);
            generateProcessButton = (GenerateProcessButton)layout.findViewById(R.id.btnUpload);
            iv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (play_statue == play_begin) {
                        if (popwindow.isShowing()) {
                            popwindow.dismiss();
                        }
                        isshow = true;
                    } else if (play_statue == play_ing) {
                        play_statue = play_end;
                        showinpopwindow();
                        isshow_small = false;
                    } else if (play_statue == play_end) {
                        showinpopwindow();
                        isshow_small = false;
    //                  editText.setFocusable(true);
                    }
                }
            });
            iv_play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (play_statue == play_begin) {
                        play_statue = play_ing;
                        indexcount = 0;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            iv_play.setBackground(AppContext.getInstance().getResources().getDrawable(R.drawable.recording_stop));
                        } else {
                            iv_play.setBackgroundDrawable(AppContext.getInstance().getResources().getDrawable(R.drawable.recording_stop));
                        }
                        timer = new Timer();
                        timer.schedule(new TimesTask(),0,1000);
                        recordManager.startRecording(AppServer.getInstance().getAccountInfo().getUid() + "");
                        recordThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (play_statue == play_ing) {
                                    if (indexcount >= 30) {
                                        play_statue=play_end;
                                        recordManager.stopRecording();
                                        Message m = new Message();
                                        m.what = RECODE_ED;
                                        messagehandler.sendMessage(m);
                                    } else {
                                        try {
                                            Thread.sleep(100);
                                            if (play_statue == play_ing) {
                                                Message m = new Message();
                                                m.what = RECORD_ING;
                                                messagehandler.sendMessage(m);
                                                voiceValue = recordManager.getAmplitude();
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                        recordThread.start();
                    } else if (play_statue == play_ing) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            iv_play.setBackground(AppContext.getInstance().getResources().getDrawable(R.drawable.recording_play));
                        } else {
                            iv_play.setBackgroundDrawable(AppContext.getInstance().getResources().getDrawable(R.drawable.recording_play));
                        }
                        if (indexcount > 1) {
                            recordManager.stopRecording();
                            Message m = new Message();
                            m.what = RECODE_ED;
                            messagehandler.sendMessage(m);
    //						indexcount = 0;
                            generateProcessButton.setText("00:00");
                        } else {
                            recordManager.cancelRecording();
                            Message m=new Message();
                            m.what = RECORD_NO;
                            messagehandler.sendMessage(m);
    //						indexcount = 0;
                            generateProcessButton.setText("00:00");
                        }
                    }
                }
            });
	    }
	}
	private void showinpopwindow() {
	    if (isshow_small) {
    //		LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
    //		View layout = inflater.inflate(R.layout.activitity_popwindow_keepdairy, null);
            showDialog("是否保存录音",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (play_statue == play_begin) {
    //				    inpopwindow.dismiss();
                        if (popwindow.isShowing()){
                            popwindow.dismiss();
                        }
                        isshow = true;
                        isshow_small = true;
                    } else if (play_statue == play_end) {
                        if (indexcount > 1) {
                            recordManager.stopRecording();
                            Message m = new Message();
                            m.what = RECODE_ED;
                            messagehandler.sendMessage(m);
        //					indexcount = 0;
        //					inpopwindow.dismiss();
                            if (popwindow.isShowing()) {
                                popwindow.dismiss();
                            }
                            isshow = true;
                            isshow_small = true;
                        } else {
                            recordManager.cancelRecording();
                            Message m = new Message();
                            m.what=RECORD_NO;
                            messagehandler.sendMessage(m);
        //					indexcount = 0;
        //					inpopwindow.dismiss();
                            popwindow.dismiss();
                            isshow = true;
                            isshow_small = true;
                        }
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
    //				inpopwindow.dismiss();
                    if (popwindow.isShowing()){
                        popwindow.dismiss();
                    }
                    Message m=new Message();
                    m.what=3;
                    messagehandler.sendMessage(m);
                    isshow_small=true;
                }
            });
	    }
    }

	final class MessagHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
                case 1:
                    setDialogImage();
                    if (!Thread.currentThread().isInterrupted()) {
    //					tv_time.setText(indexcount + "''");
                        if (indexcount>=10) {
                            generateProcessButton.setText("00:" + indexcount);
                        } else {
                            generateProcessButton.setText("00:0" + indexcount);
                        }
                    }
                    break;
                case 2:
    //				pro_keepdariy.setProgress(0);
                    file = recordManager.getRecordFilePath(AppServer.getInstance().getAccountInfo().getUid() + "");
                    isrun = true;
                    iv_add_voice.setVisibility(View.GONE);
                    iv_show_voice.setVisibility(View.VISIBLE);
                    tv_add_voice.setText(indexcount + "秒");
                    play_statue = play_begin;
                    if (popwindow.isShowing()) {
                        popwindow.dismiss();
                    }
                    isshow = true;
                    timer.cancel();
                    iv_delet_voice.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    showToast("录音时间太短，录音结束");
                    recordManager.cancelRecording();
                    isshow = true;
                    if (popwindow.isShowing()) {
                        popwindow.dismiss();
                    }
                    timer.cancel();
                    iv_delet_voice.setVisibility(View.GONE);
                    iv_add_voice.setVisibility(View.VISIBLE);
                    iv_show_voice.setVisibility(View.GONE);
                    play_statue = play_begin;
                    tv_add_voice.setText("添加语音");
                    iv_show_voice.setImageResource(R.drawable.grow_voice_show);
                    file = "";
                    break;
                case 3:
                    isshow = true;
                    if (popwindow.isShowing()) {
                        popwindow.dismiss();
                    }
                    timer.cancel();
                    recordManager.cancelRecording();
                    iv_delet_voice.setVisibility(View.GONE);
                    iv_add_voice.setVisibility(View.VISIBLE);
                    iv_show_voice.setVisibility(View.GONE);
                    play_statue = play_begin;
                    tv_add_voice.setText("添加语音");
                    iv_show_voice.setImageResource(R.drawable.grow_voice_show);
                    file = "";
                    break;
                default:
                    break;
			}
		}
	}
	public  void hideSoftInput(EmoticonsEditText et) {
        InputMethodManager imm = (InputMethodManager) et.getContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
	
	@SuppressWarnings("resource")
	public void startPlayRecord(String filePath, boolean isUseSpeaker) {
		if (!(new File(filePath).exists())) {
			return;
		}
		AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer = new MediaPlayer();
		if (isUseSpeaker) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		} else {
			audioManager.setSpeakerphoneOn(false); // 关闭扬声器
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		try {
			mediaPlayer.reset();
			// 单独使用此方法会报错播放错误:setDataSourceFD failed.: status=0x80000000
//			mediaPlayer.setDataSource(filePath);
			// 因此采用此方式会避免这种错误
			FileInputStream fis = new FileInputStream(new File(filePath));
			mediaPlayer.setDataSource(fis.getFD());
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					isPlaying = true;
//					currentMsg = message;
					arg0.start();
//					startRecordAnimation();
				}
			});
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    stopPlayRecord();
                    animationDrawable.stop();
                    iv_show_voice.setImageResource(R.drawable.grow_voice_show);
                    flag = true;
                }
            });
//			currentPlayListener = this;
//			isPlaying = true;
//			currentMsg = message;
//			mediaPlayer.start();
//			startRecordAnimation();
		} catch (Exception e) {
			System.out.println(e.getMessage());
            animationDrawable.stop();
            iv_show_voice.setImageResource(R.drawable.grow_voice_show);
            flag = true;
		}
	}

	public void stopPlayRecord() {
//		stopRecordAnimation();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
            mediaPlayer=null;
            voiceValue = 0.0;
		}
		isPlaying = false;
	}

    void setDialogImage(){
        if (voiceValue < 200.0) {
            voice_force = 90;
        } else if (voiceValue > 200.0 && voiceValue < 400) {
            voice_force = 75;
        } else if (voiceValue > 400.0 && voiceValue < 800) {
            voice_force = 60;
        } else if (voiceValue > 800.0 && voiceValue < 1600) {
            voice_force = 45;
        } else if (voiceValue > 1600.0 && voiceValue < 3200) {
            voice_force = 30;
        } else if (voiceValue > 3200.0 && voiceValue < 5000) {
            voice_force = 15;
        }
        generateProcessButton.setProgress(voice_force);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    class TimesTask extends TimerTask {
        @Override
        public void run() {
            indexcount++;
        }
    }
	
}
