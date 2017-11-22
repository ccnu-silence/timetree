/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * Parent.java
 * 
 * 2014年6月27日-下午4:18:31
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 小孩
 * Parent
 * chaowen
 * 511644784@qq.com
 * 2014年6月27日 下午4:18:31
 * @version 1.0.0
 * 
 */
@Table(name="Parent")
public class Parent extends EntityBase{
	@Column(column="uid")
	private int uid;
	@Column(column="nickname")
	private String nickname;
	@Column(column="cname")
	private String cname;
	@Column(column="gname")
	private String gname;
	@Column(column="phone")
	private String phone;
	@Column(column="avatar")
	private String avatar;
	@Column(column="app_headpic")
	private String app_headpic;
	@Column(column="headversion")
	private String headversion;
	@Column(column="app_headversion")
	private String app_headversion;
	@Column(column="realname")
	private String realname;
	@Column(column="cid")
	private int cid;
    @Column(column="birthday")
    private String birthday;
    @Column(column="birthdaystatus")
    private int birthdaystatus;


    public Parent() {

    }
    public Parent(int uid, String avatar, String realname, int cid, String cname) {
        this.uid = uid;
        this.avatar = avatar;
        this.realname = realname;
        this.cid = cid;
        this.cname = cname;
    }
    public Parent(int uid, String avatar, String realname, int cid, String cname,String birthday, int birthdaystatus) {
        this.uid = uid;
        this.avatar = avatar;
        this.realname = realname;
        this.cid = cid;
        this.cname = cname;
        this.birthday = birthday;
        this.birthdaystatus = birthdaystatus;
    }

    public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public String getApp_headpic() {
		return app_headpic;
	}
	public void setApp_headpic(String app_headpic) {
		this.app_headpic = app_headpic;
	}
	public String getHeadversion() {
		return headversion;
	}
	public void setHeadversion(String headversion) {
		this.headversion = headversion;
	}
	public String getApp_headversion() {
		return app_headversion;
	}
	public void setApp_headversion(String app_headversion) {
		this.app_headversion = app_headversion;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public int getBirthdaystatus() {
        return birthdaystatus;
    }
    public void setBirthdaystatus(int birthdaystatus) {
        this.birthdaystatus = birthdaystatus;
    }

	
}
