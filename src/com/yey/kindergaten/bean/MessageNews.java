/**
 * 健康中心
 * com.yey.kindergaten.bean
 * MessageNews.java
 * 
 * 2014年7月23日-上午11:41:05
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.bean;

import java.util.List;

/**
 * 
 * MessageNews
 * chaowen
 * 511644784@qq.com
 * 2014年7月23日 上午11:41:05
 * @version 1.0.0
 * 
 */
public class MessageNews {

    List<Chat> friends;
    List<MessagePublicAccount> publics;
    List<MessageSystems> systems;

    public List<Chat> getFriends() {
        return friends;
    }

    public void setFriends(List<Chat> friends) {
        this.friends = friends;
    }

    public List<MessagePublicAccount> getPublics() {
        return publics;
    }

    public void setPublics(List<MessagePublicAccount> publics) {
        this.publics = publics;
    }

    public List<MessageSystems> getSystems() {
        return systems;
    }

    public void setSystems(List<MessageSystems> systems) {
        this.systems = systems;
    }
}
