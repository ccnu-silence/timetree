package com.yey.kindergaten.widget;

import java.util.ArrayList;
import java.util.List;

import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectTermPopupWindow extends PopupWindow{
	
	 private View mMenuView;
	 private ListView listview;
	 private SpinnerAdapter adapter;
	 private List<String>list=new ArrayList<String>();
	 private Context context;
     private List<Term>termList=new ArrayList<Term>();
     private LinearLayout loading;
	 public SelectTermPopupWindow(Activity context,final Handler handler,List<Term>termList) {
		 LayoutInflater inflater = (LayoutInflater) context
	             .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        mMenuView = inflater.inflate(R.layout.infalter_select_term_service, null);
	        this.setContentView(mMenuView);  
	        this.context=context;
	        this.termList=termList;
            //设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(LayoutParams.FILL_PARENT);
            //设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(LayoutParams.WRAP_CONTENT);
            setOutsideTouchable(true);
            //设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(true);
            //设置SelectPicPopupWindow弹出窗体动画效果
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            //设置SelectPicPopupWindow弹出窗体的背景
            this.setBackgroundDrawable(dw);
            this.setAnimationStyle(R.style.showtermdialog);
	        listview=(ListView)mMenuView.findViewById(R.id.id_service_schedule_remind_lv);
	        listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterview, View view,
						int position, long l) {					
					if(SelectTermPopupWindow.this.isShowing()){
						 dismiss();	
					}     
				    Message msg=new Message();
		            msg.what=3;
		            msg.obj=position;	           	            
		            handler.sendMessage(msg);
		            if(!SelectTermPopupWindow.this.isShowing()){
						 dismiss();	
					} 	
				}	        	  
			});
	        
	        SpinnerAdapter adapter=new SpinnerAdapter();
	        listview.setAdapter(adapter);
	}
	 

	     class SpinnerAdapter extends BaseAdapter{
	     	
	     	private int position;
	       	@Override
	 		public int getCount() {
	 		
	 			return termList.size();
	 		}

	 		@Override
	 		public Object getItem(int position) {
	 			
	 			return position;
	 		}
	 		
	 		public int getposition(){
	 		
	 			return position;
	 		}

	 		@Override
	 		public long getItemId(int position) {
	 			
	 			return 0;
	 		}

	 	
	 		
	 		@Override
	 		public View getView(int position, View view, ViewGroup arg2) {
	 			
	 			View v=LayoutInflater.from(context).inflate(R.layout.inflater_select_term_popu, null);
	             TextView tv=(TextView) v.findViewById(R.id.id_inflater_sendmsg_showname_tv);			
	             tv.setText(termList.get(position).getCname());                                 
	              return v;
	 		
	     	
	 		}    
	     }

}
