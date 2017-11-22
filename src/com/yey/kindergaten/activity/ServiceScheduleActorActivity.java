package com.yey.kindergaten.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.FragmentAdapter;
import com.yey.kindergaten.adapter.SendMsgExpandAdapter;
import com.yey.kindergaten.adapter.ServiceSelectFriendAdapter;
import com.yey.kindergaten.adapter.ServiceTeacherAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.SendMsgChildItem;
import com.yey.kindergaten.bean.SendMsgGroupItem;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.FragmentBase;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;
/**
 * 选择日程中的参与者
 * @author zy
 *
 */
@SuppressLint("ValidFragment")
public class ServiceScheduleActorActivity extends FragmentActivity implements OnClickListener, OnCheckedChangeListener, OnPageChangeListener{

	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView  iv_left;
	@ViewInject(R.id.right_tv)TextView  tv_right;
	
	/**顶部选择不同类型*/
//	private RadioButton friendfl;
	private RadioButton teacerfl;
	private RadioButton parentfl;
	private TextView smovetv = null;
	private RadioGroup radioGroup;
	/*好友，老师，家长布局*/
	@SuppressLint("ValidFragment")
//	private FriedndFragment mFriendFrag=new FriedndFragment();;
//	private ParentFragment mParentFrag=new ParentFragment();	
	private TeacherFragment  mTeacherFrag=new TeacherFragment();;
	private FrameLayout conentfl;
	 /**好友，老师家长选择列表*/
	private ArrayList<String>friendlistselelct=new ArrayList<String>();
	 /**好友，老师家长选择列表*/
	private ArrayList<String>parentlistselelct=new ArrayList<String>();
     /**好友，老师家长选择列表*/
    private ArrayList<String>teacherlistselelct=new ArrayList<String>();
	 /**显示之前选择的老师列表*/
	private ArrayList<String>teachershowlist=new ArrayList<String>();
	 /**显示之前选择的家长的列表*/
	private ArrayList<String>parentshowlist=new ArrayList<String>(); 
	private ArrayList<String>friendshowlist=new ArrayList<String>(); 
    /**选择的全部的好友列表uid*/
    private ArrayList<String>allselectlist=new ArrayList<String>();
    /**数据库中的uid*/
    private String uids=null;
    AccountInfo accountInfo;
    private List<Classe>classlist;
    Children child;
    Contacts contants=AppContext.getInstance().getContacts();
    private Handler mHandler ;
    List<Children> list=null;
    private List<Teacher>listbeans=null;
    private ArrayList<String>parentnonlist=new ArrayList<String>();
    private ArrayList<String>friendnonlist=new ArrayList<String>();
    private List<Parent>childlist=null;
    private List<Friend>friendlist=null;
	private LoadingDialog loadingdialog;
	List<Children>listbeanss=null;
	private View showview;
	List<Fragment> fragmentList=new ArrayList<Fragment>();
	FragmentAdapter fragAdapter;
	ViewPager viewpager;
	
	
	   List<SendMsgChildItem> childlistpa=new ArrayList<SendMsgChildItem>();
       SendMsgChildItem  childItem=null;
       SendMsgGroupItem groupItem=null;
       List<Children>listbean=null;
       List<Children> listpa;
       public   List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();
       List<SendMsgGroupItem>groupshow=new ArrayList<SendMsgGroupItem>();
	
       
       public ServiceScheduleActorActivity() {
		super();
	   }
       
       @Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_schedule_select_actor);
		ViewUtils.inject(this);		
		prepareView();
	    uids=null;
	    uids=this.getIntent().getStringExtra(AppConstants.SERVICE_SHOW_DB_TEACHER);		
		if(uids!=null){
			quaryUids(uids);
		}
		 
		if(teachershowlist!=null&&teachershowlist.size()!=0){
			teacherlistselelct.clear();
		   teacherlistselelct.addAll(teachershowlist);}
		if(parentnonlist!=null&&parentnonlist.size()!=0){		
			 parentlistselelct.clear();
			 parentlistselelct.addAll(parentnonlist);
		}	  
		if(friendnonlist!=null&&friendnonlist.size()!=0){
			 friendlistselelct.clear();
			 friendlistselelct.addAll(friendnonlist);
		}
	 
		
//			fragmentList.add(mFriendFrag);
			fragmentList.add(mTeacherFrag);
//			fragmentList.add(mParentFrag);
			
	
		fragAdapter=new FragmentAdapter(getSupportFragmentManager(), fragmentList);
		viewpager.setAdapter(fragAdapter);
//		viewpager.setOnPageChangeListener(this);
	}	  

       private void quaryUids(String uids){
    		String[]uid=uids.split(",");				
    	    for(int i=0;i<uid.length;i++){
    			      try {
    					  listbeans=DbHelper.getDB(this).findAll(Teacher.class, WhereBuilder						            							                                          .b("uid", "=",uid[i]));
    					  childlist=DbHelper.getDB(this).findAll(Parent.class,WhereBuilder
    							                                           .b("uid","=",uid[i]));
    					  friendlist=DbHelper.getDB(this).findAll(Friend.class,WhereBuilder						                                           .b("uid", "=", uid[i]));
  				                    
    					 if(listbeans!=null&&listbeans.size()!=0){ 
    					    for(int is=0;is<listbeans.size();is++){		
    					    	if(teachershowlist!=null){
    					    		teachershowlist.add(listbeans.get(is).getUid()+",");					    		
    					         	}				          
    					        }
    					     }	
    					 	                                
    					  if(childlist!=null&&childlist.size()!=0){
    						 for(int is=0;is<childlist.size();is++){
    						if(parentnonlist!=null){
    							 parentnonlist.add(childlist.get(is).getUid()+",");							 
    						  }								
    						 }  					  
                          }
    					  
                        if(friendlist!=null&&friendlist.size()!=0){
    							 for(int is=0;is<friendlist.size();is++){
    							if(friendnonlist!=null){
    								 friendnonlist.add(friendlist.get(is).getUid()+",");
    							}											 
    						 }  
    					  }
    				} catch (DbException e) {
    					e.printStackTrace();
    				}
    			  }
    	   
       }
       
	  
	 	private void prepareView() {
		    tv_headerTitle.setText("选择参与者");
	       	iv_left.setVisibility(View.VISIBLE);
	       	iv_left.setOnClickListener(this);
	       	iv_right.setVisibility(View.GONE);
	       	tv_right.setVisibility(View.VISIBLE);
	       	tv_right.setText("确定");
	       	tv_right.setOnClickListener(this);		
	       	viewpager=(ViewPager) findViewById(R.id.id_service_schedule_actor_content_fl);
//	       	friendfl=(RadioButton) findViewById(R.id.id_service_schedule_actor_friend_fl);
//	       	friendfl.setTextColor(this.getResources().getColor(R.color.radio_button_check_color));
//	       	teacerfl=(RadioButton) findViewById(R.id.id_service_schedule_actor_teacher_fl);
//	       	parentfl=(RadioButton) findViewById(R.id.id_service_schedule_actor_parent_fl);		
//	       	radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
//	       	smovetv = (TextView)findViewById(R.id.smovetextview);		
//	       	radioGroup.setOnCheckedChangeListener(this);	       	
	 	}
	 	
	 	@Override
	 	public boolean onKeyDown(int keyCode, KeyEvent event) {
	 		if(keyCode==KeyEvent.KEYCODE_BACK){
	 			if(uids!=null){
	 			Intent intent=new Intent(ServiceScheduleActorActivity.this,ServiceScheduleWriteActivity.class);
				if(friendlistselelct.size()!=0&&friendlistselelct!=null){
					intent.putStringArrayListExtra(AppConstants.SERVICE_SHOW_FRIEND, friendlistselelct);
					allselectlist.addAll(friendlistselelct);										
				}
				if(parentlistselelct!=null){
					intent.putStringArrayListExtra(AppConstants.SERVICE_SHOW_PARENR, parentlistselelct);
					allselectlist.addAll(parentlistselelct);
				}
				if(teacherlistselelct!=null){
					intent.putStringArrayListExtra(AppConstants.SERVICE_SHOW_TEACHER, teacherlistselelct);
					allselectlist.addAll(teacherlistselelct);
				}	
				   List<String>namelist = new ArrayList<String>();
				   List<Teacher>tList = new ArrayList<Teacher>();
				   for(int i=0;i<allselectlist.size();i++){		
					   String sUid =allselectlist.get(i); 
					   int uid=0;
					   if(sUid.contains(",")){
						   sUid = sUid.substring(0, sUid.length()-1);
						   uid  =Integer.valueOf(sUid); 
					   }else{
						   uid  =Integer.valueOf(sUid); 
					   }
					   try {
						tList=DbHelper.getDB(this).findAll(Teacher.class,WhereBuilder
						          .b("uid","=",uid));
						for(int n=0;n<tList.size();n++){
							namelist.add(tList.get(n).getRealname());
						}
					} catch (DbException e) {
						e.printStackTrace();
					}
				   }
				   intent.putStringArrayListExtra("selectlist", allselectlist);			   
				   intent.putStringArrayListExtra("namelist", (ArrayList<String>) namelist);
				   setResult(RESULT_OK, intent);
				   this.finish();		 			
	 		}
	 			}
	 		return super.onKeyDown(keyCode, event);	 		
	 	}
	 	
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_btn:
				this.finish();
				break;
			case R.id.right_tv:
				Intent intent=new Intent(ServiceScheduleActorActivity.this,ServiceScheduleWriteActivity.class);
				if(friendlistselelct.size()!=0&&friendlistselelct!=null){
					intent.putStringArrayListExtra(AppConstants.SERVICE_SHOW_FRIEND, friendlistselelct);
					allselectlist.addAll(friendlistselelct);
				}
				if(parentlistselelct!=null){
					intent.putStringArrayListExtra(AppConstants.SERVICE_SHOW_PARENR, parentlistselelct);
					allselectlist.addAll(parentlistselelct);

				}
				if(teacherlistselelct!=null){
					intent.putStringArrayListExtra(AppConstants.SERVICE_SHOW_TEACHER, teacherlistselelct);
					allselectlist.addAll(teacherlistselelct);
				}				    
				   List<String>namelist = new ArrayList<String>();
				   List<Teacher>tList = new ArrayList<Teacher>();
				   for(int i=0;i<allselectlist.size();i++){		
					   String sUid =allselectlist.get(i); 
					   int uid=0;
					   if(sUid.contains(",")){
						   sUid = sUid.substring(0, sUid.length()-1);
						   uid  =Integer.valueOf(sUid); 
					   }else{
						   uid  =Integer.valueOf(sUid); 
					   }
					
					   try {
						tList=DbHelper.getDB(this).findAll(Teacher.class,WhereBuilder
						          .b("uid","=",uid));
						for(int n=0;n<tList.size();n++){
							namelist.add(tList.get(n).getRealname());
						}
					} catch (DbException e) {
						e.printStackTrace();
					}
				   }
				   intent.putStringArrayListExtra("selectlist", allselectlist);
				   intent.putStringArrayListExtra("namelist", (ArrayList<String>) namelist);
				    setResult(RESULT_OK, intent);
				    this.finish();		
				break;					
			}
		}
		public void showLoadingDialog(String text){
			if(loadingdialog!=null){
				loadingdialog.setText(text);
			}else{
				loadingdialog = new LoadingDialog(this, text);
				loadingdialog.show();
			}
			
		}
  public  class FriedndFragment extends FragmentBase{
	   
	   public FriedndFragment() {
		super();
	}
	    public ListView expListView;
	    ServiceSelectFriendAdapter adapter;
	    public List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();		   
	 @Override
	     public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		    View view=inflater.inflate(R.layout.service_schedule_select_friend, null);
	        expListView=(ListView) view.findViewById(R.id.id_sendmsg_tc_pre_exlist);
	        expListView.setDivider(null);
	        group.clear();
	        List<Friend>friendbean=new ArrayList<Friend>();
	        friendbean=contants.getFriends();	      
	        if(friendnonlist!=null&&friendnonlist.size()!=0){
	     	   adapter=new ServiceSelectFriendAdapter(this.getActivity(), friendbean, friendnonlist);
	    	   friendnonlist.clear();
	         }else
	        if(friendnonlist!=null){
	        	 adapter=new ServiceSelectFriendAdapter(this.getActivity(), friendbean, friendlistselelct);
	        }else if(friendlistselelct==null||friendlistselelct.size()==0){
	        	 adapter=new ServiceSelectFriendAdapter(this.getActivity(), friendbean,expListView);
	        }else{
	        	 adapter=new ServiceSelectFriendAdapter(this.getActivity(), friendbean, friendlistselelct);
	        }      
    	    if(friendlistselelct!=null)
	         {
    	     adapter.notifyDataSetChanged();
  	    	 adapter.setCheckedChildren(friendlistselelct);	    	
	         }	 	    
	         expListView.setAdapter(adapter);   
		return view;
	}
	 
	   public List<String> getCheckedChildren() {	
			return   adapter.getCheckedChildren();
		}		 
	 
	 @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
	    super.onActivityCreated(savedInstanceState);
	}
		}
    public  class TeacherFragment extends FragmentBase{
    	
        ExpandableListView expListView;
	    ServiceTeacherAdapter adapter2;
	    SendMsgExpandAdapter adapter3;
	   
	    SendMsgChildItem  childItem=null;
	    SendMsgGroupItem groupItem=null;
	    List<Parent>listbean=null;
	    List<SendMsgChildItem> childlist=null;
	    public List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();		   	
    	public TeacherFragment() {
		     super();
		}
	   
	     @Override
	     public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		   View view=inflater.inflate(R.layout.inflater_select_people_exl, null);
		
		   expListView=(ExpandableListView) view.findViewById(R.id.id_exlist);
		   expListView.setGroupIndicator(null);	 
	       expListView.setDivider(null);
	       expListView.setCacheColorHint(0); 
	       expListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView expandablelistview,
					View view, int i, long l) {
			
				return true ;
		    	}
		   });
	       List<Teacher>listbean=null;	 
           listbean=contants.getTeachers();	             
	        if(group==null||group.size()==0){    
		         for(int i=0;i<listbean.size();i++){		         		  	        		     
		           childlist = new ArrayList<SendMsgChildItem>();
		        	  for(int li=0;li<listbean.size();li++){
				         childItem=new SendMsgChildItem(listbean.get(li).getUid()+"", listbean.get(li).getPhone(), listbean.get(li).getRealname(),listbean.get(li).getAvatar());					    
					     childlist.add(childItem);   
					    }			 					
					        	  							        	 	      	               
		          }	 
		         groupItem  = new SendMsgGroupItem(111+"","全选",childlist);
			     group.add(groupItem);	
	        }  
	       
	        notifydate();
	         if(teacherlistselelct!=null)
	         {
	        	 adapter3.setCheckedChildren(teacherlistselelct);
	         }
	         expListView.setAdapter(adapter3); 
	         expListView.expandGroup(0);
		return view;
	}	     
	  	void notifydate(){
			  if(teachershowlist!=null){
		    	     adapter3=new SendMsgExpandAdapter(this.getActivity(), group,teacherlistselelct,teachershowlist,expListView); 
		         }else if(teachershowlist==null){
		        	 adapter3=new SendMsgExpandAdapter(this.getActivity(), group,teacherlistselelct,teachershowlist,expListView); 
		         }
		         else{
		        	 adapter3=new SendMsgExpandAdapter(this.getActivity(), groupshow,teacherlistselelct,expListView);
		        }
			  expListView.setAdapter(adapter3);					 		  		
		 }
	     
//	     void notifyDateChanged(){
//	    	 if(teachershowlist!=null&&teachershowlist.size()!=0){
//		    	   adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean, teachershowlist);
//		    	   teachershowlist.clear();
//		        }else
//		   
//		        if(teachershowlist!=null){
//		        	 adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean, teacherlistselelct);
//		        }else if(teacherlistselelct==null||teacherlistselelct.size()==0){
//		        	 adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean,expListView);
//		        }else{
//		        	 adapter2=new ServiceTeacherAdapter(this.getActivity(), listbean, teacherlistselelct);
//		        } 
//	     }
	 
	   public List<String> getCheckedChildren() {	
			return   adapter2.getCheckedChildren();
		}		 
	 
	 @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
	    super.onActivityCreated(savedInstanceState);
	}
	}
   class ParentFragment extends FragmentBase{
	   
	   public ParentFragment() {
		super();
	}
	    public ExpandableListView expListView;
		
	    SendMsgExpandAdapter adapter3;
	    
	       
	     @Override
	     public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		   View view=inflater.inflate(R.layout.inflater_select_people_exl, null);
	       expListView=(ExpandableListView) view.findViewById(R.id.id_exlist);
	       expListView.setGroupIndicator(null);	 
	       String sql="select * from Parent group by cid";   
	       Contacts contants=AppContext.getInstance().getContacts();
	       classlist=contants.getClasses();
	       accountInfo=AppServer.getInstance().getAccountInfo();  	     		      
		   SendMsgChildItem  childItem=null;
		   SendMsgGroupItem groupItem=null;
		   List<Parent>listbean=null;
		   List<SendMsgChildItem> childlist=null;
	        if(group==null||group.size()==0){
		       List<Parent> list= DbHelper.QueryTData(sql, Parent.class);	       
		          for(int i=0;i<list.size();i++){
		        	  try {     		  
		        		  listbean=DbHelper.getDB(this.getActivity()).findAll(Parent.class, WhereBuilder.b("cname", "=", list.get(i).getCname()));			      
		        		  childlist = new ArrayList<SendMsgChildItem>();
		        		  for(int li=0;li<listbean.size();li++){
//						     childItem=new SendMsgChildItem(listbean.get(li).getUid()+"", listbean.get(li).getPhone(), listbean.get(li).getNickname());	
						     childItem=new SendMsgChildItem(listbean.get(li).getUid()+"", listbean.get(li).getPhone(), listbean.get(li).getRealname(),listbean.get(li).getAvatar());					    
						     childlist.add(childItem);   
					    }			 					
					    	groupItem  = new SendMsgGroupItem(classlist.get(i).getCid()+"",list.get(i).getCname(),childlist);
						    group.add(groupItem);	        	  					
		        	  } catch (DbException e) {			
						e.printStackTrace();
					}		      	               
		          }
		       
	        }       
	      notifydate();    
	       if(parentlistselelct!=null)
	         {
	        	 adapter3.setCheckedChildren(parentlistselelct);
	         }	 
//		  }else{
//			  notifydate();    
//		       if(parentlistselelct!=null)
//		         {
//		        	 adapter3.setCheckedChildren(parentlistselelct);
//		         }
//		  }		   
		return view;
	}
	 
	 void notifydate(){
		  if(parentnonlist!=null){
	    	     adapter3=new SendMsgExpandAdapter(this.getActivity(), group,parentlistselelct,parentnonlist,expListView); 
	         }else if(parentnonlist==null&&parentshowlist!=null){
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
	   
			
		private boolean hidden;
		@Override
		public void onHiddenChanged(boolean hidden) {
			super.onHiddenChanged(hidden);
			this.hidden = hidden;		
		}
    @Override
    public void onResume() {
    	
    super.onResume(); 
 	
    }
    
 
	}
   
//   public  Fragment getparentFragment(){
//	
//	   return mParentFrag;	   
//   }
   public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		}
		public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		}

	@Override
	public void onCheckedChanged(RadioGroup radiogroup, int checkedId) {
		  int current = viewpager.getCurrentItem();
	       switch (checkedId) {
 
//             case R.id.id_service_schedule_actor_teacher_fl:
//                 if(current != 1){
//               	  viewpager.setCurrentItem(0);
//                 }

//             break;
//             case R.id.id_service_schedule_actor_parent_fl:
//                 if(current != 2){
//               	  viewpager.setCurrentItem(1);
//                 }
//             break;
 
         }
	}
	
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		   int x = (int)((position + positionOffset) * smovetv.getWidth());
		     ((View)smovetv.getParent()).scrollTo(-x, smovetv.getScrollY());        
		
	}
	@Override
	public void onPageSelected(int position) {
//		if(position == 0){
////		  	friendfl.setChecked(true);
////		  	friendfl.setTextColor(this.getResources().getColor(R.color.radio_button_check_color));
//		    teacerfl.setTextColor(this.getResources().getColor(R.color.contact_mainlvtitletv));
//		    parentfl.setTextColor(this.getResources().getColor(R.color.contact_mainlvtitletv));		    
//		}
	 if(position == 0){
			teacerfl.setChecked(true);
			teacerfl.setTextColor(this.getResources().getColor(R.color.radio_button_check_color));
//		    friendfl.setTextColor(this.getResources().getColor(R.color.contact_mainlvtitletv));
		    parentfl.setTextColor(this.getResources().getColor(R.color.contact_mainlvtitletv));
		}else if(position == 1){
			parentfl.setChecked(true);
			parentfl.setTextColor(this.getResources().getColor(R.color.radio_button_check_color));
		    teacerfl.setTextColor(this.getResources().getColor(R.color.contact_mainlvtitletv));
//		    friendfl.setTextColor(this.getResources().getColor(R.color.contact_mainlvtitletv));
		}
	}
	@Override
	public void onPageScrollStateChanged(int position) {
	
	}

       
}

//public void initdata(final List<Classe>classlist){  				
//		 accountInfo=AppServer.getInstance().getAccountInfo();			  		
//	     if(classlist!=null){			    
//	     for(int  i=0;i<classlist.size();i++){
//	     final int x=i;
//	     	AppServer.getInstance().GetParentByCid(accountInfo.getUid(), classlist.get(i).getCid(), new OnAppRequestListener() {
//					@Override
//					public void onAppRequest(int code, String message, Object obj) {
//						if(code==0){
//							Children []children=(Children[]) obj;
//							list=Arrays.asList(children);
//						    if(list!=null){
//						    	for(int j=0;j<list.size();j++){
//						    		list.get(j).setCname(classlist.get(x).getCname());					    		
//						    	}
//						    	try {
////									DbHelper.getDB(ServiceScheduleActorActivity.this).createTableIfNotExist(Children.class);
//									DbHelper.getDB(ServiceScheduleActorActivity.this).saveAll(list);
//								} catch (DbException e) {
//									e.printStackTrace();
//								}
//						    }				    
//						}else{
//							list=new ArrayList<Children>();
//						}
//					}
//				});	     	     
//	         }
//		    }					
//		}

//  
//expListView.setOnGroupClickListener(new OnGroupClickListener() {		
//@Override
//public boolean onGroupClick(ExpandableListView arg0, View arg1,final int position,
//long arg3) {	
//if(showview!=null){
//showview.setVisibility(View.VISIBLE);
//}                  
//if(!expListView.isGroupExpanded(position)) {					  
//AppServer.getInstance().GetParentByCid(accountInfo.getUid(), classlist.get(position).getCid(), new OnAppRequestListener() {
//@Override
//public void onAppRequest(int code, String message, Object obj) {
//if(code==0){
//Children []children=(Children[]) obj;									
//listpa=Arrays.asList(children);												
//try {	
//List<Children>childlist=DbHelper.QueryTData("select * from Children", Children.class);
//for(int i=0;i<listpa.size();i++){
//	 if(childlist!=null){
//		if(childlist.contains(listpa.get(i).getUid())&&childlist.contains(listpa.get(i).getRealname())){								
//			 break;
//		  }else{
//			   DbHelper.getDB(ServiceScheduleActorActivity.this).saveAll(listpa);
//			        }
//		         }
//        }							   
//   	} catch (DbException e) {
//	}
//List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();	
//   groupshow.clear();
//   childlistpa.clear();
//	for(int li=0;li<listpa.size();li++){			    	
//	     childItem=new SendMsgChildItem(listpa.get(li).getUid()+"", listpa.get(li).getPhone(), listpa.get(li).getRealname(),listpa.get(li).getAvatar());					    
//	     childlistpa.add(childItem);   									    
//  }								    
//	   groupItem  = new SendMsgGroupItem(classlist.get(position).getCid()+"",classlist.get(position).getCname(),childlistpa);
//	   group.add(groupItem); 
//	   if(showview!=null){
//		   showview.setVisibility(View.GONE);
//	   }	    	    	  
// notifydate();											 
// expListView.expandGroup(position);
// expListView.setSelection(position);
// 
//}else{
//listpa=new ArrayList<Children>();
//}
//}
//});
//
//}else{
//expListView.collapseGroup(position);
//expListView.setSelection(position);
//}
//return true;
//}
//});

//if(classlist!=null){
//    for(int i=0;i<classlist.size();i++){	    	   
// 	   groupItem  = new SendMsgGroupItem(classlist.get(i).getCid()+"",classlist.get(i).getCname(),childlistpa);
// 	   group.add(groupItem);
//     }
//    }
