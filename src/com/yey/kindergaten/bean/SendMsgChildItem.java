package com.yey.kindergaten.bean;

public class SendMsgChildItem {
	String textName;
	String textPhone;
	String id;
	String img;
    String birthday;
    int birthdaystatus;
	
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public SendMsgChildItem(String id,String textPhone,String textName){
		this.id = id;
		this.textPhone = textPhone;
		this.textName = textName;
	}
    public SendMsgChildItem(String id,String textPhone,String textName,String headiv){
        this.id=id;
        this.textPhone = textPhone;
        this.textName = textName;
        this.img = headiv;
    }
	public SendMsgChildItem(String id,String textPhone,String textName,String headiv,String birthday,int birthdaystatus){
		this.id=id;
		this.textPhone = textPhone;
		this.textName = textName;
		this.img = headiv;
        this.birthday = birthday;
        this.birthdaystatus = birthdaystatus;
	}
	public String getTextName() {
		return textName;
	}
	public void setTextName(String textName) {
		this.textName = textName;
	}
	public String getTextPhone() {
		return textPhone;
	}
	public void setTextPhone(String textPhone) {
		this.textPhone = textPhone;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
