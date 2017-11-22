package com.yey.kindergaten.bean;

import java.util.List;


public class SendMsgGroupItem {
	String Text;
	String id;
	List<SendMsgChildItem>childitem;
	
	public SendMsgGroupItem(String id,String Text,List<SendMsgChildItem>childItems){
		   this.id=id;
		   this.Text=Text;
		   this.childitem=childItems;
	}
	public String getText() {
		return Text;
	}
	public void setText(String text) {
		Text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<SendMsgChildItem> getChilditem() {
		return childitem;
	}
	public void setChilditem(List<SendMsgChildItem> childitem) {
		this.childitem = childitem;
	}
}
