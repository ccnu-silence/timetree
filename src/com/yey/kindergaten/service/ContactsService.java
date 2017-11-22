package com.yey.kindergaten.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.LeaveSchoolBean;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Msgtypes;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.inter.OnAooRequestParentListener;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.task.SimpleTask;
import com.yey.kindergaten.task.TaskExecutor;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.Session;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by zy on 2015/3/11.
 */

public class ContactsService extends Service{

    public AppContext appcontext;
    public static boolean isruning = false;
    public static int contactsInfoFlag = AppConstants.GET_CONTACTS_DEFAULT;

    private int role;
    private AccountInfo accountinfo;
    private final static String TAG = "ContactsService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UtilsLog.i(TAG, "into onStartCommand");
        accountinfo = AppServer.getInstance().getAccountInfo();
        appcontext = AppContext.getInstance();
        role = accountinfo.getRole();
        UtilsLog.i(TAG, "get accountinfo and appcontext: " + accountinfo + "/" + appcontext);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isruning = false;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        isruning = true;
        prepareData();
    }

    int scode = 0;
    private void prepareData() {
        final long starttime = System.currentTimeMillis();
        UtilsLog.i(TAG, "into prepareData is start");
        Session session = Session.getSession();
        // session.put(AppConstants, value);

        if (accountinfo!=null) {
            UtilsLog.i(TAG, "accountinfo is not null, uid is :" + accountinfo.getUid() + "");
            SimpleTask<Integer> task = new SimpleTask<Integer>() {

                @Override
                protected Integer doInBackground() {
                    UtilsLog.i(TAG, "start to getContacts interface doInBackground");
                    AppServer.getInstance().getContacts(accountinfo.getUid(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            scode = code;
                            if (code == AppServer.REQUEST_SUCCESS) {
                                UtilsLog.i(TAG,"getContacts interface success");
                                List<Teacher> teachers;
                                List<Classe> classeslist;
                                List<PublicAccount> pms;
                                List<Friend> friends;
                                List<Msgtypes> msgtypes;
                                Contacts contacts = (Contacts)obj;

                                if (contacts !=null) {
                                    appcontext.setContacts(contacts);
                                    teachers = contacts.getTeachers();
                                    classeslist = contacts.getClasses();
                                    pms = contacts.getPublics();

                                    try {
                                        List<PublicAccount> publicAccountList = DbHelper.getDB(AppContext.getInstance()).findAll(PublicAccount.class);
                                        Map<Integer,Integer> id_firstlook = new HashMap<Integer, Integer>();
                                        if (publicAccountList!=null && pms!=null) {

                                            for (PublicAccount publicAccount:publicAccountList) {
                                                id_firstlook.put(publicAccount.getPublicid(), publicAccount.getIsfirstlook());
                                            }

                                            for (int i = 0; i < pms.size(); i++) {
                                                PublicAccount publicAccount = pms.get(i);
                                                if (id_firstlook.containsKey(publicAccount.getPublicid())) {
                                                    publicAccount.setIsfirstlook(id_firstlook.get(publicAccount.getPublicid()));
                                                    pms.set(i, publicAccount);
                                                } else {
                                                    UtilsLog.i(TAG,"PublicAccount.class don't contain" + pms.get(i).getNickname());
                                                }
                                            }
                                        }
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                        UtilsLog.i(TAG,"getDB findAll PublicAccount DbException");
                                    }

                                    msgtypes = contacts.getMsgtypes();
                                    friends = contacts.getFriends();

                                    UtilsLog.i(TAG,"contacts teachers classeslist getPublics msgtypes friends: " + teachers + "/" + classeslist + "/" + pms + "/" + msgtypes + "/" + friends);

                                    try {
                                        if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Teacher.class)) {
                                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                                            UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll Teacher ");
                                        }
                                        if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Classe.class)) {
                                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Classe.class);
                                            UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll Classe ");
                                        }
                                        if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Children.class)) {
                                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Children.class);
                                            UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll Children ");
                                        }
                                        if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Friend.class)) {
                                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Friend.class);
                                            UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll Friend ");
                                        }
                                        if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(PublicAccount.class)) {
                                            DbHelper.getDB(AppContext.getInstance()).deleteAll(PublicAccount.class);
                                            UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll PublicAccount ");
                                        }
                                        if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Msgtypes.class)) {
                                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Msgtypes.class);
                                            UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll Msgtypes ");
                                        }
                                        UtilsLog.i(TAG, "getContacts interface success deletetable OK ");
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(teachers);
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(classeslist);
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(friends);
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(pms);
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(msgtypes);
                                        UtilsLog.i(TAG, "getContacts interface success saveAlltable OK ");

                                        contactsInfoFlag = AppConstants.GET_CONTACTS_OK;

                                        // 当主页没有最新公众号消息时，取出一些历史消息到主页
                                        // ParentGetMessage(); // 刷新消息

                                        if (role == 2) {
                                            DbHelper.getDB(AppContext.getInstance()).saveAll(contacts.getParents());
                                            UtilsLog.i(TAG, "role is not 0, start to getParent");
                                            //getParent();
                                            getTeachersAndParentsByCid();
                                        } else if (role == 0) {
                                            UtilsLog.i(TAG, "role is 0, start to getClasssListByKid");
                                            if (contacts.getClasses() == null) {
                                                getClassListByKid();
                                            }
                                        } else if(role == 1) {
                                            getParentsAndClassByKid();
                                        }
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                        UtilsLog.i(TAG, "getContacts success ,but deletetable or saveAlltable DbException:"
                                                + e.getMessage() + "/" + e.getCause());
                                    }
                                } else {
                                    UtilsLog.i(TAG, "getContacts success , but contacts is null");
                                }
                                long endtime = System.currentTimeMillis();
                                UtilsLog.i(TAG, "getContacts success , cost:" + (endtime - starttime) + "ms");
                            } else {
                                contactsInfoFlag = AppConstants.GET_CONTACTS_FAIL;
                                // 通讯录加载失败
                                List<Teacher> teachers = DbHelper.getDataList(Teacher.class);
                                List<Classe> classeslist = DbHelper.getDataList(Classe.class);
                                List<PublicAccount> pms = DbHelper.getDataList(PublicAccount.class);
                                List<Msgtypes> msgtypes = DbHelper.getDataList(Msgtypes.class);

                                Contacts contacts = new Contacts();
                                contacts.setClasses(classeslist);
                                contacts.setTeachers(teachers);
                                contacts.setPublics(pms);
                                contacts.setMsgtypes(msgtypes);
                                appcontext.setContacts(contacts);
                            }
                            // 更新一下最近消息表的头像,名字
                            DbHelper.initSql();
                            // updateRecentHead();
                            // isloadContact = true;
                            // getNewFlag = 0;
                            // getNewMessage();
                        }
                    });
                    return scode;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if (DbHelper.flag) {
                        DbHelper.flag = false;
                        UtilsLog.i(TAG, "set flag is default false");
                    }
                    // 刷新通讯录
                    if (result == AppServer.REQUEST_SUCCESS) {
                        postEvent(AppEvent.HOMEFRAGMENT_REFRESH_CONTACT);
                    }
                }
            };
            task.execute();
        }

    }

    public void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                UtilsLog.i(TAG, "PostThreadId:" + Thread.currentThread().getId());
            }
        }).start();
    }

    public void getTeachersAndParentsByCid(){
        AccountInfo account = AppContext.getInstance().getAccountInfo();
        AppServer.getInstance().getTeachersAndParentsByCid(account.getUid(), account.getCid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == 0) {
                    UtilsLog.i(TAG, "getTeachersAndParentsByCid success ");
//                  ShowToast("获取成功");
                } else {
                    UtilsLog.i(TAG, "getTeachersAndParentsByCid fail ");
                }
            }
        });
    }

    /**
     * 老师刷新通讯录
     */
    private void getParentsAndClassByKid(){
        AppServer.getInstance().getParentsByTeacherKid(accountinfo.getUid(), accountinfo.getKid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    UtilsLog.i(TAG, "getParentsAndClassByKid success ");
                    List<Parent>list = (List<Parent>) obj;
                    if (list!=null) {
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                            UtilsLog.i(TAG, "deleteAll or deleteAll Parent ok" );
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                            UtilsLog.i(TAG, "deleteAll or saveAll Parent ok" );
                        } catch (DbException e) {
                            e.printStackTrace();
                            UtilsLog.i(TAG, "deleteAll or saveAll fail,because DbException" );
                        }
                    }
                    initLeaveSchoolData();
                    postEvent(AppEvent.TEACHERFRFRAGMENT_RELOADDATA);
                } else {
                    UtilsLog.i(TAG, "getParentsAndClassByKid fail ");
                }
            }
        });
    }

    /**
     * 修改头像
     */
    List<MessageRecent> mdata = new ArrayList<MessageRecent>();
    private void updateRecentHead() {
        List<SimpleTask> list = new ArrayList<SimpleTask>();

        if (mdata!=null && mdata.size()!=0){
            for (MessageRecent recent: mdata) {
                if (recent.getTypeid() == -1) {
                    list.add(getUpdateHeadTask(recent.getFromId()+"",recent.getAction()));
                }
            }

            TaskExecutor.OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
            for (int i=0;i<list.size();i++){
                executor.put(list.get(i));
            }
            executor.start();
        }
    }

    private Friend tofriend = null;
    private PublicAccount topublicAccount = null;
    private SimpleTask<String> getUpdateHeadTask(final String id,final int action) {
        UtilsLog.i(TAG, "getUpdateHeadTask is start");
        SimpleTask<String> simple = new SimpleTask<String>() {

            @Override
            protected String doInBackground() {
                if (action == AppConstants.PUSH_ACTION_FRIENDS) {
                    AppServer.getInstance().findUser(AppServer.getInstance().getAccountInfo().getUid(), id, 1, new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if (code == AppServer.REQUEST_SUCCESS) {
                                UtilsLog.i(TAG, "findUser is ok");
                                List<Friend> flist = (List<Friend>) obj;
                                if (flist.size() > 0) {
                                    tofriend = flist.get(0);
                                    postEvent(AppEvent.HOMEFRAGMENT_REFRESH_HEAD);
                                }
                            }
                        }
                    });
                } else if (action == AppConstants.PUSH_ACTION_PUBLICACCOUNT) {
                    AppServer.getInstance().viewInfo(AppServer.getInstance().getAccountInfo().getUid() + "", 0 + "", id + "", 3, new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if (code == 0) {
                                topublicAccount = (PublicAccount) obj;
                                postEvent(AppEvent.HOMEFRAGMENT_REFRESH_PUBLIC_HEAD);
                            }
                        }
                    });
                }
                return id;
            }

            @Override
            protected void onCancelled() { }

            @Override
            protected void onPostExecute(String result) { }

        };
        UtilsLog.i(TAG, "getUpdateHeadTask is finish");
        return simple;
    }

    private void getParent(){
        UtilsLog.i(TAG, "getParent is start");
        List<SimpleTask> list = new ArrayList<SimpleTask>();
        Contacts contants = AppContext.getInstance().getContacts();
        List<Classe> alist = contants.getClasses();
        if (alist == null || alist.size() == 0) {
            try {
                List<Classe>classes = DbHelper.getDB(ContactsService.this).findAll(Classe.class);
                UtilsLog.i(TAG, "getParent getDB findall Classe");
                if (classes == null) {
                    UtilsLog.i(TAG, "getParent getDB findall Classe, but classes is null");
                    classes = new ArrayList<Classe>();
                }
                contants.setClasses(classes);
                if (alist!=null) {
                    alist.clear();
                    alist.addAll(classes);
                }
                AppContext.getInstance().setContacts(contants);
            } catch (DbException e) {
                e.printStackTrace();
                UtilsLog.i(TAG, "getParent getDB findall Classe fail, because DbException");
            }
        }
        List<Parent>childlist = null;
        try {
            childlist = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
            UtilsLog.i(TAG, "getParent getDB findall Parent");
        } catch (DbException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "getParent getDB findall Parent fail, because DbException");
        }

        if (DbHelper.flag && alist !=null) {
            UtilsLog.i(TAG, "first to updateDB,flag :" + DbHelper.flag + "");
            /*try{
                if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Parent.class)) {
                    DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                    UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll Parent ");
                }
            } catch (DbException e) {
                e.printStackTrace();
                UtilsLog.i(TAG, "getParent getDB deleteAll Parent fail, because DbException");
            }*/
            for (int i = 0; i < alist.size(); i++) {
                list.add(getTask(i));
            }
            TaskExecutor.OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
            for (int i = 0; i < alist.size(); i++) {
                executor.put(list.get(i));
            }
            executor.start();
        } else {
            if ((childlist == null || childlist.size() == 0) && alist != null) {
                for (int i = 0; i < alist.size(); i++) {
                    list.add(getTask(i));
                }
                TaskExecutor.OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
                for (int i = 0; i < alist.size(); i++) {
                    executor.put(list.get(i));
                }
                executor.start();
            }
        }

        if (DbHelper.flag){
            DbHelper.flag = false;
            UtilsLog.i(TAG, "set flag is default false");
        }

        UtilsLog.i(TAG, "getParent is finish");
    }

    /**
     * 获取任务，按照队列执行
     * @param postion
     * @return
     */
    private List<Parent>list;
    private SimpleTask<Integer> getTask(final int postion){
        UtilsLog.i(TAG, "GetParentByCid interface is start");
        final long starttime = System.currentTimeMillis();
        Contacts contants = AppContext.getInstance().getContacts();
        final List<Classe> alist = contants.getClasses();
        SimpleTask<Integer> simple = new SimpleTask<Integer>() {
            @Override
            protected Integer doInBackground() {
                AppServer.getInstance().GetParentByCid(accountinfo.getUid(), alist.get(postion).getCid(), new OnAooRequestParentListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj, Object obj2) {
                        if (code == 0) {
                            UtilsLog.i(TAG, "GetParentByCid interface is success");
                            Parent[]parents = (Parent[]) obj2;
                            list = new ArrayList<Parent>();
                            for (int i = 0; i < parents.length; i++) {
                                list.add(parents[i]);
                            }
                            if (list!=null && list.size()!=0) {
                                for (int j = 0; j < list.size(); j++) {
                                    list.get(j).setCname(alist.get(postion).getCname());
                                    list.get(j).setCid(alist.get(postion).getCid());
                                }
                            } else {
//                              list = new ArrayList<Parent>();
//                              Parent parent = new Parent();
//                              parent.setCname(alist.get(postion).getCname());
//                              parent.setCid(alist.get(postion).getCid());
//                              list.add(parent);
                            }
                            try {
                                List<Parent>parentslist = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
                                UtilsLog.i(TAG, "getDB findall parent");
                                if (list!=null && list.size() > 0) {
                                    if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Parent.class)) {
                                        DbHelper.getDB(AppContext.getInstance()).delete(Parent.class, WhereBuilder.b("cid", "=", alist.get(postion).getCid()));
                                        UtilsLog.i(TAG, "getContacts interface success,tableIsExist deleteAll Parent ");
                                    }
                                    DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                                }

                            } catch (DbException e) {
                                e.printStackTrace();
                                UtilsLog.i(TAG, "getDB findall parent fail, DbException");
                            } catch (NullPointerException e) {
                                UtilsLog.i(TAG, "getDB findall parent fail, NullPointerException");
                                return;
                            }
                            long entime = System.currentTimeMillis();
                            UtilsLog.i(TAG, "getPublicLateMessage success,cost :" + (entime-starttime) + "ms");
                        } else {
                            UtilsLog.i(TAG, "GetParentByCid interface is fail");
                            list = new ArrayList<Parent>();
                        }
                    }
                });
                return postion;
            }

            @Override
            protected void onCancelled() { }

            @Override
            protected void onPostExecute(Integer result) { }

        };
        UtilsLog.i(TAG, "getTask is finish");
        return simple;
    }

    public void getClassListByKid() {
        AppServer.getInstance().getClassesByKid(accountinfo.getUid(), accountinfo.getKid(), accountinfo.getRole(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    UtilsLog.i(TAG,"getClassesByKid interface success");
                    List<Classe> list = (List<Classe>) obj;
                    Contacts contacts = AppContext.getInstance().getContacts();
                    if (list!=null) {
                        contacts.setClasses(list);
                        try {
                            DbHelper.getDB(ContactsService.this).deleteAll(Classe.class);
                            DbHelper.getDB(ContactsService.this).saveAll(list);
                        } catch (DbException e) {
                            UtilsLog.i(TAG, "getClassesByKid interface success,but DbException");
                            e.printStackTrace();
                        }
                    }
                    AppContext.getInstance().setContacts(contacts);
                } else {
                    UtilsLog.i(TAG,"getClassesByKid interface fail");
                    // ShowToast(message);
                }
            }
        });
    }

    /**
     * 初始化离园数据
     * 每天的离园数据不一样，每次检查表中有无数据
     * 表中有数据，查看日期是不是昨天。是昨天重新刷新数据。
     */
    private void initLeaveSchoolData(){
//        try {
//            List<LeaveSchoolBean> list = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(LeaveSchoolBean.class).orderBy("date", true));
//
//            String currentTime = TimeUtil.getYMDHMS();
//            long  currentYmdTime;
//            String historyTime ;
//            long  historyYmdTime ;
//
//            if (list!=null && list.size()!=0) { // 表示不是第一次初始化数据，更新日期数据
//                historyTime = list.get(0).getDate(); // 查询是按照时间大小排序，所以第一条应该是最新的时间
//                historyYmdTime = TimeUtil.StringToDate(historyTime);
//                currentYmdTime = TimeUtil.StringToDate(currentTime);
//
//                if (historyYmdTime<currentYmdTime) { // 表示现在的时间比表中的历史时间大，则更新表中的时间到最新时间
//
//                    List<Parent> parents = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
//
//                    if (parents!=null && parents.size()!=0) {
//                        for (int i = 0; i < list.size(); i++) {
//                            LeaveSchoolBean bean = list.get(i);
//                            bean.setDate(currentTime);
//                            bean.setIsLeave(0); // 0表示未离园
//                            // 检测到时间是第二天，重新初始化数据
//                            // 条件是：必须是数据库中的时间中最大的时间小于当前天
//                            DbHelper.getDB(AppContext.getInstance()).update(bean, WhereBuilder.b("uid", "=", bean.getUid()));
//                        }
//                    }
//                }
//            } else {
//                if (DbHelper.getDB(AppContext.getInstance()).tableIsExist(Parent.class)) {
//                    List<Parent> parents = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
//                    if (parents != null && parents.size()!=0) {
//                        for (int i = 0; i < parents.size(); i++) {
//                            Parent parent = parents.get(i);
//                            LeaveSchoolBean bean = new LeaveSchoolBean(parent.getRealname(), parent.getAvatar(),
//                                    parent.getUid(), currentTime, 0, "", parent.getCname(), parent.getCid());
//                            DbHelper.getDB(AppContext.getInstance()).save(bean);
//                        }
//                    }
//                }
//            }
//        } catch (DbException e){
//            UtilsLog.i(TAG, "initLeaveSchoolData DbException");
//        }
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
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
