package com.yey.kindergaten.bean;

import java.io.Serializable;

public class WLImage implements Serializable{
  
    private int photoid;
    private String m_path;
    private String photo_desc;
    private String editection; // 0表示不显示，1表示显示但是为选中，2表示显示选中

    public WLImage(int photoid, String m_path, String photo_desc, String edition) {
        this.photoid = photoid;
        this.photo_desc = photo_desc;
        this.m_path = m_path;
    }
	
    public WLImage(){ }
	  
    public int getPhotoid() {
		return photoid;
    }

    public void setPhotoid(int photoid) {
		this.photoid = photoid;
    }

    public String getM_path() {
		return m_path;
    }

    public void setM_path(String m_path) {
		this.m_path = m_path;
    }

    public String getPhoto_desc() {
		return photo_desc;
    }

    public void setPhoto_desc(String photo_desc) {
		this.photo_desc = photo_desc;
    }

    public String getEditection() {
		return editection;
    }
	 
    public void setEditection(String editection) {
		this.editection = editection;
    }

}
