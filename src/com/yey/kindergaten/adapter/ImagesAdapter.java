package com.yey.kindergaten.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.WLImage;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;
public class ImagesAdapter extends BaseListAdapter<Object> {

    private List<Object> mCheckList;
    private String type;
    public boolean editAction = false;
    protected static ArrayList<Album> checkList = new ArrayList<Album>();
    protected static ArrayList<WLImage>imagelist = new ArrayList<WLImage>();
    private Context context;
    private DisplayImageOptions imageOptions;
    private String flag = "2"; // 0 表示全选，1 表示全不选，2 表示撤销编辑状态
    private ImageLoader imageLoader;

    public void setAction(boolean action) {
        this.editAction = action;
        this.notifyDataSetChanged();
    }

    public ImagesAdapter(Context context, List<Object> list, List<Object> checkList, String type, DisplayImageOptions imageOptions, ImageLoader imageLoader) {
        super(context, list);
        mCheckList = checkList;             // 选择的list
        this.type = type;                   // 类型：班级相册；生活剪影，手工作品；
        this.context = context;             // 上下文
        this.imageOptions = imageOptions;   // imageLoader属性
        this.imageLoader = imageLoader;     // 加载器
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        try {
            if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                Album album = (Album)list.get(position);
                String url;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.common_browser_image_item, null);
                }

                ImageView selectphoto_select = ViewHolder.get(convertView, R.id.selectphoto_select);
                ImageView selectphoto_unselect = ViewHolder.get(convertView, R.id.selectphoto_unselect);
                ImageView mImageView = ViewHolder.get(convertView, R.id.selectphoto_image);
                View press = ViewHolder.get(convertView, R.id.press);
                press.setVisibility(View.VISIBLE);
                // TextView tv_selectPhtot = ViewHolder.get(convertView, R.id.tv_selectPhtot);
                if (this.editAction) {
                    if (position!=0 && album.getFilepath().contains("http")) {
                        if (AppConstants.photocheckList != null && AppConstants.photocheckList.contains(album)) {
                            selectphoto_select.setVisibility(View.VISIBLE);
                            selectphoto_unselect.setVisibility(View.GONE);
                            press.setBackgroundColor(context.getResources().getColor(R.color.focus_color));
                        } else {
                            selectphoto_unselect.setVisibility(View.VISIBLE);
                            selectphoto_select.setVisibility(View.GONE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                press.setBackground(context.getResources().getDrawable(R.drawable.item_press_bg));
                            } else {
                                press.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_press_bg));
                            }
                        }
                        // selectphoto_unselect.setVisibility(View.VISIBLE);
                        // selectphoto_select.setVisibility(View.INVISIBLE);
                    } else {
                        selectphoto_unselect.setVisibility(View.GONE);
                        selectphoto_select.setVisibility(View.GONE);
                        press.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    }
                } else {
                    AppConstants.photocheckList.clear();
                    selectphoto_unselect.setVisibility(View.GONE);
                    selectphoto_select.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        press.setBackground(context.getResources().getDrawable(R.drawable.item_press_bg));
                    } else {
                        press.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_press_bg));
                    }
                }
                if (album.getYp() == 1) {
                    url = album.getFilepath() + "!200x200";
                } else {
                    url = album.getFilepath() == null ? "" : album.getFilepath();
                }
                if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
                        if (album.getFilepath()!=null && album.getFilepath().contains("http")) {
                            // String url = album.getSmallPhotoAttachsURL().replace("img1.yeyimg.com", "sgsimg.zgyey.com");
                            GlideUtils.loadImage(context, url, mImageView );
//                            imageLoader.displayImage(url, mImageView, imageOptions);
                        } else {
                            GlideUtils.loadImage(context, "file:///" + album.getFilepath(), mImageView);
//                            imageLoader.displayImage("file:///" + album.getFilepath(), mImageView, imageOptions);
                        }
                } else {
                    if (position != 0) {
                        if (album.getFilepath()!=null && album.getFilepath().contains("http")) {
                            // String url = album.getSmallPhotoAttachsURL().replace("img1.yeyimg.com", "sgsimg.zgyey.com");
                            GlideUtils.loadImage(context, url, mImageView );
//                            imageLoader.displayImage(url, mImageView, imageOptions);
                        } else {
//                            Glide.with(mContext)
//                                    .load("file:///" + url)
//                                    .centerCrop()
//                                    .placeholder(R.drawable.icon_image_loading_default)
//                                    .crossFade()
//                                    .into(mImageView);
                            GlideUtils.loadImage(context, "file:///" + url, mImageView );
//                            imageLoader.displayImage("file:///" + url, mImageView, imageOptions);
                        }
                    } else {
                        // tv_selectPhtot.setVisibility(View.GONE);
                        mImageView.setImageResource(R.drawable.button_upload_classphoto);
                    }
                }
            } else if (type.equals(AppConstants.PARAM_UPLOAD_WORK) || type.equals(AppConstants.PARAM_UPLOAD_LIFE)) {
                WLImage image = (WLImage) list.get(position);
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.common_browser_image_item, null);
                }
                ImageView show_iv = (ImageView) ViewHolder.get(convertView, R.id.selectphoto_image);
                TextView decs_tv = (TextView) ViewHolder.get(convertView, R.id.classphoto_gv_count_tv);
                ImageView selectphoto_select = (ImageView) ViewHolder.get(convertView, R.id.selectphoto_select);
                ImageView selectphoto_unselect = (ImageView) ViewHolder.get(convertView, R.id.selectphoto_unselect);
                View press = ViewHolder.get(convertView, R.id.press);
                decs_tv.setVisibility(View.VISIBLE);
                decs_tv.setText(image.getPhoto_desc());
                press.setVisibility(View.VISIBLE);
                if (image.getPhoto_desc() == null || image.getPhoto_desc().length() == 0 || position == 0) {
                    decs_tv.setVisibility(View.GONE);
                }
                if (position!=0) {
                    if (flag.equals("2")) {
                        imagelist.clear();
                        selectphoto_unselect.setVisibility(View.INVISIBLE);
                        selectphoto_select.setVisibility(View.INVISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            press.setBackground(context.getResources().getDrawable(R.drawable.item_press_bg));
                        } else {
                            press.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_press_bg));
                        }
                    } else if (flag.equals("0")) {
                        selectphoto_unselect.setVisibility(View.INVISIBLE);
                        selectphoto_select.setVisibility(View.VISIBLE);
                        press.setBackgroundColor(context.getResources().getColor(R.color.focus_color));
                    } else if (flag.equals("1")) {
                        selectphoto_unselect.setVisibility(View.VISIBLE);
                        selectphoto_select.setVisibility(View.INVISIBLE);
                        press.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    }
                }

                if (position!=0 && image.getM_path()!=null && image.getM_path().contains("http")) {
                    if (AppConstants.checklist != null && AppConstants.checklist.contains(image)) {
                        selectphoto_select.setVisibility(View.VISIBLE);
                        selectphoto_unselect.setVisibility(View.GONE);
                        press.setBackgroundColor(context.getResources().getColor(R.color.focus_color));
                    } else {
                        if (!flag.equals("2")) {
                            selectphoto_unselect.setVisibility(View.VISIBLE);
                            selectphoto_select.setVisibility(View.GONE);
                            press.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                        }
                    }
                } else {
                    selectphoto_unselect.setVisibility(View.GONE);
                    selectphoto_select.setVisibility(View.GONE);
    //              press.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                }
                if (position!=0) {
                    if (image.getM_path()!=null && image.getM_path().contains("http")) {
                        String url = image.getM_path(); // String 不是基本类型，但是是传值，不是传引用
    //                  String url_rl = url.replace("small", "m");
                        GlideUtils.loadImage(AppContext.getInstance(), url, show_iv);
//                        imageLoader.displayImage(url, show_iv, ImageLoadOptions.getLifePhotoOptions(), new SimpleImageLoadingListener() {
//
//                            @Override
//                            public void onLoadingStarted(String imageUri, View view) { }
//
//                            @Override
//                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) { }
//
//                            @Override
//                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) { }
//
//                        }, new ImageLoadingProgressListener() {
//
//                            @Override
//                            public void onProgressUpdate(String imageUri, View view, int current, int total) { }
//
//                        });
                    } else {
                        GlideUtils.loadImage(AppContext.getInstance(), "file:///" + image.getM_path(), show_iv);
//                        imageLoader.displayImage("file:///" + image.getM_path(), show_iv, ImageLoadOptions.getLifePhotoOptions(), new SimpleImageLoadingListener() {
//                            @Override
//                            public void onLoadingStarted(String imageUri, View view) { }
//
//                            @Override
//                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) { }
//
//                            @Override
//                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) { }
//
//                        }, new ImageLoadingProgressListener() {
//
//                            @Override
//                            public void onProgressUpdate(String imageUri, View view, int current, int total) { }
//
//                        });
                    }
                } else {
                    if (show_iv!=null) {
                        show_iv.setImageResource(R.drawable.button_upload_classphoto);
                        if (position == 0) {
                            press.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                        }
                    }
                }
            } else if (type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                WLImage image = (WLImage) list.get(position);
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.common_browser_image_item, null);
                }
                ImageView show_iv = (ImageView) ViewHolder.get(convertView, R.id.selectphoto_image);
                TextView decs_tv = (TextView) ViewHolder.get(convertView, R.id.classphoto_gv_count_tv);

                ImageView selectphoto_select = (ImageView) ViewHolder.get(convertView, R.id.selectphoto_select);
                ImageView selectphoto_unselect = (ImageView) ViewHolder.get(convertView, R.id.selectphoto_unselect);

                decs_tv.setVisibility(View.VISIBLE);
                decs_tv.setText(image.getPhoto_desc());

                if (image.getPhoto_desc() == null || image.getPhoto_desc().length() == 0) {
                   decs_tv.setVisibility(View.GONE);
                }

                if (flag.equals("2")) {
                    selectphoto_unselect.setVisibility(View.INVISIBLE);
                    selectphoto_select.setVisibility(View.INVISIBLE);
                } else if (flag.equals("0")) {
                    selectphoto_unselect.setVisibility(View.INVISIBLE);
                    selectphoto_select.setVisibility(View.VISIBLE);
                } else if (flag.equals("1")) {
                    selectphoto_unselect.setVisibility(View.VISIBLE);
                    selectphoto_select.setVisibility(View.INVISIBLE);
                }

                if (this.editAction) {
                    if (position!=0) {
                        selectphoto_unselect.setVisibility(View.VISIBLE);
                        selectphoto_select.setVisibility(View.INVISIBLE);
                    }
                } else {
                    imagelist.clear();
                    selectphoto_unselect.setVisibility(View.INVISIBLE);
                    selectphoto_select.setVisibility(View.INVISIBLE);
                }

                if (image.getM_path()!=null && image.getM_path().contains("http")) {
                    ImageLoader.getInstance().displayImage(image.getM_path(), show_iv, ImageLoadOptions.getLifePhotoOptions(), new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) { }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) { }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) { }

                    }, new ImageLoadingProgressListener() {

                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) { }

                    });
                } else {
                    ImageLoader.getInstance().displayImage("file:///" + image.getM_path(), show_iv, ImageLoadOptions.getLifePhotoOptions(), new SimpleImageLoadingListener(){

                        @Override
                        public void onLoadingStarted(String imageUri, View view) { }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) { }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) { }

                    }, new ImageLoadingProgressListener() {

                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) { }

                    });
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("信息" + e.getMessage() + "原因--->" + e.getCause());
        }
        return convertView;
    }

    /**
     * 删除图片
     * @param imageList
     */
    public void delAdpaterPhoto(List<WLImage> imageList) {
        list.removeAll(imageList);
        notifyDataSetChanged();
    }

	public void setCheck(int postion, View view) { // 添加照片到checkList中，并切换图标
		if (postion!=0) {
            if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                Album photo = (Album) getList().get(postion);
                boolean checked = checkList.contains(photo);
                ImageView select_iv = ViewHolder.get(view, R.id.selectphoto_select);
                ImageView unselect_iv = ViewHolder.get(view, R.id.selectphoto_unselect);
                View press = ViewHolder.get(view, R.id.press);
                press.setVisibility(View.VISIBLE);
                if (checked) {
                    AppConstants.photocheckList.remove(getList().get(postion));
                    checkList.remove(photo);
                    select_iv.setVisibility(View.INVISIBLE);
                    unselect_iv.setVisibility(View.VISIBLE);
                    press.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                } else {
                    AppConstants.photocheckList.add(getList().get(postion));
                    checkList.add(photo);
                    select_iv.setVisibility(View.VISIBLE);
                    unselect_iv.setVisibility(View.INVISIBLE);
                    press.setBackgroundColor(context.getResources().getColor(R.color.focus_color));
                }
            } else if (type.equals(AppConstants.PARAM_UPLOAD_WORK) ||
                    type.equals(AppConstants.PARAM_UPLOAD_LIFE) || type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                WLImage photo = (WLImage) getList().get(postion);
                boolean checked = imagelist.contains(photo);
                ImageView select_iv = ViewHolder.get(view, R.id.selectphoto_select);
                ImageView unselect_iv = ViewHolder.get(view, R.id.selectphoto_unselect);

                View press = ViewHolder.get(view,R.id.press);
                press.setVisibility(View.VISIBLE);
                if (checked) {
                    AppConstants.checklist.remove(getList().get(postion));
                    imagelist.remove(photo);
                    select_iv.setVisibility(View.INVISIBLE);
                    unselect_iv.setVisibility(View.VISIBLE);
                    press.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                } else {
                    AppConstants.checklist.add(getList().get(postion));
                    imagelist.add(photo);
                    select_iv.setVisibility(View.VISIBLE);
                    unselect_iv.setVisibility(View.INVISIBLE);
                    press.setBackgroundColor(context.getResources().getColor(R.color.focus_color));
                }
            }
		}
    }
	
	public void setAllCheck(String flag) {
	  	this.flag = flag;
		if (flag.equals("0")) {
			for (int i = 1; i < getList().size(); i++) {
				WLImage photo = (WLImage) getList().get(i);
	        	imagelist.add(photo);
	        	AppConstants.checklist.add(photo);
			}
		} else if (flag.equals("1") || flag.equals("2")) {
			AppConstants.checklist.clear();
			imagelist.clear();
		}
		this.notifyDataSetChanged();
	}
	
	public static ArrayList<Album> getCheckList() {
		return checkList;
	}

	public static ArrayList<WLImage> getCheckImageList(){
		return imagelist;	
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}


}
