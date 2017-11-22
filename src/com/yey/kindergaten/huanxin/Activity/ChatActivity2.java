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
package com.yey.kindergaten.huanxin.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.GroupReomveListener;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.huanxin.adapter.ExpressionAdapter;
import com.yey.kindergaten.huanxin.adapter.ExpressionPagerAdapter;
import com.yey.kindergaten.huanxin.adapter.MessageAdapter;
import com.yey.kindergaten.huanxin.adapter.VoicePlayClickListener;
import com.yey.kindergaten.huanxin.utils.CommonUtils;
import com.yey.kindergaten.huanxin.utils.ImageUtils;
import com.yey.kindergaten.huanxin.utils.SmileUtils;
import com.yey.kindergaten.huanxin.widget.photoview.ExpandGridView;
import com.yey.kindergaten.huanxin.widget.photoview.PasteEditText;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.receive.PushReceiver;
import com.yey.kindergaten.service.HuanxinService;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 聊天页面
 *
 */
public class ChatActivity2 extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;
    public static final int RESULT_CODE_SAVE_PHOTO = 8;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;

    public static final String COPY_IMAGE = "EASEMOBIMG";
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    private PasteEditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    // private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
    private ImageView locationImgview;
    private View more;
    private int position;
    private ClipboardManager clipboard;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
    private Drawable[] micImages;
    private int chatType;
    private EMConversation conversation;
    private NewMessageBroadcastReceiver receiver;
    public static ChatActivity2 activityInstance = null;
    // 给谁发送消息
    private String toChatUsername;
    private VoiceRecorder voiceRecorder;
    private MessageAdapter adapter;
    private File cameraFile;
    static int resendPos;
    private String tousername = "";
    private String toChatAvatar = "";
    private GroupListener groupListener;

    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private Button btnMore;
    public String playMsgId;
    public  ImageView btn_left,btn_right;

    private String state;
    private EMMessage message;

    private boolean isSend = false; // 是否发送过消息

    public  final static String TAG = "ChatActivity2";

    public  static ChatActivity2 getActivityInstance;
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    private EMGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        initView();
        setUpView();
        getActivityInstance = this;
    }

    /**
     * initView
     */
    protected void initView() {
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        locationImgview = (ImageView) findViewById(R.id.btn_location);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (Button) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        more = findViewById(R.id.more);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        btn_left = (ImageView) findViewById(R.id.left_btn);
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setOnClickListener(this);
        btn_right = (ImageView) findViewById(R.id.right_btn);
        btn_right.setVisibility(View.GONE);
        btn_right.setOnClickListener(this);

        state = getIntent().getStringExtra("state");
        message = getIntent().getParcelableExtra("message");

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14), };

        // 表情list
        reslist = getExpressionRes(21);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        // View gv2 = getGridChildView(2);
        views.add(gv1);
        // views.add(gv2);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        edittext_layout.requestFocus();
        voiceRecorder = new VoiceRecorder(micImageHandler);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

        });

    }

    private void setUpView() {
        activityInstance = this;
        iv_emoticons_normal.setOnClickListener(this);
        iv_emoticons_checked.setOnClickListener(this);
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        // 判断单聊还是群聊
        // chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
        // if (chatType == CHATTYPE_SINGLE) { // 单聊
        toChatUsername = getIntent().getStringExtra("userId");

        tousername = getIntent().getStringExtra("nick");
        toChatAvatar = getIntent().getStringExtra("toChatAvatar");
        int role = getIntent().getIntExtra("role",0);
        int relations = 1;
        ((TextView) findViewById(R.id.header_title)).setText(tousername);
        // conversation = EMChatManager.getInstance().getConversation(toChatUsername, false);
//        } else {
            // 群聊
//            findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
//            findViewById(R.id.container_remove).setVisibility(View.GONE);
//            findViewById(R.id.container_voice_call).setVisibility(View.GONE);
//            toChatUsername = getIntent().getStringExtra("groupId");
//            group = EMGroupManager.getInstance().getGroup(toChatUsername);
//            ((TextView) findViewById(R.id.name)).setText(group.getGroupName());
//            // conversation =
//            // EMChatManager.getInstance().getConversation(toChatUsername,true);
//        }
        if (EMChat.getInstance().isLoggedIn()) {
            conversation = EMChatManager.getInstance().getConversation(toChatUsername);
            // 把此会话的未读数置为0
            conversation.resetUnreadMsgCount();
            adapter = new MessageAdapter(this, toChatUsername, chatType);
            adapter.setHeader_avatar(toChatAvatar);
            // 显示消息
            listView.setAdapter(adapter);
        }

        listView.setOnScrollListener(new ListScrollListener());
        int count = listView.getCount();
        if (count > 0) {
            listView.setSelection(count);
        }

        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });
        // 注册接收消息广播
        receiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(5);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个消息送达的BroadcastReceiver
        IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
        deliveryAckMessageIntentFilter.setPriority(5);
        registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);

        // 监听当前会话的群聊解散被T事件
        groupListener = new GroupListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupListener);

        // show forward message if the message is not null
        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
        if (forward_msg_id != null) {
            // 显示发送要转发的消息
            forwardMessage(forward_msg_id);
        }
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case RESULT_CODE_COPY: // 复制消息
                    EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
                    // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
                    // ((TextMessageBody) copyMsg.getBody()).getMessage()));
                    clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                    break;
                case RESULT_CODE_DELETE: // 删除消息
                    EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                    conversation.removeMessage(deleteMsg.getMsgId());
                    adapter.refresh();
                    listView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
                    break;
                case RESULT_CODE_FORWARD: // 转发消息
//                  EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
//                  Intent intent = new Intent(this, ForwardMessageActivity.class);
//                  intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
//                  startActivity(intent);
                    break;
                case RESULT_CODE_SAVE_PHOTO: // 保存图片
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    String time = null;
                    Date d1 = new Date(System.currentTimeMillis());
                    time = format.format(d1);
                    String photoName = time + ".png";
                    // 创建目录
                    createSDCardDir();
                    EMMessage savePhotoMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));

                    // 接收方向的消息
                    if (savePhotoMsg.direct == EMMessage.Direct.RECEIVE) {
                        // "it is receive msg";
                        if (savePhotoMsg.status!=EMMessage.Status.INPROGRESS) {
                            ImageMessageBody imgBody = (ImageMessageBody) savePhotoMsg.getBody();
                            if (imgBody.getLocalUrl()!=null) {
                                // String filePath = imgBody.getLocalUrl();
                                String remotePath = imgBody.getRemoteUrl();
                                String filePath = ImageUtils.getImagePath(remotePath);
                                String thumbRemoteUrl = imgBody.getThumbnailUrl();
                                String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
//                                if (thumbnailPath!=null && new File(thumbnailPath).exists()) {
                                UtilsLog.i(TAG, "photo path is : " + filePath);
                                if (thumbnailPath!=null && !thumbnailPath.contains("http")) {
                                    thumbnailPath = "file:///" + thumbnailPath;
                                }
                                savePhoto(thumbnailPath == null ? "" : thumbnailPath);
//                                } else {
//                                    showToast("图片出错");
//                                }
                            }
                        }
                    } else {
                        ImageMessageBody imgBody = (ImageMessageBody) savePhotoMsg.getBody();
                        String filePath = imgBody.getLocalUrl();
                        if (new File(filePath).exists()) {
                            UtilsLog.i(TAG, "photo path is : " + filePath);
                            if (!filePath.contains("http")) {
                                filePath = "file:///" + filePath;
                            }
                            savePhoto(filePath);
                        } else {
                            showToast("图片出错");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                EMChatManager.getInstance().clearConversation(toChatUsername);
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
                    isSend = true;
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频
                int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                Bitmap bitmap = null;
                FileOutputStream fos = null;
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                    if (bitmap == null) {
                        EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
                    }
                    fos = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.JPEG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fos = null;
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
                sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);
                isSend = true;
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                        isSend = true;
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFile(uri);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    more(more);
                    sendLocationMsg(latitude, longitude, "", locationAddress);
                } else {
                    Toast.makeText(this, "无法获取到您的位置信息！", Toast.LENGTH_SHORT).show();
                }
                // 重发消息
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
                    || requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
                resendMessage();
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                // 粘贴
                if (!TextUtils.isEmpty(clipboard.getText())) {
                    String pasteText = clipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        // 把图片前缀去掉，还原成正常的path
                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
                    }
                }
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
                EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
                addUserToBlacklist(deleteMsg.getFrom());
            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                setResult(RESULT_OK);
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
    }

    public void savePhoto(String photoPath) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = null;
        Date d1 = new Date(System.currentTimeMillis());
        time = format.format(d1);
        String photoName = time + ".png";
        // 创建目录
        createSDCardDir();
        // 保存图片
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(photoPath);
        if (bitmap!=null) {
            save(bitmap, photoName);
            bitmap = null;
            scanPhoto(AppConstants.SAVE_PHOTO_PATH + photoName);
            Toast.makeText(ChatActivity2.this, "保存成功！ " + "图片已存至 " + AppConstants.SAVE_PHOTO_PATH + photoName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ChatActivity2.this, "保存失败！", Toast.LENGTH_LONG).show();
        }
    }

    // 在SD卡上创建一个文件夹
    public void createSDCardDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // 创建一个文件夹对象，赋值为外部存储器的目录
            String path = AppConstants.SAVE_PHOTO_PATH;
            File path1 = new File(path);
            if (!path1.exists()) {
                // 若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }
        } else {
            setTitle("false");
            return;
        }
    }

    /**
     * 保存图片的方法
     * @param bm
     */
    private void save(Bitmap bm, String photoName) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            FileOutputStream fos = new FileOutputStream(new File(AppConstants.SAVE_PHOTO_PATH, photoName));
            int options = 100;

            while (baos.toByteArray().length / 1024 > 80 && options != 10) {

                baos.reset();

                bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 30;
            }
            fos.write(baos.toByteArray());
            fos.close();
            baos.close();
            bm = null;

        } catch (Exception e) {
            UtilsLog.i(TAG, "saveBitmap Exception");
        }
    }

    /**
     * 刷新本地图片，能及时在本地相册中看到
     * 调用系统扫描文件类
     * @param imgFileName
     */
    private void scanPhoto(String imgFileName) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    /**
     * 消息图标点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_send) { // 点击发送按钮(发文字和表情)
            String s = mEditTextContent.getText().toString();
            if (EMChat.getInstance().isLoggedIn()) {
                sendText(s);
            } else {
                startHuanxinService(AppConstants.HUANXIN_LOGIN);
            }
            isSend = true;
        } else if (id == R.id.btn_take_picture) {
            selectPicFromCamera(); // 点击照相图标
        } else if (id == R.id.btn_picture) {
            selectPicFromLocal(); // 点击图片图标
        } else if (id == R.id.btn_location) { // 位置
//          startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
            more.setVisibility(View.VISIBLE);
            iv_emoticons_normal.setVisibility(View.INVISIBLE);
            iv_emoticons_checked.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.GONE);
            emojiIconContainer.setVisibility(View.VISIBLE);
            hideKeyboard();
        } else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
        } else if (id == R.id.btn_video) {
            // 点击摄像图标
            Intent intent = new Intent(ChatActivity2.this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
        } else if (id == R.id.btn_file) { // 点击文件图标
//            selectFileFromLocal();
        } else if (id == R.id.btn_voice_call) { // 点击语音电话图标
//            if (!EMChatManager.getInstance().isConnected())
//                Toast.makeText(this, "尚未连接至服务器，请稍后重试", Toast.LENGTH_SHORT).show();
//            else
//                startActivity(new Intent(ChatActivity2.this, VoiceCallActivity.class).putExtra("username", toChatUsername).putExtra( "isComingCall", false));
        } else if (id == R.id.left_btn) {
            finish();;
        } else if (id == R.id.right_btn) {
            startActivityForResult( new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", "是否清空所有聊天记录").putExtra("cancel", true),
                    REQUEST_CODE_EMPTY_HISTORY);
        }
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), AppContext.getInstance().getUserName()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
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

    /**
     * 启动环信服务
     */
    private void startHuanxinService(String state) {
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        Intent serviceIntent = new Intent(ChatActivity2.this, HuanxinService.class);
        serviceIntent.putExtra(AppConstants.PARAM_UID, info.getUid());
        serviceIntent.putExtra(AppConstants.PARAM_PASSWORD, info.getPassword());
        serviceIntent.putExtra("state", state);
        serviceIntent.putExtra("relation", info.getRelationship());
        ChatActivity2.this.startService(serviceIntent);
    }

    @Override
    protected void onStop() {
        if (isSend) {
            UtilsLog.i(TAG,"postEvent to homefragment refresh");
            postEvent(AppEvent.HOMEFRAGMENT_REFRESH_CHAT);
        }
        super.onStop();
    }

    /**
     * 选择文件
     */
    private void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 发送文本消息
     *
     * @param content
     *            message content
     * boolean resend
     */
    private void sendText(String content) {
        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(ChatType.GroupChat);
            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            message.setAttribute("avatar", AppServer.getInstance().getAccountInfo().getAvatar() == null ? "" : AppServer.getInstance().getAccountInfo().getAvatar());
            message.setAttribute("nick", AppServer.getInstance().getAccountInfo().getRealname() == null ? "" : AppServer.getInstance().getAccountInfo().getRealname());
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);
            // 把messgage加到conversation中

            conversation.addMessage(message);
            handleWeChat(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refresh();

            listView.setSelection(listView.getCount() - 1);
            mEditTextContent.setText("");

            setResult(RESULT_OK);
        }
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(ChatType.GroupChat);
            message.setReceipt(toChatUsername);
            int len = Integer.parseInt(length);
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
            message.addBody(body);
            message.setAttribute("avatar", AppServer.getInstance().getAccountInfo().getAvatar() == null ? "" : AppServer.getInstance().getAccountInfo().getAvatar());
            message.setAttribute("nick", AppServer.getInstance().getAccountInfo().getRealname() == null ? "" : AppServer.getInstance().getAccountInfo().getRealname());

            conversation.addMessage(message);
            handleWeChat(message);
            adapter.refresh();
            listView.setSelection(listView.getCount() - 1);
            setResult(RESULT_OK);
            // send file
            // sendVoiceSub(filePath, fileName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        String to = toChatUsername;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        message.setAttribute("avatar", AppServer.getInstance().getAccountInfo().getAvatar() == null ? "" : AppServer.getInstance().getAccountInfo().getAvatar());
        message.setAttribute("nick", AppServer.getInstance().getAccountInfo().getRealname() == null ? "" : AppServer.getInstance().getAccountInfo().getRealname());
        conversation.addMessage(message);
        handleWeChat(message);
        listView.setAdapter(adapter);
        adapter.refresh();
        listView.setSelection(listView.getCount() - 1);
        setResult(RESULT_OK);
        // more(more);
    }

    /**
     * 发送视频消息
     */
    private void sendVideo(final String filePath, final String thumbPath, final int length) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(ChatType.GroupChat);
            String to = toChatUsername;
            message.setReceipt(to);
            VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
            message.addBody(body);
            message.setAttribute("avatar", AppServer.getInstance().getAccountInfo().getAvatar() == null ? "" : AppServer.getInstance().getAccountInfo().getAvatar());
            message.setAttribute("nick", AppServer.getInstance().getAccountInfo().getRealname() == null ? "" : AppServer.getInstance().getAccountInfo().getRealname());
            conversation.addMessage(message);
            handleWeChat(message);
            listView.setAdapter(adapter);
            adapter.refresh();
            listView.setSelection(listView.getCount() - 1);
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;
            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(file.getAbsolutePath());
        }
    }

    /**
     * 发送位置信息
     *
     * @param latitude
     * @param longitude
     * @param imagePath
     * @param locationAddress
     */
    private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
        message.addBody(locBody);
        message.setReceipt(toChatUsername);
        conversation.addMessage(message);
        handleWeChat(message);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount() - 1);
        setResult(RESULT_OK);
    }

    /**
     * 发送文件
     *
     * @param uri
     */
    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getApplicationContext(), "文件不能大于10M", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        message.setReceipt(toChatUsername);
        message.setAttribute("avatar", AppServer.getInstance().getAccountInfo().getAvatar() == null ? "" : AppServer.getInstance().getAccountInfo().getAvatar());
        message.setAttribute("nick", AppServer.getInstance().getAccountInfo().getRealname() == null ? "" : AppServer.getInstance().getAccountInfo().getRealname());
        // add message body
        NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
        message.addBody(body);
        conversation.addMessage(message);
        handleWeChat(message);
        listView.setAdapter(adapter);
        adapter.refresh();
        listView.setSelection(listView.getCount());
        setResult(RESULT_OK);
    }

    /**
     * 重发消息
     */
    private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;

        adapter.refresh();
        listView.setSelection(resendPos);
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        emojiIconContainer.setVisibility(View.GONE);
    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        // mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
        // {
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // if(hasFocus){
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // }
        // }
        // });
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击进入群组详情
     *
     * @param view
     */
    public void toGroupDetails(View view) {
//        startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
//                REQUEST_CODE_GROUP_DETAIL);
    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void more(View view) {
        if (more.getVisibility() == View.GONE) {
            System.out.println("more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
        } else {
            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 点击文字输入框
     *
     * @param v
     */
    public void editClick(View v) {
        listView.setSelection(listView.getCount() - 1);
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 消息广播接收者
     *
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 记得把广播给终结掉
            isSend = true;

            String username = intent.getStringExtra("from");
            String msgid = intent.getStringExtra("msgid");

            if (username!=null && username.contains("b")) { // 环信username中,包含a表示聊天用，包含b表示离园提醒消息

            } else {
                AppContext.getInstance().getMediaPlayer().start();
                long mill = 500;
                AppContext.Vibrate(ChatActivity2.this,mill);
                abortBroadcast();
            }

            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgid);
            String from = "";
            String to = "";
            if (message.getFrom().length() > 2 && message.getTo().length() > 2) {
                from = message.getFrom().substring(0, message.getFrom().length() - 2);
                to = message.getTo().substring(0, message.getTo().length() - 2);
            }
            String nick = null;
            String avatar = null;
            MessageRecent newMessagePublic = null;
            try {
                avatar = message.getStringAttribute("avatar") == null ? "" : message.getStringAttribute("avatar");
                nick = message.getStringAttribute("nick") == null ? "" : message.getStringAttribute("nick");
            } catch (EaseMobException e) {
                Teacher teacher = DbHelper.findAvatarById(from);
                if (teacher!=null) {
                    avatar = teacher.getAvatar();
                    nick = teacher.getRealname();
                }
            }
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(toChatUsername)) {
                // 消息不是发给当前会话，return
                showNotify(R.drawable.ic_launcher,message);
                return;
            }
            switch (message.getType()) {
                case TXT:
                    TextMessageBody txtBody = (TextMessageBody)message.getBody();
                    newMessagePublic  = new MessageRecent(message.getMsgId(), nick, TimeUtil.getMoreTime(message.getMsgTime()), from , to, txtBody.getMessage(),txtBody.getMessage(), "", "", 0, AppConstants.PUSH_ACTION_FRIENDS, 0,avatar,0, message.getFrom(),message.getTo());
                    break;
                case IMAGE:
                    ImageMessageBody imgBody = (ImageMessageBody)message.getBody();
                               /* Log.d("img message from:" + mess.getFrom() + " thumbnail:" + imgBody.getThumbnailUrl()
                                        + " remoteurl:" + imgBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(message.getMsgId(), nick, TimeUtil.getMoreTime(message.getMsgTime()), from , to, imgBody.getRemoteUrl(), imgBody.getRemoteUrl(), "", "", 0, AppConstants.PUSH_ACTION_FRIENDS, 1, avatar, 0, message.getFrom(), message.getTo());
                    break;
                case VOICE:
                    VoiceMessageBody voiceBody = (VoiceMessageBody)message.getBody();
                              /*  Log.d("voice message from:" + message.getFrom() + " length:" + voiceBody.getLength()
                                        + " remoteurl:" + voiceBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(message.getMsgId(), nick, TimeUtil.getMoreTime(message.getMsgTime()), from , to, voiceBody.getRemoteUrl(), voiceBody.getRemoteUrl(), "", "", 0, AppConstants.PUSH_ACTION_FRIENDS, 2, avatar, 0, message.getFrom(), message.getTo());
                    break;
                case LOCATION:
                    LocationMessageBody locationBody = (LocationMessageBody)message.getBody();
                             /*   Log.d("location message from:" + message.getFrom() + " address:" + locationBody.getAddress() +" \n\r");*/
                    break;
                case VIDEO:
                    VideoMessageBody videoMessageBody = (VideoMessageBody)message.getBody();
                    newMessagePublic  = new MessageRecent(message.getMsgId(), nick, TimeUtil.getMoreTime(message.getMsgTime()), from , to, videoMessageBody.getRemoteUrl(), videoMessageBody.getRemoteUrl(), "", "", 1, AppConstants.PUSH_ACTION_FRIENDS, 3, avatar, 0, message.getFrom(), message.getTo());
                    break;

            }
            PushReceiver.mNewNum = 0;
            if (toChatUsername.equals(username)) {
                try {
                    MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("hxfrom", "=", newMessagePublic.getHxfrom()).and("hxto", "=", newMessagePublic.getHxto()).and("action", "=", 0));
                    if (messagePublic == null) {
                        DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                    } else { // 不是好友聊天
                        int count = messagePublic.getNewcount();
                        DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
                        DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            adapter.refresh();
            listView.setSelection(listView.getCount());
        }
    }

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
            if (conversation!=null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    msg.isAcked = true;
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * 消息送达BroadcastReceiver
     */
    private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation!=null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    msg.isDelivered = true;
                }
            }
            adapter.notifyDataSetChanged();
        }
    };
    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     *
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        Toast.makeText(ChatActivity2.this, "发送语音需要sdcard支持！", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ChatActivity2.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint.setText(getString(R.string.release_to_cancel));
                        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();
                    } else {
                        // stop recording and send voice file
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
                                        Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), "无录音权限", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity2.this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
//      if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
//      } else if (i == 2) {
//          list.addAll(reslist.subList(20, reslist.size()));
//      }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {
                        if (filename != "delete_expression") { // 不是删除键，显示表情
                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                            Class clz = Class.forName("com.yey.kindergaten.huanxin.utils.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity2.this, (String) field.get(null)));
                        } else { // 删除文字或者表情
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {
                                int selectionStart = mEditTextContent.getSelectionStart(); // 获取光标的位置
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText().toString();
                                    String tempStr = body.substring(0, selectionStart);
                                    int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            mEditTextContent.getEditableText().delete(i, selectionStart);
                                        else
                                            mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    UtilsLog.i(TAG, "getGridChildView Exception");
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 0; x <= getSum; x++) {
            String filename = "face" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
        // 注销广播
        try {
            unregisterReceiver(receiver);
            receiver = null;
        } catch (Exception e) {
            UtilsLog.i(TAG, "unregisterReceiver receiver Exception");
        }
        try {
            unregisterReceiver(ackMessageReceiver);
            ackMessageReceiver = null;
            unregisterReceiver(deliveryAckMessageReceiver);
            deliveryAckMessageReceiver = null;
        } catch (Exception e) {
            UtilsLog.i(TAG, "unregisterReceiver ackMessageReceiver or deliveryAckMessageReceiver  Exception");
        }
    }

    @Override
    protected void onResume() {
        if (state!=null && message!=null) {
            handleChat(message);
            if (adapter!=null) {
                adapter.refresh();
            }
            listView.setSelection(listView.getCount());
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld())
            wakeLock.release();
        if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
            // 停止语音播放
            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }
        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            UtilsLog.i(TAG, "停止录音 Exception");
        }
    }

    /** 隐藏软键盘 */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 加入到黑名单
     *
     * @param username
     */
    private void addUserToBlacklist(String username) {
        try {
            EMContactManager.getInstance().addUserToBlackList(username, false);
            Toast.makeText(getApplicationContext(), "移入黑名单成功",Toast.LENGTH_SHORT).show();
        } catch (EaseMobException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "移入黑名单失败", Toast.LENGTH_SHORT).show();
        }
    }

    /** 覆盖手机返回键 */
    @Override
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    /** listview滑动监听listener */
    private class ListScrollListener implements OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                        loadmorePB.setVisibility(View.VISIBLE);
                        // sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
                        List<EMMessage> messages;
                        try {
                            // 获取更多messges，调用此方法的时候从db获取的messages
                            // sdk会自动存入到此conversation中
                            if (chatType == CHATTYPE_SINGLE)
                                messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
                            else
                                messages = conversation.loadMoreGroupMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
                        } catch (Exception e1) {
                            loadmorePB.setVisibility(View.GONE);
                            return;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            UtilsLog.i(TAG, "sleep InterruptedException");
                        }
                        if (messages.size() != 0) {
                            // 刷新ui
                            adapter.notifyDataSetChanged();
                            listView.setSelection(messages.size() - 1);
                            if (messages.size() != pagesize)
                                haveMoreData = false;
                        } else {
                            haveMoreData = false;
                        }
                        loadmorePB.setVisibility(View.GONE);
                        isloading = false;
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    /**
     * 转发消息
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                // 获取消息内容，发送消息
                String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
                sendText(content);
                break;
            case IMAGE:
                // 发送图片
                String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // 不存在大图发送缩略图
                        filePath = ImageUtils.getThumbnailImagePath(filePath);
                    }
                    sendPicture(filePath);
                }
                break;
            default:
                break;
        }
    }

    /** 监测群组解散或者被T事件 */
    class GroupListener extends GroupReomveListener {
        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (toChatUsername.equals(groupId)) {
//                      Toast.makeText(ChatActivity.this, "你被群创建者从此群中移除", 1).show();
//                      if (GroupDetailsActivity.instance != null)
//                          GroupDetailsActivity.instance.finish();
//                      finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            runOnUiThread(new Runnable() {
                public void run() {
                    if (toChatUsername.equals(groupId)) {
//                        Toast.makeText(ChatActivity.this, "当前群聊已被群创建者解散", Toast.LENGTH_SHORT).show();
//                        if (GroupDetailsActivity.instance != null)
//                            GroupDetailsActivity.instance.finish();
//                        finish();
                    }
                }
            });
        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    private void handleChat( final EMMessage mess) {
        String from = "";
        String to = "";
        if (mess.getFrom().length() > 2 && mess.getTo().length() > 2) {
            from = mess.getFrom().substring(0, mess.getFrom().length() - 2);
            to = mess.getTo().substring(0, mess.getTo().length() - 2);
        }
        AccountInfo info2 =  AppContext.getInstance().getAccountInfo();
        MessageRecent newMessagePublic = null;
        try {
            String nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
            String avatar = mess.getStringAttribute("avatar") == null ? "" : mess.getStringAttribute("avatar");

            switch (mess.getType()) {
                case TXT:
                    TextMessageBody txtBody = (TextMessageBody)mess.getBody();
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()), from , to, txtBody.getMessage(), txtBody.getMessage(), "", "", 1, AppConstants.PUSH_ACTION_FRIENDS, 0, avatar, 0, mess.getFrom(), message.getTo());
                    break;
                case IMAGE:
                    ImageMessageBody imgBody = (ImageMessageBody)mess.getBody();
                               /* Log.d("img message from:" + mess.getFrom() + " thumbnail:" + imgBody.getThumbnailUrl()
                                        + " remoteurl:" + imgBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()), from , to ,  imgBody.getRemoteUrl(), imgBody.getRemoteUrl(), "", "", 1, AppConstants.PUSH_ACTION_FRIENDS, 1, avatar, 0, mess.getFrom(), message.getTo());
                    break;
                case VOICE:
                    VoiceMessageBody voiceBody = (VoiceMessageBody)mess.getBody();
                              /*  Log.d("voice message from:" + message.getFrom() + " length:" + voiceBody.getLength()
                                        + " remoteurl:" + voiceBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getMoreTime(mess.getMsgTime()), from , to , voiceBody.getRemoteUrl(), voiceBody.getRemoteUrl(), "", "", 1, AppConstants.PUSH_ACTION_FRIENDS, 2, avatar, 0, mess.getFrom(), message.getTo());
                    break;
                case LOCATION:
                    LocationMessageBody locationBody = (LocationMessageBody)mess.getBody();
                             /*   Log.d("location message from:" + message.getFrom() + " address:" + locationBody.getAddress() +" \n\r");*/
                    break;

            }
            try {
                MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("hxfrom", "=", newMessagePublic.getHxfrom()).and("hxto", "=", newMessagePublic.getHxto()).and("action", "=", 0));
                if (messagePublic == null) {
                    DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                } else { // 不是好友聊天
                    int count =messagePublic.getNewcount();
                    DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
                    newMessagePublic.setNewcount(count + 1);
                    DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                }
//                else {
//                    if (newMessagePublic.getAction() == 0) { // 好友聊天
//                        if (messagePublic.getHxfrom().equals(newMessagePublic.getHxfrom())) { // 相等表示用户身份一致
//                            int count =messagePublic.getNewcount();
//                            DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
//                            newMessagePublic.setNewcount(0);
//                            DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
//                        } else { // 身份不一致
//                            List<MessageRecent>list = DbHelper.getDB(AppContext.getInstance()).findAll(MessageRecent.class,WhereBuilder.b("action","=",0));
//                            boolean exsit = true;
//                            if (list!=null && list.size()!=0) {
//                                for (MessageRecent recent : list) {
//                                    if (recent.getHxfrom().equals(newMessagePublic.getHxfrom())) {
//                                        int count = recent.getNewcount();
//                                        DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", recent.getMsgid()));
//                                        newMessagePublic.setNewcount(0);
//                                        DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
//                                        exsit = true;
//                                        break;
//                                    } else {
//                                        exsit = false;
//                                    }
//                                }
//                            }
//                            if (!exsit) {
//                                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
//                            }
//                        }
//                    } else { // 不是好友聊天
//                        int count =messagePublic.getNewcount();
//                        DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
//                        newMessagePublic.setNewcount(count+1);
//                        DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
//                    }
//                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    };

    private void handleWeChat( final EMMessage mess) {
        String from = "";
        String to = "";
        if (mess.getFrom().length() > 2 && mess.getTo().length() > 2) {
            from = mess.getFrom().substring(0, mess.getFrom().length() - 2);
            to = mess.getTo().substring(0, mess.getTo().length() - 2);
        }
        // String from = mess.getFrom().substring(0,mess.getFrom().length()-2);
        // String to = mess.getTo().substring(0,mess.getTo().length()-2);
        AccountInfo info2 =  AppContext.getInstance().getAccountInfo();
        String name = tousername;
        if (name.contains("(")) {
            name = name.substring(0, name.lastIndexOf("("));
        }
        MessageRecent newMessagePublic = null;
        try {
            String nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
            String avatar = mess.getStringAttribute("avatar") == null ? "" : mess.getStringAttribute("avatar");

            switch (mess.getType()) {
                case TXT:
                    TextMessageBody txtBody = (TextMessageBody)mess.getBody();
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), name, TimeUtil.getMoreTime(mess.getMsgTime()), to, from , txtBody.getMessage(), txtBody.getMessage(), "", "", 0, AppConstants.PUSH_ACTION_FRIENDS, 0, toChatAvatar, 0, mess.getTo(), mess.getFrom());
                    break;
                case IMAGE:
                    ImageMessageBody imgBody = (ImageMessageBody)mess.getBody();
                               /* Log.d("img message from:" + mess.getFrom() + " thumbnail:" + imgBody.getThumbnailUrl()
                                        + " remoteurl:" + imgBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), name, TimeUtil.getMoreTime(mess.getMsgTime()), to, from ,  imgBody.getRemoteUrl(), imgBody.getRemoteUrl(), "", "", 0, AppConstants.PUSH_ACTION_FRIENDS, 1, toChatAvatar, 0, mess.getTo(), mess.getFrom());
                    break;
                case VOICE:
                    VoiceMessageBody voiceBody = (VoiceMessageBody)mess.getBody();
                              /*  Log.d("voice message from:" + message.getFrom() + " length:" + voiceBody.getLength()
                                        + " remoteurl:" + voiceBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), name, TimeUtil.getMoreTime(mess.getMsgTime()), to, from , voiceBody.getRemoteUrl(), voiceBody.getRemoteUrl(), "", "", 0, AppConstants.PUSH_ACTION_FRIENDS, 2, toChatAvatar, 0, mess.getTo(), mess.getFrom());
                    break;
                case LOCATION:
                    LocationMessageBody locationBody = (LocationMessageBody)mess.getBody();
                             /*   Log.d("location message from:" + message.getFrom() + " address:" + locationBody.getAddress() +" \n\r");*/
                    break;

            }
            try {
                MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("toId", "=", from).and("action", "=", 0));
                if (messagePublic == null) {
                    DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                } else {
                    if (newMessagePublic!=null && newMessagePublic.getAction() == 0) { // 好友聊天
                        if (messagePublic.getHxfrom().equals(newMessagePublic.getHxfrom())) { // 相等表示用户身份一致
                            int count = messagePublic.getNewcount();
                            DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
                            newMessagePublic.setNewcount(0);
                            DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
                        } else { // 身份不一致
                            List<MessageRecent>list = DbHelper.getDB(AppContext.getInstance()).findAll(MessageRecent.class,WhereBuilder.b("action", "=", 0));
                            boolean exsit = true;
                            if (list!=null && list.size()!=0) {
                                for (MessageRecent recent : list) {
                                    if (recent.getHxfrom().equals(newMessagePublic.getHxfrom())) {
                                        int count = recent.getNewcount();
                                        DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", recent.getMsgid()));
                                        newMessagePublic.setNewcount(0);
                                        DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
                                        exsit = true;
                                        break;
                                    } else {
                                        exsit = false;
                                    }
                                }
                            }
                            if (!exsit) {
                                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                            }
                        }
                    } else { // 不是好友聊天
                        int count = messagePublic.getNewcount();
                        DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));
                        if (newMessagePublic!=null) {
                            newMessagePublic.setNewcount(count + 1);
                        }
                        DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    };

}
