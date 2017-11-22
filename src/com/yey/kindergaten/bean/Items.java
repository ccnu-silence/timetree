package com.yey.kindergaten.bean;

public class Items {
	private String nickname;
	private String avatar;
	private int id;
	private String type;
	private Boolean lines;
	public  int sctype;
	public int role;
	private String birthday;
    private String job;
    private int birthdaystatus;
    private int cid;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getBirthdaystatus() {
        return birthdaystatus;
    }

    public void setBirthdaystatus(int birthdaystatus) {
        this.birthdaystatus = birthdaystatus;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public  int viewtype;

	public int getViewtype() {
		return viewtype;
	}
	public void setViewtype(int viewtype) {
		this.viewtype = viewtype;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getSectionPosition() {
		return sectionPosition;
	}
	public void setSectionPosition(int sectionPosition) {
		this.sectionPosition = sectionPosition;
	}
	public int getListPosition() {
		return listPosition;
	}
	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}
	public  String text;

	public int sectionPosition;
	public int listPosition;
	
	
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
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
	
}
