package com.yey.kindergaten.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.classvideo.ClassVideoActivity;
import com.yey.kindergaten.adapter.FriendsterActivityAdapter;
import com.yey.kindergaten.bean.GroupTwritte;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.LoadState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2015/7/3.
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener, View.OnClickListener {

    private FriendsterActivityAdapter adapter;
    private int headViewHeight;
    int currentScrollState;
    private float lastDownY;
    private int deltaCount;
    private ArrayList<String> data;
    private int currentState;
    private final int DECREASE_HEADVIEW_PADDING = 100;
    private final int LOAD_DATA = 101;
    private final int DISMISS_CIRCLE = 102;
    private ImageView circle;
    private Thread thread;
    private CircleImageView headimg;
    private Boolean isremove = true;
    private String jsonimg;
    private static View footView;
    private ImageView ivbtn_down;
    private View headView;
    private int CircleMarginTop;
    private LinearLayout ll_coupon,ll_class_announcement,ll_class_arrangement,ll_class_video;
    private int firstVisibleItem,lastItem;
    private TextView Username;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    List<GroupTwritte> list = new ArrayList<GroupTwritte>();

    private Context context;
    public TextView getUsername() {
        return Username;
    }

    public View getPullIv() {
        return ivbtn_down;
    }

    public  View getFootView() {
        return footView;
    }

    public onPullClickListener pullClickListener;

    public interface  onPullClickListener{
        public void pullClick();
    }

    public onBottomViewClickListener bottomClickListener;
    public interface onBottomViewClickListener{
        public void bottomViewClick();
    }

    public onScrollChanged scrollChangedListener;
    public interface onScrollChanged{
        public void scollStateChanged(int position,int state,AbsListView view);
    }

    @Override
    public FriendsterActivityAdapter getAdapter() {
        return adapter;
    }

    public void setScrollChangedListener(onScrollChanged scrollChangedListener) {
        this.scrollChangedListener = scrollChangedListener;
    }

    public void setBottomClickListener(onBottomViewClickListener bottomClickListener) {
        this.bottomClickListener = bottomClickListener;
    }

    public void setPullClickListener(onPullClickListener pullClickListener) {
        this.pullClickListener = pullClickListener;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DECREASE_HEADVIEW_PADDING:
                    setHeadViewPaddingTop(deltaCount);
                    setCircleMargin();
                    break;
                case LOAD_DATA:
                    // clearCircleViewMarginTop();
                    thread = new Thread(new DismissCircleThread());
                    thread.start();
                    currentState = LoadState.LOADSTATE_IDLE;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case DISMISS_CIRCLE:
                    int margin = msg.arg1;
                    setCircleMargin(margin);
                    if (margin == 0) {
                        if (circle.isShown()) {
                            CircleAnimation.stopRotateAnmiation(circle);
                        }
                    }
                    break;
            }
        }
    };

    protected void setCircleMargin(int margin) {
        MarginLayoutParams lp = (MarginLayoutParams) circle.getLayoutParams();
        lp.topMargin = margin;
        circle.setLayoutParams(lp);
    }

    protected void setCircleMargin() {
        MarginLayoutParams lp = (MarginLayoutParams) circle.getLayoutParams();
        lp.topMargin = CircleMarginTop - headView.getPaddingTop();
        circle.setLayoutParams(lp);
    }

    private class DecreaseThread implements Runnable {
        private final static int TIME = 25;
        private int decrease_length;

        public DecreaseThread(int count) {
            decrease_length = count / TIME;
        }

        @Override
        public void run() {
            for (int i = 0; i < TIME; i++) {
                if (i == 24) {
                    deltaCount = 0;
                } else {
                    deltaCount = deltaCount - decrease_length;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
                Message msg = Message.obtain();
                msg.what = DECREASE_HEADVIEW_PADDING;
                handler.sendMessage(msg);
            }
        }
    }

    public void setAdapter(Context context, List<GroupTwritte> grouptwrittelist, int gtype) {
        this.context = context;
        adapter = new FriendsterActivityAdapter(context, grouptwrittelist,gtype, ImageLoadOptions.getGalleryOptions());
//      initHeadView(context);
        setAdapter(adapter);
    }

    public void setList(List<GroupTwritte> groupTwrittes) {
        list = groupTwrittes;
        adapter.setList(list);
//      adapter = new FriendsterActivityAdapter(context, list,0, ImageLoadOptions.getClassPhotoOptions());
        setAdapter(adapter);
        setSelection(list.size() - 10);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeadView(context);
    }

    private void initHeadView(final Context context) {
        headView = LayoutInflater.from(context).inflate(R.layout.header, null);
        headView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        footView = LayoutInflater.from(context).inflate(R.layout.activity_service_listview_foot, null);
        if (!isremove) {
            addFooterView(footView);
            isremove = true;
        }
        addHeaderView(headView);
        circle = (ImageView) headView.findViewById(R.id.circleprogress);
        headimg = (CircleImageView) headView.findViewById(R.id.iv_activity_friendster_head);
        Username = (TextView) headView.findViewById(R.id.tv_friendster_user);
        ivbtn_down = (ImageView) headView.findViewById(R.id.iv_friendster_down);
        headimg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_group_class));
        headView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.title_banji));

        ll_coupon = (LinearLayout) headView.findViewById(R.id.layout_coupon_title);             // 班级相册
        ll_coupon.setOnClickListener(this);

        ll_class_announcement = (LinearLayout) headView.findViewById(R.id.layout_balance_title);// 班级公告
        ll_class_announcement.setOnClickListener(this);

        ll_class_arrangement = (LinearLayout) headView.findViewById(R.id.layout_contact_title); // 班级安排
        ll_class_arrangement.setOnClickListener(this);

        ll_class_video = (LinearLayout) headView.findViewById(R.id.layout_class_video_type);    // 班级视频
        ll_class_video.setOnClickListener(this);

        headView.getViewTreeObserver().addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (headView.getMeasuredHeight() > 0) {
                    headViewHeight = headView.getMeasuredHeight();
                    headView.getViewTreeObserver()
                            .removeOnPreDrawListener(this);
                }
                return true;
            }
        });
        Username.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pullClickListener!=null) {
                    pullClickListener.pullClick();
                }
            }
        });
        ivbtn_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pullClickListener!=null) {
                    pullClickListener.pullClick();
                }
            }
        });

        setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollChangedListener!=null) {
                    scrollChangedListener.scollStateChanged(lastItem, scrollState, view);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstvisibleItem, int visibleItemCount, int totalItemCount) {
                firstVisibleItem = firstvisibleItem;
                lastItem = firstVisibleItem + visibleItemCount - 2;
            }

        }));

        setClickable(false);
        setDivider(new ColorDrawable(0xffd4d5d6));
        setDividerHeight(1);
        currentScrollState = OnScrollListener.SCROLL_STATE_IDLE;
        currentState = LoadState.LOADSTATE_IDLE;
        firstVisibleItem = 0;
        CircleMarginTop = 76;
        setSelector(new ColorDrawable(Color.TRANSPARENT));
        setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
        setItemsCanFocus(true);
    }

//        @Override
//        public boolean dispatchTouchEvent(MotionEvent ev) {
//            float downY = ev.getY();
//            switch (ev.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    lastDownY = downY;
//                    break;
//            }
//            return super.dispatchTouchEvent(ev);
//        }

//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            float downY = event.getY();
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_UP:
//                    if (deltaCount > 0 && currentState != LoadState.LOADSTATE_LOADING
//                            && firstVisibleItem == 0
//                            && headView.getBottom() >= headViewHeight) {
//                        decreasePadding(deltaCount);
//                        loadDataForThreeSecond();
//                        startCircleAnimation();
//                    }
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    int nowDeltaCount = (int) ((downY - lastDownY) / 3.0);
//                    int grepDegree = nowDeltaCount - deltaCount;
//                    deltaCount = nowDeltaCount;
//                    if (deltaCount > 0 && currentState != LoadState.LOADSTATE_LOADING
//                            && firstVisibleItem == 0
//                            && headView.getBottom() >= headViewHeight) {
//                        setHeadViewPaddingTop(deltaCount);
//                        setCircleViewStay();
//                        CircleAnimation.startCWAnimation(circle,
//                                5 * (deltaCount - grepDegree), 5 * deltaCount);
//                    }
//                    break;
//                case MotionEvent.ACTION_DOWN:
//                    break;
//            }
//
//            return super.onTouchEvent(event);
//        }

    private void startCircleAnimation() {
        CircleAnimation.startRotateAnimation(circle);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_coupon_title:      // 班级视频
                if (bottomClickListener!=null)
                    bottomClickListener.bottomViewClick();
                break;
            case R.id.layout_balance_title:     // 班级公告
                if (AppServer.getInstance().getAccountInfo()!=null && AppServer.getInstance().getAccountInfo().getClassnotifyurl()!=null
                        && !AppServer.getInstance().getAccountInfo().getClassnotifyurl().equals("")) {
                    Intent intent = new Intent(context, CommonBrowser.class);
                    intent.putExtra(AppConstants.INTENT_URL, AppUtils.replaceUnifiedUrl(AppServer.getInstance().getAccountInfo().getClassnotifyurl()));
                    intent.putExtra(AppConstants.INTENT_NAME, "班级公告");
                    context.startActivity(intent);
                }
                break;
            case R.id.layout_contact_title:     // 班级安排
                if (AppServer.getInstance().getAccountInfo()!=null && AppServer.getInstance().getAccountInfo().getClassscheduleurl()!=null
                        && !AppServer.getInstance().getAccountInfo().getClassscheduleurl().equals("")) {
                    Intent intent = new Intent(context, CommonBrowser.class);
                    intent.putExtra(AppConstants.INTENT_URL, AppUtils.replaceUnifiedUrl(AppServer.getInstance().getAccountInfo().getClassscheduleurl()));
                    intent.putExtra(AppConstants.INTENT_NAME, "教学安排");
                    context.startActivity(intent);
                }
                break;
            case R.id.layout_class_video_type:  // 班级视频
                Intent intent = new Intent(context, ClassVideoActivity.class);
                context.startActivity(intent);
                break;
        }

    }

    private class DismissCircleThread implements Runnable {

        private final int COUNT = 10;
        private final int deltaMargin;

        public DismissCircleThread() {
            deltaMargin = CircleMarginTop / COUNT;
        }

        @Override
        public void run() {
            int temp = 0;
            for (int i = 0; i <= COUNT; i++) {
                if (i == 10) {
                    temp = 0;
                } else {
                    temp = CircleMarginTop - deltaMargin * i;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
                Message msg = Message.obtain();
                msg.what = DISMISS_CIRCLE;
                msg.arg1 = temp;
                handler.sendMessage(msg);
            }

        }
    }

    private void setCircleViewStay() {
        if (headView.getPaddingTop() > (CircleMarginTop)) {
            MarginLayoutParams lp = (MarginLayoutParams) circle
                    .getLayoutParams();
            lp.topMargin = CircleMarginTop - headView.getPaddingTop();
            circle.setLayoutParams(lp);
        }
    }

    private void loadDataForThreeSecond() {
        currentState = LoadState.LOADSTATE_LOADING;
        Message msg = Message.obtain();
        msg.what = LOAD_DATA;
        handler.sendMessageDelayed(msg, 3000);
    }

    private void setHeadViewPaddingTop(int deltaY) {
        headView.setPadding(0, deltaY, 0, 0);
    }

    private void decreasePadding(int count) {
        Thread thread = new Thread(new DecreaseThread(count));
        thread.start();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        lastItem = firstVisibleItem+visibleItemCount-2;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollChangedListener!=null) {
            scrollChangedListener.scollStateChanged(lastItem,scrollState,view);
        }
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                currentScrollState = SCROLL_STATE_FLING;
                break;
            case SCROLL_STATE_IDLE:
                Log.i("srcoll", imageLoader.toString() + "            onScrollStateChanged   ");
                new PauseOnScrollListener(imageLoader, false, false);
                currentScrollState = SCROLL_STATE_IDLE;
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                currentScrollState = SCROLL_STATE_TOUCH_SCROLL;
                break;
        }
    }

}