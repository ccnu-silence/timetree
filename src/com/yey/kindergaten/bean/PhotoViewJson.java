/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * Contacts.java
 * 
 * 2014年6月26日-下午7:19:41
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import java.util.ArrayList;

/**
 * 图片预览的JSON解析类
 *
 * 2015年9月23日
 * 
 */
public class PhotoViewJson {

    String title;
    int nextid;
    ArrayList<PhotoShow> photos;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNextid() {
        return nextid;
    }

    public void setNextid(int nextid) {
        this.nextid = nextid;
    }

	public ArrayList<PhotoShow> getPhotoShow() {
  		return photos;
  	}

  	public void setPhotoShow(ArrayList<PhotoShow> photos) {
  		this.photos = photos;
  	}


}
