package com.yey.kindergaten.bean;

/**
 * Created by zy on 2015/7/13.
 */
public class ClassVideo {

    private int id;//视频id

    private String date;//视频日期

    private String title;//标题

    private String content;//内容

    private long duration;//时常

    private String path;//播放路径

    private long size;//大小


   public  ClassVideo(){

    }

    public ClassVideo(int id, long size,String path, long duration, String content, String title, String date) {
        this.id = id;
        this.size = size;
        this.path = path;
        this.duration = duration;
        this.content = content;
        this.title = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
