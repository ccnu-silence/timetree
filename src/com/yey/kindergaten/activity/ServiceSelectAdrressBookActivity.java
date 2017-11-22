package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import u.aly.bu;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ContactPuacAdapter;
import com.yey.kindergaten.adapter.ContactPuacAdapter.PuacOnclickback;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.DateWidgetDayCell;
import com.yey.kindergaten.util.DateWidgetDayCell.OnItemClick;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceSelectAdrressBookActivity extends BaseActivity implements OnClickListener,PuacOnclickback,OnItemClickListener{

	ListView listView ;	
	ContactPuacAdapter adapter;
	TextView titletextview;  
	ImageView leftbtn;    
	TextView righttv;
	Button commitbtn;
	List<AddressBookBean> sqllist=new ArrayList<AddressBookBean>();
	List<AddressBookBean> list=new ArrayList<AddressBookBean>();
	AccountInfo accountInfo;
	TextView nodatatext;
	int state[];
	int pdtid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serviceselectaddress);
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(getIntent().getExtras()!=null){
			pdtid=getIntent().getExtras().getInt("pdtid");
		}
		FindViewById();
		initdate();
		setOnClick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setText("选择地址本");
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);   
    	 leftbtn.setVisibility(View.VISIBLE);
    	 righttv= (TextView) findViewById(R.id.right_tv);   
         righttv.setVisibility(View.VISIBLE);  
         righttv.setText("新增");
         commitbtn=(Button) findViewById(R.id.service_commit);
         listView=(ListView) findViewById(R.id.service_addressbook_listview);
     }
     
	 
	 public void initdate()
	 {
		 sqllist=DbHelper.QueryTData("select * from AddressBookBean", AddressBookBean.class);
		 list.clear();
		 list.add(sqllist.get(sqllist.size()-1));
		 state=new int[list.size()];
		 for(int i=0;i<list.size();i++){
			 if(i==0){
				 state[i]=1; 
			 }else{
				 state[i]=0; 
			 }
		 }
		 adapter=new ContactPuacAdapter(this, list, AppConstants.SERIVCE_ADRESSBOOKSELECT, state);
		 adapter.setPuacOnclickback(this);
		 listView.setAdapter(adapter);
		 listView.setOnItemClickListener(this);
	 }
   
	 @Override
	 public void puacClick(int id, int position, int state) {	
		 Intent intent=new Intent(this,ServicePointExchangeFillInforActivity.class);
		 Bundle bundle=new Bundle();
		 bundle.putSerializable(AppConstants.ADDRESSBOOKBEAN, list.get(position));
		 bundle.putString(AppConstants.ADDRESSFILLSTATE, AppConstants.SERIVCE_POINTEXCHANGEDIT);
		 intent.putExtras(bundle);
		 startActivityForResult(intent, 1);	
	 }
     
     public void setOnClick()
     {
    	 leftbtn.setOnClickListener(this);    	
         righttv.setOnClickListener(this); 
         commitbtn.setOnClickListener(this);
     }
  
	@Override
	public void onClick(View v) {
		Intent intent;	
	 switch (v.getId()) {
		case R.id.left_btn:
			 intent=new Intent(this,ServicePointExchangeActivity.class);
		     intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     startActivity(intent);
			break;	
	   case R.id.right_tv:
		     intent=new Intent(this,ServicePointExchangeFillInforActivity.class);
		     startActivityForResult(intent, 1);	
		    break;
	   case R.id.service_commit:
				  AppServer.getInstance().exchangeGoods(accountInfo.getUid(), 123, list.get(0).getReceiver(), list.get(0).getAddress(), list.get(0).getPhone(), list.get(0).getCode(), new OnAppRequestListenerFriend() {
						@Override
						public void onAppRequestFriend(int code, String message, Object obj,
								int nextid) {
	                             if(code==0){
	                            	showToast("兑换成功");	                         
	                            	Intent intent=new Intent(ServiceSelectAdrressBookActivity.this,ServicePointExchangeSureActivity.class); 			 		
	                    	 		startActivity(intent);	
	                             }else{
	                            	 showToast("兑换失败");	  	                         
	                          }
						}
					});
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
          if(keyCode==KeyEvent.KEYCODE_BACK){
        	  this.finish();
          }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent=new Intent(ServiceSelectAdrressBookActivity.this,ServiceAllAdrressBookActivity.class);		
		startActivityForResult(intent, 0);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode==0){  //相册返回
			if(intent!=null){			
			  AddressBookBean addressBookBean=(AddressBookBean) intent.getExtras().getSerializable(AppConstants.ADDRESSBOOKBEAN);
			  list.clear();
			  list.add(addressBookBean);
			  adapter=new ContactPuacAdapter(this, list, AppConstants.SERIVCE_ADRESSBOOKSELECT, state);
			  adapter.setPuacOnclickback(this);
			  listView.setAdapter(adapter);
			  listView.setOnItemClickListener(this);
			 }
		 }else{
			 if(intent!=null){			
				   AddressBookBean addressBookBean=(AddressBookBean) intent.getExtras().getSerializable(AppConstants.ADDRESSBOOKBEAN);
				   list.clear();
				   list.add(addressBookBean);
				   adapter=new ContactPuacAdapter(this, list, AppConstants.SERIVCE_ADRESSBOOKSELECT, state);
				   adapter.setPuacOnclickback(this);
				   listView.setAdapter(adapter);
				   listView.setOnItemClickListener(this);
				 }
		    }
	    }

}
