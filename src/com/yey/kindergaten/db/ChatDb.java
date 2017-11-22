/**
 * 
 */
package com.yey.kindergaten.db;

import android.content.Context;
import android.database.Cursor;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.Chat;
import com.yey.kindergaten.bean.Friend;

/**
 * @author chaowen
 *
 */
public class ChatDb {

    public ChatDb(Context context)  {
        try {
            DbHelper.getDB(context).createTableIfNotExist(Chat.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
   
    public int getId()  {
        int strid = 0;
        try {
            Cursor cursor = DbHelper.getDB(AppContext.getInstance()).execQuery("select max(id) from chat");
            if (cursor.moveToFirst()) {
                 strid = cursor.getInt(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return strid + 1;
    }

    public void save(Chat chat) {
        try {
            DbHelper.getDB(AppContext.getInstance()).save(chat);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public Friend findTarget(int uid) {
        Friend f = null;
        try {
            f = DbHelper.getDB(AppContext.getInstance()).findFirst(Friend.class, WhereBuilder.b("uid", "=", uid));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return f;
    }

    public void updateChat(Chat chat,int pmid){
        try {
            DbHelper.getDB(AppContext.getInstance()).update(chat, WhereBuilder.b("pmid", "=", pmid), new String[]{"status","pmid","content","contenttype"});
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    public void DelChat(int pmid) {
        try {
            DbHelper.getDB(AppContext.getInstance()).delete(Chat.class, WhereBuilder.b("pmid", "=", pmid));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
