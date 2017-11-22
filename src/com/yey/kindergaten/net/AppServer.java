package com.yey.kindergaten.net;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.async.HttpAsyncExecutor;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpClientException.ClientException;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpNetException.NetException;
import com.litesuits.http.exception.HttpServerException;
import com.litesuits.http.exception.HttpServerException.ServerException;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.response.Response;
import com.litesuits.http.response.handler.HttpExceptionHandler;
import com.litesuits.http.response.handler.HttpResponseHandler;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.bean.AccountBean;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.ClassPhoto;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.DiaryHomeInfo;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.GroupTwritte;
import com.yey.kindergaten.bean.GroupTwritte.comments;
import com.yey.kindergaten.bean.KindergartenInfo;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.MenuBean;
import com.yey.kindergaten.bean.MessageNews;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.MobanContentInfo;
import com.yey.kindergaten.bean.NotificationInfo;
import com.yey.kindergaten.bean.NotificationMobanInfo;
import com.yey.kindergaten.bean.OrderInfo;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.PhotoShow;
import com.yey.kindergaten.bean.PhotoViewJson;
import com.yey.kindergaten.bean.Product;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.PublicAccountMenu;
import com.yey.kindergaten.bean.RelationShipBean;
import com.yey.kindergaten.bean.SchedulesBean;
import com.yey.kindergaten.bean.SelfInfo;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.bean.TaskBean;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.bean.Twitter;
import com.yey.kindergaten.bean.TwitterSelf;
import com.yey.kindergaten.bean.WLImage;
import com.yey.kindergaten.bean.WxEntity;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.inter.OnAooRequestParentListener;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.Utils;
import com.yey.kindergaten.util.UtilsLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: zy
 * Date: 13-12-4
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class AppServer {

    public static LiteHttpClient liteclient;
    public static final String TAG_CODE = "code";
    public static final String TAG_INFO = "info";
    public static final String TAG_RESULT = "result";
    public static final String TAG_NEXTID = "nextid";

    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_FAILED = -1;
    public static final int REQUEST_ERROR = 1;
    public static final int REQUEST_NO_NETWORK = -2;
    public static final int REQUEST_LOGIN_ERROR_ACCOUNT = 1; // 账号不存在
    public static final int REQUEST_LOGIN_ERROR_POSSWORD = 2; // 密码不正确
    public static final int REQUEST_CLIENT_ERROR = 111; // 客户端异常
    public static final int REQUEST_NETWORK_ERROR = 112; // 无可用网络
    public static final int REQUEST_UNREACHABLE_ERROR = 113; // 服务器不可访问(或网络不稳定)
    public static final int REQUEST_NETWORKDISABLED_ERROR = 114; // 网络类型被设置禁用

    private final static String TAG = "AppServer";
    static final String SERVER_URL = "http://t.kmapp.zgyey.com/";
    static final String SERVER_GET_MAIN = SERVER_URL + "pub/getMainGateway";

    private interface OnSendRequestListener {
        public void onSendRequest(int code, String message, String result);
    }

    private interface OnSendRequestListenerFriend {
        public void onSendRequestfriend(int code, String message, String result, int nextid);
    }

    private static AppServer mInstance;
    public static AppServer getInstance() {
        if (mInstance == null) {
            mInstance = new AppServer();
        }
        if (liteclient == null) {
            liteclient = LiteHttpClient.newApacheHttpClient(AppContext.getInstance());
        }
        return mInstance;
    }

    private AccountInfo mAccountInfo;
    private AccountBean mAccountBean;

    public void setmAccountInfo(AccountInfo mAccountInfo) {
        this.mAccountInfo = mAccountInfo;
    }

    public AccountBean getmAccountBean() {
        return mAccountBean;
    }

    public void setmAccountBean(AccountBean mAccountBean) {
        this.mAccountBean = mAccountBean;
    }

    public AccountInfo getAccountInfo() {
        if (mAccountInfo == null) {
            int uid = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getInt(AppConstants.PARAM_UID, 0);
            UtilsLog.i(TAG, "getAccountInfo uid is o, read from sp, uid of sp is : " + uid);
            if (uid!=0) {
                try {
                    mAccountInfo = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(AccountInfo.class).where("uid", "=", uid));
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            if (mAccountInfo == null) {
                UtilsLog.i(TAG, " uid is o, new AccountInfo ");
                mAccountInfo = new AccountInfo();
            }
        }
        return mAccountInfo;
    }

//    public AccountBean getAccountBean() {
//        if (mAccountBean == null) {
//            int uid = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getInt(AppConstants.PARAM_UID, 0);
//            if (uid!=0) {
//                try {
//                    mAccountBean = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(AccountBean.class).where("uid", "=", uid));
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (mAccountBean == null) {
//                mAccountBean = new AccountBean();
//            }
//        }
//        return mAccountBean;
//    }

    /**
     * 发送Form请求
     *
     * @param url
     * @param listener
     */
    private void sendFormRequest(HashMap<String, String> params,String url, final OnSendRequestListener listener){
        StringBuffer paramsStr = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) { // 构建表单字段内容
            paramsStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String newparams = paramsStr.toString().substring(0, paramsStr.toString().length() - 1);
        System.out.println("newparams--" + newparams);
        System.out.println("url--" + url);
        JsonServer.getInstance().sendRequestForm(newparams, url, new OnRequestFinishedListener() {
            @Override
            public void onRequestFinished(String jsonStr) {
                int code = REQUEST_NO_NETWORK;
                String message = "当前网络不可用，请检查你的网络设置。";
                String result = null;
                try {
                    JSONObject jObj = new JSONObject(jsonStr);
                    code = Integer.valueOf(jObj.getString(TAG_CODE));
                    message = jObj.getString(TAG_INFO);
                    result = jObj.getString(TAG_RESULT);

                } catch (JSONException e) {
                    e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
                }
                if (listener != null) {
                    listener.onSendRequest(code, message, result);
                }
            }
        });
    }

    private void sendVolleyRequestStringFriend(HashMap<String, String> params, String url, final OnSendRequestListenerFriend listener){
        StringBuffer paramsStr = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) { // 构建表单字段内容
            paramsStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String newparams = paramsStr.toString().substring(0, paramsStr.toString().length() - 1);
        System.out.println("newparams--" + newparams);
        System.out.println("url--" + url);
        JsonServer.getInstance().sendRequestForm(newparams, url, new OnRequestFinishedListener() {
            @Override
            public void onRequestFinished(String jsonStr) {
                int code = REQUEST_NO_NETWORK;
                String message = "当前网络不可用，请检查你的网络设置。";
                int nextid = -1;
                String result = null;
                try {
                    JSONObject jObj = new JSONObject(jsonStr);
                    code = Integer.valueOf(jObj.getString(TAG_CODE));
                    message = jObj.getString(TAG_INFO);
                    result = jObj.getString(TAG_RESULT);
                    nextid =Integer.valueOf(jObj.getString(TAG_NEXTID));
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                if (listener != null) {
                    listener.onSendRequestfriend(code, message, result, nextid);
                }
            }
        });
    }

    /**
     * litehttp
     * @param map
     * @param url
     */
    public void sendVolleyRequestString(final HashMap<String, String> map, final String url, final OnSendRequestListener listener){
        final long start1 = System.currentTimeMillis();
        HttpAsyncExecutor asyncExcutor = HttpAsyncExecutor.newInstance(liteclient);
        Request req = new Request(url);
        System.out.println("url----" + url);
        req.setMethod(com.litesuits.http.request.param.HttpMethod.Post);
        LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) { // 构建表单字段内容
            pList.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        if (map!=null && map.containsKey("uid")) {
            UtilsLog.i(TAG, "NET: uid  :  " + map.get("uid") + "; url  :  " + url + pList.toString());
        }
        req.setHttpBody(new UrlEncodedFormBody(pList));
        asyncExcutor.execute(req, new HttpResponseHandler() {
            @Override
            protected void onFailure(com.litesuits.http.response.Response res, HttpException e) {
                final String ress = res.toString();
                new HttpExceptionHandler() {
                    @Override
                    protected void onClientException(HttpClientException e, ClientException type) {
                        if (listener != null) {
                            listener.onSendRequest(REQUEST_CLIENT_ERROR, e.toString() + "", ress);
                        }
                    }
                    @Override
                    protected void onNetException(HttpNetException e, NetException type) {
                        if (type == NetException.NetworkError) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_NETWORK_ERROR, e.toString() + "", ress);
                            }
                        } else if (type == NetException.UnReachable) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_UNREACHABLE_ERROR, e.toString() + "", ress);
                            }
                        } else if (type == NetException.NetworkDisabled) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_NETWORKDISABLED_ERROR, e.toString() + "", ress);
                            }
                        }
                    }
                    @Override
                    protected void onServerException(HttpServerException e, ServerException type, HttpStatus status) { }
                }.handleException(e);
            }

            @Override
            protected void onSuccess(com.litesuits.http.response.Response res, HttpStatus status, NameValuePair[] headers) {
                long end1 = System.currentTimeMillis();
                System.out.println("使用litehttp共用时间" + (end1 - start1) + "ms");
                int code = REQUEST_NO_NETWORK;
                String message = "请求服务失败";
                String result = null;
                try {
                    String resString = res.getString();
                    JSONObject jObj = new JSONObject(res.getString());
                    if (!jObj.isNull(TAG_CODE)) {
                        code = Integer.valueOf(jObj.getString(TAG_CODE));
                    }
                    if (!jObj.isNull(TAG_INFO)) {
                        message = jObj.getString(TAG_INFO);
                    }
                    if (!jObj.isNull(TAG_RESULT)) {
                        result = jObj.getString(TAG_RESULT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
                }
                if (listener != null) {
                    listener.onSendRequest(code, message, result);
                }
            }
        });
    }

    /**
     * 直接返回json不解析
     *
     * @param map
     * @param url
     */
    public void sendLiteHttpRequestString(final HashMap<String, String> map, final String url, final OnSendRequestListener listener){
        final long start1 = System.currentTimeMillis();
        HttpAsyncExecutor asyncExcutor = HttpAsyncExecutor.newInstance(liteclient);
        Request req = new Request(url);
        if (map!=null && map.containsKey("uid")) {
            UtilsLog.i(TAG, "NET: uid  :  " + map.get("uid") + "; url  :  " + url);
        }
        req.setMethod(com.litesuits.http.request.param.HttpMethod.Post);
        LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) { // 构建表单字段内容
            pList.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        System.out.println("url----" + url + "??" + pList.toString());
        req.setHttpBody(new UrlEncodedFormBody(pList));
        asyncExcutor.execute(req, new HttpResponseHandler() {
            @Override
            protected void onFailure(com.litesuits.http.response.Response res, HttpException e) {
                final String ress = res.toString();
                new HttpExceptionHandler() {
                    @Override
                    protected void onClientException(HttpClientException e, ClientException type) {
                        if (listener != null) {
                            listener.onSendRequest(REQUEST_CLIENT_ERROR, e + "", ress);
                        }
                    }
                    @Override
                    protected void onNetException(HttpNetException e, NetException type) {
                        if (type == NetException.NetworkError) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_NETWORK_ERROR, e + "", ress);
                            }
                        } else if (type == NetException.UnReachable) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_UNREACHABLE_ERROR, e + "", ress);
                            }
                        } else if (type == NetException.NetworkDisabled) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_NETWORKDISABLED_ERROR, e + "", ress);
                            }
                        }
                    }
                    @Override
                    protected void onServerException(HttpServerException e, ServerException type, HttpStatus status) { }
                }.handleException(e);
            }

            @Override
            protected void onSuccess(com.litesuits.http.response.Response res, HttpStatus status, NameValuePair[] headers) {
                long end1 = System.currentTimeMillis();
                System.out.println("使用litehttp共用时间" + (end1 - start1) + "ms");
                int code = REQUEST_SUCCESS;
                String message = "";
                if (listener != null) {
                    listener.onSendRequest(code, message, res.getString());
                }
            }
        });
    }

    public void sendVolleyImageRequestString(final RequestParams params, final String url, final OnSendRequestListener listener){
        HttpUtils http = new HttpUtils();
        http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() { }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                if (isUploading) {
//                  FriendsterGridviewAdapter.getCirclelist().get(i).setVisibility(View.VISIBLE);
                    DecimalFormat df = new DecimalFormat("##");
                    int value = Integer.parseInt(df.format((double)current / (double)total * 100));
                    System.out.print("==================>" + value + "");
                    System.out.print("==================>" + total + "");
                    System.out.print("==================>" + current + "");
//                  FriendsterGridviewAdapter.getCirclelist().get(i).setProgressNotInUiThread(value);
                } else {
                    UtilsLog.i(TAG, "sendVolleyImageRequestString is not Uploading");
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                long end1 = System.currentTimeMillis();
                int code = REQUEST_NO_NETWORK;
                String message = "请求服务失败";
                String result = null;
                try {
                    JSONObject jObj = new JSONObject(arg0.result);
                    if (!jObj.isNull(TAG_CODE)) {
                        code = Integer.valueOf(jObj.getString(TAG_CODE));
                    }
                    if (!jObj.isNull(TAG_INFO)) {
                        message = jObj.getString(TAG_INFO);
                    }
                    if (!jObj.isNull(TAG_RESULT)) {
                        result = jObj.getString(TAG_RESULT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
                }
                if (listener != null) {
                    listener.onSendRequest(code, message, result);
                }
            }

            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                final String ress = s.toString();
                new HttpExceptionHandler() {
                    @Override
                    protected void onClientException(HttpClientException e, ClientException type) {
                        if (listener != null) {
                            listener.onSendRequest(REQUEST_CLIENT_ERROR, e + "", ress);
                        }
                    }
                    @Override
                    protected void onNetException(HttpNetException e, NetException type) {
                        if (type == NetException.NetworkError) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_NETWORK_ERROR, e + "", ress);
                            }
                        } else if (type == NetException.UnReachable) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_UNREACHABLE_ERROR, e + "", ress);
                            }
                        } else if (type == NetException.NetworkDisabled) {
                            if (listener != null) {
                                listener.onSendRequest(REQUEST_NETWORKDISABLED_ERROR, e + "", ress);
                            }
                        }
                    }
                    @Override
                    protected void onServerException(HttpServerException e, ServerException type, HttpStatus status) { }
                }.handleException(e);
            }
        });
    }

    /**
     * 登录
     *
     * @param username
     * @param pwd
     * @param listener
     *   REQUEST_LOGIN_ERROR_ACCOUNT(账号不存在)
     *   REQUEST_LOGIN_ERROR_POSSWORD(密码不正确)
     *   REQUEST_SUCCESS(登录成功)
     *   REQUEST_FAILED(登录失败)
     *
     */
    public void login(final Context context, final String username, final String pwd, final String clientId, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_ACCOUNT, username);
        params.put(AppConstants.PARAM_PASSWORD, AppUtils.getShaMD5(pwd));
        params.put(AppConstants.PARAM_CLIENTID, clientId);
        params.put(AppConstants.PARAM_APPVER, AppUtils.getVersionName());
        Random r = new Random();
        int random = r.nextInt(100);
        params.put("random", random + "");
        params.put(AppConstants.PARAM_SYSVER, "1"); // 系统平台android:1，ios:2
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);

        StringBuilder sb = new StringBuilder();
        sb.append(username).append(AppUtils.getShaMD5(pwd)).append(clientId).append(timestamp);
        String strbefore = sb.toString();
        String strlater = AppUtils.Md5(sb.toString());
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String s = AppContext.getInstance().getMainGateWay();
        String url = AppContext.getInstance().getMainGateWay() + "/main/login";
//      String url = "http://192.168.0.138:555/main/login";
        UtilsLog.i(TAG, "print login params, username = " + username + " ;  password = " + AppUtils.getShaMD5(pwd)
                + " ;  clientId = " + clientId
                + " ;  appver = " + AppUtils.getVersionName()
                + " ;  client = " + "1"
                + " ;  [Constants-imestamp] is " + AppConstants.PARAM_TIMESTAMP
                + " ;  timestamp = " + URL.urlkey);
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "login interface is success");
                    Gson gson = new Gson();
                    Object objBean = new Object();
                    obj = gson.fromJson(result, AccountInfo.class);
                    objBean = gson.fromJson(result, AccountBean.class);
                    mAccountInfo = (AccountInfo)obj;
                    // 修复返回obj为null时的报错
                    if (mAccountInfo == null) {
                        if (listener != null) {
                            listener.onAppRequest(-1, message, obj);
                        }
                        return;
                    }
//                  mAccountBean = (AccountBean)objBean;
                    UtilsLog.i(TAG, "login interface is success,save obj to accountifo ok");
                    List<RelationShipBean>list = new ArrayList<RelationShipBean>();
                    List<AccountInfo.RelationShip> accountlist = mAccountInfo.getRelationships();
//                  List<AccountBean.RelationShip> accountlist1 = mAccountBean.getRelationships();

                    // 加号菜单项
                    List<MenuBean> menuBeans = new ArrayList<MenuBean>();
                    List<AccountInfo.Menu> menus = mAccountInfo.getMenus();

                    AppContext.getInstance().setAccountInfo(mAccountInfo);
                    // mAccountInfo.setAccount(username);
                    mAccountInfo.setPassword(pwd);
                    // mAccountBean.setPassword(pwd);
                    UtilsLog.i(TAG, "start to save accountinfo to sharedPreferences ,and uid is: " + mAccountInfo.getUid() + "");
                    SharedPreferencesHelper.getInstance(context).setInt(AppConstants.PARAM_UID, mAccountInfo.getUid());
                    SharedPreferencesHelper.getInstance(context).setInt(AppConfig.ROLE, mAccountInfo.getRole());
                    SharedPreferencesHelper.getInstance(context).setInt(AppConfig.KID, mAccountInfo.getKid());
                    SharedPreferencesHelper.getInstance(context).setInt(AppConfig.NUM, mAccountInfo.getNum());
                    SharedPreferencesHelper.getInstance(context).setInt(AppConstants.PARAM_CID, mAccountInfo.getCid());
                    SharedPreferencesHelper.getInstance(context).setInt(AppConstants.PREF_ISLOGIN, 1);
                    // SharedPreferencesHelper.getInstance(context).setBoolean(AppConstants.FLAG_FIRST_LOGINSUCCESS, false);
                    DbHelper.updateAccountInfo(mAccountInfo);
                    UtilsLog.i(TAG, "login interface is success,uodateaccountinfo ok");
                    AppContext.getInstance().setAccountInfo(mAccountInfo);
                    if (menus!=null && menus.size()!=0) {
                        for (int i = 0; i < menus.size(); i++) {
                            MenuBean menuBean = new MenuBean();
                            menuBean.setType(menus.get(i).getType());
                            menuBean.setTitle(menus.get(i).getTitle());
                            menuBean.setUrl(menus.get(i).getUrl());
                            menuBeans.add(menuBean);
                        }
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(MenuBean.class);
                            UtilsLog.i(TAG, "login interface is success,deleteAll MenuBean ok");
                            DbHelper.getDB(AppContext.getInstance()).saveAll(menuBeans);
                            UtilsLog.i(TAG, "login interface is success,saveAll MenuBean ok");
                        } catch (DbException e) {
                            e.printStackTrace();
                            UtilsLog.i(TAG, "login interface is success,deleteAll or saveAll MenuBean DbException");
                        }
                    }
                    if (accountlist!=null && accountlist.size()!=0) {
                        for (int i = 0; i < accountlist.size(); i++) {
                            RelationShipBean bean = new RelationShipBean();
                            bean.setDefaultrelation(mAccountInfo.getDefaultrelation());
                            bean.setRelationship(accountlist.get(i).getRelationship());
                            bean.setHxregtag(accountlist.get(i).getHxregtag());
                            list.add(bean);
                        }
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
                            UtilsLog.i(TAG, "login interface is success,deleteAll RelationShipBean ok");
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                            UtilsLog.i(TAG, "login interface is success,saveAll RelationShipBean ok");
                        } catch (DbException e) {
                            UtilsLog.i(TAG, "login interface is success,deleteAll or saveAll RelationShipBean DbException");
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            UtilsLog.i(TAG, "mAccountInfo.getRelationships() is null value,start to deleteAll RelationShipBean");
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
                        } catch (DbException e) {
                            UtilsLog.i(TAG, "deleteAll RelationShipBean fail DbException");
                            e.printStackTrace();
                        }
                    }
                    if ((accountlist == null || accountlist.size() == 0) && mAccountInfo.getRole()!=2) {
                        RelationShipBean bean = new RelationShipBean();
                        bean.setDefaultrelation(mAccountInfo.getDefaultrelation());
                        bean.setRelationship(0);
                        bean.setHxregtag(0);
                        list.add(bean);
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
                            UtilsLog.i(TAG, "role == 2 ,deleteAll RelationShipBean ok");
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                            UtilsLog.i(TAG, "role == 2 ,saveAll RelationShipBean ok");
                        } catch (DbException e) {
                            e.printStackTrace();
                            UtilsLog.i(TAG, "deleteAll or saveall RelationShipBean fail DbException");
                        }
                    }

                    // 记住登录历史记录
                    String accouts = username.trim() + "||" + (pwd + "zgyey") + "||" + mAccountInfo.getAvatar();
                    SharedPreferences settings = context.getSharedPreferences(SharedPreferencesHelper.PREF_LOGIN_FILE,  Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(username.trim(), accouts);
                    editor.commit();
                    UtilsLog.i(TAG, "saveAll login status sharedpreferences ok");
                    // 记住最后一次登录的配置文件
                    SharedPreferences set = context.getSharedPreferences(AppConfig.LOGIN_DEFALUTE_VALUE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editors = set.edit();
                    editors.putString(AppConfig.LOGIN_DEFAULTE_ACCOUNT, username.trim());
                    editors.putString(AppConfig.LOGIN_DEFAULTE_PASSWORD, pwd + "zgyey");
                    editors.putString(AppConfig.LOGIN_DEFAULTE_AVATER, mAccountInfo.getAvatar());
                    editors.commit();

                } else {
                    obj = result;
                }
                setmAccountInfo(mAccountInfo);
                SharedPreferencesHelper.getInstance(context).setInt(AppConstants.PARAM_UID, mAccountInfo.getUid());
                if (listener != null) {
                    UtilsLog.i(TAG, "listener is not null,start to onAppRequest code: " + code + "");
                    listener.onAppRequest(code, message, obj);
                } else {
                    UtilsLog.i(TAG, "sorry, listener is null");
                }
            }
        });

    }

    /**
     * 获取验证码
     *
     * @param phone 手机号码
     * @param listener
     */
    public void getPhoneCode(String phone, String type, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String,String>();
        params.put(AppConstants.PARAM_PHONE, phone);
        params.put("type", type);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(phone + timestamp));
        String url = URL.PWSENDPHONECODE;
//      String url = "http://192.168.0.138:555/main/sendPhoneCode";
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                String phone_code = null;
                if (code == REQUEST_SUCCESS) {
                    try {
                        JSONObject json = new JSONObject(result);
                        phone_code=json.getString("phonecode");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    phone_code = " ";
                }
                if (listener!=null) {
                    listener.onAppRequest(code,message,phone_code);
                }
            }
        });
    }

    /**
     * register(注册)
     *
     * @param password
     * @param role 角色  0:园长 1:老师 2:家长 3:公众号 4:服务
     * @param listener
     *void
     * @exception
     * @since  1.0.0
     */
    public void register(final String account, final String password, final int role, String code, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_ACCOUNT, account);
        params.put(AppConstants.PARAM_PASSWORD, AppUtils.getShaMD5(password));
        params.put("role", role + "");
        params.put("code", code);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(account+AppUtils.getShaMD5(password)+role+timestamp));
        sendVolleyRequestString(params, URL.REGISTER, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    obj = gson.fromJson(result, AccountInfo.class);
                    mAccountInfo = (AccountInfo)obj;
                    mAccountInfo.setAccount(account);
                    SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt(AppConstants.ROLE, role);
                    SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt(AppConstants.PARAM_UID, mAccountInfo.getUid());
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * getServiceMenu(获取服务菜单)
     *
     * @param uid
     * @param role
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void getServiceMenu(final int uid,final int role, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.ROLE, role + "");
        params.put(AppConstants.CLIENT, 1 + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        params.put(AppConstants.PARAM_KID, AppServer.getInstance().getAccountInfo().getKid() + "");
        params.put(AppConstants.PARAM_APPVER, AppUtils.getVersionName());
        params.put(AppConstants.PARAM_SYSVER, "1"); // 系统平台android:1，ios:2
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(role).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getMaingw() + URL.GETSERVICES, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        Services[] Services = gson.fromJson(result, Services[].class);
                        if (Services.length > 0) {
                            List<Services> tvlist = Arrays.asList(Services);
                            List<Services> newserviceList = new ArrayList<Services>(tvlist);
                            obj = newserviceList;
                        } else {
                            obj = new ArrayList<Services>();
                        }
                    } else if (json.isJsonObject()) {
                        Services gi = gson.fromJson(result, Services.class);
                        if (gi!=null) {
                            List<Services> list = new ArrayList<Services>();
                            list.add(gi);
                            obj = list;
                        } else {
                            obj = new ArrayList<Services>();
                        }
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "getServiceMenu json.isJsonNull");
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 搜索用户
     *
     * @param uid
     * @param value
     * @param vtype 0:用户   用户名:nickname, 1:用户uid, , 2:公众号account, 公众号name 3:公众号publicid, 
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void findUser(int uid, String value, final int vtype, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_VTYPE, vtype + "");
        params.put(AppConstants.PARAM_VALUE, value);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(vtype).append(value).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getContactgw() + URL.FINDUSER, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = result;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    if (vtype > 1) {
                        PublicAccount[] puacs = gson.fromJson(result, PublicAccount[].class);
                        if (puacs.length > 0) {
                            List<PublicAccount> puaclist = Arrays.asList(puacs);
                            List<PublicAccount> newpuaclist = new ArrayList<PublicAccount>(puaclist);
                            obj = newpuaclist;
                        } else {
                            obj = new ArrayList<PublicAccount>();
                        }
                    } else {
                        Friend[] friend = gson.fromJson(result, Friend[].class);
                        if (friend.length > 0) {
                            List<Friend> friendlist = Arrays.asList(friend);
                            List<Friend> newfriendlist = new ArrayList<Friend>(friendlist);
                            obj = newfriendlist;
                        } else {
                            obj = new ArrayList<Friend>();
                        }
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * addFriends(发送好友请求)
     *
     * @param uid
     * @param friendid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void addFriend( int uid,int friendid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_FRIEND, friendid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(friendid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.ADDFRIEND, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    Friend[] selfInfo = gson.fromJson(result, Friend[].class);
                    obj = selfInfo;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * handleNewFriend(处理好友请求)
     *
     * @param uid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void handleNewFriend(int uid, int reqid, int action, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_REQID, reqid + "");
        params.put(AppConstants.PARAM_ACTION, action + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(reqid).append(action).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.HANDLEFRIEND, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    Friend[] selfInfo = gson.fromJson(result, Friend[].class);
                    obj = selfInfo;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * modifySelfInfo(修改个人资料)
     *
     * @param uid  &avatar=&nickname=你好&gender=男&location=233&realname=&telephone=&timestamp=&key=34253ydfg675hr56
     * @param listener
     * void
     * @exception
     * @since  1.0.0男  ：2  女:3
     */
    public void modifySelfInfo( int uid, String avatar, String nickname, String gender, String location, String realname, String telephone, String birthday, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("avatar", avatar == null ? "" : avatar);
        params.put(AppConstants.PARAM_NICKNAME, nickname == null ? "" : nickname);
        params.put(AppConstants.PARAM_GENDER, gender == null ? "" : gender);
        params.put(AppConstants.PARAM_LOCATION, location == null ? "" : location);
        params.put("realname", realname == null ? "" : realname);
        params.put("telephone", telephone == null ? "" : telephone);
        params.put("birthday", birthday == null ? "" : birthday);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
//      sb.append(uid).append(avatar == null ? "" : avatar).append(nickname).append(gender).append(location==null?"":location).append(realname==null?"":realname).append(birthday==null?"":birthday).append(telephone==null?"":telephone).append(timestamp);
        sb.append(uid).append(gender).append(realname == null ? "" : realname).append(birthday == null ? "" : birthday).append(telephone == null ? "" : telephone).append(timestamp);
        System.out.println("sbsb---" + sb.toString());
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params,info.getMaingw() + URL.MODIFYSELFINFO, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    obj = code;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * modifyPassword(修改密码)
     *
     * @param uid
     * @param oldpw
     * @param newpw
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void modifyPassword(int uid, String oldpw, String newpw, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_OLDPW, AppUtils.getShaMD5(oldpw));
        params.put(AppConstants.PARAM_NEWPW, AppUtils.getShaMD5(newpw));
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(AppUtils.getShaMD5(oldpw)).append(AppUtils.getShaMD5(newpw)).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getMaingw() + URL.MODIFYPASSWORD, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    obj = new Object();
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * unbindPhone(绑定手机)
     *
     * @param uid
     * @param phone
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void bindPhone(int uid, String phone, String phonecode, final OnAppRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_PHONE, phone);
        params.put(AppConstants.PARAM_PHONECODE, phonecode);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(phone).append(phonecode).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params,info.getMaingw() + URL.BINDPHONE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    SelfInfo[] selfInfo = gson.fromJson(result, SelfInfo[].class);
                    obj = selfInfo;
                    if (selfInfo.length > 0) {
                        List<SelfInfo> tvlist = Arrays.asList(selfInfo);
                        List<SelfInfo> newserviceList = new ArrayList<SelfInfo>(tvlist);
                        obj = newserviceList;
                    } else {
                        obj = new ArrayList<Services>();
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * viewInfo(查看资料卡)
     *
     * @param uid
     * @param role
     * @param listener
     *void
     * @exception
     * @since  1.0.0
     */
    public void viewInfo(String uid, String tuid, String tpublicid, final int role, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid);
        params.put(AppConstants.PARAM_TUID, tuid);
        params.put(AppConstants.PARAM_TPUBLICID, tpublicid);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        Random r = new Random();
        int random = r.nextInt(100);
        params.put("random", random + "");
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put(AppConstants.PARAM_KID, info.getKid() + "");
        params.put(AppConstants.ROLE,  info.getRole() + "");
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(tuid).append(tpublicid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
//      String url = "http://192.168.0.138:555/" + URL.VIEWINFO;
        String url = info.getContactgw() + URL.VIEWINFO;
        sendVolleyRequestString(params,url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    if (role == 3) {
                        PublicAccount publicAccount = gson.fromJson(result, PublicAccount.class);
                        if (publicAccount == null) {
                            publicAccount = new PublicAccount();
                        }
                        obj = publicAccount;
                    } else {
                        SelfInfo selfInfo = gson.fromJson(result, SelfInfo.class);
                        if (selfInfo == null) {
                            selfInfo = new SelfInfo();
                        }
                        obj = selfInfo;
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 更新订阅公众号
     *
     * bookPublicAccount
     * @param uid
     * @param subscription 0 取消订阅  1 订阅
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void bookPublicAccount(int uid, final int publicid, final int subscription, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_PUBLICID, publicid + "");
        params.put(AppConstants.PARAM_SUBSCRIPTION, subscription + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(publicid).append(subscription).append(timestamp);
        System.out.println("sb--" + sb);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params,info.getMsggw() + URL.BOOKPUBLICACCOUNT, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    obj = code;
                    if (subscription == 0) {
                        DbHelper.deletePublicAccount(publicid);
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取某人的说说
     *
     * @param uid
     * @param tuid
     * @param fetchnum
     * @param nextid
     * @param listener
     */
    public void getTwittersByUid(int uid, int tuid, int fetchnum, int nextid, final OnAppRequestListenerFriend listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_TUID, tuid + "");
        params.put(AppConstants.PARAM_FETCHNUM, fetchnum + "");
        params.put(AppConstants.PARAM_NEXTID, nextid + "");
        String timetamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timetamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(tuid).append(fetchnum).append(nextid).append(timetamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params, URL.TWITTERSBYUID, new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    TwitterSelf[] tw = gson.fromJson(result, TwitterSelf[].class);
                    if (tw.length > 0) {
                        List<TwitterSelf> twlist = Arrays.asList(tw);
                        List<TwitterSelf> newtwList = new ArrayList<TwitterSelf>(twlist);
                        obj = newtwList;
                    } else {
                        obj = new ArrayList<TwitterSelf>();
                    }
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, nextid);
                }
            }
        });
    }

    /**
     * 发说说
     *
     * sendTwitter
     * @param uid
     * @param content
     * @param pri 可见性
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void sendTwitter( int uid, String content, String imgs, int pri, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_CONTENT, content);
        params.put(AppConstants.PARAM_IMGS, imgs);
        params.put(AppConstants.PARAM_PRIVATE, pri + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(content).append(imgs).append(pri).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params,URL.SENDTWITTER, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    Twitter twitters = gson.fromJson(result, Twitter.class);
                    obj = twitters;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     *
     * @param uid 用户id
     * @param action 0新增 1更新 2删除
     * @param i  日记服务端id
     * @param day    日期
     * @param time   当前时间
     * @param theme   主题
     * @param note    备注
     * @param remind  提醒方式
     * @param people   参与者
     */
    public void UploadSchedule(int uid, int action, int i, String day, String time, String theme, String note,
            int remind, String people, String uids, String realnames, final OnAppRequestListener listener) {
        HashMap<String, String>params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("action", action + "");
        params.put("sheid", i + "");
        params.put("day", day);
        params.put("time", time);
        params.put("theme", theme);
        params.put("note", note);
        params.put("remind", remind + "");
        params.put("people", people);
        params.put("uids", people);
        if (realnames!=null) {
            params.put("realnames", realnames);
        } else {
            params.put("realnames", " ");
        }
        String timestamp = URL.urlkey;
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(action).append(i).append(day).append(time).append(theme).append(note).append(remind).append(people).
                append(people).append(realnames).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params,info.getSchedulegw() + URL.UPLOADSEHEDULE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    SchedulesBean schedules = gson.fromJson(result, SchedulesBean.class);
                    if (schedules!=null) {
                        obj = schedules.getSheid();
                    } else {
                        obj = code;
                        UtilsLog.i(TAG, "UploadSchedule, schedules is null");
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取最新消息
     *
     * getNewMessage
     * @param uid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void getNewMessage(int uid, int relationship, final OnAppRequestListener listener){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_SYSOP, info.getSysop() == null ? "" : info.getSysop());
        params.put(AppConstants.PARAM_RELATIONSHIP, relationship + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String url = AppServer.getInstance().getAccountInfo().getMsggw() + URL.NEWMESSAGE;
        UtilsLog.i(TAG, "print getNewMessage params, uid = " + uid + " ;  relation = " + relationship
                + " ;  [Constants-imestamp] is " + AppConstants.PARAM_TIMESTAMP
                + " ;  timestamp = " + timestamp
                + " ;  [Constants-sysop] = " +  AppConstants.PARAM_SYSOP
                + " ;  sysop = " +  info.getSysop() );
        if (uid == 0) {
            UtilsLog.i(TAG, "appcontext uid is : " + AppContext.getInstance().getAccountInfo().getUid());
        }
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    MessageNews news = gson.fromJson(result, MessageNews.class);
                    obj = news;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取最新消息
     *
     * getNewMessage
     * @param uid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void getConversationMessages(int uid, int relationship, final OnAppRequestListener listener) {
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_SYSOP, info.getSysop() == null ? "" : info.getSysop());
        params.put(AppConstants.PARAM_RELATIONSHIP, relationship + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String url = AppServer.getInstance().getAccountInfo().getMsggw() + URL.CONVERSORMESSAGE;
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    MessageNews news = gson.fromJson(result, MessageNews.class);
                    obj = news;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 删除联系人
     *
     * getNewMessage
     * @param uid
     * @param targetid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void deletContactPeople( int uid, int targetid ,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_FUID, targetid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(targetid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params,URL.DELETCONTACTPEOPLE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "deletContactPeople success");
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 自定义菜单
     *
     * getPublicAccountMenu
     * @param publicid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void getPublicAccountMenu(String publicid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_PUBLICID, publicid);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(publicid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.PUBLICACCOUNT_MENUS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    PublicAccountMenu[] menu = gson.fromJson(result, PublicAccountMenu[].class);
                    List<PublicAccountMenu> menuslist =  Arrays.asList(menu);
                    List<PublicAccountMenu> newMenuslist = new ArrayList<PublicAccountMenu>(menuslist);
                    obj = newMenuslist;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取日程
     *
     * @param i
     * @param listener
     */
    public void getSchduleInfo(int i,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, i + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(i).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params,info.getSchedulegw()+URL.GETSCHEDULEINFO, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement jsonele = (JsonElement) parser.parse(result);
                    List<SchedulesBean>listbean = new ArrayList<SchedulesBean>();
                    SchedulesBean[] bean = null;
                    SchedulesBean beans = null;
                    if (jsonele.isJsonArray()) {
                        bean = gson.fromJson(result, SchedulesBean[].class);
                    } else {
                        beans = gson.fromJson(result, SchedulesBean.class);
                        listbean.add(beans);
                        final int size = listbean.size();
                        bean= (SchedulesBean[])listbean.toArray(new SchedulesBean[size]);
                    }
                    obj = bean;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 发表评论
     *
     * setcommment
     * @author cm_pc2
     * uid = 653548&twrid=1&content=&timestamp=&key=34253ydfg675hr56
     */
    public void sentCommment(int uid, int touid, int twrid, String content, final OnAppRequestListener listener){
        HashMap<String, String>  params =new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_TOUID, touid + "");
        params.put(AppConstants.PARAM_TWRID, twrid + "");
        params.put(AppConstants.PARAM_CONTENT, content);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(touid).append(twrid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.SENTCOMMENT, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    comments comment = gson.fromJson(result, comments.class);
                    obj = comment;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    public void sentChat(int uid, int to, int contenttype, String content, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("to", to + "");
        params.put(AppConstants.PARAM_CONTENTTYPE, contenttype + "");
        params.put(AppConstants.PARAM_CONTENT, content);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(to).append(contenttype).append(content).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getMsggw() + URL.SENDCHAT, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(result);
                    int pmid = element.getAsJsonObject().get("pmid").getAsInt();
                    obj = pmid;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 删除个人说说
     *
     * @param uid
     * @param twrid
     * @param listener
     */
    public void delTwitter(int uid,int twrid,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_TWRID, twrid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(twrid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.DELTWITTER, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, result);
                }
            }
        });
    }

    /**
     * 上传头像
     *
     * @param uid
     * @param avatar
     * @param listener
     */
    public void UploadAvatar(int uid,String avatar,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID,uid + "");
        params.put(AppConstants.AVATAR, avatar + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(avatar).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getMaingw() + URL.UPAVATAR, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, result);
                }
            }
        });
    }

    /**
     * 检测是否开通短信服务
     *
     * @param uid
     * @param listener
     */
    public void checkOpenSMS(int uid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID,uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.CHECKOPENSMS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    NotificationInfo info = gson.fromJson(result, NotificationInfo.class);
                    obj = info;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 上传头像
     *
     * @param uid
     * @param listener
     */
    public void GetParentByCid(int uid, int cid, final OnAooRequestParentListener listener) {
        HashMap<String, String> params =new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID,uid + "");
        params.put(AppConstants.PARAM_CID, cid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(cid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getContactgw() + URL.GETPARENTBYCID, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                Object obj2 = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement jsonele = parser.parse(result);
                    List<Children>listbean = new ArrayList<Children>();
                    List<Parent>listbeanP = new ArrayList<Parent>();
                    Children[] bean = null;
                    Parent[]beanP = null;
                    Children beans = null;
                    Parent pBean = null;
                    if (jsonele.isJsonArray()) {
                        bean = gson.fromJson(result, Children[].class);
                        beanP = gson.fromJson(result, Parent[].class);
                    } else {
                        beans = gson.fromJson(result, Children.class);
                        pBean = gson.fromJson(result, Parent.class);
                        listbean.add(beans);
                        final int size = listbean.size();
                        bean = listbean.toArray(new Children[size]);
                        listbean.add(beans);
                        beanP = listbeanP.toArray(new Parent[size]);
                    }
                    obj = bean;
                    obj2 = beanP;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj,obj2);
                }
            }
        });
    }

    /**
     * 发送通知接口
     *
     * @param uid
     * @param when
     * @param people
     * @param content
     */
    public void SendNoitification(int uid, String when, String people, String content, final OnAppRequestListener  listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("when", when);
        params.put("people", people);
        params.put("content", content);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(when).append(people).append(content).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.SENDNOTIFICATION, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(result);
                    int pmid = element.getAsJsonObject().get("notid").getAsInt();
                    obj = pmid;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取模板类型
     *
     * @param uid
     * @param listener
     */
    public void GetTemplateTypes(int uid, final OnAppRequestListener listener){
        HashMap<String, String>params = new HashMap<String, String>();
        // params.put(AppConstants.PARAM_UID,uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.GETTEMPLATETYPES, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    NotificationMobanInfo[] info = gson.fromJson(result, NotificationMobanInfo[].class);
                    obj = info;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    public void getTemplatesByType(int uid, int type, int fetchnum, int nextid, final OnAppRequestListenerFriend listener){
        HashMap<String ,String>params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("type", type + "");
        params.put("fetchnum", fetchnum + "");
        params.put("nextid", nextid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(type).append(fetchnum).append(nextid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params, URL.GETTEMPLATEBYTYPE, new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj = null;
                int pmid = 0;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    MobanContentInfo[] info = gson.fromJson(result, MobanContentInfo[].class);
                    obj = info;
                    pmid = nextid;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, pmid);
                }
            }
        });
    }

    /**
     * 获取任务
     *
     * @param uid
     * @param role
     * @param listener
     */
    public void GetTasks(int uid, int role, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.ROLE, role + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(role).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.GETTASKS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = new Object();
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement jsonresult = (JsonElement ) parser.parse(result);
                    if (jsonresult.isJsonArray()) {
                        TaskBean[] tasks = gson.fromJson(result, TaskBean[].class);
                        List<TaskBean> datalist = Arrays.asList(tasks);
                        obj = datalist;
                    } else if (jsonresult.isJsonObject()) {
                        TaskBean task = gson.fromJson(result, TaskBean.class);
                        obj = task;
                    } else if (jsonresult.isJsonNull()) {
                        obj = new Object();
                    }
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 新建地址本
     *
     * @param uid
     * @param listener
     */
    public void SaveAddress(int uid, String receiver, String address, String phone, String code, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.RECEIVRE, receiver);
        params.put(AppConstants.ADDRESS, address);
        params.put(AppConstants.PHONE, phone);
        params.put(AppConstants.CODE, code);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(receiver).append(address).append(phone).append(code).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.SAVEADDRESS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    obj = result;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取所有的地址本信息
     *
     * @param uid
     * @param listener
     */
    public void GetAllAddress(int uid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.GETALLADDRESS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    AddressBookBean []addressBooks = gson.fromJson(result, AddressBookBean[].class);
                    List<AddressBookBean> list = Arrays.asList(addressBooks);
                    obj = list;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 更新消息状态
     *
     * @param pmids
     * @pmtype 消息类型  0是个人，1是公众号 2是系统 
     * @param listener
     */
    public void updateMessageStatus(String pmids, int uid, int pmtype, int relationship, final OnAppRequestListener listener){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_PMIDS, pmids);
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_PMTYPE, pmtype + "");
        params.put(AppConstants.PARAM_RELATIONSHIP, relationship + "");
        params.put(AppConstants.PARAM_SYSOP, info.getSysop() == null ? "" : info.getSysop());
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(pmids).append(pmtype).append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, info.getMsggw() + URL.UPDATEMESSAGE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (listener!=null) {
                    listener.onAppRequest(code, message, null);
                }
            }
        });
    }

    /**
     * 获取公众号历史消息
     *
     * getNewMessage
     * @param uid
     * @param listener
     * void
     */
    public void getPublicHistoryMessage(int uid, int publicid, int fetchnum, int nextid, final OnAppRequestListenerFriend listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_PUBLICID, publicid + "");
        params.put(AppConstants.PARAM_FETCHNUM, fetchnum + "");
        params.put(AppConstants.PARAM_NEXTID, nextid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(publicid).append(fetchnum).append(nextid).append(timestamp);
        System.out.println("加密前:" + sb.toString());
        System.out.println("加密后:" + AppUtils.Md5(sb.toString()));
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put(AppConstants.PARAM_KID,  info.getKid() + "");
        sendVolleyRequestStringFriend(params, info.getMsggw() + URL.GETPUBLICHISTORYMESSAGE, new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object  obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    MessagePublicAccount[] pa=gson.fromJson(result, MessagePublicAccount[].class);
                    List<MessagePublicAccount> list = Arrays.asList(pa);
                    List<MessagePublicAccount> newlist = new ArrayList<MessagePublicAccount>(list);
                    obj = newlist;
                } else {
                    obj = message;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, nextid);
                }
            }
        });
    }

    /**
     * 获取公众号最近几条消息
     *
     * getPublicLateMessage
     * @param uid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void getPublicLateMessage( int uid, int publicid, int typeid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_PUBLICID, publicid + "");
        params.put(AppConstants.PARAM_TYPEID, typeid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(publicid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params,info.getMsggw()+URL.GETPMLATEMESSAGE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson =new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement jsonElement = parser.parse(result);
                    if (jsonElement.isJsonArray() && !jsonElement.isJsonNull()) {
                        MessagePublicAccount[] pa = gson.fromJson(result, MessagePublicAccount[].class);
                        List<MessagePublicAccount> list = Arrays.asList(pa);
                        List<MessagePublicAccount> newlist = new ArrayList<MessagePublicAccount>(list);
                        obj = newlist;
                    } else if (jsonElement.isJsonObject() && !jsonElement.isJsonNull()) {
                        MessagePublicAccount pa = gson.fromJson(result, MessagePublicAccount.class);
                        List<MessagePublicAccount> newlist = new ArrayList<MessagePublicAccount>();
                        newlist.add(pa);
                        obj = newlist;
                    }
                } else {
                    obj = new Object();
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 验证绑定手机
     *
     * checkPhone
     * @param  phone
     * @param  listener
     */
    public void checkPhone(String phone, final OnAppRequestListener listener){
        HashMap<String, String>  params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_PHONE, phone);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(phone).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.PWCHECKPHONE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "checkPhone success");
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, result);
                }
            }
        });
    }

    /**
     * 设置密码
     *
     */
    public void setPassword(String phone, String phonecode, String password, final OnAppRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_PHONE, phone);
        params.put("code", phonecode);
        params.put(AppConstants.PARAM_PASSWORD, AppUtils.getShaMD5(password));
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(phone).append(phonecode).append(AppUtils.getShaMD5(password)).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.PWUPDATEPASSWORD, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "setPassword success");
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, result);
                }
            }
        });
    }

    public void getHistoryNotify(int uid, int fetchnum, int nextid, final OnAppRequestListenerFriend listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("fetchnum", fetchnum + "");
        params.put("nextid", nextid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(fetchnum).append(nextid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params,URL.GETHISTORYNOTIFICATION, new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj;
                int pmid = 0;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    NotificationInfo[] pa = gson.fromJson(result, NotificationInfo[].class);
                    obj = pa;
                    pmid = nextid;
                } else {
                    obj = message;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, pmid);
                }
            }
        });
    }

    /**
     * 获取积分兑换商品
     *
     * getNewMessage
     * @param uid,fetchnum,nextid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void getProducts(int uid, int fetchnum, int nextid, final OnAppRequestListenerFriend listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_FETCHNUM, fetchnum + "");
        params.put(AppConstants.PARAM_NEXTID, nextid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(fetchnum).append(nextid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params,URL.GETPRODUCT,new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson =new Gson();
                    JSONArray data = null;
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        data = jsonObject.getJSONArray("nextid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Product []products = gson.fromJson(data.toString(), Product[].class);
                    List<Product> list = Arrays.asList(products);
                    obj = list;
                } else {
                    obj = message;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, nextid);
                }
            }
        });
    }

    /**
     * 获取积分兑换商品
     *
     * getNewMessage
     * @param uid,fetchnum,nextid
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void getCheckPoint( int uid,final OnAppRequestListenerFriend listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params,URL.GETCHECKPOINT,new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson =new Gson();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        obj = jsonObject.get("point");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    obj = message;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, nextid);
                }
            }
        });
    }

    /**
     * 兑换商品
     *
     * getNewMessage
     * @param uid,fetchnum,nextid
     * @param listener
     * void
     * @exception
     * @since  1.0.0   uid=&pdtid=&receiver=&address=&phone=&code=&timestamp=&key=34253ydfg675hr56
     */
    public void exchangeGoods(int uid, int pdtid, String receiver, String address, String phone, String code, final OnAppRequestListenerFriend listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("pdtid", pdtid + "");
        params.put("receiver", receiver + "");
        params.put("address", address + "");
        params.put("phone", phone + "");
        params.put("code", code + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(pdtid).append(receiver).append(address).append(phone).append(code).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params,URL.EXCHANGEPOINT,new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "exchangeGoods success");
                } else {
                    obj = message;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, nextid);
                }
            }
        });
    }

    /**
     * 兑换商品
     *
     * getNewMessage
     * @param uid,adsid //用户ID  地址本ID
     * @param listener
     * void
     * @exception
     * @since  1.0.0
     */
    public void delAddressBook(int uid,int adsid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("adsid", adsid + "");
        String timestamp = TimeUtil.getCurrentTime();
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(adsid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.DELADDRESSBOOK, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "delAddressBook success");
                } else {
                    obj = message;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, null);
                }
            }
        });
    }

    /**
     * 修改地址本信息
     *
     * @param uid
     * @param listener
     */
    public void updateAddressBook(int uid, int adsid, String receiver, String address, String phone, String code, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("adsid", adsid + "");
        params.put("receiver", receiver + "");
        params.put("address", address + "");
        params.put("phone", phone + "");
        params.put("code", code + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(adsid).append(receiver).append(address).append(phone).append(code).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.UPADDRESSBOOK, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }
    
    /**新版写说说
     *
     *
     */
    public void sendGroupTwitter(int uid, int cid, String content, String imgs, String albumid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_CID, cid + "");
        params.put(AppConstants.PARAM_CONTENT, content);
        params.put(AppConstants.PARAM_IMGS, imgs);
        params.put(AppConstants.PARAM_ALBUMID, albumid);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(cid).append(content).append(imgs).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.SENDTWITTER, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    GroupTwritte twritte = gson.fromJson(result, GroupTwritte.class);
                    obj = twritte;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**获取群说说
     *
     * uid
     * gid:群ID，好友群则为0
     * fetchnum:
     * timestamp
     * nextid:
     * key:MD5(uid+gid+fetchnum+nextid+timestamp)
     */
    public void getGroupTwitters(int uid,int cid,int fetchnum,int nextid,final OnAppRequestListenerFriend listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_CID, cid + "");
        params.put(AppConstants.PARAM_FETCHNUM, fetchnum + "");
        params.put(AppConstants.PARAM_NEXTID, nextid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(cid).append(fetchnum).append(nextid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params, URL.GETGROUPTWRITTER, new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    GroupTwritte[] tw = gson.fromJson(result, GroupTwritte[].class);
                    obj = tw;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, nextid);
                }
            }
        });
    }

    /**
     * 获取群列表
     *
     *
     */
    public void getGroups(int uid,final OnAppRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.GETGROUPS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        GroupInfoBean[] gl = gson.fromJson(result, GroupInfoBean[].class);
                        obj = gl;
                    } else if (json.isJsonObject()) {
                        GroupInfoBean gi = gson.fromJson(result, GroupInfoBean.class);
                        obj = gi;
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "getGroups json.isJsonNull");
                    }
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取通讯录 三个身份 不同的URL
     *
     * @param userid
     * @param listener
     */
    public void getContacts(int userid, final OnAppRequestListener listener) {
        if (userid == 0) {
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, userid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuilder sb = new StringBuilder();
        sb.append(userid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String appurl = "";
        AccountInfo info = getAccountInfo();
        int role = info.getRole();
        String str = URL.SERVER_URL_CONTACT;
        if (role == 0) { // 园长
            appurl = info.getContactgw() + URL.GETCONTACTBYMASTER;
        } else if (role == 1) {
            appurl = info.getContactgw() + URL.GETCONTACTBYTEACHER;
        } else {
            appurl = info.getContactgw() + URL.GETCONTACTBYPARENT;
        }
        params.put(AppConstants.PARAM_KID, info.getKid() + "");
        sendVolleyRequestString(params,appurl, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    Contacts contacts = gson.fromJson(result, Contacts.class);
                    obj = contacts;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取系统配置
     *
     * @param uid
     * @param listener
     */
    public void getSysConfig(int uid,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        String sys = AppServer.getInstance().getAccountInfo().getSysgw() + "sys/getSysConf";
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, AppServer.getInstance().getAccountInfo().getSysgw() + URL.GETSYSCONFIG, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    // 获取系统服务菜单是否要更新,0不需要，1
                    if (json.isJsonArray()) {
                        JsonArray array = json.getAsJsonArray();
                    } else if (json.isJsonObject()) {
                        JsonObject jobject = json.getAsJsonObject();
                        JsonElement jsonservicesupdate = jobject.get("servicesupdate");
                        int servicesupdate = jsonservicesupdate.getAsInt();
                        if (servicesupdate == 1) {
//                          obj = servicesupdate;
//                          JsonElement contactgw_json = jobject.get("contactgw");
//                          String contactgw = contactgw_json.getAsString();
//                          JsonElement msggw_json = jobject.get("msggw");
//                          String msggw = msggw_json.getAsString();
//                          JsonElement taskgw_json = jobject.get("taskgw");
//                          String taskgw = taskgw_json.getAsString();
//                          JsonElement sysgw_json = jobject.get("sysgw");
//                          String sysgw = sysgw_json.getAsString();
//                          JsonElement groupgw_json = jobject.get("groupgw");
//                          String groupgw = groupgw_json.getAsString();
//                          JsonElement schedulegw_json = jobject.get("schedulegw");
//                          String schedulegw = schedulegw_json.getAsString();
//                          JsonElement notifygw_json = jobject.get("notifygw");
//                          String notifygw = notifygw_json.getAsString();
//                          JsonElement classnotifyurl_json = jobject.get("classnotifyurl");
//                          String classnotifyurl = classnotifyurl_json.getAsString();
//                          JsonElement classphotourl_json = jobject.get("classphotourl");
//                          String classphotourl = classphotourl_json.getAsString();
//                          JsonElement classscheduleurl_json = jobject.get("classscheduleurl");
//                          String classscheduleurl = classscheduleurl_json.getAsString();
//                          JsonElement masterletterurl_json = jobject.get("masterletterurl");
//                          String masterletterurl = masterletterurl_json.getAsString();
//                          JsonElement noticeurl_json = jobject.get("noticeurl");
//                          String noticeurl = noticeurl_json.getAsString();
//                          AccountInfo info = AppServer.getInstance().getAccountInfo();
//                          info.setContactgw(contactgw);
//                          info.setMsggw(msggw);
//                          info.setTaskgw(taskgw);
//                          info.setSysgw(sysgw);
//                          info.setGroupgw(groupgw);
//                          info.setSchedulegw(schedulegw);
//                          info.setNotifygw(notifygw);
//                          info.setClassnotifyurl(classnotifyurl);
//                          info.setClassphotourl(classphotourl);
//                          info.setClassscheduleurl(classscheduleurl);
//                          info.setMasterletterurl(masterletterurl);
//                          info.setNoticeurl(noticeurl);
//                          DbHelper.updateAccountUrl(info);
                        } else {
                            servicesupdate = 0;
                        }
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "getSysConfig json.isJsonNull");
                    }
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取成长日记首页内容
     *
     * @param uid
     * @param listener
     */
    public void getDiaryHome(int uid, int fetchnum, int nextid, final OnAppRequestListenerFriend listener){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_FETCHNUM, fetchnum + "");
        params.put(AppConstants.PARAM_NEXTID, nextid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(fetchnum).append(nextid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestStringFriend(params,info.getDiarygw()+ URL.GETGROWTHDARIY, new OnSendRequestListenerFriend() {
            @Override
            public void onSendRequestfriend(int code, String message, String result, int nextid) {
                Object obj = null;
                Gson gson = new Gson();
                if (code == REQUEST_SUCCESS) {
                    DiaryHomeInfo[] info = gson.fromJson(result, DiaryHomeInfo[].class);
                    obj = info;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequestFriend(code, message, obj, nextid);
                }
            }
        });
    }

    /**
     * 保存日记
     *
     * txt=&img=&snd=&uid=&share=&key=34253ydfg675hr56
     */
    public void saveDiary(String txt,String img,String snd, int uid, int share, final OnAppRequestListener listener){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_TXT, txt);
        params.put(AppConstants.PARAM_IMG, img);
        params.put(AppConstants.PARAM_SND, snd);
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_SHARE, share + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(txt).append(img).append(snd).append(uid).append(share).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, info.getDiarygw() + URL.SETGROWTHDARIY, new OnSendRequestListener() {
            Object obj = null;
            Gson gson = new Gson();
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (code == REQUEST_SUCCESS) {
                    DiaryHomeInfo  info = gson.fromJson(result, DiaryHomeInfo.class);
                    obj = info;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 用户 退出
     *
     * @param uid
     * @param listener
     */
    public void loginout(int uid,int relationship, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("relationship", relationship + "");
        String clientid = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getString(AppConstants.CLIENTID, "");
        params.put(AppConstants.PARAM_CLIENTID, clientid);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getMaingw() + URL.LOGINOUT, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 更新设备id
     *
     * @param uid
     * @param listener
     * action 之前是用来获取推送消息的，为0表示不需要推送
     */
    public void updateDeviceId(int uid, String deviceid, final int relationship, int action, final OnAppRequestListener listener){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_CLIENTID, deviceid);
        /**
         * 在第二次自动登录的时候会调用这个接口
         * 如果没有身份(代表是老师或者园长或者还没选身份的家长)
         * 会自动踢掉已经登录的账号
         */
        if (relationship == 0) {
            params.put("relationship", "");
        } else {
            params.put("relationship", relationship + "");
        }
//      params.put("action", action+"");
        params.put(AppConstants.PARAM_SYSOP, info.getSysop() == null?"":info.getSysop());
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(deviceid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
//      String url = "http://192.168.0.138:555/" + URL.UPDATEDEVICEID;
//        String url = "http://t.kmapp.zgyey.com/" + URL.UPDATEDEVICEID;
        String url = info.getMaingw() + URL.UPDATEDEVICEID;
        sendVolleyRequestString(params,url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int hxState = jsonObject.getInt("exist");
                        obj = hxState;
                    }  catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 意见反馈
     *
     * uid=653548&txt=1212122323232&timestamp=&key=34253ydfg675hr56
     *
     *
     */
    public void feedback(int uid,String txt,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.TXT, txt);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(txt).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getSysgw() + URL.FEEDBACK, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object  obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "feedback success");
                } else {
                    UtilsLog.i(TAG, "feedback fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取班级相册
     *
     */
    public void loadClassPhoto(final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_TOKEN, AppServer.getInstance().getAccountInfo().getToken());
        params.put(AppConstants.PARAM_PP, Utils.getPP());
        sendVolleyRequestString(params, URL.CLASSPHOTODATA, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "loadClassPhoto success");
                } else {
                    UtilsLog.i(TAG, "loadClassPhoto fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 更新相册名称
     *
     * @param albumdId
     * @param cid
     * @param description
     * @param title
     * @param listener
     */
    public void updateClassPhoto(String albumdId, int cid, String description, String title, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_ALBUMID, albumdId);
        params.put(AppConstants.PARAM_CID, cid + "");
        params.put(AppConstants.PARAM_DESCRIPTION, description + "");
        params.put(AppConstants.PARAM_TITLE, title + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(title).append(description).append(cid).append(albumdId).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw() + URL.UPDATECLASSABLUM, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "updateClassPhoto success");
                } else {
                    UtilsLog.i(TAG, "updateClassPhoto fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 删除日记
     *
     * @param diaryid
     * @param listener
     */
    public void deleteDiary(int diaryid, final OnAppRequestListener listener){
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_DIARYID, diaryid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(diaryid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, info.getDiarygw() + URL.DELETEGROWTHDARIY, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "deleteDiary success");
                } else {
                    UtilsLog.i(TAG, "deleteDiary fail");
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, result);
                }
            }
        });
    }

    /**
     * 获取班级相册
     *
     */
    public void loadClassPhoto(int uid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw() + URL.CLASSPHOTODATA, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        ClassPhoto[] gl = gson.fromJson(result, ClassPhoto[].class);
                        List<ClassPhoto> gplist = Arrays.asList(gl);
                        List<ClassPhoto> newserviceLists = new ArrayList<ClassPhoto>(gplist);
                        obj = newserviceLists;
                    } else if (json.isJsonObject()) {
                        ClassPhoto gi = gson.fromJson(result, ClassPhoto.class);
                        List<ClassPhoto> newserviceList = new ArrayList<ClassPhoto>();
                        newserviceList.add(gi);
                        obj = newserviceList;
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "loadClassPhoto json.isJsonNull");
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 根据Cid获取班级相册
     *
     */
    public void loadClassPhotoByCid(int uid, int cid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_CID, cid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(cid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw() + URL.CLASSPHOTODATABYCID, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        Album[] gl = gson.fromJson(result, Album[].class);
                        List<Album> gplist = Arrays.asList(gl);
                        List<Album> newserviceLists = new ArrayList<Album>(gplist);
                        obj = newserviceLists;
                    } else if (json.isJsonObject()) {
                        Album gi = gson.fromJson(result, Album.class);
                        List<Album> newServiceList = new ArrayList<Album>();
                        newServiceList.add(gi);
                        obj = newServiceList;
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "loadClassPhotoByCid json.isJsonNull");
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 创建班级相册
     *
     */
    public void createClassPhoto(String author, int cid, String description, String title, int uid, int kid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_AUTHOR, author + "");
        params.put(AppConstants.PARAM_CID, cid + "");
        params.put(AppConstants.PARAM_DESCRIPTION, description + "");
        params.put(AppConstants.PARAM_TITLE, title + "");
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_KID, kid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(author).append(cid).append(description).append(title).append(uid).append(kid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw() + URL.CLASSPHOTO_CREATE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(result);
                    int pmid = element.getAsJsonObject().get("albumid").getAsInt();
                    obj = String.valueOf(pmid);
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 删除班级相册
     *
     */
    public void deleteClassPhoto(String albumId, int uid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_ALBUMIDS, albumId);
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(albumId).append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw()+URL.CLASSPHOTO_DELETE, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                obj = String.valueOf(code);
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 保存相册照片并发动态（又拍）
     *
     * @param uid
     * @param albumid
     * @param imgs
     * @param description
     * @param listener
     */
    public void insertAlbumParams(int uid, String albumid, String imgs, String description, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_ALBUMID, albumid);
        params.put("imgs",imgs);
        params.put("description", description);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(albumid).append(imgs).append(description).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw() + URL.INSERTINTOALBUM, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取班级相册的照片
     *
     * @param uid
     * @param listener
     */
    public void getPhotoByAlbumId(String uid, String albumid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_ALBUMID, albumid);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(albumid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw() + URL.CLASSPHOTO_GET, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj;
                Gson gson = new Gson();
                if (code == AppServer.REQUEST_SUCCESS) {
                    Album[] info = gson.fromJson(result, Album[].class);
                    List<Album> gplist = Arrays.asList(info);
                    List<Album> newserviceList = new ArrayList<Album>(gplist);
                    obj = newserviceList;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 删除班级相片
     *
     */
    public void deleteClassPhotoGalley(String photoId, int uid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_PHOTOIDS, photoId);
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(photoId).append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getClassalbumgw() + URL.CLASSPHOTO_DELETEPHOTO, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object  obj = null;
                obj = String.valueOf(code);
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 握手
     *
     * @param url
     * @param listener
     */
    public void hands(String url,final OnAppRequestListener listener){
        Response res = liteclient.execute(new Request(url));
        String html = res.getString();
//      System.out.println(html);
    }

    /**
     * 获取学期列表
     *
     * @param uid
     * @param listener
     */
    public void getTermList(int uid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getLifephotogw()+URL.GETTERLIST, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if(code == REQUEST_SUCCESS){
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement jsonele = parser.parse(result);
                    List<Term>listbean = new ArrayList<Term>();
                    Term[] bean = null;
                    Term beans = null;
                    if (jsonele.isJsonArray()) {
                        bean= gson.fromJson(result, Term[].class);
                    } else {
                        beans = gson.fromJson(result, Term.class);
                        listbean.add(beans);
                        final int size = listbean.size();
                        bean = listbean.toArray(new Term[size]);
                    }
                    obj = bean;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取生活剪影/手工作品数据
     *
     * @param cid
     * @param term
     * @param listener
     */
    public void getLifePhoto(String type,int cid,String term,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", type);
        params.put(AppConstants.PARAM_CID, cid + "");
        params.put(AppConstants.PARAM_TERM, term);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(cid).append(term).append(type).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getLifephotogw() + URL.GETLIFEPHOTO, new OnSendRequestListener(){
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    LifePhoto[] list = null;
                    list = gson.fromJson(result, LifePhoto[].class);
                    obj = list;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取全部的生活剪影|手工作品数据
     *
     * @param type
     * @param listener
     */
    public void getChildPhoto(int uid,String type,final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("type",type);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(type).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getLifephotogw() + URL.GETALLLIFEPHOTO, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (code == 0) {
                    Object obj = null;
                    if (code == REQUEST_SUCCESS) {
                        Gson gson = new Gson();
                        Term[] list=null;
                        list = gson.fromJson(result, Term[].class);
                        obj = list;
                    } else {
                        obj = result;
                    }
                    if (listener != null) {
                        listener.onAppRequest(code, message, obj);
                    }
                }
            }
        });
    }

    /**
     * 获取指定的生活剪影|手工作品
     *
     * @param type 1生活剪影,2手工作品
     * @param gbid
     * @param listener
     */
    public void getChildLifePhoto(String type, int page, int size, int gbid, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("type", type + "");
        params.put("page", page + "");
        params.put("size", size + "");
        params.put(AppConstants.PARAM_GBID, gbid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(gbid).append(type).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getLifephotogw() + URL.GETCHILDLIFEPHOTO, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        WLImage[] wl = gson.fromJson(json, WLImage[].class);
                        List<WLImage> gplist = Arrays.asList(wl);
                        List<WLImage> newserviceList = new ArrayList<WLImage>(gplist);
                        obj = newserviceList;
                    } else if (json.isJsonObject()) {
                        WLImage gi = gson.fromJson(result, WLImage.class);
                        List<WLImage> list = new ArrayList<WLImage>();
                        list.add(gi);
                        obj = list;
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "getChildLifePhoto json.isJsonNull");
                    }
                } else {
                    UtilsLog.i(TAG, "getChildLifePhoto fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 删除生活剪影|手工作品图片
     *
     * @param photoids
     * @param listener
     */
    public void deleteChildPhoto(String photoids, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("photoids", photoids);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(photoids).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getLifephotogw() + URL.DELETECHILDPHOTO, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == 0) {
                    UtilsLog.i(TAG, "deleteChildPhoto success");
                } else {
                    UtilsLog.i(TAG, "deleteChildPhoto fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 编辑生活剪影|手工作品
     *
     * @param photoids
     * @param photo_decs
     * @param listener
     */
    public void editChldPhoto(String type, String photoids, String photo_decs, final OnAppRequestListener listener ){
        HashMap<String, String>parmas = new HashMap<String, String>();
        parmas.put("photoids", photoids);
        parmas.put("photodesc", photo_decs);
        String timestamp = URL.urlkey;
        parmas.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(photo_decs).append(photoids).append(timestamp);
        parmas.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(parmas, info.getLifephotogw() + URL.EDITCHILDDECS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "editChldPhoto success");
                } else {
                    UtilsLog.i(TAG, "editChldPhoto fail");
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 查询幼儿园
     *
     * @param number
     * @param phone
     * @param listener
     */
    public void quaryKindergaten(String number, String phone, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("number",number);
        params.put("phone",phone);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(number).append(phone).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        sendVolleyRequestString(params, URL.SEARCHKINDERGATEN, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonElement json = parser.parse(result);
                if (json.isJsonArray()) {
                    KindergartenInfo[]  wl = gson.fromJson(json, KindergartenInfo[].class);
                    List<KindergartenInfo> gplist = Arrays.asList(wl);
                    List<KindergartenInfo> newserviceList = new ArrayList<KindergartenInfo>(gplist);
                    obj = newserviceList;
                } else if (json.isJsonObject()) {
                    KindergartenInfo gi = gson.fromJson(result, KindergartenInfo.class);
                    List<KindergartenInfo> list = new ArrayList<KindergartenInfo>();
                    list.add(gi);
                    obj = list;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取地址
     *
     * @param uid
     * @param superior
     * @param listener
     */
    public void getArea(String uid, int superior, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid", uid);
        params.put("superior", superior + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(superior).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        sendVolleyRequestString(params, URL.GETKINDERGATENAREA, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        AddressBean[]  wl = gson.fromJson(json, AddressBean[].class);
                        List<AddressBean> gplist = Arrays.asList(wl);
                        List<AddressBean> newserviceList = new ArrayList<AddressBean>();
                        for (int i = 0; i < wl.length; i++) {
                            newserviceList.add(wl[i]);
                        }
                        obj = newserviceList;
                    } else if (json.isJsonObject()) {
                        AddressBean gi = gson.fromJson(result, AddressBean.class);
                        List<AddressBean> list = new ArrayList<AddressBean>();
                        list.add(gi);
                        obj = list;
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "getArea json.isJsonNull");
                    }
                } else {
                    UtilsLog.i(TAG, "getArea fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 创建幼儿园
     *
     * @param uid
     * @param privince
     * @param city
     * @param area
     * @param residence
     * @param kname
     * @param username
     * @param listener
     */
    public void creatKindergarten(int uid, int privince, int city, int area, int residence, String kname, String username, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put("privince", privince + "");
        params.put("city", city + "");
        params.put("area", area + "");
        params.put("residence", residence + "");
        params.put(AppConstants.PARAM_KNAME, kname);
        params.put("username", username);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(privince).append(city).append(area).append(residence).append(kname).append(username).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        sendVolleyRequestString(params, URL.CREATKINDERGATEN, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        KindergartenInfo[] info = gson.fromJson(json, KindergartenInfo[].class);
                        List<KindergartenInfo> newserviceList = new ArrayList<KindergartenInfo>();
                        for (int i = 0; i < info.length; i++) {
                            newserviceList.add(info[i]);
                        }
                        obj = newserviceList;
                    } else if (json.isJsonObject()) {
                        KindergartenInfo gi = gson.fromJson(result, KindergartenInfo.class);
                        List<KindergartenInfo> list = new ArrayList<KindergartenInfo>();
                        list.add(gi);
                        obj = list;
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "creatKindergarten json.isJsonNull()");
                    }
                } else {
                    UtilsLog.i(TAG, "creatKindergarten fail");
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 老师搜索幼儿园
     *
     * @param number 幼儿园编号
     * @param phone  园长手机号码
     * @param listener
     */
    public void findKinderGarten(String number, String phone, final OnAppRequestListener listener){
        HashMap<String,String>params = new HashMap<String,String>();
        params.put("number", number);
        params.put("phone", phone);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(number).append(phone).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        sendVolleyRequestString(params, URL.SEARCHKINDERGATEN, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "findKinderGarten success");
                } else {
                    UtilsLog.i(TAG, "findKinderGarten fail");
                }
            }
        });
    }

    /**
     * 加入幼儿园
     *
     * @param uid 老师id
     * @param listener
     */
    public void joinKinderGarten(int uid, int kid, final OnAppRequestListener listener){
        HashMap<String,String>params = new HashMap<String,String>();
        params.put("uid",uid + "");
        params.put("kid",kid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(kid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        sendVolleyRequestString(params, URL.JOINKINDERGATEN, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "joinKinderGarten success");
                } else {
                    UtilsLog.i(TAG, "joinKinderGarten fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取班级
     *
     * @param uid
     * @param kid
     * @param role
     * @param listener
     */
    public void getClassList(int uid, int kid, int role, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid", uid + "");
        params.put("kid", kid + "");
        params.put("role", role + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(kid).append(role).append(timestamp);
        params.put(AppConstants.PARAM_KEY,AppUtils.md5(sb.toString()));
        sendVolleyRequestString(params, URL.GETCLASSLIST, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        Classe[] info = gson.fromJson(json, Classe[].class);
                        List<Classe> newserviceList = new ArrayList<Classe>();
                        for (int i = 0; i < info.length; i++) {
                            newserviceList.add(info[i]);
                        }
                        obj = newserviceList;
                    } else if (json.isJsonObject()) {
                        Classe gi = gson.fromJson(result, Classe.class);
                        List<Classe> list = new ArrayList<Classe>();
                        list.add(gi);
                        obj = list;
                    } else if (json.isJsonNull()) {
                        UtilsLog.i(TAG, "getClassList json.isJsonNull");
                    }
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 加入班级
     *
     * @param uid
     * @param cid
     * @param kid
     * @param listener
     */
    public void joinClass(int uid, int cid, int kid, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid", uid + "");
        params.put("cid", cid + "");
        params.put("kid", kid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(cid).append(kid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        sendVolleyRequestString(params, URL.JOINCLASS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "joinClass success");
                } else {
                    UtilsLog.i(TAG, "joinClass fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取邀请的文本
     *
     * @param uid
     * @param listener
     */
    public  void  getInviteText(int uid, int kid, int role, final OnAppRequestListener listener){
        HashMap<String, String> params =new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_KID, kid + "");
        params.put(AppConstants.ROLE, role + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(kid).append(role).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getMaingw() + URL.GETINVITETEXT, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonElement json = parser.parse(result);
                    if (json.isJsonArray()) {
                        JsonArray array = json.getAsJsonArray();
                    } else if (json.isJsonObject()) {
                        JsonObject jobject = json.getAsJsonObject();
                        JsonElement jsonInviteText = jobject.get("invitetext");
                        String inviteText = jsonInviteText.getAsString();
                        obj = inviteText;
                    }
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 修改幼儿园接口
     *
     * @param listener
     */
    public void updateGartenName(int uid,int kid,String kName,final OnAppRequestListener listener){
/*      HashMap<String, String> params =new HashMap<String, String>();
        params.put(AppConstants.PARAM_APPVER,AppUtils.getVersionName());
        params.put(AppConstants.PARAM_SYSVER,"1");//系统平台android:1，ios:2
        String url = URL.SERVER_URL + "pub/getMainGateway"; */
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_KID, kid + "");
        params.put(AppConstants.PARAM_KNAME, kName + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(kid).append(kName).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getMaingw() + URL.UPDATEGARTENNAME, new OnSendRequestListener(){
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "updateGartenName success");
                } else {
                    UtilsLog.i(TAG, "updateGartenName fail");
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     *  获取主网关的接口
     *
     * @param listener
     */
    public void getMainGateWay(final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_APPVER, AppUtils.getVersionName());
        params.put(AppConstants.PARAM_SYSVER, "1");//系统平台android:1，ios:2
        String url = URL.SERVER_URL + "pub/getMainGateway";

        sendLiteHttpRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                String hx = "";
                String contracturl = "";
                String birthdayurl = "";
                String addschoolurl = "";
                String addclassurl = "";
                String payurl = "";
                int update = 0;
                try {
                    JSONObject jObj = new JSONObject(result);
                    if (!jObj.isNull(TAG_CODE)){
                        code = Integer.valueOf(jObj.getString(TAG_CODE));
                    }
                    if (!jObj.isNull(TAG_INFO)){
                        message = jObj.getString(TAG_INFO);
                    }
                    if (!jObj.isNull(TAG_RESULT)){
                        result = jObj.getString(TAG_RESULT);
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString(AppConstants.PARAM_MAINGATEWAY, result);
                    }
                    if (!jObj.isNull("hx")){
                        hx = jObj.getString("hx");  //0是不关。 1是开
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString(AppConstants.PARAM_HUANXIN, hx);
                    }
                    if (!jObj.isNull("contracturl")){
                        contracturl = jObj.getString("contracturl");
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString("contracturl", contracturl);
                    }
                    if (!jObj.isNull("birthdayurl")){
                        birthdayurl = jObj.getString("birthdayurl");
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString("birthdayurl", birthdayurl);
                    }
                    if (!jObj.isNull("addschoolurl")){
                        addschoolurl = jObj.getString("addschoolurl");
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString("addschoolurl", addschoolurl);
                    }
                    if (!jObj.isNull("addclassurl")){
                        addclassurl = jObj.getString("addclassurl");
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString("addclassurl", addclassurl);
                    }
                    if (!jObj.isNull("update")){
                        update = jObj.getInt("update");
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt("update", update);
                    }
                    if (!jObj.isNull("payurl")){
                        payurl = jObj.getString("payurl");
                        SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString("payurl", payurl);
                    }
                } catch (JSONException e) {
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, result);
                }
            }
        });
    }

    /**
     *
     * @param uid
     * @param filetype
     */
    public void uploadimage(int uid, String filetype, File file, final OnAppRequestListener listener){
        RequestParams params = new RequestParams();
        params.addBodyParameter(AppConstants.PARAM_UID, uid + "");
        params.addBodyParameter(AppConstants.PARAM_FILETYPE, filetype);
        params.addBodyParameter("file", file);
        String timestamp = URL.urlkey;
        params.addBodyParameter(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(filetype).append(timestamp);
        params.addBodyParameter(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyImageRequestString(params, info.getUploadurl() + URL.UPLOADFILEIMAG, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    try {
                        JSONObject json = new JSONObject(result);
                        obj = json.get("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 上传Web端图片
     *
     * @param file
     * @param filetype
     */
    public void uploadwebimage(File file, int filetype, String style, final OnAppRequestListener listener){
        RequestParams params = new RequestParams();
        params.addBodyParameter(AppConstants.PARAM_FILETYPE, filetype + "");
        params.addBodyParameter("file", file);
        String timestamp = URL.urlkey;
        params.addBodyParameter(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        params.addBodyParameter(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        String url = "";
        if (style!=null && style.equals("other")) {
            url = info.getUploadurl() + URL.UPLOADWEBIMG;
        } else if (style!=null && style.equals("general")) {
            url = AppUtils.replaceUnifiedUrl(CommonBrowser.uploadWG).replace("{type}", filetype + ""); // 需替换通配符
        }
        sendVolleyImageRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    JSONObject json = null;
                    try {
                        if (result!=null) { // result == null 会报错
                            json = new JSONObject(result);
                            obj = json.get("url");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 设置默认联系人
     *
     * @param uid
     * @param defaultRelation
     * @param listener
     */
    public void updateDefaultRelation(int uid, int defaultRelation, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid", uid + "");
        params.put("defaultRelation", defaultRelation+"");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(defaultRelation).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        sendVolleyRequestString(params, info.getSysgw() + URL.UPDATEDEFAULTRELATION, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "updateDefaultRelation success");
                } else {
                    UtilsLog.i(TAG, "updateDefaultRelation fail");
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 更新环信注册状态
     *
     * @param uid
     * @param relationship
     * @param state
     * @param remark
     * @param listener
     */
    public void updateHxState(int uid, int relationship, int state, String remark, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid", uid + "");
        params.put("relationship", relationship + "");
        params.put("state", state + "");
        params.put("remark", remark);
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(relationship).append(state).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        String  url = URL.UPDATEHXSTATE;
//      String url = "http://192.168.0.138:555/" + "main/updateHXState";
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "updateHxState success");
                } else {
                    UtilsLog.i(TAG, "updateHxState fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

   /****
    *
    * @param uid
    * @param to
    */
    public void sendSmsMessage(int uid ,int to,String content,final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("uid", uid + "");
        params.put("to", to + "");
        params.put("content", content + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(to).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.md5(sb.toString()));
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        String url = info.getMaingw() + URL.SENDSMSMESSAGE;
//      String url = "http://192.168.0.138:555/" + URL.SENDSMSMESSAGE;
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "sendSmsMessage success");
                } else {
                    UtilsLog.i(TAG, "sendSmsMessage fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

   /****
    * 赞某人的说说
    *
    * @param uid
    * @param uname
    */
    public void setZan(int uid, String uname, int twrid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_UNAME, uname);
        params.put(AppConstants.PARAM_TWRID, twrid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(uname).append(twrid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.SETZAN, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    obj = result;
                } else {
                    obj = result;
                }
                if (listener!=null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

   /**
    * 获取cid
    *
    * @param uid
    */
    public void getCid(int uid, final OnAppRequestListener listener){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.GETCID, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    Classe[] cl = gson.fromJson(result, Classe[].class);
                    obj = cl;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

   /**
    * 删除评论
    *
    * @param uid
    * @param cmtid
    */
    public void delDiscuss(int uid, int cmtid, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put(AppConstants.PARAM_UID, uid + "");
        params.put(AppConstants.PARAM_CMTID, cmtid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(cmtid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, URL.DELDISCUSS, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    obj = result;
                } else {
                    UtilsLog.i(TAG, "delDiscuss fail");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 根据kid获取班级列表
     *
     * @param uid
     * @param kid
     * @param role
     * @param listener
     */
    public void getClassesByKid(int uid, int kid, int role, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        params.put("kid", kid + "");
        params.put("role", role + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(kid).append(role).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, info.getContactgw() + URL.GETCLASSBYKID, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    List<Classe>list = new ArrayList<Classe>();
                    Classe[]classes = gson.fromJson(result, Classe[].class);
                    for (int i = 0; i < classes.length; i++) {
                        list.add(classes[i]);
                    }
                    obj = list;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 根据KID获取全园老师
     *
     * @param uid
     * @param kid
     * @param listener
     */
    public void getTeachersByKid(int uid,int kid,final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        params.put("kid", kid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(kid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params,info.getContactgw() + URL.GETTEACHERBYKID, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    List<Teacher>list = new ArrayList<Teacher>();
                    Teacher[]teachers = gson.fromJson(result, Teacher[].class);
                    for (int i = 0; i < teachers.length; i++) {
                        list.add(teachers[i]);
                    }
                    obj = list;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 老师根据kid获取家长列表（老师所执教的班级）
     *
     * @param uid
     * @param kid
     * @param listener
     */
    public void getParentsByTeacherKid(int uid, int kid, final OnAppRequestListener listener){
        final HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        params.put("kid", kid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        String sb = uid + "" + kid + "" + timestamp;
//      StringBuffer sb = new StringBuffer();
//      sb.append(uid).append(kid).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        sendVolleyRequestString(params, info.getContactgw() + URL.GETPARENTBYKID, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    List<Parent>list = new ArrayList<Parent>();
                    List<Classe>classeList = new ArrayList<Classe>();
                    try {
                        JSONArray parentHasCid = new JSONArray(result);
                        JSONArray  parentList;
                        for (int i = 0; i < parentHasCid.length(); i++) {
                            JSONObject objectCid = parentHasCid.getJSONObject(i);
                            String parents = objectCid.getString("parents");
                            parentList = new JSONArray(parents);
                            int cid = objectCid.getInt("cid");
                            String cname = objectCid.getString("cname");
                            Classe classe = new Classe();
                            classe.setCid(cid);
                            classe.setCname(cname);
                            if (parentList.length() == 1) {
                                try {
                                    JSONObject objectName = parentList.getJSONObject(0);
                                    int uid = objectName.getInt("uid");
                                    classe.setChildrencount(parentList.length());
                                } catch (JSONException e) {
                                    classe.setChildrencount(0);
                                }
                            } else {
                                classe.setChildrencount(parentList.length());
                            }
                            classeList.add(classe);
                            for (int index = 0; index < parentList.length(); index++){
                                try {
                                    JSONObject objectName = parentList.getJSONObject(index);
                                    int uid = objectName.getInt("uid");
                                    String name = objectName.getString("realname");
                                    String avatar = objectName.getString("avatar");
                                    String birthday = objectName.getString("birthday");
                                    int birthdaystatus = objectName.getInt("birthdaystatus");
                                    Parent parent = new Parent(uid, avatar, name, cid, cname, birthday, birthdaystatus);
                                    list.add(parent);
                                } catch (JSONException e) {
                                    UtilsLog.i(TAG, "getParentsByTeacherKid into JSONException");
                                }
                            }
                        }
                        if (classeList!=null && classeList.size()!=0) {
                            Contacts contacts = AppContext.getInstance().getContacts();
                            contacts.setClasses(classeList);
                            AppContext.getInstance().setContacts(contacts);
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Classe.class);
                            DbHelper.getDB(AppContext.getInstance()).saveAll(classeList);
                        }
                        if (list!=null && list.size() > 0) {
//                          Contacts contacts = AppContext.getInstance().getContacts();
//                          contacts.setParents(list);
//                          AppContext.getInstance().setContacts(contacts);
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        UtilsLog.i(TAG, "getParentsByTeacherKid JSONException");
                    } catch (DbException e) {
                        UtilsLog.i(TAG, "getParentsByTeacherKid DbException");
                    }
                    obj = list;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 家长刷新通讯录接口
     *
     * @param uid
     * @param cid
     * @param listener
     */
    public void getTeachersAndParentsByCid(int uid,int cid ,final OnAppRequestListener listener){
        final HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        params.put("cid", cid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        String sb = uid + "" + cid + "" + timestamp;
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
//      String url = "http://t.kmapp.zgyey.com/" + URL.GETTEACHERANDPARENTBYCID;
        String url = info.getContactgw() + URL.GETTEACHERANDPARENTBYCID;
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                Gson gson = new Gson();
                if (code == AppServer.REQUEST_SUCCESS) {
                    Teacher[]arrayTeacher;
                    Parent[]arrayParent;
                    List<Parent>parents = new ArrayList<Parent>();
                    List<Children>childrens = new ArrayList<Children>();
                    List<Teacher>teachers = new ArrayList<Teacher>();
                    try {
                        JSONArray array = new JSONArray(result);
                        if (array!=null && array.length()!=0) {
                            for (int index = 0; index < 2; index++) {
                                JSONObject object = array.getJSONObject(index);
                                int cid = object.getInt("cid");
                                String cname = object.getString("cname");
                                JSONArray parentArray = object.getJSONArray("parents");
                                JSONArray teacherArray = object.getJSONArray("teachers");
                                if (teacherArray!=null) {
                                    arrayTeacher = gson.fromJson(String.valueOf(teacherArray), Teacher[].class);
                                    if (arrayTeacher!=null && arrayTeacher.length!=0) {
                                        for (int i = 0; i < arrayTeacher.length; i++) {
                                            Teacher teacher = arrayTeacher[i];
                                            teachers.add(teacher);
                                        }
                                        Contacts contacts = AppContext.getInstance().getContacts();
                                        contacts.setTeachers(teachers);
                                        AppContext.getInstance().setContacts(contacts);
                                        DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(teachers);
                                    } else {
                                        AppContext.getInstance().getContacts().setTeachers(null);
                                        DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                                    }
                                } else {
                                    AppContext.getInstance().getContacts().setTeachers(null);
                                    DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                                }
                                if (parentArray!=null) {
                                    arrayParent = gson.fromJson(String.valueOf(parentArray), Parent[].class);
                                    if (arrayParent!=null && arrayParent.length!=0) {
                                        for (int i = 0; i < arrayParent.length; i++) {
                                            Parent parent = arrayParent[i];
                                            parent.setCid(cid);
                                            parent.setCname(cname);
                                            parents.add(parent);

                                            Children children = new Children(parent);
                                            childrens.add(children);
                                        }
                                        Contacts contacts = AppContext.getInstance().getContacts();
                                        contacts.setParents(childrens);
                                        DbHelper.getDB(AppContext.getInstance()).deleteAll(Children.class);
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(childrens);
                                        DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(parents);
                                    } else {
                                        AppContext.getInstance().getContacts().setParents(null);
                                        DbHelper.getDB(AppContext.getInstance()).deleteAll(Children.class);
                                        DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                                    }
                                } else {
                                    AppContext.getInstance().getContacts().setParents(null);
                                    DbHelper.getDB(AppContext.getInstance()).deleteAll(Children.class);
                                    DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (DbException e){
                    }
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 获取用户公众号
     *
     * @param uid
     * @param listener
     */
    public void getPublics(int uid,final OnAppRequestListener listener){
        final HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        String sb = uid + timestamp;
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String url = info.getContactgw() + URL.GETPUBLICS;
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    PublicAccount[] accounts = gson.fromJson(result, PublicAccount[].class);
                    List<PublicAccount> list = new ArrayList<PublicAccount>();
                    if (accounts!=null) {
                        for (PublicAccount account : accounts) {
                            list.add(account);
                        }
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(PublicAccount.class);
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                            AppContext.getInstance().getContacts().getPublics().clear();
                            AppContext.getInstance().getContacts().setPublics(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        obj = list;
                    }
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 创建支付宝订单
     *
     * @param uid
     * @param listener
     */
    public void createAlipayOrder(int uid, String feeid, final OnAppRequestListener listener){
        final HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        params.put("feeid", feeid + "");
        params.put("from", "SGS");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(feeid).append("SGS").append(timestamp);
        String sss = AppUtils.Md5(sb.toString());
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String url = info.getPaygw() == null ? URL.PAY_URL : info.getPaygw() + URL.CREATEALIPAYORDER;
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    OrderInfo order = gson.fromJson(result, OrderInfo.class);
                    obj = order;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 创建微信订单
     *
     * @param uid
     * @param listener
     */
    public void createWechatOrder(int uid, String feeid, final OnAppRequestListener listener){
        final HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        params.put("feeid", feeid + "");
        params.put("from", "SGS");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(feeid).append("SGS").append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String url = info.getPaygw() == null ? URL.PAY_URL : info.getPaygw() + URL.CREATEWECHATORDER;
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    Gson gson = new Gson();
                    WxEntity entity = gson.fromJson(result, WxEntity.class);
                    obj = entity;
                } else {
                    obj = result;
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 开通VIP套餐
     *
     * @param uid
     * @param listener
     */
    public void openVIP(int uid, String orderno, final OnAppRequestListener listener){
        final HashMap<String,String> params = new HashMap<String,String>();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        params.put("uid", uid + "");
        params.put("orderno", orderno + "");
        String timestamp = URL.urlkey;
        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
        StringBuffer sb = new StringBuffer();
        sb.append(uid).append(orderno).append(timestamp);
        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
        String url = info.getPaygw() == null ? URL.PAY_URL : info.getPaygw() + URL.OPENVIP;
        sendVolleyRequestString(params, url, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "openVIP success!");
                } else {
                    UtilsLog.i(TAG, "openVIP fail!");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 公用的网络打开照片
     *
     * @param api
     * @param listener
     */
    public void getPhotos(String api, int nextId, final OnAppRequestListener listener){
        final HashMap<String,String> params = new HashMap<String,String>();
//        AccountInfo info = AppServer.getInstance().getAccountInfo();
//        params.put("nextid", nextId + "");
//        params.put("fetchnum", 6 + "");
//        params.put("orderno", orderno + "");
//        String timestamp = URL.urlkey;
//        params.put(AppConstants.PARAM_TIMESTAMP, timestamp);
//        StringBuffer sb = new StringBuffer();
//        sb.append(uid).append(orderno).append(timestamp);
//        params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
//        String url = info.getPaygw() == null ? URL.PAY_URL : info.getPaygw() + URL.OPENVIP;
        api = AppUtils.replaceUnifiedUrl(api).replace("{nextid}", nextId + "").replace("{fetchnum}", 12 + "");
        sendVolleyRequestString(params, api, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                Object obj = null;
                if (code == REQUEST_SUCCESS) {
                    // 解析api
                    Gson gson = new Gson();
                    PhotoViewJson photoViewJson = gson.fromJson(result, PhotoViewJson.class);
                    obj = photoViewJson;
                    String title = photoViewJson.getTitle();
                    ArrayList<PhotoShow> photoShows = photoViewJson.getPhotoShow();

                    UtilsLog.i(TAG, "getPhotos success!");
                } else {
                    UtilsLog.i(TAG, "getPhotos fail!");
                }
                if (listener != null) {
                    listener.onAppRequest(code, message, obj);
                }
            }
        });
    }

    /**
     * 上传用户打开APP记录
     *
     * @param uid
     * @param role
     * @param client
     * @param appver
     * @param content
     */
    public void launchLog(int uid, int role, int client, String appver, String content, final OnAppRequestListener listener){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put(AppConstants.PARAM_UID,uid + "");
        params.put("role",role+"");
        params.put("client",client+"");
        params.put("appver",appver);
        params.put("content",content);
        sendVolleyRequestString(params, AppContext.getInstance().getMainGateWay() + URL.LAUNCHLOG, new OnSendRequestListener() {
            @Override
            public void onSendRequest(int code, String message, String result) {
                if (listener != null) {
                    listener.onAppRequest(code, message, result);
                }
            }
        });
    }


}