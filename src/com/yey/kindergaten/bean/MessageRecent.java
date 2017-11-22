/**
 * 系统项目名称
 *
 * com.yey.kindergaten.bean
 * MessageRecent.java
 * 2014年7月2日-下午5:04:29
 * 2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 最近的消息
 *
 * MessageRecent
 * chaowen
 * 511644784@qq.com
 * 2014年7月2日 下午5:04:29
 * @version 1.0.0
 * 
 */
@Table(name="messageRecent")
public class MessageRecent extends EntityBase implements Serializable{
    @Column(column="msgid")
    private String msgid ;
    @Column(column="name")
    private String name;    // 消息来自
    @Column(column="date")
    private String date;    // 时间
    @Column(column="fromId")
    private String fromId;  // 对方id
    @Column(column="toId")
    private String toId;    // 接收人ID
    @Column(column="title")
    private String title;
    @Column(column="content")
    private String content;
    @Column(column="url")
    private String url;
    @Column(column="fileurl")
    private String fileurl;
    @Column(column="newcount")
    private int newcount;
    @Column(column="action")
    private int action;
    @Column(column="contenttype")
    private int contenttype;
    @Column(column="avatar")
    private String avatar;
    @Column(column="typeid")
    private int typeid;     // 默认-1，表示无分类
    @Column(column="hxfrom")
    private String hxfrom;  //
    @Column(column = "hxto")
    private String hxto;

    public MessageRecent() {
        super();
    }

    public MessageRecent(String msgid, String name, String date, String fromId,
            String toId, String title, String content, String url,
            String fileurl, int newcount, int action, int contenttype,
            String avatar, int typeid, String hxfrom, String hxto) {
        super();
        this.msgid = msgid;
        this.name = name;
        this.date = date;
        this.fromId = fromId;
        this.toId = toId;
        this.title = title;
        this.content = content;
        this.url = url;
        this.fileurl = fileurl;
        this.newcount = newcount;
        this.action = action;
        this.contenttype = contenttype;
        this.avatar = avatar;
        this.typeid = typeid;
        this.hxfrom = hxfrom;
        this.hxto = hxto;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNewcount() {
        return newcount;
    }

    public void setNewcount(int newcount) {
        this.newcount = newcount;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public int getContenttype() {
        return contenttype;
    }

    public void setContenttype(int contenttype) {
        this.contenttype = contenttype;
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

    public String getHxfrom() {
        return hxfrom;
    }

    public void setHxfrom(String hxfrom) {
        this.hxfrom = hxfrom;
    }

    public String getHxto() {
        return hxto;
    }

    public void setHxto(String hxto) {
        this.hxto = hxto;
    }

}
