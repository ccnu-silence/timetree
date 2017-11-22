package com.yey.kindergaten.activity;

import java.io.UnsupportedEncodingException;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.TabButton;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class BindAccountActivity extends FragmentActivity{
	BindPhoneFregment bindphone=new BindPhoneFregment();
	BindPostBoxFragment bindpostbox=new BindPostBoxFragment();
	public Boolean isShowRecordFragmnet;
	String state="";
	@ViewInject(R.id.tabBtn_left)TabButton tabLeftButton;
	@ViewInject(R.id.tabBtn_right)TabButton tabRightButton;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.header_title)TextView tv_title;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	@ViewInject(R.id.right_tv)TextView tv_topright;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_create_bindaccount);
		if(getIntent().getExtras()!=null){
			state=getIntent().getExtras().getString("state");
		}
		ViewUtils.inject(this);
		Bundle bundle=new Bundle();
		bundle.putString("state", state);
		bindphone.setArguments(bundle);
		bindpostbox.setArguments(bundle);
		prepareView();
	    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fl_createsuccess_contain, bindphone).commit();	
	     resetState(R.id.tabBtn_left);
	}
	
	private void prepareView() {
		if(state.equals(AppConstants.IDSAFE)){
			tv_title.setText("绑定手机");
		}else{
			tv_title.setText("注册");
		}
		iv_left.setVisibility(View.VISIBLE);
		tv_topright.setVisibility(View.VISIBLE);
		if(state.equals(AppConstants.IDSAFE)){
			tv_topright.setVisibility(View.GONE);
		}else{
			tv_topright.setText("跳过");
		}
		
	}


	@OnClick({R.id.tabBtn_left,R.id.tabBtn_right,R.id.left_btn,R.id.right_tv})
	public void onclick(View v){
		resetState(v.getId());
		switch (v.getId()) {
		case R.id.tabBtn_left:
			tabLeftButton.setSelected(true);
			setBindPhoneFragment();
			break;
		case R.id.tabBtn_right:
			tabRightButton.setSelected(true);
			setBindPostBoxFragment();
			break;
		case R.id.left_btn:
			Intent intent;
			if(state.equals(AppConstants.IDSAFE)){
			    intent=new Intent(this,IdSafeActivity.class);
			    startActivity(intent);
				finish();
			}else if(state.equals(AppConstants.TASKMAIN)){
				finish();
			}else{			
				showDialog("提示", "只差一步就完成注册哦", "确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent=new Intent(BindAccountActivity.this,LoginActivity.class);
						startActivity(intent);
						finish();
					}
				},new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
//						 Toast.makeText(AppContext.getInstance(), "", Toast.LENGTH_SHORT).show();
					}
				});
			    
			}
			
			break;
		case R.id.right_tv:			
		    intent=new Intent(BindAccountActivity.this,CreateNickActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
	private void setBindPostBoxFragment() {
		FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
		 bindphone.onPause();
		 if(bindpostbox.isAdded()){
			 bindpostbox.onResume();
		 }else{
			 ft.add(R.id.fl_createsuccess_contain, bindpostbox);
		 }
		ft.hide(bindphone);
		ft.show(bindpostbox);
        ft.commit();
		
	}
	private void setBindPhoneFragment() {
		FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
		 bindpostbox.onPause();
		 if(bindphone.isAdded()){
			 bindphone.onResume();
		 }else{
			 ft.add(R.id.fl_createsuccess_contain, bindphone);
		 }
		ft.hide(bindpostbox);
		ft.show(bindphone);
		ft.commit();
		
		
	}
	
	private void resetState(int id) {
		tabLeftButton.setSelected(false);
		tabRightButton.setSelected(false);
	
		
		// 将点击的按钮背景设置为已选中
		switch (id) {
		case R.id.tabBtn_left:
			tabLeftButton.setSelected(true);
			break;
		case R.id.tabBtn_middle:
			tabRightButton.setSelected(true);
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
	
	public void showDialog(String title,String message,String buttonText,DialogInterface.OnClickListener onSuccessListener,DialogInterface.OnClickListener onCancelListener) {
		DialogTips dialog = new DialogTips(this,title,message, buttonText,true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(onSuccessListener);
		dialog.SetOnCancelListener(onCancelListener);
		// 显示确认对话框
		dialog.show();
	}
}
