package com.yey.kindergaten;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 应用程序异常类：用于捕获异常和提示错误信息
 *
 * @author chaowen 
 * @version 1.0
 * @created 2013-6-13
 */
public class AppException extends Exception implements UncaughtExceptionHandler{

    public static final String TAG = "AppException";
    private final static boolean Debug = false; // 是否保存错误日志

    /** 定义异常类型 */
    public final static byte TYPE_NETWORK   = 0x01;
    public final static byte TYPE_SOCKET    = 0x02;
    public final static byte TYPE_HTTP_CODE = 0x03;
    public final static byte TYPE_HTTP_ERROR= 0x04;
    public final static byte TYPE_XML       = 0x05;
    public final static byte TYPE_IO        = 0x06;
    public final static byte TYPE_RUN       = 0x07;

    private byte type;
    private int code;

    /** 系统默认的UncaughtException处理类 */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    AppContext appContext = null;

    private AppException(AppContext appcontext){
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.appContext = appcontext;
    }

    private AppException(byte type, int code, Exception excp) {
        super(excp);
        this.type = type;
        this.code = code;
        if (Debug) {
            this.saveErrorLog(excp);
        }
    }
    public int getCode() {
        return this.code;
    }
    public int getType() {
        return this.type;
    }

    /**
     * 保存异常日志
     *
     * @param excp
     */
    public void saveErrorLog(Exception excp) {
        String errorlog = "errorlog.txt";
        String savePath = "";
        String logFilePath = "";
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            // 判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Log/";
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                logFilePath = savePath + errorlog;
            }
            // 没有挂载SD卡，无法写文件
            if (logFilePath == "") {
                return;
            }
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile,true);
            pw = new PrintWriter(fw);
            pw.println("--------------------" + (new Date().toLocaleString()) + "---------------------");
            excp.printStackTrace(pw);
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) { pw.close(); }
            if (fw != null) { try { fw.close(); } catch (IOException e) { }}
        }
    }

    public static AppException http(int code) {
        return new AppException(TYPE_HTTP_CODE, code, null);
    }

    public static AppException http(Exception e) {
        return new AppException(TYPE_HTTP_ERROR, 0 ,e);
    }

    public static AppException socket(Exception e) {
        return new AppException(TYPE_SOCKET, 0 ,e);
    }

    public static AppException io(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof IOException) {
            return new AppException(TYPE_IO, 0 ,e);
        }
        return run(e);
    }

    public static AppException xml(Exception e) {
        return new AppException(TYPE_XML, 0, e);
    }

    public static AppException network(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof SocketException) {
            return socket(e);
        }
        return http(e);
    }

    public static AppException run(Exception e) {
        return new AppException(TYPE_RUN, 0, e);
    }

    /** 获取APP异常崩溃处理对象 */
    public static AppException getAppExceptionHandler(AppContext appContext){
        return new AppException(appContext);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        /* if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            Intent intent = new Intent(appContext.getApplicationContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    appContext.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
            // 退出程序
            AlarmManager mgr = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    restartIntent); // 1秒钟后重启应用
            AppManager.getAppManager().finishActivity();
        }    */
        restartApp();
    }

    // 重启应用
    public void restartApp() {
    Toast.makeText(appContext, "报错", Toast.LENGTH_LONG).show();
    Intent intent = new Intent();
    // 参数1：包名，参数2：程序入口的activity
    intent.setClassName(appContext.getPackageName(), "com.yey.kindergaten.MainActivity");
    PendingIntent restartIntent = PendingIntent.getActivity(
            appContext.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
    AlarmManager mgr = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
    restartIntent); // 1秒钟后重启应用
//  AppManager.getAppManager().finishAllActivity();
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        final Context context = AppManager.getAppManager().currentActivity();
        if (context == null) {
            return false;
        }

        final String crashReport = getCrashReport(context, ex);
        // 显示异常信息&发送报告
        /* new Thread() {
            public void run() {
                Looper.prepare();
                UIHelper.sendAppCrashReport(context, crashReport);
                Looper.loop();
            }
        }.start();*/
        return true;
    }

    /**
     * 获取APP崩溃异常报告
     * @param ex
     */
    private String getCrashReport(Context context, Throwable ex) {
        PackageInfo pinfo = ((AppContext)context.getApplicationContext()).getPackageInfo();
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
        exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE + "("+android.os.Build.MODEL + ")\n");
        exceptionStr.append("Exception: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return exceptionStr.toString();
    }

}
