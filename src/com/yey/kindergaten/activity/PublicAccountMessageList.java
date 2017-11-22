/**
 * 系统项目名称
 * com.yey.kindergaten.activity
 * PublicAccountMessageList.java
 *
 * 2014年7月4日-下午4:47:49
 *  2014中幼信息科技公司-版权所有
 *
 */
package com.yey.kindergaten.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.PublicAccountMessageAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Message;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.PublicAccountMenu;
import com.yey.kindergaten.bean.PublicAccountMenu.SubMenu;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.PushReceiver;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.Session;
import com.yey.kindergaten.util.StringUtils;
import com.yey.kindergaten.widget.BottomMenuView;
import com.yey.kindergaten.widget.popubmenu.MyPopupMenu;
import com.yey.kindergaten.widget.popubmenu.PopupMenuItem;
import com.yey.kindergaten.widget.xlist.XListView;
import com.yey.kindergaten.widget.xlist.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公众号消息列表
 *
 * PublicAccountMessageList
 * chaowen
 * 511644784@qq.com
 * 2014年7月4日 下午4:47:49
 * @version 1.0.0
 */
public class PublicAccountMessageList extends BaseActivity implements OnItemClickListener,IXListViewListener,PushReceiver.EventHandler,Handler.Callback{
    @ViewInject(R.id.left_btn)ImageView left_iv;                            // 左箭头
    @ViewInject(R.id.lookdata_btn)ImageView right_iv;                       // 查看详情
    @ViewInject(R.id.header_title)TextView tv_headerTitle ;                 // 标题title
    @ViewInject(R.id.f_header)View header_view;                             // 标题栏
    @ViewInject(R.id.publicAccount_Message_lv)XListView messageLv;          // 消息列表listview
    @ViewInject(R.id.ll_loadMoreHistoryMsg)LinearLayout loadMoreHistoryMsg; // 查看更多历史消息布局
    @ViewInject(R.id.btm_menulayout)LinearLayout menulayout;                // 公众号菜单布局

    PublicAccountMessageAdapter pamAdapter;                                 // 消息列表适配器
    private List<MessagePublicAccount> paMessageList = new ArrayList<MessagePublicAccount>();  // 保存公众号消息的list
    private String fromId;
    private int typeId;
    private PublicAccount publicAccount;
    private AccountInfo accountInfo;
    private List<MyPopupMenu> popMenuList = new ArrayList<MyPopupMenu>();
    LinearLayout rootview_pa;
    List<PublicAccountMenu> menusList = null;
    public static final int NEW_MESSAGE = 0x001; // 公众号收到的消息
    public static int pageId = -1;
    String state = "";
    public int mcurrentMenu = 0;
    public int pagecount = 0;
    public int pageIndex = 0;
    public int width;
    private String type;
    private String intent_title = "";
    private String ishowLookData = "";
    private int isfirstlook;
    String titleName;

    private View headView;

    private Handler mHandler = new Handler(this);
    private static final int REFRESH_MESSAGE = 1;
    private static final int REFRESH_MESSAGE_TIME = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = this.getWindowManager().getDefaultDisplay().getWidth();
        setContentView(R.layout.public_account_messagelist);
        ViewUtils.inject(this);
        accountInfo = AppServer.getInstance().getAccountInfo();
        prepareView();
        initData();
    }

    private void prepareView() {
        left_iv.setVisibility(View.VISIBLE);

        // 获取fromId, isfirstlook, type, typeId, state.
        Session session = Session.getSession();
        fromId = (String) session.get(AppConstants.INTENT_KEY_FROMID);

        if (getIntent().getExtras()!=null) {
            isfirstlook = getIntent().getExtras().getInt("isFirstLook", 1);
            type = getIntent().getExtras().getString("type");
            intent_title = getIntent().getExtras().getString(AppConstants.INTENT_KEY_TITLE);
            ishowLookData = getIntent().getExtras().getString(AppConstants.INTENT_IS_SHOWLOOKDATA);
        }
        state = (String) session.get("state");
        Object session_typeid = session.get(AppConstants.INTENT_KEY_TYPEID);
        if (session_typeid != null) {
            typeId = (Integer) session.get(AppConstants.INTENT_KEY_TYPEID);
        }
        if (ishowLookData!=null && ishowLookData.equals("false")) {
            right_iv.setVisibility(View.GONE);
        } else {
            if (fromId!=null && (fromId.equals("16") || fromId.equals("17") || fromId.equals("18"))) {
                right_iv.setVisibility(View.GONE);
            } else {
                right_iv.setVisibility(View.VISIBLE);
                right_iv.setImageResource(R.drawable.lookdata);
            }
        }
        // 从数据库根据publicid查找该公众号
        try {
            publicAccount = DbHelper.getDB(AppContext.getInstance()).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", fromId));
            if (type == null || (type!=null && !type.equals("fromguide"))) {
                if (publicAccount != null) {
                    isfirstlook = publicAccount.getIsfirstlook();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (publicAccount == null) {
            publicAccount = new PublicAccount();
        }

        if (intent_title == null || intent_title.equals("")) {
            // 设置标题
            intent_title = publicAccount.getNickname() + "";
        }
        tv_headerTitle.setText(intent_title);

        initXListView();
    }


    private void initXListView() {
        // 首先不允许加载更多
        messageLv.setPullLoadEnable(false);
        // 允许下拉
        messageLv.setPullRefreshEnable(true);
        // 设置监听器
        messageLv.setXListViewListener(this);
        messageLv.pullRefreshing();
        messageLv.setDividerHeight(0);

        headView = LayoutInflater.from(this).inflate(R.layout.publicaccount_msg_listview_header, null);
        messageLv.addHeaderView(headView);
        headView.setVisibility(View.GONE);
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Session session = Session.getSession();
//                session.put(AppConstants.INTENT_KEY_FROMID, fromId + "");
//                openActivity(PublicAccountHistoryMessageList.class);
                Bundle bundle1 = new Bundle();
                String url = "";
                url = AppUtils.replacePubHistoryUrl(fromId + "", typeId + "");

                bundle1.putString(AppConstants.INTENT_URL, url);
                bundle1.putString(AppConstants.INTENT_NAME, intent_title);
                openActivity(CommonBrowser.class, bundle1);
            }
        });

        // 加载数据
        initOrRefresh();
        if (isfirstlook!=AppConstants.IS_FIRST_LOOK){
            messageLv.setSelection(pamAdapter.getCount() - 1);
        }

        // 指导界面进入,从第一条开始显示
        if (type!=null) {
            if (type.equals("fromguide")) {
                messageLv.setSelection(0);
            }
        }

        // 设置点击事件
        messageLv.setOnItemClickListener(this);
        messageLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (messageLv.getFirstVisiblePosition() == 0) {
                    headView.setVisibility(View.VISIBLE);
                } else {
                    headView.setVisibility(View.GONE);
                }
            }
        });
        messageLv.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                    int position, long arg3) {
                final int location = position;
                showDialogNoTitle("确认删除此会话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg) {
                        if (pamAdapter!= null && pamAdapter.getList()!= null && pamAdapter.getList().size() > location - 2) {
                            try {
                                DbHelper.getDB(AppContext.getInstance()).delete(MessagePublicAccount.class, WhereBuilder.b("pmid", "=", pamAdapter.getList().get(location - 2).getPmid()));
                                pamAdapter.remove(location - 2);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            pamAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                return true;
            }
        });
        // 重发按钮的点击事件
    }

    private void initData() {
        pagecount = getpageCount();
        if (publicAccount!=null && publicAccount.getIsmenu() == 1) {
            AppServer.getInstance().getPublicAccountMenu(fromId, new OnAppRequestListener() {

                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (code == AppServer.REQUEST_SUCCESS) {
                        menusList = (List<PublicAccountMenu>) obj;
                        if (menusList != null && menusList.size() > 0) {
                            menulayout.setVisibility(View.VISIBLE);
                            initMenu(menusList);
                        } else {
                            menulayout.setVisibility(View.GONE);
                        }
                    } else {
                        showToast("获取菜单失败");
                    }
                }
            });
        }
    }

    private  MyPopupMenu pop;

    @OnClick({R.id.left_btn,R.id.lookdata_btn,R.id.ll_loadMoreHistoryMsg})
    public void onclickView(View view){
        switch (view.getId()) {
        case R.id.left_btn:
            this.finish();
            break;
        case R.id.lookdata_btn:
            Intent intent = new Intent(this,ContactsPuacDatacardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("state", state);
            bundle.putInt("role", 2);
            if (fromId!=null && !fromId.equals("")) {
                bundle.putInt("publicid", Integer.parseInt(fromId));
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                showToast("获取公众号资料异常");
            }
            break;
        case R.id.ll_loadMoreHistoryMsg:
//            Session session = Session.getSession();
//            session.put(AppConstants.INTENT_KEY_FROMID, fromId + "");
//            openActivity(PublicAccountHistoryMessageList.class);
            // 换成web端
            Bundle bundle1 = new Bundle();
            String url = "";
            url = AppUtils.replacePubHistoryUrl(fromId + "", typeId + "");
            bundle1.putString(AppConstants.INTENT_URL, url);
            bundle1.putString(AppConstants.INTENT_NAME, publicAccount.getNickname());
            openActivity(CommonBrowser.class, bundle1);
            break;
        default:
            break;
        }
    }

    View.OnClickListener menuOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object object = view.getTag();
            int tag = Integer.parseInt(object.toString());
            mcurrentMenu = tag;
            List<PopupMenuItem> list = subMap.get(tag);
            if (list!=null && list.size()!=0) {
                pop = new MyPopupMenu(PublicAccountMessageList.this, list, menuItemclick);
                popMenuList.add(pop);
                pop.show(view);
            } else {
                Toast.makeText(PublicAccountMessageList.this,"暂未开放，尽请期待",Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void showWhatPage(int fromId){
        Intent intent = new Intent(this, WizardActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("type","from_PublicAccountMessage");
        switch (fromId){
            case AppConstants.TIMETREE_DO_DIRECTOR:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DO_DIRECTOR);
                break;
            case AppConstants.TIMETREE_DO_TEACHER:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DO_TEACHER);
                break;
            case AppConstants.TIMETREE_DO_PARENT:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DO_PARENT);
                break;
            case AppConstants.TIMETREE_DIRECTOR_PUBLIC:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DIRECTOR_PUBLIC);
                break;
            case AppConstants.TIMETREE_TEACHER_PUBLIC:
                intent.putExtra("fromdId", AppConstants.TIMETREE_TEACHER_PUBLIC);
                break;
            case AppConstants.TIMETREE_PARENT_PUBLIC:
                intent.putExtra("fromdId", AppConstants.TIMETREE_PARENT_PUBLIC);
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }



    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        paMessageList = pamAdapter.getList();

        if (paMessageList == null || paMessageList.size() == 0) {
            mHandler.sendEmptyMessageDelayed(REFRESH_MESSAGE, REFRESH_MESSAGE_TIME);
            if (paMessageList.size() == 0) {
                return;
            }
        }

        if (paMessageList.get(position - 2).getPmid() == 0) {
            showWhatPage(paMessageList.get(position - 2).getPublicid());
            return;
        }

        if (paMessageList.get(position - 2).getContenttype() == AppConstants.TYPE_IMAGE_TEXT || paMessageList.get(position-2).getContenttype() == AppConstants.TYPE_VIDEO
                || paMessageList.get(position - 2).getContenttype() == AppConstants.TYPE_NO_IMAGE_TEXT) {
            Bundle bundle = new Bundle();
            if (paMessageList.get(position - 2).getTitle().contains("班级相册")||paMessageList.get(position - 2).getTitle().contains("在园剪影")
                    || paMessageList.get(position - 2).getTitle().contains("手工作品") || paMessageList.get(position - 2).getContenturl().contains("view=0")){
                String contentUrl = paMessageList.get(position - 2).getContenturl();
                if (contentUrl!=null && contentUrl.contains("api=1")) {
                    String mode = "";
                    String replace = "";
                    if (!StringUtils.getValue(contentUrl, "mode=").equals("")
                            && (StringUtils.getValue(contentUrl, "mode=").equals("0") || StringUtils.getValue(contentUrl, "mode=").equals("1"))) {
                        mode = StringUtils.getValue(contentUrl, "mode=");
                    } else {
                        mode = "0";
                    }
                    if (!StringUtils.getValue(contentUrl, "replace=").equals("")) {
                        replace = StringUtils.getValue(contentUrl, "replace=");
                    } else {
                        replace = "";
                    }
                    Intent showPhotoIntent;
                    showPhotoIntent = new Intent(this, PhotoShowGeneralActivity.class);
                    showPhotoIntent.putExtra("openType", mode + "");
                    showPhotoIntent.putExtra("api", contentUrl.replace("{result}", "1"));
                    showPhotoIntent.putExtra("replace", replace);
                    startActivity(showPhotoIntent);
                } else {
                    bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUnifiedUrl(paMessageList.get(position - 2).getContenturl()));
                    bundle.putString(AppConstants.INTENT_NAME, paMessageList.get(position - 2).getTitle() + "");
                    openActivity(CommonBrowser.class, bundle);
                }
            } else {
                if (paMessageList.get(position - 2).getContenturl()!=null && !paMessageList.get(position - 2).getContenturl().equals("")) {

                    String contentUrl = paMessageList.get(position - 2).getContenturl();

                    if (contentUrl!=null && contentUrl.contains("api=1")) {
                        String mode = "";
                        String replace = "";
                        if (!StringUtils.getValue(contentUrl, "mode=").equals("")
                                && (StringUtils.getValue(contentUrl, "mode=").equals("0") || StringUtils.getValue(contentUrl, "mode=").equals("1"))) {
                            mode = StringUtils.getValue(contentUrl, "mode=");
                        } else {
                            mode = "0";
                        }
                        if (!StringUtils.getValue(contentUrl, "replace=").equals("")) {
                            replace = StringUtils.getValue(contentUrl, "replace=");
                        } else {
                            replace = "";
                        }
                        Intent showPhotoIntent;
                        showPhotoIntent = new Intent(this, PhotoShowGeneralActivity.class);
                        showPhotoIntent.putExtra("openType", mode + "");
                        showPhotoIntent.putExtra("api", contentUrl.replace("{result}", "1"));
                        showPhotoIntent.putExtra("replace", replace);
                        startActivity(showPhotoIntent);
                    } else {
                        bundle.putString("type", AppConstants.PARAM_PublicAccount);
                        bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUnifiedUrl(paMessageList.get(position - 2).getContenturl()));
                        bundle.putString(AppConstants.INTENT_NAME, paMessageList.get(position - 2).getTitle() + "");
                        openActivity(CommonBrowser.class, bundle);
                    }
                }
            }
        } else {
            return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
            try {
                paMessageList = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).and(WhereBuilder.b("typeid", "=", typeId)).limit(AppConstants.PAGESIZE).offset(AppConstants.PAGESIZE*AppConstants.PAGEINDEX));
                //paMessageList = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())));
                publicAccount = DbHelper.getDB(this).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", fromId));
            } catch (DbException e) {
                e.printStackTrace();
            }
        pamAdapter.getList().clear();
        pamAdapter.addAll(paMessageList);
    }

    private OnItemClickListener menuItemclick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterview, View view, int i, long l) {
            pop.dissmiss();
            List<PopupMenuItem> plist = subMap.get(mcurrentMenu);
            if (plist!=null && plist.size()!=0) {
                PopupMenuItem item =  plist.get(i);
                if ((item.getUrl() != null) && (item.getUrl().length() > 0)) {
                    //打开浏览器
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl(item.getUrl()));
                    bundle.putString(AppConstants.INTENT_NAME, item.getTitle());
                    openActivity(CommonBrowser.class, bundle);
                } else {
                   showToast("暂未开放");
                }
            }
        }
    };

    /**
     * 组装自定义菜单
     *
     * initMenu
     * @param menulist
     * void
     * @exception
     * @since  1.0.0
     */
    private Map<Integer,List<PopupMenuItem>> subMap = new HashMap<Integer, List<PopupMenuItem>>();
    public void initMenu(List<PublicAccountMenu> menulist) {
        if (menulist.size() > 0) {
            for (int i = 0; i < menulist.size(); i++){
                List<PopupMenuItem> itemList = new ArrayList<PopupMenuItem>();
                PublicAccountMenu pm = menulist.get(i);
                BottomMenuView bottomMenuView = new BottomMenuView(PublicAccountMessageList.this);
                bottomMenuView.setLayoutParams(new FrameLayout.LayoutParams(width/menulist.size(), FrameLayout.LayoutParams.FILL_PARENT));
                bottomMenuView.setTitle(pm.getName());
                bottomMenuView.setTextSize(18);
                bottomMenuView.setTag(i);
                bottomMenuView.setOnClickListener(menuOnclickListener);
                if ((pm.getSub()!=null) && (pm.getSub().size() > 0)) {
                    bottomMenuView.setIshaveSub(true);
                } else {
                    bottomMenuView.setIshaveSub(false);
                }
                menulayout.addView(bottomMenuView);
                menulayout.setGravity(Gravity.CENTER);
                for (SubMenu s: pm.getSub()) {
                    PopupMenuItem pi = new PopupMenuItem(s.getTag(), R.drawable.ic_launcher, s.getName(),s.getAction()+"",s.getUrl());
                    itemList.add(pi);
                }
                subMap.put(i,itemList );
            }
        }
    }

    @Override
    public void onMessage(Message message) {
        android.os.Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onBind(String method, int errorCode, String content) { }

    @Override
    public void onNotify(String title, String content) { }

    @Override
    public void onNetChange(boolean isNetConnected) { }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        // 新消息到达，重新刷新界面
        //initOrRefresh();
        PushReceiver.ehList.add(this);// 监听推送的消息
        //清空消息未读数-这个要在刷新之后
        PushReceiver.mNewNum=0;
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        AppContext.getInstance().getNotificationManager().cancel(PushReceiver.NOTIFY_ID);
    }

    // 有新消息:逆序添加新消息刷新; 没有新消息:直接刷新;
    private void initOrRefresh() {
        List<MessagePublicAccount> messagePublicAccounts = initMsgData();
        if (pamAdapter != null) {
            if (PushReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news =  PushReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = messagePublicAccounts.size();
                // 如果只有一条,直接加到后面，如果大于一条,逆序！
                if (size == 1) {
                    pamAdapter.add(messagePublicAccounts.get(size-1));
                } else {
                    for (int i=news;i>=0;i--) {
                        pamAdapter.add(messagePublicAccounts.get(size-(i+1)));// 添加最后一条消息到界面显示
                    }
                }
                /*if (size <= 0){
                    //显示历史消息
                    loadMoreHistoryMsg.setVisibility(View.VISIBLE);
                }*/
                // 如果是首次查看,设置查看位置到底部
                if (isfirstlook!=AppConstants.IS_FIRST_LOOK){
                    messageLv.setSelection(pamAdapter.getCount() - 1);
                }
            } else {
                pamAdapter.notifyDataSetChanged();
            }
        } else {
            pamAdapter = new PublicAccountMessageAdapter(this, messagePublicAccounts);
            messageLv.setAdapter(pamAdapter);
        }
        if (messagePublicAccounts.size() <= 3){
            //显示历史消息
            headView.setVisibility(View.VISIBLE);
        }
        if (publicAccount!=null){
//                    && publicAccount.getLoadhistory()==0
            if (paMessageList!=null && paMessageList.size()<=1) {
                loadHistoryMessage();
            }
        }
    }

    /**
     * 加载消息历史，从数据库中读出
     *
     * @throws DbException
     */
    private List<MessagePublicAccount> initMsgData(){
        List<MessagePublicAccount> list = new ArrayList<MessagePublicAccount>();

        try {
            publicAccount = DbHelper.getDB(this).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", fromId));
            Selector selector ;
            if (isfirstlook != AppConstants.IS_FIRST_LOOK) {
                selector = Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).and(WhereBuilder.b("typeid", "=", typeId)).orderBy("date desc,pmid",true).limit(AppConstants.PAGESIZE).offset(AppConstants.PAGESIZE*pageIndex);
            } else {
                selector = Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).and(WhereBuilder.b("typeid", "=", typeId)).orderBy("date desc,pmid",true).limit(8).offset(AppConstants.PAGESIZE*pageIndex);
            }
            list = DbHelper.getDB(this).findAll(selector);

            if (publicAccount == null) {
                publicAccount = new PublicAccount();
            }
//            MessagePublicAccount messagePublicAccount = DbHelper.getDB(AppContext.getInstance()).findFirst(MessagePublicAccount.class,WhereBuilder.b("pmid","=",0));
//            if(messagePublicAccount==null){
//                for(String public_id:AppConstants.SPECIAL_PUBLIC_IDS){
//                    if(public_id.equals(fromId)){
//                        MessagePublicAccount account = new MessagePublicAccount();
//                        account.setTitle(publicAccount.getNickname()+"介绍");
//                        account.setFiledesc(publicAccount.getNickname() + "能为你做什么？");
//                        account.setPublicid(Integer.valueOf(fromId));
//                        account.setShareable(-1);//是否支持分享
//                        account.setPmid(0);//公众号消息id
//                        account.setTypeid(-1);//公众号子分类类型
//                        account.setToId(AppServer.getInstance().getAccountInfo().getUid());
//                        account.setContenttype(4);//图文消息
//                        account.setAction(1);//公众号类型
//                        //account.setDate(TimeUtil.getYMDHMS());
//                        list.add(account);
//                        List<MessagePublicAccount>newMessageList = new ArrayList<MessagePublicAccount>();
//                        newMessageList.add(account);
//                        List<MessagePublicAccount> messagePublicAccountList = DbHelper.getDB(AppContext.getInstance()).findAll(MessagePublicAccount.class);
//                        if(messagePublicAccountList!=null){
//                            newMessageList.addAll(messagePublicAccountList);
//                        }
//                        DbHelper.getDB(AppContext.getInstance()).deleteAll(MessagePublicAccount.class);
//                        DbHelper.getDB(AppContext.getInstance()).saveAll(newMessageList);
//                        break;
//                    }
//                }
//            }

            paMessageList = list;

            //list = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).orderBy("date", true).limit(AppConstants.PAGESIZE).offset(AppConstants.PAGESIZE*pageIndex));

            //list = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid()+"")));
            if (list == null){
                list = new ArrayList<MessagePublicAccount>();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        Collections.reverse(list);
        return list;
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        PushReceiver.ehList.remove(this);// 监听推送的消息
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == NEW_MESSAGE) {
				Message message = (Message) msg.obj;
				PublicAccount tagetPublicAccount = null;
				try {
					tagetPublicAccount = DbHelper.getDB(AppContext.getInstance()).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", message.getPublicid()));
				} catch (DbException e) {
					e.printStackTrace();
				}
				if (tagetPublicAccount!=null) {
					MessagePublicAccount messagePublicAccount = new MessagePublicAccount(message.getPmid(), message.getTitle(), message.getAction(), message.getDate(), message.getToid(), message.getPublicid(), message.getContenturl(), message.getUrl(), message.getFileurl(), message.getFiledesc(), message.getContenttype(), message.getShareable(),tagetPublicAccount.getNickname(),tagetPublicAccount.getAvatar(),message.getTypeid(), message.getOptag());
					int pid = messagePublicAccount.getPublicid();
					if (typeId != messagePublicAccount.getTypeid() )// 如果不是当前正在看的公众号，不处理
						return;
					if (AppServer.getInstance().getAccountInfo().getUid()==message.getToid()){
                        if (0 == messagePublicAccount.getOptag()){
                            //删除
                            MessagePublicAccount mp = null;
                            for (int i = 0; i < paMessageList.size(); i++){
                                mp = paMessageList.get(i);
                                if (mp.getPmid() == messagePublicAccount.getPmid()) {
                                    paMessageList.remove(mp);
                                    pamAdapter.setList(paMessageList);
                                }
                            }
                        } else {
                            MessagePublicAccount mp = null;
                            try {
                                mp = DbHelper.getDB(AppContext.getInstance()).findFirst(MessagePublicAccount.class, WhereBuilder.b("pmid", "=", messagePublicAccount.getPmid()));
                                if (mp == null) {
                                    for (MessagePublicAccount p: paMessageList) {
                                        if (p.getPmid()!=messagePublicAccount.getPmid()) {
                                            pamAdapter.add(messagePublicAccount);
                                        }
                                    }

                                } else {
                                    for (int i = 0; i < paMessageList.size(); i++) {
                                        mp = paMessageList.get(i);
                                        if (mp.getPmid() == messagePublicAccount.getPmid()){
                                            paMessageList.set(i,messagePublicAccount);
                                            pamAdapter.setList(paMessageList);
                                        }
                                    }
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
						// 定位
                        if (isfirstlook != AppConstants.IS_FIRST_LOOK) {
                            messageLv.setSelection(pamAdapter.getCount() - 1);
                        }
						//paMessageList.add(messagePublicAccount);
					}
				}
			}
		}
	};

    @Override
    public void onRefresh() {
        headView.setVisibility(View.GONE);
        if (pageIndex!=pagecount) {
            ++pageIndex;
            try {
                List<MessagePublicAccount> templist = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).and(WhereBuilder.b("typeid", "=", typeId)).orderBy("date desc,pmid", true).limit(AppConstants.PAGESIZE).offset(AppConstants.PAGESIZE*pageIndex));
                if (templist!=null && templist.size() > 0){
//                    headView.setVisibility(View.VISIBLE);
                    List<MessagePublicAccount> newMess = new ArrayList<MessagePublicAccount>();
                    newMess.addAll(sortList(templist));
                    if (pamAdapter!=null) {
                        pamAdapter.setList(newMess);
                    } else {
                        pamAdapter = new PublicAccountMessageAdapter(PublicAccountMessageList.this, newMess);
                        pamAdapter.setList(newMess);
                    }
                    paMessageList.clear();
                    paMessageList.addAll(newMess);
                } else {
                    messageLv.setPullRefreshEnable(false);
                    //显示历史消息
//                    headView.setVisibility(View.VISIBLE);
                }
                messageLv.stopRefresh();
                if (pageIndex == pagecount - 1) {
                    messageLv.setPullRefreshEnable(false);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

}

    @Override
    public void onLoadMore() { }

    @Override
    public void onRefreshData(String title) { }

    public int getpageCount(){
        int totalNum = 0;
        try {
            List<MessagePublicAccount> mplist = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).and(WhereBuilder.b("typeid", "=", typeId)));
            int count = mplist.size();

            totalNum = count / AppConstants.PAGESIZE + 1;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return totalNum ;
    }

    public List<MessagePublicAccount> sortList(List<MessagePublicAccount> tmplist){
        List<MessagePublicAccount> newList = new ArrayList<MessagePublicAccount>();
        Collections.reverse(tmplist);
        newList.addAll(tmplist);
        newList.addAll(pamAdapter.getList());

        //Collections.reverse(newList);
        return newList;
}

    public void loadHistoryMessage(){
        publicAccount.setLoadhistory(1);
        try {
            DbHelper.getDB(AppContext.getInstance()).update(publicAccount, WhereBuilder.b("publicid", "=", publicAccount.getPublicid()), new String[]{"loadhistory"});
        } catch (DbException e) {
            e.printStackTrace();
        }
        AccountInfo info = AppContext.getInstance().getAccountInfo();
        int f_id;
        try {
            f_id = Integer.valueOf(fromId);
        } catch (NumberFormatException e){
            f_id = 0;
        }
        AppServer.getInstance().getPublicLateMessage(info.getUid(), f_id, typeId, new OnAppRequestListener() {

            @Override
            public void onAppRequest(int code, String message, Object obj) {
                List<MessagePublicAccount> newlist = new ArrayList<MessagePublicAccount>();
                if (code == AppServer.REQUEST_SUCCESS) {
                    newlist = (List<MessagePublicAccount>) obj;
                    if (newlist!=null && newlist.size()>0){
                        paMessageList = newlist;
                        for (int i=0;i<newlist.size();i++) {
                            MessagePublicAccount m = newlist.get(i);
                            m.setToId(AppContext.getInstance().getAccountInfo().getUid());
                            newlist.set(i, m);
                        }
                        paMessageList = sortList(newlist);
                        updateNewsPublicAccounts(paMessageList);
                        List<MessagePublicAccount> list =  initMsgData();
                        pamAdapter = new PublicAccountMessageAdapter(PublicAccountMessageList.this, list);
                        messageLv.setAdapter(pamAdapter);
                        if(isfirstlook != AppConstants.IS_FIRST_LOOK){
                        messageLv.setSelection(pamAdapter.getCount() - 1);}
                    } else {
                        pamAdapter = new PublicAccountMessageAdapter(PublicAccountMessageList.this,paMessageList);
                        messageLv.setAdapter(pamAdapter);
                    }
                } else {
                    showToast(message);
                    pamAdapter = new PublicAccountMessageAdapter(PublicAccountMessageList.this,newlist);
                    messageLv.setAdapter(pamAdapter);
                }
            }
        });
    }

    //获取最新的公众号消息更新到数据库
    public void updateNewsPublicAccounts(List<MessagePublicAccount> mpa){
        try {
            for (MessagePublicAccount mp: mpa) {
                MessagePublicAccount m = DbHelper.getDB(AppContext.getInstance()).findFirst(MessagePublicAccount.class,WhereBuilder.b("pmid", "=", mp.getPmid()));
                if (m == null){
                    DbHelper.getDB(AppContext.getInstance()).save(mp);
                } else {
                    //更新
                    DbHelper.getDB(AppContext.getInstance()).delete(m);
                    DbHelper.getDB(AppContext.getInstance()).save(mp);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (publicAccount!=null && publicAccount.getIsfirstlook() == 0){
                publicAccount.setIsfirstlook(1);
                DbHelper.getDB(AppContext.getInstance()).update(publicAccount, WhereBuilder.b("publicid", "=", fromId));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (null != mHandler) {
            mHandler.removeMessages(REFRESH_MESSAGE);
        }
    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case REFRESH_MESSAGE:
                initOrRefresh();
                break;
            default:
                break;
        }
        return false;
    }
}
