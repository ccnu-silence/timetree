package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.GroupMemberInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

public class ServiceGroupMemberActivity extends BaseActivity implements OnClickListener,OnItemClickListener{

	TextView titletextview;   //通讯录
	ImageView leftbtn;
	ListView listView;
	ServiceAdapter adapter;
	List<GroupMemberInfo> datalist=new ArrayList<GroupMemberInfo>();
	AppContext appcontext = null;
	AccountInfo accountInfo;
	int  gnum;
	String groupname="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list);
		appcontext = AppContext.getInstance();
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(getIntent().getExtras()!=null){
			gnum=getIntent().getExtras().getInt(AppConstants.GNUM);
			groupname=getIntent().getExtras().getString(AppConstants.GROUPNAME);
		}	
		initdata();
	}
	
	public void initdata()
	{
		showLoadingDialog("正在加载");
		GroupInfoServer.getInstance().GetGroupMember(accountInfo.getUid(), gnum, new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				if(code==0){
					GroupMemberInfo[] groupMemberInfos=(GroupMemberInfo[]) obj;
					List<GroupMemberInfo> sreachlist=Arrays.asList(groupMemberInfos);
					if(sreachlist!=null&&sreachlist.size()>0){
						for(int roletype=0;roletype<=2;roletype++){
							for(int i=0;i<sreachlist.size();i++){
								if(sreachlist.get(i).getRole()==roletype){
									datalist.add(sreachlist.get(i));
								}
							}
						}
					}
				}
				cancelLoadingDialog();
				initview();
				
			}
		});
	}
	
	public void initview()
	{
		titletextview=(TextView) findViewById(R.id.header_title);
    	leftbtn=(ImageView) findViewById(R.id.left_btn);
    	leftbtn.setVisibility(View.VISIBLE);
    	leftbtn.setOnClickListener(this);
    	titletextview.setText(groupname);
		listView=(ListView) findViewById(R.id.contact_list_lv);	
		adapter=new ServiceAdapter(this, datalist, AppConstants.SERVICEGROUPMEMBER);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
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
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		  Intent  intent;
		  if(datalist.get(position).getUid()==accountInfo.getUid()){
			  intent=new Intent(this, MeInfoActivity.class);
			  startActivity(intent);
		  }else{
			  intent=new Intent(this,ContactFriendDatacardActivity.class);
			  Bundle bundle=new Bundle();
			  bundle.putString("state", AppConstants.GROUPMEMBER);	 		
			  bundle.putInt("role", 2);
			  bundle.putInt("targetid", datalist.get(position).getUid()); 
			  intent.putExtras(bundle);
			  startActivity(intent);
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

