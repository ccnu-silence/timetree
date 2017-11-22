package com.yey.kindergaten.fragment;

import java.util.List;
import org.apache.http.impl.client.TunnelRefusedException;

import u.aly.bu;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.EditActivity;
import com.yey.kindergaten.activity.GetAddressActivity;
import com.yey.kindergaten.activity.MeInfoActivity;
import com.yey.kindergaten.activity.ServiceCodeCardActivity;
import com.yey.kindergaten.activity.ServiceCreateKinderSuccessActivity;
import com.yey.kindergaten.activity.ServiceGetgroupActivity;
import com.yey.kindergaten.activity.ServiceGroupMemberActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.db.DBManager;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.SlipButton;
import com.yey.kindergaten.widget.SlipButton.OnChangedListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KinderGroupInforFragment extends FragmentBase implements OnClickListener,OnChangedListener{

	@ViewInject(R.id.serviceedit_kindnumbertv) TextView knumbertv;
	@ViewInject(R.id.serviceedit_addcodetv) TextView kaddcodetv;
	@ViewInject(R.id.serviceedit_kindnametv) TextView ktopnametv;
	@ViewInject(R.id.serviceedit_knametv) TextView knametv;
	@ViewInject(R.id.serviceedit_groupmember) TextView kmembernum;
	@ViewInject(R.id.serviceedit_kindlocationtv) TextView klocationtv;
	@ViewInject(R.id.serviceedit_kindcontactertv) TextView kcontactpeopletv;
	@ViewInject(R.id.serviceedit_kindphonetv) TextView kphonetv;
	@ViewInject(R.id.serviceedit_kindmiaoshutv) TextView kmiaoshutv;
	@ViewInject(R.id.serviceedit_barcodely) LinearLayout kbarcodely;
	@ViewInject(R.id.tabBtn_middlely) LinearLayout tabbtnly;
	@ViewInject(R.id.service_invitationly)LinearLayout invily;
	@ViewInject(R.id.serviceedit_groupmemberly)LinearLayout lookmember;
	@ViewInject(R.id.slipButton)SlipButton slipButton;
	@ViewInject(R.id.serviceedit_addcodely)LinearLayout addcodely;
	@ViewInject(R.id.arrow_ivkind)ImageView arrowivk;
	@ViewInject(R.id.arrow_ivadderss)ImageView arrowdress;
	@ViewInject(R.id.arrow_ivcontact)ImageView arrowcontact;
	@ViewInject(R.id.arrow_ivphone)ImageView arrowphone;;
	@ViewInject(R.id.arrow_ivmiaoshu)ImageView arrowmiaoshu;;
	@ViewInject(R.id.group_miaoshu)TextView groupmiaoshu;
	GroupInfoBean groupInfoBean;
	Boolean iscandirt=false;
	AccountInfo accountInfo;
	private DBManager dbm;
	private SQLiteDatabase sqlite;
	String locationID="";
	String locationText="";
	Boolean isadd;
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
		View view=inflater.inflate(R.layout.service_editkinderinfo2, null);
		ViewUtils.inject(this, view); 
		return view;
	}
	
	public void initview()
    {
		String text="<font color='#111111' size='17sp'>群说明:</font><font color='#999999' size='17sp'>方便园长，老师和家长等够及时的交流分享和掌握群动态,电子文档共享,群发消息,只能通讯录,在线管理......</font>";
		groupmiaoshu.setText(Html.fromHtml(text));
    	if(groupInfoBean!=null){
    		knumbertv.setText("群号:"+groupInfoBean.getGnum());
    		kaddcodetv.setText(groupInfoBean.getJoincode());
    		ktopnametv.setText(groupInfoBean.getGname());
    		knametv.setText(groupInfoBean.getGname());
    		kmembernum.setText(groupInfoBean.getMembercount()+"人");
    		locationID=groupInfoBean.getLocation();
    		if(!groupInfoBean.getLocation().equals("0")){
    			klocationtv.setText(getlocationByid(groupInfoBean.getLocation()+""));
			}else{
				klocationtv.setText("");
			}
    		kcontactpeopletv.setText(groupInfoBean.getContact());
    		kphonetv.setText(groupInfoBean.getPhone());
    		kmiaoshutv.setText(groupInfoBean.getDesc());
    		slipButton.setSlip(true);
    		if(groupInfoBean.getJoinable()==1){   //可以加
    			slipButton.setCheck(true);   	
    			addcodely.setVisibility(View.VISIBLE);
    		}else{
    			slipButton.setCheck(false);
    			addcodely.setVisibility(View.GONE);
    			isadd=false;
    		}
    		if(accountInfo.getRole()==0){
    			tabbtnly.setVisibility(View.VISIBLE);
    			invily.setVisibility(View.VISIBLE); 
    			addcodely.setVisibility(View.VISIBLE);
    		}else{
    			tabbtnly.setVisibility(View.GONE);
    			invily.setVisibility(View.GONE);
    			addcodely.setVisibility(View.GONE);
    			arrowivk.setVisibility(View.GONE);
    			arrowdress.setVisibility(View.GONE);
    			arrowcontact.setVisibility(View.GONE);
    			arrowphone.setVisibility(View.GONE);
    			arrowmiaoshu.setVisibility(View.GONE);
    		}
    	}
    }
	
	  public void setonclick()
      {
		    invily.setOnClickListener(this);
		    kbarcodely.setOnClickListener(this);
		    lookmember.setOnClickListener(this);
		    slipButton.SetOnChangedListener(this);
		    if(accountInfo.getRole()==0){
		        knametv.setOnClickListener(this);
			    kaddcodetv.setOnClickListener(this);
			    klocationtv.setOnClickListener(this);
			    kcontactpeopletv.setOnClickListener(this);
			    kphonetv.setOnClickListener(this);
			    kmiaoshutv.setOnClickListener(this);
		    }
	  }
	 
		@Override
		public void onClick(View v) {
			 Intent intent;
			switch (v.getId()) {	   
				    case  R.id.serviceedit_knametv:
				    	if(accountInfo.getRole()!=0){
							return ;
						}
        		        intent=new Intent(getActivity(),EditActivity.class);
				        intent.putExtra("text", knametv.getText().toString());												
				        intent.putExtra(AppConstants.TITLE,"群名字");
				        intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
        		        startActivityForResult(intent, 5);
        		              break;
		        	case  R.id.serviceedit_addcodetv:
		        		if(accountInfo.getRole()!=0){
		    				return ;
		    			}
		        		intent=new Intent(getActivity(),EditActivity.class);
						intent.putExtra("text", kaddcodetv.getText().toString());												
						intent.putExtra(AppConstants.TITLE,"加入口令");
						intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
		        		startActivityForResult(intent, 0);
		        		break;
		        	case  R.id.serviceedit_kindlocationtv:
		        		if(accountInfo.getRole()!=0){
		    				return ;
		    			}
		        		intent=new Intent(getActivity(),GetAddressActivity.class);
						intent.putExtra("clickposition", 1);
						startActivityForResult(intent, 1);//requestCode;
		        		break;
		        	case  R.id.serviceedit_kindcontactertv:
		        		if(accountInfo.getRole()!=0){
		    				return ;
		    			}
		        		intent=new Intent(getActivity(),EditActivity.class);
						intent.putExtra("text", kcontactpeopletv.getText().toString());												
						intent.putExtra(AppConstants.TITLE,"联系人");
						intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
		        		startActivityForResult(intent, 2);
		        		break;
		        	case  R.id.serviceedit_kindphonetv:
		        		if(accountInfo.getRole()!=0){
		    				return ;
		    			}
		        		intent=new Intent(getActivity(),EditActivity.class);
						intent.putExtra("text", kphonetv.getText().toString());												
						intent.putExtra(AppConstants.TITLE,"联系电话");
						intent.putExtra(AppConstants.INPUTTYPE,AppConstants.PHONE);
		        		startActivityForResult(intent, 3);
		        		break;
		        	case  R.id.serviceedit_kindmiaoshutv:
		        		if(accountInfo.getRole()!=0){
		    				return ;
		    			}
		        		intent=new Intent(getActivity(),EditActivity.class);
						intent.putExtra("text", kmiaoshutv.getText().toString());												
						intent.putExtra(AppConstants.TITLE,"描述");
						intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
		        		startActivityForResult(intent, 4);
		        		break;
		  			case R.id.serviceedit_barcodely:
		  				intent=new Intent(getActivity(),ServiceCodeCardActivity.class);
		  				intent.putExtra(AppConstants.CODESTRING, "TIMES_TREE_QRCODE_2#"+groupInfoBean.getGnum());
		  				intent.putExtra(AppConstants.GROUPNAME,groupInfoBean.getGname());
		  				intent.putExtra(AppConstants.GROUPNUM,groupInfoBean.getGnum()+"");
		  				startActivity(intent);
		  				break;
		  			case R.id.serviceedit_groupmemberly:		  				
		  				intent =new Intent(getActivity(),ServiceGroupMemberActivity.class);
		  				intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
		  				intent.putExtra(AppConstants.GROUPNAME, groupInfoBean.getGname());
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
           if(requestCode==0){      //口令
				if(intent.getExtras()!=null){	
					String edittext=intent.getExtras().getString("edittext");
					kaddcodetv.setText(edittext);
					groupInfoBean.setJoincode(edittext);
					editGroupData();
				 }
		    }else if(requestCode==1){  //地区
		    	if(intent.getExtras()!=null){	
		    		String locationID=intent.getExtras().getString("locationID");
					String locationText=intent.getExtras().getString("locationText");
					klocationtv.setText(locationText);
					groupInfoBean.setLocation(locationID);
					editGroupData();
				 }
		    }else if(requestCode==2){  //联系人
		    	if(intent.getExtras()!=null){	
		    		String edittext=intent.getExtras().getString("edittext");
		    		kcontactpeopletv.setText(edittext);
		    		groupInfoBean.setContact(edittext);
		    		editGroupData();
				 }
		    }else if(requestCode==3){   //联系电话
		    	if(intent.getExtras()!=null){	
		    		String edittext=intent.getExtras().getString("edittext");
		    		kphonetv.setText(edittext);
		    		groupInfoBean.setPhone(edittext);
		    		editGroupData();
				 }
		    }else if(requestCode==4){  //群介绍
		    	if(intent.getExtras()!=null){	
		    		String edittext=intent.getExtras().getString("edittext");
		    		kmiaoshutv.setText(edittext);
		    		groupInfoBean.setDesc(edittext);
		    		editGroupData();
				 }
		    }else if(requestCode==5){  //群名字
		    	if(intent.getExtras()!=null){	
		    		String edittext=intent.getExtras().getString("edittext");
		    		knametv.setText(edittext);
		    		ktopnametv.setText(edittext);
		    		groupInfoBean.setGname(edittext);
		    		editGroupData();
				 }
		    }
	  }
		
		public void editGroupData(){
			GroupInfoServer.getInstance().editKinderGroupData(accountInfo.getUid(), groupInfoBean.getGnum(), groupInfoBean.getGname(), groupInfoBean.getLocation(), groupInfoBean.getJoincode(), groupInfoBean.getContact(), groupInfoBean.getPhone(), groupInfoBean.getDesc(), groupInfoBean.getJoinable(), new OnAppRequestListener() {
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
//						 Intent intent=new Intent(getActivity(),ServiceGetgroupActivity.class);
//						 intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//					  	 startActivity(intent);				  		
					 }else{
						 ShowToast("修改失败");							
					 }	
				}
			});		
		}
		
		 public String getlocationByid(String  id)
		    {  	
		    	if(id.equals("0")){
		    		return "";
		    	}
		        dbm = new DBManager(getActivity());
		 	 	dbm.openDatabase();
		 	 	sqlite = dbm.getDatabase();
		 	 	String sql = "select * from district where locationid='"+id+"'";  	 
		 	    Cursor cursor = sqlite.rawQuery(sql,null);  
		 	    cursor.moveToFirst();
		 	    AddressBean addressBean=new AddressBean();
		        List<AddressBean> list=DbHelper.getAList(addressBean, cursor);
		        if(list!=null&&list.size()>0){
		        	 String address="";
		        	 if(list.get(0).getProvince()!=null&&!list.get(0).getProvince().equals("")){
		        		 address=address+list.get(0).getProvince();
		        	 }
		        	 if(list.get(0).getCity()!=null&&!list.get(0).getCity().equals("")){
		        		 address=address+","+list.get(0).getCity();
		        	 }
		        	 if(list.get(0).getLocation()!=null&&!list.get(0).getLocation().equals("")){
		        		 address=address+","+list.get(0).getLocation();
		        	 }
		        	 return address;
//		        	 return list.get(0).getProvince()+","+list.get(0).getCity()+","+list.get(0).getCity();
		        }
		        return "";
		    }
		@Override
		public void OnChanged(boolean CheckState) {
			if(accountInfo.getRole()!=0){
				return ;
			}
			if(CheckState){
			    groupInfoBean.setJoinable(1);
				addcodely.setVisibility(View.VISIBLE);
				isadd=true;
				editGroupData();
			}else{
				groupInfoBean.setJoinable(0);
				addcodely.setVisibility(View.GONE);
				isadd=false;
				editGroupData();
			}
		}
      }
