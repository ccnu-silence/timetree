package com.yey.kindergaten.bean;

public class FolderItem {
	private String folderName;
	private String folderIconUrl;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getFolderIconUrl() {
		return folderIconUrl;
	}
	public void setFolderIconUrl(String folderIconUrl) {
		this.folderIconUrl = folderIconUrl;
	}
}
