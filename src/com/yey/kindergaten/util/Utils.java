package com.yey.kindergaten.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yey.kindergaten.AppContext;

import java.security.MessageDigest;
import java.util.List;

public class Utils {

    private final static String TAG = "Utils";
    /**
     * Hides the input method.
     * 
     * @param context context
     * @param view The currently focused view
     * @return success or not.
     */
    public static boolean hideInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return false;
    }

    /**
     * Show the input method.
     * 
     * @param context context
     * @param view The currently focused view, which would like to receive soft keyboard input
     * @return success or not.
     */
    public static boolean showInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.showSoftInput(view, 0);
        }

        return false;
    }

    public static boolean isContactsNull(AppContext appcontext, int role) {
        UtilsLog.i(TAG, "ready to contactsService,and contacts is : " + appcontext.getContacts() + "");
        UtilsLog.i(TAG, "ready to contactsService,and judge the Parameter,Teachers is : " + appcontext.getContacts().getTeachers() + "");
        UtilsLog.i(TAG, "ready to contactsService,and judge the Parameter,Classes is : " + appcontext.getContacts().getClasses() + "");
        //UtilsLog.i(TAG, "ready to contactsService,and judge the Parameter,Friends is : " + appcontext.getContacts().getFriends() + "");
        //UtilsLog.i(TAG, "ready to contactsService,and judge the Parameter,Msgtypes is : " + appcontext.getContacts().getMsgtypes() + "");
        UtilsLog.i(TAG, "ready to contactsService,and judge the Parameter,parents is : " + appcontext.getContacts().getParents() + "");
        UtilsLog.i(TAG, "ready to contactsService,and judge the Parameter,Publics is : " + appcontext.getContacts().getPublics() + "");

        if (role == 0){
            /*|| appcontext.getContacts().getFriends() == null || appcontext.getContacts().getFriends().size() == 0*/
            if (appcontext.getContacts() == null
                    || appcontext.getContacts().getTeachers()==null || appcontext.getContacts().getTeachers().size() == 0
                    || appcontext.getContacts().getClasses()==null || appcontext.getContacts().getClasses().size() == 0
                    || appcontext.getContacts().getPublics() == null || appcontext.getContacts().getPublics().size() == 0 ) {
                UtilsLog.i(TAG, "start contactsService");
                return true;
            } else {
                UtilsLog.i(TAG, "jump the contactsService");
                return false;
            }
        } else if (role == 1) {
            // || appcontext.getContacts().getFriends() == null || appcontext.getContacts().getFriends().size() == 0
            if (appcontext.getContacts() == null
                    || appcontext.getContacts().getTeachers()==null || appcontext.getContacts().getTeachers().size() == 0
                    || appcontext.getContacts().getClasses()==null || appcontext.getContacts().getClasses().size() == 0
                    || appcontext.getContacts().getPublics() == null || appcontext.getContacts().getPublics().size() == 0
//                    || appcontext.getContacts().getParents() == null || appcontext.getContacts().getParents().size() == 0
                    ){
                UtilsLog.i(TAG, "start contactsService");
                return true;
            } else {
                UtilsLog.i(TAG, "jump the contactsService");
                return false;
            }
        } else {
            if (appcontext.getContacts() == null
                    || appcontext.getContacts().getTeachers()==null || appcontext.getContacts().getTeachers().size() == 0
                    || appcontext.getContacts().getPublics() == null || appcontext.getContacts().getPublics().size() == 0
                    || appcontext.getContacts().getParents() == null || appcontext.getContacts().getParents().size() == 0) {
                UtilsLog.i(TAG, "start contactsService");
                return true;
            } else {
                UtilsLog.i(TAG, "jump the contactsService");
                return false;
            }
        }
    }


    public static float pixelToDp(Context context, float val) {
        float density = context.getResources().getDisplayMetrics().density;
        return val * density;
    }
    
    public static String getHashedFileName(String url) {   
        if (url == null || url.endsWith("/" )) {
            return null ;
        }

        String suffix = getSuffix(url);
        StringBuilder sb = null;
        
        try {
            MessageDigest digest = MessageDigest. getInstance("MD5");
            byte[] dstbytes = digest.digest(url.getBytes("UTF-8")); // GMaFroid uses UTF-16LE
            sb = new StringBuilder();
            for (int i = 0; i < dstbytes.length; i++) {
                sb.append(Integer. toHexString(dstbytes[i] & 0xff));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (null != sb && null != suffix) {
            return sb.toString() + "." + suffix;
        }
       
        return null;
    }
    
    private static String getSuffix(String fileName) {
        int dot_point = fileName.lastIndexOf( ".");
        int sl_point = fileName.lastIndexOf( "/");
        if (dot_point < sl_point) {
            return "" ;
        }
        
        if (dot_point != -1) {
            return fileName.substring(dot_point + 1);
        }
        
        return null;
    }
    
    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param intent The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();

        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }
    
    public static String getPP(){
   	 String time2 = TimeUtil.getCurrentTime();
	     String pp = StringUtils.getDigestStr(time2+"youeryuan");
	     return pp;
   }

}
