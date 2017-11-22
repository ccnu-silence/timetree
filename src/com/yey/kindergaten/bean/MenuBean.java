package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 统一入口菜单项
 * Created by longhengdong on 2015/2/12.
 */
@Table(name="menu")
public class MenuBean extends EntityBase{

    @Column(column="type")
    private int type;
    @Column(column="title")
    private String title;
    @Column(column = "url")
    private String url;

    public void setType(int type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
