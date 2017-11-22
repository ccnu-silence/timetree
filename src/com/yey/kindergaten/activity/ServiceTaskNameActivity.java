package com.yey.kindergaten.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.R;


public class ServiceTaskNameActivity extends Activity implements OnClickListener{

	TextView titletextview;   //通讯录
	ImageView rightbtn;       //右边点击的
	ImageView leftbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_taskmain);
		FindViewById();
    	initData();
    	initView();
    	setOnClick();
	}
	
	public void FindViewById()
    {
   	 titletextview=(TextView) findViewById(R.id.header_title);
   	 titletextview.setText(R.string.service_taskname);
   	 rightbtn=(ImageView) findViewById(R.id.right_btn);
   	 rightbtn.setVisibility(View.VISIBLE);  	
   	 leftbtn=(ImageView) findViewById(R.id.left_btn);
   	 leftbtn.setVisibility(View.VISIBLE);  	
    }
    
    public void initData()
    {
    	
    }
    
    public void setOnClick()
    {
   	 rightbtn.setOnClickListener(this);
   	 leftbtn.setOnClickListener(this);
    }

    public void  initView()
    {
    	
    }
    
	@Override
	public void onClick(View v) {
	switch (v.getId()) {
		case R.id.left_btn:
	   	      this.finish();
			break;		
		case R.id.right_btn:
			
			break;		
		
		default:
			break;
		}
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
