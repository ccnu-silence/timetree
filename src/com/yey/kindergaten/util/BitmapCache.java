package com.yey.kindergaten.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.activity.GetSDCardAlbumActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class BitmapCache extends Activity {

	public Handler h = new Handler();
	public final String TAG = getClass().getSimpleName();
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private static BitmapCache cache;
	public synchronized static BitmapCache getInstance() {
		if(cache ==null){
			cache = new BitmapCache();
		}
		return cache;
	}
	public void put(String path, Bitmap bmp) {
		if (!TextUtils.isEmpty(path) && bmp != null) {
			imageCache.put(path, new SoftReference<Bitmap>(bmp));
		}
	}

	public void displayBmp(final ImageView iv, final String thumbPath,
			final String sourcePath, final ImageCallback callback) {
		if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)) {
			Log.e(TAG, "no paths pass in");
			return;
		}

		final String path;
		final boolean isThumbPath;
		if (!TextUtils.isEmpty(thumbPath)) {
			path = thumbPath;
			isThumbPath = true;
		} else if (!TextUtils.isEmpty(sourcePath)) {
			path = sourcePath;
			isThumbPath = false;
		} else {
			// iv.setImageBitmap(null);
			return;
		}

		if (imageCache.containsKey(path)) {
			SoftReference<Bitmap> reference = imageCache.get(path);
			Bitmap bmp = reference.get();
			if (bmp != null) {
				if (callback != null) {
					callback.imageLoad(iv, bmp, sourcePath);
				}
				iv.setImageBitmap(bmp);
				Log.d(TAG, "hit cache");
				return;
			}
		}
		iv.setImageBitmap(null);

		new Thread() {
			Bitmap thumb;

			public void run() {

				try {
					if (isThumbPath) {
						thumb = BitmapFactory.decodeFile(thumbPath);
						if (thumb == null) {
							thumb = revitionImageSize(sourcePath);						
						}						
					} else {
						thumb = revitionImageSize(sourcePath);											
					}
				} catch (Exception e) {	
					
				}
				if (thumb == null) {
					thumb = GetSDCardAlbumActivity.bimap;
				}
				Log.e(TAG, "-------thumb------"+thumb);
				put(path, thumb);

				if (callback != null) {
					h.post(new Runnable() {
						@Override
						public void run() {
							callback.imageLoad(iv, thumb, sourcePath);
						}
					});
				}
			}
		}.start();

	}
	
	public void displayNetBmp(final ImageView iv, 
			final String Path, final ProgressBar  loadbar,DisplayImageOptions options) {
		
		if (TextUtils.isEmpty(Path)) {
			Log.e(TAG, "no paths pass in");
			return;
		}
		 final String filename = Path.substring(Path.lastIndexOf("/")+1, Path.length());
		 String md5name = StringUtils.getDigestStr(filename);
        final File image = new File(FileUtils.getSDRoot() + "yey/imagecache/"+md5name);
		if (image.exists()) {
			System.out.println("有缓存"+Path);
			 final DisplayMetrics metrics = AppContext.getInstance().getResources().getDisplayMetrics();
				int size = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) / 0.75);
             DecodeUtils decodeUtils = new DecodeUtils();
	         Bitmap bmp=decodeUtils.decode(AppContext.getInstance(), Uri.parse(FileUtils.getSDRoot() + "yey/imagecache/"+md5name), size, size);
			//Bitmap bmp = BitmapUtil.readBitMap(FileUtils.getSDRoot() + "yey/imagecache/"+md5name);
			if (bmp != null) {
				if(loadbar!=null){
					loadbar.setVisibility(View.GONE);
				}
				
				iv.setImageBitmap(bmp);
				Log.d(TAG, "hit cache");
				return;
			}
		}
		iv.setImageBitmap(null);
       
		if(Path.contains("http")){
			ImageLoader.getInstance().displayImage(Path, iv, options,new SimpleImageLoadingListener(){
				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					if(loadbar!=null){
						loadbar.setVisibility(View.GONE);
					}
					if (!image.exists()) {
						System.out.println("put__"+Path);
						put(Path, loadedImage);
						BitmapUtil.saveBitmapByArray(loadedImage, filename);
					}
					
					iv.setImageBitmap(loadedImage);
				}
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					if(loadbar!=null){
						loadbar.setVisibility(View.VISIBLE);
					}
					
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					if(loadbar!=null){
						loadbar.setVisibility(View.GONE);
					}
				}
			});
		}else{
			if(new File(Path).exists()){
				ImageLoader.getInstance().displayImage("file://"+Path, iv, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						loadbar.setVisibility(View.GONE);
						if (!image.exists()) {
							System.out.println("put__"+Path);
							put(Path, loadedImage);
							BitmapUtil.saveBitmapByArray(loadedImage, filename);
						}
						
						iv.setImageBitmap(loadedImage);
					}
					
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						loadbar.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						loadbar.setVisibility(View.GONE);
					}
				});
			}
			
		}
		
	    

	}

	public Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 256)
					&& (options.outHeight >> i <= 256)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params);
	}
	
	
}
