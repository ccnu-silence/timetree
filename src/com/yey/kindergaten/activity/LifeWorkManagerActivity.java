package com.yey.kindergaten.activity;

import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ImagesAdapter;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.ThreadsBean;
import com.yey.kindergaten.bean.WLImage;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.ImageLoadOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class LifeWorkManagerActivity extends BaseActivity implements OnClickListener, OnItemClickListener{
 
	//导航栏控件
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.right_btn)ImageView right_btn;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.right_tv)TextView right_tv;
	
	@ViewInject(R.id.id_show_life_work_gv)GridView lifework_gv;
	@ViewInject(R.id.id_show_edit_ll)LinearLayout showedit_rl;
	@ViewInject(R.id.id_edit_decs_btn)Button  decs_btn;
	@ViewInject(R.id.id_edit_delete_btn)Button del_btn;
	@ViewInject(R.id.id_show_upload_state)ImageView upload_state;
	public String phototype = null;
	
	private LifePhoto photo = null;
    
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
    private List<WLImage>imagelist=null;
    
	private  ImagesAdapter adapter =null;
    
    private List<Object>newImageList =new ArrayList<Object>();
    /**存放的选中的图片状态*/
    private HashMap<Integer,Boolean> select_map= new HashMap<Integer, Boolean>();
    /**存放选中图片的id*/
    private List<String> photoidlist= new ArrayList<String>();
    
    private String edit_flag=null;
    /**编辑状态*/
    private String EDIT_STATE="edit_action";
    /**正常状态*/
    private String NORMAL_STATE="nor_action";
    
	private static final String PATH = Environment
			.getExternalStorageDirectory() + "/yey/kindergaten/lifephoto/";
	private DisplayImageOptions options;
	private String name;
    ArrayList<WLImage> checkList;
	private String term;
	private String type;
	private String lifetype;
    public static boolean is_uploading=false;
	private ConnectivityManager connectivityManager;
	public boolean editAction  = false;
	private AnimationDrawable animationDrawable;  
	private NetworkInfo info;
	private boolean isFlag=true;
	private static final int PHOTO_SUCCESS = 1; //拍照
    private static final int CAMERA_SUCCESS = 2; //相册
 	List<ThreadsBean>threadlist = null;
	private Handler netHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {	    
	
			if(msg.what==AppConstants.NET_SENDMESSAG_WHAT_CODE_HASNET){
		   		        if(animationDrawable!=null){  		      
		   		        	animationDrawable.start();}	   		        
		    	  }else{
			  		   if(animationDrawable!=null){
		   		        	animationDrawable.stop();
		   		        }
		    	  }	   			   
		 };
	 } ;
    
	CharSequence[] items = { "手机上传照片"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life_work_manager);
		ViewUtils.inject(this); 
		
		photo = (LifePhoto) getIntent().getSerializableExtra(AppConstants.PARAM_ALBUMID);
		phototype = getIntent().getStringExtra(AppConstants.INTENT_ALBUM_TYPE);
		edit_flag = NORMAL_STATE;
		term = getIntent().getStringExtra("term");
		lifetype = getIntent().getStringExtra("lifetype"); 
		type= getIntent().getStringExtra("type");
		
		  options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();		
		initView();
	
		adapter = new ImagesAdapter(LifeWorkManagerActivity.this, newImageList, null, AppConstants.PARAM_UPLOAD_WORK, ImageLoadOptions.getGalleryOptions(),ImageLoader.getInstance());
		
		lifework_gv.setAdapter(adapter);
		lifework_gv.setOnItemClickListener(this);
	
		adapter.setOnInViewClickListener(R.id.selectphoto_select, new ImagesAdapter.onInternalClickListener() {

			@Override
			public void OnClickListener(View parentV, View v,
					Integer position, Object values) {				
			     	adapter.setCheck(position, parentV);
			}
		});
		adapter.setOnInViewClickListener(R.id.selectphoto_unselect, new ImagesAdapter.onInternalClickListener() {

			@Override
			public void OnClickListener(View parentV, View v,
					Integer position, Object values) {
				   adapter.setCheck(position, parentV);
			}
		});
		//监听网络广播
		IntentFilter mFilter = new IntentFilter();
	    mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    registerReceiver(mReceiver, mFilter);	
	       	
    	//注册动态广播，更新上传状态
    	IntentFilter filter = new IntentFilter(UploadImageActivity.action); 
        registerReceiver(broadcastReceiver, filter); 
	                  
	
	
    	try {
    		threadlist=DbHelper.getDB(this).findAll(ThreadsBean.class);
         if(threadlist==null||threadlist.size()==0){
//    		AppContext.getInstance().setAmShow(false);
    	 }else{
//    		 AppContext.getInstance().setAmShow(true);    	   
    	 }
    	 } catch (DbException e) {
			e.printStackTrace();
		 }
    	boolean flag = AppContext.getInstance().isAmShow();
        String  from = AppContext.getInstance().getIsFromMain();
    	System.out.println(flag+"--onCreat--"+from);
    	 if(AppContext.getInstance().isAmShow()&&AppContext.getInstance().getIsFromMain().
  	    		  equals("fromlifemain")){
  	    	   upload_state.setVisibility(View.VISIBLE);				      
  		  }	else{
  			   upload_state.setVisibility(View.GONE);	
  		  }
		initClick();
		initData();
	}
	
	@Override
	protected void onResume() {
		boolean flag = AppContext.getInstance().isAmShow();
        String  from = AppContext.getInstance().getIsFromMain();
    	System.out.println(flag+"--onResume--"+from);
	    if(AppContext.getInstance().isAmShow()&&AppContext.getInstance().getIsFromMain().
	    		  equals("fromlifemain")){
	    	   upload_state.setVisibility(View.VISIBLE);				      
		  }	else{
			   upload_state.setVisibility(View.GONE);	
		  }
		super.onResume();
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
	                    netHandler.sendEmptyMessage(AppConstants.NET_SENDMESSAG_WHAT_CODE_HASNET);//表示有网络     
	                } else {
	                	netHandler.sendEmptyMessage(AppConstants.NET_SENDMESSAG_WHAT_CODE_NONET);//表示没网络              	
	                }
	            }
	        }
	    };
	
	   
	    
	private void initData() {	
		editAction = false;
		showDelView(editAction);
		
		AppServer.getInstance().getChildLifePhoto(lifetype, 1,10,photo.getGbid(),new OnAppRequestListener() {		
			@Override
		   public void onAppRequest(int code, String message, Object obj) {
               if(code == 0){  
            	 newImageList.clear();
            	 imagelist = (List<WLImage>) obj;
				 WLImage wlphoto = new WLImage();
				 wlphoto.setM_path("add");
				 newImageList.add(wlphoto);
				 Iterator<WLImage>it=imagelist.iterator();				 
				 while (it.hasNext()) {
					 wlphoto=it.next();
					 newImageList.add(wlphoto);
				}
				 adapter.setList(newImageList);
               }else if(code ==1){
            	 newImageList.clear();
              	 imagelist = (List<WLImage>) obj;
  				 WLImage wlphoto = new WLImage();
  				 wlphoto.setM_path("add");
  				 newImageList.add(wlphoto);     	   
               }	
               adapter.setList(newImageList);				
			}
		});
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}
	
	private void initClick() {
	  	 right_tv.setOnClickListener(this);
    	 left_btn.setOnClickListener(this);		
    	 decs_btn.setOnClickListener(this);
    	 del_btn.setOnClickListener(this);
    	 lifework_gv.setOnItemClickListener(this);
    	 upload_state.setOnClickListener(this);
	}
	
	private void initView() {
		titletv.setVisibility(View.VISIBLE);    	
    	titletv.setText(photo.getName());
    	left_btn.setVisibility(View.VISIBLE);
    	right_tv.setVisibility(View.VISIBLE);
    	right_tv.setText("编辑");	 	
    	lifework_gv.setHorizontalSpacing(5);
    	lifework_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
    	animationDrawable= (AnimationDrawable) upload_state.getDrawable();		              
	}

	@Override
	public void onClick(View v) {
		  switch (v.getId()) {	  
		case R.id.right_tv:	
			if(!editAction){
				editAction = true;
				showDelView(editAction);
			}else{			
				editAction = false;				
				showDelView(editAction);
			}			
			break;
		case R.id.left_btn:
			Intent intent = new Intent(LifeWorkManagerActivity.this,ServiceLifePhotoMainActivity.class);
			startActivity(intent);	
			this.finish();
			break;
		case R.id.id_show_upload_state:
			Intent uploadIntent = new Intent(LifeWorkManagerActivity.this,	 UploadImageActivity.class);;
		    uploadIntent.putExtra("lifephoto",photo);
            uploadIntent.putExtra("fromtype", "uploading");
            Bundle bundles = new Bundle();
            bundles.putString("type", "fromlife");	
            uploadIntent.putExtras(bundles);
			startActivity(uploadIntent);
			break;			
		case R.id.id_edit_decs_btn:
			checkList = adapter.getCheckImageList();
			 photoidlist.clear();
			 if(checkList!=null&&checkList.isEmpty()){
				 showToast("请选择相片后在编辑");
			 }else{
				 Iterator<WLImage> it = checkList.iterator();
				 while (it.hasNext()) {
					WLImage wlImage = (WLImage) it.next();
					photoidlist.add(wlImage.getPhotoid()+"");					
				}				 
				 final EditText et = new EditText(this);
				 et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,40));
				 
				 showDialog("请输入描述内容", et, new DialogInterface.OnClickListener() {									
					 @Override
					public void onClick(DialogInterface dialog, int which) {	
						showLoadingDialog("正在加载");
						String photoids=null;
						StringBuffer buffer = new StringBuffer();		
						for(int i=0;i<photoidlist.size();i++){
							 buffer.append(photoidlist.get(i)).append(","); 
							if(i==photoidlist.size()-1){
								 buffer.append(photoidlist.get(i)); 
							}						
						 }
					     photoids = buffer.toString();
					     String rom = et.getText().toString();
						 AppServer.getInstance().editChldPhoto(lifetype,photoids, et.getText().toString(), new OnAppRequestListener() {					
							@Override
							public void onAppRequest(int code, String message, Object obj) {
							    if(code==0){							   							    
							        RefreshPhoto(" ");
							        showToast("描述修改成功");								    							        
							   	}else{
							   	   RefreshPhoto(NORMAL_STATE);
							       showToast("修改失败");
							   	}							
							}
						});
					}
				});
			 }			
			break;
		case R.id.id_edit_delete_btn:
			 photoidlist.clear();
			checkList = adapter.getCheckImageList();
			if(checkList!=null&&checkList.isEmpty()){
				 showToast("请选择相片后在删");
			 }else{
			    Iterator<WLImage> it = checkList.iterator();
				 while (it.hasNext()) {
					WLImage wlImage = (WLImage) it.next();
					photoidlist.add(wlImage.getPhotoid()+"");					
				 }
			    final String photoids;
				StringBuffer buffer = new StringBuffer();		
				for(int i=0;i<photoidlist.size();i++){
					 buffer.append(photoidlist.get(i)).append(","); 
					if(i==photoidlist.size()-1){
					 buffer.append(photoidlist.get(i)); 
					}						
				 }
			    photoids = buffer.toString();			    
		        showDialog("删除照片", "您选择了"+photoidlist.size()+"张图片，确定删除吗？", "确定",new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
					photoidlist.clear();			
				    showLoadingDialog("正在加载");
					AppServer.getInstance().deleteChildPhoto(photoids,new OnAppRequestListener() {						
						@Override
						public void onAppRequest(int code, String message, Object obj) {
						      if(code==0){						    			    	
						    	  RefreshPhoto(" ");
						    	  showToast("删除成功");
						        }else{
						    	  RefreshPhoto(NORMAL_STATE);	 
						    	  showToast("删除失败");					    	  
						        }
						    }
					    });						
					 }
				});
			 }		
			break;
		}
	}
	
	/**
	 * 刷新动作
	 * @param action
	 */
	public void RefreshPhoto(String action){
		if(action.equals(NORMAL_STATE)){
		  if(newImageList!=null&&newImageList.size()!=0){
//			adapter.addData(newImageList);
			}
		}else{
			 right_tv.setText("编辑");
			 edit_flag=NORMAL_STATE;
			 showedit_rl.setVisibility(View.GONE);
			 initData();			
		}	
		loadingdialog.dismiss();
	}
		
	public void showDelView(boolean action){
		if(action){
			adapter.setAction(true);
			editAction = true;
			showedit_rl.setVisibility(View.VISIBLE);
			right_tv.setText("取消");
		}else{
			adapter.setAction(false);
			editAction = false;
			showedit_rl.setVisibility(View.GONE);
			right_tv.setText("编辑");
		}		
	}
	
	private void showDialog(){
		  showDialogItems(items, "上传照片", new DialogInterface.OnClickListener() {			
 				@Override
 				public void onClick(DialogInterface dialog, int item) {
 			        switch (item) {
 					case 0:
 			        	Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                     	name = DateFormat.format("yyyyMMddhhmmss",
             					Calendar.getInstance(Locale.CHINA))
             					+ ".jpg";
                     	File file = new File(PATH+"takephoto/"); 		            			
             			if(!file.exists()){
             				file.mkdirs();// 创建文件夹  
             			}       
             			Uri imageUri = Uri.fromFile(new File(PATH, name));         
                     	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, CAMERA_SUCCESS);         //2
 						break;
 					case 1:
 						Intent i=new Intent(LifeWorkManagerActivity.this,GetSDCardAlbumActivity.class);
 						i.putExtra("typefrom", "fromlifemain");
 						i.putExtra("lifetype", lifetype);
 						i.putExtra("term", term);
 						i.putExtra("lifephoto", photo);
 						startActivity(i);
 						break;
 					}   					
 				}
 			});
	}	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(editAction){
            if(position==0){
	       
            }else{
               adapter.setCheck(position, view);
            }
		}else{
			if(position!=0){
//			    	checkList = adapter.getCheckImageList();
					Intent intent = new Intent(LifeWorkManagerActivity.this,PhotoManager_ViewPager.class);
				    ArrayList<String>imglist = new ArrayList<String>();
				    ArrayList<String>desclist = new ArrayList<String>();
				    Iterator<Object> it = newImageList.iterator();
				    while(it.hasNext()){
				    	WLImage bean = (WLImage) it.next();
				    	imglist.add(bean.getM_path());
				    	desclist.add(bean.getPhoto_desc());					    	
				    }					
					Bundle bundler = new Bundle();
					desclist.remove(0);
					imglist.remove(0);
					bundler.putSerializable("childname", titletv.getText().toString());
					bundler.putStringArrayList("decslist", desclist);
					bundler.putStringArrayList("imglist", imglist);
					bundler.putInt("position",position-1 );
					bundler.putString("type", "lifeworktype");
					intent.putExtras(bundler);
					LifeWorkManagerActivity.this.startActivity(intent);				
			   }else{
//				  showDialog(); 
					Intent i=new Intent(LifeWorkManagerActivity.this,GetSDCardAlbumActivity.class);
					i.putExtra("typefrom", "fromlifemain");
					i.putExtra("lifetype", lifetype);
					i.putExtra("term", term);
					i.putExtra("lifephoto", photo);
					startActivity(i);
			  }	
		}
	}
	
	private void refreshLocalPhoto(String desc){
		
	 List<Photo> list = new ArrayList<Photo>(); 
			        list = AppContext.checkList; 
       if(newImageList!=null||newImageList.size()!=0){
    	       if(list!=null&&list.size()!=0){
    	    	   Iterator<Photo>it = list.iterator();
    	    	   while (it.hasNext()) {
					Photo photo = (Photo) it.next();
					String local_path= photo.imgPath;
					WLImage img = new WLImage();
					img.setM_path(local_path);
					img.setPhoto_desc(desc);
					newImageList.add(img);					
				 }    	    		
    	    	   if(adapter!=null){
						adapter.setList(newImageList);
					}else{
						adapter = new ImagesAdapter(this, newImageList, null, AppConstants.PARAM_UPLOAD_WORK,ImageLoadOptions.getGalleryOptions(),ImageLoader.getInstance());
						lifework_gv.setAdapter(adapter);
					}
    	       }
         }else{
        	 initData();
         }		        
       

	}
	
	  BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { 			   
	        @Override 
	        public void onReceive(Context context, Intent intent)
	        {
	        	String type=intent.getExtras().getString("type");
	        	String lifetype = intent.getExtras().getString("lifetype");
	        	String photo_desc = intent.getExtras().getString("desc");
	        	if(type.equals("begin")){
	        	     is_uploading=true;
	        		 upload_state.setVisibility(View.VISIBLE);						 
					if(animationDrawable.isRunning()&&isFlag){
	            		  isFlag=false;
	            		  animationDrawable.start();
	            	 }
	            }else if(type.equals("over")){
	            	     upload_state.setClickable(false);
	            	     upload_state.setEnabled(false);
	            	     LifeWorkManagerActivity.this.lifetype = lifetype;
	            	     is_uploading=false;	        		   
	        	     	 animationDrawable.stop();		        	     	 
	        	     	 upload_state.setVisibility(View.GONE);
						 refreshLocalPhoto(photo_desc);
						 AppContext.checkList.clear();						 						 
	        	}  	        	
	        } 
	    }; 
   
	@Override
   protected void onPause() {
//		imageLoader.clearMemoryCache();
//		imageLoader.clearDiscCache();
		super.onPause();
  };
	    
	    
	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver); 
		unregisterReceiver(mReceiver);
//		imageLoader.clearMemoryCache();
//		imageLoader.clearDiscCache();
		super.onDestroy();
	}
}  
