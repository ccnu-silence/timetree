package com.yey.kindergaten.bean;

import java.io.Serializable;

public class AddressBean implements Serializable{
	private int  ID;
	private String locationid;
	private int level;
	private String location;
	private String province;
    private String Title;
	private String city;
	private int parentid; 
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getLocationid() {
		return locationid;
	}
	public void setLocationid(String locationid) {
		this.locationid = locationid;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getParentid() {
		return parentid;
	}
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}
    public void setTitle(String Title){
        this.Title = Title;
    }
    public String getTitle(){
        return Title;
    }


}