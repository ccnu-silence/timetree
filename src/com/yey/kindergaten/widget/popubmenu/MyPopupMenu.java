package com.yey.kindergaten.widget.popubmenu;

import java.util.List;

import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter.onInternalClickListener;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MyPopupMenu {
	  public static int TYPE_BG_BLACK = 1;
	  public static int TYPE_BG_WHITE = 0;
	  private PopupMenuAdapter adapter;
	  private Context context;
	  private List items;
	  private OnItemClickListener menuItemClick;
	  public PopupWindow popWindow;
	  private View rootView;
	  private int typeBg = 0;
	  
	  
	  public MyPopupMenu(Context paramContext, List paramList, OnItemClickListener paramMenuItemClickListener)
	  {
	    this.context = paramContext;
	    this.items = paramList;
	    this.menuItemClick = paramMenuItemClickListener;
	    initListView();
	    initPopupWindow();
	  }
	  
	  public MyPopupMenu(Context paramContext, List paramList, OnItemClickListener paramMenuItemClickListener, int paramInt)
	  {
	    this.context = paramContext;
	    this.items = paramList;
	    this.menuItemClick = paramMenuItemClickListener;
	    this.typeBg = paramInt;
	    initListView();
	    initPopupWindow();
	  }
	  
	  
	  private void initListView()
	  {
	      
		  this.rootView = LayoutInflater.from(this.context).inflate(R.layout.pop_menu_layout, null);
		  ListView localListView = (ListView)this.rootView.findViewById(R.id.popmenu_listview);
	     localListView.setOnItemClickListener(menuItemClick);
	      this.adapter = new PopupMenuAdapter(this.context, this.items, this.typeBg);
	      localListView.setAdapter(this.adapter);
	      this.rootView.setFocusableInTouchMode(true);
	      //this.rootView.setOnKeyListener(new MyPopupMenu.2(this));
	      return;
	  }
	  
	  private void initPopupWindow()
	  {
	    if (this.popWindow == null)
	    {
	    	
	      this.popWindow = new PopupWindow(this.context);
	      this.popWindow.setContentView(this.rootView);
	      this.rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	 	  this.rootView.measure(0, 0);
	 	  this.popWindow.setWidth(this.rootView.getMeasuredWidth() + 15);
	      //this.popWindow.setWidth(LayoutParams.WRAP_CONTENT);
	      this.popWindow.setHeight(LayoutParams.WRAP_CONTENT);
	      this.popWindow.setTouchable(true);
	      this.popWindow.setBackgroundDrawable(new BitmapDrawable());
	     // this.popWindow.setOnDismissListener(new MyPopupMenu.3(this));
	      
	    }
	  }
	  
	  public void setPopupWindowSize()
	  {
	    this.rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	    this.rootView.measure(0, 0);
	    this.popWindow.setWidth(this.rootView.getMeasuredWidth() + 15);
	    this.popWindow.update();
	  }
	  
	  public void dissmiss()
	  {
	    if (isShowing()) {
	      this.popWindow.dismiss();
	    }
	  }
	  
	  public boolean isShowing()
	  {
	    if ((this.popWindow != null) && (this.popWindow.isShowing())) {}
	    for (boolean bool = true;; bool = false) {
	      return bool;
	    }
	  }
	  
	  public void show(View paramView)
	  {
	    if (this.popWindow == null) {}
	   
	      if (this.popWindow.isShowing())
	      {
	        this.popWindow.dismiss();
	      }
	      else
	      {
	        this.popWindow.setFocusable(true);	
	        
	        //this.popWindow.showAtLocation(paramView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,paramView.getHeight());
	        this.popWindow.showAsDropDown(paramView, 10,paramView.getHeight()/2-50);
	      }
	    
	  }
	  
	  
	  public void notifyData()
	  {
	    this.adapter.notifyDataSetChanged();
	  }
	  
	  public static abstract interface MenuItemClickListener 
	  {
	    public abstract void onItemClick(PopupMenuItem paramPopupMenuItem);
	  }

	  
	  public void SetOnMenuItemClick(OnItemClickListener listener){
			this.menuItemClick = listener;
		}
	  
	  public List getMenuItem(){
		  return items;
	  }
}
