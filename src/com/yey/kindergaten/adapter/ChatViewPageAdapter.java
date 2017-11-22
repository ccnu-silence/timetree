package com.yey.kindergaten.adapter;

import java.util.List;

import cn.sharesdk.framework.l;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ChatLookPictureActivity;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.ImageLoadingDialog;
import com.yey.kindergaten.widget.TouchImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatViewPageAdapter extends PagerAdapter{

	private List<String> list;
	private ImageLoadingDialog dialog;
	private Context context;
	private  ImageLoader imageLoader;

	public ChatViewPageAdapter(List<String> list,Context context){
		this.list = list;
		this.context=context;
		dialog = new ImageLoadingDialog(context);
		dialog.setCanceledOnTouchOutside(false);
		imageLoader=ImageLoader.getInstance();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_friendster_imageview,container, false);		
		
		final TouchImageView  iv= (TouchImageView) view.findViewById(R.id.iv_friendster_showimg);
		
		String path=list.get(position);
		String showUrl = "";
		if(path.contains("&")){
			showUrl = path.split("&")[0];
		}else{
			showUrl = path;
		}
         if (showUrl.contains("http")) {    
        	 if(path.contains("small")){
        		 path=list.get(position).replace("small", "big");
        	 }
	    	 imageLoader.displayImage(path, iv,ImageLoadOptions.getOptions(),new ImageLoadingListener() {		
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				dialog.show();		
				iv.setVisibility(View.GONE);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {	
				iv.setVisibility(View.GONE);
				Toast.makeText(context, "图片加载失败", Toast.LENGTH_SHORT).show();
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
			imageLoader.displayImage("file://"+showUrl, iv, ImageLoadOptions.getFriendDataOptions());
		}
		container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		return view;
	  }
	
	  public void destroyItem(ViewGroup container, int position,
      		Object object) {
      	 container.removeView((View) object);
      }
}
