package com.yey.kindergaten.receive;

import com.igexin.sdk.PushManager;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.service.ServiceBoot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootCompletedReceiver extends BroadcastReceiver {  
	  
    @Override  
    public void onReceive(Context context, Intent intent) {  
    	if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
    		 Toast.makeText(context, "启动", Toast.LENGTH_SHORT).show();
    	     context.startService(new Intent(context, ServiceBoot.class));
		}
        
    }  
}  
