package com.yey.kindergaten.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.RelationShipBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.SharedPreferencesHelper;

import java.util.List;

/**
 * Created by zy on 2015/3/11.
 */
public class HuanxinService extends Service{

    private int state;
    private String type;
    private int uid ;
    private String relation;
    private String isRegedit ;
    private int action;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//      System.out.println("start service。。。。。");
        if (intent!=null && intent.getExtras()!=null) {
            Log.i("updateHx", "start service。。。。。");
            uid = intent.getIntExtra(AppConstants.PARAM_UID,0);
            type = intent.getStringExtra("state");
            relation = intent.getStringExtra("relation");
            isRegedit = intent.getStringExtra("regedit");
            action = intent.getIntExtra("action",0);
            Log.i("updateHx", "type is value>>>>>>>>>>" + type);
            if (type.equals(AppConstants.HUANXIN_LOGIN)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        huanxinLogin(uid, Integer.valueOf(relation == null || relation.length() == 0 ? "0" : relation), "al1M0Ak3sG6");
                    }
                }).start();
            } else if (type.equals(AppConstants.HUANXIN_REGEDIT)) {
                regeditHuanxin(uid, "al1M0Ak3sG6", Integer.valueOf(relation));
            } else {

            }
        } else {
            Log.i("updateHx", "service intent is null。。。。。");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 更新设备号，通知服务端是否发送透传。
     *
     * @param account
     * @param relationShip
     */
    private void upDateClientId(final int account, final int relationShip){
        Log.i("updateHx", "更新设备号ID.....................");
        String clientid = SharedPreferencesHelper.getInstance(this).getString(AppConstants.CLIENTID, "");
        com.yey.kindergaten.net.AppServer.getInstance().updateDeviceId(com.yey.kindergaten.net.AppServer.getInstance().getAccountInfo().getUid(), clientid, relationShip, 0, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == com.yey.kindergaten.net.AppServer.REQUEST_SUCCESS) {
                    Log.i("updateHx", "更新设备号成功.....................完成");
                    updateRemoteHuanxinState(account, relationShip, "注册成功");
                } else {
                    Log.i("updateHx", "更新设备号失败.....................完成");
                }
             }
         });
    }

   /**
    * 注册环信
    *
    * @param account
    * @param password
    * @param relationShip
    */
    private void regeditHuanxin(final int account, final String password, final int relationShip) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    String name = account + "a" + String.valueOf(relationShip);
                    Log.i("updateHx", "环信注册....................." + name);
                    EMChatManager.getInstance().createAccountOnServer(name, password);
                    Log.i("updateHx", "环信注册成功....................." + name);
                    state = 1; // 表示注册环信成功
                    updateLocalHxState(relationShip);
                    huanxinLogin(account, relationShip, password);
                } catch (final EaseMobException e) {
                    state = 0; // 表示注册环信失败
                    if (e!=null) {
                        int errorCode = e.getErrorCode();
                        if (errorCode == EMError.NONETWORK_ERROR) {
                            Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                        } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                            huanxinLogin(account, relationShip, password);
                            state = 1; // 表示注册环信成功
                        }
                    }
                    updateLocalHxState(relationShip);
                }
            }
        }).start();
    }

    /**
     * 登陆环信
     *
     * @param currentPassword
     */
    public void huanxinLogin( final int account, final int relation, final String currentPassword) {
        final String currentUsername = account + "a" + relation;
        Log.i("updateHx", "huanxinLogin...............currentUsername: " + currentUsername);
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.i("updateHx", "环信登陆成功.....................");
                // 登陆成功，保存用户名密码
                AppContext.getInstance().setUserName(currentUsername);
                AppContext.getInstance().setPassword(currentPassword);
                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    // conversations in case we are auto login
                    EMChatManager.getInstance().loadAllConversations();
                    // 仅仅在园长或者老师注册环信-->登陆环信时告知服务器发送透传消息。
                    state = 1;
                    upDateClientId(account, relation);
                    // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members), sdk会把群组存入到内存和db中
//                  EMGroupManager.getInstance().getGroupsFromServer();
                } catch (Exception e) {
                    Log.i("updateHx", "环信登陆异常....................." + e.getCause());
                    if (isRegedit != null) {
                        upDateClientId(account, relation);
                    } else {
                        HuanxinService.this.stopSelf();
                    }
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onProgress(int progress, String status) { }

            @Override
            public void onError(final int code, final String message) {
                Log.i("updateHx", "onError+环信登陆异常.....................code: " + code + "message: " + message);
            }
        });

    }

    /**
     * 更新本地数据库中的环信注册状态
     *
     * @param relationShip
     */
    private void updateLocalHxState(int relationShip) {
        try {
            List<RelationShipBean> list = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
            if (list!=null && list.size()!=0) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRelationship() == relationShip) {
                        RelationShipBean bean = new RelationShipBean();
                        bean = list.get(i);
                        bean.setHxregtag(state);
                        list.set(i,bean);
                    }
                }
            }
            DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新服务器环信状态
     *
     * @param account
     * @param relationShip
     * @param note
     */
    private void updateRemoteHuanxinState(int account, int relationShip, String note) {
        Log.i("updateHx", "更新环信状态>>>>>>>>>>>>>>>>>>>");
        com.yey.kindergaten.net.AppServer.getInstance().updateHxState(account, relationShip, state, "注册成功", new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                    Log.i("updateHx", " 更新设备号成功>>>>>>>>>>>>>>>>");
                    HuanxinService.this.stopSelf();
                } else {
                    Log.i("updateHx", " 更新设备号成功>>>>>>>>>>>>>>>>");
                    HuanxinService.this.stopSelf();
                }
            }
        });
    }

}
