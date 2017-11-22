package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
@Table(name="ScedulesNameBean")
public class ScedulesNameBean extends EntityBase{
	@Column(column="name")
	private String name;
    private int sheid;
	public int getSheid() {
		return sheid;
	}

	public void setSheid(int sheid) {
		this.sheid = sheid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ScedulesNameBean() {
		
	} 
	public   ScedulesNameBean(String name,int sheid){
		this.name=name;
		this.sheid=sheid;
	}
}
