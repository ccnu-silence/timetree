package com.yey.kindergaten.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.header_title)TextView tv_head;
    @ViewInject(R.id.left_btn)ImageView iv_left;
    ByPhoneChangeFragment byphone = null;
    private String type = null;
    private String phonenumber = null;
    private String code = null;
    private int role;
//  ByPostBoxChangeFragment bypostbox = null;
//  ByServiceChangeFragment byservice = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_change);
        ViewUtils.inject(this);

        type = getIntent().getStringExtra("type");
        phonenumber = getIntent().getStringExtra("phonenumber");
        code = getIntent().getStringExtra("code");
        role = getIntent().getIntExtra("role", 0);
        initView();
        getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fl_change_contain, byphone).commit();
    }

    private void initView() {
        byphone = new ByPhoneChangeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("phonenumber", phonenumber);
        bundle.putString("code", code);
        bundle.putInt("role", role);
        byphone.setArguments(bundle);
        if (type == null) {
            tv_head.setText("验证手机");
        } else {
            tv_head.setText("注册/设置密码");}

        iv_left.setVisibility(View.VISIBLE);
        iv_left.setOnClickListener(this);
    }

    @SuppressLint("NewApi")
    private void setByPhoneFragment() {
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fl_change_contain, byphone)
        .commit();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
        }
    }

}
