/**
 * 系统项目名称
 *
 * com.yey.kindergaten.bean
 * MessagePublicAccount.java
 * 2014年7月3日-下午6:14:38
 * 2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 公众号消息
 *
 * MessagePublicAccount
 * chaowen
 * 511644784@qq.com
 * 2014年7月3日 下午6:14:38
 * @version 1.0.0
 * 
 */
@Table(name="messagePublicAccount")
public class MessagePublicAccount extends EntityBase {
    @Column(column="pmid")
    private int pmid ;           // 公众号消息id
    @Column(column="title")
    private String title;        // 标题，例如: 【2015-08-20 晨检报告】
    @Column(column="action")
    private int action;          // 0:聊天消息; 1:公众号消息; 大于50的是用于打开ur(除玩转时光树的特殊消息)
    @Column(column="date")
    private String date;         // 日期
    @Column(column="toid")
    private int toId;            // 一般为用户的uid
    @Column(column="publicid")
    private int publicid;        // 公众号id - 时光树: (园长:16; 老师:17; 家长:18;)
    @Column(column="contenturl")
    private String contenturl;   // 网址 用这个
    @Column(column="url")
    private String url;          // 跳转的WEB地址",--原文地址 用上面那个
    @Column(column="file_url")
    private String fileurl;      // 文件URL
    @Column(column="filedesc")
    private String filedesc;     // 摘要
    @Column(column="contenttype")
    private int contenttype;     // 图文、、等等类型 : TYPE_TEXT = 0; TYPE_IMAGE = 1; TYPE_AUDIO = 2; TYPE_VIDEO = 3; TYPE_IMAGE_TEXT = 4; TYPE_NO_IMAGE_TEXT = 5;
    @Column(column="shareable")
    private int shareable;       // 是否可分享 默认为-1
    @Column(column="name")
    private String name;         // 名字，例如: 健康与安全
    @Column(column="avatar")
    private String avatar;       // 头像
    @Column(column="typeid")
    private int typeid;          // 默认-1，表示无分类
    @Column(column = "optag")
    private int optag;           // 0:删除; 1:更新
    public MessagePublicAccount() {
        super();
    }

    public MessagePublicAccount(int pmid, String title, int action,
            String date, int toId, int publicid, String contenturl, String url,
            String fileurl, String filedesc, int contenttype, int shareable,
            String name, String avatar, int typeid, int optag) {
        super();
        this.pmid = pmid;
        this.title = title;
        this.action = action;
        this.date = date;
        this.toId = toId;
        this.publicid = publicid;
        this.contenturl = contenturl;
        this.url = url;
        this.fileurl = fileurl;
        this.filedesc = filedesc;
        this.contenttype = contenttype;
        this.shareable = shareable;
        this.name = name;
        this.avatar = avatar;
        this.typeid = typeid;
        this.optag = optag;
    }

    public int getPmid() {
        return pmid;
    }
    public void setPmid(int pmid) {
        this.pmid = pmid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getAction() {
        return action;
    }
    public void setAction(int action) {
        this.action = action;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getToId() {
        return toId;
    }
    public void setToId(int toId) {
        this.toId = toId;
    }
    public int getPublicid() {
        return publicid;
    }
    public void setPublicid(int publicid) {
        this.publicid = publicid;
    }
    public String getContenturl() {
        return contenturl;
    }
    public void setContenturl(String contenturl) {
        this.contenturl = contenturl;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getFileurl() {
        return fileurl;
    }
    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }
    public String getFiledesc() {
        return filedesc;
    }
    public void setFiledesc(String filedesc) {
        this.filedesc = filedesc;
    }
    public int getContenttype() {
        return contenttype;
    }
    public void setContenttype(int contenttype) {
        this.contenttype = contenttype;
    }
    public int getShareable() {
        return shareable;
    }
    public void setShareable(int shareable) {
        this.shareable = shareable;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public int getTypeid() {
        return typeid;
    }
    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }
    public int getOptag() {
        return optag;
    }
    public void setOptag(int optag) {
        this.optag = optag;
    }

}
