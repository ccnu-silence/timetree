package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by zy on 2015/7/20.
 */
 @Table(name = "leaveschool")
public class LeaveSchoolBean extends  EntityBase {

    @Column(column = "nick")
    private String nick;     // 昵称

    @Column(column = "avatar")
    private String avatar;   // 头像

    @Column(column = "date")
    private String date;     // 离园日期

    @Column(column = "uid")
    private int uid;         // 用户id

    @Column(column = "isLeave")
    private int isLeave;     // 是否离园 0表示未离园，1表示离园。

    @Column(column = "content")
    private String content;  // 备注

    @Column(column = "cid")
    private int cid;

    @Column(column = "cname")
    private String cname;

    public LeaveSchoolBean() { }

    public LeaveSchoolBean(String nick, String avatar, int uid, String date, int isLeave, String content,  String cname, int cid) {
        this.nick = nick;
        this.avatar = avatar;
        this.uid = uid;
        this.date = date;
        this.isLeave = isLeave;
        this.content = content;
        this.cname = cname;
        this.cid = cid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getIsLeave() {
        return isLeave;
    }

    public void setIsLeave(int isLeave) {
        this.isLeave = isLeave;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
