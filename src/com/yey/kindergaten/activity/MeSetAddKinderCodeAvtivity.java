package com.yey.kindergaten.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;

public class MeSetAddKinderCodeAvtivity extends BaseActivity implements OnClickListener{

    @ViewInject(R.id.left_btn) ImageView leftbtn;
    @ViewInject(R.id.header_title) TextView titletv;
    @ViewInject(R.id.right_tv) TextView righttv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setaddtokingdercode);
        ViewUtils.inject(this);
        initview();
    }

    public void initview() {
        leftbtn.setVisibility(View.VISIBLE);
        leftbtn.setOnClickListener(this);
        titletv.setVisibility(View.VISIBLE);
        titletv.setText("设置加入幼儿园口令");
        righttv.setVisibility(View.VISIBLE);
        righttv.setText("保存");
        righttv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.right_tv:
                break;
            default:
                break;
        }
    }

}
