/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * Teacher.java
 * 
 * 2014年6月26日-下午7:20:46
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 老师个体
 * Teacher
 * chaowen
 * 511644784@qq.com
 * 2014年6月26日 下午7:20:46
 * @version 1.0.0
 * 
 */
@Table(name="Teacher")
public class Teacher extends EntityBase{
	@Column(column="uid")
    private int uid;
	@Column(column="nickname")
    private String nickname;
	@Column(column="phone")
    private String phone;
	@Column(column="avatar")
    private String avatar;
	@Column(column="headversion")
    private String headversion;
	@Column(column="department")
    private String department;
	@Column(column="realname")
	private String realname; //真实姓名
	@Column(column="role")
	private int role;
    @Column(column = "job")
    private String job;

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
    
    
}
