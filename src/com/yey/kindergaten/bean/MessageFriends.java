/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * MessageFriends.java
 * 
 * 2014年7月3日-下午6:14:01
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 存好友消息的bean
 * MessageFriends
 * chaowen
 * 511644784@qq.com
 * 2014年7月3日 下午6:14:01
 * @version 1.0.0
 * 
 */
@Table(name="messageFriends")
public class MessageFriends {
	@Id(column="pmid")
	private int pmid ;
	@Id(column="contenttype")
	private int contenttype;
	@Column(column="content")
	private String content;
	@Column(column="name")
	private String name;
	@Column(column="date")
	private String date;
	@Column(column="toid")
	private int toid;
	@Column(column="uid")
	private int uid;
	@Column(column="action")
	private int action;
	public MessageFriends() {
		super();
	}
	public MessageFriends(int pmid, int contenttype, String content,
			String name, String date, int toid, int uid, int action) {
		super();
		this.pmid = pmid;
		this.contenttype = contenttype;
		this.content = content;
		this.name = name;
		this.date = date;
		this.toid = toid;
		this.uid = uid;
		this.action = action;
	}
	public int getPmid() {
		return pmid;
	}
	public void setPmid(int pmid) {
		this.pmid = pmid;
	}
	public int getContenttype() {
		return contenttype;
	}
	public void setContenttype(int contenttype) {
		this.contenttype = contenttype;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}

	
}
