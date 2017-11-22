package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
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

import java.util.ArrayList;
import java.util.List;

public class ServiceTaskBookPuacActivity extends BaseActivity implements OnClickListener,OnItemClickListener,PuacOnclickback{

	TextView titletextview;   
	ImageView leftbtn;
	AccountInfo accountInfo;
	ProgressBar progressBar;
	List<PublicAccount> puaclist=new ArrayList<PublicAccount>();
	int []bookstate;
	ListView listView;
	EditText editText;
	Button sreachbtn;
	LinearLayout ly;
	ContactPuacAdapter listAdapter;
	AppContext appcontext;
	List<PublicAccount> datalist=new ArrayList<PublicAccount>();
	String state="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicetaskbookpuacmain);
		accountInfo=AppServer.getInstance().getAccountInfo();
		appcontext=AppContext.getInstance();
		if(getIntent().getExtras()!=null){
			state=getIntent().getExtras().getString(AppConstants.STATE);
		}
		FindViewById();
		initData();
		setonclick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);
    	 leftbtn.setVisibility(View.VISIBLE);
    	 titletextview.setText(R.string.contacts_public_account);   
    	 progressBar=(ProgressBar) findViewById(R.id.contact_addfriend_progressbar);
    	 editText=(EditText) findViewById(R.id.contact_addfriend_edittext);
		 sreachbtn=(Button) findViewById(R.id.contact_addfriend_sreachbt);
		 listView=(ListView) findViewById(R.id.contact_addfriend_listview);
		 ly=(LinearLayout) findViewById(R.id.contact_addfriend_ly);
		 progressBar=(ProgressBar) findViewById(R.id.contact_addfriend_progressbar);
    	
     }
	 
	 public void setonclick()
	 {
		 leftbtn.setOnClickListener(this);
		 listView.setOnItemClickListener(this);
		 sreachbtn.setOnClickListener(this);
	 }
	 
	 public void initData()
	 {		 
		 datalist=appcontext.getContacts().getPublics();
    	 if(datalist!=null){
    		 bookstate=new int[datalist.size()];
    		 for(int i=0;i<datalist.size();i++){
    			 PublicAccount puac=datalist.get(i);
    			 if(puac.getSubscription()==1){
    				 bookstate[i]=1;
    			 }else{
    				 bookstate[i]=0;
    				 puaclist.add(puac);
    			 }
    		 }
    	 }
    	 bookstate=new int[puaclist.size()];
    	for(int i=0;i<bookstate.length;i++){
    		bookstate[i]=0;
		 }	
         if(progressBar!=null){
				if(puaclist.size()>0){
					ly.setVisibility(View.VISIBLE);
		        }else{
					ly.setVisibility(View.GONE);
			      }	
    	 }
         progressBar.setVisibility(View.GONE);
		 listAdapter=new ContactPuacAdapter(ServiceTaskBookPuacActivity.this, puaclist,AppConstants.CONTACTS_PUACMAIN,bookstate);
		 listAdapter.setPuacOnclickback(ServiceTaskBookPuacActivity.this);
		 listView.setAdapter(listAdapter);	 
	 }

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.left_btn:
			if(state.equals(AppConstants.TASKMAIN)){
				intent=new Intent(this,ServiceTaskMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}else{
				intent=new Intent(this,ContactsAddFriendActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			this.finish();
			break;
		case R.id.contact_addfriend_sreachbt:
		    intent = new Intent(ServiceTaskBookPuacActivity.this,ContactsAddPuacResultActivity.class);
			if(editText.getText().toString().equals("")){					
				showToast("请输入查询条件");
				return;
			}
			if(isNumeric(editText.getText().toString())){
				intent.putExtra("vtype", 2);
			}else{
				intent.putExtra("vtype", 2);
			}
			intent.putExtra("state", AppConstants.TASKMAIN);
			intent.putExtra("value", editText.getText().toString());
			startActivity(intent);
			break;	
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		    Intent intent=new Intent(ServiceTaskBookPuacActivity.this,ContactsPuacDatacardActivity.class);
		    Bundle bundle=new Bundle();
		    bundle.putInt("publicid", puaclist.get(position).getPublicid());
		    if(bookstate[position]==0){
		    	   bundle.putString("state", AppConstants.TASK_BOOKPUAC);
		    }else{
		    	   bundle.putString("state", AppConstants.TASK_LOOKPUAC);
		    }
			intent.putExtras(bundle);
			startActivity(intent);
		
	}

	@Override
	public void puacClick(int id, final int position, int thestate) {
		if(thestate==0){
			System.out.println("accountInfo.getUid()---"+accountInfo.getUid());
			System.out.println("puaclist.get(position).getPublicid()---"+puaclist.get(position).getPublicid());
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
										DbHelper.getDB(ServiceTaskBookPuacActivity.this).update(puac, WhereBuilder.b("publicid", "=", puac.getPublicid()),"subscription");
									}
								}
							}
							newContacts.setPublics(plist);
							appcontext.setContacts(newContacts);
							bookstate[position]=1;
							listAdapter.setState(bookstate);
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
							Toast.makeText(ServiceTaskBookPuacActivity.this, "已经取消订阅", Toast.LENGTH_SHORT).show();
							Contacts newContacts=appcontext.getContacts();
							List<PublicAccount> plist = newContacts.getPublics();
							if(plist!=null){
								for(int i=0;i<plist.size();i++){
									if(plist.get(i).getPublicid()==puaclist.get(position).getPublicid()){
										PublicAccount puac=plist.get(i);
										puac.setSubscription(0);
										plist.set(i, puac);
										DbHelper.getDB(ServiceTaskBookPuacActivity.this).update(puac, WhereBuilder.b("publicid", "=", puac.getPublicid()),"subscription");
									}
								}
							}
							newContacts.setPublics(plist);
							appcontext.setContacts(newContacts);
							bookstate[position]=0;
							listAdapter.setState(bookstate);
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
			for(int i=0;i<puaclist.size();i++){
				if(puaclist.get(i).getPublicid()==pid){
					bookstate[i]=thestate;
					listAdapter.setState(bookstate);
				}
			}
		}
	}
	public static boolean isNumeric(String str){
		   for(int i=str.length();--i>=0;){
		      int chr=str.charAt(i);
		      if(chr<48 || chr>57)
		         return false;
		   }
		   return true;
		}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		       Intent intent;
		  if(keyCode==KeyEvent.KEYCODE_BACK){
			  if(state.equals(AppConstants.TASKMAIN)){
					intent=new Intent(this,ServiceTaskMainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
					intent=new Intent(this,ContactsAddFriendActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
		  }
		return super.onKeyDown(keyCode, event);
	}

}
