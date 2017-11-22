package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.PhotoDialog;


/**
 * Created by zy on 2014/12/22.
 */
public class SelectRoleActivity extends BaseActivity implements View.OnClickListener {

    //导航栏控件
    @ViewInject(R.id.header_title)TextView title_tv;
    @ViewInject(R.id.left_btn)ImageView left_btn;
    //选择角色内部控件
    @ViewInject(R.id.id_select_role)LinearLayout select_role;
    @ViewInject(R.id.id_select_role_master_rl)RelativeLayout master_rl;
    @ViewInject(R.id.id_select_role_teacher_rl)RelativeLayout teacher_rl;
    @ViewInject(R.id.id_select_role_parent_rl)RelativeLayout parent_rl;
    @ViewInject(R.id.select_role_tip)TextView select_role_tip;

    //选择方式内部控件
    @ViewInject(R.id.id_select_type)LinearLayout select_type;
    @ViewInject(R.id.id_select_type_yourself_rl)RelativeLayout phone_type;
    @ViewInject(R.id.id_select_type_mate_rl)RelativeLayout mate_type;
    private int role;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_role_activity);
        ViewUtils.inject(this);
        type = getIntent().getStringExtra("type");
        initView();
        initClick();
    }

    private void initClick() {
        master_rl.setOnClickListener(this);
        teacher_rl.setOnClickListener(this);
        parent_rl.setOnClickListener(this);
        phone_type.setOnClickListener(this);
        mate_type.setOnClickListener(this);
    }

   private void initView(){
       left_btn.setVisibility(View.VISIBLE);
       title_tv.setVisibility(View.VISIBLE);
       if(type==null){
           title_tv.setText("注册/选择角色");
           select_role.setVisibility(View.VISIBLE);
//           select_role_tip.setVisibility(View.VISIBLE);
       }else{
           title_tv.setText("找回密码");
           select_type.setVisibility(View.VISIBLE);
           select_role_tip.setVisibility(View.GONE);
       }
       left_btn.setOnClickListener(this);
   }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_select_role_master_rl:
                role = 0;
                Intent mas_intent = new Intent(this,CreateIdActivity.class);
                mas_intent.putExtra("role",role);
                startActivity(mas_intent);
                break;
            case R.id.id_select_role_teacher_rl:
                role = 1;
                Intent tch_intent = new Intent(this,CreateIdActivity.class);
                tch_intent.putExtra("role",role);
                startActivity(tch_intent);
                break;
            case R.id.id_select_role_parent_rl:
                new PhotoDialog(SelectRoleActivity.this, "如果您是家长\n请直接联系幼儿园老师\n获取账号即可，无须另外注册", AppConstants.DIALOG_TYPE_PARENT).show();
                break;
            case R.id.id_select_type_mate_rl:
                Intent mate_intent = new Intent(this,Invite_add_Activity.class);
                mate_intent.putExtra(AppConstants.BUNDLE_INVITE,AppConstants.RESETPASSWORD);
                startActivity(mate_intent);
                break;
            case R.id.id_select_type_yourself_rl:
                Intent phone_intent = new Intent(this,ForgetPasswordActivity.class);
                phone_intent.putExtra("role",role);
                startActivity(phone_intent);
                break;

            case R.id.left_btn:
                this.finish();
                break;
        }
    }
}
