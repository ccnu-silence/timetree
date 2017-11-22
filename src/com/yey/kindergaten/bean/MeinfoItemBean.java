package com.yey.kindergaten.bean;

public class MeinfoItemBean {
	
	String imageurl;
	String value;
	int type;
	String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	
	public MeinfoItemBean(String title ,String imageurl,String value,int type)
	{
		this.title=title;
		this.imageurl=imageurl;
		this.value=value;
		this.type=type;
	}
}
