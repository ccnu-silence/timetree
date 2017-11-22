/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * MessageRecent.java
 * 
 * 2014年7月2日-下午5:04:29
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 系统消息
 * MessageSystem
 * chaowen
 * 511644784@qq.com
 * 2014年7月2日 下午5:04:29
 * @version 1.0.0
 * 
 */
@Table(name="messageRecent")
public class MessageSystem extends EntityBase{
	@Column(column="pmid")
	private int pmid ;
	@Column(column="name")
    private String name; //消息来自
	@Column(column="date")
    private String date; //时间
	@Column(column="toid")
	private int toid;//接收人ID
	@Column(column="title")
    private String title;
	@Column(column="content")
    private String content;
	@Column(column="newcount")
	private int newcount;
	@Column(column="action")
	private int action;
	@Column(column="avatar")
	private String avatar;

	public MessageSystem() {
		super();

	}

    public MessageSystem(int pmid, String name, String date, int toid, String title, String content, int newcount, int action, String avatar) {
        this.pmid = pmid;
        this.name = name;
        this.date = date;
        this.toid = toid;
        this.title = title;
        this.content = content;
        this.newcount = newcount;
        this.action = action;
        this.avatar = avatar;
    }

    public int getPmid() {
        return pmid;
    }

    public void setPmid(int pmid) {
        this.pmid = pmid;
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

    public int getToid() {
        return toid;
    }

    public void setToid(int toid) {
        this.toid = toid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
