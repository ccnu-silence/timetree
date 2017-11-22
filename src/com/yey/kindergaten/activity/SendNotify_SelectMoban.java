package com.yey.kindergaten.activity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.NotificationMobanInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SendNotify_SelectMoban extends BaseActivity implements OnClickListener, OnItemClickListener{
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	
	
	private ListView listview;
	private AccountInfo accountInfo;
	private MobanAdapter adapter;
	private int  type;
	
	private String content;
	private String showtime;
	
	private ArrayList<String>parentlist;
	private ArrayList<String>teacherlist;
	private String state=null;//用来判断是模板分类界面，还是模板内容界面
    private List<String>list=new ArrayList<String>();
    private List<NotificationMobanInfo>listinfo=new ArrayList<NotificationMobanInfo>();
	   @Override
	protected void onCreate(Bundle savedInstanceState) {
		
		  		   
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendnotify_selectmoban);
		ViewUtils.inject(this);
		prepareView();
		
		content=this.getIntent().getStringExtra("content");
		accountInfo=AppServer.getInstance().getAccountInfo();	
		parentlist=this.getIntent().getStringArrayListExtra("parentlist");
		teacherlist=this.getIntent().getStringArrayListExtra("teacherlist");
		showtime=this.getIntent().getStringExtra("time");
		
		AppServer.getInstance().GetTemplateTypes(accountInfo.getUid(), new OnAppRequestListener() {		
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				if(code==AppServer.REQUEST_SUCCESS){
					NotificationMobanInfo[] info=(NotificationMobanInfo[]) obj;
					listinfo=Arrays.asList(info);
					adapter=new MobanAdapter(listinfo);
					listview.setAdapter(adapter);
				}		
			}
		});
	}   
		  private void prepareView(){
			    tv_headerTitle.setText(R.string.sendmsg_msgselectmoban_title);		   
		      	iv_left.setVisibility(View.VISIBLE);
		      	iv_left.setOnClickListener(this);	
		    	listview=(ListView) findViewById(R.id.id_sendnotify_fenleicontent_lv);		       		  
		        listview.setOnItemClickListener(this);
		  }
		  				 
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.left_btn:
				 Intent intent =new Intent(this,SendNotificationActivity.class);	
			     intent.putExtra("content", content);
			     intent.putExtra("time", showtime);
			     intent.putStringArrayListExtra("teacherlist", teacherlist);
			     intent.putStringArrayListExtra("parentlist", parentlist);
			     startActivity(intent);
				 finish();
				break;		
			}			
		}
         class  MobanAdapter extends BaseAdapter {      	 
        	 private List<NotificationMobanInfo>list=new ArrayList<NotificationMobanInfo>();
        	 
        	 public MobanAdapter(List<NotificationMobanInfo>list) {
				this.list=list;
			}
			@Override
			public int getCount() {
				
				return list.size();
			}

			@Override
			public Object getItem(int position) {
		
				return position;
			}

			@Override
			public long getItemId(int position) {
				
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup arg2) {
				
				convertView=LayoutInflater.from(SendNotify_SelectMoban.this).inflate(R.layout.inflater_notification_moban, null);
				TextView tc=(TextView) convertView.findViewById(R.id.id_inflater_show_moban_type_content);				
				tc.setText(list.get(position).getName());
				return convertView;
			}
	

	}
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			     Intent intent=new Intent(SendNotify_SelectMoban.this,NotifyShowMbConActivity.class);
			     intent.putExtra("type", listinfo.get(position).getType());
			     intent.putExtra("name", listinfo.get(position).getName());
			     intent.putExtra("content", content);
			     intent.putExtra("time", showtime);
			     intent.putStringArrayListExtra("teacherlist", teacherlist);
			     intent.putStringArrayListExtra("parentlist", parentlist);
			     startActivity(intent);
			     this.finish();			     
	    	}
		
		public void onResume() {
			super.onResume();
			MobclickAgent.onResume(this);
		}
		public void onPause() {
			super.onPause();
			MobclickAgent.onPause(this);
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode==KeyEvent.KEYCODE_BACK){
				Intent intent =new Intent(this,SendNotificationActivity.class);	
			     intent.putExtra("content", content);
			     intent.putExtra("time", showtime);
			     intent.putStringArrayListExtra("teacherlist", teacherlist);
			     intent.putStringArrayListExtra("parentlist", parentlist);
			     startActivity(intent);
				 finish();
				this.finish();
			}
			return super.onKeyDown(keyCode, event);
		}
}
