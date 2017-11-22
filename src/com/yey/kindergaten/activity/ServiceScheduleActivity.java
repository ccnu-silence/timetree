package com.yey.kindergaten.activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AlarmBean;
import com.yey.kindergaten.bean.SchedulesBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.service.ScheduleServiceUnbind;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.DateWidgetDayCell;
import com.yey.kindergaten.util.DateWidgetDayCell.CancelFoucse;
import com.yey.kindergaten.util.DateWidgetDayHeader;
import com.yey.kindergaten.util.DayStyle;
import com.yey.kindergaten.util.SortCompareUtil;
import com.yey.kindergaten.widget.SwipeListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
/**
 * 日程
 * @author zyj
 *
 */
public class ServiceScheduleActivity extends BaseActivity implements OnClickListener, CancelFoucse{
	// 生成日历，外层容器
	private LinearLayout layContent = null;
	private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView  iv_left;
	//用来保存数据给下个界面传值的list
	private List<SchedulesBean>nowMonthList=new ArrayList<SchedulesBean>();//本月日程数据源
	private List<SchedulesBean>clickMonthList=new ArrayList<SchedulesBean>();//点击日期显示日常数据源
	
	private SchedulesBean Bean=new SchedulesBean();
	// 日期变量
	public static Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
    //新建日记返回的date
	private String date;
	//当前操作日期
	private int iMonthViewCurrentMonth = 0;//是否是当前月
	private int iMonthViewCurrentYear = 0;
	//星期日显示在最前端
	private int iFirstDayOfWeek = Calendar.SUNDAY;
    //用来显示点击后当前日期时间
	private String statetime=null;
	//用来判断点击的List显示的时间是否是当前时间
	private String boolti=null;	
	
	private String showtime=null;
	
	private String witime=null;	
	private TextView showtoday;
	private int datenum=0;
	private int Calendar_Width = 0;
	private int Cell_Width = 0;
	private int y=0;
	int x=0;;
    GestureDetector mGestureDetector;  
	DateWidgetDayCell daySelected = null;
	AccountInfo accountInfo;
	// 页面控件
	TextView Top_Date = null;
	ImageView btn_pre_month = null;
	ImageView btn_next_month = null;
	LinearLayout mainLayout = null;
	LinearLayout arrange_layout = null;
	SwipeListView listview=null;
	private boolean flags;
	// 数据源
	ArrayList<String> Calendar_Source = null;
	Hashtable<Integer, Integer> calendar_Hashtable = new Hashtable<Integer, Integer>();
	Boolean[] flag = null;
	ArrayList<SchedulesBean>listbean;	
	Calendar startDate = null;
	Calendar endDate = null;
	private  TextView  titletimetv;
	private RelativeLayout rl_contrnt;
	int dayvalue = -1;
    /**用来判断显示那天的日程**/
    private String showdate=null;
     
	private String monthdate=null;
	
	/**主题图片url列数组*/
    private static final int[]imgsrc={R.drawable.service_schedule_workcheck,R.drawable.service_scheduke_meeting,
    	         R.drawable.service_schedule_activity,R.drawable.service_schedule_dating,R.drawable.service_schedule_traning,
    	               R.drawable.service_scheduke_important,R.drawable.service_schedule_party,R.drawable.service_schedule_others};
   /**主题名称数组*/
    private static final String[]theme={"工作检查","开会","园所活动","约见","培训","重要日子","聚会","其他" };
	public static int Calendar_WeekBgColor = 0;
	public static int Calendar_DayBgColor = 0;
	public static int isHoliday_BgColor = 0;
	public static int unPresentMonth_FontColor = 0;
	public static int isPresentMonth_FontColor = 0;
	public static int isToday_BgColor = 0;
	public static int special_Reminder = 0;
	public static int common_Reminder = 0;
	public static int Calendar_WeekFontColor = 0;
//	MyAdapter adapter;
	 MyAdapter clickadapter;
	String UserName = "";
    private SchedulesBean bean=new SchedulesBean();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// 获得屏幕宽和高，并計算出屏幕寬度分七等份的大小
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int height=display.getHeight();
		 y=height;
		Calendar_Width = screenWidth;
		Cell_Width = Calendar_Width / 7 + 1;

		// 制定布局文件，并设置属性
		mainLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.activity_service_schedule, null);
		// mainLayout.setPadding(2, 0, 2, 0);
		setContentView(mainLayout);
		accountInfo=AppServer.getInstance().getAccountInfo();
		ViewUtils.inject(this);
		prepareView();
		showdate=this.getIntent().getStringExtra("showdate");
		lodadate();
	   
		// 声明控件，并绑定事件
		Top_Date = (TextView) findViewById(R.id.Top_Date);
		btn_pre_month = (ImageView) findViewById(R.id.btn_pre_month);
		btn_next_month = (ImageView) findViewById(R.id.btn_next_month);
		btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
		btn_next_month.setOnClickListener(new Next_MonthOnClickListener());
		rl_contrnt=(RelativeLayout) findViewById(R.id.id_service_show);
	    listview=(SwipeListView) findViewById(R.id.id_service_schedule_content_lv);
	    listview.setSelector(R.drawable.listview_select_item);
	    titletimetv=(TextView) findViewById(R.id.id_service_schedule_title_time);
//	    String YearMonth=Top_Date.getText().toString();
//	    String Year=YearMonth.substring(0,YearMonth.lastIndexOf("年"));
//		String Month=YearMonth.substring(YearMonth.indexOf("年")+1,YearMonth.lastIndexOf("月"));	   	
	
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long l) {			
			
				if(listview.getHidenType()==false){
				listview.setHideType(true);
				Intent intent=new Intent(ServiceScheduleActivity.this,ServiceScheduleWriteActivity.class);
				if(titletimetv.getText().toString().equals(statetime)){					
				if(nowMonthList!=null){					
					nowMonthList.clear();
					nowMonthList.addAll(clickadapter.getData());
					intent.putExtra("state", "showschedule");
					intent.putExtra("time",  nowMonthList.get(position).getDay());
					intent.putExtra("statetime", statetime);
					intent.putExtra("datetime", nowMonthList.get(position).getTime());
					intent.putExtra("comments", nowMonthList.get(position).getNote());
					intent.putExtra("remind", nowMonthList.get(position).getReminds());
					intent.putExtra("sheid", nowMonthList.get(position).getLocalsheid());
					Bundle bundle=new Bundle();
					bundle.putSerializable(AppConstants.Schedule_Bean, nowMonthList.get(position));				
					intent.putExtras(bundle);				
				  }
				}else{
					if(nowMonthList!=null){				
					intent.putExtra("state", "showschedule");					
				    intent.putExtra("time", nowMonthList.get(position).getTime());
					intent.putExtra("statetime", statetime);
					intent.putExtra("datetime", nowMonthList.get(position).getTime());
				    intent.putExtra("comments", nowMonthList.get(position).getNote());
	                intent.putExtra("remind", nowMonthList.get(position).getReminds());
	                intent.putExtra("sheid", nowMonthList.get(position).getLocalsheid());
	        		Bundle bundle=new Bundle();
					bundle.putSerializable(AppConstants.Schedule_Bean, nowMonthList.get(position));
					intent.putExtras(bundle);
					view.setBackgroundColor(ServiceScheduleActivity.this.getResources()
							.getColor(R.color.light_grey));
					}
				}
				startActivity(intent);
				ServiceScheduleActivity.this.finish();
			}else{
				listview.hiddenRight(view);
				listview.setHideType(false);
			}
		  }
		});
	    // 计算本月日历中的第一天(一般是上月的某天)，并更新日历
		calStartDate = getCalendarStartDate();	
		 Calendar calendasr=Calendar.getInstance();
			int startDay=calStartDate.get(Calendar.DAY_OF_MONTH);
			int endDay=calStartDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		    int iDay=endDay-startDay+1;		
			int daynum=calendasr.getActualMaximum(Calendar.DAY_OF_MONTH);
			int all=daynum+iDay;				
		rl_contrnt.addView(generateCalendarMain(getRow(all)));
		rl_contrnt.setLongClickable(true);

		
	    daySelected = updateCalendar(true);		    
						

		if (daySelected != null)
			daySelected.requestFocus();
		ScrollView view = new ScrollView(this);	
		mainLayout.setBackgroundColor(Color.WHITE);	   
		startDate = GetStartDate();//当前的第一天
		calToday = GetTodayDate();//当前第几天		
	     Calendar calendar=Calendar.getInstance();
	     calendar.setTimeInMillis(System.currentTimeMillis());
		int Year=calendar.get(Calendar.YEAR);
		int Month=calendar.get(Calendar.MONTH)+1;
		int data=calendar.get(Calendar.DAY_OF_MONTH);
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		int minute=calendar.get(Calendar.MINUTE);
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
	    String boomon= String.valueOf(calToday.get(Calendar.MONTH)+1);
	    String booday=String .valueOf(calToday.get(Calendar.DAY_OF_MONTH));
	    if(boomon.length()==1){
	    	boomon="0"+boomon;
	    }
	    if(booday.length()==1){
	    	booday="0"+booday;
	    }
	    statetime=calToday.get(Calendar.YEAR)+"-"+boomon+"-"+booday;
	    titletimetv.setText(statetime);
	    witime=Years+"-"+Months+"-"+datas+"  "+hours+":"+minutes;
	    if(listbean!=null){   	
	    	if(showdate!=null){    		
	    			  clickadapter=new MyAdapter(this,listbean,statetime,"first");
		  		      listview.setAdapter(clickadapter);	    			    	     	
	    	}else{
	    		      clickadapter=new MyAdapter(this,listbean,statetime,"first");
	  		          listview.setAdapter(clickadapter);	
	    	}
	  
	    }		
		endDate = GetEndDate(startDate);			
		mainLayout.addView(view);
		// 新建线程
		new Thread() {
			@Override
			public void run() {
				int day = GetNumFromDate(calToday, startDate);			
				if (calendar_Hashtable != null
						&& calendar_Hashtable.containsKey(day)) {
					dayvalue = calendar_Hashtable.get(day);
				}
			}			
		}.start();
		Calendar_WeekBgColor = this.getResources().getColor(
				R.color.Calendar_WeekBgColor);
		Calendar_DayBgColor = this.getResources().getColor(
				R.color.Calendar_DayBgColor);
		isHoliday_BgColor = this.getResources().getColor(
				R.color.isHoliday_BgColor);
		unPresentMonth_FontColor = this.getResources().getColor(
				R.color.unPresentMonth_FontColor);
		isPresentMonth_FontColor = this.getResources().getColor(
				R.color.isPresentMonth_FontColor);
		isToday_BgColor = this.getResources().getColor(R.color.isToday_BgColor);
		special_Reminder = this.getResources()
				.getColor(R.color.specialReminder);
		common_Reminder = this.getResources().getColor(R.color.commonReminder);
		Calendar_WeekFontColor = this.getResources().getColor(
				R.color.Calendar_WeekFontColor);
	}

	private void prepareView() {
	    tv_headerTitle.setText("日程");
	    showtoday=(TextView) findViewById(R.id.showTod);
//	    showtoday.setText("");
       	iv_left.setVisibility(View.VISIBLE);
       	iv_left.setOnClickListener(this);
       	iv_right.setVisibility(View.VISIBLE);
       	iv_right.setImageResource(R.drawable.btn_top_right_add_selector);
       	iv_right.setOnClickListener(this);
	}

	public int  getRow(int all){		
	
		if(all>28&&all<36){//如果是
			x=5;
		}else if(all<28||all==28){
			x=4;
		}else{
			x=6;
		}
		
		return x;
		 
	}
	
	private void lodadate(){
		try {		
			listbean= (ArrayList<SchedulesBean>) DbHelper.getDB(this).findAll(SchedulesBean.class);
			if(listbean!=null&&listbean.size()!=0){			
			       if(listbean.get(0).getUid()!=accountInfo.getUid()){
			        DbHelper.getDB(this).dropTable(SchedulesBean.class);
			        listbean.clear();			       
			       }
			   }		
	     	     if(listbean==null||listbean.size()==0){	     	    
	             	AppServer.getInstance().getSchduleInfo(accountInfo.getUid(), new OnAppRequestListener() {							
						@Override
						public void onAppRequest(int code, String message, Object obj) {
						   if(code==0){
							 listbean  =new ArrayList<SchedulesBean>();
							   SchedulesBean[] beans=(SchedulesBean[])obj;
							   for(int i=0;i<beans.length;i++){	
								   beans[i].setDelete("0");	
								   beans[i].setFlag("0");
								   beans[i].setUid(accountInfo.getUid());
									   switch (beans[i].getRemind()) {
									   case 0:
										   beans[i].setReminds("不提醒");
										   break;
									   case 1:
										   beans[i].setReminds("准时提醒");
											break;
										case 2:
											beans[i].setReminds("提前20分钟提醒");
											break;
										case 3:
											beans[i].setReminds("提前30分钟提醒");
											break;
										case 4:
											beans[i].setReminds("提前1小时提醒");
											break;
										case 5:
											beans[i].setReminds("提前1.5小时提醒");
											break;
										case 6:
											beans[i].setReminds("提前2小时提醒");
											break;
										} 	
							     		 
								      listbean.add(beans[i]);								 
							         }	 
							   try {
                                      if(listbean!=null && listbean.size()>0){
                                          DbHelper.getDB(ServiceScheduleActivity.this).saveAll(listbean);
                                      }
								     } catch (DbException e) {
									e.printStackTrace();
								   }
					         try {									
						       listbean= (ArrayList<SchedulesBean>) DbHelper.getDB(ServiceScheduleActivity.this).findAll(SchedulesBean.class);							        								   
							if(listbean!=null&&listbean.size()!=0){
								clickadapter =new MyAdapter(ServiceScheduleActivity.this, listbean," ","first");
								listview.setAdapter(clickadapter);	
							}								    						    								    
								         } catch (DbException e) {
								    	e.printStackTrace();
							    	  }
							     daySelected = updateCalendar(true);							
						        }else{
						         daySelected = updateCalendar(true);		
						   }							
						}
					});
	            }		
			} catch (DbException e) {			
				e.printStackTrace();
			} 
	}


	private Thread mThread=new Thread(new Runnable() {		
		@Override
		public void run() {
			while(true){
         	AppServer.getInstance().getSchduleInfo(accountInfo.getUid(), new OnAppRequestListener() {							
				@Override
				public void onAppRequest(int code, String message, Object obj) {
				   if(code==0){
					 listbean  =new ArrayList<SchedulesBean>();
					   SchedulesBean[] beans=(SchedulesBean[])obj;
					   for(int i=0;i<beans.length;i++){	
						   beans[i].setDelete("0");	
						   beans[i].setFlag("0");
						   beans[i].setUid(accountInfo.getUid());
							   switch (beans[i].getRemind()) {
							   case 0:
								   beans[i].setReminds("不提醒");
								   break;
							   case 1:
								   beans[i].setReminds("准时提醒");
									break;
								case 2:
									beans[i].setReminds("提前20分钟提醒");
									break;
								case 3:
									beans[i].setReminds("提前30分钟提醒");
									break;
								case 4:
									beans[i].setReminds("提前1小时提醒");
									break;
								case 5:
									beans[i].setReminds("提前1.5小时提醒");
									break;
								case 6:
									beans[i].setReminds("提前2小时提醒");
									break;
								} 	
					     		 
						      listbean.add(beans[i]);								 
					         }	 
					   try {
							  DbHelper.getDB(ServiceScheduleActivity.this).saveAll(listbean);
						     } catch (DbException e) {					
							e.printStackTrace();
						   }
			         try {									
				       listbean= (ArrayList<SchedulesBean>) DbHelper.getDB(ServiceScheduleActivity.this).findAll(SchedulesBean.class);							        								   
					if(listbean!=null&&listbean.size()!=0){
//						clickadapter =new MyAdapter(ServiceScheduleActivity.this, listbean," ");
						listview.setAdapter(clickadapter);	
					}								    						    								    
						         } catch (DbException e) {
						    	e.printStackTrace();
					    	  }
//					     daySelected = updateCalendar();
					
				        }		
				}
			});
		 }
		}
	
	});
	
	protected String GetDateShortString(Calendar date) {
		String returnString = date.get(Calendar.YEAR) + "/";
		returnString += date.get(Calendar.MONTH) + 1 + "/";
		returnString += date.get(Calendar.DAY_OF_MONTH);
		
		return returnString;
	}

	// 得到当天在日历中的序号
	private int GetNumFromDate(Calendar now, Calendar returnDate) {
		Calendar cNow = (Calendar) now.clone();
		Calendar cReturnDate = (Calendar) returnDate.clone();
		setTimeToMidnight(cNow);
		setTimeToMidnight(cReturnDate);
		
		long todayMs = cNow.getTimeInMillis();
		long returnMs = cReturnDate.getTimeInMillis();
		long intervalMs = todayMs - returnMs;
		int index = millisecondsToDays(intervalMs);
		
		return index;
	}

	private int millisecondsToDays(long intervalMs) {
		return Math.round((intervalMs / (1000 * 86400)));
	}

	private void setTimeToMidnight(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	// 生成布局
	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		
		return lay;
	}

	// 生成日历头部
	private View generateCalendarHeader() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
//		 layRow.setBackgroundColor(Color.argb(255, 207, 207, 205));
		
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayHeader day = new DateWidgetDayHeader(this, Cell_Width,
					50);
			
			final int iWeekDay = DayStyle.getWeekDay(iDay, iFirstDayOfWeek);
		          	day.setData(iWeekDay);
			layRow.addView(day);
		}
		
		return layRow;
	}

	// 生成日历主体
	private View generateCalendarMain(int index) {
		layContent = createLayout(LinearLayout.VERTICAL);
		// layContent.setPadding(1, 0, 1, 0);
		layContent.setBackgroundColor(Color.WHITE);
		layContent.addView(generateCalendarHeader());
		days.clear();
	   
		for (int iRow = 0; iRow < index; iRow++) {
			layContent.addView(generateCalendarRow());
		}
		
		return layContent;
	}

	// 生成日历中的一行，仅画矩形
	private View generateCalendarRow() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);						
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayCell dayCell = new DateWidgetDayCell(this, Cell_Width,
					(y/(3*6)+2));
//		  dayCell.			
			dayCell.setCancelFoucse(this);
			dayCell.setItemClick(mOnDayCellClick);
			days.add(dayCell);
			layRow.addView(dayCell);
		}	
		return layRow;
	}

	// 设置当天日期和被选中日期
	private Calendar getCalendarStartDate() {
		//返回当前的时间
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}
		
		UpdateStartDateForMonth();
		return calStartDate;
	}

	// 由于本日历上的日期都是从周日开始的，此方法可推算出上月在本月日历中显示的天数
	private int UpdateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.HOUR_OF_DAY, 0);
		calStartDate.set(Calendar.MINUTE, 0);
		calStartDate.set(Calendar.SECOND, 0);
		// update days for week
		UpdateCurrentMonthDisplay();
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
		return iDay;
	}
	// 更新日历
	private DateWidgetDayCell updateCalendar(boolean isFirst) {
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		DateWidgetDayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
	     	calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());		
		    for (int i = 0; i < days.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DateWidgetDayCell dayCell = days.get(i);
			dayCell.invalidate();
	        String Year=String.valueOf(iYear);
	        String Month=String.valueOf(iMonth+1);
	        String day=String.valueOf(iDay);
			boolean bToday = false;
			boolean bHasWrite=false;
			// 判断是否当天
			if (calToday.get(Calendar.YEAR) == iYear) {
				if (calToday.get(Calendar.MONTH) == iMonth) {
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay) {
						bToday = true;
							}
				}
			}			
           if(Month.length()==1){
        	Month="0"+Month;
            }		
            if(day.length()==1){
        	day="0"+day;
            }
            
            String boomon= String.valueOf(calToday.get(Calendar.MONTH)+1);
    	    String booday=String .valueOf(calToday.get(Calendar.DAY_OF_MONTH));
    	    if(boomon.length()==1){
    	    	boomon="0"+boomon;
    	    }
    	    if(booday.length()==1){
    	    	booday="0"+booday;
    	    }
    	    statetime=calToday.get(Calendar.YEAR)+"-"+boomon+"-"+booday;
		  String time=Year+"-"+Month+"-"+day;
		  Date nowDate=null;
		  Date clickDate=null;
		  Date showDate=null;
		  if(listbean!=null&&listbean.size()!=0){
			  for(int in=0;in<listbean.size();in++){
				  String time2=listbean.get(in).getDay();
				  if(time2!=null){
						try {
							nowDate = sdf.parse(statetime);
							clickDate =sdf.parse(time);
							showDate=sdf.parse(time2);
						} catch (ParseException e) {

							e.printStackTrace();
						}
						
				  if(time2.equals(time)&&listbean.get(in).getDelete().equals("0")){		   				
					 bHasWrite=true;	
				  }else{
				     bHasWrite=false;		
				  }
//				  if(nowDate.getTime()<clickDate.getTime()){//大于今天的时间以后的时间都不显示写的状态
//					  bHasWrite=false;	
//				  }
//				  if(bToday&&showDate.getTime()>nowDate.getTime()){//如果是今天，而且数据库中时间有大于当前时间
//					  bHasWrite=true;	
//				  }

			       if(bHasWrite==true){
			          break;//如果日历是写的状态，跳出循环
			     }}else{		    
			     }
			  }
		  }	
		    dayCell.setSelect(bHasWrite);
			boolean bHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY)
					|| (iDayOfWeek == Calendar.SUNDAY))
				bHoliday = true;
			if ((iMonth == Calendar.JANUARY) && (iDay == 1))
				bHoliday = true;
			// 是否被选中
			bSelected = false;			
			if (bIsSelection)
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}			
			dayCell.setSelected(bSelected);
		

			// 是否有记录
			boolean hasRecord = false;
			
			if (flag != null && flag[i] == true && calendar_Hashtable != null
					&& calendar_Hashtable.containsKey(i)) {
				// hasRecord = flag[i];
				hasRecord = Calendar_Source.get(calendar_Hashtable.get(i))
						.contains(UserName);
			}

			if (bSelected)
				daySelected = dayCell;
		   
			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
					iMonthViewCurrentMonth, hasRecord,isFirst);			
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		layContent.invalidate();
		
		return daySelected;
	}

	// 更新日历标题上显示的年月
	private void UpdateCurrentMonthDisplay() {
		 monthdate = calStartDate.get(Calendar.YEAR) + "年"
				+ (calStartDate.get(Calendar.MONTH) + 1) + "月";
		 String month=calStartDate.get(Calendar.MONTH) + 1+"";
		 if(month.length()<2){
			 month="0"+month;
		 }
		 String date= calStartDate.get(Calendar.YEAR) + "."
					+ month;
		Top_Date.setText(date);
	}

	// 点击上月按钮，触发事件
	class Pre_MonthOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth--;
			
			if (iMonthViewCurrentMonth == -1) {
				iMonthViewCurrentMonth = 11;
				iMonthViewCurrentYear--;
			}
			
			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			calStartDate.set(Calendar.HOUR_OF_DAY, 0);
			calStartDate.set(Calendar.MINUTE, 0);
			calStartDate.set(Calendar.SECOND, 0);
			calStartDate.set(Calendar.MILLISECOND, 0);
			UpdateStartDateForMonth();

			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);
			// 新建线程
			new Thread() {
				@Override
				public void run() {

					int day = GetNumFromDate(calToday, startDate);
					
					if (calendar_Hashtable != null
							&& calendar_Hashtable.containsKey(day)) {
						dayvalue = calendar_Hashtable.get(day);
					}
				}
			}.start();

//			updateCalendar();
		}

	}

	// 点击下月按钮，触发事件
	class Next_MonthOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
					
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth++;			
			if (iMonthViewCurrentMonth == 12) {
				iMonthViewCurrentMonth = 0;
				iMonthViewCurrentYear++;
			}			
			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			UpdateStartDateForMonth();
			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);
			// 新建线程
			new Thread() {
				@Override
				public void run() {
					int day = 5;
					
					if (calendar_Hashtable != null
							&& calendar_Hashtable.containsKey(day)) {
						dayvalue = calendar_Hashtable.get(day);
					}
				}
			}.start();

//			updateCalendar();
		}
	}

    // 点击日历，触发事件
    private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
        final int iMonth = calCalendar.get(Calendar.MONTH);
        public void OnClick(DateWidgetDayCell item) {
            updateCalendar(false);
            calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
            // 得到点击的时候的index-->是从本月当前的第一天开始，不是日历的第一天
            int day = GetNumFromDate(calSelected, startDate) + 1;
            System.out.println(day + "-->day");
            String data = item.getsDate().toString();
            String YearMonth = monthdate;
            String Year = YearMonth.substring(0, YearMonth.lastIndexOf("年"));
            String Month = YearMonth.substring(YearMonth.indexOf("年") + 1, YearMonth.lastIndexOf("月"));
            int years = Integer.valueOf(Year);
            int months = Integer.valueOf(Month);
            datenum = Integer.valueOf(data);
            int month = GetTodayDate().get(Calendar.MONTH) + 1;
            // showtoday.setText(" ");
            // 判断日历上的今天是不是真正的今天
            if (years == GetTodayDate().get(Calendar.YEAR)) {
                if (months == GetTodayDate().get(Calendar.MONTH) + 1) {
                    if (datenum == GetTodayDate().get(Calendar.DAY_OF_MONTH)) {
                        showtoday.setText("今天");
                    } else {
                        showtoday.setText(" ");
                    }
                }
            }
          
            String trueMonth = null;
            // 判断是不是当前月
            if (item.getIsMonth() == true) {
               trueMonth = Month;
            } else {
                if (datenum > 20) { // 判断当前日期是否大于20(一般情况下不会超过20)
                    int truemonth = Integer.valueOf(Month);
                    int lastmonth = truemonth - 1;
                    trueMonth = String.valueOf(lastmonth);
                } else {
                   int truemonth = Integer.valueOf(Month);
                   int lastmonth = truemonth + 1;
                   trueMonth = String.valueOf(lastmonth);
                }
            }
            if (trueMonth.length() == 1) {
                trueMonth = "0" + trueMonth;
            }
            if (data.length() == 1) {
               data = "0" + data;
            }
            String time = Year + "-" + trueMonth + "-" + data;
            showtime = time;
            Date nowDate = null;
            Date clickDate = null;
            Date showdate = null;
            List<SchedulesBean>list = new ArrayList<SchedulesBean>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (listbean!=null && listbean.size()!=0) {
                for (int i = 0; i < listbean.size(); i++) {
                    if (listbean.get(i).getDay()!=null) {
                        try {
                            nowDate = sdf.parse(statetime);                 // 现在的时间
                            clickDate = sdf.parse(showtime);                 // 点击的时间
                            showdate = sdf.parse(listbean.get(i).getDay());   // 数据库中的时间
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (listbean.get(i).getDay().equals(showtime)) {
                           list.add(listbean.get(i));
                        }
                        if (showdate!=null && showdate.getTime() > nowDate.getTime() && clickDate.getTime() == nowDate.getTime()) {
                           list.add(listbean.get(i));
                        }
                    }
                }
//              if (nowDate.getTime() < clickDate.getTime()) {
//                  list.clear();
//              }
            }
            clickMonthList.clear();
            clickMonthList.addAll(list);          
            clickadapter = new MyAdapter(ServiceScheduleActivity.this, list, " ", "click");
            listview.setAdapter(clickadapter);
            clickadapter.notifyDataSetChanged();
            item.setSelected(true);
            updateCalendar(false);
        }
    };

    public Calendar GetTodayDate() {
        Calendar cal_Today = Calendar.getInstance();
        cal_Today.set(Calendar.HOUR_OF_DAY, 0);
        cal_Today.set(Calendar.MINUTE, 0);
        cal_Today.set(Calendar.SECOND, 0);
        cal_Today.setFirstDayOfWeek(Calendar.SUNDAY);
        return cal_Today;
    }

    // 得到当前日历中的第一天
    public Calendar GetStartDate() {
        int iDay = 0;
        Calendar cal_Now = Calendar.getInstance();
        cal_Now.set(Calendar.DAY_OF_MONTH, 1);
        cal_Now.set(Calendar.HOUR_OF_DAY, 0);
        cal_Now.set(Calendar.MINUTE, 0);
        cal_Now.set(Calendar.SECOND, 0);
        cal_Now.setFirstDayOfWeek(Calendar.SUNDAY);

        iDay = cal_Now.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;

        if (iDay < 0) {
            iDay = 6;
        }
        cal_Now.add(Calendar.DAY_OF_WEEK, -iDay);
        return cal_Now;
    }

    public Calendar GetEndDate(Calendar startDate) {
        // Calendar end = GetStartDate(enddate);
        Calendar endDate = Calendar.getInstance();
        endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DAY_OF_MONTH, 31);
        return endDate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left_btn:
            this.finish();
            break;
        case R.id.right_btn:
            Intent intent = new Intent(ServiceScheduleActivity.this, ServiceScheduleWriteActivity.class);
            intent.putExtra("state", "newschedule");
            intent.putExtra("time", witime);
            startActivity(intent);
            this.finish();
            break;
        }
    }

    class MyAdapter extends BaseAdapter {
        private boolean shown;
        private LayoutInflater mInflater;
        private Context context;
        private List<SchedulesBean >list;
        RelativeLayout item_right;
        private String showdate = null;
        private View view;
        private String type;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        Date writeDate = null;
        private List<SchedulesBean>list2 = new ArrayList<SchedulesBean>();

        @SuppressWarnings("unchecked")
        public MyAdapter(Context context, List<SchedulesBean>list, String showdate, String type) {
            this.list = list;
            this.context = context;
            this.showdate = showdate;
            this.type = type;
            if (showdate!=" ") {
                statetime = showdate;
            }
            System.out.println("statetime-->" + statetime);
            mInflater = LayoutInflater.from(context);
            // 只显示今天的日期,在返回的数据库全部的list中过滤掉日期不相同的时间
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDay()!=null) {
                    if (list.get(i).getDay().equals(statetime)&&list.get(i).getDelete().equals("0")) {
                        list2.add(list.get(i));
                    }
                    try {
                        nowDate = sdf.parse(statetime);
                        writeDate = sdf.parse(list.get(i).getDay());
                        if (writeDate.getTime() > nowDate.getTime() && type.equals("first")) {
                            list2.add(list.get(i));
                        } else if (writeDate.getTime() < nowDate.getTime() && type.equals("click")) {
                            list2.add(list.get(i));
                        }
                        if (type.equals("click")) { // 只要在今天以后的全部的日程
                            list2.clear();
                            list2.addAll(list);
                        }else if(type.equals("delete")){
                            list2.clear();
                            list2.addAll(list);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            nowMonthList.clear();
            for (int i = 0; i < list2.size(); i++) {
                nowMonthList.add(list2.get(i));
            }

            if (list2!=null && list2.size()!=0) {
               SortCompareUtil compare = new SortCompareUtil();
               Collections.sort(list2,compare);
            }
        }

        @Override
        public int getCount() {
            return list2.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setData(List<SchedulesBean>list2){
            this.list2 = list2;
            notifyDataSetChanged();
        }

        private List<SchedulesBean> getData(){
               return list2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewgroup) {
            convertView = mInflater.inflate(R.layout.inflater_service_schedule, null);
            view = convertView;
            shown = listview.getHidenType();
            item_right = (RelativeLayout)convertView.findViewById(R.id.item_right);
            TextView time = ViewHolder.get(convertView, R.id.tv_msg);
            ImageView iv = ViewHolder.get(convertView, R.id.iv_icon);
            TextView tv = ViewHolder.get(convertView, R.id.tv_time);
            LinearLayout.LayoutParams lp2 = new LayoutParams(listview.getRightViewWidth(), LayoutParams.MATCH_PARENT);
            item_right.setLayoutParams(lp2);
            item_right.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    int x = 0;
                    AlarmBean beans = new AlarmBean();
                    if (ServiceScheduleWriteActivity.alarmlist!=null && ServiceScheduleWriteActivity.alarmlist.size()!=0) {
                        for (int i = 0; i < ServiceScheduleWriteActivity.alarmlist.size(); i++) {
                            if (list2.get(position).getLocalsheid() == ServiceScheduleWriteActivity.alarmlist.get(i).getRequestcode()) {
                                beans = ServiceScheduleWriteActivity.alarmlist.get(i);
                                i = x;
                            }
                        }
                        am.cancel(beans.getIntent());
                        ServiceScheduleWriteActivity.alarmlist.remove(x);
                    }
                    if (clickadapter.getData()!=null) {
                        try {
                            if (DbHelper.getDB(ServiceScheduleActivity.this).tableIsExist(SchedulesBean.class)) {
                                DbHelper.getDB(ServiceScheduleActivity.this).delete(SchedulesBean.class,
                                WhereBuilder.b("localsheid","=",clickadapter.getData().get(position).getLocalsheid()));
                                AppServer.getInstance().UploadSchedule(accountInfo.getUid(), 2, clickadapter.getData().get(position).getSheid(),
                                        " ", " ", " ", " ", 1, " "," "," ",new OnAppRequestListener() {
                                    @Override
                                    public void onAppRequest(int code,String message, Object obj) {
                                        if (code == 0) {
                                            try {
                                                DbHelper.getDB(ServiceScheduleActivity.this).delete(SchedulesBean.class,WhereBuilder.b("localsheid","=",bean.getLocalsheid()));
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intentservice = new Intent(ServiceScheduleActivity.this,
                                            ScheduleServiceUnbind.class);intentservice.putExtra("action", 2);
                                            startService(intentservice);
                                        } else {
                                            try {
                                                bean.setDelete("-1");
                                                DbHelper.getDB(ServiceScheduleActivity.this).update(bean);
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                listbean = (ArrayList<SchedulesBean>) DbHelper.getDB(ServiceScheduleActivity.this).findAll(SchedulesBean.class);
                                List<SchedulesBean> list = new ArrayList<SchedulesBean>();
                                Date showDate = null;
                                Date thisDate = null;
                                Date clickDate = null;
                                for (int i = 0; i < listbean.size(); i++) {
                                    if (listbean.get(i).getDay() != null) {
                                        try {
                                            thisDate = sdf.parse(statetime);
                                            showDate =sdf.parse(listbean.get(i).getDay());
                                            if (showtime == null) {
                                                showtime=statetime;
                                            }
                                            clickDate =sdf.parse(showtime);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (listbean.get(i).getDay().equals(showtime)) {
                                            list.add(listbean.get(i));
                                        }
                                        if (thisDate!=null && showDate!=null && clickDate!=null
                                                && thisDate.getTime() < showDate.getTime() && clickDate.getTime() == thisDate.getTime()) {
                                            list.add(listbean.get(i));
                                        }
                                    }
                                }
//                              if (list!=null && list.size()!=0) {
//                                  for (int i = 0; i < list.size(); i++) {
//                                      if (list.get(i).getDay().equals(statetime)) {
//                                          list.remove(i);
//                                      }
//                                  }
//                              }
                                clickadapter = new MyAdapter(ServiceScheduleActivity.this, list, " ", "delete");
                                listview.setAdapter(clickadapter);
                                listview.setSelection(position);
                                clickadapter.notifyDataSetChanged();
                                updateCalendar(false);
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            int themeposition = 0;
            String theme = list2.get(position).getTheme();
            if (theme!=null) {
                for (int i = 0; i < ServiceScheduleActivity.theme.length; i++) {
                    if (theme.equals(ServiceScheduleActivity.theme[i])) {
                        themeposition = i;
                    }
                }
            }
            String stime = list2.get(position).getTime();
            String hour = stime.trim().substring(0, 2);
            if (hour.substring(0, 1).equals("0")) {
                hour = hour.substring(1, 2);
            }
//          int hours = Integer.valueOf(hour);
//          if (hours > 6 && hours < 9) {
//              stime = "早晨" + stime;
//          } else if (hours > 9 && hours < 12 || hours == 9) {
//              stime = "上午" + stime;
//          } else if (hours == 12) {
//              stime = "中午" + stime;
//          } else if (hours > 12 && hours < 18 || hours == 18) {
//              stime = "下午" + stime;
//          } else {
//              stime = "晚上" + stime;
//          }
            String note = list2.get(position).getNote();
            if (theme!=null) {
                if (note!=null && !note.equals(" ") && !note.equals("")) {
                    if (note.length() > 8) {
                        tv.setText(note.substring(0, 8) + "......");
                    } else {
                        tv.setText(note);
                    }
                } else {
                    tv.setText(ServiceScheduleActivity.theme[themeposition]);
                }
            } else {
                tv.setText("");
            }
            time.setTextColor(Color.rgb(50, 50, 50));

            Date nowDate = null;
            Date writeDate = null;
            try {
                nowDate=sdf.parse(statetime);
                writeDate=sdf.parse(list2.get(position).getDay());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (writeDate.getTime() == nowDate.getTime()) {
                time.setText("今天" + "   " + stime);
            } else {
                String date=list2.get(position).getDay();
                String month = date.substring(date.indexOf("-") + 1, date.lastIndexOf("-"));
                String day = date.substring(date.lastIndexOf("-") + 1);
                time.setText(month+"月"+day+"日"+"   "+stime);
            }

			if(theme!=null&&!theme.equals("null")){
			    if(theme.equals("工作检查")){
					  iv.setImageResource(imgsrc[0]);
				   }else if(theme.equals("开会")){
					   iv.setImageResource(imgsrc[1]);
				   }else if(theme.equals("园所活动")){
					   iv.setImageResource(imgsrc[2]);
				   }else if(theme.equals("约见")){
					   iv.setImageResource(imgsrc[3]);
				   }else if(theme.equals("培训")){
					   iv.setImageResource(imgsrc[4]);
				   }else if(theme.equals("重要日子")){
					   iv.setImageResource(imgsrc[5]);
				   }else if(theme.equals("聚会")){
					   iv.setImageResource(imgsrc[6]);
				   }else if(theme.equals("其他")){
					   iv.setImageResource(imgsrc[7]);
				   }
			  }else{
//				  iv.setVisibility(View.INVISIBLE);
			  }
			
			return convertView;
		}		
	}	   
	@Override
	public void canceltoucel(float x1,float x2) {
	    if (x1 - x2 > 100) {  
	    	 showtoday.setText(" ");
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth++;			
			if (iMonthViewCurrentMonth == 12) {
				iMonthViewCurrentMonth = 0;
				iMonthViewCurrentYear++;
			}
		
			
			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			Calendar c=(Calendar) calStartDate.clone();
		    int iDay=UpdateStartDateForMonth();
			int daynum= c.getActualMaximum(Calendar.DAY_OF_MONTH);
		    int all=iDay+daynum;		   
//		    days.clear();	    
		    rl_contrnt.removeAllViews();
		    rl_contrnt.addView(generateCalendarMain(getRow(all)));

			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);
			// 新建线程
			new Thread() {
				@Override
				public void run() {
					int day = 5;					
					if (calendar_Hashtable != null
							&& calendar_Hashtable.containsKey(day)) {
						dayvalue = calendar_Hashtable.get(day);
					}
				}
			}.start();
			updateCalendar(false);
	    } else if (x2 - x1 > 100) {  
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth--;
			 showtoday.setText(" ");
			if (iMonthViewCurrentMonth == -1) {
				iMonthViewCurrentMonth = 11;
				iMonthViewCurrentYear--;
			}			
			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			calStartDate.set(Calendar.HOUR_OF_DAY, 0);
			calStartDate.set(Calendar.MINUTE, 0);
			calStartDate.set(Calendar.SECOND, 0);
			calStartDate.set(Calendar.MILLISECOND, 0);
			Calendar c=(Calendar) calStartDate.clone();
		
		    int iDay=UpdateStartDateForMonth();		
			int daynum= c.getActualMaximum(Calendar.DAY_OF_MONTH);
		    int all=iDay+daynum;		   
		    rl_contrnt.removeAllViews();
		    rl_contrnt.addView(generateCalendarMain(getRow(all)));
			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);
			// 新建线程
			new Thread() {
				@Override
				public void run() {

					int day = GetNumFromDate(calToday, startDate);
					
					if (calendar_Hashtable != null
							&& calendar_Hashtable.containsKey(day)) {
						dayvalue = calendar_Hashtable.get(day);
					}
				}
			}.start();
			updateCalendar(false);	       
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
      @Override
     protected void onDestroy() {
	
	super.onDestroy();
    }
      
      
}