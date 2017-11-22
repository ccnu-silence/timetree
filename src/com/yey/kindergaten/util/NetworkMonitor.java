package com.yey.kindergaten.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 13-12-26.
 */
public class NetworkMonitor {

    public interface OnNetworkChangedListener{
        public void onNetworkConnected();
        public void onNetworkDisconnected();
    }
    private static NetworkMonitor mInstance;
    public static NetworkMonitor getInstance(Context context){
        if(mInstance == null){
            mInstance = new NetworkMonitor(context);
        }
        return mInstance;
    }
    private Context mContext;

    private NetworkReceiver mReceiver;
    private static List<OnNetworkChangedListener> mListenerList = new ArrayList<OnNetworkChangedListener>();

    public NetworkMonitor(Context context){
        mContext = context.getApplicationContext();
    }
    public boolean isNetworkConnected(){
        ConnectivityManager conn = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conn.getActiveNetworkInfo() == null ? false : conn.getActiveNetworkInfo().isConnected();
    }

    public boolean isWifi(){
        ConnectivityManager conn = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        if(info != null && info.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }
    public void regist(OnNetworkChangedListener listener){
        mListenerList.add(listener);
    }
    public void unRegist(OnNetworkChangedListener listener){
        if(mListenerList.contains(listener)){
            mListenerList.remove(listener);
        }
    }
    public void start(){
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkReceiver();
        mContext.registerReceiver(mReceiver, filter);
    }
    public void stop(){
        if(mReceiver != null){
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

    }
    public static class NetworkReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if(connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().isConnected()){
                for(OnNetworkChangedListener listener : mListenerList){
                    if(listener != null){
                        listener.onNetworkConnected();
                    }
                }
            }else{
                for(OnNetworkChangedListener listener : mListenerList){
                    if(listener != null){
                        listener.onNetworkConnected();
                    }
                }
            }

        }
    }
}
