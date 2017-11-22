package com.yey.kindergaten.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.fragment.ServiceFragement;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;

import java.util.List;

public class Invite_select_Activity extends BaseActivity {
    @ViewInject(R.id.header_title)TextView tv;
    @ViewInject(R.id.left_btn)ImageView leftbtn;
    @ViewInject(R.id.rl_contactService)LinearLayout rl_contactService;
    @ViewInject(R.id.invite_platform)LinearLayout ll_invite_platform;
    @ViewInject(R.id.show_text_title_mission)TextView mission_tv;

    @ViewInject(R.id.ll_join_class)LinearLayout ll_jionclass;
    @ViewInject(R.id.ll_join_kindergaten)LinearLayout ll_joinkinder;
    @ViewInject(R.id.join_kinergaten_iv)ImageView iv_joinkinder;
    @ViewInject(R.id.join_class_iv)ImageView iv_joinclass;
    int action = 10 ;

    private AccountInfo accountInfo = null;
    private  List<Classe>classeList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_select_);
        ViewUtils.inject(this);
        accountInfo = AppServer.getInstance().getAccountInfo();
        Contacts contacts = AppContext.getInstance().getContacts();
        classeList = contacts.getClasses();

        initview();
        if(accountInfo.getRole() == 1){
              tv.setText("批量开通家长账号");
              mission_tv.setText("    轻松导入家长信息\n   让您的工作更轻松");
              ll_invite_platform.setVisibility(View.GONE);
              ll_joinkinder.setVisibility(View.VISIBLE);
              ll_jionclass.setVisibility(View.VISIBLE);
             if(accountInfo.getKid()>0){
                iv_joinkinder.setImageResource(R.drawable.puaccheck);
                if(classeList!=null&&classeList.size()>0){
                  iv_joinclass.setImageResource(R.drawable.puaccheck);
                }
             }
        }
    }

    private void initview() {
        if(getIntent().getExtras()!=null){
            action = this.getIntent().getExtras().getInt(AppConstants.INTENT_ACTION);
            if(AppConstants.PUSH_ACTION_GUIDE_TEACHER == action){
                tv.setText("开通家长账号");
                ll_invite_platform.setVisibility(View.GONE);
            }
        }
        tv.setText("开通老师账号");
        leftbtn.setVisibility(View.VISIBLE);
        int kid = AppServer.getInstance().getAccountInfo().getKid();
        if(AppServer.getInstance().getAccountInfo().getKid()<=0){
            ll_invite_platform.setVisibility(View.GONE);
        }else{
            ll_invite_platform.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.left_btn,R.id.rl_contactService, R.id.invite_platform,R.id.ll_join_class,R.id.ll_join_kindergaten})
    public void viewClick(View view){
        switch (view.getId()){
            case R.id.left_btn:
                finish();
                break;
            case R.id.rl_contactService:
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
            case R.id.invite_platform:
                AccountInfo info = AppServer.getInstance().getAccountInfo();
                if(info.getRole()==0){
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.BUNDLE_INVITE,AppConstants.INVITETEACHER);
                    openActivity(Invite_add_Activity.class,bundle);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.BUNDLE_INVITE,"");
                    openActivity(Invite_add_Activity.class,bundle);
                }
                break;
            case R.id.ll_join_class:
                if(classeList==null||classeList.size()==0){
                Intent intent = new Intent(this, JoinClassActivity.class);
                intent.putExtra("state","have_call");
                this.startActivity(intent);
                }else{
                    showToast("您已经加入班级了");
                  return;
                }
                break;

            case R.id.ll_join_kindergaten:
                if(accountInfo.getKid()==0){
                Intent intents = new Intent(this, ServiceCreateKinderActivity.class);
                intents.putExtra(AppConstants.SERVICECREATESTATE,AppConstants.CREATECLASS);
                this.startActivity(intents);}else {
                    showToast("您已经加入幼儿园了");
                    return;
                }
                break;
        }
    }

}
