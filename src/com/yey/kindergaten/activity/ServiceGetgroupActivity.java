package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.GroupListAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.GroupList;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.xlist.XListView;
import com.yey.kindergaten.widget.xlist.XListView.IXListViewListener;

public class ServiceGetgroupActivity extends BaseActivity implements OnClickListener, IXListViewListener{
	private  XListView lv;
	private GroupListAdapter  listadapter;
	private ArrayList<GroupInfoBean>  grouplist=new ArrayList<GroupInfoBean>();
	private AccountInfo accountinfo;
	private Handler mHandler;
	private Animation pop_in, pop_out;
	private Boolean istop=true;
	@ViewInject(R.id.left_btn)ImageView leftbtn;
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.right_btn)ImageView rightbtn;
	@ViewInject(R.id.servicegrouplist_creategroupbtn)TextView creatgroupbtn;
	@ViewInject(R.id.servicegrouplist_addgroupbtn)TextView addgroupbtn;
	@ViewInject(R.id.servicegrouplist_menubtn)RelativeLayout menurl;
	
	private List<GroupInfoBean>group_friend_km=new ArrayList<GroupInfoBean>();

	private List<GroupInfoBean>group_class=new ArrayList<GroupInfoBean>();

	GroupListAdapter  alistadapter;
	List<GroupInfoBean> sqllist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getgroup_main);
		ViewUtils.inject(this);
		initView();
		initdata();
		setonclick();
	}

	
	private void initView() {
		leftbtn.setVisibility(View.VISIBLE);
		rightbtn.setVisibility(View.VISIBLE);
		rightbtn.setImageResource(R.drawable.icon_spanner_up);
		titletv.setVisibility(View.VISIBLE);
		mHandler = new Handler();
		titletv.setText("群动态");
		lv=(XListView) findViewById(R.id.lv_activity_getgroup_item);
		accountinfo=AppServer.getInstance().getAccountInfo();
		lv.setXListViewListener(this);
	    sqllist=DbHelper.QueryTData("select * from GroupInfoBean ", GroupInfoBean.class);
	    if(sqllist!=null&&sqllist.size()>0){
	    	grouplist.addAll(sqllist);	    
	    }else{ 
	    	try {
				DbHelper.getDB(ServiceGetgroupActivity.this).saveAll(grouplist);
			} catch (DbException e) {				
				e.printStackTrace();
			}
	    }
		
	    alistadapter=new GroupListAdapter(ServiceGetgroupActivity.this);	
		SeparatorAdapter(grouplist);
		lv.setAdapter(alistadapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {				 				    
					Intent intent=new Intent(ServiceGetgroupActivity.this, ServiceFriendsterActivity.class);
					Bundle bundle=new Bundle();
					int n=0;
					if(grouplist.get(position-1).getGtype()==0||grouplist.get(position-1).getGtype()==1){
						n=1;
					}else{
						n=2;
						}
					bundle.putInt(AppConstants.GNUM, grouplist.get(position-n).getGnum());
					bundle.putInt("gtype", grouplist.get(position-n).getGtype());
					bundle.putInt("cid", grouplist.get(position-n).getGid());
					bundle.putString("groupname", grouplist.get(position-n).getGname());
					intent.putExtras(bundle);
					startActivity(intent);
			}
		});
	}	
	
	public void setonclick()
	{
		leftbtn.setOnClickListener(this);
		rightbtn.setOnClickListener(this);
		menurl.setOnClickListener(this);
		creatgroupbtn.setOnClickListener(this);
		addgroupbtn.setOnClickListener(this);
	}
		
	public void SeparatorAdapter(ArrayList<GroupInfoBean> grouplist){
		for(int i=0;i<grouplist.size();i++){	       			       			
   			switch (grouplist.get(i).getGtype()) {
			case 0:
			case 1:
				group_friend_km.add(grouplist.get(i));    //好友群       幼儿园群
				break;
			case 2:
				group_class.add(grouplist.get(i));    //班级群
				break;

			}       		        			
   		}	
		if(group_friend_km!=null&&group_friend_km.size()!=0){
			alistadapter.addData(group_friend_km);
			if(group_class!=null&&group_class.size()!=0){
			alistadapter.addSeparatorItem(new GroupInfoBean());
			}
   		}

   	    if(group_class!=null&&group_class.size()!=0){
   	    	alistadapter.addData(group_class);  
   	    }

	}
	
	private void initdata() {
		pop_in = AnimationUtils.loadAnimation(this, R.anim.pop_in);
	    pop_out = AnimationUtils.loadAnimation(this, R.anim.pop_out);
		AppServer.getInstance().getGroups(accountinfo.getUid(), new OnAppRequestListener() {		
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				if (code==AppServer.REQUEST_SUCCESS) {
					if(obj.getClass().isArray()){
						GroupInfoBean[] groupinfobeans=(GroupInfoBean[]) obj;
						grouplist.clear();
                         for(int i=0;i<groupinfobeans.length;i++){                       	  
                        	  grouplist.add(groupinfobeans[i]);                       	  
                          }
						
				   if(sqllist==null||sqllist.size()==0){
				    		alistadapter=new GroupListAdapter(ServiceGetgroupActivity.this);	
				    		SeparatorAdapter(grouplist);
				    		lv.setAdapter(alistadapter);
				          }
					try {
							DbHelper.getDB(ServiceGetgroupActivity.this).deleteAll(GroupInfoBean.class);
							DbHelper.getDB(ServiceGetgroupActivity.this).saveAll(grouplist);
						} catch (DbException e) {								
							e.printStackTrace();
						}
					}
				}			
			}
		});		
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.right_btn:
			if (istop) {
				rightbtn.setImageResource(R.drawable.icon_plus);
				menurl.startAnimation(pop_in);
				menurl.setVisibility(View.VISIBLE);
				rightbtn.setImageResource(R.drawable.icon_spanner_down);
				istop=false;
			}else{				
				rightbtn.setImageResource(R.drawable.icon_plus);
				menurl.startAnimation(pop_out);
				menurl.setVisibility(View.GONE);
				rightbtn.setImageResource(R.drawable.icon_spanner_up);
				istop=true;
			}			
			break;		
		case R.id.left_btn:
			Intent i=new Intent(ServiceGetgroupActivity.this,MainActivity.class);
			startActivity(i);
			break;
		case R.id.servicegrouplist_menubtn:
			rightbtn.setImageResource(R.drawable.icon_plus);
			menurl.startAnimation(pop_out);
			menurl.setVisibility(View.GONE);
			rightbtn.setImageResource(R.drawable.icon_spanner_up);
			istop=true;
			
			break;
		case R.id.servicegrouplist_creategroupbtn:
			if(!istop){
				rightbtn.setImageResource(R.drawable.icon_plus);
				menurl.startAnimation(pop_out);
				menurl.setVisibility(View.GONE);
				rightbtn.setImageResource(R.drawable.icon_spanner_up);
				istop=true;
			}
			intent=new Intent(this,ServiceCreatKinderSelectActivity.class);
			startActivity(intent);
			break;
		case R.id.servicegrouplist_addgroupbtn:
			if(!istop){
				rightbtn.setImageResource(R.drawable.icon_plus);
				menurl.startAnimation(pop_out);
				menurl.setVisibility(View.GONE);
				rightbtn.setImageResource(R.drawable.icon_spanner_up);
				istop=true;
			}
		   	intent=new Intent(this,ServiceAddKinderActivity.class);
		   	intent.putExtra(AppConstants.STATE, AppConstants.SERVICEGROUP);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		 grouplist.clear();
		 group_friend_km.clear();
		 group_class.clear();
		 GroupInfoBean groupInfoBean=new GroupInfoBean();
	     groupInfoBean.setGnum(0);
	     groupInfoBean.setGtype(0);
	     groupInfoBean.setGname("好友群");
	     grouplist.add(groupInfoBean);  
		 sqllist=DbHelper.QueryTData("select * from GroupInfoBean ", GroupInfoBean.class);
		 if(sqllist!=null){
			 for(int grouptype=1;grouptype<4;grouptype++){
		    		for (int i = 0; i < sqllist.size(); i++) {
						if(sqllist.get(i).getGnum()!=0){
							if(sqllist.get(i).getGtype()==grouptype){
								grouplist.add(sqllist.get(i));
							}								
						}	
					}
		    	}
		  }
		 alistadapter=new GroupListAdapter(ServiceGetgroupActivity.this);	
		 SeparatorAdapter(grouplist);
		 lv.setAdapter(alistadapter);
	}
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 1000);
		
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 1000);
	}
	
	private void onLoad() {
		lv.stopRefresh();
		lv.stopLoadMore();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent=new Intent(ServiceGetgroupActivity.this,MainActivity.class);
			startActivity(intent);
		}
		return false;
	}
	
}
