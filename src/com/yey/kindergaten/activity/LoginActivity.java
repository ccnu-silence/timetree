package com.yey.kindergaten.activity;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Msgtypes;
import com.yey.kindergaten.bean.RelationShipBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.service.ContactsService;
import com.yey.kindergaten.service.HuanxinService;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.Utils;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.CustomAutoCompleteTextView;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.PhotoDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class LoginActivity extends BaseActivity implements PhotoDialog.RelationChoosed {
    private static final String FLAG_FIRST_LOGIN = "first";
    private static final String FLAG_FIRST_LOGINSUCCESS = "first";
    @ViewInject(R.id.btn_activity_login)Button btn;
    @ViewInject(R.id.tv_activity_login_login_forget)TextView tv_forget;
    @ViewInject(R.id.tv_activity_login_login_create)TextView tv_create;
    CustomAutoCompleteTextView edt_id;
    @ViewInject(R.id.edt_activity_login_login_password)EditText  edt_pass;
    @ViewInject(R.id.login_logo)CircleImageView login_iv;
    String isFirstLook;
    AppContext appcontext = null;
    SharedPreferences settings = null;
    List<Map<String,String>> list=null; // 用来保存头像的list
    DialogTips dialog;
    private  int state;
    private String kickoutstate;
    boolean isRun, flag;
    private PhotoDialog photoDialog;
    AccountInfo info;
    private final static String TAG = "LoginActivity";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            isRun = false;
            flag = false;
            if (msg.what == 111) {
                String path = (String) msg.obj;
                ImageLoader.getInstance().displayImage(path, login_iv, ImageLoadOptions.getAppPicOptions());
//              imageLoader.init(ImageLoaderConfiguration.createDefault(LoginActivity.this));
//              imageLoader.displayImage(path, login_iv, ImageLoadOptions.getHeadOptions());
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsLog.i(TAG, "into oncreate");
        appcontext = AppContext.getInstance();
        setContentView(R.layout.activity_login_login);
        ViewUtils.inject(this);
        edt_id = (CustomAutoCompleteTextView)this.findViewById(R.id.edt_activity_login_login_id);
        settings = this.getSharedPreferences(SharedPreferencesHelper.PREF_LOGIN_FILE, Context.MODE_PRIVATE);
        edt_id.setThreshold(1);
        edt_id.setDropDownBackgroundResource(R.color.white);

        photoDialog = new PhotoDialog(LoginActivity.this);

        info = AppServer.getInstance().getAccountInfo();
        kickoutstate = getIntent().getStringExtra("message");
        if (kickoutstate!=null && kickoutstate.equals("kickout")) {
            UtilsLog.i(TAG, "kickout ,begin to loginout");
            AppServer.getInstance().loginout(AppServer.getInstance().getAccountInfo().getUid(), AppServer.getInstance().getAccountInfo().getRelationship(), new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    UtilsLog.i(TAG, "loginout complete, code is: " + code);
                }
            });
        }

        final Map<String, ?> allMap = settings.getAll();
        final List<Map<String, String>> aList = new ArrayList<Map<String,String>>();
        list = new ArrayList<Map<String, String>>();
        Set<String> keysSet = allMap.keySet();
        Iterator<String> iterator = keysSet.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();   // key
            Object value = allMap.get(key); // value

            if (value.toString().contains("||")) {
                HashMap<String, String> hm = new HashMap<String,String>();
                HashMap<String, String> pic = new HashMap<String,String>();
                String account = value.toString().substring(0, value.toString().indexOf("||"));
                String url = value.toString().substring(value.toString().lastIndexOf("||") + 2);
                hm.put("txt", account);
                pic.put(account, url);
                aList.add(hm);
                list.add(pic);
            }
        }

        thread.start();
        String[] from = {"txt"};
        int[] to = { R.id.txt};
        SimpleAdapter adapter = new SimpleAdapter(this, aList, R.layout.autocomplete_layout, from, to);
        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                String path = null;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).get(edt_id.getText().toString())!=null) {
                        path = list.get(i).get(edt_id.getText().toString());
                        // imageLoader.init(ImageLoaderConfiguration.createDefault(LoginActivity.this));
                        GlideUtils.loadPicImage(AppContext.getInstance(), path, login_iv);
//                        imageLoader.displayImage(path, login_iv, ImageLoadOptions.getAppPicOptions());
                    }
                }
                String pas_pref = allMap.get(edt_id.getText().toString().trim()).toString();
                String password_has_key = pas_pref.substring(pas_pref.indexOf("||") + 2, pas_pref.lastIndexOf("||"));
                String password = password_has_key.substring(0, password_has_key.length() - 5);
                edt_pass.setText(password);
            }
        };

        edt_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {
                String account = arg0.toString();
                String path = null;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).get(account)!=null) {
                        path = list.get(i).get(edt_id.getText().toString());
                        // imageLoader.init(ImageLoaderConfiguration.createDefault(LoginActivity.this));
                        imageLoader.displayImage(path, login_iv, ImageLoadOptions.getAppPicOptions());
                    }
                }
            }
        });

        edt_id.setOnItemClickListener(itemClickListener);
        edt_id.setAdapter(adapter);
        SharedPreferences setting = this.getSharedPreferences(AppConfig.LOGIN_DEFALUTE_VALUE, Context.MODE_PRIVATE);
        if (setting!=null && !setting.getAll().isEmpty()) {
            String password = setting.getString(AppConfig.LOGIN_DEFAULTE_PASSWORD, "");
            String account = setting.getString(AppConfig.LOGIN_DEFAULTE_ACCOUNT, "");
            String url = setting.getString(AppConfig.LOGIN_DEFAULTE_AVATER, "");
            edt_pass.setText(password.substring(0, password.length() - 5).trim());
            edt_id.setText(account);
            // imageLoader.init(ImageLoaderConfiguration.createDefault(LoginActivity.this));
            imageLoader.displayImage(url, login_iv, ImageLoadOptions.getAppPicOptions());
        }
    }

    Thread thread = new Thread(new Runnable() {
        String path = null;
        @Override
        public void run() {
            isRun = true;
            flag = true;
            while (isRun) {
                if (flag) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).get(edt_id.getText().toString())!=null) {
                            path = list.get(i).get(edt_id.getText().toString());
                        }
                        System.out.println(path + "aaaa" + list.get(i));
                    }
                    Message msg = new Message();
                    msg.what = 111;
                    msg.obj = path;
                    handler.sendMessage(msg);
                }
            }
        }
    });

    /**
     * 启动环信服务
     * @param state  判断是登陆环信还是注册环信
     * @param relation
     */
    private void startHuanxinService(String state, String relation) {
        UtilsLog.i(TAG, "into startHuanxinService");
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        Intent serviceIntent = new Intent(LoginActivity.this, HuanxinService.class);
        serviceIntent.putExtra(AppConstants.PARAM_UID, info.getUid());
        serviceIntent.putExtra(AppConstants.PARAM_PASSWORD, info.getPassword());
        serviceIntent.putExtra("state", state);
        serviceIntent.putExtra("relation", relation);
        if (state.equals(AppConstants.HUANXIN_REGEDIT)) {
            UtilsLog.i(TAG, "state is huanxin_regedit");
            serviceIntent.putExtra("regedit", "updateClient");
            if (info.getRole() == AppConstants.DIRECTORROLE) { // 当园长注册环信时，需要调用updateClientId
                serviceIntent.putExtra("action", AppConstants.HX_DIRECTOR_ACTION);
            } else if (info.getRole() == AppConstants.TEACHERROLE) { // 当老师注册环信环信
                if (info.getKid() == 0){ // 老师没有幼儿园
                    serviceIntent.putExtra("action", AppConstants.HX_TEACHER_NO_KID);
                } else { // 老师存在幼儿园
                    serviceIntent.putExtra("action", AppConstants.HX_TEACHER_HAS_KID);
                }
            }
        } else {
            UtilsLog.i(TAG, "state is not huanxin_regedit");
        }
        LoginActivity.this.startService(serviceIntent);
    }

    @OnClick({(R.id.btn_activity_login),(R.id.tv_activity_login_login_forget),(R.id.tv_activity_login_login_create)})
    public void onClick(View v){
        switch (v.getId()) {
        case R.id.btn_activity_login:
            hideSoftInputView();
            String clientId = SharedPreferencesHelper.getInstance(this).getString(AppConstants.CLIENTID, "");
            final String accout = edt_id.getText().toString();
            final String password = edt_pass.getText().toString();

            if (TextUtils.isEmpty(accout)) {
                showToast("请输入账号");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                showToast("请输入密码");
                return;
            }
            if (accout.equals("*#*#zgyey4006011063#*#*") && password.equals("*#*#zgyey4006011063#*#*")) { // log暗码
                boolean isprintlog = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getBoolean(AppConstants.PREF_ISPRINTLOG_SWITCH, false);
                SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean(AppConstants.PREF_ISPRINTLOG_SWITCH, !isprintlog);
                showToast("设置log开关：" + !isprintlog);
                return;
            }

            showLoadingDialog("正在登录...");
            AppServer.getInstance().login(this, accout, password, clientId, new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, final String message, Object obj) {
                    UtilsLog.i(TAG, "login Callback code:" + code + "");
                    System.out.println("结束登录:" + System.currentTimeMillis());

                    if (code == AppServer.REQUEST_SUCCESS) {
                        UtilsLog.i(TAG, "login success");
                        info = AppServer.getInstance().getAccountInfo();
                        String relation = "";
                        try {
                            relation = String.valueOf(info.getRelationship());
                            UtilsLog.i(TAG, "get relation is:" + relation);
                        } catch (Exception e) {
                            e.printStackTrace();
                            UtilsLog.i(TAG, "String.valueOf(info.getRelationship()) Exception");
                        }

                        if (info == null || info.getUid() == 0) {
                            UtilsLog.i(TAG, "info is null or uid is 0" + info + "");
                            cancelLoadingDialog();
                            showToast("请重新登录");
                            return;
                        }
                        appcontext = AppContext.getInstance();

                        Msgtypes msgtype = null;
                        try {
                            if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Msgtypes.class)) {
                                msgtype = DbHelper.getDB(AppContext.getInstance()).findFirst(Msgtypes.class);
                            }
                        } catch (Exception e) {
                            UtilsLog.i(TAG, e.getMessage() + e.getCause() + "");
                        }
                        if (Utils.isContactsNull(appcontext, info.getRole()) || msgtype == null || msgtype.getDesc() == null){
                            Intent it = new Intent(LoginActivity.this, ContactsService.class);
                            LoginActivity.this.startService(it);
                        }

                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString(AppConstants.PREF_VERSION, AppContext.getInstance().getVersionName());
                        // 是否设置亲属身份
                        boolean relationflag = info.getRelationship() == 0;
                        cancelLoadingDialog();
                        isFirstLook = SharedPreferencesHelper.getInstance(appcontext).getString(info.getUid() + "$", "0$" + info.getUid());
                        if (info.getRole() == 2) {
                            UtilsLog.i(TAG, "role is parent");
                            if (relationflag) {
                                UtilsLog.i(TAG, "relationflag is true , openphotodialog");
                                if (photoDialog!=null && !photoDialog.isShowing()) {
                                    photoDialog.setRelationChoosed(LoginActivity.this);
                                    photoDialog.show();
                                }
                            } else {
                                /** 启动环信登陆服务 */
                                startHuanxinService(AppConstants.HUANXIN_LOGIN, relation);
                                if (isFirstLook!=null && isFirstLook.equals(AppConstants.SHAREPAFERENCE_IS_FIRST_LOGIN+info.getUid())) {
                                    UtilsLog.i(TAG, "isfirstlook, open wizardactivity");
                                    Intent intent = new Intent(LoginActivity.this, WizardActivity.class);
                                    intent.putExtra("type","fromLogin");
                                    intent.putExtra("fromdId",AppConstants.TIMETREE_DO_PARENT);
                                    startActivity(intent);
                                } else {
                                    UtilsLog.i(TAG, "not isfirstlook, into mainactivity");
                                    Intent a = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(a);
                                    LoginActivity.this.finish();
                                }
                            }
                        } else {
                            cancelLoadingDialog();
                            try {
                                List<RelationShipBean> relationList = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
                                if (relationList!=null && relationList.size()!=0) {
                                    RelationShipBean bean = relationList.get(0);
                                    int hxFlag = bean.getHxregtag();
                                    if (hxFlag == 0) {
                                        startHuanxinService(AppConstants.HUANXIN_REGEDIT, relation);
                                    } else {
                                        startHuanxinService(AppConstants.HUANXIN_LOGIN, relation); /////////////////////////////////////////////////
                                    }
                                }
                            } catch (DbException e) {
                                UtilsLog.i(TAG, "get dbhelper RelationShipBean DbException:" + e.getMessage() + "/" + e.getCause());
                                e.printStackTrace();
                            }
                            if (isFirstLook.equals(AppConstants.SHAREPAFERENCE_IS_FIRST_LOGIN+info.getUid())) {
                                UtilsLog.i(TAG, "isfirstlook, open wizardactivity");
                                Intent intent = new Intent(LoginActivity.this, WizardActivity.class);
                                intent.putExtra("type", "fromLogin");
                                if (info.getRole() == AppConstants.TEACHERROLE) {
                                    intent.putExtra("fromdId", AppConstants.TIMETREE_DO_TEACHER);
                                } else {
                                    intent.putExtra("fromdId", AppConstants.TIMETREE_DO_DIRECTOR);
                                }
                                startActivity(intent);
                            } else {
                                UtilsLog.i(TAG, "not isfirstlook, into mainactivity");
                                Intent a = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(a);
                                LoginActivity.this.finish();
                            }
                        }
                    } else {
                        UtilsLog.i(TAG, "login fail: code" + code);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cancelLoadingDialog();
                                showToast(message);
                            }
                        });
                    }
                }
            });
            break;
        case R.id.tv_activity_login_login_forget:
            Intent b = new Intent(LoginActivity.this, SelectRoleActivity.class);
            b.putExtra("type", "forget");
            startActivity(b);
            break;
        case R.id.tv_activity_login_login_create:
            Intent c = new Intent(LoginActivity.this, SelectRoleActivity.class);
            startActivity(c);
            break;
        }
    }

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//          if ((System.currentTimeMillis() - mExitTime) > 2000) {
//              Object mHelperUtils;
//              showToast("再按一次返回桌面");
//              mExitTime = System.currentTimeMillis();
//          } else {
                Intent i= new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
//          }
//          return true;
        }
        return super.onKeyDown(keyCode, event);
    }
     
    public void onResume() {
        flag = true;
        if (kickoutstate!=null && kickoutstate.equals("kickout")) { // AppContext.quitLogout
            appcontext.logout(new EMCallBack() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialog("系统提示", "您的账号在另一台设备上登录，请重新登录。如您是账号本人，建议修改密码。", "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    kickoutstate = null;
                                }
                            });
                        }
                    });
                }

                @Override
                public void onError(int i, String s) { }

                @Override
                public void onProgress(int i, String s) { }

            });
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        flag = false;
        super.onPause();
        UtilsLog.i(TAG, "onPause");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UtilsLog.i(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilsLog.i(TAG,"onDestroy");
        isRun = false;
    }

    /**
     * 显示踢掉的dialog
     * @param title
     * @param message
     * @param buttonText
     * @param onSuccessListener
     */
    public void showDialog(String title, String message, String buttonText, DialogInterface.OnClickListener onSuccessListener) {
        if (dialog == null) {
        long mill = 500;
        Vibrator vib = (Vibrator)  AppContext.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(mill);
        dialog = new DialogTips(this, message, buttonText);}
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        dialog.setTitle(title);
        // 显示确认对话框
        // dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setIconTitle(R.drawable.btn_chat_fail_resend);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void loginhuanxin(int relation) {
        boolean isLogin = false;
        final String accout = edt_id.getText().toString();
        final String password = edt_pass.getText().toString();
        int hxflag = 0;
        String relationShip = String.valueOf(relation);
        try {
            List<RelationShipBean> listbean = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
            if (listbean!=null) {
                for (int i = 0; i < listbean.size(); i++) {
                    if (listbean.get(i).getRelationship() == relation) {
                        isLogin = true;
                        hxflag = listbean.get(i).getHxregtag();
                    }
                }
            }
            if (isLogin) { // 身份已经存在，判断hxflag 0表示未注册，1表示已注册，默认为0
                if (hxflag == 1) {
                    AppServer.getInstance().updateHxState(AppServer.getInstance().getAccountInfo().getUid(), Integer.valueOf(relationShip), hxflag, "注册成功 ", new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) { }
                    });
                    startHuanxinService(AppConstants.HUANXIN_LOGIN, relationShip);
                } else { // 判断是否注册过，有可能是0但是实际上是注册过的
                    startHuanxinService(AppConstants.HUANXIN_REGEDIT, relationShip);
                }
            } else { // 身份首次登陆
                if (hxflag == 1) {
                    startHuanxinService(AppConstants.HUANXIN_LOGIN, relationShip);
                } else { // 判断是否注册过，有可能是0但是实际上是注册过的0
                    startHuanxinService(AppConstants.HUANXIN_REGEDIT, relationShip);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            public void run() {
                // 进入主页面
                if (isFirstLook!=null && isFirstLook.contains(AppConstants.SHAREPAFERENCE_IS_FIRST_LOGIN)) {
                    Intent intent = new Intent(LoginActivity.this, WizardActivity.class);
                    intent.putExtra("type", "fromLogin");
                    intent.putExtra("fromdId", AppConstants.TIMETREE_DO_PARENT);
                    startActivity(intent);
                } else {
                    Intent a = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(a);
                    LoginActivity.this.finish();
                }
            }
        });
    }

}

