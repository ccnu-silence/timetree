package com.yey.kindergaten.bean;

import java.io.Serializable;

/**
 * 订单信息表
 * @author zy
 *
 */
public class OrderInfo implements Serializable {

    private String privateKey; // 商户私钥，pkcs8格式
    private String partner; // 商户PID,合作者身份id
    private String seller; // 商户收款账号
    private int feeid; // 套餐id
    private String orderNo; // 订单号
    private String feeName; // 套餐名称
    private String fee_des; // 套餐描述
    private String amount; // 金额
    private String notifyUrl; // 通知url
    private String payType; // 支付方式 1,支付宝2，微信
    private String crttime; // 当前时间：yyyy-MM-dd HH:mm:ss

    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    public String getPartner() {
        return partner;
    }
    public void setPartner(String partner) {
        this.partner = partner;
    }
    public String getSeller() {
        return seller;
    }
    public void setSeller(String seller) {
        this.seller = seller;
    }
    public int getFeeid() {
        return feeid;
    }
    public void setFeeid(int feeid) {
        this.feeid = feeid;
    }
    public String getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    public String getFeeName() {
        return feeName;
    }
    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }
    public String getFee_des() {
        return fee_des;
    }
    public void setFee_des(String fee_des) {
        this.fee_des = fee_des;
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getNotifyUrl() {
        return notifyUrl;
    }
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
    public String getPayType() {
        return payType;
    }
    public void setPayType(String payType) {
        this.payType = payType;
    }
    public String getCrttime() {
        return crttime;
    }
    public void setCrttime(String crttime) {
        this.crttime = crttime;
    }

}