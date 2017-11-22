package com.yey.kindergaten.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.RelationShipBean;
import com.yey.kindergaten.bean.SelfInfo;
import com.yey.kindergaten.db.DBManager;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.huanxin.Activity.ChatActivity2;
import com.yey.kindergaten.huanxin.Constant;
import com.yey.kindergaten.huanxin.bean.User;
import com.yey.kindergaten.huanxin.db.UserDao;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.IntArraySortUtil;
import com.yey.kindergaten.util.StringUtils;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactFriendDatacardActivity extends BaseActivity implements OnClickListener {

    TextView titletextview;   //通讯录
    ImageView leftbtn;
    ImageView rightbtn;
    String state = "";
    String birthday = "";
    // String childrenTextPhone = "";
    // String childrenImg = "";
    int role;
    int targetid;
    int cid;
    RelativeLayout kgrly;
    RelativeLayout sexrly;
    RelativeLayout regionrly;
    LinearLayout delcontactly;
    LinearLayout sendmessagely;
    TextView delfriendbtn;
    View kgrlyview;
    View sexrlyview;
    View regionrlyview;
    View birthdayview;

    TextView nametv;
    TextView numbertv;
    CircleImageView headimage;
    TextView sextv;
    TextView regiontv;
    TextView realnametv;
    TextView birthday_tv;
    TextView phonetv;
    SelfInfo selfInfo;
    Friend friend;
    AccountInfo accountInfo;
    PublicAccount puacAccount;
    AppContext appcontext = null;
    private DBManager dbm;
    private SQLiteDatabase sqlite;
    boolean isfriend;

    private int hxState;
    List<Integer> integersrelation = new ArrayList<Integer>();
    String strings[];
    String phonedialog[] = {"打电话", "发短信"};
    private int[] relation_number = {0, 1, 2, 3, 4, 99};
    private String[] relation_name = {"爸爸", "妈妈", "爷爷", "奶奶", "外公", "外婆", "叔叔", "阿姨"};
    RelationAdapter adapter;
    private final static String TAG = "ContactFriendDatacardActivity";
    private List<SelfInfo.RelationShip> checkList = new ArrayList<SelfInfo.RelationShip>();
    private List<Integer> relationlist = new ArrayList<Integer>();

    @ViewInject(R.id.common_network_disable) LinearLayout layout_networkdisable;
    @ViewInject(R.id.srcoll_card_view) ScrollView scrollview;
    @ViewInject(R.id.common_loading) LinearLayout layout_loading;
    @ViewInject(R.id.common_error) LinearLayout layout_error;
    @ViewInject(R.id.common_empty) LinearLayout layout_empty;
    @ViewInject(R.id.network_disable_button_relink) ToggleButton nettogButton;
    @ViewInject(R.id.contact_frienddatacard_sendmessagebtn) TextView msg_tv;
    @ViewInject(R.id.id_show_relationship_select_lv) ListView relation_lv;
    @ViewInject(R.id.contact_frienddatacard_phonely) RelativeLayout callphone_rl;
    @ViewInject(R.id.contact_frienddatacard_birthday) RelativeLayout birthday_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_frienddatacard);
        ViewUtils.inject(this);
        appcontext = AppContext.getInstance();
        accountInfo = AppServer.getInstance().getAccountInfo();
        if (getIntent().getExtras() != null) {
            state = getIntent().getExtras().getString("state");
            if (state.equals(AppConstants.CONTACTS_ADDFRIEND)) {
                friend = (Friend) getIntent().getExtras().getSerializable("friend");
                role = getIntent().getExtras().getInt("role");
                targetid = getIntent().getExtras().getInt("targetid");
            } else {
                role = getIntent().getExtras().getInt("role");
                targetid = getIntent().getExtras().getInt("targetid");
                cid = getIntent().getExtras().getInt(AppConstants.PARAM_CID);
                birthday = getIntent().getExtras().getString("birthday");
                UtilsLog.i(TAG, "get intent birthday :" + birthday);
                // childrenTextPhone = getIntent().getExtras().getString("childrenTextPhone");
                // childrenImg = getIntent().getExtras().getString("childrenImg");
            }
        }
        FindViewById();
        initData();
        setOnClick();
    }

    public void FindViewById() {
        titletextview = (TextView) findViewById(R.id.header_title);
        leftbtn = (ImageView) findViewById(R.id.left_btn);
        leftbtn.setVisibility(View.VISIBLE);
        kgrly = (RelativeLayout) findViewById(R.id.contact_frienddatacard_kgrly);
        sexrly = (RelativeLayout) findViewById(R.id.contact_frienddatacard_sexrly);
        regionrly = (RelativeLayout) findViewById(R.id.contact_frienddatacard_regionrly);
        delfriendbtn = (TextView) findViewById(R.id.contact_frienddatacard_deletecontactbtn);
        delcontactly = (LinearLayout) findViewById(R.id.contact_frienddatacard_deletecontactly);
        sendmessagely = (LinearLayout) findViewById(R.id.contact_frienddatacard_sendly);
        kgrlyview = findViewById(R.id.contact_frienddatacard_kgrview);
        sexrlyview = findViewById(R.id.contact_frienddatacard_sexrview);
        // regionrlyview = findViewById(R.id.contact_frienddatacard_regionrview);
        birthdayview = findViewById(R.id.contact_friendbirthdaycard_sexrview);

        birthday_tv = (TextView) findViewById(R.id.contact_friendtext_birthday);
        nametv = (TextView) findViewById(R.id.contact_frienddatacard_nametv);
        numbertv = (TextView) findViewById(R.id.contact_frienddatacard_numbertv);
        headimage = (CircleImageView) findViewById(R.id.contact_frienddatacard_iv);
        sextv = (TextView) findViewById(R.id.contact_frienddatacard_sextv);
        regiontv = (TextView) findViewById(R.id.contact_frienddatacard_regiontv);
        realnametv = (TextView) findViewById(R.id.contact_frienddatacard_realnametv);
        phonetv = (TextView) findViewById(R.id.contact_frienddatacard_phonetv);
        rightbtn = (ImageView) findViewById(R.id.lookdata_btn);
        rightbtn.setImageResource(R.drawable.trash);
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        if (info.getRole() == 0 && state.equals(AppConstants.CONTACTS_PARENT)) {
            // 园长发通知
        }
    }

    public void initData() {
        showLoadingDialog("正在加载");
        // 资料卡的人查询现在的人的角色
        AppServer.getInstance().viewInfo(accountInfo.getUid() + "", targetid + "", 0 + "", role, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == 0) {
                    selfInfo = (SelfInfo) obj;
                    if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                        if (selfInfo.getRole() == AppConstants.DIRECTORROLE || selfInfo.getRole() == AppConstants.TEACHERROLE) {
                            msg_tv.setText("发消息");
                        } else {
                            msg_tv.setText("发通知");
                        }
                    }
                } else if (code == AppServer.REQUEST_NETWORK_ERROR) {
                    layout_networkdisable.setVisibility(View.VISIBLE);
                    scrollview.setVisibility(View.GONE);
                    titletextview.setText("暂无网络");
                } else if (code == AppServer.REQUEST_CLIENT_ERROR) {
                    layout_error.setVisibility(View.GONE);
                    scrollview.setVisibility(View.GONE);
                } else {
                    layout_empty.setVisibility(View.VISIBLE);
                    scrollview.setVisibility(View.GONE);
                }
                cancelLoadingDialog();
                initView();
            }
        });
    }

    public void initView() {
        if (selfInfo == null) {
            rightbtn.setVisibility(View.GONE);
            return;
        }
        if (selfInfo.getRealname() != null && selfInfo.getRealname().length() != 0) {
            titletextview.setText(selfInfo.getRealname());
            nametv.setText(selfInfo.getRealname() + "");
        } else if (selfInfo.getNickname() != null && selfInfo.getNickname().length() != 0) {
            titletextview.setText(selfInfo.getNickname());
            nametv.setText(selfInfo.getNickname() + "");
        } else {
            if (selfInfo.getRole() == AppConstants.DIRECTORROLE) {
                titletextview.setText("园长");
                nametv.setText("园长");
            } else if (selfInfo.getRole() == AppConstants.TEACHERROLE) {
                titletextview.setText("老师");
                nametv.setText("老师");
            }
        }
        if (state.equals(AppConstants.CONTACTS_NOFRIEND)) {
            if (selfInfo.getAvatar() == null) {
                headimage.setImageResource(R.drawable.friendicon);
            } else {
                imageLoader.displayImage(selfInfo.getAvatar(), headimage, ImageLoadOptions.getContactsFriendPicOptions());
            }
            if (selfInfo.getAccount() != null && !selfInfo.getAccount().equals("")) {
                numbertv.setText("(账号:" + selfInfo.getAccount() + ")");
            }
            realnametv.setText(selfInfo.getRealname());
            // phonetv.setText(selfInfo.getTelephone());
            if (selfInfo.getTelephone() == null || selfInfo.getTelephone().equals("")) {
                callphone_rl.setVisibility(View.GONE);
            } else {
                phonetv.setText(selfInfo.getTelephone());
                callphone_rl.setVisibility(View.VISIBLE);
            }
            if (selfInfo.getGender() != null) {
                if (selfInfo.getGender().equals("3")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexman);
                } else if (selfInfo.getGender().equals("2")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexwoman);
                } else {
                    sextv.setText("未填写");
                }
            }
            regiontv.setText(getlocationByid(selfInfo.getLocation()));
            delfriendbtn.setText("加为好友");
            sendmessagely.setVisibility(View.GONE);
        } else if (state.equals(AppConstants.CONTACTS_TEACHER) || state.equals(AppConstants.CONTACTS_PARENT)) {
            if (selfInfo.getAvatar() == null) {
                headimage.setImageResource(R.drawable.friendicon);
            } else {
                imageLoader.displayImage(selfInfo.getAvatar(), headimage, ImageLoadOptions.getContactsFriendPicOptions());
            }
            nametv.setText(selfInfo.getRealname() + "");
//            if (selfInfo.getAccount() != null && !selfInfo.getAccount().equals("")) {
//                numbertv.setText("(账号:" + selfInfo.getAccount() + ")");
//            }
            if (selfInfo!=null && selfInfo.getRole() == AppConstants.PARENTROLE) {
                String name = StringUtils.getCall(selfInfo.getRole(), selfInfo.getRelationship());
                numbertv.setText(name);
            } else {
                if (selfInfo.getJob() !=null && !selfInfo.getJob().equals("")) {
                    numbertv.setText(selfInfo.getJob());
                } else {
                    numbertv.setText(selfInfo.getRole() == 0 ? "园长" : "老师");
                }
            }
            nametv.setVisibility(View.VISIBLE);
            realnametv.setText(selfInfo.getRealname());

            int role_self = accountInfo.getRole();
            if (state.equals(AppConstants.CONTACTS_PARENT) && role_self == AppConstants.PARENTROLE) {
                callphone_rl.setVisibility(View.GONE);
            } else {
                if (selfInfo.getTelephone() == null || selfInfo.getTelephone().equals("")) {
                    phonetv.setText("未填写");
                    callphone_rl.setVisibility(View.VISIBLE);
                } else {
                    phonetv.setText(selfInfo.getTelephone());
                    callphone_rl.setVisibility(View.VISIBLE);
                }
            }
            if (selfInfo.getGender() != null) {
                if (selfInfo.getGender().equals("3")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexman);
                } else if (selfInfo.getGender().equals("2")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexwoman);
                } else {
                    sextv.setText("未填写");
                }
            }
            UtilsLog.i(TAG, "from CONTACTS_TEACHER or CONTACTS_PARENT name and birthday is :" + selfInfo.getNickname() + "/" + birthday);

            headimage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> paths = new ArrayList<String>();
                    ArrayList<String> titles = new ArrayList<String>();
                    if (selfInfo.getAvatar() != null && !selfInfo.getAvatar().equals("")) {
                        titles.add("");
                        paths.add(selfInfo.getAvatar());
                        Intent intent = new Intent(ContactFriendDatacardActivity.this, PhotoManager_ViewPager.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("type", AppConstants.PARAM_FRIENDSTER_HEAD);
                        // path list
                        bundle.putStringArrayList("imglist", paths);
                        // title list
                        bundle.putStringArrayList("decslist", titles);
                        bundle.putInt("position", 0);
                        intent.putExtras(bundle);
                        ContactFriendDatacardActivity.this.startActivity(intent);
                    }
                }
            });

            if (selfInfo.getRole() == AppConstants.PARENTROLE) { // 家长身份才显示生日
                birthday_tv.setText(birthday);
                birthdayview.setVisibility(View.VISIBLE);
                birthday_rl.setVisibility(View.VISIBLE);
            } else {
                birthdayview.setVisibility(View.GONE);
                birthday_rl.setVisibility(View.GONE);
            }

            regionrly.setVisibility(View.GONE);
            // regionrlyview.setVisibility(View.GONE);

            // regiontv.setText(getlocationByid(selfInfo.getLocation()));
            delcontactly.setVisibility(View.GONE);
            sendmessagely.setVisibility(View.VISIBLE);
        } else if (state.equals(AppConstants.GROUPMEMBER)) {
            if (selfInfo.getAvatar() == null) {
                headimage.setImageResource(R.drawable.friendicon);
            } else {
                imageLoader.displayImage(selfInfo.getAvatar(), headimage, ImageLoadOptions.getContactsFriendPicOptions());
            }
            nametv.setText(selfInfo.getNickname() + "");
            if (selfInfo.getAccount() != null && !selfInfo.getAccount().equals("")) {
                numbertv.setText("(账号:" + selfInfo.getAccount() + ")");
            }
            realnametv.setText(selfInfo.getRealname());
            // phonetv.setText(selfInfo.getTelephone());
            if (selfInfo.getTelephone() == null || selfInfo.getTelephone().equals("")) {
                callphone_rl.setVisibility(View.GONE);
            } else {
                phonetv.setText(selfInfo.getTelephone());
                callphone_rl.setVisibility(View.VISIBLE);
            }
            if (selfInfo.getGender() != null) {
                if (selfInfo.getGender().equals("3")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexman);
                } else if (selfInfo.getGender().equals("2")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexwoman);
                } else {
                    sextv.setText("未填写");
                }
            }
            regiontv.setText(getlocationByid(selfInfo.getLocation()));
            isfriend = containtFriend(targetid);
            delcontactly.setVisibility(View.GONE);
            if (selfInfo.getUid() == accountInfo.getUid()) {
                sendmessagely.setVisibility(View.GONE);
            } else {
                sendmessagely.setVisibility(View.VISIBLE);
            }
        } else {
            if (selfInfo.getAvatar() == null) {
                headimage.setImageResource(R.drawable.friendicon);
            } else {
                imageLoader.displayImage(selfInfo.getAvatar(), headimage, ImageLoadOptions.getContactsFriendPicOptions());
            }
            nametv.setText(selfInfo.getNickname() + "");
            if (selfInfo.getAccount() != null && !selfInfo.getAccount().equals("")) {
                numbertv.setText("(账号:" + selfInfo.getAccount() + ")");
            }
            realnametv.setText(selfInfo.getRealname());
            // phonetv.setText(selfInfo.getTelephone());
            if (selfInfo.getTelephone() == null || selfInfo.getTelephone().equals("")) {
                callphone_rl.setVisibility(View.GONE);
            } else {
                phonetv.setText(selfInfo.getTelephone());
                callphone_rl.setVisibility(View.VISIBLE);
            }
            if (selfInfo.getGender() != null) {
                if (selfInfo.getGender().equals("3")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexman);
                } else if (selfInfo.getGender().equals("2")) {
                    sextv.setText(R.string.contacts_puacdatacard_sexwoman);
                } else {
                    sextv.setText("未填写");
                }
            }
            regiontv.setText(getlocationByid(selfInfo.getLocation()));
            delcontactly.setVisibility(View.GONE);
            rightbtn.setVisibility(View.VISIBLE);
            sendmessagely.setVisibility(View.VISIBLE);
        }

        // 通过配置权限控制家长之间不能互相发消息
        if (accountInfo.getRole() == AppConstants.PARENTROLE && selfInfo.getRole() == AppConstants.PARENTROLE
                && accountInfo.getRights().contains(AppConstants.PARENT_COMUNICATE_RIGHTS + "")) {
            sendmessagely.setVisibility(View.GONE);
        }
    }

    public void setOnClick(){
        leftbtn.setOnClickListener(this);
        delfriendbtn.setOnClickListener(this);
        sendmessagely.setOnClickListener(this);
        rightbtn.setOnClickListener(this);
        nettogButton.setOnClickListener(this);
        callphone_rl.setOnClickListener(this);
    }

    /**
     * 获取本地relation
     *
     * @return
     */
    private int getLocalRelation(){
         return accountInfo.getRelationship();
    }

    /**
     * 检查环信本地注册状态
     *
     * @param role
     * @param relation
     * @return true 表示已经注册，false表示还没注册
     */
    private boolean checkLoaclHxState(int role, String relation){
        int hxState = 0;
        try {
            List<RelationShipBean> list = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
            if (list!=null && list.size()!=0) {
                if (role == 2) { // 角色是家长时，需要判断家长和小朋友的身份关系。
                    for (RelationShipBean bean:list) {
                        if (bean.getRelationship() == Integer.valueOf(relation)) {
                            hxState = bean.getHxregtag();
                        }
                    }
                } else if (role == 1) { // 老师时取第一条。
                    RelationShipBean shipBean = list.get(0);
                    hxState = shipBean.getHxregtag();
                } else if (role == 0) {
                    RelationShipBean shipBean = list.get(0);
                    hxState = shipBean.getHxregtag();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (hxState == 0) {
            return false;
        } else {
            return true;
        }

    }

    private void jumpToSms(){
        Intent intent = new Intent(ContactFriendDatacardActivity.this, ServiceScheduleComments.class);
        intent.putExtra("type", "smsmessage");
        intent.putExtra("to", targetid);
        intent.putExtra("role", selfInfo.getRole());
        intent.putExtra("name", selfInfo.getNickname());
        startActivity(intent);
    }

    /**
     * 根据获取到的消息判断发送消息的逻辑
     *
     * @param selfInfo
     */
    private void sendMessageByRole(SelfInfo selfInfo){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        List<SelfInfo.RelationShip> list = selfInfo.getRelationships();
        if (info.getRole() == AppConstants.DIRECTORROLE) {
            // 园长发通知
            if (selfInfo.getRole() == AppConstants.TEACHERROLE || selfInfo.getRole() == AppConstants.DIRECTORROLE) {
                boolean flag = false;
                for (SelfInfo.RelationShip ship:selfInfo.getRelationships()) {
                    if (ship.getHxregtag() == 1) {
                        flag = true; // 如果存在等于1的情况，列出家长。
                    }
                }
                if (!flag) { // 表示对方还未注册环信
                    jumpToSms();
                } else {
                    if (checkLoaclHxState(accountInfo.getRole(), String.valueOf(getLocalRelation()))) {
                        Intent intent = new Intent(this, ChatActivity2.class);
                        intent.putExtra("toChatAvatar", selfInfo.getAvatar());
                        intent.putExtra("userId", selfInfo.getUid() + "a" + 0);
                        if (selfInfo.getRole() == 0) {
                            intent.putExtra("nick", selfInfo.getRealname() + "(园长)");
                        } else if (selfInfo.getRole() == 1) {
                            intent.putExtra("nick", selfInfo.getRealname() + "(老师)");
                        }
                        intent.putExtra("role", selfInfo.getRole());
                        startActivity(intent);
                    } else {
                        regeditHuanxin(Integer.valueOf(accountInfo.getUid()), "al1M0Ak3sG6", Integer.valueOf(getLocalRelation()), 0);
                    }
                }
            } else {
                if (info.getNoticeurl()!=null && !info.getNoticeurl().equals("")) {
                    Bundle noticebundle = new Bundle();
                    noticebundle.putString(AppConstants.INTENT_URL, info.getNoticeurl() + "&touid=" + selfInfo.getUid() + "&realname=" + selfInfo.getRealname() + "&cid=" + cid);
                    noticebundle.putString(AppConstants.INTENT_NAME, "发通知");
                    openActivity(CommonBrowser.class, noticebundle);
                }
            }
        } else { // 老师角色,家长角色
            String relation = "";
            if (list!=null && list.size()!=0) { // 当对方没有登录环信的时候
                if (list.size() == 1 && list.get(0).getRelationship() == 0) { // 当时老师给园长或老师发消息时
                    Intent intent = new Intent(this, ChatActivity2.class);
                    relation = selfInfo.getRelationship() + "";
                    /** 先检查对方是否有环信，在检查本地是否注册环信 */
                    if (selfInfo.getRelationships().get(0).getHxregtag() == 0) { // 表示对方还没注册环信账号
                        jumpToSms();
                    } else {
                        if (checkLoaclHxState(accountInfo.getRole(), String.valueOf(getLocalRelation()))) {
                            intent.putExtra("toChatAvatar", selfInfo.getAvatar());
                            intent.putExtra("userId", selfInfo.getUid() + "a" + relation);
                            if (selfInfo.getRole() == 0) {
                                intent.putExtra("nick", selfInfo.getRealname() + "(园长)");
                            } else if (selfInfo.getRole() == 1) {
                                intent.putExtra("nick", selfInfo.getRealname() + "(老师)");
                            } else if (selfInfo.getRole() == 2) {
                                intent.putExtra("nick", selfInfo.getRealname() + "(" + AppConstants.RELATIONNAME[Integer.valueOf(relation)] + ")");
                            }
                            intent.putExtra("role", selfInfo.getRole());
                            startActivity(intent);
                        } else {
                            regeditHuanxin(Integer.valueOf(accountInfo.getUid()), "al1M0Ak3sG6", Integer.valueOf(getLocalRelation()), Integer.valueOf(relation));
                        }
                    }
                } else {
                    boolean flag = false;
                    for (SelfInfo.RelationShip ship:list) {
                        if (ship.getHxregtag() == 1) {
                            flag = true; // 如果存在等于1的情况，列出家长。
                        }
                    }
                    if (!flag) { // 表示对方还没注册环信账号
                        jumpToSms();
                    } else {
                        integersrelation.clear();
                        for (SelfInfo.RelationShip sip : list) {
                            if (sip.getRelationship()!=0) {
                                integersrelation.add(sip.getRelationship() - 1);
                            }
                        }
                        IntArraySortUtil comSort = new IntArraySortUtil();
                        Collections.sort(integersrelation, comSort);
                        strings = new String[integersrelation.size()];
                        for (int i = 0, j = integersrelation.size(); i < j; i++) {
                            strings[i] = relation_name[integersrelation.get(i)];
                        }
                        int relation_index = 0;
                        String relationname = null;
                        for (int i = 0; i < relation_name.length; i++) {
                            if (i == selfInfo.getDefault_relation() - 1) {
                                relationname = relation_name[i];
                            }
                        } // 查出defaultRelation对应的名字
                        for (int i = 0; i < strings.length; i++) {
                            if (strings[i].equals(relationname)) {
                                relation_index = i;
                            }
                        } // 根据名字查询对应后数组中的位置
                        if (strings.length == 1) {
                            Intent intent = new Intent(this, ChatActivity2.class);
                            intent.putExtra("toChatAvatar", selfInfo.getAvatar());
                            intent.putExtra("userId", selfInfo.getUid() + "a" + (selfInfo.getDefault_relation()));
                            if (selfInfo.getRole() == 0) {
                                intent.putExtra("nick", selfInfo.getRealname() + "(园长)");
                            } else if (selfInfo.getRole() == 1) {
                                intent.putExtra("nick", selfInfo.getRealname() + "(老师)");
                            } else if (selfInfo.getRole() == 2) {
                                intent.putExtra("nick", selfInfo.getRealname() + "(" + AppConstants.RELATIONNAME[Integer.valueOf(selfInfo.getDefault_relation())] + ")");
                            }
                            intent.putExtra("role", selfInfo.getRole());
                            startActivity(intent);
                        } else if (strings.length > 1) {
                            showDialog("请选择要发送的家长", strings, mOkOnClickListener, -1);
                        }
                    }
                }
            } else {
                jumpToSms();
            }
        }
//        else { // 家长角色
//           String relation = "";
//            relation = String.valueOf(selfInfo.getDefault_relation());
//            if (selfInfo.getRelationships()!=null && selfInfo.getRelationships().size()!=0) {
//                boolean flag = false;
//                for (SelfInfo.RelationShip ship:list) {
//                    if (ship.getHxregtag() == 1) {
//                        flag = true; // 如果存在等于1的情况，列出家长。
//                    }
//                }
//                if (!flag) { // 表示对方还没注册环信账号
//                    jumpToSms();
//                } else {
//                    if (checkLoaclHxState(accountInfo.getRole(), String.valueOf(getLocalRelation()))) { // 表示本地已经注册环信(使用本地的relation)
//                        Intent intent = new Intent(this, ChatActivity2.class);
//                        intent.putExtra("toChatAvatar", selfInfo.getAvatar());
//                        intent.putExtra("userId", selfInfo.getUid() + "a" + relation);
//                        if (selfInfo.getRole() == 0) {
//                            intent.putExtra("nick",selfInfo.getNickname() + "(园长)");
//                        } else if (selfInfo.getRole() == 1) {
//                            intent.putExtra("nick",selfInfo.getNickname() + "(老师)");
//                        } else if (selfInfo.getRole() == 2) {
//                            intent.putExtra("nick",selfInfo.getNickname() + "(" + AppConstants.RELATIONNAME[Integer.valueOf(relation)] + ")");
//                        }
//                        startActivity(intent);
//                    } else { // 否则要先注册环信-->登陆环信在进行聊天(使用本地的relation)
//                        int relations = getLocalRelation();
//                        regeditHuanxin(Integer.valueOf(accountInfo.getUid()), "al1M0Ak3sG6",getLocalRelation(), relations);
//                    }
//                }
//            } else {
//                jumpToSms();
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left_btn:
            ContactFriendDatacardActivity.this.finish();
            appcontext.setRefresh(false);
            break;
        case R.id.contact_frienddatacard_phonely:
            showDialog("请选择联系方式", phonedialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (selfInfo.getTelephone()!=null && selfInfo.getTelephone().length()!=0) {
                        if (i == 0) {
                            showDialog("打电话", "确定拨打电话吗？", "拨打", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                     // 调用Android系统API打电话
                                     Uri uri = Uri.parse("tel:" + selfInfo.getTelephone());
                                     Intent intent = new Intent(Intent.ACTION_CALL, uri);
                                     startActivity(intent);
                                }
                            });
                        } else {
                            showDialog("发信息", "确定发送信息吗？", "发送", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //调用Android系统API发送短信
                                    Uri uri = Uri.parse("smsto:"+selfInfo.getTelephone());
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        showToast("该用户没有填写手机号码，抱歉");
                    }
                    dialogInterface.dismiss();
               }
           }, -1);
            break;
        case R.id.contact_frienddatacard_deletecontactbtn:
            if (state.equals(AppConstants.CONTACTS_NOFRIEND) || state.equals(AppConstants.GROUPMEMBER)) {
                AppServer.getInstance().addFriend(accountInfo.getUid(), targetid, new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == 0) {
                            showToast("已经发送添加好友请求，请等待对方回复");
                            ContactFriendDatacardActivity.this.finish();
                            if (state.equals(AppConstants.CONTACTS_FRIEND)) {
                                /* Intent intent=new Intent(ContactFriendDatacardActivity.this,MainActivity.class);
                                intent.putExtra("acivityType", MainActivity.TAB_TAG_CONTACTS);
                                startActivity(intent); */
                            } else if (state.equals(AppConstants.CONTACTS_PARENT)) {
                                ContactFriendDatacardActivity.this.finish();
                            } else {
                                Intent intent = new Intent(ContactFriendDatacardActivity.this, ContactsAddFriendActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                ContactFriendDatacardActivity.this.finish();
                            }
                        }
                    }
                });
            }
            break;
        case R.id.contact_frienddatacard_sendly:
            if (selfInfo!=null && selfInfo.getUid()!=0) {
                sendMessageByRole(selfInfo);
            }
            break;
        case R.id.lookdata_btn:
            new AlertDialog.Builder(this).setTitle("是否删除好友").setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppServer.getInstance().deletContactPeople(accountInfo.getUid(), targetid, new OnAppRequestListener(){
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if (code == 0) {
                                showToast("成功好友删除");
                                try {
                                    DbHelper.getDB(ContactFriendDatacardActivity.this).delete(Friend.class, WhereBuilder.b("uid", "=", targetid));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                Contacts contacts = appcontext.getContacts();
                                List<Friend> list = contacts.getFriends();
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getUid() == targetid) {
                                        list.remove(i);
                                    }
                                }
                                contacts.setFriends(list);
                                appcontext.setContacts(contacts);
                                if (state.equals(AppConstants.CONTACTS_FRIEND)) {
                                    /* Intent intent=new Intent(ContactFriendDatacardActivity.this,MainActivity.class);
                                    intent.putExtra("acivityType", MainActivity.TAB_TAG_CONTACTS);
                                    startActivity(intent); */
                                    ContactFriendDatacardActivity.this.finish();
                                } else {
                                    ContactFriendDatacardActivity.this.finish();
                                    Intent intent=new Intent(ContactFriendDatacardActivity.this, ContactsAddFriendResultActivity.class);
                                    intent.putExtra("state", 0);
                                    intent.putExtra("uid", selfInfo.getUid());
                                    startActivity(intent);
                                }
                            }
                        }
                     });
                }
            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            }).create().show();
            break;
        case R.id.network_disable_button_relink:
//            WifiManager wifiManager = (WifiManager) ContactFriendDatacardActivity.this.getSystemService(Context.WIFI_SERVICE);
//            wifiManager.setWifiEnabled(true);
//            final Timer timer = new Timer();
//            timer.schedule(new TimerTask(){
//                @Override
//                public void run() {
            if (AppUtils.isNetworkAvailable(appcontext)) {
                AppServer.getInstance().viewInfo(accountInfo.getUid() + "", targetid + "", 0 + "", role, new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == 0) {
                            selfInfo = (SelfInfo) obj;
                            initView();
                            layout_networkdisable.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
            }
//                timer.cancel();
//                }
//            }, 10000, 10000);
            break;
        default:
            break;
        }
    }

    private class RegTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            initData();
        }
    }

    /**
     * 实现对话框点击接口
     */
    private DialogInterface.OnClickListener mOkOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent=new Intent(ContactFriendDatacardActivity.this, ChatActivity2.class);
            int relation;

            if (integersrelation == null || integersrelation.size() == 0) {
                relation = i + 1;
            } else {
                relation = integersrelation.get(i) + 1;
            }

            int relation_index = i;
            String relationname = null;
            for (int which = 0; which < strings.length; which++) {
                if (which == relation_index) {
                    relationname = strings[which];
                }
            } // 查出defaultRelation对应的名字
            for (int which = 0; which < relation_name.length; which++) {
                if (relation_name[which].equals(relationname)) {
                    relation_index = which;
                }
            } // 根据名字查询对应后数组中的位置
            // 要在选择完身份后，在注册环信
            if (checkLoaclHxState(accountInfo.getRole(), String.valueOf(getLocalRelation()))) {
                intent.putExtra("toChatAvatar", selfInfo.getAvatar());
                intent.putExtra("userId", selfInfo.getUid() + "a" + String.valueOf(relation_index + 1));
                if (selfInfo.getRole() == 0) {
                    intent.putExtra("nick",selfInfo.getRealname() + "(园长)");
                } else if (selfInfo.getRole() == 1) {
                    intent.putExtra("nick",selfInfo.getRealname() + "(老师)");
                } else if (selfInfo.getRole() == 2) {
                    intent.putExtra("nick", selfInfo.getRealname() + "(" + AppConstants.RELATIONNAME[relation_index + 1] + ")");
                }
                intent.putExtra("role", selfInfo.getRole());
                intent.putExtra("toChatAvatar", selfInfo.getAvatar());
                startActivity(intent);
            } else {
                regeditHuanxin(Integer.valueOf(accountInfo.getUid()), "al1M0Ak3sG6", getLocalRelation(), relation);
            }
            dialogInterface.dismiss();
        }
    };

    /**
     * 更新本地数据库中的环信注册状态
     * @param relationShip
     */
    private void updateLocalHxState(int relationShip) {
        try {
            List<RelationShipBean> list = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
            if (list!=null && list.size()!=0) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRelationship() == relationShip) {
                        RelationShipBean bean = new RelationShipBean();
                        bean = list.get(i);
                        bean.setHxregtag(hxState);
                        list.set(i, bean);
                    }
                }
            }
            DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 环信登陆
     * @param currentUsername
     * @param currentPassword
     */
    public void huanxinLogin(final String currentUsername, final String currentPassword, final int relation) {
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {
            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                AppContext.getInstance().setUserName(currentUsername);
                AppContext.getInstance().setPassword(currentPassword);
                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    // conversations in case we are auto login
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();

                    // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();
                    EMLog.d("roster", "contacts size: " + usernames.size());
                    Map<String, User> userlist = new HashMap<String, User>();
                    for (String username : usernames) {
                        User user = new User();
                        user.setUsername(username);
                        // setUserHearder(username, user);
                        userlist.put(username, user);
                    }
                    // 添加user"申请与通知"
                    User newFriends = new User();
                    newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                    newFriends.setNick("申请与通知");
                    newFriends.setHeader("");
                    userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                    // 添加"群聊"
                    User groupUser = new User();
                    groupUser.setUsername(Constant.GROUP_USERNAME);
                    groupUser.setNick("群聊");
                    groupUser.setHeader("");
                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                    cancelLoadingDialog();
                    // 存入内存
                    AppContext.getInstance().setContactList(userlist);
                    // 存入db
                    UserDao dao = new UserDao(ContactFriendDatacardActivity.this);
                    List<User> users = new ArrayList<User>(userlist.values());
                    dao.saveContactList(users);
                    // 获取群聊列表(群聊里只有 groupid 和 groupname 等简单信息，不包含members), sdk会把群组存入到内存和db中
                    EMGroupManager.getInstance().getGroupsFromServer();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面，也可以不管这个exception继续进到主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AppContext.getInstance().logout(null);
                            Toast.makeText(getApplicationContext(), " 获取好友或群聊失败", Toast.LENGTH_SHORT).show();
                            Intent a = new Intent(ContactFriendDatacardActivity.this, ChatActivity2.class);
                            a.putExtra("username", selfInfo.getNickname());
                            a.putExtra("userId", selfInfo.getUid() + String.valueOf(relation)); // 对方的环信账号(账号+relation)
                            if (selfInfo.getRole() == 0) {
                                a.putExtra("nick", selfInfo.getRealname() + "(园长)");
                            } else if (selfInfo.getRole() == 1) {
                                a.putExtra("nick", selfInfo.getRealname() + "(老师)");
                            } else if (selfInfo.getRole() == 2) {
                                a.putExtra("nick", selfInfo.getRealname() + "(" + AppConstants.RELATIONNAME[relation] + ")");
                            }
                            a.putExtra("toChatAvatar", selfInfo.getAvatar());
                            ContactFriendDatacardActivity.this.startActivity(a);
                        }
                    });
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(AppContext.currentUserNick.trim());

                if (!ContactFriendDatacardActivity.this.isFinishing())
                    cancelLoadingDialog();
                // 进入主页面
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 进入主页面
                        Intent a = new Intent(ContactFriendDatacardActivity.this, ChatActivity2.class);
                        a.putExtra("username", selfInfo.getNickname());
                        a.putExtra("userId", selfInfo.getUid() + String.valueOf(relation));
                        if (selfInfo.getRole() == 0) {
                            a.putExtra("nick", selfInfo.getRealname() + "(园长)");
                        } else if (selfInfo.getRole() == 1) {
                            a.putExtra("nick", selfInfo.getRealname() + "(老师)");
                        } else if (selfInfo.getRole() == 2) {
                            a.putExtra("nick", selfInfo.getRealname() + "(" + AppConstants.RELATIONNAME[relation] + ")");
                        }
                        a.putExtra("toChatAvatar", selfInfo.getAvatar());
                        ContactFriendDatacardActivity.this.startActivity(a);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) { }

            @Override
            public void onError(final int code, final String message) {
                cancelLoadingDialog();
                runOnUiThread(new Runnable() {
                    public void run() {
                        jumpToSms();
                    }
                });
            }
        });
    }

    /**
     * 注册环信
     * @param account
     * @param password
     * @param localRelationShip
     */
    private void regeditHuanxin(final int account, final String password, final int localRelationShip, final int toRelation) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    String name = account + "a" + String.valueOf(localRelationShip);
                    EMChatManager.getInstance().createAccountOnServer(name, password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            hxState = 1; // 表示注册环信成功
                            updateLocalHxState(localRelationShip);
                            huanxinLogin(account + "a" + String.valueOf(localRelationShip), password, localRelationShip);
                            AppServer.getInstance().updateHxState(account, localRelationShip, hxState, "注册成功", new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) { }
                            });
                        }
                    });
                } catch (final EaseMobException e) {
                    hxState = 0; // 表示注册环信失败
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String message = null;
                            if (e!=null) {
                                message = e.getMessage();
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.NONETWORK_ERROR) {
                                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                    hxState = 1; // 表示注册环信成功
                                } else if (errorCode == EMError.UNAUTHORIZED) {

                                } else {
                                    Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            updateLocalHxState(localRelationShip);
                            huanxinLogin(account + "a" + String.valueOf(localRelationShip), password,toRelation);
                            AppServer.getInstance().updateHxState(account, localRelationShip, hxState, message + " ", new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) { }
                            });
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//          Intent intent=new Intent(ContactFriendDatacardActivity.this,MainActivity.class);
//          intent.putExtra("acivityType", MainActivity.TAB_TAG_CONTACTS);
//          startActivity(intent);
//          this.finish();
            ContactFriendDatacardActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getlocationByid(String id) {
        if (id == null || id.equals("") || id.equals("0") || id.equals("-1")) {
            return "";
        }
        dbm = new DBManager(this);
        dbm.openDatabase();
        sqlite = dbm.getDatabase();
        String sql = "select * from district where locationid='" + id + "'";
        Cursor cursor = sqlite.rawQuery(sql,null);
        cursor.moveToFirst();
        AddressBean addressBean = new AddressBean();
        List<AddressBean> list = DbHelper.getAList(addressBean, cursor);
        if (list!=null && list.size()!=0) {
            String address = "";
            if (list.get(0).getProvince()!=null && !list.get(0).getProvince().equals("")) {
                address = address + list.get(0).getProvince();
            }
            if (list.get(0).getCity()!=null && !list.get(0).getCity().equals("")) {
                address = address + "," + list.get(0).getCity();
            }
            if (list.get(0).getLocation()!=null && !list.get(0).getLocation().equals("")) {
                address = address + "," + list.get(0).getLocation();
            }
            return address;
        }
        return "";
    }

    public Boolean containtFriend(int id) {
        List<Friend> list = AppContext.getInstance().getContacts().getFriends();
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getUid()) {
                return true;
            }
        }
        return false;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    class RelationAdapter extends BaseListAdapter<SelfInfo.RelationShip> {
        private Context context;
        private LayoutInflater mInflater;
        private List<SelfInfo.RelationShip> list = null;

        RelationAdapter(Context context, List<SelfInfo.RelationShip>list) {
            super(context, list);
            this.context = context;
            this.list = list;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View bindView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.inflater_sendnotice_group, null);
            }
            TextView relation_tv = ViewHolder.get(convertView, R.id.id_sendmsg_inflater_group_nametv);
            ImageView relation_iv = ViewHolder.get(convertView, R.id.id_sendmsg_inflater_group_nameck);
            RelativeLayout relativeLayout = ViewHolder.get(convertView, R.id.id_sendmsg_inflater_rl);
            relativeLayout.setBackgroundResource(R.color.white);
            SelfInfo.RelationShip relationShip = list.get(position);
            int index = relationShip.getRelationship();
            switch (index) {
                case 0:
                    relation_tv.setText(relation_name[0]);
                    break;
                case 1:
                    relation_tv.setText(relation_name[1]);
                    break;
                case 2:
                    relation_tv.setText(relation_name[2]);
                    break;
                case 3:
                    relation_tv.setText(relation_name[3]);
                    break;
                case 4:
                    relation_tv.setText(relation_name[4]);
                    break;
            }
            if (checkList!=null && checkList.size()!=0) {
                SelfInfo.RelationShip relationShips = getList().get(position);
                boolean checked = checkList.contains(relationShips);
                if (checked) {
                    relation_iv.setImageResource(R.drawable.friendster_check_true);
                } else {
                    relation_iv.setImageResource(R.drawable.friendster_check_false);
                }
            }
            return convertView;
        }

        public void setCheck(int postion, View view) {
            SelfInfo.RelationShip relationShip = getList().get(postion);
            boolean checked = checkList.contains(relationShip);
            ImageView select_iv = ViewHolder.get(view, R.id.id_sendmsg_inflater_group_nameck);
            if (checked) {
                select_iv.setImageResource(R.drawable.friendster_check_false);
                checkList.remove(relationShip);
            } else {
                select_iv.setImageResource(R.drawable.friendster_check_true);
                checkList.add(relationShip);
            }
            for (int i = 0; i < checkList.size(); i++) {
                if (i < checkList.size() - 1) {
                    checkList.remove(i);
                }
            }
            notifyDataSetChanged();
        }

    }

}
