package com.yey.kindergaten.widget;


import com.yey.kindergaten.R;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ContactPopwindow extends PopupWindow{

	public String clicktext="";   //有需要动态改变的文本
	public EditText editText;     //有需要从PopupWindow编辑的控件
	public TextView nametext;     //有需要从PopupWindow动态改变文本的按钮
	public ContactPopwindow(Context context,OnClickListener onClickListener,int layoutId,String type)
	{
		super(context);
		final View view=LayoutInflater.from(context).inflate(layoutId, null);
	    if(type.equals("postscript")){
			TextView postscriptclosebtn=(TextView) view.findViewById(R.id.contact_addfriend_fuyuanpop_closebt);
			TextView postscriptsendbtn=(TextView) view.findViewById(R.id.contact_addfriend_fuyuanpop_sendbt);
			editText=(EditText) view.findViewById(R.id.contact_addfriend_fuyuanpop_fyet);
			postscriptclosebtn.setOnClickListener(onClickListener);
			postscriptsendbtn.setOnClickListener(onClickListener);
		}else if(type.equals("ContactCenterPuac")){
			nametext=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv1);
			nametext.setText(clicktext);
			nametext.setOnClickListener(onClickListener);
			
			TextView textView2=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv2);
			textView2.setText("查看资料");
			textView2.setOnClickListener(onClickListener);
			
			TextView textView3=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv3);
			textView3.setText("取消订阅");
			textView3.setOnClickListener(onClickListener);
					
			TextView textView4=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv4);
			textView4.setVisibility(View.GONE);
			View v=view.findViewById(R.id.contact_main_centerpopwindowviewline3);
			v.setVisibility(View.GONE);
		}else{
			nametext=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv1);
			nametext.setText(clicktext);
			nametext.setOnClickListener(onClickListener);
			
			TextView textView2=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv2);
			textView2.setText("查看资料");
			textView2.setOnClickListener(onClickListener);
			
			TextView textView3=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv3);
		    textView3.setText("发消息");
			textView3.setOnClickListener(onClickListener);
			
			TextView textView4=(TextView) view.findViewById(R.id.contact_main_centerpopwindowtv4);
			textView4.setText("删除联系人");
			textView4.setVisibility(View.VISIBLE);
			textView4.setOnClickListener(onClickListener);
		}
		
		this.setContentView(view);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.FILL_PARENT);
		this.setAnimationStyle(R.style.PopwindowAnimation);
		ColorDrawable dw = new ColorDrawable(0x0b000000);
		this.setBackgroundDrawable(dw);
		this.setFocusable(true);
		view.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {							
				if(event.getAction()==MotionEvent.ACTION_UP){	
						dismiss();					
				}				
				return true;
			}
		});
	}	
	
	public void setClicktext(String clicktext) {
		this.clicktext = clicktext;
		nametext.setText(clicktext);
	}
	

	public String getEdittext() {
		if(editText!=null)
		{
			return editText.getText().toString();
		}
		return null;
	}
	

	
}
