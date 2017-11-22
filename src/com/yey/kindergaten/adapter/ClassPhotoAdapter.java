package com.yey.kindergaten.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.LazyGallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPhotoAdapter extends BaseListAdapter<Album> {

    private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
    protected static ArrayList<Album> checkList = new ArrayList<Album>();
    private LazyGallery lazyGallery;
    private DisplayImageOptions imageOptions;
    private ImageLoader imageLoader;
    Context context;

    public ClassPhotoAdapter(Context context, List<Album> list, DisplayImageOptions imageOptions, ImageLoader imageLoader) {
        super(context, list);
        this.context = context;
        lazyGallery = new LazyGallery();
        this.imageOptions = imageOptions;
        this.imageLoader = imageLoader;
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {
        final Album cp = getList().get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_classphoto_main_gridview_item, null);
        }
        final ImageView album = ViewHolder.get(convertView, R.id.classphoto_gv_album_iv);
        TextView albumcount = ViewHolder.get(convertView, R.id.classphoto_gv_count_tv);
        LinearLayout bottomblock = ViewHolder.get(convertView, R.id.ll_bottom_block);
        ImageView select_iv = ViewHolder.get(convertView, R.id.select);
        ImageView unselect_iv = ViewHolder.get(convertView, R.id.unselect);
        String url = cp.getAlbumCover();
        String count = cp.getPhotoCount();
        String name = cp.getAlbumName();
        if (url!=null && !url.equals("")) {
            if (!url.equals("add")) {
                albumcount.setVisibility(View.VISIBLE);
                albumcount.setText(name + "(" + count + ")");
                albumcount.setTextColor(context.getResources().getColor(R.color.white));
                bottomblock.setVisibility(View.VISIBLE);
                bottomblock.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent_black));
                GlideUtils.loadClassPhotoImage(context, url, album);
//                imageLoader.displayImage(url, album, imageOptions);
//                album.setImageBitmap(lazyGallery.loadGallery(context, url, album, new LazyGallery.ImageCallback() {
//                    @Override
//                    public void imageLoaded(Bitmap bitmap) {
//                        if (album.getTag()!=null) {
//                            if (!((album.getTag())).equals(position))return;
//                        }
//                        album.setImageBitmap(bitmap);
//                    }
//                }));

                // BitmapCache.getInstance().displayNetBmp(album, url, null,ImageLoadOptions.getClassPhotoOptions());
            } else {
                albumcount.setText("新建相册");
                albumcount.setVisibility(View.GONE);
                albumcount.setTextColor(context.getResources().getColor(R.color.black));
                bottomblock.setVisibility(View.VISIBLE);
                bottomblock.setBackgroundDrawable(null);
                album.setImageResource(R.drawable.button_new_album);
            }
        } else {
            album.setImageResource(R.drawable.icon_image_galley_default);
            albumcount.setVisibility(View.VISIBLE);
            albumcount.setText(name + "(" + count + ")");
            albumcount.setTextColor(context.getResources().getColor(R.color.white));
            bottomblock.setVisibility(View.VISIBLE);
            bottomblock.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent_black));
        }
        if (position!=0) {
            if (this.editAction) {
                boolean checked = checkList.contains(cp);
                if (!checked) {
                    select_iv.setVisibility(View.INVISIBLE);
                    unselect_iv.setVisibility(View.VISIBLE);
                } else {
                    select_iv.setVisibility(View.VISIBLE);
                    unselect_iv.setVisibility(View.INVISIBLE);
                }
            } else {
                checkList.clear();
                unselect_iv.setVisibility(View.INVISIBLE);
                select_iv.setVisibility(View.INVISIBLE);
            }
        } else {
            unselect_iv.setVisibility(View.INVISIBLE);
            select_iv.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void setmSelectMap(Map<Integer, Boolean> mSelectMap) {
        this.mSelectMap = mSelectMap;
    }

    public Map<Integer, Boolean> getmSelectMap() {
        return this.mSelectMap;
    }

    public boolean editAction = false;
    public void setAction(boolean action) {
        this.editAction = action;
        this.notifyDataSetChanged();
    }

    public void setCheck(int postion, View view) {
        Album photo = getList().get(postion);
        boolean checked = checkList.contains(photo);
        // ViewHolder holder = (ViewHolder) view.getTag();
        ImageView select_iv = ViewHolder.get(view, R.id.select);
        ImageView unselect_iv = ViewHolder.get(view, R.id.unselect);
        if (checked) {
            AppConstants.checkList.remove(photo);
            checkList.remove(photo);
            select_iv.setVisibility(View.INVISIBLE);
            unselect_iv.setVisibility(View.VISIBLE);
        } else {
            AppConstants.checkList.add(photo);
            checkList.add(photo);
            select_iv.setVisibility(View.VISIBLE);
            unselect_iv.setVisibility(View.INVISIBLE);
        }
    }

    public static ArrayList<Album> getCheckList() {
		return checkList;
	}

}
