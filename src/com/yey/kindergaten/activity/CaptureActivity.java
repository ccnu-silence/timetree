package com.yey.kindergaten.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;
import com.zxing.barcode.util.CameraManager;
import com.zxing.barcode.util.CaptureActivityHandler;
import com.zxing.barcode.util.InactivityTimer;
import com.zxing.barcode.util.RGBLuminanceSource;
import com.zxing.barcode.util.ViewfinderView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 描述: 扫描界面
 */
public class CaptureActivity extends BaseActivity implements Callback {
	private Context mContext;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private SurfaceView surfaceView;
	private ImageView mBack;
	private View mDialogView;
	private Button mCancle;
	private Button mSure;
	private TextView mUrl;
	private Dialog mDialog;
	Button my_erweima;
	Button scan_pic;

	private String resultString = "";
	String photo_path = "";
	private int screenWidth;
	Button btn_deng;
	TextView text_wenzi;
	ImageView image_erweima;
	String state="";
	public static boolean isOpen = false; // 定义开关状态，flase 关闭 true 打开
	private CameraManager cameraManager;
    @ViewInject(R.id.header_title)TextView tv_header;	
    @ViewInject(R.id.left_btn)ImageView left_iv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camera);
		if(getIntent().getExtras()!=null){
		   state=getIntent().getExtras().getString("state");	
		}
		ViewUtils.inject(this);
		mContext = this;
		CameraManager.init(getApplication());
		initControl();

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	private void initControl() {
		
		left_iv.setVisibility(View.VISIBLE);
		tv_header.setText("扫一扫");
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;

	}

	

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		resultString = result.getText();
		if(resultString==null ||resultString.equals("")){
			showToast("扫描失败");
		}else{
			if(getUrl(resultString)){
				//打开浏览器
				Intent intent = new Intent();        
	            intent.setAction("android.intent.action.VIEW");    
	            Uri content_url = Uri.parse(resultString);   
	            intent.setData(content_url);  
	            startActivity(intent);
			}
		}
        	int index=resultString.indexOf('#');
        	int position=resultString.lastIndexOf('_');
        	if(index==-1){
        		showToast("无法识别的二维码");      		
        		return ;
        	}
    		
    		if(position==-1){
    			showToast("无法识别的二维码");          		
        		return ;
        	}
    		String type=resultString.substring(0, position);
    		if(!type.equals("TIMES_TREE_QRCODE")){
    			showToast("无法识别的二维码"); 
        		return ;
    		}
    		int role=Integer.parseInt(resultString.substring(position+1,index));
			int value=Integer.parseInt(resultString.substring(index+1,resultString.length()));
			if(role==1){
				Intent intent=new Intent(CaptureActivity.this,ContactsAddPuacResultActivity.class);		
				intent.putExtra("vtype", 3);		//公众号ID
				intent.putExtra("value", value+"");
				intent.putExtra("state", AppConstants.CAPETURE);
				startActivity(intent);
			}else if(role==0){                      //加好友
				Intent intent=new Intent(CaptureActivity.this,ContactsAddFriendResultActivity.class);		
				intent.putExtra("vtype", 1);	           //好友的ID	
				intent.putExtra("value", value+"");
				intent.putExtra("state", AppConstants.CAPETURE);
				startActivity(intent);
			}else{
				Intent intent=new Intent(CaptureActivity.this,ServiceSreachKinderResultActivity.class);					
				intent.putExtra(AppConstants.SREACHGROUPVALUE,value+"");
				startActivity(intent);
			}
			
		
			/*if (getUrl(resultString) != true) {
				Toast.makeText(CaptureActivity.this, resultString, Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(resultString);
				intent.setData(content_url);
				startActivity(intent);
				finish();
			}*/
	}

	/**
	 * 开始扫描
	 * 
	 */
	private void start() {
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	/**
	 * 停止扫描
	 */
	private void stop() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	/**
	 * 扫描正确后的震动声音,如果感觉apk大了,可以删除
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.qrcode_found);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	/**
	 * 验证字符串是否为网址
	 */
	public static boolean getUrl(String url) {
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern patt = Pattern.compile(regex);
		Matcher matcher = patt.matcher(url);
		boolean isMatch = matcher.matches();
		return isMatch;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

			image_erweima.setVisibility(View.VISIBLE);
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			image_erweima.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			scanningImage();

		}

	}

	/**
	 * 解析QR图内容
	 * 
	 * @param imageView
	 * @return
	 */
	// 解析QR图片
	private void scanningImage() {

		Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

		// 获得待解析的图片
		Bitmap bitmap = ((BitmapDrawable) image_erweima.getDrawable())
				.getBitmap();
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		Result result;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//image_erweima.setVisibility(View.GONE);
		return super.onTouchEvent(event);
	}
	
   @OnClick({R.id.left_btn})
   public void onclickview(View view){
	   switch (view.getId()) {
	case R.id.left_btn:
		finish();
		break;

	default:
		break;
	}
   }
}