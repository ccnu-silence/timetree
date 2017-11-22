/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yey.kindergaten.huanxin.controller;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig.EMEnvMode;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.activity.LeaveSchoolActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.LeaveSchoolBean;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.db.MessageDb;
import com.yey.kindergaten.huanxin.Activity.ChatActivity2;
import com.yey.kindergaten.huanxin.model.DefaultHXSDKModel;
import com.yey.kindergaten.huanxin.model.HXSDKModel;
import com.yey.kindergaten.huanxin.util.CommonUtils;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.HuanxinController;
import com.yey.kindergaten.util.StringUtils;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * The developer can derive from this class to talk with HuanXin SDK
 * All the Huan Xin related initialization and global listener are implemented in this class which will 
 * help developer to speed up the SDK integration。
 * this is a global instance class which can be obtained in any codes through getInstance()
 * 
 * 开发人员可以选择继承这个环信SDK帮助类去加快初始化集成速度。此类会初始化环信SDK，并设置初始化参数和初始化相应的监听器
 * 不过继承类需要根据要求求提供相应的函数，尤其是提供一个{@link com.yey.kindergaten.huanxin.model.HXSDKModel}. 所以请实现abstract protected HXSDKModel createModel();
 * 全局仅有一个此类的实例存在，所以可以在任意地方通过getInstance()函数获取此全局实例
 * 
 * @author easemob
 *
 */
public abstract class HXSDKHelper {
    private static final String TAG = "HXSDKHelper";
    /**
     * application context
     */
    protected Context appContext = null;
    private String[]relation_name = {"","爸爸","妈妈","爷爷","奶奶","外公","外婆","叔叔","阿姨"};
    private String totalName = null;
    /**
     * HuanXin mode helper, which will manage the user data and user preferences
     */
    protected HXSDKModel hxModel = null;
    
    /**
     * MyConnectionListener
     */
    protected EMConnectionListener connectionListener = null;
    
    /**
     * HuanXin ID in cache
     */
    protected String hxId = null;
    
    /**
     * password in cache
     */
    protected String password = null;
    
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;

    private final static String ShowDialogBroadcast = "showdialognotice";

    /**
     * the global HXSDKHelper instance
     */
    private static HXSDKHelper me = null;
    
    public HXSDKHelper(){
        me = this;
    }
    
    /**
     * this function will initialize the HuanXin SDK
     * 
     * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
     * 
     * 环信初始化SDK帮助函数
     * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
     * 
     * for example:
     * 例子：
     * 
     * public class DemoHXSDKHelper extends HXSDKHelper
     * 
     * HXHelper = new DemoHXSDKHelper();
     * if(HXHelper.onInit(context)){
     *     // do HuanXin related work
     * }
     */
    public synchronized boolean onInit(Context context) {
        if (sdkInited) {
            return true;
        }

        appContext = context;
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        
        // 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
        if (processAppName == null || processAppName.equals("")) {
            Log.e(TAG, "enter the service process!");
            // workaround for baidu location sdk
            // 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
            // 创建新的进程。
            // 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
            // processName，
            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }
        
        // create HX SDK model
        hxModel = createModel();
        
        // create a defalut HX SDK model in case subclass did not provide the model
        if (hxModel == null) {
            hxModel = new DefaultHXSDKModel(appContext);
        }

        // 初始化环信SDK,一定要先调用init()
        EMChat.getInstance().init(context);
        
        // 设置sandbox测试环境
        // 建议开发者开发时设置此模式
        if (hxModel.isSandboxMode()) {
            EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
        }
        
        if(hxModel.isDebugMode()) {
            // set debug mode in development process
            EMChat.getInstance().setDebugMode(true);    
        }

        Log.d(TAG, "initialize EMChat SDK");
        
        initHXOptions();
        initListener();
        sdkInited = true;
        return true;
    }
    
    /**
     * get global instance
     * @return
     */
    public static HXSDKHelper getInstance(){
        return me;
    }
    
    public HXSDKModel getModel(){
        return hxModel;
    }
    
    public String getHXId() {
        if (hxId == null) {
            hxId = hxModel.getHXId();
        }
        return hxId;
    }
    
    public String getPassword() {
        if (password == null) {
            password = hxModel.getPwd();
        }
        return password;
    }
    
    public void setHXId(String hxId) {
        if (hxId != null) {
            if (hxModel.saveHXId(hxId)) {
                this.hxId = hxId;
            }
        }
    }
    
    public void setPassword(String password) {
        if (hxModel!=null && hxModel.savePassword(password)) {
            this.password = password;
        }
    }


    private TextToSpeech textToSpeech;

    /**
     * the subclass must override this class to provide its own model or directly use {@link com.yey.kindergaten.huanxin.model.DefaultHXSDKModel}
     * @return
     */
    abstract protected HXSDKModel createModel();
    
    /**
     * please make sure you have to get EMChatOptions by following method and set related options
     *      EMChatOptions options = EMChatManager.getInstance().getChatOptions();
     */
    protected void initHXOptions() {
        Log.d(TAG, "init HuanXin Options");
        textToSpeech = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) { }
        }, null);
        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(hxModel.getAcceptInvitationAlways());
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(hxModel.getUseHXRoster());
        // 设置收到消息是否有新消息通知(声音和震动提示)，默认为true
        options.setNotifyBySoundAndVibrate(hxModel.getSettingMsgNotification());
        // 设置收到消息是否有声音提示，默认为true
//        options.setNoticeBySound(hxModel.getSettingMsgSound());
        // 设置收到消息是否震动 默认为true
        options.setNoticeBySound(false);
        options.setNoticedByVibrate(false);
//        options.setNoticedByVibrate(hxModel.getSettingMsgVibrate());
        // 设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(hxModel.getSettingMsgSpeaker());
        // 设置是否需要已读回执
        options.setRequireAck(hxModel.getRequireReadAck());
        // 设置是否需要已送达回执
        options.setNotifyText(new OnMessageNotifyListener() {
            @Override
            public String onNewMessageNotify(EMMessage mess) {

                AppContext.getInstance().getMediaPlayer().start();
                long mill = 500;
                Vibrator vib = (Vibrator) AppContext.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(mill);

                String trueMsg = CommonUtils.getMessageDigest(mess, appContext);
                String nick = null;
                String from = mess.getFrom().substring(0, mess.getFrom().length() - 2);
                try {
                    nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
                } catch (EaseMobException e) {
                    Teacher teacher = DbHelper.findAvatarById(from);
                    if (teacher!=null) {
                        nick = teacher.getRealname();
                    }
                }

                if (mess.getFrom()!=null && mess.getFrom().contains("b")) { // 环信username中,包含a表示聊天用，包含b表示离园提醒消息
                    handleLeaveMessage(mess);
                    String content = mess.getBody() + "";
                    if (content.contains("\"")) {
                        content = StringUtils.getStringBetweenString(content, "\"", "\"");
                    }
                    return content; // 通知栏上显示的信息
                } else {
                    postEvent(AppEvent.HOMEFRAGMENT_REFRESH_CHAT);
                    nitifyHuanxinMessage(mess);
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

                return nick + ": " + trueMsg;
            }

            @Override
            public String onLatestMessageNotify(EMMessage emMessage, int i, int i2) {
                return null;
            }

            @Override
            public String onSetNotificationTitle(EMMessage emMessage) {
                return null;
            }

            @Override
            public int onSetSmallIcon(EMMessage emMessage) {
                return 0;
            }
        });
        options.setRequireDeliveryAck(hxModel.getRequireDeliveryAck());
//      options.setOnNotificationClickListener(getNotificationClickListener());
        options.setOnNotificationClickListener(new OnNotificationClickListener() {
            @Override
            public Intent onNotificationClick(EMMessage emMessage) {
//              handleWeChat(emMessage);
                /**
                 * 通知服务器用户点击了通知栏
                 */
                final AccountInfo accountInfo =  AppContext.getInstance().getAccountInfo();
                String version = AppUtils.getVersionName(AppContext.getInstance());
                AppServer.getInstance().launchLog(accountInfo.getUid(),accountInfo.getRole(), 1, version, "点击消息通知栏打开", new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) { }
                });

                String nick ;
                int uid = 0;
                Intent intent = null;
                try {
                    uid = emMessage.getIntAttribute("uid");
                } catch (EaseMobException e) {
                    UtilsLog.i(TAG, "handle leaveSchool message fail, because EaseMobException ");
                }
                try {
                    nick = emMessage.getStringAttribute("nick") == null ? "" : emMessage.getStringAttribute("nick");
                    if (emMessage.getFrom()!=null && emMessage.getFrom().toLowerCase().contains("b")) { // 环信username中,包含a表示聊天用，包含b表示离园提醒消息
                        if (uid!=0) { // 表示是老师： 老师传uid，家长传toid
                            intent = handleLeaveIntent(1);
                        } else {
                            intent = handleLeaveIntent(2);
                        }
                    } else {
                        intent = handleIntoChat(emMessage);
                    }
                } catch (EaseMobException e) {

                }

                return intent;
            }
        });

    }

    private Intent handleLeaveIntent(int role) {
        Intent intent = null;
        if (role == AppConstants.PARENTROLE) {
            intent = new Intent(appContext, MainActivity.class);
        } else if (role == AppConstants.TEACHERROLE) {
            intent = new Intent(appContext, LeaveSchoolActivity.class);
        }
        return intent;
    }

    protected Intent handleIntoChat(EMMessage emMessage) {
        String avatar;
        Intent intent = null;
        try {
            avatar = emMessage.getStringAttribute("avatar") == null ? "" : emMessage.getStringAttribute("avatar");
            intent = new Intent(appContext, ChatActivity2.class);
            Bundle bundle = new Bundle();
            bundle.putString("userId", emMessage.getFrom());
            totalName = HuanxinController.getRelationNameByHuanxinRecent(emMessage);
            bundle.putString("nick", totalName );
            bundle.putString("toChatAvatar", avatar);
            bundle.putString("state", "notifyclick");
            bundle.putParcelable("message", emMessage);
            intent.putExtras(bundle);
        } catch (EaseMobException e) {

        }

        return intent;
    }

    /**
     * 处理离校人员消息
     *
     * @param mess
     */
    private void handleLeaveMessage(final EMMessage mess) {
        int uid = 0;
        int toid = 0;
        String date = null;
        String title = null;
        String filedesc = null;
        int optag = 0;
        int shareable = 0;
        AccountInfo accountInfo = null;
        try {
            toid = mess.getIntAttribute("toid");
        } catch (EaseMobException e) {
            UtilsLog.i(TAG, "handle leaveSchool message fail, because EaseMobException ");
        }
        if (toid!=0) { // 家长的离园消息：构造成 "健康与安全" 的公众号消息，保存到最新消息和公众号消息中；
            //                1、pmid从10亿开始； 2、没有url，纯文本消息，不能点击查看，不出现 "阅读全文" 字样
            // 1、保存到 "健康与安全" 公众号消息中 (对家长来说，"幼儿园" <以前是时光树> publicid为18，"健康与安全" typeid为3)
            try {
                optag = mess.getIntAttribute("optag");
                shareable = mess.getIntAttribute("shareable");

                title = mess.getStringAttribute("title") == null ? "" : mess.getStringAttribute("title");
                date = mess.getStringAttribute("date") == null ? TimeUtil.getYMDHMSS() : mess.getStringAttribute("date");
                filedesc = mess.getStringAttribute("filedesc") == null ? "" : mess.getStringAttribute("filedesc");
                uid = mess.getIntAttribute("toid");
//              nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
//              avatar = mess.getStringAttribute("avatar") == null ? "" : mess.getStringAttribute("avatar");
                int pmid = 0;
                String sql = "select * from messagePublicAccount where pmid = (select max(pmid) from messagePublicAccount)";
                List<MessagePublicAccount> maxpmidmessages = DbHelper.QueryTData(sql, MessagePublicAccount.class);
                if (maxpmidmessages!=null && maxpmidmessages.size() > 0) {
                    if (maxpmidmessages.get(0).getPmid() > 999999999) {
                        pmid = maxpmidmessages.get(0).getPmid() + 1;
                    } else {
                        pmid = 1000000000;
                    }
                } else {
                    pmid = 1000000000;
                }
                MessagePublicAccount newleaveshool = new MessagePublicAccount(pmid, title, 1, date, uid, 18, "", "", "", filedesc, 5, shareable,
                        "健康与安全", "http://sgsimg.zyey.com/timetree/default/sgs_aqjk.png?v=1", 3, optag);
                // 2、保存到最新消息数据库中
                MessageDb mdb = new MessageDb();
                List<MessagePublicAccount> messageList = new ArrayList<MessagePublicAccount>();
                messageList.add(newleaveshool);
                mdb.updateNewsPublicAccounts(messageList);
                postEvent(AppEvent.CHAT_HOMEFRAGMENT_REFRESH);
            } catch (EaseMobException e) {
                UtilsLog.i(TAG, "handle leaveSchool message fail, because EaseMobException ");
            }
        } else {
            try {
                uid = mess.getIntAttribute("uid");
                date = mess.getStringAttribute("date") == null ? "" : mess.getStringAttribute("date");
            } catch (EaseMobException e) {
                UtilsLog.i(TAG, "handle leaveSchool message fail, because EaseMobException ");
            }
            // 更新离园数据库数据，并通知首页
            try {
                LeaveSchoolBean bean = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(LeaveSchoolBean.class).where("uid", "=", uid));
                if (bean == null) {
                    UtilsLog.i(TAG, "handle leaveSchool message find LeaveSchoolBean, but it is null ");
                    return;
                } else {
                    bean.setIsLeave(1); // 表示已经离园
                    bean.setDate(date);
//                  bean.setContent(mess.getBody().toString());
                    textToSpeech.speak(bean.getNick() + "家长来接小朋友了", TextToSpeech.QUEUE_FLUSH, null);
                    DbHelper.getDB(AppContext.getInstance()).update(bean, WhereBuilder.b("uid", "=", bean.getUid()));
                }
            } catch (DbException e) {
                UtilsLog.i(TAG, "handle leaveSchool message find LeaveSchoolBean DbException ");
                e.printStackTrace();
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

    public void nitifyHuanxinMessage(EMMessage mess) {
        MessageRecent newMessagePublic = null;
        String from = mess.getFrom().substring(0, mess.getFrom().length() - 2);
        String to = mess.getTo().substring(0, mess.getTo().length() - 2);
        String nick = null;
        String avatar = null;
        String content = null;
        try {
            nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
            avatar = mess.getStringAttribute("avatar") == null ? "" : mess.getStringAttribute("avatar");
        } catch (EaseMobException e) {
            Teacher teacher = DbHelper.findAvatarById(from);
            if (teacher!=null) {
                avatar = teacher.getAvatar();
                nick = teacher.getRealname();
            }
        }
        switch (mess.getType()) {
            case TXT:
                TextMessageBody txtBody = (TextMessageBody)mess.getBody();
                newMessagePublic = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()),from , to, txtBody.getMessage(),txtBody.getMessage(), "", "", 1, AppConstants.PUSH_ACTION_FRIENDS, 0,avatar,0,mess.getFrom(),mess.getTo());
                break;
            case IMAGE:
                ImageMessageBody imgBody = (ImageMessageBody)mess.getBody();
                               /* Log.d("img message from:" + mess.getFrom() + " thumbnail:" + imgBody.getThumbnailUrl()
                                        + " remoteurl:" + imgBody.getRemoteUrl()+ " \n\r");*/
                newMessagePublic = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()),from , to, imgBody.getRemoteUrl(),imgBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 1,avatar,0,mess.getFrom(),mess.getTo());
                break;
            case VOICE:
                VoiceMessageBody voiceBody = (VoiceMessageBody)mess.getBody();
                              /*  Log.d("voice message from:" + message.getFrom() + " length:" + voiceBody.getLength()
                                        + " remoteurl:" + voiceBody.getRemoteUrl()+ " \n\r");*/
                newMessagePublic = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()),from , to,voiceBody.getRemoteUrl(),voiceBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 2,avatar,0,mess.getFrom(),mess.getTo());
                break;
            case LOCATION:
                LocationMessageBody locationBody = (LocationMessageBody)mess.getBody();
                             /*   Log.d("location message from:" + message.getFrom() + " address:" + locationBody.getAddress() +" \n\r");*/
                break;
            case VIDEO:
                VoiceMessageBody vedioBody = (VoiceMessageBody)mess.getBody();
                newMessagePublic = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()),from , to,vedioBody.getRemoteUrl(),vedioBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 3,avatar,0,mess.getFrom(),mess.getTo());
                break;
        }
        try {
            MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", from).and("action","=",0));
            if (messagePublic == null) {
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
            } else { // 不是好友聊天
                int count = messagePublic.getNewcount();
                DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
                newMessagePublic.setNewcount(count + 1);
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * logout HuanXin SDK
     */
    public void logout(final EMCallBack callback) {
        EMChatManager.getInstance().logout(new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                setPassword(null);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }
    
    /**
     * 检查是否已经登录过
     *
     * @return
     */
    public boolean isLogined() {
        UtilsLog.i(TAG, "jugy the islogined");
        if (hxModel!=null && hxModel.getHXId() != null && hxModel.getPwd() != null) {
            return true;
        }
        return false;
    }

    /**
     * get the message notify listener
     * @return
     */
    protected OnMessageNotifyListener getMessageNotifyListener() {
        OnMessageNotifyListener listener  = new OnMessageNotifyListener() {
            @Override
            public String onNewMessageNotify(EMMessage emMessage) {
                return null;
            }

            @Override
            public String onLatestMessageNotify(EMMessage emMessage, int i, int i2) {
                return null;
            }

            @Override
            public String onSetNotificationTitle(EMMessage emMessage) {
                return null;
            }

            @Override
            public int onSetSmallIcon(EMMessage emMessage) {
                return 0;
            }

        };

        return listener;
    }
    
    /**
     * get notification click listener
     */
    protected OnNotificationClickListener getNotificationClickListener(){
        return null;
    }

    /**
     * init HuanXin listeners
     */
    protected void initListener() {
        Log.d(TAG, "init listener");
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                } else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                } else {
                    onConnectionDisconnected(error);
                }
            }
            @Override
            public void onConnected() {
                onConnectionConnected();
            }
        };
        EMChatManager.getInstance().addConnectionListener(connectionListener);
    }
    
    /**
     * the developer can override this function to handle connection conflict error
     */
    protected void onConnectionConflict(){}

    /**
     * the developer can override this function to handle user is removed error
     */
    protected void onCurrentAccountRemoved(){}

    /**
     * handle the connection connected
     */
    protected void onConnectionConnected(){}
    
    /**
     * handle the connection disconnect
     * @param error see {@link com.easemob.EMError}
     */
    protected void onConnectionDisconnected(int error){}

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
