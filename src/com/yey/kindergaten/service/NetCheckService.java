package com.yey.kindergaten.service;

import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.NetworkMonitor;
import com.yey.kindergaten.util.NetworkMonitor.NetworkReceiver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class NetCheckService extends Service{

	private boolean isflag=true;
	 
    private  IBinder myBind=new BinServiceBind();  

	@Override
	public IBinder onBind(Intent arg0) {	
		return myBind;
	}
	
	@Override  
    public void onCreate() {  
	        super.onCreate();   
	        myBind = new BinServiceBind();  
	    } 
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
 
	 @Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		 if(!thread.isAlive()){
		 thread.start();}
		return super.onStartCommand(intent, flags, startId);
	}
	 
	 Thread thread=new Thread(new Runnable() {		
		@Override
		public void run() {		  
           while (true) {   
//	 		if(AppUtils.isNetworkAvailable(NetCheckService.this)){
//				 isflag=true;
//				 MainActivity.serviceHandler.sendEmptyMessage(111);//表示有网络
//			}else{
//				 isflag=false;
//				 MainActivity.serviceHandler.sendEmptyMessage(000);//表示没有网络
//			}			
		 }			
		}
	});
		
	   @Override    
	    public boolean onUnbind(Intent intent) {    
	         // 所有的客户端使用unbindService()解除了绑定     
	         return false;    
	     }  
	 
	    public class BinServiceBind extends Binder{   
	        public NetCheckService getBinServiceBind(){        
	            return NetCheckService.this;  
	        }  
	  
	        public boolean isNetAvaliable(){              
	            return isflag;  
	        }  
	  
	    }
	    
//	    public  void SetHandler(Handler handler){
//	    	this.handler=handler;
//	    }
}
