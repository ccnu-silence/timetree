package com.yey.kindergaten.task;

/**
 * <p>
 * FileName: MainService.java
 * </p>
 * <p>
 * Description: 后台总调度服务类
 * <p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>
 *          Modification History
 */
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.AppManager;
import com.yey.kindergaten.activity.ServiceScheduleActorActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.ServiceFragement;
import com.yey.kindergaten.inter.OnAooRequestParentListener;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.task.TaskExecutor.OrderedTaskExecutor;
import com.yey.kindergaten.util.UtilsLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainService extends Service implements Runnable {

    public static boolean isrun = false;
    private AccountInfo accountInfo; 
    List<Parent>list;
    private final static String TAG = "MainService";
    public static int allsize;
    public static int lastsize;
    public static int midsize;
    private static ArrayList<Task> allTask = new ArrayList<Task>();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    // 添加窗口到集合中
    public static void addActivity(Activity ia) {
        AppManager.getAppManager().addActivity(ia);
    }
    
    public static void addFragment(Fragment fragment) {
        AppManager.getAppManager().addFragment(fragment);
    }
    
    public static void removeFragment(Fragment fragment) {
        AppManager.getAppManager().removeFragment(fragment);
    }

    public static void removeActivity(Activity ia) {
        AppManager.getAppManager().finishActivity(ia);
    }

    // 添加任务
    public static void newTask(Task ts) {
        allTask.add(ts);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isrun = false;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        isrun = true;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (isrun) {
            if (allTask.size() > 0) {
                doTask(allTask.get(0));
                UtilsLog.i(TAG, "alltask have value start to dotast");
            } else {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    List<Services> serverslist = null;
    @SuppressWarnings("rawtypes")
    private void doTask(Task ts) {
        final Message messageTask = hand.obtainMessage();
        messageTask.what = ts.getTaskID();
        switch (ts.getTaskID()) {
            case TaskType.TS_SERVICE_INIT: // 初始化服务
                AccountInfo info = AppServer.getInstance().getAccountInfo();
                AppServer.getInstance().getServiceMenu(info.getUid(), info.getRole(), new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        List<Services> serverslist = new ArrayList<Services>();
                        if (code == AppServer.REQUEST_SUCCESS) {
                            serverslist = (List<Services>) obj;
                            messageTask.obj = serverslist;
//                            List<Services> servicesList = DbHelper.getService();
                            List<Services> servicesList = new ArrayList<Services>();
                            try {
                                servicesList = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Services.class).orderBy("orderno"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            List<Services> sameList = new ArrayList<Services>();
//                          if (serverslist!=null && serverslist.size() > 0 && servicesList!=null && servicesList.size() > 0) {
//                              if (serverslist.size() > servicesList.size()) {
//                                  serverslist.removeAll(servicesList);
//                                  servicesList.addAll(serverslist);
//                              } else if (servicesList.size() > serverslist.size()) {
//                                  servicesList.removeAll(serverslist); // 取剩下的list
//                                  sameList.addAll(serverslist); // 保存在sameList中。
//                                  servicesList = DbHelper.getService();
//                                  servicesList.removeAll(sameList);
//                              }
//                              for (int i = 0; i < serverslist.size(); i++) {
//                                  Services services = servicesList.get(i);
//                                  Services newService = serverslist.get(i);
//                                  newService.setIsfirstlook(services.getIsfirstlook());
//                                  serverslist.set(i, newService);
//                              }
//                              try {
//                                  DbHelper.getDB(AppContext.getInstance()).deleteAll(Services.class);
//                                  DbHelper.getDB(AppContext.getInstance()).saveAll(serverslist);
//                              } catch (DbException e) {
//                                  e.printStackTrace();
//                              }
//                          }
                            if (serverslist.size() > 0) { //（** 不应取并集 **）
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
                            try {
                                DbHelper.getDB(AppContext.getInstance()).deleteAll(Services.class);
                                DbHelper.getDB(AppContext.getInstance()).saveAll(serverslist);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            hand.sendMessage(messageTask);
                        }
                    }
                });
                break;
            case TaskType.TS_CONTACTS_PARENT_INIT:
                List<SimpleTask> list = new ArrayList<SimpleTask>();
                Contacts contants = AppContext.getInstance().getContacts();
                List<Classe> alist = contants.getClasses();
                if (alist == null || alist.size() == 0) {
                    try {
                        List<Classe>classes = DbHelper.getDB(this).findAll(Classe.class);
                        if (classes == null) {
                            classes = new ArrayList<Classe>();
                        }
                        contants.setClasses(classes);
                        alist.clear();
                        alist.addAll(classes);
                        AppContext.getInstance().setContacts(contants);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                List<Parent>childlist = null;
                try {
                    childlist = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if ((childlist == null || childlist.size() == 0) && alist!=null) {
                    for (int i = 0; i < alist.size(); i++) {
                        list.add(getTask(i));
                    }
                    OrderedTaskExecutor executor = TaskExecutor.newOrderedExecutor();
                    for (int i= 0; i < alist.size(); i++) {
                        executor.put(list.get(i));
                    }
                    executor.start();
                }
                break;
            case TaskType.FRIENDSTER_UPLOAD:
                break;
            case TaskType.TS_CONTACTS_LAST_INIT_Four:
                break;
        }
        allTask.remove(ts);
       
    }

    private final Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TaskType.TS_SERVICE_INIT: // 获取服务
                    Fragment fragment = AppManager.getAppManager().getFragmentByName("ServiceFragement");
                    if (fragment!=null) {
                       ServiceFragement service = (ServiceFragement) AppManager.getAppManager().getFragmentByName("ServiceFragement");
                       service.refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_CONTACTS_PARENT_INIT:
                    Activity activity = AppManager.getAppManager().getActivityByName("ServiceScheduleActorActivity");
                    if (activity!=null) {
                       ServiceScheduleActorActivity parentactivity = (ServiceScheduleActorActivity) AppManager.getAppManager().getActivityByName("ServiceScheduleActorActivity");
//                     parentactivity.refresh(msg.what,msg.obj,msg.arg1);
                    }
                    break;
            }
        };
    };
    
    public void createSDCardDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = Environment.getExternalStorageDirectory();
            // 得到一个路径，内容是sdcard的文件夹路径和名字
            String path = sdcardDir.getPath() + "/yey/kindergaten/uploadimg/";
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }
        } else {
            return;
        }
    }
    
    /**
     * 获取任务，按照队列执行
     * @param postion
     * @return
     */
    private SimpleTask<Integer> getTask(final int postion) {
        accountInfo = AppServer.getInstance().getAccountInfo();
        Contacts contants = AppContext.getInstance().getContacts();
        final List<Classe> alist = contants.getClasses();
        SimpleTask<Integer> simple = new SimpleTask<Integer>() {
            @Override
            protected Integer doInBackground() {
                AppServer.getInstance().GetParentByCid(accountInfo.getUid(), alist.get(postion).getCid(), new OnAooRequestParentListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj, Object obj2) {
                        if (code == 0) {
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
                                if (list!=null && list.size() > 0) {
                                    if (parentslist!=null) {
                                        if (list.get(0).getUid()!=parentslist.get(0).getUid()) {
                                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                                        }
                                    } else {
                                        DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                                    }
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        } else {
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
        return simple;
    }

}
