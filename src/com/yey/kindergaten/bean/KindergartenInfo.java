package com.yey.kindergaten.bean;

import java.io.Serializable;

public class KindergartenInfo implements Serializable{
     private int uid;
     private int kid;
     private int number;
	 private String groupnum;
     private String name;
     private String address;
     private String phone;
     private String desc;
     private String kname;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getKid() {
		return kid;
	}
	public void setKid(int kid) {
		this.kid = kid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getGroupnum() {
		return groupnum;
	}
	public void setGroupnum(String groupnum) {
		this.groupnum = groupnum;
	}
    public void setNumber(int number){
        this.number = number;
    }

    public int getNumber(){
        return  number;
    }
    public void setKname(String kname){
        this.kname = kname;
    }
    public String getKname(){
        return  kname;
    }
}
