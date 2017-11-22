package com.yey.kindergaten.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;

public class ServiceCreatKinderSelectActivity extends BaseActivity  {

	@ViewInject(R.id.left_btn)ImageView leftbtn;
	@ViewInject(R.id.header_title)TextView titletv;
    @ViewInject(R.id.createkindly)LinearLayout creatkindly;
    @ViewInject(R.id.createclassly)LinearLayout creatclassly;
    @ViewInject(R.id.creategenerally)LinearLayout creatgenerally;
    
	AccountInfo accountInfo;
	List<GroupInfoBean> sqllist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_creatkinderselect);
		ViewUtils.inject(this);
		accountInfo=AppServer.getInstance().getAccountInfo();
		sqllist=DbHelper.QueryTData("select * from GroupInfoBean ", GroupInfoBean.class);
		initview();
	}
	
	public void initview()
	{
		leftbtn.setVisibility(View.VISIBLE);
		titletv.setVisibility(View.VISIBLE);
		titletv.setText("创建群");
		if(accountInfo.getRole()==0){
			creatclassly.setVisibility(View.GONE);
		}else if(accountInfo.getRole()==1){
			creatkindly.setVisibility(View.GONE);
		}else{
			creatclassly.setVisibility(View.GONE);
			creatkindly.setVisibility(View.GONE);
		}
	}

	
	@OnClick({R.id.left_btn,R.id.createkindly,R.id.createclassly,R.id.creategenerally})
	public void onclick(View v){
		 Intent intent;
	  switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;	
		case R.id.createkindly:			
				Boolean isflag=true;
				for(int i=0;i<sqllist.size();i++){
					if(sqllist.get(i).getGtype()==1){
						isflag=false;
					    break;
					}
				}
				if(isflag){
					if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
								intent=new Intent(ServiceCreatKinderSelectActivity.this,ServiceCompleteInformationActivity.class);	
								startActivityForResult(intent, 0);
					}else{
						intent=new Intent(this,ServiceCreatGroupExplainActivity.class);
						intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEKINDER);
						startActivity(intent);
					}
				}else{
					showToast("已经拥有一个幼儿园群，不可以重新创建幼儿园群");
				}
			break;	
			
		case R.id.createclassly:
			if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
				intent=new Intent(ServiceCreatKinderSelectActivity.this,ServiceCompleteInformationActivity.class);	
				startActivityForResult(intent, 1);
			}else{
				intent=new Intent(this,ServiceCreatGroupExplainActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
				startActivity(intent);
			}			
			break;	
		case R.id.creategenerally:
		if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
				intent=new Intent(ServiceCreatKinderSelectActivity.this,ServiceCompleteInformationActivity.class);	
				startActivityForResult(intent, 2);
			}else{
				intent=new Intent(this,ServiceCreatGroupExplainActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEGENERALGROUP);
				startActivity(intent);
			}		
			break;	
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		super.onActivityResult(arg0, arg1, intent);
		if(intent!=null){
			if(arg0==0){
				intent=new Intent(this,ServiceCreatGroupExplainActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEKINDER);
				startActivity(intent);
			}else if(arg0==1){
				intent=new Intent(this,ServiceCreatGroupExplainActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
				startActivity(intent);
			}else{
				intent=new Intent(this,ServiceCreatGroupExplainActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEGENERALGROUP);
				startActivity(intent);
			}
		}
	}


}
