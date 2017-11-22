package com.yey.kindergaten.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.GetAddressActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.KindergartenInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ServiceCreateKinderFragment extends FragmentBase implements OnClickListener{

	TextView finishtv;                  // 完成注册按钮
	RelativeLayout createkindlocationrl;

	EditText createkindnametv;          // 幼儿园名称
	TextView createkindlocationtv;      // 所属地区
	EditText createkindcontactpeopletv; // 园长姓名

	AccountInfo accountInfo;
	String locationID;
	LoadingDialog loadingDialog;
	Handler maHandler = new Handler();
	Boolean setcodeflag = false;

    private AddressBean priAddressBeans;    // 省级的对象
    private AddressBean cityAddressBeans;   // 市级的对象
    private AddressBean areaAddressBeans;   // 区级的对象
    private AddressBean roadAddressBeans;   // 街道级的对象

    private int pId = 0;
    private int cId = 0;
    private int aId = 0;
    private int rId = 0;

    private List<KindergartenInfo> listinfo = new ArrayList<KindergartenInfo>();
    private KindergartenInfo info;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		accountInfo = AppServer.getInstance().getAccountInfo();
	    createkindlocationrl.setOnClickListener(this);
		finishtv.setOnClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.servicecreatekinderfragment, null);
		finishtv = (TextView) view.findViewById(R.id.mecreatekinder_finish);
		createkindnametv = (EditText) view.findViewById(R.id.createkind_nametv);
	    createkindlocationrl = (RelativeLayout) view.findViewById(R.id.createkind_locationrl);
	    createkindlocationtv = (TextView) view.findViewById(R.id.createkind_locationtv);
		createkindcontactpeopletv = (EditText) view.findViewById(R.id.createkind_contactpeopletv);
		return view;
	}

	@Override
	public void onClick(View v) {
		final Intent intent;
		switch (v.getId()) {
            case R.id.mecreatekinder_finish:
                final String gname = createkindlocationtv.getText().toString();
                final String kname = createkindnametv.getText().toString();
                final String pname = createkindcontactpeopletv.getText().toString();
                if (gname == null || gname.equals("")) {
                    ShowToast("请选择所属地区");
                    return;
                }
                if (kname == null || kname.equals("")) {
                    ShowToast("请填写幼儿园名称");
                    return;
                }
                if (pname == null || pname.equals("")) {
                    ShowToast("请填写您的姓名");
                    return;
                }
                pId = priAddressBeans.getID();
                if (cityAddressBeans!=null) {
                    cId = cityAddressBeans.getID();
                }
                if (areaAddressBeans!=null) {
                    aId = areaAddressBeans.getID();
                }
                if (roadAddressBeans!=null) {
                    rId = roadAddressBeans.getID();
                }
                showLoadingDialog("正在注册幼儿园...");
                AppServer.getInstance().creatKindergarten(accountInfo.getUid(), pId, cId, aId, rId, kname, pname, new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
                            listinfo = (List<KindergartenInfo>) obj;
                            if (listinfo!=null && listinfo.size()!=0) {
                               info = listinfo.get(0);
                            }
                            accountInfo.setKid(info.getKid());
                            accountInfo.setKname(kname);
                            accountInfo.setLocation(gname);
                            accountInfo.setRealname(pname);
                            accountInfo.setNum(info.getNumber());
                            try {
                                DbHelper.getDB(AppContext.getInstance()).update(accountInfo, WhereBuilder.b("uid", "==", accountInfo.getUid()), new String[]{"kid","kname","location","realname","num"});
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            AppContext.getInstance().setAccountInfo(accountInfo);
                            modifyInfo();
                        } else {
                            Toast.makeText(ServiceCreateKinderFragment.this.getActivity(), "创建失败，" + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.createkind_locationrl:
                intent = new Intent(getActivity(), GetAddressActivity.class);
                intent.putExtra("clickposition", 1);
                intent.putExtra("type","internet");
                startActivityForResult(intent, 1); // requestCode;
                break;
		}
	}

    private void modifyInfo(){
        AppServer.getInstance().modifySelfInfo(accountInfo.getUid()," ", " ", " ", " ",accountInfo.getRealname(),accountInfo.getAccount(), accountInfo.getBirthday(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == 0) {
                    postEvent(AppEvent.TEACHERFRFRAGMENT_RELOADDATA);
                    cancelLoadingDialog();
                    Intent intent = new Intent(ServiceCreateKinderFragment.this.getActivity(), MainActivity.class);
                    SharedPreferencesHelper.getInstance(ServiceCreateKinderFragment.this.getActivity()).setInt(AppConstants.PREF_ISLOGIN, 1);
                    SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt(AppConfig.KID,info.getKid());
                    ServiceCreateKinderFragment.this.startActivity(intent);
                } else {
                    Toast.makeText(ServiceCreateKinderFragment.this.getActivity(), "创建失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (intent.getExtras()!=null) {
			String edittext = intent.getExtras().getString("edittext");
			switch (requestCode) {
                case 0:
                    createkindnametv.setText(edittext);
                    break;
                case 1:
                    locationID = intent.getExtras().getString("locationID");
                    Bundle bundle = intent.getExtras();
                    priAddressBeans = (AddressBean) bundle.getSerializable("priAddressBeans");
                    cityAddressBeans = (AddressBean) bundle.getSerializable("cityAddressBeans");
                    areaAddressBeans = (AddressBean) bundle.getSerializable("areaAddressBeans");
                    roadAddressBeans = (AddressBean) bundle.getSerializable("roadAddressBeans");
                    StringBuffer buffer = new StringBuffer();
                    String location = buffer.toString();
                        if (priAddressBeans!=null) {
                            buffer.append(priAddressBeans.getTitle()).append(",");
                            location = buffer.toString();
                        }
                        if (cityAddressBeans!=null) {
                            buffer.append(cityAddressBeans.getTitle()).append(",");
                            location = buffer.toString();
                        } else {
                            location = location.substring(0,location.lastIndexOf(","));
                        }
                        if (areaAddressBeans!=null) {
                            buffer.append(areaAddressBeans.getTitle()).append(",");
                            location = buffer.toString();
                        } else {
                            location = location.substring(0,location.lastIndexOf(","));
                        }
                        if (roadAddressBeans!=null) {
                            buffer.append(roadAddressBeans.getTitle());
                        }
                        createkindlocationtv.setText(location);
                    break;
			}
		}
	}

    public void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                System.out.println("PostThreadId:" + Thread.currentThread().getId());
            }
        }).start();
    }

}
