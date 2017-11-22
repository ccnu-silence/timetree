package com.yey.kindergaten.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.AddressPickWight;

public class ServicePointExchangeSureActivity extends Activity implements OnClickListener{

	
	TextView titletextview;   //通讯录
	ImageView leftbtn;       //右边点击的
	List<String> list;
	String type="";
	TextView resultetextview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_pointexchangesure);		
		FindViewById();
	    initData();
	    initView();
	    setOnClick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setText(R.string.service_pointsexchange);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);   
    	 leftbtn.setVisibility(View.VISIBLE);  
    	 resultetextview=(TextView) findViewById(R.id.service_pointexchange_resulttv);
    	 if(type.equals(AppConstants.SERIVCE_POINTEXCHANGESURE)){
    		 resultetextview.setVisibility(View.VISIBLE); 
    	 }else{  		
    		 resultetextview.setVisibility(View.GONE);
    	 }   
     }
     
     public void initData()
     {
    	
    
     }
     
     public void setOnClick()
     {
    	 leftbtn.setOnClickListener(this);    
    	
     }

     public void  initView()
     {
    	
     }
     
    

	@Override
	public void onClick(View v) {
		Intent intent;
	switch (v.getId()) {
		case R.id.left_btn:
			 intent=new Intent(ServicePointExchangeSureActivity.this,ServicePointExchangeActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(intent);
			break;			
	
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK){
	    	 Intent intent=new Intent(ServicePointExchangeSureActivity.this,ServicePointExchangeActivity.class);			 
	    	 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	 startActivity(intent);
	    }
		return super.onKeyDown(keyCode, event);
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

