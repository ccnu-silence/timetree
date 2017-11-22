package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat.OnCloseListenerCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.GradeInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

public class ServiceSelectGradeActivity extends  BaseActivity implements OnItemClickListener,OnClickListener{

	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.header_title)TextView titletext;
	@ViewInject(R.id.getaddress_listview)ListView listView;
	List<GradeInfo> list = new ArrayList<GradeInfo>();
	int   clicknum=0;
	ServiceAdapter adapter;
	AccountInfo accountInfo;
	String gradename;
	int gradeid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getaddressactivity);
		accountInfo=AppServer.getInstance().getAccountInfo();
		ViewUtils.inject(this);
		initView();
		initdata();
		
	}
	
	public void initView()
	{
		left_btn.setVisibility(View.VISIBLE);
		left_btn.setOnClickListener(this);
		titletext.setVisibility(View.VISIBLE);
		titletext.setText("选择班级");
	}
	
	public void initdata()
	{
		GroupInfoServer.getInstance().GetGradelist(accountInfo.getUid(), new OnAppRequestListener() {
			
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				if(code==0){
					GradeInfo []gradeInfos=(GradeInfo[]) obj;
					list=Arrays.asList(gradeInfos);
				}
				adapter=new ServiceAdapter(ServiceSelectGradeActivity.this, list, AppConstants.SERVICEGRADELIST);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(ServiceSelectGradeActivity.this);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		        GradeInfo gradeInfo=list.get(position);
			    Intent intent=new Intent();
		    	intent.putExtra(AppConstants.GRADEID, gradeInfo.getGradeid());
		    	intent.putExtra(AppConstants.GRADENAME, gradeInfo.getGradename());
		    	setResult(3, intent);
		    	this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			Intent intent=new Intent();
	    	setResult(3, intent);
	    	this.finish();
			break;

		default:
			break;
		}

		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent();
	    	setResult(3, intent);
	    	this.finish();
	    }
	  	return  false;
	}
	
	public String getGradename() {
		return gradename;
	}

	public void setGradename(String gradename) {
		this.gradename = gradename;
	}

	public int getGradeid() {
		return gradeid;
	}

	public void setGradeid(int gradeid) {
		this.gradeid = gradeid;
	}

}
