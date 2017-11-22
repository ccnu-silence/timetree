package com.yey.kindergaten.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.util.LazyGallery;
import com.yey.kindergaten.widget.SquareImageView;

import java.util.ArrayList;
import java.util.List;

public class ImgChooserAdapter extends BaseListAdapter<Photo> {

	private LayoutInflater mInflater;
	private Context context;
	private LazyGallery lazyGallery;
	private ArrayList<Photo> gridImageItemList;
    private DisplayImageOptions imageOptions;
    private List<Photo> mCheckList;

	public ImgChooserAdapter(Context context, ArrayList<Photo> data, ArrayList<Photo> checkList, DisplayImageOptions imageOptions) {
        super(context, data);
		lazyGallery = new LazyGallery();
		this.context = context;
		gridImageItemList = data;
        mCheckList = checkList;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageOptions = imageOptions;
	}

	public int getCount() {
		return gridImageItemList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate( R.layout.item_gridview, null);
            holder.imageview = (SquareImageView) convertView.findViewById(R.id.item_img_gridview);
            holder.selectImage = (ImageView) convertView.findViewById(R.id.selectphoto_select);
            holder.unselectImage = (ImageView) convertView.findViewById(R.id.selectphoto_unselect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageview.setId(position);
        holder.imageview.setTag(position);

        if (AppContext.checkList.contains(gridImageItemList.get(position))) {
            holder.selectImage.setVisibility(View.VISIBLE);
            holder.unselectImage.setVisibility(View.GONE);
        } else {
            holder.selectImage.setVisibility(View.GONE);
            holder.unselectImage.setVisibility(View.VISIBLE);
        }
        // 加上会报错
//        GlideUtils.loadImage(context, gridImageItemList.get(position).imgPath, holder.imageview);
        holder.imageview.setImageBitmap(lazyGallery.loadGallery(context, gridImageItemList.get(position).imgPath, holder.imageview, new LazyGallery.ImageCallback() {
            public void imageLoaded(Bitmap bitmap) {
                if (!((Integer)(holder.imageview.getTag())).equals((Integer)position)) return;
                holder.imageview.setImageBitmap(bitmap);
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                holder.imageview.setAnimation(animation);
            }
        }));

//      imageLoader.displayImage("file://" + gridImageItemList.get(position).imgPath, holder.imageview, imageOptions);
        return convertView;
    }

    public void setCheck(int postion, View view, boolean isOnePhoto) {
        Photo photo = list.get(postion);
        boolean checked = mCheckList.contains(photo);
        ViewHolder holder = (ViewHolder) view.getTag();

        if (checked) {
            mCheckList.remove(photo);
            holder.selectImage.setVisibility(View.GONE);
            holder.unselectImage.setVisibility(View.VISIBLE);
        } else {
            if (isOnePhoto && mCheckList.size() > 1) {
                ShowToast("只能选一张哦");
            } else {
                if (mCheckList.size() < 20) {
                    mCheckList.add(photo);
                    holder.selectImage.setVisibility(View.VISIBLE);
                    holder.unselectImage.setVisibility(View.GONE);
                } else {
                    ShowToast("最多只能选择20张照片进行上传哦");
                }
            }
        }
    }

    public void setOneCheck(int postion, View view) {
        Photo photo = list.get(postion);
        boolean checked = mCheckList.contains(photo);
        ViewHolder holder = (ViewHolder) view.getTag();

        if (checked) {
            mCheckList.remove(photo);
            holder.selectImage.setVisibility(View.GONE);
            holder.unselectImage.setVisibility(View.VISIBLE);
        } else {
            mCheckList.add(photo);
            holder.selectImage.setVisibility(View.VISIBLE);
            holder.unselectImage.setVisibility(View.GONE);
        }
    }

    public List<Photo> getItem(){
         return gridImageItemList;
    }

    public static class ViewHolder {
		public SquareImageView imageview;
        public ImageView selectImage, unselectImage;
	}
	
	public void clearCache() {
		lazyGallery.clearImgCache();
	}
	
}
