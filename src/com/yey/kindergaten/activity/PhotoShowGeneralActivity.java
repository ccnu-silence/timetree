package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.PhotoShow;
import com.yey.kindergaten.bean.PhotoViewJson;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.AutoResizeTextView;
import com.yey.kindergaten.widget.MyGridviewWithScrollView;
import com.yey.kindergaten.widget.MyListViewWithScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一的图片浏览类，网格浏览 & 并排浏览
 * Created by cm_pc2 on 2015/9/22.
 */
public class PhotoShowGeneralActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.left_btn)ImageView leftbtn;
    @ViewInject(R.id.header_title)TextView tv_title;

    @ViewInject(R.id.tv_more_photo)TextView tv_more_photo; //　查看更多

    @ViewInject(R.id.show_gridview)MyGridviewWithScrollView show_gridview;
    @ViewInject(R.id.show_listview)MyListViewWithScrollView show_listview;

    private PhotoShowAdapter viewAdapter = null;

    private String mTitle = "";                                  // 标题
    private String api = "";                                     // 接口
    private String openType = "";                                // 客户端浏览模式。0 : 网格浏览，一排多张图片。1 : 并排浏览
    private List<PhotoShow> photos = new ArrayList<PhotoShow>(); // 字符串。供客户端调用，需要是完整路径的，用于获取需要浏览的所有图片url，后台可动态替换，但api的返回必须是一致的
    private String replace = "";                                 // 小图都是包含"_small"，大图没有统一，替换成replace的字符串；

    private ArrayList<PhotoShow> photoShows = new ArrayList<PhotoShow>();
    private int nextId = -1;

    private ArrayList<String> imglist = new ArrayList<String>();
    private ArrayList<String> decslist = new ArrayList<String>();
    private boolean isActivityExist = true;

    private static final String PATHA = Environment.getExternalStorageDirectory() + "/yey/kindergaten/readyuoload/";
    private final static String TAG = "PhotoShowGeneralActivity";

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    initAdapter();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoshow_general);
        ViewUtils.inject(this);
        initView();
    }

    private void releaseImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null)
            d.setCallback(null);
        imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActivityExist = false;
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();

        leftbtn = null;
        tv_title = null;

        tv_more_photo = null;
        show_gridview = null;
        show_listview = null;
        setContentView(R.layout.activity_null);
    }

    private void initIntentData() {
        if (!("").equals(api)) {
            AppServer.getInstance().getPhotos(api, nextId, new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (!isActivityExist) {
                        return;
                    }
                    if (code == AppServer.REQUEST_SUCCESS) {
                        PhotoViewJson photoViewJson = (PhotoViewJson) obj;
                        mTitle = photoViewJson.getTitle();
                        nextId = photoViewJson.getNextid();

                        photoShows.clear();
                        photoShows = photoViewJson.getPhotoShow();

                        if (photos!=null) {
                            photos.addAll(photoShows);
                        }
                        if (imglist!=null) {
                            imglist.clear();
                        }
                        if (photos!=null && photos.size()!=0) {
                            for (PhotoShow photo : photos) { // 平铺浏览用小图，点击用大图
                                if (photo.source == 0) {
                                    String imgurl1 = photo.url;
                                    if (replace!=null && !replace.equals("")) {
                                        imgurl1 = imgurl1.replace("_small", replace);
                                    }
                                    imglist.add(imgurl1);
                                } else {
                                    String imgurl2 = photo.url;
                                    if (imgurl2!=null && imgurl2.contains("!")) {
                                        imgurl2 = imgurl2.substring(0, imgurl2.indexOf("!"));
                                    }
                                    imglist.add(imgurl2);
                                }
                            }
                        }
                        if (decslist!=null) {
                            decslist.clear();
                        }
                        if (photos!=null && photos.size()!=0) {
                            for (PhotoShow photo : photos) {
                                decslist.add(photo.desc);
                            }
                        }

                        mhandler.sendEmptyMessage(1);
                        UtilsLog.i(TAG, "getPhoto success! ");
                    } else {
                        UtilsLog.i(TAG, "getPhoto fail: " + message);
                    }
                }
            });
        } else {
            UtilsLog.i(TAG, "api is empty! ");
        }

    }

    private void startLargeView(int posision) {
        Intent intent = new Intent(PhotoShowGeneralActivity.this, PhotoManager_ViewPager.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", AppConstants.PARAM_PHOTOSHOWGENERAL);
        bundle.putStringArrayList("imglist", imglist);
        bundle.putStringArrayList("decslist", decslist);
        bundle.putInt("position", posision);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initView() {
        if (getIntent().getExtras()!=null) {
            api = getIntent().getExtras().getString("api");
//            title = getIntent().getExtras().getString("title");
            openType = getIntent().getExtras().getString("openType");
//            photos = getIntent().getParcelableArrayListExtra("photos");
            replace = getIntent().getExtras().getString("replace");
        }

        leftbtn.setVisibility(View.VISIBLE);
        tv_title.setText(("").equals(mTitle) ? "图片预览" : mTitle);
        tv_more_photo.setVisibility(View.VISIBLE);
        initIntentData();
        initAdapter();
        tv_more_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextId == 0) {
                    tv_more_photo.setText("没有更多照片了...");
                } else {
                    tv_more_photo.setText("查看更多照片...");
                    initIntentData();
                }
            }
        });
    }

    private void initAdapter() {
        if (photos == null) {return;}
        viewAdapter = new PhotoShowAdapter(this, photos);

        if (openType!=null && openType.equals("0")) {       // 网格浏览
            show_gridview.setVisibility(View.VISIBLE);
            show_gridview.setAdapter(viewAdapter);
            show_listview.setVisibility(View.GONE);
            show_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startLargeView(i);
                }
            });
        } else if (openType!=null && openType.equals("1")) {// 并排浏览
            show_gridview.setVisibility(View.GONE);
            show_listview.setVisibility(View.VISIBLE);
            show_listview.setAdapter(viewAdapter);
            show_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startLargeView(i);
                }
            });
        }
    }

    class PhotoShowAdapter extends BaseListAdapter<PhotoShow> {

        private Context context;
        public PhotoShowAdapter(Context context, List<PhotoShow> list) {
            super(context, list);
            this.context = context;
        }
        @Override
        public View bindView(int position, View convertView, ViewGroup parent) {
            final PhotoShow photoShow = getList().get(position);

            if (convertView == null) {
                convertView = createViewByType();
            }

//            RelativeLayout progress_rl0 = (RelativeLayout) convertView.findViewById(R.id.progress_rl);
//            ProgressBar progress_bar0 = (ProgressBar) convertView.findViewById(R.id.progress_bar);

            if (openType.equals("0")) {
                RelativeLayout progress_rl0 = ViewHolder.get(convertView, R.id.progress_rl);
                ProgressBar progress_bar0 = ViewHolder.get(convertView, R.id.progress_bar);
                ImageView selectphoto_image = ViewHolder.get(convertView, R.id.selectphoto_image);            // 图片
                TextView classphoto_gv_count_tv = ViewHolder.get(convertView, R.id.classphoto_gv_count_tv);   // 描述

                if (photoShow != null) {
                    classphoto_gv_count_tv.setText(photoShow.desc);

                    final RelativeLayout progress_rl = progress_rl0;
                    final ProgressBar progress_bar = progress_bar0;
                    progress_bar.setProgress(0);
                    progress_rl.setVisibility(View.VISIBLE);
                    String pathUrl = photoShow.url == null ? "" : photoShow.url;
                    if (!pathUrl.contains("http")) {
                        pathUrl = "file:///" + pathUrl;
                    }
                    if (pathUrl.contains("!")) {
                        pathUrl = pathUrl.substring(0, pathUrl.indexOf("!"));
                    }

                    Glide.with(AppContext.getInstance())
                        .load(pathUrl)
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
                        .into(selectphoto_image);

//                    imageLoader.displayImage(pathUrl, selectphoto_image, ImageLoadOptions.getPhotoViewOptions(), new ImageLoadingListener(){
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
//                            if (current < total) {
//                                progress_rl.setVisibility(View.VISIBLE);
//                            } else {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//                    ImageLoader.getInstance().displayImage(photoShow.url, selectphoto_image, ImageLoadOptions.getPhotoViewOptions()); // 平铺浏览用小图，点击用大图
                }
            } else {
                RelativeLayout progress_rl0 = ViewHolder.get(convertView, R.id.progress_rl);
                ProgressBar progress_bar0 = ViewHolder.get(convertView, R.id.progress_bar);
                LinearLayout ll_show_imageview = ViewHolder.get(convertView, R.id.ll_show_imageview);   // 布局
                ImageView show_imageview = ViewHolder.get(convertView, R.id.show_imageview);            // 图片
                AutoResizeTextView show_image_desc = ViewHolder.get(convertView, R.id.show_image_desc); // 描述

                if (photoShow != null) {
                    show_image_desc.setText(photoShow.desc);

                    final RelativeLayout progress_rl = progress_rl0;
                    final ProgressBar progress_bar = progress_bar0;

                    String pathUrl = photoShow.url == null ? "" : photoShow.url;
                    if (!pathUrl.contains("http")) {
                        pathUrl = "file:///" + pathUrl;
                    }
                    if (pathUrl.contains("!")) {
                        pathUrl = pathUrl.substring(0, pathUrl.indexOf("!"));
                    }

                    Glide.with(AppContext.getInstance())
                        .load(pathUrl)
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
                        .into(show_imageview);

//                    imageLoader.displayImage(pathUrl, show_imageview, ImageLoadOptions.getPhotoViewOptions(), new ImageLoadingListener(){
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
//                            if (current < total) {
//                                progress_rl.setVisibility(View.VISIBLE);
//                            } else {
//                                progress_rl.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//                    ImageLoader.getInstance().displayImage(photoShow.url, show_imageview, ImageLoadOptions.getPhotoViewOptions()); // 平铺浏览用小图，点击用大图
                }
            }

            return convertView;
        }

        private View createViewByType() {
            if (openType.equals("0")) { // 网格浏览
                return mInflater.inflate(R.layout.inflater_showphoto_wg, null);
            } else {                    // 并排浏览
                return mInflater.inflate(R.layout.inflater_showphoto_pp, null);
            }
        }

    }

    @OnClick({R.id.left_btn})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_btn:
                PhotoShowGeneralActivity.this.finish();
                break;
            default:
                break;
        }
    }

}
