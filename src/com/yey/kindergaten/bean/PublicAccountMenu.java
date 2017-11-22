/**
 * 时光树
 * com.yey.kindergaten.bean
 * PublicAccountMenu.java
 * 
 * 2014年7月23日-下午6:37:47
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import java.util.List;

/**
 * 公众号自定义菜单
 * PublicAccountMenu
 * chaowen
 * 511644784@qq.com
 * 2014年7月23日 下午6:37:47
 * @version 1.0.0
 * 
 */
public class PublicAccountMenu {
	private String name;
	private int tag;
	private String action;
	private String url;
	private List<SubMenu> sub;



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getTag() {
		return tag;
	}



	public void setTag(int tag) {
		this.tag = tag;
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



	public List<SubMenu> getSub() {
		return sub;
	}



	public void setSub(List<SubMenu> sub) {
		this.sub = sub;
	}



	public static class SubMenu{
		private String name;
		private int tag;
		private String action;
		private String url;
        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getTag() {
			return tag;
		}
		public void setTag(int tag) {
			this.tag = tag;
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


	}

}
