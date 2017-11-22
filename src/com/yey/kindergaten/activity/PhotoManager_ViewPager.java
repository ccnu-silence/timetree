
package com.yey.kindergaten.activity;

/**
 * @author zy
 * 滑动预览图片
 */

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.HackyViewPager;
import com.yey.kindergaten.widget.PhotoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoManager_ViewPager extends BaseActivity implements OnClickListener{

    SensorManager sensorManager;
    ProgressBar bar;
    View imageLayout;
    private SamplePagerAdapter adapter;
    private HackyViewPager mViewPager;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private ArrayList<String>desclist;
    private String type; // 跳转标示符
    private ArrayList<String> imglist = new ArrayList<String>();
    private String name;
    private int position;
    private Object object;
    private TextView header_tv;
    private TextView letf_tv;
    private TextView right_tv;

    private int page;
    private TextView count_tv;
    private TextView desc_tv;
    private FrameLayout viewpage_head;
    private float x, y, z;
    int oritention = 1; // 1竖屏，0:横屏
    static double angle = 0, last_angle = -1;
    private final static String TAG = "PhotoManager_ViewPager";

    private int currentPosition = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager = null;
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
        sensorManager = null;
        imageLayout = null;
        adapter = null;
        mViewPager = null;
        imageLoader = null;
        desclist = null;
        object = null;
        header_tv = null;
        letf_tv = null;
        right_tv = null;
        count_tv = null;
        desc_tv = null;
        viewpage_head = null;
        setContentView(R.layout.activity_null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_viewpager);
        mViewPager = (HackyViewPager) findViewById(R.id.id_show_image_vp);
        header_tv = (TextView) findViewById(R.id.header_title);
        letf_tv = (TextView) findViewById(R.id.left_tv);
        right_tv = (TextView) findViewById(R.id.right_tv);

        count_tv = (TextView)findViewById(R.id.id_text_showcount);
        desc_tv = (TextView)findViewById(R.id.id_show_desc_text);

        viewpage_head = (FrameLayout) findViewById(R.id.viewpage_head);

        letf_tv.setVisibility(View.VISIBLE);
        letf_tv.setText("关闭");
        letf_tv.setOnClickListener(this);
        right_tv.setVisibility(View.VISIBLE);
        right_tv.setText("保存图片");
        right_tv.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            object = bundle.getSerializable(AppConstants.PARAM_ALBUM);
            type = bundle.getString("type");
            // path list
            imglist = bundle.getStringArrayList("imglist");
            // title list
            desclist = bundle.getStringArrayList("decslist");
            position = bundle.getInt("position");
            currentPosition = position;
            page = bundle.getInt("page");
            if (type.equals(AppConstants.PARAM_UPLOAD_LIFE) || type.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                adapter = new SamplePagerAdapter(imglist, desclist);
                LifePhoto photo = (LifePhoto) object;
                header_tv.setText(photo.getName());
            } else if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                adapter = new SamplePagerAdapter(imglist, desclist);
//                header_tv.setText("班级相册");
            } else if (type.equals(AppConstants.PARAM_CommonBrowser)) {
                adapter = new SamplePagerAdapter(imglist);
//                header_tv.setText("预览图片");
            } else if (type.equals(AppConstants.PARAM_PHOTOSHOWGENERAL)) {
                adapter = new SamplePagerAdapter(imglist, desclist);
//                header_tv.setText("预览图片");
            } else if (type.equals(AppConstants.PARAM_FRIENDSTER_HEAD)) {
                adapter = new SamplePagerAdapter(imglist, desclist);
                viewpage_head.setVisibility(View.GONE);
            } else if (type.equals(AppConstants.PARAM_UPLOAD_STER)) {
                adapter = new SamplePagerAdapter(imglist);
//                header_tv.setText("预览图片");
            } else if (type.equals(AppConstants.PARAM_UPLOAD_GROW)) {
                adapter = new SamplePagerAdapter(imglist);
//                header_tv.setText("预览图片");
            } else {
                adapter = new SamplePagerAdapter(imglist, desclist);
//                header_tv.setText("批量编辑");
            }
            if (imglist.size() == 1) {
                header_tv.setText("1");
            } else {
                header_tv.setText((position == 0 ? "1/" : (position + 1 + "/")) + imglist.size());
            }
        }

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPosition = i;
                UtilsLog.i(TAG, "pageselect i: " + i);
                if (adapter!=null) {
                    String text = (i + 1) + "/" + adapter.getCount() + "\n\n";
                    header_tv.setText(text);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

//        mViewPager.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                return false;
//            }
//        });
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorEventListener lsn = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent e) {
                x = e.values[SensorManager.DATA_X];
                y = e.values[SensorManager.DATA_Y];
                z = e.values[SensorManager.DATA_Z];

                angle = Math.atan(y/x);
                if (Math.abs(angle) < 1.0) {
                    if (oritention == 1) {
                        // 设置横屏
                        viewpage_head.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        oritention = 0;
                    }
                } else {
                    if (oritention == 0) {
                        // 设置竖屏
                        viewpage_head.setVisibility(View.VISIBLE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        oritention = 1;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) { }

        };
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // .SENSOR_ACCELEROMETER);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    class SamplePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        // path list
        private ArrayList<String> selectedList = new ArrayList<String>();
        // title list
        private ArrayList<String> arrayList;
        private Boolean istext;

        public SamplePagerAdapter(ArrayList<String> selectedDataList){
            this.selectedList = selectedDataList;
            inflater = getLayoutInflater();
            istext = false;
        }

        public SamplePagerAdapter(ArrayList<String> selectedDataList, ArrayList<String> arrayList) {
            this.selectedList = selectedDataList;
            this.arrayList = arrayList;
            inflater = getLayoutInflater();
//            if (arrayList.size() > 0) {
//                istext = true;
//            } else {
//                istext = false;
//            }
        }

        public ArrayList<String> getSelectedList() {
            return selectedList;
        }

        @Override
        public int getCount() {
            return selectedList.size();
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//          super.destroyItem(container, position, object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }

        @SuppressWarnings("deprecation")
        public View instantiateItem(ViewGroup container, final int position) {

            imageLayout = inflater.inflate(R.layout.item_pager_image, container, false);
            RelativeLayout rl = (RelativeLayout) imageLayout.findViewById(R.id.pagerlayout);
            PhotoView photoview = new PhotoView(container.getContext());

            RelativeLayout imagelayout = new RelativeLayout(container.getContext());
            imagelayout.setId(R.id.photo_desc_name_tv);
            RelativeLayout.LayoutParams ll_params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//          imagelayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv = new TextView(container.getContext());
            tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            ll_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextColor(PhotoManager_ViewPager.this.getResources().getColor(R.color.white));
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//          lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            imagelayout.addView(tv, lp1);

            // 下载图片
            TextView downtv = new TextView(container.getContext());
            downtv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 80));
//          downtv.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);
            downtv.setTextSize(18);
            downtv.setTextColor(PhotoManager_ViewPager.this.getResources().getColor(R.color.white));
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//          lp2.addRule(RelativeLayout.ALIGN_END, RelativeLayout.TRUE); // 加了会报错
            imagelayout.addView(downtv, lp2);

            final RelativeLayout progress_rl = new RelativeLayout(container.getContext());
            RelativeLayout.LayoutParams pl_params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 80);
//			final TextView percent_tv = new TextView(container.getContext());
//            final RoundProgressBar progress_bar = new RoundProgressBar(container.getContext());
            final ProgressBar progress_bar = new ProgressBar(container.getContext());
            progress_bar.setLayoutParams(new LayoutParams(75, 75));
//            progress_bar.setTextColor(PhotoManager_ViewPager.this.getResources().getColor(R.color.white));
//            progress_bar.setTextSize(23);
//            progress_bar.setCricleColor(PhotoManager_ViewPager.this.getResources().getColor(R.color.white));
//            progress_bar.setCricleProgressColor(PhotoManager_ViewPager.this.getResources().getColor(R.color.purple));

//			percent_tv.setTextColor(PhotoManager_ViewPager.this.getResources().getColor(R.color.white));
            progress_rl.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            progress_rl.setVisibility(View.VISIBLE);

//		    progress_rl.addView(percent_tv);
            progress_rl.addView(progress_bar);
            rl.addView(photoview, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            ll_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rl.addView(imagelayout, ll_params);
            pl_params.addRule(RelativeLayout.CENTER_IN_PARENT);
            rl.addView(progress_rl, pl_params);
            String path = "";
            if (selectedList!=null && selectedList.size()!=0) {
                path = selectedList.get(position);
            }
            if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                if (path != null && !path.equals("")) {
                    path = path.replace("small", "middle");
                }
            } else if (type.equals(AppConstants.PARAM_FRIENDSTER_HEAD)) {
                if (path != null && !path.equals("")) {
                    path = path.replace("small", "big");
                }
            } else if (type.equals(AppConstants.PARAM_CommonBrowser)) {

            } else if (type.equals(AppConstants.PARAM_PHOTOSHOWGENERAL)) {

            } else if (type.equals(AppConstants.PARAM_UPLOAD_STER)){
                if (path != null && !path.equals("")) {
                    path = path.replace("small", "middle");
                }
            } else if (type.equals(AppConstants.PARAM_UPLOAD_GROW)) {
                if (path != null && !path.equals("")) {
                    path = path.replace("small", "big");
                }
            } else if (type.equals(AppConstants.PARAM_UPLOAD_LIFE) || type.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                path = path.replace("small", "big");
            } else {
                path = path.replace("small", "middle");
            }
            if (!type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                if (type.equals(AppConstants.PARAM_UPLOAD_GROW)) {
                    if (path!=null && path.contains("http")) {

                        progress_rl.setVisibility(View.VISIBLE);
                        Glide.with(AppContext.getInstance())
                            .load(path)
                            .centerCrop()
                            .placeholder(R.color.grey) // 设置加载的时候的图片
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                                    progress_rl.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                                    progress_rl.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .error(R.drawable.ic_error) // 设置加载失败后显示的图片
                            .into(photoview);
//                        imageLoader.displayImage(path, photoview, ImageLoadOptions.getPhotoViewOptions(), new ImageLoadingListener(){
//
//                            @Override
//                            public void onLoadingStarted(String s, View view) {
//                                progress_bar.setProgress(0);
//                                progress_rl.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//
//                            @Override
//                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//
//                            @Override
//                            public void onLoadingCancelled(String s, View view) {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//                        }, new ImageLoadingProgressListener() {
//                            @Override
//                            public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                                progress_bar.setProgress((int) ((float) current / total * 100));
////                              percent_tv.setText((int) ((float) current/ total * 100)+"%");
//                                if (current < total) {
//                                    progress_rl.setVisibility(View.VISIBLE);
//                                } else {
//                                    progress_rl.setVisibility(View.GONE);
//                                }
//                            }
//                        });
                    } else {
                        if (path!=null && !path.equals("")) {

                            progress_bar.setProgress(0);
                            progress_rl.setVisibility(View.VISIBLE);
                            Glide.with(AppContext.getInstance())
                                .load("file:///" + path)
                                .centerCrop()
                                .placeholder(R.color.grey) // 设置加载的时候的图片
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                                        progress_rl.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                                        progress_rl.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .error(R.drawable.ic_error) // 设置加载失败后显示的图片
                                .into(photoview);
//                            imageLoader.displayImage("file:///" + path, photoview, ImageLoadOptions.getPhotoViewOptions(), new ImageLoadingListener(){
//
//                                @Override
//                                public void onLoadingStarted(String s, View view) {
//                                    progress_bar.setProgress(0);
//                                    progress_rl.setVisibility(View.VISIBLE);
//                                }
//
//                                @Override
//                                public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                    progress_rl.setVisibility(View.GONE);
//                                }
//
//                                @Override
//                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                                    progress_rl.setVisibility(View.GONE);
//                                }
//
//                                @Override
//                                public void onLoadingCancelled(String s, View view) {
//                                    progress_rl.setVisibility(View.GONE);
//                                }
//                            }, new ImageLoadingProgressListener() {
//                                @Override
//                                public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                                    progress_bar.setProgress((int) ((float) current / total * 100));
////                              percent_tv.setText((int) ((float) current / total * 100) + "%");
//                                    if (current < total) {
//                                        progress_rl.setVisibility(View.VISIBLE);
//                                    } else {
//                                        progress_rl.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                            imageLoader.displayImage("file:///" + path, photoview);
                        } else {
                            photoview.setImageResource(R.drawable.defaulticon);
                        }
                    }
                } else {
                    if (path == null || path.equals("")) {
                        photoview.setImageResource(R.drawable.defaulticon);
                    } else {
                        if (!path.contains("http")) {
                            path = "file:///" + path;
                        }

                        progress_bar.setProgress(0);
                        progress_rl.setVisibility(View.VISIBLE);
                        Glide.with(AppContext.getInstance())
                            .load(path)
                            .centerCrop()
                            .placeholder(R.color.grey) // 设置加载的时候的图片
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                                    progress_rl.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                                    progress_rl.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .error(R.drawable.ic_error) // 设置加载失败后显示的图片
                            .into(photoview);
//                        imageLoader.displayImage(path, photoview, ImageLoadOptions.getPhotoViewOptions(), new ImageLoadingListener(){
//
//                            @Override
//                            public void onLoadingStarted(String s, View view) {
//                                progress_bar.setProgress(0);
//                                progress_rl.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//
//                            @Override
//                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//
//                            @Override
//                            public void onLoadingCancelled(String s, View view) {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//                        }, new ImageLoadingProgressListener() {
//                            @Override
//                            public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                                progress_bar.setProgress((int) ((float) current / total * 100));
////                              percent_tv.setText((int) ((float) current / total * 100) + "%");
//                                if (current < total) {
//                                    progress_rl.setVisibility(View.VISIBLE);
//                                } else {
//                                    progress_rl.setVisibility(View.GONE);
//                                }
//                            }
//                        });
                    }
                }
            } else {
                if (path!=null && !path.equals("")) {

                    progress_bar.setProgress(0);
                    progress_rl.setVisibility(View.VISIBLE);
                    Glide.with(AppContext.getInstance())
                        .load("file:///" + path)
                        .centerCrop()
                        .placeholder(R.color.grey) // 设置加载的时候的图片
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                                progress_rl.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                                progress_rl.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .error(R.drawable.ic_error) // 设置加载失败后显示的图片
                        .into(photoview);
//                    imageLoader.displayImage("file:///" + path, photoview, ImageLoadOptions.getPhotoViewOptions(), new ImageLoadingListener(){
//
//                        @Override
//                        public void onLoadingStarted(String s, View view) {
//                            progress_bar.setProgress(0);
//                            progress_rl.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void onLoadingFailed(String s, View view, FailReason failReason) {
//                            progress_rl.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                            progress_rl.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onLoadingCancelled(String s, View view) {
//                            progress_rl.setVisibility(View.GONE);
//                        }
//                    }, new ImageLoadingProgressListener() {
//                        @Override
//                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                            progress_bar.setProgress((int) ((float) current / total * 100));
////                              percent_tv.setText((int) ((float) current / total * 100) + "%");
//                            if (current < total) {
//                                progress_rl.setVisibility(View.VISIBLE);
//                            } else {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//                    imageLoader.displayImage("file:///" + path, photoview);
                } else {
                    photoview.setImageResource(R.drawable.defaulticon);
                }
            }
            ((ViewPager)container).addView(imageLayout, 0);
//            String text = (position + 1) + "/" + selectedList.size() + "\n\n";
            String text = "\n\n";
//            if (istext) {
                if (arrayList!=null && arrayList.size() > position && arrayList.get(position)!=null && arrayList.get(position).length() > 0) {
                    text = text + arrayList.get(position);
                } else {
                    text = "";
                }
//            }
//          count_tv.setText(text);
            tv.setText(text);
            downtv.setText("");
            downtv.setFocusable(true);
//            final String newPath = path;
//            downtv.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//                            String time = null;
//                            Date d1 = new Date(System.currentTimeMillis());
//                            time = format.format(d1);
//                            String photoName = time + ".png";
//                            // 创建目录
//                            createSDCardDir();
//                            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(newPath);
//                            if (bitmap!=null) {
//                                save(bitmap, photoName);
//                                imgSaveName = photoName;
//                                handler.sendEmptyMessage(MSG_SAVE_OK);
//                            } else {
//                                handler.sendEmptyMessage(MSG_SAVE_FAIL);
//                            }
//                            bitmap = null;
//                        }
//                    }).start();
//                }
//            });
            return imageLayout;
        }

//        private final static int MSG_SAVE_OK = 1;
//        private final static int MSG_SAVE_FAIL = 2;
//        private String imgSaveName = "";
//        private Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MSG_SAVE_OK:
//                        Toast.makeText(PhotoManager_ViewPager.this, "保存成功！ " + "图片已存至 " + AppConstants.SAVE_PHOTO_PATH + imgSaveName, Toast.LENGTH_LONG).show();
//                        scanPhoto(AppConstants.SAVE_PHOTO_PATH + imgSaveName);
//                        break;
//                    case MSG_SAVE_FAIL:
//                        Toast.makeText(PhotoManager_ViewPager.this, "保存失败！", Toast.LENGTH_LONG).show();
//                        break;
//                }
//            }
//        };

        private int mChildCount = 0;

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
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
     * 刷新本地图片，能及时在本地相册中看到
     * 调用系统扫描文件类
     * @param imgFileName
     */
    private void scanPhoto(String imgFileName) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        if (file.exists()) {
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    Intent intent = new Intent(PhotoManager_ViewPager.this, ClassPhotoDetialManager.class);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                    intent.putExtra(AppConstants.PARAM_ALBUM, (Album)object);
                    startActivity(intent);
                } else if (type.equals(AppConstants.PARAM_CommonBrowser)) {
                    this.finish();
                } else if (type.equals(AppConstants.PARAM_PHOTOSHOWGENERAL)) {
                    this.finish();
                } else if (type.equals(AppConstants.PARAM_FRIENDSTER_HEAD)) {
                    this.finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_GROW)) {
                    this.finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_STER)) {
                    this.finish();
                } else {
                    Intent intent = new Intent(PhotoManager_ViewPager.this,LifeWorkPhoto.class);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                    intent.putExtra(AppConstants.PARAM_ALBUM, (LifePhoto)object);
                    intent.putExtra(AppConstants.INTENT_UPLOAD_TYPE, type);
                    intent.putExtra("index", mViewPager.getCurrentItem());
                    intent.putStringArrayListExtra("photos", imglist);
                    intent.putExtra("page", page);
                    intent.putStringArrayListExtra("descs", desclist);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(PhotoManager_ViewPager.this, LifeWorkPhoto.class);
                intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                intent.putExtra(AppConstants.INTENT_UPLOAD_TYPE, type);
                intent.putStringArrayListExtra("photos", imglist);
                intent.putStringArrayListExtra("descs", desclist);
                startActivity(intent);
            }
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_tv:
                if (!type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                    if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                        Intent intent = new Intent(PhotoManager_ViewPager.this, ClassPhotoDetialManager.class);
                        intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                        intent.putExtra(AppConstants.PARAM_ALBUM, (Album)object);
                        startActivity(intent);
                    } else if (type.equals(AppConstants.PARAM_CommonBrowser)) {
                        this.finish();
                    } else if (type.equals(AppConstants.PARAM_PHOTOSHOWGENERAL)) {
                        this.finish();
                    } else if (type.equals(AppConstants.PARAM_FRIENDSTER_HEAD)) {
                        this.finish();
                    } else if (type.equals(AppConstants.PARAM_UPLOAD_STER)) {
                        this.finish();
                    } else if (type.equals(AppConstants.PARAM_UPLOAD_GROW)) {
                        this.finish();
                    } else {
                        Intent intent = new Intent(PhotoManager_ViewPager.this, LifeWorkPhoto.class);
                        intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                        intent.putExtra(AppConstants.PARAM_ALBUM, (LifePhoto)object);
                        intent.putExtra(AppConstants.INTENT_UPLOAD_TYPE, type);
                        intent.putExtra("index", mViewPager.getCurrentItem());
                        intent.putStringArrayListExtra("photos", imglist);
                        intent.putExtra("page", page);
                        intent.putStringArrayListExtra("descs", desclist);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(PhotoManager_ViewPager.this, LifeWorkPhoto.class);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                    intent.putExtra(AppConstants.INTENT_UPLOAD_TYPE, type);
                    intent.putStringArrayListExtra("photos", imglist);
                    intent.putStringArrayListExtra("descs", desclist);
                    startActivity(intent);
                }
                this.finish();
                break;
            case R.id.right_tv:
                String oldPath = "";
                if (adapter == null) {
                    UtilsLog.i(TAG, "adapter is null");
                    return;
                }
                if (adapter.getSelectedList().size() > currentPosition) {
                    UtilsLog.i(TAG, "begin to save get currentPosition: " + currentPosition);
                    oldPath = adapter.getSelectedList().get(currentPosition);
                } else {
                    UtilsLog.i(TAG, "position is error");
                    return;
                }

                if (oldPath == null) {
                    UtilsLog.i(TAG, "olepath is null");
                    return;
                }

                if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    if (!oldPath.equals("")) {
                        oldPath = oldPath.replace("small", "middle");
                    }
                } else if (type.equals(AppConstants.PARAM_FRIENDSTER_HEAD)) {
                    if (!oldPath.equals("")) {
                        oldPath = oldPath.replace("small", "big");
                    }
                } else if (type.equals(AppConstants.PARAM_CommonBrowser)) {

                } else if (type.equals(AppConstants.PARAM_PHOTOSHOWGENERAL)) {

                } else if (type.equals(AppConstants.PARAM_UPLOAD_STER)){
                    if (!oldPath.equals("")) {
                        oldPath = oldPath.replace("small", "middle");
                    }
                } else if (type.equals(AppConstants.PARAM_UPLOAD_GROW)) {
                    if (!oldPath.equals("")) {
                        oldPath = oldPath.replace("small", "big");
                    }
                } else if (type.equals(AppConstants.PARAM_UPLOAD_LIFE) || type.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                    oldPath = oldPath.replace("small", "big");
                } else {
                    oldPath = oldPath.replace("small", "middle");
                }

                if (oldPath!=null && !oldPath.contains("http")) {
                    oldPath = "file:///" + oldPath;
                }

                final String newPath = oldPath;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        String time = null;
                        Date d1 = new Date(System.currentTimeMillis());
                        time = format.format(d1);
                        String photoName = time + ".png";
                        // 创建目录
                        createSDCardDir();
                        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(newPath);
                        if (bitmap!=null) {
                            save(bitmap, photoName);
                            imgSaveName = photoName;
                            handler.sendEmptyMessage(MSG_SAVE_OK);
//                              Toast.makeText(PhotoManager_ViewPager.this, "保存成功！ " + "图片已存至 " + AppConstants.SAVE_PHOTO_PATH + photoName, Toast.LENGTH_LONG).show();
                        } else {
                            handler.sendEmptyMessage(MSG_SAVE_FAIL);
//                              Toast.makeText(PhotoManager_ViewPager.this, "保存失败！", Toast.LENGTH_LONG).show();
                        }
                        if (bitmap!=null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                            System.gc();
                            bitmap = null;
                        }
                    }
                }).start();
                break;
            default:
                break;
        }

    }

    private final static int MSG_SAVE_OK = 1;
    private final static int MSG_SAVE_FAIL = 2;
    private String imgSaveName = "";
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_OK:
//                    Toast.makeText(PhotoManager_ViewPager.this, "保存成功！ " + "图片已存至 " + AppConstants.SAVE_PHOTO_PATH + imgSaveName, Toast.LENGTH_LONG).show();
                    showToast("图片保存成功");
                    scanPhoto(AppConstants.SAVE_PHOTO_PATH + imgSaveName);
                    break;
                case MSG_SAVE_FAIL:
                    showToast("保存失败");
                    break;
            }
        }
    };

    private void notifyData(String percent,TextView tv){
        tv.setText(percent);
    }

}
