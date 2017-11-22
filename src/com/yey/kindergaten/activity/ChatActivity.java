/**
 * 时光树
 * com.yey.kindergaten.activity
 * ChatActivity.java
 *
 * 2014年7月24日-下午2:39:14
 *  2014中幼信息科技公司-版权所有
 *
 */
package com.yey.kindergaten.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.EmoViewPagerAdapter;
import com.yey.kindergaten.adapter.EmoteAdapter;
import com.yey.kindergaten.adapter.MessageChatAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Chat;
import com.yey.kindergaten.bean.FaceText;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.Message;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.db.ChatDb;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.db.MessageDb;
import com.yey.kindergaten.inter.OnRecordChangeListener;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.receive.PushReceiver;
import com.yey.kindergaten.task.SimpleTask;
import com.yey.kindergaten.task.TaskExecutor;
import com.yey.kindergaten.task.TaskExecutor.OrderedTaskExecutor;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.DecodeUtils;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.RecordManager;
import com.yey.kindergaten.util.Session;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.EmoticonsEditText;
import com.yey.kindergaten.widget.xlist.XListView;
import com.yey.kindergaten.widget.xlist.XListView.IXListViewListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面
 * ChatActivity
 * chaowen
 * 511644784@qq.com
 * 2014年7月24日 下午2:39:14
 * @version 1.0.0
 *
 */
public class ChatActivity extends BaseActivity implements IXListViewListener,PushReceiver.EventHandler{
    @ViewInject(R.id.left_btn)ImageView left_iv;
    @ViewInject(R.id.header_title)TextView tv_headerTitle ;
    @ViewInject(R.id.btn_chat_emo)Button btn_chat_emo;
    @ViewInject(R.id.btn_chat_send)Button btn_chat_send;
    @ViewInject(R.id.btn_chat_image)Button btn_chat_image;
    @ViewInject(R.id.btn_chat_keyboard)Button btn_chat_keyboard;
    @ViewInject(R.id.btn_speak)Button btn_speak;
    @ViewInject(R.id.btn_chat_voice)Button btn_chat_voice;
    @ViewInject(R.id.mListView)XListView mListView;
    @ViewInject(R.id.edit_user_comment)EmoticonsEditText edit_user_comment;
    @ViewInject(R.id.layout_more)LinearLayout layout_more;
    @ViewInject(R.id.layout_emo)LinearLayout layout_emo;
    @ViewInject(R.id.layout_add)LinearLayout layout_add;
    @ViewInject(R.id.tv_picture)TextView tv_picture;
    //语音相关
    @ViewInject(R.id.iv_record)ImageView iv_record;
    @ViewInject(R.id.layout_record)RelativeLayout layout_record;
    @ViewInject(R.id.tv_voice_tips)TextView tv_voice_tips;
    Toast toast;
    private Drawable[] drawable_Anims;// 话筒动画
    private ViewPager pager_emo;
    Friend target = null;
    MessageChatAdapter mAdapter;
    AccountInfo info = null;
    public static final int NEW_MESSAGE = 0x001;// 收到消息
    RecordManager recordManager = null;
    private static float recodeTime=0.0f;    //录音的时间
    private static double voiceValue=0.0;    //麦克风获取的音量值
    private Thread recordThread;
    public static int RECORD_NO = 0;  //不在录音
    public static int RECORD_ING = 1;   //正在录音
    public static int RECODE_ED = 2;   //完成录音
    public static int RECODE_STATE = 0;      //录音的状态
    String state="";
    private String name;
    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/chatting/";
    private String localCameraPath = "";// 拍照后得到的图片地址
    CharSequence[] items = { "相册", "拍照" };
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static ChatActivity activityInstance = null;

    // 给谁发送消息
    private String toChatUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ViewUtils.inject(this);
        Session session = Session.getSession();
        target = (Friend) session.get(AppConstants.SESSION_TARGETFRIEND);
        state=(String) session.get("state");
        info = AppServer.getInstance().getAccountInfo();
        initView();
    }
    private void initRecordManager(){
        // 语音相关管理器
        recordManager = RecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                // TODO Auto-generated method stub
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                // TODO Auto-generated method stub
                Log.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= RecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    //sendVoiceMessage(localPath);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                }else{

                }
            }
        });
    }


    /**
     * 初始化语音布局
     *
     */
    private void initVoiceView() {
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 初始化语音动画资源
     *
     * @Title: initVoiceAnimRes
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[] {
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6) };
    }

    /**
     * 长按说话
     * @ClassName: VoiceTouchListen
     * @Description: TODO
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (RECODE_STATE != RECORD_ING) {
                        if (!AppUtils.checkSdCard()) {
                            showToast("发送语音需要sdcard支持！");
                            return false;
                        }
                        try {
                            RECODE_STATE=RECORD_ING;
                            v.setPressed(true);
                            layout_record.setVisibility(View.VISIBLE);
                            tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                            // 开始录音
                            recordManager.startRecording(target.getUid()+"");
                            mythread();
                        } catch (Exception e) {
                        }
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips
                                .setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    if (RECODE_STATE == RECORD_ING) {
                        RECODE_STATE=RECODE_ED;
                        v.setPressed(false);
                        layout_record.setVisibility(View.INVISIBLE);
                        try {
                            if (event.getY() < 0) {// 放弃录音
                                recordManager.cancelRecording();
                                Log.i("voice", "放弃发送语音");
                            } else {
                                int recordTime = recordManager.stopRecording();
                                voiceValue = 0.0;
                                if (recordTime > 1) {
                                    // 发送语音文件
                                    Log.i("voice", "发送语音");
                                    String file = recordManager.getRecordFilePath(target.getUid()+"");
                                    sendVoiceMessage(file,recordTime);
                                } else {// 录音时间过短，则提示录音过短的提示
                                    layout_record.setVisibility(View.GONE);
                                    showShortToast().show();
                                }
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                    return true;
                default:
                    return false;
            }
        }
    }


    /**
     * 显示录音时间过短的Toast
     *
     * @Title: showShortToast
     * @return void
     * @throws
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    private void initView() {

        initEmoView();
        initVoiceView();
        left_iv.setVisibility(View.VISIBLE);
        tv_headerTitle.setText(target.getNickname());
        btn_chat_send.setVisibility(View.VISIBLE);
        btn_chat_voice.setVisibility(View.GONE);
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                //因为去掉了语音，这段注释了
			/*	if (!TextUtils.isEmpty(s)) {
					btn_chat_send.setVisibility(View.VISIBLE);
					btn_chat_keyboard.setVisibility(View.GONE);
					btn_chat_voice.setVisibility(View.GONE);
				} else {
					if (btn_chat_voice.getVisibility() != View.VISIBLE) {
						btn_chat_voice.setVisibility(View.VISIBLE);
						btn_chat_send.setVisibility(View.GONE);
						btn_chat_keyboard.setVisibility(View.GONE);
					}
				}*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        initXListView();
    }

    List<FaceText> emos;

    /**
     * 初始化表情布局
     *
     * @Title: initEmoView
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initEmoView() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 1; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(1, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this,
                list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText()
                                .insert(start, key);
                        edit_user_comment.setText(content);
                        // 定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
        return view;
    }

    @OnClick({R.id.btn_chat_voice,R.id.tv_picture,R.id.left_btn,R.id.lookdata_btn,R.id.edit_user_comment,R.id.btn_chat_send,R.id.btn_chat_emo,R.id.btn_chat_image,R.id.btn_chat_keyboard})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_chat_voice:// 语音按钮
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.left_btn:
                if(state.equals(AppConstants.CONTACTS_FRIENDREQUEST)){
                    Intent intent=new Intent(this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    this.finish();
                }
                break;

            case R.id.edit_user_comment:// 点击文本输入框
                mListView.setSelection(mListView.getCount() - 1);
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_send:// 发送文本
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    showToast("请输入发送消息!");
                    return;
                }
                boolean isNetConnected = AppUtils.isNetworkAvailable(this);
                if (!isNetConnected) {
                    showToast("当前网络不可用");
                    // return;
                }
                // 组装Chat对象
                final ChatDb chatdb = new ChatDb(this);
                Chat chat = new Chat();
                chat.setContent(msg);
                chat.setContenttype(AppConstants.TYPE_TEXT);
                chat.setDate(TimeUtil.getYMDHMS());
                final int creId  = chatdb.getId();
                chat.setPmid(chatdb.getId());
                chat.setStatus(1); //1保存在本地，0是成功发送到服务器
                chat.setUid(info.getUid());
                chat.setToid(target.getUid());
                chat.setAvatar(AppServer.getInstance().getAccountInfo().getAvatar());
                chatdb.save(chat);
                //保会话中存到最近
                try {
                    MessageDb mdb = new MessageDb();
                    MessageRecent messagerecent = DbHelper.getDB(this).findFirst(Selector.from(MessageRecent.class).where("fromId","=",chat.getToid()));
                    MessageRecent newmessageRecent = new MessageRecent(chat.getPmid()+"", target.getNickname(), chat.getDate(), chat.getToid()+"", chat.getUid()+"", chat.getContent(),chat.getContent(), "", "", mdb.getNewcount(target.getUid()), AppConstants.PUSH_ACTION_FRIENDS, AppConstants.PUSH_CONTENT_TYPE_TEXT,target.getAvatar(),-1,"0","0");
                    if(messagerecent == null){
                        mdb.save(newmessageRecent);
                    }else{
                        int count =messagerecent.getNewcount();
                        DbHelper.getDB(this).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagerecent.getMsgid()));;
                        newmessageRecent.setNewcount(0);
                        DbHelper.getDB(this).save(newmessageRecent); //存入最近会话
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

//			BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
//			默认发送完成，将数据保存到本地消息表和最近会话表中
//			manager.sendTextMessage(targetUser, message);
//			刷新界面
                final Chat newchat = chat;
                AppServer.getInstance().sentChat(chat.getUid(), chat.getToid(), chat.getContenttype(), chat.getContent(), new OnAppRequestListener() {

                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if(code == AppServer.REQUEST_SUCCESS){
                            int pmid = (Integer)obj;
                            System.out.println(pmid);
                            newchat.setPmid(pmid);
                            newchat.setStatus(0);
                            updateMessage(newchat);
                            chatdb.updateChat(newchat, creId);
                        }else{
                            newchat.setStatus(-1);
                            updateMessage(newchat);
                            chatdb.updateChat(newchat, creId);
                        }
                    }
                });
                refreshMessage(chat);

                break;
            case R.id.btn_chat_emo:
                if (layout_more.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_chat_image://// 添加按钮-显示图片、拍照、位置
			/*if (layout_more.getVisibility() == View.GONE) {
				layout_more.setVisibility(View.VISIBLE);
				layout_add.setVisibility(View.VISIBLE);
				layout_emo.setVisibility(View.GONE);
				hideSoftInputView();
			} else {
				if (layout_emo.getVisibility() == View.VISIBLE) {
					layout_emo.setVisibility(View.GONE);
					layout_add.setVisibility(View.VISIBLE);
				} else {
					layout_more.setVisibility(View.GONE);
				}
			}*/
                layout_emo.setVisibility(View.GONE);
                hideSoftInputView();
                AppContext.checkList.clear();

                showDialogItems(items, "选择图片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){   //相册
                            selectImageFromLocal();
                        }else{ 		   //相机
                            selectImageFromCamera();
                        }
                    }
                });
                break;
            case R.id.tv_picture:// 图片
                selectImageFromLocal();
                break;
            case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
                showEditState(false);
                break;
            default:
                break;
        }

    }

    @Override
    public void onMessage(Message mess) {
        android.os.Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = mess;
        handler.sendMessage(handlerMsg);

    }

    @Override
    public void onBind(String method, int errorCode, String content) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNotify(String title, String content) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub

    }



    @Override
    public void onRefresh() {


    }



    @Override
    public void onLoadMore() {


    }

    /**
     * 加载消息历史，从数据库中读出
     * @throws DbException
     */
    private List<Chat> initMsgData(){
        List<Chat> list = new ArrayList<Chat>();
        try {
            if(target!=null){
                list = DbHelper.getDB(this).findAll(Chat.class, WhereBuilder.b("toid", "=", target.getUid()).or("uid", "=", target.getUid()));
            }
            if(list == null){
                list = new ArrayList<Chat>();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 界面刷新
     * @Title: initOrRefresh
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initOrRefresh() {
        if (mAdapter != null) {
            if (PushReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news=  PushReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
				/*for(int i=news;i>=0;i--){
					mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
				}*/
                mAdapter.setList(initMsgData());
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);

        }
    }


    private void initXListView() {
        // 首先不允许加载更多
        mListView.setPullLoadEnable(false);
        // 允许下拉
        mListView.setPullRefreshEnable(false);
        // 设置监听器
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mListView.setDividerHeight(0);
        // 加载数据
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);

        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                //btn_chat_voice.setVisibility(View.VISIBLE); //暂时去掉语音，加上要去掉注释
                btn_chat_keyboard.setVisibility(View.GONE);
                //btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });

        // 重发按钮的点击事件
        // 重发按钮的点击事件
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
                new MessageChatAdapter.onInternalClickListener() {

                    @Override
                    public void OnClickListener(View parentV, View v,
                                                Integer position, Object values) {
                        // 重发消息
                        showResendDialog(parentV, v, values,position);
                    }
                });
    }

    /**
     * 显示重发按钮 showResendDialog
     * @Title: showResendDialog
     * @Description: TODO
     * @param @param recent
     * @return void
     * @throws
     */
    public void showResendDialog(final View parentV, View v, final Object values,final Integer position) {
        DialogTips dialog = new DialogTips(this, "确定重发该消息", "确定", "取消", "提示",
                true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                if (((Chat) values).getContenttype() == AppConstants.TYPE_IMAGE) {// 图片和语音类型的采用
                    resendImageMsg(parentV, values,position);
                } else {
                    resendTextMsg(parentV, values,position);
                }
                dialogInterface.dismiss();
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    /**
     * 重发图片
     * @param parentV
     * @param values
     */
    protected void resendImageMsg(final View parentV, Object values,int position) {
        final Chat newchat = (Chat) values;
        newchat.setStatus(-2);
        mAdapter.getList().set(position, newchat);
        final ChatDb chatdb = new ChatDb(this);
        RequestParams params = new RequestParams();
        params.addBodyParameter(AppConstants.PARAM_UID, AppServer.getInstance().getAccountInfo().getUid()+"");
        params.addBodyParameter("file",new File(newchat.getContent()));
        params.addBodyParameter("type",AppConstants.PARAM_UPLOAD_CHAT);
        String timestamp = URL.urlkey;
        params.addBodyParameter(AppConstants.PARAM_KEY, AppUtils.Md5(AppServer.getInstance().getAccountInfo().getUid()+""+timestamp));
        HttpUtils http = new HttpUtils();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        http.send(HttpRequest.HttpMethod.POST, info.getSysgw()+URL.UPLOADFILE,params, new RequestCallBack<String>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //返回图片url更新到数据库
                String result  = responseInfo.result;
                JsonParser parser = new JsonParser();
                JsonElement  elements = parser.parse(result);
                if(elements.isJsonArray()){
                    JsonArray  array = elements.getAsJsonArray();
                }else{
                    JsonObject object =	elements.getAsJsonObject();
                    String code =  object.get("code").getAsString();
                    JsonObject rejsonobject = object.get("result").getAsJsonObject();
                    String url = rejsonobject.get("url").getAsString();
                    newchat.setContent(url);
                    System.out.println(url);

                    AppServer.getInstance().sentChat(newchat.getUid(), newchat.getToid(), newchat.getContenttype(), newchat.getContent(), new OnAppRequestListener() {

                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if(code == AppServer.REQUEST_SUCCESS){
                                System.out.println("code----onsuccess-----------");
                                int pmid = (Integer)obj;
                                System.out.println(pmid);
                                newchat.setPmid(pmid);
                                newchat.setStatus(0);
                                updateMessage(newchat);
                                chatdb.updateChat(newchat, newchat.getId());
                                parentV.findViewById(R.id.progress_load).setVisibility(
                                        View.INVISIBLE);
                                parentV.findViewById(R.id.iv_fail_resend)
                                        .setVisibility(View.INVISIBLE);
                            }else{
                                System.out.println("code----onFailure-----------");
                                newchat.setStatus(-1);
                                updateMessage(newchat);
                                chatdb.updateChat(newchat, newchat.getId());
                                parentV.findViewById(R.id.progress_load).setVisibility(
                                        View.INVISIBLE);
                                parentV.findViewById(R.id.iv_fail_resend)
                                        .setVisibility(View.VISIBLE);
                                parentV.findViewById(R.id.tv_send_status)
                                        .setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }

                //mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                System.out.println("onFailure-----------");
                newchat.setStatus(-1);
                updateMessage(newchat);
                chatdb.updateChat(newchat, newchat.getId());
                parentV.findViewById(R.id.progress_load).setVisibility(
                        View.INVISIBLE);
                parentV.findViewById(R.id.iv_fail_resend)
                        .setVisibility(View.VISIBLE);
                parentV.findViewById(R.id.tv_send_status)
                        .setVisibility(View.INVISIBLE);
            }
        });

        mAdapter.notifyDataSetChanged();

    }
    /**
     * 重发文本消息
     */
    private void resendTextMsg(final View parentV, final Object values,int position) {
        final Chat chat = (Chat) values;
        chat.setStatus(1);
        mAdapter.getList().set(position, chat);
        final ChatDb chatdb = new ChatDb(this);
        parentV.findViewById(R.id.progress_load).setVisibility(
                View.VISIBLE);
        parentV.findViewById(R.id.iv_fail_resend)
                .setVisibility(View.INVISIBLE);
        mAdapter.notifyDataSetChanged();
        AppServer.getInstance().sentChat(chat.getUid(), chat.getToid(), chat.getContenttype(), chat.getContent(), new OnAppRequestListener() {

            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if(code == AppServer.REQUEST_SUCCESS){
                    int pmid = (Integer)obj;
                    System.out.println(pmid);
                    chat.setPmid(pmid);
                    chat.setStatus(0);
                    updateMessage(chat);
                    chatdb.updateChat(chat, chat.getId());
                    parentV.findViewById(R.id.progress_load).setVisibility(
                            View.INVISIBLE);
                    parentV.findViewById(R.id.iv_fail_resend)
                            .setVisibility(View.INVISIBLE);
                }else{
                    chat.setStatus(-1);
                    updateMessage(chat);
                    chatdb.updateChat(chat, chat.getId());
                    parentV.findViewById(R.id.progress_load).setVisibility(
                            View.INVISIBLE);
                    parentV.findViewById(R.id.iv_fail_resend)
                            .setVisibility(View.VISIBLE);
                    parentV.findViewById(R.id.tv_send_status)
                            .setVisibility(View.INVISIBLE);

                }
            }
        });
        mAdapter.notifyDataSetChanged();
        //refreshMessage(chat);
        //mAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新界面
     *
     * @Title: refreshMessage
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshMessage(Chat chat) {
        // 更新界面
        mAdapter.add(chat);
        mListView.setSelection(mAdapter.getCount() - 1);
        edit_user_comment.setText("");
    }


    private void updateMessage(Chat chat){
        List<Chat> list = initMsgData();
        list.set(list.size()-1, chat);
        mAdapter.setList(list);

        //mAdapter.addAll(list);
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == NEW_MESSAGE) {
                PushReceiver.mNewNum=0;
                if(target!=null){
                    try {
                        MessageRecent recent = DbHelper.getDB(AppContext.getInstance()).findFirst(MessageRecent.class, WhereBuilder.b("fromId", "=", target.getUid()));
                        recent.setNewcount(0);
                        DbHelper.getDB(AppContext.getInstance()).update(recent, "newcount");
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

                Message message = (Message) msg.obj;
                Chat chat;
                try {
                    chat = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(Chat.class).where(WhereBuilder.b("pmid", "=", message.getPmid())));
                    //Chat chat = new Chat(message.getPmid(), message.getContent(), message.getContentype(), message.getUid(), message.getToid(), message.getDate(), 0,message.getAction(),target.getAvatar());
                    if(chat!=null){
                        int uid = chat.getUid();
                        if (uid != target.getUid())// 如果不是当前正在聊天对象的消息，不处理
                            return;
                        mAdapter.add(chat);
                    }
                    // 定位
                    mListView.setSelection(mAdapter.getCount() - 1);
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        // 新消息到达，重新刷新界面
        initOrRefresh();
        PushReceiver.ehList.add(this);// 监听推送的消息
        //清空消息未读数-这个要在刷新之后
        PushReceiver.mNewNum=0;
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        AppContext.getInstance().getNotificationManager().cancel(
                PushReceiver.NOTIFY_ID);

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        PushReceiver.ehList.remove(this);// 监听推送的消息
    }


    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @Title: showEditState
     * @Description: TODO
     * @param @param isEmo: 用于区分文字和表情
     * @return void
     * @throws
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        //btn_chat_keyboard.setVisibility(View.GONE);
        //btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
        }
    }

    /**
     * 启动相机拍照 startCamera
     *
     * @Title: startCamera
     * @throws
     */
    public void selectImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent,
                AppConstants.REQUESTCODE_TAKE_CAMERA);
    }

    /**
     * 选择图片
     *
     * @Title: selectImage
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    public void selectImageFromLocal() {
			/*Intent intent;
			if (Build.VERSION.SDK_INT < 19) {
				intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
			} else {
				intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			}*/
        Intent i=new Intent(ChatActivity.this,GetSDCardAlbumActivity.class);
        i.putExtra("typefrom", AppConstants.FROMChat);
        startActivityForResult(i, AppConstants.REQUESTCODE_TAKE_LOCAL);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
                    String pressImageCamera=save(localCameraPath);
                    sendImageMessage(pressImageCamera,localCameraPath);
                    break;
                case AppConstants.REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        ArrayList<Photo> list = data.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
                        for(Photo p: list){
                            String pressImageUri=save(p.imgPath);
                            sendImageMessage(pressImageUri,p.imgPath);
                        }
						/*Uri selectedImage = data.getData();
						if (selectedImage != null) {
							Cursor cursor = getContentResolver().query(
									selectedImage, null, null, null, null);
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							String localSelectPath = cursor.getString(columnIndex);
							cursor.close();
							if (localSelectPath == null
									|| localSelectPath.equals("null")) {
								showToast("找不到您想要的图片");
								return;
							}
							String pressImageUri=save(localSelectPath);				
							sendImageMessage(pressImageUri);
						}*/

                    }
                    break;

            }
        }
    }
    /**
     * 默认先上传本地图片，之后才显示出来 sendImageMessage
     *
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendImageMessage(final String comparesslocal,String srclocal) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        //先更新到本地
        final ChatDb chatdb = new ChatDb(this);
        Chat chat = new Chat();
        chat.setContent(srclocal);
        chat.setContenttype(AppConstants.TYPE_IMAGE);
        chat.setDate(TimeUtil.getYMDHMS());
        final int creId  = chatdb.getId();
        chat.setPmid(creId);
        chat.setStatus(-2); //1保存在本地，0是成功发送到服务器
        chat.setUid(info.getUid());
        chat.setToid(target.getUid());
        chat.setAvatar(AppServer.getInstance().getAccountInfo().getAvatar());
        chatdb.save(chat);
        //保存到最近会话中
        try {
            MessageDb mdb = new MessageDb();
            MessageRecent messagerecent = DbHelper.getDB(this).findFirst(Selector.from(MessageRecent.class).where("fromId","=",chat.getToid()));
            MessageRecent message = new MessageRecent(chat.getPmid()+"", target.getNickname(), chat.getDate(), chat.getToid()+"", chat.getUid()+"", chat.getContent(),chat.getContent(), "", "", mdb.getNewcount(target.getUid()), AppConstants.PUSH_ACTION_FRIENDS, AppConstants.PUSH_CONTENT_TYPE_IMAGE,target.getAvatar(),-1,"0","");
            if(messagerecent == null){
                mdb.save(message);
            }else{
                int count =messagerecent.getNewcount();
                DbHelper.getDB(this).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagerecent.getMsgid()));;
                //DbHelper.getDB(this).delete(messagerecent);
                message.setNewcount(0);
                DbHelper.getDB(this).save(message); //存入最近会话
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        //BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
        // 默认发送完成，将数据保存到本地消息表和最近会话表中
        //manager.sendTextMessage(targetUser, message);
        // 刷新界面
        final Chat newchat = chat;
        RequestParams params = new RequestParams();
        params.addBodyParameter(AppConstants.PARAM_UID, AppServer.getInstance().getAccountInfo().getUid()+"");
        params.addBodyParameter("file",new File(comparesslocal));
        params.addBodyParameter("type",AppConstants.PARAM_UPLOAD_CHAT);
        String timestamp = URL.urlkey;
        params.addBodyParameter(AppConstants.PARAM_KEY, AppUtils.Md5(AppServer.getInstance().getAccountInfo().getUid()+""+timestamp));
        //params.addBodyParameter(AppConstants.PARAM_TIMESTAMP,TimeUtil.getCurrentTime());
        HttpUtils http = new HttpUtils();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        http.send(HttpRequest.HttpMethod.POST, info.getSysgw()+URL.UPLOADFILE,params, new RequestCallBack<String>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //返回图片url更新到数据库
                String result  = responseInfo.result;
                JsonParser parser = new JsonParser();
                JsonElement  elements = parser.parse(result);
                if(elements.isJsonArray()){
                    JsonArray  array = elements.getAsJsonArray();
                }else{
                    JsonObject object =	elements.getAsJsonObject();
                    String code =  object.get("code").getAsString();
                    JsonObject rejsonobject = object.get("result").getAsJsonObject();
                    String url = rejsonobject.get("url").getAsString();
                    System.out.println("url"+url);
                    String local = newchat.getContent()+"&"+url;
                    newchat.setContent(local);
                    System.out.println(url);
                    //删除压缩的文件
                    FileUtils.deleteFileByPath(comparesslocal);
                    AppServer.getInstance().sentChat(newchat.getUid(), newchat.getToid(), newchat.getContenttype(), url, new OnAppRequestListener() {

                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if(code == AppServer.REQUEST_SUCCESS){
                                int pmid = (Integer)obj;
                                System.out.println(pmid);
                                newchat.setPmid(pmid);
                                newchat.setStatus(0);
                                updateMessage(newchat);
                                chatdb.updateChat(newchat, creId);

                            }else{
                                newchat.setStatus(-1);
                                updateMessage(newchat);
                                chatdb.updateChat(newchat, creId);
                            }
                        }
                    });
                }

                //mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException error, String msg) {

                newchat.setStatus(-1);
                updateMessage(newchat);
                chatdb.updateChat(newchat, creId);
            }
        });

        refreshMessage(chat);

    }


    /**
     * 发送语音消息
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendVoiceMessage(String local,int length) {
        final ChatDb chatdb = new ChatDb(this);
        Chat chat = new Chat();
        chat.setContent(local);
        chat.setContenttype(AppConstants.TYPE_AUDIO);
        chat.setDate(TimeUtil.getYMDHMS());
        final int creId  = chatdb.getId();
        chat.setPmid(chatdb.getId());
        chat.setStatus(1); //1保存在本地，0是成功发送到服务器
        chat.setUid(info.getUid());
        chat.setToid(target.getUid());
        chat.setAvatar(AppServer.getInstance().getAccountInfo().getAvatar());
        chatdb.save(chat);
			/*//保存到最近会话中
			try {
				MessageDb mdb = new MessageDb();
				MessageRecent messagerecent = DbHelper.getDB(this).findFirst(Selector.from(MessageRecent.class).where("fromId","=",chat.getToid()));
				MessageRecent message = new MessageRecent(chat.getPmid()+"", target.getNickname(), chat.getDate(), chat.getToid()+"", chat.getUid()+"", chat.getContent(),chat.getContent(), "", "", mdb.getNewcount(target.getUid()), AppConstants.PUSH_ACTION_FRIENDS, AppConstants.PUSH_CONTENT_TYPE_AUDIO,target.getAvatar(),-1);
				if(messagerecent == null){
					mdb.save(message);
            	}else{
            		int count =messagerecent.getNewcount();
            		DbHelper.getDB(this).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagerecent.getMsgid()));;
            		//DbHelper.getDB(this).delete(messagerecent);
            		message.setNewcount(0);
            		DbHelper.getDB(this).save(message); //存入最近会话
            	}
			} catch (DbException e) {
				e.printStackTrace();
			}*/


        final Chat newchat = chat;
        RequestParams params = new RequestParams();
        params.addBodyParameter(AppConstants.PARAM_UID, AppServer.getInstance().getAccountInfo().getUid()+"");
        params.addBodyParameter("file",new File(local));
        params.addBodyParameter("type",AppConstants.PARAM_UPLOAD_AUDIO);
        String timestamp = URL.urlkey;
        params.addBodyParameter(AppConstants.PARAM_KEY, AppUtils.Md5(AppServer.getInstance().getAccountInfo().getUid()+""+timestamp));
        HttpUtils http = new HttpUtils();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        http.send(HttpRequest.HttpMethod.POST, info.getSysgw()+URL.UPLOADFILE,params, new RequestCallBack<String>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //返回图片url更新到数据库
                String result  = responseInfo.result;
                JsonParser parser = new JsonParser();
                JsonElement  elements = parser.parse(result);
                if(elements.isJsonArray()){
                    JsonArray  array = elements.getAsJsonArray();
                }else{
                    JsonObject object =	elements.getAsJsonObject();
                    String code =  object.get("code").getAsString();
                    JsonObject rejsonobject = object.get("result").getAsJsonObject();
                    String url = rejsonobject.get("url").getAsString();
                    newchat.setContent(url);
                    System.out.println(url);

                    AppServer.getInstance().sentChat(newchat.getUid(), newchat.getToid(), newchat.getContenttype(), newchat.getContent(), new OnAppRequestListener() {

                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if(code == AppServer.REQUEST_SUCCESS){
                                int pmid = (Integer)obj;
                                System.out.println(pmid);
                                newchat.setPmid(pmid);
                                newchat.setStatus(0);
                                updateMessage(newchat);
                                chatdb.updateChat(newchat, creId);

                            }else{

                            }
                        }
                    });
                }

                //mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException error, String msg) {

                //mAdapter.notifyDataSetChanged();
            }
        });

        refreshMessage(chat);

    }


    //录音计时线程
    void mythread(){
        recordThread = new Thread(ImgThread);
        recordThread.start();
    }


    //录音线程
    private Runnable ImgThread = new Runnable() {

        @Override
        public void run() {

            recodeTime = 0.0f;
            while (RECODE_STATE==RECORD_ING) {
                System.out.println("录音进行");
                if (recodeTime >= RecordManager.MAX_TIME && RecordManager.MAX_TIME != 0) {
                    imgHandle.sendEmptyMessage(0);
                }else{
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        if (RECODE_STATE == RECORD_ING) {
                            voiceValue = recordManager.getAmplitude();
                            imgHandle.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Handler imgHandle = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {

                switch (msg.what) {
                    case 0:
                        //录音超过15秒自动停止
                        if (RECODE_STATE == RECORD_ING) {
                            RECODE_STATE=RECODE_ED;
                            btn_speak.setPressed(false);
                            layout_record.setVisibility(View.INVISIBLE);
                            recordManager.stopRecording();
                            voiceValue = 0.0;
                            System.out.println("录音结束");
                            if (recodeTime < 1.0) {
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                                RECODE_STATE=RECORD_NO;
                            }
                        }
                        break;
                    case 1:
                        setDialogImage();
                        break;
                    default:
                        break;
                }

            }
        };
    };


    //录音Dialog图片随声音大小切换
    void setDialogImage(){
        if (voiceValue < 200.0) {
            iv_record.setImageResource(R.drawable.chat_icon_voice1);
        }else if (voiceValue > 200.0 && voiceValue < 400) {
            iv_record.setImageResource(R.drawable.chat_icon_voice2);
        }else if (voiceValue > 400.0 && voiceValue < 800) {
            iv_record.setImageResource(R.drawable.chat_icon_voice3);
        }else if (voiceValue > 800.0 && voiceValue < 1600) {
            iv_record.setImageResource(R.drawable.chat_icon_voice4);
        }else if (voiceValue > 1600.0 && voiceValue < 3200) {
            iv_record.setImageResource(R.drawable.chat_icon_voice5);
        }else if (voiceValue > 3200.0 && voiceValue < 5000) {
            iv_record.setImageResource(R.drawable.chat_icon_voice6);
        }
    }



    @Override
    public void onRefreshData(String title) {
        // TODO Auto-generated method stub

    }

    private String save(String path) {
        File file = new File(PATH);
        if(!file.exists()){
            file.mkdirs();// 创建文件夹
        }

        int degree = readPictureDegree(path);
        // Bitmap cbitmap=BitmapUtil.readBitMap(path);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) / 0.75);

        DecodeUtils decodeUtils = new DecodeUtils();
        Bitmap cbitmap=decodeUtils.decode(ChatActivity.this, Uri.parse(path), size, size);
        if(degree>0){
            cbitmap=rotaingImageView(degree, cbitmap);
        }

        File f = new File(path);
        name=f.getName();
        Boolean contents=false;
        File root=new File(path);
        File[] fils=root.listFiles();
        if(fils != null){
            for (File af : fils){
                if(af.isDirectory()){
                    af.getName().equals(name);
                    contents=true;
                    break;
                }
            }
        }
        if(!contents){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(new File(PATH,
                        name));
                int options = 100;
                while (baos.toByteArray().length / 1024 > 80 && options != 10) {
                    baos.reset();
                    cbitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    options -= 30;
                }

                fos.write(baos.toByteArray());
                fos.close();
                baos.close();
                cbitmap=null;
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return PATH+name;
    }


    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /*
       * 旋转图片
       * @param angle
       * @param bitmap
       * @return Bitmap
       */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(state.equals(AppConstants.CONTACTS_FRIENDREQUEST)){
                Intent intent=new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }else{
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent){
        if (intent.getExtras()!=null) {
            ArrayList<Photo> list = intent.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
            List<SimpleTask> tlist = new ArrayList<SimpleTask>();
            for(Photo p: list){
                tlist.add(getSendImageTask(p));
            }
            OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
            for(int i=0;i<tlist.size();i++){
                executor.put(tlist.get(i));
            }
            executor.start();
        }

    }

    public SimpleTask<String> getSendImageTask(final Photo photo){
        SimpleTask<String> task = new SimpleTask<String>() {

            @Override
            protected String doInBackground() {
                String pressImageUri= null;
                if(photo.imgPath!=null && !photo.imgPath.equals("")){
                    pressImageUri=save(photo.imgPath);
                }
                return pressImageUri;
            }

            @Override
            protected void onPostExecute(String result) {
                sendImageMessage(result,photo.imgPath);
            }
        };
        return task;
    }


    public String getToChatUsername() {
        return toChatUsername;
    }
}
