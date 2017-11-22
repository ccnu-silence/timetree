package com.yey.kindergaten.widget.popubmenu;

import java.util.List;

import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupMenuAdapter extends BaseAdapter{
	   public static int TYPE_BG_BLACK = 1;
	  public static int TYPE_BG_WHITE = 0;
	  private Context context;
	  private LayoutInflater inflater;
	  private List list;
	  private int typeBg = 0;
	  
	  static class ViewHolder
	  {
	    public ImageView icon;
	    public TextView title;
	    
	   
	  }
	  
	  public PopupMenuAdapter(Context context, List paramList, int paramInt)
	  {
	    this.list = paramList;
	    this.inflater =  LayoutInflater.from(context);
	    this.typeBg = paramInt;
	  }
	  
	  
	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View converView, ViewGroup viewgroup) {
		ViewHolder viewHolder = null;
		View localView;
	    PopupMenuItem localPopupMenuItem = (PopupMenuItem)this.list.get(position);
	    if (converView == null) {
	     /* if (this.typeBg == TYPE_BG_BLACK)
	      {*/
	    	viewHolder = new ViewHolder();
	    	converView = this.inflater.inflate(R.layout.popup_menu_list_item, null);
	    	 converView.setTag(viewHolder);
	    	viewHolder.icon = (ImageView)converView.findViewById(R.id.popup_menu_icon);
	        viewHolder.title = (TextView)converView.findViewById(R.id.popup_menu_title);
	        viewHolder.icon.setVisibility(View.GONE);
	     // }
	    }else{
	    	viewHolder=(ViewHolder) converView.getTag();
	    }
	   
	    viewHolder.title.setText(localPopupMenuItem.title);
	    viewHolder.icon.setVisibility(View.GONE);
	    
		return converView;
	}

}
