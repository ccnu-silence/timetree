package com.yey.kindergaten.activity;

import java.util.Timer;
import java.util.TimerTask;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.SelfInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.LoadingDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class BindPhoneFregment extends Fragment implements OnClickListener{
	    
	    Button bindingphonebtn;
	    Button getConfirmPwbtn;
	    EditText phoneet;
	    static EditText comfirmet;
	    AccountInfo accountInfo;
	    LoadingDialog loadingDialog;
	    String phonecode="";
	    String phone="";
	    String state;
	    Timer timer;
	    static int time =0;
	    public Handler handler=new Handler()
	    {
	    	public void handleMessage(android.os.Message msg)
	    	{
	    		time++;
	    		if(time>=60){	    
	    			getConfirmPwbtn.setClickable(true);	 
	    			getConfirmPwbtn.setText("获取验证码");
	    			getConfirmPwbtn.setBackgroundResource(R.drawable.selector_main_button);
	    			timer.cancel();
	    			time=0;
	    		}else{
	    			getConfirmPwbtn.setText("倒数"+(60-time)+"秒");	   
	    		}
	    	};
	    };
	    public static  SmsCallback smsCallback=new SmsCallback() {
			@Override
			public void smsBack(String code) {
				if(comfirmet!=null){
					time=60;
					comfirmet.setText(code);
				}
			}
		};
	    
	    public interface SmsCallback
	    {
	    	public void smsBack(String code);
	    }
	    public BindPhoneFregment()
	    {
	           super();
	    }
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			accountInfo=AppServer.getInstance().getAccountInfo();
			loadingDialog=new LoadingDialog(getActivity(), "正在处理请求");
			if(getArguments()!=null){
				state=getArguments().getString("state");
			}
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view=inflater.inflate(R.layout.bindphonefragment,container, false);	
			bindingphonebtn=(Button) view.findViewById(R.id.btn_bindphonefragment_bind);
			getConfirmPwbtn=(Button) view.findViewById(R.id.btn_bindphonefragment_yahnzheng);
			bindingphonebtn.setOnClickListener(this);
			getConfirmPwbtn.setOnClickListener(this);
			phoneet=(EditText) view.findViewById(R.id.bindphoneet);
			comfirmet=(EditText) view.findViewById(R.id.bindconfirmpwet);
			return view;
		}
		@Override
		public void onClick(View v) {
			final String phonenum=phoneet.getText().toString();
			String tillphonecode=comfirmet.getText().toString();
			switch (v.getId()) {
			case R.id.btn_bindphonefragment_bind:      //绑定
				if(phonenum.equals("")){
	    			Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
	    			return;
	    		}else if(phonenum.length()!=11){
	    			Toast.makeText(getActivity(), "请输入合法的手机号码手机号码", Toast.LENGTH_SHORT).show();
	    			return;
	    		}else if(tillphonecode.equals("")){
	    			Toast.makeText(getActivity(), "请输入验证码", Toast.LENGTH_SHORT).show();
	    			return;
	    		}else if(!tillphonecode.equals(phonecode)){
	    			Toast.makeText(getActivity(), "有错误验证码", Toast.LENGTH_SHORT).show();
	    			return;
	    		}else if(!phonenum.equals(phone)){
	    			Toast.makeText(getActivity(), "输入的手机号码有获取验证码的手机号不一致", Toast.LENGTH_SHORT).show();
	    			return;
	    		}else{
	    			AppServer.getInstance().bindPhone(accountInfo.getUid(), phonenum, "653548",new OnAppRequestListener() {						
						@Override
						public void onAppRequest(int code, String message, Object obj) {
				           if(code==0){
				        	   accountInfo.setPhone(phonenum);
				        	   AppServer.getInstance().setmAccountInfo(accountInfo);
				        	   if(state.equals(AppConstants.IDSAFE)){
				        		   Intent intent=new Intent(getActivity(),IdSafeActivityChange.class);								  
								   startActivity(intent);								  
				        	   }else if(state.equals(AppConstants.TASKMAIN)){
				        		   Intent intent=new Intent(getActivity(),ServiceTaskMainActivity.class);								  
								   startActivity(intent);
				        	   }else{
				        		  Intent intent=new Intent(getActivity(),CreateNickActivity.class);
				       			  startActivity(intent);
				        	   }
				           }else{
				        	   Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
				           }
							
						}
					});
	    		}
				
				break;

	       case R.id.btn_bindphonefragment_yahnzheng:  //获取验证码
	    		if(phonenum.equals("")){
	    			Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
	    			return;
	    		}else if(phonenum.length()!=11){
	    			Toast.makeText(getActivity(), "请输入合法的手机号码手机号码", Toast.LENGTH_SHORT).show();
	    			return;
	    		}else{	    
	    			 if(loadingDialog!=null){
	    		   		 loadingDialog.show();
	    		   	     }
	    		}
	    		break;
			default:
					break;	

			}		
		}
		class MyTask extends TimerTask {  
		    @Override  
		    public void run() {  
		    	handler.sendEmptyMessage(111);
		    }  
		  
		}  
}
