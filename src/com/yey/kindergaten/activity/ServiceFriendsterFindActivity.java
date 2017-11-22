package com.yey.kindergaten.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.FriendsterActivityAdapterSelf;
import com.yey.kindergaten.bean.Twitter;
import com.yey.kindergaten.bean.TwitterSelf;
import com.yey.kindergaten.bean.TwitterSelf.CommentsSelf;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.widget.xlist.XListView;
import com.yey.kindergaten.widget.xlist.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceFriendsterFindActivity extends BaseActivity implements IXListViewListener{

	@ViewInject(R.id.header_title)TextView tv_header;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	//@ViewInject(R.id.header_layout)FrameLayout ft;
	@ViewInject(R.id.lv_activity_service_friendster_item)
	static XListView lv;
	private static FriendsterActivityAdapterSelf adapter;
	private static List<TwitterSelf> list = new ArrayList<TwitterSelf>();
	private static List<CommentsSelf> allist = new ArrayList<CommentsSelf>();
	private static List<CommentsSelf> zlist = new ArrayList<CommentsSelf>();
	private static int tuid;
	private View footView;
	private Handler mHandler;
	private int NextId;
	private boolean flag ;
	private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_friendster_alon);
        ViewUtils.inject(this);
        initView();
        loaddata();
    }

    private void cleanDbhelper() {
        try {
            DbHelper.getDB(ServiceFriendsterFindActivity.this).deleteAll(TwitterSelf.class);
            DbHelper.getDB(ServiceFriendsterFindActivity.this).deleteAll(CommentsSelf.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void loaddata() {
        ShowData();
        AppServer.getInstance().getTwittersByUid(AppServer.getInstance().getAccountInfo().getUid(),tuid, 10, -1, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, int nextid){
                if (code == AppServer.REQUEST_SUCCESS) {
                    NextId = nextid;
                    int a = AppServer.getInstance().getAccountInfo().getUid();
                    TwitterSelf[] tw = (TwitterSelf[]) obj;
                    if (tw.length > 0) {
                        cleanDbhelper();
                        for (int i = 0; i < tw.length; i++) {
                            List<TwitterSelf> xlist = DbHelper.QueryTData("select * from TwitterSelf where twrid='" + tw[i].getTwrid() + "'", TwitterSelf.class);
                            if (xlist.size() < 1) {
                                try {
                                    DbHelper.getDB(ServiceFriendsterFindActivity.this).save(tw[i]);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                            CommentsSelf[] commentsself = tw[i].getComment();
                            if (commentsself.length > 0) {
                                for (int j = 0; j < commentsself.length; j++) {
                                    List<CommentsSelf> ylist = DbHelper.QueryTData("select * from CommentsSelf where cmtid='" + commentsself[j].getCmtid() + "'", CommentsSelf.class);
                                    if (ylist.size() < 1) {
                                        try {
                                            DbHelper.getDB(ServiceFriendsterFindActivity.this).save(commentsself[j]);
                                        } catch (DbException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        cleanDbhelper();
                    }
                    ShowData();
                }

            }

        });
    }

    private void initView() {
        iv_right.setVisibility(View.GONE);
        iv_left.setVisibility(View.VISIBLE);
        lv.setPullLoadEnable(true);
        lv.setDivider(new ColorDrawable(0xffd4d5d6));
        lv.setDividerHeight(1);
        tuid = getIntent().getExtras().getInt("tuid");
        name = getIntent().getExtras().getString("name");
        tv_header.setText(name);
        if (tuid == AppServer.getInstance().getAccountInfo().getUid()) {
            flag = true;
        } else {
            flag = false;
        }
        adapter = new FriendsterActivityAdapterSelf(ServiceFriendsterFindActivity.this, list,flag,tuid);
        lv.setAdapter(adapter);
        lv.setXListViewListener(this);
        mHandler = new Handler();
    }
		
    public static void ShowData(){
        list.clear();
        allist.clear();
        list = DbHelper.QueryTData("select * from TwitterSelf order by twrid desc", TwitterSelf.class);
        allist = DbHelper.QueryTData("select * from CommentsSelf order by cmtid asc", CommentsSelf.class);
        Log.i("userid", AppServer.getInstance().getAccountInfo().getUid() + "");
        for (int i = 0; i < list.size(); i++) {
            zlist.clear();
            for (int j = 0; j < allist.size(); j++) {
                if (list.get(i).getTwrid() == allist.get(j).getTwrid()) {
                    zlist.add(allist.get(j));
                    CommentsSelf[] comments = (CommentsSelf[])zlist.toArray(new CommentsSelf[zlist.size()]);
                    list.get(i).setComment(comments);
                }
            }
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPosterid()!=tuid) {
                    list.remove(i);
                }
            }
            adapter.setList(list);
            lv.setVisibility(View.VISIBLE);
        } else {
            lv.setVisibility(View.GONE);
        }

    }
		
    @OnClick(R.id.left_btn)
    public void onclick(View v){
        switch (v.getId()) {
        case R.id.left_btn:
            Intent i = new Intent(ServiceFriendsterFindActivity.this, ServiceFriendsterActivity.class);
            startActivity(i);
            break;
        default:
            break;
        }
    }

    private void loadNewData() {
        ShowData();
        AppServer.getInstance().getTwittersByUid(AppServer.getInstance().getAccountInfo().getUid(), tuid, 20, -1, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, int nextid) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    TwitterSelf[] tw = (TwitterSelf[]) obj;
                    if (tw.length > 0) {
                        cleanDbhelper();
                        for (int i = 0; i < tw.length; i++) {
                            List<TwitterSelf> list = DbHelper.QueryTData("select * from TwitterSelf where twrid='" + tw[i].getTwrid() + "'", TwitterSelf.class);
                            if (list.size() < 1) {
                                try {
                                    DbHelper.getDB(ServiceFriendsterFindActivity.this).save(tw[i]);
                                } catch (DbException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            CommentsSelf[] comments = tw[i].getComment();
                            for (int j = 0; j < comments.length; j++) {
                                List<CommentsSelf> alist = DbHelper.QueryTData("select * from CommentsSelf where cmtid='" + comments[j].getCmtid() + "'", CommentsSelf.class);
                                if (alist.size() < 1) {
                                    try {
                                        DbHelper.getDB(ServiceFriendsterFindActivity.this).save(comments[j]);
                                    } catch (DbException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    ShowData();
                }

            }
        });
    }

    private void loadMoreDate() {
        AppServer.getInstance().getTwittersByUid(AppServer.getInstance().getAccountInfo().getUid(), tuid, 20, NextId, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, int nextid) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    NextId = nextid;
//					tv.clear();
                    TwitterSelf[] tw = (TwitterSelf[]) obj;
                    for (int i = 0; i < tw.length; i++) {
                        List<Twitter> list = DbHelper.QueryTData("select * from TwitterSelf where twrid='" + tw[i].getTwrid() + "'", TwitterSelf.class);
                        if (list.size() < 1) {
                            try {
                                DbHelper.getDB(ServiceFriendsterFindActivity.this).save(tw[i]);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                        CommentsSelf[] comments = tw[i].getComment();
                        for (int j = 0; j < comments.length; j++) {
                            List<CommentsSelf> alist = DbHelper.QueryTData("select * from CommentsSelf where twrid='" + comments[j].getTwrid() + "'", CommentsSelf.class);
                            if (alist.size() < 1) {
                                try {
                                    DbHelper.getDB(ServiceFriendsterFindActivity.this).save(comments[j]);
                                } catch (DbException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    ShowData();
                }
                if (nextid == 0) {
                    lv.setPullLoadEnable(false);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ServiceFriendsterFindActivity.this, "数据全部加载完成，没有更多数据！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNewData();
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreDate();
                onLoad();
            }
        }, 2000);
    }

    private void onLoad() {
        lv.stopRefresh();
        lv.stopLoadMore();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
