package com.yey.kindergaten.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ContactPuacAdapter;
import com.yey.kindergaten.adapter.ContactPuacAdapter.PuacOnclickback;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.Session;

import java.util.List;

public class ContactsFriendRequestActivity extends Activity implements OnClickListener,PuacOnclickback,OnItemClickListener{

	ListView listview;
	ContactPuacAdapter adapter;
	TextView titletextview;   //通讯录
	ImageView leftbtn;
	List<MessageRecent> list;
	AccountInfo accountInfo;
	AppContext appcontext;
	int []state;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_friendrequest);
		accountInfo=AppServer.getInstance().getAccountInfo();
		appcontext = AppContext.getInstance();
		FindViewById();
		initData();
		setOnClick();
	}
	public void FindViewById()
    {
	 titletextview=(TextView) findViewById(R.id.header_title);
     titletextview.setText(R.string.contacts_friendrequest);
     leftbtn=(ImageView) findViewById(R.id.left_btn);
     leftbtn.setVisibility(View.VISIBLE);
   	 listview=(ListView) findViewById(R.id.contact_friendreqest_listview); 	 
    }
    
	public void initData()
	{
	 list=DbHelper.QueryTData("select * from messageRecent where action='"+AppConstants.PUSH_ACTION_ADD_FRIENDS+"' or action='"+AppConstants.PUSH_ACTION_AGREE_FRIENDS+"'",MessageRecent.class);	
	 state=new int[list.size()];
	 for(int i=0;i<list.size();i++){
		 List<Friend> frinedlist=DbHelper.QueryTData("select * from Friend where uid='"+list.get(i).getFromId()+"'",Friend.class);	
		 if(frinedlist.size()>0){
			 state[i]=1;
		 }else{
			 state[i]=0;
		 }
	 }
	 adapter=new ContactPuacAdapter(this,list,AppConstants.CONTACTS_FRIENDREQUEST,state);
	 adapter.setPuacOnclickback(this);
	 listview.setAdapter(adapter);
	}
  
    public void setOnClick()
    {
    	leftbtn.setOnClickListener(this);
    	listview.setOnItemClickListener(this);
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			ContactsFriendRequestActivity.this.finish();
			break;

		default:
			break;
		}
		
	}
	@Override
	public void puacClick(int id, final int position, final int thestate) {
		switch (id) {
		case R.id.contact_friend_request_itemaccepttv:
			AppServer.getInstance().handleNewFriend(accountInfo.getUid(), Integer.parseInt(list.get(position).getFromId()+""), 0, new OnAppRequestListener() {
				@Override
				public void onAppRequest(int code, String message, Object obj) {
					if(code==0){
						Toast.makeText(ContactsFriendRequestActivity.this, "已经同意请求", Toast.LENGTH_SHORT).show();
						Friend friend=new Friend();
						MessageRecent mesr=list.get(position);
						if(mesr.getName()!=null){
							friend.setNickname(mesr.getName());
						}						
						friend.setUid(Integer.parseInt(mesr.getFromId()+""));
						if(mesr.getUrl()!=null){
							friend.setAvatar(mesr.getUrl());
						}
						try {
							DbHelper.getDB(ContactsFriendRequestActivity.this).save(friend);
						} catch (DbException e) {
							e.printStackTrace();
						}
						Contacts contacts=appcontext.getContacts();
						List<Friend> frindlist=contacts.getFriends();
						frindlist.add(friend);
						contacts.setFriends(frindlist);
						appcontext.setContacts(contacts);
						 Session session = Session.getSession();
						 Friend f = new Friend();	 	   
						 f.setUid(Integer.parseInt(mesr.getFromId()+""));
						 f.setAvatar(mesr.getAvatar());
						 f.setNickname(mesr.getName());
						 session.put(AppConstants.SESSION_TARGETFRIEND, f);
						 session.put("state",AppConstants.CONTACTS_FRIENDREQUEST);
						 Intent intent=new Intent(ContactsFriendRequestActivity.this,ChatActivity.class);
						 startActivity(intent);		
//						state[position]=1;
//						adapter=new ContactPuacAdapter(ContactsFriendRequestActivity.this,list,AppConstants.CONTACTS_FRIENDREQUEST,state);
//						adapter.setPuacOnclickback(ContactsFriendRequestActivity.this);
//						listview.setAdapter(adapter);
					}else{
						
					}
				}
			});
			
			break;
        case R.id.contact_friend_request_itemrefusetv:
			this.finish();
			break;

		default:
			break;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		MessageRecent messager=list.get(position);
		Intent intent=new Intent(this,ContactFriendDatacardActivity.class);
        Bundle bundle=new Bundle();	
		 bundle.putInt("targetid", Integer.parseInt(messager.getFromId()+""));
		 bundle.putString("role",  1+"");
		 bundle.putString("state", AppConstants.CONTACTS_REQUEST);
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
	
}
