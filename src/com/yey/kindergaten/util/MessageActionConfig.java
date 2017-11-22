package com.yey.kindergaten.util;

/**
 * Created by zy on 2015/4/3.
 */
public class MessageActionConfig {

    /***********环信驱动消息**********/

    /**园长注册玩转时光树*/
    public static final int  DIRECTORREDIGIT_ACTION = 71;

    /**老师注册玩转时光树(有KID)*/
    public static final int  TEACHERREDIGIT_HASKID_ACTION = 72;

    /**老师注册玩转时光树(无KID)*/
    public static final int  TEACHERREDIGIT_NOKID_ACTION = 73;

    /**园长增加消息(邀请老师加入)*/
    public static final int  DIRECTOR_INVITE_TEACHER_ACTION = 74;

    /**园长增加消息(邀请家长加入)*/
    public static final int  DIRECTOR_INVETE_PARENT_ACTION = 75;

    /**老师增加消息(邀请家长加入)*/
    public static final int  TEACHER_INVETE_PARENT_ACTION = 76;

    /**完善个人资料消息*/
    public static final int  COPLETE_SELFINGO_ACTION = 77;
}
