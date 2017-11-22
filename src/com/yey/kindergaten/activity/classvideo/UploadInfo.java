package com.yey.kindergaten.activity.classvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.bokecc.sdk.mobile.upload.Uploader;
import com.bokecc.sdk.mobile.upload.VideoInfo;
import com.yey.kindergaten.util.ParamsUtil;

import java.lang.reflect.Method;

public class UploadInfo {
	
	public final static String UPLOAD_PRE = "U_";
	
	private String uploadId;
	
	private VideoInfo videoInfo;
	
	private int status;
	
	private int progress;
	
	private String progressText;

    /**
     * @param uploadId
     * @param videoInfo
     * @param status 上传状态
     * @param progress
     * @param progressText
     */
	public UploadInfo(String uploadId, VideoInfo videoInfo, int status, int progress, String progressText) {
		super();
		this.uploadId = uploadId;
		this.videoInfo = videoInfo;
		this.status = status;
		this.progress = progress;
		this.progressText = progressText;
	}
	
	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public VideoInfo getVideoInfo() {
		return videoInfo;
	}

	public void setVideoInfo(VideoInfo videoInfo) {
		this.videoInfo = videoInfo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public String getProgressText() {
		if (progressText == null) {
			String fileSizeStr = ParamsUtil.byteToM(ParamsUtil.getLong(videoInfo.getFileByteSize())).concat("M");
			if (status == Uploader.FINISH) {
				progressText = fileSizeStr.concat(" / ").concat(fileSizeStr);
				
			} else {
				progressText = "0M / ".concat(fileSizeStr);
			}
		}
		
		return progressText;
	}

	public void setProgressText(String progressText) {
		this.progressText = progressText;
	}

	public String getStatusInfo(){
		String statusInfo = "";
		switch (status) {
		case Uploader.WAIT:
			statusInfo = "等待中";
			break;
		case Uploader.UPLOAD:
			statusInfo = "上传中";
			break;
		case Uploader.PAUSE:
			statusInfo = "已暂停";
			break;
		case Uploader.FINISH:
			statusInfo = "已上传";
			break;
		default:
			statusInfo = "上传失败";
			break;
		}
		
		return statusInfo;
	}
	
	public Bitmap getBitmap(Context context){
		Bitmap bitmap = getVideoFirstFrame(context, Uri.parse(videoInfo.getFilePath()));
		return bitmap;
	}
    /**
     * 截取视频第一帧
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getVideoFirstFrame(Context context, Uri uri) {
        Bitmap bitmap = null;
        String className = "android.media.MediaMetadataRetriever";
        Object objectMediaMetadataRetriever = null;
        Method release = null;
        try {
            objectMediaMetadataRetriever = Class.forName(className).newInstance();
            Method setDataSourceMethod = Class.forName(className).getMethod("setDataSource", Context.class, Uri.class);
            setDataSourceMethod.invoke(objectMediaMetadataRetriever, context, uri);
            Method getFrameAtTimeMethod = Class.forName(className).getMethod("getFrameAtTime");
            bitmap = (Bitmap) getFrameAtTimeMethod.invoke(objectMediaMetadataRetriever);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (release != null) {
                    release.invoke(objectMediaMetadataRetriever);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
