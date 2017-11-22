package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.util.AppConstants;


public class ServicePointexchangeHisActivity extends Activity implements OnClickListener{

	ListView listView ;	
	ServiceAdapter adapter;
	TextView titletextview;   //通讯录
	ImageView leftbtn;      
	ImageView rightbtn;  
	List<String> list;
	String type="";  //selectaddressbook  和manage状态
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_pointexchangehis);		
		FindViewById();
	    initData();
	    initView();
	    setOnClick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setText(R.string.service_exchangehistory);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);   
    	 leftbtn.setVisibility(View.VISIBLE);  
//    	 rightbtn=(ImageView) findViewById(R.id.left_btn);   
//    	 rightbtn.setVisibility(View.VISIBLE);  
    	 listView=(ListView) findViewById(R.id.service_pointexchangehis_listview);
    	
     }
     
     public void initData()
     {
    	 list=new ArrayList<String>();
    	 list.add("a"); list.add("a"); list.add("a"); list.add("a"); list.add("a");
    	 adapter=new ServiceAdapter(this,list,AppConstants.SERIVCE_POINTEXCHANGEHIS);
    
     }
     
     public void setOnClick()
     {
    	 leftbtn.setOnClickListener(this);    	
        // rightbtn.setOnClickListener(this);    	
    	
     }

     public void  initView()
     {
    	 listView.setAdapter(adapter);
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


