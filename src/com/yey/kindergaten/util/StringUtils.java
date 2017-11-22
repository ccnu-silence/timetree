package com.yey.kindergaten.util;

import android.content.Context;

import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.Upload;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/** 
 * 字符串操作工具包
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils 
{
	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");
	
	  /**
     * 加密算法
     * @param src
     * @return
     */
    public static String getSHA1(byte[] src) {
        StringBuffer sb = new StringBuffer();
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            md.update(src);
            byte[] bts = md.digest();
            int len = bts.length;
            String tmp;
            for (int i = 0; i < len; i++) {
                tmp = (Integer.toHexString(bts[i] & 0xFF));
                if (tmp.length() == 1) {
                    sb.append("0");
                }
                sb.append(tmp);
            }
        } catch (NoSuchAlgorithmException e) {
            return sb.toString();
        }
        return sb.toString();
    }

    public static String getValue(String sourceStr, String containStr) {
        if (sourceStr == null || containStr == null || sourceStr.equals("") || containStr.equals("")) {
            return "";
        }
        String str = "";
        if (sourceStr.contains(containStr)) {
            int indexstart = sourceStr.indexOf(containStr);
            String endString = sourceStr.substring(indexstart + containStr.length());
            if (endString.length() > 1) {
                if (endString.contains("&")) {
                    str = endString.substring(0, endString.indexOf("&"));
                } else {
                    str = endString;
                }
            }
        }
        return str;
    }
	
	
	/**
	 * 将字符串转位日期类型
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 以友好的方式显示时间
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if(time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();
		
		//判断是否是同一天
		String curDate = dateFormater2.format(cal.getTime());
		String paramDate = dateFormater2.format(time);
		if(curDate.equals(paramDate)){
			int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
			if(hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"分钟前";
			else 
				ftime = hour+"小时前";
			return ftime;
		}
		
		long lt = time.getTime()/86400000;
		long ct = cal.getTimeInMillis()/86400000;
		int days = (int)(ct - lt);		
		if(days == 0){
			int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
			if(hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"分钟前";
			else 
				ftime = hour+"小时前";
		}
		else if(days == 1){
			ftime = "昨天";
		}
		else if(days == 2){
			ftime = "前天";
		}
		else if(days > 2 && days <= 10){ 
			ftime = days+"天前";			
		}
		else if(days > 10){			
			ftime = dateFormater2.format(time);
		}
		return ftime;
	}
	
	/**
	 * 判断给定字符串时间是否为今日
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate){
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if(time != null){
			String nowDate = dateFormater2.format(today);
			String timeDate = dateFormater2.format(time);
			if(nowDate.equals(timeDate)){
				b = true;
			}
		}
		return b;
	}
	
	/**
	 * 判断给定字符串是否空白串。
	 * 空白串是指由空格、制表符、回车符、换行符组成的字符串
	 * 若输入字符串为null或空字符串，返回true
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		if(email == null || email.trim().length()==0) 
			return false;
	    return emailer.matcher(email).matches();
	}
	/**
	 * 字符串转整数
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try{
			return Integer.parseInt(str);
		}catch(Exception e){}
		return defValue;
	}
	/**
	 * 对象转整数
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if(obj==null) return 0;
		return toInt(obj.toString(),0);
	}
	/**
	 * 对象转整数
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try{
			return Long.parseLong(obj);
		}catch(Exception e){}
		return 0;
	}
	/**
	 * 字符串转布尔值
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try{
			return Boolean.parseBoolean(b);
		}catch(Exception e){}
		return false;
	}
	
	
	
	
	//将十六进制Unicode编码字符串转换为中文字符串
		public static String UnicodeToString(String uniStr){
			char aChar;
			int len = uniStr.length();
			StringBuffer outBuffer = new StringBuffer(len);
			for (int x = 0; x < len;) {
				aChar = uniStr.charAt(x++);
				if (aChar == '\\') {
					aChar = uniStr.charAt(x++);
					if (aChar == 'u') {
						// Read the xxxx
						int value = 0;
						for (int i = 0; i < 4; i++) {
							aChar = uniStr.charAt(x++);
							switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException(
								"Malformed   \\uxxxx   encoding.");
							}
						}
						outBuffer.append((char) value);
					} else {
						if (aChar == 't')
							aChar = '\t';
						else if (aChar == 'r')
							aChar = '\r';
						else if (aChar == 'n')
							aChar = '\n';
						else if (aChar == 'f')
							aChar = '\f';
						outBuffer.append(aChar);
					}
				} else
					outBuffer.append(aChar);
			}
			return outBuffer.toString();
		}
		
		//中文字符串转换为十六进制Unicode编码字符串
		public static String StringToUnicode(String str) {  
			str = (str == null ? "" : str); 
			String tmp; 
			StringBuffer sb = new StringBuffer(1000); 
			char c; 
			int i, j; 
			sb.setLength(0); 
			for (i = 0; i < str.length(); i++) 
			{ 
			c = str.charAt(i); 
			sb.append("\\u"); 
			j = (c >>>8); //取出高8位 
			tmp = Integer.toHexString(j); 
			if (tmp.length() == 1) 
			sb.append("0"); 
			sb.append(tmp); 
			j = (c & 0xFF); //取出低8位 
			tmp = Integer.toHexString(j); 
			if (tmp.length() == 1) 
			sb.append("0"); 
			sb.append(tmp); 

			} 
			return (new String(sb)); 
	 
	    }  
		
		
		
		 public static  String getDigestStr(String info) 
			{    
				try {        
					byte[] res = info.getBytes();
					MessageDigest md = MessageDigest.getInstance("MD5");
					byte[] result = md.digest(res);
					for (int i = 0; i < result.length; i++) {
						md.update(result[i]);        
						}        
					byte[] hash = md.digest();        
					StringBuffer d = new StringBuffer("");
					for (int i = 0; i < hash.length; i++) 
					{           
						int v = hash[i] & 0xFF;
						if (v < 16) {        
							d.append("0");     
							}          
						d.append(Integer.toString(v, 16).toUpperCase());  
						}        
					return d.toString();   
					} catch (Exception e) {
						return null;   
				}
			}
		 
		 
		 public static int dip2px(Context context, float dipValue){ 
			 final float scale = context.getResources().getDisplayMetrics().density; 
			 return (int)(dipValue * scale + 0.5f); 
		 } 

		 public static int px2dip(Context context, float pxValue){ 
			 final float scale = context.getResources().getDisplayMetrics().density; 
			 return (int)(pxValue / scale + 0.5f); 
		 } 
		 
		 public static byte[] intToBytes(int n){ 
				byte[] b = new byte[4]; 
				for(int i = 0;i < 4;i++){ 
				b[i] = (byte) (0xff & (n >> (i * 8)));
				} 
				return b; 
			}

         public static  String changeListToString(List<Upload> list,List<Photo>photos){

             StringBuffer buffer = new StringBuffer();
             if(list!=null){
             for (int i=0;i<list.size();i++){
                 Upload upload = list.get(i);
                  if(upload.getModule().equals("Success")){
                      if(i==list.size()-1){
                          buffer.append(list.get(i).getUploadfilepath()) ;
                      }else {
                          buffer.append(list.get(i).getUploadfilepath()).append(",");
                      }
                  }
              }
             }else {
                 for (int i=0;i<photos.size();i++){

                         if(i==photos.size()-1){
                             buffer.append(photos.get(i).imgPath) ;
                         }else {
                             buffer.append(photos.get(i).imgPath).append(",");
                         }
                 }
             }
             return buffer.toString();
         }

    /**
     * 获取两个字符之间的字符串
     *
     * @param oldString
     * @param firstString
     * @param lastString
     */
    public static String getStringBetweenString(String oldString, String firstString, String lastString) {
        String newString = "";
        if (oldString!=null && !oldString.equals("")) {
            int firstIndex = oldString.indexOf(firstString);
            int lastIndex = oldString.lastIndexOf(lastString);
            newString = oldString.substring(firstIndex + 1, lastIndex);
        }
        return newString;
    }

    /**
     * 获取称呼
     *
     * @param role
     * @param relationship
     */
    public static String getCall(int role, int relationship) {
        String newName = "";
        if (role == AppConstants.PARENTROLE) {
            switch (relationship) {
                case 1:
                    newName = "爸爸";
                    break;
                case 2:
                    newName = "妈妈";
                    break;
                case 3:
                    newName = "爷爷";
                    break;
                case 4:
                    newName = "奶奶";
                    break;
                case 5:
                    newName = "外公";
                    break;
                case 6:
                    newName = "外婆";
                    break;
                case 7:
                    newName = "叔叔";
                    break;
                case 8:
                    newName = "阿姨";
                    break;
                default:
                    newName = "家长";
                    break;
            }
        } else {
            if (role == 0) {
                newName = "园长";
            } else {
                newName = "老师";
            }
        }
        return newName;
    }

}