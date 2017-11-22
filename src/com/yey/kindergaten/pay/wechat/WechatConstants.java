package com.yey.kindergaten.pay.wechat;

public class WechatConstants {

    // appid
    // 请同时修改  androidmanifest.xml里面，.PayActivityd里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
    public static final String APP_ID = "wxdcc245ed8aee4963";
    // 商户号
    public static final String MCH_ID = "1243905402";
    // API密钥，在商户平台设置
    public static final String API_KEY = "Z0G4YE61Y006013al2o2n2g20FuD8L1F";
    // 微信获取prepay_id的接口网关地址
    public static final String prepay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

}
