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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.receive.PushReceiver;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.Session;
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
 * 公众号历史消息列表
 * PublicAccountMessageList
 * chaowen
 * 511644784@qq.com
 * 2014年7月4日 下午4:47:49
 * @version 1.0.0
 *
 */
public class PublicAccountHistoryMessageList extends BaseActivity implements OnItemClickListener,IXListViewListener,PushReceiver.EventHandler{
	@ViewInject(R.id.left_btn)ImageView left_iv;
	@ViewInject(R.id.lookdata_btn)ImageView right_iv;
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.publicAccount_Message_lv)XListView messageLv;
	PublicAccountMessageAdapter pamAdapter;
	private List<MessagePublicAccount> paMessageList = new ArrayList<MessagePublicAccount>();
    private String fromId;
    private PublicAccount publicAccount;
    @ViewInject(R.id.customMenuMessageLayout)FrameLayout fl_customMenu;
    private List<BottomMenuView> bottomMenuList = new ArrayList<BottomMenuView>();
    private List<MyPopupMenu> popMenuList = new ArrayList<MyPopupMenu>();
    LinearLayout rootview_pa;
    List<PublicAccountMenu> menusList = null;
    public static final int NEW_MESSAGE = 0x001;// 收到消息
    public static int pageId = -1;
    public int mcurrentMenu = 0;
    public int pagecount = 0;
    public int pageIndex = 0;
    public int currentnextid = -1;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.public_account_messagelist);
    	ViewUtils.inject(this);
    	prepareView();
    	initData();
    }

	private void prepareView() {

		left_iv.setVisibility(View.VISIBLE);
		right_iv.setVisibility(View.GONE);

		Session session = Session.getSession();
		fromId = (String) session.get(AppConstants.INTENT_KEY_FROMID);

			try {
				//paMessageList = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).orderBy("date", true).limit(AppConstants.PAGESIZE).offset(AppConstants.PAGESIZE*AppConstants.PAGEINDEX));
				publicAccount = DbHelper.getDB(this).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", fromId));
			    if(publicAccount==null){
			    	publicAccount = new PublicAccount();
			    	publicAccount.setNickname("公众号");
			    }
			} catch (DbException e) {
				e.printStackTrace();
			}
		tv_headerTitle.setText(publicAccount.getNickname());
		initXListView();
		loadHistoryMessage(currentnextid);
	}


	private void initXListView() {
		// 首先不允许加载更多
		messageLv.setPullLoadEnable(true);
		// 允许下拉
		messageLv.setPullRefreshEnable(false);
		// 设置监听器
		messageLv.setXListViewListener(this);
		messageLv.pullRefreshing();
		messageLv.setDividerHeight(0);
		// 加载数据
		initOrRefresh();
		//messageLv.setSelection(pamAdapter.getCount() - 1);
		messageLv.setOnItemClickListener(this);

		// 重发按钮的点击事件

	}


	private void initData() {
		pagecount = getpageCount();
	/*	AppServer.getInstance().getPublicAccountMenu(fromId, new OnAppRequestListener() {

			@Override
			public void onAppRequest(int code, String message, Object obj) {
				if(code == AppServer.REQUEST_SUCCESS){
					menusList = (List<PublicAccountMenu>) obj;
					initMenu(menusList);
				}else{
					showToast("获取菜单失败");
				}

			}
		});*/

		//messageLv.setOnItemClickListener(this);
	}
	private MyPopupMenu pop,pop2,pop3,pop4;
	@OnClick({R.id.left_btn,R.id.lookdata_btn})
	public void onclickView(View view){
		switch (view.getId()) {
		case R.id.left_btn:
			this.finish();
			break;

		case R.id.lookdata_btn:
			   Intent  intent=new Intent(this,ContactsPuacDatacardActivity.class);
	    	   Bundle bundle=new Bundle();
	    	   bundle.putString("state", AppConstants.PUACFRAGMENT_LOOKPUAC);
	    	   bundle.putInt("role", 2);
	 		   bundle.putInt("publicid", Integer.parseInt(fromId));
	 		   intent.putExtras(bundle);
	 		   startActivity(intent);
				break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		Bundle bundle = new Bundle();
		bundle.putString(AppConstants.INTENT_URL, paMessageList.get(position-1).getContenturl());
		bundle.putString(AppConstants.INTENT_NAME,publicAccount.getNickname());
		openActivity(CommonBrowser.class,bundle);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

			try {
				paMessageList = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).limit(AppConstants.PAGESIZE).offset(AppConstants.PAGESIZE*AppConstants.PAGEINDEX));
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
		public void onItemClick(AdapterView<?> adapterview, View view, int i,
				long l) {
			showToast("点击");
			List<PopupMenuItem> plist = subMap.get(mcurrentMenu);
					PopupMenuItem item = (PopupMenuItem) plist.get(i);
					 if(item.getAction().equals("0")){

							//打开浏览器
							Bundle bundle = new Bundle();
							bundle.putString(AppConstants.INTENT_URL, item.getUrl());
							bundle.putString(AppConstants.INTENT_NAME,item.getTitle());
							openActivity(CommonBrowser.class,bundle);
						}else{
							//获取api的消息

						}


		}

	/*	@Override
		public void onItemClick(PopupMenuItem item) {
			if(item.getAction().equals("0")){

				//打开浏览器
				Bundle bundle = new Bundle();
				bundle.putString(AppConstants.INTENT_URL, item.getUrl());
				bundle.putString(AppConstants.INTENT_NAME,item.getTitle());
				openActivity(CommonBrowser.class,bundle);
			}else{
				//获取api的消息

			}

		}*/
	};

	/**
	 * 组装自定义菜单
	 * initMenu
	 * @param menulist
	 * void
	 * @exception
	 * @since  1.0.0
	 */
	private Map<Integer,List<PopupMenuItem>> subMap = new HashMap<Integer, List<PopupMenuItem>>();
	public void initMenu(List<PublicAccountMenu> menulist){

		if(menulist.size()>0){
			fl_customMenu.setVisibility(View.VISIBLE);
			for(int i=0;i<menulist.size();i++){
				List<PopupMenuItem> itemList = new ArrayList<PopupMenuItem>();
				PublicAccountMenu pm = menulist.get(i);
				bottomMenuList.get(i).setVisibility(View.VISIBLE);
				bottomMenuList.get(i).setTitle(pm.getName());
				bottomMenuList.get(i).setTextSize(18);
				for(SubMenu s: pm.getSub()){
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		// 新消息到达，重新刷新界面
		//initOrRefresh();
		PushReceiver.ehList.add(this);// 监听推送的消息
		//清空消息未读数-这个要在刷新之后
		PushReceiver.mNewNum=0;
		// 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
		AppContext.getInstance().getNotificationManager().cancel(
				PushReceiver.NOTIFY_ID);

	}

	private void initOrRefresh() {
		if (pamAdapter != null) {
			pamAdapter.notifyDataSetChanged();
		} else {
			pamAdapter = new PublicAccountMessageAdapter(this, paMessageList);
			messageLv.setAdapter(pamAdapter);

		}

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
                if(message!=null) {
                    MessagePublicAccount messagePublicAccount = new MessagePublicAccount(message.getPmid(), message.getTitle(), message.getAction(), message.getDate(), message.getToid(), message.getPublicid(), message.getContenturl(), message.getUrl(), message.getFileurl(), message.getFiledesc(), message.getContenttype(), message.getShareable(), message.getName(), tagetPublicAccount.getAvatar(), message.getTypeid(), message.getOptag());
                    int pid = messagePublicAccount.getPublicid();
                    if (pid != publicAccount.getPublicid())// 如果不是当前正在看的公众号，不处理
                        return;
                    if(AppServer.getInstance().getAccountInfo().getUid()==message.getToid()){
                        pamAdapter.add(messagePublicAccount);
                        // 定位
                        messageLv.setSelection(pamAdapter.getCount() - 1);
                    }
                }
			}
		}
	};
	@Override
	public void onRefresh() {
		if(pageIndex!=pagecount){
			++pageIndex;
			try {
				List<MessagePublicAccount>  templist = DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())).orderBy("date", true).limit(AppConstants.PAGESIZE).offset(AppConstants.PAGESIZE*pageIndex));
				if(templist!=null && templist.size()>0){
					pamAdapter.setList(sortList(templist));
				}else{
					messageLv.setPullRefreshEnable(false);
				}

				messageLv.stopRefresh();
				if(pageIndex == pagecount-1){
					messageLv.setPullRefreshEnable(false);
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onLoadMore() {
		if (currentnextid != -1 && flag) {
            loadHistoryMessage(currentnextid);
		}
	}


	@Override
	public void onRefreshData(String title) {

	}

	public int getpageCount(){
		int totalNum = 0;
		try {
			List<MessagePublicAccount> mplist =	DbHelper.getDB(this).findAll(Selector.from(MessagePublicAccount.class).where("publicid","=",fromId).and(WhereBuilder.b("toid", "=", AppContext.getInstance().getAccountInfo().getUid())));
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

    private boolean flag = false;
    public void loadHistoryMessage(int nextid){
        flag = false;
        if (nextid == 0){
            messageLv.setPullLoadEnable(false);
        } else {
            AccountInfo info = AppContext.getInstance().getAccountInfo();
            if(fromId==null||fromId.length()==0){
                return;
            }
            fromId.trim();
                AppServer.getInstance().getPublicHistoryMessage(info.getUid(), Integer.parseInt(fromId), AppConstants.PAGESIZE, nextid, new OnAppRequestListenerFriend() {

                    @Override
                    public void onAppRequestFriend(int code, String message, Object obj,
                            int nextid) {
                        messageLv.stopLoadMore();
                        List<MessagePublicAccount> newlist = new ArrayList<MessagePublicAccount>();
                        if(code == AppServer.REQUEST_SUCCESS){
                            newlist = (List<MessagePublicAccount>) obj;
                            if (newlist!=null && newlist.size() > 0){
                                paMessageList.addAll(newlist);
                            }
                            currentnextid = nextid;
                            if (nextid == 0){
                                messageLv.setPullLoadEnable(false);
                            }
                        } else {
                            showToast(message);
                        }
                        pamAdapter = new PublicAccountMessageAdapter(PublicAccountHistoryMessageList.this, paMessageList);
                        messageLv.setAdapter(pamAdapter);
                        if (newlist!=null) {
                            messageLv.setSelection(pamAdapter.getCount() - newlist.size());
                        }
                        flag = true;
                    }
                });
        }
    }
}
