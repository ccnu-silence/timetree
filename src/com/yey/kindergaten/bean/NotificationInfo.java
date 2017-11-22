package com.yey.kindergaten.bean;

import com.igexin.push.c.c.o;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
@Table(name="Notification")
public class NotificationInfo extends EntityBase{
   @Column(column="uid")
   private int uid;//用户uid
   @Column(column="whens")
   private String senddate;//发送时间
   @Column(column="content")
   private String content;//发送内容
   @Column(column="status")//是否发送成功,0表示还未到用户设定发送时间，1表示已发送，2表示发送失败
   private int status;
   @Column(column="noteid")
   private int noteid;//数据库中id
   @Column(column="count")
   private int count;//剩余短信数量，在open为未开通状态时，短信数量为0
   @Column(column="open")//是否开通短信服务
   private int open;    
   /**
    * @param uid      登录用户id
    * @param when     发送时间
    * @param content  发送内容
    * @param status   发送状态
    * @param notid    通知id
    * @param count    剩余短信数量
    * @param open     是否开通通知短信
    **/
   public NotificationInfo(int uid,String when,String content,int status,
		     int notid,int count,int open) {
		this.uid=uid;
		this.senddate=when;
		this.content=content;
		this.status=status;
		this.noteid=notid;
		this.count=count;
		this.open=open;				
   }
   
   public NotificationInfo() {
	
     }
   
   
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getNoteid() {
		return noteid;
	}

	public void setNoteid(int noteid) {
		this.noteid = noteid;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}
   
    public String getSenddate() {
			return senddate;
	}

	public void setSenddate(String senddate) {
			this.senddate = senddate;
	}
   
}
