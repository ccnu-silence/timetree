package com.yey.kindergaten.fragment;


import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.JoinClassActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.KindergartenInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.DialogTips;

import java.util.List;

import de.greenrobot.event.EventBus;


public class ServiceCreateClassFragment extends FragmentBase implements OnClickListener{

	private EditText  classnametv;//输入幼儿园编号
	private EditText  gradenametv;//输入园长手机号码
    private LinearLayout have_result;//查询到结果
    private LinearLayout no_result;//没有查询到结果
    private TextView  search_tv;//搜索按钮
    private TextView  haveresult_tv;//显示幼儿园名称
    private TextView  jumpto_tv;//跳过按钮
    private TextView  join_tv;//加入按钮

    private LinearLayout loading_ll;
    private View view=null;
	AccountInfo accountInfo;
    String gradename="";
    int gradeid;

	private  KindergartenInfo info;
    private List<KindergartenInfo>listinfo;



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 accountInfo=AppServer.getInstance().getAccountInfo();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

            initView(inflater);
            initClick();

		return view;
	}

    private void initView(LayoutInflater inflater){
        view=inflater.inflate(R.layout.servicecreateclassfragment, null);
        classnametv = (EditText) view.findViewById(R.id.createclass_classtv);
        gradenametv = (EditText) view.findViewById(R.id.createclass_gradetv);

        have_result = (LinearLayout)view.findViewById(R.id.have_result_kindergaten_ll);
        no_result   = (LinearLayout)view.findViewById(R.id.no_result_kindergaten_ll);
        loading_ll  = (LinearLayout)view.findViewById(R.id.show_loding);

        search_tv = (TextView)view.findViewById(R.id.mecreatekinder_finish);
        haveresult_tv = (TextView)view.findViewById(R.id.kingdergaten_name);
        jumpto_tv = (TextView)view.findViewById(R.id.jump_kindergaten_tv);
        join_tv = (TextView)view.findViewById(R.id.jion_kindergaten_tv);

        classnametv.setInputType(InputType.TYPE_CLASS_NUMBER);
        classnametv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        gradenametv.setInputType(InputType.TYPE_CLASS_NUMBER);
        gradenametv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
    }

    private void initClick(){
        search_tv.setOnClickListener(this);
        join_tv.setOnClickListener(this);
        jumpto_tv.setOnClickListener(this);
    }

    private void  showEmptyDialog(){
        showDialog("提示","跳过加入" ,"抱歉，没有搜到与您输入条件相匹配的幼儿园。",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intens = new Intent(ServiceCreateClassFragment.this.getActivity(), MainActivity.class);
                startActivity(intens);
            }
        });


    }

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.mecreatekinder_finish:
            String param = null;
			String code = classnametv.getText().toString();
            String phonenumber = gradenametv.getText().toString();
			if(code==null||code.equals("")){
				ShowToast("请输入幼儿园编号");
				return;
			}
            if(phonenumber==null||phonenumber.equals("")){
                  phonenumber = "";
            }
            if(code.length()>6){
                phonenumber =code;
                code ="";
            }
            closeKeyboard();
            loading_ll.setVisibility(View.VISIBLE);
            AppServer.getInstance().quaryKindergaten(code,phonenumber,new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    loading_ll.setVisibility(View.GONE);
                    if(code==0){
                        listinfo = (List<KindergartenInfo>) obj;
                        if(listinfo.size()==0){
                            showEmptyDialog();
                            return;
                        }else if(listinfo!=null&&listinfo.size()>0){
                            info = listinfo.get(0);
                             if(info.getKid()==0){
//                                 no_result.setVisibility(View.VISIBLE);
                                 showEmptyDialog();
                                 have_result.setVisibility(View.GONE);
                                 return;
                             }
                        }else{
                            info = listinfo.get(0);
                        }
                        if(info!=null){
                        accountInfo.setKid(info.getKid());
                        accountInfo.setKname(info.getKname());
                            try {
                                DbHelper.getDB(ServiceCreateClassFragment.this.getActivity()).update(accountInfo, WhereBuilder.b("uid","=",accountInfo.getUid()));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            have_result.setVisibility(View.VISIBLE);
//                            no_result.setVisibility(View.GONE);
                            haveresult_tv.setText(info.getKname());}
                        }else{
                            showEmptyDialog();
                            have_result.setVisibility(View.GONE);
                      }
                }
            });
        break;
		case R.id.jion_kindergaten_tv:
            jumpJoinRealName();
			break;
        case R.id.jump_kindergaten_tv:
            Intent intens = new Intent(ServiceCreateClassFragment.this.getActivity(), MainActivity.class);
            startActivity(intens);
            break;
		}	
	}

    /**
     * 关闭软键盘
     */
    public  void closeKeyboard( ){
        InputMethodManager imm = (InputMethodManager) this.getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
          // 得到InputMethodManager的实例
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void jumpJoinRealName(){
        final EditText et = new EditText(this.getActivity());
        et.setMinHeight(80);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        et.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        et.setPadding(10, 5, 0, 0);
        et.setHint("请输入少于20个字符");
        et.setBackground(null);
        DialogTips dialog = new DialogTips(this.getActivity(),"请输入您的真实姓名，有助于园长快速确认您的身份，顺利加入幼儿园。","", "确定",false,true);
        dialog.setView(et);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (et.getText().toString() == null || et.getText().toString().length() == 0) {
                    ShowToast("请填写您的真实姓名");
                    return;
                }
                accountInfo.setRealname(et.getText().toString());
                DbHelper.updateAccountInfo(accountInfo);
                AppServer.getInstance().modifySelfInfo(accountInfo.getUid(), " ", " ", " ", " ", accountInfo.getRealname(), accountInfo.getAccount(), accountInfo.getBirthday(), new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == 0) {
                             showLoadingDialog("正在加入...");
                            AppServer.getInstance().joinKinderGarten(accountInfo.getUid(),accountInfo.getKid(),new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {
                                    if(code == AppServer.REQUEST_SUCCESS){
                                        postEvent(AppEvent.TEACHERFRFRAGMENT_RELOADDATA);

                                        cancelLoadingDialog();
                                        Intent intent = null;
                                        intent=new Intent(getActivity(), JoinClassActivity.class);
                                        intent.putExtra("info",info);
                                        startActivity(intent);//requestCode;
                                    }else{
                                        Toast.makeText(ServiceCreateClassFragment.this.getActivity(),message,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            ShowToast("填写失败");
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void showDialogs(String title,View view,boolean flag ){

    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(intent.getExtras()!=null){
			switch (requestCode) {
			case 1:
				if(intent.getExtras()!=null){//选择地址
					gradeid=intent.getExtras().getInt(AppConstants.GRADEID);
					gradename=intent.getExtras().getString(AppConstants.GRADENAME);
					gradenametv.setText(gradename);
				 }
				break;
			}
		}
	}

    public void postEvent(final int type) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                System.out.println("PostThreadId:"
                        + Thread.currentThread().getId());

            }
        }).start();

    }
}
