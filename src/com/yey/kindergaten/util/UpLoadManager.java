/**
 * 
 */
package com.yey.kindergaten.util;

import java.io.File;

import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.receive.AppEvent;

import de.greenrobot.event.EventBus;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * @author chaowen
 *  上传管理
 */
public class UpLoadManager {
	    // 上传失败状态
		public final static int UPLOAD_FAILL = -1;
		// 上传成功状态
		public final static int UPLOAD_SUCCESS = 1;
		// 上传更新状态
		public final static int UPLOAD_UPDATE = 2;
		// 上传暂停
		public final static int UPLOAD_PAUSE = 3;
		// 上传开始
		public final static int UPLOAD_START = 4;
		private Context context;
        public static boolean isupload = false;
		// 是否上传
		private boolean start = true;

		private static UpLoadManager uploadManager;

		private UploadThread runnable;

		public static UpLoadManager getUpManager() {

			if (uploadManager == null) {
				uploadManager = new UpLoadManager();
			}
			return uploadManager;
		}
        private File currentfile;
		/**
		 * 上传方法方法
		 * 
		 * @param context
		 * @param application
		 * @param progressBar
		 * @param progressText
		 * @param stopButton
		 * @param savePath
		 * @param param
		 */
	/*	public void oneUpLoad(final Context context, AppContext appcontext,
				final File file, final ProgressBar progressBar,
				final TextView progressText, final Button stopButton,
				String savePath,String param) {
			this.context = context;
			this.currentfile = file;
			final UploadDB dbManager = new UploadDB(context);
			Long fileid = dbManager.getBindId(file.getAbsolutePath());
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == UPLOAD_FAILL) {
						Long fileid = dbManager.getBindId(file.getAbsolutePath());
						Long length = (Long) msg.obj;
						dbManager.updateUpload(fileid, length);
						//stopButton.setVisibility(View.VISIBLE);
						//stopButton.setText("继续上传");
						//EventBus.getDefault().post(new AppEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, UPLOAD_FAILL, null,progressBar,progressText,stopButton,file));
					} else if (msg.what == UPLOAD_SUCCESS) {
						stopButton.setText("上传成功");
						Long fileid = dbManager.getBindId(file.getAbsolutePath());
						runnable.setStart(false, fileid);
						dbManager.delUpload(file.getAbsolutePath());
						
						
						//EventBus.getDefault().post(new AppEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, UPLOAD_SUCCESS, null,progressBar,progressText,stopButton,file));
					} else if (msg.what == UPLOAD_UPDATE) {
						stopButton.setVisibility(View.GONE);
						stopButton.setText("暂停上传");				
						Long length = (Long) msg.obj;
						Long fileid = dbManager.getBindId(file.getAbsolutePath());
						dbManager.updateUpload(fileid, length);
						// 当前进度值
						int progress = (int) (((float) length / file.length()) * 100);

						progressBar.setProgress(progress);

						progressText.setText(FileUtils.formatFileSize(length) + "/"
								+ FileUtils.formatFileSize(file.length()));
						//EventBus.getDefault().post(new AppEvent(AppEvent.SERVICE_CLASSPHOTO_UPLOADING, UPLOAD_UPDATE, length,progressBar,progressText,stopButton,file));
					}
				}
			};
			
			if(runnable!=null){
				runnable.setDbManager(dbManager);
				runnable.setFile(file);
				runnable.setHandler(handler);
				runnable.setParam(param);
				runnable.setSavePath(savePath);
			 
			}else{
				runnable = new UploadThread(appcontext, dbManager, handler, file,
						savePath,param);
				
				runnable.start();
			}
			
			
			
		}*/

		public void setStart(boolean start,Long id) {
			this.start = start;
			runnable.setStart(start,id);
		}

		public boolean isStart() {
			return start;
		}

		public File getCurrentfile() {
			return currentfile;
		}

		public void setCurrentfile(File currentfile) {
			this.currentfile = currentfile;
		}
		
		
}
