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
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.LoadingDialog;


public class ContactsAddPuacResultActivity extends BaseActivity implements OnItemClickListener,OnClickListener,PuacOnclickback{

	ListView listview;
	ContactPuacAdapter listAdapter;
	List<PublicAccount> puaclist=new ArrayList<PublicAccount>();
	TextView titletextview;   //通讯录
	ImageView leftbtn;
	TextView noresulttv;
	String value;
	int vtype;
	AppContext appcontext = null;
	AccountInfo accountInfo;
	String type="";
	List<PublicAccount> querylist;
	int []state;
	LoadingDialog loadingDialog;
	String acstate="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_addfriend_result);
		appcontext = AppContext.getInstance();
		accountInfo=AppServer.getInstance().getAccountInfo();
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
						puaclist=(List<PublicAccount>) obj;
						state=new int[puaclist.size()];
						for(int i=0;i<puaclist.size();i++){
							 querylist=DbHelper.QueryTData("select * from PublicAccount where publicid='"+puaclist.get(i).getPublicid()+"'", PublicAccount.class);
								if(querylist.size()>0){
									if(querylist.get(0).getSubscription()==1){
										state[i]=1;
									}else{
										state[i]=0;
									}
								}else{				
									state[i]=0;
							}
						}
						listAdapter=new ContactPuacAdapter(ContactsAddPuacResultActivity.this, puaclist,AppConstants.CONTACTS_ADDPUACRESULT,state);
						listAdapter.setPuacOnclickback(ContactsAddPuacResultActivity.this);
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
	
    public void setOnClick()
    {
    	listview.setOnItemClickListener(this);
    	leftbtn.setOnClickListener(this);
    }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		    Intent intent=new Intent(ContactsAddPuacResultActivity.this,ContactsPuacDatacardActivity.class);
		    Bundle bundle=new Bundle();
		    bundle.putInt("publicid", puaclist.get(position).getPublicid());
		    if(state[position]==0){
		       bundle.putString("state", AppConstants.CONTACTADDPUACRESULT_BOOKPUAC);
	    	}else{
	    	   bundle.putString("state", AppConstants.CONTACTADDPUACRESULT_LOOKPUAC);
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
			if(acstate.equals(AppConstants.TASKMAIN)){
				this.finish();
			}else{
				Intent intent=new Intent(ContactsAddPuacResultActivity.this,MainActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				this.finish();
			}
			
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void puacClick(int id, final int position, final int thestate) {
		if(thestate==0){
    		AppServer.getInstance().bookPublicAccount(accountInfo.getUid(), puaclist.get(position).getPublicid(), 1, new OnAppRequestListener(){
    			@Override
    			public void onAppRequest(int code, String message, Object obj) {
    				if(code==0){
    					try {
    						showToast("订阅成功");						
    						Contacts newContacts=appcontext.getContacts();
							List<PublicAccount> plist = newContacts.getPublics();
							if(plist!=null){
								for(int i=0;i<plist.size();i++){
									if(plist.get(i).getPublicid()==puaclist.get(position).getPublicid()){
										PublicAccount puac=plist.get(i);
										puac.setSubscription(1);
										plist.set(i, puac);
										DbHelper.getDB(ContactsAddPuacResultActivity.this).update(puac, WhereBuilder.b("publicid", "=", puac.getPublicid()),"subscription");
									}
								}
							}
							newContacts.setPublics(plist);
							appcontext.setContacts(newContacts);	
    						state[position]=1;
    						listAdapter.setState(state);
    					} catch (DbException e) {						
    						e.printStackTrace();
    					}
    				}					
    			}
    		});   	
    	}else{
    		AppServer.getInstance().bookPublicAccount(accountInfo.getUid(), puaclist.get(position).getPublicid(), 0, new OnAppRequestListener(){
    			@Override
    			public void onAppRequest(int code, String message, Object obj) {
    				if(code==0){
    					try {
    						showToast("已经取消订阅");	  					
    						Contacts newContacts=appcontext.getContacts();
							List<PublicAccount> plist = newContacts.getPublics();
							if(plist!=null){
								for(int i=0;i<plist.size();i++){
									if(plist.get(i).getPublicid()==puaclist.get(position).getPublicid()){
										PublicAccount puac=plist.get(i);
										puac.setSubscription(0);
										plist.set(i, puac);
										DbHelper.getDB(ContactsAddPuacResultActivity.this).update(puac, WhereBuilder.b("publicid", "=", puac.getPublicid()),"subscription");
									}
								}
							}
							newContacts.setPublics(plist);
						    appcontext.setContacts(newContacts);
    						state[position]=0;
    						listAdapter.setState(state);
    					} catch (DbException e) {						
    						e.printStackTrace();
    					}
    				}					
    			}
    		});   	
    	}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent!=null){
			int pid=intent.getExtras().getInt("publicid");
			int thestate=intent.getExtras().getInt("state");
			System.out.println("pid--"+pid);
			System.out.println("thestate--"+thestate);
			for(int i=0;i<puaclist.size();i++){
				if(puaclist.get(i).getPublicid()==pid){
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
			if(acstate.equals(AppConstants.TASKMAIN)){
				this.finish();
			}else{
				Intent intent=new Intent(ContactsAddPuacResultActivity.this,MainActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				this.finish();
			}
		}
		return false;
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
