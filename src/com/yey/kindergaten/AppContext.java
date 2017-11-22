package com.yey.kindergaten;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

import com.bokecc.sdk.mobile.util.HttpUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.mato.sdk.proxy.Proxy;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yey.kindergaten.activity.LoginActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.LeaveSchoolBean;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.bean.Twitter;
import com.yey.kindergaten.bean.Twitter.comments;
import com.yey.kindergaten.bean.TwitterSelf;
import com.yey.kindergaten.bean.TwitterSelf.CommentsSelf;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.HomeFragement;
import com.yey.kindergaten.huanxin.DemoHXSDKHelper;
import com.yey.kindergaten.huanxin.bean.User;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.receive.HuanxinConnectionListener;
import com.yey.kindergaten.service.ContactsService;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.CrashHandler;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.MethodsCompat;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.StringUtils;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author chaowen
 * @version 1.0
 * @created 2013-6-13
 */
public class AppContext extends Application{
    public static final String DBNAME = "kindergaten";
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    private Thread.UncaughtExceptionHandler mOnRuntimeError;

    /** 判断上传动作是否还在进行 */
    private boolean isAmShow = false;

    /** 微信支付 */
    private IWXAPI msgApi;

    /** 上传界面保存数据 */
    private String type;
    private String lifetype;
    private String terms;
    private Term   term;
    private LifePhoto photo;
    private List<String>uidlist = new ArrayList<String>();
    private String albumId;
    private String imgType;
    public static List<Activity> activitys = new ArrayList<Activity>();
    private final static String TAG = "AppContext";

    public void addActivity(Activity activity) {
        // activitys.clear();
        activitys.add(activity);
    }

    public void  finishActivitys () {
        if (activitys!=null && activitys.size()!=0) {
            for (int i = 0; i < activitys.size(); i++) {
                activitys.get(i).finish();
            }
            activitys.clear();
        }
    }

    public HashMap<String,List<Photo>>listHashMap = null;

    public HashMap<String, List<Photo>> getListHashMap() {
        return listHashMap;
    }

    public void setListHashMap(HashMap<String, List<Photo>> listHashMap) {
        this.listHashMap = listHashMap;
    }

    public boolean isRefresh;

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    private String isFromMain = "fromlife";
    private static AppContext mApplication;
    private NotificationManager mNotificationManager;
    public static final ArrayList<String> selectphoto = new ArrayList<String>();
    private static AccountInfo accountInfo;
    public List<Activity> tabActivityList = new ArrayList<Activity>();
    public static ArrayList<Photo> checkList = new ArrayList<Photo>();
    public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

    /** 当前用户nickname,为了苹果推送不是userid而是昵称 */
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        updateDb();
//      DbHelper.flag = false;
        initData();
        initLeaveSchoolData();

        // 微信api初始化
        msgApi = WXAPIFactory.createWXAPI(this, null);

        // imageLoader初始化
        ActivityManager activityManager = (ActivityManager) this.getSystemService(this.ACTIVITY_SERVICE);          // 内存最大值（单位：KB）
        setMinHeapSize( (activityManager.getMemoryClass() * 1024 * 1024 * 20));
        PushManager.getInstance().initialize(this.getApplicationContext());
        InitImageLoad();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        // 环信初始化信息
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase("com.yey.kindergaten")) {
            UtilsLog.i("huanxinState", "enter the service process!");
            // "com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        hxSDKHelper.onInit(mApplication);
        EMChatManager.getInstance().addConnectionListener(new HuanxinConnectionListener());

        // MAA加速代理
        Proxy.supportWebview(this);
        Proxy.start(this);

        // 使用CC视频SDK进行播放、上传、下载过程中，如果遇到与网络通信相关的问题，可通过设置HTTP日志的级别获取通信信息
        HttpUtil.LOG_LEVEL = HttpUtil.HttpLogLevel.DETAIL;

        Glide.setup(new GlideBuilder(this)
                .setDecodeFormat(DecodeFormat.PREFER_RGB_565)); // Glide默认的Bitmap格式是RGB_565，只能设置一次
    }

    public IWXAPI getMsgApi() {
        if (msgApi == null) {
            msgApi = WXAPIFactory.createWXAPI(this, null);
        }
        return msgApi;
    }

    public void setMsgApi(IWXAPI msgApi) {
        this.msgApi = msgApi;
    }

    /**
     * 建表，并将Parents的数据封装到LeaveScholBean中
     */
    private void initLeaveSchoolData() {
        try {
            String currentTime = TimeUtil.getYMDHMS();
            // 创建表LeaveSchoolBean
            DbHelper.getDB(AppContext.getInstance()).createTableIfNotExist(LeaveSchoolBean.class);
            // 将Parent数据移植到LeaveSchoolBean表中
            if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Parent.class)) {
                List<Parent> parents = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
                if (parents != null && parents.size()!=0) {
                    for (int i = 0; i < parents.size(); i++) {
                        Parent parent = parents.get(i);
                        if (parent!=null) {
                            LeaveSchoolBean bean = DbHelper.getDB(AppContext.getInstance()).findFirst(LeaveSchoolBean.class, WhereBuilder.b("uid", "=", parent.getUid()));
                            if (bean == null) {
                                LeaveSchoolBean leaveSchoolBean = new LeaveSchoolBean(parent.getRealname(), parent.getAvatar(),
                                        parent.getUid(), currentTime, 0, "", parent.getCname(), parent.getCid());
                                DbHelper.getDB(AppContext.getInstance()).save(leaveSchoolBean);
//                                UtilsLog.i(TAG, "save leaveschoolbean ok , uid is : " + parent.getUid());
                            }
                        }
                    }
                }
            }

//            List<LeaveSchoolBean> list = DbHelper.getDB(mApplication).findAll(Selector.from(LeaveSchoolBean.class).orderBy("date", true));
//            String currentTime = TimeUtil.getYMDHMS();
//            long currentYmdTime;
//            String historyTime;
//            long historyYmdTime;
//
//            if (list!=null && list.size()!=0) { // 表示不是第一次初始化数据，更新日期数据
//                historyTime = list.get(0).getDate(); // 查询是按照时间大小排序，所以第一条应该是最新的时间
//                historyYmdTime = TimeUtil.StringToDate(historyTime);
//                currentYmdTime = TimeUtil.StringToDate(currentTime);
//
//                if (historyYmdTime < currentYmdTime) { // 表示现在的时间比表中的历史时间大，则更新表中的时间到最新时间
//                    List<Parent> parents = DbHelper.getDB(mApplication).findAll(Parent.class);
//                    if (parents!=null && parents.size()!=0) {
//                        for (int i = 0; i < list.size(); i++) {
//                            LeaveSchoolBean bean = list.get(i);
//                            bean.setDate(currentTime);
//                            bean.setIsLeave(0); // 0表示未离园
//                            // 检测到时间是第二天，重新初始化数据
//                            // 条件是：必须是数据库中的时间中最大的时间小于当前天
//                            DbHelper.getDB(mApplication).update(bean, WhereBuilder.b("uid", "=", bean.getUid()));
//                        }
//                    }
//                }
//            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    public static void setMinHeapSize(long size) {
        try {
            Class<?> cls = Class.forName("dalvik.system.VMRuntime");
            Method getRuntime = cls.getMethod("getRuntime");
            Object obj = getRuntime.invoke(null); // obj就是Runtime
            if (obj == null) {
                System.err.println("obj is null");
            } else {
                System.out.println(obj.getClass().getName());
                Class<?> runtimeClass = obj.getClass();
                Method setMinimumHeapSize = runtimeClass.getMethod("setMinimumHeapSize", long.class);
                setMinimumHeapSize.invoke(obj, size);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void InitImageLoad() {
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().destroy();
        }
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), FileUtils.getSDRoot() + "yey/imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(mApplication)
                .memoryCacheExtraOptions(480, 800) // 新加（缓存文件最大的长宽）
                .diskCacheExtraOptions(480, 800, null) // 保存到硬盘的缓存文件最大的长宽
                .threadPoolSize(3) // 线程池内加载的数量
                .memoryCacheSize(2 * 1024 * 1024) // 新加（缓存超过这个大小会释放）
                .discCacheSize(20 * 1024 * 1024) // 新加（硬盘最大缓存数）
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
//              .memoryCache(new FIFOLimitedMemoryCache(ValuesOf(AppUtils.getmem_TOLAL())-1024*4)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .discCacheFileNameGenerator(new Md5FileNameGenerator()) // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .discCacheFileCount(100) // 缓存的文件数量
                .discCache(new UnlimitedDiscCache(cacheDir)) // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(mApplication, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build(); // 开始构建
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public int ValuesOf(long size){
        int ia = (int)size;
        Long lb = new Long(size);
        int ib = lb.intValue();
        return ia;
    }

    public String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    public String getIsFromMain() {
        return isFromMain;
    }

    public void setIsFromMain(String isFromMain) {
        this.isFromMain = isFromMain;
    }

    public boolean isAmShow() {
        return isAmShow;
    }

    public void setAmShow(boolean isAmShow) {
        this.isAmShow = isAmShow;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLifetype() {
        return lifetype;
    }

    public void setLifetype(String lifetype) {
        this.lifetype = lifetype;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public LifePhoto getPhoto() {
        return photo;
    }

    public void setPhoto(LifePhoto photo) {
        this.photo = photo;
    }

    public List<String> getUidlist() {
        return uidlist;
    }

    public void setUidlist(List<String> uidlist) {
        this.uidlist = uidlist;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    private void updateDb() {
        DbHelper.getDB(mApplication);
    }

    private void initData() {
        initContacts();
    }

    private Contacts contacts = new Contacts();

    public Contacts getContacts() {
        if (contacts == null) {
            UtilsLog.i(TAG, "contacts is null,initContacts");
            contacts = new Contacts();
            initContacts();
        }
        return contacts;
    }

    public Contacts getContactsData() {
        initContacts();
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        if(this.contacts!=null){
            this.contacts = null;
        }
        this.contacts = contacts;
    }

    public void initContacts(){
        AccountInfo info = AppContext.getInstance().getAccountInfo();
        if (info.getUid()!=0) {
            try {
                List<PublicAccount> plist = DbHelper.getDB(mApplication).findAll(PublicAccount.class);
                List<Friend> friendlist = DbHelper.getDB(mApplication).findAll(Friend.class);
                List<Teacher> teacherlist = DbHelper.getDB(mApplication).findAll(Teacher.class);
//                List<Children> parentlist = DbHelper.getDB(mApplication).findAll(Children.class);
                List<Children> parentlist = DbHelper.getDB(mApplication).findAll(Children.class);
                List<Classe> classe = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Classe.class).orderBy("OrderNo", false));
                if (plist == null) {
                    plist = new ArrayList<PublicAccount>();
                }
                if (friendlist == null) {
                    friendlist = new ArrayList<Friend>();
                }
                if(teacherlist == null) {
                    teacherlist = new ArrayList<Teacher>();
                }
                if (parentlist == null) {
                    parentlist = new ArrayList<Children>();
                }
                if (classe == null) {
                    classe = new ArrayList<Classe>();
                }
                this.contacts.setPublics(plist);
                this.contacts.setFriends(friendlist);
                this.contacts.setTeachers(teacherlist);
                this.contacts.setClasses(classe);
                if (info.getRole() == 2) {
                    this.contacts.setParents(parentlist);
                }
                if (info.getRole() == 0) {
                    this.contacts.setClasses(classe);
                }
                if (contacts.getClasses() == null) {
                    System.out.println("fuck----tou");
                } else {
                    System.out.println("you----zhi");
                }
//              if (info.getRole() == 1) {
//                  this.contacts.setClasses(classe);
//              }
            } catch (DbException e) {
                e.printStackTrace();
            }
            int userid = SharedPreferencesHelper.getInstance(mApplication).getInt(AppConfig.USERID, 0);
            int role = SharedPreferencesHelper.getInstance(mApplication).getInt(AppConfig.ROLE, 0);
            int kid = SharedPreferencesHelper.getInstance(mApplication).getInt(AppConfig.KID,0);
        }
    }

    public synchronized static AppContext getInstance() {
        return mApplication;
    }

    /**
     * 获取App安装包信息
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    /**
     * 检测网络是否连接或正在连接
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 判断缓存数据是否可读
     * @param cachefile
     * @return
     */
    private boolean isReadDataCache(String cachefile)
    {
        return readObject(cachefile) != null;
    }

    /**
     * 读取对象
     * @param file
     * @return
     * @throws IOException
     */
    public Serializable readObject(String file){
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable)ois.readObject();
        } catch(FileNotFoundException e) {
            UtilsLog.i(TAG, "readObject FileNotFoundException");
        } catch(Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {}
            try {
                fis.close();
            } catch (Exception e) {}
        }
        return null;
    }

    /**
     * 判断缓存是否存在
     * @param cachefile
     * @return
     */
    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    public void setProperty(String key,String value){
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key){
        return AppConfig.getAppConfig(this).get(key);
    }
    public void removeProperty(String...key){
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取App唯一标识
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 保存对象
     * @param ser
     * @param file
     * @throws IOException
     */
    public boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {}
            try {
                fos.close();
            } catch (Exception e) {}
        }
    }

    /**
     * 检测当前系统声音是否为正常模式
     * @return
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache(){
        // 清除sd卡的缓存
        FileUtils.deleSDFolder(new File(Environment.getExternalStorageDirectory()+File.separator + "yey/"));;
        // 清除webview缓存
        /*File file = CacheManager.getCacheFileBaseDir();
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        } */
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        // 清除数据缓存
        clearCacheFolder(getFilesDir(),System.currentTimeMillis());
        clearCacheFolder(getCacheDir(),System.currentTimeMillis());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            clearCacheFolder(MethodsCompat.getExternalCacheDir(this), System.currentTimeMillis());
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 清除缓存目录
     * @param dir 目录
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    public Properties getProperties(){
        return AppConfig.getAppConfig(this).get();
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        UtilsLog.i(TAG, "setAccountInfo and uid is : " + accountInfo.getUid());
        this.accountInfo = accountInfo;
    }

    public AccountInfo getAccountInfo() { // 问题
        if (this.accountInfo == null || accountInfo.getUid() == 0) {
            UtilsLog.i(TAG, "getAccountInfo uid is o, read from sp ");
            this.accountInfo = new AccountInfo();
            this.accountInfo.setUid(SharedPreferencesHelper.getInstance(mApplication).getInt(AppConstants.PARAM_UID, 0));
            this.accountInfo.setRole(SharedPreferencesHelper.getInstance(mApplication).getInt(AppConfig.ROLE, 0));
            this.accountInfo.setKid(SharedPreferencesHelper.getInstance(mApplication).getInt(AppConfig.KID, 0));
            this.accountInfo.setNum(SharedPreferencesHelper.getInstance(mApplication).getInt(AppConfig.NUM, 0));
            this.accountInfo.setCid(SharedPreferencesHelper.getInstance(mApplication).getInt(AppConstants.PARAM_CID, 0));
            this.accountInfo.setNickname(SharedPreferencesHelper.getInstance(mApplication).getString(AppConstants.PREF_NICKNAME, ""));
        }
        return this.accountInfo;
    }

    public String getDeviceId() {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout() {
        // delDatabase();
        cleanPref();
        clearWebViewCache();
        if (ContactsService.isruning) {
            Intent intent = new Intent(AppContext.this, ContactsService.class);
            AppContext.getInstance().stopService(intent);
            //AppContext.getInstance().stopService(intent);
        }
        setContacts(null);
        this.accountInfo = null;
        HomeFragement.getNewFlag = 1;
        logout(new EMCallBack() {
            @Override
            public void onSuccess() { UtilsLog.i(TAG, "hxsdkhelper loginout success"); }
            @Override
            public void onError(int i, String s) { UtilsLog.i(TAG, "hxsdkhelper loginout onerror"); }
            @Override
            public void onProgress(int i, String s) { UtilsLog.i(TAG, "hxsdkhelper loginout onprogress"); }
        });
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {
        // 清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath() + AppConfig.APP_CACAHE_WEBVIEW);

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");

        // 删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            FileUtils.deleSDFolder(webviewCacheDir);
        }
        // 删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            FileUtils.deleSDFolder(appCacheDir);
        }
    }

    // 多设备退出登录（不支持多设备登陆，直接被踢出） package com.yey.kindergaten.receive.HuanxinConnectionListener
    public void quitLogout() {

        AppServer.getInstance().loginout(AppServer.getInstance().getAccountInfo().getUid(), AppServer.getInstance().getAccountInfo().getRelationship(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                UtilsLog.i(TAG, "loginout complete, code is: " + code);
            }
        });

        logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("message", "kickout");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

//      Toast.makeText(AppContext.getInstance(), "你的账号已在其他设备上登录", Toast.LENGTH_LONG).show();
        // 如果把所有的应用都finish掉，那么在loginActivity中不会调用登出接口了
        Activity activit = AppManager.getAppManager().getActivity(MainActivity.class);
        AppManager.getAppManager().finishActivity(activit);
    }

    public void cleanPref() {
        DbHelper.closedb();
        SharedPreferencesHelper.getInstance(mApplication).setString(AppConstants.PARAM_ACCOUNT, "");
        SharedPreferencesHelper.getInstance(mApplication).setInt(AppConfig.NUM, 0);
        SharedPreferencesHelper.getInstance(mApplication).setInt(AppConfig.ROLE, 0);
        SharedPreferencesHelper.getInstance(mApplication).setInt(AppConfig.KID, 0);
        SharedPreferencesHelper.getInstance(mApplication).setInt(AppConstants.PREF_ISLOGIN, 0);
        SharedPreferencesHelper.getInstance(mApplication).setInt(AppConstants.PARAM_UID, 0);
        SharedPreferencesHelper.getInstance(mApplication).setBoolean(AppConstants.FLAG_FIRST_LOGINSUCCESS, false);
    }

    /**
     * 退出登录删除数据库的记录
     */
    public void delDatabase() {
        try {
            if (DbHelper.getDB(mApplication) == null) {return;}
            DbHelper.getDB(mApplication).deleteAll(TwitterSelf.class);
            DbHelper.getDB(mApplication).deleteAll(Twitter.class);
            DbHelper.getDB(mApplication).deleteAll(CommentsSelf.class);
            DbHelper.getDB(mApplication).deleteAll(comments.class);
            // DbHelper.getDB(mApplication).deleteAll(MessagePublicAccount.class);
            // DbHelper.getDB(mApplication).deleteAll(MessageRecent.class);
            DbHelper.getDB(mApplication).deleteAll(Teacher.class);
            DbHelper.getDB(mApplication).deleteAll(Children.class);
            DbHelper.getDB(mApplication).deleteAll(Parent.class);
            DbHelper.getDB(mApplication).deleteAll(Friend.class);
            // DbHelper.getDB(mApplication).deleteAll(Services.class);
            // DbHelper.getDB(mApplication).deleteAll(PublicAccount.class);
            DbHelper.getDB(mApplication).close();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取应用是否在打开运行
     * isTopActivity
     * @return
     * boolean
     */
    public boolean isTopActivity() {
        accountInfo = null;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo>  tasksInfo = am.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if ("com.yey.kindergaten".equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    MediaPlayer mMediaPlayer;
    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }

    /**
     * 判断一个服务是否是正在运行
     * @param context
     * @param serviceName serivce全名
     * @return 正在运行返回true 不运行返回false
     */
    public static boolean isServiceRunning(Context context,String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> infos = am.getRunningServices(20);
        for (RunningServiceInfo info:infos) {
            //包名+类名
            String myServiceName = info.service.getClassName();
            if (myServiceName.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public String getMainGateWay() {
//        return "http://t.kmapp.zgyey.com/";
        return SharedPreferencesHelper.getInstance(AppContext.getInstance()).getString(AppConstants.PARAM_MAINGATEWAY, URL.SERVER_URL);
    }

    // 环信的代码
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        return hxSDKHelper.getContactList();
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        hxSDKHelper.setContactList(contactList);
    }

    /**
     * 获取当前登陆用户名
     *
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }

    /**
     * 设置用户名
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     *
     * @param pwd
     */
    public void setPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final EMCallBack emCallBack) {
        UtilsLog.i(TAG, "hxsdkhelper begin to loginout");
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(emCallBack);
    }

    /**
     *
     */
    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

}
