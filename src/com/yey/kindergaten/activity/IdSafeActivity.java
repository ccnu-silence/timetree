package com.yey.kindergaten.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.net.AppServer;

import java.util.ArrayList;

public class IdSafeActivity extends BaseActivity implements OnClickListener{

	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<String> alist = new ArrayList<String>();
	@ViewInject(R.id.left_btn)ImageView leftbtn;
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.idsafe_updatepw)RelativeLayout uppw;
	@ViewInject(R.id.idsafe_bangdephone)RelativeLayout bangdephone;
	@ViewInject(R.id.idsafe_tv)TextView phoneText;
	AccountInfo accountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_idsafe_main);
    	accountInfo = AppServer.getInstance().getAccountInfo();
    	ViewUtils.inject(this);
    	initView();
    }

	private void initView() {
		titletv.setText("账号安全");
		leftbtn.setVisibility(View.VISIBLE);
	}

	@OnClick({(R.id.idsafe_updatepw),(R.id.idsafe_bangdephone),(R.id.left_btn)})
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.idsafe_updatepw:
                intent = new Intent(IdSafeActivity.this, IdSafeActivityAmend.class);
                startActivity(intent);
                finish();
                break;
            case R.id.idsafe_bangdephone:
                intent = new Intent(IdSafeActivity.this, IdSafeActivityChange.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
		}
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
        if (accountInfo.getPhone()!=null && !accountInfo.getPhone().equals("")) {
            phoneText.setText(accountInfo.getPhone());
        } else {
            phoneText.setText("该账号尚未绑定手机号");
        }
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return false;
	}

}
