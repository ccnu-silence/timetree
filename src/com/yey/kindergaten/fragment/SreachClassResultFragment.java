package com.yey.kindergaten.fragment;

import java.util.List;

import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.EditActivity;
import com.yey.kindergaten.activity.ServiceCompleteInformationActivity;
import com.yey.kindergaten.activity.ServiceGetgroupActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SreachClassResultFragment extends FragmentBase implements OnClickListener{

	LinearLayout addly;
	TextView classnametv;
	TextView classnumtv;
	TextView gradenametv;
	TextView Kinder;
	TextView kteacher;
	TextView maioshutv;
	GroupInfoBean groupInfoBean;
	AccountInfo accountInfo;
	String addvalue="";
	List<GroupInfoBean> sqllist;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getArguments()!=null){
			groupInfoBean=(GroupInfoBean) getArguments().getSerializable(AppConstants.GROUPBEAN);
		}
		accountInfo=AppServer.getInstance().getAccountInfo();
		sqllist=DbHelper.QueryTData("select * from GroupInfoBean ", GroupInfoBean.class);
		 addly.setOnClickListener(this);   	
    	 if(groupInfoBean!=null){
    		 classnametv.setText(groupInfoBean.getGname());
    		 classnumtv.setText(groupInfoBean.getGnum()+"");
    		 gradenametv.setText(groupInfoBean.getGrade());
    		 Kinder.setText(groupInfoBean.getGarten());
    		 kteacher.setText(groupInfoBean.getTeacher());   
    		 maioshutv.setText(groupInfoBean.getDesc());  	
    	 }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View view=inflater.inflate(R.layout.sreachclassresultfragment2, null);
		 addly=(LinearLayout)view.findViewById(R.id.sreachkinder_addtokinderly);
		 classnametv=(TextView) view.findViewById(R.id.sreachclass_classnametv);
		 classnumtv=(TextView) view.findViewById(R.id.sreachclass_classnumtv);
		 gradenametv=(TextView) view.findViewById(R.id.sreachclass_gradenametv);
		 Kinder=(TextView) view.findViewById(R.id.sreachclass_kindertv);
		 kteacher=(TextView) view.findViewById(R.id.sreachclass_classteachertv);
		 maioshutv=(TextView) view.findViewById(R.id.sreachclass_miaoshutv);
    	 return view;
	}
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sreachkinder_addtokinderly:
			 if(groupInfoBean.getJoinable()==0){
				 ShowToast("对不起，此群暂时不对外开放,不能加入");	 		
	 		     return;
				 } 
				if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
//				new AlertDialog.Builder(getActivity()).setTitle("资料不足，必须完善个人信息才可以加入幼儿园").setPositiveButton("是", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
						Intent intent=new Intent(getActivity(),ServiceCompleteInformationActivity.class);	
						startActivityForResult(intent, 0);
//					}
//				}).setNegativeButton("否", new DialogInterface.OnClickListener() {					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {						
//					}
//				}).show();
			}else{
				if(groupInfoBean.getJoincode()==null||groupInfoBean.getJoincode().equals("")){
					showLoadingDialog("正在加入");
					GroupInfoServer.getInstance().addToGroup(accountInfo.getUid(), groupInfoBean.getGnum(), addvalue, new OnAppRequestListener() {
						@Override
						public void onAppRequest(int code, String message, Object obj) {
							if(code==0){
								 ShowToast("加入成功");	
								try {
									DbHelper.getDB(getActivity()).save(groupInfoBean);
								} catch (DbException e) {										
									e.printStackTrace();
								}
								AppServer.getInstance().getContacts(accountInfo.getUid(), new OnAppRequestListener() {
									@Override
									public void onAppRequest(int code, String message, Object obj) {
										if(code==0){
											Contacts contacts  =(Contacts) obj;
											AppContext.getInstance().setContacts(contacts);
										}
										cancelLoadingDialog();
										Intent intent=new Intent(getActivity(),ServiceGetgroupActivity.class);
										intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
								  		startActivity(intent);
									}
								});
							}else{
								 ShowToast("加入失败");	
							}
							
						}
					});
				}else{
					Intent intent=new Intent(getActivity(),EditActivity.class);
					intent.putExtra("clickposition", 0);
					intent.putExtra("text", addvalue);
					intent.putExtra(AppConstants.TITLE, "请输入邀请码");
					startActivityForResult(intent, 3);
				}
				
			}
			break	;
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);	
			if(requestCode==0){
				if(groupInfoBean.getJoincode()==null||groupInfoBean.getJoincode().equals("")){
					showLoadingDialog("正在处理...");
					GroupInfoServer.getInstance().addToGroup(accountInfo.getUid(), groupInfoBean.getGnum(), addvalue, new OnAppRequestListener() {
						@Override
						public void onAppRequest(int code, String message, Object obj) {
							if(code==0){
								 ShowToast("加入成功");	
								try {
									DbHelper.getDB(getActivity()).save(groupInfoBean);
								} catch (DbException e) {										
									e.printStackTrace();
								}
								AppServer.getInstance().getContacts(accountInfo.getUid(), new OnAppRequestListener() {
									@Override
									public void onAppRequest(int code, String message, Object obj) {
										if(code==0){
											Contacts contacts  =(Contacts) obj;
											AppContext.getInstance().setContacts(contacts);
										}
										cancelLoadingDialog();
										Intent intent=new Intent(getActivity(),ServiceGetgroupActivity.class);
										intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
								  		startActivity(intent);
								  	
									}
								});
								
							}else{
								 ShowToast("加入失败");	
							}
							
						}
					});
				}else{
				    intent=new Intent(getActivity(),EditActivity.class);
					intent.putExtra("clickposition", 0);
					intent.putExtra("text", addvalue);
					intent.putExtra(AppConstants.TITLE, "请输入邀请码");
					startActivityForResult(intent, 3);
				}
			}else if(requestCode==3){
				if(intent.getExtras()!=null){
					addvalue=intent.getExtras().getString("edittext");
					if(!addvalue.equals(groupInfoBean.getJoincode())){
						 ShowToast("对不起,输入的邀请码有误");	
						 return;
					}else{
						showLoadingDialog("正在处理...");
						GroupInfoServer.getInstance().addToGroup(accountInfo.getUid(), groupInfoBean.getGnum(), addvalue, new OnAppRequestListener() {
							@Override
							public void onAppRequest(int code, String message, Object obj) {
								if(code==0){
									 ShowToast("加入成功");	
									try {
										DbHelper.getDB(getActivity()).save(groupInfoBean);
									} catch (DbException e) {										
										e.printStackTrace();
									}
									AppServer.getInstance().getContacts(accountInfo.getUid(), new OnAppRequestListener() {
										@Override
										public void onAppRequest(int code, String message, Object obj) {
											if(code==0){											
												List<Teacher> teachers;
												List<Classe> classeslist;
												List<PublicAccount> pms;
												List<Friend> friends;
												Contacts contacts = (Contacts)obj;
												if(contacts !=null){
													appcontext.setContacts(contacts);
													teachers=contacts.getTeachers();
													classeslist=contacts.getClasses();
													pms=contacts.getPublics();					  
													friends=contacts.getFriends();		
													try {
														if(DbHelper.getDB(AppContext.getInstance()).tableIsExist(Teacher.class)){
															DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
														}
														if(DbHelper.getDB(AppContext.getInstance()).tableIsExist(Children.class)){
															DbHelper.getDB(AppContext.getInstance()).deleteAll(Children.class);
														}	
														if(DbHelper.getDB(AppContext.getInstance()).tableIsExist(Friend.class)){
															DbHelper.getDB(AppContext.getInstance()).deleteAll(Friend.class);
														}
														if(DbHelper.getDB(AppContext.getInstance()).tableIsExist(PublicAccount.class)){
															DbHelper.getDB(AppContext.getInstance()).deleteAll(PublicAccount.class);
														}
														DbHelper.getDB(AppContext.getInstance()).saveAll(teachers);
														DbHelper.getDB(AppContext.getInstance()).saveAll(classeslist);
														DbHelper.getDB(AppContext.getInstance()).saveAll(friends);
														DbHelper.getDB(AppContext.getInstance()).saveAll(pms);
													} catch (DbException e) {
														e.printStackTrace();
													}
												}
										    }							
											cancelLoadingDialog();
											Intent intent=new Intent(getActivity(),ServiceGetgroupActivity.class);
											intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
									  		startActivity(intent);
										}
									});
									
								}else{
									 ShowToast("加入失败");	
								}
								
							}
						});
					}
				}	
			}else{
				
			}
	}
}
