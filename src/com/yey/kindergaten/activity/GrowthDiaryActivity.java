package com.yey.kindergaten.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.DiaryHomeInfo;
import com.yey.kindergaten.bean.News;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.OnAppRequestListenerFriend;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.EmoticonsEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GrowthDiaryActivity extends BaseActivity  {

    @ViewInject(R.id.layout_head_growthdairy)FrameLayout header_layout;
    @ViewInject(R.id.right_btn)ImageView iv_right;
    @ViewInject(R.id.left_btn)ImageView iv_left;
    @ViewInject(R.id.header_title)TextView tv_title;
    @ViewInject(R.id.iv_growthdiary_head)CircleImageView iv_head;
    @ViewInject(R.id.tv_growthdiary_head)TextView tv_head;
    @ViewInject(R.id.ll_activity_service_friendster_item_input)LinearLayout ll_input;
    @ViewInject(R.id.input_activity_service_friendster_item)EmoticonsEditText et;
    @ViewInject(R.id.tv_null)TextView  tv_null;
    @ViewInject(R.id.network_listener_ll)RelativeLayout netCheckRL;
    @ViewInject(R.id.network_listener_tv)TextView netCheckTv;

    private ListView sListView;
    private List<News> sNewsList;
    private Handler sHandler;
    private NewsAdapter sNewsAdapter;
    private String name;
    private String  jsonimg;
    private List<DiaryHomeInfo> list = new ArrayList<DiaryHomeInfo>();
    private String img,recording,content;
    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/uploadimg/";
    private String imgurl = "";
    private String recordurl = "";
    private StringBuffer urlstring = new StringBuffer();
    private static List<DiaryHomeInfo> showlist = new ArrayList<DiaryHomeInfo>();
    private String mDate;
    private Boolean flag = true;
    private int mStatusHeight;
    private int cKeyBoardHeight;
    private int page = 1;
    private ImageView mHeaderPicture;
    private View mPlaceHolderView;
    private View mHeader,moreView;
    private AccelerateDecelerateInterpolator mSmoothInterpolator;
    private int mActionBarTitleColor;
    private int mActionBarHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private TypedValue mTypedValue = new TypedValue();
    private ImageView mHeaderLogo;
    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();
    private int lastItem;
    private int NextId = -1;
    private int curplayposition = -1;
    private AnimationDrawable animationDrawable;
    private Boolean isremove = true;
    NetWorkStateReceive mReceiver;
    private final static String TAG = "GrowthDiaryActivity";
//    private TextView ll_progress;
//    private ProgressBar pb_dialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mHeaderHeight + getResources().getDimensionPixelSize(R.dimen.headlayout_height);
        setContentView(R.layout.growthdiary_main);
        ViewUtils.inject(this);

        mReceiver = new NetWorkStateReceive();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);

        initView();
        sNewsList = new ArrayList<News>();
        LoadData();
    }

    public class NetWorkStateReceive extends BroadcastReceiver {
        private ConnectivityManager connectivityManager;
        private NetworkInfo info;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    netCheckRL.setVisibility(View.GONE);
                } else {
                    netCheckRL.setVisibility(View.VISIBLE);
                    netCheckTv.setText("网络不可用，请检查您的网络设置。");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        sNewsAdapter.stopPlayRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sNewsAdapter.stopPlayRecord();
    }

    private void cleanDbhelper() {
        try {
            DbHelper.getDB(GrowthDiaryActivity.this).delete(DiaryHomeInfo.class, WhereBuilder.b("status", "=", "0"));
//			DbHelper.getDB(GrowthDiaryActivity.this).delete(DiaryHomeInfo.class);
//			DbHelper.getDB(GrowthDiaryActivity.this).deleteAll(comments.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void LoadData() {
//		showDate();
        showLoadingDialog("正在加载...");
        AppServer.getInstance().getDiaryHome(AppServer.getInstance().getAccountInfo().getUid(), 10, NextId, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, int nextid) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    cancelLoadingDialog();
                    DiaryHomeInfo[] dairy = (DiaryHomeInfo[]) obj;
                    NextId = nextid;
                    if (dairy.length > 0) {
                        cleanDbhelper();
                        for (int i = 0; i < dairy.length; i++) {
                            List<DiaryHomeInfo> xlist = DbHelper.QueryTData("select * from DiaryHomeInfo where diaryid='" + dairy[i].getDiaryid() + "'", DiaryHomeInfo.class);
                            if (xlist.size() < 1) {
                                try {
                                    DbHelper.getDB(GrowthDiaryActivity.this).save(dairy[i]);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    showDate();
                }

            }
        });
    }

    private void showDate() {
        showlist.clear();
        showlist = DbHelper.QueryTData("select * from DiaryHomeInfo order by date desc", DiaryHomeInfo.class);
        if (showlist.size() == 0) {
//				tv_null.setVisibility(View.VISIBLE);
            DiaryHomeInfo newdiaryhomeinfo = new DiaryHomeInfo();
            newdiaryhomeinfo.setCon("当我们和孩子在一起时,请珍惜孩子的每一分、每一秒.记录孩子的成长点滴,珍藏一份与孩子美好的回忆");
            newdiaryhomeinfo.setStatus(0);
            newdiaryhomeinfo.setDiaryid("");
            newdiaryhomeinfo.setImg("");
            newdiaryhomeinfo.setSnd("");
            newdiaryhomeinfo.setZancnt(0);
            newdiaryhomeinfo.setBg(0);
            newdiaryhomeinfo.setId(0);
            newdiaryhomeinfo.setDate("");
            showlist.add(newdiaryhomeinfo);
            sNewsAdapter.setList(showlist);
            sListView.removeFooterView(moreView);
        } else {
            sNewsAdapter.setList(showlist);
            if (showlist.size() < 10) {
                moreView.setVisibility(View.GONE);
            } else {
                tv_null.setVisibility(View.GONE);
                moreView.setVisibility(View.VISIBLE);
            }
            tv_null.setVisibility(View.GONE);
        }
    }

    private void initView() {
        iv_left.setVisibility(View.VISIBLE);
        iv_right.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("成长日记");
        mHeader = findViewById(R.id.ll_headview);
        ImageLoader.getInstance().displayImage(AppServer.getInstance().getAccountInfo().getAvatar(), iv_head, ImageLoadOptions.getFriendOptions());
        tv_head.setText(AppServer.getInstance().getAccountInfo().getNickname());
        sListView = (ListView) findViewById(R.id.xListView);
        mPlaceHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, sListView, false);
        moreView = getLayoutInflater().inflate(R.layout.homecontact_qimo_moban_bottom, null,false);
//      ll_progress = (TextView) moreView.findViewById(R.id.tv_addmore);
//      pb_dialog = (ProgressBar)moreView.findViewById(R.id.pg);
        sListView.addHeaderView(mPlaceHolderView);
        sListView.addFooterView(moreView);
        sNewsAdapter = new NewsAdapter(GrowthDiaryActivity.this,list);
        sListView.setAdapter(sNewsAdapter);
        sHandler = new Handler();
        mHeaderPicture = (ImageView) findViewById(R.id.header_picture);
        header_layout.setBackgroundColor(getApplication().getResources().getColor(R.color.purple));
        netCheckRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                UtilsLog.i(TAG, "wifiSettingIntent to settings.WIFI_SETTINGS");
            }
        });
        sListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem == sNewsAdapter.getCount()) {
                    LoadMoreData();
                }
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断界面上显示的最后一项item的position，是否等于item的总个数减1（item的position从0开始），如果是，说明滑动到了底部。
                    if (view.getLastVisiblePosition() == (view.getCount() - 1) && NextId == 0) {
                        if (isremove) {
                            sListView.removeFooterView(moreView);
                            showDate();
                            isremove = false;
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 2;
                int scrollY = getScrollY();
                ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
                float ratio = clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
//                interpolate(iv_head, getActionBarIconView(), mSmoothInterpolator.getInterpolation(ratio));              
//                interpolate(tv_head, getActionBarIconView(), mSmoothInterpolator.getInterpolation(ratio));
                if (scrollY <= mHeaderHeight - 80) {
                    iv_head.setVisibility(View.VISIBLE);
                    tv_head.setVisibility(View.VISIBLE);
                } else {
                    iv_head.setVisibility(View.GONE);
                    tv_head.setVisibility(View.GONE);
                }
                System.out.print("最后一次的值" + mHeaderHeight);
                System.out.print("最后一次滑动的值" + scrollY);
            }
        });
        iv_head.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(GrowthDiaryActivity.this, MeInfoActivity.class);
                startActivity(i);
            }
        });
    }

    @OnClick({(R.id.right_btn),(R.id.left_btn)})
    public  void  onclick(View v){
        switch (v.getId()) {
            case R.id.right_btn:
                Intent i = new Intent(this, KeepDiaryActivity.class);
                startActivity(i);
                break;
            case R.id.left_btn:
                sNewsAdapter.stopPlayRecord();
                finish();
                break;
            default:
                break;
        }
    }

    public int getScrollY() {
        View c = sListView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = sListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    @Override
    protected void onNewIntent(Intent intent){
        showDate();
        if (intent.getExtras()!=null && intent!=null) {
//		ShowData();
            sListView.setSelectionAfterHeaderView();
            AppContext.selectphoto.clear();
            img = intent.getExtras().getString("img");
            recording = intent.getExtras().getString("recording");
            content = intent.getExtras().getString("content");
            mDate = intent.getExtras().getString("date");
//		    gnum = intent.getExtras().getInt(AppConstants.GNUM);
            if (img.equals("local")) {
                uploadRecord(recording);
            } else {
                uploadImg(img);
            }
        }
    }

    private void uploadRecord(String recording) {
        File file = new File(recording);
        if (recording.equals("")) {
            saveDiary();
        } else {
            AppServer.getInstance().uploadimage(AppServer.getInstance().getAccountInfo().getUid(), "3", file, new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (code == AppServer.REQUEST_SUCCESS) {
                        recordurl = (String) obj;
                        saveDiary();
                    }
                }
            });
        }
    }

    private void saveDiary() {
        AppServer.getInstance().saveDiary(content, imgurl, recordurl, AppServer.getInstance().getAccountInfo().getUid(), 0, new OnAppRequestListener(){
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    DiaryHomeInfo info = (DiaryHomeInfo) obj;
                    showlist.get(0).setDiaryid(info.getDiaryid());
                    try {
                        DbHelper.getDB(GrowthDiaryActivity.this).update( showlist.get(0), WhereBuilder.b("diaryid", "=", -1), "diaryid");
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void uploadImg(String img) {
        BitmapUtil.createSDCardDir();
//      BitmapUtil.savePhotoToSDCard(BitmapUtil.getImageByPath(img,false),PATH,name);
        File f = new File(img);
        name = f.getName();
        BitmapUtil.save(img, name, PATH);
        File file = new File(PATH + name);
        AppServer.getInstance().uploadimage(AppServer.getInstance().getAccountInfo().getUid(), "4", file, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    imgurl = (String) obj;
                    uploadRecord(recording);
                }
            }
        });
    }

    private void LoadMoreData() {
        showDate();
        AppServer.getInstance().getDiaryHome(AppServer.getInstance().getAccountInfo().getUid(), 10, NextId, new OnAppRequestListenerFriend() {
            @Override
            public void onAppRequestFriend(int code, String message, Object obj, int nextid) {
                // TODO Auto-generated method stub
                if (code == AppServer.REQUEST_SUCCESS) {
                    DiaryHomeInfo[] dairyinfo = (DiaryHomeInfo[]) obj;
                    NextId = nextid;
                    for (int i = 0; i < dairyinfo.length; i++) {
                        List<DiaryHomeInfo> xlist = DbHelper.QueryTData("select * from DiaryHomeInfo where diaryid='" + dairyinfo[i].getDiaryid() + "'", DiaryHomeInfo.class);
                        if (xlist.size() < 1) {
                            try {
                                DbHelper.getDB(GrowthDiaryActivity.this).save(dairyinfo[i]);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                showDate();
            }
        });
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    private ImageView getActionBarIconView() {
        return (ImageView) findViewById(R.id.left_btn);
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

//      view1.setTranslationX(translationX);
        ViewHelper.setTranslationX(view1, translationX);
//      view1.setTranslationY(translationY - mHeader.getTranslationY());
        ViewHelper.setTranslationY(view1, translationY - ViewHelper.getTranslationY(mHeader));
//      view1.setScaleX(scaleX);
        ViewHelper.setScaleX(view1, scaleX);
//      view1.setScaleY(scaleY);
        ViewHelper.setScaleY(view1, scaleY);
    }
    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    private class NewsAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private MediaPlayer mediaPlayer = null;
        public  boolean isPlaying = false;
        private List<DiaryHomeInfo> list = new ArrayList<DiaryHomeInfo>();
        private Context context;
        private int curplayposition = -1;

        public NewsAdapter(Context context, List<DiaryHomeInfo> list) {
            this.context = context;
            this.list = list;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder h;
            if (convertView == null) {
                h = new Holder();
                convertView = mInflater.inflate(R.layout.growthdiary_item,null);
                h.tv = (TextView) convertView.findViewById(R.id.tv_day);
                h.tv_content = (TextView) convertView.findViewById(R.id.tv_growthdiary_item);
                h.content = (ImageView) convertView.findViewById(R.id.growthdiary_item_iv);
                h.iv_record = (LinearLayout) convertView.findViewById(R.id.layout_voice);
                h.iv_delete = (ImageView) convertView.findViewById(R.id.growthdiary_delete_iv);
                h.iv_voice = (ImageView) convertView.findViewById(R.id.iv_voice_grow);
                h.ll_today = (LinearLayout)convertView.findViewById(R.id.ll_today);
                h.tv_time_grow = (TextView) convertView.findViewById(R.id.tv_time_grow);
                h.tv_time_voice_grow = (TextView) convertView.findViewById(R.id.tv_time_voice_grow);
                convertView.setTag(h);
            } else {
                h = (Holder) convertView.getTag();
            }
            if (list.get(position).getImg()!=null && !list.get(position).getImg().equals("") && !list.get(position).getImg().equals("local")) {
                h.content.setVisibility(View.VISIBLE);
                if (list.get(position).getImg().substring(0, 4).equals("http")) {
                    if (list.get(position).getImg().contains(",")) {
                        String[] img = list.get(position).getImg().split(",");
//						BitmapCache.getInstance().displayNetBmp(h.content, img[0], null, ImageLoadOptions.getMessagePublicOptions_view());
                        ImageLoader.getInstance().displayImage(img[0], h.content,ImageLoadOptions.getMessagePublicOptions_view());
                    } else {
                        ImageLoader.getInstance().displayImage(list.get(position).getImg(), h.content, ImageLoadOptions.getMessagePublicOptions_view());
                    }
                } else {
                    ImageLoader.getInstance().displayImage("file://" + list.get(position).getImg(), h.content, ImageLoadOptions.getMessagePublicOptions_view());
                }
            } else {
                h.content.setVisibility(View.GONE);
            }
            System.out.println("position22222222---->" + position);
            h.tv_time_voice_grow.setVisibility(View.GONE);
            h.iv_delete.setVisibility(View.VISIBLE);
            h.tv_content.setText(list.get(position).getCon().replace("\r", "\n"));
            h.tv_time_grow.setText(TimeUtil.getGrowDayYMDTime(list.get(position).getDate()));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            if (!list.get(position).getDate().equals("")) {
                if (df.format(new Date()).equals(list.get(position).getDate().substring(0, 10))) {
                    h.tv.setText("今天");
                    h.ll_today.setBackgroundResource(R.drawable.grow_today);
                    h.iv_voice.setBackgroundResource(R.drawable.voice_three);
                    h.tv_time_voice_grow.setTextColor(getResources().getColor(R.color.blue_time));
                } else {
                    try {
                        Date now = null;
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        now = f.parse(list.get(position).getDate());
                        SimpleDateFormat f2 = new SimpleDateFormat("MM-dd");
                        h.tv.setText(f2.format(now).replace("-", "月") + "日");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    h.ll_today.setBackgroundResource(R.drawable.annotherday_bg);
                    h.iv_voice.setBackgroundResource(R.drawable.voice_another_three);
                    h.tv_time_voice_grow.setTextColor(getResources().getColor(R.color.pink_time));
                }
            } else {
                h.tv.setText("今天");
                h.iv_delete.setVisibility(View.GONE);
            }
            if (list.get(position).getSnd()!=null && !list.get(position).getSnd().equals("")) {
                getDownLoadFilePath(list.get(position).getSnd());
            }
            if (list.get(position).getSnd()==null || list.get(position).getSnd().equals("")) {
                h.iv_voice.setVisibility(View.GONE);
            } else {
                h.iv_voice.setVisibility(View.VISIBLE);
            }

            if (curplayposition == position) {
                if (h.tv.getText().toString().equals("今天")){
                    h.iv_voice.setBackgroundResource(R.anim.animotion_voice_play);
                } else {
                    h.iv_voice.setBackgroundResource(R.anim.animotion_voice_play_another);
                }
            } else {
                if (h.tv.getText().toString().equals("今天")) {
                    h.iv_voice.setBackgroundResource(R.drawable.voice_three);
                } else {
                    h.iv_voice.setBackgroundResource(R.drawable.voice_another_three);
                }
            }

            h.iv_record.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    curplayposition = position;
                    if (isPlaying) {
                        stopPlayRecord();
                        if (h.tv.getText().toString().equals("今天")) {
                            h.iv_voice.setBackgroundResource(R.drawable.voice_three);
                        } else {
                            h.iv_voice.setBackgroundResource(R.drawable.voice_another_three);
                        }
                    }
                    if (flag) {
                        if (h.tv.getText().toString().equals("今天")) {
                            h.iv_voice.setBackgroundResource(R.anim.animotion_voice_play);
                        } else {
                            h.iv_voice.setBackgroundResource(R.anim.animotion_voice_play_another);
                        }
                        mediaPlayer = new MediaPlayer();
                        animationDrawable = (AnimationDrawable) h.iv_voice.getBackground();
                    } else {
                        animationDrawable.stop();
                        animationDrawable.selectDrawable(0);
                        animationDrawable.clearColorFilter();
                        if (h.tv.getText().toString().equals("今天")){
                            h.iv_voice.setBackgroundResource(R.anim.animotion_voice_play);
                        } else {
                            h.iv_voice.setBackgroundResource(R.anim.animotion_voice_play_another);
                        }
                        animationDrawable = (AnimationDrawable) h.iv_voice.getBackground();
                        stopPlayRecord();
                        mediaPlayer = new MediaPlayer();
                    }
                    if (list.get(position).getSnd()!=null && !list.get(position).getSnd().equals("")) {
                        if (list.get(position).getSnd().contains("http")) {
                            if (fileIsExists(list.get(position).getSnd())) {
                                flag = false;
                                animationDrawable.start();
                                startPlayRecord(getSndfile(list.get(position).getSnd()), true);
                            }
                        } else {
                            flag = false;
                            animationDrawable.start();
                            startPlayRecord(list.get(position).getSnd(), true);
                        }
                    }
                }
            });

            h.iv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog("友情提示：", "你确定要删除日记吗？", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            DbHelper.deletegrowthdiary(Integer.valueOf(list.get(position).getDiaryid()));
                            AppServer.getInstance().deleteDiary(Integer.valueOf(list.get(position).getDiaryid()),new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {
                                    if (code == AppServer.REQUEST_SUCCESS) {

                                    }
                                }
                            });
                            showDate();
                        }
                    });

                }
            });

            h.content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getImg().contains(",")) {
                        String[] imglist = list.get(position).getImg().split(",");
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < imglist.length; i++) {
                            list.add(imglist[i]);
                        }
                        Intent i = new Intent(GrowthDiaryActivity.this, PhotoManager_ViewPager.class);
//						i.putStringArrayListExtra("imglist", list);
                        Bundle bundler = new Bundle();
                        bundler.putStringArrayList("imglist", list);
                        bundler.putString("type", AppConstants.PARAM_UPLOAD_GROW);
                        i.putExtras(bundler);
                        GrowthDiaryActivity.this.startActivity(i);
                    } else {
                        String imglist = list.get(position).getImg();
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(imglist);
                        Intent i = new Intent(GrowthDiaryActivity.this, PhotoManager_ViewPager.class);
//						i.putStringArrayListExtra("imglist", list);
                        Bundle bundler = new Bundle();
                        bundler.putStringArrayList("imglist", list);
                        bundler.putString("type", AppConstants.PARAM_UPLOAD_GROW);
                        i.putExtras(bundler);
                        GrowthDiaryActivity.this.startActivity(i);
                    }
                }
            });
            return convertView;
        }

        public boolean fileIsExists(String name){
            try {
                File f = new File(AppConstants.VOICE_DIR + File.separator
                        + AppServer.getInstance().getAccountInfo().getUid() + File.separator + name.substring(name.lastIndexOf("/") + 1));
                if (!f.exists()) {
                    return false;
                }

            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
            return true;
        }

        public String getSndfile(String name){
            File f = new File(AppConstants.VOICE_DIR + File.separator
                    + AppServer.getInstance().getAccountInfo().getUid() + File.separator + name.substring(name.lastIndexOf("/") + 1));
            return f.getPath();
        }

        private class Holder {
            public TextView tv;
            public ImageView iv;
            public ImageView content;
            public LinearLayout iv_record;
            public TextView  tv_content;
            public ImageView iv_delete;
            public ImageView iv_voice;
            public LinearLayout ll_today;
            public TextView tv_time_grow;
            public TextView tv_time_voice_grow;
        }

        private void setList(List<DiaryHomeInfo> diarylist){
            this.list = diarylist;
            sNewsAdapter.notifyDataSetChanged();
        }
        @SuppressWarnings("resource")
        public void startPlayRecord(String filePath, boolean isUseSpeaker) {
            if (!(new File(filePath).exists())) {
                flag = true;
                return;
            }
            AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (isUseSpeaker) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } else {
                audioManager.setSpeakerphoneOn(false); // 关闭扬声器
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
            try {
                mediaPlayer.reset();
                // 单独使用此方法会报错播放错误:setDataSourceFD failed.: status=0x80000000
//				mediaPlayer.setDataSource(filePath);
                // 因此采用此方式会避免这种错误
                FileInputStream fis = new FileInputStream(new File(filePath));
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer arg0) {
                        // TODO Auto-generated method stub
                        isPlaying = true;
//						currentMsg = message;
                        arg0.start();
//						startRecordAnimation();
                    }
                });
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        stopPlayRecord();
                        animationDrawable.stop();
                        animationDrawable.selectDrawable(0);
                        flag = true;
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "该音频无法播放", Toast.LENGTH_SHORT).show();
                System.out.println(e.getMessage());
                animationDrawable.stop();
                animationDrawable.selectDrawable(0);
            };
        }

        public void getDownLoadFilePath(String name) {
            File dir = new File(AppConstants.VOICE_DIR + File.separator
                    + AppServer.getInstance().getAccountInfo().getUid());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 在当前用户的目录下面存放录音文件
            File audioFile = new File(dir.getAbsolutePath() + File.separator
                    + name.substring(name.lastIndexOf("/") + 1));
            try {
                if (!audioFile.exists()) {
                    audioFile.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            HttpUtils http = new HttpUtils();
            http.download(name,audioFile.getAbsolutePath(),
                new RequestCallBack<File>() {
                    @Override
                    public void onStart() {
                        System.out.println("开始下载");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        System.out.println("正在下载");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        System.out.println(responseInfo.result.getPath());
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        System.out.println();
                    }
                });
        }

        public void stopPlayRecord() {
//			stopRecordAnimation();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
            }
            isPlaying = false;
        }
    }


}