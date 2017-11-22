/**
 * 
 */
package com.yey.kindergaten.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.yey.kindergaten.inter.OnRecordChangeListener;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 语音管理
 * @author chaowen
 *
 */
public  class RecordManager  implements RecordControlListener{
	public static int MAX_TIME = 15;    //最长录制时间，单位秒，0为无时间限制
	public static int MIX_TIME = 1;     //最短录制时间，单位秒，0为无时间限制，建议设为1
	
	private static int SAMPLE_RATE_IN_HZ = 8000; 
	public static final String VOICE_DIR ;
	AtomicBoolean ab = null;
	public static RecordManager recordManager = null;
	 private static Object INSTANCE_LOCK = new Object();
	 ExecutorService  service = null;
	 OnRecordChangeListener onRecordChangeListener;
	 private MediaRecorder mediaRecorder;
	 public static  String filename;
	 private String filepath;
	 private File file;
	 private long startTime;
	 public static int MAX_RECORD_TIME = 60;
	 public static int MIN_RECORD_TIME = 1;
	public RecordManager() {
		ab = new AtomicBoolean(false);
	}
	
	static{
		VOICE_DIR = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()))).append(File.separator).append("yey").append(File.separator).append("voice").toString();
	}
	
	public static RecordManager getInstance(Context context)
    {
        if(recordManager == null)
            synchronized(INSTANCE_LOCK)
            {
                if(recordManager == null)
                	recordManager = new RecordManager();
                recordManager.init(context);
            }
        return recordManager;
    }
	
	
	 public void init(Context context)
	    {
		   
		 service = Executors.newCachedThreadPool();
	    }
	
	@Override
	public void startRecording(String s) {
		filename = null;
		 if(mediaRecorder == null)
	        {
			 mediaRecorder = new MediaRecorder();
			 mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			 mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			 mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			 mediaRecorder.setAudioEncodingBitRate(SAMPLE_RATE_IN_HZ);
			 mediaRecorder.setOnErrorListener(new RecorderErrorListener());
	        } else
	        {
	        	mediaRecorder.stop();
	        	mediaRecorder.reset();
	        }
		    filename = getRecordFileName();
		    filepath = getRecordFilePath(s);
	        file = new File(filepath);
	        mediaRecorder.setOutputFile(file.getAbsolutePath());
	        try
	        {
	        	mediaRecorder.prepare();
	        	mediaRecorder.start();
	        	ab.set(true);
	            startTime = (new Date()).getTime();
	            System.out.println("开始录音:"+startTime);
	            return;
	        }
	        catch(IllegalStateException _ex)
	        {
	          
	        	ab.set(false);
	        	mediaRecorder.release();
	        	mediaRecorder = null;
	            return;
	        }
	        catch(IOException _ex)
	        {
	           System.out.println(_ex);
	        }
	        ab.set(false);
	        mediaRecorder.release();
	        mediaRecorder = null;
		
	}

	@Override
	public void cancelRecording() {
		if(mediaRecorder == null&&!isRecording())
            return;
		mediaRecorder.stop();
		mediaRecorder.release();
		mediaRecorder = null;
        if(file != null && file.exists() && !file.isDirectory())
            file.delete();
        ab.set(false);
		
	}

	@Override
	public int stopRecording() {
		if(mediaRecorder != null&&isRecording())
        {
            ab.set(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            
            long endtime = (new Date()).getTime();
            System.out.println("停止录音:"+endtime);
            int i = (int)((new Date()).getTime() - startTime) / 1000 ;
            return i ;
        } else
        {
            return 0;
        }
	}

	@Override
	public boolean isRecording() {
		 return ab.get();
	}

	@Override
	public MediaRecorder getMediaRecorder() {
		return mediaRecorder;
	}

	@Override
	public String getRecordFilePath(String path) {
		/*if (!path.startsWith("/"))
		{
			path = "/" + path;
		}
		if (!path.contains("."))
		{
			path += ".amr";
		}
		String amrfile = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/my" + path;
		File file = new File(amrfile);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		File appPath = new File(AppConstants.VOICE_DIR +File.separator+ path);
		if(!appPath.exists()){
			appPath.mkdirs();
		}
		
		String amrfile =  AppConstants.VOICE_DIR +File.separator+ path+File.separator+filename;
		File file = new File(amrfile);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file.getAbsolutePath();
	}
	
	
	public void setOnRecordChangeListener(OnRecordChangeListener onrecordchangelistener)
    {
		onRecordChangeListener = onrecordchangelistener;
		
    }

	
	private class RecorderErrorListener
	implements android.media.MediaRecorder.OnErrorListener
	{

		public void onError(MediaRecorder mediarecorder, int i, int j)
		{
			
		}

		public RecorderErrorListener()
		{
		}
	}
   
	public String getRecordFileName()
    {
        return (new StringBuilder(String.valueOf(System.currentTimeMillis()))).append(".aac").toString();
    }
	
	public double getAmplitude() {		
		if (getMediaRecorder() != null){			
			return  (getMediaRecorder().getMaxAmplitude());		
			}		
		else			
			return 0;	
		}

}
