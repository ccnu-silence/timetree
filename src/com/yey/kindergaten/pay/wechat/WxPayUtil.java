package com.yey.kindergaten.pay.wechat;

import android.util.Log;
import android.util.Xml;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.WxEntity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 处理微信支付
 * @author zy
 *
 */
public class WxPayUtil {
    private WxEntity entity;

    public WxPayUtil(WxEntity entity) {
        this.entity = entity;
    }

    public void sendPayReq() {
        AppContext.getInstance().getMsgApi().registerApp(entity.getAppid());
        AppContext.getInstance().getMsgApi().sendReq(genPayReq());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static Map<String,String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (!"xml".equals(nodeName)) {
                            //实例化student对象
                            xml.put(nodeName,parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    /** 生成签名 */
    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        // entity有则用entity的，没有则用默认时光树的。
        if (entity!=null && entity.getKey()!=null) {
            sb.append(entity.getKey());
        } else {
            sb.append(WechatConstants.API_KEY);
        }

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    /**
    * 重新签名
    * @param params
    * @return
    */
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        // entity有则用entity的，没有则用默认时光树的。
        if (entity!=null && entity.getKey()!=null) {
            sb.append(entity.getKey());
        } else {
            sb.append(WechatConstants.API_KEY);
        }

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

    /**
     * 获取参数
     * @return
     */
    private PayReq genPayReq() {
        PayReq req = new PayReq();
        req.appId = entity.getAppid();
        req.partnerId = entity.getMch_id();
        req.prepayId = entity.getPrepay_id();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = entity.getNonce_str();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);

        return req;
    }

    /***
     * 获取本地ip地址,暂时没用
     * @return
     */
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
               NetworkInterface intf = en.nextElement();
               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                   InetAddress inetAddress = enumIpAddr.nextElement();
                   if (!inetAddress.isLoopbackAddress()) {
                       return inetAddress.getHostAddress().toString();
                   }
               }
           }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

}
