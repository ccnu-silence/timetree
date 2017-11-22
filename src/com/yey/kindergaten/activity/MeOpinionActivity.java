package com.yey.kindergaten.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

public class MeOpinionActivity extends BaseActivity implements OnClickListener {

    @ViewInject(R.id.left_btn)ImageView left_btn;
    @ViewInject(R.id.right_tv)TextView  right_text;
    @ViewInject(R.id.header_title)TextView  title_tv;
    @ViewInject(R.id.et_meopinion) EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meopinion_main);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        right_text.setVisibility(View.VISIBLE);
        right_text.setOnClickListener(this);
        right_text.setText("完成");
        title_tv.setText("意见反馈");
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.right_tv:
                if (!et.getText().toString().trim().equals("")) {
                    hideSoftInputViewV2();
                    AppServer.getInstance().feedback(AppContext.getInstance().getAccountInfo().getUid(), et.getText().toString(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            // TODO Auto-generated method stub
                            if (code == AppServer.REQUEST_SUCCESS) {
                                finish();
                                showToast("反馈成功，谢谢您的宝贵意见");
                            } else {
                                showToast("反馈失败，待会再试试吧");
                            }
                        }
                    });
                } else {
                    showToast("反馈意见不能为空哦。");
                }
                break;
            case R.id.left_btn:
                finish();
                break;
            default:
                break;
        }
    }

}
