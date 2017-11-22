package com.yey.kindergaten.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ChatViewPageAdapter;

public class ChatLookPictureActivity extends BaseActivity {
	private ImageView  img;
	private ArrayList<String> imglist;
	private int position;
	private ViewPager faceViewPage;
	private TextView tv_title;
	ChatViewPageAdapter chatadapter;
	private String type = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friendster_showimg);
		initView();
		faceViewPage.setCurrentItem(position);
	 }

	private void initView() {
		faceViewPage=(ViewPager) findViewById(R.id.vp_friendster_showimg);
		imglist=getIntent().getExtras().getStringArrayList("imglist");
		position=getIntent().getExtras().getInt("position");
		type = getIntent().getExtras().getString("type");
		ImageView iv_left=(ImageView)findViewById(R.id.left_btn);
		tv_title=(TextView)findViewById(R.id.header_title);
		tv_title.setText((position+1)+"/"+imglist.size());
		iv_left.setVisibility(View.VISIBLE);
		iv_left.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(type!=null){
					if(type.equals("CommonBrowserWebImage")){
						openActivity(ClassPhotoDetialManager.class);
						finish();
					}else{
						finish();
					}
				}else{
					finish();
				}
							
			}
		});
		chatadapter=new ChatViewPageAdapter(imglist,this);
		faceViewPage.setAdapter(chatadapter);	
		faceViewPage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				tv_title.setText((arg0+1)+"/"+imglist.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
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
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(type!=null){
				if(type.equals("CommonBrowserWebImage")){
					openActivity(ClassPhotoDetialManager.class);
					finish();
				}
			}else{
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}

