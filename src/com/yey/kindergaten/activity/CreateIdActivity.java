package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.SMSBroadCast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CreateIdActivity extends BaseActivity{
    @ViewInject(R.id.btn_activity_login_create_create)Button btn;
    // @ViewInject(R.id.choose_activity_login_create)ChooseButton choose_btn;
    @ViewInject(R.id.header_title)TextView tv_head;
    @ViewInject(R.id.creat_id_tip)TextView creat_id_tip;
    @ViewInject(R.id.left_btn)ImageView left_iv;

    @ViewInject(R.id.ed_regester_password)EditText ed_password;
    @ViewInject(R.id.show_hide_login_btn)Button slipbtn;
    @ViewInject(R.id.creat_get_code_btn)Button code_btn;
    @ViewInject(R.id.creat_get_code_et)EditText code_et;
    @ViewInject(R.id.ed_regester_phone_number)EditText regest_et;
    private AccountInfo info;
    private int role = 0;
    private String phonecode;
    private int code_time = 60; // 倒计时时间内，不能发送短信（1min）
    private boolean flag = true; // true表示可以发验证码短信，false表示还不能发送
    private Timer timer;
    private SMSBroadCast broadCast;
    private int Code = 0; // 因为判断登陆的原因，需要在登陆时判断账号的异常在跳转；

    private boolean isPhoneUsed = false;
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private Handler handler = new Handler() {
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

    private AccountInfo mAccountInfo;
    // 暂未使用
    private boolean isfoucs;
    private boolean isshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_create);
        role = getIntent().getIntExtra("role",0);
        ViewUtils.inject(this);
        mAccountInfo = AppServer.getInstance().getAccountInfo();
//      broadCast = new SMSBroadCast();
//      // 实例化过滤器并设置要过滤的广播
//      IntentFilter intentFilter = new IntentFilter(ACTION);
//      intentFilter.setPriority(Integer.MAX_VALUE);
//      // 注册广播
//      this.registerReceiver(broadCast, intentFilter);
//      broadCast.setOnReceivedMessageListener(new SMSBroadCast.MessageListener() {
//          @Override
//          public void onReceived(String message) {
//              code_et.setText(message);
//          }
//      });
        initView();
    }

    private void initView() {
        tv_head.setText("注册/输入账号信息");
        ed_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        code_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        regest_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        code_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        regest_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        creat_id_tip.setVisibility(View.VISIBLE);
        regest_et.setFocusable(true);
        regest_et.setFocusableInTouchMode(true);
        regest_et.requestFocus();

        Timer timers = new Timer();
        timers.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) regest_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(regest_et, 0);
            }
        }, 500);

        slipbtn.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View arg0) {
                if (isshow) {
                    ed_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ed_password.setSelection(ed_password.getText().length());
                    isshow = false;
                    slipbtn.setBackgroundDrawable(CreateIdActivity.this.getResources().getDrawable(R.drawable.check_phone_hover));
                } else {
                    ed_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // 显示密码
                    ed_password.setSelection(ed_password.getText().length());
                    isshow = true;
                    slipbtn.setBackgroundDrawable(CreateIdActivity.this.getResources().getDrawable(R.drawable.check_phone_code));
                }
            }
        });
        code_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regest_et.getText().length() < 11) {
                    showToast("您输入的手机号码不正确");
                    return;
                }
                code_et.setFocusable(true);
                code_et.setFocusableInTouchMode(true);
                code_et.requestFocus();
                AppServer.getInstance().getPhoneCode(regest_et.getText().toString(), "1", new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
                            code_btn.setClickable(false);
                            // 当发送短信成功后，会开启线程，1min不让再次发送
                            flag = false;
                            timer = new Timer();
                            timer.schedule(new TimesTask(), 0, 1000);
                            phonecode = (String) obj;
//                          code_et.setText(phonecode);
                            System.out.print(phonecode + "~~~~~");
                            isPhoneUsed = false;
                        } else if (code == -1) {
                            isPhoneUsed = true;
                            showToast(message);
                        } else {
                            showToast(message);
                        }
                    }
                });
            }
        });

        left_iv.setVisibility(View.VISIBLE);
        left_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 检查是否有连续的整数
     * @return
     */
    public boolean haveConTinueNum() {
        String password = ed_password.getText().toString();
        List<Integer>allNum = new ArrayList<Integer>();
        int count = password.length();

        int abcnum = 0;
        for (int i = 0; i < count; i++) {
            if (isString(password.substring(i, i + 1))) {
                int each = Integer.valueOf(password.substring(i, i + 1));
                allNum.add(each);
            } else {
                if (count > 5) {
                    isfoucs = true;
                    abcnum++;
                } else {
                    Toast.makeText(CreateIdActivity.this, "您的密码位数过短", Toast.LENGTH_LONG).show();
                }
            }
        }

        int index = 0;
        for (int i = 0; i < allNum.size() - 1; i++) {
            if (allNum.get(i) + 1 == allNum.get(i + 1)) {
                index++;
            } else {
                isfoucs = true;
            }
        }
        if (index == allNum.size() - 1) {
            if (abcnum > 0) {
                   isfoucs = true;
            } else {
                isfoucs = false;
            }
        }
        return isfoucs;

    }

    /**
     * 检查数字是否都是相同的
     * @return
     */
    public boolean haveSameNum(){
        String password = ed_password.getText().toString();
        List<Integer>allNum = new ArrayList<Integer>();
        int count = password.length();

        int abcnum = 0;
        for (int i = 0; i < count; i++) {
            if (isString(password.substring(i, i + 1))) {
                int each = Integer.valueOf(password.substring(i, i + 1));
                allNum.add(each);
            } else {
                isfoucs = true;
                abcnum++;
            }
        }

        int sameindex = 0;
        for (int i = 0; i < allNum.size() - 1; i++) {
            if (allNum.get(i) == allNum.get(i + 1)) {
                sameindex++;
            } else {
                isfoucs = true;
            }
        }
        // 判断是否连号算法
        if (sameindex == allNum.size() - 1) {
            if (abcnum > 0) {
                isfoucs = true;
            } else {
                isfoucs = false;
            }
        }
        return isfoucs;

    }

    /**
     * 判断str能否转化成int
     * @param str
     * @return true表示能，flase捕获异常
     */
    public boolean isString(String str) {
        int temp = 0;
        try {
            temp = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @OnClick({R.id.btn_activity_login_create_create,R.id.left_btn,R.id.tabBtn_left,R.id.tabBtn_middle,R.id.tabBtn_right})
    public void onClick(View v){
        switch (v.getId()) {
        case R.id.btn_activity_login_create_create:
            final AccountInfo info = AppServer.getInstance().getAccountInfo();
            if (regest_et.getText().toString().length() < 11) {
                showToast("您输入的手机号码不正确");
                return;
            }

            if (code_et.getText().toString().length() == 0) {
                showToast("请输入验证码");
                return;
            }

            if (phonecode == null) {
                phonecode = "";
            }
            if (!code_et.getText().toString().equals(phonecode)) {
                showToast("输入验证码不正确");
                return;
            }

            if (isPhoneUsed) {
                showToast("手机账号已存在");
                return;
            }
            Intent intent = new Intent(CreateIdActivity.this, ForgetPasswordActivity.class);
            intent.putExtra("type","regedit");
            intent.putExtra("phonenumber", regest_et.getText().toString());
            intent.putExtra("code", code_et.getText().toString());
            intent.putExtra("role", role);
            startActivity(intent);
            this.finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//      this.unregisterReceiver(broadCast);
    }

}
