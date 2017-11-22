package com.yey.kindergaten.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;


@Table(name="Term")
public class LifePhoto implements Serializable{
	 @Column(column="gbid")
	 private int gbid;
	 @Column(column="userid")
	 private int userid;
	 @Column(column="name")
	 private String name;
	 @Column(column="photocount")
	 private int photocount;
     @Column(column="headpic")
	 private String headpic;
	 public String getHeadpic() {
		return headpic;
	}
	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}
	public int getGbid() {
		return gbid;
	}
	public void setGbid(int gbid) {
		this.gbid = gbid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPhotocount() {
		return photocount;
	}
	public void setPhotocount(int photocount) {
		this.photocount = photocount;
	}
	 
}
