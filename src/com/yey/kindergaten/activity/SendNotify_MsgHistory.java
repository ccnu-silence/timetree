package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.utils.L;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.HistoryMsgBean;
import com.yey.kindergaten.bean.MobanContentInfo;
import com.yey.kindergaten.bean.NotificationInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SendNotify_MsgHistory extends BaseActivity implements OnClickListener, OnScrollListener{
	    @ViewInject(R.id.header_title)TextView tv_headerTitle ;
	    @ViewInject(R.id.right_btn)ImageView iv_right;
	    @ViewInject(R.id.left_btn)ImageView iv_left;
	
	    private ListView listview;
        private Context context;
        private List<NotificationInfo>list=new ArrayList<NotificationInfo>();
        private List<NotificationInfo>showlist=new ArrayList<NotificationInfo>();
        private Notification bean;
        private MyAdapter adapter;
        private View bottomView;
        private AccountInfo accountInfo;
        
        private int NextId;
    	private int type;//模板分类
    	private int lastitem;
        private int count;//数据总数量

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {	    	
	    	super.onCreate(savedInstanceState);
	    	setContentView(R.layout.activity_sendnotice_msghistory);
	        listview=(ListView) findViewById(R.id.id_sendmsg_msghistory_lv);
	        listview.setOnScrollListener(this);	
	        listview.setVerticalScrollBarEnabled(true);
	    	ViewUtils.inject(this);
		    accountInfo=AppServer.getInstance().getAccountInfo();
	    	prepareView();
			loaddata();    
	    }  
	    
	    
	    public void loaddata(){
	     	
	    	AppServer.getInstance().getHistoryNotify(accountInfo.getUid(), 8, -1, new OnAppRequestListenerFriend() {
				
				@Override
				public void onAppRequestFriend(int code, String message, Object obj,
						int nextid) {
				     if(code==AppServer.REQUEST_SUCCESS){
						  NotificationInfo[]info=(NotificationInfo[]) obj;
						  List<NotificationInfo>listinfo= Arrays.asList(info);
						  for(int i=0;i<listinfo.size();i++){
							  list.add(listinfo.get(i));
						  }
						  showlist.addAll(list);
						  NextId=nextid;
						  count=info.length;
						  adapter=new MyAdapter(SendNotify_MsgHistory.this, list);
							 listview.addFooterView(bottomView); 
								if(count<8){
									listview.removeFooterView(bottomView);
								}	
						  listview.setAdapter(adapter);
				      }					
			       }
	 	        });	
	          }
	    
		  private void prepareView(){
			    tv_headerTitle.setText(R.string.sendmsg_msghistory_title);		   
		      	iv_right.setOnClickListener(this);
		      	iv_left.setVisibility(View.VISIBLE);
		      	iv_left.setOnClickListener(this);
		      	bottomView = getLayoutInflater().inflate(R.layout.inflater_show_bottom_moban_view, null);
		   }
	    class MyAdapter extends BaseAdapter{

	    	private List<NotificationInfo>list;
	    	private Context context;
	    	private LayoutInflater mIflater;
	    	
	    	public MyAdapter(Context context ,List<NotificationInfo>list) {
				 this.list=list;
				 mIflater=LayoutInflater.from(context);
	    		
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
		
				return 0;
			}
            private void addData(List<NotificationInfo>lists){
            	 if(lists!=null&&list!=null){
          		   list.addAll(lists);
          	   }
          	     this.notifyDataSetChanged();      	
            }
            
            public void setData(List<NotificationInfo>lists){
           	 if(lists!=null&&list!=null){
        		   list=lists;
        	   }
             this.notifyDataSetChanged();      	
            }
			@Override
			public View getView(int position, View view, ViewGroup viewgroup) {					
				ViewHolder holder=null;		
				if(view==null){
					view=mIflater.inflate(R.layout.inflater_sendnotice_msghistory, null);
					holder=new ViewHolder();
				    holder.contenttv=(TextView) view.findViewById(R.id.id_senmdmsg_inflater_hiscontent_tv);
				    holder.statetv=(TextView) view.findViewById(R.id.id_sendmsg_inflater_hisstate_tv);
				    holder.timetv=(TextView)view.findViewById(R.id.id_sendmsg_inflater_histime_tv);
				    view.setTag(holder);
				}else{
					holder=(ViewHolder)view.getTag();
				}
				   holder.statetv.setTextSize(10);
				   holder.contenttv.setText(list.get(position).getContent());
				   int   state=list.get(position).getStatus();
				  if(state==1){
					  holder.statetv.setText("未发送");
				  }else{	
					  holder.statetv.setText("已发送");
				  }
				
				    holder.timetv.setText(list.get(position).getSenddate());
				return view;
			}
	    	
	    }
	    class ViewHolder {
	    	private TextView timetv;
	    	private TextView contenttv;
	    	private TextView statetv;
	    }
		@Override
		public void onClick(View v) {
		    switch (v.getId()) {
			case R.id.left_btn:								
				Intent intent =new Intent(this,SendNotificationActivity.class);
				setResult(RESULT_OK,intent);
				this.finish();
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


		@Override
		public void onScrollStateChanged(AbsListView abslistview, int scrollState) {
			  if(lastitem == count  && scrollState == this.SCROLL_STATE_IDLE){      
	              bottomView.setVisibility(View.VISIBLE);  
	              mHandler.sendEmptyMessage(0);             
	      }
			
		}
		@Override
		public void onScroll(AbsListView abslistview, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			  lastitem = firstVisibleItem + visibleItemCount - 1;
			
		}
		  private void loadMoreData(){
			     
			    AppServer.getInstance().getHistoryNotify(accountInfo.getUid(), 8, NextId, new OnAppRequestListenerFriend() {			
					@Override
					public void onAppRequestFriend(int code, String message, Object obj,
							int nextid) {
						if(code==AppServer.REQUEST_SUCCESS){
						 NotificationInfo[]info=(NotificationInfo[]) obj;
						 NextId=nextid;
					     List<NotificationInfo> listinfo=Arrays.asList(info);
	                     list.clear();
						 for(int i=0;i<listinfo.size();i++){
							 list.add(listinfo.get(i));
						 }
//	                     adapter.addData(list);	 
	                     showlist.addAll(list);
	                     adapter.setData(showlist);
	                     count = adapter.getCount(); 
	                     if(NextId!=0){
	                         listview.setSelection(NextId-4);
	                     }else{
	                    	 listview.setSelection(adapter.getCount()-4);
	                     }
	                
				       }else{
//				    	 list=new ArrayList<MobanContentInfo>();
//				    	   count = listinfo.size(); 
				       }
					}
				});			    
		}		
        @SuppressLint("HandlerLeak")
	     private Handler mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                    switch (msg.what) {
                    case 0:         
                    	new Thread(new Runnable() {						
							@Override
							public void run() {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}							
							}
						});
                        loadMoreData();
                        bottomView.setVisibility(View.GONE);                  
                        if(NextId ==0){
                                Toast.makeText(SendNotify_MsgHistory.this, "没有更多数据", Toast.LENGTH_LONG).show();
                                listview.removeFooterView(bottomView);
                        }    
                    }
             };
     };        
     @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {	    
         if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
        		Intent intent =new Intent(this,SendNotificationActivity.class);
				setResult(RESULT_OK,intent);
				this.finish();
     		        return true;
     		    }   	 
	    return super.onKeyDown(keyCode, event);
 }
     
}
