/**
 * 时光树
 * com.yey.kindergaten.activity
 * CommonBrowser.java
 * 
 * 2014年7月7日-上午11:35:41
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils.TruncateAt;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountBean;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.cropimage.ClipPictureActivity;
import com.yey.kindergaten.cropimage.CropImage;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.task.AsyncTask;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.MyWebView;
import com.yey.kindergaten.widget.RoundCornerProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import de.greenrobot.event.EventBus;

/**
 * 浏览URL
 *
 * CommonBrowser
 * chaowen
 * 511644784@qq.com
 * 2014年7月7日 上午11:35:41
 * @version 1.0.0
 * 
 */
public class CommonBrowser extends BaseActivity {

    @ViewInject(R.id.commonwebview)MyWebView webview;
    @ViewInject(R.id.header_title)TextView header_tv;
    @ViewInject(R.id.left_btn)ImageView left_iv;
    @ViewInject(R.id.head_layout)FrameLayout head_layout;
    @ViewInject(R.id.right_btn)ImageView right_btn;
    @ViewInject(R.id.second_right_iv)ImageView right_tv;
    // 提示页面
    @ViewInject(R.id.common_network_disable)LinearLayout layout_networkdisable;
    @ViewInject(R.id.network_disable_button_relink)ToggleButton networkbutton;
    @ViewInject(R.id.common_loading)LinearLayout layout_loading;
    @ViewInject(R.id.common_error)LinearLayout layout_error;
    @ViewInject(R.id.error_button)ToggleButton errorbutton;
    @ViewInject(R.id.common_empty)LinearLayout layout_empty;
    @ViewInject(R.id.title_layout)View title_layout;
    @ViewInject(R.id.progress_web)RoundCornerProgressBar progressbar;
    private String title = "";
    private String url = "";
    private String showtitle = "";
    private Context context;
    List<GroupInfoBean> sqllist;
    AccountInfo accountInfo;
    CharSequence[] kinditems = {"创建幼儿园", "加入幼儿园"};
    CharSequence[] classitems = { "加入班级", "创建班级" };
    GroupInfoBean groupInfoBean;
    private View myView = null;
    private CustomViewCallback myCallback = null;
    private WebChromeClient chromeClient = null;
    private FrameLayout frameLayout = null;
    private boolean isplay = false;
    String Status = "";
    private boolean isVideo = false;
    private CustomViewCallback xCustomViewCallback;
    private myWebChromeClient xwebchromeclient;
    boolean blockLoadingNetworkImage = false;

    boolean errorFlag = false;
    boolean errorFinishFlag = false;
    private String wrong_url = null;
    ValueCallback<Uri> mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 3;
    private static final int REQ_CAMERA = FILECHOOSER_RESULTCODE + 1;
    private static final int REQ_CHOOSE = REQ_CAMERA + 1;
    private String vid = null; // 视频id
    MediaScannerConnection msc = null;
    // private LoadingDialog dialog;
    public String fromType = "";
    public int isFullscreen;
    public String birthdayfrom = "";
    public String toidBirthday = "";
    public int grounpPosition;
    public int childPosition;
    public int cidBirthday;

    public  Date date;
    CharSequence[] items = { "相册", "拍照" };
    private String name;
    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/uploadimg/";
    private static final String PATHA = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/readyuoload/";
    private static final int CAMERA_SUCCESS = 2;
    private String uploadimgurl = "";
    private static final int PHOTO_CROP = 9;
    private int uploadtype;
    public static String uploadWG = "";
    private final static String TAG = "CommonBrowser";
    private final static int RESULT_CONTACTSPARENTLIST = 1;
    private final static int RESULT_TEACHERPARANTFRAGMENT = 2;

    @SuppressLint("SetJavaScriptEnabled")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
//                Intent showPhotoIntent;
//                    if (mode == 0) { // 网格浏览
//                showPhotoIntent = new Intent(CommonBrowser.this, PhotoShowGeneralActivity.class);
//                showPhotoIntent.putExtra("openType", msg.arg1 + "");
//                showPhotoIntent.putExtra("title", mTitle);
//                showPhotoIntent.putParcelableArrayListExtra("photos", photoShows);
//                showPhotoIntent.putExtra("replace", mReplace);
//                startActivity(showPhotoIntent);
            } else if (msg!=null) {
                date = null;
                Intent intent = new Intent(CommonBrowser.this, PhotoManager_ViewPager.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", AppConstants.PARAM_CommonBrowser);
                bundle.putStringArrayList("imglist", (ArrayList<String>) msg.obj);
                bundle.putStringArrayList("decslist", (ArrayList<String>) msg.obj);
                bundle.putInt("position", msg.arg1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commonbrowser);
        accountInfo = AppServer.getInstance().getAccountInfo();
        ViewUtils.inject(this);

        initView();
        loadUrl(url);
        context = this;
        this.registerForContextMenu(webview);
    }

    MyWebView.WebViewOnclickListener webViewOnclickListener = new MyWebView.WebViewOnclickListener() {
        @Override
        public void webviewOnclick(final String url, final String clickImageUrl) {
            Date nowdata = new Date();
            if (date == null) {
                date = nowdata;
            } else {
                if ((nowdata.getTime() - date.getTime()) > 2000) {
                    date = nowdata;
                } else {
                    date = nowdata;
                    return;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
                        Elements links = doc.select("img");
                        if (links.size() > 0) {
                            ArrayList<String> list = new ArrayList<String>();
                            int position = 0;
                            for (int i = 0; i < links.size(); i++) {
                                org.jsoup.nodes.Element element = links.get(i);
                                final String imgUrl = element.attr("src");
                                list.add(imgUrl);
                                if (imgUrl!=null && imgUrl.equals(clickImageUrl)) {
                                    position = i;
                                }
                            }
                            if (url!=null && url.contains("view=1")) {
                                Message message = new Message();
                                message.what = 1;
                                message.obj = list;
                                message.arg1 = position;
                                handler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    /**
     * 启动分享组件
     *
     * @param content
     */
    private void shareComments(String content) {
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.setDialogMode();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("时光树分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sgs.yey.com/");
        // text是分享文本，所有平台都需要这个字段
        String shareContent = content;
        oks.setText(shareContent);

//        oks.setUrl("http://sgs.yey.com/");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sgs.yey.com/");
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (QZone.NAME.equals(platform.getName())) {
                    // qq空间
                    paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                } else if (QQ.NAME.equals(platform.getName())) {
//                    paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                } else if (Email.NAME.equals(platform.getName())) {
                    paramsToShare.setImageUrl("http://sgs.yey.com/qrcode.png");
                }
            }
        });
        // 启动分享GUI
        oks.show(AppContext.getInstance());
    }

    /**
     * 启动分享组件
     *
     * @param text
     */
    private void share(String title, String text, String img, String url) {
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.setDialogMode();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        final String imagUrl = img;
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (QZone.NAME.equals(platform.getName())) {
                    // qq空间
                    paramsToShare.setImageUrl(imagUrl);
                } else if (Wechat.NAME.equals(platform.getName())) {
                    // 微信
                    paramsToShare.setImageUrl(imagUrl);
                } else if (QQ.NAME.equals(platform.getName())) {
                    // QQ
                    paramsToShare.setImageUrl(imagUrl);
                } else if (Email.NAME.equals(platform.getName())) {
                    paramsToShare.setImageUrl(imagUrl);
                } else {
                    paramsToShare.setImageUrl(imagUrl);
                }
            }
        });
        // 启动分享GUI
        oks.show(AppContext.getInstance());
    }

    /**
     * 拨打电话
     *
     * @param phone
     */
    public void callPhone(String phone) {
        Intent phoneIntent = new Intent(
                "android.intent.action.CALL", Uri.parse("tel:" + phone));
        startActivity(phoneIntent);
    }

    /**
     * 园长在新建幼儿园点击下一步时调用
     *
     * @param json
     */
    public void parseJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            AppContext.getInstance().getMediaPlayer().start();

            updateMessage(object.getString("avatar"), object.getString("title"), object.getString("content"),
                    object.getString("date"), object.getString("url"), object.getInt("action"));
            if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                updateAccontInfo(object.getInt("kid"), object.getString("kname"));
                AppContext.getInstance().getMediaPlayer().start();
                getAddParentMessage(object);
                getClassListByKid(object);
//                addCompleteSelfInfo();
            } else {
                updateAccontInfo(object.getInt("kid"), object.getString("kname"), object.getString("uname"), object.getInt("cid"));
                reFreshTeachers(object.getInt("kid"));
                getParentsAndClassByKid(accountInfo.getUid(), object.getInt("kid"));
//                addCompleteSelfInfo();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装完善个人资料消息
     */
    public void addCompleteSelfInfo() {
        if (accountInfo.getRealname() == null || accountInfo.getRealname().length() == 0) {
            updateMessage("","玩转时光树", "完善个人资料！",
                    TimeUtil.getMoreTime(System.currentTimeMillis()), "", 77);
        }
    }

    /**
     * 园长新增一条邀请家长加入消息
     *
     * @param object
     * @throws JSONException
     */
    private void getAddParentMessage(JSONObject object) throws JSONException {
        updateMessage(object.getString("avatar"), object.getString("titleParent"), object.getString("contentParent"),
                object.getString("date"), object.getString("urlParent"), object.getInt("actionParent") );
    }

    /**
     * 园长刷新班级列表
     *
     * @param object
     * @throws JSONException
     */
    public void getClassListByKid(JSONObject object) throws JSONException {
        AppServer.getInstance().getClassesByKid(accountInfo.getUid(), object.getInt("kid"), accountInfo.getRole(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    List<Classe> list = (List<Classe>) obj;
                    Contacts contacts = AppContext.getInstance().getContacts();
                    if (list!=null) {
                        contacts.setClasses(list);
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Classe.class);
                            DbHelper.getDB(CommonBrowser.this).saveAll(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    AppContext.getInstance().setContacts(contacts);
                }
                postEvent(AppEvent.PARENTFRAGMENT_RELOADDATA);
            }
        });
    }

    /**
     * 老师刷新通讯录
     *
     * @param uid
     * @param kid
     */
    private void getParentsAndClassByKid(int uid,int kid) {
        AppServer.getInstance().getParentsByTeacherKid(uid, kid, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                 if (code == AppServer.REQUEST_SUCCESS) {
                     List<Parent>list = (List<Parent>) obj;
                     if (list!=null) {
                         try {
                             DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                             DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                         } catch (DbException e) {
                             e.printStackTrace();
                         }
                     }
                 }
            }
        });
    }

    private void teacherUpdateAccountInfo(String json) {
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            Log.i("account","kid------>" + object.getInt("kid"));
            Log.i("account","uname------>" + object.getString("uname"));
            Log.i("account","kname------>" + object.getString("kname"));
            updateAccontInfo(object.getInt("kid"), object.getString("kname"), object.getString("uname"), 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reFreshTeachers(int kid) {
        AppServer.getInstance().getTeachersByKid(accountInfo.getUid(), kid, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    List<Teacher>list = (List<Teacher>) obj;
                    if (list!=null) {
                        try {
                        Contacts contacts = AppContext.getInstance().getContacts();
                        contacts.setTeachers(list);
                        DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                        DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    postEvent(AppEvent.TEACHERFRFRAGMENT_RELOADDATA);
                }
            }
        });
    }

    public void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                System.out.println("PostThreadId:" + Thread.currentThread().getId());
            }
        }).start();
    }

    /**
     * js调用adroid传值，更新本地kid
     *
     * @param kid
     */
    public void updateAccontInfo(int kid, String kname) {
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        info.setKid(kid);
        info.setKname(kname);
        AppServer.getInstance().setmAccountInfo(info);
        AccountBean bean = new AccountBean(info);
        AppServer.getInstance().setmAccountBean(bean);
        try {
            DbHelper.getDB(AppContext.getInstance()).update(info, WhereBuilder.b("uid", "=", info.getUid()), new String[]{"kid", "kname"});
            DbHelper.getDB(AppContext.getInstance()).update(bean, WhereBuilder.b("uid", "=", bean.getUid()), new String[]{"kid", "kname"});
        } catch (DbException e) {
            e.printStackTrace();
        }
//        DbHelper.updateAccountInfo(info);
    }

    /**
     * js调用adroid传值，更新本地kid
     *
     * @param kid
     */
    public void updateAccontInfo(int kid, String kname, String realname, int cid) {
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        info.setKid(kid);
        info.setKname(kname);
        info.setCid(cid);
        info.setRealname(realname);
        AppServer.getInstance().setmAccountInfo(info);
        AccountBean bean = new AccountBean(info);
        AppServer.getInstance().setmAccountBean(bean);
        try {
            DbHelper.getDB(AppContext.getInstance()).update(info, WhereBuilder.b("uid", "=", info.getUid()), new String[]{"kid","kname","realname","cid"});
            DbHelper.getDB(AppContext.getInstance()).update(bean, WhereBuilder.b("uid", "=", bean.getUid()), new String[]{"kid","kname","realname","cid"});
        } catch (DbException e) {
            e.printStackTrace();
        }
//      DbHelper.updateAccountInfo(info);
    }

    /**
     * 更新消息表
     *
     * @param avatar
     * @param title
     * @param content
     * @param date
     * @param url
     * @param acition
     */
    public void updateMessage(String avatar, String title, String content, String date, String url, int acition) {
        MessageRecent newMessagePublic = new MessageRecent("0", title, date, "", "", "", content, url, "", 1, acition, 0, avatar, 0, "0", "0");
        try {
            MessageRecent m = DbHelper.getDB(AppContext.getInstance()).findFirst(MessageRecent.class, WhereBuilder.b("action", "=", acition));
            if (m == null) {
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
            } else {
                DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("action", "=", acition)); // 74表示园长完成创建幼儿园的下一步
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

//    private String mTitle = "";
//    private String mReplace = "";
//    private ArrayList<PhotoShow> photoShows = new ArrayList<PhotoShow>();

    private void loadUrl(String url) {
        UtilsLog.i(TAG, url);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
        final boolean isFirstLogin = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getBoolean(AppConstants.DIRECTOR_ISFIRST_CREAT,false);
        final boolean isFirsTeacherRediget = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getBoolean("TEACHER_REDIGET",false);
        // id格式：例如：
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void startAppActivity(String id) {
                Intent intent;
                if (id.equals("closeScheduleTip")) {
                    intent = new Intent(CommonBrowser.this, ServiceScheduleActivity.class);
                    startActivity(intent);
                } else if (id.equals("closeWebPage")){ // 关闭页面
                    CommonBrowser.this.finish();
                } else if (id.contains("weixin$") || id.contains("qq$") || id.contains("sms$")){ // 分享App
                    shareComments(id.substring(id.lastIndexOf("$") + 1));
                } else if (id.contains("share")) { // 统一分享
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    try {
                        JSONObject object = new JSONObject(messageJson);
                        String title = object.getString("title");// 分享标题
                        String text = object.getString("text");  // 分享内容
                        String img = object.getString("img");    // 分享显示小图标
                        String url = object.getString("url");    // 点击跳转的url
                        share(title, text, img, url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (id.contains("uploadPhoto")) { // 统一上传图片接口
                    AppContext.checkList.clear();
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    try {
                        JSONObject object = new JSONObject(messageJson);
                        uploadtype = object.getInt("type");         // 照片类型。整型
                        uploadWG = object.getString("uploadurl");   // 处理照片上传的网关，此url要有时光树标准的参数占位符
                        int cut = object.getInt("cut");             // 0表示照片不需要在客户端经过选择裁减过程，1表示需要
                        mCut = cut;
                        showDiaglog(cut);
//                      uploadImgByParams(type, uploadurl, cut);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (id.contains("viewPhotos")) {
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    try {
                        JSONObject object = new JSONObject(messageJson);
                        int mode = 0;
                        String api = "";
                        String replace = "";
                        if (!object.isNull("mode")) {
                            mode = object.getInt("mode");            // 客户端浏览模式。0 :网格浏览，一排多张图片。1: 并排浏览
                        }
                        if (!object.isNull("api")) {
                            api = object.getString("api");           // 字符串。供客户端调用，api需要是完整路径的，用于获取需要浏览的所有图片url，后台可动态替换api，但api的返回必须是一致的
                        }
                        if (!object.isNull("replace")) {
                            replace = object.getString("replace");   // 小图都是包含"_small"，大图没有统一，替换成replace的字符串；
                        }

                        final int finalMode = mode;
                        final String finalReplace = replace;

                        Intent showPhotoIntent;
                        showPhotoIntent = new Intent(CommonBrowser.this, PhotoShowGeneralActivity.class);
                        showPhotoIntent.putExtra("openType", mode + "");
                        showPhotoIntent.putExtra("api", api);
                        showPhotoIntent.putExtra("replace", replace);
                        startActivity(showPhotoIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (id.contains("tel$")) { // 拨打电话
                    String phone = id.substring(id.lastIndexOf("$") + 1);
                    callPhone(phone);
                } else if (id.contains("regkinfo$")) {
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    teacherUpdateAccountInfo(messageJson);
//                  SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean("TEACHER_REDIGET", true);
                } else if (id.contains("regmsg$") && !isFirstLogin) { // 园长新建幼儿园，重新收到一个通知
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    parseJson(messageJson);
                    SharedPreferencesHelper.getInstance(AppContext.getInstance()).setBoolean(AppConstants.DIRECTOR_ISFIRST_CREAT, true);
                } else if(id.equals("closeScheduleTip")) {
                    intent = new Intent(CommonBrowser.this, ServiceScheduleActivity.class);
                    startActivity(intent);
                } else if (id.equals("closeAlbumTip")) {
                    intent = new Intent(CommonBrowser.this, ClassPhotoMainActivity.class);
                    startActivity(intent);
                } else if (id.equals("closeLifePhotoTip")) {
                    intent = new Intent(CommonBrowser.this, ServiceLifePhotoMainActivity.class);
                    intent.putExtra("type", "2");
                    startActivity(intent);
                } else if (id.equals("closeWorkPhotoTip")) {
                    intent = new Intent(CommonBrowser.this, ServiceLifePhotoMainActivity.class);
                    intent.putExtra("type", "1");
                    startActivity(intent);
                } else if (id.equals("closediarytip")) {
                    intent = new Intent(CommonBrowser.this, GrowthDiaryActivity.class);
                    startActivity(intent);
                } else if (id.contains("getPhoto$")) {
                    AppContext.checkList.clear();
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    try {
                        JSONObject object = new JSONObject(messageJson);
                        uploadtype = object.getInt("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showDiaglog(-1);
                } else if (id.contains("updateBirthdayStatus")) {
                    UtilsLog.i(TAG, "callback is updateBirthdayStatus$");
                    if (birthdayfrom!=null && ("TeacherParantFragment").equals(birthdayfrom)) {
                        UtilsLog.i(TAG, "已发送生日祝福，返回TeacherParantFragment,grounpPosition,childPosition : " + grounpPosition + "," + childPosition);
                        if (cidBirthday!=0 && toidBirthday!=null && !toidBirthday.equals("")) {
                            setBirthdayStatus(cidBirthday, toidBirthday);
                        }
                        intent = new Intent();
                        intent.putExtra("birthdaystatus", "hassend");
                        intent.putExtra("grounpPosition", grounpPosition);
                        intent.putExtra("childPosition", childPosition);
                        setResult(RESULT_TEACHERPARANTFRAGMENT, intent);
                        UtilsLog.i(TAG, "set result ok");
                        postEvent(AppEvent.TEACHERPARANTFRAGMENT_BIRTHDAY);
                        CommonBrowser.this.finish();
                    } else if (birthdayfrom!=null && ("ContactsParentList").equals(birthdayfrom)) {
                        UtilsLog.i(TAG,"已发送生日祝福，返回ContactsParentList,grounpPosition,childPosition : " + grounpPosition + "," + childPosition);
                        /*if (cidBirthday!=0 && toidBirthday!=null && !toidBirthday.equals("")) {
                            setBirthdayStatus(cidBirthday, toidBirthday);
                        }*/
                        intent = new Intent();
                        intent.putExtra("birthdaystatus", "hassend");
                        intent.putExtra("grounpPosition", grounpPosition);
                        intent.putExtra("childPosition", childPosition);
                        setResult(RESULT_CONTACTSPARENTLIST, intent);
                        UtilsLog.i(TAG,"set result ok");
                        postEvent(AppEvent.CONTACTSPARENTLIST_BIRTHDAY);
                        CommonBrowser.this.finish();
                    } else {
                        CommonBrowser.this.finish();
                    }
                } else if (id.contains("playCCVideo$")) {
                    Log.i("vedioplay"," get vedio id...");
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    try {
                        JSONObject object = new JSONObject(messageJson);
                        playCcVedio(object.getString("videoId"));
                        Log.i("vedioplay","prepare to play...");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (id.contains("pay$")) {
                    String messageJson = id.substring(id.lastIndexOf("$") + 1);
                    try {
                        JSONObject object = new JSONObject(messageJson);
                        String feename = object.getString("feename");
                        String feeid = object.getString("feeid");
                        String price = object.getString("price");
                        String desc = object.getString("desc");
                        Intent payintent = new Intent(CommonBrowser.this, OpenHealthServicesActivity.class);
                        payintent.putExtra("feename", feename);
                        payintent.putExtra("feeid", feeid);
                        payintent.putExtra("price", price);
                        payintent.putExtra("desc", desc);
                        startActivity(payintent);
                        CommonBrowser.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "wst");
    }

    public void playCcVedio(String playVedio) {
        Intent intent = new Intent(this, MediaPlayActivity.class);
        intent.putExtra("videoId", playVedio);
        startActivity(intent);
        Log.i("vedioplay", "start MediaPlayActivity...");
    }

    public void setBirthdayStatus(int cid, String toid) {
        UtilsLog.i(TAG,"start to setBirthdayStatus");
        if (toid!=null && !toid.equals("")) {
            try {
                Parent parent = DbHelper.getDB(this).findFirst(Selector.from(Parent.class).where("uid", "=", toid));
                if (parent!=null) {
                    parent.setBirthdaystatus(2);
                    DbHelper.getDB(this).update(parent, WhereBuilder.b("uid", "=", toid));
                    UtilsLog.i(TAG, "update BrithdayStatus complete, uid is: " + toid);
                } else {
                    UtilsLog.i(TAG, "update BrithdayStatus fail, because parent is null");
                }
            } catch (DbException e) {
                e.printStackTrace();
                UtilsLog.i(TAG, "exception" + e.getMessage() + "/" + e.getCause());
            }

        /*try {
            List<Parent> parents = DbHelper.getDB(CommonBrowser.this).findAll(Parent.class);
            UtilsLog.i(TAG,"find parents ok");
            List<Parent> parentsNew  = new ArrayList<Parent>();
            if (parents == null) {
                parents = new ArrayList<Parent>();
            }
            for (int i = 0; i < parents.size(); i++) {
                Parent parent = parents.get(i);
                if ((parent.getUid() + "").equals(toid) && parent.getCid() == cid) {
                    parent.setBirthdaystatus(2);
                    UtilsLog.i(TAG,"set birthdaystatus cid toid:" + cid + "/" + toid);
                }
                parentsNew.add(parent);
            }
            DbHelper.getDB(CommonBrowser.this).deleteAll(Parent.class);
            UtilsLog.i(TAG,"delete parents ok");
            DbHelper.getDB(CommonBrowser.this).save(parentsNew);
            UtilsLog.i(TAG,"save parents ok");
        } catch (DbException e) {
            e.printStackTrace();
            UtilsLog.i(TAG,"findall or save parent fail, because DbException");
        } catch (Exception e) {
            UtilsLog.i(TAG, e.getMessage() + "/" + e.getCause());
        }*/
        }
    }

    // @JavascriptInterface
    public void startAppActivity(String id) {
        Toast.makeText(this,id,Toast.LENGTH_SHORT).show();
        if (id.equals("closeScheduleTip")) {
            Intent intent = new Intent(this, ServiceScheduleActivity.class);
            startActivity(intent);
        }
        /*c
           下面的代码是任务模块的代码，先注销，以后要用到的时候，JS页面所传的参数需要转变成String类型
            而不是整形
        Intent intent = null;
        if(Status.equals("1")) {
            showToast("该任务已完成,请选择其他任务");
            return;
        }
        int type = 0;
        switch (Integer.parseInt(id)) {
        case 1:   //APP功能介绍

            break;
        case 2:   //创建班级
                if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
                             intent=new Intent(CommonBrowser.this,ServiceCompleteInformationActivity.class);
                            startActivityForResult(intent, 1);
                }else{
                    intent=new Intent(this,ServiceCreateKinderActivity.class);
                    intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
                    startActivity(intent);
                }
            break;
        case 3:   //订阅公众号
            intent=new Intent(this,ServiceTaskBookPuacActivity.class);
            intent.putExtra("state", AppConstants.TASKMAIN);
            startActivity(intent);
            break;
        case 4:   //加好友
            intent=new Intent(this,ContactsAddFriendActivity.class);
            intent.putExtra("state", AppConstants.TASKMAIN);
            startActivity(intent);
            break;
        case 5:   //加入班级
            intent=new Intent(this,ServiceAddKinderActivity.class);
            startActivity(intent);
            break;
        case 6:  //创建或者加入班级
            showDialogItems(classitems, "创建或者加入班级", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if(item==0){   //加入班级
                        Intent  intent=new Intent(CommonBrowser.this,ServiceAddKinderActivity.class);
                        intent.putExtra(AppConstants.STATE, AppConstants.TASKMAIN);
                        startActivity(intent);
                  }else{           //创建班级
                        if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
                                    Intent intent=new Intent(CommonBrowser.this,ServiceCompleteInformationActivity.class);
                                    startActivityForResult(intent, 1);
                        }else{
                            Intent intent=new Intent(CommonBrowser.this,ServiceCreateKinderActivity.class);
                            intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
                            startActivity(intent);
                        }
                 }
                }
            });
            break;
        case 7:   //加入或创建一个幼儿园
            showDialogItems(kinditems, "创建或者加入幼儿园群", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if(item==1){   //加入幼儿园
                        Intent  intent=new Intent(CommonBrowser.this,ServiceAddKinderActivity.class);
                        intent.putExtra(AppConstants.STATE, AppConstants.TASKMAIN);
                        startActivity(intent);
                  }else{           //创建幼儿园
                    Boolean isflag=true;
                    for(int i=0;i<sqllist.size();i++){
                        if(sqllist.get(i).getGtype()==1){
                            isflag=false;
                            break;
                        }
                    }
                    if(isflag){
                        if(accountInfo.getRealname()==null||accountInfo.getTelephone()==null||accountInfo.getRealname().equals("")||accountInfo.getTelephone().equals("")){
                                Intent intent=new Intent(CommonBrowser.this,ServiceCompleteInformationActivity.class);
                                startActivityForResult(intent, 0);
                        }else{
                            Intent intent=new Intent(CommonBrowser.this,ServiceCreateKinderActivity.class);
                            intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEKINDER);
                            startActivity(intent);
                        }
                    }else{
                        showToast("已经拥有一个幼儿园群，不可以在添加幼儿园群");
                    }
                 }
                }
            });
            break;
        case 8:   //加入幼儿园
            intent=new Intent(CommonBrowser.this,ServiceAddKinderActivity.class);
            startActivity(intent);
            break;
        case 9:  //完善班级资料
            intent=new Intent(this, ServiceGetgroupActivity.class);
            startActivity(intent);
            break;
        case 10:  //完善个人资料
            intent=new Intent(this,MeInfoActivity.class);
            intent.putExtra("state", AppConstants.TASKMAIN);
            startActivity(intent);

            break;
        case 11:  // 完善幼儿园资料
            for (int i = 0 ;i<sqllist.size();i++) {
                groupInfoBean=sqllist.get(i);
                if(groupInfoBean.getGtype()==1){
                    intent=new Intent(this,ServiceCreateKinderSuccessActivity.class);
                    intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
                    startActivity(intent);
                }
            }
            break;
        case 12:   // 邀请家长加入班级
            for (int i = 0; i < sqllist.size(); i++) {
                groupInfoBean = sqllist.get(i);
                if (groupInfoBean.getGtype() == 2) {  // 班级群
                    intent = new Intent(this, ServiceCreateKinderSuccessActivity.class);
                    intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
                    startActivity(intent);
                }
            }
            break;
        case 13:   // 邀请老师加入幼儿园
            for (int i = 0; i < sqllist.size(); i++) {
                groupInfoBean = sqllist.get(i);
                if (groupInfoBean.getGtype() == 2) {  // 班级群
                    intent = new Intent(this, ServiceCreateKinderSuccessActivity.class);
                    intent.putExtra(AppConstants.GNUM, groupInfoBean.getGnum());
                    startActivity(intent);
                }
            }
            break;
        case 14:   // 在朋友圈中发表动态
            intent = new Intent(this, ServiceFriendsterActivity.class);
            intent.putExtra("state", AppConstants.TASKMAIN);
            startActivity(intent);
            break;
        case 15:  // 在朋友圈中发表评论
            intent = new Intent(this, ServiceFriendsterActivity.class);
            intent.putExtra("state", AppConstants.TASKMAIN);
            startActivity(intent);
            break;
        case 16:   // 账户安全
            intent = new Intent(this, IdSafeActivity.class);
            intent.putExtra("state", AppConstants.TASKMAIN);
            startActivity(intent);
            break;
        case 17:
            break;
        default:
            break;
        }
        */
      }

    private void showDiaglog(final int cut) {
        showDialogItems(items, "选择照片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 1) {   // 拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    name = DateFormat.format("yyyyMMddhhmmss",
                            Calendar.getInstance(Locale.CHINA))
                            + ".jpg";
                    File file = new File(PATH + "takephoto/");
                    if (!file.exists()) {
                        file.mkdirs(); // 创建文件夹
                    }
                    Uri imageUri = Uri.fromFile(new File(PATH, name));
                    System.out.println("imageUri----" + imageUri.toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_SUCCESS);
                } else {         // 相册
                    Intent intent = new Intent(CommonBrowser.this, GalleryActivity.class);
                    intent.putExtra("typefrom", AppConstants.FROMWEB);
                    intent.putExtra("cut", cut);
                    startActivity(intent);
                }
            }
        });
    }

//    private void startImageAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
//        Intent intent = null;
//        if (isCrop) {
//            intent = new Intent("com.android.camera.action.CROP");
//        } else {
//            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//        }
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("scale", true);
//        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        intent.putExtra("return-data", true);
//        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        //intent.putExtra("noFaceDetection", true); // no face detection
//        startActivityForResult(intent, requestCode);
//    }

    // launchMode为singleTask的时候，通过Intent启到一个Activity,
    // 如果系统已经存在一个实例，系统就会将请求发送到这个实例上，
    // 但这个时候，系统就不会再调用通常情况下我们处理请求数据的onCreate方法，
    // 而是调用onNewIntent方法
    // 不要忘记，系统可能会随时杀掉后台运行的Activity，
    // 如果这一切发生，那么系统就会调用onCreate方法，而不调用onNewIntent方法，
    // 一个好的解决方法就是在onCreate和onNewIntent方法中调用同一个处理数据的方法
    @Override
    protected void onNewIntent(Intent intent){
        if (intent!=null && intent.getExtras()!=null) {
            ArrayList<Photo> list = intent.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
            String cut_type = intent.getStringExtra("cut_type");
            if (cut_type!=null && cut_type.equals("yes")) {
                name = DateFormat.format("yyyyMMddhhmmss",
                        Calendar.getInstance(Locale.CHINA))
                        + ".jpg"; // 拿到裁剪的照片bitmap后，保存的名称
                startCropImage(list.get(0).imgPath);
                AppContext.checkList.clear();
            } else {
                if (list != null) {
                    uploadImgByParams(uploadtype, list.get(0).imgPath, mCut);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Uri uri = null;
        if (null == mUploadMessage) {
            if (requestCode == CAMERA_SUCCESS) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    showToast("SD不可用");
                    return;
                }
                if (mCut == 0) {
                    uploadImgByParams(uploadtype, PATH + name, mCut);
                } else {
                    startCropImage(PATH + name);
                }
            } else if (requestCode == PHOTO_CROP) {
                if (intent == null) {
                    // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (intent.getExtras()!=null) {
                        String path = intent.getExtras().getString(CropImage.IMAGE_PATH);
                        Bitmap newBitmap = FileUtils.getLocalBitmap(this, path);
                        BitmapUtil.savePhotoToSDCard(newBitmap, PATHA, name);
                        uploadImgByParams(uploadtype, PATHA + name, mCut);
                    } else {
                        showToast("上传失败");
                    }
                }
            } else {
                return;
            }
        }
        if (requestCode == 0) {
            intent = new Intent(this, ServiceCreateKinderActivity.class);
            intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEKINDER);
            startActivity(intent);
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
            super.onActivityResult(requestCode, resultCode, intent);
        } else if (requestCode == 1) {
            intent = new Intent(this,ServiceCreateKinderActivity.class);
            intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
            startActivity(intent);
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
            super.onActivityResult(requestCode, resultCode, intent);
        } else if (requestCode == REQ_CAMERA) {
            afterOpenCamera();
            uri = cameraUri;
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
            super.onActivityResult(requestCode, resultCode, intent);
        } else if (requestCode == REQ_CHOOSE) {
            uri = afterChosePic(intent);
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void startCropImage(String path) {
//        Intent intent = new Intent(this, CropImage.class);
//        intent.putExtra(CropImage.IMAGE_PATH, path);
//        intent.putExtra(CropImage.SCALE, true);
//
//        intent.putExtra(CropImage.ASPECT_X, 1);
//        intent.putExtra(CropImage.ASPECT_Y, 1);
//
//        startActivityForResult(intent, PHOTO_CROP);

        Intent intent = new Intent(this, ClipPictureActivity.class);
        intent.putExtra(ClipPictureActivity.IMAGE_PATH, path);

        startActivityForResult(intent, PHOTO_CROP);
    }

//    private void uploadImg(String img) {
//        BitmapUtil.createSDCardDir();
//        File f= new File(img);
//        name = f.getName();
//        BitmapUtil.save(img, name, PATH);
//        File  file = new File(PATH + name);
//        showLoadingDialog("正在上传...");
//        AppServer.getInstance().uploadwebimage(file, uploadtype, new OnAppRequestListener() {
//            @Override
//            public void onAppRequest(int code, String message, Object obj) {
//                if (code == AppServer.REQUEST_SUCCESS) {
//                    uploadimgurl = (String) obj;
//                    webview.loadUrl("javascript:photoCallBack( " + "'" + uploadimgurl + "'" + ")");
//                    cancelLoadingDialog();
//                } else {
//                    cancelLoadingDialog();
//                    showToast("上传失败");
//                }
//            }
//        });
//    }
    private int mCut = -1; // 是否需要在客户端选择裁剪：  0 表示照片不需要，1 表示需要
    private void uploadImgByParams(final int type, final String uploadurl, int cut) {
        BitmapUtil.createSDCardDir();
        File f = new File(uploadurl);
        name = f.getName();
        BitmapUtil.save(uploadurl, name, PATH);
        File file = new File(PATH + name);
        showLoadingDialog("正在上传...");
        String style = ""; // general 通用的上传； other 保留之前的上传方式
        if (cut == -1) {    // 原来的uploadImg，直接上传照片
            style = "other";
        } else { // 通用的上传照片
            style = "general";
        }
        final String finalStyle = style;
        AppServer.getInstance().uploadwebimage(file, type, style, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    uploadimgurl = (String) obj;
                    if (finalStyle.equals("other")) {
                        webview.loadUrl("javascript:photoCallBack( " + "'" + uploadimgurl + "'" + ")");
                    } else if (finalStyle.equals("general")) {
                        webview.loadUrl("javascript:uploadSuccess( " + type + "," + "'" + uploadimgurl + "'" + ")");
                    }
                    UtilsLog.i(TAG, "type : " + type + "uploadimgurl" + uploadimgurl);
                    cancelLoadingDialog();
                    showToast("上传成功");
                } else {
                    cancelLoadingDialog();
                    showToast("上传失败");
                }
            }
        });

//        BitmapUtil.createSDCardDir();
//        File f= new File(img);
//        name = f.getName();
//        BitmapUtil.save(img, name, PATH);
//        File  file = new FileuploadSuccess(PATH + name);
//        showLoadingDialog("正在上传...");
//        AppServer.getInstance().uploadwebimage(file, uploadtype, new OnAppRequestListener() {
//            @Override
//            public void onAppRequest(int code, String message, Object obj) {
//                if (code == AppServer.REQUEST_SUCCESS) {
//                    uploadimgurl = (String) obj;
//                    webview.loadUrl("javascript:photoCallBack( " + "'" + uploadimgurl + "'" + ")");
//                    cancelLoadingDialog();
//                } else {
//                    cancelLoadingDialog();
//                    showToast("上传失败");
//                }
//            }
//        });
    }

    private void startImageAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    public class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // if (AppIntent.getMIMEType(url).equals("url")) {
                view.loadUrl(AppUtils.replaceUnifiedUrl(url));
            /*} else {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 设置intent的Action属性
                intent.setAction(Intent.ACTION_VIEW);
                // 获取文件file的MIME类型
                String type = AppIntent.getMIMEType(url);
                // 设置intent的data和Type属性。
                intent.setDataAndType(Uri.fromFile(new File(url)), type);
                // 跳转
                startActivity(intent);
            }*/
            return false;
        }

        // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
        @Override
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            UtilsLog.d(TAG, " onLoadResource ");
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished ( WebView view, String url ) {
            if (webview.getTitle() != null) {
                String title = webview.getTitle();
                if (title!=null && title.length() > 12) {
                    title = title.substring(0, 12) + "...";
                }
                CommonBrowser.this.setTitle(title);
                header_tv.setText(title);
                if (isFullscreen == 20 || isFullscreen == 99) {
                    title_layout.setVisibility(View.GONE);
                }
            }
            /*if (dialog!=null) {
                dialog.dismiss();
            }*/
            // layout_loading.setVisibility(View.GONE);
            progressbar.setVisibility(View.GONE);
            if (!errorFlag) {
                layout_networkdisable.setVisibility(View.GONE);
            }
            if (errorFlag) {
                layout_networkdisable.setVisibility(View.VISIBLE);
            }
//          errorFlag = false;
            if (!webview.getSettings().getLoadsImagesAutomatically()) {
                webview.getSettings().setLoadsImagesAutomatically(true);
            }
           
            if (webview.canGoBack()) {
                right_tv.setVisibility(View.VISIBLE);
                right_tv.setImageDrawable(getResources().getDrawable(R.drawable.icon_webhome));
                right_btn.setVisibility(View.VISIBLE);
                right_btn.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
            } else {
                right_btn.setVisibility(View.GONE);
                right_tv.setVisibility(View.GONE);
            }
            // 取视频id
            String callvid = "javascript:getvid()";
            webview.loadUrl(callvid);
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            layout_networkdisable.setVisibility(View.VISIBLE);
            wrong_url = failingUrl;
            loadUrl("");
            errorFlag = true;
        }

        @Override 
        public void onPageStarted ( WebView view, String url, Bitmap favicon ) {
            // errorFlag = false;
            // super.onPageStarted(view, url, favicon);
            // dialog.show();
            // layout_loading.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.VISIBLE);
            progressbar.setProgress(0);
            if (url.endsWith(".apk")) {
                // download(url); // 下载处理
            }
        }
    }

    public class myWebChromeClient extends WebChromeClient {
        private View xprogressvideo;
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            if (mUploadMessage != null) return;
            mUploadMessage = uploadMsg;
            selectImage();
            // Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            // i.addCategory(Intent.CATEGORY_OPENABLE);
            // i.setType("*/*");
            // startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );
        }
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser( uploadMsg, "" );
        }
        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            isVideo = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            webview.setVisibility(View.INVISIBLE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (myView != null) {
                callback.onCustomViewHidden();
                return;
            }
            frameLayout.addView(view);
            myView = view;
            xCustomViewCallback = callback;
            frameLayout.setVisibility(View.VISIBLE);
            title_layout.setVisibility(View.GONE);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {
            isVideo = false;
            if (myView == null) // 不是全屏播放状态
                return;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            myView.setVisibility(View.GONE);
            frameLayout.removeView(myView);
            myView = null;
            frameLayout.setVisibility(View.VISIBLE);
            xCustomViewCallback.onCustomViewHidden();
            webview.setVisibility(View.VISIBLE);
            title_layout.setVisibility(View.VISIBLE);
        }

        public void onProgressChanged ( WebView view, int progress ) {
            progressbar.setProgress(progress);
            progressbar.setProgressColor(getResources().getColor(R.color.red_500));
            if (progress > 90) {
                /* if(dialog!=null){
                    dialog.dismiss();
                }*/
                // layout_loading.setVisibility(View.GONE);
                progressbar.setVisibility(View.GONE);
            }
        }
        public void onReceivedTitle(WebView view, String title) {
            if (title.length() > 8) {
                title = title.substring(0,8) + "...";
            }
            header_tv.setText(title);
            CommonBrowser.this.setTitle(title);
            if (isFullscreen == 20 || isFullscreen == 99) {
                title_layout.setVisibility(View.GONE);
            }
            super.onReceivedTitle(view, title);
        };
    }
      
    private void initView() {
        frameLayout = (FrameLayout)findViewById(R.id.framelayout);
        if (Build.VERSION.SDK_INT >= 19) {
            webview.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webview.getSettings().setLoadsImagesAutomatically(false);
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle!=null) {
            title = bundle.getString(AppConstants.INTENT_NAME);
            url = bundle.getString(AppConstants.INTENT_URL);
            fromType = bundle.getString("type");
            isFullscreen = bundle.getInt(AppConstants.INTENT_FULL_SCREEN);
            birthdayfrom = bundle.getString("birthdayfrom");
            grounpPosition = bundle.getInt("grounpPosition", -1);
            childPosition = bundle.getInt("childPosition", -1);
            cidBirthday = bundle.getInt("cidBirthday", 0);
            toidBirthday = bundle.getString("toidBirthday");
        }
        networkbutton.setText("重新连接");
        networkbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                errorFlag = false;
                if (AppUtils.isNetworkAvailable(CommonBrowser.this)) {
                    if (wrong_url!=null && wrong_url.length()!=0) {
                        loadUrl(wrong_url);
                    } else {
                        loadUrl(url);
                    }
                } else {
                    showToast("请检查网络设置，再点击重新连接");
                }
            }
        });
        // dialog = new LoadingDialog(CommonBrowser.this,"正在加载...");
        if (title!=null && title.length() > 12) {
            title = title.substring(0,12) + "...";
        }
        header_tv.setText(title);
        header_tv.setSingleLine();
        header_tv.setEllipsize(TruncateAt.END);
        header_tv.setMaxEms(12);
        // header_tv.setEms(ems);
        left_iv.setVisibility(View.VISIBLE);
        if (isFullscreen == 20 || isFullscreen == 99) {
            title_layout.setVisibility(View.GONE);
        }
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        WebSettings ws = webview.getSettings();
        ws.setBuiltInZoomControls(false);                       // 隐藏缩放按钮
        //ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // 排版适应屏幕
        ws.setUseWideViewPort(true);                            // 可任意比例缩放
        ws.setLoadWithOverviewMode(true);                       // setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        ws.setRenderPriority(RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= 19) {
            ws.setLoadsImagesAutomatically(true);
        } else {
            ws.setLoadsImagesAutomatically(false);
        }
        // ws.setBlockNetworkImage(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);              // 设置 缓存模式
        ws.setDomStorageEnabled(true);                          // 开启 DOM storage API 功能
        ws.setDatabaseEnabled(true);                            //开启 database storage API 功能
        String cacheDirPath = getFilesDir().getAbsolutePath() + AppConfig.APP_CACAHE_WEBVIEW;
        ws.setDatabasePath(cacheDirPath);                       // 设置数据库缓存路径
        ws.setAppCachePath(cacheDirPath);                       // 设置 Application Caches 缓存目录
        ws.setAppCacheEnabled(true);                            // 开启 Application Caches 功能
        ws.setSavePassword(true);
        ws.setSaveFormData(true);                               // 保存表单数据
        ws.setJavaScriptEnabled(true);

        ws.setGeolocationEnabled(true);                         // 启用地理定位
        ws.setGeolocationDatabasePath("/data/data/com.yey.kindergaten/webview/databases/"); // 设置定位的数据库路径
        ws.setSupportMultipleWindows(true);                     // 新加
        ws.setPluginState(PluginState.ON);
        setPageCacheCapacity(ws);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new JsInteration(), "control");
        String describle = null;
        webview.addJavascriptInterface(describle, "js_decscrible");
        String s = describle;
        webview.addJavascriptInterface(this, "MainActivity");
        xwebchromeclient = new myWebChromeClient();
        webview.setWebChromeClient(xwebchromeclient);
        webview.setWebViewClient(new myWebViewClient());
//        if (fromType!=null && fromType.equals(AppConstants.PARAM_PublicAccount)) {
            webview.setWebViewOnclickListener(webViewOnclickListener);
//        }
    }

    @OnClick({R.id.left_btn,R.id.right_btn,R.id.second_right_iv})
    public void onclickView(View view) {
        switch (view.getId()) {
        case R.id.left_btn:
            if (webview.canGoBack()) {
                webview.goBack();
                // 显示关闭页面
            } else {
                finish();
            }
            break;
        case R.id.right_btn:
            finish();
            break;
        case R.id.second_right_iv:
            loadUrl(url);
            break;
        default:
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        frameLayout.removeAllViews();
        webview.loadUrl("about:blank");
        webview.stopLoading();
        webview.setWebChromeClient(null);
        webview.setWebViewClient(null);
        webview.destroy();
        webview = null;
    }

    /**
     * 判断是否是全屏
     * @return
     */
    public boolean inCustomView() {
        return (myView != null);
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        xwebchromeclient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        try {
            webview.getClass().getMethod("onResume").invoke(webview, (Object[])null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        closeAudio();
    }

    /**
     *
     */
    private void closeAudio() {
        try {
            webview.getClass().getMethod("onPause").invoke(webview, (Object[])null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isVideo == true) {
            String callPause = "javascript:player_pause(" + vid + ")";
            webview.loadUrl(callPause);
            closeAudio();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        } else if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            webview.goBack(); // goBack()表示返回webView的上一页面
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (vid!=null) {
                  String callPause = "javascript:player_pause(" + vid + ")";
                  webview.loadUrl(callPause);
            }
            CommonBrowser.this.finish();
            return true;
         }
        return super.onKeyDown(keyCode, event);
    }

    // 以下代码是webview上传照片用的
    /**
     * 检查SD卡是否存在
     *
     * @return
     */
    public final boolean checkSDcard() {
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!flag) {
            Toast.makeText(this, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    String compressPath = "";
    protected final void selectImage() {
        if (!checkSDcard()) return;
        String[] selectPicTypeStr = { "手机拍照","相册浏览" };
        new AlertDialog.Builder(this)
            .setItems(selectPicTypeStr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        // 相机拍摄
                        case 0:
                            openCarcme();
                            break;
                        // 手机相册
                        case 1:
                            chosePic();
                            break;
                        default:
                            break;
                    }
                    compressPath = Environment.getExternalStorageDirectory().getPath() + "/fuiou_wmp/temp";
                    new File(compressPath).mkdirs();
                    compressPath = compressPath + File.separator + "compress.jpg";
                }
            }).show();
    }

    String imagePaths;
    Uri cameraUri;
    /**
     * 打开照相机
     */
    private void openCarcme() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagePaths = Environment.getExternalStorageDirectory().getPath() + "/fuiou_wmp/temp/" + (System.currentTimeMillis() + ".jpg");
        // 必须确保文件夹路径存在，否则拍照后无法完成回调
        File vFile = new File(imagePaths);
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }
        cameraUri = Uri.fromFile(vFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, REQ_CAMERA);
    }

    /**
     * 拍照结束后
     */
    private void afterOpenCamera() {
        File f = new File(imagePaths);
        addImageGallery(f);
        File newFile = FileUtils.compressFile(f.getPath(), compressPath);
    }

    /** 解决拍照后在相册中找不到的问题 */
    private void addImageGallery(File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 本地相册选择图片
     */
    private void chosePic() {
        FileUtils.delFile(compressPath);
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        String IMAGE_UNSPECIFIED = "image/*";
        innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
        Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        startActivityForResult(wrapperIntent, REQ_CHOOSE);
    }

    /**
     * 选择照片后结束
     *
     * @param data
     */
    private Uri afterChosePic(Intent data) {
        // 获取图片的路径：
        String[] proj = { MediaStore.Images.Media.DATA };
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
        if (cursor == null) {
            Toast.makeText(this, "上传的图片仅支持png或jpg格式",Toast.LENGTH_SHORT).show();
            return null;
        }
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        String path = cursor.getString(column_index);
        if (path != null && (path.endsWith(".png") || path.endsWith(".PNG") || path.endsWith(".jpg") || path.endsWith(".JPG"))) {
            File newFile = FileUtils.compressFile(path, compressPath);
            return Uri.fromFile(newFile);
        } else {
            Toast.makeText(this, "上传的图片仅支持png或jpg格式", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public class JsInteration {
        /**
         * 视频id的js函数
         * @param result
         */
        @JavascriptInterface
        public void ongetvid(String result){
            vid = result;
         }
    }

    /**
     * 这个函数是用来处理 当进行goBack的时候 使用前一个页面的缓存 避免每次都从新载入
     * 在2.3的时候 有 这个函数setPageCacheCapacity 可以用来设置。可惜后来没有了。是隐藏的了。但是可以通反射来调用到这个函数
     * @param webSettings webView的settings
     */
    protected void setPageCacheCapacity(WebSettings webSettings) {
        try {
            Class<?> c = Class.forName("android.webkit.WebSettingsClassic");

            Method tt = c.getMethod("setPageCacheCapacity", new Class[] { int.class });

            tt.invoke(webSettings, 5);

        } catch (ClassNotFoundException e) {
            System.out.println("No such class: " + e);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private String imgurl = "";

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "保存到手机") {
                    new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
                } else {
                    return false;
                }
                return true;
            }
        };
        if (v instanceof WebView) {
            WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (result != null) {
                int type = result.getType();
                if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    imgurl = result.getExtra();
                    menu.setHeaderTitle("提示");
                    menu.add(0, v.getId(), 0, "保存到手机").setOnMenuItemClickListener(handler);
                }
            }
        }
    }
    
    /***
     * 功能：用线程保存图片
     * 
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        String savepath = "";
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/DCIM/Camera");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + "/DCIM/Camera/" + new Date().getTime() + ext);
                savepath = file.getAbsolutePath();
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();
                
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // 刷新相册
            Uri localUri = Uri.fromFile(new File(savepath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
            showToast(result);
        }
    }

}
