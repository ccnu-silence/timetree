package com.yey.kindergaten.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.util.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

public class GridAddImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Photo> dataList;
    private DisplayMetrics dm;

    public GridAddImageAdapter(Context c, ArrayList<Photo> dataList) {
        mContext = c;
        this.dataList = dataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    public void addList(ArrayList<Photo> datalist){
           this.dataList=datalist;
    }

    public List<Photo> getData(){
        return dataList;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, dipToPx(75)));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else
            imageView = (ImageView) convertView;
            String path;
            if (dataList != null && position<dataList.size()) {
                path = dataList.get(position).imgPath;
            } else{
                path = "camera_default";
            }
            Log.i("path", "path:" + path + "::position" + position);
            if (path.contains("default"))
                imageView.setImageResource(R.drawable.camera_default);
            else {
                ImageLoader.getInstance().displayImage("file:///"+path, imageView, ImageLoadOptions.getOptions());
           }
        return imageView;
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }

    // 对分辨率较大的图片进行缩放
    public Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();

        float scaleWidth = (width / w);
        float scaleHeight = (height / h);

        matrix.postScale(scaleWidth, scaleHeight); // 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

        return newbmp;

    }

}
