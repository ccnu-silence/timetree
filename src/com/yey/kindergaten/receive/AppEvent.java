package com.yey.kindergaten.receive;

import android.os.Message;

public class AppEvent{

    public static final int PUSH_ADDFRIEND = 1; // 好友请求
    public static final int PUSH_AGREEFRIEND = 2; // 好友同意请求
    public static final int HOMEFRAGMENT_REFRESH_HEAD = 3; // 刷新主页
    public static final int HOMEFRAGMENT_REFRESH_NOTICE = 6; // 刷新通知
    public static final int HOMEFRAGMENT_REFRESH_CHAT = 4; // 刷新消息
    public static final int HOMEFRAGMENT_REFRESH_PUBLIC_HEAD = 5; // 刷新公众号头像
    public static final int HOMEFRAGMENT_REFRESH_CONTACT = 7; // 刷新通讯录
    public static final int HOMEFRAGMENT_REFRESH_ADDALBUM = 8; // 添加了相册刷新
    public static final int SERVICE_LIFEPHOTO_UPLOADING = 9; // 生活剪影/手工作品..正在上传
    public static final int SERVICE_LIFEPHOTO_UPLOAD_FINISH = 10; // 生活剪影/手工作品..上传完成
    public static final int SERVICE_CLASSPHOTO_UPLOADING = 11; // 班级照片上传

    public static final int UPLOAD_FINISH = 12; // 上传完成
    public static final int UPLOAD_START = 13; // 上传开始
    public static final int UPLOAD_PAUSE = 14; // 上传暂停
    public static final int NetERROR = 15; // 网络问题
    public static final int HOMEFRAGMENT_REFRESH_SYSTEMMESSAGE = 16; // 刷新系统消息
    public static final int HOMEFRAGMENT_REFRESH_GUIDE = 17; // 刷新新手任务
    public static final int TEACHERFRFRAGMENT_RELOADDATA = 18; // 重新刷新数据
    public static final int PARENTFRAGMENT_RELOADDATA = 19;
    public static final int REFRESHGETNEWMESSAGE = 20; // 透传刷新首页公众号消息
    public static final int KINDERFRAGMENT_RELOADDATA = 21;
    public static final int TEACHERPARANTFRAGMENT_BIRTHDAY = 22; // 老师发送生日祝福后，返回刷新
    public static final int CONTACTSPARENTLIST_BIRTHDAY = 23; // 园长发送生日祝福后，返回刷新
    public static final int PUBLICFRAGMENT = 24; // 刷新公众号
    public static final int CHAT_HOMEFRAGMENT_REFRESH = 25;
    public static final int CHAT_CHATACTIVITY_REFRESH = 26;
    public static final int CHAT_SCHOOLACTIVITY_REFRESH = 27; // 离园刷新
    public static final int CHAT_SCHOOL_NOTIFYDIALOG = 28; // 离园弹出框通知
    public static final int PUSH_FORCE_UPDATE = 29; // 强制更新
    public static final int PUSH_REFRESH_HOMEFRAGEMENT = 30; // 刷新首页消息
    public static final int REFRESH_SERVICES = 31; // 刷新服务

    private int type;
    private int position;
    private Message msg;

    public AppEvent(int type) {
        this.type = type;
    }

    public AppEvent(int type, Message msg, int position) {
        super();
        this.type = type;
        this.msg = msg;
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public Message getMsg() {
        return msg;
    }

}
