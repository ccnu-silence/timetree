/**
 * 时光树
 * com.yey.kindergaten.activity
 * SplashActivity.java
 * 
 * 2014年7月10日-下午7:12:02
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.huanxin.DemoHXSDKHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.task.MainService;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;

/**
 * 欢迎界面
 * SplashActivity
 * chaowen
 * 511644784@qq.com
 * 2014年7月10日 下午7:12:02
 * @version 1.0.0
 * 
 */
public class SplashActivity extends BaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN =200;
    AppContext appContext;
    String appVersion = "0";
    private static final int sleepTime = 2000;
    ImageView iv_image;
    private final static String TAG = "SplashActivity";
    private int[] welcome = new int[] { R.drawable.welcome_1, R.drawable.welcome_2, R.drawable.welcome_3 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "into onCreate");
        MobclickAgent.updateOnlineConfig(AppContext.getInstance());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        iv_image = (ImageView)this.findViewById(R.id.iv_splash);
        // PushManager.getInstance().initialize(this.getApplicationContext());
        int splash = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getInt(AppConstants.PREF_SPLASH, 0);
        String lasttime = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getString(AppConstants.PREF_LASTTIME, TimeUtil.getCurrentTimeYMD());
        String currenttime = TimeUtil.getCurrentTimeYMD();

        appContext = AppContext.getInstance();
        if (currenttime!=null && !currenttime.equals(lasttime)) {

            if (splash>=2) {
                splash = 0;
            } else {
                splash++;
            }
            Drawable imagebakground = getResources().getDrawable(welcome[splash]);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
                iv_image.setBackground(imagebakground);
            } else {
                iv_image.setBackgroundDrawable(imagebakground);
            }
            SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt(AppConstants.PREF_SPLASH, splash);
            SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString(AppConstants.PREF_LASTTIME, TimeUtil.getCurrentTimeYMD());
            UtilsLog.i(TAG, "save welcome image index and time:" + splash + TimeUtil.getCurrentTimeYMD());
        } else {
            SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString(AppConstants.PREF_LASTTIME, TimeUtil.getCurrentTimeYMD());
            UtilsLog.i(TAG, "currenttime.equals(lasttime)");
        }

        try {
            String pkName = this.getPackageName();
            String versionName = this.getPackageManager().getPackageInfo(pkName, 0).versionName;
            int versionCode = this.getPackageManager().getPackageInfo(pkName, 0).versionCode;
            UtilsLog.i(TAG, "~~~~~~~~~~~!!!!!!时光树versionName: "+ versionName + "versionCode: " + versionCode);
        } catch (Exception e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "get versionName and versionCode Exception");
        }

        // 启动系统服务
        if (!MainService.isrun) {
            Intent it = new Intent(this, MainService.class);
            this.startService(it);
            UtilsLog.i(TAG, "the isrun of mainservice  is false, start mainservice");
        }
        /* AppServer.getInstance().login(this, "", "", "1111111111", new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) { }
        });*/
    }
     
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GO_HOME:
                openActivity(com.yey.kindergaten.MainActivity.class);
                UtilsLog.i(TAG, "handleMessage open mainactivity");
                finish();
                break;
            case GO_LOGIN:
                openActivity(LoginActivity.class);
                // finish();
                break;
            }
        }
    };

    /**
     * 更新服务器环信状态
     *
     * @param account
     * @param relationShip
     * @param note
     */
    private void updateRemoteHuanxinState(int account,int relationShip,String note){
        UtilsLog.i("updateHx", "prepare to updateHxState");
        com.yey.kindergaten.net.AppServer.getInstance().updateHxState(account, relationShip, 1, "注册成功", new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    Log.i(TAG, "updateHx success");
                } else {
                    Log.i(TAG, "updateHx fail");
                }
            }
        });
    }

    /**
     * 更新设备号，通知服务端是否发送透传。
     */
    private void upDateClientId(final int account, final int relationShip) {
        Log.i("updateHx", "更新设备号ID.....................");
        String clientid = SharedPreferencesHelper.getInstance(this).getString(AppConstants.CLIENTID, "");
        com.yey.kindergaten.net.AppServer.getInstance().updateDeviceId(com.yey.kindergaten.net.AppServer.getInstance().getAccountInfo().getUid(), clientid, relationShip, 0, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == com.yey.kindergaten.net.AppServer.REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "updateDeviceId success");
                    updateRemoteHuanxinState(account, relationShip, "注册成功");
                } else {
                    UtilsLog.i(TAG, "updateDeviceId fail");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        UtilsLog.i(TAG, "into onStart");
        AppServer.getInstance().getMainGateWay(new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                UtilsLog.i(TAG, "getMainGateWay and code is : " + code);
                int islogin = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getInt(AppConstants.PREF_ISLOGIN, 0);

                final AccountInfo accountInfo = appContext.getAccountInfo();
                UtilsLog.i(TAG, "getMainGateWay islogin / accountinfo is : " + islogin + " / " + accountInfo);
                if (accountInfo.getUid() == 0 && islogin == 0) {
                    UtilsLog.i(TAG, "uid is 0 and islogin is 0, delayed 2s and start LoginActivity");
                    SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean(AppConstants.FLAG_FIRST_LOGINSUCCESS, true);
                    mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
                } else {
//                  String version = AppContext.getInstance().getVersionName();
//                  String localversion = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getString(AppConstants.PREF_VERSION, "0");
//                  if (!version.equals(localversion)) {
//                      mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
//                  } else{
                    UtilsLog.i(TAG, "uid and islogin is not 0");
                    new Thread(new Runnable() {
                        public void run() {
                            if (DemoHXSDKHelper.getInstance().isLogined() && !DbHelper.flag) {
                                UtilsLog.i(TAG, "--------免登陆--------");
                                // ** 免登陆情况 加载所有本地群和会话
                                // 不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                                // 加上的话保证进了主页面会话和群组都已经load完毕
                                long start = System.currentTimeMillis();
                                // EMGroupManager.getInstance().loadAllGroups();
                                try {
                                    EMChatManager.getInstance().loadAllConversations();
                                } catch (Exception e) {
                                    UtilsLog.i(TAG, "------环信异常: " + e.getMessage());
                                    mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
                                    return;
                                }
                                long costTime = System.currentTimeMillis() - start;
                                // 等待sleeptime时长
                                if (sleepTime - costTime > 0) {
                                    UtilsLog.i(TAG,"sleepTime more than costTime");
                                    try {
                                        Thread.sleep(sleepTime - costTime);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        UtilsLog.i(TAG,"InterruptedException");
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AccountInfo info = AppServer.getInstance().getAccountInfo();
                                        if (info.getUid()!=0) {
                                            UtilsLog.i(TAG,"uid is not 0, updateclientid relationship");
                                            upDateClientId(info.getUid(),info.getRelationship());
                                        }
                                        AppServer.getInstance().updateHxState(info.getUid(), info.getRelationship(), 1, "更新成功", new OnAppRequestListener() {
                                            @Override
                                            public void onAppRequest(int code, String message, Object obj) { }
                                        });
                                        String version = AppUtils.getVersionName(appContext);
                                        UtilsLog.i(TAG, "start to launchLog get versionName: " + version);
                                        AppServer.getInstance().launchLog(info.getUid(),info.getRole(), 1, version, "首页打开", new OnAppRequestListener() {
                                            @Override
                                            public void onAppRequest(int code, String message, Object obj) {
                                                UtilsLog.i(TAG,"lauchLog complete code is : " + code);
                                            }
                                        });
                                    }
                                });
                                UtilsLog.i(TAG, "ready into Mainactivity");
                                // 进入主页面
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                UtilsLog.i(TAG,"DemoHXSDKHelper is not isLogined, sleep 2s to loginactivity");
                                try {
                                    Thread.sleep(sleepTime);
                                } catch (InterruptedException e) {
                                    UtilsLog.i(TAG,"InterruptedException");
                                }
                                UtilsLog.i(TAG,"start to loginacitivity");
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }).start();
                }
            }
//          }
        });
    }

}
