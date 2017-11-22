package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

@Table(name="Term")
public class Term extends EntityBase implements Serializable{
    
	@Column(column="cid")
	private int cid;
	@Column(column="cname")
	private String cname;
	@Column(column="term")
	private String term;
	@Column(column="hbid")	
	private int hbid;
	private List<LifePhoto> photo;

	public Term() { }

//	Term
	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getHbid() {
		return hbid;
	}
	
	public void setHbid(int hbid) {
		this.hbid = hbid;
	}

	public List<LifePhoto> getPhoto() {
		return photo;
	}

	public void setPhoto(List<LifePhoto> photo) {
		this.photo = photo;
	}	

}
