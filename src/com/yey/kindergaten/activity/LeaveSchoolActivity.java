package com.yey.kindergaten.activity;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.leaveschool.LeaveSchoolClassAdapter;
import com.yey.kindergaten.adapter.leaveschool.LeaveSchoolClassBean;
import com.yey.kindergaten.adapter.leaveschool.LeaveSchoolDetailAdapter;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.LeaveSchoolBean;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.receive.HxinChatMessageReceiver;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.MyListViewWithScrollView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zy on 2015/7/20.
 * 离园播报主页
 */
public class LeaveSchoolActivity extends BaseActivity{

    // 标题名称
    private TextView header_tv;
    private ImageView left_iv;
    // 显示班级列表的Listview
    private MyListViewWithScrollView mClassListView; // (横向listView)
    // 显示小朋友列表的listview
    private ListView mChildListListView;
    // 暂无小朋友离园时显示界面
    private LinearLayout nochild_ll;
    // 小朋友离园明细数据
    private List<LeaveSchoolBean> mList;
    // 班级离园信息统计
    private List<LeaveSchoolClassBean> mClassBeanList = new ArrayList<LeaveSchoolClassBean>();
    private LeaveSchoolClassBean mClassBean;

    private LeaveSchoolClassAdapter mClassAdapter; // 班级
    private LeaveSchoolDetailAdapter mDetailAdapter; // 小朋友明细
    private Timer timer;
    private long preFreashTime;
    private long reFreashingTime;
    private HxinChatMessageReceiver msgReceiver;
    private AudioManager audioManager = null; // 媒体音频
    public static final String TAG = "LeaveSchoolActivity";

    // 主线程接收消息，刷新界面
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                refreash();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaveschool_activity);
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        initData();
        // 初始化信息显示栏，内容布局
        initView();
        // 初始化班级离园概况
        initClassData();
        // 小朋友离园细节
        initDetailData();
        // 任务调度
        initTimerTask();
        // 初始化广播
        initReceiver();
    }

    private void initData() {
        try {
            List<LeaveSchoolBean> leaveSchoolBeans = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(LeaveSchoolBean.class).orderBy("date", true));

            if (leaveSchoolBeans!=null && leaveSchoolBeans.size()!=0) {
                for (int i = 0; i < leaveSchoolBeans.size(); i++) {
                    if (leaveSchoolBeans.get(i)!=null) {

                        String historyYmdTime = leaveSchoolBeans.get(i).getDate();
                        long historyTime = TimeUtil.StringToDate(historyYmdTime);

                        if (historyTime < TimeUtil.getYesterdayMaxTimeMillis()
                                && DbHelper.getDB(AppContext.getInstance()).tableIsExist(Parent.class)) { // 表示是今天凌晨以前
                            List<Parent> parents = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
                            if (parents != null && parents.size() != 0) {
                                LeaveSchoolBean bean = leaveSchoolBeans.get(i);
                                bean.setIsLeave(0); // 0表示未离园
                                DbHelper.getDB(AppContext.getInstance()).update(bean, WhereBuilder.b("uid", "=", bean.getUid()));
                            }
                        }
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initReceiver() {
        // 注册一个接收消息的BroadcastReceiver
        msgReceiver = new HxinChatMessageReceiver(this, AppEvent.CHAT_SCHOOLACTIVITY_REFRESH);
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(5);
        registerReceiver(msgReceiver, intentFilter);
//      EventBus.getDefault().register(this);
    }

    private void initView() {
        // 初始化标题
        header_tv = (TextView)findViewById(R.id.header_title);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int Month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String currentYmdTime = Month + "月" + day + "日";
        header_tv.setText(currentYmdTime + "  离园播报  ");
        header_tv.setVisibility(View.VISIBLE);

        left_iv = (ImageView) findViewById(R.id.left_btn);
        left_iv.setVisibility(View.VISIBLE);
        left_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mClassListView = (MyListViewWithScrollView) findViewById(R.id.leave_school_class_detail_ml);
        mClassListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LeaveSchoolClassBean bean = (LeaveSchoolClassBean) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(LeaveSchoolActivity.this, LeftSchoolActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("class", bean);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mChildListListView = (ListView) findViewById(R.id.leave_school_child_detail_ll); // 离园详情
        nochild_ll = (LinearLayout) findViewById(R.id.show_no_child_leave_ll); // 暂无小朋友离园

        mClassAdapter = new LeaveSchoolClassAdapter(this);
        mClassListView.setAdapter(mClassAdapter);

        mDetailAdapter = new LeaveSchoolDetailAdapter(this);
        mDetailAdapter.setType("fromLeave");
        mChildListListView.setAdapter(mDetailAdapter);
    }

    // 初始化班级列表数据
    private void initClassData() {
        mClassBeanList.clear();
        preFreashTime = System.currentTimeMillis();
        try {
            List<Classe> mClassList = null;
            mClassList = DbHelper.getDB(AppContext.getInstance()).findAll(Classe.class);

            if (mClassList!=null) {
                for (int i = 0; i < mClassList.size(); i++){
                    mClassBean = new LeaveSchoolClassBean();
                    Classe classe = mClassList.get(i);

                    // 查询相应班级还未离园的小朋友名单
                    List<LeaveSchoolBean> list = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(LeaveSchoolBean.class).
                            where("isLeave", "=", 0).and("cid", "=", classe.getCid()));
                    if (list == null) return;
                    mClassBean.setCid(classe.getCid());
                    mClassBean.setCname(classe.getCname());
                    mClassBean.setNoLeavedCount(list.size());
                    mClassBean.setHasLeavedCount(classe.getChildrencount() - list.size());
                    mClassBeanList.add(mClassBean);
                }

                mClassAdapter.setmList(mClassBeanList);

            }
        } catch (DbException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "initClassData fail, because DbException");
        }
    }

    // 初始化离园小朋友名单
    private void initDetailData() {
        try {
            //查询已经离园小朋友列表
            mList =  DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(LeaveSchoolBean.class). where("isLeave", "=", 1).orderBy("date",true));
            if (mList!=null) {
                Collections.reverse(mList);
            }

            if (mList == null || mList.size() == 0) {
                nochild_ll.setVisibility(View.VISIBLE);
            } else {
                mDetailAdapter.setmList(mList);
                nochild_ll.setVisibility(View.GONE);
                mChildListListView.setSelection(mList.size() - 1);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        if (timer!=null) {
            timer.cancel();
        }

    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
             mHandler.sendEmptyMessage(0);
        }
    };
    private void initTimerTask() {
        timer = new Timer();
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.CHAT_SCHOOLACTIVITY_REFRESH) {
            refreash();
            UtilsLog.i(TAG,"----------来了吗---------");
//          showNotifyDialog();
//          timer.schedule(task, 0, 3000);
        }
    }

    private void refreash() {
        initClassData();
        initDetailData();
//      // 班级列表刷新
//      if (mClassBeanList!=null && mClassAdapter!=null) {
//          mClassAdapter.
//      } else {
//          initClassData();
//      }
//      // 小朋友离园明细刷新
//      if (mDetailAdapter!=null && mList!=null) {

//      } else{
//          initDetailData();
//      }
    }

    /** 音量键控制媒体音量 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND
                                | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND
                                | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }

}
