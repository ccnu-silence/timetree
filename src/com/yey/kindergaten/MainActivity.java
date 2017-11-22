package com.yey.kindergaten;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yey.kindergaten.activity.ChatActivity;
import com.yey.kindergaten.activity.LoginActivity;
import com.yey.kindergaten.activity.classvideo.DataSet;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Message;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.fragment.HomeFragement;
import com.yey.kindergaten.fragment.KingderFragment;
import com.yey.kindergaten.fragment.MeFragement;
import com.yey.kindergaten.fragment.ServiceFragement;
import com.yey.kindergaten.fragment.TeacherFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.receive.HxinChatMessageReceiver;
import com.yey.kindergaten.receive.HxinCmdMessageReceiver;
import com.yey.kindergaten.receive.PushReceiver;
import com.yey.kindergaten.task.MainService;
import com.yey.kindergaten.task.Task;
import com.yey.kindergaten.task.TaskType;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.SlidingMenu;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements OnClickListener, PushReceiver.EventHandler, HxinCmdMessageReceiver.EventHandler{

    public static final String TAG = "MainActivity";
    @ViewInject(R.id.btn_home)Button btn_home;
    @ViewInject(R.id.btn_service)Button btn_service;
    @ViewInject(R.id.btn_contact)Button btn_contact;
    @ViewInject(R.id.btn_me)Button btn_me;
    @ViewInject(R.id.iv_recent_tips)ImageView iv_recent_tips;
    private Button[] mTabs;
    private HomeFragement homeFragment;
//    private NewContactFragement newcontactFragment;
    private KingderFragment kingderFragment;
//    private ContactFragement contactFragment;
    private TeacherFragment contactFragment;
    private ServiceFragement serviceFragment;
    private MeFragement meFragment;
    public static Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    private AppContext appcontext;
    private Fragment fg;
    private AccountInfo accountInfo;
    private HxinCmdMessageReceiver cmdMessageReceiver;
    public static final int NEW_MESSAGE = 0x001; // 收到消息
    public static final int SYS_MESSAGE = 0x002; // 收到消息
    private boolean isExit = false;
    private String[]relation_name = {"","爸爸","妈妈","爷爷","奶奶","外公","外婆","叔叔","阿姨"};
    // 用来判断显示那个fragment的标示符
    private String type = null;
    private HxinChatMessageReceiver msgReceiver;
    private SlidingMenu mMenu;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsLog.i(TAG, "into oncreate");
        AppContext.getInstance().finishActivitys();

        int forceUpdate = SharedPreferencesHelper.getInstance(appcontext).getInt("update", 0);
        if (forceUpdate == 1) { // 强制更新
            showUpdateDialog();
        }

        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                case 0: // has update
                UmengUpdateAgent.showUpdateDialog(AppContext.getInstance(), updateInfo);
                    break;
                case 1: // has no update
                    break;
                case 2: // none wifi
                    // showToast("没有wifi连接， 只在wifi下更新");
                    break;
                case 3: // time out
                    // showToast("超时");
                    break;
                }
            }
        });
        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
            @Override
            public void onClick(int status) {
                switch (status) {
                    case UpdateStatus.Update:
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt(AppConstants.PREF_ISLOGIN,0);
                        break;
                    case UpdateStatus.Ignore:
                        break;
                    case UpdateStatus.NotNow:
                        break;
                }
            }
        });
        MobclickAgent.updateOnlineConfig(AppContext.getInstance());
        String updateAll = MobclickAgent.getConfigParams(this, "updateAll");
        String updateByUserid = MobclickAgent.getConfigParams(this, "updateByUserid");
        String updateByKid = MobclickAgent.getConfigParams(this, "updateByKid");
        String deltaUpdate = MobclickAgent.getConfigParams(this, "DeltaUpdate");
        UmengUpdateAgent.setDeltaUpdate(Boolean.valueOf(deltaUpdate));
//      AccountBean account = AppServer.getInstance().getAccountBean();
        AccountInfo account = AppServer.getInstance().getAccountInfo();
        if (account!=null) {
            if (!updateAll.equals("-1")) {
                UmengUpdateAgent.update(this);
            } else if (!updateByKid.equals("-1")) {
                if (account.getKid()!=0 && updateByKid.contains(String.valueOf(account.getKid()))) {
                    UmengUpdateAgent.update(this);
                }
            } else if (!updateByUserid.equals("-1")) {
                if (account.getUid()!=0 && updateByUserid.contains(String.valueOf(account.getUid()))) {
                    UmengUpdateAgent.update(this);
                }
            }
        }

        PushManager.getInstance().initialize(this.getApplicationContext());
        setContentView(R.layout.activity_main2);
//      mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        appcontext = AppContext.getInstance();

        int login = SharedPreferencesHelper.getInstance(appcontext).getInt(AppConstants.PREF_ISLOGIN, 0);
        if (login == 0) {

            AppServer.getInstance().loginout(AppServer.getInstance().getAccountInfo().getUid(), AppServer.getInstance().getAccountInfo().getRelationship(), new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    UtilsLog.i(TAG, "loginout complete, code is: " + code);
                }
            });

            // 已经退出
            showToast("你的账号已在其他设备上登录,请重新登录!");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            finish();
        } else {
            accountInfo = AppServer.getInstance().getAccountInfo();
            ViewUtils.inject(this);
            initView();
            initTab();
            type = this.getIntent().getStringExtra("type");
        }

       // 注册一个接收消息的BroadcastReceiver
       msgReceiver = new HxinChatMessageReceiver(this, AppEvent.CHAT_HOMEFRAGMENT_REFRESH);
       IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
       intentFilter.setPriority(3);
       registerReceiver(msgReceiver, intentFilter);

       // 注册一个ack回执消息的BroadcastReceiver
       IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
       registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

       // 注册一个透传消息的BroadcastReceiver
       IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
       cmdMessageIntentFilter.setPriority(3);
       cmdMessageReceiver = new HxinCmdMessageReceiver();
       registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
       EMChat.getInstance().setAppInited();

       DataSet.init(this);
    }

    /***
     * 弹出系统提示框
     * @param title
     * @param message
     * @param buttonText
     * @param onSuccessListener
     */
    public void showDialog(String title, String message, String buttonText, boolean isDismiss, DialogInterface.OnClickListener onSuccessListener) {
        DialogTips dialog = new DialogTips(this,message, buttonText);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        dialog.setTitle(title);
        // 显示确认对话框
        // dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 用来旧版本强制更新操作
     */
    public void showUpdateDialog() {
        Dialog dialog = new Dialog(this, R.style.Dialog_Fullscreen);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_force_upload, null);
        dialog.setContentView(view);
        Button force = (Button) view.findViewById(R.id.force_upload_bt);
        force.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri CONTENT_URI_BROWSERS = Uri.parse("http://sgs.yey.com/");
                intent.setData(CONTENT_URI_BROWSERS);
                startActivity(intent);
                AppManager.getAppManager().finishActivity(MainActivity.this);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // 设置其他按键无法点击
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void toggleMenu(View view)
    {
        mMenu.toggle();
    }

    private void initView(){
        mTabs = new Button[4];
        mTabs[0] = btn_home;
        mTabs[1] = btn_service;
        mTabs[2] = btn_contact;
        mTabs[3] = btn_me;
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);
        if (type!=null) {
            if (type.equals(AppConfig.SWITCH_TYPE_HOME)) {
                mTabs[0].setSelected(true);
            } else if (type.equals(AppConfig.SWITCH_TYPE_SERVICE)) {
                mTabs[1].setSelected(true);
            } else if (type.equals(AppConfig.SWITCH_TYPE_SERVICE)) {
                mTabs[2].setSelected(true);
            } else if (type.equals(AppConfig.SWITCH_TYPE_SERVICE)) {
                mTabs[3].setSelected(true);
            }
        }
    }

    private void initTab(){
        homeFragment = new HomeFragement();
        serviceFragment = new ServiceFragement();
        MainService.addFragment(serviceFragment);
        MainService.newTask(new Task(TaskType.TS_SERVICE_INIT));
//        newcontactFragment = new NewContactFragement();
        kingderFragment = new KingderFragment();
//        contactFragment = new ContactFragement();
        contactFragment = new TeacherFragment();
        meFragment = new MeFragement();
        if (accountInfo!=null && accountInfo.getRole() == 2) {
//          newcontactFragment.getPuacFragment().setRefreshMessage(this);
            fragments = new Fragment[] {homeFragment, serviceFragment, kingderFragment, meFragment };
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment).
            add(R.id.fragment_container, serviceFragment).hide(serviceFragment)
            .add(R.id.fragment_container, kingderFragment).hide(kingderFragment)
            .add(R.id.fragment_container, meFragment).hide(meFragment)
            .show(homeFragment).commitAllowingStateLoss();
        } else {
//          contactFragment.getPuacFragment().setRefreshMessage(this);
            fragments = new Fragment[] {homeFragment, serviceFragment, contactFragment, meFragment };
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment).
            add(R.id.fragment_container, serviceFragment).hide(serviceFragment)
            .add(R.id.fragment_container, contactFragment).hide(contactFragment)
            .add(R.id.fragment_container, meFragment).hide(meFragment)
            .show(homeFragment).commitAllowingStateLoss();
       }
    }

    /**
     * button点击事件
     * @param view
     */
    public void onTabSelect(View view) {
        homeFragment.hidePullMenu();
        serviceFragment.hidePullMenu();

        /** 集中通讯录后，取消 */
//      if (accountInfo!=null && accountInfo.getRole() == 2) {
//          kingderFragment.hidePullMenu();
//      } else {
//          contactFragment.hidePullMenu();
//      }

        meFragment.hidePullMenu();
        switch (view.getId()) {
            case R.id.btn_home:
                iv_recent_tips.setVisibility(View.GONE);
                index = 0;
                break;
            case R.id.btn_service:
                index = 1;
                break;
            case R.id.btn_contact:
                index = 2;
                break;
            case R.id.btn_me:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    // PushReceiver个推回调
    @Override
    public void onMessage(Message message) {
        int voice = message.getVoice();
        if (1 != message.getVoice()){
            AppContext.getInstance().getMediaPlayer().start();
        }
        android.os.Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
        // 当前页面如果为会话页面，刷新此页面
        if (homeFragment != null) {
            homeFragment.refresh();
        }
        if (currentTabIndex == 0) {
            iv_recent_tips.setVisibility(View.GONE);
        } else {
            iv_recent_tips.setVisibility(View.VISIBLE);
        }
    }

    // 封装ToQuitTheApp方法
    private void ToQuitTheApp() {
        if (isExit) {
            // ACTION_MAIN with category CATEGORY_HOME 启动主屏幕
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            isExit = false;
            startActivity(intent);
            if (toast!=null) {
                toast.cancel();
            }
            AppManager.getAppManager().AppExit(appcontext);
        } else {
            isExit = true;
            toast = Toast.makeText(AppContext.getInstance(), "再按一次退出APP", Toast.LENGTH_SHORT);
            toast.show();
            mHandler.sendEmptyMessageDelayed(0, 3000); // 3秒后发送消息
        }
    }

    // 创建Handler对象，用来处理消息
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) { // 处理消息
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private long hxMediaTime = 0;
    // HxinCmdMessageReceiver环信回调
    @Override
    public void onMessage(MessageRecent message) {
        long hxMediaNowTime = System.currentTimeMillis();
        if (hxMediaNowTime - hxMediaTime > 2000) {
            AppContext.getInstance().getMediaPlayer().start();
            hxMediaTime = hxMediaNowTime;
        }
//        if (message!=null) {
//            android.os.Message handlerMsg = handler.obtainMessage(SYS_MESSAGE);
//            handlerMsg.obj = message;
//            handler.sendMessage(handlerMsg);
//        } else {
//            if (homeFragment != null) {
//                HomeFragement.getNewFlag=0;
//                homeFragment.getNewMessage();
//                homeFragment.refresh();
//            }
//        }
        // 当前页面如果为会话页面，刷新此页面
        if (homeFragment != null) {
            homeFragment.refresh();
        }
        if (currentTabIndex == 0) {
            iv_recent_tips.setVisibility(View.GONE);
        } else {
            iv_recent_tips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBind(String method, int errorCode, String content) { }

    @Override
    public void onNotify(String title, String content) { }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (!isNetConnected) { }
    }

    @Override
    protected void onResume() {
        UtilsLog.i(TAG, "into onresume");
        super.onResume();
        PushReceiver.ehList.add(this);
        HxinCmdMessageReceiver.ehList.add(this);
        if (!PushManager.getInstance().isPushTurnedOn(this.getApplicationContext())) {
            PushManager.getInstance().turnOnPush(this.getApplicationContext());
        }
        appcontext.getNotificationManager().cancel(PushReceiver.NOTIFY_ID);
        PushReceiver.mNewNum = 0;
        registerHomeKeyReceiver(this);
        accountInfo = AppServer.getInstance().getAccountInfo();
        if (mTabs == null) {
            initView();
        }
        if (fragments == null){
            initTab();
        }
    }

    @Override
    protected void onPause() {
        UtilsLog.i(TAG, "into onpause");
        unregisterHomeKeyReceiver(this);
        super.onPause();
        PushReceiver.ehList.remove(this);// 暂停就移除监听
        HxinCmdMessageReceiver.ehList.remove(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataSet.saveData();
        UtilsLog.i(TAG, "into ondestroy");
        // 注销广播接收者
        try {
            unregisterReceiver(msgReceiver);
        } catch (Exception e) {
            UtilsLog.i(TAG, "unregisterReceiver msgReceiver Exception");
        }
        try {
            unregisterReceiver(ackMessageReceiver);
        } catch (Exception e) {
            UtilsLog.i(TAG, "unregisterReceiver ackMessageReceiver Exception");
        }
        try {
            unregisterReceiver(cmdMessageReceiver);
        } catch (Exception e) {
            UtilsLog.i(TAG, "unregisterReceiver cmdMessageReceiver Exception");
        }
    }

    @Override
    public void onBackPressed() { }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.CHAT_HOMEFRAGMENT_REFRESH) {
            if (currentTabIndex == 0) {
                iv_recent_tips.setVisibility(View.GONE);
            } else {
                iv_recent_tips.setVisibility(View.VISIBLE);
            }
            if (homeFragment!=null) {
                homeFragment.refresh();
            }
        } else if (event.getType() == AppEvent.PUSH_FORCE_UPDATE) {
            showUpdateDialog();
        } else if (event.getType() == AppEvent.PUSH_REFRESH_HOMEFRAGEMENT) {
            if (currentTabIndex == 0) {
                iv_recent_tips.setVisibility(View.GONE);
                // 当前页面如果为会话页面，刷新此页面
                if (homeFragment != null) {
                    homeFragment.refresh();
                }
            } else {
                iv_recent_tips.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == NEW_MESSAGE) {
                Message message = (Message) msg.obj;
                 if (AppContext.getInstance().getAccountInfo().getUid() == message.getToid()) {
                     if (message.getAction() == AppConstants.PUSH_ACTION_FRIENDS
                             || message.getAction() == AppConstants.PUSH_ACTION_PUBLICACCOUNT
                             || message.getAction() == AppConstants.PUSH_ACTION_ADD_FRIENDS
                             || message.getAction() == AppConstants.PUSH_ACTION_AGREE_FRIENDS) {
                        /* if(CURRENT_TAG_NAME.endsWith(TAB_TAG_HOME)){
                             setCurrentTabByTag(TAB_TAG_HOME);
                             HomeActivity home = (HomeActivity) mTabHost.getCurrentView().getContext();
                               home.refreshData();
                              ll_tab_home.setVisibility(View.GONE);
                              ll_tab_home_selected.setVisibility(View.VISIBLE);
                         }*/
                     }
                 }
            }
        }
    };

    @Override
    public void onRefreshData(String title) { }

    @Override
    public void onClick(View view) { }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = true;
        // 返回键取消弹出框
        if (!HomeFragement.istop) {
            homeFragment.hidePullMenu();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (!mTabs[0].isSelected()) {
                    if (currentTabIndex != 0) {
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        trx.hide(fragments[currentTabIndex]);
                        if (!fragments[0].isAdded()) {
                            trx.add(R.id.fragment_container, fragments[0]);
                        }
                        trx.show(fragments[0]).commitAllowingStateLoss();
                    }
                    mTabs[currentTabIndex].setSelected(false);
                    //把当前tab设为选中状态
                    mTabs[0].setSelected(true);
                    currentTabIndex = 0;
                } else {
                    ToQuitTheApp();
                }
            } else {
                flag = super.onKeyDown(keyCode, event);
            }}
            return flag;
        }

//    @Override
//    public void refreshMessage() {
//        if(homeFragment!=null)
//            AppContext.getInstance().getMediaPlayer().start();
//            HomeFragement.getNewFlag=0;
//            homeFragment.getNewMessage();
//            homeFragment.refresh();
//
//          if(currentTabIndex==0){
//            iv_recent_tips.setVisibility(View.GONE);
//            //当前页面如果为会话页面，刷新此页面
//            if(homeFragment != null){
//                homeFragment.refresh();
//              }
//           }else{
//            iv_recent_tips.setVisibility(View.VISIBLE);
//          }
//    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    // 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
                    if (ChatActivity.activityInstance != null) {
                        if (msg.getChatType() == EMMessage.ChatType.Chat) {
                            if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
                                return;
                        }
                    }
                    msg.isAcked = true;
                } else {
                    UtilsLog.i(TAG, "msg is null");
                }
            } else {
                UtilsLog.i(TAG, "conversation is null");
            }
        }
    };

    public void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                UtilsLog.i(TAG, "PostThreadId:" + Thread.currentThread().getId());
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(AppConstants.PARAM_ACCOUNT,accountInfo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        accountInfo = (AccountInfo) savedInstanceState.getSerializable(AppConstants.PARAM_ACCOUNT);
    }

}
