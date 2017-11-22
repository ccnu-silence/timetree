package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;

public class IdSafeActivityAmend extends BaseActivity implements OnClickListener{
    @ViewInject(R.id.header_title)TextView tv;
    @ViewInject(R.id.left_btn)	ImageView leftbtn;
    @ViewInject(R.id.idsafe_newpwet) EditText newpwet;
    @ViewInject(R.id.idsafe_confirmpwet)EditText confirmet;
    @ViewInject(R.id.idsafe_confirmpwet_agin)EditText confirmet_again;
    @ViewInject(R.id.idsafe_submitbtn)Button submitbtn;
    AccountInfo accountInfo;
    private String TAG = "IdSafeActivityAmend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idsafe_main_amend);
        accountInfo=AppServer.getInstance().getAccountInfo();
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        tv.setText("修改密码");
        leftbtn.setVisibility(View.VISIBLE);
    }
    
    @OnClick({(R.id.idsafe_submitbtn),(R.id.left_btn)})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left_btn:
            this.finish();
            break;
        case R.id.idsafe_submitbtn:
            final String newpw=newpwet.getText().toString().trim();
            final  String confirmpw=confirmet.getText().toString().trim();
            String cinfirmpw_again = confirmet_again.getText().toString().trim();
//          System.out.println("accountInfo.getPassword()---"+accountInfo.getPassword());
            if (confirmet.length() > 20) {
                showToast("密码长度过长，请您重新设置");
                return;
            }
            if (confirmet.length() < 6){
                showToast("密码长度过短，请您重新设置");
                return;
            }
            if (!cinfirmpw_again.equals(confirmpw)){
                showToast("您新密码前后输入不一致");
                return;
            }
            if (newpw.equals("")){
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return ;
            } else {
                if (!newpw.equals(accountInfo.getPassword())) {
                    Toast.makeText(IdSafeActivityAmend.this, "当前密码验证失败", Toast.LENGTH_LONG).show();
                } else {
                    showLoadingDialog("正在修改...");
                    AppServer.getInstance().modifyPassword(accountInfo.getUid(), accountInfo.getPassword(), confirmpw, new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if (code == 0) {
                                Toast.makeText(IdSafeActivityAmend.this, "修改成功", Toast.LENGTH_SHORT).show();
                                AppServer.getInstance().getAccountInfo().setPassword(confirmpw);
                                DbHelper.updateAccountInfo(AppServer.getInstance().getAccountInfo());
                                // 记住登录历史记录
                                String accouts = accountInfo.getAccount().trim() + "||" + (accountInfo.getPassword() + "zgyey") + "||" + accountInfo.getAvatar();
                                SharedPreferences settings = AppContext.getInstance().getSharedPreferences(SharedPreferencesHelper.PREF_LOGIN_FILE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(accountInfo.getAccount().trim(), accouts);
                                editor.commit();
                                UtilsLog.i(TAG, "saveAll login status sharedpreferences ok");
                                // 记住最后一次登录的配置文件
                                SharedPreferences set = AppContext.getInstance().getSharedPreferences(AppConfig.LOGIN_DEFALUTE_VALUE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editors = set.edit();
                                editors.putString(AppConfig.LOGIN_DEFAULTE_ACCOUNT, accountInfo.getAccount().trim().trim());
                                editors.putString(AppConfig.LOGIN_DEFAULTE_PASSWORD, accountInfo.getPassword() + "zgyey");
                                editors.putString(AppConfig.LOGIN_DEFAULTE_AVATER, accountInfo.getAvatar());
                                editors.commit();
//                              AppServer.getInstance().setmAccountBean(accountInfo);
                                IdSafeActivityAmend.this.finish();
                            } else {
                                Toast.makeText(IdSafeActivityAmend.this, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                            cancelLoadingDialog();
                        }
                    });
                }
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

}
