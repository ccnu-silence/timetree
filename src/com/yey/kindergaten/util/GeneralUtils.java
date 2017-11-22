package com.yey.kindergaten.util;
public class GeneralUtils {
	/**
     * <将YYYYMMDDHHmmss 转换为 YYYY-MM-DD hh:mm:ss> <功能详细描述>
     * 
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String splitToSecond(String str)
    {
        if (isNullOrZeroLenght(str) || str.length() != 14)
        {
            return str;
        }
        
        String strs = "";
        strs =
            str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10)
                + ":" + str.substring(10, 12) + ":" + str.substring(12, 14);
        return strs;
    }

public static String splitToPhotoTime(String time)
    {
        String date = "";
        if (time != null)
        {
            String[] sfm = time.split(" ");
            if (sfm.length > 1)
            {
                String[] nyr = sfm[0].split(":");
                for (int i = 0; i < nyr.length; i++)
                {
                    date += nyr
;
                    if (i != nyr.length - 1)
                        date += "-";
                }
                date += " ";
                date += sfm[1];
            }
        }
        return date;
    }
/**
     * 判断字符串是否为null或者0长度，字符串在判断长度时，先去除前后的空格,空或者0长度返回true,否则返回false
     * 
     * @param str 被判断的字符串
     * 
     * @return boolean
     */
    public static boolean isNullOrZeroLenght(String str)
    {
        return (null == str || "".equals(str)) ? true : false;
    }
}
