package com.yey.kindergaten.receive;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.LeaveSchoolBean;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.db.MessageDb;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by zy on 2015/7/20.
 */
public class HxinChatMessageReceiver extends BroadcastReceiver {

    private String TAG = "HxinChatMessageReceiver";
    private int type; // EventBus 类型
    private TextToSpeech textToSpeech; // 文本转语音操作类
    private AccountInfo accountInfo = AppServer.getInstance().getAccountInfo();
    public HxinChatMessageReceiver(Context context, int type) {
        this.type = type;
        textToSpeech = new TextToSpeech(context, listener, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
        String username = intent.getStringExtra("from"); // uid + a + relation
        String msgId = intent.getStringExtra("msgid"); // 消息id

        EMMessage message = EMChatManager.getInstance().getMessage(msgId);
        EMChatOptions option = EMChatManager.getInstance().getChatOptions();

        abortBroadcast();
        UtilsLog.i(TAG, "get hxinchatmessage username is " + username);
        if (username!=null && username.toLowerCase().contains("b")) { // 环信username中,包含a表示聊天用，包含b表示离园提醒消息
            /*处理离园消息*/
            handleLeaveMessage(message);
            return;
        } else {
            AppContext.getInstance().getMediaPlayer().start(); // 开启提示音
            long mill = 500;
            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(mill); // 开启震动
        }

        if (message == null || option == null) {
            UtilsLog.i(TAG, "message or option is null, return");
            return;
        }

        /* 处理聊天消息 */
        handleChat(message);
    }

    /**
     * 用来初始化TextToSpeech引擎
     *
     * status:SUCCESS或ERROR这2个值
     * setLanguage设置语言，帮助文档里面写了有22种
     * TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失。
     * TextToSpeech.LANG_NOT_SUPPORTED:不支持
     */
    TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                if (textToSpeech!=null) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        UtilsLog.i(TAG, "数据丢失或不支持");
                    }
                }
            }
        }
    };

    private long lastSpeakTime; // 播报时间
    private String speakContent = ""; // 播报内容
    private final static int MSG_PLAY_VOICE = 1;
    private final static int MSG_PLAY_VOICE_TIME = 5000;
    private long count = 0;
    private long currentCount = 0;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case MSG_PLAY_VOICE:
                    if (currentCount == count && !speakContent.equals("")) {
                        speakContent = speakContent + "家长来接小朋友了";
                        textToSpeech.speak(speakContent, TextToSpeech.QUEUE_FLUSH, null);
                        speakContent = "";
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 处理离校人员消息
     *
     * @param mess
     */
    private void handleLeaveMessage(final EMMessage mess) {
        UtilsLog.i(TAG, "handleleavemessage");
        // 1、先根据消息属性获取uid和date
        int uid = 0;
        String date = null;
        String title = null;
        String filedesc = null;
        int optag = 0;
        int shareable = 0;

        if (accountInfo!=null && accountInfo.getRole() == 2) { // 家长的离园消息：构造成 "健康与安全" 的公众号消息，保存到最新消息和公众号消息中；
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
            } catch (EaseMobException e) {
                UtilsLog.i(TAG, "handle leaveSchool message fail, because EaseMobException ");
            }
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
            MessagePublicAccount newleaveshool = new MessagePublicAccount(pmid, title, 1,
                    date, uid, 18, "", "", "", filedesc, 5, shareable,
                    "健康与安全", "http://sgsimg.zyey.com/timetree/default/sgs_aqjk.png?v=1", 3, optag);
            // 2、保存到最新消息数据库中
            MessageDb mdb = new MessageDb();
            List<MessagePublicAccount> messageList = new ArrayList<MessagePublicAccount>();
            messageList.add(newleaveshool);
            mdb.updateNewsPublicAccounts(messageList);
            postEvent(type);
        } else {
            try {
                uid = mess.getIntAttribute("uid");
                date = mess.getStringAttribute("date") == null ? "" : mess.getStringAttribute("date");
//              nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
//              avatar = mess.getStringAttribute("avatar") == null ? "" : mess.getStringAttribute("avatar");
            } catch (EaseMobException e) {
                UtilsLog.i(TAG,"handle leaveSchool message fail, because EaseMobException ");
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
                    long currentSpeakTime = System.currentTimeMillis();

                    // 5秒内，拼接到一起
                    if (currentSpeakTime - lastSpeakTime < 5 * 1000) {
                        count++;
                        currentCount = count;
                        speakContent = speakContent + ". " + bean.getNick();
                        mHander.sendEmptyMessageDelayed(MSG_PLAY_VOICE, MSG_PLAY_VOICE_TIME);
                    } else {
                        count++;
                        lastSpeakTime = currentSpeakTime;
                        speakContent = speakContent + ". " + bean.getNick() + "小朋友. 家长来了";
                        textToSpeech.speak(speakContent, TextToSpeech.QUEUE_FLUSH, null);
                        speakContent = "";
                    }

                    DbHelper.getDB(AppContext.getInstance()).update(bean, WhereBuilder.b("uid", "=", bean.getUid()));
                    postEvent(type);
                }
            } catch (DbException e) {
                UtilsLog.i(TAG, "handle leaveSchool message find LeaveSchoolBean DbException ");
                e.printStackTrace();
            }
        }
    }

    private void handleChat(final EMMessage mess) {
        UtilsLog.i(TAG, "into handlechat");
        postEvent(AppEvent.PUSH_REFRESH_HOMEFRAGEMENT);
        String from = "";
        String to = "";
        UtilsLog.i(TAG, "getfrom and getto is :" + mess.getFrom() + "|" + mess.getTo());

        if (mess.getFrom().length() > 2 && mess.getTo().length() > 2) {
            from = mess.getFrom().substring(0, mess.getFrom().length() - 2);
            to = mess.getTo().substring(0, mess.getTo().length() - 2);
        } else {
            UtilsLog.i(TAG, "getfrom and getto length less than 2");
        }

        MessageRecent newMessagePublic = null;
        String nick = null;
        String avatar = null;

        try {
            nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
            avatar = mess.getStringAttribute("avatar") == null ? "" : mess.getStringAttribute("avatar");
        } catch (EaseMobException e) {
            UtilsLog.i(TAG, "get nick or avatar EaseMobException");
            // 老师发聊天消息时，不传avatar和nick
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
                                        + " remoteurl:" + imgBody.getRemoteUrl() + " \n\r");*/
                newMessagePublic = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()),from , to, imgBody.getRemoteUrl(),imgBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 1,avatar,0,mess.getFrom(),mess.getTo());
                break;
            case VOICE:
                VoiceMessageBody voiceBody = (VoiceMessageBody)mess.getBody();
                               /* Log.d("voice message from:" + message.getFrom() + " length:" + voiceBody.getLength()
                                        + " remoteurl:" + voiceBody.getRemoteUrl() + " \n\r");*/
                newMessagePublic  = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()),from , to,voiceBody.getRemoteUrl(),voiceBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 2,avatar,0,mess.getFrom(),mess.getTo());
                break;
            case LOCATION:
                LocationMessageBody locationBody = (LocationMessageBody)mess.getBody();
                               /* Log.d("location message from:" + message.getFrom() + " address:" + locationBody.getAddress() +" \n\r");*/
                break;
            case VIDEO:
                VoiceMessageBody vedioBody = (VoiceMessageBody)mess.getBody();
                newMessagePublic = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()),from , to,vedioBody.getRemoteUrl(),vedioBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 3,avatar,0,mess.getFrom(),mess.getTo());
                break;
        }

        try {
            MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("hxfrom","=",newMessagePublic.getHxfrom()).and("hxto","=",newMessagePublic.getHxto()).and("action","=",0));
            if (messagePublic == null) {
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                UtilsLog.i(TAG, "handleChat : save newMessagePublic ok");
            } else { // 不是好友聊天
                int count = messagePublic.getNewcount();
                DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
                UtilsLog.i(TAG, "handleChat : delete MessageRecent ok");
                newMessagePublic.setNewcount(count + 1);
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
            }
        } catch (DbException e) {
            UtilsLog.i(TAG, "handleChat : save or delete Message fail, because DbException");
            e.printStackTrace();
        }
    };

    private long lastTime = 0;
    /**
     * 解析完成后通知首页刷新消息
     *
     * @param eventType
     */
    public void postEvent(final int eventType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(eventType));
                if (accountInfo!=null && accountInfo.getRole() == 1 && eventType == AppEvent.CHAT_HOMEFRAGMENT_REFRESH) {
                    // 控制5分钟内不重复提示
                    long currentTime = System.currentTimeMillis();
                    boolean supportDialogNotice = SharedPreferencesHelper.getInstance(AppContext.getInstance())
                            .getBoolean(AppConstants.PREF_SUPPORT_DIALOG_NOTICE_SWITCH, true);
                    if (supportDialogNotice && (currentTime - lastTime > 5 * 60 * 1000)) {
                        lastTime = currentTime;
                        EventBus.getDefault().post(new AppEvent(AppEvent.CHAT_SCHOOL_NOTIFYDIALOG));
                    }
                }
            }
        }).start();
    }

//  PreferenceManager.OnActivityDestroyListener

}
