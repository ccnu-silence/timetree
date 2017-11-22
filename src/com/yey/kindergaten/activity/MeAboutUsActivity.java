package com.yey.kindergaten.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class MeAboutUsActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

    ServiceAdapter adapter;
    List<String> list = new ArrayList<String>();
    List<Integer> iconlist = new ArrayList<Integer>();
    @ViewInject(R.id.header_title)TextView titletv;
    @ViewInject(R.id.left_btn)ImageView leftbtn;
    @ViewInject(R.id.meaboutus_lv)ListView listView;
    @ViewInject(R.id.version_tv)TextView version_tv;
    @ViewInject(R.id.desc_tv)TextView desc_tv;
    String currentVersion = "";
    boolean firstcheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meaboutus);
        ViewUtils.inject(this);
        initview();
        initdata();
        version_tv.setText("版本 " + getVersionName());

        UmengUpdateAgent.setUpdateOnlyWifi(false);
        String updateAll = MobclickAgent.getConfigParams(this.getApplicationContext(), "updateAll");
        String updateByUserid = MobclickAgent.getConfigParams(this.getApplicationContext(), "updateByUserid");
        String updateByKid = MobclickAgent.getConfigParams(this.getApplicationContext(), "updateByKid");
        String deltaUpdate = MobclickAgent.getConfigParams(this, "DeltaUpdate");
        UmengUpdateAgent.setDeltaUpdate(Boolean.valueOf(deltaUpdate));
        AccountInfo account = AppServer.getInstance().getAccountInfo();
        if (!updateAll.equals("-1")) {
            UmengUpdateAgent.forceUpdate(MeAboutUsActivity.this);
            UmengUpdateAgent.setUpdateAutoPopup(false);
        } else if (!updateByKid.equals("-1")) {
            if (updateByKid.contains(String.valueOf(account.getKid()))) {
                UmengUpdateAgent.forceUpdate(MeAboutUsActivity.this);
                UmengUpdateAgent.setUpdateAutoPopup(false);
            }
        } else if (!updateByUserid.equals("-1")) {
            if (updateByUserid.contains(String.valueOf(account.getUid()))) {
                UmengUpdateAgent.forceUpdate(MeAboutUsActivity.this);
                UmengUpdateAgent.setUpdateAutoPopup(false);
            }
        }

        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                case 0: // has update
                    currentVersion = "发现新版本";
                    list.set(1, "检查更新" + "|" + currentVersion);
                    adapter.notifyDataSetChanged();
                    if (!firstcheck) {
                        UmengUpdateAgent.showUpdateDialog(AppContext.getInstance(), updateInfo);
                    }
                    break;
                case 1: // has no update
                    currentVersion = "已经是最新版本";
                    list.set(1, "检查更新" + "|" + currentVersion);
                    adapter.notifyDataSetChanged();
                    if (!firstcheck) {
                        showToast("已经是最新版本");
                    }
                    break;
                case 2: // none wifi
                    // showToast("没有wifi连接， 只在wifi下更新");
                    break;
                case 3: // time out
                   // showToast("超时");
                    break;
                }
                firstcheck = false;
            }
        });

        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
            @Override
            public void onClick(int status) {
                switch (status) {
                    case UpdateStatus.Update:
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt(AppConstants.PREF_ISLOGIN, 0);
                        break;
                    case UpdateStatus.Ignore:

                        break;
                    case UpdateStatus.NotNow:

                        break;
                }
            }
        });
    }

    public void initview() {
        leftbtn.setVisibility(View.VISIBLE);
        leftbtn.setOnClickListener(this);
        titletv.setVisibility(View.VISIBLE);
        titletv.setText("关于时光树");
    }

    public void initdata() {
        list.add("分享App");
        // list.add("给APP评分");
        currentVersion = "已经是最新版本";
        list.add("检查更新" + "|"+currentVersion);
        list.add("意见反馈");
        iconlist.add(R.drawable.icon_me_shareapp);
        // iconlist.add(R.drawable.icon_me_priceapp);
        iconlist.add(R.drawable.icon_me_update);
        iconlist.add(R.drawable.icon_me_suggest);
        adapter = new ServiceAdapter(MeAboutUsActivity.this, list, iconlist, AppConstants.MEMAIN_DOWN);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left_btn:
            this.finish();
            break;
        default:
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(MeAboutUsActivity.this, MeShareActivity.class);
                startActivity(intent);
                break;
            /*case 2:
                //评分
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                Intent mark = new Intent(Intent.ACTION_VIEW,uri);
                mark.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mark);
                break;*/
            case 1:
                UmengUpdateAgent.setUpdateOnlyWifi(false);
                String updateAll = MobclickAgent.getConfigParams(this.getApplicationContext(), "updateAll");
                String updateByUserid = MobclickAgent.getConfigParams(this.getApplicationContext(), "updateByUserid");
                String updateByKid = MobclickAgent.getConfigParams(this.getApplicationContext(), "updateByKid");
                String deltaUpdate = MobclickAgent.getConfigParams(this, "DeltaUpdate");
                UmengUpdateAgent.setDeltaUpdate(Boolean.valueOf(deltaUpdate));
                AccountInfo account = AppServer.getInstance().getAccountInfo();
                if (!updateAll.equals("-1")) {
                    UmengUpdateAgent.forceUpdate(MeAboutUsActivity.this);
                    UmengUpdateAgent.setUpdateAutoPopup(true);
                } else if (!updateByKid.equals("-1")) {
                    if (updateByKid.contains(String.valueOf(account.getKid()))) {
                        UmengUpdateAgent.forceUpdate(MeAboutUsActivity.this);
                        UmengUpdateAgent.setUpdateAutoPopup(true);
                    }
                } else if (!updateByUserid.equals("-1")) {
                    if (updateByUserid.contains(String.valueOf(account.getUid()))) {
                        UmengUpdateAgent.forceUpdate(MeAboutUsActivity.this);
                        UmengUpdateAgent.setUpdateAutoPopup(true);
                    }
                }
                break;
            case 2:
               intent = new Intent(MeAboutUsActivity.this, MeOpinionActivity.class);
               startActivity(intent);
               break;
            default:
                break;
        }
    }

    public String getVersionName() {
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            // 获取packagemanager的实例
               PackageManager packageManager = getPackageManager();
               packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (packInfo!=null) {
            version = packInfo.versionName;
        }
        return version;
    }

}
