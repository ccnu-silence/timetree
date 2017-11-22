package com.yey.kindergaten.receive;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Chat;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.Message;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Msgtypes;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.db.ChatDb;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PushReceiver extends BroadcastReceiver {

    public static final String TAG = PushReceiver.class.getSimpleName();
    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;
    public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

    public static abstract interface EventHandler {
        public abstract void onMessage(Message message);
        public abstract void onBind(String method, int errorCode, String content);
        public abstract void onNotify(String title, String content);
        public abstract void onNetChange(boolean isNetConnected);
        public abstract void onRefreshData(String title);
        // public void onNewFriend(User u);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);
                    parseMessage(context, data);
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                SharedPreferencesHelper.getInstance(context).setString(AppConstants.CLIENTID, cid);
                String clientid = SharedPreferencesHelper.getInstance(context).getString(AppConstants.CLIENTID, "");
                if (clientid!=null && clientid.length()!=0 && !cid.equals(clientid)) {
                    int relationship = AppServer.getInstance().getAccountInfo().getRelationship();
                    SharedPreferencesHelper.getInstance(context).setString(AppConstants.CLIENTID, cid);
                    if (AppServer.getInstance().getAccountInfo()!=null && AppServer.getInstance().getAccountInfo().getUid()!=0){
                        AppServer.getInstance().updateDeviceId(AppServer.getInstance().getAccountInfo().getUid(), cid, relationship, 0, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) { }
                        });
                    }
                }
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                break;
            default:
                break;
        }
    }

    private void parseMessage(final Context context, String json) {
        int code = 0;
        String message = "当前网络不可用，请检查你的网络设置。";
        String resultContent = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            code = Integer.valueOf(jsonObject.getString(AppServer.TAG_CODE));
            message = jsonObject.getString(AppServer.TAG_INFO);
            resultContent = jsonObject.getString(AppServer.TAG_RESULT);
            Gson gson = new Gson();
            final Message mess = gson.fromJson(resultContent, Message.class);
            final int action = mess.getAction();
            
            int login = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getInt(AppConstants.PREF_ISLOGIN, 0);
            final AccountInfo info2 = AppContext.getInstance().getAccountInfo();
            int uid = AppContext.getInstance().getAccountInfo().getUid();
            int uid2 = mess.getToid();
            if (AppContext.getInstance().getAccountInfo().getUid() == mess.getToid() && login == 1) {
                if (action == AppConstants.PUSH_ACTION_FRIENDS) {
                    AppServer.getInstance().updateMessageStatus(mess.getPmid() + "", info2.getUid(), AppConstants.PUSH_PMTYPE_FRIEND, info2.getRelationship(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) { }
                    });
                    return;
//                  MessageDb mdb = new MessageDb();
//                  AppServer.getInstance().findUser(info2.getUid(), mess.getUid()+"", 1, new OnAppRequestListener() {
//
//                        @Override
//                        public void onAppRequest(int code, String message, Object obj) {
//                               if(code == AppServer.REQUEST_SUCCESS){
//                                   List<Friend> flist = (List<Friend>) obj;
//                                   if(flist.size()>0){
//                                       Friend tofriend = flist.get(0);
//                                       handleChat(context, mess,tofriend);
//                                       showNotify(R.drawable.ic_launcher,mess,action);
//
//                                        postEvent(AppEvent.HOMEFRAGMENT_REFRESH_CHAT);
//                                   }
//                               }
//                        }
//                    });
                } else if (action == AppConstants.PUSH_ACTION_PUBLICACCOUNT) {
//                  之前的代码，存在异步的问题，推送完都是空的。
//                  final MessageRecent messagePublic = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId","=",mess.getPublicid()).and(WhereBuilder.b("typeid", "=", mess.getTypeid())));
                    PublicAccount tagetPublicAccount = DbHelper.getDB(context).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", mess.getPublicid()));
                    if (tagetPublicAccount == null) { // 这个时候公众号也是空的，获取公众号线程还没执行完，导致查询messagePublic也是空。
                        UtilsLog.i(TAG,"parseMessage tagetPublicAccount is null");
                        AppServer.getInstance().findUser(info2.getUid(), mess.getPublicid() + "", 3, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == AppServer.REQUEST_SUCCESS) {
                                    List<PublicAccount> flist = (List<PublicAccount>) obj;
                                    if (flist.size() > 0) {
                                        PublicAccount toPublicAccount = flist.get(0);
                                        UtilsLog.i(TAG,"parseMessage findUser is success");
                                        handlePublicAccount(context, mess, toPublicAccount);
                                    }
                                }
                            }
                        });
                    } else {
                        handlePublicAccount(context, mess, tagetPublicAccount);
                        AppServer.getInstance().updateMessageStatus(mess.getPmid() + "", info2.getUid(), AppConstants.GT_PUSH_PUBLIC_MESSAGE,info2.getRelationship(), new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) { }
                        });
                    }
                } else if (action == AppConstants.PUSH_ACTION_ADD_FRIENDS) {
                    // Toast.makeText(AppContext.getInstance(), "收到好友请求", Toast.LENGTH_SHORT).show();
                    // 收到好友请求
                    final MessageRecent messagePublic = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", mess.getUid()).where("action", "=", action));
                    final Friend friend = DbHelper.getDB(context).findFirst(Friend.class, WhereBuilder.b("uid", "=", mess.getUid()));
                    AccountInfo info = AppContext.getInstance().getAccountInfo();
                    if (friend == null) {
                        AppServer.getInstance().findUser(info.getUid(), mess.getUid() + "", 1, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == AppServer.REQUEST_SUCCESS) {
                                    List<Friend> friendlist = (List<Friend>) obj;
                                    Friend newfriend = friendlist.get(0);
                                    handleAddFriend(context, mess, messagePublic, newfriend);
                                }
                            }
                        });
                    } else {
                        handleAddFriend(context, mess, messagePublic, friend);
                    }
                    if (ehList.size() > 0) { // 有监听的时候，传递下去
                        for (int i = 0; i < ehList.size(); i++)
                            ((EventHandler) ehList.get(i)).onMessage(mess);
                    }

                    showNotify(R.drawable.ic_launcher, mess, AppConstants.PUSH_ACTION_ADD_FRIENDS);
                    AppServer.getInstance().updateMessageStatus(mess.getPmid() + "", info2.getUid(), AppConstants.PUSH_PMTYPE_FRIEND, info.getRelationship(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) { }
                    });
                } else if (action == AppConstants.PUSH_ACTION_AGREE_FRIENDS) {
                    // Toast.makeText(AppContext.getInstance(), "收到对方同意请求" + "fromid=" + mess.getUid() + ",toid=" + mess.getToid(), Toast.LENGTH_SHORT).show();
                    // 收到对方同意请求
                    final MessageRecent messagePublic = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", mess.getUid()));
                    final Friend friend = DbHelper.getDB(context).findFirst(Friend.class, WhereBuilder.b("uid", "=", mess.getUid()));
                    AccountInfo info = AppContext.getInstance().getAccountInfo();
                    if (friend == null) {
                        AppServer.getInstance().findUser(info.getUid(), mess.getUid() + "", 1, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == AppServer.REQUEST_SUCCESS) {
                                    List<Friend> friendlist = (List<Friend>) obj;
                                    Friend newfriend = friendlist.get(0);
                                    handleAgreeFriend(context, mess, messagePublic, friend, newfriend);
                                    // 保存好友到通讯录
                                }
                            }
                        });
                    } else {
                         handleAgreeFriend(context, mess, messagePublic, friend, friend);
                    }
                    showNotify(R.drawable.ic_launcher, mess, AppConstants.PUSH_ACTION_AGREE_FRIENDS);
                    AppServer.getInstance().updateMessageStatus(mess.getPmid() + "", info2.getUid(), AppConstants.PUSH_PMTYPE_FRIEND, info.getRelationship(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) { }
                    });
                } else if (action == AppConstants.PUSH_ACTION_DEL_FRIENDS) {
                    Toast.makeText(AppContext.getInstance(), "删除好友" + "fromid=" + mess.getUid() + ",toid=" + mess.getToid(), Toast.LENGTH_SHORT).show();
                    DbHelper.getDB(context).delete(Friend.class, WhereBuilder.b("uid", "=", mess.getUid()));
                    showNotify(R.drawable.ic_launcher, mess, AppConstants.PUSH_ACTION_DEL_FRIENDS);
                } else if (action == AppConstants.PUSH_ACTION_QUIT) {
//                  AppContext.getInstance().quitLogout();
//                  AppServer.getInstance().loginout(AppServer.getInstance().getAccountInfo().getUid(), new OnAppRequestListener() {
//                      @Override
//                      public void onAppRequest(int code, String message, Object obj) { }
//                  });
                } else if (action>=AppConstants.PUSH_ACTION_SYSTEM_MESSAGE) { // 系统消息,打开url (不只是99)
//                  final MessageRecent recentSystem = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", mess.getPmid()));
                    final MessageRecent recentSystem = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("action", "=", mess.getAction()));
                    AccountInfo info = AppContext.getInstance().getAccountInfo();
                    if (info.getUid()!=0 && mess.getToid()!=info.getUid()) {
                        return;
                    }
                    // typeid 为 0 ，Hxfromid 为 0, name取标题。这里不做通配符的替换，统一在打开的时候替换通配符
                    MessageRecent newRecentSystem  = new MessageRecent(mess.getPmid() + "", mess.getTitle() + "", mess.getDate(), mess.getPmid() + "", mess.getToid() + "",
                            mess.getTitle(), mess.getContent(), mess.getUrl() + "", mess.getFileurl() + "", 1, mess.getAction(), AppConstants.PUSH_CONTENT_TYPE_TEXT, mess.getAvatar() == null ? "" : mess.getAvatar(), 0, "0", info.getUid() + "a" + info.getRelationship());

                    handleSystemMessage(context, mess, recentSystem, newRecentSystem);
                    showNotify(R.drawable.ic_launcher, mess, AppConstants.PUSH_ACTION_SYSTEM);
                    postEvent(AppEvent.HOMEFRAGMENT_REFRESH_SYSTEMMESSAGE);
                    if (ehList.size() > 0) { // 有监听的时候，传递下去
                        for (int i = 0; i < ehList.size(); i++)
                            ((EventHandler) ehList.get(i)).onMessage(mess);
                    }

                    AppServer.getInstance().updateMessageStatus(mess.getPmid() + "", info2.getUid(), AppConstants.PUSH_ACTION_SYSTEM, info2.getRelationship(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) { }
                    });
                    return;
                } else if(action == AppConstants.PUSH_ACTION_FORCE_UPDATE) { // 强制更新app
                    postEvent(AppEvent.PUSH_FORCE_UPDATE);
                    return;
                } else if (action == AppConstants.PUSH_ACTION_GUIDE_MASTER || action == AppConstants.PUSH_ACTION_GUIDE_TEACHER){
                    return;
                      // 新手指导园长
//                    final MessageRecent messageRecent = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId","=",mess.getUid()));
//                    AccountInfo info = AppContext.getInstance().getAccountInfo();
//                    MessageRecent newRecentSystem  = new MessageRecent(mess.getPmid()+"", "新手指导", mess.getDate(), mess.getPmid()+"", mess.getToid()+"", mess.getTitle()==null?"":mess.getTitle(), mess.getContent(),mess.getData()+"", "", 1, mess.getAction(), AppConstants.PUSH_CONTENT_TYPE_TEXT,"",0,"0");
//                    AppServer.getInstance().updateMessageStatus(mess.getPmid()+"", info2.getUid(), AppConstants.PUSH_ACTION_SYSTEM, new OnAppRequestListener() {
//                        @Override
//                        public void onAppRequest(int code, String message, Object obj) { }
//                    });
//                    if (messageRecent == null) {
//                        DbHelper.getDB(context).save(newRecentSystem); // 存入最近会话
//                    } else {
//                        int count =messageRecent.getNewcount();
//                        newRecentSystem.setNewcount(count+1);
//                        DbHelper.getDB(context).delete(messageRecent);
//                        DbHelper.getDB(context).save(newRecentSystem); // 存入最近会话
//                    }
//                    postEvent(AppEvent.HOMEFRAGMENT_REFRESH_GUIDE);
//                    if (ehList.size() > 0) {// 有监听的时候，传递下去
//                        for (int i = 0; i < ehList.size(); i++)
//                            ((EventHandler) ehList.get(i)).onMessage(mess);
//                    }
                }
            }
            // 因为用了uid，所以提取出来判断uid
            if (AppContext.getInstance().getAccountInfo().getUid() == mess.getUid()) {
                if (action == AppConstants.PUSH_ACTION_TASK) {
                    // 任务
                    final MessageRecent messagePublic = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", mess.getUid()));
                    AccountInfo info = AppContext.getInstance().getAccountInfo();
                    MessageRecent newMessagePublic  = new MessageRecent(mess.getPmid() + "", "任务" + "", mess.getDate(), mess.getUid() + "", mess.getUid()
                            + "", mess.getContent(), mess.getContent(), mess.getData() + "", mess.getFileurl() + "", 1, mess.getAction(), mess.getContenttype(), "", mess.getTypeid(), "0", info.getUid() + "a" + info.getRelationship());
                    try {
                        if (messagePublic == null) {
                            DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
                        } else {
                            int count = messagePublic.getNewcount();
                            DbHelper.getDB(context).delete(messagePublic);
                            newMessagePublic.setNewcount(count + 1);
                            DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                     AppServer.getInstance().updateMessageStatus(mess.getPmid() + "", info.getUid(), AppConstants.PUSH_PMTYPE_FRIEND, info.getRelationship(), new OnAppRequestListener() {
                         @Override
                         public void onAppRequest(int code, String message, Object obj) { }
                     });
                    showNotify(R.drawable.ic_launcher, mess, AppConstants.PUSH_ACTION_TASK);
                    postEvent(AppEvent.HOMEFRAGMENT_REFRESH_HEAD);
                } else if (action == AppConstants.PUSH_ACTION_NOTICE) {
                    return;
//                  final MessageRecent messagePublic = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId","=",mess.getUid()));
//                   AccountInfo info = AppContext.getInstance().getAccountInfo();
//                   MessageRecent newMessagePublic  = new MessageRecent(mess.getPmid()+"", "通知"+"", mess.getDate(), mess.getPmid()+"", mess.getUid()+"", mess.getContent(), mess.getContent(),mess.getData()+"", mess.getFileurl()+"", 1, mess.getAction(), mess.getContenttype(),"",mess.getTypeid(),"0");
//                  try {
//                      DbHelper.getDB(context).delete(MessageRecent.class, WhereBuilder.b("action", "=", AppConstants.PUSH_ACTION_NOTICE));
//                      DbHelper.getDB(context).save(newMessagePublic); //存入最近会话
//                  } catch (DbException e) {
//                      e.printStackTrace();
//                  }
//                  showNotify(R.drawable.ic_launcher, mess,AppConstants.PUSH_ACTION_NOTICE);
//                  postEvent(AppEvent.HOMEFRAGMENT_REFRESH_NOTICE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param mess
     * @param tagetPublicAccount
     * @throws DbException
     */
    private synchronized void handlePublicAccount(final Context context, final Message mess,PublicAccount tagetPublicAccount) {
        try {
            final MessageRecent messagePublic = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId","=",mess.getPublicid()).and(WhereBuilder.b("typeid", "=", mess.getTypeid())));
            AccountInfo info = AppServer.getInstance().getAccountInfo();
            String url = AppUtils.replaceUrl( mess.getUrl());

            String contansKey = info.getUid() + "" + info.getKid() + URL.urlkey;
            url = url.replace("{hxuid}", info.getUid() + "a" + info.getRelationship()).replace("{client}", "1").replace("{kid}", info.getKid() + "")
                    .replace("{uid}", info.getUid() + "").replace("{appver}", AppUtils.getVersionName(context)).replace("{key}", contansKey);
            MessageRecent newMessagePublic = new MessageRecent(mess.getPmid() + "", tagetPublicAccount.getNickname() + "", mess.getDate(), mess.getPublicid()+"", mess.getToid()+"", mess.getTitle(), mess.getContent(),url, mess.getFileurl()+"", 1, mess.getAction(), mess.getContenttype(),tagetPublicAccount.getAvatar(),mess.getTypeid(),"0",info.getUid()+"a"+info.getRelationship());
            MessagePublicAccount pa = DbHelper.getDB(AppContext.getInstance()).findFirst(MessagePublicAccount.class, WhereBuilder.b("pmid", "=", mess.getPmid()));
            Msgtypes type = DbHelper.getDB(context).findFirst(Msgtypes.class, WhereBuilder.b("publicid", "=", mess.getPublicid()).and("typeid", "=", mess.getTypeid()));

            if (messagePublic == null && pa == null) {
                if (mess.getTypeid() > 0 && type!=null) {
                    newMessagePublic.setName(type.getTypename());
                }
                UtilsLog.i(TAG,"handlePublicAccount messagePublic is null and pa is null");
                DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
            } else {
                if (pa == null) {
                    int count = messagePublic.getNewcount();
                    newMessagePublic.setNewcount(count + 1);
                    if (mess.getTypeid() > 0 && type!=null){
                        newMessagePublic.setName(type.getTypename());
                    }
                    // DbHelper.getDB(context).update(newMessagePublic, WhereBuilder.b("fromId", "=", mess.getPublicid()), new String[]{"date","fromId","toId","content","url","file_url","newcount","action","contentType","avatar","msgid"});
                    DbHelper.getDB(context).delete(messagePublic);
                    DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
                } else {
                    mess.setVoice(1);
                }
            }

            // 存入公众号表
            if (pa == null) {
                MessagePublicAccount messagePublicAccount = new MessagePublicAccount(mess.getPmid(), mess.getTitle(), mess.getAction(), mess.getDate(), mess.getToid(), mess.getPublicid(), mess.getContenturl(), mess.getUrl(), mess.getFileurl(), mess.getFiledesc(), mess.getContenttype(), mess.getShareable(),tagetPublicAccount.getNickname(),tagetPublicAccount.getAvatar(),mess.getTypeid(), mess.getOptag());
                if (mess.getTypeid() > 0 && type!=null) {
                    messagePublicAccount.setName(type.getTypename());
                    mess.setName(type.getTypename());
                } else {
                    mess.setName(tagetPublicAccount.getNickname());
                }
                DbHelper.getDB(context).save(messagePublicAccount);
                mess.setContent(messagePublicAccount.getTitle());
            } else {
                if (0 == mess.getOptag()) {
                    // 删除
                    DbHelper.getDB(context).delete(pa);
                } else {
                    // 更新
                    DbHelper.getDB(context).delete(pa);
                    MessagePublicAccount messagePublicAccount = new MessagePublicAccount(mess.getPmid(), mess.getTitle(), mess.getAction(), mess.getDate(), mess.getToid(), mess.getPublicid(), mess.getContenturl(), mess.getUrl(), mess.getFileurl(), mess.getFiledesc(), mess.getContenttype(), mess.getShareable(),tagetPublicAccount.getNickname(),tagetPublicAccount.getAvatar(),mess.getTypeid(), mess.getOptag());
                    DbHelper.getDB(context).save(messagePublicAccount);
                }
            }
            // showNotify(R.drawable.ic_launcher,mess,action);
            if (ehList.size() > 0) { // 有监听的时候，传递下去
                for (int i = 0; i < ehList.size(); i++)
                    ((EventHandler) ehList.get(i)).onMessage(mess);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void showNotify(int notifyRes, Message message, int action) {
        mNewNum++;
        // 更新通知栏
        AppContext application = AppContext.getInstance();
        if (application.isTopActivity()) {
            /*if(action == AppConstants.PUSH_ACTION_AGREE_FRIENDS||action == AppConstants.PUSH_ACTION_DEL_FRIENDS){
                //刷新通讯录列表
                ContactsActivity contact = (ContactsActivity) application.tabActivityList.get(1);
                contact.refreshData();
                HomeActivity home = (HomeActivity) application.tabActivityList.get(1);
                home.refreshData();
            }else {
                HomeActivity home = (HomeActivity) application.tabActivityList.get(0);
                home.refreshData();
            }*/
            return;
        } else {
            int icon = notifyRes;
            String trueMsg = "";
               trueMsg = message.getTitle() == null ? "" : message.getTitle();
            if (message.getContenttype() == AppConstants.TYPE_TEXT && trueMsg.contains("face")) {
                trueMsg = "[表情]";
            } else if (message.getContenttype() == AppConstants.TYPE_IMAGE) {
                trueMsg = "[图片]";
            } else if (message.getContenttype() == AppConstants.TYPE_AUDIO) {
                trueMsg = "[语音]";
            } else {
                trueMsg = message.getTitle() == null?message.getContent() + "":message.getTitle();
            }
            CharSequence tickerText = message.getName() + ":" + trueMsg;
            if (message.getAction() == AppConstants.PUSH_ACTION_TASK) {
                tickerText = trueMsg;
            } else if (message.getAction() == AppConstants.PUSH_ACTION_NOTICE) {
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
            notification.setLatestEventInfo(application,
                    SharedPreferencesHelper.getInstance(application).getString(AppConstants.USER_NICK, "") + " (" + mNewNum + "条新消息)",
                    tickerText, contentIntent);
            application.getNotificationManager().notify(NOTIFY_ID, notification); // 通知一下才会生效哦
        }
    }

    /**
     * @param context
     * @param mess
     * @param tofriend
     */
    private void handleChat(final Context context, final Message mess, Friend tofriend) {
        // Toast.makeText(AppContext.getInstance(), "收到好友消息", Toast.LENGTH_SHORT).show();
        // 好友消息
        // 组装chat对象
        Chat chat = new Chat(mess.getPmid(), mess.getContent(), mess.getContenttype(), mess.getUid(), mess.getToid(), mess.getDate(), 0,mess.getAction(), tofriend.getAvatar());
        // 普通消息，
        // 存储接收到的消息,并发送消息回执给对方
        ChatDb cdb = new ChatDb(AppContext.getInstance());
        cdb.save(chat);

        // 存入最近会话
        try {
            MessageRecent messagePublic = DbHelper.getDB(context).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", mess.getUid()));
            mess.setName(tofriend.getNickname());
            mess.setTitle(mess.getContent() == null?"":mess.getContent());
            if (tofriend.getAvatar() == null || tofriend.getAvatar().equals("")) {
                // Friend friend = DbHelper.getDB(AppContext.getInstance()).findFirst(Friend.class, WhereBuilder.b("uid", "=", tofriend.getUid()));
                tofriend.setAvatar("");
            }
            mess.setAvatar(tofriend.getAvatar());
            MessageRecent newMessagePublic = new MessageRecent(mess.getPmid() + "", tofriend.getNickname() + "", mess.getDate(), mess.getUid() + "",
                    mess.getToid() + "", mess.getContent(), mess.getContent(), mess.getContenturl() + "", mess.getFileurl() + "", 1, mess.getAction(), mess.getContenttype(), tofriend.getAvatar(), mess.getTypeid(), "0", "0");

            if (messagePublic == null) {
                DbHelper.getDB(context).save(newMessagePublic); //存入最近会话
            } else {
                int count = messagePublic.getNewcount();
                DbHelper.getDB(context).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));;
                // DbHelper.getDB(context).delete(messagePublic);
                newMessagePublic.setNewcount(count+1);
                DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
            }

            // 修改chat的所有记录头像
            DbHelper.updatechatHead(tofriend);
            if (ehList.size() > 0) { // 有监听的时候，传递下去
                for (int i = 0; i < ehList.size(); i++)
                    ((EventHandler) ehList.get(i)).onMessage(mess);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param mess
     * @param messagePublic
     * @param newfriend
     */
    private void handleAddFriend(final Context context, final Message mess,
            final MessageRecent messagePublic, Friend newfriend) {
        // 保存好友请求的消息
        MessageRecent newMessagePublic = new MessageRecent(mess.getPmid() + "", newfriend.getNickname() + "", mess.getDate(), mess.getUid() + "",
                mess.getToid() + "", "请求添加好友", "请求添加好友", mess.getContenturl() + "", mess.getFileurl() + "", 1, mess.getAction(), mess.getContenttype(), newfriend.getAvatar(), mess.getTypeid(), "0", "0");
        try {
            if (messagePublic == null) {
                DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
            } else {
                int count = messagePublic.getNewcount();
                DbHelper.getDB(context).delete(messagePublic);
                newMessagePublic.setNewcount(count+1);
                DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
            }
            postEvent(AppEvent.PUSH_ADDFRIEND);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param mess
     * @param messagePublic
     * @param friend
     * @param newfriend
     */
    private void handleAgreeFriend(final Context context, final Message mess,
            final MessageRecent messagePublic, final Friend friend, Friend newfriend) {
        MessageRecent newMessagePublic = new MessageRecent(mess.getPmid() + "", newfriend.getNickname() + "", mess.getDate(), mess.getUid() + "", mess.getToid() + "", "同意你的好友请求", "同意你的好友请求",
                mess.getContenturl() + "", mess.getFileurl() + "", 1, mess.getAction(), mess.getContenttype(), newfriend.getAvatar(), mess.getTypeid(), "0", "0");
        try {
            if (messagePublic == null) {
                DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
            } else {
                int count = messagePublic.getNewcount();
                DbHelper.getDB(context).delete(messagePublic);
                newMessagePublic.setNewcount(count+1);
                DbHelper.getDB(context).save(newMessagePublic); // 存入最近会话
            }
            if (friend!=null) {
                DbHelper.getDB(context).delete(Friend.class, WhereBuilder.b("uid", "=", mess.getUid()));
            }
            DbHelper.getDB(context).save(newfriend);
            postEvent(AppEvent.PUSH_AGREEFRIEND);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void handleSystemMessage(final Context context, final Message mess, MessageRecent messageRecent, MessageRecent newRecentSystem) {
        try {
            if (messageRecent == null) {
                DbHelper.getDB(context).save(newRecentSystem); // 存入最近会话
            } else {
                int count = messageRecent.getNewcount();
                newRecentSystem.setNewcount(count + 1);
                DbHelper.getDB(context).delete(messageRecent);
                DbHelper.getDB(context).save(newRecentSystem); // 存入最近会话
            }
            /** 存入系统消息表 (打开网页，无需保存。参照 HxinCmdMessageReceiver) */
//            MessageSystem systemMessage = DbHelper.getDB(AppContext.getInstance()).findFirst(MessageSystem.class, WhereBuilder.b("pmid", "=", mess.getPmid()));
//            MessageSystem newMessageSystem = new MessageSystem(mess.getPmid(), "", mess.getDate(), mess.getToid(), mess.getTitle(), mess.getContent(), 0, mess.getAction(), "");
//            if (systemMessage == null) {
//                DbHelper.getDB(context).save(newMessageSystem);
//                mess.setContent(newMessageSystem.getTitle());
//            } else {
//                DbHelper.getDB(context).delete(systemMessage);
//                DbHelper.getDB(context).save(newMessageSystem);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                UtilsLog.i(TAG, "PostThreadId:" + Thread.currentThread().getId());
            }
        }).start();
    }

}
