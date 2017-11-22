package com.yey.kindergaten.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
/**
 *日程实体映射类
 *@author zy
 */

@Table(name="SchedulesBean")
public class SchedulesBean implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id(column="localsheid")
	private int localsheid ;
	@Column(column="sheid")
	private int sheid;
	@Column(column="day")
	private String day;
	@Column(column="time")
	private String time;
	@Column(column="reminds")
	private String reminds;
	@Column(column="theme")
	private String theme;
	@Column(column="note")
	private String note;
	@Column(column="people")
	private String  people;
	@Column(column="name")
	private String name;	
	@Column(column="flag")
	private String flag;//上传成功标志位，有没有上传成功0表示成功，-1表示失败
	@Column(column="remind")
	private int remind;//提醒标志位0-5依次对应的提醒的各种方式。
	@Column(column="deleteflag")
	private String delete;//显示标志位，本地删除后，服务器删除失败。0表示显示，-1表示不显示	                       
	@Column(column="uid")
	private int uid;
	@Column(column="realnames")
	private String realnames;
	@Column(column="uids")
	private String uids;
	/**
     * 
     * @param day 日期
     * @param time 日程时间（时分）
     * @param theme 日程主题
     * @param note  备忘内容
     * @param uid   参与者id
     * @param name  参与者姓名
     * @param type  日程提醒方式
     */
    public  SchedulesBean(int sheid,String day,String time,String theme,String note,String type,String
       uid, String name,String flag,int remind,int uids){
    	this.flag=flag;
    	this.sheid=sheid;
    	this.day=day;
    	this.time=time;
    	this.theme=theme;
    	this.note=note;
    	this.reminds=type;
    	this.people=uid;
    	this.name=name;
    	this.uid=uids;
//    	this.remind=remind;  
    }
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getDelete() {
		return delete;
	}
	public void setDelete(String delete) {
		this.delete = delete;
	}
	public int getRemind() {
		return remind;
	}
	public void setRemind(int remind) {
		this.remind = remind;
	}

	public String getReminds() {
		return reminds;
	}

	public void setReminds(String remind) {
		this.reminds = remind;
	}

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public int getSheid() {
		return sheid;
	}

	public void setSheid(int sheid) {
		this.sheid = sheid;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public 	SchedulesBean(){
	    	super();
   }
	public int getLocalsheid() {
		return localsheid;
	}

	public void setLocalsheid(int localsheid) {
		this.localsheid = localsheid;
	}
	
	public String getRealnames() {
		return realnames;
	}
	public void setRealnames(String realnames) {
		this.realnames = realnames;
	}
	public String getUids() {
		return uids;
	}
	public void setUids(String uids) {
		this.uids = uids;
	}
		
}
