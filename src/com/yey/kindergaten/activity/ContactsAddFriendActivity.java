package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.List;

import com.igexin.push.core.bean.BaseAction;
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
import com.yey.kindergaten.fragment.FragmentBase;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.LoadingDialog;
import com.yey.kindergaten.widget.TabButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ContactsAddFriendActivity extends BaseActivity implements OnClickListener{

	TextView titletextview;   //通讯录
	ImageView leftbtn;
	EditText sreaEditText;
	Button   sreachbtn;
	LinearLayout saoly;
	LinearLayout sreachpuac;
	AppContext appcontext = null;
	AccountInfo accountInfo;
	int []state;
	LoadingDialog loadingDialog;
	List<PublicAccount> puaclist=new ArrayList<PublicAccount>();
	ContactPuacAdapter listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_addfriend);
		loadingDialog=new LoadingDialog(this, "正在加载");
		appcontext = AppContext.getInstance();
		accountInfo=AppServer.getInstance().getAccountInfo();
		FindViewById();
		setOnClick();	
	}
	
	
	 public void FindViewById()
     {
    	 titletextview=(TextView) findViewById(R.id.header_title);
    	 leftbtn=(ImageView) findViewById(R.id.left_btn);
    	 leftbtn.setVisibility(View.VISIBLE);
    	 titletextview.setText(R.string.contacts_sreachfriend);
    	 sreaEditText=(EditText) findViewById(R.id.addfriend_sreachet);
    	 sreachbtn=(Button) findViewById(R.id.addfriend_sreachbtn);
    	 saoly=(LinearLayout) findViewById(R.id.addfriend_sao);
    	 sreachpuac=(LinearLayout) findViewById(R.id.addfriend_sreachpuac);
     }
     
     
     public void setOnClick()
     {
    	leftbtn.setOnClickListener(this);
    	sreachbtn.setOnClickListener(this);
    	saoly.setOnClickListener(this);
    	sreachpuac.setOnClickListener(this);
     }
  

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.addfriend_sreachbtn:
			intent=new Intent(this,ContactsAddFriendResultActivity.class);
			if(sreaEditText.getText()==null||sreaEditText.getText().toString().equals("")){
				showToast("请输入查询条件");			
				return;
			}
			if(isNumeric(sreaEditText.getText().toString())){
				intent.putExtra("vtype", 0);
			}else{
				intent.putExtra("vtype", 0);
			}
			intent.putExtra("state", AppConstants.CONTACTS_ADDFRIEND);
			intent.putExtra("value", sreaEditText.getText().toString());
			startActivity(intent);
			break;
		case R.id.addfriend_sao:
			intent=new Intent(this,CaptureActivity.class);
			startActivity(intent);
			break;
		case R.id.addfriend_sreachpuac:
			intent=new Intent(this,ServiceTaskBookPuacActivity.class);
			intent.putExtra(AppConstants.STATE, AppConstants.ADDFRIEND);
			startActivity(intent);
			break;

		default:
			break;
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
	
	 public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	 }
	 public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	 }
	
}
