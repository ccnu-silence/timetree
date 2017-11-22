package com.yey.kindergaten.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
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
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.LifePhotoCount;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.LifeWorkFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.SyncHorizontalScrollView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 生活剪影类
 * 供老师查看
 * @author zy
 *
 */
public class ServiceLifePhotoMainActivity extends BaseActivity implements OnClickListener {	
	
	//导航栏控件
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.right_btn)ImageView right_btn;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.right_tv)TextView right_tv;
	
	//其他内部控件	
	@ViewInject(R.id.common_loading)
	static LinearLayout loading_ll;
	@ViewInject(R.id.id_show_upload_percent_tv)TextView percent_tv;
	@ViewInject(R.id.id_show_upload_state)ImageView upload_state;	
	@ViewInject(R.id.network_listener_ll)RelativeLayout netCheckRL;
	@ViewInject(R.id.network_listener_tv)TextView netCheckTv;
	
	@ViewInject(R.id.rl_nav)RelativeLayout rl_nav;
	@ViewInject(R.id.mHsv)SyncHorizontalScrollView mHsv;
	@ViewInject(R.id.rg_nav_content)RadioGroup rg_nav_content;
	@ViewInject(R.id.iv_nav_indicator)ImageView iv_nav_indicator;
	@ViewInject(R.id.iv_nav_left)ImageView iv_nav_left;
	@ViewInject(R.id.iv_nav_right)ImageView iv_nav_right;
	@ViewInject(R.id.mViewPager)ViewPager mViewPager;
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.left_btn)ImageView left_iv;
	@ViewInject(R.id.rl_tab)RelativeLayout rl_tab;
	@ViewInject(R.id.initLayout)FrameLayout initLayout;
	
    @ViewInject(R.id.common_loading)LinearLayout layout_loading;
    @ViewInject(R.id.common_error)LinearLayout layout_error;
    @ViewInject(R.id.error_button)ToggleButton errorbutton;
    @ViewInject(R.id.teacher_have_no_class_ll) LinearLayout layout_empty;
	public static List<LifeWorkFragment> fragments;
	public static List<String> tabTitle = new ArrayList<String>(); // 标 题
	private int indicatorWidth;
	private int currentIndicatorLeft = 0;
	public static final String ARGUMENTS_NAME = "arg";
	private LayoutInflater mInflater;
	public LifeWorkFragment curFragment ;
	private TabFragmentPagerAdapter mAdapter;

    DialogTips dialog;
    private boolean isrunning;

	private static int position; // 点击学期列表的位置
	private String name;	
	private boolean isRun = false;
	private AnimationDrawable animationDrawable;  
	private boolean isFlag = true;
	private ConnectivityManager connectivityManager;	
	private NetworkInfo info;
	private static String terms;	 
    private static boolean is_uploading = false;
    private static String type; // 1，生活剪影； 2，手工作品

	private Handler netHandler = new Handler(){
	    public void handleMessage(android.os.Message msg) {
			if (msg.what == AppConstants.NET_SENDMESSAG_WHAT_CODE_HASNET) {
//		   		netCheckRL.setVisibility(View.GONE);
                if (animationDrawable!=null) {
                    animationDrawable.start();
                }
            } else {
//				netCheckRL.setVisibility(View.VISIBLE);
//			  	netCheckTv.setText("网络不可用，请检查您的网络设置。");
                if (animationDrawable!=null) {
                    animationDrawable.stop();
                }
            }
		}
	};

	/**
	 * 为什么进到了里面，却没有更新界面
	 * handlemessage更新界面不成功
	 */
	private Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 666) {
			    isRun = true;
			    upload_state.setVisibility(View.VISIBLE);
				if (animationDrawable.isRunning() && isFlag) {
            		isFlag = false;
            		animationDrawable.start();
            	}
			} else if (msg.what == 777) {
				isRun = false;
				animationDrawable.stop();
			}
			super.handleMessage(msg);
		}
	};

	private static final int PHOTO_SUCCESS = 1; // 拍照
    private static final int CAMERA_SUCCESS = 2; // 相册
	CharSequence[] items = { "拍照上传", "手机上传照片","管理照片" };

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 3) {
                position = (Integer) msg.obj;
                SharedPreferencesHelper.getInstance(ServiceLifePhotoMainActivity.this).
                                                         setInt("termposition", position);
                loading_ll.setVisibility(View.VISIBLE);
                
		        AppServer.getInstance().getLifePhoto(type,termList.get(position).getCid(), termList.get(position).getTerm(), new OnAppRequestListener() {
                    @Override
					public void onAppRequest(int code, String message, Object obj) {
                        if (code == 0) {
                            LifePhoto[] bean = (LifePhoto[]) obj;
                            listPhotoCount.clear();
						    for (int i = 0; i < bean.length; i++) {
						    	listPhotoCount.add(bean[i]);
						    }
						    loading_ll.setVisibility(View.GONE);
					    }
				    }
				});
			}
		}
	};

	private Handler termHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 111) {
				int position = SharedPreferencesHelper.getInstance(ServiceLifePhotoMainActivity.this).getInt("termposition", 0);
				termList = (List<Term>) msg.obj;
//				term = termList.get(position).getTerm();
		        AppServer.getInstance().getLifePhoto(type,termList.get(position).getCid(), termList.get(position).getTerm(), new OnAppRequestListener() {
                    @Override
					public void onAppRequest(int code, String message, Object obj) {
						if (code == 0) {
							loading_ll.setVisibility(View.GONE);
							LifePhoto[] bean = (LifePhoto[]) obj;
							listPhotoCount.clear();
						    for (int i = 0; i < bean.length; i++) {
						    	listPhotoCount.add(bean[i]);
						    }
					    }
				    }
				});
			}
		}
	};

	private static List<LifePhoto>listPhotoCount = new ArrayList<LifePhoto>();
	private LifePhotoCount photoCount;
	private static List<Term>termList = new ArrayList<Term>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifephoto_main);
        ViewUtils.inject(this);
        // 监听网络广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
        type = this.getIntent().getStringExtra("type");

        initHeadView();
        initData();
        isrunning = true;
        // 注册动态广播，更新上传状态
        IntentFilter filter = new IntentFilter(UploadImageActivity.action);
        registerReceiver(broadcastReceiver, filter);
    }

    private void initHeadView(){
        titletv.setVisibility(View.VISIBLE);
        if (type!=null) {
            if (type.equals("1")) {
                titletv.setText("生活剪影");
            } else {
                titletv.setText("手工作品");
            }
        }
        left_btn.setVisibility(View.VISIBLE);
        right_tv.setVisibility(View.VISIBLE);
        right_tv.setText("批量");
        left_btn.setOnClickListener(this);
    }

    public void showDialog(String title, String message, String buttonText, DialogInterface.OnClickListener onSuccessListener, DialogInterface.OnClickListener OnCancelListener) {
        DialogTips dialog = new DialogTips(this, title, message, buttonText,true,true);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        dialog.SetOnCancelListener(OnCancelListener);
        dialog.setCancel(false);
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    private void initData() {
        loading_ll.setVisibility(View.VISIBLE);
        AppServer.getInstance().getChildPhoto(AppServer.getInstance().getAccountInfo().getUid(), type, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == 0) {
                    Term[]term = (Term[]) obj;
                    if (term == null || term.length == 0) {
                        return;
                    }
                    termList.clear();
                    for(int i = 0; i < term.length; i++) {
                        if (term[i].getPhoto().size()!=0) {
                            termList.add(term[i]);
                        }
                    }
                    Iterator<Term> it = termList.iterator();

                    terms = termList.get(0).getTerm();
                    tabTitle.clear();
                    while (it.hasNext()) {
                        Term photo = (Term) it.next();
                        tabTitle.add(photo.getCname());
                    }
                    if (termList!=null && termList.size() == 1) {
                        initView("1");
                    } else {
                        initView("0");
                    }
                    initClick();
                    setListener();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        refreshAddAlbum();
        super.onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        isrunning = false;
        super.onStop();
    }

    /**
    *监听网络广播
    */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    netHandler.sendEmptyMessage(AppConstants.NET_SENDMESSAG_WHAT_CODE_HASNET);// 表示有网络
                } else {
                    netHandler.sendEmptyMessage(AppConstants.NET_SENDMESSAG_WHAT_CODE_NONET); // 表示没网络
                }
            }
        }
    };
	    
    private void initView(String type) {
        animationDrawable = (AnimationDrawable) upload_state.getDrawable();
        initLayout.setVisibility(View.GONE);
        fragments = new ArrayList<LifeWorkFragment>();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels / tabTitle.size();

        LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);
        if (type.equals("1")) {
            mHsv.setVisibility(View.GONE);
            if (tabTitle!=null && tabTitle.size() > 0) {
                titletv.setText(tabTitle.get(0));
            }
        }
        // 获取布局填充器
        mInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        if (mAdapter == null) {
            mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(0);
            ((RadioButton)rg_nav_content.getChildAt(0)).performClick();
        }
    }
	   
    protected  void  initClick(){
        right_tv.setOnClickListener(this);
    }

    private void initNavigationHSV() {
        rg_nav_content.removeAllViews();
        for (int i = 0; i < tabTitle.size(); i++) {
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            rb.setId(i);
            rb.setText(tabTitle.get(i));
            rb.setLayoutParams(new LayoutParams(indicatorWidth, LayoutParams.MATCH_PARENT));
            rg_nav_content.addView(rb);
            fragments.add(new LifeWorkFragment());
        }
        curFragment = fragments.get(0);
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
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageScrollStateChanged(int arg0) { }

        });

        rg_nav_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rg_nav_content.getChildAt(checkedId)!=null) {
                    TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft,
                            ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    iv_nav_indicator.startAnimation(animation); // 执行位移动画
                    mViewPager.setCurrentItem(checkedId);	// ViewPager 跟随一起 切换

                    // 记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();

                    /*mHsv.smoothScrollTo(
                            (checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);*/
                }
            }
        });
    }
	    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.id_show_upload_state:
                Intent uploadIntent = new Intent(ServiceLifePhotoMainActivity.this,	UploadImageActivity.class);
                LifePhoto lifePhoto = listPhotoCount.get(position);
                uploadIntent.putExtra(AppConstants.PARAM_ALBUMID,lifePhoto);
                uploadIntent.putExtra("fromtype", "uploading");
                Bundle bundles = new Bundle();
                uploadIntent.putExtras(bundles);
                startActivity(uploadIntent);
                break;
            case R.id.right_tv:
                  AppContext.checkList.clear();
                  AppContext.getInstance().getUidlist().clear();
                  Intent intent=new Intent(this,BatchLifePhotoActivity.class);
                  Bundle bundle = new Bundle();
                  BatchLifePhotoActivity.decs = null;
                  bundle.putString("type", AppConstants.PARAM_UPLOAD_BATCH);
                  int index = 0;
                  for (int i = 0; i < fragments.size(); i++) {
                      if (fragments.get(i) == curFragment) {
                          index = i;
                      }
                  }
                  bundle.putSerializable("term", termList.get(index));
                  terms = termList.get(index).getTerm();
                  bundle.putString("terms", terms);
                  bundle.putString("lifetype", type);
                  intent.putExtras(bundle);
                  startActivity(intent);
                break;
        }
    }

    public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager fm;
        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void setFragments() {
            if (fragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for(Fragment f:fragments){
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
            LifeWorkFragment ft = null;
            switch (arg0) {
                default :
                    ft = fragments.get(arg0);
                    if (ft == null) {
                        ft = new LifeWorkFragment();
                        fragments.add(ft);
                    }
                    Bundle args = new Bundle();
                    args.putSerializable(AppConstants.BUNDLE_ALBUM, (Serializable) termList);
                    args.putString("type", type);
                    args.putInt(AppConstants.BUNDLE_INDEX, arg0);
                    args.putString(ARGUMENTS_NAME, tabTitle.get(arg0));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getExtras().getString("type");
            if (type.equals("begin")) {
                is_uploading = true;
                upload_state.setVisibility(View.VISIBLE);
                if (animationDrawable.isRunning() && isFlag) {
                    isFlag = false;
                    animationDrawable.start();
                }
            } else if (type.equals("over")) {
                is_uploading = false;
//		        		Toast.makeText(ServiceLifePhotoMainActivity.this, "上传完成！", 2000).show();
                animationDrawable.stop();
                upload_state.setVisibility(View.GONE);
                position = SharedPreferencesHelper.getInstance(ServiceLifePhotoMainActivity.this).getInt("termposition", 0);
                loading_ll.setVisibility(View.VISIBLE);
                try {
                    List<Term> list = DbHelper.getDB(ServiceLifePhotoMainActivity.this).findAll(Term.class);
                    if (list !=null) {
                        android.os.Message msg=new  android.os.Message();
                        msg.what = 111;
                        msg.obj = list;
                        termHandler.sendMessage(msg);
                    } else {
                        initData();
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }
    };

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
                case CAMERA_SUCCESS:
                    break;
                case PHOTO_SUCCESS:
                    break;
                }
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        isrunning = false;
        if (dialog!=null && dialog.isShowing()) {
            dialog.cancel();
        }
		unregisterReceiver(broadcastReceiver);
		unregisterReceiver(mReceiver);
	}

    /**
     * 刷新相册，添加了相册
     */
    public void refreshAddAlbum(){     	  	
    	AppServer.getInstance().getChildPhoto(AppServer.getInstance().getAccountInfo().getUid(), type, new OnAppRequestListener() {			
  			@Override
  			public void onAppRequest(int code, String message, Object obj) {
  				if (code == AppServer.REQUEST_SUCCESS) {
  					Term[]term=(Term[]) obj;
					termList.clear();
					for (int i = 0; i < term.length; i++) {
						if (term[i].getPhoto().size()!=0) {
							termList.add(term[i]);
						}
    			    }
  					curFragment.refresh(termList);
  				} else {
  					// 显示错误的页面
  				}
  			}
  		});
    }

}
