package com.yey.kindergaten.widget.popubmenu;

public class PopupMenuItem
{
	public int icon;
	public int tag;
	public String title;
	public String action;
	public String url;

	

	public PopupMenuItem(int icon, int tag, String title, String action,
			String url) {
		super();
		this.icon = icon;
		this.tag = tag;
		this.title = title;
		this.action = action;
		this.url = url;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
