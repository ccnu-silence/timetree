package com.yey.kindergaten.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.EmoViewPagerAdapter;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.ImageLoadingDialog;

import java.util.ArrayList;
import java.util.List;

public class FriendsterShowImage extends BaseActivity {
	private ImageView  img;
	private ImageLoadingDialog dialog;
	private ArrayList<Photo> imglist;
	private int position;
	private ViewPager faceViewPage;
	private TextView tv_title;
    private TextView tv_right;
    private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friendster_showimg);
        options= ImageLoadOptions.getClassPhotoOptions();
		dialog = new ImageLoadingDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		initView();
		faceViewPage.setCurrentItem(position);

	}

	private void initView() {
		faceViewPage=(ViewPager) findViewById(R.id.vp_friendster_showimg);
		imglist=getIntent().getExtras().getParcelableArrayList("imglist");
        Photo photo = new Photo();
        photo.imgPath = "local";
        if(imglist!=null&&imglist.size()!=0)
        imglist.remove(photo);
		position=getIntent().getExtras().getInt("position");
		ImageView iv_left=(ImageView)findViewById(R.id.left_btn);
		tv_title=(TextView)findViewById(R.id.header_title);
		tv_title.setText((position+1)+"/"+imglist.size());
		iv_left.setVisibility(View.VISIBLE);
		iv_left.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        tv_right= (TextView) findViewById(R.id.right_tv);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("删除");
        tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imglist.remove(faceViewPage.getCurrentItem());
                AppContext.checkList.remove(faceViewPage.getCurrentItem());
                Intent intent=new Intent(FriendsterShowImage.this,ServicePublishSpeakActivity.class);
                intent.putExtra(AppConstants.PHOTOLIST, imglist);
                intent.putExtra("deleteimage","deleteimage");
                startActivity(intent);
            }
        });
		final List<View> views = new ArrayList<View>();
		for (int i = 0; i < imglist.size(); i++) {
//            if (i==imglist.size()-1){
//                imglist.remove(i);
//            }else {
                views.add(getImageView(i));
//            }
		}
		faceViewPage.setAdapter(new EmoViewPagerAdapter(views));	
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

	private View getImageView(int i) {
		View view = View.inflate(AppContext.getInstance(), R.layout.activity_friendster_imageview, null);
		final ImageView  iv= (ImageView) view.findViewById(R.id.iv_friendster_showimg);	
		String path=imglist.get(i).imgPath;
		String showUrl = "";
		if(path.contains("&")){
			showUrl = path.split("&")[0];
		}else{
			showUrl = path;
		}
         if (showUrl.contains("http")) {    
	    	 path=imglist.get(position).imgPath.replace("small", "big");
	    	 imageLoader.displayImage(path, iv,options,new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				dialog.show();		
				iv.setVisibility(View.GONE);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {	
				iv.setVisibility(View.GONE);
				Toast.makeText(FriendsterShowImage.this, "图片加载失败", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				dialog.dismiss();	
				iv.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				
			}
	        	});
		}else{ 
			imageLoader.displayImage("file://"+showUrl, iv, options);
		}
		return view;
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
