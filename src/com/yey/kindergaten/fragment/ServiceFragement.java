package com.yey.kindergaten.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CaptureActivity;
import com.yey.kindergaten.activity.ClassPhotoMainActivity;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.ContactsAddFriendActivity;
import com.yey.kindergaten.activity.GrowthDiaryActivity;
import com.yey.kindergaten.activity.LeaveSchoolActivity;
import com.yey.kindergaten.activity.SendNotificationActivity;
import com.yey.kindergaten.activity.ServiceCreatKinderSelectActivity;
import com.yey.kindergaten.activity.ServiceFriendsterActivity;
import com.yey.kindergaten.activity.ServiceLifePhotoMainActivity;
import com.yey.kindergaten.activity.ServicePointExchangeActivity;
import com.yey.kindergaten.activity.ServicePublishSpeakActivity;
import com.yey.kindergaten.activity.ServiceScheduleActivity;
import com.yey.kindergaten.activity.ServiceScheduleWriteActivity;
import com.yey.kindergaten.activity.WizardActivity;
import com.yey.kindergaten.adapter.ServiceMainActivityAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.task.TaskType;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.widget.PhotoDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 服务主界面
 * @author chaowen
 *
 */
public class ServiceFragement extends FragmentBase implements OnItemClickListener{
    @ViewInject(R.id.right_tv)TextView right_tv;
    @ViewInject(R.id.header_title)TextView tv_headerTitle;
    @ViewInject(R.id.right_btn)
    static ImageView iv_right;
    @ViewInject(R.id.menu_btn)
    static RelativeLayout rl_menu;
    @ViewInject(R.id.menu_btn_parent)
    static RelativeLayout rlMenuParent;
    @ViewInject(R.id.lv_activity_service_main)ListView lv;

    private RelativeLayout netCheckRL;
    private TextView netCheckTv;
    public static Boolean istop = true;
    public static boolean isback = true;
    private Animation pop_in;
    private static Animation pop_out;
    private ServiceMainActivityAdapter adapter;
    Services item = new Services();
    private NetworkInfo info;
    private ConnectivityManager connectivityManager;
    private List<Services> gropone = new ArrayList<Services>();
    private List<Services> groptwo = new ArrayList<Services>();
    private List<Services> gropthr = new ArrayList<Services>();
    private List<Services> gropfor = new ArrayList<Services>();
    private List<Services> gropfiv = new ArrayList<Services>();
    private List<Services> gropsix = new ArrayList<Services>();
    private List<Services> list = new ArrayList<Services>();
    private static int role = 0; // 1表示老师
    private static final int REQUEST_CODE = 1;

    private Handler nethandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == AppConstants.NET_SENDMESSAG_WHAT_CODE_NONET) {
//              netCheckRL.setVisibility(View.VISIBLE);
//              netCheckTv.setText("网络不可用，请检查您的网络设置。");
            } else {
//              netCheckRL.setVisibility(View.GONE);
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_service_main, container, false);
        ViewUtils.inject(this, view);
        netCheckRL = (RelativeLayout) view.findViewById(R.id.network_listener_ll);
        netCheckTv = (TextView) view.findViewById(R.id.network_listener_tv);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this); // 7- 27 龙衡东修改 BaseActivity已注册
        }
        prepareView();
//      checkUpdateSysConfig();
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.REFRESH_SERVICES) {
            // 刷新界面
            updateService();
        }
    }

    // 引导页返回后，弹出框
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AccountInfo accountinfo = AppServer.getInstance().getAccountInfo();
        switch (resultCode) {
            case 1:
                if (accountinfo.getKid() == 0) {
                    showWaringDialog(0);
                }
                break;
            case 2:
                if (accountinfo.getKid() == 0) {
                    showWaringDialog(1);
                }
                break;
            case 3:
                if (accountinfo.getKid() == 0) {
                    showWaringDialog(2);
                }
                break;
            default:
                break;
        }
    }

    private void checkUpdateSysConfig() {
        AppServer.getInstance().getSysConfig(AppServer.getInstance().getAccountInfo().getUid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    if (obj!=null) {
                        int serviceupdate = (Integer) obj;
                        if (serviceupdate == 1) {
                            getServiceApi();
                        }
                    }
                } else {
                    AccountInfo info = AppServer.getInstance().getAccountInfo();
                    List<AccountInfo> list = DbHelper.getDataList(AccountInfo.class);
                    if (list.size() > 0) {
                        AccountInfo infoFromDb = list.get(0);
                        info.setContactgw(infoFromDb.getContactgw());
                        info.setMsggw(infoFromDb.getMsggw());
                        info.setTaskgw(infoFromDb.getTaskgw());
                        info.setSysgw(infoFromDb.getSysgw());
                        info.setGroupgw(infoFromDb.getGroupgw());
                        info.setSchedulegw(infoFromDb.getSchedulegw());
                        info.setNotifygw(infoFromDb.getNotifygw());
                        info.setClassnotifyurl(infoFromDb.getClassnotifyurl());
                        info.setClassphotourl(infoFromDb.getClassphotourl());
                        info.setClassscheduleurl(infoFromDb.getClassscheduleurl());
                        info.setMasterletterurl(infoFromDb.getMasterletterurl());
                        info.setNoticeurl(infoFromDb.getNoticeurl());
                    }
                }
            }
        });
    }

    private void prepareView(){
        // 监听网络广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.getActivity().registerReceiver(mReceiver, mFilter);
        // 初始化view
        tv_headerTitle.setText(R.string.main_buttom_tab_kindergarten);
        iv_right.setVisibility(View.GONE);
        right_tv.setText("发通知");
//      if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            right_tv.setVisibility(View.GONE);  /* 有了统一入口，其他界面的"发通知"全部隐藏 */
//      } else {
//          right_tv.setVisibility(View.VISIBLE);
//      }
        pop_in = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_in);
        pop_out = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_out);
//        list = DbHelper.getService();
        try {
            list = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Services.class).orderBy("orderno"));
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (list == null || list.isEmpty()) {
            updateService();
        } else {
            adapter = new ServiceMainActivityAdapter(AppContext.getInstance());
            for (int i = 0; i < list.size(); i++) {
                Services service = list.get(i);
//              if (service.getUrl()!=null && !service.getUrl().equals("")) {
                switch (service.getGroup()) {
                    case 0:
                        gropone.add(service);
                        break;
                    case 1:
                        groptwo.add(service);
                        break;
                    case 2:
                        gropthr.add(service);
                        break;
                    case 3:
                        gropfor.add(service);
                        break;
                    case 4:
                        gropfiv.add(service);
                        break;
                    case 5:
                        gropsix.add(service);
                        break;
                }
//              }
            }

            if (gropone!=null && gropone.size()!=0) {
                adapter.addSeparatorItem(new Services());
                adapter.addData(gropone);
                adapter.addSeparatorItem(new Services());
            }
            if (groptwo!=null && groptwo.size()!=0) {
                adapter.addData(groptwo);
                adapter.addSeparatorItem(new Services());
            }
            if (gropthr!=null && gropthr.size()!=0) {
                adapter.addData(gropthr);
                adapter.addSeparatorItem(new Services());
            }
            if (gropfor!=null && gropfor.size()!=0) {
                adapter.addData(gropfor);
                adapter.addSeparatorItem(new Services());
            }
            if (gropfiv!=null && gropfiv.size()!=0) {
                adapter.addData(gropfiv);
                adapter.addSeparatorItem(new Services());
            }
            if (gropsix!=null && gropsix.size()!=0) {
                adapter.addData(gropsix);
            }
            lv.setAdapter(adapter);
        }
        lv.setOnItemClickListener(this);

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

    /**
     * 更新服务
     */
    List<Services> slist = null;
    private void updateService() { // 数据库没有，调接口取； 数据库有，直接设置适配
//        slist = DbHelper.getService();
        try {
            list = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Services.class).orderBy("orderno"));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (slist == null || slist.size() == 0) {
            getServiceApi();
        } else {
            adapter = new ServiceMainActivityAdapter(AppContext.getInstance(), slist);
            lv.setAdapter(adapter);
            // getServiceApi();
        }
    }

    /**
     * 调接口获取服务列表
     */
    private void getServiceApi() {
//      showLoadingDialog("正在加载服务");
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        AppServer.getInstance().getServiceMenu(info.getUid(), info.getRole(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                cancelLoadingDialog();
                List<Services> serverslist = new ArrayList<Services>(); // 接口返回的服务列表
                if (obj instanceof java.util.List) {
                    serverslist = (List<Services>) obj;
                }
                if (code == AppServer.REQUEST_SUCCESS) {
                    if (slist!=null && slist.size() > 0) {  // 没数据，直接用接口数据替换； 有数据，
                        // 更新数据库
//                        List<Services> servicesList = DbHelper.getService(); // 数据库的服务列表
                        List<Services> servicesList = new ArrayList<Services>(); // 数据库的服务列表
                        try {
                            servicesList = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Services.class).orderBy("orderno"));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        List<Services> sameList = new ArrayList<Services>();
//                        if (servicesList!=null && servicesList.size()!=0 && (servicesList.size() < serverslist.size())){
//                            if (serverslist.size() > servicesList.size()) { // 接口数据长度比数据库长，替换为接口数据
//                                serverslist.removeAll(servicesList);
//                                servicesList.addAll(serverslist);
//                            } else if (servicesList.size() > serverslist.size()) { // 否则
//                                servicesList.removeAll(serverslist); // 取剩下的list
//                                sameList.addAll(serverslist); // 保存在sameList中。
//                                servicesList = DbHelper.getService();
//                                servicesList.removeAll(sameList);
//                            }
//                            for (int i = 0; i < serverslist.size(); i++) {
//                               Services services = servicesList.get(i);
//                                Services newService = serverslist.get(i);
//                                newService.setIsfirstlook(services.getIsfirstlook());
//                                serverslist.set(i,newService);
//                            }
//                        }
                        if (serverslist!=null && serverslist.size() > 0) { //（** 不应取并集 **）
                            List<Integer> isLookeds = new ArrayList<Integer>(); // 已看过的服务
                            if (servicesList != null && servicesList.size() > 0) {
                                for (Services service : servicesList) {
                                    if (service.getIsfirstlook() == 1) { // 1:已看; 0:未看
                                        isLookeds.add(service.getType());
                                    }
                                }
                                for (int i = 0; i < serverslist.size(); i++) {
                                    Services newService = serverslist.get(i); // 远程
                                    if (isLookeds.contains(newService.getType())) {
                                        newService.setIsfirstlook(1);
                                    } else {
                                        newService.setIsfirstlook(0);
                                    }
                                    serverslist.set(i, newService);
                                }
                            }
                        }
                        updateUI(serverslist);
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Services.class);
                            DbHelper.getDB(appcontext).saveAll(serverslist);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 保存数据库
                        updateUI(serverslist);
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Services.class);
                            DbHelper.getDB(AppContext.getInstance()).saveAll(serverslist);
                            list = serverslist;
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    adapter = new ServiceMainActivityAdapter(AppContext.getInstance(), serverslist);
                    lv.setAdapter(adapter);
                }
            }
        });
    }

    /**
     * @param serverslist
     */
    private void updateUI(List<Services> serverslist) {
        gropone.clear();
        groptwo.clear();
        gropthr.clear();
        gropfor.clear();
        gropfiv.clear();
        gropsix.clear();
        if (serverslist == null) {
            return;
        }
        // 保存数据库
        adapter = new ServiceMainActivityAdapter(AppContext.getInstance());
        for (int i = 0; i < serverslist.size(); i++) {
            Services service = serverslist.get(i);
//          if(service.getUrl()!=null && !service.getUrl().equals("")) {
            switch (service.getGroup()) {
                case 0:
                    gropone.add(service);
                    break;
                case 1:
                    groptwo.add(service);
                    break;
                case 2:
                    gropthr.add(service);
                    break;
                case 3:
                    gropfor.add(service);
                    break;
                case 4:
                    gropfiv.add(service);
                    break;
                case 5:
                    gropsix.add(service);
                    break;
            }
//          }
        }
        if (gropone!=null && gropone.size()!=0) {
            adapter.addSeparatorItem(new Services());
            adapter.addData(gropone);
            adapter.addSeparatorItem(new Services());
        }
        if (groptwo!=null && groptwo.size()!=0) {
            adapter.addData(groptwo);
            adapter.addSeparatorItem(new Services());
        }
        if (gropthr!=null && gropthr.size()!=0) {
            adapter.addData(gropthr);
            adapter.addSeparatorItem(new Services());
        }
        if (gropfor!=null && gropfor.size()!=0) {
            adapter.addData(gropfor);
            adapter.addSeparatorItem(new Services());
        }
        if (gropfiv!=null && gropfiv.size()!=0) {
            adapter.addData(gropfiv);
            adapter.addSeparatorItem(new Services());
        }
        if (gropsix!=null && gropsix.size()!=0) {
            adapter.addData(gropsix);
        }
        lv.setAdapter(adapter);
    }

    @OnClick({R.id.right_tv, R.id.id_creategroup_iv, R.id.btn_me_signout, R.id.right_btn, R.id.menu_btn,
            R.id.id_sendmsg_iv, R.id.btn_top_barcodeiv, R.id.btn_top_barcode_teacheriv, R.id.id_sendspeak_iv,
            R.id.id_sendspeak_tv_teacheriv, R.id.id_writesc_iv, R.id.id_addfriend_iv, R.id.id_addfriend_tv_teacheriv})
    public void viewClick(View v) {
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
                    noticebundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl(info.getNoticeurl()));
                    noticebundle.putString(AppConstants.INTENT_NAME, "发通知");
                    startAnimActivity(CommonBrowser.class, noticebundle);
                }
                break;
            case R.id.id_creategroup_iv:
                switchmenu(istop);
                intent = new Intent(getActivity(), ServiceCreatKinderSelectActivity.class);
                startActivity(intent);
                break;
            case R.id.right_btn:
                switchmenu(istop);
                break;
            case R.id.menu_btn:
                switchmenu(istop);
                break;
            case R.id.id_sendmsg_iv:
                switchmenu(istop);
                intent = new Intent(getActivity(), SendNotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_top_barcode_iv:
            case R.id.btn_top_barcode_teacheriv:
                switchmenu(istop);
                startAnimActivity(CaptureActivity.class);
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
                intent.putExtra("type", AppConfig.SWITCH_TYPE_SERVICE);
                startActivity(intent);
                break;
            case R.id.id_addfriend_iv:
                switchmenu(istop);
                intent = new Intent(getActivity(), ContactsAddFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.id_addfriend_tv_teacheriv:
                switchmenu(istop);
                intent = new Intent(getActivity(), ContactsAddFriendActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 打开服务首次引导界面
     * @param showDraw
     * @param openState 打开方式--1表示webview模式，2表示其他非webview方式
     */
    public void openServiceTips(int showDraw, Services services, int openState){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        Intent intent = new Intent(this.getActivity(), WizardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", "fromService");
        bundle.putInt("showDraw", showDraw);
        bundle.putSerializable("service", services);
        bundle.putInt("openState", openState);
        // 判断指导页面结束返回后，是否需要提示弹出框
        if (role == 0 && info.getKid() == 0) {
            if (services.getType()!= 2) {
                bundle.putInt("fromServicerole", role);
            }
        } else if (role == 1 && info.getKid() == 0) {
            if (services.getType() == 7 || services.getType() == 9 || services.getType() == 10 || services.getType() == 16 ||
                    services.getType() == 12 || services.getType() == 13 || services.getType() == 14 || services.getType() == 18) {
                bundle.putInt("fromServicerole", role);
            }
        } else if (role == 2 && info.getKid() == 0) {
            if (services.getType() == 7 || services.getType() == 12 || services.getType() == 13 || services.getType() == 14 || services.getType() == 18) {
                bundle.putInt("fromServicerole", role);
            }
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE);
        // startActivity(intent);
    }

    public void updateLookState(Services service, int lookState){
        try {
            service.setIsfirstlook(lookState);
            DbHelper.getDB(AppContext.getInstance()).update(service, WhereBuilder.b("type", "=", service.getType()), new String[]{"isfirstlook"});
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void showWaringDialog(int role){
        PhotoDialog dialog = new PhotoDialog(getActivity(), role);
        dialog.show();
    }

    private void showRemindParentDialog(){
        if (AppContext.getInstance().getContacts().getClasses()!=null && AppContext.getInstance().getContacts().getClasses().size()!=0) {
            showDialog("提示","去邀请","此项服务需要邀请家长才能使用",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AppUtils.startWebUrlForGuide(ServiceFragement.this.getActivity(), AppUtils.replaceUrlByUrl(AppConstants.TEACHER_INVITE_PARENT, AppContext.getInstance().getContacts().getClasses().get(0).getCid()));
                }
            });
        } else {
            showWaringDialog(1);
        }
    }

    @Override
    public void onResume() {
        if (adapter!=null && adapter.getCount() == 0) {
            updateService();
        }
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        Intent intent;
        Services s = adapter.getData().get(position);
        Bundle bundle = new Bundle();
        bundle.putString("type", "fromService");
        List<Classe>classes = AppContext.getInstance().getContacts().getClasses();

        int childrenCount = 0; // 所有班级小朋友总数, 对于家长身份来说，目前没有classes。
        if (classes!=null && classes.size()!=0) {
            for (int i = 0; i < classes.size(); i++) {
                childrenCount = childrenCount + classes.get(i).getChildrencount();
            }
        }

        /*1任务 2日程 3报表 4积分兑换 5健康中心 6成长档案 7家园联系 8园长信箱 9幼儿园文档 10微网站
          11班级主页 12生活剪影 13手工作品 14班级相册 15成长日记 16我的文档 18班级动态 19离园播报
          20一日流程 21借阅服务 22微课堂 23成长评估*/

        // 根据Type列表，先判断是否加入幼儿园，再判断是否加入班级，再判断是否已查看，最后根据身份进入指导页面。
        if (s.getType() == 2) { // 修改日程
            if (s.getIsfirstlook() == 1) {
                intent = new Intent(getActivity(), ServiceScheduleActivity.class);
                startActivity(intent);
            } else {
                updateLookState(s, 1);
                openServiceTips(R.drawable.director_service_guide_schedule,s,2);
            }
        } else if (s.getType() == 3) { // 报表
            if (info.getKid() == 0){
                if (s.getUrl()!=null && !s.getUrl().equals("")) {
                    openServiceTips(R.drawable.director_service_guide_report, s, 1);
                } else {
                    if(role == 0 || role == 1 || role == 2) {
                        showWaringDialog(role);
                    }
                }
                return;
            }
            if (s.getIsfirstlook() == 1) {
                bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
                bundle.putString(AppConstants.INTENT_NAME, s.getName());
                bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
                startAnimActivity(CommonBrowser.class, bundle);
            } else {
                if (s.getUrl()!=null && !s.getUrl().equals("")) {
                    updateLookState(s, 1);
                    openServiceTips(R.drawable.director_service_guide_report, s, 1);
                } else {
                    return;
                }
            }
        } else if (s.getType() == 4) { // 积分兑换
            intent = new Intent(getActivity(), ServicePointExchangeActivity.class);
            startActivity(intent);
        } else if (s.getType() == 12) { // 生活剪影12
            if (info.getKid() == 0) {
                openServiceTips(R.drawable.tercher_service_guide_lifephoto, s, 2);
                return;
            }
            if (!(childrenCount > 0) && role!=2) {
                showRemindParentDialog();
                return;
            }
            if (s.getIsfirstlook() == 1) {
                intent = new Intent(getActivity(), ServiceLifePhotoMainActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
            } else {
                updateLookState(s, 1);
                openServiceTips(R.drawable.tercher_service_guide_lifephoto, s, 2);
            }
        } else if ( s.getType() == 13) {
            if (info.getKid() == 0){
                openServiceTips(R.drawable.tercher_service_guide_workphoto, s, 2);
                return;
            }
            if (!(childrenCount > 0) && role!=2) {
                showRemindParentDialog();
                return;
            }
            if (s.getIsfirstlook() == 1) {
                intent = new Intent(getActivity(), ServiceLifePhotoMainActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
            } else {
                updateLookState(s, 1);
                openServiceTips(R.drawable.tercher_service_guide_workphoto,s,2);
            }
        } else if (s.getType() == 11){ // 班级主页
            if (info.getKid() == 0 ){
                openServiceTips(R.drawable.parent_service_guide_classmain, s, 1);
                return;
            }
            if (s.getIsfirstlook() == 1) {
                bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUnifiedUrl( s.getUrl()));
                bundle.putString(AppConstants.INTENT_NAME, s.getName());
                bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
                startAnimActivity(CommonBrowser.class, bundle);
            } else {
                updateLookState(s, 1);
                openServiceTips(R.drawable.parent_service_guide_classmain, s, 1);
            }
        } else if (s.getType() == 15) { // 成长日记
            if (info.getKid() == 0){
                openServiceTips(R.drawable.parent_service_guide_diary, s, 2);
                return;
            }
            if (s.getIsfirstlook() == 1) {
                intent = new Intent(getActivity(), GrowthDiaryActivity.class);
//              intent = new Intent(getActivity(), ServiceFriendsterActivity.class);
                startActivity(intent);
            } else {
                updateLookState(s, 1);
                openServiceTips(R.drawable.parent_service_guide_diary, s, 2);
            }
        } else if (s.getType() == 14) { // 班级相册
            if (info.getKid() == 0){
                openServiceTips(R.drawable.tercher_service_guide_classphoto, s, 2);
                return;
            }
            // 有幼儿园没班级
            if (role == 1 && classes!=null && classes.size() == 0 ) {
                showWaringDialog(1);
                return;
            }
//          // 班级没有小朋友，提示邀请小朋友
//          if (!(childrenCount > 0) && role!=2) {
//              showRemindParentDialog();
//              return;
//          }
            if (s.getIsfirstlook() == 1) {
                intent = new Intent(getActivity(), ClassPhotoMainActivity.class);
                intent.putExtra("typefrom", AppConstants.FROMFRIENDSTER);
                intent.putExtra("classphototype",AppConstants.HOMEACTIVITY);
                startActivity(intent);
            } else {
                updateLookState(s, 1);
                openServiceTips(R.drawable.tercher_service_guide_classphoto, s, 2);
            }
        } else if (s.getType() == 18) { // 班级圈
            if (info.getKid() == 0) {
                openServiceTips(R.drawable.director_service_guide_friendster, s, 2);
                return;
            }
            // 有幼儿园没班级
            if (role == 1 && classes!=null && classes.size() == 0) {
                showWaringDialog(1);
                return;
            }
            if (s.getIsfirstlook() == 1) {
                intent = new Intent(getActivity(), ServiceFriendsterActivity.class);
                startActivity(intent);
            } else {
                updateLookState(s, 1);
                openServiceTips(R.drawable.director_service_guide_friendster, s, 2);
            }
//          intent = new Intent(getActivity(), ServiceFriendsterActivity.class);
//          startActivity(intent);
        } else if (s.getType() == 19) {
            intent = new Intent(getActivity(), LeaveSchoolActivity.class);
            startActivity(intent);
        }
        else {
            if (s.getUrl()!=null && !s.getUrl().equals("")) {
                if (info.getKid() == 0){
                    switch (s.getType()) {
                        case 5:
                            openServiceTips(R.drawable.parent_service_guide_health, s, 1);
                            break;
                        case 7: // 家园联系
                            if (role == 1) {
                                openServiceTips(R.drawable.tercher_service_guide_homebook, s, 1);
                            } else if (role == 2) {
                                openServiceTips(R.drawable.parent_service_guide_homebook, s, 1);
                            }
                            break;
                        case 8:
                            openServiceTips(R.drawable.director_service_guide_diretor_letter, s, 1);
                            break;
                        case 9: // 幼儿园文档
                            if (role == AppConstants.TEACHERROLE) {
                                openServiceTips(R.drawable.tercher_service_guide_kinderdocment, s, 1);
                            } else if (role == AppConstants.DIRECTORROLE) {
                                openServiceTips(R.drawable.director_service_guide_kinderdocment, s, 1);
                            }
                            break;
                        case 10: // 微网站
                            if (role == AppConstants.TEACHERROLE) {
                                openServiceTips(R.drawable.parent_service_guide_microweb, s, 1);
                            } else if (role == AppConstants.PARENTROLE) {
                                openServiceTips(R.drawable.tercher_service_guide_microweb, s, 1);
                            } else if (role == AppConstants.DIRECTORROLE) {
                                openServiceTips(R.drawable.director_service_guide_microweb, s, 1);
                            }
                            break;
                        case 11: // 班级主页
                            openServiceTips(R.drawable.parent_service_guide_classmain, s, 1);
                            break;
                        case 16: // 我的文档
                            if (role == AppConstants.TEACHERROLE) {
                                openServiceTips(R.drawable.tercher_service_guide_mydocment, s, 1);
                            } else if (role == AppConstants.DIRECTORROLE){
                                openServiceTips(R.drawable.director_service_guide_mydocment, s, 1);
                            }
                            break;
                        case 20:
                            bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
                            bundle.putString(AppConstants.INTENT_NAME, s.getName());
                            bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
                            bundle.putInt(AppConstants.INTENT_FULL_SCREEN, s.getType()); // 是否全屏显示，针对一日流程是否全屏显示效果
                            startAnimActivity(CommonBrowser.class, bundle);
//                          openServiceTips(R.drawable.parent_service_guide_,s.getName(),"");
//                            openServiceTips(R.drawable.teacher_service_guide_leaveschool, s, 1);
                            break;
                        case 99: // 阅读中心
                            bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
                            bundle.putString(AppConstants.INTENT_NAME, s.getName());
                            bundle.putString(AppConstants.INTENT_SHOWTITLE,"1");
                            startAnimActivity(CommonBrowser.class, bundle);
//                          openServiceTips(R.drawable.parent_service_guide_,s.getName(),"");
//                          openServiceTips(R.drawable.director_guide_g, s, 1);
                            break;
                        case 22: // 微课堂
                            bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
                            bundle.putString(AppConstants.INTENT_NAME, s.getName());
                            bundle.putString(AppConstants.INTENT_SHOWTITLE,"1");
                            startAnimActivity(CommonBrowser.class, bundle);
                            break;
                        default:
                            if (role == 0 || role == 1 || role == 2) {
                                showWaringDialog(role);
                            }
                    }
                    return;
                }

                if ((!(childrenCount > 0)) && s.getType() == 7 && role!=2) {
                    showRemindParentDialog();
                    return;
                }

                if (s.getIsfirstlook() == 1) {
                    // bundle.putString(AppConstants.INTENT_URL, "http://192.168.0.14:805");
                    bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
                    bundle.putString(AppConstants.INTENT_NAME, s.getName());
                    bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
                    if (s.getType() == 20) {
                        bundle.putInt(AppConstants.INTENT_FULL_SCREEN, s.getType()); // 是否全屏显示，针对一日流程是否全屏显示效果
                    }
                    startAnimActivity(CommonBrowser.class, bundle);
                } else {
                    switch (s.getType()) {
                        case 5: // 健康中心
                            updateLookState(s, 1);
                            openServiceTips(R.drawable.parent_service_guide_health, s, 1);
                            break;
                        case 7: // 家园联系
                            updateLookState(s, 1);
                            if (role == 1) {
                                openServiceTips(R.drawable.tercher_service_guide_homebook, s, 1);
                            } else if (role == 2) {
                                openServiceTips(R.drawable.parent_service_guide_homebook, s, 1);
                            }
                            break;
                        case 8:
                            updateLookState(s, 1);
                            openServiceTips(R.drawable.director_service_guide_diretor_letter, s, 1);
                            break;
                        case 9: // 幼儿园文档
                            updateLookState(s, 1);
                            if (role == AppConstants.TEACHERROLE) {
                                openServiceTips(R.drawable.tercher_service_guide_kinderdocment, s, 1);
                            } else if (role == AppConstants.DIRECTORROLE) {
                                openServiceTips(R.drawable.director_service_guide_kinderdocment, s, 1);
                            }
                            break;
                        case 10: // 微网站
                            updateLookState(s,1);
                            if (role == AppConstants.TEACHERROLE) {
                                openServiceTips(R.drawable.parent_service_guide_microweb, s, 1);
                            } else if (role == AppConstants.PARENTROLE) {
                                openServiceTips(R.drawable.tercher_service_guide_microweb, s, 1);
                            } else if(role == AppConstants.DIRECTORROLE) {
                                openServiceTips(R.drawable.director_service_guide_microweb, s, 1);
                            }
                            break;
                        case 11: // 班级主页
                            updateLookState(s, 1);
                            openServiceTips(R.drawable.parent_service_guide_classmain, s, 1);
                            break;
                        case 16: // 我的文档
                            updateLookState(s,1);
                            if (role == AppConstants.TEACHERROLE) {
                                openServiceTips(R.drawable.tercher_service_guide_mydocment, s, 1);
                            } else if (role == AppConstants.DIRECTORROLE) {
                                openServiceTips(R.drawable.director_service_guide_mydocment, s, 1);
                            }
                            break;
                        case 20:
                            updateLookState(s,1);
                            openServiceTips(R.drawable.teacher_service_guide_leaveschool, s, 1);

//                            bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
//                            bundle.putString(AppConstants.INTENT_NAME, s.getName());
//                            bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
//                            bundle.putInt(AppConstants.INTENT_FULL_SCREEN, s.getType()); // 是否全屏显示，针对一日流程是否全屏显示效果
//                            startAnimActivity(CommonBrowser.class, bundle);

//                          openServiceTips(R.drawable.parent_service_guide_,s.getName(),"");
                            break;
                        case 99: // 阅读中心
                            bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
                            bundle.putString(AppConstants.INTENT_NAME, s.getName());
                            bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
                            startAnimActivity(CommonBrowser.class, bundle);
//                          openServiceTips(R.drawable.parent_service_guide_,s.getName(), "");
                            break;
                        default:
                            bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl( s.getUrl()));
                            bundle.putString(AppConstants.INTENT_NAME, s.getName());
                            bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
                            startAnimActivity(CommonBrowser.class, bundle);
                            break;
                    }
                }
            } else {
                return;
            }
        }
    }

    public void refresh(Object... param) {
        int type = (Integer) param[0];
        switch (type) {
        case TaskType.TS_SERVICE_INIT:
            if (param[2] != null && (Integer) param[2] != 0) {
                // 有异常--显示加载出错 & 弹出错误消息
                ShowToast("好像出问题了哦");
            } else {
                List<Services> serverslist = (List<Services>) param[1];
                if (serverslist!=null && !serverslist.isEmpty()&& serverslist.size() > 0) {
                    updateUI(serverslist);
                }
            }
            break;
        }
    }

    public void switchmenu(Boolean isstop) {
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
        System.out.println("sdsdsdsdsdsdsd");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!istop) {
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
        role = AppServer.getInstance().getAccountInfo().getRole();
        if (role == 2) {
            if (rlMenuParent.getVisibility() == View.VISIBLE) {
                rlMenuParent.setVisibility(View.GONE);
            }
        } else {
            if (rl_menu.getVisibility() == View.VISIBLE) {
                rl_menu.setVisibility(View.GONE);
            }
        }
        istop = true;
        isback = true;
    }

    @Override
    public void onDestroy() {
        this.getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     *监听网络广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager) ServiceFragement.this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    nethandler.sendEmptyMessage(AppConstants.NET_SENDMESSAG_WHAT_CODE_HASNET); // 表示有网络
                } else {
                    nethandler.sendEmptyMessage(AppConstants.NET_SENDMESSAG_WHAT_CODE_NONET); // 表示没网络
                }
            }
        }
    };

}
