package com.yey.kindergaten.bean;

import java.util.List;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;


@Table(name="ThreadsBean")
public class ThreadsBean extends EntityBase{
	
	  @Column(column="threadid")
	  private int threadid;
	  @Column(column="indexs")
	  private int indexs;//传到的第几张图片
	  @Column(column="url")
	  private String url;//保存的图片的路径
	  @Column(column="currentSize")
	  private int currentSize;//目前下载的图片的下载量
	  @Column(column="flag")
	  private String  flag;//下载状态  0表示在下载，1表示暂停，3还没开始下载	  
	  private int sumSize;
	  private List<String>urlList;//这是重新组装过的targetUrl	  
	  @Column(column="uids")
	  private String uids;
	  @Column(column="lifetype")
	  private String lifetype;
	  @Column(column="decs")
	  private String decs;
	  @Column(column="term")
	  private String term;
	/**
	   * @param treadid 对象id
	   * @param index   显示第几张图片
	   * @param url     目标地址
	   * @param currentSize  目前上传流量
	   * @param flag    上传状态 0表示在下载，1表示暂停，2表示停止
	   */
	  public ThreadsBean(int threadid,int index ,String url,int currentSize,String flag) {
		this.threadid=threadid;
		this.indexs=index;
		this.url=url;
		this.currentSize=currentSize;
		this.flag=flag;
	}
	  
	  public ThreadsBean() {
		
	}

	  
	public int getSumSize() {
		return sumSize;
	}

	public void setSumSize(int sumSize) {
		this.sumSize = sumSize;
	}

	public int getIndex() {
		return indexs;
	}

	public void setIndex(int index) {
		this.indexs = index;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}


	  public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public int getThreadid() {
		return threadid;
	}

	public void setThreadid(int threadid) {
		this.threadid = threadid;
	}
	  public int getIndexs() {
		return indexs;
	}

	public void setIndexs(int indexs) {
		this.indexs = indexs;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
	

	public String getUids() {
		return uids;
	}

	public void setUids(String uids) {
		this.uids = uids;
	}

	public String getLifetype() {
		return lifetype;
	}

	public void setLifetype(String lifetype) {
		this.lifetype = lifetype;
	}

	public String getDecs() {
		return decs;
	}

	public void setDecs(String decs) {
		this.decs = decs;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
