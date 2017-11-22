package com.yey.kindergaten.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MyImageView extends ImageView{

	 private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
	 private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	 private static final int COLORDRAWABLE_DIMENSION = 1;
	 private static final int DEFAULT_BORDER_WIDTH = 0;
	 private static final int DOWN = 0;
	 private static final int UP = 1;
	 
	
	Bitmap bitmap;
	int state=UP;
    Paint paint = new Paint();
	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyImageView(Context context) {
		super(context);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		this.bitmap=bm;
	}
	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		this.bitmap = getBitmapFromDrawable(drawable);
	}
	
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if(resId!=0){
			Resources res=getResources();
			this.bitmap=BitmapFactory.decodeStream(res.openRawResource(resId));
		}
		
	}
	
	 private Bitmap getBitmapFromDrawable(Drawable drawable) {
	        if (drawable == null) {
	            return null;
	        }
	        if (drawable instanceof BitmapDrawable) {
	            return ((BitmapDrawable) drawable).getBitmap();
	        }
	        try {
	            Bitmap bitmap;
	            if (drawable instanceof ColorDrawable) {
	                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
	            } else {
	                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
	            }
	            Canvas canvas = new Canvas(bitmap);
	            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	            drawable.draw(canvas);
	            return bitmap;
	        } catch (OutOfMemoryError e) {
	            return null;
	        }
	    }
	 
	 
	 @Override
	protected void onDraw(Canvas canvas) {
		if(state==DOWN){
			paint.setAlpha(100);
		}else{
			paint.setAlpha(255);
		}
         canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap
                .getHeight()),new Rect(0, 0, bitmap.getWidth(), bitmap
                        .getHeight()), paint);
        }	
	 
	 @Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			state=DOWN;
			invalidate();
			break;
          
		case MotionEvent.ACTION_UP:
			state=UP;
			invalidate();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}


}
