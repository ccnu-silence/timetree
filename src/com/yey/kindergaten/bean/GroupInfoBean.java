package com.yey.kindergaten.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 群
 * group
 * tyj
 * @version 1.0.0
 */
@Table(name="GroupInfoBean")
public class GroupInfoBean extends EntityBase implements Serializable{
	@Column(column="gid")      //年级的ID
	int gid;
	@Column(column="gtype")   
	int gtype;
	@Column(column="gnum")    
	int gnum;
	int creatoruid;
	String contact;
	String phone;
	String location;
	@Column(column="gname")  
	String gname;
	String creator;
	@Column(column="desc")
	String desc;
	@Column(column="joincode")
	String joincode;
	@Column(column="joinable")
	int joinable;
	String grade;
	int membercount;
	String garten;
	String teacher;
	public int getMembercount() {
		return membercount;
	}
	public void setMembercount(int membercount) {
		this.membercount = membercount;
	}
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
	public int getGtype() {
		return gtype;
	}
	public void setGtype(int gtype) {
		this.gtype = gtype;
	}
	public int getGnum() {
		return gnum;
	}
	public void setGnum(int gnum) {
		this.gnum = gnum;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getJoincode() {
		return joincode;
	}
	public void setJoincode(String joincode) {
		this.joincode = joincode;
	}
	public int getJoinable() {
		return joinable;
	}
	public void setJoinable(int joinable) {
		this.joinable = joinable;
	}	
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getGarten() {
		return garten;
	}
	public void setGarten(String garten) {
		this.garten = garten;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getCreatoruid() {
		return creatoruid;
	}
	public void setCreatoruid(int creatoruid) {
		this.creatoruid = creatoruid;
	}
	
}
