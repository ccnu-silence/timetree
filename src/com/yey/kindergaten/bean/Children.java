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
@Table(name="Children")
public class Children extends EntityBase{
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
	@Column(column="role")
	private int role;
    @Column(column="birthday")
    private String birthday;
    @Column(column="birthdaystatus")
    private int birthdaystatus;

    public Children() {
    }

    public Children(Parent parent) {
        this.uid = parent.getUid();
        this.nickname = parent.getNickname();
        this.cname = parent.getCname();
        this.gname = parent.getGname();
        this.phone = parent.getPhone();
        this.avatar = parent.getAvatar();
        this.app_headpic = parent.getApp_headpic();
        this.headversion = parent.getHeadversion();
        this.realname = parent.getRealname();
        this.app_headversion = parent.getApp_headversion();
        this.cid = parent.getCid();
        this.role = 2;
        this.birthday = parent.getBirthday();
        this.birthdaystatus = parent.getBirthdaystatus();
    }

    public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
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
