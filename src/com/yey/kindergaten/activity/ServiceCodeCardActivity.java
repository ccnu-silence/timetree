package com.yey.kindergaten.activity;

import java.util.Hashtable;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
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
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.CircleImageView;


public class ServiceCodeCardActivity extends BaseActivity{
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.mecard_miaoshutv)TextView miaoshutv;
	@ViewInject(R.id.mecard_nametv)TextView nametv;
	@ViewInject(R.id.mecard_accounttv)TextView accounttv;
	@ViewInject(R.id.mecard_ciciv)CircleImageView ciciv;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.me_card_bariv)ImageView imageView;
	Bitmap bitmap;
	String codestring;
	String groupname="";
	String groupnum="";
	
	private static final int PORTRAIT_SIZE = 55;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me_main_card);
		ViewUtils.inject(this);
		if(getIntent()!=null){
			codestring=getIntent().getExtras().getString(AppConstants.CODESTRING);
			groupname=getIntent().getExtras().getString(AppConstants.GROUPNAME);
			groupnum=getIntent().getExtras().getString(AppConstants.GROUPNUM);
		}
		initView();
	}
	private void initView() {
		titletv.setText("群二维码名片");
		nametv.setText(groupname);
		accounttv.setText(groupnum);
		miaoshutv.setText("扫描上面的二维码加入群");
		left_btn.setVisibility(View.VISIBLE);
		try {	
			bitmap=Create2DCode(codestring);						
			imageView.setImageBitmap(bitmap);		
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
	                }  
	                  
	            }  
	        }  
	        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  	    
	        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
	        return bitmap;  
	    }  
	 
	 
	
	 	          
	 public void onResume() {
			super.onResume();
			MobclickAgent.onResume(this);
		}
		public void onPause() {
			super.onPause();
			MobclickAgent.onPause(this);
		}
} 
