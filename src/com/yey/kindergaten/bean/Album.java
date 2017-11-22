package com.yey.kindergaten.bean;

import java.io.Serializable;

/**
 * 班级相片的类
 * @author Administrator
 *
 */
public class Album implements Serializable {

    public String albumid;      // 班级相册id
    public String albumName;    // 相册名称
    public String photoCount;   // 照片数量
    public String albumCover;   // 相册封面
    public int photoid;         // 照片id
    public String title;        // 标题
    public String filepath;     // 路径
    public int yp;              // 用来判断这张图片是不是使用又拍云上传


    public Album() { }

    public Album(String albumid, String albumName, String photoCount,
            String albumCover, int photoid, String title, String filepath) {
        super();
        this.albumid = albumid;
        this.albumName = albumName;
        this.photoCount = photoCount;
        this.albumCover = albumCover;
        this.photoid = photoid;
        this.title = title;
        this.filepath = filepath;
    }

    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public String getPhotoCount() {
        return photoCount;
    }
    public void setPhotoCount(String photoCount) {
        this.photoCount = photoCount;
    }
    public String getAlbumCover() {
        return albumCover;
    }
    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }
    public String getAlbumid() {
        return albumid;
    }
    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }
    public int getPhotoid() {
        return photoid;
    }
    public void setPhotoid(int photoid) {
        this.photoid = photoid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public int getYp() {
        return yp;
    }
    public void setYp(int yp) {
        this.yp = yp;
    }

}
