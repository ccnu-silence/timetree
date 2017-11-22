/**
 * 
 */
package com.yey.kindergaten.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CaptureActivity;
import com.yey.kindergaten.activity.ClassesInviteActivity;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.ContactsAddFriendActivity;
import com.yey.kindergaten.activity.IdSafeActivity;
import com.yey.kindergaten.activity.LoginActivity;
import com.yey.kindergaten.activity.MeAboutUsActivity;
import com.yey.kindergaten.activity.MeCardActivity;
import com.yey.kindergaten.activity.MeInfoActivity;
import com.yey.kindergaten.activity.MeModifyKinderActivity;
import com.yey.kindergaten.activity.SendNotificationActivity;
import com.yey.kindergaten.activity.ServiceCreatKinderSelectActivity;
import com.yey.kindergaten.activity.ServicePublishSpeakActivity;
import com.yey.kindergaten.activity.ServiceScheduleWriteActivity;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.StringUtils;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.MyListViewWithScrollView;
import com.yey.kindergaten.widget.PhotoDialog;

import java.util.ArrayList;

/**
 * "我"
 * @author chaowen
 *
 */
public class MeFragement extends FragmentBase{
    @ViewInject(R.id.right_tv) TextView right_tv;
    @ViewInject(R.id.right_btn) static ImageView iv_right;
    @ViewInject(R.id.menu_btn) static RelativeLayout rl_menu;
    @ViewInject(R.id.menu_btn_parent) static RelativeLayout  rlMenuParent;
    @ViewInject(R.id.me_info_iv) CircleImageView me_info_iv;
    @ViewInject(R.id.me_info_titletv) TextView me_info_tv;
    @ViewInject(R.id.activity_me_main_lvup) MyListViewWithScrollView uplv;
    @ViewInject(R.id.activity_me_main_lvmiddle) MyListViewWithScrollView middlelv;
    @ViewInject(R.id.activity_me_main_lvdown) MyListViewWithScrollView downlv;
    @ViewInject(R.id.me_main_addservice) LinearLayout addservice_ll;
    @ViewInject(R.id.header_title) TextView tv;
    @ViewInject(R.id.btn_me_signout) Button btn_signout;

    public static Boolean istop = true;
    public static boolean isback = true;
    private static Animation pop_in;
    private static Animation pop_out;
    private ServiceAdapter upadapter;
    private ServiceAdapter middleadapter;
    private ServiceAdapter downadapter;
    private ArrayList<String> uplist = new ArrayList<String>();
    private ArrayList<String> middlelist = new ArrayList<String>();
    private ArrayList<String> downlist = new ArrayList<String>();
    private ArrayList<Integer> upiconlist = new ArrayList<Integer>();
    private ArrayList<Integer> middleiconlist = new ArrayList<Integer>();
    private ArrayList<Integer> downiconlist = new ArrayList<Integer>();
    private AccountInfo  accountInfo;
    private static int role = 0; // 1表示老师
    private  static final String TAG = "MeFragement";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView()");
        View view = inflater.inflate(R.layout.activity_me_main1, container, false);
        ViewUtils.inject(this, view);
        accountInfo = AppServer.getInstance().getAccountInfo();
        role = accountInfo.getRole();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // accountInfo = AppServer.getInstance().getAccountInfo();
        Log.i(TAG,"onActivityCreated()");
        initView();
    }

    /** 返回界面刷新 */
    @Override
    public void onResume() {
        super.onResume();
        UtilsLog.i(TAG, "onResume");
        accountInfo = AppServer.getInstance().getAccountInfo();
        setRealName();
        reFreshHead();
    }

    // 刷新头像
    private void reFreshHead(){
        accountInfo = AppServer.getInstance().getAccountInfo();
        if (accountInfo.getAvatar()!=null) {
            ImageLoader.getInstance().displayImage(accountInfo.getAvatar(), me_info_iv, ImageLoadOptions.getHeadOptions());
        }
        if (upadapter!=null) {
            upadapter.accountInfo = accountInfo;
            upadapter.notifyDataSetChanged();
        }
    }

    // 更新真实姓名
    private void setRealName() {
        String suffix = "";
        if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
            suffix = accountInfo.getJob() == null || accountInfo.getJob().length() == 0?"园长":accountInfo.getJob();
        } else if (accountInfo.getRole() == AppConstants.TEACHERROLE) {
            suffix = accountInfo.getJob() == null || accountInfo.getJob().length() == 0?"老师":accountInfo.getJob();
        } else {
            suffix = StringUtils.getCall(accountInfo.getRole(), accountInfo.getRelationship());
//            int relationship = accountInfo.getRelationship();
//            switch (relationship) {
//                case 1:
//                    suffix = "爸爸";
//                    break;
//                case 2:
//                    suffix = "妈妈";
//                    break;
//                case 3:
//                    suffix = "爷爷";
//                    break;
//                case 4:
//                    suffix = "奶奶";
//                    break;
//                case 5:
//                    suffix = "外公";
//                    break;
//                case 6:
//                    suffix = "外婆";
//                    break;
//                case 7:
//                    suffix = "叔叔";
//                    break;
//                case 8:
//                    suffix = "阿姨";
//                    break;
//                default:
//                    suffix = "家长";
//                    break;
//            }
        }
        if (accountInfo.getRealname()!=null && !accountInfo.getRealname().equals("")) {
            me_info_tv.setText(accountInfo.getRealname() + "   " + suffix);
            UtilsLog.i(TAG, "realname is :" + accountInfo.getRealname());
        } else {
            me_info_tv.setText("未填写" + "   " + suffix);
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 5) {
            UtilsLog.i(TAG,"accepte the new kname");
            if (intent.getExtras() != null) {//真实姓名
                String kName = intent.getExtras().getString("kname");
                if (kName!=null && !kName.equals("")) {
                    accountInfo.setKname(kName);
                    upadapter = new ServiceAdapter(getActivity(), uplist, upiconlist, accountInfo, AppConstants.MEMAIN_UP);
                    uplv.setAdapter(upadapter);
                    //AppServer.getInstance().getAccountBean().setKname(kName);
                }
            }
        } else {
            return;
        }
    }*/

    private void initView() {
        tv.setText("我");
        right_tv.setText("发通知");
//        if (AppServer.getInstance().getAccountInfo().getRole() == AppConstants.PARENTROLE) { // 家长没有"发通知"
            right_tv.setVisibility(View.GONE); // 首页有统一入口，故隐藏
//        } else {
//            right_tv.setVisibility(View.VISIBLE);
//        }
        iv_right.setVisibility(View.GONE);
        pop_in = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_in); // 初始化动画
        pop_out = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_out);

        setRealName();
        if (accountInfo!=null && accountInfo.getAvatar()!=null) {
            ImageLoader.getInstance().displayImage(accountInfo.getAvatar(), me_info_iv, ImageLoadOptions.getHeadOptions());
        }

//      uplist.add("二维码名片");
//      upiconlist.add(R.drawable.icon_me_code);
//      if (accountInfo!=null && accountInfo.getRole() == 0 && !accountInfo.getMicroweb().equals("")) {
//          uplist.add("微网站");
//          upiconlist.add(R.drawable.icon_service_main_site);
//      }

        /* 上半部 */
        uplist.add("幼儿园");                       // 幼儿园
        if (accountInfo!=null && accountInfo.getKid() == 0) {
            upiconlist.add(R.drawable.me_main_addkindergaten);
        } else {
            upiconlist.add(R.drawable.me_main_mykindergaten);
        }
        if (accountInfo!=null && accountInfo.getRole() == AppConstants.DIRECTORROLE) {            // 邀请老师加入 / 邀请家长加入 | 我的班级
//            uplist.add("邀请老师加入");
//            upiconlist.add(R.drawable.me_main_addkindergaten);
//            uplist.add("邀请家长加入");
//            upiconlist.add(R.drawable.me_main_addkindergaten);
        } else {
            if (accountInfo!=null && accountInfo.getRole() == AppConstants.TEACHERROLE
                    && accountInfo.getRights()!=null && accountInfo.getRights().contains("120")) {
                UtilsLog.i(TAG, "No Right of invite parent ");
            } else {
                uplist.add("我的班级");
                upiconlist.add(R.drawable.me_main_myclass);
            }
        }

        /* 中部 */
        if (accountInfo!=null) {
            middlelist.add(accountInfo.getPaytitle());
            middleiconlist.add(R.drawable.icon_me_addservice);
        }

        /* 下半部 */
        downlist.add("账号安全");                   // 账号安全
        downiconlist.add(R.drawable.icon_me_accuntsafe);
        downlist.add("关于时光树");                 // 关于时光树
        downiconlist.add(R.drawable.icon_me_aboutus);

        upadapter = new ServiceAdapter(getActivity(), uplist, upiconlist, accountInfo, AppConstants.MEMAIN_UP);
        uplv.setAdapter(upadapter);
        upadapter = new ServiceAdapter(getActivity(), middlelist, middleiconlist, accountInfo, AppConstants.MEMAIN_MIDDLE);
        middlelv.setAdapter(upadapter);
        downadapter = new ServiceAdapter(getActivity(), downlist, downiconlist, accountInfo, AppConstants.MEMAIN_DOWN);
        downlv.setAdapter(downadapter);

        if (accountInfo.getRole() == AppConstants.PARENTROLE && accountInfo.getRights().contains(AppConstants.PARENT_ADDVALUE_RIGHTS + "")) {
            addservice_ll.setVisibility(View.VISIBLE);
        } else {
            addservice_ll.setVisibility(View.GONE);
        }

        /* 上半部点击 */
        uplv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent;
                role = accountInfo.getRole();
                /* 家长不让点，老师在kid为0的时候可以邀请，园长可以修改名称 */
                if (position == 0) {                                        // 幼儿园
                    if (role == AppConstants.PARENTROLE) {
                        return;  // 家长不让点击
                    }
                    if (accountInfo.getKid()!=0) {
                        // 园长修改幼儿园名字
                        if (role!=AppConstants.DIRECTORROLE) {
                            return;
                        }
                        intent = new Intent(getActivity(), MeModifyKinderActivity.class);
                        intent.putExtra(AppConstants.STATE, AppConstants.MEINFO);
                        if (accountInfo.getKname()!=null) {
                            intent.putExtra("kname", accountInfo.getKname());
                        }
                        startActivity(intent); // 目前只有一个返回结果的(园长修改幼儿园名称)，暂且定义为1吧
                    } else {
                        String url;
                        if (role == AppConstants.DIRECTORROLE) {
                            url = AppUtils.replaceUrlByUrl(AppConstants.HX_DIRECTOR_ACTION, 0);
                        } else {
                            url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_NO_KID, 0);
                        }
                        AppUtils.startWebUrlForGuide(MeFragement.this.getActivity(), url);
//                              showWaringDialog(0);
                    }
                /* 家长：不让点，
                   园长：邀请老师加入，
                   老师：没有kid，提示加入班级
                        有kid没班级，选择班级
                        有kid有班级，进入班级列表邀请界面*/
                } else if (position == 1) {                                  // 我的班级 / 邀请老师加入
                    if (role == AppConstants.DIRECTORROLE) { // 园长
                        if (accountInfo.getKid()!=0) {
                            String url= AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_TEACHER, 0);
                            UtilsLog.i(TAG, url + "");
                            AppUtils.startWebUrlForGuide(MeFragement.this.getActivity(), url);
                        } else {
                            // 需添加引导页
                            showWaringDialog(0);
                        }
                    } else if (role == AppConstants.TEACHERROLE) { // 老师
                        if (accountInfo.getKid()!=0) {
                            if (AppContext.getInstance().getContacts().getClasses()!=null
                                    && AppContext.getInstance().getContacts().getClasses().size()!=0) {
                                // 有Kid，有班级
                                intent = new Intent(getActivity(), ClassesInviteActivity.class);
                                startActivity(intent);
                            } else { // 有kid，但是没有班级
                                String url= AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_HAS_KID, 0);
                                AppUtils.startWebUrlForGuide(MeFragement.this.getActivity(), url);
                            }
                        } else { // 没有kid, 邀请加入班级
                            String url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_NO_KID, 0);
                            AppUtils.startWebUrlForGuide(MeFragement.this.getActivity(),url);
                        }
                    } else {  // 家长
                        return;
                    }
                } else if (position == 2) {                                    // 园长邀请家长加入
                    if (role == AppConstants.DIRECTORROLE) {
                        if (accountInfo.getKid()!=0) {
                            String url= AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_PARENT, 0);
                            UtilsLog.i(TAG, url + "");
                            AppUtils.startWebUrlForGuide(MeFragement.this.getActivity(), url);
                        } else {
                            // 需添加引导页
                            showWaringDialog(0);
                        }
                    }
                } else {                                                       // 其他（多余代码暂留）
                    if (uplist.get(position).equals("二维码名片")) {
                        intent = new Intent(getActivity(), MeCardActivity.class);
                        intent.putExtra(AppConstants.STATE, AppConstants.MEINFO);
                        startActivity(intent);
                    } else if (uplist.get(position).equals("微网站")) {
                        if (accountInfo.getMicroweb()!=null && !accountInfo.getMicroweb().equals("")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConstants.INTENT_URL, accountInfo.getMicroweb());
                            bundle.putString(AppConstants.INTENT_NAME, "微网站");
                            startAnimActivity(CommonBrowser.class, bundle);
                        }
                    } else {
                        intent = new Intent(getActivity(), IdSafeActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        /* 中部点击 */
        middlelv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                String defaulturl = "http://t.sgs.pay.zgyey.com/pay/index?kid={kid}&client={client}&appver={appver}&uid={uid}&hxuid={hxuid}&role={role}&key={key}";
                String payurl = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getString("payurl", defaulturl);
//              String payurl = "http://t.sgs.pay.zgyey.com/pay/FeeList/?&kid={kid}&client={client}&appver={appver}&uid={uid}";
//              bundle.putString(AppConstants.INTENT_URL, "http://t.sgs.pay.zgyey.com/pay/FeeList/?&kid=12511&client=1&appver=1.083&uid=295767");
                String url111 = AppUtils.replaceUnifiedUrl(payurl);
                bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUnifiedUrl(payurl));
                bundle.putString(AppConstants.INTENT_NAME, accountInfo.getPaytitle() == null ? "" : accountInfo.getPaytitle());
                startAnimActivity(CommonBrowser.class, bundle);
            }
        });

        /* 下半部点击 */
        downlv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                /*case 0:
                    intent = new Intent(getActivity(), MeOpinionActivity.class);
                    startActivityForResult(intent, 0);
                    break;*/
                case 0:                                                         // 账号安全
                    intent = new Intent(getActivity(), IdSafeActivity.class);
                    startActivity(intent);
                    break;
                case 1:                                                         // 关于时光树
                    intent = new Intent(getActivity(), MeAboutUsActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        });

        rl_menu.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (istop) {
                    istop = false;
                    isback = false;
                    rl_menu.startAnimation(pop_in);
                    if (role == 2) {
                        rlMenuParent.startAnimation(pop_in);
                        rlMenuParent.setVisibility(View.VISIBLE);
                    } else {
                        rl_menu.startAnimation(pop_in);
                        rl_menu.setVisibility(View.VISIBLE);
                    }
                    iv_right.setImageDrawable(getResources().getDrawable(R.drawable.icon_plus));
                } else {
                    istop = true;
                    isback = true;
                    if (role == 2) {
                        rlMenuParent.startAnimation(pop_out);
                        rlMenuParent.setVisibility(View.GONE);
                    } else {
                        rl_menu.startAnimation(pop_out);
                        rl_menu.setVisibility(View.GONE);
                    }
                    rl_menu.setEnabled(false);
                    iv_right.setImageDrawable(getResources().getDrawable(R.drawable.icon_plus));
                }
                return false;
            }
        });

        rlMenuParent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (istop) {
                    istop = false;
                    isback = false;
                    rl_menu.startAnimation(pop_in);
                    if (role == 2) {
                        rlMenuParent.startAnimation(pop_in);
                        rlMenuParent.setVisibility(View.VISIBLE);
                    } else {
                        rl_menu.startAnimation(pop_in);
                        rl_menu.setVisibility(View.VISIBLE);
                    }
                    iv_right.setImageDrawable(getResources().getDrawable(R.drawable.icon_plus));
                } else {
                    istop = true;
                    isback = true;
                    if (role == 2) {
                        rlMenuParent.startAnimation(pop_out);
                        rlMenuParent.setVisibility(View.GONE);
                    } else {
                        rl_menu.startAnimation(pop_out);
                        rl_menu.setVisibility(View.GONE);
                    }
                    rlMenuParent.setEnabled(false);
                    iv_right.setImageDrawable(getResources().getDrawable(R.drawable.icon_plus));
                }
                return false;
            }
        });
        role = AppServer.getInstance().getAccountInfo().getRole();
    }

    public void showWaringDialog(int role) {
        PhotoDialog dialog = new PhotoDialog(this.getActivity(), role);
        dialog.show();
    }

    @OnClick({R.id.right_tv, R.id.me_self_info, R.id.id_creategroup_iv, R.id.btn_me_signout, R.id.right_btn,
            R.id.menu_btn, R.id.id_sendmsg_iv, R.id.btn_top_barcode_iv, R.id.btn_top_barcode_teacheriv, R.id.id_sendspeak_iv,
            R.id.id_sendspeak_tv_teacheriv, R.id.id_writesc_iv, R.id.id_addfriend_iv, R.id.id_addfriend_tv_teacheriv})
    public void viewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.right_tv:
                AccountInfo info = AppServer.getInstance().getAccountInfo();
                if (info.getKid() == 0) {
                    if (info.getRole() == 0) {
                        showWaringDialog(0);
                    } else if (info.getRole() == 1){
                        showWaringDialog(1);
                    }
                    return;
                }
                if (info.getNoticeurl()!=null && !info.getNoticeurl().equals("")) {
                    Bundle noticebundle = new Bundle();
                    noticebundle.putString(AppConstants.INTENT_URL, info.getNoticeurl());
                    noticebundle.putString(AppConstants.INTENT_NAME,"发通知");
                    startAnimActivity(CommonBrowser.class, noticebundle);
                }
                break;
            case R.id.me_self_info:
                intent = new Intent(getActivity(), MeInfoActivity.class);
//              intent = new Intent(getActivity(), AccountInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_me_signout:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UtilsLog.i(TAG, "begin to loginout uid is : " + AppServer.getInstance().getAccountInfo().getUid());
                        AppServer.getInstance().loginout(AppServer.getInstance().getAccountInfo().getUid(),
                                AppServer.getInstance().getAccountInfo().getRelationship(), new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                UtilsLog.i(TAG, "loginout code is : " + code + ", message is : " + message);
                            }
                        });
                        AppContext.getInstance().logout();
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean(AppConstants.DIRECTOR_ISFIRST_CREAT, false);
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean("TEACHER_REDIGET", false);
//                      SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean("isFirstlook",true);
//                      SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean("isFirstlookTime",true);
                        getActivity().finish();
                        startAnimActivity(LoginActivity.class);
                    }
                });
                break;
            case R.id.id_sendmsg_iv:
                switchmenu(istop);
                intent = new Intent(getActivity(), SendNotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_top_barcode_iv:
            case R.id.btn_top_barcode_teacheriv:
                startAnimActivity(CaptureActivity.class);
                break;
            case R.id.right_btn:
                switchmenu(istop);
                break;
            case R.id.menu_btn:
                switchmenu(istop);
                break;
            case R.id.id_sendspeak_iv:
            case R.id.id_sendspeak_tv_teacheriv:
                switchmenu(istop);
                intent = new Intent(getActivity(), ServicePublishSpeakActivity.class);
                intent.putExtra("type", AppConstants.MAINSPEAK);
                startActivity(intent);
                break;
            case R.id.id_writesc_iv: // 记日程
                switchmenu(istop);
                intent = new Intent(getActivity(), ServiceScheduleWriteActivity.class);
                intent.putExtra("state", "homeschedule");
                intent.putExtra("type", AppConfig.SWITCH_TYPE_ME);
                startActivity(intent);
                break;
            case R.id.id_addfriend_iv:
            case R.id.id_addfriend_tv_teacheriv:
                switchmenu(istop);
                intent=new Intent(getActivity(), ContactsAddFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.id_creategroup_iv:
                switchmenu(istop);
                intent=new Intent(getActivity(), ServiceCreatKinderSelectActivity.class);
                startActivity(intent);
                break;
            /*case R.id.id_shareapp_tv:
                switchmenu(istop);
                intent=new Intent(getActivity(), MeShareActivity.class);
                startActivity(intent);
                break;*/
            default:
                break;
        }
    }

    public void switchmenu(Boolean isstop){
        if (isstop) {
            iv_right.setImageResource(R.drawable.icon_plus);
            role = AppServer.getInstance().getAccountInfo().getRole();
            if (role == 2) {
                rlMenuParent.startAnimation(pop_in);
                rlMenuParent.setVisibility(View.VISIBLE);
            } else {
                rl_menu.startAnimation(pop_in);
                rl_menu.setVisibility(View.VISIBLE);
            }
            istop = false;
            isback = false;
            rl_menu.setEnabled(true);
            rlMenuParent.setEnabled(true);
            iv_right.setImageDrawable(getResources().getDrawable(R.drawable.icon_plus));
        } else {
            iv_right.setImageResource(R.drawable.icon_plus);
            role = AppServer.getInstance().getAccountInfo().getRole();
            if (role == 2) {
                rlMenuParent.startAnimation(pop_out);
                rlMenuParent.setVisibility(View.GONE);
            } else {
                rl_menu.setVisibility(View.GONE);
                rl_menu.startAnimation(pop_out);
            }
            istop = true;
            isback = true;
            iv_right.setImageDrawable(getResources().getDrawable(R.drawable.icon_plus));
        }
    }

    public static boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
             if (!istop){
                 iv_right.setImageResource(R.drawable.icon_plus);
                 role = AppServer.getInstance().getAccountInfo().getRole();
                 if (role == 2) {
                     rlMenuParent.startAnimation(pop_out);
                     rlMenuParent.setVisibility(View.GONE);
                 } else {
                     rl_menu.setVisibility(View.GONE);
                     rl_menu.startAnimation(pop_out);
                 }
                 istop = true;
                 isback = true;
             }
         }
         return true;
    }

    public void hidePullMenu() {
        rl_menu.setVisibility(View.GONE);
        rlMenuParent.setVisibility(View.GONE);
        istop = true;
        isback = true;
    }

}
