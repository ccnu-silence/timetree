/**
 *
 */
package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.ClassPhoto;
import com.yey.kindergaten.fragment.ClassPhotoFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.SyncHorizontalScrollView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 班级相册
 * @author chaowen
 *
 */
public class ClassPhotoMainActivity extends BaseActivity {

    @ViewInject(R.id.rl_nav)RelativeLayout rl_nav;
    @ViewInject(R.id.mHsv)SyncHorizontalScrollView mHsv;
    @ViewInject(R.id.rg_nav_content)RadioGroup rg_nav_content;
    @ViewInject(R.id.iv_nav_indicator)ImageView iv_nav_indicator;
    @ViewInject(R.id.iv_nav_left)ImageView iv_nav_left;
    @ViewInject(R.id.iv_nav_right)ImageView iv_nav_right;
    @ViewInject(R.id.mViewPager)ViewPager mViewPager;
    @ViewInject(R.id.right_tv)TextView right_tv;
    @ViewInject(R.id.header_title)TextView tv_headerTitle ;
    @ViewInject(R.id.left_btn)ImageView left_iv;
    @ViewInject(R.id.rl_tab)RelativeLayout rl_tab;
    @ViewInject(R.id.initLayout)FrameLayout initLayout;
    // 提示页面
    @ViewInject(R.id.common_network_disable)LinearLayout layout_networkdisable;
    @ViewInject(R.id.network_disable_button_relink)ToggleButton networkbutton;
    @ViewInject(R.id.common_loading)LinearLayout layout_loading;
    @ViewInject(R.id.common_error)LinearLayout layout_error;
    @ViewInject(R.id.error_button)ToggleButton errorbutton;
    @ViewInject(R.id.teacher_have_no_class_ll)LinearLayout layout_empty;

    private int indicatorWidth;
    private LayoutInflater mInflater;
    public static List<String> tabTitle = new ArrayList<String>(); // 班级列表 (标题)
    private TabFragmentPagerAdapter mAdapter;
    private int currentIndicatorLeft = 0;
    public static final String ARGUMENTS_NAME = "arg";
    public static List<ClassPhotoFragment> fragments;
    public ClassPhotoFragment curFragment ;
    public boolean editAction = false;
    public static List<ClassPhoto> list = new ArrayList<ClassPhoto>();
    List<Album> albumlist = new ArrayList<Album>();
    List<Album> anotherlist = new ArrayList<Album>();
    private int position;
    private String typefrom,classphototype; // 区分是否来自服务主页
    private int cid;
    private String cname;
    private boolean isActivityExist = true;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        isActivityExist = false;
        rl_nav = null;
        mHsv = null;
        rg_nav_content = null;
        iv_nav_indicator = null;
        iv_nav_left = null;
        iv_nav_right = null;
        mViewPager = null;
        right_tv = null;
        tv_headerTitle = null;
        left_iv = null;
        rl_tab = null;
        initLayout = null;
        layout_networkdisable = null;
        networkbutton = null;
        layout_loading = null;
        layout_error = null;
        errorbutton = null;
        layout_empty = null;

        mInflater = null;
        mAdapter = null;
        albumlist = null;
        anotherlist = null;

        setContentView(R.layout.activity_null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_photo);
        ViewUtils.inject(this);

        if (getIntent().getExtras()!=null) {
             typefrom = getIntent().getExtras().getString("typefrom");
             classphototype = getIntent().getExtras().getString("classphototype"); // 区分是否来自服务主页
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this); // 7- 27 龙衡东修改 BaseActivity已注册
        }

        setonClick();
        loadData();

        tv_headerTitle.setText("班级相册");
        left_iv.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        if (classphototype!=null && classphototype.equals(AppConstants.HOMEACTIVITY)) {
            list.clear();
            layout_loading.setVisibility(View.VISIBLE);
            AppServer.getInstance().loadClassPhoto(AppServer.getInstance().getAccountInfo().getUid(), new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (!isActivityExist) {
                        return;
                    }
                    tabTitle.clear();
                    layout_loading.setVisibility(View.GONE);
                    layout_networkdisable.setVisibility(View.GONE);
                    networkbutton.setChecked(true);
                    layout_error.setVisibility(View.GONE);
                    errorbutton.setChecked(true);
                    layout_empty.setVisibility(View.GONE);
                    if (code == AppServer.REQUEST_SUCCESS) {
                        list = (List<ClassPhoto>) obj;
                        if (list!=null && list.size() > 0) {
                            if (list.size() == 1 && list.get(0).getCname() == null) {
                                layout_empty.setVisibility(View.VISIBLE);
                            } else {
                                if (list.get(0).getAlbumlist()!=null && list.get(0).getAlbumlist().size() > 0) {
                                    albumlist.addAll(list.get(0).getAlbumlist());
                                }
                                for (int i = 0; i < list.size(); i++) {
                                    tabTitle.add(list.get(i).getCname());
                                }
                                if (list != null && list.size() == 1) {
                                    initView("1"); // 只有一个班，班级名称放标题
                                } else {
                                    initView("0"); // 多个班，班级名称放tabTitle
                                }
                                setListener();
                            }
                        } else {
                            layout_empty.setVisibility(View.VISIBLE);
                        }

                    } else if (code == AppServer.REQUEST_NETWORK_ERROR) {
                        layout_networkdisable.setVisibility(View.VISIBLE);
                    } else if (code == AppServer.REQUEST_CLIENT_ERROR) {
                        layout_error.setVisibility(View.GONE);
                    } else if (code == AppServer.REQUEST_UNREACHABLE_ERROR) {
                        layout_networkdisable.setVisibility(View.VISIBLE);
                        networkbutton.setChecked(true);
                    } else {
                        layout_empty.setVisibility(View.VISIBLE);
                    }

                }
            });
        } else {
            if (getIntent().getExtras()!=null) {
                cid = getIntent().getExtras().getInt("cid");
                cname = getIntent().getExtras().getString("cname");
            }
            layout_loading.setVisibility(View.VISIBLE);
            list.clear();
            AppServer.getInstance().loadClassPhotoByCid(AppServer.getInstance().getAccountInfo().getUid(), cid, new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (!isActivityExist) {
                        return;
                    }
                    tabTitle.clear();
                    layout_loading.setVisibility(View.GONE);
                    layout_networkdisable.setVisibility(View.GONE);
                    networkbutton.setChecked(true);
                    layout_error.setVisibility(View.GONE);
                    errorbutton.setChecked(true);
                    layout_empty.setVisibility(View.GONE);
                    if (code == AppServer.REQUEST_SUCCESS) {
                        anotherlist = (List<Album>) obj;
                        ClassPhoto classphoto = new ClassPhoto();
                        classphoto.setAlbumlist(anotherlist);
                        classphoto.setCid(cid);
                        classphoto.setCname(cname);
                        list.add(classphoto);
                        if (list != null && list.size() > 0) {
                            if (list.size() == 1 && list.get(0).getCname() == null) {
                                layout_empty.setVisibility(View.VISIBLE);
                            } else {
                                if (list.get(0).getAlbumlist() != null && list.get(0).getAlbumlist().size() > 0) {
                                    albumlist.addAll(list.get(0).getAlbumlist());
                                }
                                for (int i = 0; i < list.size(); i++) {
                                    tabTitle.add(list.get(i).getCname());
                                }
                                if (list != null && list.size() == 1) {
                                    initView("1");
                                } else {
                                    initView("0");
                                }
                                setListener();
                            }
                        } else {
                            layout_empty.setVisibility(View.VISIBLE);
                        }

                    } else if (code == AppServer.REQUEST_NETWORK_ERROR) {
                        layout_networkdisable.setVisibility(View.VISIBLE);
                    } else if (code == AppServer.REQUEST_CLIENT_ERROR) {
                        layout_error.setVisibility(View.GONE);
                    } else if (code == AppServer.REQUEST_UNREACHABLE_ERROR) {
                        layout_networkdisable.setVisibility(View.VISIBLE);
                        networkbutton.setChecked(true);
                    } else {
                        layout_empty.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void initView(String type) { // 1: 只有一个班，班级名称放标题; 0: 多个班，班级名称放tabTitle
        initLayout.setVisibility(View.GONE);
        fragments = new ArrayList<ClassPhotoFragment>();
        // 获得屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / tabTitle.size();

        LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth; // 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);
        if (type.equals("1")) {
            mHsv.setVisibility(View.GONE);
            if (tabTitle!=null && tabTitle.size() > 0) {
                tv_headerTitle.setText(tabTitle.get(0));
            }
        }
        // 获取布局填充器
        mInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        if (mAdapter == null) {
            mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), typefrom);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(0);
            ((RadioButton)rg_nav_content.getChildAt(0)).performClick();
        }

        right_tv.setText("编辑");
        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            right_tv.setVisibility(View.GONE);
        } else {
            right_tv.setVisibility(View.VISIBLE);
        }

    }

    private void setonClick(){
        networkbutton.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonview, boolean isChecked) {
                loadData();
            }
        });
    }

    private void initNavigationHSV() {
        rg_nav_content.removeAllViews();
        for (int i = 0; i < tabTitle.size(); i++){
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            rb.setText(tabTitle.get(i));
            rb.setLayoutParams(new LayoutParams(indicatorWidth, LayoutParams.MATCH_PARENT));

            rg_nav_content.addView(rb);
            fragments.add(new ClassPhotoFragment());
        }
        curFragment = fragments.get(0);
    }

    public static class TabFragmentPagerAdapter extends FragmentPagerAdapter{

        private FragmentManager fm;
        private String typefrom;

        public TabFragmentPagerAdapter(FragmentManager fm, String typefrom) {
            super(fm);
            this.fm = fm;
            this.typefrom = typefrom;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void setFragments() {
            if (fragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : fragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int arg0) {
            ClassPhotoFragment ft = null;
            switch (arg0) {
                default:
                    ft = fragments.get(arg0);
                    if (ft == null) {
                        ft = new ClassPhotoFragment();
                        fragments.add(ft);
                    }
                    Bundle args = new Bundle();
                    args.putSerializable(AppConstants.BUNDLE_ALBUM, (Serializable) list);
                    args.putInt(AppConstants.BUNDLE_INDEX, arg0);
                    args.putString(ARGUMENTS_NAME, tabTitle.get(arg0));
                    if (typefrom.equals(AppConstants.FROMSPEAK)) {
                        args.putString("typefrom", AppConstants.FROMSPEAK);
                    } else {
                        args.putString("typefrom", AppConstants.FROMFRIENDSTER);
                    }
                    ft.setArguments(args);
                    break;
            }
            return ft;
        }

        @Override
        public int getCount() {
            return tabTitle.size();
        }

    }

    private void setListener() {
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                curFragment = fragments.get(position);
                if (rg_nav_content!=null && rg_nav_content.getChildCount() > position) {
                    ((RadioButton)rg_nav_content.getChildAt(position)).performClick();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {  }

            @Override
            public void onPageScrollStateChanged(int arg0) { }

        });

        rg_nav_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rg_nav_content.getChildAt(checkedId)!=null) {
                    TranslateAnimation animation = new TranslateAnimation( currentIndicatorLeft ,
                            ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    // 执行位移动画
                    iv_nav_indicator.startAnimation(animation);
                    mViewPager.setCurrentItem(checkedId); // ViewPager 跟随一起 切换

                    // 记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
                    /*mHsv.smoothScrollTo(
                            (checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);*/
                }
            }
        });
    }

    @OnClick(value={R.id.left_btn,R.id.right_tv})
    public void setOnClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                if (editAction) {
                    // 取消编辑
                    editAction = false;
                    if (curFragment!=null) {
                        curFragment.showDelView(editAction);
                    }
                    right_tv.setText("编辑");
                    left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
                } else {
                    finish();
                }
                break;
            case R.id.right_tv:
                if (!editAction) {
                    editAction = true;
                    // 通知当前的fragment切换编辑状态
                    if (curFragment!=null) {
                        curFragment.showDelView(editAction);
                    }
                    right_tv.setText("删除");
                    left_iv.setImageResource(R.drawable.icon_close);
                } else {
                    // 提示删除并提交
                    editAction = false;
                    right_tv.setText("编辑");
                    left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
                    if (curFragment!=null) {
                        curFragment.sumitDel();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 刷新相册，添加了相册
     */
    public void refreshAddAlbum(){
        AppServer.getInstance().loadClassPhoto(AppServer.getInstance().getAccountInfo().getUid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (!isActivityExist) {
                    return;
                }
                if (code == AppServer.REQUEST_SUCCESS) {
                    list = (List<ClassPhoto>) obj;
                    curFragment.refresh(list);
                } else {
                    // 显示错误的页面
                }

            }
        });
    }

    public void refreshFriendsterAddAlbum(){
        list.clear();
        AppServer.getInstance().loadClassPhotoByCid(AppServer.getInstance().getAccountInfo().getUid(), cid, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (!isActivityExist) {
                    return;
                }
                if (code == AppServer.REQUEST_SUCCESS) {
                    anotherlist = (List<Album>) obj;
                    ClassPhoto classphoto =new ClassPhoto();
                    classphoto.setAlbumlist(anotherlist);
                    classphoto.setCid(cid);
                    classphoto.setCname(cname);
                    list.add(classphoto);
                    curFragment.refresh(list);
                } else {
                    // 显示错误的页面
                }
            }
        });
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.HOMEFRAGMENT_REFRESH_ADDALBUM) {
            // 刷新界面
            if (classphototype.equals(AppConstants.HOMEACTIVITY)) {
                refreshAddAlbum();
            } else {
                refreshFriendsterAddAlbum();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        refreshAddAlbum();
    }

}
