package com.yey.kindergaten.bean;
import java.util.List;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created with IntelliJ IDEA.
 * User: feezoner
 * Date: 13-12-11
 * Time: 下午8:55
 * To change this template use File | Settings | File Templates.
 */
@Table(name="DiaryHomeInfo")
public class DiaryHomeInfo extends EntityBase{
	@Column(column="date")  
    private String date;
	@Column(column="diaryid") 
    private String diaryid;
	@Column(column="img")
    private String img;
	@Column(column="con")
    private String con;
	@Column(column="snd")
    private String snd;
	@Column(column="bg")
    private int bg;
	@Column(column="zanlist")
    private String zanlist;
	@Column(column="review")
    private List<DiaryHomeReview> review;
	@Column(column="zancnt")
    private int zancnt;
	@Column(column="reviewcnt")
    private int reviewcnt;
	@Column(column="status")
	private int status;
    public int getZancnt() {
        return zancnt;
    }

    public void setZancnt(int zancnt) {
        this.zancnt = zancnt;
    }

    public int getReviewcnt() {
        return reviewcnt;
    }

    public void setReviewcnt(int reviewcnt) {
        this.reviewcnt = reviewcnt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiaryid() {
        return diaryid;
    }

    public void setDiaryid(String diaryid) {
        this.diaryid = diaryid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getSnd() {
        return snd;
    }

    public void setSnd(String snd) {
        this.snd = snd;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public String getZanlist() {
        return zanlist;
    }

    public void setZanlist(String zanlist) {
        this.zanlist = zanlist;
    }

    public List<DiaryHomeReview> getReview() {
        return review;
    }

    public void setReview(List<DiaryHomeReview> review) {
        this.review = review;
    }
    

    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public static class DiaryHomeReview {
        private String con;
        private String author;

        public String getCon() {
            return con;
        }

        public void setCon(String con) {
            this.con = con;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}

