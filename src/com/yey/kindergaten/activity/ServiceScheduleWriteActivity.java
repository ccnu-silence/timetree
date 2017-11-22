package com.yey.kindergaten.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceShowActorNameAdapter;
import com.yey.kindergaten.adapter.ServiceShowActorNameAdapter.ChangeItemView;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AlarmBean;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.ScedulesNameBean;
import com.yey.kindergaten.bean.SchedulesBean;
import com.yey.kindergaten.bean.ServiceScheduleThemeBean;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.ServiceScheduleRecevier;
import com.yey.kindergaten.service.ScheduleServiceUnbind;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.widget.PhotoDialog;
import com.yey.kindergaten.widget.PhotoInfalteDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
/**
 * 编辑  ，删除，新建日程,更新
 * 本地操作
 * @author zy
 */
public class ServiceScheduleWriteActivity extends BaseActivity implements OnClickListener,ChangeItemView{

	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView  iv_left;
	@ViewInject(R.id.right_tv)TextView  tv_right;
	
	private GridView gridview;	
	private RelativeLayout  Timerl;//选择时间
	private RelativeLayout  actorrl;//选择参与者
	private RelativeLayout  remindrl;//选择提醒
	private RelativeLayout  commentsrl;//备忘录
	private RelativeLayout  shownamerl;
	private GridView shownamegrid;
	private TextView timetv;
	private TextView actortv;
	private TextView remindtv;
	private TextView actortv2;
	private TextView actortv3;
	private TextView commentstv;
	AccountInfo accountInfo;
	private String state;/**状态值。标志位*/
	private String LocalTime;//获取时间
	private String comments;//获取备忘内容
	private String type;//获取提醒方式
	private int  sheid;/**自定义的日程id*/
	/**从上个界面传过来的date和time,当保存时不修改时用来修改所用*/
	private String statetime;
	private String datetime;
	/**表示从设置日程时间的界面传值*/
	private String updatedate;
	private String updatetime;
    /**查询当前数据库的返回值*/
	private List<ScedulesNameBean>listbean=new ArrayList<ScedulesNameBean>();
	/**选择 的参与者的uid*/
	private ArrayList<String>selectlist=new ArrayList<String>();
    /**编辑时数据库中的uid*/
	private String showuid=null;
	/**选择参与者的uid字符串*/
	private String uids=null;
	private  AlarmBean alarmbean=new AlarmBean();
	/**选择的参与者的姓名*/
	private ArrayList<String>selectnamelist= new ArrayList<String>();
	/**编辑时显示的参与者的姓名*/
	private ArrayList<String>shownamelist=new ArrayList<String>();
	/**保存用户选择的参与者，三种存在不同的list中,用来在保存数据到添加参与者界面中*/
	private ArrayList<String>friendlist=new ArrayList<String>();
	/**保存用户选择的参与者，三种存在不同的list中,用来在保存数据到添加参与者界面中*/
	private ArrayList<String>parentlist=new ArrayList<String>();
	/**保存用户选择的参与者，三种存在不同的list中,用来在保存数据到添加参与者界面中*/
	private ArrayList<String>teacherlist=new ArrayList<String>();
	private static final String[]imgname={"service_schedule_workcheck","service_scheduke_meeting","service_schedule_activity",
		        "service_schedule_dating","service_schedule_traning","service_scheduke_important","service_schedule_party","service_schedule_others"};
	/**主题图片url列数组*/
    private static final int[]imgsrc={R.drawable.service_schedule_workcheck,R.drawable.service_scheduke_meeting,
    	         R.drawable.service_schedule_activity,R.drawable.service_schedule_dating,R.drawable.service_schedule_traning,
    	               R.drawable.service_scheduke_important,R.drawable.service_schedule_party,R.drawable.service_schedule_others};
    /**主题图片hover状态**/
    private static final int[]themehover={R.drawable.service_schedule_workcheck_hover,R.drawable.service_scheduke_meeting_hover,
        R.drawable.service_schedule_activity_hover,R.drawable.service_schedule_dating_hover,R.drawable.service_schedule_traning_hover,
              R.drawable.service_scheduke_important_hover,R.drawable.service_schedule_party_hover,R.drawable.service_schedule_others_hover};
    /**主题名称数组*/
    private static final String[]theme={"工作检查","开会","园所活动","约见","培训","重要日子","聚会","其他" };
    List<Teacher>listbeans=null;
    List<Parent>parentlistbeans=null;
    List<Friend>friendlistbeans=null;
	private String comment;
	private  String deletestate="delete";//删除状态--按钮状态
	private RelativeLayout deletebtn;
	SchedulesBean bean=null;
	SchedulesBean bean2=null;
    /**dialog中选择的date和time*/
	private String dialogdate=null;
	private String dialogtime=null;
	public static PendingIntent checkintnent;
	View v1,v2;
	private ImageView showthemiv;
	private View view;
	private String newTheme=null;
	private int minutes;
	private int hours;
	/**提醒方式的标志位**/
	private int typeflag=1;
    int s=0;
    int x=0;
    String html=null;
    String time=null;
    int themenum=0;
    
    private String switchtype=null;
    public static List<AlarmBean>alarmlist=new ArrayList<AlarmBean>();
    Contacts contants=AppContext.getInstance().getContacts();
	private ServiceShowActorNameAdapter nameadapter;
    public  static  Activity context;
	List<Children> list=null;
	List<String>namelist = new ArrayList<String>();
	String realname=null;
	 List<ServiceScheduleThemeBean>themelist=new ArrayList<ServiceScheduleThemeBean>();
	 MyAdapter themeadapter=null;
	  String realnames=null;
	 private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==1){
				timetv.setText((String)msg.obj);
		        String date=timetv.getText().toString();
		        dialogdate=date.substring(0,date.lastIndexOf("  ")).trim();
		        dialogtime=date.substring(date.lastIndexOf("  ")).trim();		   
			}
			if(msg.what==2){
				remindtv.setText((String)msg.obj);
				type=(String) msg.obj;	
			   if(type.equals("不提醒")){
				    typeflag=0;
				}else if(type.equals("准时提醒")){
					typeflag=1;
				}else if(type.equals("提前20分钟提醒")){
					typeflag=2;
				}else if(type.equals("提前30分钟提醒")){
					typeflag=3;
				}else if(type.equals("提前1小时提醒")){
					typeflag=4;
				}else if(type.equals("提前1.5小时提醒")){
					typeflag=5;
				}else if(type.equals("提前2小时提醒")){
					typeflag=6;
				}								
			}
		};
	};
	
	public interface ShowWriteTime{
		public void ThisTime(String datetime,String statetime,String state);
	}
	@SuppressWarnings("null")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		 setContentView(R.layout.activity_service__write_schedule);	
		 context=this;
	     ViewUtils.inject(this);		
		 prepareView();	 
		 ServiceScheduleThemeBean beans=null;
		 accountInfo=AppServer.getInstance().getAccountInfo();
	     themelist=new ArrayList<ServiceScheduleThemeBean>();
		    for(int i=0;i<8;i++){
		    	beans=new ServiceScheduleThemeBean();
		    	beans.setImgsrc(themehover[i]);
		    	beans.setTheme(theme[i]);
		    	beans.setColor(this.getResources().getColor(R.color.service_write_schedule_show_tv));
		    	themelist.add(beans);
		    }
		 themeadapter=new MyAdapter(this, themelist);
   	     gridview.setAdapter(themeadapter);
	     Calendar calendar=Calendar.getInstance();
	     calendar.setTimeInMillis(System.currentTimeMillis());
	     hours= calendar.get(Calendar.HOUR_OF_DAY);
	     minutes= calendar.get(Calendar.MINUTE);
	   
		 state=this.getIntent().getStringExtra("state");
		 LocalTime=this.getIntent().getStringExtra("time");
		 comments=this.getIntent().getStringExtra("comments");

		 type=this.getIntent().getStringExtra("remind");
		 switchtype=this.getIntent().getStringExtra("type");
		 if(type==null){
			 type="准时提醒";
		 }
		 sheid=this.getIntent().getIntExtra("sheid",0);
		 statetime=this.getIntent().getStringExtra("statetime");
		 datetime=this.getIntent().getStringExtra("datetime");
		
		 Bundle  bundle=this.getIntent().getExtras();
		 
		 bean=(SchedulesBean)bundle.getSerializable(AppConstants.Schedule_Bean);     	    
		 if(state!=null&&LocalTime!=null){
		 if(state.equals("newschedule")){
			 timetv.setText(LocalTime);			 			
			 actortv.setText("请选择参与者");
			 remindtv.setText("准时提醒");
			 tv_headerTitle.setText("新建日程");
			 commentstv.setText("请填写备注");
			 shownamegrid.setClickable(false);
			 shownamegrid.setFocusable(false);
			 shownamegrid.setEnabled(false);
			 tv_right.setVisibility(View.VISIBLE);
			 tv_right.setText("保存");
			 iv_right.setVisibility(View.GONE);

		   	 showthemiv.setVisibility(View.INVISIBLE);
		  }else if(state.equals("showschedule")){//点击日程列表
			  String[]uid=null;
			  String[]realneams=null;
			  actortv.setVisibility(View.GONE);
			  shownamerl.setVisibility(View.VISIBLE);
//			  shownamerl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			  shownamegrid.setClickable(false);
			  shownamegrid.setFocusable(false);
			  shownamegrid.setEnabled(false);
			 
			  for(int i=0;i<theme.length;i++){
				     if(theme[i].equals(bean.getTheme())){
				    	 s=i;
				     }
			  }			  
//			  showthemiv.setImageResource(imgsrc[s]);
			  if(bean.getRealnames()!=null&&bean.getRealnames().length()!=0){
				  realneams = bean.getRealnames().split(",");
				  for(int i=0;i<realneams.length;i++){
					  shownamelist.add(realneams[i]);
				  }
				  nameadapter=new ServiceShowActorNameAdapter(ServiceScheduleWriteActivity.this, (ArrayList<String>)removeDuplicate(shownamelist));
			      nameadapter.setListener(this);
				  shownamegrid.setAdapter(nameadapter);
			      nameadapter.notifyDataSetChanged();	
			  }else{			  
			   if(bean.getPeople()!=null){			
				 showuid=bean.getPeople();
				 SharedPreferencesHelper.getInstance(this).setString("uids", showuid);
				 uid=showuid.split(",");				
			     for(int i=0;i<uid.length;i++){
					  try {
						  listbeans=DbHelper.getDB(this)
							        .findAll(Teacher.class, WhereBuilder
								                     .b("uid", "=",uid[i]));
						  parentlistbeans=DbHelper.getDB(this).findAll(Parent.class,WhereBuilder
								   .b("uid","=",uid[i]));
						  friendlistbeans=DbHelper.getDB(this).findAll(Friend.class,WhereBuilder.b(
								     "uid", "=", uid[i]));
			    	  if(listbeans!=null){
						for(int a=0;a<listbeans.size();a++){
							shownamelist.add(listbeans.get(a).getRealname());						
						}	
						  }
			    	  if(parentlistbeans!=null){
						for(int b=0;b<parentlistbeans.size();b++){
							shownamelist.add(parentlistbeans.get(b).getRealname());
						}
			    	  }
			    	  if(friendlistbeans!=null){
			    		  for(int c=0;c<friendlistbeans.size();c++){
			    			   shownamelist.add(friendlistbeans.get(c).getNickname());
			    		  }		    		
			    	  }
					} catch (DbException e) {
						e.printStackTrace();
					}
				  }		
			      
				  nameadapter=new ServiceShowActorNameAdapter(ServiceScheduleWriteActivity.this, (ArrayList<String>)removeDuplicate(shownamelist));
			      nameadapter.setListener(this);
				  shownamegrid.setAdapter(nameadapter);
			      nameadapter.notifyDataSetChanged();			      
			  }else{
				  nameadapter=new ServiceShowActorNameAdapter(ServiceScheduleWriteActivity.this, (ArrayList<String>)removeDuplicate(shownamelist));
				  shownamegrid.setAdapter(nameadapter);
			      nameadapter.notifyDataSetChanged();
			  }
			 }
			  timetv.setText(bean.getDay()+"  "+bean.getTime());
			  actortv.setText("请填写参与者");		
			  remindtv.setText(type);
		
			  if(bean.getTheme()!=null&&!bean.getTheme().equals("null")){	
				  int s=0;
				  for(int i=0;i<theme.length;i++){
					  if(theme[i].equals(bean.getTheme())){
						  s=i;
					  }
				  }
					ServiceScheduleThemeBean  themebean=null;
					themelist.clear();
					 for(int x=0;x<8;x++){
						 themebean=new ServiceScheduleThemeBean();
						 themebean.setImgsrc(themehover[x]);
						 themebean.setTheme(theme[x]);
						 themebean.setColor(this.getResources().getColor(R.color.service_write_schedule_show_tv));

						 if(x==s){
							 themebean.setImgsrc(imgsrc[s]);
							 themebean.setTheme(theme[s]);
							 themebean.setColor(this.getResources().getColor(R.color.service_write_schedule_index_tv));
						 }
					     themelist.add(themebean);
					    }				
					 themeadapter.setData(themelist);
			  }
					  					  			 
			  if(comments!=null && !comments.equals(" ")&&comments.length()!=0){							
					  commentstv.setText(comments);				  		
			  }else{
				  commentstv.setText("请填写备注");
			  }			
			  tv_right.setText("编辑");
			  tv_right.setVisibility(View.VISIBLE);
			  tv_headerTitle.setText("编辑日程");
		      iv_right.setVisibility(View.GONE);
		      gridview.setEnabled(false);
			  gridview.setFocusable(false);
			  gridview.setClickable(false);
			  showthemiv.setVisibility(View.INVISIBLE);
		        } 
		 }else if(state.equals("homeschedule")){
			    Calendar calendars=Calendar.getInstance();
			    calendar.setTimeInMillis(System.currentTimeMillis());
				int Year=calendars.get(Calendar.YEAR);
				int Month=calendars.get(Calendar.MONTH)+1;
				int data=calendars.get(Calendar.DAY_OF_MONTH);
				int hour=calendars.get(Calendar.HOUR_OF_DAY);
				int minute=calendars.get(Calendar.MINUTE);
				String Years=String.valueOf(Year);
				String Months=String.valueOf(Month);
				String datas=String.valueOf(data);
				String hours=hour+"";
				String minutes=minute+"";
			    if(Months.length()==1){
			    	Months="0"+Months;
			    }
			    if(datas.length()==1){
			    	datas="0"+datas;
			    }
			    if(hour<10){
			    	hours="0"+hours;
			    }
			    if(minute<10){
			    	minutes="0"+minutes;
			    }
			  String time=Years+"-"+Months+"-"+datas+"  "+hours+":"+minutes;
			 timetv.setText(time);			 			
			 actortv.setText("请选择参与者");
			 remindtv.setText("准时提醒");
			 tv_headerTitle.setText("新建日程");
			 commentstv.setText("请填写备注");
			 shownamegrid.setClickable(false);
			 shownamegrid.setFocusable(false);
			 shownamegrid.setEnabled(false);
			 tv_right.setVisibility(View.VISIBLE);
			 tv_right.setText("保存");
			 iv_right.setVisibility(View.GONE);
		   	 showthemiv.setVisibility(View.INVISIBLE);
		 }	 
	 else if(state.equals("notifischedule")){		
	        if(bean.getTime()!=null){
	          timetv.setText(bean.getDay()+"  "+bean.getTime());	
	          }	 					
			 String type=this.getIntent().getStringExtra("type");
//	         String comments=this.getIntent().getStringExtra("comments");
	         String themes=this.getIntent().getStringExtra("theme");
			 remindtv.setText(type);		 			       
			  if(themes!=null&&!themes.equals("null")){					 
				  for(int i=0;i<theme.length;i++){
					  if(theme[i].equals(themes)){
						  themenum=i;
					  }
				  }
					ServiceScheduleThemeBean  themebean=null;
					themelist.clear();
					 for(int x=0;x<8;x++){
						 themebean=new ServiceScheduleThemeBean();
						 themebean.setImgsrc(themehover[x]);
						 themebean.setTheme(theme[x]);
						 themebean.setColor(this.getResources().getColor(R.color.service_write_schedule_show_tv));

						 if(x==themenum){
							 themebean.setImgsrc(imgsrc[themenum]);
							 themebean.setTheme(theme[themenum]);
							 themebean.setColor(this.getResources().getColor(R.color.service_write_schedule_index_tv));
						 }
					     themelist.add(themebean);
					    }				
					 themeadapter.setData(themelist);
			  }
			 	 				  
			  if(bean.getNote()!=null && !bean.getNote().equals(" ")){				 				
			      commentstv.setText(bean.getNote());				  	
			  }else{
				  commentstv.setText("未填写备注");
			  }				 			 						 
			 showName();			 
			 shownamegrid.setClickable(false);
			 shownamegrid.setFocusable(false);
			 shownamegrid.setEnabled(false);
			 gridview.setClickable(false);
			 gridview.setFocusable(false);
			 gridview.setEnabled(false);
			 tv_right.setVisibility(View.GONE);
			 iv_right.setVisibility(View.GONE);
			 actortv.setVisibility(View.GONE);
			 tv_headerTitle.setVisibility(View.VISIBLE);
			 tv_headerTitle.setText("查看日程");
	        }
		 
		 gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					 final int i, long l) {
				ServiceScheduleThemeBean  themebean=null;
				  themelist.clear();
				 for(int s=0;s<8;s++){
					 themebean=new ServiceScheduleThemeBean();
					 themebean.setImgsrc(themehover[s]);
					 themebean.setTheme(theme[s]);
					 themebean.setColor(ServiceScheduleWriteActivity.this.getResources().getColor(R.color.service_write_schedule_show_tv));
					 if(s==i){
						 themebean.setImgsrc(imgsrc[i]);
						 themebean.setTheme(theme[i]);
						 themebean.setColor(ServiceScheduleWriteActivity.this.getResources().getColor(R.color.service_write_schedule_index_tv));						 
					 }
				     themelist.add(themebean);
				    }				
				 themeadapter.setData(themelist);
				if(!state.equals("newschedule")&&!state.equals("homeschedule")){
					if(bean!=null){
						  bean.setTheme(theme[i]);	
						  newTheme=theme[i];										
				 						 
						  showthemiv.setVisibility(View.INVISIBLE);
					    }
				}else{
					 showthemiv.setImageResource(imgsrc[i]);
					 showthemiv.setVisibility(View.INVISIBLE);											  							
					 newTheme=theme[i];
				}			
											  
			}

		});
		 
	}  
	
	
	public void showName(){	
		String[] uid=null;
		  if(bean.getPeople()!=null){			
			  showuid=bean.getPeople();
			SharedPreferencesHelper.getInstance(this).setString("uids", showuid);
			uid=showuid.split(",");				
		    for(int i=0;i<uid.length;i++){
				  try {
					  listbeans=DbHelper.getDB(this)
						        .findAll(Teacher.class, WhereBuilder
							                     .b("uid", "=",uid[i]));
					  parentlistbeans=DbHelper.getDB(this).findAll(Parent.class,WhereBuilder
							   .b("uid","=",uid[i]));
					  friendlistbeans=DbHelper.getDB(this).findAll(Friend.class,WhereBuilder.b(
							     "uid", "=", uid[i]));
		    	  if(listbeans!=null){
					for(int a=0;a<listbeans.size();a++){
						shownamelist.add(listbeans.get(a).getRealname());						
					}	
					  }
		    	  if(parentlistbeans!=null){
					for(int b=0;b<parentlistbeans.size();b++){
						shownamelist.add(parentlistbeans.get(b).getNickname());
					}
		    	  }
		    	  if(friendlistbeans!=null){
		    		  for(int c=0;c<friendlistbeans.size();c++){
		    			   shownamelist.add(friendlistbeans.get(c).getNickname());
		    		  }
		    		
		    	  }
				} catch (DbException e) {
					e.printStackTrace();
				}
			  }
  
			  nameadapter=new ServiceShowActorNameAdapter(ServiceScheduleWriteActivity.this,   (ArrayList<String>)removeDuplicate(shownamelist));
		      nameadapter.setListener(this);
			  shownamegrid.setAdapter(nameadapter);
		      nameadapter.notifyDataSetChanged();
		      
		  }else{
			  nameadapter=new ServiceShowActorNameAdapter(ServiceScheduleWriteActivity.this,   (ArrayList<String>)removeDuplicate(shownamelist));
		      nameadapter.setListener(this);
			  shownamegrid.setAdapter(nameadapter);
		      nameadapter.notifyDataSetChanged();
		  }
	}

	 
		private void prepareView() {		
	       	iv_left.setVisibility(View.VISIBLE);
	       	iv_left.setOnClickListener(this);
	       	iv_right.setVisibility(View.VISIBLE);
	       	iv_right.setOnClickListener(this);
	       	iv_right.setImageResource(R.drawable.btn_top_right_add_selector);
	       	tv_right.setVisibility(View.GONE);
	       	tv_right.setOnClickListener(this);
		    gridview=(GridView) findViewById(R.id.id_service_write_schedule_grid);
		    gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		    Timerl=(RelativeLayout) findViewById(R.id.id_service_schedule_time_rl);
		    actorrl=(RelativeLayout) findViewById(R.id.id_service_schedule_actor_rl);
		    remindrl=(RelativeLayout) findViewById(R.id.id_service_schedule_tixing_rl);
		    commentsrl=(RelativeLayout) findViewById(R.id.id_service_schedule_comments_rl);
		    Timerl.setOnClickListener(this);
		    actorrl.setOnClickListener(this);
		    remindrl.setOnClickListener(this);
		    commentsrl.setOnClickListener(this);
		    
		    timetv=(TextView) findViewById(R.id.id_service_write_schedule_time_tv);
		    actortv=(TextView) findViewById(R.id.id_service_write_schedule_actor_tv);
		    remindtv=(TextView) findViewById(R.id.id_service_write_schedule_tixing_tv);
		    commentstv=(TextView) findViewById(R.id.id_service_write_schedule_comments_tv);
		    
		    actortv2=(TextView) findViewById(R.id.id_service_write_schedule_actor_tv2);
		    actortv3=(TextView) findViewById(R.id.id_service_write_schedule_actor_tv3);
		    
		    deletebtn=(RelativeLayout) findViewById(R.id.id_service_delete_schedule_btn);
		    deletebtn.setOnClickListener(this);
		    deletebtn.setVisibility(View.GONE);

		    shownamegrid=(GridView) findViewById(R.id.id_sendmsg_showname_gridview);
		    shownamerl=(RelativeLayout) findViewById(R.id.id_service_showactor_rl);
		    commentstv.setOnClickListener(this);
		    showthemiv=(ImageView)findViewById(R.id.id_service_showtheme_iv);
		    shownamerl.setBackgroundColor(this.getResources().getColor(R.color.white));
		    shownamegrid.setBackgroundColor(this.getResources().getColor(R.color.white));
		}

       public void showThisTime(ShowWriteTime showtime){
    	   if(state.equals("newschedule")||state.equals("homeschedule")){
    		   showtime.ThisTime("","",state);
    	   }else{    		  
    		   showtime.ThisTime(bean.getDay(),bean.getTime(),state);
    	   }
	        
       }
		
		class MyAdapter extends BaseAdapter{
			
			private LayoutInflater mInflater;
			private Context context;
	        private List<ServiceScheduleThemeBean >list;
			public MyAdapter(Context context,List<ServiceScheduleThemeBean>list){
				this.list=list;
				this.context=context;
				mInflater=LayoutInflater.from(context);				
			}
			
			@Override
			public int getCount() {
				
				return list.size();
			}

			public void setData(List<ServiceScheduleThemeBean>list){
                    this.list=list;
                    
                    this.notifyDataSetChanged();
			}
			@Override
			public Object getItem(int position) {
			
				return position;
			}

			public void setData(){
				
			}
			 
			@Override
			public long getItemId(int position) {
				
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup viewgroup) {
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.inflater_service_write_schedule_, null);
				}
				ImageView iv = ViewHolder.get(convertView, R.id.id_inflater_service_schedule_iv);		
				TextView tv = ViewHolder.get(convertView, R.id.id_inflater_service_schedule_tv);
				tv.setTextSize(17);
				tv.setTextColor(list.get(position).getColor());				
				tv.setText(list.get(position).getTheme());
				iv.setImageResource(list.get(position).getImgsrc());		
				return convertView;
			}
			
		}
		
		
		
	    public String	dateForm(String time){
		
	        String dates=dialogdate.substring(0,dialogdate.indexOf("  "));
			String year=dates.substring(0,4);
			String month=dates.substring(dates.indexOf("-")+1,dates.lastIndexOf("-"));
			String day=dates.substring(dates.lastIndexOf("-")+1);
			if(month.length()==1){
				month="0"+month;			
			}
			if(day.length()==1){
				day="0"+day;
			}
			String date=year+"-"+month+"-"+day;
	    	return date;			
		}
       public String  timeForm(String date){
    		String times=dialogtime.substring(dialogtime.indexOf(" "));
			String hour=times.substring(0,times.indexOf(":")).trim();
			String minute=times.substring(times.lastIndexOf(" ")).trim();
            String ampm=dialogtime.substring(0,2);	
            int hourint=Integer.valueOf(hour);
            if(ampm.equals("下午")){
            	hourint=hourint+12;
            	String houris=String.valueOf(hourint);
            	hour=houris;
            }
            if(ampm.equals("晚上")){
              if(hourint>6){
                hourint=hourint+12;
            	String houris=String.valueOf(hourint);
            	hour=houris;
            	}
            }
			if(hour.length()==1){
				hour="0"+hour;
			}					
			if(minute.length()==1){
				minute="0"+minute;
			}
			String time=hour+":"+minute;
    	   return time;
    	   
       }
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) { // 如果是点击新建进入到界面，没有编辑界面可选
            case R.id.right_btn:
                break;
            case R.id.right_tv:
                if (state.equals("newschedule") || state.equals("homeschedule")) { // 判断是否有值
                    if (newTheme!=null) {
                    // 保存的uid可以随意
                    showLoadingDialog("正在保存日程...");
                    String people = null;
                    if (uids!=null){
                        people = uids;
                        if (people.endsWith(",")) {
                            people = people.substring(0, people.length() - 1);
                        }
                    } else {
                        people = " ";
                    }

                    if (namelist!=null && namelist.size()!=0) {
                        StringBuffer buffer = new StringBuffer();
                        for (int index = 0; index < namelist.size(); index++) {
                            if (index == namelist.size() - 1) {
                                buffer.append(namelist.get(index).replace(",",""));
                            }else{
                                buffer.append(namelist.get(index).replace(",","")).append(",");
                            }
                        }
                        realnames = buffer.toString();
                    }

                    String time = timetv.getText().toString();
                    final String showdate = time.substring(0,time.indexOf("  "));
                    final String showtime = time.substring(time.indexOf("  ")).trim();
                    if (newTheme == null) {
                        newTheme = " ";
                    }
                    if (comment == null) {
                       comment = " ";
                    }
                        //int uid, int action, int i, String day, String time, String theme, String note,
                        //int remind, String people, String uids, String realnames, final OnAppRequestListener listener

                    AppServer.getInstance().UploadSchedule(accountInfo.getUid(), 0, 0, showdate, showtime, newTheme, comment, typeflag, people,
                            people, realnames ,new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            try {
                            if(code==0){
                                  String flag="0";
                                  int sheid=(Integer)obj;
                                  bean=new SchedulesBean(sheid,showdate,showtime,newTheme,comment,type,uids,null,flag,0,accountInfo.getUid());
                                  bean.setDelete("0");
                                  bean.setSheid(sheid);
                                  bean.setRealnames(realnames);
                                     try {
                                        DbHelper.getDB(ServiceScheduleWriteActivity.this).save(bean);
                                    } catch (DbException e1) {
                                        e1.printStackTrace();
                                    }
                                 Toast.makeText(ServiceScheduleWriteActivity.this, "日程已保存", Toast.LENGTH_LONG).show();
                                 StringToInt(showdate, showtime, 0,comment,type,true,newTheme);
                                 Intent intents=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);
                                 intents.putExtra("showdate", showdate);
                                 startActivity(intents);
                                 Intent intentservice=new Intent(ServiceScheduleWriteActivity.this,ScheduleServiceUnbind.class);
                                 intentservice.putExtra("realnames",realnames);
                                 intentservice.putExtra("action",0);
                                 startService(intentservice);
                                 cancelLoadingDialog();
                                 ServiceScheduleWriteActivity.this.finish();
                                }else{//上传失败保存至数据库，开启线程
                                    String flag="-1";
                                    bean=new SchedulesBean(sheid,showdate,showtime,newTheme,comment,type,uids,null,flag,0,accountInfo.getUid());
                                    bean.setDelete("0");
                                    DbHelper.getDB(ServiceScheduleWriteActivity.this).save(bean);
                                    Intent intents=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);

                                    Toast.makeText(ServiceScheduleWriteActivity.this, "日程已保存", Toast.LENGTH_LONG).show();
                                    StringToInt(showdate, showtime, 0,comment,type,true,newTheme);
                                    intents.putExtra("showdate", showdate);
                                     startActivity(intents);
                                    ServiceScheduleWriteActivity.this.finish();
                                    cancelLoadingDialog();
                                }
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                        }
                    });
				}else{
			    	Toast.makeText(this, "请选择主题", Toast.LENGTH_LONG).show();
			    }
			    }
								
				if(!state.equals("newschedule")&&!state.equals("homeschedule")){				
					iv_right.setVisibility(View.GONE);
					tv_right.setVisibility(View.VISIBLE);
					if(deletestate.equals("write")){
						if(bean.getTheme()!=null){
                          showLoadingDialog("正在保存日程..");
						  tv_right.setText("编辑");
						  gridview.setEnabled(false);
						  gridview.setFocusable(false);
						  gridview.setClickable(false);
						  deletestate="delete";								 
						  deletebtn.setVisibility(View.GONE);	
                        	/**在更新数据时，先判断是否有值在进行更新**/
                          	if(type!=null){
                        	  bean.setReminds(type);
                             }else{
                            	 bean.setReminds(" "); 
                             }
                        	if(comment!=null){
                        		bean.setNote(comment);
                        	}else{
                        		if(comments==null){
                        			 bean.setNote(" "); 
                        		}else{
                        			 bean.setNote(comments); 
                        		}
                           	
                            }                       	
                        	if(dialogtime!=null){
                        		bean.setTime(dialogtime);                 		
                        	}
                        	
                        	if(dialogdate!=null){
                        		bean.setDay(dialogdate);   
                        	} 
                        	if(uids!=null){
                                if (uids.endsWith(",")) {
                                    uids = uids.substring(0, uids.length() - 1);
                                }
                        	   bean.setPeople(uids);
                        	   bean.setUids(uids);
                        	}else{
                        		if(showuid==null){
                        			 bean.setPeople(" "); 
                        			  bean.setUids(" ");
                        		}else{
                        			 bean.setPeople(showuid);
                        			  bean.setUids(showuid);
                        		}
                              	
                               }
                        	
                        	  if(namelist!=null&&namelist.size()!=0){
									StringBuffer buffer=new StringBuffer();							       	
								    	for(int index=0;index<namelist.size();index++){
								    		if(index==namelist.size()-1){
								    			   buffer.append(namelist.get(index).replace(",",""));
								    		}else{
								    			   buffer.append(namelist.get(index).replace(",","")).append(",");
								    		}					          		    	     		   			    	    
								    	  }	
									  realname= buffer.toString();
									  bean.setRealnames(realname);
								   }
                        	if(dialogdate==null){						
								StringToInt(statetime, datetime, 3, comment, type, true,bean.getTheme());
							}else{
								StringToInt(dialogdate, dialogtime, 3, comment, type, true,bean.getTheme());
							}			
                    
                        	 AppServer.getInstance().UploadSchedule(accountInfo.getUid(),1,bean.getSheid(),bean.getDay(), 
                        			           bean.getTime(), bean.getTheme(), bean.getNote(), typeflag, 
                        			                bean.getPeople(), bean.getPeople(),realname, new OnAppRequestListener() {
						      @Override
						      public void onAppRequest(int code, String message, Object obj) {
						    	  if(code==0){
						    	    	try {
											DbHelper.getDB(ServiceScheduleWriteActivity.this).update(bean);
										   } catch (DbException e) {
											e.printStackTrace();
											}	
						    	    	
											Intent intentservice=new Intent(ServiceScheduleWriteActivity.this,ScheduleServiceUnbind.class);
							  				intentservice.putExtra("action",1);
							  			    intentservice.putExtra("realnames",realname);
							  				startService(intentservice);	
							  				Toast.makeText(ServiceScheduleWriteActivity.this, "日程已更新", Toast.LENGTH_LONG).show();
							  				Intent intents=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);
											intents.putExtra("showdate", bean.getDay());
											startActivity(intents);
											cancelLoadingDialog();
										    ServiceScheduleWriteActivity.this.finish();
								        }else{
										try {
										    bean.setFlag("-1");
											DbHelper.getDB(ServiceScheduleWriteActivity.this).update(bean);
											      } catch (DbException e) {
												     e.printStackTrace();
											        }
											Toast.makeText(ServiceScheduleWriteActivity.this, "日程已更新", Toast.LENGTH_LONG).show();
											Intent intents=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);
											intents.putExtra("showdate", bean.getDay());
											startActivity(intents);
											cancelLoadingDialog();
										    ServiceScheduleWriteActivity.this.finish();
										}								    	  
								  }
								});                   																	
							
						
                        }else{
							Toast.makeText(this, "请选择主题", Toast.LENGTH_LONG).show();
						}						 
					}else{
						tv_right.setText("保存");
						deletestate="write";
						gridview.setEnabled(true);
						gridview.setFocusable(true);
						gridview.setClickable(true);
					    deletebtn.setVisibility(View.VISIBLE);
				    	 }			    
				  }
				 
				break;
			case R.id.id_service_delete_schedule_btn:
				if(deletestate.equals("write")){							        		
						try {
							boolean flag;
							try {
								flag = DbHelper.getDB(this).tableIsExist(Class.forName("com.yey.kindergaten.bean.SchedulesBean"));
								if(flag){		
									showDialog("删除日程", "您确定要删除这条日程吗？", "确定", new DialogInterface.OnClickListener() {								
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
										    try {
										    	if(!state.equals("notifischedule")){
										    	StringToInt("2014-07-14", "13:54", 1, "你好", "", false,null);									
										    	}
										    	AppServer.getInstance().UploadSchedule(accountInfo.getUid(),2,bean.getSheid()," ", " ", " ",											
														" ", 1, " ","","", new OnAppRequestListener() {								
											    	    @Override
												  public void onAppRequest(int code, String message, Object obj) {
											    	  	  if(code==0){
																try {
																	DbHelper.getDB(ServiceScheduleWriteActivity.this).delete(SchedulesBean.class, WhereBuilder.b("localsheid", "=", bean.getLocalsheid()));
																   } catch (DbException e) {									
																	e.printStackTrace();
																}
																Intent intentservice=new Intent(ServiceScheduleWriteActivity.this,ScheduleServiceUnbind.class);
												  				intentservice.putExtra("action",2);
												  				startService(intentservice);	
//												  			    Toast.makeText(ServiceScheduleWriteActivity.this, "删除成功", Toast.LENGTH_LONG).show();
															    Intent intentdelete=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);
															    intentdelete.putExtra("showdate", bean.getDay());
															    startActivity(intentdelete);
															    ServiceScheduleWriteActivity.this.finish();
															}else{
																try {
																   bean.setDelete("-1");													   
																   DbHelper.getDB(ServiceScheduleWriteActivity.this).update(bean);
																   } catch (DbException e) {									
																	e.printStackTrace();
																   }
															    Toast.makeText(ServiceScheduleWriteActivity.this, "删除成功", Toast.LENGTH_LONG).show();
															    Intent intentdelete=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);
															    intentdelete.putExtra("showdate", bean.getDay());
															    startActivity(intentdelete);
															    ServiceScheduleWriteActivity.this.finish();
															}
																			}
																		});
										    } catch (Exception e) {
									    		e.printStackTrace();
											}
											
										}
								});												   							      
						     }
				         	} catch (ClassNotFoundException e) {		
								     e.printStackTrace();
							      }																												
						} catch (DbException e) {		
							e.printStackTrace();
						}
				}
				break;
			case R.id.id_service_schedule_actor_rl:		
				final Intent intents;
				List<Friend>friendbean=null;
				if(tv_right.getText().toString().equals("保存")||state.equals("newschedule")){
				 	intents=new Intent(ServiceScheduleWriteActivity.this, ServiceScheduleActorActivity.class);						 
				    friendbean=contants.getFriends();	   
				    if(true){					     
						        intents.putStringArrayListExtra(AppConstants.SERVICE_SHOW_PARENR, parentlist);
						        intents.putStringArrayListExtra(AppConstants.SERVICE_SHOW_PARENR, parentlist);
								intents.putStringArrayListExtra(AppConstants.SERVICE_SHOW_TEACHER,teacherlist);
								intents.putStringArrayListExtra(AppConstants.SERVICE_SHOW_FRIEND, friendlist);
//								String uid=SharedPreferencesHelper.getInstance(ServiceScheduleWriteActivity.this).getString("uids", showuid);									          
						        if(uids!=null){ //重新传入的uid       	 
						        	intents.putExtra(AppConstants.SERVICE_SHOW_DB_TEACHER, uids);
						        }else {//第一次选择时候的uid
						    	 if(showuid!=null){
						    	 intents.putExtra(AppConstants.SERVICE_SHOW_DB_TEACHER, showuid);
						    	 }							     
							      }
						      startActivityForResult(intents, 4);

				     }
				}			
				break;
			case R.id.id_service_schedule_time_rl:
				if(tv_right.getText().toString().equals("保存")&&!state.equals("newschedule")&&!state.equals("homeschedule")){			
		        	PhotoInfalteDialog dialog=new PhotoInfalteDialog(ServiceScheduleWriteActivity.this, R.layout.inflater_service_schedule_select_time,handler,"service");			       		        	
		        	dialog.show();
			
				}
				if(state.equals("newschedule")||state.equals("homeschedule")){
					PhotoInfalteDialog dialog=new PhotoInfalteDialog(this, R.layout.inflater_service_schedule_select_time,handler,"service");
			        dialog.show();
				}
				
				break;
			case R.id.id_service_schedule_tixing_rl:
				if(tv_right.getText().toString().equals("保存")&&!state.equals("newschedule")){
	                   new PhotoDialog(this, handler).show();
				}
				if(state.equals("newschedule")){
					   new PhotoDialog(this, handler).show();
				}
				break;
			case R.id.id_service_write_schedule_comments_tv:
			case R.id.id_service_schedule_comments_rl:
				if(tv_right.getText().toString().equals("保存")&&!state.equals("newschedule")&&!state.equals("homeschedule")){
					   intent=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleComments.class);
				    if(comments!=null&&!comments.equals("请填写备注")&&comments.length()!=1){			
				    	intent.putExtra("comm", commentstv.getText().toString());
				      }
					    startActivityForResult(intent, 3);				    
					}
					if(state.equals("newschedule")||state.equals("homeschedule")){
						intent=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleComments.class);
					    if(comment!=null&&!comment.equals("请填写备注")&&comment.length()!=1){	
					    	intent.putExtra("comm", comment);
					    }
						startActivityForResult(intent, 3);
					}
                break;
			case R.id.left_btn:
				if(!state.equals("homeschedule")){
					   Intent intentback=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);
					   startActivity(intentback);
					   this.finish();}
					else if(switchtype.equals(AppConfig.SWITCH_TYPE_HOME)){
						Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
						intentback.putExtra("type", AppConfig.SWITCH_TYPE_HOME);
					    startActivity(intentback);
						this.finish();
					}else if(switchtype.equals(AppConfig.SWITCH_TYPE_SERVICE)){
						Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
						intentback.putExtra("type", AppConfig.SWITCH_TYPE_SERVICE);
					    startActivity(intentback);
						this.finish();
					}else if(switchtype.equals(AppConfig.SWITCH_TYPE_CONTACTS)){
						Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
						intentback.putExtra("type", AppConfig.SWITCH_TYPE_CONTACTS);
					    startActivity(intentback);
						this.finish();
					}else if(switchtype.equals(AppConfig.SWITCH_TYPE_ME)){
						Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
						intentback.putExtra("type", AppConfig.SWITCH_TYPE_ME);
					    startActivity(intentback);
						this.finish();
					}		
				break;
			}
			
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode==KeyEvent.KEYCODE_BACK){
				if(!state.equals("homeschedule")){
				   Intent intentback=new Intent(ServiceScheduleWriteActivity.this,ServiceScheduleActivity.class);
				   startActivity(intentback);
				   this.finish();}
				else if(switchtype.equals(AppConfig.SWITCH_TYPE_HOME)){
					Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
					intentback.putExtra("type", AppConfig.SWITCH_TYPE_HOME);
				    startActivity(intentback);
					this.finish();
				}else if(switchtype.equals(AppConfig.SWITCH_TYPE_SERVICE)){
					Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
					intentback.putExtra("type", AppConfig.SWITCH_TYPE_SERVICE);
				    startActivity(intentback);
					this.finish();
				}else if(switchtype.equals(AppConfig.SWITCH_TYPE_CONTACTS)){
					Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
					intentback.putExtra("type", AppConfig.SWITCH_TYPE_CONTACTS);
				    startActivity(intentback);
					this.finish();
				}else if(switchtype.equals(AppConfig.SWITCH_TYPE_ME)){
					Intent intentback=new Intent(ServiceScheduleWriteActivity.this,MainActivity.class);
					intentback.putExtra("type", AppConfig.SWITCH_TYPE_ME);
				    startActivity(intentback);
					this.finish();
				}
			}
			return super.onKeyDown(keyCode, event);
		}
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
			if(resultCode==RESULT_OK){
				if(requestCode==1){//设置日程时间
				Bundle bundle=data.getExtras();
				updatedate=bundle.getString("date");
				updatetime=bundle.getString("time");
				timetv.setText(updatedate+"  "+updatetime);
				}
				if(requestCode==2){//设置提醒方式
				Bundle bundle=data.getExtras();
		    	type=bundle.getString("remindtype");		    	
				remindtv.setText(type);
				}
			 	if(requestCode==3){
			 		 shownamerl.setBackgroundColor(this.getResources().getColor(R.color.white));
				     Bundle bundle=data.getExtras();
					 comment=bundle.getString("comments");																	  					 											  					 
						  if(comment!=null&&!comment.equals(" ")){
							  commentstv.setText(comment);
						  }else{
							  commentstv.setText("请填写备注");
						  }												  			   		 
				}		         
			 	if(requestCode==4){
			 	   Bundle bundle=data.getExtras();
			       selectlist=bundle.getStringArrayList("selectlist");
			       namelist = bundle.getStringArrayList("namelist");
			       parentlist=bundle.getStringArrayList(AppConstants.SERVICE_SHOW_PARENR);
			       teacherlist=bundle.getStringArrayList(AppConstants.SERVICE_SHOW_TEACHER);
			       friendlist=bundle.getStringArrayList(AppConstants.SERVICE_SHOW_FRIEND);
			       uids=null;
			       selectnamelist.clear();	
			       if(selectlist!=null&&selectlist.size()!=0){	
			    	StringBuffer buffer=new StringBuffer();
			       	
			    	for(int index=0;index<selectlist.size();index++){		               
		                  buffer.append(selectlist.get(index));			    	     		   			    	    
			    	      }			    	
			    	 uids= buffer.toString();	
			    	 //数据库中的属性的原因，把list做成String存入数据库，在代码中对数据库中进行操作
			    	 //在这个把这个标示符遍历成数组
			    	 String[] uid=uids.split(",");
			    	 for(int n=0;n<uid.length;n++){	 
			         try {
						List<Teacher>list=DbHelper.getDB(this).findAll(Teacher.class, WhereBuilder.b("uid", "=",uid[n]));
                        List<Parent>childlist=DbHelper.getDB(this).findAll(Parent.class,WhereBuilder.b("uid", "=", uid[n]));
					    List<Friend>friendlist=DbHelper.getDB(this).findAll(Friend.class,WhereBuilder.b("uid", "=", uid[n]));
					    String teacheruid=null;
					    String childuid=null;
					    String frienduid=null;		   
//					    if(list!=null&&list.size()!=0&&(n<list.size()||n==0)){
//					    	teacheruid =String.valueOf(list.get(n).getUid());                       
//                        }
//                        if(childlist!=null&&childlist.size()!=0&&(n<childlist.size()||n==0)){
//                           childuid=String.valueOf(childlist.get(n).getUid());
//                         }
//                        if(friendlist!=null&&friendlist.size()!=0&&(n<friendlist.size()||n==0)){
//                          frienduid=String.valueOf(friendlist.get(n).getUid());
//                          }
                                             
					    if(list!=null){
                        for(int a=0;a<list.size();a++){                         	
							  selectnamelist.add(list.get(a).getRealname());
						  }
						 }					                         
                       
						if(childlist!=null){
						  for(int b=0;b<childlist.size();b++){							 
							  selectnamelist.add(childlist.get(b).getRealname());
						  }
						}
						
						
                        if(friendlist!=null){
							  for(int b=0;b<friendlist.size();b++){							 
								  selectnamelist.add(friendlist.get(b).getNickname());
							  }
							}						
			    		 } catch (DbException e) {		
							   e.printStackTrace();
						}			    		 
			    	 }			    	 
			        }
			       
			       
			       actortv.setVisibility(View.GONE);
			       shownamerl.setVisibility(View.VISIBLE);
			       shownamelist.clear();
			       nameadapter=new ServiceShowActorNameAdapter(ServiceScheduleWriteActivity.this, shownamelist);
			       nameadapter.setListener(this);
			       shownamegrid.setAdapter(nameadapter);			   
			       nameadapter.notifyDataSetChanged();
			       removeDuplicate(selectnamelist);
			       nameadapter=new ServiceShowActorNameAdapter(ServiceScheduleWriteActivity.this, selectnamelist);
			       shownamegrid.setAdapter(nameadapter);
	            	
			 	}
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		
		/**
		 *去掉list中的重复项
		 * @param list
		 * @return
		 */
		public   static  List<String>  removeDuplicate(List<String> list)  {     
		    HashSet<String> h  =   new  HashSet<String>(list);     
		    list.clear();     
		    list.addAll(h);     
		    return list;     
		}   
		/**
		 * 将string类型的时间转化为calendar的类型并用来设置提醒方式
		 * @param date 日期
		 * @param time 时间
		 * @param comments 内容
		 * @param id  表示是编辑更新，删除，保存的状态。3表示更新
		 * @param type 提醒方式
		 * @param flag  判断是否发送广播
		 */
		private void StringToInt(String date,String time,int id,String comments,String type,boolean flag,String theme){					
			    String year=date.substring(0,4);
			    String month=date.substring(5,7);
			    if(month.substring(0, 1).contains("0")){
			    	month=month.substring(1,2);
			    }
			    String day=date.substring(8,10);
			    if(day.substring(0, 1).contains("0")){
                      day=day.substring(1,2);			    	
			    }
			    time=time.trim();
                String hour=time.substring(0,2);
                if(hour.substring(0, 1).contains("0")){
                	hour=time.substring(1,2);
                }
                String minute=time.substring(3,5);
                if(minute.substring(0, 1).contains("0")){
                	minute=minute.substring(1,2);
                }
			    int interyear=Integer.valueOf(year);
			    int intermonth=Integer.valueOf(month);
			    int interday=Integer.valueOf(day);
			    int interhour=Integer.valueOf(hour.trim());
			    int interminute=Integer.valueOf(minute.trim());		
			    Calendar canlendar=Calendar.getInstance();
			    canlendar.setTimeInMillis(System.currentTimeMillis());
			    //获得设置时间的操作对象  
			    canlendar.set(Calendar.YEAR,interyear);
			    canlendar.set(Calendar.MONTH,intermonth-1);
			    canlendar.set(Calendar.DAY_OF_MONTH,interday);
			    canlendar.set(Calendar.HOUR_OF_DAY,interhour);			   
			   	canlendar.set(Calendar.MINUTE,interminute);			    
			    canlendar.set(Calendar.SECOND, 0);
			    canlendar.set(Calendar.MILLISECOND, 0);
                AlarmManager am=null;
                PendingIntent pendingIntent = null ;
                int sheids=0;               
        	    ArrayList<SchedulesBean>beans;
			    try {
					beans=(ArrayList<SchedulesBean>) DbHelper.getDB(this).findAll(SchedulesBean.class);				
					for(int index=0;index<beans.size();index++){
						String days=beans.get(index).getDay();
						String times=beans.get(index).getTime();
						if(days.equals(date)&&
								times.equals(time)){
							sheids=beans.get(index).getLocalsheid();
						}					
					}				
			    } catch (DbException e) {					
					e.printStackTrace();
				}
                /* 获取闹钟管理的实例 */
                am = (AlarmManager) getSystemService(ALARM_SERVICE);         
               if(id==1||id==0){
                   if(flag==true){
                	    Intent intent = new Intent(ServiceScheduleWriteActivity.this,
                                 ServiceScheduleRecevier.class);
         			    Bundle bundler=new Bundle();  			   
         			    bundler.putInt("id", sheid);
         			    bundler.putString("updatetime", date);
         			    bundler.putString("update", time);
         			    bundler.putString("checkup", "1||2");
         			    bundler.putString("comments", comments);
         			    bundler.putSerializable("bean", bean);
         			    bundler.putString("type", type);
         			    bundler.putBoolean("flag", flag); 
         			    if(theme!=null){
         			     bundler.putString("theme",theme );
         			    }else{
         			      bundler.putString("theme", "开会");
         			    }			   
         			    intent.putExtras(bundler);
         			    pendingIntent= PendingIntent         			    		
       			    		.getBroadcast(ServiceScheduleWriteActivity.this, sheids+10,intent, PendingIntent.FLAG_CANCEL_CURRENT);				  
         			    alarmbean.setIntent(pendingIntent);
          			    alarmbean.setRequestcode(sheids);
         			    alarmlist.add(alarmbean);         	
         			    //checkintnent=pendingIntent;
         			    bundler.putParcelable("intent", pendingIntent);
                     
                    if(canlendar.getTimeInMillis()<System.currentTimeMillis()){                   	
//                   	 Toast.makeText(this, "您设置的时间已经过了", Toast.LENGTH_LONG).show();            
                      }else{
                      	 /* 设置闹钟 */
                      am.set(AlarmManager.RTC_WAKEUP, canlendar                                               .getTimeInMillis(), pendingIntent);	               
                     }   
                     if(type.equals("不提醒")){
                     	am.cancel(pendingIntent);
                     }
                     if(type.equals("提前20分钟提醒")){
                    	 
                    	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<1200000){
                    		 am.cancel(pendingIntent);
//                    		 Toast.makeText(this, "您设置的时间已经过了", Toast.LENGTH_LONG).show();
                    		 return;
                    	 }                   	 
                    		 /* 设置闹钟 */
                             am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                    .getTimeInMillis()-1200000, pendingIntent);                    	 
                     }              
                     if(type.equals("提前30分钟提醒")){
                    	 
                    	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<1800000){
                    		 am.cancel(pendingIntent);
//                    		 Toast.makeText(this, "您设置的时间已经过了", Toast.LENGTH_LONG).show();
                    		 return;
                    	 }
                    
                    		 /* 设置闹钟 */
                             am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                    .getTimeInMillis()-1800000, pendingIntent);
                    	 
                     }
                     if(type.equals("提前1小时提醒")){
                    	
                    	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<3600000){
                    		 am.cancel(pendingIntent);
//                    		 Toast.makeText(this, "您设置的时间已经过了", Toast.LENGTH_LONG).show();
                    		 return;
                    	 }
                    
                    		 /* 设置闹钟 */
                             am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                    .getTimeInMillis()-3600000, pendingIntent);
                    	 
                     }
                     if(type.equals("提前1.5小时提醒")){                 	
                    	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<5400000){
                    		 am.cancel(pendingIntent);
//                    		 Toast.makeText(this, "您设置的时间已经过了", Toast.LENGTH_LONG).show();
                    		 return;
                    	 }                	
                    		 /* 设置闹钟 */
                             am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                    .getTimeInMillis()-5400000, pendingIntent);
                    	 
                     }
                     if(type.equals("提前2小时提醒")){
                    	
                    	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<7200000){
                    		 am.cancel(pendingIntent);
//                    		 Toast.makeText(this, "您设置的时间已经过了", Toast.LENGTH_LONG).show();
                    		 return;
                    	 }                   
                    		 /* 设置闹钟 */
                             am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                    .getTimeInMillis()-7200000, pendingIntent);
                     }
                     
                   }else{              
                    int x=0;
                   if(alarmlist!=null&&alarmlist.size()!=0){
                    AlarmBean beanss=new AlarmBean();
                	   for(int i=0;i<alarmlist.size();i++){
                		     if(sheid==alarmlist.get(i).getRequestcode()){
                		    	 beanss=alarmlist.get(i);
                		    	 i=x;
                		     }
                	   }

                	   am.cancel(beanss.getIntent());
                	   alarmlist.remove(x);
                   }   }
               } 
               if(id==3){
            	    if(flag==true){   
            	    	AlarmBean beanss=new AlarmBean();
            	        if(alarmlist!=null&&alarmlist.size()!=0){
                 	   for(int i=0;i<alarmlist.size();i++){
                 		     if(sheid==alarmlist.get(i).getRequestcode()){
                 		    	 beanss=alarmlist.get(i);
                 		     }
                 	   }
                 	        am.cancel(beanss.getIntent());
                 	   }           	       
           			    Intent intents = new Intent(ServiceScheduleWriteActivity.this,
                                   ServiceScheduleRecevier.class);
           			    Bundle bundlers=new Bundle();  			   
           			    bundlers.putInt("id", sheid);
           			    bundlers.putString("updatetime", date);
           			    bundlers.putString("update", time);
           			    bundlers.putString("comments", comments);
           			    bundlers.putBoolean("flag", flag);
           			    bundlers.putString("type", type);
           			    bundlers.putSerializable("bean", bean);
           			    bundlers.putString("checkup", "3");
           			    if(theme!=null){
         			     bundlers.putString("theme",theme );
         			    }else{
         			      bundlers.putString("theme", "开会");
         			    }
           			    intents.putExtras(bundlers);          		    			  
           			    PendingIntent	    pendingIntents= PendingIntent
                                .getBroadcast(ServiceScheduleWriteActivity.this, sheid,intents, 0);	
           			      AlarmBean beannew=new AlarmBean();
           			      beannew.setIntent(pendingIntents);
           			      beannew.setRequestcode(sheid);
           			      alarmlist.add(beannew);
                        if(canlendar.getTimeInMillis()<System.currentTimeMillis()){                           	
                        	Toast.makeText(this, "您设置的时间已经过了", Toast.LENGTH_LONG).show();                      
                        }else{
                                am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                       .getTimeInMillis(), pendingIntents);	                              
                            }
                        if(type.equals("不提醒")){                   
                        	am.cancel(pendingIntents);
                        }
                        if(type.equals("提前20分钟提醒")){                    	    
                        	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<1200000){
                        		 am.cancel(pendingIntents);
//                        		 Toast.makeText(this, "您提前的时间超过了剩余的时间了，请重新选择日程提醒方式", Toast.LENGTH_LONG).show();
                        		 return;
                        	 }
                          	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()==1200000){                      
                                am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                       .getTimeInMillis()-1200000, pendingIntents);
                       	 }
                        }
                        
                        if(type.equals("提前30分钟提醒")){                  	 
                    	   if(canlendar.getTimeInMillis()-System.currentTimeMillis()<1800000){
                    		 am.cancel(pendingIntents);
//                    		 Toast.makeText(this, "您提前的时间超过了剩余的时间了，请重新选择日程提醒方式", Toast.LENGTH_LONG).show();
                    		 return;
                    	 }                             		
                                am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                       .getTimeInMillis()-1800000, pendingIntents);                       	 
                        }
                        
                        if(type.equals("提前1小时提醒")){                 	    
                    	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<3600000){
                    		 am.cancel(pendingIntents);
//                    		 Toast.makeText(this, "您提前的时间超过了剩余的时间了", Toast.LENGTH_LONG).show();
                    		 return;
                    	 }                   	                       		 
                                am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                       .getTimeInMillis()-3600000, pendingIntents);                     	   
                        }
                        
                        
                        if(type.equals("提前1.5小时提醒")){                   	                       	
                       	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<5400000){
                       		am.cancel(pendingIntents);
//                       		 Toast.makeText(this, "您提前的时间超过了剩余的时间了", Toast.LENGTH_LONG).show();
                       		 return;
                       	 }                       
                                am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                       .getTimeInMillis()-5400000, pendingIntents);
                        }
                        
                        if(type.equals("提前2小时提醒")){                          
                          	 if(canlendar.getTimeInMillis()-System.currentTimeMillis()<7200000){
                          		am.cancel(pendingIntents);
//                          		 Toast.makeText(this, "您提前的时间超过了剩余的时间了", Toast.LENGTH_LONG).show();
                          		 return;
                          	 }
                     
                                am.set(AlarmManager.RTC_WAKEUP, canlendar
                                                       .getTimeInMillis()-7200000, pendingIntents);                       	 
                        }
                        
                        
                       } 
               }
                                
//                  /* 设置周期闹 */
//                am.setRepeating(AlarmManager.RTC_WAKEUP, canlendar
//                                         .getTimeInMillis(), (24 * 60 * 60 * 1000),
//                                             pendingIntent);
                             
		}
		
		public void onResume() {
			super.onResume();
			MobclickAgent.onResume(this);
		
			
			}
			public void onPause() {
			super.onPause();
			MobclickAgent.onPause(this);
			}

//			@Override
//			public boolean onTouch(View view, MotionEvent motionevent) {
//				switch (motionevent.getAction()) {
//				
//				case MotionEvent.ACTION_DOWN:
//					if(view.getId()==R.id.id_service_schedule_comments_rl){
//						shownamerl.setBackgroundColor(this.getResources().getColor(R.color.light_grey));
//						commentsrl.setBackgroundColor(this.getResources().getColor(R.color.light_grey));
//					}else if(view.getId()==R.id.id_service_schedule_actor_rl){
//						actorrl.setBackgroundColor(this.getResources().getColor(R.color.light_grey));
//                      if(ServiceScheduleWriteActivity.this.view!=null){
//                    	  ServiceScheduleWriteActivity.this.view.setBackgroundColor(this.getResources().getColor(R.color.light_grey));
//                    	  nameadapter.notifyDataSetInvalidated(); 
//                       }
//					    actorrl.getChildAt(2).setBackgroundColor(this.getResources().getColor(R.color.light_grey));
//					}			
//					break;
//				case MotionEvent.ACTION_MOVE:
//				case MotionEvent.ACTION_UP:
//				case MotionEvent.ACTION_CANCEL:
//					if(view.getId()==R.id.id_service_schedule_comments_rl){
//					shownamerl.setBackgroundColor(this.getResources().getColor(R.color.white));
//					commentsrl.setBackgroundColor(this.getResources().getColor(R.color.white));
//					}else if(view.getId()==R.id.id_service_schedule_actor_rl){
////						shownamegrid.setBackgroundColor(this.getResources().getColor(R.color.white));
//						actorrl.setBackgroundColor(this.getResources().getColor(R.color.white));
//	                      if(ServiceScheduleWriteActivity.this.view!=null){
//	                    	  ServiceScheduleWriteActivity.this.view.setBackgroundColor(this.getResources().getColor(R.color.white));
//	                          nameadapter.notifyDataSetInvalidated();
//	                      }
//	                      actorrl.getChildAt(2).setBackgroundColor(this.getResources().getColor(R.color.white));
//					}
//					break;
//				}
//				return false;
//			}
			@Override
			public void selector(View view) {
				ServiceScheduleWriteActivity.this.view=view;
			}
}
