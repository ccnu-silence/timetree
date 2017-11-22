/**
 * 系统项目名称
 * com.yey.kindergaten.bean
 * Contacts.java
 * 
 * 2014年6月26日-下午7:19:41
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import java.util.List;

/**
 * 通讯录
 * Contacts
 * chaowen
 * 511644784@qq.com
 * 2014年6月26日 下午7:19:41
 * @version 1.0.0
 * 
 */
public class Contacts {
	List<Teacher> teachers;
    List<Classe> classes;
	List<PublicAccount> publics;
    List<Friend> friends;
    List<Children> parents;
    List<Msgtypes> msgtypes;
	public List<Classe> getClasses() {
  		return classes;
  	}

  	public void setClasses(List<Classe> classes) {
  		this.classes = classes;
  	}
	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}


	public List<Teacher> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<Teacher> teachers) {
		this.teachers = teachers;
	}

	
    public List<PublicAccount> getPublics() {
		return publics;
	}

	public void setPublics(List<PublicAccount> publics) {
		this.publics = publics;
	}

	public List<Children> getParents() {
		return parents;
	}

	public void setParents(List<Children> parents) {
		this.parents = parents;
	}

	public List<Msgtypes> getMsgtypes() {
		return msgtypes;
	}

	public void setMsgtypes(List<Msgtypes> msgtypes) {
		this.msgtypes = msgtypes;
	}

	
}
