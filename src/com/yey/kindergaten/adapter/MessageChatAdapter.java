/**
 * 
 */
package com.yey.kindergaten.adapter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.litesuits.android.log.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ChatLookPictureActivity;
import com.yey.kindergaten.activity.FriendsterShowImage;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.Chat;
import com.yey.kindergaten.db.ChatDb;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.BitmapCache;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.widget.CircleImageView;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
/**
 * @author chaowen
 *
 */
public class MessageChatAdapter extends BaseListAdapter<Chat>{
	//8种Item的类型
	//文本
	private final int TYPE_RECEIVER_TXT = 0;
	private final int TYPE_SEND_TXT = 1;
	//图片
	private final int TYPE_SEND_IMAGE = 2;
	private final int TYPE_RECEIVER_IMAGE = 3;
	//语音
	private final int TYPE_SEND_VOICE =4;
	private final int TYPE_RECEIVER_VOICE =5;

	String currentObjectId = "";

	DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	Context context;
	public MessageChatAdapter(Context context, List<Chat> list) {
		super(context, list);
		this.context = context;
		currentObjectId = AppServer.getInstance().getAccountInfo().getUid()+"";
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
	}

	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		final Chat item = list.get(position);
		if (convertView == null) {
			convertView = createViewByType(item, position);
		}
		//文本类型
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);//失败重发
		final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);//发送状态
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		final TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
		//图片
	    ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
		final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);//进度条
		//语音
		final LinearLayout ll_voice = ViewHolder.get(convertView, R.id.layout_voice);
		final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
		//语音长度
		final TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);
		
		
		
		String avatar = item.getAvatar();
		System.out.println(position+"头像:"+avatar);
		Log.i("头像", position+"头像:"+avatar);
		if(avatar!=null && !avatar.equals("")){//加载头像-为了不每次都加载头像
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getChatOptions(),animateFirstListener);
		}else{
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getChatOptions(),animateFirstListener);
		}
		
		if(getItemViewType(position)==TYPE_SEND_TXT
//				||getItemViewType(position)==TYPE_SEND_IMAGE//图片单独处理
				||getItemViewType(position)==TYPE_SEND_VOICE){//只有自己发送的消息才有重发机制
			//状态描述
			if(item.getStatus()==AppConstants.STATUS_SEND_SUCCESS){//发送成功
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if(item.getContenttype()==AppConstants.TYPE_AUDIO){
					tv_send_status.setVisibility(View.GONE);
					tv_voice_length.setVisibility(View.VISIBLE);
				}else{
					tv_send_status.setVisibility(View.GONE);
					tv_send_status.setText("已发送");
				}
			}else if(item.getStatus()==AppConstants.STATUS_SEND_FAIL){//服务器无响应或者查询失败等原因造成的发送失败，均需要重发
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if(item.getContenttype()==AppConstants.TYPE_AUDIO){
					tv_voice_length.setVisibility(View.GONE);
				}
			}/*else if(item.getStatus()==BmobConfig.STATUS_SEND_START){//开始上传
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
					tv_voice_length.setVisibility(View.GONE);
				}
			}*/
		}
		
		tv_time.setText(TimeUtil.getChatTime(item.getDate()));
		final String text = item.getContent();
		final int location = position;

		switch (item.getContenttype()) {
		   case AppConstants.TYPE_TEXT:
			   try {
					SpannableString spannableString = FaceTextUtils
							.toSpannableString(mContext, text);
					tv_message.setText(spannableString);
					
				} catch (Exception e) {
				}
			   tv_message.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View arg0) {
						CharSequence[] menu={"复制文本","删除","取消"};
						showDialogItems(menu, "", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(which==0){
									showToast("已经成功复制文本内容");
							  		if (android.os.Build.VERSION.SDK_INT > 11) {
							  		     android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
							  		     c.setText(tv_message.getText().toString());
							  		  } else {
							  		     android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
							  		     c.setText(tv_message.getText().toString());
							  		 }
								}else if(which==1){
									remove(location);
									ChatDb chatdb = new ChatDb(AppContext.getInstance());
									chatdb.DelChat(item.getPmid());
									notifyDataSetChanged();
									dialog.dismiss();
								}else{
									dialog.dismiss();
								}
								
							}
						});
						return false;
					}
				});
			   break;
		   case AppConstants.TYPE_IMAGE://图片类
				try {
					if (text != null && !text.equals("")) {//发送成功之后存储的图片类型的content和接收到的是不一样的
						dealWithImage(position, progress_load, iv_fail_resend, tv_send_status, iv_picture, item);
					}
					iv_picture.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Intent intent =new Intent(mContext,ChatLookPictureActivity.class);
							int imageposition=0;
							int size=0;
							ArrayList<String> photos = new ArrayList<String>();
							for(int i=0;i<list.size();i++){
								if(list.get(i).getContenttype()==AppConstants.TYPE_IMAGE){	
									size++;
									Chat imageitem=list.get(i);
									photos.add(imageitem.getContent());	
									if(imageitem.getContent().equals(text)){
										imageposition=size;										
									}
								}
							}
							intent.putStringArrayListExtra("imglist", photos);
							intent.putExtra("position", imageposition-1);					
							mContext.startActivity(intent);
						}
					});
					
				} catch (Exception e) {
				}
				iv_picture.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View arg0) {
		                 showDialogNoTitle("确认删除此会话", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								remove(location);
								ChatDb chatdb = new ChatDb(AppContext.getInstance());
								chatdb.DelChat(item.getPmid());
								notifyDataSetChanged();
								dialog.dismiss();
							}
						});
						return false;
					}
				});
				break;
		   case AppConstants.TYPE_AUDIO://语音消息
				try {
					if (text != null && !text.equals("")) {
						tv_voice_length.setVisibility(View.VISIBLE);
						String content = item.getContent();
						/*MediaPlayer mp = MediaPlayer.create(AppContext.getInstance(), Uri.parse(content));
						final int duration = mp.getDuration();*/
						if (String.valueOf(item.getUid()).equals(currentObjectId)) {//发送的消息
							if(item.getStatus()==AppConstants.STATUS_SEND_SUCCESS){//当发送成功或者发送已阅读的时候，则显示语音长度
								tv_voice_length.setVisibility(View.VISIBLE);
								//String length = content.split("&")[2];
								tv_voice_length.setText(""+"\''");
							}else{
								tv_voice_length.setVisibility(View.INVISIBLE);
							}
						} else {//收到的消息
							progress_load.setVisibility(View.VISIBLE);
							tv_voice_length.setVisibility(View.GONE);
							iv_voice.setVisibility(View.INVISIBLE);//只有下载完成才显示播放的按钮
						
							 String name = item.getContent();
								File dir = new File(AppConstants.VOICE_DIR + File.separator
										+ item.getUid());
								if (!dir.exists()) {
									dir.mkdirs();
								}
								// 在当前用户的目录下面存放录音文件
								File audioFile = new File(dir.getAbsolutePath() + File.separator
										+ name.substring(name.lastIndexOf("/")+1));
								
									
							//boolean isExists = BmobDownloadManager.checkTargetPathExist(currentObjectId,item);
							if(!audioFile.exists()){//若指定格式的录音文件不存在，则需要下载，因为其文件比较小，故放在此下载
								if (!audioFile.exists()) {
									audioFile.createNewFile();
								}
								String netUrl = item.getContent();
								HttpUtils http = new HttpUtils();
								 http.download(netUrl, audioFile.getAbsolutePath(), new RequestCallBack<File>() {
									
									@Override
									public void onSuccess(ResponseInfo<File> arg0) {
										progress_load.setVisibility(View.GONE);
										tv_voice_length.setVisibility(View.VISIBLE);
										tv_voice_length.setText(""+"\''");
										iv_voice.setVisibility(View.VISIBLE);
									}
									
									@Override
									public void onFailure(HttpException arg0, String arg1) {
										progress_load.setVisibility(View.GONE);
										tv_voice_length.setVisibility(View.GONE);
										iv_voice.setVisibility(View.INVISIBLE);
									}
								});
							}else{
								progress_load.setVisibility(View.GONE);
								tv_voice_length.setVisibility(View.VISIBLE);
								iv_voice.setVisibility(View.VISIBLE);
								//String length = content.split("&")[2];
								tv_voice_length.setText(""+"\''");
							}
						 
						}
					}
					//播放语音文件
					ll_voice.setOnClickListener(new NewRecordPlayClickListener(mContext,item,iv_voice));
				} catch (Exception e) {
					
				}
				ll_voice.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View arg0) {
		                 showDialogNoTitle("确认删除此会话", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								remove(location);
								ChatDb chatdb = new ChatDb(AppContext.getInstance());
								chatdb.DelChat(item.getPmid());
								notifyDataSetChanged();
								dialog.dismiss();
							}
						});
						return false;
					}
				});
				break;
		}
		return convertView;
	}
	
	
	@Override
	public int getItemViewType(int position) {
		Chat msg = list.get(position);
		if(msg.getContenttype()==AppConstants.TYPE_IMAGE){
			return String.valueOf(msg.getUid()).equals(currentObjectId) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
		}else if(msg.getContenttype()==AppConstants.TYPE_AUDIO){
			return String.valueOf(msg.getUid()).equals(currentObjectId) ? TYPE_SEND_VOICE: TYPE_RECEIVER_VOICE;
		}else{
		    return String.valueOf(msg.getUid()).equals(currentObjectId) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 6;
	}
	
	
	private View createViewByType(Chat message, int position) {
		int type = message.getContenttype();
	   if(type==AppConstants.TYPE_IMAGE){//图片类型
		   return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? 
					mInflater.inflate(R.layout.chat_item_received_image, null) 
					:
					mInflater.inflate(R.layout.chat_item_sent_image, null);
		}else if(type==AppConstants.TYPE_AUDIO){//语音类型
			return getItemViewType(position) == TYPE_RECEIVER_VOICE ? 
					mInflater.inflate(R.layout.chat_item_received_voice, null) 
					:
					mInflater.inflate(R.layout.chat_item_sent_voice, null);
		}else{//剩下默认的都是文本
			return getItemViewType(position) == TYPE_RECEIVER_TXT ? 
					mInflater.inflate(R.layout.chat_item_received_message, null) 
					:
					mInflater.inflate(R.layout.chat_item_sent_message, null);
		}
	}
	
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	
	/** 处理图片
	  * @Description: TODO
	  * @param @param position
	  * @param @param progress_load
	  * @param @param iv_fail_resend
	  * @param @param tv_send_status
	  * @param @param iv_picture
	  * @param @param item 
	  * @return void
	  * @throws
	  */
	private void dealWithImage(int position,final ProgressBar progress_load,ImageView iv_fail_resend,TextView tv_send_status,ImageView iv_picture,Chat item){
		String text = item.getContent();
		if(getItemViewType(position)==TYPE_SEND_IMAGE){//发送的消息
			if(item.getStatus()==AppConstants.STATUS_SEND_START){
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}else if(item.getStatus()==AppConstants.STATUS_SEND_SUCCESS){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				//tv_send_status.setVisibility(View.VISIBLE);
				//tv_send_status.setText("已发送");
			}else if(item.getStatus()==AppConstants.STATUS_SEND_FAIL){
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			}
//			如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
			String showUrl = "";
			if(text.contains("&")){
				showUrl = text.split("&")[0];
			}else{
				showUrl = text;
			}
			//为了方便每次都是取本地图片显示
			
			if(showUrl.contains("http")){
				System.out.println(position+","+text);
				ImageLoader.getInstance().displayImage(showUrl, iv_picture,options,new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub
						progress_load.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						progress_load.setVisibility(View.INVISIBLE);
					}
					
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						// TODO Auto-generated method stub
						progress_load.setVisibility(View.INVISIBLE);
						
					}
					
					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
						progress_load.setVisibility(View.INVISIBLE);
					}
				});
			}else{
				BitmapCache.getInstance().displayNetBmp(iv_picture, showUrl, progress_load,options);
				//ImageLoader.getInstance().displayImage("file://"+showUrl, iv_picture);
			}
			
		}else{
			BitmapCache.getInstance().displayNetBmp(iv_picture, text, progress_load,options);
			/*ImageLoader.getInstance().displayImage(text, iv_picture,options,new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub
					progress_load.setVisibility(View.INVISIBLE);
				}
			});*/
		
		}
	}
	public void showDialogItems(CharSequence[] menu, String title,DialogInterface.OnClickListener mOkOnClickListener) {
		 AlertDialog.Builder builder = new Builder(context);
		 builder.setTitle(title);
		 builder.setItems(menu, mOkOnClickListener);
		 builder.create().show();
	}
	
	public void showToast(String text) {
        View layout=LayoutInflater.from(context).inflate(R.layout.toast, null);	   
        TextView textView=(TextView)layout.findViewById(R.id.toast_tv);	     
        textView.setText(text);
        Toast toast=new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
   }

	/**
	 * 没有标题的对话框
	 * @param title
	 * @param msg
	 * @param mOkOnClickListener
	 */
	public void showDialogNoTitle(String msg,DialogInterface.OnClickListener mOkOnClickListener) {
		 AlertDialog.Builder builder = new Builder((Activity)this.context);
		 builder.setMessage(msg);
		 builder.setPositiveButton("确认",mOkOnClickListener);
		 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   dialog.dismiss();
			   }
		 });
		 builder.create().show();
	}
	
	public class DelClickListener implements View.OnLongClickListener{

		@Override
		public boolean onLongClick(View view) {
			   showDialogNoTitle("确认删除此会话", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						remove(position);
						final Chat item = list.get(position);
						ChatDb chatdb = new ChatDb(AppContext.getInstance());
						chatdb.DelChat(item.getPmid());
						notifyDataSetChanged();
						dialog.dismiss();
					}
				});
			return false;
		}
		
	}
	
	

}
