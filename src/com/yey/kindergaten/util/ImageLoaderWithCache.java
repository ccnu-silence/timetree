package com.yey.kindergaten.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/5/15.
 */
public class ImageLoaderWithCache {
    private static HashMap<String, SoftReference<Bitmap>> mImageCache;

    public static Bitmap loadBitmapImage(String path) {

        if(mImageCache.containsKey(path)) {

            SoftReference<Bitmap> softReference = mImageCache.get(path);

            Bitmap bitmap = softReference.get();

            if(null != bitmap)

                return bitmap;

        }

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        mImageCache.put(path, new SoftReference<Bitmap>(bitmap));

        return bitmap;

    }

    public static Drawable loadDrawableImage(String path) {

        return new BitmapDrawable(loadBitmapImage(path));

    }
}
