
package com.yey.kindergaten;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EasyUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.activity.LeaveSchoolActivity;
import com.yey.kindergaten.huanxin.util.CommonUtils;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.receive.HomeWatcherReceiver;
import com.yey.kindergaten.receive.PushReceiver;
import com.yey.kindergaten.task.MainService;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.LoadingDialog;

import de.greenrobot.event.EventBus;

/**
 * BaseActivity
 * 
 * chaowen
 * 511644784@qq.com
 * 2014年5月12日 下午5:15:26
 * @version 1.0.0
 */
public class BaseActivity extends FragmentActivity {
    private static HomeWatcherReceiver mHomeKeyReceiver = null;
    private View view;
    // 是否允许销毁
    private boolean allowDestroy = true;
    /** 加载框的文字说明. */
    private String mProgressMessage = "请稍候...";
    /** 全局的加载框对象，已经完成初始化. */
    public ProgressDialog mProgressDialog;

    private View mainActionBarView;
    public ImageView iv_back;
    public Button btnSave;
    public ImageLoader imageLoader = null;
    public LoadingDialog loadingdialog;
    private static final int notifiId = 11;
    protected NotificationManager notificationManager;
    public Boolean isrunning;
    private boolean isDisplay = false; // 是否在前台显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewUtils.inject(this); // 注入view和事件
        AppManager.getAppManager().addActivity(this);
        MainService.addActivity(this);

        if (AppServer.getInstance().getAccountInfo().getRole() == AppConstants.TEACHERROLE) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this); // 7- 27 龙衡东修改 BaseActivity已注册
            }
        }

        imageLoader = ImageLoader.getInstance();
        // imageLoader.clearDiskCache();
        // imageLoader.getMemoryCache().clear();
        isrunning = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isrunning = false;
        System.gc();
        // imageLoader.clearDiskCache();
        // imageLoader.clearMemoryCache();

        // 结束Activity&从堆栈中移除
        // AppManager.getAppManager().finishActivity(this);
    }

    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle!=null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle!=null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && view!=null) {
            view.onKeyDown(keyCode, event);
            if (!allowDestroy) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setAllowDestroy(boolean allowDestroy) {
        this.allowDestroy = allowDestroy;
    }

    public void setAllowDestroy(boolean allowDestroy, View view) {
        this.allowDestroy = allowDestroy;
        this.view = view;
    }

    /** 网络判断 */
    public Boolean isnetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info!=null && info.isAvailable()) {
           return true;
        } else {
            return false;
        }
    }

    /**
     * 描述：对话框dialog （无按钮）.
     * @param title 对话框标题内容
     * @param view  对话框提示内容
     */
    public AlertDialog showDialog(String title, View view) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setView(view);
        builder.create();
        AlertDialog mAlertDialog  = builder.create();
        mAlertDialog.show();
        return mAlertDialog;
    }

    /**
     * 描述：Toast提示文本.
     * @param text 文本
     */
    public void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = LayoutInflater.from(this).inflate(R.layout.toast, null);
        TextView textView = (TextView)layout.findViewById(R.id.toast_tv);
        textView.setText(text);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    /**
     * 描述：对话框dialog （确认，取消）.
     * @param title 对话框标题内容
     * @param msg  对话框提示内容
     * @param mOkOnClickListener  点击确认按钮的事件监听
     */
    public void showDialog(String title, String msg, DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("确认", mOkOnClickListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 没有标题的对话框
     * @param msg
     * @param mOkOnClickListener
     */
    public void showDialogNoTitle(String msg, DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("确认", mOkOnClickListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDialog(String title, EditText et, DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setView(et);
        builder.setPositiveButton("确认", mOkOnClickListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDialog(String title, View view, DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setView(view);
        builder.setPositiveButton("确认", mOkOnClickListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDialog(String title, String[] item, DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(item, 0, mOkOnClickListener);
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    public void showDialog(String title, String[] item, DialogInterface.OnClickListener mOkOnClickListener, int selectPosition) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(item, selectPosition, mOkOnClickListener);
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    public void showDialogItems(CharSequence[] menu, String title, DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setItems(menu, mOkOnClickListener);
        builder.create().show();
    }

    public void showClassDialog(CharSequence[] menu, String title, DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this, R.style.alert);
        builder.setItems(menu, mOkOnClickListener);
        builder.create().show();
    }

    public void showDialogs(String title, View view, DialogInterface.OnClickListener mOkOnClickListener) {
        DialogTips dialog = new DialogTips(this, title, "", "确定", true, true);
        dialog.setView(view);
        dialog.SetOnSuccessListener(mOkOnClickListener);
        dialog.show();
    }

    public void showDialogs(String title, View view, boolean hasNegtive, DialogInterface.OnClickListener mOkOnClickListener) {
        DialogTips dialog = new DialogTips(this, title,"", "确定", false, true);
        dialog.setView(view);
        dialog.SetOnSuccessListener(mOkOnClickListener);
        dialog.show();
    }

    public void showDialog(String title, String message, String buttonText, DialogInterface.OnClickListener onSuccessListener) {
        DialogTips dialog = new DialogTips(this, title, message, buttonText, true, true);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    public void showDialog(String title, String message, String buttonText, DialogInterface.OnClickListener onSuccessListener,DialogInterface.OnClickListener  OnCancelListener) {
        DialogTips dialog = new DialogTips(this, title, message, buttonText, true, true);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        dialog.SetOnCancelListener(OnCancelListener);
        dialog.setCancel(false);
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    public void showDialog(String title, DialogInterface.OnClickListener mOkOnClickListener, DialogInterface.OnClickListener ImOkOnClickListener) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setPositiveButton("确认", mOkOnClickListener);
        builder.setNegativeButton("取消", ImOkOnClickListener);
        builder.create().show();
    }

    /**
     * 显示加载对话框
     * showLoadingDialog
     * void
     */
    public void showLoadingDialog(String text) {
        if (loadingdialog!=null) {
            loadingdialog.setText(text);
            if (!loadingdialog.isShowing()) {
                loadingdialog.show();
            }
        } else {
            loadingdialog = new LoadingDialog(this, text);
            loadingdialog.show();
        }
    }

    /**
     * 取消加载对话框
     * showLoadingDialog
     * @since 1.0.0
     */
    public void cancelLoadingDialog(){
        if (loadingdialog!=null && loadingdialog.isShowing()) {
            loadingdialog.dismiss();
        }
    }

    /**
     * 隐藏软键盘
     * hideSoftInputView
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void hideSoftInputViewV2() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    int clickCount = 0;
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
        // imageLoader.clearDiskCache();
        // imageLoader.getMemoryCache();
        // imageLoader.clearMemoryCache();
        AppContext application = AppContext.getInstance();
        if (!application.isTopActivity()) {
            MainService.isrun = false;
            Intent it = new Intent(this, MainService.class);
            this.stopService(it);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDisplay = true;
        if (!MainService.isrun) {
            Intent it = new Intent(this, MainService.class);
            this.startService(it);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isDisplay = false;
    }

    public void registerHomeKeyReceiver(Context context) {
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    public void unregisterHomeKeyReceiver(Context context) {
        if (null!=mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

    /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     * @param message
     */
    protected void notifyNewMessage(EMMessage message) {
        // 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        // 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if (!EasyUtils.isAppRunningForeground(this)) {
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);

        String ticker = CommonUtils.getMessageDigest(message, this);
        if (message.getType() == EMMessage.Type.TXT)
            ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
        // 设置状态栏提示
        mBuilder.setTicker(message.getFrom() + ": " + ticker);

        // 必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify(notifiId, notification);
        notificationManager.cancel(notifiId);
    }

    protected void showNotify(int notifyRes, EMMessage message) {
        PushReceiver.mNewNum++;
        // 更新通知栏
        AppContext application = AppContext.getInstance();
        if (application.isTopActivity()) {
            return;
        } else {
            AppContext.getInstance().getMediaPlayer().start();
            long mill = 500;
            Vibrator vib = (Vibrator) application.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(mill);
            int icon = notifyRes;
            String trueMsg = "";
            TextMessageBody txtBody = (TextMessageBody)message.getBody();
            trueMsg = txtBody.getMessage();
            switch (message.getType()) {
                case TXT:
                    if (trueMsg.contains("face")) {
                        trueMsg = "[表情]";
                    }
                    break;
                case IMAGE:
                    trueMsg = "[图片]";
                    break;
                case VOICE:
                    trueMsg = "[语音]";
                    break;
                case LOCATION:
                    trueMsg = "[位置]";
                    break;
            }

            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, trueMsg, when);

            notification.flags = Notification.FLAG_AUTO_CANCEL;
            // 设置默认声音
            notification.defaults |= Notification.DEFAULT_SOUND;
            // 设定震动(需加VIBRATE权限)
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.contentView = null;

            Intent intent = new Intent(application, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(application, 0, intent, 0);
            notification.setLatestEventInfo(application,
                    SharedPreferencesHelper.getInstance(application).getString(AppConstants.USER_NICK, "") + " (" + PushReceiver.mNewNum + "条新消息)",
                    trueMsg, contentIntent);
            application.getNotificationManager().notify(notifiId, notification); // 通知一下才会生效哦
        }
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.CHAT_SCHOOL_NOTIFYDIALOG) {
            showNotifyDialog();
//          timer.schedule(task, 0, 3000);
        }
    }

    public void showNotifyDialog() {
        if (isDisplay) {
            Toast.makeText(AppContext.getInstance(), "小朋友来了", Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this)
                .setTitle("通知")
                .setMessage("是否进入离园播报页面？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openActivity(LeaveSchoolActivity.class);
//                      Intent intent = new Intent(BaseActivity.this, LeaveSchoolActivity.class);
//                      AppContext.getInstance().startActivity(intent);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
        }
    }

}
