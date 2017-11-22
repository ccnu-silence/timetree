package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ContactPuacAdapter;
import com.yey.kindergaten.adapter.ContactPuacAdapter.PuacOnclickback;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.Items;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.widget.ContactPopwindow;
import com.yey.kindergaten.widget.LoadingDialog;


public class ContactsAddFriendResultActivity extends BaseActivity implements OnItemClickListener,OnClickListener,PuacOnclickback{

	ListView listview;
	ContactPuacAdapter listAdapter;
	TextView titletextview;   //通讯录
	ImageView leftbtn;
	ContactPopwindow postscriptPopwindow;
	EditText postscriptfuyanet;
	TextView postscriptclosebtn;
	TextView postscriptsendbtn;
	TextView noresulttv;
	String value;
	int vtype;
	int clickposition;
	List<Friend> friendlist=new ArrayList<Friend>();
	List<Friend> querylist=new ArrayList<Friend>();
	AccountInfo accountInfo;
	Contacts contacts;
	LoadingDialog loadingDialog;
	String acstate="";
	int []state;
	AppContext appcontext = null;
	List<Items> datalist=new ArrayList<Items>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		appcontext = AppContext.getInstance();
		setContentView(R.layout.contacts_addfriend_result);
		accountInfo=AppServer.getInstance().getAccountInfo();
		contacts=AppContext.getInstance().getContacts();
		loadingDialog=new LoadingDialog(this, "正在加载");
		if(getIntent().getExtras()!=null)
		{
			vtype=getIntent().getExtras().getInt("vtype");
			value=getIntent().getExtras().getString("value");
			acstate=getIntent().getExtras().getString("state");
		}
		FindViewById();
		initData();
		setOnClick();
	}
	public void FindViewById()
    {
	 titletextview=(TextView) findViewById(R.id.header_title);
     titletextview.setText(R.string.contacts_addfriend_sreachresulte);
     leftbtn=(ImageView) findViewById(R.id.left_btn);
     leftbtn.setVisibility(View.VISIBLE);
   	 listview=(ListView) findViewById(R.id.contact_addfriend_result_listview);
   	 noresulttv=(TextView) findViewById(R.id.contact_addfriend_noresulttv);
    }
    
	public void initData()
	{
		if(loadingDialog!=null){
   		 loadingDialog.show();
   	     }
      AppServer.getInstance().findUser(accountInfo.getUid(), value, vtype, new OnAppRequestListener(){
		@Override
		public void onAppRequest(int code, String message, Object obj) {
			if(code==0){
				friendlist=(List<Friend>) obj;
				state=new int[friendlist.size()];
				if(contacts.getPublics()!=null&&contacts.getPublics().size()>0){
		    		 datalist.addAll( 	AppUtils.GetListItem(contacts.getPublics()));
		    	 }			    	
		    	 if(contacts.getFriends()!=null&&contacts.getFriends().size()>0){
		    	    datalist.addAll(AppUtils.GetListItem(contacts.getFriends()));
		    	 }			    
		    	 if(contacts.getTeachers()!=null&&contacts.getTeachers().size()>0){
		    	    datalist.addAll(AppUtils.GetListItem(contacts.getTeachers()));
		    	 }		    		
//		    	 if(contacts.getParents()!=null&&contacts.getParents().size()>0){
//		    		 datalist.addAll(AppUtils.GetListItem(contacts.getParents()));
//		    	 }	
				for(int i=0;i<friendlist.size();i++){									
			    	 for(int q=0;q<datalist.size();q++){
			    		 Items item=datalist.get(q);
			    		 if(item.getId()==friendlist.get(i).getUid()){
			    			 if(item.equals("Friend")){
			    				 state[i]=1; 
			    			 }else if(item.equals("Teacher")){
			    				 state[i]=2; 
			    			 }else{
			    				 state[i]=3; 
			    			 }
			    		 }else{
			    			 state[i]=0; 
			    		 }			    	 }
				}
				listAdapter=new ContactPuacAdapter(ContactsAddFriendResultActivity.this, friendlist, AppConstants.CONTACTS_ADDFRIENDRESULT,state);
				listAdapter.setPuacOnclickback(ContactsAddFriendResultActivity.this);
				listview.setAdapter(listAdapter);				 	
		    	noresulttv.setVisibility(View.GONE);
			}else{
				noresulttv.setVisibility(View.VISIBLE);	
			}		
			if(loadingDialog!=null){
	    		 loadingDialog.cancel();
	    	 }
		 }
      });  	
	}

    public void  initView()
    {   	
    	postscriptPopwindow=new ContactPopwindow(ContactsAddFriendResultActivity.this, postscriptOnclick, R.layout.contacts_addfriend_fuyanpop, "postscript");   	
    }
    
    public void setOnClick()
    {
    	listview.setOnItemClickListener(this);
    	leftbtn.setOnClickListener(this);
    }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	    	 Intent intent=new Intent(ContactsAddFriendResultActivity.this,ContactFriendDatacardActivity.class);
	    	 Bundle bundle=new Bundle();	 	
	    	 bundle.putInt("role", 1);	 	    	
	 		 bundle.putInt("targetid",friendlist.get(position).getUid()); 
	 		 if(state[position]==0){
	 			     bundle.putString("state", AppConstants.CONTACTS_NOFRIEND);	
		    	}else if(state[position]==1){
		    		 bundle.putString("state", AppConstants.CONTACTS_ISFRIEND);	
		    	}else{
		    		 bundle.putString("state", AppConstants.CONTACTS_TEACHER);	
		    	}
	    	 intent.putExtras(bundle);
	 		 startActivity(intent);
	}
	
	OnClickListener postscriptOnclick=new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.contact_addfriend_fuyuanpop_closebt:
				
				break;
            case R.id.contact_addfriend_fuyuanpop_sendbt:

				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:		
			if(acstate.equals(AppConstants.CAPETURE)){
				Intent intent=new Intent(ContactsAddFriendResultActivity.this,MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}else{
				Intent intent=new Intent(ContactsAddFriendResultActivity.this,ContactsAddFriendActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}		
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent!=null){
			int uid=intent.getExtras().getInt("uid");
			int thestate=intent.getExtras().getInt("state");
			System.out.println("uid--"+uid);
			System.out.println("thestate--"+thestate);
			for(int i=0;i<friendlist.size();i++){
				if(friendlist.get(i).getUid()==uid){
					state[i]=thestate;
					System.out.println("state[i]--"+state[i]);
					listAdapter.setState(state);
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(acstate.equals(AppConstants.CAPETURE)){
				Intent intent=new Intent(ContactsAddFriendResultActivity.this,MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}else{
				Intent intent=new Intent(ContactsAddFriendResultActivity.this,ContactsAddFriendActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}		
		}
		return false;
	}
	@Override
	public void puacClick(int id, final int position, int thestate) {
		if(thestate==0){	
			 AppServer.getInstance().addFriend(accountInfo.getUid(), friendlist.get(position).getUid(), new OnAppRequestListener(){
		 			@Override
		 			public void onAppRequest(int code, String message, Object obj) {		 			
		 				if(code==0){	
		 					showToast("已经发送好友请求，请等待对方回复");	 
		 					ContactsAddFriendResultActivity.this.finish();
		 				}else if(code==2){
		 					showToast("已经发送好友请求，请等待对方回复");	 
		 					ContactsAddFriendResultActivity.this.finish();
		 				}else{
		 					
		 				}
		 			}  
		 	      });		
		  }else if(thestate==1){
			  AppServer.getInstance().deletContactPeople(accountInfo.getUid(), friendlist.get(position).getUid(), new OnAppRequestListener(){
		 			@Override
		 			public void onAppRequest(int code, String message, Object obj) {
		 				if(code==0){	
		 					showToast("成功好友删除");	 	 				
							try {
								DbHelper.getDB(ContactsAddFriendResultActivity.this).delete(Friend.class, WhereBuilder.b("uid", "=", friendlist.get(position).getUid()));
							} catch (DbException e) {								
								e.printStackTrace();
							}														
		 					Contacts contacts=appcontext.getContacts();
		 					List<Friend> list=contacts.getFriends();
		 					for(int i=0;i<list.size();i++){
		 						if(list.get(i).getUid()==friendlist.get(position).getUid()){
		 							list.remove(i);
		 						}
		 					}
		 					contacts.setFriends(list);
		 					appcontext.setContacts(contacts);
		 					Intent intent=new Intent(ContactsAddFriendResultActivity.this,ContactsAddFriendActivity.class);
		 					startActivity(intent);		 					
		 				}		
		 			}  
		 	      });		
		  }else{
			  
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
