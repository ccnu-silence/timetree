package com.yey.kindergaten.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.yey.kindergaten.R;
import com.yey.kindergaten.util.ParamsUtil;

import java.util.ArrayList;

/**
 * 
 * 弹出菜单
 * 
 * @author CC视频
 *
 */
public class PopMenu implements OnItemClickListener {
	
	public interface OnItemClickListener {
		public void onItemClick(int position);
	}

	private ArrayList<String> itemList;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	private OnItemClickListener listener;
	private int checkedPosition;

	public PopMenu(Context context, int resid, int checkedPosition) {
		this.context = context;
		this.checkedPosition = checkedPosition;

		itemList = new ArrayList<String>();
		RelativeLayout view = new RelativeLayout(context);
		view.setBackgroundResource(resid);

		listView = new ListView(context);
		listView.setPadding(0, ParamsUtil.dpToPx(context, 3), 0, ParamsUtil.dpToPx(context, 3));
		view.addView(listView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		listView.setAdapter(new PopAdapter());
		listView.setOnItemClickListener(this);

		popupWindow = new PopupWindow(view, context.getResources().getDimensionPixelSize(R.dimen.dimen_100_dip), LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (listener != null) {
			listener.onItemClick(position);
			checkedPosition = position;
			listView.invalidate();
		}
		
		dismiss();
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public void addItems(String[] items) {
		for (String s : items)
			itemList.add(s);
	}
	
	public void addItem(String item) {
		itemList.add(item);
	}

	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent, 0, context.getResources().getDimensionPixelSize(R.dimen.dimen_5_dip));
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
	}

	public void dismiss() {
		popupWindow.dismiss();
	}

	private final class PopAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			RelativeLayout layoutView = new RelativeLayout(context);
			TextView textView = new TextView(context);
			textView.setTextSize(16);
			textView.setText(itemList.get(position));
			textView.setTextColor(Color.WHITE);
			textView.setTag(position);
			
			if (checkedPosition == position) {
				layoutView.setBackgroundColor(0x8033B5E5);
			}
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			layoutView.addView(textView, params);
			layoutView.setMinimumHeight(ParamsUtil.dpToPx(context, 26));
			return layoutView;

		}

	}
}
