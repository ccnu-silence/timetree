package com.yey.kindergaten.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

public class Invite_add_Activity  extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.left_btn)ImageView leftbtn;
    @ViewInject(R.id.header_title)TextView titletv;
    @ViewInject(R.id.creatkinderfinish_tv)TextView contenttv;
    @ViewInject(R.id.creatkinderfinish_sharely)LinearLayout sharebtn;
    @ViewInject(R.id.ll_kefu)LinearLayout ll_kefu;
    @ViewInject(R.id.btn_call_phone)Button callPhoto;
    GroupInfoBean groupInfoBean;
    AccountInfo accountInfo;
    String state="";
    private String sharetext="";
    private String invitetype = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicecreatekindersuccess);
        accountInfo= AppServer.getInstance().getAccountInfo();
        if(getIntent().getExtras()!=null){
            groupInfoBean=(GroupInfoBean) getIntent().getExtras().getSerializable(AppConstants.GROUPINFOBEAN);
            state=getIntent().getExtras().getString(AppConstants.STATE);
            invitetype = getIntent().getExtras().getString(AppConstants.BUNDLE_INVITE);
        }
        ViewUtils.inject(this);
        intiview();
        initdata();
    }

    public void initdata()
    {
        if(!invitetype.equals(AppConstants.RESETPASSWORD)) {
            showLoadingDialog("正在获取邀请分享内容");
            AppServer.getInstance().getInviteText(accountInfo.getUid(), accountInfo.getKid(), accountInfo.getRole(), new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    cancelLoadingDialog();
                    if (code == 0) {
                        sharetext = (String) obj;
                        contenttv.setText(sharetext);
                    } else {
                        sharetext = "最好用的家校沟通软件，帮助园长,老师,家长，完全免费";
                        //showToast("获取分享内容失败");
                    }
                }
            });
        }
        if(invitetype.equals(AppConstants.INVITEPARENT)){
            sharebtn.setVisibility(View.GONE);
            ll_kefu.setVisibility(View.VISIBLE);
            titletv.setText("开通家长账号");
            contenttv.setText(R.string.invite_parent);
        }else if(invitetype.equals(AppConstants.INVITETEACHER)){
            sharebtn.setVisibility(View.VISIBLE);
            ll_kefu.setVisibility(View.GONE);
            contenttv.setText(R.string.invite_teacher);
            titletv.setText("开通老师账号");
        }else if(invitetype.equals(AppConstants.RESETPASSWORD)){
            sharebtn.setVisibility(View.GONE);
            ll_kefu.setVisibility(View.VISIBLE);
            titletv.setText("联系客服");
            contenttv.setText(getString(R.string.reset_password));
        }else if(invitetype.equals("call_phone")){
            sharebtn.setVisibility(View.GONE);
            ll_kefu.setVisibility(View.VISIBLE);
            titletv.setText("联系客服");
        }

    }

    public void intiview()
    {
        leftbtn.setVisibility(View.VISIBLE);
        leftbtn.setOnClickListener(this);
        titletv.setVisibility(View.VISIBLE);
        sharebtn.setOnClickListener(this);
        callPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                if(state!=null && state.equals(AppConstants.CREATESUCCESS)){
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
                oks.setTitle("时光树分享");
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl("http://sgs.yey.com/");
                // text是分享文本，所有平台都需要这个字段
                oks.setText(sharetext);
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
              //  oks.setImageUrl("http://sgs.yey.com/qrcode.png");
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://www.yey.com/dl/sgs.htm");
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                oks.setComment("");
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://sgs.yey.com/");
                oks.setTheme(OnekeyShareTheme.CLASSIC);
                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                        if (QZone.NAME.equals(platform.getName())) {
                            //qq空间
                            paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                        }else if(Wechat.NAME.equals(platform.getName())){
                            paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                        }else if(QQ.NAME.equals(platform.getName())){
                            paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                        }else if(Email.NAME.equals(platform.getName())){
                            paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                        }
                    }
                });
                // 启动分享GUI
                oks.show(this);
                break;
            case R.id.btn_call_phone:
                showDialog("提示","确认拨打客服电话【4006011063】吗?",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent phoneIntent = new Intent(
                                "android.intent.action.CALL", Uri.parse("tel:"
                                + "4006011063"));
                        // 启动
                        startActivity(phoneIntent);
                    }
                });
                break;
            default:
                break;
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(state!=null && state.equals(AppConstants.CREATESUCCESS)){
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
