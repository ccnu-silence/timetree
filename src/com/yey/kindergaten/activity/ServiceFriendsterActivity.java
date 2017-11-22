package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.EmoViewPagerAdapter;
import com.yey.kindergaten.adapter.EmoteAdapter;
import com.yey.kindergaten.adapter.FriendsterActivityAdapter;
import com.yey.kindergaten.adapter.FriendsterActivityAdapter.Disscuss;
import com.yey.kindergaten.adapter.FriendsterActivityAdapter.ShowComment;
import com.yey.kindergaten.adapter.FriendsterActivityAdapter.StarRun;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.FaceText;
import com.yey.kindergaten.bean.GroupTwritte;
import com.yey.kindergaten.bean.GroupTwritte.comments;
import com.yey.kindergaten.bean.GroupTwritte.likers;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.task.SimpleTask;
import com.yey.kindergaten.task.TaskExecutor;
import com.yey.kindergaten.task.TaskExecutor.OrderedTaskExecutor;
import com.yey.kindergaten.upyun.UpYunException;
import com.yey.kindergaten.upyun.UpYunUtils;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.EmoticonsEditText;
import com.yey.kindergaten.widget.PullToRefreshHeaderView;
import com.yey.kindergaten.widget.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class ServiceFriendsterActivity extends BaseActivity implements StarRun, ShowComment, Disscuss, PullToRefreshHeaderView.OnHeaderRefreshListener, PullToRefreshListView.onScrollChanged, PullToRefreshListView.onPullClickListener, PullToRefreshListView.onBottomViewClickListener {

    @ViewInject(R.id.ll_activity_service_friendster_main)PullToRefreshHeaderView ll;// 下拉刷新布局
    @ViewInject(R.id.pull_to_refresh_lv)
    static PullToRefreshListView lv;                                                // 主体listview
    @ViewInject(R.id.header_title)TextView tv_head;                                 // 标题
//    @ViewInject(R.id.right_btn)ImageView iv_right;                                // 发动态

    @ViewInject(R.id.right_tv)TextView right_tv;                                    // 发动态
    @ViewInject(R.id.left_btn)ImageView iv_left;                                    // 返回按钮
    @ViewInject(R.id.circleprogress)ImageView iv_progress;                          // 滚动圈
    @ViewInject(R.id.iv_activity_friendster_head)static CircleImageView circleiv;   // 班级
    @ViewInject (R.id.ll_activity_service_friendster_item_input)LinearLayout ll_input; // 发送消息布局
    @ViewInject(R.id.input_activity_service_friendster_item)EmoticonsEditText et;   // 消息编辑框
    // @ViewInject(R.id.rl_friendster_head)static FrameLayout rl_head;
    // @ViewInject(R.id.tv_friendster_nodata)static LinearLayout ll_nodata;
    @ViewInject(R.id.tv_friendster_user)static TextView tv_user;                    // 选班级下箭头
    @ViewInject(R.id.head_layout)static FrameLayout head_layout;                    // 公共标题
    @ViewInject(R.id.btn_activity_service_friendster_item)Button btn_send;          // 发送按钮
    @ViewInject(R.id.service_publishspeak_facely)LinearLayout bq_ll;                // 表情布局
    @ViewInject(R.id.service_publishspeak_face)ViewPager vp_face;                   // 表情的viewpager

    private static FriendsterActivityAdapter adapter;                               // adapter

    private static List<GroupTwritte> grouptwrittelist = new ArrayList<GroupTwritte>();        // 展示动态的list
    private static List<comments> dbhelperlist = new ArrayList<comments>();                // 数据库获取的承接评论list
    private static List<comments> commentlist = new ArrayList<comments>();                 // 用来拼接的评论list
    private static ArrayList<String> nodatalist = new ArrayList<String>();               // 图片拼接list
    private static List<likers> dbhelperliskerlist = new ArrayList<likers>();            // 数据库获取的承接点赞list
    private static List<likers> likerlist = new ArrayList<likers>();                     // 用来拼接的点赞list
    private int NextId;
    private CircleImageView headimg;
    private String content, imglist;
    private String share, typefrom;
    private StringBuffer url = new StringBuffer();
    private StringBuffer urlstring = new StringBuffer();
    private Boolean flag = true;
    private int position; // 判断评论点击是哪项Item
    private int classposition = 0;
    public Handler mHandler = new Handler();
    private static final String PATH = Environment.getExternalStorageDirectory() + "/yey/kindergaten/uploadimg/";
    private String name;
    // private static PullToRefreshListView lv;
    private String jsonimg;
    private static String gname;
    private static int gtype;
    private int mStatusHeight;
    private int cKeyBoardHeight, list_child_height;
    private List<FaceText> emos = null;
    private PopupWindow popWindow;
    private EmoticonsEditText et4;
    private ArrayList<String> Numlist = new ArrayList<String>();
    private List<Classe> classlist = new ArrayList<Classe>();
    private String imgurl = "";
    private static int cid;
    private static String cname;
    private int touid;
    private static Boolean isremove;
    private String[] items;
    private String albumid;
    private int index = 0; // 用于记录上传成功张数
    private int allIndex = 0; // 用于记录上传总张数
    private AccountInfo accountInfo;

    // public static final String UP_URL = "http://" + UpYunUtils.CLASSGROUP_BUCKET + ".b0.upaiyun.com/";
    public static final String UP_URL = "http://" + UpYunUtils.CLASSGROUP_BUCKET + ".yp.yeyimg.com";  // 换新的域名

    private final static int MSG_FRESH = 1; // 发动态后，刷新界面
    private final static int MSG_SHOWTOAST = 2; // 发动态后，刷新界面
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FRESH:
                    if (adapter!=null) {
                        ShowData(false);
                    }
                    break;
                case MSG_SHOWTOAST:
                    showToast("目前网络不给力，试试重新发送吧！");
                    break;
                default:
                    break;
            }
        }
    };
    private String TAG = "ServiceFriendsterActivity";

    @Override
    protected void onDestroy() {
        isremove = false;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_friendster_main);
        ViewUtils.inject(this);

        accountInfo = AppServer.getInstance().getAccountInfo();
        initView();
        initListener();
        initClasseData();
    }

    /** 初始化View */
    private void initView() {
        isremove = false;
//        iv_right.setVisibility(View.VISIBLE);
        right_tv.setVisibility(View.VISIBLE);
        right_tv.setText("发动态");

        iv_left.setVisibility(View.VISIBLE);
        tv_head.setText("班级圈");
        lv.setAdapter(this, grouptwrittelist, gtype);

        emos = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 1; ++i) {
            views.add(getGridView(i, et));
        }
        vp_face.setAdapter(new EmoViewPagerAdapter(views));

        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            if (AppServer.getInstance().getAccountInfo().getRights().contains("111")) {
//                iv_right.setVisibility(View.GONE);
                right_tv.setVisibility(View.GONE);
            } else {
//                iv_right.setVisibility(View.VISIBLE);
                right_tv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initListener() {
        adapter = lv.getAdapter();
        adapter.setStarrun(this);
        adapter.setShowcomment(this);
        adapter.setDisscuss(this);

        lv.setScrollChangedListener(this);
        lv.setPullClickListener(this);
        lv.setBottomClickListener(this);
        ll.setOnHeaderRefreshListener(this);
    }

    private void initClasseData() {
        if (accountInfo.getRole() == AppConstants.PARENTROLE) { // 家长没有班级表数据，需要在登录返回的数据中自己组装数据
            Classe classe = new Classe();
            classe.setCid(accountInfo.getCid());
            classe.setCname(accountInfo.getCname());
            cid = accountInfo.getCid();
            cname = accountInfo.getCname();
            lv.getUsername().setText(cname);
            classlist.add(classe);
            lv.getPullIv().setVisibility(View.GONE);
            loadData(accountInfo.getCid());
        } else {
            try {
                classlist = DbHelper.getDB(AppContext.getInstance()).findAll(Classe.class);
                if (classlist!=null) {
                    cid = classlist.get(0).getCid();
                    cname = classlist.get(0).getCname();
                }
                lv.getUsername().setText(cname);
                loadData(cid);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    boolean isWaitNet = false;
    private void loadData(final int cid) {
        ShowData(true);
        if (!isremove) {
            lv.addFooterView(lv.getFootView());
            isremove = true;
        }
        isWaitNet = true;
        AppServer.getInstance().getGroupTwitters(AppServer.getInstance().getAccountInfo().getUid(), cid, 10, -1, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, int nextid) {
                isWaitNet = false;
                if (code == AppServer.REQUEST_SUCCESS) {
                    NextId = nextid;
                    GroupTwritte[] tw = (GroupTwritte[]) obj;
                    if (tw.length > 0) {
                        cleanDbhelper();
                        for (int i = 0; i < tw.length; i++) {
                            GroupTwritte twi = tw[i];
                            twi.setCid(cid);
                            try {
                                DbHelper.getDB(ServiceFriendsterActivity.this).update(twi, WhereBuilder.b("twrid", "=", (tw[i].getTwrid())), "cid");
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            List<GroupTwritte> xlist = DbHelper.QueryTData("select * from GroupTwritte where twrid='" + tw[i].getTwrid() + "'", GroupTwritte.class);
                            if (xlist.size() < 1) {
                                try {
                                    DbHelper.getDB(ServiceFriendsterActivity.this).save(tw[i]);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                            comments[] comments = tw[i].getComment();
                            if (comments.length > 0) {
                                for (int j = 0; j < comments.length; j++) {
                                    List<comments> ylist=DbHelper.QueryTData("select * from comments where cmtid='" + comments[j].getCmtid() + "'", comments.class);
                                    if (ylist.size() < 1) {
                                        try {
                                            DbHelper.getDB(ServiceFriendsterActivity.this).save(comments[j]);
                                        } catch (DbException e) {
                                            UtilsLog.i(TAG, "save comments fail because DbException");
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            likers[] likers = tw[i].getLikers();
                            if (likers.length > 0) {
                                for (int j = 0; j < likers.length; j++) {
                                    List<likers> zlist = DbHelper.QueryTData("select * from likers where likeid='" + likers[j].getLikeid() + "'", likers.class);
                                    if (zlist.size() < 1) {
                                        try {
                                            DbHelper.getDB(ServiceFriendsterActivity.this).save(likers[j]);
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ShowData(true);
                    if (nextid == 0) {
                        if (isremove) {
                            lv.removeFooterView(lv.getFootView());
                            isremove=false;
                        }
                    }
                } else {
                    cancelLoadingDialog();
                }

            }
        });
    }

    public static void ShowData(boolean isFirstload) {
        if (grouptwrittelist!=null) {
            grouptwrittelist.clear();
        }
        if (dbhelperlist!=null) {
            dbhelperlist.clear();
        }
        List<GroupTwritte> grouptwrittelistaLL = DbHelper.QueryTData("select * from GroupTwritte where cid='" + cid + "'order by date desc", GroupTwritte.class);
        for (int i = 0; i < grouptwrittelistaLL.size(); i++) {
            GroupTwritte groupTwritte = grouptwrittelistaLL.get(i);
            if (groupTwritte.getTwrid() != -1) {
                grouptwrittelist.add(groupTwritte);
            }
        }
        if (grouptwrittelist!=null) {
            for (int i = 0; i < grouptwrittelist.size(); i++) {
                if (grouptwrittelist.get(i).getStatus() == 0) {
                    if (commentlist != null) {
                        commentlist.clear();
                    }
                    if (likerlist != null) {
                        likerlist.clear();
                    }
                    dbhelperlist = DbHelper.QueryTData("select * from comments where twrid='" + grouptwrittelist.get(i).getTwrid() + "'order by cmtid asc", comments.class);
                    dbhelperliskerlist = DbHelper.QueryTData("select * from likers where twrid='" + grouptwrittelist.get(i).getTwrid() + "'order by likeid asc", likers.class);
                    for (int j = 0; j < dbhelperlist.size(); j++) {
                        commentlist.add(dbhelperlist.get(j));
                        comments[] comments = (comments[]) commentlist.toArray(new comments[commentlist.size()]);
                        grouptwrittelist.get(i).setComment(comments);
                    }
                    for (int k = 0; k < dbhelperliskerlist.size(); k++) {
                        likerlist.add(dbhelperliskerlist.get(k));
                        likers[] likers = (likers[]) likerlist.toArray(new likers[likerlist.size()]);
                        grouptwrittelist.get(i).setLikers(likers);
                    }
                } else {
                    if (nodatalist != null) {
                        nodatalist.clear();
                    }
                    String[] img = grouptwrittelist.get(i).getImgs().split(",");
                    for (int j = 0; j < img.length; j++) {
                        nodatalist.add(img[j]);
                    }

                }
            }
        }
        if (grouptwrittelist!=null && grouptwrittelist.size() > 0) {
            // adapter = new FriendsterActivityAdapter(this, grouptwrittelist,gtype, ImageLoadOptions.getClassPhotoOptions());
            lv.setList(grouptwrittelist);
            lv.setVisibility(View.VISIBLE);
        } else {
            grouptwrittelist = new ArrayList<GroupTwritte>();
            List<GroupTwritte> list = null;
            try {
                list = DbHelper.getDB(AppContext.getInstance()).findAll(GroupTwritte.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (isFirstload && (list == null || list.size() == 0)) { // 表示第一次取值的时候，没有数据
                GroupTwritte newValues = new GroupTwritte();
                newValues.setContent("该班级圈暂无动态，记录下小朋友们的精彩瞬间吧!");
                newValues.setRealname("时光树");
                newValues.setUid(-1);
                newValues.setDate("");
                newValues.setTwrid(-1);
                newValues.setStatus(3);
                newValues.setCid(cid);
                newValues.setZan("");
                newValues.setAvatar(AppServer.getInstance().getAccountInfo().getAvatar());
                newValues.setImgs("");
                grouptwrittelist.add(newValues);
            }
            lv.setList(grouptwrittelist);
            if (isremove) {
                lv.removeFooterView(lv.getFootView());
                isremove = false;
            }
        }
    }

    /** 下拉刷新 */
    private void reFresh() { }

    @OnClick({(R.id.right_tv),(R.id.left_btn),(R.id.iv_activity_friendster_head),(R.id.btn_activity_service_friendster_item),(R.id.service_publishspeak_facely),(R.id.biaoqing_activity_service_friendster_item)})
    public void onClik(View v){
        Bundle bundle = new Bundle();
        Intent intent;
        switch (v.getId()) {
            case R.id.right_tv:
                index = 0;
                allIndex = 0;
                Intent a = new Intent(this, ServicePublishSpeakActivity.class);
                a.putExtra("type", AppConstants.GETGROUP);
                a.putExtra("cid", cid);
                a.putExtra("cname", cname);
                startActivity(a);
                break;
            case R.id.left_btn:
                finish();
                break;
            case R.id.iv_activity_friendster_head:
                if (gtype!=0) {
                    intent = new Intent(ServiceFriendsterActivity.this, ServiceKinderInfoActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_activity_service_friendster_item:
                if (!et.getText().toString().trim().equals("")) {
                    bq_ll.setVisibility(View.GONE);
                    ll_input.setVisibility(View.GONE);
                    hideSoftInput(et);
                    AppServer.getInstance().sentCommment(AppServer.getInstance().getAccountInfo().getUid(), touid, grouptwrittelist.get(position).getTwrid(), et.getText().toString(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            UtilsLog.i(TAG, "评论信息：uid、toid、twrid、message is :"
                                    + AppServer.getInstance().getAccountInfo().getUid() + "/" + touid + "/" + grouptwrittelist.get(position).getTwrid() + "/" + et.getText().toString());
                            if (code == AppServer.REQUEST_SUCCESS) {
                                UtilsLog.i(TAG, "评论成功");
                                comments cm = (comments) obj;
                                try {
                                    DbHelper.getDB(ServiceFriendsterActivity.this).save(cm);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                ShowData(false);
                                et.setText("");
                            } else {
                                UtilsLog.i(TAG, "评论失败");
                            }
                        }
                    });
                } else {
                    showToast("请输入评论内容");
                }
                break;
            case R.id.biaoqing_activity_service_friendster_item:
                if (bq_ll.isShown()) {
                    et.requestFocus();
                    bq_ll.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    ll_input.setLayoutParams(lp);
                    WindowManager vm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
                    int c = vm.getDefaultDisplay().getHeight();
                    int d = ll_input.getHeight();
                    int b = head_layout.getHeight();
                    lv.setSelectionFromTop(position + 1, (c - list_child_height - d - 3 * b / 2));
                    showSoftInput(et);
                } else {
                    et.requestFocus();
                    bq_ll.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ABOVE, R.id.service_publishspeak_facely);
                    ll_input.setLayoutParams(lp);
                    WindowManager vm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
                    int c = vm.getDefaultDisplay().getHeight();
                    int d = ll_input.getHeight();
                    int b = head_layout.getHeight();
                    int f = bq_ll.getHeight();
                    lv.setSelectionFromTop(position + 1, (c - list_child_height - d - 3 * b / 2 - f));
                    hideSoftInput(et);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshHeaderView view) {
        if (!isWaitNet) {
            loadData(cid);
        }
        view.onHeaderRefreshComplete();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isWaitNet = false;
    }

    /**
     * listview滑动监听
     * @param position
     * @param state
     */
    @Override
    public void scollStateChanged(int position, int state, AbsListView view) {
        ll_input.setVisibility(View.GONE);
        hideSoftInput(et);
        bq_ll.setVisibility(View.GONE);
        if (position == adapter.getCount()&&state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (NextId!=0) {
                loadMoreDate();
            }
            if (view.getLastVisiblePosition() == (view.getCount() - 1) && NextId == 0) {
                if (isremove) {
                    lv.removeFooterView(lv.getFootView());
                    ShowData(false);
                    isremove = false;
                }
            }
        }
    }

    /** 选择班级点击监听 */
    @Override
    public void pullClick() {
        classlist = DbHelper.QueryTData("select * from Classe", Classe.class);
        Numlist.clear();
        for (int i = 0; i < classlist.size(); i++) {
            Numlist.add(classlist.get(i).getCname());
            items = new String[Numlist.size()];
            Numlist.toArray(items);
        }
        showClassDialog(items, "选择班级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cid = classlist.get(which).getCid();
                cname = classlist.get(which).getCname();
                classposition = which;
                lv.getUsername().setText(cname);
                loadData(cid);
            }
        });
    }

    /** 底部控件点击监听 */
    @Override
    public void bottomViewClick() {
        Intent intent = new Intent(ServiceFriendsterActivity.this, ClassPhotoMainActivity.class);
        intent.putExtra("typefrom", AppConstants.FROMFRIENDSTER);
        intent.putExtra("classphototype", AppConstants.FROMFRIENDSTER);
        intent.putExtra("cid", cid);
        intent.putExtra("cname", cname);
        startActivity(intent);
    }

    private void loadMoreDate() {
        AppServer.getInstance().getGroupTwitters(AppServer.getInstance().getAccountInfo().getUid(), cid, 10, NextId, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, final int nextid) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    NextId = nextid;
                    GroupTwritte[] tw = (GroupTwritte[]) obj;
                    if (tw.length > 0) {
                        if (!isremove) {
                            lv.addFooterView(lv.getFootView());
                            isremove = true;
                        }
                        for (int i = 0; i < tw.length; i++) {
                            GroupTwritte twi = tw[i];
                            twi.setCid(cid);
                            try {
                                DbHelper.getDB(ServiceFriendsterActivity.this).update(twi, WhereBuilder.b("twrid", "=", (tw[i].getTwrid())), "cid");
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            List<GroupTwritte> xlist = DbHelper.QueryTData("select * from GroupTwritte where twrid='" + tw[i].getTwrid() + "'", GroupTwritte.class);
                            if (xlist.size() < 1) {
                                try {
                                    DbHelper.getDB(ServiceFriendsterActivity.this).save(tw[i]);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    DbHelper.getDB(AppContext.getInstance()).delete(GroupTwritte.class, WhereBuilder.b("twrid", "=", tw[i].getTwrid()));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                            comments[] comments = tw[i].getComment();
                            if (comments.length > 0) {
                                for (int j = 0; j < comments.length; j++) {
                                    List<comments> ylist = DbHelper.QueryTData("select * from comments where cmtid='" + comments[j].getCmtid() + "'", comments.class);
                                    if (ylist.size() < 1) {
                                        try {
                                            DbHelper.getDB(ServiceFriendsterActivity.this).save(comments[j]);
                                        } catch (DbException e) {
                                            UtilsLog.i(TAG, "save comments fail because DbException");
                                            e.printStackTrace();
                                        }
                                    } else {
                                        DbHelper.deletefriendstercomment(comments[j].getCmtid());
                                    }
                                }
                            }
                            likers[] likers = tw[i].getLikers();
                            if (likers.length > 0) {
                                for (int j = 0; j < likers.length; j++) {
                                    List<likers> zlist = DbHelper.QueryTData("select * from likers where likeid='" + likers[j].getLikeid() + "'", likers.class);
                                    if (zlist.size() < 1) {
                                        try {
                                            DbHelper.getDB(ServiceFriendsterActivity.this).save(likers[j]);
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            DbHelper.getDB(AppContext.getInstance()).delete(likers.class, WhereBuilder.b("likeid", "=", likers[j].getLikeid()));
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ShowData(true);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ShowData(false);
        if (intent.getExtras()!=null) {
            typefrom = intent.getExtras().getString("typefrom");
            if (typefrom!=null) {
                AppContext.selectphoto.clear();
                url.setLength(0);
                url = new StringBuffer();
                imglist = intent.getExtras().getString("imglist");
                nodatalist = intent.getStringArrayListExtra("imgalist");
                content = intent.getExtras().getString("content");
                albumid = intent.getExtras().getString("albumid");
                if (imglist.equals("")) {
                    sendGroupTwritte(content,"");
                } else {
                    index = 0;
                    allIndex = 0;
                    List<SimpleTask> list = new ArrayList<SimpleTask>();
                    for (int i = 0; i < nodatalist.size(); i++) {
                        list.add(getTask(i));
                    }
                    OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
                    for (int i = 0; i < nodatalist.size(); i++) {
                        executor.put(list.get(i));
                    }
                    executor.start();
                }
            }
        }
    }

    /**
     * 发送班级动态
     * @param content
     * @param url
     */
    private void sendGroupTwritte(final String content, String url) {
        index = 0;
        allIndex = 0;
        AppServer.getInstance().sendGroupTwitter(AppServer.getInstance().getAccountInfo().getUid(), cid, content, url, albumid, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    GroupTwritte tw = (GroupTwritte) obj;
                    tw.setStatus(0);
                    try {
                        DbHelper.getDB(ServiceFriendsterActivity.this).update(tw, WhereBuilder.b("twrid", "=", "-1"), "twrid", "status");
                        handler.sendEmptyMessage(MSG_FRESH);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    UtilsLog.i(TAG, "sendGroupTwritte success");
//                    showToast("发送成功");
                } else {
                    if (grouptwrittelist!=null) {
                        GroupTwritte tw = grouptwrittelist.get(0);
                        tw.setStatus(1);
                        try {
                            DbHelper.getDB(ServiceFriendsterActivity.this).update(tw, WhereBuilder.b("twrid", "=", "-1"), "status");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    UtilsLog.i(TAG, "sendGroupTwritte fail");
                    handler.sendEmptyMessage(MSG_SHOWTOAST);
//                    showToast("目前网络不给力，试试重新发送吧！");
                }
            }
        });
    }

    /**
     * 获取post提交参数
     * @param url
     * @return
     */
    public RequestParams getRequestParams(String url) {
        RequestParams requestParams = new RequestParams();
        File file = new File(url);
        String SAVE_KEY = "/{year}/{mon}/{day}/{filemd5}{.suffix}";

        // 取得base64编码后的policy
        String policy = null;
        try {
            policy = UpYunUtils.makePolicy(SAVE_KEY, UpYunUtils.EXPIRATION, UpYunUtils.CLASSGROUP_BUCKET);
        } catch (UpYunException e) {
            e.printStackTrace();
        }
        // 根据表单api签名密钥对policy进行签名

        String signature = UpYunUtils.signature(policy + "&" + UpYunUtils.CLASSGROUP_API_KEY);
        requestParams.addBodyParameter(AppConstants.POLICY, policy);
        requestParams.addBodyParameter(AppConstants.SIGNATURE, signature);
        requestParams.addBodyParameter("file", file);
        return requestParams;
    }

    /**
     * 获取url
     * @param bucket
     * @return
     */
    private static String getUrl(String bucket) {
        String host = "http://v0.api.upyun.com/";
        return host + bucket + "/";
    }

    private void uploadImage(final String path, final int i) {

        BitmapUtil.createSDCardDir();
        File f = new File(path);
        name = f.getName();
        HttpUtils http = new HttpUtils();
        BitmapUtil.save(path, name, PATH);
        RequestParams requestParams = getRequestParams(PATH + name);
        String URL = getUrl(UpYunUtils.CLASSGROUP_BUCKET);
        http.send(HttpMethod.POST, URL, requestParams, new RequestCallBack<String>() {

            @Override
            public void onStart() { }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                if (isUploading) {
                    DecimalFormat df = new DecimalFormat("##");
                    int value = Integer.parseInt(df.format((double)current / (double)total * 100));
//                  FriendsterGridviewAdapter.getCirclelist().get(i).setProgressNotInUiThread(value);
                }
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                allIndex++;
                if (nodatalist!=null && allIndex == nodatalist.size()) {
                    index = 0;
                    allIndex = 0;
                    handler.sendEmptyMessage(MSG_SHOWTOAST);
                    UtilsLog.i(TAG, "uploadImage fail");
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                JSONObject jobj;
                try {
                    jobj = new JSONObject(arg0.result);
                    imgurl = jobj.getString("url");
                    FileUtils.deleteFile(PATH + name);
                } catch (JSONException e) {
                    UtilsLog.i(TAG, "uploadImage success but JSONException");
                }
                index++;
                allIndex++;
                UtilsLog.e(TAG, "uploadImage index: " + index);
                if (allIndex!=nodatalist.size()) { // 还未完
                    UtilsLog.e("ServiceFriendster", "append url index : " + index);
                    url.append(ServiceFriendsterActivity.UP_URL + imgurl);
                    url.append(",");
                } else { // 已完成
                    if (index == allIndex) { // 成功的数量和失败的数量一样
                        url.append(ServiceFriendsterActivity.UP_URL + imgurl);
                        UtilsLog.e("ServiceFriendsterActivity", "ready to upload index: " + index);
                        sendGroupTwritte(content, url.toString());
                    } else {
                        index = 0;
                        allIndex = 0;
                        if (grouptwrittelist!=null) {
                            GroupTwritte tw = grouptwrittelist.get(0);
                            tw.setStatus(1);
                            try {
                                DbHelper.getDB(ServiceFriendsterActivity.this).update(tw, WhereBuilder.b("twrid", "=", "-1"), "status");
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(MSG_SHOWTOAST);
                        UtilsLog.i(TAG, "success number is not equal all number show toast fail");
//                        handler.sendEmptyMessage(MSG_FRESH);
                    }
                }
            }
        });
    }

    // 在SD卡上创建一个文件夹
    public void createSDCardDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = Environment.getExternalStorageDirectory();
            // 得到一个路径，内容是sdcard的文件夹路径和名字
            String path = sdcardDir.getPath() + "/yey/kindergaten/uploadimg/";
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

    @Override
    public void starrun(ArrayList<String> imglist, String content) {
        this.content = content;
        List<SimpleTask> list = new ArrayList<SimpleTask>();
        for (int i = 0; i < imglist.size(); i++) {
            list.add(getTask(i));
        }
        OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
        for (int i = 0; i < imglist.size(); i++) {
            executor.put(list.get(i));
        }
        executor.start();
        url.setLength(0);
        url = new StringBuffer();
    }

    @Override
    public void discuss(int position, int downposition) {
        this.position = position;
        if (adapter == null) {
            return;
        } else {
            View listItem = adapter.getView(position, null, lv);
            listItem.measure(0, 0);
            list_child_height = listItem.getMeasuredHeight() + lv.getDividerHeight();
            ll_input.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            ll_input.setLayoutParams(lp);
            et.setFocusable(true);
            et.requestFocus();
            showSoftInput(et);
            et.setHint("回复" + grouptwrittelist.get(position).getComment()[downposition].getRealname());
            touid = grouptwrittelist.get(position).getComment()[downposition].getUid();
        }
    }

    @Override
    public void showcomment(final int position) {
        this.position = position;
        if (adapter == null) {
            return;
        } else {
            View listItem = adapter.getView(position, null, lv);
            listItem.measure(0, 0);
            list_child_height = listItem.getMeasuredHeight()+lv.getDividerHeight();
            ll_input.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            ll_input.setLayoutParams(lp);
            et.setFocusable(true);
            et.requestFocus();
            showSoftInput(et);
            et.setHint("");
            touid = -1;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i = getResources().getIdentifier("headlayout_height", "dimen", "android");
        if (i > 0)
            mStatusHeight = getResources().getDimensionPixelSize(i);
        initKeyBoard();
    }
    private void initKeyBoard() {
        final View localView = findViewById(R.id.friendster_main_activity); // activity的根view_group
        localView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect localRect = new Rect();
                localView.getWindowVisibleDisplayFrame(localRect);
                int i = localView.getRootView().getHeight() - (localRect.bottom - localRect.top) - mStatusHeight;
                if (cKeyBoardHeight!=i) {
                    cKeyBoardHeight = i;
                    if (i > 0) {
                        WindowManager vm = (WindowManager) AppContext.getInstance().getSystemService(Context.WINDOW_SERVICE);
                        int c = vm.getDefaultDisplay().getHeight();
                        int a = ll_input.getHeight();
                        int b = head_layout.getHeight();
                        lv.setSelectionFromTop(position + 1, (c - i - a - b - list_child_height));
                    }
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShowData(false);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private SimpleTask<Integer> getTask(final int postion) {
        UtilsLog.e("ServiceFriendsterActivity", "任务" + postion);
        SimpleTask<Integer> simple = new SimpleTask<Integer>() {

            @Override
            protected Integer doInBackground() {
                uploadImage(nodatalist.get(postion), postion);
                return postion;
            }

            @Override
            protected void onCancelled() { }

            @Override
            protected void onPostExecute(Integer result) { }

        };
        return simple;
    }

    public void hideSoftInput(EditText et) {
        InputMethodManager imm = (InputMethodManager) et.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public  void showSoftInput(EditText et) {
        InputMethodManager imm = (InputMethodManager) et.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private View getGridView(final int i,final EmoticonsEditText et) {
        View view = View.inflate(AppContext.getInstance(), R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(1, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (et != null && !TextUtils.isEmpty(key)) {
                        int start = et.getSelectionStart();
                        CharSequence content = et.getText().insert(start, key);
                        et.setText(content);
                        CharSequence info = et.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void cleanDbhelper() {
        try {
            DbHelper.getDB(ServiceFriendsterActivity.this).delete(GroupTwritte.class, WhereBuilder.b("status", "=", "0"));
            DbHelper.getDB(ServiceFriendsterActivity.this).deleteAll(comments.class);
            DbHelper.getDB(ServiceFriendsterActivity.this).deleteAll(likers.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}