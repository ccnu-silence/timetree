package com.yey.kindergaten.widget;

import com.yey.kindergaten.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class SlipButton extends View implements OnTouchListener {
	private boolean NowChoose = true;// 记录当前按钮是否打开,true为打开,flase为关闭

	private float DownX=0,NowX=0;// 按下时的x,当前的x

	private float jiliH;

	private boolean isChgLsnOn = false;

	private boolean isSlip = false;
	
	private OnChangedListener ChgLsn;

	private Bitmap bg_on, bg_off, slip_btn;
	
	Rect spdst = new Rect();// 图片 >>原矩形     
    Rect bgdst = new Rect();// 屏幕 >>目标矩形     
	

	public SlipButton(Context context) {
		super(context);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {// 初始化

		bg_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.slip_grey);
		bg_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.slip_green);
		slip_btn = BitmapFactory.decodeResource(getResources(),
				R.drawable.slip_btn);
		
		
		
	    
		   spdst.left = 0;     
		   spdst.top = 0;        
		   spdst.bottom = 0 + slip_btn.getHeight()-2;     
	    
	       bgdst.left = 0;     
	       bgdst.top = 0;     
	       bgdst.right = 0 +bg_off.getWidth();      
	       bgdst.bottom = 0 +bg_off.getHeight();      
		
		  jiliH=0;
		  setOnTouchListener(this);// 设置监听器,也可以直接复写OnTouchEvent
	}

	@Override
	protected void onDraw(Canvas canvas) {// 绘图函数
		super.onDraw(canvas);	
		Paint paint = new Paint();
		float x;
		if (NowX < (bg_on.getWidth() / 2))// 滑动到前半段与后半段的背景不同,在此做判断
		{
			x = NowX - slip_btn.getWidth() / 2;
			//canvas.drawBitmap(bg_off, 0, jiliH, paint);
			//canvas.drawBitmap(bg_off, bg_off.getHeight(), bg_off.getWidth(), paint);
			 canvas.drawBitmap(bg_off, bgdst, bgdst, paint);   
		}else {
			x = bg_on.getWidth() - slip_btn.getWidth() / 2;
		//	canvas.drawBitmap(bg_on, 0, jiliH, paint);
		//	canvas.drawBitmap(bg_on, bg_on.getHeight(), bg_on.getWidth(), paint);
			canvas.drawBitmap(bg_on, bgdst, bgdst, paint);   
		}
		
		if (NowChoose)// 根据现在的开关状态设置画游标的位置
		{
			x = bg_on.getWidth() - slip_btn.getWidth() / 2;
		//	canvas.drawBitmap(bg_on, 0, jiliH, paint);
		//	canvas.drawBitmap(bg_on, bg_on.getHeight(), bg_on.getWidth(), paint);
			canvas.drawBitmap(bg_on, bgdst, bgdst, paint);   
		} else{
			x = -1;
		 //canvas.drawBitmap(bg_off, 0, jiliH, paint);
		//	canvas.drawBitmap(bg_off, bg_off.getHeight(), bg_off.getWidth(), paint);
			canvas.drawBitmap(bg_off, bgdst, bgdst, paint);   
		}

		if (x < 0)// 对游标位置进行异常判断...
			x = 0-1;
		else if (x > bg_on.getWidth() - slip_btn.getWidth())
			x = bg_on.getWidth() - slip_btn.getWidth()+1;
		     spdst.right = (int) (x + slip_btn.getWidth());     
		canvas.drawBitmap(slip_btn, x, (bg_on.getHeight()-slip_btn.getHeight())/2+1, paint);// 画出游标.
	//	canvas.drawBitmap(slip_btn, slip_btn.getHeight(), slip_btn.getWidth(), paint);
	//	canvas.drawBitmap(slip_btn, bgdst, spdst, paint);   
	//	canvas.save();
	}

	public boolean onTouch(View v, MotionEvent event) {
		if(!isSlip){
			return true;
		}
		switch (event.getAction())
		{
		case MotionEvent.ACTION_MOVE:// 滑动
			NowX = event.getX();
			break;

		case MotionEvent.ACTION_DOWN:// 按下

			if (event.getX() > bg_on.getWidth()
					|| event.getY() > bg_on.getHeight())
				return false;
			DownX = event.getX();
			NowX = DownX;
			break;

		case MotionEvent.ACTION_CANCEL: // 移到控件外部

			boolean choose = NowChoose;
			if (NowX >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			} else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}
			if (isChgLsnOn && (choose != NowChoose)) // 如果设置了监听器,就调用其方法..
				ChgLsn.OnChanged(NowChoose);
			break;
		case MotionEvent.ACTION_UP:// 松开

			boolean LastChoose = NowChoose;

			if (event.getX() >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			}

			else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}

			if (isChgLsnOn && (LastChoose != NowChoose)) // 如果设置了监听器,就调用其方法..
				ChgLsn.OnChanged(NowChoose);
			break;
		default:
		}
		invalidate();// 重画控件
		return true;
	}

	public void SetOnChangedListener(OnChangedListener l) {// 设置监听器,当状态修改的时候
		isChgLsnOn = true;
		ChgLsn = l;
	}

	public interface OnChangedListener {
		abstract void OnChanged(boolean CheckState);
	}

	public void setCheck(boolean isChecked) {
		NowChoose = isChecked;
	}
	public void setCheck(int isChecked) {
		if(isChecked==0)
		{
			NowChoose = false;
		}else{
			NowChoose = true;
		}
		
	}
	
	public void setSlip(boolean isSlip) {
		this.isSlip = isSlip;
	}
}


