package com.yey.kindergaten.bean;

import java.util.List;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "Twitter")
public class Twitter extends EntityBase {
	private int upload;
	@Column(column = "twrid")
	private int twrid;
	@Column(column = "posterid")
	private int posterid;
	@Column(column = "private")
	private int privat;
	@Column(column = "content")
	private String content;
	@Column(column = "status")
	private int status;
	@Column(column = "date")
	private String date;
	@Column(column = "postername")
	private String postername;
	@Column(column = "imgs")
	private String imgs;
	@Column(column = "posteravatar")
	private String posteravatar;
	private comments[] comments;
	@Table(name="comments")
	public static class comments extends EntityBase{
		@Column(column = "twrid")
		private int twrid;
		@Column(column="cmtid")
		private int cmtid;
		@Column(column = "cmterid")
		private int cmterid;
		@Column(column = "cmtername")
		private String cmtername;
		@Column(column = "content")
		private String content;
		@Column(column = "date")
		private String date;
		@Column(column="cmteravatar")
		private String cmteravatar;
				
		public String getCmteravatar() {
			return cmteravatar;
		}

		public void setCmteravatar(String cmteravatar) {
			this.cmteravatar = cmteravatar;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
		public int getCmtid() {
			return cmtid;
		}

		public void setCmtid(int cmtid) {
			this.cmtid = cmtid;
		}

		public int getCmterid() {
			return cmterid;
		}

		public void setCmterid(int cmterid) {
			this.cmterid = cmterid;
		}

		public String getCmtername() {
			return cmtername;
		}

		public void setCmtername(String cmtername) {
			this.cmtername = cmtername;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public int getTwrid() {
			return twrid;
		}

		public void setTwrid(int twrid) {
			this.twrid = twrid;
		}
		
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getTwrid() {
		return twrid;
	}

	public void setTwrid(int twrid) {
		this.twrid = twrid;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}


	public int getPosterid() {
		return posterid;
	}

	public void setPosterid(int posterid) {
		this.posterid = posterid;
	}

	public String getPostername() {
		return postername;
	}

	public void setPostername(String postername) {
		this.postername = postername;
	}

	public int getPrivat() {
		return privat;
	}

	public void setPrivat(int privat) {
		this.privat = privat;
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

	public comments[] getComment() {
		return comments;
	}

	public void setComment(comments[] comments) {
		this.comments = comments;
	}

	public int getUpload() {
		return upload;
	}

	public void setUpload(int upload) {
		this.upload = upload;
	}

	public String getPosteravatar() {
		return posteravatar;
	}

	public void setPosteravatar(String posteravatar) {
		this.posteravatar = posteravatar;
	}

	


}
