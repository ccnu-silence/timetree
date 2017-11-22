package com.yey.kindergaten.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 班级相册图片
 * @author chaowen
 * @date:2013-7-10 下午5:12:30
 */
public class ClassPhoto implements Serializable {

    public String cname;            // 班级名称
    public int cid;                 // 班级id
    public List<Album> albumlist;   // 相册列表 ( albumid cid albumName photoCount albumCover )

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public List<Album> getAlbumlist() {
        return albumlist;
    }

    public void setAlbumlist(List<Album> albumlist) {
        this.albumlist = albumlist;
    }

}
