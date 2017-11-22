package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.fragment.ContactsBarcodeSearchFragment;
import com.yey.kindergaten.fragment.ContactsKinderNumSreachFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.TabButton;

public class ServiceAddKinderActivity extends BaseActivity implements OnClickListener{

	
	TextView titletextview;   //通讯录
	ImageView leftbtn;
	Button barsreachbtn;
	TextView   sreachbtn;
	AppContext appcontext = null;
	AccountInfo accountInfo;
	TextView sreachtv;
	String state="";
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.contacts_addkinder);
		appcontext = AppContext.getInstance();
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(getIntent().getExtras()!=null){
			state=getIntent().getExtras().getString(AppConstants.STATE);
		}
		FindViewById();
		setOnClick();	
	}
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 titletextview.setText("加入群");  
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);
    	 leftbtn.setVisibility(View.VISIBLE);
    	 barsreachbtn=(Button)findViewById(R.id.addkind_barsreach);
    	 sreachbtn=(TextView) findViewById(R.id.contact_addkinder_sreachbt);   	 
    	 sreachtv=(TextView) findViewById(R.id.contact_addkind_edittext);
    		
     }
     public void setOnClick()
     {
    	leftbtn.setOnClickListener(this);
    	barsreachbtn.setOnClickListener(this);
    	sreachbtn.setOnClickListener(this);
     }
  
   
     
     @Override
 	public void onClick(View v) {
    	 Intent intent;
 		switch (v.getId()) {
 		case R.id.left_btn:		
 			if(state.equals(AppConstants.TASKMAIN)){
 				this.finish();
 			}else{
 				intent=new Intent(this,ServiceGetgroupActivity.class);
 	 		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	 		    startActivity(intent);
 			}
 			break;		
 		case R.id.addkind_barsreach:
			 intent=new Intent(this,CaptureActivity.class);		
			 intent.putExtra("state", AppConstants.CONTACTS);
			 startActivity(intent);			 
			 break;
 		case R.id.contact_addkinder_sreachbt:
 			String text=sreachtv.getText().toString();
 		    intent=new Intent(this,ServiceSreachKinderResultActivity.class);
 		    intent.putExtra(AppConstants.SREACHGROUPVALUE, text);
 			startActivity(intent);
 			break;
 		default:
 			break;
 		}
 		
 	}

     @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode==KeyEvent.KEYCODE_BACK){
    		if(state.equals(AppConstants.TASKMAIN)){
 				this.finish();
 			}else{
 				Intent intent=new Intent(this,ServiceGetgroupActivity.class);
 	 		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	 		    startActivity(intent);
 			}
    	}
    	return super.onKeyDown(keyCode, event);
    }
}
