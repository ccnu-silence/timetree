package com.yey.kindergaten.receive;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.util.ExitAppUtils;

/**
 * 连接监听
 * Created by zy on 2015/3/9.
 */
public class HuanxinConnectionListener implements EMConnectionListener {

    @Override
    public void onConnected() { }

    @Override
    public void onDisconnected(final int error) {
        if (error == EMError.USER_REMOVED) {
            // 显示帐号已经被移除
        } else if (error == EMError.CONNECTION_CONFLICT) {
            AppContext.getInstance().quitLogout();
            ExitAppUtils.getInstance().exit();
        } else if (error == EMError.NONETWORK_ERROR){
            return;
        }
//      else {
//          if (NetUtils.hasNetwork(AppContext.getInstance())){
//
//          }  // 连接不到聊天服务器
//          else {
//
//          }  // 当前网络不可用，请检查网络设置
//
//      }
    }

}
