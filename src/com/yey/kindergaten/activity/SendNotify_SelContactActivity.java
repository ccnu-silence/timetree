package com.yey.kindergaten.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.SendMsgExpandAdapter;
import com.yey.kindergaten.adapter.ServiceTeacherAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.SendMsgChildItem;
import com.yey.kindergaten.bean.SendMsgGroupItem;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.FragmentBase;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.widget.TabButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;

import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;

import android.widget.TextView;


public class SendNotify_SelContactActivity extends FragmentActivity implements OnClickListener{
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	@ViewInject(R.id.right_tv)TextView  tv_right;
	@ViewInject(R.id.id_sendmsg_parent_btn)TabButton parent;
	@ViewInject(R.id.id_sendmsg_teacher_btn)TabButton teacher;
	

    private ArrayList<String>teacherlistselelct=new ArrayList<String>();
    private ArrayList<String>teachershowlist=new ArrayList<String>();
    private ArrayList<String>parentshowlist=new ArrayList<String>(); 
	private TeacherFragment mTeacherFrag;
	private ParentFragment  mParentFrag;
	private ArrayList<String>parentnonlist=new ArrayList<String>();
	private ArrayList<String>parentlistselelct=new ArrayList<String>();
	 Contacts contants=AppContext.getInstance().getContacts();
	  @Override
	 protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_sendnotice_addcontans);
		ViewUtils.inject(this);
		prepareView();
		mTeacherFrag=new TeacherFragment();
		mParentFrag=new ParentFragment();	
		teachershowlist=this.getIntent().getStringArrayListExtra("teacherlist");
		parentnonlist=this.getIntent().getStringArrayListExtra("parentlist");
	    if(parentlistselelct!=null&&parentnonlist!=null){
		parentlistselelct.addAll(parentnonlist);
	    }
	    if(teacherlistselelct!=null){
		   teacherlistselelct.addAll(teachershowlist);
	   }
		getSupportFragmentManager().beginTransaction()
	    .add(R.id.id_sendmsg_fragment_content, mTeacherFrag).show(mTeacherFrag)
	    .commit();
	    getSupportFragmentManager().beginTransaction()
	    .add(R.id.id_sendmsg_fragment_content, mParentFrag).hide(mParentFrag)
	    .commit();
	}
	  
	  
	  private void prepareView(){
		    tv_headerTitle.setText(R.string.sendmsg_addcontact);
	      	tv_right.setVisibility(View.VISIBLE);
	      	tv_right.setOnClickListener(this);
	      	tv_right.setText("确定");
	      	iv_left.setVisibility(View.VISIBLE);
	      	iv_left.setOnClickListener(this);
	      	teacher.setOnClickListener(this);
	      	parent.setOnClickListener(this);
            resetState(R.id.id_sendmsg_teacher_btn);
	   }

	@Override
	public void onClick(View v) {
		
		 switch(v.getId()){
		 case R.id.left_btn:
			 finish();			 
			 break;
		 case R.id.id_sendmsg_teacher_btn:
			 resetState(v.getId());
			 teacher.setSelected(true);
			   parentlistselelct=(ArrayList<String>) mParentFrag.getCheckedChildren();		
	           mTeacherFrag.getFragmentManager().beginTransaction()
	           .show(mTeacherFrag).commit();
	           mParentFrag.getFragmentManager().beginTransaction()
	           .hide(mParentFrag).commit();
			 break;
		 case R.id.id_sendmsg_parent_btn:	
			 resetState(v.getId());
			 parent.setSelected(true);
			  mTeacherFrag.getFragmentManager().beginTransaction()
	           .hide(mTeacherFrag).commit();
	           mParentFrag.getFragmentManager().beginTransaction()
	           .show(mParentFrag).commit();
			 break;

		 case R.id.right_tv:			 
			 Intent intent=new Intent(SendNotify_SelContactActivity.this,SendNotificationActivity.class);
			 intent.putStringArrayListExtra("teacherlist", teacherlistselelct);
			 intent.putStringArrayListExtra("parentlist", parentlistselelct);
			 setResult(RESULT_OK, intent);
			 this.finish();
			 break;
		    
		 } 
		 
	}
	
	private void resetState(int id) {
		parent.setSelected(false);
    	teacher.setSelected(false);			
		switch (id) {
		case R.id.id_sendmsg_parent_btn:
			parent.setSelected(true);
			break;
		case R.id.id_sendmsg_teacher_btn:
			teacher.setSelected(true);
			break;
		}
	}
	
	   @SuppressLint("ValidFragment")
	class TeacherFragment extends Fragment{
		    public ListView expListView;
		    ServiceTeacherAdapter adapter2;
		    public List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();		   
		     @Override
		     public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			   View view=inflater.inflate(R.layout.fragment_sendmsg_teacher_parent, null);
		       expListView=(ListView) view.findViewById(R.id.id_sendmsg_tc_pre_exlist);
	           expListView.setDivider(null);
		       List<Teacher>listbean=null;
	           listbean=contants.getTeachers();	       	     
	           if(teachershowlist!=null&&teachershowlist.size()!=0){
		    	   adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean, teachershowlist);
		    	   teachershowlist.clear();
		        }else
		        if(teachershowlist!=null){

		        	 adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean, teacherlistselelct);
		        }else if(teacherlistselelct==null||teacherlistselelct.size()==0){
		        	 adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean,expListView);
		        }else{
		        	 adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean, teacherlistselelct);
		        } 
		        
		         if(teacherlistselelct!=null)
		         {
		        	 adapter2.setCheckedChildren(teacherlistselelct);
		         }
		         expListView.setAdapter(adapter2);   
			return view;
		}
		 
		   public List<String> getCheckedChildren() {	
				return   adapter2.getCheckedChildren();
			}		 
		 
		 @Override
		public void onActivityCreated(Bundle savedInstanceState) {
			
		    super.onActivityCreated(savedInstanceState);
		}
		}
	   @SuppressLint("ValidFragment")
	class ParentFragment extends FragmentBase{
		    public ExpandableListView expListView;
			
		    SendMsgExpandAdapter adapter3;

		       List<SendMsgChildItem> childlist=new ArrayList<SendMsgChildItem>();
		       SendMsgChildItem  childItem=null;
		       SendMsgGroupItem groupItem=null;
		       List<Children>listbean=null;
		       List<Children> list;
		       List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();
		       List<SendMsgGroupItem>groupshow=new ArrayList<SendMsgGroupItem>();
		       List<Classe>classlist;
		       AccountInfo accountInfo;
		 @Override
		     public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			   View view=inflater.inflate(R.layout.inflater_select_people_exl, null);
		       expListView=(ExpandableListView) view.findViewById(R.id.id_exlist);
		       expListView.setGroupIndicator(null);	 	    
		       Contacts contants=AppContext.getInstance().getContacts();
		       classlist=contants.getClasses();
		       accountInfo=AppServer.getInstance().getAccountInfo();        
		       for(int i=0;i<classlist.size();i++){	    	   
		    	   groupItem  = new SendMsgGroupItem(i+"",classlist.get(i).getCname(),childlist);
		    	   group.add(groupItem);
		       }
     	 
		  	    		      
//		       expListView.setOnGroupClickListener(new OnGroupClickListener() {		
//				 @Override
//				public boolean onGroupClick(ExpandableListView arg0, View arg1,final int position,
//						long arg3) {	
//			
//					  if(!expListView.isGroupExpanded(position)) {					  
//				         	AppServer.getInstance().GetParentByCid(accountInfo.getUid(), classlist.get(position).getCid(), new OnAppRequestListener() {
//								@Override
//								public void onAppRequest(int code, String message, Object obj) {
//									if(code==0){
//										Children []children=(Children[]) obj;									
//										list=Arrays.asList(children);	
//								 
//										  List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();	
//								   	      groupshow.clear();
//								   	      childlist.clear();
//									    	for(int li=0;li<list.size();li++){			    	
//											     childItem=new SendMsgChildItem(list.get(li).getUid()+"", list.get(li).getPhone(), list.get(li).getNickname(),list.get(li).getAvatar());					    
//											     childlist.add(childItem);   									    
//										    }								    
//									    	   groupItem  = new SendMsgGroupItem(position+"",classlist.get(position).getCname(),childlist);
//							    	    	   group.add(groupItem); 
//	    	    	  
//										   notifydate();											 
//										   expListView.expandGroup(position);
//										   expListView.setSelection(position);
//						  			     
//									}else{
//										list=new ArrayList<Children>();
//									}
//								}
//							});
//				        
//					 }else{
//					    expListView.collapseGroup(position);
//					    expListView.setSelection(position);
//					 }
//					 	return true;
//				}
//			});
		       
		              
		      notifydate();    
		  
		        	 adapter3.setCheckedChildren(parentlistselelct);
		          
		            
			return view;
		}
		 
		 void notifydate(){	
			  if(parentnonlist!=null){
		    	     adapter3=new SendMsgExpandAdapter(this.getActivity(), group,parentlistselelct,parentnonlist,expListView); 
		         }else if(parentnonlist==null){
		        	 adapter3=new SendMsgExpandAdapter(this.getActivity(), group,parentlistselelct,parentshowlist,expListView); 
		         }
		         else{
		        	 adapter3=new SendMsgExpandAdapter(this.getActivity(), groupshow,parentlistselelct,expListView);
		        }
			  expListView.setAdapter(adapter3);						 		  		
		 }
		 
		   public List<String> getCheckedChildren() {	
				return  adapter3.getCheckedChildren();
			}		 
			public void refres(){					
				getActivity().runOnUiThread(new Runnable() {		
					@Override
					public void run() {
						notifydate();
				
					}
				});
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
}
