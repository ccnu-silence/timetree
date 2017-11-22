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
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

import java.util.Timer;
import java.util.TimerTask;

public class SetPasswordActivity extends BaseActivity {

	private String phone;
	private String phonecode;
    static int time = 0;
    private String state;
    private boolean flag = true;
    private Timer timer;
    private int code_time = 60; // 倒计时时间内，不能发送短信（1min）
	@ViewInject(R.id.left_btn)ImageView iv;
	@ViewInject(R.id.header_title)TextView tv_head;
	@ViewInject(R.id.btn_setpassword_set)Button btn_set;
	@ViewInject(R.id.edt_activity_login_set_password)EditText et_password;
	@ViewInject(R.id.edt_activity_login_set_repassword)EditText et_repassword;
    @ViewInject(R.id.btn_get_phone_code)Button code_btn;
    @ViewInject(R.id.edt_activity_login_code)EditText code_et;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 000) {
                code_btn.setEnabled(flag);
                if (!flag) {
                    code_btn.setText(code_time + "秒后再试");
                } else {
                    code_btn.setText("获取验证码");
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
		setContentView(R.layout.activity_login_set);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		iv.setVisibility(View.VISIBLE);	
		phone = getIntent().getExtras().getString("phone");
		tv_head.setText("重设密码");
	}

	@OnClick({(R.id.right_btn),(R.id.btn_setpassword_set),(R.id.btn_get_phone_code),(R.id.left_btn)})
	public void OnClick(View v){
		switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.btn_get_phone_code:
                AppServer.getInstance().getPhoneCode(phone, "0", new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
                            // 当发送短信成功后，会开启线程，1min不让再次发送
                            flag = false;
                            timer = new Timer();
                            timer.schedule(new TimesTask(), 0, 1000);
                            phonecode = (String) obj;
                            // code_et.setText(phonecode);
                            System.out.print(phonecode + "~~~~~");
                        } else {
                            Toast.makeText(SetPasswordActivity.this, "验证码发送失败，请重新发送", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.btn_setpassword_set:
                final String newpw = et_password.getText().toString();
                    String confirmpw = et_repassword.getText().toString();
                    if (newpw.equals("")) {
                        Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        return ;
                    } else if (!newpw.equals(confirmpw)) {
                        Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        showLoadingDialog("正在修改...");
                        AppServer.getInstance().setPassword(phone, phonecode, confirmpw, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == AppServer.REQUEST_SUCCESS) {
                                    Toast.makeText(SetPasswordActivity.this, "重设成功", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SetPasswordActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    cancelLoadingDialog();
                                } else {
                                    Toast.makeText(SetPasswordActivity.this, "重设失败"+message, Toast.LENGTH_SHORT).show();
                                    cancelLoadingDialog();
                                }
                            }
                        });
                    }
                break;
            default:
                break;
		}
	}

    class TimesTask extends TimerTask{
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

    public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
