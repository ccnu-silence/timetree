package com.yey.kindergaten.receive;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yey.kindergaten.activity.BindPhoneFregment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSBroadCast extends BroadcastReceiver{

    private static MessageListener mMessageListener;

    public interface MessageListener {
        public void onReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
	   {
		Bundle bundle=intent.getExtras();
		if(bundle!=null){	
			Object[] objArray=(Object[]) bundle.get("pdus");
			SmsMessage[] message=new SmsMessage[objArray.length];
			for(int i=0;i<objArray.length;i++){
				message[i]=SmsMessage.createFromPdu((byte[]) objArray[i]);
				String phonenumber=message[i].getOriginatingAddress();
				String phonecontent = null;
				try {
					phonecontent = new String(message[i].getDisplayMessageBody().getBytes(),"UTF-8");			
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}		
				if(phonecontent!=null){
					if(phonecontent.contains("【幼儿园】")){
						 String code = null;
			             Pattern pattern = Pattern.compile("\\【[0-9]{4}\\】");
			             Matcher matcher = pattern.matcher(phonecontent);
			             while(matcher.find()){
			            	 code=matcher.group();
			              }
			             if(code!=null){
			            	 code=code.substring(1,code.length()-1);
                             mMessageListener.onReceived(code);
							 BindPhoneFregment.smsCallback.smsBack(code);
			             }
					}
				}
			}
		  }
		}
	}
}
