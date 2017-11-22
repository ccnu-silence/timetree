package com.yey.kindergaten.activity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.CircleImageView;

public class MeCardActivity extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.mecard_miaoshutv)TextView miaoshutv;
	@ViewInject(R.id.mecard_nametv)TextView nametv;
	@ViewInject(R.id.mecard_accounttv)TextView accounttv;
	@ViewInject(R.id.mecard_ciciv)CircleImageView ciciv;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.me_card_bariv)ImageView imageView;
	@ViewInject(R.id.down_to_phone_tv)TextView down;
	Bitmap bitmap;
	AccountInfo accountInfo;
	Bitmap portrait=null;
	String state="";
	
	private String name="barcode";
	/** 头像图片大小 */
	private static final int PORTRAIT_SIZE = 55;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me_main_card);
		if(getIntent().getExtras()!=null){
			state=getIntent().getExtras().getString(AppConstants.STATE);
		}
		ViewUtils.inject(this);
		accountInfo=AppServer.getInstance().getAccountInfo();
		initView();

	}
	private void initView() {
		ImageLoader.getInstance().displayImage(accountInfo.getAvatar()+"", ciciv, ImageLoadOptions.getContactsFriendPicOptions());
		down.setOnClickListener(this);
//		down.setVisibility(View.GONE);
		miaoshutv.setText("扫一扫上面的二维码加我为好友");
		nametv.setText(accountInfo.getNickname());
		accounttv.setText(accountInfo.getAccount()+"");
		titletv.setText("二维码名片");
		left_btn.setVisibility(View.VISIBLE);
		try {	
			bitmap=Create2DCode("TIMES_TREE_QRCODE_0#"+accountInfo.getUid());						
			imageView.setImageBitmap(bitmap);
			portrait=imageLoader.loadImageSync(accountInfo.getAvatar(), ImageLoadOptions.getHeadOptions());
	    	if(portrait!=null){
	    		Matrix mMatrix = new Matrix();
			float width = portrait.getWidth()-8;
			float height = portrait.getHeight()-8;
			mMatrix.setScale(PORTRAIT_SIZE / width, PORTRAIT_SIZE / height);
			portrait= Bitmap.createBitmap(portrait, 0, 0, (int) width,
					(int) height, mMatrix, true);
			Bitmap bmp=BitmapFactory.decodeResource(this.getResources(), R.drawable.commont_white_button);
			Bitmap bmp2=BitmapUtil.zoomBitmap(bmp, 65, 65);
//			createQRCodeBitmapWithPortrait(bitmap, bmp2);
//			createQRCodeBitmapWithPortrait(bitmap, portrait);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	
	}
	
	@OnClick({(R.id.left_btn)})
	public void onclick(View v){
		switch (v.getId()) {
		 case R.id.left_btn:
			this.finish();
		 default:
			break;
		}
	}
	
	 public Bitmap Create2DCode(String str) throws WriterException {  
		    // 用于设置QR二维码参数  
		    Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();  
		    // 设置QR二维码的纠错级别——这里选择最高H级别  
		    qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);  
		    // 设置编码方式  
		    qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");   
		    //创建二维码
		    BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 300, 300);  
	        int width = matrix.getWidth();  
	        int height = matrix.getHeight();  
	
	        int[] pixels = new int[width * height];  
	        for (int y = 0; y < height; y++) {  
	            for (int x = 0; x < width; x++) {  
	                if(matrix.get(x, y)){  
	                    pixels[y * width + x] = 0xff000000;  
	                }  else {                              //无信息设置像素点为白色
						pixels[y * width + x] = 0xffffffff;
					} 	                  
	            }  
	        }  
	        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  	    
	        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
	        return bitmap;  
	    }  
	 
	 
	 /** 
	  * 在二维码上绘制头像 
	  */  
	 private void createQRCodeBitmapWithPortrait(Bitmap qr, Bitmap portrait) {  
	     // 头像图片的大小  
	     int portrait_W = portrait.getWidth();  
	     int portrait_H = portrait.getHeight();  
	   
	     // 设置头像要显示的位置，即居中显示  
	     int left = (300 - portrait_W) / 2;  
	     int top = (300 - portrait_H) / 2;  
	     int right = left + portrait_W;  
	     int bottom = top + portrait_H;  
	     Rect rect1 = new Rect(left, top, right, bottom);  
	   
	     // 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像  
	     Canvas canvas = new Canvas(qr);  
	   
	     // 设置我们要绘制的范围大小，也就是头像的大小范围  
	     Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);  
	     // 开始绘制  
	     canvas.drawBitmap(portrait, rect2, rect1, null);  
	 }
	 	    
		private void save(Bitmap bm) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				FileOutputStream fos = new FileOutputStream(new File(AppConfig.PATH,
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
			}
		}

		//在SD卡上创建一个文件夹
		    public void createSDCardDir(){
		     if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
		            // 创建一个文件夹对象，赋值为外部存储器的目录
		             String path=AppConfig.PATH;
		             File path1 = new File(path);
		            if (!path1.exists()) {
		             //若不存在，创建目录，可以在应用启动的时候创建
		             path1.mkdirs();
		           }
		            }
		     else{
		      setTitle("false");
		      return;
		    }
		   }
	 
	 public void onResume() {
			super.onResume();
			MobclickAgent.onResume(this);
		}
		public void onPause() {
			super.onPause();
			MobclickAgent.onPause(this);
		}
		@Override
		public void onClick(View arg0) {
	         switch (arg0.getId()) {
			case R.id.down_to_phone_tv:	
				String last_name=accountInfo.getRealname();
				if(last_name==null||last_name.equals("")){
					last_name="时光树";
				}
				BitmapUtil.savePhotoToSDCard(bitmap, AppConfig.PATH, name+"_"+last_name+".png");		 			    
				showToast("文件保存到"+AppConfig.PATH+"目录中");
				break;
			}		
		}
} 
