package com.yey.kindergaten.bean;

public class Item {
	private String nickname;
	private String avatar;
	private int id;
	private String type;
	private Boolean lines;
	public  int sctype;
	public Boolean getLines() {
		return lines;
	}
	public void setLines(Boolean lines) {
		this.lines = lines;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSctype() {
		return sctype;
	}
	public void setSctype(int sctype) {
		this.sctype = sctype;
	}
}
