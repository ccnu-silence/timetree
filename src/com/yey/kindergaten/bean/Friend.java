/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * Friend.java
 *
 * 2014年6月27日-下午4:57:23
 *  2014中幼信息科技公司-版权所有
 *
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 好友
 * Friend
 * chaowen
 * 511644784@qq.com
 * 2014年6月27日 下午4:57:23
 * @version 1.0.0
 *
 */
@Table(name="Friend")
public class Friend extends EntityBase implements Serializable{
	@Column(column="uid")
	private int uid;
	@Column(column="nickname")
	private String nickname;
	@Column(column="avatar")
	private String avatar;
	@Column(column="role")
	private String role;
	@Column(column="headversion")
	private String headversion;
	@Column(column="account")
	private String account;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getHeadversion() {
		return headversion;
	}
	public void setHeadversion(String headversion) {
		this.headversion = headversion;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}
