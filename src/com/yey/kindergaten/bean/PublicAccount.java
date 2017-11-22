/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * PublicAccount.java
 * 
 * 2014年7月3日-下午6:13:07
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 公众号
 *
 * PublicAccount
 * chaowen
 * 511644784@qq.com
 * 2014年7月3日 下午6:13:07
 * @version 1.0.0
 * 
 */
@Table(name="PublicAccount")
public class PublicAccount extends EntityBase implements Serializable{
    @Column(column="publicid")
    private int publicid;       // 公众号id
    @Column(column="loadhistory")
    private int loadhistory;    // 用于加载历史消息的判断，0是默认值
    @Column(column="pmtype")
    private int pmtype;         // 二级分类
    @Column(column="desc")
    private String desc;        // 描述
    @Column(column="subscription")
    private int subscription;   // 1:已订阅; -1:订阅失败; 0:未订阅;
    @Column(column="nickname")
    private String nickname;
    @Column(column="account1")
    private String account1;
    @Column(column="domain")
    private String domain;      // 头像版本号
    @Column(column="headversion")
    private int headversion;    // 头像版本号
    @Column(column="account")
    private String account;     // 头像版本号
    @Column(column="avatar")
    private String avatar;      // 头像
    @Column(column="fixed")
    private int fixed;          // 0不固定的。可取消订阅  1固定的。不可取消订阅
    @Column(column="isfirstlook")
    private int isfirstlook;    // 是否首次查看
    @Column(column="ismenu")
    private int ismenu;         // 是否有子菜单

    public PublicAccount() {}
    public PublicAccount(int publicid, int pmtype, String desc, String nickname, String avatar, int fixed) {
        this.publicid = publicid;
        this.pmtype = pmtype;
        this.desc = desc;
        this.nickname = nickname;
        this.avatar = avatar;
        this.fixed = fixed;
    }
    public int getIsmenu() {
        return ismenu;
    }

    public void setIsmenu(int ismenu) {
        this.ismenu = ismenu;
    }

    public int getIsfirstlook() {
        return isfirstlook;
    }

    public void setIsfirstlook(int isfirstlook) {
        this.isfirstlook = isfirstlook;
    }

    /*@Column(column="typeid")
        private int typeid; // 默认-1，表示无分类
    */
    public String getAccount1() {
        return account1;
    }
    public void setAccount1(String account1) {
        this.account1 = account1;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public int getPublicid() {
        return publicid;
    }
    public void setPublicid(int publicid) {
        this.publicid = publicid;
    }
    public int getPmtype() {
        return pmtype;
    }
    public void setPmtype(int pmtype) {
        this.pmtype = pmtype;
    }
    public int getFixed() {
        return fixed;
    }
    public void setFixed(int fixed) {
        this.fixed = fixed;
    }
    public int getHeadversion() {
        return headversion;
    }
    public void setHeadversion(int headversion) {
        this.headversion = headversion;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getSubscription() {
        return subscription;
    }
    public void setSubscription(int subscription) {
        this.subscription = subscription;
    }
    public int getLoadhistory() {
        return loadhistory;
    }
    public void setLoadhistory(int loadhistory) {
        this.loadhistory = loadhistory;
    }

}
