package com.yey.kindergaten.activity;

/**
 * @author zy
 * 发通知类
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.NotificationInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.widget.PhotoInfalteDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SendNotificationActivity extends BaseActivity implements OnClickListener{

	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.right_tv)TextView tv_right;
    private EditText mobanContent;
    private String mobanneirong = "   ";
	private TextView showTime;	
	private TextView showmsgcount;
    private RelativeLayout selecttime;
    private RelativeLayout selectmoban;
    private RelativeLayout selectpeople;
    private RelativeLayout showhistory;
    private String time;
    private TextView showcount;
    private String showtime;
    private ArrayList<String>teacherlist = new ArrayList<String>();
    private ArrayList<String>parentlist = new ArrayList<String>();
    private ArrayList<String>teacherlists = new ArrayList<String>();
    private ArrayList<String>parentlists = new ArrayList<String>();
    private List<String>namelist = new ArrayList<String>();
    private NotificationInfo info = new NotificationInfo();
    private NotificationInfo notifyinfo;
    private AccountInfo  accountinfo;
    private String people;
    String uid = null;

    private Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
    		if (msg.what == 1) {
    			time = (String) msg.obj;
    			showTime.setText(time);
    		}
    	}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendnotice);
		ViewUtils.inject(this);
		prepareView();
		initView();
		accountinfo = AppServer.getInstance().getAccountInfo();
		teacherlists = this.getIntent().getStringArrayListExtra("teacherlist");
		parentlists = this.getIntent().getStringArrayListExtra("parentlist");
	    showtime = this.getIntent().getStringExtra("time");
		if (teacherlists!=null && parentlists!=null) {
		   namelist.addAll(teacherlists);
		   namelist.addAll(parentlists);
	    }
		if (namelist!=null) {
		    showcount.setText(namelist.size() + "人");
		}		
		mobanneirong = this.getIntent().getStringExtra("content");
		if (mobanneirong!=null) {
			mobanContent.setText(mobanneirong);
			mobanContent.setSelection(mobanneirong.length());
		}	
		if (showtime!=null) {
			showTime.setText(showtime);
		}
		
		AppServer.getInstance().checkOpenSMS(accountinfo.getUid(), new OnAppRequestListener() {			
			@Override
			public void onAppRequest(int code, String message, Object obj) {
			    if (code == 0) {
			        info = (NotificationInfo)obj;
			    	if (info.getOpen() == 1) {
			    		showmsgcount.setText("您还可以使用" + info.getCount() + "条短信");
			    	} else {
			    		showmsgcount.setText("幼儿园没有开通短信服务，通知以手机客户端接受");
			    	}
			    }
			}
		});
	}

	private void initView() {
		showTime = (TextView) findViewById(R.id.sendnotify_show_time);
		showTime.setText("即时发送");
		showcount = (TextView) findViewById(R.id.id_sendmsg_namecount_tv);
	    selecttime = (RelativeLayout)findViewById(R.id.sendnotif_select_time_rl);
	    selectmoban = (RelativeLayout)findViewById(R.id.sendnotify_select_moban_rl);
	    selectpeople = (RelativeLayout)findViewById(R.id.sendnotify_select_people_rl);
		mobanContent = (EditText) findViewById(R.id.id_showmoban_content_et);
		showhistory = (RelativeLayout)findViewById(R.id.sendnotify_show_history_rl);
		showmsgcount = (TextView) findViewById(R.id.id_show_msg_count_tv);
		showhistory.setOnClickListener(this);
		selecttime.setOnClickListener(this);
		selectmoban.setOnClickListener(this);
		selectpeople.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		String content = mobanContent.getText().toString();
		String showtime = showTime.getText().toString();
		String struid = null;
		
		switch(arg0.getId()){
            case R.id.sendnotify_select_people_rl:
                Intent intent = new Intent(SendNotificationActivity.this, SendNotify_SelContactActivity.class);
                intent.putStringArrayListExtra("teacherlist", teacherlist);
                intent.putStringArrayListExtra("parentlist", parentlist);
                startActivityForResult(intent,0);
                break;
            case R.id.sendnotif_select_time_rl:
                PhotoInfalteDialog dialog = new PhotoInfalteDialog(SendNotificationActivity.this, R.layout.inflater_service_schedule_select_time,handler, "notification");
                dialog.show();
                break;
            case R.id.sendnotify_show_history_rl:
                Intent hisintent = new Intent(SendNotificationActivity.this, SendNotify_MsgHistory.class);
                startActivityForResult(hisintent,1);
                break;
            case R.id.right_tv:
                if (time == null) {
                    long times = System.currentTimeMillis(); // long now = android.os.SystemClock.uptimeMillis();
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date d1 = new Date(times);
                    String t1 = format.format(d1);
                    time = t1;
                }
                if (namelist!=null) {
                    StringBuffer buffer = new StringBuffer();
                    for (int index = 0; index < namelist.size(); index++) {
                        buffer.append(namelist.get(index));
                    }
                    uid = buffer.toString();
                }
                String[] uids = uid.split("//");
                List<String>listuid = Arrays.asList(uids);
                if (listuid!=null) {
                    StringBuffer buffer = new StringBuffer();
                    for(int index = 0; index < listuid.size(); index++) {
                        buffer.append(listuid.get(index)).append(",");
                    }
                    struid = buffer.toString();
                }
                if (struid!=null) {
                    int length = struid.length();
                    people = struid.substring(0, length - 1);
                }
                if (showtime.equals("即时发送")) {
                    time = "0";
                }
                if (info.getOpen() == 1) {
                    if (content!=null) {
                        AppServer.getInstance().SendNoitification(accountinfo.getUid(), time, people, content, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == AppServer.REQUEST_SUCCESS) {
                                    notifyinfo = new NotificationInfo(accountinfo.getUid(), time, mobanContent.getText().toString() , 1, 0, info.getCount(), info.getOpen());
                                    try {
//				    				    DbHelper.getDB(SendNotificationActivity.this).save(notifyinfo);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        this.finish();
                    } else {
                        Toast.makeText(this, "您还未填写通知内容", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "对不起，您尚未开通短信服务", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.sendnotify_select_moban_rl:
                Intent mobanintent=new Intent(SendNotificationActivity.this, SendNotify_SelectMoban.class);
                mobanintent.putExtra("content", content);
                mobanintent.putStringArrayListExtra("teacherlist", (ArrayList<String>) teacherlist);
                mobanintent.putStringArrayListExtra("parentlist",(ArrayList<String>) parentlist);
                mobanintent.putExtra("time", showtime);
                startActivity(mobanintent);
                this.finish();
                break;
            default:
                break;
        }
		
	}
	   private void prepareView(){
		    tv_headerTitle.setText(R.string.sendmsg_title);
	      	iv_right.setVisibility(View.GONE);
	      	tv_right.setVisibility(View.VISIBLE);
	      	tv_right.setText("发送");
	      	tv_right.setOnClickListener(this);
	   }
	   
	   class ShowNameAdapter extends BaseAdapter{

		   private  LayoutInflater mInflater;
		   private List<String>list;
		   private Context context;
		   public ShowNameAdapter(Context context,List<String>list) {		      
			    this.context=context;
			    this.list=list;
			    mInflater=LayoutInflater.from(context);
			   
		}
		@Override
		public int getCount() {
	
			return list.size();
		}

		@Override
		public Object getItem(int position) {
		
			return position;
		}

		@Override
		public long getItemId(int position) {
		
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
	
			convertView=mInflater.inflate(R.layout.inflater_sendmsg_showname, null);
			TextView name=(TextView) convertView.findViewById(R.id.id_inflater_sendmsg_showname_tv);
			name.setText(list.get(position));
			return convertView;
		}
		   
	   }
         @Override
       public boolean onKeyDown(int keyCode, KeyEvent event) {	    
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
        		  Intent intent=new Intent(SendNotificationActivity.this,MainActivity.class);  
            	  startActivity(intent);
        		  this.finish();
        		        return true;
        		    }
       	 
	    return super.onKeyDown(keyCode, event);
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
    protected void onActivityResult(int requestCode, int resultcode, Intent intent) { 	
    	 if(resultcode==RESULT_OK){
    		 switch (requestCode) {
			case 0:
				 teacherlist.clear();
				 parentlist.clear();
				 namelist.clear();
				teacherlist=intent.getStringArrayListExtra("teacherlist");
				parentlist=intent.getStringArrayListExtra("parentlist");	
				   if(teacherlist!=null&&parentlist!=null){
					   namelist.addAll(teacherlist);
					   namelist.addAll(parentlist);	
				      }
				   if(namelist!=null){
					showcount.setText(namelist.size()+"人");
					}	
				
				break;
			}
    		 
    	 }
    	 
    	super.onActivityResult(requestCode, resultcode, intent);
    }
}
