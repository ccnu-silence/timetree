package com.yey.kindergaten.receive;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.DialogTips;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zy on 2015/3/4.
 *
 */
public class HxinCmdMessageReceiver extends BroadcastReceiver{

    private static final int notifiId = 11;
    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;
    private Context context;
    public String to;
    public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

    /**表示系统消息和其他消息的区分段*/
    private final static  int messageTypeFlag = 50;
    /**表示通知特定的action*/
    private final static int NoticeMessageType = 62;
    /**刷新通讯录*/
    private final static  int RefreshPeopleType = 20;

    private  EMMessage message ;
    private  MessageRecent recent ;
    private   DialogTips dialog ;
    private AccountInfo accountInfo;
    private final static String TAG = "HxinCmdMessageReceiver";

    public static abstract interface EventHandler {
        public abstract void onMessage(MessageRecent message);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        accountInfo = AppServer.getInstance().getAccountInfo();
        abortBroadcast();
        message = intent.getParcelableExtra("message");
        try {
            int action = message.getIntAttribute("action");
            if (action == RefreshPeopleType) { // 20表示刷新通讯录
                String method = message.getStringAttribute("method") == null ? "" : message.getStringAttribute("method");
                if (URL.GETTEACHERBYKID.contains(method)) {
//                  Toast.makeText(context, "刷新老师列表" + URL.GETTEACHERBYKID, Toast.LENGTH_LONG).show();
                    /** 刷新老师列表 */
                    reFreshTeachers();
                } else if (URL.GETCLASSBYKID.contains(method)) {
//                  Toast.makeText(context,"刷新班级列表" + URL.GETCLASSBYKID, Toast.LENGTH_LONG).show();
                    /** 刷新班级列表 */
                    if (accountInfo.getRole() == AppConstants.DIRECTORROLE) { // 登班级记老师在登记班级的时候会透传刷新全部班级。
                        getClassListByKid();
                    }
                } else if (URL.GETPARENTBYKID.contains(method)) {
//                    Toast.makeText(context,"刷新老师班级列表*" + URL.GETPARENTBYKID,Toast.LENGTH_LONG).show();
                    /** 刷新老师班级列表 */
                    getParentsAndClassByKid();
                } else if (URL.GETTEACHERANDPARENTBYCID.contains(method)) {
//                  Toast.makeText(context,"刷新老师家长列表"+URL.GETTEACHERANDPARENTBYCID,Toast.LENGTH_LONG).show();
                    /** 刷新老师家长列表 */
                    getParentsAndTeachersByCid();
                } else if (URL.NEWMESSAGE.contains(method)) {
                    /** 刷新首页消息 */
                    postEvent(AppEvent.REFRESHGETNEWMESSAGE);
                } else if (URL.GETPUBLICS.contains(method)) {
                    /** 刷新公众号消息 */
                    getPublics();
                } else if (URL.GETSERVICES.contains(method)) {
                    /** 刷新服务 */
                    getServices();
                }
                return;
            }
            recent = parseEmmMessage(message,intent);
//          int action = recent.getAction();
            if (action>=messageTypeFlag) {
                recent.setUrl(AppUtils.replaceUrl(recent));
                if (action == NoticeMessageType) { // 60表示是通知，这个时候弹出系统提示框。
                    if (!AppContext.getInstance().isTopActivity()) {
                        showSystemDialog(AppUtils.replaceUrl(recent), recent.getTitle());
                    }
                }
            }
            parseMessage(context, recent, 1);
        } catch (EaseMobException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "onreceive EaseMobException");
        }
    }

    /**
     * 将环信的数据解析成本地java对象
     *
     * @param message
     * @param intent
     * @throws EaseMobException
     */
    private MessageRecent parseEmmMessage(EMMessage message, Intent intent) throws EaseMobException {
//      String msgId = intent.getStringExtra("msgid");
        String from;
        String to;
        UtilsLog.i(TAG, "getfrom and getto is :" + message.getFrom() + "|" + message.getTo());
        if (message.getFrom().length() < 2 || message.getTo().length() < 2) {
            UtilsLog.i(TAG, "getfrom and getto length less than 2");
            from = "";
            to = accountInfo.getUid() + "";
        } else {
            from = message.getFrom().substring(0, message.getFrom().length() - 2);
            to = message.getTo().substring(0, message.getTo().length() - 2);
        }
        String dateStr = message.getStringAttribute("date") == null ? "" : message.getStringAttribute("date");
        String content = message.getStringAttribute("content") == null ? "" : message.getStringAttribute("content");
        String title = message.getStringAttribute("title") == null ? "" : message.getStringAttribute("title");
        int pmid = message.getIntAttribute("pmid");
        int action = message.getIntAttribute("action");
        String url = message.getStringAttribute("url") == null ? "" : message.getStringAttribute("url"); // url replace undo
        String avatar = message.getStringAttribute("avatar") == null || message.getStringAttribute("avatar") == "" ? "http://t.ydscene.zgyey.com/Content/icon/" + action+".png" : message.getStringAttribute("avatar");
        if (avatar == null || avatar.equals("")) {
            UtilsLog.i(TAG, "avatar is null or null value");
            avatar = "http://t.ydscene.zgyey.com/Content/icon/" + action + ".png";
        }
//      String date = String.valueOf(message.getMsgTime());
//      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(date));
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//      String dateStr = sdf.format(calendar.getTime());
        MessageRecent  recentMessage = new MessageRecent();
        recentMessage.setAction(action);
        recentMessage.setUrl(url);
        recentMessage.setContent(content);
        recentMessage.setTitle(title);
        recentMessage.setToId(to);
        recentMessage.setFromId(from);
        recentMessage.setMsgid(pmid+"");
        recentMessage.setDate(dateStr);
        recentMessage.setAvatar(avatar);

        return recentMessage;
    }

    /**
     * 解析消息并组装成数据库
     *
     * @param context
     * @param mess
     */
    private void parseMessage(Context context, MessageRecent mess, int account) {
        if (mess.getAction() >= 50) {
            UtilsLog.i(TAG, "parsemessage ation > = 50");
            // 系统消息,打开url
            MessageRecent recentSystem ;
            try {
                recentSystem = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("action", "=", mess.getAction()));
                AccountInfo info = AppContext.getInstance().getAccountInfo();
                MessageRecent newRecentSystem = new MessageRecent(mess.getMsgid() + "", mess.getTitle() + "", mess.getDate(), mess.getFromId() + "", mess.getToId() + "", mess.getTitle(), mess.getContent(), mess.getUrl() + "", mess.getFileurl() + "", account, mess.getAction(), AppConstants.PUSH_CONTENT_TYPE_TEXT, mess.getAvatar(), 0, "0", info.getUid() + "a" + info.getRelationship());
                AppServer.getInstance().updateMessageStatus(mess.getMsgid() + "", info.getUid(), AppConstants.HX_PUSH_SYSTEM_MESSAGE, info.getRelationship(), new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        UtilsLog.i(TAG, "updateMessageStatus, and code is : " + code);
                    }
                });
                handleSystemMessage(context, recentSystem, newRecentSystem);

                showNotify(R.drawable.ic_launcher, recentSystem, message);

                postEvent(AppEvent.HOMEFRAGMENT_REFRESH_SYSTEMMESSAGE);
                if (ehList.size() > 0) { // 有监听的时候，传递下去
                    for (int i = 0; i < ehList.size(); i++)
                        ((EventHandler) ehList.get(i)).onMessage(mess);
                }
            } catch (DbException e) {
                  UtilsLog.i(TAG, "get Dbhelper messagerecent DbException");
                  e.printStackTrace();
              }
          }
    }

    /**
     * 当接收通知时，主界面不在时光树时，弹出提示框。
     *
     */
    public void showSystemDialog(final String finalUrl,String title){
        showDialog("您有新的通知消息", title, "去看看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                mNewNum = 0;
                final AccountInfo accountInfo = AppContext.getInstance().getAccountInfo();
                String version = AppUtils.getVersionName(AppContext.getInstance());
                AppServer.getInstance().launchLog(accountInfo.getUid(), accountInfo.getRole(), 1, version, "点击通知打开", new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) { }
                });
                Intent intent = new Intent(AppContext.getInstance(), CommonBrowser.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle noticebundle = new Bundle();
                noticebundle.putString(AppConstants.INTENT_URL, finalUrl);
                noticebundle.putString(AppConstants.INTENT_NAME, "发通知");
                intent.putExtras(noticebundle);
                clearMessageCount(context, recent);
                AppContext.getInstance().startActivity(intent);
            }
        });
    }

    /**
     * 园长刷新班级列表
     *
     * @throws JSONException
     */
    public void getClassListByKid() {
        AppServer.getInstance().getClassesByKid(accountInfo.getUid(), accountInfo.getKid(), accountInfo.getRole(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "getclasslistbykid success ");
                    List<Classe> list = (List<Classe>) obj;
                    Contacts contacts = AppContext.getInstance().getContacts();
                    if (list!=null) {
                        contacts.setClasses(list);
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Classe.class);
                            UtilsLog.i(TAG, "deleteAll or deleteAll Classe ok" );
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                            UtilsLog.i(TAG, "deleteAll or saveAll Classe ok" );
                        } catch (DbException e) {
                            e.printStackTrace();
                            UtilsLog.i(TAG, "deleteAll or saveAll fail,because DbException" );
                        }
                    }
                    AppContext.getInstance().setContacts(contacts);
                } else {
                    UtilsLog.i(TAG, "getclasslistbykid fail" );
                }
                postEvent(AppEvent.PARENTFRAGMENT_RELOADDATA);
            }
        });
    }

    /**
     * 老师刷新通讯录
     */
    private void getParentsAndClassByKid() {
        AppServer.getInstance().getParentsByTeacherKid(accountInfo.getUid(), accountInfo.getKid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "getParentsAndClassByKid success ");
                    List<Parent>list = (List<Parent>) obj;
                    if (list!=null) {
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                            UtilsLog.i(TAG, "deleteAll Parent ok" );
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                            UtilsLog.i(TAG, "saveAll  Parent ok" );
                        } catch (DbException e) {
                            e.printStackTrace();
                            UtilsLog.i(TAG, "deleteAll or saveAll fail,because DbException" );
                        }
                    }
                    postEvent(AppEvent.TEACHERFRFRAGMENT_RELOADDATA);
                } else {
                    UtilsLog.i(TAG, "getParentsAndClassByKid fail ");
                }
            }
        });
    }

    /**
     * 刷新老师列表
     */
    private void  reFreshTeachers() {
//      Log.i("account","kid------>"+accountInfo.getUid());
        AppServer.getInstance().getTeachersByKid(accountInfo.getUid(), accountInfo.getKid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "getTeachersByKid success ");
                    List<Teacher> list = (List<Teacher>) obj;
                    if (list!=null) {
                        try {
                            Contacts contacts = AppContext.getInstance().getContacts();
                            contacts.setTeachers(list);
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                            UtilsLog.i(TAG, "deleteAll Teacher ok" );
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                            UtilsLog.i(TAG, "saveAll Teacher ok" );
                        } catch (DbException e) {
                            e.printStackTrace();
                            UtilsLog.i(TAG, "deleteAll or saveAll fail,because DbException" );
                        }
                    }
                    postEvent(AppEvent.TEACHERFRFRAGMENT_RELOADDATA);
                } else {
                    UtilsLog.i(TAG, "getTeachersByKid fail ");
                }
            }
        });
    }

    /**
     * 根据cid获取家长和老师
     */
    private void getParentsAndTeachersByCid() {
        AppServer.getInstance().getTeachersAndParentsByCid(accountInfo.getUid(), accountInfo.getCid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == 0) {
                    UtilsLog.i(TAG, "getParentsAndTeachersByCid success ");
                    postEvent(AppEvent.KINDERFRAGMENT_RELOADDATA);
                }
            }
        });
    }

    /**
     * 获取公众号
     */
    private void getPublics() {
        AppServer.getInstance().getPublics(accountInfo.getUid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == 0) {
                    UtilsLog.i(TAG, "getParentsAndTeachersByCid success ");
                    postEvent(AppEvent.PUBLICFRAGMENT);
                }
            }
        });
    }

    /**
     * 获取服务列表
     */
    private void getServices() {
        AppServer.getInstance().getServiceMenu(accountInfo.getUid(), accountInfo.getRole(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                List<Services> serverslist = new ArrayList<Services>();
                if (code == 0) {
                    serverslist = (List<Services>) obj; // 接口返回
//                    List<Services> servicesList = DbHelper.getService(); // 本地数据库
                    List<Services> servicesList = new ArrayList<Services>(); // 本地数据库
                    try {
                        servicesList = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Services.class).orderBy("orderno"));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    List<Services> sameList = new ArrayList<Services>();
                    if (serverslist != null && serverslist.size() > 0) { //（** 不应取并集 **）
//                        if (serverslist.size() > servicesList.size()) { // 远程数据 比 本地数据库 多，把本地没有的加上。
//                            serverslist.removeAll(servicesList);
//                            servicesList.addAll(serverslist);
//                        } else if (servicesList.size() > serverslist.size()) { // 本地数据 比 远程数据 多，
//                            servicesList.removeAll(serverslist); // 取剩下的list，servicesList：本地数据库； serverslist：接口返回
//                            sameList.addAll(serverslist); // 保存在sameList中。
//                            servicesList = DbHelper.getService();
//                            servicesList.removeAll(sameList);
//                        }
                        List<Integer> isLookeds = new ArrayList<Integer>(); // 已看过的服务
                        if (servicesList != null && servicesList.size() > 0) {
                            for (Services service : servicesList) {
                                if (service.getIsfirstlook() == 1) { // 1:已看; 0:未看
                                    isLookeds.add(service.getType());
                                }
                            }
                            for (int i = 0; i < serverslist.size(); i++) {
                                Services newService = serverslist.get(i); // 远程
                                if (isLookeds.contains(newService.getType())) {
                                    newService.setIsfirstlook(1);
                                } else {
                                    newService.setIsfirstlook(0);
                                }
                                serverslist.set(i, newService);
                            }
                        }
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Services.class);
                            DbHelper.getDB(AppContext.getInstance()).saveAll(serverslist);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    UtilsLog.i(TAG, "getParentsAndTeachersByCid success ");
                    postEvent(AppEvent.REFRESH_SERVICES);
                }
            }
        });
    }

    /**
     * 清除气泡状态
     *
     * @param context
     * @param messageRecent
     */
    private void clearMessageCount(final Context context, MessageRecent messageRecent) {
        try {
            messageRecent.setNewcount(0);
            DbHelper.getDB(context).update(messageRecent, WhereBuilder.b("action", "=", NoticeMessageType)); // 存入最近会话
            UtilsLog.i(TAG, "clearMessageCount ok ");
        } catch (DbException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "clearMessageCount DbException ");
        }
    }

    /**
     * 存入数据库
     * @param context
     * @param messageRecent
     * @param newRecentSystem
     */
    private void handleSystemMessage(final Context context, MessageRecent messageRecent, MessageRecent newRecentSystem) {
        try {
            if (messageRecent == null) {
                DbHelper.getDB(context).save(newRecentSystem); // 存入最近会话
            } else {
                int count = messageRecent.getNewcount();
                newRecentSystem.setNewcount(count + 1);
                DbHelper.getDB(context).delete(messageRecent);
                UtilsLog.i(TAG, "delete messageRecent ok");
                DbHelper.getDB(context).save(newRecentSystem); // 存入最近会话
                UtilsLog.i(TAG, "save newRecentSystem ok ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "handleSystemMessage Exception ");
        }
    }

    /**
     * 解析完成后通知首页刷新消息
     *
     * @param type
     */
    public void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                System.out.println("PostThreadId:" + Thread.currentThread().getId());
            }
        }).start();
    }

    /***
     * 弹出系统提示框
     *
     * @param title
     * @param message
     * @param buttonText
     * @param onSuccessListener
     */
    public void showDialog(String title, String message, String buttonText, DialogInterface.OnClickListener onSuccessListener) {
        dialog = new DialogTips(AppContext.getInstance(), message, buttonText);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        dialog.setTitle(title);
        // 显示确认对话框
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    @SuppressWarnings("deprecation")
    private void showNotify(int notifyRes, MessageRecent message, EMMessage mess) {
        mNewNum++;
        // 更新通知栏
        AppContext application = AppContext.getInstance();
        if (application.isTopActivity()) {
            return;
        } else {
            int icon = notifyRes;
            String trueMsg = "";
            String title = "";
            int action = 0;
            try {
                title = mess.getStringAttribute("title") == null ? "" : mess.getStringAttribute("title");
                action = mess.getIntAttribute("action");
            } catch (EaseMobException e) {
                e.printStackTrace();
            }
            if (message!=null) {
                trueMsg = message.getTitle() == null ? "" : message.getTitle();
            } else {
                trueMsg = title;
            }

            switch (mess.getType()) {
                case TXT:
                    trueMsg = trueMsg.replaceAll( "\\[/[a-z]{4}[0-9]{2}\\]", "[表情]");
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
            CharSequence tickerText = "时光树" + ":" + trueMsg;
            if (message!=null) {
                action = message.getAction();
            }

            if (action == AppConstants.PUSH_ACTION_TASK) {
                tickerText = trueMsg;
            } else if (action == AppConstants.PUSH_ACTION_NOTICE) {
                tickerText = trueMsg;
            }
            long when = System.currentTimeMillis();

            Notification notification = new Notification(icon, tickerText, when);

            notification.flags = Notification.FLAG_AUTO_CANCEL;
            // 设置默认声音
            notification.defaults |= Notification.DEFAULT_SOUND;
            // 设定震动(需加VIBRATE权限)
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.contentView = null;

            Intent intent = new Intent(application, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(application, 0, intent, 0);

            notification.setLatestEventInfo(application, tickerText,
                    SharedPreferencesHelper.getInstance(application).getString(AppConstants.USER_NICK, "") + " (" + mNewNum + "条新消息)",
                    contentIntent);

//          if (dialog!=null)
//              dialog.dismiss();
            application.getNotificationManager().notify(NOTIFY_ID, notification); // 通知一下才会生效哦
        }
    }
}
