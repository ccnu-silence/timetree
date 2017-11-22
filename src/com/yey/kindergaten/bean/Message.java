/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * Message.java
 * 
 * 2014年7月2日-下午2:17:39
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 
 * Message
 * chaowen
 * 511644784@qq.com
 * 2014年7月2日 下午2:17:39
 * @version 1.0.0
 * 
 */

@Table(name="message")
public class Message extends EntityBase{
	@Column(column="pmid")
	private int pmid ;
	@Column(column="title")
	private String title;
	@Column(column="content_url")
	private String contenturl;
	@Column(column="date")
	private String date;
	@Column(column="publicid")
	private int publicid;  
	@Column(column="toid")
	private int toid;
	@Column(column="file_url")
	private String fileurl;
	@Column(column="contenttype")
	private int contenttype;
	@Column(column="action")
	private int action;
	@Column(column="uid")
	private int uid;
	@Column(column="content")
	private String content;
	@Column(column="url")
	private String url;
	@Column(column="shareable")
	private int shareable;
	@Column(column="file_desc")
	private String filedesc;
	@Column(column="name")
	private String name;
	@Column(column="avatar")
	private String avatar;
	@Column(column="data")
	private String data;
	@Column(column="typeid")
	private int typeid; // 默认-1，表示无分类
    private int optag; // 操作标志，0删除 1更新或新增
    private int voice; // voice=1 有声音，voice=0 无声音
	public Message() {
		super();
	}
	
	public Message(int pmid, String title, String contenturl, String date,
			int publicid, int toid, String fileurl, int contenttype,
			int action, int uid, String content, String url, int shareable,
			String filedesc, String name, String avatar, String data, int typeid) {
		super();
		this.pmid = pmid;
		this.title = title;
		this.contenturl = contenturl;
		this.date = date;
		this.publicid = publicid;
		this.toid = toid;
		this.fileurl = fileurl;
		this.contenttype = contenttype;
		this.action = action;
		this.uid = uid;
		this.content = content;
		this.url = url;
		this.shareable = shareable;
		this.filedesc = filedesc;
		this.name = name;
		this.avatar = avatar;
		this.data = data;
		this.typeid = typeid;
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
	public String getContenturl() {
		return contenturl;
	}
	public void setContenturl(String contenturl) {
		this.contenturl = contenturl;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getPublicid() {
		return publicid;
	}
	public void setPublicid(int publicid) {
		this.publicid = publicid;
	}
	public int getToid() {
		return toid;
	}
	public void setToid(int toid) {
		this.toid = toid;
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
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
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
	public int getShareable() {
		return shareable;
	}
	public void setShareable(int shareable) {
		this.shareable = shareable;
	}
	public String getFiledesc() {
		return filedesc;
	}
	public void setFiledesc(String filedesc) {
		this.filedesc = filedesc;
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
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
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

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }
}
