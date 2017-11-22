package com.yey.kindergaten.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.R;

public class ServicePointexchangeExplainActivity extends Activity implements OnClickListener{

	TextView titletextview;   //通讯录
	ImageView leftbtn;      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_pointexchange_explain);		
		FindViewById();
	    initData();
	    initView();
	    setOnClick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setText(R.string.service_exchangeexplain);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);   
    	 leftbtn.setVisibility(View.VISIBLE);  
    	
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
		  this.finish();
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


