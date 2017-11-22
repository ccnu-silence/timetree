package com.yey.kindergaten.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    private final static String TAG = "TimeUtil";

    /**
     * yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentTimeYMD() {
        String time = "";
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = new java.util.Date();
            time = format.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "NumberFormatException");
        }
        return time;
    }

    /**
     * yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static String getYMDTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    /**
     * yyyy-MM-dd HH
     *
     * @return
     */
    public static String getCurrentTime() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-ddHH");
        java.util.Date date = new java.util.Date();
        return format.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm
     *
     * @return
     */
    public static String getYMDHM() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date();
        return format.format(date);
    }

    /**
     * yy-MM-dd HH:mm
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getYMDHMS() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        return format.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getYMDHMSS() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        return format.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String getMoreTime(long time) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        return format.format(date);
    }

    /**
     * HH:mm
     *
     * @param time
     * @return
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    ////////////////////////   <!-- 格式转换 -->   ////////////////////////
    /**
     * yyyy年MM月dd日 格式转换
     *
     * @param date - yyyy-MM-dd HH:mm:ss
     * @return     - yyyy年MM月dd日
     */
    public static String getYMD(String date) {
        try {
            Date now = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            now = f.parse(date);

            SimpleDateFormat f2 = new SimpleDateFormat("yyyy年MM月dd日");
            String nowtime = f2.format(now);
            return nowtime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * yyyy-MM-dd HH:mm 格式转换
     *
     * @param date - yyyy-MM-dd HH:mm:ss
     * @return     - yyyy-MM-dd HH:mm
     */
    public static String getYMDhm(String date){
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = f.parse(date);
            SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String nowtime = f2.format(now);
            return nowtime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * yyyy-MM-dd 格式转换
     *
     * @param time - yyyy-MM-dd HH:mm:ss
     * @return     - yyyy-MM-dd 增加今天
     */
    public static String getGrowRecentTime(String time)  {

        String result = "";
        try {
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
            long timesamp = date.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            result = "";
            Date today = new Date(System.currentTimeMillis());
            Date otherDay = new Date(timesamp);
            int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

            switch (temp) {
                case 0:
                    result = "今天";
                    break;
                default:
                    result = getGrowYMDTime(timesamp);
                    break;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * yyyy-MM-dd 替换 - 为 月
     *
     * @param time
     * @return
     */
    public static String getGrowYMDTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String growtime = format.format(new Date(time));
        return growtime.substring(growtime.indexOf("-") + 1).replace("-", "月");
    }

    /**
     * HH:mm 格式转换
     *
     * @param time - yyyy-MM-dd HH:mm:ss
     * @return     - HH:mm
     */
    public static String getGrowDayYMDTime(String time) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = f.parse(time);
            SimpleDateFormat f2 = new SimpleDateFormat("HH:mm");
            String nowtime = f2.format(now);
            return nowtime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符型转化成date类型(date只包含年月日)
     *
     * @param time - yyyy-MM-dd
     * @return     - date (long)
     */
    public static long StringToDate(String time) {
        if (time!=null && !time.equals("")) {
            Date date = new Date();
            String YmdTime = time.substring(0, 10).trim();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                date = format.parse(YmdTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timesamp = date.getTime();

            return timesamp;
        } else {
            return 0;
        }
    }

    ////////////////////////   <!-- 增加提示格式 -->   ////////////////////////

    private static Calendar mCalendar = Calendar.getInstance();
    /**
     * 获取昨天时间的最小值：
     *
     * @return
     */
    public static long getYesterdayMinTimeMillis() {

        long currTime = System.currentTimeMillis();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 0, 0, 0);
        long minToday = mCalendar.getTimeInMillis() - 24 * 3600 * 1000;

        return minToday;
    }

    /**
     * 获取昨天时间的最大值：
     *
     * @return
     */
    public static long getYesterdayMaxTimeMillis() {
        long currTime = System.currentTimeMillis();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 23, 59, 59);
        long minToday = mCalendar.getTimeInMillis() - 24 * 3600 * 1000;

        return minToday;
    }

    /**
     * yyyy-MM-dd HH:mm:ss 增加昨天、今天
     *
     * @param time
     * @return
     */
    public static String getChatTime(String time) {
        String result = "";
        try {
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (time == null || time.length() == 0){return "";}
            date = format.parse(time);
            long timesamp = date.getTime();

            /*SimpleDateFormat sdf = new SimpleDateFormat("dd");
            result = "";
            Date today = new Date(System.currentTimeMillis());
            Date otherDay = new Date(timesamp);
            int temp = Integer.parseInt(sdf.format(today))
                    - Integer.parseInt(sdf.format(otherDay));*/

            if (timesamp > getYesterdayMaxTimeMillis()) {
                result = "今天 " + getHourAndMin(timesamp);
            } else if (timesamp > getYesterdayMinTimeMillis() && timesamp < getYesterdayMaxTimeMillis()) {
                result = "昨天 " + getHourAndMin(timesamp);
            } else {
                result = getYMDhm(time);
            }
            // 按下述方法，上个月的的昨天会出错
            /*switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                result = getYMDhm(time);
                break;
            }*/

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * yyyy-MM-dd HH:mm:ss 增加前天、昨天、今天
     *
     * @param time
     * @return
     */
    public static String getChatTime2(String time)  {
        String result = "";
        try {
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
            long timesamp = date.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            result = "";
            Date today = new Date(System.currentTimeMillis());
            Date otherDay = new Date(timesamp);
            int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

            switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;
            default:
                result = time;
                break;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * yyyy-MM-dd HH:mm:ss 增加前天、昨天、今天
     *
     * @param time
     * @return
     */
    public static String getRecentTime(String time)  {

        String result = "";
        try {
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
            long timesamp = date.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            result = "";
            Date today = new Date(System.currentTimeMillis());
            Date otherDay = new Date(timesamp);
            int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

            switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                result = getYMDTime(timesamp);
                break;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * yy-MM-dd HH:mm 增加前天、昨天、今天
     *
     * @param timesamp
     * @return
     */
    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
        case 0:
            result = "今天 " + getHourAndMin(timesamp);
            break;
        case 1:
            result = "昨天 " + getHourAndMin(timesamp);
            break;
        case 2:
            result = "前天 " + getHourAndMin(timesamp);
            break;

        default:
            result = getTime(timesamp);
            break;
        }

        return result;
    }

    ////////////////////////   <!-- 其他 -->   ////////////////////////
    /**
     * 获取 GMT 格式时间戳
     *
     * @return GMT 格式时间戳
     */
    public static String getGMTDate() {
        SimpleDateFormat formater = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formater.format(new Date());
    }

    /**
     * 判断该 birthday(YYYY-MM-dd)是否是本月生日
     *
     * @param birthday
     * @return
     */
    public static boolean getBirthday(String birthday) {
        String nowMouth = null;
        String brithdayMouth = null;
        try {
            // 当前的时间
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = new java.util.Date();
            String today = format.format(date);
            nowMouth = today.substring(5, 7);
            brithdayMouth = birthday.substring(5, 7);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.i("TimeUtil","getBirthday fail");
        }
        // android.util.Log.i("TimeUtil","nowMouth:" + nowMouth);
        // android.util.Log.i("TimeUtil","brithdayMouth:" + brithdayMouth);

        if (nowMouth!=null && nowMouth.equals(brithdayMouth)) {
            return true;
        }
        return false;
    }

}
