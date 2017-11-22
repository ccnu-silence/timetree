package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.adapter.ServiceAdapter.Onclickback;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.bean.Product;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.util.AppConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ServicePointExchangeActivity extends BaseActivity implements OnClickListener,OnItemClickListener,Onclickback{

	ListView listView ;	
	ServiceAdapter adapter;
	LinearLayout gridviewly;
	RelativeLayout poply;
	TextView titletextview;   
	ImageView leftbtn;     
	ImageView rightbtn; 
	List<Product> list=new ArrayList<Product>();
	LinearLayout adddressbookly;
	LinearLayout exchangehisly;
	LinearLayout exchangeexplianly;
	private Boolean istop=true;
	AccountInfo accountInfo;
	TextView pointtextview;
	int mypoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_pointexchangemain);
		accountInfo=AppServer.getInstance().getAccountInfo();
		FindViewById();
	    initData();	  
	    setOnClick();
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setText(R.string.service_pointsexchange);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);   
    	 leftbtn.setVisibility(View.VISIBLE); 
    	 rightbtn=(ImageView) findViewById(R.id.right_btn);
    	 rightbtn.setVisibility(View.VISIBLE);
    	 listView=(ListView) findViewById(R.id.service_pointexchange_listview);
    	 adddressbookly=(LinearLayout) findViewById(R.id.service_pointexchange_adddressbookly);
    	 exchangehisly=(LinearLayout) findViewById(R.id.service_pointexchange_exchangehisly);
    	 exchangeexplianly=(LinearLayout) findViewById(R.id.service_pointexchange_exchangeexplainly);
    	 poply=(RelativeLayout) findViewById(R.id.point_exchange_poply);
    	 pointtextview=(TextView) findViewById(R.id.service_pointexchange_pointtv);
     }
     
     public void initData()
     {
    	 showLoadingDialog("正在加载...");
    	 AppServer.getInstance().getCheckPoint(accountInfo.getUid(), new OnAppRequestListenerFriend() {
 			@Override
 			public void onAppRequestFriend(int code, String message, Object obj,
 					int nextid) {
 				if(code==0){
 					mypoint=Integer.parseInt(obj.toString());
 					pointtextview.setText(obj.toString());
 				}else{
 					showToast("积分获取失败"); 					
 				}
 			}
 		});
//    	 AppServer.getInstance().getProducts(accountInfo.getUid(), 11, -1, new OnAppRequestListenerFriend() {
//			@Override
//			public void onAppRequestFriend(int code, String message, Object obj,
//					int nextid) {
//			   if(code==0){
//				   list=(List<Product>) obj;
//			   }else{
//				   Toast.makeText(ServicePointExchangeActivity.this, "商品获取失败", Toast.LENGTH_SHORT).show();
//			   }
    	 		Product product=new Product();
    	 		list.add(product);
			     adapter=new ServiceAdapter(ServicePointExchangeActivity.this,list,AppConstants.SERIVCE_POINTEXCHANGEMAIN);
		    	 adapter.setOnclickback(ServicePointExchangeActivity.this);
		    	 listView.setAdapter(adapter); 
				 cancelLoadingDialog();
//			}
//		});    	
    	
    
     }
     
     public void setOnClick()
     {
    	 leftbtn.setOnClickListener(this);   
    	 rightbtn.setOnClickListener(this);
    	 adddressbookly.setOnClickListener(this);
    	 exchangehisly.setOnClickListener(this);
    	 exchangeexplianly.setOnClickListener(this);
    	 listView.setOnItemClickListener(this);
    	 poply.setOnClickListener(this);
     }

    
     
    

	@Override
	public void onClick(View v) {		
		Intent intent;	
	switch (v.getId()) {
		case R.id.left_btn:
		   /* intent=new Intent(ServicePointExchangeActivity.this,MainActivity.class);
			intent.putExtra("acivityType", MainActivity.TAB_TAG_SERVICE);
			startActivity(intent);*/
			finish();
			break;		
		case R.id.right_btn:
			swapPoply(istop);
			break;	
		case R.id.point_exchange_poply:
			swapPoply(istop);
			break;	
		case R.id.service_pointexchange_adddressbookly:
			 swapPoply(istop);
			 intent=new Intent(ServicePointExchangeActivity.this,ServiceAddressBookActivity.class);	
			 startActivity(intent);
				break;		
		case R.id.service_pointexchange_exchangehisly:
			 swapPoply(istop);
			 intent=new Intent(ServicePointExchangeActivity.this,ServicePointexchangeHisActivity.class);			 
			 startActivity(intent);			
				break;		
		case R.id.service_pointexchange_exchangeexplainly:
			 swapPoply(istop);
			 intent=new Intent(ServicePointExchangeActivity.this,ServicePointexchangeExplainActivity.class);			 
			 startActivity(intent);			
			 break;		
		default:
			break;
		}
	}

	public void swapPoply(Boolean  isflag)
	{
		if (istop) {
			poply.setVisibility(View.VISIBLE);
			istop=false;
		}else{				
			poply.setVisibility(View.GONE);
			istop=true;
		}		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {	
		
	}

	@Override
	public void click(int id, final int position) {
		if(list.get(position).getCost()>mypoint){
			showToast("对不起您积分不够兑换此商品"); 			
		}else{
            final List<AddressBookBean> addresslist=DbHelper.QueryTData("select * from AddressBookBean", AddressBookBean.class);
            if(addresslist.size()>0){
            	Intent intent=new Intent(ServicePointExchangeActivity.this,ServiceSelectAdrressBookActivity.class);
            	//intent.putExtra("pdtid", list.get(position).getPdtid());
            	intent.putExtra("pdtid", 392715);
            	startActivity(intent);
            }else{
            	 showLoadingDialog("正在加载");
            	 AppServer.getInstance().GetAllAddress(accountInfo.getUid(), new OnAppRequestListener() {
         			@Override
         			public void onAppRequest(int code, String message, Object obj) {
         		           if(code==0){
         		        	  List<AddressBookBean> querylist=(List<AddressBookBean>) obj;
         		        	  if(querylist!=null&&querylist.size()>0){   
         		        		 try {
									DbHelper.getDB(ServicePointExchangeActivity.this).saveAll(querylist);
								} catch (DbException e) {
									e.printStackTrace();
								}
         		        		 Intent intent=new Intent(ServicePointExchangeActivity.this,ServiceSelectAdrressBookActivity.class);
         		        		//intent.putExtra("pdtid", list.get(position).getPdtid());
         		            	 intent.putExtra("pdtid", 392715);
         		    			 startActivity(intent);
         		        	  }else{
         		        		Intent intent=new Intent(ServicePointExchangeActivity.this,ServicePointExchangeFillInforActivity.class);
         		       			intent.putExtra(AppConstants.ADDRESSFILLSTATE,AppConstants.SERIVCE_POINTEXCHANGEFILL);
         		       			startActivity(intent);
         		        	   }
         		           }else{
         		        		Intent intent=new Intent(ServicePointExchangeActivity.this,ServicePointExchangeFillInforActivity.class);
         		    			intent.putExtra(AppConstants.ADDRESSFILLSTATE,AppConstants.SERIVCE_POINTEXCHANGEFILL);
         		    			startActivity(intent);
         		           }
         		      	   cancelLoadingDialog();	
         			}
         		});
            }		
		}		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		return super.onKeyDown(keyCode, event);
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
