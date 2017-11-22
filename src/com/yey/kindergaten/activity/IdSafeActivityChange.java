package com.yey.kindergaten.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountBean;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

public class IdSafeActivityChange extends BaseActivity implements OnClickListener{
	public Boolean isShowRecordFragmnet;
	@ViewInject(R.id.header_title)TextView tv_title;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	@ViewInject(R.id.idsafe_bangdetv)TextView bangdetv;
	@ViewInject(R.id.idsafe_bangdebtn)TextView bangdebtn;

    @ViewInject(R.id.change_phone_ll)LinearLayout change_ll;
    @ViewInject(R.id.show_ischecked_phone)ImageView ischecked_iv;
    @ViewInject(R.id.show_ischecked_phone_tv)TextView ischecked_tv;

	AccountInfo accountInfo;
	Boolean isbinding;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_idsafe_main_change);
		ViewUtils.inject(this);
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(accountInfo.getPhone()==null||accountInfo.getPhone().equals("")){
			isbinding=false;
		}else{
			isbinding=true;
		}
		prepareView();
	}
	
	private void prepareView() {
		iv_left.setVisibility(View.VISIBLE);
		if(isbinding){
            ischecked_iv.setImageResource(R.drawable.checkbox_true);
            tv_title.setText("取消绑定");
			bangdetv.setText(accountInfo.getPhone());
			bangdebtn.setText("取消绑定");
		}else{
            ischecked_iv.setImageResource(R.drawable.checkbox_false);
            ischecked_tv.setText("未绑定");
            ischecked_tv.setTextColor(getResources().getColor(R.color.base_color_text_black));
            tv_title.setText("绑定手机");
			bangdetv.setText("手机号");
			bangdebtn.setText("绑定手机");
		}
	}
	
	@OnClick({(R.id.left_btn),(R.id.change_phone_ll)})
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.change_phone_ll:
			if(isbinding){
                showLoadingDialog("正在取消绑定..");
                AppServer.getInstance().bindPhone(accountInfo.getUid(), "", "", new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        cancelLoadingDialog();
                        if (code == AppServer.REQUEST_SUCCESS) {
                            showToast(message);
                            accountInfo.setPhone("");
                            AppServer.getInstance().setmAccountInfo(accountInfo);
                            AppServer.getInstance().setmAccountBean(new AccountBean(accountInfo));
                            DbHelper.updateAccountInfo(accountInfo);
                            IdSafeActivityChange.this.finish();
                        } else {
                            showToast(message);
                        }
                    }
                });
			}else{
				intent=new Intent(IdSafeActivityChange.this,BindPhoneActivity.class);
				startActivity(intent);
                finish();
			}
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent=new Intent(this,IdSafeActivity.class);
			startActivity(intent);
			this.finish();
		}
		return false;
	}
}
