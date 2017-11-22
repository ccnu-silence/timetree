package com.yey.kindergaten.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Looper;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.bean.SchedulesBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

import java.util.ArrayList;
import java.util.List;
/**
 * 服务类
 * @author zy
 * 用来检测本地数据库中是否存在还没有上传或者删除成功的日程
 * 并继续进行没有成功的动作
 */
public class ScheduleServiceUnbind extends Service{

    private List<SchedulesBean> listbean = new ArrayList<SchedulesBean>();
    private List<SchedulesBean> listflag = new ArrayList<SchedulesBean>();
    SchedulesBean bean = new SchedulesBean();
    boolean flag = false;
    private String realnames = null;
    Thread thread;
    int action = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
           action=intent.getIntExtra("action", 0);
           realnames = intent.getStringExtra("realnames");
           if (realnames == null) {
               realnames = " ";
           }
        }
        try {
            listbean = DbHelper.getDB(this).findAll(SchedulesBean.class,WhereBuilder.b("deleteflag", "=", "-1"));
            listflag = DbHelper.getDB(this).findAll(SchedulesBean.class, WhereBuilder.b("flag", "=", "-1"));
            if (action!=2) {
                for (int i = 0; i < listflag.size(); i++) {
                    if (listflag.get(i).getFlag().contains("-1")) {
                        System.out.println(bean.getNote() + "--action--" + action);
                        flag = true;
                        bean = listflag.get(i);
                        updateData(bean,flag);
                        thread.start();
                    } else {
                        flag = false;
                        if (thread!=null) {
                            thread.stop();
                        }
                    }
                }
            } else { // 删除状态下的数据库。不显示但是存在本地数据库中
                for (int i = 0; i < listbean.size(); i++) {
                    if (listbean.get(i).getDelete().contains("-1")) {
                        flag = true;
                        bean = listbean.get(i);
                        updateData(bean, flag);
                        thread.start();
                    }else{
                        flag=false;
                        if(thread!=null){
                            thread.stop();
                        }
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 线程中联网加载数据
     *
     * @param bean
     */
    public void uploadData(final SchedulesBean bean){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnectedOrConnecting()) {
            AppServer.getInstance().UploadSchedule(bean.getUid(), action, bean.getSheid(), bean.getDay(), bean.getTime(),
                bean.getTheme(), bean.getNote(), 0, bean.getPeople(), bean.getPeople(), realnames, new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (code == 0) {
                        bean.setFlag("0");
                        try {
                            DbHelper.getDB(ScheduleServiceUnbind.this).update(bean);
                            if (action == 2) {
                                DbHelper.getDB(ScheduleServiceUnbind.this).delete(bean);
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * 开辟线程用来检测本地数据库中还没完成的工作
     *
     * @param bean 数据库对象
     * @param flag 操作标志
     */
    public void updateData(final SchedulesBean bean, final boolean flag) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    Looper.prepare();   // 因为每个线程looper的时候，都需要prepare
                    uploadData(bean);
                    Looper.loop();      // 线程执行完后loop死循环。
                }
            }
        });
    }

//  public void StopService(final SchedulesBean bean,final boolean flag,final int action) {
//      new Thread(new Runnable() {
//          @Override
//          public void run() {
//              if (action == 2) {
//                  if (bean.getFlag()) {
//
//                  }
//              }
//          }
//      });
//  }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
