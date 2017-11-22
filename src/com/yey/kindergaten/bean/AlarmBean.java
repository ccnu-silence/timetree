package com.yey.kindergaten.bean;

import android.app.PendingIntent;

/**
 * 用来保存不同的pendingintent
 * @author zy
 * requestcode  对应的sheid
 */
public class AlarmBean {
     public  int requestcode;
     public  PendingIntent intent;
     
     public AlarmBean() {

	}
     
	public  int getRequestcode() {
		return requestcode;
	}
	public  void setRequestcode(int requestcode) {
		this.requestcode = requestcode;
	}
	public  PendingIntent getIntent() {
		return intent;
	}
	public  void setIntent(PendingIntent intent) {
		this.intent = intent;
	}
}
