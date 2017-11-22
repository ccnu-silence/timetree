package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import u.aly.co;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.adapter.ServiceAdapter.Onclickback;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;


public class ServiceAddressBookActivity extends BaseActivity implements OnClickListener,OnItemClickListener,Onclickback{

	ListView listView ;	
	ServiceAdapter adapter;
	TextView titletextview;   //通讯录
	ImageView leftbtn;    
	TextView righttv;
	List<AddressBookBean> list=new ArrayList<AddressBookBean>();
	AccountInfo accountInfo;
	TextView nodatatext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_pointexchange_addressbook);
		accountInfo=AppServer.getInstance().getAccountInfo();		
		FindViewById();
	    initData();
	    setOnClick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setText(R.string.service_addressbook);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);   
    	 leftbtn.setVisibility(View.VISIBLE);
    	 righttv= (TextView) findViewById(R.id.right_tv);   
    	 righttv.setVisibility(View.VISIBLE);
         righttv.setText("新增");    	
    	 listView=(ListView) findViewById(R.id.service_addressbook_listview);
    	 nodatatext=(TextView) findViewById(R.id.pointexchange_addressbooknodatatv);
     }
     
     public void initData()
     {
    	 showLoadingDialog("正在加载...");
    	 AppServer.getInstance().GetAllAddress(accountInfo.getUid(), new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {
		           if(code==0){
		        	   List<AddressBookBean> query=(List<AddressBookBean>) obj;
		        	   list.addAll(query);
		        	   try {
							DbHelper.getDB(ServiceAddressBookActivity.this).deleteAll(AddressBookBean.class);
							DbHelper.getDB(ServiceAddressBookActivity.this).saveAll(list);
						} catch (DbException e) {
						
							e.printStackTrace();
						}
		           }else{
		        	   list=new ArrayList<AddressBookBean>();
		           }
		          
		           adapter=new ServiceAdapter(ServiceAddressBookActivity.this,list,AppConstants.ADDRESSBOOKMANAGEEDIT);
		      	   adapter.setOnclickback(ServiceAddressBookActivity.this);
		      	   listView.setAdapter(adapter);
		      	   listView.setOnItemClickListener(ServiceAddressBookActivity.this);
		      	   if(list.size()>0){
		      		   nodatatext.setVisibility(View.GONE);
		      	   }else{
		      		   nodatatext.setVisibility(View.VISIBLE);
		      	   }
		      	   cancelLoadingDialog();	
			}
		});
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
		    intent=new Intent(this,ServicePointExchangeActivity.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
			break;	
	   case R.id.right_tv:
//		   if(type.equals(AppConstants.ADDRESSBOOKMANAGELOOK)){
//			   type=AppConstants.ADDRESSBOOKMANAGEEDIT;
//		   }else{
//			   type=AppConstants.ADDRESSBOOKMANAGELOOK;
//		   }
//		   adapter=new ServiceAdapter(ServiceAddressBookActivity.this,list,type);
//		   adapter.setOnclickback(ServiceAddressBookActivity.this);
//      	   listView.setAdapter(adapter);  
//     	   listView.setOnItemClickListener(ServiceAddressBookActivity.this);
		    intent=new Intent(ServiceAddressBookActivity.this,ServicePointExchangeFillInforActivity.class);
   			intent.putExtra(AppConstants.ADDRESSFILLSTATE,AppConstants.SERIVCE_POINTEXCHANGEMANAGEFILL);
   			startActivity(intent);
		    break;			
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {			
				Intent intent=new Intent(this,ServicePointExchangeFillInforActivity.class);	
				Bundle bundle=new Bundle();			
				bundle.putSerializable(AppConstants.ADDRESSBOOKBEAN, list.get(position));
				bundle.putString(AppConstants.ADDRESSFILLSTATE, AppConstants.SERIVCE_POINTEXCHANGMANAGEEDIT);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);	
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
						 adapter=new ServiceAdapter(ServiceAddressBookActivity.this,list,AppConstants.ADDRESSBOOKMANAGEEDIT);
				      	 adapter.setOnclickback(ServiceAddressBookActivity.this);
				      	 listView.setAdapter(adapter);
				      	 listView.setOnItemClickListener(ServiceAddressBookActivity.this);
				      	 try {
				      		DbHelper.getDB(ServiceAddressBookActivity.this).deleteAll(AddressBookBean.class);
							DbHelper.getDB(ServiceAddressBookActivity.this).saveAll(list);
						} catch (DbException e) {
							e.printStackTrace();
						}
				      	 return ;
					}
				}
			}
		}
	}

	@Override
	public void click(int id, final int position) {
//		if(id==R.id.service_addressbook_itemrecdelbt){
//			showLoadingDialog("正在删除...");
//			AppServer.getInstance().delAddressBook(accountInfo.getUid(), list.get(position).getAdsid(), new OnAppRequestListener() {
//				@Override
//				public void onAppRequest(int code, String message, Object obj) {
//		            if(code==0){	  
//		            	   list.remove(position);	
//		            	   adapter=new ServiceAdapter(ServiceAddressBookActivity.this,list,type);
//		       		       adapter.setOnclickback(ServiceAddressBookActivity.this);
//		             	   listView.setAdapter(adapter);  
//		            	   listView.setOnItemClickListener(ServiceAddressBookActivity.this);
//		            	   Toast.makeText(ServiceAddressBookActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
//		            }else{
//		            	Toast.makeText(ServiceAddressBookActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
//		            }
//		            cancelLoadingDialog();
//				}
//			});
//		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		list=DbHelper.QueryTData("select * from AddressBookBean", AddressBookBean.class);
		adapter=new ServiceAdapter(ServiceAddressBookActivity.this,list,AppConstants.ADDRESSBOOKMANAGEEDIT);
        adapter.setOnclickback(ServiceAddressBookActivity.this);
     	listView.setAdapter(adapter);
     	listView.setOnItemClickListener(ServiceAddressBookActivity.this);
		super.onNewIntent(intent);
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
        	  Intent  intent=new Intent(this,ServicePointExchangeActivity.class);
  		      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  		      startActivity(intent);
          }
		return super.onKeyDown(keyCode, event);
	}
}

