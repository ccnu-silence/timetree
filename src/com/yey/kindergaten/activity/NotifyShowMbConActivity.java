package com.yey.kindergaten.activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.MobanContentInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class NotifyShowMbConActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnScrollListener{

    @ViewInject(R.id.header_title)TextView tv_headerTitle ;
    @ViewInject(R.id.right_btn)ImageView iv_right;
    @ViewInject(R.id.left_btn)ImageView iv_left;

    private int height;
    private int fetchnum = 8; // 分页数量
    private ListView listview;
    private AccountInfo accountInfo;
    private MobanAdapter adapter;
    private View bottomView;
    private MobanContentInfo bean;

    private String content; // 发送界面输入框中的文字内容
    private ArrayList<String>parentlist;
    private ArrayList<String>teacherlist;
    private String showtime;

    private int NextId;
    private int type; // 模板分类
    private String name;
    private int lastitem;
    private int count; // 数据总数量
    private List<MobanContentInfo>showlist = new ArrayList<MobanContentInfo>();
    private List<MobanContentInfo>listbean = new ArrayList<MobanContentInfo>();
    private List<MobanContentInfo>listinfo = new ArrayList<MobanContentInfo>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendnotify_selectmoban);
        ViewUtils.inject(this);

        type = this.getIntent().getIntExtra("type", 0);
        name = this.getIntent().getStringExtra("name");
        content = this.getIntent().getStringExtra("content");
        teacherlist = this.getIntent().getStringArrayListExtra("teacherlist");
        parentlist = this.getIntent().getStringArrayListExtra("parentlist");
        showtime = this.getIntent().getStringExtra("time");

        prepareView();
        DisplayMetrics dm = new DisplayMetrics();getWindowManager().getDefaultDisplay().getMetrics(dm);
        height = dm.heightPixels; // 高度
        accountInfo = AppServer.getInstance().getAccountInfo();
        AppServer.getInstance().getTemplatesByType(accountInfo.getUid(), type, fetchnum, -1, new OnAppRequestListenerFriend() {
            public void onAppRequestFriend(int code, String message, Object obj, int nextid) {
                MobanContentInfo[] info = (MobanContentInfo[]) obj;
                NextId = nextid;
                listinfo = Arrays.asList(info);
                for (int i = 0; i < listinfo.size(); i++) {
                    listbean.add(listinfo.get(i));
                }
                showlist.addAll(listbean);
                adapter = new MobanAdapter(listbean);
                count = listinfo.size();

                listview.addFooterView(bottomView);
                if (count < fetchnum) {
                    listview.removeFooterView(bottomView);
                }
                listview.setAdapter(adapter);
                listview.setOnScrollListener(NotifyShowMbConActivity.this);
            }
        });
    }

    private void prepareView() {
        tv_headerTitle.setText(name);
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.id_sendnotify_fenleicontent_lv);
        listview.setOnItemClickListener(this);
        listview.setVerticalScrollBarEnabled(true);
        bottomView = getLayoutInflater().inflate(R.layout.inflater_show_bottom_moban_view, null);
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.left_btn) {
            Intent intent = new Intent(NotifyShowMbConActivity.this, SendNotificationActivity.class);
            if (content!=null) {
                intent.putStringArrayListExtra("teacherlist", teacherlist);
                intent.putStringArrayListExtra("parentlist", parentlist);
                intent.putExtra("time", showtime);
            } else {
                intent.putStringArrayListExtra("teacherlist", teacherlist);
                intent.putStringArrayListExtra("parentlist", parentlist);
                intent.putExtra("time", showtime);
            }
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(NotifyShowMbConActivity.this,SendNotificationActivity.class);
        if (content!=null) {
            intent.putExtra("content", content + listbean.get(position).getContent());
            intent.putStringArrayListExtra("teacherlist", teacherlist);
            intent.putStringArrayListExtra("parentlist", parentlist);
            intent.putExtra("time", showtime);
        } else {
            intent.putExtra("content", listbean.get(position).getContent());
            intent.putStringArrayListExtra("teacherlist", teacherlist);
            intent.putStringArrayListExtra("parentlist", parentlist);
            intent.putExtra("time", showtime);
        }
        startActivity(intent);
        this.finish();
    }

    class  MobanAdapter extends BaseAdapter {

        private List<MobanContentInfo>list;

        public MobanAdapter(List<MobanContentInfo>list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        private List<MobanContentInfo> getData(){
            return list;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addData(List<MobanContentInfo>lists) {
           if (lists!=null && list!=null) {
                list.addAll(lists);
           }
           this.notifyDataSetChanged();
        }
       
        public void setData(List<MobanContentInfo>lists) {
            if (lists!=null && list!=null) {
                list = lists;
            }
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            convertView = LayoutInflater.from(NotifyShowMbConActivity.this).inflate(R.layout.inflater_show_moban_content, null);
            TextView tc = (TextView) convertView.findViewById(R.id.id_inflater_show_moban_content_tv);
            tc.setText(list.get(position).getContent());
            return convertView;
        }

    }

    @Override
    public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastitem = firstVisibleItem + visibleItemCount - 1;

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int scrollState) {
        if (lastitem == count && scrollState == this.SCROLL_STATE_IDLE) {
            bottomView.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(0);
        }
    }

    private void loadMoreData(){
        count = adapter.getCount();
        AppServer.getInstance().getTemplatesByType(accountInfo.getUid(), type, fetchnum, NextId, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, int nextid) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    MobanContentInfo[]info=(MobanContentInfo[]) obj;
                    NextId = nextid;
                    listinfo = Arrays.asList(info);
                    listbean.clear();
                    for (int i = 0; i < listinfo.size(); i++) {
                        listbean.add(listinfo.get(i));
                    }
                    showlist.addAll(listbean);
                    adapter.setData(showlist);
                    if (NextId!=0) {
                        listview.setSelection(nextid - 4);
                    } else {
                        listview.setSelection(adapter.getCount() - 4);
                    }
                } else {
                    listinfo = new ArrayList<MobanContentInfo>();
                    count = listinfo.size();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    loadMoreData();
                    bottomView.setVisibility(View.GONE);
                    if (NextId == 0) {
                        Toast.makeText(NotifyShowMbConActivity.this, "木有更多数据！", Toast.LENGTH_LONG).show();
                        listview.removeFooterView(bottomView);
                    }
                default:
                    break;
            }
         };
    };
     
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(NotifyShowMbConActivity.this, SendNotificationActivity.class);
            if (content!=null) {
                intent.putStringArrayListExtra("teacherlist", teacherlist);
                intent.putStringArrayListExtra("parentlist", parentlist);
                intent.putExtra("time", showtime);
            } else {
                intent.putStringArrayListExtra("teacherlist", teacherlist);
                intent.putStringArrayListExtra("parentlist", parentlist);
                intent.putExtra("time", showtime);
            }
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
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
