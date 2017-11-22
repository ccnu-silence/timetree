package com.yey.kindergaten.activity;
import java.io.File;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.adapter.ServiceAdapter.Onclickback;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.TaskBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.widget.MyListViewWithScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ServiceTaskMainActivity extends BaseActivity implements OnClickListener,OnItemClickListener,Onclickback{
	MyListViewWithScrollView listView;
	ServiceAdapter adapter;
	TextView titletextview;   //通讯录
	ImageView leftbtn;       
	List<TaskBean> list=new ArrayList<TaskBean>();
	AccountInfo accountInfo;
	Double mypoint;
	List<GroupInfoBean> sqllist;
	CharSequence[] kinditems = {"创建幼儿园" , "加入幼儿园"};
	CharSequence[] classitems = { "加入班级", "创建班级" };
    GroupInfoBean groupInfoBean;
    TextView nodatatv;
    //提示页面
    @ViewInject(R.id.common_network_disable)LinearLayout layout_networkdisable;
    @ViewInject(R.id.network_disable_button_relink)ToggleButton networkbutton;
    @ViewInject(R.id.common_loading)LinearLayout layout_loading;
    @ViewInject(R.id.common_error)LinearLayout layout_error;
    @ViewInject(R.id.error_button)ToggleButton errorbutton;
    @ViewInject(R.id.common_empty)LinearLayout layout_empty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_taskmain);
		ViewUtils.inject(this);
		accountInfo=AppServer.getInstance().getAccountInfo();
		FindViewById();
    	initData();
    	sqllist=DbHelper.QueryTData("select * from GroupInfoBean ", GroupInfoBean.class);
    	setOnClick();
	}
	
	
	public void FindViewById()
    {
		
   	 titletextview=(TextView) findViewById(R.id.header_title);
   	 titletextview.setText(R.string.service_task);
   	 leftbtn=(ImageView) findViewById(R.id.left_btn);
   	 leftbtn.setVisibility(View.VISIBLE);  
   	 listView=(MyListViewWithScrollView) findViewById(R.id.service_taskmain_listview);
   	 nodatatv=(TextView) findViewById(R.id.nodatatv);
   	 //提示页面事件
     networkbutton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonview, boolean isChecked) {
			initData();
		}
	});
    }
    
	
	
	
	
    public void initData()
    {
    	 //showLoadingDialog("正在处理...");
    	layout_loading.setVisibility(View.VISIBLE);
    	 AppServer.getInstance().GetTasks(accountInfo.getUid(), accountInfo.getRole(), new OnAppRequestListener() {	
			@Override
			public void onAppRequest(int code, String message, Object obj) {
			//	cancelLoadingDialog();	
				layout_loading.setVisibility(View.GONE);
				layout_networkdisable.setVisibility(View.GONE);
				networkbutton.setChecked(true);
				layout_error.setVisibility(View.GONE);
				errorbutton.setChecked(true);
				if(code==0){
					if(obj instanceof java.util.List){
						list= (List<TaskBean>) obj;	
					}else{
						if(obj instanceof TaskBean){
							TaskBean task = (TaskBean)obj;
							list.add(task);
						}else{
							layout_empty.setVisibility(View.VISIBLE);
						}
					}
						
					nodatatv.setVisibility(View.GONE);
				}else if(code == AppServer.REQUEST_NETWORK_ERROR){
					layout_networkdisable.setVisibility(View.VISIBLE);
				}else if(code == AppServer.REQUEST_CLIENT_ERROR){
					layout_error.setVisibility(View.GONE);
				}
				else{
					layout_empty.setVisibility(View.VISIBLE);
				}
				adapter=new ServiceAdapter( ServiceTaskMainActivity.this,list,AppConstants.SERIVCE_TASKMAIN);
		    	adapter.setOnclickback(ServiceTaskMainActivity.this);
		    	listView.setAdapter(adapter);		
			}
		});
    	
    }
    
    public void setOnClick()
    {
   	 leftbtn.setOnClickListener(this);
   	 listView.setOnItemClickListener(this);
    } 
    
	@Override
	public void onClick(View v) {
	switch (v.getId()) {
		case R.id.right_btn:
			
			break;	
       case R.id.left_btn:
    	   finish();
			break;	
		
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {	
		TaskBean taskdate=list.get(position);
		startWebLooktask(taskdate.getTaskid(),taskdate.getStatus());
	}

	@Override
	public void click(int id, int position) {
		TaskBean taskdate=list.get(position);
		if(taskdate.getStatus()==1){
			 showToast("该任务已完成,请选择其他任务");
			return;
		}
		Intent intent = null;
		switch (taskdate.getTaskid()) {
		case 1:   //APP功能介绍
			
			break;
		case 2:   //创建班级
				if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
				    intent=new Intent(ServiceTaskMainActivity.this,ServiceCompleteInformationActivity.class);	
					startActivityForResult(intent, 1);
				}else{
					intent=new Intent(this,ServiceCreateKinderActivity.class);
					intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
					startActivity(intent);
				}		
			break;
		case 3:   //订阅公众号
			intent=new Intent(this,ServiceTaskBookPuacActivity.class);
			intent.putExtra("state", AppConstants.TASKMAIN);
			startActivity(intent);
			break;
		case 4:   //加好友
			intent=new Intent(this,ContactsAddFriendActivity.class);
			intent.putExtra("state", AppConstants.TASKMAIN);
			startActivity(intent);
			break;
		case 5:   //加入班级
			intent=new Intent(this,ServiceAddKinderActivity.class);	
			startActivity(intent);
			break;
		case 6:  //创建或者加入班级
			showDialogItems(classitems, "创建或者加入班级", new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if(item==0){   //加入班级
						Intent  intent=new Intent(ServiceTaskMainActivity.this,ServiceAddKinderActivity.class);	
						intent.putExtra(AppConstants.STATE, AppConstants.TASKMAIN);
						startActivity(intent);                   
                    }else{ 		   //创建班级        
            				if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
            						Intent intent=new Intent(ServiceTaskMainActivity.this,ServiceCompleteInformationActivity.class);	
            						startActivityForResult(intent, 1);
            				}else{
            					Intent intent=new Intent(ServiceTaskMainActivity.this,ServiceCreateKinderActivity.class);
                				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
                				startActivity(intent);
            				}		         			
                   } 
				}
			});
			break;
		case 7:   //加入或创建一个幼儿园
			showDialogItems(kinditems, "创建或者加入幼儿园群", new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if(item==1){   //加入幼儿园
						Intent  intent=new Intent(ServiceTaskMainActivity.this,ServiceAddKinderActivity.class);	
						intent.putExtra(AppConstants.STATE, AppConstants.TASKMAIN);
						startActivity(intent);                   
                    }else{ 		   //创建幼儿园         	
                    	Boolean isflag=true;
        				for(int i=0;i<sqllist.size();i++){
        					if(sqllist.get(i).getGtype()==1){
        						isflag=false;
        					    break;
        					}
        				}
        				if(isflag){
        					if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
        							Intent intent=new Intent(ServiceTaskMainActivity.this,ServiceCompleteInformationActivity.class);	
        							startActivityForResult(intent, 0);
        					}else{
        						Intent intent=new Intent(ServiceTaskMainActivity.this,ServiceCreateKinderActivity.class);
            					intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEKINDER);
            					startActivity(intent);
        					}
        				}else{
        					 showToast("已经拥有一个幼儿园群，不可以在添加幼儿园群");       				
        				}
                   } 
				}
			});
			break;
		case 8:   //加入幼儿园
			intent=new Intent(ServiceTaskMainActivity.this,ServiceAddKinderActivity.class);	
			startActivity(intent);
			break;
		case 9:  //完善班级资料
			intent=new Intent(this, ServiceGetgroupActivity.class);	
			startActivity(intent);
			break;
		case 10:  //完善个人资料
			intent=new Intent(this,MeInfoActivity.class);
			intent.putExtra("state", AppConstants.TASKMAIN);
			startActivity(intent);

			break;
		case 11:  //完善幼儿园资料
			for(int i=0 ;i<sqllist.size();i++)
			{
				groupInfoBean=sqllist.get(i);
				if(groupInfoBean.getGtype()==1){  
					intent=new Intent(this,ServiceCreateKinderSuccessActivity.class);
			        intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
			 		startActivity(intent);
				}
			}		
			break;
		case 12:   //邀请家长加入班级
			for(int i=0 ;i<sqllist.size();i++)
			{
				groupInfoBean=sqllist.get(i);
				if(groupInfoBean.getGtype()==2){  //班级群
					intent=new Intent(this,ServiceCreateKinderSuccessActivity.class);
			        intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
			 		startActivity(intent);
				}
			}		
			break;
		case 13:   //邀请老师加入幼儿园	
			for(int i=0 ;i<sqllist.size();i++)
			{
				groupInfoBean=sqllist.get(i);
				if(groupInfoBean.getGtype()==2){  //班级群
					intent=new Intent(this,ServiceCreateKinderSuccessActivity.class);
			        intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
			 		startActivity(intent);
				}
			}		
			break;
		case 14:   //在朋友圈中发表动态
			intent=new Intent(this,ServiceFriendsterActivity.class);
			intent.putExtra("state", AppConstants.TASKMAIN);
			startActivity(intent);
			break;
		case 15:  //在朋友圈中发表评论
			intent=new Intent(this,ServiceFriendsterActivity.class);
			intent.putExtra("state", AppConstants.TASKMAIN);
			startActivity(intent);
			break;
		case 16:   //账户安全
			intent=new Intent(this,IdSafeActivity.class);
			intent.putExtra("state", AppConstants.TASKMAIN);
			startActivity(intent);
			break;
		case 17:   
			
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		super.onActivityResult(arg0, arg1, intent);
		if(intent!=null){
			if(arg0==0){
				intent=new Intent(this,ServiceCreateKinderActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEKINDER);
				startActivity(intent);
			}
		}else if(arg0==1){
			intent=new Intent(this,ServiceCreateKinderActivity.class);
			intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
			startActivity(intent);
		}
	}
	
	public void startWebLooktask(int taskid,int Status)
	{
		Intent intent = new Intent(this,CommonBrowser.class);
		Bundle bundle = new Bundle();
		bundle.putString(AppConstants.INTENT_URL, "http://58.220.10.100:701/task/Taskinfo?taskid="+taskid);
		bundle.putString(AppConstants.INTENT_NAME,"任务介绍");
		bundle.putString("Status", Status+"");
		intent.putExtras(bundle);
	    startActivity(intent);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	

	private View.OnClickListener mErrorClickListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			showToast("请点击重试一次");
		}
	};
}
