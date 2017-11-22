package com.yey.kindergaten.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;

public class GrowthDiaryMoreActivity extends BaseActivity{
	@ViewInject(R.id.right_btn)ImageView  iv_right;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	@ViewInject(R.id.header_title)TextView tv_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.growthdiarymore_main);
		ViewUtils.inject(this);
		initView();
	}
	private void initView() {
		iv_left.setVisibility(View.VISIBLE);
		iv_right.setVisibility(View.VISIBLE);
		tv_title.setText("写日记");

	}
}

