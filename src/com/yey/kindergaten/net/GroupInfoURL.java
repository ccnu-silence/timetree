package com.yey.kindergaten.net;

public class GroupInfoURL {

	
	// public static final String SERVER_URL ="http://test.mapp.zgyey.com/";
     //static final String GROUPINFOSERVER_URL ="http://192.168.0.203:555/";
     static final String GROUPINFOSERVER_URL =AppServer.getInstance().getAccountInfo().getGroupgw()+"/";
     // static final String GROUPINFOSERVER_URL =" http://kmapp.zgyey.com/";
	//static final String SERVER_URL ="http://192.168.0.160:555/";
	//static final String SERVER_URL ="http://192.168.0.160:555/";
 
     /**创建幼儿园群**/
 	 public static final String CREATEKINDERGROUP = GROUPINFOSERVER_URL+"group/createGartenGroup";
 	 /**创建班级群**/
 	 public static final String CREATECLASSGROUP = GROUPINFOSERVER_URL+"group/createClassGroup";
 	 /**创建交流群**/
 	 public static final String CREATEGENERALGROUP = GROUPINFOSERVER_URL+"group/createGeneralGroup";
 	 /**加入群**/
 	 public static final String ADDGROUP = GROUPINFOSERVER_URL+"group/joinGroup";
 	 /**查看群资料byid**/
 	 public static final String LOOKGROUPDATABYID = GROUPINFOSERVER_URL+"group/getGroupInfoByGID";
 	 /**查看群分享文本**/
 	 public static final String GETSHARETEXT = GROUPINFOSERVER_URL+"group/getGroupShareTxt";
	 /**编辑幼儿园群**/
 	 public static final String EDIRTKINDERGROUP = GROUPINFOSERVER_URL+"group/updateGartenGroupInfo";
 	 /**编辑班级群**/
 	 public static final String EDIRTCLASSGROUP = GROUPINFOSERVER_URL+"group/updateClassGroupInfo";
 	 /**编辑交流群**/
 	 public static final String EDIRTGENERALGROUP = GROUPINFOSERVER_URL+"group/updateGeneralGroupInfo";
 	 /**伤处群成员**/
 	 public static final String DELGROUPMENBER = GROUPINFOSERVER_URL+"group/delMember";
 	 /**查看群资料bynum**/
 	 public static final String LOOKGROUPDATABYNUM = GROUPINFOSERVER_URL+"group/getGroupInfoByGNum";
 	 /**获取群成员列表**/
 	 public static final String GETGROUPMEMBER = GROUPINFOSERVER_URL+"group/getGroupMember";
 	 /**获取年级列表**/
 	 public static final String GETGRADELIST = GROUPINFOSERVER_URL+"group/getGrades";
 	
 	
}
