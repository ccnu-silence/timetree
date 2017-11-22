package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;


@Table(name="TwitterComments")
public class TwitterComments extends EntityBase{
	@Column(column="commentid")
	private String commentid;
	@Column(column="twrid")
	private String twrid;
	@Column(column="userid")
	private int userid;
	@Column(column="content")
	private String content;
	@Column(column="date")
	private String date;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCommentid() {
		return commentid;
	}
	public void setCommentid(String commentid) {
		this.commentid = commentid;
	}
	public String getTwrid() {
		return twrid;
	}
	public void setTwrid(String twrid) {
		this.twrid = twrid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}	
