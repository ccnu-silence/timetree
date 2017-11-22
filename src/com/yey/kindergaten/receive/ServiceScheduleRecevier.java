package com.yey.kindergaten.receive;


import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ServiceScheduleWriteActivity;
import com.yey.kindergaten.bean.SchedulesBean;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.DialogTips;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * 日程提醒接受
 * @author zy
 */
public class ServiceScheduleRecevier extends BroadcastReceiver{

	/**主题图片url列数组*/
    private static final int[]imgsrc={R.drawable.service_schedule_workcheck,R.drawable.service_scheduke_meeting,
    	         R.drawable.service_schedule_activity,R.drawable.service_schedule_dating,R.drawable.service_schedule_traning,
    	               R.drawable.service_scheduke_important,R.drawable.service_schedule_party,R.drawable.service_schedule_others};
   /**主题名称数组*/
    private static final String[]theme={"工作检查","开会","园所活动","约见","培训","重要日子","聚会","其他" };
	
	private Context contexts;
	private SchedulesBean bean;
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {		

		this.contexts=context;
	    Bundle  bundle=intent.getExtras();
	    int id=bundle.getInt("id",1);
	    String updatedata=bundle.getString("update");
	    String updatetime=bundle.getString("updatetime");
	    bean=(SchedulesBean) bundle.getSerializable("bean");
	    String comm=bundle.getString("comments");
	    String type=bundle.getString("type");
	    String themes=bundle.getString("theme");
	    if(ServiceScheduleWriteActivity.alarmlist!=null&&
	    		ServiceScheduleWriteActivity.alarmlist.size()!=0){    	
	   	for(int i=0;i<ServiceScheduleWriteActivity.alarmlist.size();i++){
	     	if(ServiceScheduleWriteActivity.alarmlist.get(i).getRequestcode()==id){
	    			ServiceScheduleWriteActivity.alarmlist.remove(i);
	    	 }
	      }	    	
	    }	 	   
	    int ids=0;	  
	    for(int i=0;i<theme.length;i++){
	    	if(theme[i].equals(themes)){
	    		ids=i;
	    	}
	    }
	    PendingIntent pendintent=bundle.getParcelable("intent");	    
	    NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    Notification notification = new Notification(imgsrc[ids], "闹钟响咯..", System.currentTimeMillis());  
	     
	    Intent intents = null;
		try {
			intents = new Intent(context.getApplicationContext(),Class.forName("com.yey.kindergaten.activity.ServiceScheduleWriteActivity"));
		  
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Bundle bundles = new Bundle();  
		bundles.putSerializable(AppConstants.Schedule_Bean, bean);
		intents.putExtras(bundle);   
		intents.putExtra("type", type);
		intents.putExtra("state", "notifischedule");
		intents.putExtra("comments", comm);
		intents.putExtra("theme", themes);
		intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);  
		PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intents,0);   
		notification.contentIntent=pendingIntent;     
        RemoteViews  view=new RemoteViews(context.getPackageName(), R.layout.inflater_notification_schedule_broadcast);
       
        view.setTextViewText(R.id.id_inflater_showimg_content_notification, comm);
        view.setTextViewText(R.id.id_inflater_showimg_time_notification, updatedata);
        view.setImageViewResource(R.id.id_inflater_showimg_theme_notification, imgsrc[ids]);
        notification.contentView=view;    
        notification.sound=Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.notify); 
        notification.defaults=Notification.DEFAULT_VIBRATE;       
        long[] vibrate = {0,100,200,300}; 
        notification.vibrate = vibrate ; 
        notification.flags = Notification.FLAG_AUTO_CANCEL ;       
        notiManager.notify(id, notification);  
        if(bean.getNote()!=null){
        	comm=bean.getNote();
        }
        view.setOnClickPendingIntent(R.id.service_click_notification, pendingIntent);

        showDialog("您有日程到时间了",comm , "我知道了", new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
							
			}
		});
	}
	
	public void showDialog(String title,String message,String buttonText,DialogInterface.OnClickListener onSuccessListener) {
		 DialogTips dialog = new DialogTips(contexts,message, buttonText);
		 // 设置成功事件
		  dialog.SetOnSuccessListener(onSuccessListener);
		  dialog.setTitle(title);
		 // 显示确认对话框
		  dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
		  dialog.show();
		  dialog = null;
	}

}
