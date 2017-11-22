package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.FileUtils;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class ServiceCreateKinderSuccessActivity extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.left_btn)ImageView leftbtn;
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.creatkinderfinish_tv)TextView contenttv;
	@ViewInject(R.id.creatkinderfinish_sharely)LinearLayout sharebtn;
	GroupInfoBean groupInfoBean;
	AccountInfo accountInfo;
	String state="";
	private String sharetext="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicecreatekindersuccess);
		accountInfo=AppServer.getInstance().getAccountInfo();
		if(getIntent().getExtras()!=null){
			groupInfoBean=(GroupInfoBean) getIntent().getExtras().getSerializable(AppConstants.GROUPINFOBEAN);
			state=getIntent().getExtras().getString(AppConstants.STATE);
		}
		ViewUtils.inject(this);
		intiview();
		initdata();
	}
	
	public void initdata()
	{
		GroupInfoServer.getInstance().getShareText(accountInfo.getUid(), groupInfoBean.getGnum()+"", new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				if(code==0){
					sharetext=(String) obj;
					contenttv.setText(sharetext);
				}
				
			}
		});
	}
	
	public void intiview()
	{
		leftbtn.setVisibility(View.VISIBLE);
	    leftbtn.setOnClickListener(this);
		titletv.setVisibility(View.VISIBLE);
		titletv.setText("邀请加入群");
		sharebtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
	  switch (v.getId()) {
	  	case R.id.left_btn:
	  		if(state.equals(AppConstants.CREATESUCCESS)){
	  			Intent intent=new Intent(this, ServiceFriendsterActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt(AppConstants.GNUM, groupInfoBean.getGnum());
				bundle.putInt("gtype", groupInfoBean.getGtype());
				bundle.putString("groupname", groupInfoBean.getGname());
				intent.putExtras(bundle);
				intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				this.finish();		
	  		}else{
	  			this.finish();
	  		}
	  		break;	  		  		
	  	case R.id.creatkinderfinish_sharely:

	  	  OnekeyShare oks = new OnekeyShare();
	        //关闭sso授权
	        oks.disableSSOWhenAuthorize();
	        oks.setDialogMode();
	        // 分享时Notification的图标和文字
	       // oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
	        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	        oks.setTitle("时光树账号分享");
	        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	        oks.setTitleUrl("http://www.yey.com/dl/sgs.htm");
	        // text是分享文本，所有平台都需要这个字段
	        oks.setText(sharetext);
	        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	        oks.setImagePath(FileUtils.getSDRoot() + "yey/" + "shareapp.png");
	        // url仅在微信（包括好友和朋友圈）中使用
	        oks.setUrl("http://www.yey.com/dl/sgs.htm");
	        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
	        oks.setComment("");
	        // site是分享此内容的网站名称，仅在QQ空间使用
	        oks.setSite(getString(R.string.app_name));
	        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
	        oks.setSiteUrl("http://www.yey.com/dl/sgs.htm");

	        // 启动分享GUI
	        oks.show(this);
	  		break;

	    default:
	       	break;
	}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(state.equals(AppConstants.CREATESUCCESS)){
				Intent intent=new Intent(this, ServiceFriendsterActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt(AppConstants.GNUM, groupInfoBean.getGnum());
				bundle.putInt("gtype", groupInfoBean.getGtype());
				bundle.putString("groupname", groupInfoBean.getGname());
				intent.putExtras(bundle);
				intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				this.finish();		
	  		}else{
	  			this.finish();
	  		}	
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
