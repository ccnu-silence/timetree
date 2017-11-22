package com.yey.kindergaten.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Chat;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * 播放录音文件
 * @ClassName: NewRecordPlayClickListener
 */
public class NewRecordPlayClickListener implements View.OnClickListener {

	Chat message;
	ImageView iv_voice;
	private AnimationDrawable anim = null;
	Context context;
	String currentObjectId = "";
	MediaPlayer mediaPlayer = null;
	public static boolean isPlaying = false;
	public static NewRecordPlayClickListener currentPlayListener = null;
	static Chat currentMsg = null;// 用于区分两个不同语音的播放

	AccountInfo info=null;

	public NewRecordPlayClickListener(Context context, Chat msg,	ImageView voice) {
		this.iv_voice = voice;
		this.message = msg;
		this.context = context;
		currentMsg = msg;
		currentPlayListener = this;
		info = AppServer.getInstance().getAccountInfo();
		currentObjectId = String.valueOf(info.getUid());
		
	}

	/**
	 * 播放语音
	 * 
	 * @Title: playVoice
	 * @Description: TODO
	 * @param @param filePath
	 * @param @param isUseSpeaker
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("resource")
	public void startPlayRecord(String filePath, boolean isUseSpeaker) {
		if (!(new File(filePath).exists())) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer = new MediaPlayer();
		if (isUseSpeaker) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// 关闭扬声器
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		try {
			mediaPlayer.reset();
			//单独使用此方法会报错播放错误:setDataSourceFD failed.: status=0x80000000
//			mediaPlayer.setDataSource(filePath);
			//因此采用此方式会避免这种错误
			FileInputStream fis = new FileInputStream(new File(filePath));
			mediaPlayer.setDataSource(fis.getFD());
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					isPlaying = true;
					currentMsg = message;
					arg0.start();
					startRecordAnimation();
				}
			});
			mediaPlayer.prepare();
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							stopPlayRecord();
						}

					});
			currentPlayListener = this;
//			isPlaying = true;
//			currentMsg = message;
//			mediaPlayer.start();
//			startRecordAnimation();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 停止播放
	 * @Title: stopPlayRecord
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void stopPlayRecord() {
		stopRecordAnimation();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
	}

	/**
	 * 开启播放动画
	 * 
	 * @Title: startRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void startRecordAnimation() {
		if (String.valueOf(message.getUid()).equals(currentObjectId)) {
			iv_voice.setImageResource(R.anim.anim_chat_voice_right);
		} else {
			iv_voice.setImageResource(R.anim.anim_chat_voice_left);
		}
		anim = (AnimationDrawable) iv_voice.getDrawable();
		anim.start();
	}

	/**
	 * 停止播放动画
	 * 
	 * @Title: stopRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void stopRecordAnimation() {
		if (String.valueOf(message.getUid()).equals(currentObjectId)) {
			iv_voice.setImageResource(R.drawable.voice_right3);
		} else {
			iv_voice.setImageResource(R.drawable.voice_left3);
		}
		if (anim != null) {
			anim.stop();
		}
	}

	@Override
	public void onClick(View arg0) {
		if (isPlaying) {
			currentPlayListener.stopPlayRecord();
			if (currentMsg != null
					&& currentMsg.hashCode() == message.hashCode()) {
				currentMsg = null;
				return;
			}
		}
		Log.i("voice", "点击事件");
		if (String.valueOf(message.getUid()).equals(currentObjectId)) {// 如果是自己发送的语音消息，则播放本地地址
			String localPath = message.getContent();
			if(localPath.contains("http")){
				localPath = getDownLoadFilePath(message);
				System.out.print(localPath);
			}
			startPlayRecord(localPath, true);
		} else {// 如果是收到的消息，则需要先下载后播放
			String localPath = getDownLoadFilePath(message);
			Log.i("voice", "收到的语音存储的地址:" + localPath);
			startPlayRecord(localPath, true);
		}
	}

	public String getDownLoadFilePath(Chat msg) {
        String name = msg.getContent();
		File dir = new File(AppConstants.VOICE_DIR + File.separator
				+ info.getUid());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 在当前用户的目录下面存放录音文件
		File audioFile = new File(dir.getAbsolutePath() + File.separator
				+ name.substring(name.lastIndexOf("/")+1));
		try {
			if (!audioFile.exists()) {
				audioFile.createNewFile();
			}
			
			
		} catch (IOException e) {
		}
		
		HttpUtils http = new HttpUtils();
		http.download(msg.getContent(),audioFile.getAbsolutePath(),
		    new RequestCallBack<File>() {

		        @Override
		        public void onStart() {
		        	 System.out.println("开始下载");
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	 System.out.println("正在下载");
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		           System.out.println(responseInfo.result.getPath());
		        }


		        @Override
		        public void onFailure(HttpException error, String msg) {
		            System.out.println();
		        }
		});
		return audioFile.getAbsolutePath();
	}

}