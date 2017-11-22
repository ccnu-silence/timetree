package com.yey.kindergaten.fragment;

import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.EditActivity;
import com.yey.kindergaten.activity.ServiceCodeCardActivity;
import com.yey.kindergaten.activity.ServiceCreateKinderSuccessActivity;
import com.yey.kindergaten.activity.ServiceGetgroupActivity;
import com.yey.kindergaten.activity.ServiceGroupMemberActivity;
import com.yey.kindergaten.activity.ServiceSelectGradeActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.SlipButton;
import com.yey.kindergaten.widget.SlipButton.OnChangedListener;
import com.yey.kindergaten.widget.TabButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ClassGroupInfoFragment extends FragmentBase implements OnClickListener,OnChangedListener{
	@ViewInject(R.id.serviceedit_classnametv) TextView ctopnametv;
	@ViewInject(R.id.serviceedit_nametv) TextView cnametv;
	@ViewInject(R.id.serviceedit_classnumtv) TextView cnum;;
	@ViewInject(R.id.serviceedit_addcodetv) TextView addcodetv;
	@ViewInject(R.id.serviceedit_kindnametv) TextView kindernametv;
	@ViewInject(R.id.serviceedit_classteachertv) TextView teachernametv;
	@ViewInject(R.id.serviceedit_classmembertv) TextView membernumtv;
	@ViewInject(R.id.serviceedit_kindmiaoshutv) TextView kmiaoshutv;
	@ViewInject(R.id.serviceedit_gradev)TextView gradetv;
	@ViewInject(R.id.serviceedit_barcodely) LinearLayout barcodely;
	@ViewInject(R.id.tabBtn_middlely) LinearLayout tabbtnly;
	@ViewInject(R.id.service_invitationly)LinearLayout invily;
	@ViewInject(R.id.serviceedit_groupmemberly)LinearLayout lookmember;
	@ViewInject(R.id.serviceedit_addcodely)LinearLayout  addcodely;
	@ViewInject(R.id.slipButton)SlipButton slipButton;
	@ViewInject(R.id.arrowiv_name)ImageView arrowiv_name;
	@ViewInject(R.id.arrowiv_code)ImageView arrowiv_code;
	@ViewInject(R.id.arrowiv_miaoshu)ImageView arrowiv_miaoshu;
	@ViewInject(R.id.arrowiv_class)ImageView arrowiv_class;
	@ViewInject(R.id.group_miaoshu)TextView groupmiaoshu;
	GroupInfoBean groupInfoBean;
	AccountInfo accountInfo;
	String gradename="";
    int gradeid;
    String  joincode="";
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		accountInfo=AppServer.getInstance().getAccountInfo();
    	if(getArguments()!=null){
    		groupInfoBean=(GroupInfoBean) getArguments().getSerializable(AppConstants.GROUPBEAN);
    	}
    	initview();
		setonclick();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view=inflater.inflate(R.layout.service_editclassinfo2, null);
		ViewUtils.inject(this, view); 
		return view;
	}

	public void initview()
    {
		String text="<font color='#111111' size='17sp'>群说明:</font><font color='#999999' size='17sp'>方便园长，老师和家长等够及时的交流分享和掌握群动态,电子文档共享,群发消息,只能通讯录,在线管理......</font>";
		groupmiaoshu.setText(Html.fromHtml(text));
    	if(groupInfoBean!=null){
    		ctopnametv.setText(groupInfoBean.getGname());
    		cnametv.setText(groupInfoBean.getGname());
    		cnum.setText("群号:"+groupInfoBean.getGnum());
    		addcodetv.setText(groupInfoBean.getJoincode());
    		kindernametv.setText(groupInfoBean.getGarten());
    		teachernametv.setText(groupInfoBean.getTeacher());
    		membernumtv.setText(groupInfoBean.getMembercount()+"人");
    		kmiaoshutv.setText(groupInfoBean.getDesc());
    		gradetv.setText(groupInfoBean.getGrade());
    		slipButton.setSlip(true);
    		if(groupInfoBean.getJoinable()==1){   //可以加
    			slipButton.setCheck(true);
    			addcodely.setVisibility(View.VISIBLE);
    		}else{
    			slipButton.setCheck(false);
    			addcodely.setVisibility(View.GONE);
    		}
			gradeid=groupInfoBean.getGid();
    		if(accountInfo.getRole()==1){
    			tabbtnly.setVisibility(View.VISIBLE);
    			addcodely.setVisibility(View.VISIBLE);
    		}else{
    			tabbtnly.setVisibility(View.GONE);
    			addcodely.setVisibility(View.GONE);
    			arrowiv_name.setVisibility(View.GONE);
    			arrowiv_miaoshu.setVisibility(View.GONE);
    			arrowiv_code.setVisibility(View.GONE);
    			arrowiv_class.setVisibility(View.GONE);
    		}
    	}
    }
	
	 public void setonclick()
      {
		    barcodely.setOnClickListener(this);
		    invily.setOnClickListener(this);
		    lookmember.setOnClickListener(this);
		    slipButton.SetOnChangedListener(this);
		    
			if(accountInfo.getRole()==1){
		      cnametv.setOnClickListener(this);
		      addcodetv.setOnClickListener(this);
		      gradetv.setOnClickListener(this);
		      kmiaoshutv.setOnClickListener(this);
			}
	  }

	
	@Override
	public void onClick(View v) {
		 Intent intent;
		switch (v.getId()) {	  
		  case R.id.serviceedit_nametv:	  	
			  if(accountInfo.getRole()!=1){
					return ;
				}
			    intent=new Intent(getActivity(),EditActivity.class);
		        intent.putExtra("text", cnametv.getText().toString());												
		        intent.putExtra(AppConstants.TITLE,"群名字");
		        intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
		        startActivityForResult(intent, 0);
				break;
		  case R.id.serviceedit_addcodetv:	
			  if(accountInfo.getRole()!=1){
					return ;
				}
			    intent=new Intent(getActivity(),EditActivity.class);
		        intent.putExtra("text", addcodetv.getText().toString());												
		        intent.putExtra(AppConstants.TITLE,"加入群口令");
		        intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
		        startActivityForResult(intent, 1);
				break;
		  case R.id.serviceedit_gradev:	  
			  if(accountInfo.getRole()!=1){
					return ;
				}
			   intent=new Intent(getActivity(),ServiceSelectGradeActivity.class);
			   startActivityForResult(intent,2);//requestCode;
				break;
		  case R.id.serviceedit_kindmiaoshutv:	  	
			  if(accountInfo.getRole()!=1){
					return ;
				}
			    intent=new Intent(getActivity(),EditActivity.class);
		        intent.putExtra("text",kmiaoshutv.getText().toString());												
		        intent.putExtra(AppConstants.TITLE,"描述");
		        intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
		        startActivityForResult(intent, 3);
				break;
		
	  	  case R.id.serviceedit_groupmemberly:	  				
		  			 intent =new Intent(getActivity(),ServiceGroupMemberActivity.class);
		  			 intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
		  			 intent.putExtra(AppConstants.GROUPNAME, groupInfoBean.getGname());
		  			 startActivity(intent);
	  				break;
	  		case R.id.serviceedit_classteacherly:
	  			   teachernametv.requestFocus();
	  				break;
	  		case R.id.serviceedit_barcodely:
	  			intent=new Intent(getActivity(),ServiceCodeCardActivity.class);
  				intent.putExtra(AppConstants.CODESTRING, "TIMES_TREE_QRCODE_2#"+groupInfoBean.getGnum());
  				intent.putExtra(AppConstants.GROUPNAME,groupInfoBean.getGname());
  				intent.putExtra(AppConstants.GROUPNUM,groupInfoBean.getGnum()+"");
  				startActivity(intent);
	  				break;
	  		case R.id.service_invitationly:
	  		  intent=new Intent(getActivity(),ServiceCreateKinderSuccessActivity.class);
	        	 Bundle bundle=new Bundle();
	        	 bundle.putSerializable(AppConstants.GROUPINFOBEAN, groupInfoBean);
	        	 bundle.putString(AppConstants.STATE, AppConstants.SHARE);			      
	        	 intent.putExtras(bundle);
	        	 startActivity(intent);
	  			  break;
	  			default:
	  				break;
	         }
	      }
	
	@Override
    public  void onActivityResult(int requestCode, int resultCode, Intent intent) {
		 super.onActivityResult(requestCode, resultCode, intent);
         if(requestCode==0){      
				if(intent.getExtras()!=null){			
					String edittext=intent.getExtras().getString("edittext");
			    	cnametv.setText(edittext);
			    	ctopnametv.setText(edittext);
			    	groupInfoBean.setGname(edittext);
			    	editGroupData();
				 }
		 }else if(requestCode==1){
				  if(intent.getExtras()!=null){		
					String edittext=intent.getExtras().getString("edittext");
			    	addcodetv.setText(edittext);
			    	groupInfoBean.setJoincode(edittext);
			    	editGroupData();
				  }
		 }else if(requestCode==2){
			  if(intent.getExtras()!=null){			
				    gradeid=intent.getExtras().getInt(AppConstants.GRADEID);				
					gradename=intent.getExtras().getString(AppConstants.GRADENAME);
					gradetv.setText(gradename);
					groupInfoBean.setGrade(gradeid+"");
					editGroupData();
				 }
		  }else if(requestCode==3){
			  if(intent.getExtras()!=null){			
				  String edittext=intent.getExtras().getString("edittext");
		    	  kmiaoshutv.setText(edittext);
		    	  groupInfoBean.setDesc(edittext);
		    	  editGroupData();
				 }
		    }
	  }

	public void editGroupData(){
		GroupInfoServer.getInstance().editClassGroupData(accountInfo.getUid(), groupInfoBean.getGnum(), groupInfoBean.getGname(), groupInfoBean.getGrade(), groupInfoBean.getJoincode(), groupInfoBean.getDesc(), groupInfoBean.getJoinable(), new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				 if(code==0){
					 ShowToast("修改成功");							
					 List<GroupInfoBean> list=DbHelper.QueryTData("select * from GroupInfoBean", GroupInfoBean.class);
					 for(int i=0;i<list.size();i++){
						 if(list.get(i).getGnum()==groupInfoBean.getGnum()){
							 try {
								DbHelper.getDB(getActivity()).delete(GroupInfoBean.class, WhereBuilder.b("gnum", "=", groupInfoBean.getGnum()));
								DbHelper.getDB(getActivity()).save(groupInfoBean);
							} catch (DbException e) {								
								e.printStackTrace();
							}
						 }
					 }
//					 Intent intent=new Intent(getActivity(),ServiceGetgroupActivity.class);
//					 intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//				  	 startActivity(intent);							
				 }else{
					 ShowToast("修改失败");					
				 }
			}
		});
	}
	
	@Override
	public void OnChanged(boolean CheckState) {
		if(CheckState){
			addcodely.setVisibility(View.VISIBLE);
			groupInfoBean.setJoinable(1);
			editGroupData();
		}else{
			addcodely.setVisibility(View.GONE);
			groupInfoBean.setJoinable(0);
			editGroupData();
		}
		
	}


}
