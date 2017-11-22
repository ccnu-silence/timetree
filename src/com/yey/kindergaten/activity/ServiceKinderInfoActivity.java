package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.FragmentAdapter;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.KindergartenInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.ClassGroupInfoFragment;
import com.yey.kindergaten.fragment.GeneralGroupFragment;
import com.yey.kindergaten.fragment.KinderGroupInforFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.MyListViewWithScrollView;

public class ServiceKinderInfoActivity extends BaseActivity implements OnClickListener{

	@ViewInject(R.id.left_btn)	ImageView leftbtn;       
	@ViewInject(R.id.header_title)TextView titletextview;       
	@ViewInject(R.id.viewpage) ViewPager viewPager;
	KinderGroupInforFragment kinderInfofragment;
	GeneralGroupFragment     generalinfofragment;
	ClassGroupInfoFragment   classgroupinfofragment;
	AccountInfo accountInfo;
	List<Fragment> list=new ArrayList<Fragment>();
	FragmentAdapter adapter;
	int gnum;
	GroupInfoBean groupinfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kindergarden_main);
    	accountInfo=AppServer.getInstance().getAccountInfo();
    	ViewUtils.inject(this);
    	if(getIntent().getExtras()!=null){
    	 gnum=getIntent().getExtras().getInt(AppConstants.GNUM);
    	}
    	findview();
    	setonclick();
    	kinderInfofragment=new KinderGroupInforFragment();
    	classgroupinfofragment=new ClassGroupInfoFragment();
    	generalinfofragment=new GeneralGroupFragment();  
    	initdata();
	}
	
	public void initdata()
	{
		GroupInfoServer.getInstance().LookGroupDataByNum(accountInfo.getUid(), gnum+"", new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {
		         if(code==0){
		        	 groupinfo=(GroupInfoBean) obj;
		        	 Bundle bundle=new Bundle();
		        	 bundle.putSerializable(AppConstants.GROUPBEAN, groupinfo);
		        	 if(groupinfo!=null){
		        		 if(groupinfo.getGtype()==1){
			        		 kinderInfofragment.setArguments(bundle);			        	
			        		 list.add(kinderInfofragment);			        		
			        	 }else if(groupinfo.getGtype()==2){
			        		 classgroupinfofragment.setArguments(bundle);			        	
			        		 list.add(classgroupinfofragment);
			        		 
			        	 }else{
			        		 generalinfofragment.setArguments(bundle);		        		
			        		 list.add(generalinfofragment); 
			        		 
			        	 }
		        		 titletextview.setText(groupinfo.getGname());
		        	 }	    		        	  
		          }
		         adapter=new FragmentAdapter(getSupportFragmentManager(), list);
		     	 viewPager.setAdapter(adapter);
			}
		});
	}
	
	 public void findview()
	 {
   	    leftbtn.setVisibility(View.VISIBLE);
   	    leftbtn.setOnClickListener(this); 	
   	    titletextview.setVisibility(View.VISIBLE);		

	 }

	 public void setonclick()
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
