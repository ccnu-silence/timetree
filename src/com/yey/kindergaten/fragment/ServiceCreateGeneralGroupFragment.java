package com.yey.kindergaten.fragment;

import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.EditActivity;
import com.yey.kindergaten.activity.ServiceCreateKinderSuccessActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.LoadingDialog;
import com.yey.kindergaten.widget.SlipButton;
import com.yey.kindergaten.widget.SlipButton.OnChangedListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceCreateGeneralGroupFragment extends FragmentBase implements OnClickListener,OnChangedListener{

	RelativeLayout   groupmiaoshuly;
	EditText  grouptv;
	EditText  codetv;
	TextView  miaoshutv;
	TextView finishtv;
	AccountInfo accountInfo;
	LoadingDialog loadingDialog;
	SlipButton   slipbtn;
    LinearLayout codely;
    Boolean setcodeflag=false;
  	String joincode="";
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		accountInfo=AppServer.getInstance().getAccountInfo();
		  groupmiaoshuly.setOnClickListener(this);
		  finishtv.setOnClickListener(this);
		 slipbtn.setSlip(true);
		 slipbtn.setCheck(false);
		 slipbtn.SetOnChangedListener(this);
		 codely.setVisibility(View.GONE);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		  View view=inflater.inflate(R.layout.servicecreategeneralgroupfg, null);
		  finishtv=(TextView) view.findViewById(R.id.mecreatekinder_finish);
		  groupmiaoshuly=(RelativeLayout) view.findViewById(R.id.generalgroup_miaoshuly);
		  grouptv=(EditText) view.findViewById(R.id.generalgroup_groupnametv);
		  codetv=(EditText) view.findViewById(R.id.generalgroup_codetv);
		  miaoshutv=(TextView) view.findViewById(R.id.generalgroup_miaoshutv);
		  slipbtn=(SlipButton) view.findViewById(R.id.slipButton);
		  codely=(LinearLayout) view.findViewById(R.id.createkind_codely);
			
		  return view;
	}

	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.mecreatekinder_finish:
			final String gname=grouptv.getText().toString();
		    joincode=codetv.getText().toString();
			final String desc=miaoshutv.getText().toString();
			if(gname==null||gname.equals("")){
				ShowToast("请输入群名字");
				return;
			}			
			loadingDialog = new LoadingDialog(getActivity(), "正在创建");
			loadingDialog.show();
			GroupInfoServer.getInstance().createGeneralGroup(accountInfo.getUid(), gname, joincode, desc, new OnAppRequestListener() {
				@Override
				public void onAppRequest(int code, String message, Object obj) {
					if(code==0){
						 String gnum=(String) obj;
						 GroupInfoBean groupInfoBean=new GroupInfoBean();									        	
			        	 groupInfoBean.setGnum(Integer.parseInt(gnum));
			        	 groupInfoBean.setGname(gname);
			        	 groupInfoBean.setJoincode(joincode);
			        	 groupInfoBean.setDesc(desc);			        	
			        	 groupInfoBean.setGtype(3);
			        	 try {
							DbHelper.getDB(getActivity()).save(groupInfoBean);
						} catch (DbException e) {						
							e.printStackTrace();
						}
			        	 ShowToast("创建成功");
			        	 Intent  intent=new Intent(getActivity(),ServiceCreateKinderSuccessActivity.class);
			        	 Bundle bundle=new Bundle();
			        	 bundle.putSerializable(AppConstants.GROUPINFOBEAN, groupInfoBean);
			        	 bundle.putString(AppConstants.STATE, AppConstants.CREATESUCCESS);
			        	 intent.putExtras(bundle);
			        	 startActivity(intent);
					}else{
						ShowToast("创建失败");						
					}
					if(loadingDialog!=null){
						loadingDialog.dismiss();
					}
				}
			});
			break;
		case R.id.generalgroup_miaoshuly:
			intent=new Intent(getActivity(),EditActivity.class);
			intent.putExtra("clickposition", 2);
			if(miaoshutv.getText()==null){
				intent.putExtra("text", "");
			}else{
				intent.putExtra("text", miaoshutv.getText());
			}
			intent.putExtra(AppConstants.INPUTTYPE,AppConstants.DESC);
			intent.putExtra(AppConstants.TITLE,"群介绍");
			startActivityForResult(intent, 2);//requestCode;
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(intent.getExtras()!=null){
			String edittext=intent.getExtras().getString("edittext");
			switch (requestCode) {
			case 0:
				grouptv.setText(edittext);
				break;
			case 1:
				codetv.setText(edittext);
				break;
			case 2:
				miaoshutv.setText(edittext);
				break;
			default:
				break;
			}
		}
	}
	@Override
	public void OnChanged(boolean CheckState) {
		if(CheckState){
			codely.setVisibility(View.VISIBLE);
			setcodeflag=true;
		}else{
			codely.setVisibility(View.GONE);
			setcodeflag=false;
		}
		
	}
}
