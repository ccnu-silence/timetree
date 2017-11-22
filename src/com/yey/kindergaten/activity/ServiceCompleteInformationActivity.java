package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

public class ServiceCompleteInformationActivity extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.left_btn)ImageView leftbtn;
	@ViewInject(R.id.header_title)TextView titletv;
	
	@ViewInject(R.id.servicecompleteinfo_nametv)EditText nametv;
	@ViewInject(R.id.servicecompleteinfo_phonetv)EditText phonetv;
	@ViewInject(R.id.servicecompleteinfo_finish)LinearLayout finishly;
	AccountInfo accountInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicecompleteinformation);
		accountInfo=AppServer.getInstance().getAccountInfo();
		ViewUtils.inject(this);
		initview();
	}
	
	
	
	public void initview()
	{
		leftbtn.setVisibility(View.VISIBLE);
		leftbtn.setOnClickListener(this);
		titletv.setVisibility(View.VISIBLE);
		titletv.setText("完善个人信息");
		nametv.setText(accountInfo.getRealname()==null?"":accountInfo.getRealname());
		phonetv.setText(accountInfo.getTelephone()==null?"":accountInfo.getTelephone());
		finishly.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
//			Intent intent=new Intent();
//			setResult(1, intent);
	    	this.finish();
			break;
		case R.id.servicecompleteinfo_finish:
			String name=nametv.getText().toString();
			String telphone=phonetv.getText().toString();
             if(name==null||name.equals("")){
            	 showToast("请输入真实姓名");	           	
            	 return;
             }
             if(telphone==null||telphone.equals("")){
            	 showToast("请输入个人电话");	            	
            	 return;
             }            
             accountInfo.setRealname(name);
             accountInfo.setTelephone(telphone);
             AppServer.getInstance().modifySelfInfo(accountInfo.getUid(), accountInfo.getAvatar(), accountInfo.getNickname(), accountInfo.getGender(), accountInfo.getLocation(), name, telphone, accountInfo.getBirthday(),new OnAppRequestListener() {
				@Override
				public void onAppRequest(int code, String message, Object obj) {
					if(code==0){
						AppServer.getInstance().setmAccountInfo(accountInfo);
						DbHelper.updateAccountInfo(accountInfo);
						Intent intent=new Intent();	
						intent.putExtra(AppConstants.STATE, "aa");
						setResult(0, intent);
						ServiceCompleteInformationActivity.this.finish();
					}else{
						showToast("修改失败");				
					}
				}
			});
             
		default:
			break;
		}
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
//			Intent intent=new Intent();
//			setResult(1, intent);
	    	this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
