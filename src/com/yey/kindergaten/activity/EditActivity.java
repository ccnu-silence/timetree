package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;

public class EditActivity extends BaseActivity implements OnClickListener{

    @ViewInject(R.id.left_btn)ImageView left_btn;
    @ViewInject(R.id.right_tv)TextView  right_text;
    @ViewInject(R.id.header_title)TextView  title_tv;
    @ViewInject(R.id.edittv) EditText et;
    int clickposition;
    String text;
    String inputtype = "";
    String titleString = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        ViewUtils.inject(this);
        if (getIntent().getExtras()!=null) {
            clickposition = getIntent().getExtras().getInt("clickposition");
            text = getIntent().getExtras().getString("text");
            inputtype = getIntent().getExtras().getString(AppConstants.INPUTTYPE);
            titleString = getIntent().getExtras().getString(AppConstants.TITLE);
            if (inputtype == null) {
                et.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                if (inputtype.equals(AppConstants.INPUTTYPE_NUMBER)) {
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if(inputtype.equals(AppConstants.PHONE)) {
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if(inputtype.equals(AppConstants.INPUTTYPE_STRING)) {
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    et.setSingleLine(false);
                }
            }
        }
        initView();
    }

    public void initView() {
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(this);
        right_text.setVisibility(View.VISIBLE);
        right_text.setOnClickListener(this);
        right_text.setText("完成");
        if (titleString!=null && !titleString.equals("")) {
            title_tv.setText(titleString);
        } else {
            title_tv.setText("编辑");
        }
        if (text.length() > 12) {
            et.setText(text.substring(0, 9) + "...");
        } else {
            et.setText(text);
        }
        et.setSelection(et.getText().toString().length());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        case R.id.left_btn:
            intent = new Intent();
            if (titleString!=null && titleString.equals("真实姓名")) {
                setResult(3, intent);
            } else {
                setResult(6, intent);
            }
            this.finish();
            break;
        case R.id.right_tv:
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
            intent = new Intent();
            String text = et.getText().toString().trim();
            if (inputtype!=null && inputtype.equals(AppConstants.PHONE)) {
                if (clickposition == 0) {
                    if (text == null || text.equals("")) {
                        showToast("请输入手机号码");
                        return;
                    }
                }
            }
            if (inputtype!=null && inputtype.equals(AppConstants.INPUTTYPE_STRING)) {
                if (text==null || text.equals("")){
                    showToast("文本内容不能为空");
                    return;
                }
            }
            intent.putExtra("edittext", text);
            intent.putExtra("clickposition", clickposition);
            if (titleString!=null && titleString.equals("真实姓名")) {
                setResult(3, intent);
            } else {
                setResult(6, intent);
            }
            this.finish();
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            if (titleString!=null && titleString.equals("真实姓名")) {
                setResult(3, intent);
            } else {
                setResult(6, intent);
            }
            this.finish();
            return false;
        }
        return false;
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
