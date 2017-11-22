package com.yey.kindergaten.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CaptureActivity;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.ContactsAddFriendActivity;
import com.yey.kindergaten.activity.ServicePublishSpeakActivity;
import com.yey.kindergaten.adapter.FragmentAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.MyImageView;
import com.yey.kindergaten.widget.PhotoDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 家长的通讯录主页
 *
 * @author chaowen
 *
 */
public class NewContactFragement extends FragmentBase implements OnClickListener ,OnPageChangeListener,OnCheckedChangeListener{
    TextView titletextview; // 通讯录
    static ImageView rightbtn; // 右边点击的
    String popstate = "";
    private Animation pop_in;
    private static Animation pop_out;
    private static Boolean istop = true;
    public static boolean isback = true;
    static RelativeLayout rl_menu; // 子菜单
    MyImageView sreachiv;
    MyImageView addfriendiv;
    MyImageView sendspeakiv;
    int itemclickposition;
    AppContext appcontext = null;
    Contacts contacts;
    int clickposition;
    AccountInfo accountInfo;

    List<Fragment> fragmentList = new ArrayList<Fragment>();
    PuacActivity puacFragment = new PuacActivity();
    // FriendFragment friendFragment = new FriendFragment();
    KingderFragment teacherFragment = new KingderFragment();
    FragmentAdapter fragAdapter;
    ViewPager viewpager;

    private RadioGroup radioGroup = null;
    private RadioButton radioButton1 = null;
//  private RadioButton radioButton2 = null;
    private RadioButton radioButton3 = null;
    private TextView smovetv = null;
    @ViewInject(R.id.right_tv)TextView right_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newcontacts_main, container, false);
        ViewUtils.inject(this, view);
        titletextview = (TextView) view.findViewById(R.id.header_title);
        titletextview.setText(R.string.contacts_public_contacts);
        rightbtn = (ImageView)view.findViewById(R.id.right_btn);
        rightbtn.setVisibility(View.GONE);
        rl_menu = (RelativeLayout) view.findViewById(R.id.contacts_main_menu_btn);
        sreachiv = (MyImageView) view.findViewById(R.id.btn_top_barcodeiv);
        addfriendiv = (MyImageView) view.findViewById(R.id.id_addfriend_iv);
        sendspeakiv = (MyImageView) view.findViewById(R.id.id_sendspeak_iv);
        viewpager = (ViewPager) view.findViewById(R.id.contacts_main_viewpage);
        radioButton1 = (RadioButton) view.findViewById(R.id.radioButton1);
        radioButton1.setTextColor(this.getActivity().getResources().getColor(R.color.radio_button_check_color));
        // radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) view.findViewById(R.id.radioButton3);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        smovetv = (TextView) view.findViewById(R.id.smovetextview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appcontext = AppContext.getInstance();
        accountInfo = AppServer.getInstance().getAccountInfo();
        radioGroup.setOnCheckedChangeListener(this);

//      setOnClick();
        initview();
        initData();
        setOnClick();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this); // 7- 27 龙衡东修改 BaseActivity已注册
        }
    }

    public PuacActivity getPuacFragment() {
        return puacFragment;
    }

    public void initview() {
        right_tv.setText("发通知");
//      if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            right_tv.setVisibility(View.GONE); // 有统一入口后，隐藏
//      } else {
//          right_tv.setVisibility(View.VISIBLE);
//      }
    }
 
    public void initData() {
        pop_in = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_in);
        pop_out = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_out);
        // fragmentList.add(friendFragment);
        fragmentList.add(teacherFragment);
//        fragmentList.add(puacFragment);
        fragAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList);
        viewpager.setAdapter(fragAdapter);
        viewpager.setOnPageChangeListener(this);
    }
    
    public void refreshFragment() {
        if (AppContext.getInstance().isRefresh()) { // 判断是否刷新
            puacFragment.refreshFrament();
            // friendFragment.refreshFrament();
            teacherFragment.refreshFrament();
            fragAdapter.notifyDataSetChanged();
        }
    }
    
    public void setOnClick() {
        rightbtn.setOnClickListener(this);
        sreachiv.setOnClickListener(this);
        addfriendiv.setOnClickListener(this);
        sendspeakiv.setOnClickListener(this);
        rl_menu.setOnClickListener(this);
        right_tv.setOnClickListener(this);
    }

    private long mExitTime;
    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }
    
    public void refresh() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    AppContext.getInstance().initContacts();
                    refreshFragment();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hidden;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    public void showWaringDialog(int role) {
        PhotoDialog dialog = new PhotoDialog(getActivity(), role);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.right_tv:
                AccountInfo info = AppServer.getInstance().getAccountInfo();
                if (info.getKid() == 0) {
                    if (info.getRole() == 0) {
                       showWaringDialog(0);
                    } else if (info.getRole() == 1) {
                       showWaringDialog(1);
                    }
                    return;
                }
                if (info.getNoticeurl()!=null && !info.getNoticeurl().equals("")) {
                    Bundle noticebundle = new Bundle();
                    noticebundle.putString(AppConstants.INTENT_URL, info.getNoticeurl());
                    noticebundle.putString(AppConstants.INTENT_NAME, "发通知");
                    startAnimActivity(CommonBrowser.class, noticebundle);
                }
                break;
            case R.id.right_btn:
                if (istop) {
                    rl_menu.setFocusable(true);
                    rl_menu.setClickable(true);
                    rl_menu.setEnabled(true);
                    rightbtn.setImageResource(R.drawable.icon_plus);
                    rl_menu.startAnimation(pop_in);
                    rl_menu.setVisibility(View.VISIBLE);
                    istop = false;
                    isback = false;
                } else {
                    rightbtn.setImageResource(R.drawable.icon_plus);
                    rl_menu.startAnimation(pop_out);
                    rl_menu.setVisibility(View.GONE);
                    istop = true;
                    isback = true;
                }
                break;
            case R.id.btn_top_barcodeiv: // 扫一扫
                if (!istop) {
                    rightbtn.setImageResource(R.drawable.icon_plus);
                    rl_menu.startAnimation(pop_out);
                    rl_menu.setVisibility(View.GONE);
                    istop = true;
                }
                isback = true;
                intent = new Intent(getActivity(), CaptureActivity.class);
                intent.putExtra("state", AppConstants.CONTACTS);
                startActivity(intent);
                break;
            case R.id.id_addfriend_iv: // 加好友
                if (!istop) {
                    rightbtn.setImageResource(R.drawable.icon_plus);
                    rl_menu.startAnimation(pop_out);
                    rl_menu.setVisibility(View.GONE);
                    istop = true;
                }
                isback = true;
                intent = new Intent(getActivity(), ContactsAddFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.id_sendspeak_iv: // 发说说
                if (!istop) {
                    rightbtn.setImageResource(R.drawable.icon_plus);
                    rl_menu.startAnimation(pop_out);
                    rl_menu.setVisibility(View.GONE);
                    istop = true;
                }
                isback = true;
                intent = new Intent(getActivity(), ServicePublishSpeakActivity.class);
                intent.putExtra("type", AppConstants.MAINSPEAK);
                startActivity(intent);
                break;
            case R.id.contacts_main_menu_btn:
                rightbtn.setImageResource(R.drawable.icon_plus);
                rl_menu.startAnimation(pop_out);
                rl_menu.setVisibility(View.GONE);
                rl_menu.setFocusable(false);
                rl_menu.setClickable(false);
                rl_menu.setEnabled(false);
                istop = true;
                isback = true;
                break;
            default:
                break;
            }
    }

    @Override
    public void onPageScrollStateChanged(int position) { }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int x = (int)((position + positionOffset) * smovetv.getWidth());
        ((View)smovetv.getParent()).scrollTo(-x, smovetv.getScrollY());
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            radioButton1.setChecked(true);
            radioButton1.setTextColor(this.getActivity().getResources().getColor(R.color.radio_button_check_color));
            // radioButton2.setTextColor(this.getActivity().getResources().getColor(R.color.contact_mainlvtitletv));
            radioButton3.setTextColor(this.getActivity().getResources().getColor(R.color.contact_mainlvtitletv));
        }
//      else if (position == 1) {
//          radioButton2.setChecked(true);
//          radioButton2.setTextColor(this.getActivity().getResources().getColor(R.color.radio_button_check_color));
//          radioButton1.setTextColor(this.getActivity().getResources().getColor(R.color.contact_mainlvtitletv));
//          radioButton3.setTextColor(this.getActivity().getResources().getColor(R.color.contact_mainlvtitletv));
//      }
        else if (position == 1) {
            radioButton3.setChecked(true);
            radioButton3.setTextColor(this.getActivity().getResources().getColor(R.color.radio_button_check_color));
            // radioButton2.setTextColor(this.getActivity().getResources().getColor(R.color.contact_mainlvtitletv));
            radioButton1.setTextColor(this.getActivity().getResources().getColor(R.color.contact_mainlvtitletv));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int current = viewpager.getCurrentItem();
            switch (checkedId) {
                case R.id.radioButton1: {
                    if (current != 0) {
                        viewpager.setCurrentItem(0);
                    }
                } break;
//                case R.id.radioButton2: {
//                    if (current != 1) {
//                        viewpager.setCurrentItem(1);
//                    }
//                } break;
                case R.id.radioButton3: {
                    if (current != 1) {
                        viewpager.setCurrentItem(1);
                    }
                } break;
            }
    }

    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!istop) {
                rightbtn.setImageResource(R.drawable.icon_plus);
                rl_menu.setVisibility(View.GONE);
                rl_menu.startAnimation(pop_out);
                istop = true;
                isback = true;
            }
        }
        return true;
    }

    public void hidePullMenu() {
        if (rl_menu.getVisibility() == View.VISIBLE) {
            rl_menu.setVisibility(View.GONE);
        }
        istop = true;
        isback = true;
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.HOMEFRAGMENT_REFRESH_CONTACT) {
            // 刷新界面
            refresh();
        }
    }

}
