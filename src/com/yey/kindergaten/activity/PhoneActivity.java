package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;

public class PhoneActivity extends BaseActivity{
	@ViewInject(R.id.right_btn)ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_phone);
		ViewUtils.inject(this);
		initView();
	}
	private void initView() {
		iv.setVisibility(View.VISIBLE);		
	}	
	@OnClick(R.id.right_btn)
	public void OnClick(View v){
		switch (v.getId()) {
		case R.id.right_btn:
			Intent a=new Intent(PhoneActivity.this, SetPasswordActivity.class);
			startActivity(a);
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
