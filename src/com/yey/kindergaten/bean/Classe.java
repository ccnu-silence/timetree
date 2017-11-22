package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="Classe")
public class Classe extends EntityBase{
	@Column(column="cid")
	int cid;
	@Column(column="cname")
	String cname;
	@Column(column="OrderNo")
	int OrderNo;
    @Column(column = "childrencount")
    int childrencount;
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
	public int getOrderNo() {
		return OrderNo;
	}
	public void setOrderNo(int orderNo) {
		OrderNo = orderNo;
	}

    public int getChildrencount() {
        return childrencount;
    }

    public void setChildrencount(int childrencount) {
        this.childrencount = childrencount;
    }
}
