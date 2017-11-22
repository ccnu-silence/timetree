package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountBean;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.widget.LoadingDialog;

import java.util.Timer;
import java.util.TimerTask;

public class BindPhoneActivity extends BaseActivity {

    AccountInfo accountInfo;
    LoadingDialog loadingDialog;
    String phonecode = "";
    String phon = "";
    String state;
    private int code_time = 60; // 倒计时时间内，不能发送短信（1min）
    private boolean flag = true; // true表示可以发验证码短信，false表示还不能发送
    private Timer timer;
    static int time = 0;
    @ViewInject(R.id.left_btn)ImageView left_iv;
	@ViewInject(R.id.bindphoneet)EditText ed_phone;
	@ViewInject(R.id.bindconfirmpwet)EditText ed_yanzheng;
	@ViewInject(R.id.btn_bindphonefragment_yahnzheng)Button btn_getyanzheng;
	@ViewInject(R.id.btn_bindphonefragment_bind)Button btn_bind;
	@ViewInject(R.id.header_title)TextView tv_head;

    @ViewInject(R.id.text_line1)TextView text_line1;
    @ViewInject(R.id.text_line2)TextView text_line2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 000){
                btn_getyanzheng.setEnabled(flag);
                if (!flag) {
                    btn_getyanzheng.setText(code_time+"秒后再试");
                } else {
                    btn_getyanzheng.setText("获取验证码");
                    code_time = 60;
                    timer.cancel();
                }
            }
            super.handleMessage(msg);
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bindphone_main);
		accountInfo = AppServer.getInstance().getAccountInfo();
		ViewUtils.inject(this);
		tv_head.setText("换绑手机");
        left_iv.setVisibility(View.VISIBLE);
        if (accountInfo.getPhone() == null || accountInfo.getPhone().length() == 0) {
            text_line1.setText("您的账号还未绑定手机");
            text_line2.setText("手机号仅用于找回密码");
            tv_head.setText("绑定手机");
        }
	}

	@OnClick({(R.id.btn_bindphonefragment_yahnzheng),(R.id.btn_bindphonefragment_bind),(R.id.left_btn)})
	public void onClick(View v){
		final String phonenum = ed_phone.getText().toString();
		String tillphonecode = ed_yanzheng.getText().toString();
		switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.btn_bindphonefragment_yahnzheng:
                if (phonenum.equals("")) {
                    showToast("请输入手机号码");
                    return;
                } else if (phonenum.length()!=11) {
                    showToast("请输入合法的手机号码手机号码");
                    return;
                } else {
                    if (loadingDialog!=null) {
                        loadingDialog.show();
                    }
                }
                AppServer.getInstance().getPhoneCode(phonenum, "0", new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
                            // 当发送短信成功后，会开启线程，1min不让再次发送
                            flag = false;
                            timer = new Timer();
                            timer.schedule(new TimesTask(), 0, 1000);
                            phonecode = (String) obj;
//                          ed_yanzheng.setText(phonecode);
//                          System.out.print(phonecode + "~~~~~");
                        } else {
                            showToast("验证码发送失败，请重新发送");
                        }
                    }
                });
                break;
            case R.id.btn_bindphonefragment_bind:
                if (phonenum.equals("")) {
                    showToast("请输入您的手机号码");
                    return;
                } else if (tillphonecode.equals("")) {
                    showToast("请输入验证码");
                    return;
                } else if (!tillphonecode.equals(phonecode)) {
                    showToast("有错误验证码");
                    return;
                } else {
                    showLoadingDialog("正在绑定...");
                    AppServer.getInstance().bindPhone(accountInfo.getUid(), phonenum, tillphonecode,new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            cancelLoadingDialog();
                           if (code == AppServer.REQUEST_SUCCESS) {
                               showToast(message);
                               accountInfo.setPhone(phonenum);
                               AppServer.getInstance().setmAccountInfo(accountInfo);
                               AppServer.getInstance().setmAccountBean(new AccountBean(accountInfo));
                               DbHelper.updateAccountInfo(accountInfo);
                               Intent i=new Intent(BindPhoneActivity.this, IdSafeActivity.class);
                               startActivity(i);
                               BindPhoneActivity.this.finish();
                           } else {
                               showToast(message);
                           }
                        }
                    });
                }
                break;
		}
	}

    class TimesTask extends TimerTask {
        @Override
        public void run() {
            if (code_time > 0) {
                code_time--;
            } else {
                flag = true;
            }
            handler.sendEmptyMessage(000);
        }
    }

}
