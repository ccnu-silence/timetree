package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.FragmentAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.fragment.SreachClassResultFragment;
import com.yey.kindergaten.fragment.SreachGeneralGroupResultFragment;
import com.yey.kindergaten.fragment.SreachKinderResultFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

public class ServiceSreachKinderResultActivity extends BaseActivity implements OnClickListener{

	TextView titletextview;   
	ImageView leftbtn;
	ViewPager viewPager;
	List<Fragment> fragmenlist=new ArrayList<Fragment>();
	SreachKinderResultFragment  kinderResultefg;
	SreachClassResultFragment   classResultefg;
	SreachGeneralGroupResultFragment   generalGroupfg;
	FragmentAdapter adapter;
	String sreachvalue; 
	TextView nodatatv;
	AccountInfo accountInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_sreachkinderresulte);
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(getIntent().getExtras()!=null){
			sreachvalue=getIntent().getExtras().getString(AppConstants.SREACHGROUPVALUE);
		}
		FindViewById();
		initdata();
	}
	
	 public void initdata()
	 {
		 showLoadingDialog("正在加载");
		 GroupInfoServer.getInstance().LookGroupDataByNum(accountInfo.getUid(), sreachvalue, new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {			
				if(code==0){
					Bundle bundle=new Bundle();
					GroupInfoBean groupInfoBean=(GroupInfoBean) obj;				
					if(groupInfoBean!=null){
						bundle.putSerializable(AppConstants.GROUPBEAN, groupInfoBean);
						if(groupInfoBean.getGtype()==1){  //幼儿园群
							 fragmenlist.clear();
							 kinderResultefg.setArguments(bundle);
							 fragmenlist.add(kinderResultefg);				
					    	 adapter=new FragmentAdapter(getSupportFragmentManager(), fragmenlist);
					 		 viewPager.setAdapter(adapter);
						}else if(groupInfoBean.getGtype()==2){  //班级群
							 fragmenlist.clear();
							 classResultefg.setArguments(bundle);
							 fragmenlist.add(classResultefg);
					    	 adapter=new FragmentAdapter(getSupportFragmentManager(), fragmenlist);
					 		 viewPager.setAdapter(adapter);
						}else{
							 fragmenlist.clear();
							 generalGroupfg.setArguments(bundle);
							 fragmenlist.add(generalGroupfg);
					    	 adapter=new FragmentAdapter(getSupportFragmentManager(), fragmenlist);
					 		 viewPager.setAdapter(adapter);
						}
						 titletextview.setText(groupInfoBean.getGname());
					}else{
						nodatatv.setVisibility(View.VISIBLE);
					}
				}else{
					nodatatv.setVisibility(View.VISIBLE);
				}
				cancelLoadingDialog();
			}
		});
	 }
	
	 public void FindViewById()
     {
		 viewPager=(ViewPager) findViewById(R.id.viewpage);
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setVisibility(View.VISIBLE);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);
    	 leftbtn.setVisibility(View.VISIBLE);
    	 leftbtn.setOnClickListener(this);  	
    	 nodatatv=(TextView) findViewById(R.id.nodatatv);
    	 kinderResultefg=new SreachKinderResultFragment();
    	 classResultefg=new SreachClassResultFragment();
    	 generalGroupfg=new SreachGeneralGroupResultFragment();
    	
     }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			Intent intent=new Intent(this,ServiceAddKinderActivity.class);
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
			Intent intent=new Intent(this,ServiceAddKinderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
		
	}
}
