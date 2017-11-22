package com.yey.kindergaten.bean;

/**
 * Created by zy on 2015/3/24.
 */
public class MessageSystems {

    private String pmid;
    private int contenttype;
    private int action;
    private String date;
    private String content;
    private String contenturl;
    private String avatar;
    private String title;

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getContenttype() {
        return contenttype;
    }

    public void setContenttype(int contenttype) {
        this.contenttype = contenttype;
    }

    public String getContenturl() {
        return contenturl;
    }

    public void setContenturl(String contenturl) {
        this.contenturl = contenturl;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
