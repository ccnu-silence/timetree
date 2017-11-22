/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * Services.java
 * 
 * 2014年6月25日-下午2:52:16
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 服务类
 * Services
 * chaowen
 * 511644784@qq.com
 * 2014年6月25日 下午2:52:16
 * @version 1.0.0
 * 
 */
@Table(name="services")
public class Services extends EntityBase implements Serializable{
	@Column(column="uid")
	private int uid;
	@Column(column="type")
	private int type;
	@Column(column="name")
	private String name;
	@Column(column="agroup")
	private int group;
	@Column(column="url")
	private String url;
	@Column(column="orderno")
	private int orderno;
    @Column(column="tip")
    private int tip;
    @Column(column="isfirstlook")//1表示已经打开过，0表示暂未打开
    private int isfirstlook;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getOrderno() {
		return orderno;
	}
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

    public int getIsfirstlook() {
        return isfirstlook;
    }

    public void setIsfirstlook(int isfirstlook) {
        this.isfirstlook = isfirstlook;
    }
}
