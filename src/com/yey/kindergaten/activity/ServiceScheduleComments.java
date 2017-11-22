package com.yey.kindergaten.activity;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.DialogTips;

public class ServiceScheduleComments extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView  iv_left;
	@ViewInject(R.id.right_tv)TextView   tv_right;
    @ViewInject(R.id.show_remind_text)TextView  remind_tv;
	private  EditText  contenttv;
    private String type;
    private int toid;
    private String name;
    private  int role;
    String comm;
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.service_write_schedule_commets);
	     ViewUtils.inject(this);

          comm=this.getIntent().getStringExtra("comm");
          type = this.getIntent().getStringExtra("type");
          toid = this.getIntent().getIntExtra("to",0);
          name = this.getIntent().getStringExtra("name");
          role = this.getIntent().getIntExtra("role",0);

          prepareView();
		
	  }
	private void prepareView() {
        contenttv=(EditText) findViewById(R.id.id_service_schedule_comments_et);
        contenttv.setPadding(6, 8, 5, 2);
        contenttv.setFocusable(true);
        contenttv.setFocusableInTouchMode(true);
        if(comm!=null){
            contenttv.setText(comm);
            contenttv.setSelection(comm.length());
        }
        if(type!=null){
            tv_headerTitle.setText("发消息");
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText("发送");
            String rolename = null;
            if(AppConstants.DIRECTORROLE==role){
                rolename = "园长";
            }else if(role == AppConstants.TEACHERROLE){
                rolename = "老师";
            }else {
                rolename = "家长";
            }
            remind_tv.setText(" *  该"+rolename+"暂未使用时光树，消息将以平台免费短信的方式发到对方手机上。");
            InputFilter[] filters = {new InputFilter.LengthFilter(60)};
            contenttv.setFilters(filters);
            contenttv.setHint("请输入消息内容，字数不超过60个字。");
        }else{
            tv_headerTitle.setText("设置备忘录");
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText("确定");
            remind_tv.setVisibility(View.GONE);
        }
       	iv_left.setVisibility(View.VISIBLE);
       	iv_left.setOnClickListener(this);
       	iv_right.setVisibility(View.GONE);
       	tv_right.setOnClickListener(this);

	    InputMethodManager inputManager =
	                    (InputMethodManager)contenttv.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.showSoftInput(contenttv,InputMethodManager.SHOW_FORCED); 
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_tv:
            final String content=contenttv.getText().toString();
            if(type==null){
			if(this.contenttv.getText().toString().trim()!=null){
				Intent intent =new Intent(ServiceScheduleComments.this,ServiceScheduleWriteActivity.class);
				intent.putExtra("comments",contenttv.getText().toString().trim());

				setResult(RESULT_OK,intent);
				this.finish();		
			}else{
				Toast.makeText(this, "请输入内容在保存", Toast.LENGTH_LONG).show();
			}
            }else{
                if(this.contenttv.getText().toString().trim()!=null){
                    if(contenttv.getText().toString().trim().length()==0){
                        showToast("您输入的内容不能为空");
                        return;
                    }

                    String allcontent = content+"  【"+name+"】";
                    showLoadingDialog("正在发送...");
                    AppServer.getInstance().sendSmsMessage(AppContext.getInstance().getAccountInfo().getUid(),toid,allcontent,new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            cancelLoadingDialog();
                            if(code == AppServer.REQUEST_SUCCESS){
                                contenttv.setText("");
                                showToast("发送成功，消息将以短信形式发送对方账号绑定的手机。");
                            }else if(code == -1){
                                showDialog("系统提示","对方暂未在时光树填写正确的手机号，无法发送短信。","确定",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                            }else{
                                showToast(message);
                            }

                        }
                    });
                }else{
                    showToast("请输入消息内容在发送");
                }
            }
			break;
		case R.id.left_btn:
			this.finish();
			break;
		}
		
	}


    public void showDialog(String title,String message,String buttonText,DialogInterface.OnClickListener onSuccessListener) {

        DialogTips dialog = new DialogTips(this,message, buttonText);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        dialog.setTitle(title);

        dialog.setIconTitle(R.drawable.btn_chat_fail_resend);
        dialog.show();
        dialog = null;
    }
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		}
		public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		}

}
