package com.yey.kindergaten.net;

import com.yey.kindergaten.AppContext;

public class URL {
//  public static final String SERVER_URL = "http://t.kmapp.zgyey.com/";
    static final String TESTURL = "http://kmapp.zgyey.com/";
//    static final String TESTURL = "http://192.168.0.138:555/";
//  public static final String SERVER_URL = "http://kmapp.zgyey.com/";
//  static final String TESTURL = "http://kmapp.zgyey.com";
    public static final String SERVER_URL = "http://kmapp.zgyey.com/";
//    public static final String SERVER_URL = "http://192.168.0.138:555/";
    static final String SERVER_BASE_URL = "http://abb.gw.zgyey.com/json.ashx?";
    // 时光树手机支付网关
//    static final String PAY_URL = "http://t.sgs.pay.zgyey.com/";
    static final String PAY_URL = "http://sgs.pay.zgyey.com/";

//  static final String SERVER_BASE_URL = "http://192.168.0.141:811/json.ashx?";
//  public static final String SERVER_URL = "http://192.168.00.138:555/";
//  public static final String SERVER_URL = "http://t.kmapp.zgyey.com/";

//  public static final String UPLOADIMG = "http://totfup.zgyey.com/classgroup/UploadClassGroupFile.ashx/";
    static final String INIT_SERVER_URL = AppContext.getInstance().getMainGateWay() + "/";
    static final String SERVER_URL_CONTACT = AppServer.getInstance().getAccountInfo().getContactgw() + "/";
    static final String SERVER_URL_MESSAGE = AppServer.getInstance().getAccountInfo().getMsggw() + "/";
    static final String SERVER_URL_TASK = AppServer.getInstance().getAccountInfo().getTaskgw() + "/";
    static final String SERVER_URL_SYS = AppServer.getInstance().getAccountInfo().getSysgw() + "/";
    static final String SERVER_URL_GROUP = AppServer.getInstance().getAccountInfo().getGroupgw() + "/";
    static final String SERVER_URL_SCHEDULE = AppServer.getInstance().getAccountInfo().getSchedulegw() + "/";
    static final String SERVER_URL_NOTIFICATION = AppServer.getInstance().getAccountInfo().getNotifygw() + "/";
    static final String SERVER_URL_UPLOAD = AppServer.getInstance().getAccountInfo().getUploadurl() + "/";
    public static final String urlkey = "zgyey_235&*9)!";

    //------main begin------
    /** 登录 **/
    public static final String LOGIN = INIT_SERVER_URL + "/main/login";
    /** 注册 **/
    public static final String REGISTER = INIT_SERVER_URL + "/main/register";
    /** 新增幼儿园 **/
    public static final String CREATKINDERGATEN = INIT_SERVER_URL + "/main/createGarten";
    /** 查询幼儿园 **/
    public static final String SEARCHKINDERGATEN = INIT_SERVER_URL + "/main/findGarten";
    /** 获取地区 **/
    public static final String GETKINDERGATENAREA = INIT_SERVER_URL + "/main/getArea";
    /** 加入幼儿园 **/
    public static final String JOINKINDERGATEN = INIT_SERVER_URL + "/main/joinGarten";
    /** 班级列表 **/
    public static final String GETCLASSLIST = INIT_SERVER_URL + "/main/getClassList";
    /** 加入班级 **/
    public static final String JOINCLASS = INIT_SERVER_URL + "/main/joinClass";
    /** 找回密码|检查手机是否绑定 **/
    public static final String PWCHECKPHONE = INIT_SERVER_URL + "/main/findPWCheckPhone";
    /** 找回密码|获取验证码 **/
    public static final String PWSENDPHONECODE = INIT_SERVER_URL +"/main/sendPhoneCode";
    /** 找回密码|设置密码 **/
    public static final String PWUPDATEPASSWORD = INIT_SERVER_URL + "/main/findPWUpdatePassword";
    /** 环信注册状态修改 **/
    public static final String UPDATEHXSTATE = INIT_SERVER_URL + "main/updateHXState";
    /** 成长日记上传照片 **/
    public static final String UPLOADIMG = SERVER_URL_UPLOAD + "classgroup/UploadClassGroupFile.ashx/";
    //------main end------

    //------Main info.getMainway() begin------
    /** 绑定或解绑手机 **/
    public static final String BINDPHONE = "main/bindingPhone";
    /** 修改密码 **/
    public static final String MODIFYPASSWORD = "main/updatePassword";
    /** 获取服务菜单 **/
    public static final String GETSERVICES = "main/getServices";
    /** 修改个人资料 **/
//  public static final String MODIFYSELFINFO = "main/updatePersonalInfo";
    public static final String MODIFYSELFINFO = "main/updatePersonalInfoV2";
    /** 用户主动退出 **/
    public static final String LOGINOUT = "main/logout";
    /** 更新设备id **/
    public static final String UPDATEDEVICEID = "main/updateClientID";
    /** 上传头像 **/
    public static final String UPAVATAR = "main/uploadAvatar";
    /** 获取分享文本 **/
    public static final String GETINVITETEXT = "main/getInviteText";
    /** 修改幼儿园名称 **/
    public static final String UPDATEGARTENNAME = "main/updateGartenName";
    /** 发送短信 **/
    public static final String SENDSMSMESSAGE = "message/sendSMS";
    //------Main info.getMainway() end------

    //------通讯录 begin------
    /** 搜索用户|公众号 **/
    public static final String FINDUSER = "/contact/search";
    /** 发送好友请求 **/
    public static final String ADDFRIEND = SERVER_URL_CONTACT + "contact/sendAddFriendRequest";
    /** 处理好友请求 **/
    public static final String HANDLEFRIEND = SERVER_URL_CONTACT + "contact/handleAddFriendRequest";
    /** 删除联系人 **/
    public static final String DELETCONTACTPEOPLE = SERVER_URL_CONTACT + "contact/delFriend";
    /** 根据cid获取家长列表 **/
    public static final String GETPARENTBYCID = "/contact/getParentsByCid";
    /** 通讯录-园长 **/
    public static final String GETCONTACTBYMASTER = "/contact/getContactsForMaster";
    /** 通讯录-老师 **/
    public static final String GETCONTACTBYTEACHER = "/contact/getContactsForTeacher";
    /** 通讯录-家长 **/
    public static final String GETCONTACTBYPARENT = "/contact/getContactsForParent";
    /** 查看园长，老师，家长，公众号资料 **/
    public static final String VIEWINFO = "/contact/viewInfo";
    /** 修改消息默认接受者 **/
    public static final String UPDATEDEFAULTRELATION = "main/updateDefaultRelation";
    /** 根据Kid获取全园班级列表 **/
    public static final String GETCLASSBYKID = "contact/getClassesByKid";
    /** 根据Kid获取全园老师(包括园长) **/
    public static final String GETTEACHERBYKID = "contact/getTeachersByKid";
    /** 根据Kid获取老师班级的全部小朋友 **/
    public static final String GETPARENTBYKID = "contact/getParentsByTeacher";
    /** 根据Cid获取全班人员 **/
    public static final String GETTEACHERANDPARENTBYCID = "contact/getTeachersAndParentsByCid";
    /** 获取用户公众号 **/
    public static final String GETPUBLICS = "contact/getPublics";
    //------通讯录 end------

    //------消息与公众号 begin------
    /** 获取最新消息 **/
    public static final String NEWMESSAGE = "/message/getNewMessages";
    /** 获取最近消息(消息列表为空时调用--->即getNewMessage取不到时) **/
    public static final String CONVERSORMESSAGE = "/message/getConversationMessages";
    /** 发消息 **/
    public static final String SENDCHAT = "/message/sendMessage";
    /** 获取公众号历史消息 **/
    public static final String GETPUBLICHISTORYMESSAGE = "/message/getPMHistoryMessages";
    /** 查看公众号最近几条消息 **/
    public static final String GETPMLATEMESSAGE = "/message/getPMLatestMessages";
    /** 订阅公众号 **/
    public static final String BOOKPUBLICACCOUNT = "/message/updateSubscription";
    /** 公众号菜单 **/
    public static final String PUBLICACCOUNT_MENUS = SERVER_URL_MESSAGE + "pm/getMenus";
    /** 公众号列表 **/
    public static final String PUBLICACCOUNT_LIST = SERVER_URL + "pm/publics_Getlist";
    /** 修改消息的状态 2为已读 **/
    public static final String UPDATEMESSAGE = "/message/pushMessageUpdate";
    //------消息与公众号 end------

    //------任务与积分 begin------
    /** 获取用户当前任务列表 **/
    public static final String GETTASKS = SERVER_URL_TASK + "task/getTasks";
    /** 获取积分 **/
    public static final String GETCHECKPOINT = SERVER_URL_TASK + "task/checkPoint";
    /** 获取积分商品列表 **/
    public static final String GETPRODUCT = SERVER_URL_TASK + "task/getProducts";
    /** 获取所有地址本信息 **/
    public static final String GETALLADDRESS = SERVER_URL_TASK + "task/getAddresses";
    /** 保存地址本 **/
    public static final String SAVEADDRESS = SERVER_URL_TASK + "task/addAddress";
    /** 删除地址本 **/
    public static final String DELADDRESSBOOK = SERVER_URL_TASK + "task/delAddress";
    /** 删除地址本 **/
    public static final String UPADDRESSBOOK = SERVER_URL_TASK + "task/updateAddress";
    /** 兑换商品 **/
    public static final String EXCHANGEPOINT = SERVER_URL_TASK + "exchange/exchangeProduct";
    //------任务与积分 end------

    //------系统 begin------
    /** 上传文件 **/
    public static final String UPLOADFILE = "/sys/uploadFile";
    /** 获取系统配置 **/
    public static final String GETSYSCONFIG = "/sys/getSysConf";
    /** FeedBack */
    public static final String FEEDBACK = "/sys/feedback";
    //------系统 end------

    //------群 begin------
    /** 获取群列表 **/
    public static final String GETGROUPS = SERVER_URL_GROUP + "group/getGroups";
    /** 获取班级id **/
    public static final String GETCID = TESTURL + "classgroup/getClasses";
    /** 获取群说说 **/
    public static final String GETGROUPTWRITTER = TESTURL + "classgroup/getGroupTwitters";
    /** 发说说 **/
    public static final String SENDTWITTER = TESTURL + "classgroup/postTwitter";
    /** 删除说说 **/
    public static final String DELTWITTER = TESTURL + "classgroup/delTwitter";
    /** 获取评论 **/
    public static final String SENTCOMMENT = TESTURL + "classgroup/comment";
    /** 点赞|删除赞 **/
    public static final String SETZAN = TESTURL + "classgroup/like";
    /** 删除评论 **/
    public static final String DELDISCUSS = TESTURL + "classgroup/delComment";
    /** 创建幼儿园群 **/
    public static final String CREATEKINDERGROUP = SERVER_URL_GROUP + "group/createGartenGroup";
    /** 创建班级群 **/
    public static final String CREATECLASSGROUP = SERVER_URL_GROUP + "group/createClassGroup";
    /** 创建交流群 **/
    public static final String CREATEGENERALGROUP = SERVER_URL_GROUP + "group/createGeneralGroup";
    /** 加入群 **/
    public static final String ADDGROUP = SERVER_URL_GROUP + "group/joinGroup";
    /** 查看群资料byid **/
    public static final String LOOKGROUPDATABYID = SERVER_URL_GROUP + "group/getGroupInfoByGID";
    /** 查看群分享文本 **/
    public static final String GETSHARETEXT = SERVER_URL_GROUP + "group/getGroupShareTxt";
    /** 编辑幼儿园群 **/
    public static final String EDIRTKINDERGROUP = SERVER_URL_GROUP + "group/updateGartenGroupInfo";
    /** 编辑班级群 **/
    public static final String EDIRTCLASSGROUP = SERVER_URL_GROUP + "group/updateClassGroupInfo";
    /** 编辑交流群 **/
    public static final String EDIRTGENERALGROUP = SERVER_URL_GROUP + "group/updateGeneralGroupInfo";
    /** 伤处群成员 **/
    public static final String DELGROUPMENBER = SERVER_URL_GROUP + "group/delMember";
    /** 查看群资料bynum **/
    public static final String LOOKGROUPDATABYNUM = SERVER_URL_GROUP + "group/getGroupInfoByGNum";
    /** 获取群成员列表 **/
    public static final String GETGROUPMEMBER = SERVER_URL_GROUP + "group/getGroupMember";
    /** 获取年级列表 **/
    public static final String GETGRADELIST = SERVER_URL_GROUP + "group/getGrades";
    /** 发布相册动态 **/
    public static final String UPLOADPHOTOFORCLASS = TESTURL + "classgroup/postTwitterForClassPhoto";
    //------群 end------

    //------日程 begin------
    /** 获取日程 **/
    public static final String GETSCHEDULEINFO = "schedule/downloadAllSchedules";
    /** 上传日程 **/
    public static final String UPLOADSEHEDULE = "schedule/uploadSchedule";
    //------日程 end------

    //------通知 begin------
    /** 查询是否开通平台短信 **/
    public static final String CHECKOPENSMS = SERVER_URL_NOTIFICATION + "notification/checkOpenSMS";
    /** 发送通知 **/
    public static final String SENDNOTIFICATION = SERVER_URL_NOTIFICATION + "notification/sendNotification";
    /** 获取模板分类 **/
    public static final String GETTEMPLATETYPES = SERVER_URL_NOTIFICATION + "notification/getTemplateTypes";
    /** 根据分类获取模板 **/
    public static final String GETTEMPLATEBYTYPE = SERVER_URL_NOTIFICATION + "notification/getTemplatesByType";
    /** 获取历史通知 **/
    public static final String GETHISTORYNOTIFICATION = SERVER_URL_NOTIFICATION + "notification/getHistoryNotification";
    //------通知 end------

    //------成长日记 begin------
    /** 获取某个人的说说 **/
    public static final String TWITTERSBYUID = INIT_SERVER_URL + "twitter/getTwittersByUid";
    /** 获取成长日记 **/
    public static final String GETGROWTHDARIY = "/diary/diaryList";
    /** 发表成长日记 **/
    public static final String SETGROWTHDARIY = "/diary/diaryCreate";
    /** 删除成长日记 **/
    public static final String DELETEGROWTHDARIY = "/diary/diaryDelete";
    //------成长日记完 end------

    //------班级相册,生活剪影，手工作品 begin------
    /** 保存相册照片并发动态(又拍) **/
    public static final String INSERTINTOALBUM = "classPhoto/InsertPhotoesIntoAlbum";
    /** 班级相册 **/
    public static final String CLASSPHOTODATA = "classPhoto/teacherClassAlbumList";
    /** 班级相册(根据cid) **/
    public static final String CLASSPHOTODATABYCID = "classPhoto/getAlbumsByCid";
    /** 创建相册 **/
    public static final String CLASSPHOTO_CREATE = "classPhoto/teacherClassAlbumCreate";
    /** 删除相册 **/
    public static final String CLASSPHOTO_DELETE = "classPhoto/teacherClassAlbumDelete";
    /** 获取相册 **/
    public static final String CLASSPHOTO_GET = "classPhoto/teacherClassPhotoList";
    /** 删除相片 **/
    public static final String CLASSPHOTO_DELETEPHOTO = "classPhoto/teacherClassPhotoDelete";
    /** 获取学期 **/
    public static final String GETTERLIST = "homebook/teacherClassTermList";
    /** 获取全部 **/
    public static final String GETALLLIFEPHOTO = "homebook/teacherClassTermPhotoList";
    /** 获取生活剪影/手工作品 **/
    public static final String GETLIFEPHOTO = "homebook/hbPhotoGetList";
    /** 获取指定的生活剪影|手工作品内容 **/
    public static final String GETCHILDLIFEPHOTO = "homebook/hbPhotoChildGetList";
    /** 删除生活剪影|手工作品相片 **/
    public static final String DELETECHILDPHOTO = "homebook/hbPhotoDelete";
    /** 编辑生活剪影|手工作品描述 **/
    public static final String EDITCHILDDECS = "homebook/hbPhotodescUpdate";
    /** 编辑班级相册 **/
    public static final String UPDATECLASSABLUM = "classPhoto/teachercClassAlbumUpdate";
    //------班级相册，生活剪影，手工作品 end------

    //------上传 begin------
    /** 班级相册上传 **/
    public static final String UPLOAD_CLASSPHONE = "/class/UploadClassPhotoStream.ashx";
    /** 生活剪影，手工作品上传**/
    public static final String UPLOAD_LIFEPHONE = "/hb/UploadHBPhotoLifeWork.ashx";
    /** 成长日记上传**/
    public static final String UPLOADFILEIMAG = "/diary/UploadDiaryFile.ashx";
    /** 网页形式上传 **/
    public static final String UPLOADWEBIMG = "/nhb_mobile/UploadPhoto.ashx";
    //------上传结束 end------

    //------支付 begin------
    /** 创建支付宝订单 **/
    public static final String CREATEALIPAYORDER = "alipayto/CreateOrder";
    /** 创建支付宝订单 **/
    public static final String CREATEWECHATORDER = "wxpay/AppPayPage.aspx";
    /** 开通套餐 **/
    public static final String OPENVIP = "Pay/OpenVip";
    //------支付结束 end------

    /** 记录用户打开APP日志 **/
    public static final String LAUNCHLOG = "/log/logLaunchSGS";

    public  static  final  String url_ceshi = "http://192.168.0.82:8080/main/register";

}
