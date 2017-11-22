package com.yey.kindergaten.bean;

import java.io.Serializable;

/**
 * 微信预支付返回参数，
 * @author zy
 *
 */
public class WxEntity implements Serializable {

    private String appid; // 应用ID
    private String crttime; // 时间戳
    private String fee_des; // 描述
    private String fee_money; // 套餐价格
    private String key; // appkey;
    private String mch_id; // 商户号id;
    private String nonce_str; // 随机数
    private String out_trade_no; // 订单号
    private int paytype; // 支付方式；
    private String prepay_id; // 预支付Id;
    private String sign; // 签名结果 注：不能拿这个签名结果去支付，需要客户端自己生成签名。
    private String fee_name; // 套餐名称

    public String getFee_name() {
        return fee_name;
    }
    public void setFee_name(String fee_name) {
        this.fee_name = fee_name;
    }
    public String getAppid() {
        return appid;
    }
    public void setAppid(String appid) {
        this.appid = appid;
    }
    public String getCrttime() {
        return crttime;
    }
    public void setCrttime(String crttime) {
        this.crttime = crttime;
    }
    public String getFee_des() {
        return fee_des;
    }
    public void setFee_des(String fee_des) {
        this.fee_des = fee_des;
    }
    public String getFee_money() {
        return fee_money;
    }
    public void setFee_money(String fee_money) {
        this.fee_money = fee_money;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getMch_id() {
        return mch_id;
    }
    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }
    public String getNonce_str() {
        return nonce_str;
    }
    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }
    public String getOut_trade_no() {
        return out_trade_no;
    }
    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
    public int getPaytype() {
        return paytype;
    }
    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }
    public String getPrepay_id() {
        return prepay_id;
    }
    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }
    public String getSign() {
        return sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }

}
