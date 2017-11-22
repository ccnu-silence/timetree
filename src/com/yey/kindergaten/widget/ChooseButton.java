package com.yey.kindergaten.widget;

import com.yey.kindergaten.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChooseButton extends LinearLayout{
	private TextView tv_first,tv_second;
	private int click;

	public ChooseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.choosebutton, this);
		tv_first=(TextView) findViewById(R.id.choosebutton_tv_first);
		tv_second=(TextView) findViewById(R.id.choosebutton_tv_second);
		tv_first.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setTextclickBackground(tv_first);
				setTextunclickBackground(tv_second);
				click=1;
			}
		});
		tv_second.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTextclickBackground(tv_second	);
				setTextunclickBackground(tv_first);
				click=2;
			}
		});
		
	}
	public void setFirstText(String text){
		tv_first.setText(text);
	}
	public void setSecondText(String text){
		tv_second.setText(text);
	}
	public void setTextclickBackground(TextView tv){
		tv.setBackgroundColor(getContext().getResources().getColor(R.color.grey));	
	}
	public void setTextunclickBackground(TextView tv){
		tv.setBackgroundColor(getContext().getResources().getColor(R.color.white));	
	}
	

}
