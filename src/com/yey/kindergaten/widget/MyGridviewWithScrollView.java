package com.yey.kindergaten.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridviewWithScrollView extends GridView{

	public MyGridviewWithScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGridviewWithScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MyGridviewWithScrollView(Context context) {
		super(context);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mEpandsec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mEpandsec);
	}
  
}
