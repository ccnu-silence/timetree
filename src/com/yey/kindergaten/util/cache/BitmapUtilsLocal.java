package com.yey.kindergaten.util.cache;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

public class BitmapUtilsLocal {
	
	public static Bitmap createScaleBitmap(Bitmap srcBitmap,int viewWidth,int viewHeight) {
		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();
		float scaleX = (float)viewWidth/srcWidth;
		float scaleY = (float)viewHeight/srcHeight;
		float scale = scaleX<scaleY?scaleY:scaleX;
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap bm = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		if(bm!=srcBitmap)srcBitmap.recycle();
		float cutX = (bm.getWidth()-viewWidth)/2f;
		float cutY = (bm.getHeight()-viewHeight)/2f;
		Bitmap bmFinal = Bitmap.createBitmap(bm,(int)cutX,(int)cutY, viewWidth,viewHeight);
		if(bmFinal!=bm)bm.recycle();
		return bmFinal;
	}
	
	public static Bitmap rotateBitmap(Bitmap _bitmap,int degree) {
		Matrix m = new Matrix();
		Bitmap mBit;
		m.postRotate(degree);
		mBit = Bitmap.createBitmap(_bitmap, 0, 0,
				_bitmap.getWidth(), _bitmap.getHeight(), m, true);
		return mBit;
	}
	
	
	//rotateBitmaps 
	public static Bitmap getRotateBitmap(Bitmap bm,String path) {
		if(bm==null)return null;
		ExifInterface sourceExif = null;
		try {
			sourceExif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("erroe", e.getMessage());
		}
		int degree = getDegree(sourceExif);
		Bitmap bmRotated = BitmapUtilsLocal.rotateBitmap(bm, degree);
		return bmRotated;
	}
	
	public static int getBitmapDegree(String path) {
		ExifInterface sourceExif = null;
		try {
			sourceExif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("erroe", e.getMessage());
		}
		return getDegree(sourceExif);
	}
	
	private static int getDegree(ExifInterface sourceExif) {
		if(sourceExif==null) return 0;
		switch (sourceExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
		case ExifInterface.ORIENTATION_NORMAL:
			return 0;
		case ExifInterface.ORIENTATION_ROTATE_90:
			return 90;
		case ExifInterface.ORIENTATION_ROTATE_180:
			return 180;
		case ExifInterface.ORIENTATION_ROTATE_270:
			return 270;
		default:return 0;
		}
	}
	
}
