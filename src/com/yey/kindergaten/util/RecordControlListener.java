package com.yey.kindergaten.util;

import android.media.MediaRecorder;

public interface  RecordControlListener {
	public abstract void startRecording(String s);

    public abstract void cancelRecording();

    public abstract int stopRecording();

    public abstract boolean isRecording();

    public abstract MediaRecorder getMediaRecorder();

    public abstract String getRecordFilePath(String s);
}
