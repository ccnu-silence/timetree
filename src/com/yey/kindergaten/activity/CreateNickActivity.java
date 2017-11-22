/**
 * 系统项目名称
 * com.yey.kindergaten.activity
 * CreateNickActivity.java
 * 
 * 2014年7月15日-上午11:19:54
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;

/**
 * 设置昵称
 * CreateNickActivity
 * chaowen
 * 511644784@qq.com
 * 2014年7月15日 上午11:19:54
 * @version 1.0.0
 * 
 */
public class CreateNickActivity extends BaseActivity{
	@ViewInject(R.id.btn_createnick_inApp)Button btn_app;
	@ViewInject(R.id.header_title)TextView tv_title;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	@ViewInject(R.id.ed_regester_nickname)EditText et_nickname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_createnick);
		ViewUtils.inject(this);
		prepareView();
	}
	
	private void prepareView() {
		tv_title.setText("注册");
		iv_left.setVisibility(View.VISIBLE);
	}

	@OnClick({R.id.btn_createnick_inApp,R.id.left_btn})
	public void viewOnClikc(View view){
		switch (view.getId()) {
		case R.id.btn_createnick_inApp:
			if(et_nickname.getText().toString().trim().equals("")){
				showToast("请填写呢称");
				return ;
			}
			showLoadingDialog("正在提交");
			final AccountInfo info = AppServer.getInstance().getAccountInfo();
			AppServer.getInstance().modifySelfInfo(info.getUid(), info.getAvatar()+"", et_nickname.getText().toString(),info.getGender()+"",info.getLocation()+"",info.getRealname()+"", info.getTelephone()+"", info.getBirthday()+"", new OnAppRequestListener() {
				
				@Override
				public void onAppRequest(int code, String message, Object obj) {
					cancelLoadingDialog();
					if(code == AppServer.REQUEST_SUCCESS){
						info.setNickname(et_nickname.getText().toString()+"");
						 SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString(AppConstants.PREF_NICKNAME, et_nickname.getText().toString());
						 DbHelper.updateAccountInfo(info);
						 openActivity(MainActivity.class);
						finish();
					}else{
						showToast(message);
					}
					
				}
			});
			
			
			break;
		case R.id.left_btn:
			new AlertDialog.Builder(this).setTitle("还未填写昵称是否退出").setPositiveButton("确定", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openActivity(LoginActivity.class);
					finish();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                         
				}
			}).show();
			
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			new AlertDialog.Builder(this).setTitle("还未填写昵称是否退出").setPositiveButton("确定", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openActivity(LoginActivity.class);
					finish();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                         
				}
			}).show();
		}
		return super.onKeyDown(keyCode, event);
	}
}
