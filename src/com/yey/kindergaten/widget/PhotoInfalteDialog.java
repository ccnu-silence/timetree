package com.yey.kindergaten.widget;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.SendNotificationActivity;
import com.yey.kindergaten.activity.ServiceScheduleWriteActivity;
import com.yey.kindergaten.activity.ServiceScheduleWriteActivity.ShowWriteTime;
import com.yey.kindergaten.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;


public class PhotoInfalteDialog extends DialogBase implements android.view.View.OnClickListener{

	private TextView showtimetv;
	private RelativeLayout dissmis;
	private RelativeLayout positive;
	WheelView mDateWheel = null;
    WheelView mHourWheel = null;
    WheelView mMinuteWheel = null;	   
	private static final int[] DAYS_PER_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    private static final String[] MONTH_NAME = { "01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12", };
    ArrayList<TextInfo> mDates = new ArrayList<TextInfo>();
    ArrayList<TextInfo> mHours = new ArrayList<TextInfo>();
    ArrayList<TextInfo> mMinute = new ArrayList<TextInfo>();
    String state=null;
    int mCurDate = 0;
    int mCurHour = 0;
    int mCurMinute = 0;
    int XMl;

    Context context;
    Handler handler;
    private TextView notifytime;

    private Handler adapterHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 123){
                ((WheelTextAdapter) mDateWheel.getAdapter()).setData(mDates);
            }else if(msg.what == 124){
                ((WheelTextAdapter) mHourWheel.getAdapter()).setData(mHours);
            }else{
                ((WheelTextAdapter) mMinuteWheel.getAdapter()).setData(mMinute);

            }
        }
    };
	/**
	 * 传递需要膨胀的xml
	 * @param XmlId
	 */
	public PhotoInfalteDialog(ServiceScheduleWriteActivity activity,int XmlId,Handler handler,String state){		
		super(activity);		
		this.context=activity;
	    this.handler=handler;
		this.XMl=XmlId;
		this.state=state;
		this.setCanceledOnTouchOutside(false);//外部点击无效
		this.getWindow().setGravity(Gravity.CENTER_VERTICAL);
		this.getWindow().setWindowAnimations(R.style.servicescheduledialog);	   
	}

	
	public PhotoInfalteDialog(SendNotificationActivity activity,int XmlId,Handler handler,String state) {
		super(activity);
		this.context=activity;
	    this.handler=handler;
		this.XMl=XmlId;
	
		this.state=state;
		this.setCanceledOnTouchOutside(false);//外部点击无效
		this.getWindow().setGravity(Gravity.CENTER_VERTICAL);
		this.getWindow().setWindowAnimations(R.style.servicescheduledialog);	   
	}
	
	  protected class TextInfo {
	        public TextInfo(int index, String text, boolean isSelected) {
	            mIndex = index;
	            mText = text;
	            mIsSelected = isSelected;

	            if (isSelected) {
	                mColor = Color.BLACK;
	            }
	        }
	        
	        public int mIndex;
	        public String mText;
	        public boolean mIsSelected = false;
	        public int mColor = Color.GRAY;
			public int getmColor() {
				return mColor;
			}
			public void setmColor(int mColor) {
				this.mColor = mColor;
			}
	    }
	  
	 
	  
    private TosGallery.OnEndFlingListener mListener = new TosGallery.OnEndFlingListener() {
        @Override
        public void onEndFling(TosGallery v) {
            int posDate = v.getSelectedItemPosition();
            int posHour = 0;
            int posMinu = 0;
            posDate=mDateWheel.getSelectedItemPosition();
        	posMinu=mMinuteWheel.getSelectedItemPosition();
        	posHour=mHourWheel.getSelectedItemPosition();  
            if (v == mDateWheel) {          	
                TextInfo info = mDates.get(posDate);
                 setDate(info.mIndex);
                 for(int i=0;i<mDates.size();i++){
                	 mDates.get(i).setmColor(Color.GRAY);
                 }        
                     mDates.get(posDate).setmColor(Color.BLACK);      
                 
                 Message msg = new Message();
                 msg.what = 123;
                 msg.obj = mDates;
                 adapterHandler.sendMessageDelayed(msg,150);

//

            } else if (v == mHourWheel) {
            	TextInfo info = mHours.get(posHour);
                setMonth(info.mIndex);
                for(int i=0;i<mHours.size();i++){
                	mHours.get(i).setmColor(Color.GRAY);
                }        
                mHours.get(posHour).setmColor(Color.BLACK);

                Message msg = new Message();
                msg.what = 124;
                msg.obj = mDates;
                adapterHandler.sendMessageDelayed(msg,150);

            } else if (v == mMinuteWheel) {            	
                TextInfo info = mMinute.get(posMinu);
                setHour(info.mIndex);       
                
                for(int i=0;i<mMinute.size();i++){
                	mMinute.get(i).setmColor(Color.GRAY);
                }        
                mMinute.get(posMinu).setmColor(Color.BLACK);

                Message msg = new Message();
                msg.what = 125;
                msg.obj = mDates;
                adapterHandler.sendMessageDelayed(msg,150);

            }           
           Calendar cal=Calendar.getInstance();
           cal.setTimeInMillis(System.currentTimeMillis());
 	       int year =cal.get(Calendar.YEAR);
 	       String date=mDates.get(posDate).mText;
           String yearr=date.substring(0,4);
 	       String month=date.substring(0,2);   
 	       String day=date.substring(3,5);
 	       if(yearr.contains(".")){//设置当前年时显示的时间  (如果前面4个字符是包含了点，说明当前年没有显示)	   	       	 	      	 	        	     		 	       
 	 	     String hourr=mHours.get(posHour).mText;
 	 	     String minutts=mMinute.get(posMinu).mText;
 	 	     if(hourr.length()==1){
 	 	    	 hourr="0"+hourr;	 	    	 
 	 	     }
 	 	     if(minutts.length()==1){
 	 	    	 minutts="0"+minutts;
 	 	     }
 	    	 showtimetv.setText(year+"-"+month+"-"+day+"  "+hourr+":"+minutts);
 	       }else{//设置不是当前年时显示的时间
 	    	   String yyear=date.substring(0,4);
 	    	   String mmonth=date.substring(5,7);	    
 	    	   String dday=date.substring(8,10);
 	    	   String hourr=mHours.get(posHour).mText;
 	 	 	   String minutts=mMinute.get(posMinu).mText;
 	 	 	     if(hourr.length()==1){
 	 	 	    	 hourr="0"+hourr;	 	    	 
 	 	 	     }
 	 	 	     if(minutts.length()==1){
 	 	 	    	 minutts="0"+minutts;
 	 	 	     } 	    	
 	    	   showtimetv.setText(yyear+"-"+mmonth+"-"+dday+"  "+hourr+":"+minutts);
 	       }	        	    
        }
    };
    
    private void setDate(int date) {
        if (date != mCurDate) {
            mCurDate = date;
        }
    }

    private void setHour(int hour) {
        if (hour != mCurHour) {
        	mCurHour = hour;
        }
    }

    private void setMonth(int minute) {
        if (minute != mCurMinute) {
        	mCurMinute = minute;
        }
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		    this.setContentView(XMl);
		    showtimetv=(TextView) findViewById(R.id.id_service_showdate_time_tv);	    	    	    
		    mDateWheel=(WheelView) findViewById(R.id.inflater_service__schedule_select_date);
		    mHourWheel=(WheelView) findViewById(R.id.inflater_service_schedule_select_hour);
		    mMinuteWheel=(WheelView) findViewById(R.id.inflater_service_schedule_select_minute);
		    positive=(RelativeLayout) findViewById(R.id.id_dialog_service_schedule_positive);
		    dissmis=(RelativeLayout) findViewById(R.id.id_dialog_service_schedule_negtive);
	        mDateWheel.setOnEndFlingListener(mListener);
	        mHourWheel.setOnEndFlingListener(mListener);
	        mMinuteWheel.setOnEndFlingListener(mListener);
//	        mDateWheel.setScrollCycle(true);
	        mHourWheel.setScrollCycle(true);
	        mMinuteWheel.setScrollCycle(true);

	        mDateWheel.setSoundEffectsEnabled(true);
	        mHourWheel.setSoundEffectsEnabled(true);
	        mMinuteWheel.setSoundEffectsEnabled(true);

	        mDateWheel.setAdapter(new WheelTextAdapter(context));
	        mHourWheel.setAdapter(new WheelTextAdapter(context));
	        mMinuteWheel.setAdapter(new WheelTextAdapter(context));
            if(!state.equals("service")){
                notifytime=(TextView) findViewById(R.id.id_show_time_select);
                notifytime.setText("设置通知时间");
            }
	        prepareData(); 

	        if(state.equals("service")){
		    ((ServiceScheduleWriteActivity) this.context).showThisTime(new ShowWriteTime() {		
					@Override
					public void ThisTime(String datetime,String statetime, String state) {
						if(state.equals("newschedule")||state.equals("homeschedule")){
						   	   String mon = null;
				        	   String days=null;
				        	   String hour=null;
				        	   String minute=null;
				        	   String datetimes=null;
				        	   int posDate=0;
						       Calendar cal=Calendar.getInstance();
						       int month=cal.get(Calendar.MONTH)+1;
						       if(month<10){
						    	   mon="0"+month;
						       }else{
						    	   mon=month+"";
						       }
						       int day=cal.get(Calendar.DAY_OF_MONTH);
						       if(day<10){
						    	   days="0"+day;
						       }else{
						    	   days=day+"";
						       }
						       int hours  =  cal.get(Calendar.HOUR_OF_DAY);
						       int minutes= cal.get(Calendar.MINUTE);
						       if(hours<10){
						    	   hour="0"+hours;
						       }else{
						    	   hour=hours+"";
						       }
						       if(minutes<10){
						    	   minute="0"+minutes;
						       }else{
						    	   minute=minutes+"";
						       }
						       int year =cal.get(Calendar.YEAR);			        					  				   	      
						       showtimetv.setText(year+"-"+mon+"-"+days+"  "+hour+":"+minute);	
						       datetimes=mon+"."+days;
						       for(int i=0;i<mDates.size();i++){

	                            	 if(mDates.get(i).mText.substring(0, 5).equals(datetimes)){
	                            		 
	                            		 posDate=i;
	                            		 
	                            		 break;
	                            	 }                           	 
						       }
						       mDateWheel.setSelection(posDate);
						       
						}else{
							   showtimetv.setText(datetime+"  "+statetime);
                                String hour=statetime.substring(0,2);
                                String minute=statetime.substring(3,5);
                                String oneh=hour.substring(0,1);
                                String onem=minute.substring(0,1);
                                if(oneh.equals("0")){
                                	  hour=hour.substring(1,2);
                                }
                                if(onem.equals("0")){
                                	 minute=minute.substring(1,2);
                                }
                                int posDate=0;
                                int posHour=0;
                                int posMinute=0;     

                                String mons= datetime.substring(5,7);
                                String days=datetime.substring(8,10);
                                 datetime=mons+"."+days;
						       for(int i=0;i<mDates.size();i++){
                            	 if(mDates.get(i).mText.substring(0, 5).equals(datetime)){
                            		 posDate=i;
                            		 break;
                            	 }                           	 
                              }						     
						     for(int i=0;i<mMinute.size();i++){
                            	 if(mMinute.get(i).mText.equals(minute)){
                            		 posMinute=i;
                            		 break;
                            	 }                           	 
                            }						     
						     for(int i=0;i<mHours.size();i++){
                            	 if(mHours.get(i).mText.equals(hour)){
                            		 posHour=i;
                            		 break;
                            	 }                           	 
                              }
						     
						       mDateWheel.setSelection(posDate);						       
						       mHourWheel.setSelection(posHour);
						       mMinuteWheel.setSelection(posMinute);

                               for(int i=0;i<mDates.size();i++){
                                mDates.get(i).setmColor(Color.GRAY);
                               }
                               mDates.get(posDate).setmColor(Color.BLACK);
                              ((WheelTextAdapter) mDateWheel.getAdapter()).setData(mDates);

                               for(int i=0;i<mMinute.size();i++){
                                   mMinute.get(i).setmColor(Color.GRAY);
                               }
                               mMinute.get(posMinute).setmColor(Color.BLACK);
					           ((WheelTextAdapter) mMinuteWheel.getAdapter()).setData(mMinute);
					           mMinuteWheel.setSlotInCenter(true);

                               for(int i=0;i<mHours.size();i++){
                                   mHours.get(i).setmColor(Color.GRAY);
                               }
					           mHours.get(posHour).setmColor(Color.BLACK);                               
					           ((WheelTextAdapter) mHourWheel.getAdapter()).setData(mHours);
					           mHourWheel.setSlotInCenter(true);
						}						
					}
				});

	        }else{
	        	  String mon = null;
	        	   String days=null;
	        	   String hour=null;
	        	   String minute=null;
	        	   String datetimes=null;
	        	   int posDate=0;
			       Calendar cal=Calendar.getInstance();
			       int month=cal.get(Calendar.MONTH)+1;
			       if(month<10){
			    	   mon="0"+month;
			       }else{
			    	   mon=month+"";
			       }
			       int day=cal.get(Calendar.DAY_OF_MONTH);
			       if(day<10){
			    	   days="0"+day;
			       }else{
			    	   days=day+"";
			       }
			       int hours  =  cal.get(Calendar.HOUR_OF_DAY);
			       int minutes= cal.get(Calendar.MINUTE);
			       if(hours<10){
			    	   hour="0"+hours;
			       }else{
			    	   hour=hours+"";
			       }
			       if(minutes<10){
			    	   minute="0"+minutes;
			       }else{
			    	   minute=minutes+"";
			       }
			       int year =cal.get(Calendar.YEAR);			        					  				   	      
			       showtimetv.setText(year+"-"+mon+"-"+days+"  "+hour+":"+minute);	
			       datetimes=mon+"."+days;
			       for(int i=0;i<mDates.size();i++){

                   	 if(mDates.get(i).mText.substring(0, 5).equals(datetimes)){
                   		 
                   		 posDate=i;
                   		 
                   		 break;
                   	 }                           	 
			       }
			       mDateWheel.setSelection(posDate);
	        }
		   

	        positive.setOnClickListener(this);
	        dissmis.setOnClickListener(this);
	 
	}
	


    private boolean isLeapYear(int year) {
        return ((0 == year % 4) && (0 != year % 100) || (0 == year % 400));
    }


    private void prepareData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        mCurDate = day;
        mCurHour = month;
        mCurMinute = year;       
        prepareDayTime();
        ((WheelTextAdapter) mHourWheel.getAdapter()).setData(mHours);
        ((WheelTextAdapter) mMinuteWheel.getAdapter()).setData(mMinute);
        prepareDayData(year, month, day);

    }

    
    private void prepareDayTime() {
    	  Calendar calNow=Calendar.getInstance();
          calNow.setTimeInMillis(System.currentTimeMillis());
          int hours=calNow.get(Calendar.HOUR_OF_DAY);
          int minute=calNow.get(Calendar.MINUTE);
          
    	  for(int i=0;i<24;i++){
    		  mHours.add(new TextInfo(i, i+"",(i==hours)));
    	  }
		  for(int n=0;n<60;n++){
			  mMinute.add(new TextInfo(n, n+"",(n==minute)));
		  }
		
		  mMinuteWheel.setSelection(minute);
		  mHourWheel.setSelection(hours);
	}  

	private void prepareDayData(int year, int month, int curDate) {
        mDates.clear();

        int days = DAYS_PER_MONTH[month]; 
        Calendar calNow=Calendar.getInstance();
        calNow.setTimeInMillis(System.currentTimeMillis());
        int NowYear= calNow.get(Calendar.YEAR); 
        int Month=calNow.get(Calendar.MONTH);
        int day=calNow.get(Calendar.DAY_OF_MONTH);
        calNow.setFirstDayOfWeek(Calendar.SUNDAY);
        //关于优化代码时间复杂度的问题。    
        for(int years=NowYear;years<NowYear+5;years++){
        	if(years==NowYear){//判断是不是今年
        		for(int months=Month;months<MONTH_NAME.length;months++){
        			days=DAYS_PER_MONTH[months];
     				if(months==1){
     					 days = isLeapYear(year) ? 29 : 28;   		
     				}        	     				
     				if(Month==months){
        				     days=DAYS_PER_MONTH[months];
        				if(months==1){
        					 days = isLeapYear(year) ? 29 : 28;
        				}
        				for(int i=1;i<days+1;i++){
        				    if(i<10){  	    	
     					        mDates.add(new TextInfo(i, MONTH_NAME[months]+"."+0+i+getWeek(years,months,i), (i==curDate)));
     			        	}else{
     			          	    mDates.add(new TextInfo(i, MONTH_NAME[months]+"."+i+getWeek(years,months,i), (i==curDate)));
     			             }     					    	         					
         			       }
        			      }else{//判断是不是这个月
       		            for(int i=1;i<days+1;i++){    
        				    if(i<10){
         					        mDates.add(new TextInfo(i, MONTH_NAME[months]+"."+0+i+getWeek(years,months,i), (i==curDate)));
         			        	}else{
         			          	    mDates.add(new TextInfo(i, MONTH_NAME[months]+"."+i+getWeek(years,months,i), (i==curDate)));
       			          }
              		    }
        		      }       			  
        		  }
        		}else{//判断是不是今年
        			for(int months=0;months<MONTH_NAME.length;months++){
        		       days=DAYS_PER_MONTH[months];
            		   if(months==1){
            					 days = isLeapYear(year) ? 29 : 28;
            				}
        				for(int i=1;i<days+1;i++){    
        				    if(i<10){
     					        mDates.add(new TextInfo(i, years+"."+MONTH_NAME[months]+"."+0+i+getWeek(years,months,i), (i==curDate)));
     			        	}else{
     			          	    mDates.add(new TextInfo(i, years+"."+MONTH_NAME[months]+"."+i+getWeek(years,months,i), (i==curDate)));
     			          }
         			     }
         			   }
        		}
        	
        	}             
                  ((WheelTextAdapter) mDateWheel.getAdapter()).setData(mDates);
    }
    
    /**
     * 根据年月日返回星期
     * @param Year
     * @param Month
     * @param Day
     * @return week
     */
    private String getWeek(int Year,int Month,int Day){
    	Calendar calNow=Calendar.getInstance();
        calNow.setTimeInMillis(System.currentTimeMillis());
        calNow.setFirstDayOfWeek(Calendar.SUNDAY);
        calNow.set(Calendar.YEAR, Year);
        calNow.set(Calendar.MONTH, Month);
        calNow.set(Calendar.DAY_OF_MONTH, Day);
        int i=calNow.get(Calendar.DAY_OF_WEEK);
		String week=null;
    	switch (i) {
		case 1:
		week="星期天";
			break;
		case 2:
		week="星期一";	
			break;
		case 3:
		week="星期二";
			break;
		case 4:
		week="星期三";
			break;
		case 5:
		week="星期四";
			break;
		case 6:
		week="星期五";
			break;
		case 7:
		week="星期六";
			break;
		}		
    	return week;    	
    }
	@Override
	protected void onBuilding() {
				
	}

	@Override
	protected boolean OnClickPositiveButton() {
	      
		
		return false;
	}

	@Override
	protected void OnClickNegativeButton() {
		
		
	}

	@Override
	protected void onDismiss() {
		
		
	}
    protected class WheelTextAdapter extends BaseAdapter {
        ArrayList<TextInfo> mData = null;
        int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int mHeight = 40;
        Context mContext = null;

        public WheelTextAdapter(Context context) {
            mContext = context;
            mHeight = (int) Utils.pixelToDp(context, mHeight);
        }

        public void setData(ArrayList<TextInfo> data) {
            mData = data;
            this.notifyDataSetChanged();
        }


        public void setItemSize(int width, int height) {
            mWidth = width;
            mHeight = (int) Utils.pixelToDp(mContext, height);
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;

            if (null == convertView) {
                convertView = new TextView(mContext);
                convertView.setLayoutParams(new TosGallery.LayoutParams(mWidth, mHeight));
                textView = (TextView) convertView;
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTextColor(Color.BLACK);
            }

            if (null == textView) {
                textView = (TextView) convertView;
            }

            TextInfo info = mData.get(position);
            textView.setText(info.mText);
            if(info.mIndex==mCurDate){
            	  textView.setTextColor(Color.BLACK);
            }
            textView.setTextColor(info.getmColor());

            return convertView;
        }
    }
    
//    public void showselectTime(ShowTime showtime){  	
//   
//    	 showtime.showSelectTime();
//    }  
	@Override
	public void onClick(View v) {
	
	    switch (v.getId()) {
		case R.id.id_dialog_service_schedule_negtive:
			this.dismiss();
			break;

		case R.id.id_dialog_service_schedule_positive:
			    Message msg=new Message();
	            msg.what=1;
	            msg.obj=showtimetv.getText().toString();	           	            
	            handler.sendMessage(msg);
	            this.dismiss();
			break;
		}
		
	}
    

}
