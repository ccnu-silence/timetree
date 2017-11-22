package com.yey.kindergaten.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.List;

public class ContactsPuacDatacardActivity extends BaseActivity implements OnClickListener{

    TextView titletextview;   // 通讯录
    ImageView leftbtn;
    String state = "";
    TextView bookbtn;
    TextView nametv;
    TextView accounttv;
    TextView fieltv;
    TextView  descripttv;
    LinearLayout bookly;
    CircleImageView imageView;
    int publicid;
    Contacts contacts;
    PublicAccount puacAccount;
    AppContext appcontext = null;
    AccountInfo accountInfo;
    LinearLayout lookhistory;

    @ViewInject(R.id.common_network_disable)LinearLayout layout_networkdisable;
    @ViewInject(R.id.common_loading)LinearLayout layout_loading;
    @ViewInject(R.id.common_error)LinearLayout layout_error;
    @ViewInject(R.id.common_empty)LinearLayout layout_empty;
    @ViewInject(R.id.network_disable_button_relink)ToggleButton nettogButton;

    @ViewInject(R.id.show_guide_image_all)LinearLayout guide_ll;
    @ViewInject(R.id.show_guide_tv)TextView guide_tv;
    LinearLayout contact_puacddatacard_lingyu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_puacdatacard);
        ViewUtils.inject(this);
        appcontext = AppContext.getInstance();
        contacts = appcontext.getContacts();
        accountInfo = AppServer.getInstance().getAccountInfo();
        puacAccount = new PublicAccount();
        // PublicAccount account = DbHelper.getDB(appcontext).findFirst()
        // if () { }
        // isfirstlook =
        if (getIntent().getExtras()!=null) {
            state = getIntent().getExtras().getString("state");
            publicid = getIntent().getExtras().getInt("publicid");
            showLoadingDialog("正在加载");
            AppServer.getInstance().viewInfo(accountInfo.getUid() + "", 0 + "",publicid + "", 3, new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (code == 0) {
                        PublicAccount puacs;
                        puacs = (PublicAccount) obj;
                        puacAccount = puacs;
                        initView();
                    } else if (code == AppServer.REQUEST_NETWORK_ERROR) {
                        layout_networkdisable.setVisibility(View.VISIBLE);
                    } else if (code == AppServer.REQUEST_CLIENT_ERROR) {
                        layout_error.setVisibility(View.GONE);
                    } else {
                        layout_empty.setVisibility(View.VISIBLE);
                    }
                    cancelLoadingDialog();
                }
            });
        }
        FindViewById();
        setOnClick();
    }

    public void FindViewById() {
        titletextview = (TextView) findViewById(R.id.header_title);
        leftbtn = (ImageView) findViewById(R.id.left_btn);
        leftbtn.setVisibility(View.VISIBLE);
        // titletextview.setText(R.string.contacts_datacard);
        bookbtn = (TextView) findViewById(R.id.contact_puacdatacard_bookbtn);
        nametv = (TextView) findViewById(R.id.contact_puacdatacard_nametv);
        accounttv = (TextView) findViewById(R.id.contact_puacdatacard_numbertv);
        fieltv = (TextView) findViewById(R.id.contact_puacdatacard_fieldtv);
        descripttv = (TextView) findViewById(R.id.contact_puacdatacard_describertv);
        imageView = (CircleImageView) findViewById(R.id.contact_puacdatacard_iv);
        lookhistory = (LinearLayout) findViewById(R.id.contact_puacdatacard_lookhistoryly);
        bookly = (LinearLayout) findViewById(R.id.contact_puacdatacard_bookly);
        contact_puacddatacard_lingyu = (LinearLayout)findViewById(R.id.contact_puacddatacard_fieldrly);
        contact_puacddatacard_lingyu.setVisibility(View.GONE);
    }

    public void  initView() {
        if (puacAccount == null) {
           return ;
        }
        titletextview.setText(puacAccount.getNickname());
        nametv.setText(puacAccount.getNickname());
        if (puacAccount.getPublicid() == 11 || puacAccount.getPublicid() == 12 || puacAccount.getPublicid() == 13 ||
            puacAccount.getPublicid() == 16 || puacAccount.getPublicid() == 17 || puacAccount.getPublicid() == 18) {
            guide_ll.setVisibility(View.VISIBLE);
        }
        guide_tv.setText(puacAccount.getNickname() + "能为您做什么?");
        /* if (puacAccount.getAccount()!=null && !puacAccount.getAccount().equals("")) {
            accounttv.setText("(账号:" + puacAccount.getAccount() + ")");
        } */
        accounttv.setVisibility(View.GONE);
        fieltv.setText(puacAccount.getDomain());
        descripttv.setText(puacAccount.getDesc());
        imageLoader.displayImage(puacAccount.getAvatar(), imageView, ImageLoadOptions.getContactsPuacPicOptions());
        if (state!=null && state.equals(AppConstants.PUACFRAGMENT_LOOKPUAC) || state.equals(AppConstants.TASK_LOOKPUAC) || state.equals(AppConstants.CONTACTADDPUACRESULT_LOOKPUAC)) {
            bookbtn.setText(R.string.contacts_puacdatacard_cancelbook);
            if (puacAccount.getFixed() == 1) {
                bookly.setVisibility(View.GONE);
            }
        } else if (state!=null && state.equals(AppConstants.CONTACTADDPUACRESULT_BOOKPUAC) || state.equals(AppConstants.TASK_BOOKPUAC) || state.equals(AppConstants.PUACFRAGMENT_BOOKPUAC)) {
            bookbtn.setText(R.string.contacts_puacdatacard_book);
            lookhistory.setVisibility(View.GONE);
        } else {
            bookly.setVisibility(View.GONE);
            lookhistory.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClick() {
        leftbtn.setOnClickListener(this);
        bookbtn.setOnClickListener(this);
        lookhistory.setOnClickListener(this);
        guide_ll.setOnClickListener(this);
        nettogButton.setOnClickListener(this);
    }

    public void showWhatPage(int fromId) {
        Intent intent = new Intent(this, WizardActivity.class);
        intent.putExtra("type", "fromDatacard");
        switch (fromId) {
            case AppConstants.TIMETREE_DIRECTOR_PUBLIC:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DIRECTOR_PUBLIC);
                break;
            case AppConstants.TIMETREE_TEACHER_PUBLIC:
                intent.putExtra("fromdId", AppConstants.TIMETREE_TEACHER_PUBLIC);
                break;
            case AppConstants.TIMETREE_PARENT_PUBLIC:
                intent.putExtra("fromdId", AppConstants.TIMETREE_PARENT_PUBLIC);
                break;
            case AppConstants.TIMETREE_DO_DIRECTOR:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DO_DIRECTOR);
                break;
            case AppConstants.TIMETREE_DO_TEACHER:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DO_TEACHER);
                break;
            case AppConstants.TIMETREE_DO_PARENT:
                intent.putExtra("fromdId", AppConstants.TIMETREE_DO_PARENT);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left_btn:
            ContactsPuacDatacardActivity.this.finish();
            break;
        case R.id.show_guide_image_all:
            showWhatPage(puacAccount.getPublicid());
            break;
        case R.id.contact_puacdatacard_bookbtn:
            if (state.equals(AppConstants.PUACFRAGMENT_LOOKPUAC) || state.equals(AppConstants.CONTACTADDPUACRESULT_LOOKPUAC) || state.equals(AppConstants.TASK_LOOKPUAC)) {
                AppServer.getInstance().bookPublicAccount(accountInfo.getUid(), puacAccount.getPublicid(), 0, new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        try {
                            if (code == 0) {
                                showToast("已经成功取消订阅");
                                Contacts newContacts = appcontext.getContacts();
                                List<PublicAccount> plist = newContacts.getPublics();
                                if (plist!=null) {
                                    for (int i = 0; i < plist.size(); i++) {
                                        if (plist.get(i).getPublicid() == puacAccount.getPublicid()) {
                                            PublicAccount puac = plist.get(i);
                                            puac.setSubscription(0);
                                            plist.set(i, puac);
                                            DbHelper.getDB(ContactsPuacDatacardActivity.this).update(puac, WhereBuilder.b("publicid", "=", puac.getPublicid()),"subscription");
                                        }
                                    }
                                }
                                newContacts.setPublics(plist);
                                appcontext.setContacts(newContacts);
                                if (state.equals(AppConstants.PUACFRAGMENT_LOOKPUAC)) {
                                    Intent intent = new Intent(ContactsPuacDatacardActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    ContactsPuacDatacardActivity.this.finish();
                                } else if (state.equals(AppConstants.TASK_LOOKPUAC)) {
                                    ContactsPuacDatacardActivity.this.finish();
                                    Intent intent = new Intent(ContactsPuacDatacardActivity.this, ServiceTaskBookPuacActivity.class);
                                    intent.putExtra("state", 0);
                                    intent.putExtra("publicid", puacAccount.getPublicid());
                                    startActivity(intent);
                                    ContactsPuacDatacardActivity.this.finish();
                                } else {
                                    ContactsPuacDatacardActivity.this.finish();
                                    Intent intent = new Intent(ContactsPuacDatacardActivity.this, ContactsAddPuacResultActivity.class);
                                    intent.putExtra("state", 0);
                                    intent.putExtra("publicid", puacAccount.getPublicid());
                                    startActivity(intent);
                                    ContactsPuacDatacardActivity.this.finish();
                                }
                            } else {
                              showToast("操作失败");
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                AppServer.getInstance().bookPublicAccount(accountInfo.getUid(), puacAccount.getPublicid(), 1, new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == 0) {
                            try {
                                showToast("订阅成功");
                                Contacts newContacts = appcontext.getContacts();
                                List<PublicAccount> plist = newContacts.getPublics();
                                if (plist!=null) {
                                    for (int i = 0; i < plist.size(); i++) {
                                        if (plist.get(i).getPublicid() == puacAccount.getPublicid()) {
                                            PublicAccount puac = plist.get(i);
                                            puac.setSubscription(1);
                                            plist.set(i, puac);
                                            DbHelper.getDB(ContactsPuacDatacardActivity.this).update(puac, WhereBuilder.b("publicid", "=", puac.getPublicid()),"subscription");
                                        }
                                    }
                                }
                                newContacts.setPublics(plist);
                                appcontext.setContacts(newContacts);
                                if (state.equals(AppConstants.PUACFRAGMENT_BOOKPUAC)) {
                                   ContactsPuacDatacardActivity.this.finish();
                                    Intent intent = new Intent(ContactsPuacDatacardActivity.this, MainActivity.class);
                                    intent.putExtra("state", 1);
                                    intent.putExtra("publicid", puacAccount.getPublicid());
                                    startActivity(intent);
                                } else if (state.equals(AppConstants.TASK_BOOKPUAC)){
                                    ContactsPuacDatacardActivity.this.finish();
                                    Intent intent=new Intent(ContactsPuacDatacardActivity.this, ServiceTaskBookPuacActivity.class);
                                    intent.putExtra("state", 1);
                                    intent.putExtra("publicid", puacAccount.getPublicid());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(ContactsPuacDatacardActivity.this, ContactsAddFriendActivity.class);
                                    intent.putExtra("state", 1);
                                    intent.putExtra("publicid", puacAccount.getPublicid());
                                    startActivity(intent);
                                    ContactsPuacDatacardActivity.this.finish();
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast( "操作失败");
                        }
                    }
                });
            }
            break;
        case R.id.contact_puacdatacard_lookhistoryly:
            // 查看历史消息
//          Session session = Session.getSession();
//          session.put(AppConstants.INTENT_KEY_FROMID, publicid + "");
//          openActivity(PublicAccountHistoryMessageList.class);
            // 换成web端
            Bundle bundle1 = new Bundle();
            String url = "";
            url = AppUtils.replacePubHistoryUrl(publicid + "", puacAccount.getPmtype() + "");
            bundle1.putString(AppConstants.INTENT_URL, url);
            bundle1.putString(AppConstants.INTENT_NAME, puacAccount.getNickname());
            openActivity(CommonBrowser.class, bundle1);
            break;
        case R.id.network_disable_button_relink:
//          WifiManager wifiManager = (WifiManager) ContactsPuacDatacardActivity.this.getSystemService(Context.WIFI_SERVICE);
//          wifiManager.setWifiEnabled(true);
//          final Timer timer = new Timer();
//          timer.schedule(new TimerTask() {
//          @Override
//              public void run() {
                    if (getIntent().getExtras()!=null) {
                        AppServer.getInstance().viewInfo(accountInfo.getUid() + "", 0 + "",publicid + "", 3, new OnAppRequestListener(){
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == 0) {
                                    PublicAccount puacs;
                                    puacs = (PublicAccount) obj;
                                    puacAccount = puacs;
                                    initView();
                                    layout_networkdisable.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
//              timer.cancel();
//              }
//          }, 10000, 10000);
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ContactsPuacDatacardActivity.this.finish();
            /* if (state.equals(AppConstants.CONTACTS_LOOKPUAC)) {
                Intent intent = new Intent(ContactsPuacDatacardActivity.this, MainActivity.class);
                intent.putExtra("acivityType", MainActivity.TAB_TAG_CONTACTS);
                startActivity(intent);
                ContactsPuacDatacardActivity.this.finish();
            } else {
                ContactsPuacDatacardActivity.this.finish();
            } */
        }
        return false;
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
