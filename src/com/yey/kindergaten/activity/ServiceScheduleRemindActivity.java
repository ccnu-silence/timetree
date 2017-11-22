package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;


public class ServiceScheduleRemindActivity extends BaseActivity implements OnClickListener{

	private ListView listview;
	private List<String>list;
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView  iv_left;
	   @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_schedule_remind);
	     ViewUtils.inject(this);		
			 prepareView();
		initdata();
		listview=(ListView) findViewById(R.id.id_service_schedule_remind_lv);
		RemindAdapter  adapter=new RemindAdapter(this, list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent=new Intent(ServiceScheduleRemindActivity.this,ServiceScheduleWriteActivity.class);
			    intent.putExtra("remindtype", list.get(position));
				setResult(RESULT_OK,intent);
				ServiceScheduleRemindActivity.this.finish();
			}
		});
	}
	   
	  private void initdata() {
		list=new ArrayList<String>();
		list.add("不提醒");
		list.add("准时提醒");
		list.add("提前20分钟提醒");
		list.add("提前30分钟提醒");
		list.add("提前1小时提醒");
		list.add("提前1.5小时提醒");
		list.add("提前2小时提醒");		
	}
		private void prepareView() {
		    tv_headerTitle.setText("设 置 日 程 提 醒");
	       	iv_left.setVisibility(View.VISIBLE);
	       	iv_left.setOnClickListener(this);
//	       	iv_right.setVisibility(View.VISIBLE);
//	       	iv_right.setOnClickListener(this);	
		}
	class RemindAdapter extends BaseAdapter {
		  
		  private List<String>list;
		  private Context context;
		  private LayoutInflater mInflater;
		  
		  private RemindAdapter(Context context,List<String>list){
			  this.list=list;
			  this.context=context;
			  mInflater=LayoutInflater.from(context);
		  }

		@Override
		public int getCount() {
			
			return list.size();
		}

		@Override
		public Object getItem(int position) {
		
			return position;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewgroup) {
		
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.inflater_sendmsg_showname, null);
			}
			TextView tv = ViewHolder.get(convertView, R.id.id_inflater_sendmsg_showname_tv);
			tv.setText(list.get(position));			
			return convertView;
		}
	

	}
	@Override
	public void onClick(View v) {
	  switch (v.getId()) {
	case R.id.left_btn:
		this.finish();
		break;

	default:
		break;
	}
		
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


