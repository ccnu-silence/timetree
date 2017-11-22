package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "GroupTwritte")
public class GroupTwritte extends EntityBase implements Serializable{
    @Column(column = "cid")
    private int cid;
    @Column(column = "twrid")
    private int twrid;
    @Column(column = "uid")
    private int uid;
    @Column(column = "content")
    private String content;
    @Column(column = "status")
    private int status; // 0 已发送 1 未发送成功 3 未发送
    @Column(column = "date")
    private String date;
    @Column(column = "realname")
    private String realname;
    @Column(column = "nickname")
    private String nickname;
    @Column(column = "imgs")
    private String imgs;
    @Column(column = "avatar")
    private String avatar;
    @Column(column = "type")
    private String type;
    @Column(column = "zan")
    private String zan;
    @Column(column = "ftype")
    private int ftype;//
    @Column(column = "albumid")
    private String albumid;
    private comments[] comments;
    @Table(name="comments")
    public static class comments extends EntityBase implements Serializable{
        @Column(column = "twrid")
        private int twrid;
        @Column(column="cmtid")
        private int cmtid;
        @Column(column = "cmterid")
        private int cmterid;
        @Column(column = "uid")
        private int uid;
        @Column(column = "touid")
        private int touid;
        @Column(column = "nickname")
        private String nickname;
        @Column(column = "tonickname")
        private String tonickname;
        @Column(column = "realname")
        private String realname;
        @Column(column = "torealname")
        private String torealname;
        @Column(column = "content")
        private String content;
        @Column(column = "date")
        private String date;

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

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public int getTouid() {
            return touid;
        }

        public void setTouid(int touid) {
            this.touid = touid;
        }

        public String getTorealname() {
            return torealname;
        }

        public void setTorealname(String torealname) {
            this.torealname = torealname;
        }
    }
    private likers[] likers;
    @Table(name = "likers")
    public static class likers extends EntityBase implements Serializable{
        @Column(column = "uid")
        private int uid;
        @Column(column = "realname")
        private String realname;
        @Column(column = "twrid")
        private int twrid;
        @Column(column = "likeid")
        private int likeid;
        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public int getTwrid() {
            return twrid;
        }

        public void setTwrid(int twrid) {
            this.twrid = twrid;
        }

        public int getLikeid() {
            return likeid;
        }

        public void setLikeid(int likeid) {
            this.likeid = likeid;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public comments[] getComments() {
        return comments;
    }

    public void setComments(comments[] comments) {
        this.comments = comments;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getZan() {
        return zan;
    }

    public void setZan(String zan) {
        this.zan = zan;
    }

    public likers[] getLikers() {
        return likers;
    }

    public void setLikers(likers[] likers) {
        this.likers = likers;
    }

    public int getFtype() {
        return ftype;
    }

    public void setFtype(int ftype) {
        this.ftype = ftype;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }
}
