package com.yey.kindergaten.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.yey.kindergaten.task.AsyncTask;

import java.lang.ref.WeakReference;

public class AysncImageLoader extends AsyncTask<String, Void, Bitmap> {


    private final WeakReference<ImageView> imageViewReference;
    private String data = "";

    public AysncImageLoader (ImageView imageView){
        imageViewReference=new WeakReference<ImageView>(imageView);
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        data=params[0];
        System.out.println("data:"+data);
        Bitmap bmp= ThumbnailUtils.createVideoThumbnail(data, MediaStore.Images.Thumbnails.MINI_KIND);
        bmp=ThumbnailUtils.extractThumbnail(bmp, 100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bmp;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference!=null && bitmap!=null) {
            final ImageView imageView=imageViewReference.get();
            if (imageView!=null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}