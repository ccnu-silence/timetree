/**
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @author Administrator
 *
 */
@Table(name="chat")
public class Chat extends EntityBase{
	@Column(column="pmid")
	private int pmid;
	@Column(column="content")
    private String content;
	@Column(column="contenttype")
    private int contenttype;
	@Column(column="uid")
    private int uid;
	@Column(column="toid")
    private int toid;
	@Column(column="date")
    private String date;
	@Column(column="status")
	private int status;
	@Column(column="action")
	private int action;
	@Column(column="avatar")
	private String avatar;
	
	
	public Chat() {
		super();
	}

	public Chat(int pmid, String content, int contenttype, int uid, int toid,
			String date, int status, int action, String avatar) {
		super();
		this.pmid = pmid;
		this.content = content;
		this.contenttype = contenttype;
		this.uid = uid;
		this.toid = toid;
		this.date = date;
		this.status = status;
		this.action = action;
		this.avatar = avatar;
	}





	public int getAction1() {
		return action;
	}

	public void setAction1(int action1) {
		this.action = action1;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getPmid() {
		return pmid;
	}
	public void setPmid(int pmid) {
		this.pmid = pmid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getContenttype() {
		return contenttype;
	}
	public void setContenttype(int contenttype) {
		this.contenttype = contenttype;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getToid() {
		return toid;
	}
	public void setToid(int toid) {
		this.toid = toid;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
