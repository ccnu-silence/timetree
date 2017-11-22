package com.yey.kindergaten.widget;

import com.yey.kindergaten.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView.ScaleType;

public class MyClickTextView extends TextView{

	 private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
	 private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	 private static final int COLORDRAWABLE_DIMENSION = 1;
	 private static final int DEFAULT_BORDER_WIDTH = 0;
	 private static final int DOWN = 0;
	 private static final int UP = 1;
	
	
	Drawable[] drawables;
	public MyClickTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyClickTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	
	
	@Override
	public Drawable[] getCompoundDrawables() {
		return super.getCompoundDrawables();
	}
  
	public void setDrawables(Drawable[] drawables) {
		this.drawables = drawables;
	}
	
	    @Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Drawable a=getResources().getDrawable(R.drawable.icon_todo);			
				this.setCompoundDrawables(a, a, a, a);
				invalidate();
				break;
	         
			case MotionEvent.ACTION_UP:
//				this.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
//				invalidate();
				Drawable a1=getResources().getDrawable(R.drawable.icon_todo);			
				this.setCompoundDrawables(a1, a1, a1, a1);
				invalidate();
				break;
			default:
				break;
			}
			return super.onTouchEvent(event);
		}

	    @SuppressLint("ResourceAsColor")
		public static StateListDrawable getStateListDrawable(Drawable normal) {
			StateListDrawable listDrawable = new StateListDrawable();    
			Bitmap srcBitmap =getBitmapFromDrawable(normal);
			Bitmap bmp = Bitmap.createBitmap(srcBitmap.getWidth(),
			srcBitmap.getHeight(), Config.ARGB_8888);
			ColorMatrix cMatrix = new ColorMatrix();
			cMatrix.set( new float[] { (float) 0.94,0,0,0,0,
					0,(float) 0.94,0,0,0,
					0,0,(float) 0.94,0,0,
					0,0,0,1,0
	         });
			Paint paint = new Paint();
			paint.setColorFilter( new ColorMatrixColorFilter(cMatrix));
			Canvas canvas = new Canvas(bmp);
			canvas.drawBitmap(srcBitmap, 0, 0, paint);
			Drawable pressed = new BitmapDrawable(bmp);
			listDrawable.addState(
			new int[] { android.R.attr.state_pressed }, pressed);
			listDrawable.addState(
			new int[] { android.R.attr.state_selected }, pressed);
			listDrawable.addState(
			new int[] { android.R.attr.state_enabled }, normal);
			return listDrawable;
			}
			
		 private static Bitmap getBitmapFromDrawable(Drawable drawable) {
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
	
}
