package com.yey.kindergaten.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.ThreadsBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.HomeFragement;
import com.yey.kindergaten.task.SimpleTask;
import com.yey.kindergaten.task.TaskExecutor;
import com.yey.kindergaten.task.TaskExecutor.OrderedTaskExecutor;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.HttpAssist;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.HttpAssist.ShowLoadingPercent;
import com.yey.kindergaten.util.HttpAssist.ShowLoadingState;
import com.yey.kindergaten.util.ImageLoadOptions;
/**
 * 处理图片断点续传类
 * @author zyj
 *
 */
public class UploadImageActivity extends BaseActivity implements OnClickListener,ShowLoadingState{

	//导航栏控件
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.right_btn)ImageView right_btn;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.right_tv)TextView right_tv;
	
	//内部控件
	@ViewInject(R.id.id_show_upload_lv)ListView upload_lv;
	
    private ConnectivityManager connectivityManager;
   
    private NetworkInfo info;
	
    public static String action="upload_action";  
    
	private List<ThreadsBean> threadslist=new ArrayList<ThreadsBean>();
	
	private List<Photo> photolist=new ArrayList<Photo>();
	
	private List<Photo> secondloadlist=new ArrayList<Photo>();
  
	private UpLoadAdapter adapter;
    
    private HttpAssist httpAssist;
    
    private SimpleTask<Integer> simple;
    
    private Photo bean;
    
    private ThreadsBean tbean;
    private String bType;
    private String photp_decs;
    private ArrayList<String>childlist;
    private String term;
    private String net;
    private  LifePhoto  photo;
    public  Handler netHandler=new Handler(Looper.getMainLooper()){
    	 @Override
  		public void handleMessage(android.os.Message msg) {			
  			if(msg.what==111){	
  				 List<ThreadsBean> list = new ArrayList<ThreadsBean>();
                 httpAssist.setPauseFlag(false);
             	try {
			    	threadslist = DbHelper.getDB(UploadImageActivity.this).findAll(ThreadsBean.class);
			    	  if(threadslist==null||threadslist.size()==0){
				            return;
				        }else{
				        	bean= new Photo();
				        	bean.imgPath = threadslist.get(0).getUrl();
				        }
				        String path = bean.imgPath;
				        if(path.contains("//")){		 
				        int index = threadslist.get(0).getIndexs(); 	
				        String[]pathlit = path.split("//");
				        if(secondloadlist!=null){
				          for(int i=index;i<pathlit.length;i++){
				        	    ThreadsBean	bean=new ThreadsBean();
				        	    if(i==index){
			                    bean.setCurrentSize(threadslist.get(0).getCurrentSize());
				        	      }else{
				        	    bean.setCurrentSize(0);
				        	      }
			                    bean.setId(1);
			                    bean.setThreadid(1);
			                    bean.setFlag("2");                    
			                    bean.setUrl(pathlit[i]);
			                    list.add(bean);
				           }
				         }	    						  
				        }else{
				    	    ThreadsBean	bean=new ThreadsBean();
		                    bean.setCurrentSize(threadslist.get(0).getCurrentSize());
		                    bean.setId(1);
		                    bean.setThreadid(1);
		                    bean.setFlag("1");                    
		                    bean.setUrl(path);
		                    list.add(bean);
				        }
						   adapter=new UpLoadAdapter(list);		   
						   upload_lv.setAdapter(adapter);	
             	} catch (DbException e) {
				    e.printStackTrace();
			    }
  			}else{
  			   httpAssist.setPauseFlag(true);  			   
  			  try {
				threadslist = DbHelper.getDB(UploadImageActivity.this).findAll(ThreadsBean.class);
		        if(threadslist==null||threadslist.size()==0){
		            return;
		        }else{
		        	bean= new Photo();
		        	bean.imgPath = threadslist.get(0).getUrl();
		        }
		        String path = bean.imgPath;
		        if(path.contains("//")){				     		 
		        int index = threadslist.get(0).getIndexs(); 	
		        String[]pathlit = path.split("//");
		        if(secondloadlist!=null){
		        	   if(secondloadlist!=null){
		        		   secondloadlist.clear();	
			    		}else{
			    			secondloadlist = new ArrayList<Photo>();
			    		}
		          for(int i=index;i<pathlit.length;i++){
		        	  bean = new Photo();
		        	  bean.imgPath=pathlit[i];
		        	  secondloadlist.add(bean);
		           }
		         }	
		        }else{
		      	  bean = new Photo();
	        	  bean.imgPath=path;
	        	  secondloadlist.add(bean);
		        }
		        initdata(secondloadlist.size());
		        
  			  } catch (DbException e) {               
				e.printStackTrace();
			 }
  			}
  		};
    };
    private int position=0;
    int sumSize=0;
    
    private String type;
      
    private boolean flag=true;
    
    private String lifeworktype;
    
    /**更新进度条的handler*/
	private  Handler handler = new Handler(){
		     @Override
			public void handleMessage(Message msg) {
		    	  if(msg.what==000){//开始上传
		    	    int size=msg.arg1;
		    		ThreadsBean bean=null;
		    		if(threadslist!=null){
		    			threadslist.clear();	
		    		}else{
		    			threadslist = new ArrayList<ThreadsBean>();
		    		}		    	
		    		AppContext.getInstance().setAmShow(true);
		    		if(flag){
		    			AppContext.getInstance().setAmShow(true);
		    			Intent intent =new  Intent(action);
		    			intent.putExtra("type", "begin");  
		    			sendBroadcast(intent);	    			
		    			flag=false;
		    		}	
		    		for(int i=position;i<photolist.size();i++){//根据position的位置，来刷新当前的适配器
		    			bean=new ThreadsBean();
	                    bean.setCurrentSize(size);
	                    bean.setId(1);
	                    bean.setThreadid(1);
	                    bean.setFlag("1");                    
	                    bean.setUrl(photolist.get(i).imgPath);
	                    threadslist.add(bean);		    			
		    		  }
		    		 adapter.addData(threadslist);		    	
		    		  if(sumSize==size&&sumSize!=0){	
						if(photolist.size()-1==position) 
						  {
							Intent intent =new  Intent(action);
							intent.putExtra("type", "over"); 
							intent.putExtra("lifetype", lifeworktype);
							intent.putExtra("desc", photp_decs);
							//在上传结束后，可能广播速度过快，启动延时发送广播。
							PendingIntent pendingIntent =PendingIntent.getBroadcast(UploadImageActivity.this, 
									   RESULT_OK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
							AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);  
							alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);//只执行一次 			    												
					        threadslist.clear();
					    	AppContext.getInstance().setAmShow(false);
					    	Toast.makeText(UploadImageActivity.this, "上传完成！", 2000).show();
					    	UploadImageActivity.this.finish();
					    	//注意点：由于需要在主页随时查看上传动态，singalTask模式下，会把上面的全部移除，所以在上传完成后，必须finish掉这个activity
					    	}
						}
		    	  }else if(msg.what==111){//断点上传
		    		  int size=msg.arg1;	    		
			    		ThreadsBean bean=null; 
			    		if(threadslist!=null){
			    			threadslist.clear();	
			    		}else{
			    			threadslist = new ArrayList<ThreadsBean>();
			    		}
			    		AppContext.getInstance().setAmShow(true);
									
			    		if(flag){
			    			Intent intent =new  Intent(action);
			    			intent.putExtra("type", "begin");  
			    			sendBroadcast(intent);	    			
			    			flag=false;
			    			System.out.println("正在上传的position--->"+position);
			    		}
			    		
						for(int i=position;i<secondloadlist.size();i++){//根据position的位置，来刷新当前的适配器
			    			bean=new ThreadsBean();
		                    bean.setCurrentSize(size);
		                    bean.setId(1);
		                    bean.setThreadid(1);
		                    bean.setFlag("1");                    
		                    bean.setUrl(secondloadlist.get(i).imgPath);
		                    threadslist.add(bean);		    			
			    		  }
						adapter.addData(threadslist);
					
						if(sumSize==size){
						  if(secondloadlist.size()-1==position){
							threadslist.clear();
							adapter.addData(threadslist);
							Intent intent =new  Intent(action);
							intent.putExtra("type", "over"); 
							intent.putExtra("lifetype", lifeworktype);
							//在上传结束后，可能广播速度过快，启动延时发送广播。
							PendingIntent pendingIntent =PendingIntent.getBroadcast(UploadImageActivity.this, 
									   RESULT_OK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
							AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);  
							alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+500, pendingIntent);//只执行一次 			    												
					    	AppContext.getInstance().setAmShow(false);
					    	UploadImageActivity.this.finish();
							Toast.makeText(UploadImageActivity.this, "上传完成！", 2000).show();
						 }
						}
		    	  }
		      };
	};
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_image_activity);
		ViewUtils.inject(this);		
		Bundle bundler=this.getIntent().getExtras();
		photolist.clear();
		photolist=bundler.getParcelableArrayList(AppConstants.PHOTOLIST);	
		term = bundler.getString("term");
		childlist = bundler.getStringArrayList("childlist");
		photp_decs =bundler.getString("decs");
	    lifeworktype=bundler.getString("lifetype");
	    bType= bundler.getString("type");
	    if(bType==null){
	    	bType="";
	    }
	    photo=(LifePhoto) bundler.getSerializable("lifephoto");
	
		
		type=this.getIntent().getStringExtra("fromtype");
	
		if(childlist!=null){
	     	httpAssist=new HttpAssist(lifeworktype,photp_decs,childlist, term);
	     }else{
		  ArrayList<String>photolist=new ArrayList<String>();
	      if(type!=null&&!type.equals("upload_go_on")){
		  if(photo!=null){
			photolist.add(photo.getUserid()+"");
			httpAssist=new HttpAssist(lifeworktype,photp_decs,photolist, term);
			 }else{
			   try {
				 List<ThreadsBean>listbean=DbHelper.getDB(this).findAll(ThreadsBean.class);
			     if(listbean!=null){
				  ThreadsBean bean = listbean.get(0);		   
			     if(bean.getUids().contains("//")){	    
	              String[]uidlist=bean.getUids().split("//");
	             for(int i=0;i<uidlist.length;i++){
	               photolist.add(uidlist[i]);
	              }
	              }else{
	        	    photolist.add(bean.getUids()+"");
	             }     			    	    	
				httpAssist=new HttpAssist(bean.getLifetype(),bean.getDecs(),photolist, bean.getTerm());}
				} catch (DbException e) {
					e.printStackTrace();
				}				
			}
		}else if(type==null&&photo!=null){
			photolist.add(photo.getUserid()+"");
			httpAssist=new HttpAssist(lifeworktype,photp_decs,photolist, term);
		}
	      else{
			getPotolist();
//			photolist.add(photo.getUserid()+"");
//			httpAssist=new HttpAssist(lifeworktype,photp_decs,photolist, term);
		 }
	    }
	   
	 
		//监听网络广播
		IntentFilter mFilter = new IntentFilter();
	    mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    registerReceiver(mReceiver, mFilter);	
	   
	    initView();
		initClick();		
		List<ThreadsBean> list=null;//为了不让监听广播
		try {
			list=DbHelper.getDB(this).findAll(ThreadsBean.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		if(type==null&&(list==null||list.size()==0)&&!bType.equals("fromlife")){			
		      initdata(photolist.size());	
		      Intent intent =new Intent(UploadImageActivity.this,LifeWorkManagerActivity.class);
	          Bundle bun = new Bundle();
	          bun.putSerializable(AppConstants.PARAM_ALBUMID, photo);
	          intent.putExtras(bun);
		      startActivity(intent);
	    }else if(bType.equals("fromlife")){
	    	  initdata(photolist.size());	
	    	  Intent intent =new Intent(UploadImageActivity.this,ServiceLifePhotoMainActivity.class);
			  startActivity(intent);
	    }
		
		if(type!=null){ 
	    	if(type.equals("upload_go_on")){
			  Intent intent =new Intent(UploadImageActivity.this,ServiceLifePhotoMainActivity.class);
			  Bundle bun = new Bundle();
		      bun.putSerializable(AppConstants.PARAM_ALBUMID, photo);
		      intent.putExtras(bun);
			  startActivity(intent);
	    	}
	   }
	  }
	
	public void getPotolist(){
		 ArrayList<String>photolist=new ArrayList<String>();
		
		 try {
			List<ThreadsBean>listbean=DbHelper.getDB(this).findAll(ThreadsBean.class);
		 
			if(listbean!=null&&listbean.size()!=0){
			   
			ThreadsBean bean = listbean.get(0);
		   
		    if(bean.getUids().contains("//")){	    
            String[]uidlist=bean.getUids().split("//");
            for(int i=0;i<uidlist.length;i++){
               photolist.add(uidlist[i]);
            }
            }else{
        	   photolist.add(bean.getUids()+"");
            }     			    	    	
			httpAssist=new HttpAssist(bean.getLifetype(),bean.getDecs(),photolist, bean.getTerm());}
			} catch (DbException e) {
				e.printStackTrace();
			}	
	}
	
    @SuppressWarnings("rawtypes")
	public void initdata(int taskSize) {		
	   	   List<SimpleTask> listTask = new ArrayList<SimpleTask>(); 	   	 	   	   
    		for(int i=0;i<taskSize;i++){
    			listTask.add(getTask(i));
    		}
    		OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
    		for(int i=0;i<taskSize;i++){
    			executor.put(listTask.get(i));     			
    		}  		
    		executor.start(); 
	}

    @Override
    protected void onResume() {
    	  Bundle bundler=this.getIntent().getExtras();
    	 bType= bundler.getString("type");
    	 flag=true;
    	super.onResume();
    }
     
       private SimpleTask<Integer> getTask(final int position){		
		 simple = new SimpleTask<Integer>() {
			@Override
			protected Integer doInBackground() {
			   initData(position);										
				return position;
			}				
			@Override
			protected void onCancelled() {
			
				super.onCancelled();
			}
		};
		return simple;		
	}
	
	/**
	 * 初始化数据，继续上传
	 */
  	 private void initData(int position) {
		try {
			threadslist = DbHelper.getDB(this).findAll(ThreadsBean.class);
			this.position=position;
			      if(threadslist!=null&&threadslist.size()!=0){
			    	  
			    	  if(secondloadlist!=null&&secondloadlist.size()!=0){
			    	    File file=new File(secondloadlist.get(position).imgPath);
			    	    int size=0;
			    	    if(position==0){
			    	    	size = threadslist.get(position).getCurrentSize();	
			    	    }else{
			    	        size = 0;	
			    	    }										
						httpAssist.setLoadingState(UploadImageActivity.this);
						System.out.println(secondloadlist.get(position).imgPath+"-->这个时候的东西");
			            System.out.println("file.getName()--->"+file.getName());
						httpAssist.uploadFile(secondloadlist,UploadImageActivity.this,file,position,size,secondloadlist.get(position).imgPath,new ShowLoadingPercent() {							
							@Override
							public void setUploadPercent(int currentSize, boolean flag) {
								Message msg=new Message();
								msg.what=111;
								msg.arg1=currentSize;
								handler.sendMessage(msg);							
							}
						});}
				    }else{	 
				    	File files=null;
				    	if(photolist==null||photolist.size()==0)
				          { 
				    	    photolist = new ArrayList<Photo>();
				    	    if(threadslist!=null){
				    	       String url = threadslist.get(0).getUrl();
				    	       if(url.contains("//")){
				    	    	   String[]urls=url.split("//");
				    	    	   for(int i=0;i<urls.length;i++){
				    	    		   Photo photo = new Photo();
					    	    	   photo.imgPath = urls[i];
					    	    	   photolist.add(photo);
				    	    	   }				    	    				    	    	   
				    	       }else{
				    	    	   Photo photo = new Photo();
				    	    	   photo.imgPath = url;
				    	    	   photolist.add(photo);
				    	       }
				    	    }else{
				    	    	return;
				    	    }			    		
				    	  }
				    	else{
				    		files=new File(photolist.get(position).imgPath);
				    	}			    
				    	httpAssist.setLoadingState(UploadImageActivity.this);
				    	
				    	httpAssist.uploadFile(photolist,UploadImageActivity.this,files,position,new ShowLoadingPercent() {						
							@Override
							public void setUploadPercent(int currentSize, boolean flag) {
								Message msg=new Message();
								msg.what=000;
								msg.arg1=currentSize;
								handler.sendMessage(msg);								
							}
						});
				    }				
		} catch (DbException e) {
			e.printStackTrace();
		}	
	}

     
//     private void updateView(int index,int currentSize)
//     {
//         int visiblePos = upload_lv.getFirstVisiblePosition();
//         int offset = index - visiblePos;
//
//         if(offset < 0) return; 
//         View view = upload_lv.getChildAt(offset);
//         
//           ViewHolder holder = (ViewHolder)view.getTag();
////         final ThreadsBean bean=adapter.list.get(index);
//    	   imageLoader.displayImage("file:///"+photolist.get(0).imgPath, holder.head_iv,ImageLoadOptions.getAppPicOptions());      	 
//    	   File file = new File(photolist.get(0).imgPath);
//    	   holder.path_tv.setText(file.getName());
//    	   if(sumSize!=0){
//    	   holder.percent_pb.setMax(sumSize);
//    	   }else{
//    	   holder.percent_pb.setMax(100);   
//    	          sumSize=100;
//    	   }
//    	   if(position==0){ 
//    	      holder.percent_pb.setProgress(currentSize);   	 
//    	      holder.percent_tv.setText((int) ((float) currentSize/ sumSize * 100)+"%");
//    	   }else{
//    		  holder.percent_pb.setProgress(0); 
//    		  holder.percent_tv.setText("0%");      		
//    	   }
//    	   holder.puase_btn.requestFocus();          	 
//     }
     
     
	private void initClick() {
		left_btn.setOnClickListener(this);		
	 }

	  protected  void initView(){ 
  		   upload_lv.setDivider(null);
  		   upload_lv.setClickable(false);
	       titletv.setVisibility(View.VISIBLE);    	
	       titletv.setText("上传图片");
	       left_btn.setVisibility(View.VISIBLE);    		    	
	    }

	  @Override
	  public void onClick(View v) {
		    switch (v.getId()) {
			case R.id.left_btn:		
				if(bType.equals("fromlifemain")){
				   Intent intent = new Intent(UploadImageActivity.this,LifeWorkManagerActivity.class);
			       startActivity(intent);		
				}else if(bType.equals("fromlife")){
				   Intent intent = new Intent(UploadImageActivity.this,ServiceLifePhotoMainActivity.class);
				   startActivity(intent);	
				}else if(type!=null||type.equals("upload_go_on")){
					Intent intent = new Intent(UploadImageActivity.this,ServiceLifePhotoMainActivity.class);
					startActivity(intent);	
				}
			break;
			}
	  }	  
	  
	  @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if(keyCode==KeyEvent.KEYCODE_BACK){
	 		if(bType.equals("fromlifemain")){
				   Intent intent = new Intent(UploadImageActivity.this,LifeWorkManagerActivity.class);
			       startActivity(intent);		
				}else if(bType.equals("fromlife")){
				   Intent intent = new Intent(UploadImageActivity.this,ServiceLifePhotoMainActivity.class);
				   startActivity(intent);	
				}else if(type!=null||type.equals("upload_go_on")){
					Intent intent = new Intent(UploadImageActivity.this,ServiceLifePhotoMainActivity.class);
					startActivity(intent);	
				}	 
	     }
		return super.onKeyDown(keyCode, event);
	}
	  
	  
	class UpLoadAdapter extends BaseAdapter{
        
        private List<ThreadsBean> list;
	    private LayoutInflater mInflater;
	     
		public UpLoadAdapter(List<ThreadsBean>list) {
			 this.list=list;
			 this.mInflater=LayoutInflater.from(UploadImageActivity.this);
	    }
		  
		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public Object getItem(int position) {
	
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
		
			return position;
		}
		
		public void addData(List<ThreadsBean> list){
			this.list=list;
			notifyDataSetChanged();
			notifyDataSetInvalidated();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final   ViewHolder holder;
			if(convertView==null){
				holder=new ViewHolder();
				convertView=mInflater.inflate(R.layout.inflater_show_upload_item, null);
				holder.head_iv=(ImageView) convertView.findViewById(R.id.id_show_image_state_iv);
				holder.path_tv=(TextView) convertView.findViewById(R.id.id_show_image_path_tv);
				holder.percent_tv=(TextView) convertView.findViewById(R.id.id_show_upload_percent_tv);
				holder.percent_pb=(ProgressBar) convertView.findViewById(R.id.id_show_upload_progress_pb);
				holder.puase_btn=(Button)convertView.findViewById(R.id.id_pause_start_upload_btn);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
	      	   final ThreadsBean bean=list.get(position);
	      	   imageLoader.displayImage("file:///"+bean.getUrl(), holder.head_iv,ImageLoadOptions.getAppPicOptions());      	 
	      	   File file = new File(bean.getUrl());
	      	   holder.path_tv.setText(file.getName());
	      	   if(bean.getFlag().equals("2")){
	      		   sumSize=(int) file.length();
	      	   }
	      	   if(sumSize!=0){
	      	       holder.percent_pb.setMax(sumSize);
	      	      }else{
	      	      holder.percent_pb.setMax(100);   
	      	        sumSize=100;
	      	      } 	         	   
	      	   if(position==0){ 
	      	      holder.percent_pb.setProgress(bean.getCurrentSize());   	 
	      	      holder.percent_tv.setText((int) ((float) bean.getCurrentSize()
						                        / sumSize * 100)+"%");
	      	   }else{
	      		  holder.percent_pb.setProgress(0); 
	      		  holder.percent_tv.setText("0%");      		
	      	   }
	      	   holder.puase_btn.requestFocus();
	      	   holder.puase_btn.setClickable(true);
	      	   holder.puase_btn.setFocusable(true);
	      	   holder.puase_btn.setEnabled(true);
	      	   holder.puase_btn.setVisibility(View.INVISIBLE);			      
	      	   holder.puase_btn.setOnClickListener(new OnClickListener() {			
			  @Override
			  public void onClick(View v) {							
					   list.remove(position);
					   photolist.remove(position);
					   adapter.notifyDataSetChanged();  
			     }
			  });  			 
			return convertView;
		  }		 		  
	   }	  
	  @Override
	protected void onDestroy() {
		super.onDestroy();
        unregisterReceiver(mReceiver);
	}
	  
	  
	  /**
	   *监听网络广播
	   */
	 private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {  
	                connectivityManager = (ConnectivityManager)      
	                                         getSystemService(Context.CONNECTIVITY_SERVICE);
	                info = connectivityManager.getActiveNetworkInfo();  
	                if(info != null && info.isAvailable()) {
	                    netHandler.sendEmptyMessage(000); //表示有网络     
	                } else {
	                	netHandler.sendEmptyMessage(111);//表示没网络              	
	                }
	            }
	        }
	    };
   @SuppressWarnings("unused")
    class ViewHolder{		 
		  private ImageView head_iv;
		  private TextView  path_tv;
		  private TextView  percent_tv;
		  private ProgressBar percent_pb;	
		  private Button puase_btn;
	  }
   
    /**
    * 回调函数，当执行上传时，回调显示界面
    */
    @Override
   public void setUploadAdapter(final int sumSize,final List<ThreadsBean> list, Boolean flag) {
    	if(flag){
    		threadslist=list;
    		UploadImageActivity.this.sumSize=sumSize;
    		runOnUiThread(new Runnable() {			
				@Override
				public void run() {
				   adapter=new UpLoadAdapter(list);		   
				   upload_lv.setAdapter(adapter);					
				}
			});   	
    	}
  }        
}
