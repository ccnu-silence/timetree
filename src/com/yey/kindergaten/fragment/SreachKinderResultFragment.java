package com.yey.kindergaten.fragment;


import java.util.List;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.EditActivity;
import com.yey.kindergaten.activity.ServiceCompleteInformationActivity;
import com.yey.kindergaten.activity.ServiceCreateKinderActivity;
import com.yey.kindergaten.activity.ServiceGetgroupActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DBManager;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SreachKinderResultFragment extends FragmentBase implements OnClickListener{
	LinearLayout addly;
	TextView kindernametv;
	TextView kindernumtv;
	TextView klocationtv;
	TextView kcontactspeopletv;
	TextView kcontactsphonetv;
	TextView kmiaoshu;
	GroupInfoBean groupInfoBean;
	AccountInfo accountInfo;
	String addvalue="";
	List<GroupInfoBean> sqllist;
	private DBManager dbm;
	private SQLiteDatabase sqlite;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(getArguments()!=null){
			groupInfoBean=(GroupInfoBean) getArguments().getSerializable(AppConstants.GROUPBEAN);
		}
		sqllist=DbHelper.QueryTData("select * from GroupInfoBean ", GroupInfoBean.class);
		addly.setOnClickListener(this);
   	   if(groupInfoBean!=null){
   		 kindernametv.setText(groupInfoBean.getGname());
   		 kindernumtv.setText(groupInfoBean.getGnum()+"");	
   		 if(!groupInfoBean.getLocation().equals("0")){
    			klocationtv.setText(getlocationByid(groupInfoBean.getLocation()+""));
			 }else{
				klocationtv.setText("");
			 }
   		 kcontactspeopletv.setText(groupInfoBean.getContact());
   		 kcontactsphonetv.setText(groupInfoBean.getPhone());    	
   		 kmiaoshu.setText(groupInfoBean.getDesc());   		
   	   }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View view=inflater.inflate(R.layout.sreachkinderresultfragment2, null);
		 kindernametv=(TextView) view.findViewById(R.id.sreachkinder_knametv);
		 kindernumtv=(TextView) view.findViewById(R.id.sreachkinder_knumtv);
		 klocationtv=(TextView) view.findViewById(R.id.sreachkinder_klocationtv);
		 kcontactspeopletv=(TextView) view.findViewById(R.id.sreachkinder_kcontactspeopletv);
		 kcontactsphonetv=(TextView) view.findViewById(R.id.sreachkinder_kcontactsphonetv);
		 kmiaoshu=(TextView) view.findViewById(R.id.sreachkinder_kmiaoshutv);	 
		 addly=(LinearLayout)view.findViewById(R.id.sreachkinder_addtokinderly);	  
    	 return view;
	}
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sreachkinder_addtokinderly:
			 if(groupInfoBean.getJoinable()==0){
				 ShowToast("对不起，此群暂时不对外开放,不能加入");
    		     return;
			 }else{
    			 if(accountInfo.getRole()==2){
    				 ShowToast("对不起，您不能加入此群");
    				 return;
    			 }else{
        			 Boolean isflag=true;
      			     for(int i=0;i<sqllist.size();i++){
      					if(sqllist.get(i).getGtype()==1){
      						isflag=false;
      					     break;
      					}
      				}
      				if(!isflag){
      					ShowToast("对不起，您已经加入了一个幼儿园群，不能加入多个幼儿园群");
      					return;
      				}
        		 }  		
    		 }  
			if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
				Intent intent=new Intent(getActivity(),ServiceCompleteInformationActivity.class);	
				startActivityForResult(intent, 0);
			}else{
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
					Intent intent=new Intent(getActivity(),EditActivity.class);
					intent.putExtra("clickposition", 0);
					intent.putExtra("text", addvalue);
					intent.putExtra(AppConstants.TITLE, "请输入邀请码");
					startActivityForResult(intent, 3);
				}
			}
			break;
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
											Intent intent=new Intent(getActivity(),ServiceGetgroupActivity.class);
											intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
									  		startActivity(intent);
										}
									});
								}else{
									ShowToast("加入失败");
								}	
								cancelLoadingDialog();
							}
						});
					}
					
				}	
			}else{
				
		    }
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
//	        	 return list.get(0).getProvince()+","+list.get(0).getCity()+","+list.get(0).getCity();
	        }
	        return "";
	    }
}
