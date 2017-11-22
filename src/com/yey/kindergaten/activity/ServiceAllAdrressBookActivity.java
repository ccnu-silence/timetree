package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import u.aly.bu;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ContactPuacAdapter;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.adapter.ContactPuacAdapter.PuacOnclickback;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.util.AppConstants;

public class ServiceAllAdrressBookActivity  extends BaseActivity implements OnClickListener,PuacOnclickback,OnItemClickListener{
	ListView listView ;	
	ContactPuacAdapter adapter;
	TextView titletextview;  
	ImageView leftbtn;    
	TextView righttv;
	List<AddressBookBean> list=new ArrayList<AddressBookBean>();
	AccountInfo accountInfo;
	TextView nodatatext;
	int state[];
	int position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicealladdress);
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
         righttv.setText("完成");
         listView=(ListView) findViewById(R.id.service_addressbook_listview);
         listView.setOnItemClickListener(this);
     }
     
	 
	 public void initdate()
	 {
		 list=DbHelper.QueryTData("select * from AddressBookBean", AddressBookBean.class);
		 state=new int[list.size()];
		 for(int i=0;i<list.size();i++){
			 if(i==0){
				 state[i]=1; 
			 }else{
				 state[i]=0; 
			 }
		 }
		 adapter=new ContactPuacAdapter(this, list, AppConstants.SERIVCE_ALLADRESSESSBOOK, state);
		 adapter.setPuacOnclickback(this);
		 listView.setAdapter(adapter);
	 }
   
	 @Override
	 public void puacClick(int id, int position, int state) {
		 Intent intent=new Intent(this,ServicePointExchangeFillInforActivity.class);
		 Bundle bundle=new Bundle();
		 bundle.putSerializable(AppConstants.ADDRESSBOOKBEAN, list.get(position));
		 bundle.putString(AppConstants.ADDRESSFILLSTATE, AppConstants.SERIVCE_POINTEXCHANGEDIT);
		 intent.putExtras(bundle);
		 startActivityForResult(intent, 0);
	 }
     
     public void setOnClick()
     {
    	 leftbtn.setOnClickListener(this);    	
         righttv.setOnClickListener(this); 
       
     }

    
	@Override
	public void onClick(View v) {
		Intent intent;	
	 switch (v.getId()) {
		case R.id.left_btn:
		  this.finish();
			break;	
	   case R.id.right_tv:
		    intent=new Intent();
		    Bundle bundle=new Bundle();
		    bundle.putSerializable(AppConstants.ADDRESSBOOKBEAN, list.get(position));
		    intent.putExtras(bundle);
		    setResult(0, intent);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
          if(keyCode==KeyEvent.KEYCODE_BACK){
        	  this.finish();
          }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int theposition,
			long id) {
		 position=theposition;
		 for(int i=0;i<list.size();i++){
			 if(i==position){
				 state[i]=1; 
			 }else{
				 state[i]=0; 
			 }
		 }
		 adapter=new ContactPuacAdapter(this, list, AppConstants.SERIVCE_ALLADRESSESSBOOK, state);
		 adapter.setPuacOnclickback(this);
		 listView.setAdapter(adapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode==0){
			if(intent.getExtras()!=null){
				AddressBookBean addressBookBean=(AddressBookBean) intent.getExtras().getSerializable(AppConstants.ADDRESSBOOKBEAN);
				for(int i=0;i<list.size();i++){
					if(list.get(i).getAdsid()==addressBookBean.getAdsid()){
						 list.set(i, addressBookBean);
						 adapter=new ContactPuacAdapter(ServiceAllAdrressBookActivity.this,list,AppConstants.SERIVCE_ADRESSBOOKSELECT,state);
						 adapter.setPuacOnclickback(this);
				      	 listView.setAdapter(adapter);
				      	 listView.setOnItemClickListener(ServiceAllAdrressBookActivity.this);
				      	 try {
				      		DbHelper.getDB(ServiceAllAdrressBookActivity.this).deleteAll(AddressBookBean.class);
							DbHelper.getDB(ServiceAllAdrressBookActivity.this).saveAll(list);
						} catch (DbException e) {
							e.printStackTrace();
						}
				      	 return ;
					}
				}
			}
		}
	}

	
}
