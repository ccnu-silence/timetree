package com.yey.kindergaten.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="AddressBookBean")
public class AddressBookBean extends EntityBase implements Serializable{
	@Column(column="adsid")
	int adsid;
	@Column(column="receiver")
	String receiver;
	@Column(column="address")
	String address;
	@Column(column="phone")
	String phone;
	@Column(column="code")
	String code;
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getAdsid() {
		return adsid;
	}
	public void setAdsid(int adsid) {
		this.adsid = adsid;
	}
}
