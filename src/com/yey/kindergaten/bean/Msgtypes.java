package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 公众号的消息类型
 *
 * @author Administrator
 */
@Table(name="msgTypes")
public class Msgtypes extends EntityBase{
    @Column(column="publicid")
    public int publicid;
    @Column(column="typeid")
    public int typeid;          // 分类id
    @Column(column="typename")
    public String typename;     // 分类名字
    @Column(column="avatar")
    public String avatar;       // 头像
    @Column(column="desc")
    public String desc;         // 详情

    public int getPublicid() {
        return publicid;
    }
    public void setPublicid(int publicid) {
        this.publicid = publicid;
    }
    public int getTypeid() {
        return typeid;
    }
    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }
    public String getTypename() {
        return typename;
    }
    public void setTypename(String typename) {
        this.typename = typename;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
