package com.yey.kindergaten.bean;

import java.util.ArrayList;
import java.util.List;

public class AlbumBean {

	public int photoCount;
	public String AlbumPath;
	public String AlbumName;
    public String albumid;
	public ArrayList<String> datalist;
	public int getPhotoCount() {
		return photoCount;
	}
	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}
	public String getAlbumPath() {
		return AlbumPath;
	}
	public void setAlbumPath(String albumPath) {
		AlbumPath = albumPath;
	}
	public String getAlbumName() {
		return AlbumName;
	}
	public void setAlbumName(String albumName) {
		AlbumName = albumName;
	}
	public List<String> getDatalist() {
		return datalist;
	}
	public void setDatalist(ArrayList<String> datalist) {
		this.datalist = datalist;
	}
    public String getAlbumid() {
        return albumid;
    }
    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }
}
