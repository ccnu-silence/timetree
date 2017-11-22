package com.yey.kindergaten.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapUtil
{
     /**
      * 图片大于300k则压缩。
     */
     private static final int imageMaxSize = 300 * 1024;
     
     /**
      * 
      * <图片按比例大小压缩方法（根据路径获取图片并压缩）>
      * <功能详细描述>
      * @param srcPath
      * @param isNeedTime
      * @return
      * @see [类、类#方法、类#成员]
      */
     public static Bitmap getImageByPath(String srcPath, boolean isNeedTime)
     {
         Bitmap bitmap = null;
         String time = null;
         try
         {
             BitmapFactory.Options newOpts = new BitmapFactory.Options();
             // 开始读入图片，此时把options.inJustDecodeBounds 设回true了  
             newOpts.inJustDecodeBounds = true;
             bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
             newOpts.inJustDecodeBounds = false;
             int width = newOpts.outWidth;
             int height = newOpts.outHeight;
             // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
             float minHeight = 800f;
             // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
             int be = 1;// be=1表示不缩放  
             if (width > height && width > minHeight)
             {
                 // 如果宽度大的话根据宽度固定大小缩放  
                 be = (int)(newOpts.outWidth / minHeight);
             }
             else if (width < height && height > minHeight)
             {
                 // 如果高度高的话根据宽度固定大小缩放  
                 be = (int)(newOpts.outHeight / minHeight);
             }
             if (be <= 0)
                 be = 1;
             newOpts.inSampleSize = be;// 设置缩放比例  
             bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
             //获取图片的生成时间
            ExifInterface exif = new ExifInterface(srcPath);
             time = exif.getAttribute(ExifInterface.TAG_DATETIME);
             if (time == null || "".equals(time))
             {
                 //取不到照片的创建时间，则取照片的最后修改时间
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                 File file = new File(srcPath);
                 Date date = new Date(file.lastModified());
                 time = GeneralUtils.splitToSecond(dateFormat.format(date));
             }
             else
             {
                 time = GeneralUtils.splitToPhotoTime(time);
             }
         }
         catch (IOException e)
         {
             e.printStackTrace();
         }
         return compressImage(bitmap, time, isNeedTime);// 压缩好比例大小后再进行质量压缩  
     }
     
     /**
      * 
      * <图片按比例大小压缩方法（根据Bitmap图片压缩）  >
      * <功能详细描述>
      * @param data
      * @param time
      * @return
      * @see [类、类#方法、类#成员]
      */
     public static Bitmap getImageByStream(byte[] data, String time)
     {
         Bitmap bitmap = null;
         ByteArrayInputStream isBm = new ByteArrayInputStream(data);
         BitmapFactory.Options newOpts = new BitmapFactory.Options();
         // 开始读入图片，此时把options.inJustDecodeBounds 设回true了  
         newOpts.inJustDecodeBounds = true;
         bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
         newOpts.inJustDecodeBounds = false;
         int width = newOpts.outWidth;
         int height = newOpts.outHeight;
         // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
         float minHeight = 800f;// 这里设置高度为800f  
         // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
         int be = 1;// be=1表示不缩放  
         if (width > height && width > minHeight)
         {// 如果宽度大的话根据宽度固定大小缩放  
             be = (int)(newOpts.outWidth / minHeight);
         }
         else if (width < height && height > minHeight)
         {// 如果高度高的话根据宽度固定大小缩放  
             be = (int)(newOpts.outHeight / minHeight);
         }
         if (be <= 0)
             be = 1;
         newOpts.inSampleSize = be;// 设置缩放比例  
         isBm = new ByteArrayInputStream(data);
         //获取图片的生成时间
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
         return compressImage(bitmap, time, true);// 压缩好比例大小后再进行质量压缩  
     }
     
     /**
      * 
      * <质量压缩方法,并且添加时间水印>
      * <功能详细描述>
      * @param image
      * @param date
      * @param isNeedTime 是否需要添加水印
     * @return
      * @see [类、类#方法、类#成员]
      */
     public static Bitmap compressImage(Bitmap image, String date, boolean isNeedTime)
     {
         if (image != null)
         {
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Bitmap bitMap = image;
             if (isNeedTime)
             {
                 bitMap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
                 String time = "拍摄于:" + date;
                 Canvas canvasTemp = new Canvas(bitMap);
                 canvasTemp.drawColor(Color.WHITE);
                 Paint p = new Paint();
                 String familyName = "宋体";// 设置水印字体
                Typeface font = Typeface.create(familyName, Typeface.BOLD);
                 p.setColor(Color.RED);// 设置水印字体颜色
                p.setTypeface(font);
                 p.setTextSize(30);
                 canvasTemp.drawBitmap(image, 0, 0, p);
                 canvasTemp.drawText(time, image.getWidth() / 2 - p.measureText(time) / 2, image.getHeight() * 9 / 10, p);
                 canvasTemp.save(Canvas.ALL_SAVE_FLAG);
                 canvasTemp.restore();
                 image.recycle();
                 // 添加水印文字
            }
             bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
             int options = 100;
             while (baos.toByteArray().length > imageMaxSize)
             { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩  
                 baos.reset();// 重置baos即清空baos  
                 bitMap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中  
                 options -= 5;// 每次都减少5  
             }
            // bitMap.recycle();
             return bitMap;
         }
         else
         {
             return null;
         }
     }
     
     /**
      * 
      * <生成缩略图>
      * <功能详细描述>
      * @param nowPhoto
      * @param zoomToPitmap
      * @return
      * @see [类、类#方法、类#成员]
      */
     public static Bitmap zoomPhoto(Bitmap nowPhoto, Bitmap zoomToPitmap)
     {
         Bitmap newbmp = ThumbnailUtils.extractThumbnail(nowPhoto, zoomToPitmap.getWidth(), zoomToPitmap.getHeight());
         nowPhoto.recycle();
         return newbmp;
     }
     
     /**
 	 * 获取保存图片的目录
 	 * 
 	 * @return
 	 */
 	public static File getAlbumDir() {
 		File dir = new File("/mnt/sdcard/yey/teacher/thumpic/");
 		if (!dir.exists()) {
 			dir.mkdirs();
 		}
 		return dir;
 	}
 	
 	 /**
 	 * 获取班级相册保存图片的目录
 	 * 
 	 * @return
 	 */
 	public static File getClassAlbumDir() {
 		File dir = new File(Environment
				.getExternalStorageDirectory()+"/yey/teacher/classpic/");
 		if (!dir.exists()) {
 			dir.mkdirs();
 		}
 		return dir;
 	}
 	
	public static ProgressDialog showProgressDialog(Activity activity){
		ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("加载中...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
       return dialog;
	}
	
	/**
	 * 处理照片
	 * 
	 * 
	 * 
	 * 
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
	/*
	 * 保存bitimap到指定文件夹
	 * 
	 * 
	 */
	
	public static void savePhotoToSDCard(Bitmap photoBitmap,String path,String photoName){
		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (!dir.exists()){
				dir.mkdirs();
			}
			
			File photoFile = new File(path , photoName);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
						fileOutputStream.flush();
						fileOutputStream.close();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally{
				
			}
		} 
	}
	
	public static boolean checkSDCardAvailable(){
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	
	/**
	 * 将图片转化为圆形头像 
	 * 
	 * @Title: toRoundBitmap
	 * @throws
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {   

		Bitmap bitmap = Bitmap   
				.createBitmap(   
						drawable.getIntrinsicWidth(),   
						drawable.getIntrinsicHeight(),   
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888   
								: Bitmap.Config.RGB_565);   
		Canvas canvas = new Canvas(bitmap);   
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());   
		drawable.draw(canvas);   
		return bitmap;   
	} 
	
	public static void saveMyBitmap(Bitmap bm,String bitName)  {
		try {
			File f = new File(FileUtils.getSDRoot() + "yey/" + "shareapp.png");
			f.createNewFile();
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 以数组形式保存图片
	 * @param bitmap
	 * @param bitName
	 */
	public static void saveBitmapByArray(Bitmap bitmap,String bitName)  {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
            String md5name = StringUtils.getDigestStr(bitName);
            if(!new File(FileUtils.getSDRoot() + "yey/imagecache").exists()){
            	new File(FileUtils.getSDRoot() + "yey/imagecache").mkdirs();
            }
			File imageFile = new File(FileUtils.getSDRoot() + "yey/imagecache/"+md5name);
			imageFile.createNewFile();
			FileOutputStream fstream = new FileOutputStream(imageFile);
			BufferedOutputStream bStream = new BufferedOutputStream(fstream);
			bStream.write(byteArray);
			if (bStream != null) {
				bStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static Bitmap readBitMap(String path){ 
        BitmapFactory.Options opt = new BitmapFactory.Options(); 
       opt.inPreferredConfig = Bitmap.Config.RGB_565; 
       opt.inPurgeable = true; 
       opt.inInputShareable = true; 
       opt.inSampleSize = computeSampleSize(opt, -1, 720*1280);  //计算出图片使用的inSampleSize
       opt.inJustDecodeBounds = false; 
        
       return BitmapFactory.decodeFile(path,opt); 
       } 


public static int computeSampleSize(BitmapFactory.Options options,
	             int minSideLength, int maxNumOfPixels) {
	         int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
	
	         int roundedSize;
	         if (initialSize <= 8 ) {
	             roundedSize = 1;
	             while (roundedSize < initialSize) {
	                 roundedSize <<= 1;
	             }
	         } else {
	             roundedSize = (initialSize + 7) / 8 * 8;
	         }
	
	        return roundedSize;
	     }
	 
	     private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	         double w = options.outWidth;
	         double h = options.outHeight;
	 
      int lowerBound = (maxNumOfPixels == -1) ? 1 :
	                 (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	         int upperBound = (minSideLength == -1) ? 128 :
	                (int) Math.min(Math.floor(w / minSideLength),
	                 Math.floor(h / minSideLength));
	
	        if (upperBound < lowerBound) {
	             // return the larger one when there is no overlapping zone.
	             return lowerBound;
	         }
	
	         if ((maxNumOfPixels == -1) &&
	                 (minSideLength == -1)) {
	             return 1;
	         } else if (minSideLength == -1) {
	             return lowerBound;
	         } else {
	             return upperBound;
	         }
	    }
	     
	     /**
	 	 * Resize a bitmap
	 	 * 
	 	 * @param input
	 	 * @param destWidth
	 	 * @param destHeight
	 	 * @return
	 	 * @throws OutOfMemoryError
	 	 */
	 	public static Bitmap resizeBitmap( final Bitmap input, int destWidth, int destHeight ) throws OutOfMemoryError {
	 		return resizeBitmap( input, destWidth, destHeight, 0 );
	 	}

	 	/**
	 	 * Resize a bitmap object to fit the passed width and height
	 	 * 
	 	 * @param input
	 	 *           The bitmap to be resized
	 	 * @param destWidth
	 	 *           Desired maximum width of the result bitmap
	 	 * @param destHeight
	 	 *           Desired maximum height of the result bitmap
	 	 * @return A new resized bitmap
	 	 * @throws OutOfMemoryError
	 	 *            if the operation exceeds the available vm memory
	 	 */
	 	public static Bitmap resizeBitmap( final Bitmap input, int destWidth, int destHeight, int rotation ) throws OutOfMemoryError {

	 		int dstWidth = destWidth;
	 		int dstHeight = destHeight;
	 		final int srcWidth = input.getWidth();
	 		final int srcHeight = input.getHeight();

	 		if ( rotation == 90 || rotation == 270 ) {
	 			dstWidth = destHeight;
	 			dstHeight = destWidth;
	 		}

	 		boolean needsResize = false;
	 		float p;
	 		if ( ( srcWidth > dstWidth ) || ( srcHeight > dstHeight ) ) {
	 			needsResize = true;
	 			if ( ( srcWidth > srcHeight ) && ( srcWidth > dstWidth ) ) {
	 				p = (float) dstWidth / (float) srcWidth;
	 				dstHeight = (int) ( srcHeight * p );
	 			} else {
	 				p = (float) dstHeight / (float) srcHeight;
	 				dstWidth = (int) ( srcWidth * p );
	 			}
	 		} else {
	 			dstWidth = srcWidth;
	 			dstHeight = srcHeight;
	 		}

	 		if ( needsResize || rotation != 0 ) {
	 			SoftReference output;

	 			if ( rotation == 0 ) {
	 				output = new SoftReference(Bitmap.createScaledBitmap( input, dstWidth, dstHeight, true ));
	 			} else {
	 				Matrix matrix = new Matrix();
	 				matrix.postScale( (float) dstWidth / srcWidth, (float) dstHeight / srcHeight );
	 				matrix.postRotate( rotation );
	 				output =new SoftReference(Bitmap.createBitmap( input, 0, 0, srcWidth, srcHeight, matrix, true )) ;
	 			}
	 			return (Bitmap) output.get();
	 		} else
	 			return input;
	 	}
	 	
	 	 /*
		    * 旋转图片 
		    * @param angle 
		    * @param bitmap 
		    * @return Bitmap 
		    */ 
		   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
		       //旋转图片 动作   
		       Matrix matrix = new Matrix();
		       matrix.postRotate(angle);  
		       System.out.println("angle2=" + angle);  
		       // 创建新的图片

//               BitmapFactory.Options  opt  =  new  BitmapFactory.Options();
//               opt.inPreferredConfig  =  Bitmap .Config.RGB_565;
//               opt.inPurgeable  =  true ;
//               opt.inInputShareable  =  true ;
//               //获取资源图片
//               InputStream is  =  getResources().openRawResource(resId);
//               BitmapFactory.decodeStream(is,null,opt);


               Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
		               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
		       return resizedBitmap;  
		   }

    public static void createSDCardDir(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir =Environment.getExternalStorageDirectory();
            //得到一个路径，内容是sdcard的文件夹路径和名字
            String path=sdcardDir.getPath()+"/yey/kindergaten/uploadimg/";
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }
        }
        else{
            return;
        }
    }

    public static void save(String path, String name, String PATH) {
        try {
            Bitmap bm = BitmapUtil.getImageByPath(path, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            File file = new File(PATH);
            if(!file.exists()){
                file.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(new File(PATH,
                    name));
            int options = 100;

            while (baos.toByteArray().length / 1024 > 80 && options != 10) {

                baos.reset();

                bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 30;
            }
            fos.write(baos.toByteArray());
            fos.close();
            baos.close();
            bm=null;
        } catch (Exception e) {
            System.out.println("save Excephiton" +e.getMessage());
        }
    }
}   
