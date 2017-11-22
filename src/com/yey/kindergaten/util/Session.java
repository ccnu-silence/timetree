/**
 * 
 */
package com.yey.kindergaten.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 会话管理操作类
 * 使用这方式 ，可以实现多个activity传值
 *
 * @author chaowen
 *
 */
public class Session {
    @SuppressWarnings("unchecked")
    private Map _objectContainer;
    private static Session session;

    @SuppressWarnings("unchecked")
    private Session(){
        _objectContainer = new HashMap();
    }

    public static Session getSession() {
        if (session == null) {
            session = new Session();
            return session;
        } else {
            return session;
        }
    }

    @SuppressWarnings("unchecked")
    public void put(Object key, Object value) {

        _objectContainer.put(key, value);
    }

    public Object get(Object key) {
        return _objectContainer.get(key);
    }

    public void cleanUpSession(){
        _objectContainer.clear();
    }

    public void remove(Object key){
        _objectContainer.remove(key);
    }
}
