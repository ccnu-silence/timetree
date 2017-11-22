package com.yey.kindergaten.activity;

import java.util.List;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.util.AppConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ServicePointExchangeFillInforActivity extends BaseActivity implements OnClickListener{

	
	TextView titletextview;   
	ImageView leftbtn;       
	TextView righttext;
	List<String> list;
	String type="";   //eidt编辑地址本状态    fill填写收信人状态     传递的参数有;edit,selectaddressbook,fill
	EditText receiveret;
	EditText addresset;
	EditText phoneet;
	EditText codeet;
	AccountInfo accountInfo;
	AddressBookBean addressBookBean;
	LinearLayout delly;
	int pdtid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_pointexchange_fillinfor);
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(getIntent().getExtras()!=null){
			type=getIntent().getExtras().getString(AppConstants.ADDRESSFILLSTATE);
			if(type.equals(AppConstants.SERIVCE_POINTEXCHANGEDIT)||type.equals(AppConstants.SERIVCE_POINTEXCHANGMANAGEEDIT)){
			   addressBookBean=(AddressBookBean) getIntent().getExtras().getSerializable(AppConstants.ADDRESSBOOKBEAN);
			}
		}
		FindViewById();
	    setOnClick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);	 
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);   
    	 leftbtn.setVisibility(View.VISIBLE);  
    	 righttext=(TextView) findViewById(R.id.right_tv);
    	 righttext.setVisibility(View.VISIBLE);
    	 righttext.setText("完成");
    	 righttext.setOnClickListener(this);
    	 receiveret=(EditText) findViewById(R.id.service_pointexchange_fillinfor_et1);
    	 addresset=(EditText) findViewById(R.id.service_pointexchange_fillinfor_et2);
    	 phoneet=(EditText) findViewById(R.id.service_pointexchange_fillinfor_et3);
    	 codeet=(EditText) findViewById(R.id.service_pointexchange_fillinfor_et4);
    	 if(type.equals(AppConstants.SERIVCE_POINTEXCHANGEDIT)||type.equals(AppConstants.SERIVCE_POINTEXCHANGMANAGEEDIT)){
    		 titletextview.setText(R.string.service_editaddressbook);    		 
        	  if(addressBookBean!=null){
  	    	    receiveret.setText(addressBookBean.getReceiver());
  		        addresset.setText(addressBookBean.getAddress());
  		    	phoneet.setText(addressBookBean.getPhone());
  		    	codeet.setText(addressBookBean.getCode());
  	          }
        	  righttext.setText("修改");
    	 }else{
    		 titletextview.setText(R.string.service_fillrecievinformation); 
    	 }    
    	 delly=(LinearLayout) findViewById(R.id.service_addressbook_itemrecdelly);
    	 if(type.equals(AppConstants.SERIVCE_POINTEXCHANGMANAGEEDIT)){
    		 delly.setVisibility(View.VISIBLE);
    	 }
     }

     public void setOnClick()
     {
    	 leftbtn.setOnClickListener(this);   
    	 righttext.setOnClickListener(this);  
    	 delly.setOnClickListener(this);  
     }

	@Override
	public void onClick(View v) {
		Intent intent;
	switch (v.getId()) {
		case R.id.left_btn:
			 if(type.equals(AppConstants.SERIVCE_POINTEXCHANGEDIT)||type.equals(AppConstants.SERIVCE_POINTEXCHANGMANAGEEDIT)){
				    intent=new Intent();				
				    setResult(0, intent);
				    ServicePointExchangeFillInforActivity.this.finish();
			 }else{
				  this.finish();
			 }
			break;	
	 	case R.id.right_tv:
	 		 if(type.equals(AppConstants.SERIVCE_POINTEXCHANGEDIT)||type.equals(AppConstants.SERIVCE_POINTEXCHANGMANAGEEDIT)){	 
	 			  final String receiver=receiveret.getText().toString().trim();
				  final String address=addresset.getText().toString().trim();
				  final String phone=phoneet.getText().toString().trim();
				  final String code=codeet.getText().toString().trim();
				  if(receiver==null||receiver.equals("")){
					  showToast("请填写收件人姓名");					  
					  return;
				  }else if(address==null||address.equals("")){
					  showToast("请填写收件人地址");
					  return;
				  }else if(phone==null||phone.equals("")){
					  showToast("请填写收件人电话");				
					  return;
				  }else if(code==null||code.equals("")){
					  showToast("请填写收件人邮编");
					  return;
				  }else{
					  
				  }
				  showLoadingDialog("正在修改...");
	 			  AppServer.getInstance().updateAddressBook(accountInfo.getUid(), addressBookBean.getAdsid(),
	 					receiver, address, phone, code, new OnAppRequestListener() {
							@Override
							public void onAppRequest(int requestcode, String message, Object obj) {
								if(requestcode==0){
									showToast("修改成功");
									addressBookBean.setReceiver(receiver);
									addressBookBean.setAddress(address);
									addressBookBean.setPhone(phone);
									addressBookBean.setCode(code+"");							
									Intent intent=new Intent();
									Bundle bundle=new Bundle();
									bundle.putSerializable(AppConstants.ADDRESSBOOKBEAN, addressBookBean);
									intent.putExtras(bundle);
								    setResult(0, intent);
								    ServicePointExchangeFillInforActivity.this.finish();
								}else{
									Toast.makeText(ServicePointExchangeFillInforActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
								}
								cancelLoadingDialog();
							}
						});
	 		 }else{
	 			  final String receiver=receiveret.getText().toString().trim();
				  final String address=addresset.getText().toString().trim();
				  final String phone=phoneet.getText().toString().trim();
				  final String code=codeet.getText().toString().trim();
				  if(receiver==null||receiver.equals("")){
					  showToast("请填写收件人姓名");						 
					  return;
				  }else if(address==null||address.equals("")){
					  showToast("请填写收件人地址");	
					  return;
				  }else if(phone==null||phone.equals("")){
					  showToast("请填写收件人电话");	
					  return;
				  }else if(code==null||code.equals("")){
					  showToast("请填写收件人邮编");	
					  return;
				  }else{
					  AppServer.getInstance().SaveAddress(accountInfo.getUid(), receiver, address, phone, code, new OnAppRequestListener() {
						@Override
						public void onAppRequest(int requestcode, String message, Object obj) {
							if(requestcode==0){
								 AddressBookBean abook=new AddressBookBean();
								 abook.setReceiver(receiver);
								 abook.setAddress(address);
								 abook.setPhone(phone);			
								 abook.setCode(code+"");
								 try {
									DbHelper.getDB(ServicePointExchangeFillInforActivity.this).save(abook);
									if(type.equals(AppConstants.SERIVCE_POINTEXCHANGEMANAGEFILL)){
										 Intent intent=new Intent(ServicePointExchangeFillInforActivity.this,ServiceAddressBookActivity.class);							 
										 startActivity(intent);
									}else{
										Intent intent=new Intent(ServicePointExchangeFillInforActivity.this,ServiceSelectAdrressBookActivity.class);
										startActivity(intent);
									}
								  
								 } catch (DbException e) {
									e.printStackTrace();
								}
								 Toast.makeText(ServicePointExchangeFillInforActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
							}else{
								 Toast.makeText(ServicePointExchangeFillInforActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
							}					
						}
					});
				  }
	 		   }				
				break;	
		case R.id.service_pointexchange_fillinfor_selectbtn:
//			 intent=new Intent(ServicePointExchangeFillInforActivity.this,ServiceAddressBookActivity.class);	
//			 intent.putExtra(AppConstants.ADDRESSBOOSTATE, AppConstants.SERIVCE_ADRESSBOOKSELECT);
//			 startActivityForResult(intent, 2);
				break;	
		case R.id.service_addressbook_itemrecdelly:
			showLoadingDialog("正在删除...");
			AppServer.getInstance().delAddressBook(accountInfo.getUid(), addressBookBean.getAdsid(), new OnAppRequestListener() {
				@Override
				public void onAppRequest(int code, String message, Object obj) {
		            if(code==0){	  
		            	try {	
							DbHelper.getDB(ServicePointExchangeFillInforActivity.this).delete(AddressBookBean.class, WhereBuilder.b("adsid", "=", addressBookBean.getAdsid()));
							Intent intent=new Intent(ServicePointExchangeFillInforActivity.this,ServiceAddressBookActivity.class);							 
							startActivity(intent);
		            	} catch (DbException e) {					
							e.printStackTrace();
						}
		            	showToast("删除成功");	
		            }else{
		            	showToast("删除失败");	
		            }
		            cancelLoadingDialog();
				}
			});
			break;	
		default:
			break;
		}
	}
       
      @Override
	  protected void onActivityResult(int arg0, int arg1, Intent arg2) {
	    	super.onActivityResult(arg0, arg1, arg2);
	    	if(arg2!=null){
	    		AddressBookBean addressBookBean=(AddressBookBean) arg2.getExtras().getSerializable(AppConstants.ADDRESSBOOKBEAN);
	 	       if(addressBookBean!=null){
	 	    	    receiveret.setText(addressBookBean.getReceiver());
	 		        addresset.setText(addressBookBean.getAddress());
	 		    	phoneet.setText(addressBookBean.getPhone());
	 		    	codeet.setText(addressBookBean.getCode());
	 	       }
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
    	  if(type.equals(AppConstants.SERIVCE_POINTEXCHANGEDIT)||type.equals(AppConstants.SERIVCE_POINTEXCHANGMANAGEEDIT)){
			    Intent intent=new Intent();				
			    setResult(0, intent);
			    ServicePointExchangeFillInforActivity.this.finish();
		 }else{
			  this.finish();
		 }
    	return super.onKeyDown(keyCode, event);
    }
}


