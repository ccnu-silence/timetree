package com.yey.kindergaten.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yey.kindergaten.R;

import java.io.InputStream;

public class GlideUtils {

    private final static String TAG = "Utils";

    public static void loadImage(Context context, String url, ImageView imageView) {
        // 这个只设置一次就好了
//        Glide.setup(new GlideBuilder(context)
//                .setDecodeFormat(DecodeFormat.PREFER_RGB_565)); // Glide默认的Bitmap格式是RGB_565
//        Glide.with(context)
//                .load(url)
//                .centerCrop()
//                .placeholder(R.drawable.icon_image_loading_default)
//                .error(R.drawable.icon_image_loading_default)
//                .crossFade()
//                .thumbnail(2)
//                .into(imageView);

        Glide.with(context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.icon_image_loading_default) // 设置加载的时候的图片
            .crossFade()
            .listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                    return false;
                }
            })
            .error(R.drawable.ic_error) // 设置加载失败后显示的图片
            .into(imageView);
    }

    public static void loadFriendDataImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.common_defalut_photo_loading) // 设置加载的时候的图片
            .crossFade()
            .error(R.drawable.common_defalut_photo_loading) // 设置加载失败后显示的图片
            .into(imageView);
    }

    public static void loadClassPhotoImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.icon_image_galley_default) // 设置加载的时候的图片
            .crossFade()
            .error(R.drawable.icon_image_galley_default) // 设置加载失败后显示的图片
            .into(imageView);
    }

    public static void loadHeadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.defaulticon) // 设置加载的时候的图片
            .crossFade()
            .error(R.drawable.defaulticon) // 设置加载失败后显示的图片
            .into(imageView);
    }

    public static void loadGeneralImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.icon_imageview_error) // 设置加载的时候的图片
            .crossFade()
            .error(R.drawable.icon_imageview_error) // 设置加载失败后显示的图片
            .into(imageView);
    }

    public static void loadPicImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher) // 设置加载的时候的图片
            .crossFade()
            .error(R.drawable.ic_launcher) // 设置加载失败后显示的图片
            .into(imageView);
    }

    public static Bitmap readBitMap (Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, options);
    }


}
