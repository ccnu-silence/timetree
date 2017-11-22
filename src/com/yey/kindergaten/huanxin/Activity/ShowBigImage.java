/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yey.kindergaten.huanxin.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatConfig;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.huanxin.task.LoadLocalBigImgTask;
import com.yey.kindergaten.huanxin.utils.ImageCache;
import com.yey.kindergaten.huanxin.widget.photoview.PhotoView;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.UtilsLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends BaseActivity {

	private ProgressDialog pd;
	private PhotoView image;
	private TextView save_photo;
	private int default_res = R.drawable.default_image;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private ProgressBar loadLocalPb;
	private final static String TAG = "ShowBigImage";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);

		image = (PhotoView) findViewById(R.id.image);
        save_photo = (TextView) findViewById(R.id.save_photo);
		loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
		default_res = getIntent().getIntExtra("default_image", R.drawable.default_avatar);
		Uri uri = getIntent().getParcelableExtra("uri");
		String remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");
		System.err.println("show big image uri:" + uri + " remotepath:" + remotepath);

        final Uri newUri = uri;
        final String newRemotepath = remotepath;
        save_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newUri != null && new File(newUri.getPath()).exists()) {
                    savePhoto(newUri.getPath());
                } else if (newRemotepath != null) {
                    savePhoto(newRemotepath);
                }
            }
        });

		// 本地存在，直接显示本地的图片
		if (uri != null && new File(uri.getPath()).exists()) {
			System.err.println("showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = ImageCache.getInstance().get(uri.getPath());
			if (bitmap == null) {
				LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
						ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			} else {
				image.setImageBitmap(bitmap);
			}
		} else if (remotepath != null) { // 去服务器下载图片
			System.err.println("download remote image");
			Map<String, String> maps = new HashMap<String, String>();
			if (!TextUtils.isEmpty(secret)) {
				maps.put("share-secret", secret);
			}
			downloadImage(remotepath, maps);
		} else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            myFileUrl = new URL(url);
//            HttpURLConnection conn;

            conn = (HttpURLConnection) myFileUrl.openConnection();

            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (conn!=null) { conn.disconnect(); }
                if (is!=null) { is.close(); }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

//    public Bitmap returnBitMap(String url) {
//         URL myFileUrl = null;
//         Bitmap bitmap = null;
//         try {
//               myFileUrl = new URL(url);
//            } catch (MalformedURLException e) {
//                 e.printStackTrace();
//             }
//         try {
//             HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//             conn.setDoInput(true);
//             conn.connect();
//             InputStream is = conn.getInputStream();
//             bitmap = BitmapFactory.decodeStream(is);
//             is.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         return bitmap;
//     }

    /***
     * 功能：用线程保存图片
     *
     */
    private class SaveImage extends com.yey.kindergaten.task.AsyncTask<String, Void, String> {
        private String savepath = "";

        public SaveImage(String savepath){
            this.savepath = savepath;
        }
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/DCIM/Camera");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = savepath.lastIndexOf(".");
                String ext = savepath.substring(idx);
                file = new File(sdcard + "/DCIM/Camera/" + new Date().getTime() + ext);
                savepath = file.getAbsolutePath();
                InputStream inputStream = null;
                URL url = new URL(savepath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();

            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // 刷新相册
            Uri localUri = Uri.fromFile(new File(savepath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
            showToast(result);
        }
    }

    public void savePhoto(String photoPath) {

        if (photoPath!=null && !photoPath.contains("http")) {
            photoPath = "file:///" + photoPath;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = null;
        Date d1 = new Date(System.currentTimeMillis());
        time = format.format(d1);
        String photoName = time + ".png";
        // 创建目录
        createSDCardDir();
        if (bitmap!=null) {
            save(bitmap, photoName);
//            bitmap = null;
            scanPhoto(AppConstants.SAVE_PHOTO_PATH + photoName);
            UtilsLog.i(TAG, "save success : " + photoPath);
            Toast.makeText(ShowBigImage.this, "保存成功！ " + "图片已存至 " + AppConstants.SAVE_PHOTO_PATH + photoName, Toast.LENGTH_LONG).show();
        } else {
            // 保存图片
            Bitmap bitMap = ImageLoader.getInstance().loadImageSync(photoPath);
            if (bitMap != null) {
                save(bitMap, photoName);
                bitMap = null;
                scanPhoto(AppConstants.SAVE_PHOTO_PATH + photoName);
                UtilsLog.i(TAG, "save success : " + photoPath);
                Toast.makeText(ShowBigImage.this, "保存成功！ " + "图片已存至 " + AppConstants.SAVE_PHOTO_PATH + photoName, Toast.LENGTH_LONG).show();
            } else {
                new SaveImage(photoPath).execute();
//            UtilsLog.i(TAG, "save fail : " + photoPath);
//            Toast.makeText(ShowBigImage.this, "保存失败！", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 在SD卡上创建一个文件夹
    public void createSDCardDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // 创建一个文件夹对象，赋值为外部存储器的目录
            String path = AppConstants.SAVE_PHOTO_PATH;
            File path1 = new File(path);
            if (!path1.exists()) {
                // 若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }
        } else {
            setTitle("false");
            return;
        }
    }

    /**
     * 保存图片的方法
     * @param bm
     */
    private void save(Bitmap bm, String photoName) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            FileOutputStream fos = new FileOutputStream(new File(AppConstants.SAVE_PHOTO_PATH, photoName));
            int options = 100;

            while (baos.toByteArray().length / 1024 > 80 && options != 10) {

                baos.reset();

                bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 30;
            }
            fos.write(baos.toByteArray());
            fos.close();
            baos.close();
            bm = null;

        } catch (Exception e) {
            UtilsLog.i(TAG, "saveBitmap Exception");
        }
    }

    /**
     * 刷新本地图片，能及时在本地相册中看到
     * 调用系统扫描文件类
     * @param imgFileName
     */
    private void scanPhoto(String imgFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

	/**
	 * 通过远程URL，确定下本地下载后的localurl
	 * @param remoteUrl
	 * @return
	 */
	public String getLocalFilePath(String remoteUrl){
		String localPath;
		if (remoteUrl.contains("/")){
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
					+ remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
		}else{
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
		}
		return localPath;
	}
	
	/**
	 * 下载图片
	 * 
	 * @param remoteFilePath
	 */
	private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("下载图片: 0%");
		pd.show();
		localFilePath = getLocalFilePath(remoteFilePath);
		final HttpFileManager httpFileMgr = new HttpFileManager(this, EMChatConfig.getInstance().getStorageUrl());
		final CloudOperationCallback callback = new CloudOperationCallback() {
			public void onSuccess(String resultMsg) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							ImageCache.getInstance().put(localFilePath, bitmap);
							isDownloaded = true;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(String msg) {
				Log.e("###", "offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						image.setImageResource(default_res);
					}
				});
			}

			public void onProgress(final int progress) {
				Log.d("ease", "Progress: " + progress);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.setMessage("下载图片: " + progress + "%");
					}
				});
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				httpFileMgr.downloadFile(remoteFilePath, localFilePath, headers, callback);
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}
}
