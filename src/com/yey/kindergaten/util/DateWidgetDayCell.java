package com.yey.kindergaten.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;

import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ServiceScheduleActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * 日历控件单元格绘制类
 * @Description: 日历控件单元格绘制类
 * @FileName: DateWidgetDayCell.java 
 */
public class DateWidgetDayCell extends View {
	// 字体大小
	CancelFoucse cancelFoucse;

	public interface CancelFoucse
	{
		public void canceltoucel(float x1,float x2);
	}
	private static final int fTextSize = R.dimen.dateWidgetDayCell;	
	// 基本元素
	private OnItemClick itemClick = null;
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private String sDate = "";
    private Context context;
	public String getsDate() {
		return sDate;
	}

	// 当前日期
	private int iDateYear = 0;
	private int iDateMonth = 0;
	private int iDateDay = 0;
    
	private int bmpHeight;//图片的高度
	private int bmpWight;//图片的宽度
	
	// 布尔变量
	private boolean bSelected = false;
	private boolean bIsActiveMonth = false;
	private boolean bToday = false;
	private boolean bTouchedDown = false;
	private boolean bHoliday = false;
	private boolean hasRecord = false;
	private boolean isFirst=false;
	
	private Paint drawBmp;//画圆点所用的画笔
	private Paint drawCir;//画圆圈所用的画笔
	
	private int y;
	Paint paint=new Paint();
	Bitmap  bmp;
	InputStream is;
    private boolean hasWrite=false;
	public static int ANIM_ALPHA_DURATION = 100;
	Matrix mMatrix = new Matrix();
	public interface OnItemClick {
		public void OnClick(DateWidgetDayCell item );
	}

	// 构造函数
	@SuppressWarnings("ResourceType")
    public DateWidgetDayCell(Context context, int iWidth, int iHeight) {
		super(context);
		setFocusable(true);
		setLayoutParams(new LayoutParams(iWidth, iHeight));
		this.context=context;
		y=iHeight;
     	is=this.getResources().openRawResource(R.drawable.service_schedule_written);
    	 BitmapDrawable bmpDraw=new BitmapDrawable(is);				        	
    	 bmp=bmpDraw.getBitmap();
    	 
    	 bmpHeight= bmp.getHeight();
    	 bmpWight= bmp.getWidth(); 
    	 
    	 drawBmp=new Paint();
    	 drawBmp.setAntiAlias(true);
    	 
    	 drawCir=new Paint();
    	 drawBmp.setAntiAlias(true);
	}

	// 取变量值
	public Calendar getDate() {
		Calendar calDate = Calendar.getInstance();
		calDate.clear();
		calDate.set(Calendar.YEAR, iDateYear);
		calDate.set(Calendar.MONTH, iDateMonth);
		calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calDate;
	}

	// 设置变量值
	public void setData(int iYear, int iMonth, int iDay, Boolean bToday,
			Boolean bHoliday, int iActiveMonth, boolean hasRecord,boolean todaySelect) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;
		this.isFirst=todaySelect;  		
		this.sDate = Integer.toString(iDateDay);
		this.bIsActiveMonth = (iDateMonth == iActiveMonth);//判断日历中的当前月份
		this.bToday = bToday;
		this.bHoliday = bHoliday;
		this.hasRecord = hasRecord;
		
	}
	public void setSelect(boolean hasWrite){
		this.hasWrite=hasWrite;
		this.invalidate();
	}

	// 重载绘制方法
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		rect.set(0, 0, this.getWidth(), this.getHeight());
//		rect.inset(1, 1);

		final boolean bFocused = IsViewFocused();

		drawDayView(canvas, bFocused);
		drawDayNumber(canvas);
	}

	public boolean IsViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}

	// 绘制日历方格
	private void drawDayView(Canvas canvas, boolean bFocused) {
           pt.setColor(Color.WHITE);
           LinearGradient lGradBkg2 = null;
           //设置的是有没有选中和有没有按下
		if (bSelected || bFocused) {
			LinearGradient lGradBkg = null;			
			pt.setShader(null);
		}

	}
	// 绘制日历中的数字
	public void drawDayNumber(Canvas canvas) {
		// draw day number
		pt.setTypeface(null);
		pt.setAntiAlias(true);
		pt.setShader(null);
		pt.setFakeBoldText(false);
		pt.setTextSize(getResources().getDimension(fTextSize));
		pt.setStyle(Paint.Style.FILL_AND_STROKE);
		pt.setColor(ServiceScheduleActivity.isPresentMonth_FontColor);
		pt.setUnderlineText(false);			

//        int width = (int)rect.width();
//        int ss = width>>1;
//        int ll = width<<1;
        //8421编码，向右移一位，表示除2；
		final int iPosX = (int) rect.left + ((int) rect.width() >> 1)
				- ((int) pt.measureText(sDate) >> 1);
		final int iPosY = (int) (this.getHeight()
				- (this.getHeight() - getTextHeight()) / 2 - pt
				.getFontMetrics().bottom);			
		if (bIsActiveMonth){//当前月	
			pt.setColor(Color.rgb(136,136,136));
			canvas.drawText(sDate, iPosX, iPosY, pt);			
			   if (hasWrite&&!bToday){//写了日程而且不是今天				 		        	

				    DrawBitmap(canvas);	        		     
		        
				    pt.setColor(Color.rgb(136,136,136));		
			        canvas.drawText(sDate, iPosX, iPosY, pt);				       
		      }			   
				if(bToday&&!hasWrite&&!bSelected){//是今天而且没写日程
					 rect.set(0, this.getHeight()-8, this.getWidth(), this.getHeight()-2);
					 
				     DrawTodayCircle(canvas);	
					 
					 pt.setColor(Color.rgb(176,151,238));		
				     canvas.drawText(sDate, iPosX, iPosY, pt);				       		     
				}else if(bToday&&!hasWrite&&bSelected&&!isFirst){
					rect.set(0, this.getHeight()-8, this.getWidth(), this.getHeight()-2);
			
				   			    				    		 
				    							   
				    DrawCircle(canvas);		         
				   
			        pt.setColor(Color.rgb(176,151,238));		
				    canvas.drawText(sDate, iPosX, iPosY, pt);				   			         
				}else if(bToday&&!hasWrite&&bSelected&&isFirst){
					 rect.set(0, this.getHeight()-8, this.getWidth(), this.getHeight()-2);
		
				   
				     paint.setColor(Color.rgb(238,233,255));
				     paint.setStyle(Paint.Style.FILL_AND_STROKE);
				     DrawCircle(canvas);
					 
					 pt.setColor(Color.rgb(176,151,238));		
				     canvas.drawText(sDate, iPosX, iPosY, pt);	         
				}else if(!bToday&&bSelected&&!isFirst){
					 rect.set(0, this.getHeight()-8, this.getWidth(), this.getHeight()-2);
					 paint.setAntiAlias(true);
				     pt.setColor(Color.rgb(133,133,133));		
				     canvas.drawText(sDate, iPosX, iPosY, pt);
			    	}
				if (bSelected&&!bToday&&!hasWrite) {//选中的项不是今天，也没有写日程
		
					   DrawCircle(canvas);
			           pt.setColor(Color.rgb(133,133,133));		
				       canvas.drawText(sDate, iPosX, iPosY, pt);	
				    }else if(bSelected&&!bToday&&hasWrite){//选中的项不是今天但是写了日程
		
						   DrawCircle(canvas);			       
				           DrawBitmap(canvas);
				        
				           pt.setColor(Color.rgb(133,133,133));		
					       canvas.drawText(sDate, iPosX, iPosY, pt);
				    }					
				if(bSelected&&isFirst&&hasWrite&&bToday){//选中状态，而且是第一次画日历，写了日程，而且是今天
					rect.set(0, this.getHeight()-8, this.getWidth(), this.getHeight()-2);
		        	pt.setColor(Color.rgb(176,151,238));	        

				    DrawCircle(canvas);		    
				    DrawBitmap(canvas);
				    				    
		        	pt.setColor(Color.rgb(148,103,255));				
				    canvas.drawText(sDate, iPosX, iPosY, pt);
				}else if(bSelected&&!isFirst&&hasWrite&&bToday){//选中状态，不是第一次更新日历，而且写了日程，且是今天
					rect.set(0, this.getHeight()-8, this.getWidth(), this.getHeight()-2);
		        			   		        		        			          						
				    DrawTodayCircle(canvas);	        	
			        DrawBitmap(canvas);
	
			        pt.setColor(Color.rgb(176,151,238));
		        	pt.setColor(Color.rgb(148,103,255));				
				    canvas.drawText(sDate, iPosX, iPosY, pt);				  	         
				}else if(!bSelected&&!isFirst&&hasWrite&&bToday){//没有选中，且不是第一次画日历，写了日程，是今天
					rect.set(0, this.getHeight()-8, this.getWidth(), this.getHeight()-2);
		        	pt.setColor(Color.rgb(176,151,238));
		        			        	
		
		        	DrawTodayCircle(canvas);	       		    
				    DrawBitmap(canvas);
		        	
		        	pt.setColor(Color.rgb(148,103,255));				
				    canvas.drawText(sDate, iPosX, iPosY, pt);
				}				
		  }else{//不是当前月
			pt.setColor(Color.rgb(209,209,209));
			canvas.drawText(sDate, iPosX, iPosY, pt);
			if (bSelected&&!bToday&&!hasWrite) {//选中状态，不是今天，而且没写日程
					  
	        	  
		            DrawCircle(canvas);
		          
		            pt.setColor(Color.rgb(133,133,133));		
			        canvas.drawText(sDate, iPosX, iPosY, pt);	
			  }
			if(bSelected&&hasWrite){//选中状态而且写了日程
		    
		        DrawCircle(canvas);
	        
			    DrawBitmap(canvas);
	    		
	        	pt.setColor(Color.rgb(133,133,133));
				canvas.drawText(sDate, iPosX, iPosY, pt);
			}else if(!bSelected&&hasWrite){//写了日程，但没有选中
				  DrawBitmap(canvas);
			}		
		}
		pt.setUnderlineText(false);
		try {
			is.close();
		} catch (IOException e) {					
			e.printStackTrace();
		}
	}
	
	/**
	 * 画圆点
	 * @param canvas
	 */
	private void DrawBitmap(Canvas canvas){		
		canvas.drawBitmap(bmp, this.getWidth()/2-(bmpWight/2), this.getHeight()-(bmpHeight-1), drawBmp);
	}
	/**
	 * 画实心圆，而且不是今天
	 * @param canvas
	 */
	private void DrawCircle(Canvas canvas){
		 drawCir.setStyle(Paint.Style.FILL_AND_STROKE);
		 drawCir.setColor(Color.rgb(229,229,229));
	     canvas.drawCircle(this.getWidth()/2, (float) (this.getHeight()/2), y/2-4, drawCir);
	}
	
	/**
	 * 画实心圆，而且是今天
	 * @param canvas
	 */
	private void DrawTodayCircle(Canvas canvas){	  
		drawCir.setColor(Color.rgb(238,233,255));
		drawCir.setStyle(Paint.Style.FILL_AND_STROKE);  
		canvas.drawCircle(this.getWidth()/2, (float) (this.getHeight()/2), y/2-4, drawCir);
	}
	
	public boolean getIsMonth(){
		return bIsActiveMonth;
	}	
	// 得到字体高度
	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}
	

	// 根据条件返回不同颜色值
	public static int getColorBkg(boolean bHoliday, boolean bToday) {
		if (bToday)
			return ServiceScheduleActivity.isToday_BgColor;
		// if (bHoliday) //如需周末有特殊背景色，可去掉注释
		// return Calendar_TestActivity.isHoliday_BgColor;
		return ServiceScheduleActivity.Calendar_DayBgColor;
	}
	// 设置是否被选中
	@Override
	public void setSelected(boolean bEnable) {
		if (this.bSelected != bEnable) {
			this.bSelected = bEnable;
			this.invalidate();
		}
	}

	public void setItemClick(OnItemClick itemClick) {
		this.itemClick = itemClick;
	}

	public void doItemClick() {
		if (itemClick != null)
			itemClick.OnClick(this);
	}

	// 点击事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {	
		boolean bHandled = false;
		if(event.getAction()==MotionEvent.ACTION_MOVE){
//			cancelFoucse.canceltoucel(1);
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			cancelFoucse.canceltoucel(event.getX(),0);
			bHandled = true;
			bTouchedDown = true;
			invalidate();
			startAlphaAnimIn(DateWidgetDayCell.this);
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			cancelFoucse.canceltoucel(0,event.getX());
			bHandled = true;
			bTouchedDown = false;
			invalidate();
			doItemClick();
		}
		return bHandled;
	}

	// 点击事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			doItemClick();
		}
		return bResult;
	}

	// 不透明度渐变
	public static void startAlphaAnimIn(View view) {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
//		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
	}

	public void CreateReminder(Canvas canvas, int Color) {
		pt.setStyle(Paint.Style.FILL_AND_STROKE);
		pt.setColor(Color);
		Path path = new Path();
		path.moveTo(rect.right - rect.width() / 4, rect.top);
		path.lineTo(rect.right, rect.top);
		path.lineTo(rect.right, rect.top + rect.width() / 4);
		path.lineTo(rect.right - rect.width() / 4, rect.top);
		path.close();
		canvas.drawPath(path, pt);
	}
	public CancelFoucse getCancelFoucse() {
		return cancelFoucse;
	}

	public void setCancelFoucse(CancelFoucse cancelFoucse) {
		this.cancelFoucse = cancelFoucse;
	}
}