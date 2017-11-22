package com.yey.kindergaten.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.FragmentAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.ServiceCreateClassFragment;
import com.yey.kindergaten.fragment.ServiceCreateGeneralGroupFragment;
import com.yey.kindergaten.fragment.ServiceCreateKinderFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;


public class ServiceCreateKinderActivity extends BaseActivity implements OnClickListener{

	@ViewInject(R.id.left_btn)ImageView leftbtn;
    @ViewInject(R.id.right_btn)ImageView right_iv;
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.viewpage)ViewPager viewPager;
	List<Fragment> fragmenlist=new ArrayList<Fragment>();
	ServiceCreateKinderFragment createkinderFragment;
	ServiceCreateClassFragment  createClassFragment;
	ServiceCreateGeneralGroupFragment  createGeneralGroupFragment;

    private String[]kinderarray ={"确定跳过注册幼儿园吗？","您还差一步就能注册幼儿园了！"};
    private String[]classarray = {"确定跳过加入幼儿园吗？","您还差一步就能加入幼儿园了！"};
	FragmentAdapter adapter;
    private AccountInfo mAccountInfo;
	String state="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mecreatekinder);
		ViewUtils.inject(this);
        mAccountInfo = AppServer.getInstance().getAccountInfo();
		if(getIntent().getExtras()!=null){
			state=getIntent().getExtras().getString(AppConstants.SERVICECREATESTATE);
		}
		initview();
		initdata();
	}
	
	public void initview()
	{
		titletv.setVisibility(View.VISIBLE);

	}
	
	public void initdata()
	{
		createkinderFragment=new ServiceCreateKinderFragment();
		createClassFragment=new ServiceCreateClassFragment();
		createGeneralGroupFragment=new ServiceCreateGeneralGroupFragment();
		if(state.equals(AppConstants.CREATEKINDER)){
			fragmenlist.add(createkinderFragment);
			titletv.setText("注册/输入幼儿园信息");
            leftbtn.setOnClickListener(this);
//            leftbtn.setVisibility(View.VISIBLE);

		}else if(state.equals(AppConstants.CREATECLASS)){
			fragmenlist.add(createClassFragment);
            leftbtn.setOnClickListener(this);
//            leftbtn.setVisibility(View.VISIBLE);

			titletv.setText("加入幼儿园");
		}else{
			fragmenlist.add(createGeneralGroupFragment);
			titletv.setText("创建普通交流群");
		}
		adapter=new FragmentAdapter(getSupportFragmentManager(), fragmenlist);
		viewPager.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.left_btn:
            if(state.equals(AppConstants.CREATEKINDER)){
                showExitDialog(kinderarray[0],kinderarray[1]);
            }else if(state.equals(AppConstants.CREATECLASS)){
                showExitDialog(classarray[0],classarray[1]);
            }
            break;
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(state.equals(AppConstants.CREATEKINDER)){
                showExitDialog(kinderarray[0],kinderarray[1]);
            }else if(state.equals(AppConstants.CREATECLASS)){
                showExitDialog(classarray[0],classarray[1]);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitDialog(String title,String message){

        showDialog(title,message,"确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(ServiceCreateKinderActivity.this, MainActivity.class);
                SharedPreferencesHelper.getInstance(ServiceCreateKinderActivity.this).setInt(AppConstants.PREF_ISLOGIN, 1);
                ServiceCreateKinderActivity.this.startActivity(intent);

//                jumpJoinRealName();
            }
        });
    }

    private void jumpJoinRealName(){
        final EditText et = new EditText(this);
        et.setMinHeight(80);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        et.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        et.setPadding(10, 5, 0, 0);
        et.setHint("请输入少于20个字符");
        et.setBackground(null);

        showDialogs("请填写您的真实姓名", et,false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(et.getText().toString()==null||et.getText().toString().length()==0){
                    showToast("请填写您的真实姓名");
                    return;
                }
                mAccountInfo.setRealname(et.getText().toString());
                DbHelper.updateAccountInfo(mAccountInfo);
                AppServer.getInstance().modifySelfInfo(mAccountInfo.getUid()," ", " ", " ", " ",mAccountInfo.getRealname(),mAccountInfo.getAccount(), mAccountInfo.getBirthday(),new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if(code==0){
                            Intent intent = new Intent(ServiceCreateKinderActivity.this, MainActivity.class);
                            SharedPreferencesHelper.getInstance(ServiceCreateKinderActivity.this).setInt(AppConstants.PREF_ISLOGIN, 1);
                            ServiceCreateKinderActivity.this.startActivity(intent);
                        }else{
                            showToast( "填写失败");
                        }
                    }
                });
            }
        });
    }




}
