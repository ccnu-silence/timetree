package com.yey.kindergaten.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.EditActivity;
import com.yey.kindergaten.activity.ServiceCodeCardActivity;
import com.yey.kindergaten.activity.ServiceCreateKinderSuccessActivity;
import com.yey.kindergaten.activity.ServiceGroupMemberActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.GroupInfoServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.SlipButton;
import com.yey.kindergaten.widget.SlipButton.OnChangedListener;

import java.util.List;

public class GeneralGroupFragment extends FragmentBase implements OnClickListener,OnChangedListener{

	@ViewInject(R.id.serviceedit_nametv)TextView topnametv;
	@ViewInject(R.id.serviceedit_name)TextView nametv;
	@ViewInject(R.id.serviceedit_gnumtv)TextView gnumtv;	
	@ViewInject(R.id.serviceedit_addcodetv)TextView addcodetv;
	@ViewInject(R.id.serviceedit_miaoshutv)TextView miaoshutetv;
	@ViewInject(R.id.serviceedit_groupmember) TextView kmembernum;
	@ViewInject(R.id.serviceedit_barcodely) LinearLayout barcodely;
	@ViewInject(R.id.tabBtn_middlely) LinearLayout tabbtnly;
	@ViewInject(R.id.serviceedit_groupmemberly)LinearLayout lookmember;
	@ViewInject(R.id.slipButton)SlipButton slipButton;
	@ViewInject(R.id.serviceedit_addcodely)LinearLayout addcodely;
	@ViewInject(R.id.service_invitationly)LinearLayout invily;
	
	@ViewInject(R.id.arrowiv_name)ImageView arrowiv_name;
	@ViewInject(R.id.arrowiv_miaoshu)ImageView arrowiv_miaoshu;
	@ViewInject(R.id.group_miaoshu)TextView groupmiaoshu;
	GroupInfoBean groupInfoBean;
	AccountInfo accountInfo;
    String joincode = "";

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
    	accountInfo = AppServer.getInstance().getAccountInfo();
    	if (getArguments()!=null) {
    		groupInfoBean = (GroupInfoBean) getArguments().getSerializable(AppConstants.GROUPBEAN);
    	}
    	initview();
    	setonclick();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
    	View view = inflater.inflate(R.layout.service_editgeneralinfo2, null);
    	ViewUtils.inject(this, view); 
    	return view;	
    }
    
    public void initview() {
    	String text = "<font color='#111111' size='17sp'>群说明:</font><font color='#999999' size='17sp'>方便园长，老师和家长等够及时的交流分享和掌握群动态,电子文档共享,群发消息,只能通讯录,在线管理......</font>";
		groupmiaoshu.setText(Html.fromHtml(text));
    	if (groupInfoBean!=null) {
    		topnametv.setText(groupInfoBean.getGname());
    		nametv.setText(groupInfoBean.getGname());
    		gnumtv.setText("群号:" + groupInfoBean.getGnum());
    		addcodetv.setText(groupInfoBean.getJoincode());
    		miaoshutetv.setText(groupInfoBean.getDesc());
    		kmembernum.setText(groupInfoBean.getMembercount() + "人");
    	    slipButton.setSlip(true);
    		if (groupInfoBean.getJoinable() == 1) {   // 可以加
    			slipButton.setCheck(true);
    			addcodely.setVisibility(View.VISIBLE);
    		} else {
    			slipButton.setCheck(false);
    			addcodely.setVisibility(View.GONE);
    		}
    		slipButton.SetOnChangedListener(this);
    		if (accountInfo.getUid() == groupInfoBean.getCreatoruid()) {
    			tabbtnly.setVisibility(View.VISIBLE);
    			addcodely.setVisibility(View.VISIBLE);   
    		} else {
    			tabbtnly.setVisibility(View.GONE);
    			addcodely.setVisibility(View.GONE);
    			arrowiv_name.setVisibility(View.GONE);
    			arrowiv_miaoshu.setVisibility(View.GONE);
    		}
    	}
    }
    
    public void setonclick() {
    	barcodely.setOnClickListener(this);
    	lookmember.setOnClickListener(this);
    	invily.setOnClickListener(this);
    	if (accountInfo.getUid() == groupInfoBean.getCreatoruid()) {
    	    nametv.setOnClickListener(this);
    	    addcodetv.setOnClickListener(this);
    	    miaoshutetv.setOnClickListener(this);
    	}
    }

	@Override
	public void onClick(View v) {
		Intent intent;		
		switch (v.getId()) {
		case R.id.serviceedit_name:
			if (accountInfo.getUid() == groupInfoBean.getCreatoruid()) {
				return ;
			}
            intent = new Intent(getActivity(),EditActivity.class);
            intent.putExtra("text", nametv.getText().toString());
            intent.putExtra(AppConstants.TITLE, "群名字");
            intent.putExtra(AppConstants.INPUTTYPE, AppConstants.INPUTTYPE_STRING);
            startActivityForResult(intent, 0);
			break;
		case R.id.serviceedit_addcodetv:
			if (accountInfo.getUid() == groupInfoBean.getCreatoruid()) {
				return ;
            }
            intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("text", addcodetv.getText().toString());
            intent.putExtra(AppConstants.TITLE,"加入口令");
            intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
            startActivityForResult(intent, 1);
			break;
		case R.id.serviceedit_miaoshutv:
			if (accountInfo.getUid() == groupInfoBean.getCreatoruid()) {
				return ;
			}
            intent = new Intent(getActivity(),EditActivity.class);
            intent.putExtra("text", miaoshutetv.getText().toString());
            intent.putExtra(AppConstants.TITLE,"描述");
            intent.putExtra(AppConstants.INPUTTYPE, AppConstants.INPUTTYPE_STRING);
            startActivityForResult(intent, 2);
			break;
		case R.id.serviceedit_groupmemberly:	
 		    intent = new Intent(getActivity(), ServiceGroupMemberActivity.class);
 			intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
 			intent.putExtra(AppConstants.GROUPNAME, groupInfoBean.getGname());
 			startActivity(intent);
			break;
		case R.id.serviceedit_barcodely:
  			intent = new Intent(getActivity(), ServiceCodeCardActivity.class);
  			intent.putExtra(AppConstants.GROUPNAME, groupInfoBean.getGname());
		    intent.putExtra(AppConstants.GROUPNUM, groupInfoBean.getGnum()+"");
		    intent.putExtra(AppConstants.CODESTRING, "TIMES_TREE_QRCODE_2#" + groupInfoBean.getGnum());
		    startActivity(intent);
            break;
		case R.id.service_invitationly:
            intent = new Intent(getActivity(), ServiceCreateKinderSuccessActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppConstants.GROUPINFOBEAN, groupInfoBean);
            bundle.putString(AppConstants.STATE, AppConstants.SHARE);
            intent.putExtras(bundle);
            startActivity(intent);
            break;
		default:
			break;
		}
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {
            if (intent.getExtras()!=null) {
                String edittext = intent.getExtras().getString("edittext");
                nametv.setText(edittext);
                topnametv.setText(edittext);
                groupInfoBean.setGname(edittext);
            }
		} else if (requestCode == 1) {
            if (intent.getExtras()!=null) {
                String edittext = intent.getExtras().getString("edittext");
                addcodetv.setText(edittext);
                groupInfoBean.setJoincode(edittext);
            }
        } else if (requestCode == 2) {
            if (intent.getExtras()!=null) {
                String edittext=intent.getExtras().getString("edittext");
                miaoshutetv.setText(edittext);
                groupInfoBean.setDesc(edittext);
			}
        }
    }
	
	public void editGroupData() {
		GroupInfoServer.getInstance().edirtGeneralGroup(accountInfo.getUid(),groupInfoBean.getGnum(), groupInfoBean.getGname(), groupInfoBean.getJoincode(), groupInfoBean.getDesc(), groupInfoBean.getJoinable(), new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				if (code == 0) {
					ShowToast("修改成功");
					List<GroupInfoBean> list = DbHelper.QueryTData("select * from GroupInfoBean", GroupInfoBean.class);
					for (int i = 0;i < list.size(); i++) {
						if (list.get(i).getGnum() == groupInfoBean.getGnum()) {
							try {
								DbHelper.getDB(getActivity()).delete(GroupInfoBean.class, WhereBuilder.b("gnum", "=", groupInfoBean.getGnum()));
								DbHelper.getDB(getActivity()).save(groupInfoBean);
                            } catch (DbException e) {
								e.printStackTrace();
							}
						}
					}
//					Intent intent = new Intent(getActivity(),ServiceGetgroupActivity.class);
//					intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//				  	startActivity(intent);
				}else{
					ShowToast("修改失败");
				}
			}
		});
	}

	@Override
	public void OnChanged(boolean CheckState) {
		if (accountInfo.getRole()!=0) {
			return ;
		}
		if (CheckState) {
			addcodely.setVisibility(View.VISIBLE);
			groupInfoBean.setJoinable(1);
			editGroupData();
		} else {
			addcodely.setVisibility(View.GONE);
			groupInfoBean.setJoinable(0);
			editGroupData();
		}
	}

}
