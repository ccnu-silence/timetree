package com.yey.kindergaten.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.Chat;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.MessageSystems;
import com.yey.kindergaten.bean.Msgtypes;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MessageDb {
    public static final String MSG_DBNAME = "kindergaten";
    private SQLiteDatabase db;
    private static MessageDb mdb = null;
    private final static String TAG = "MessageDb";
    public MessageDb() {
        try {
            if (DbHelper.getDB(AppContext.getInstance())!=null) {
                DbHelper.getDB(AppContext.getInstance()).createTableIfNotExist(MessageRecent.class);
                db = DbHelper.getDB(AppContext.getInstance()).getDatabase();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public synchronized static MessageDb getInstance() {
        if (mdb == null) {
            mdb = new MessageDb();
        }
        return mdb;
    }

    // 获取所有消息
    public List<MessageRecent> getRecent(int id) {
        List<MessageRecent> list = new ArrayList<MessageRecent>();
        Cursor cursor = db.rawQuery("select * from messageRecent where toId=?", new String[]{id + ""});
        MessageRecent r = null;
        while (cursor.moveToNext()) {
            r = new MessageRecent();
            setClassValueBycursor(r, cursor);
            list.add(r);
        }
        // list = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(MessageRecent.class).where(WhereBuilder.b("toId", "=", id)));
        if (list == null) {
            list = new ArrayList<MessageRecent>();
        }
        return list;
    }
   
    // 获取最后一条记录id
    public int getId() {
        int strid = 0;
        try {
            Cursor cursor = DbHelper.getDB(AppContext.getInstance()).execQuery("select last_insert_rowid() from messageRecent");
            if (cursor.moveToFirst()) {
                strid = cursor.getInt(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return strid + 1;
    }

    // 获取最近会话的新数量
    public int getNewcount(int targetid) {
        int count = 0;
        try {
            MessageRecent mr = DbHelper.getDB(AppContext.getInstance()).findFirst(MessageRecent.class, WhereBuilder.b("fromId", "=", targetid));
            count = (mr == null ? 0 : mr.getNewcount() + 1);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void save(MessageRecent message) {
        try {
            DbHelper.getDB(AppContext.getInstance()).save(message);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public Friend findFriend(int uid) {
        Friend f = null;
        try {
            f = DbHelper.getDB(AppContext.getInstance()).findFirst(Friend.class, WhereBuilder.b("uid", "=", uid));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return f;
    }

    // 获取最新的好友消息更新到数据库
    public void updateNewsFriends(List<Chat> friends) {
        try {
            for (Chat chat: friends) {
                Chat c = DbHelper.getDB(AppContext.getInstance()).findFirst(Chat.class, WhereBuilder.b("pmid", "=", chat.getPmid()));
                if (c == null) {
                    DbHelper.getDB(AppContext.getInstance()).save(chat);
                    updateChatRecent(chat);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存新手指导系统消息
     *
     * @param mess
     */
    public void addNewSystemMessage(MessageSystems mess,int action) {
        MessageRecent newMessagePublic = new MessageRecent(mess.getPmid() + "", mess.getTitle(), mess.getDate(), "", "", mess.getTitle() == null ? "玩转时光树" : mess.getTitle(), mess.getContent(), AppUtils.replaceUrl(mess), "", 1, mess.getAction(), mess.getContenttype(), mess.getAvatar(), 0, "0", "0");
        try {
            MessageRecent m = DbHelper.getDB(AppContext.getInstance()).findFirst(MessageRecent.class,WhereBuilder.b("action", "=", action));
            if (m == null) {
                newMessagePublic.setToId(AppServer.getInstance().getAccountInfo().getUid() + "");
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
            } else {
                DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("action", "=", action));
                DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    // 获取最新的公众号消息更新到数据库
    public void updateNewsPublicAccounts(List<MessagePublicAccount> mpa) {
        try {
            for (MessagePublicAccount mp: mpa) {
                MessagePublicAccount m = DbHelper.getDB(AppContext.getInstance()).findFirst(MessagePublicAccount.class, WhereBuilder.b("pmid", "=", mp.getPmid()));
                if (m == null) {
                    DbHelper.getDB(AppContext.getInstance()).save(mp);
                    updatePublicAccountRecent(mp);
                } else {
                    if (0 == mp.getOptag()) {
                        // 删除
                        DbHelper.getDB(AppContext.getInstance()).delete(MessagePublicAccount.class, WhereBuilder.b("pmid", "=", mp.getPmid()));
                    } else {
                        // 更新
                        DbHelper.getDB(AppContext.getInstance()).delete(m);
                        DbHelper.getDB(AppContext.getInstance()).save(mp);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param chat
     * @throws DbException
     */
    private void updateChatRecent(final Chat chat) throws DbException {
        final MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", chat.getUid()));

        AppServer.getInstance().findUser(chat.getToid(), chat.getUid() + "", 1, new OnAppRequestListener() {

            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (code == AppServer.REQUEST_SUCCESS) {
                   List<Friend> flist = (List<Friend>) obj;
                    if (flist.size() > 0) {
                       Friend tofriend = flist.get(0);
                        if (tofriend!=null) {
                            String title = "";
                            if (chat.getAction() == AppConstants.PUSH_ACTION_AGREE_FRIENDS) {
                                chat.setContent("同意你的好友请求");
                                title = "同意你的好友请求";
                            }
                            if (chat.getAction() == AppConstants.PUSH_ACTION_ADD_FRIENDS) {
                                chat.setContent("请求添加好友");
                                title = "请求添加好友";
                            }
                            MessageRecent newMessagePublic = new MessageRecent(chat.getPmid() + "", tofriend.getNickname() + "", chat.getDate(), chat.getUid() + "", chat.getToid() + "", chat.getContent(), title, "", "", 1, chat.getAction(), chat.getContenttype(), chat.getAvatar(), -1, "0", "0");
                            if (messagePublic == null) {
                                try {
                                    DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                } // 存入最近会话
                            } else {
                                int count = messagePublic.getNewcount();
                                try {
                                    DbHelper.getDB(AppContext.getInstance()).delete(messagePublic);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                newMessagePublic.setNewcount(count + 1);
                                try {
                                    DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                } // 存入最近会话
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new AppEvent(AppEvent.HOMEFRAGMENT_REFRESH_CHAT));
                                }
                            }).start();
                        }
                    }
                }
            }
        });

    }

    /**
     * @param pa
     * @throws DbException
     */
    public void updatePublicAccountRecent(MessagePublicAccount pa) throws DbException {
        MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("fromId", "=", pa.getPublicid()).and(WhereBuilder.b("typeid", "=", pa.getTypeid())));
        PublicAccount tagetPublicAccount = DbHelper.getDB(AppContext.getInstance()).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", pa.getPublicid()));
        if (tagetPublicAccount == null) {
            tagetPublicAccount = new PublicAccount();
            tagetPublicAccount.setPublicid(pa.getPublicid());
            tagetPublicAccount.setNickname("公众号");
            tagetPublicAccount.setAvatar("");
        }
        // 将MessagePublicAccount转换为MessageRecent
        MessageRecent newMessagePublic = new MessageRecent(pa.getPmid() + "", tagetPublicAccount.getNickname(), pa.getDate(), pa.getPublicid() + "", AppServer.getInstance().getAccountInfo().getUid() + "", pa.getTitle(), pa.getContenturl(), pa.getUrl(), pa.getFileurl(), 1, pa.getAction(), pa.getContenttype(), tagetPublicAccount.getAvatar(), pa.getTypeid(), "0", "0");
        // MessageRecent newMessagePublic  = new MessageRecent(pa.getPmid(), tagetPublicAccount.getNickname() + "", pa.getDate(), pa.getPublicid(), AppServer.getInstance().getAccountInfo().getUid(), pa.getTitle(), pa.getContent()+"", pa.getFileurl()+"", 1, pa.getAction(), pa.getContenttype(),tagetPublicAccount.getAvatar());
        Msgtypes type = DbHelper.getDB(AppContext.getInstance()).findFirst(Msgtypes.class, WhereBuilder.b("publicid", "=", pa.getPublicid()).and("typeid", "=", pa.getTypeid()));
        if (messagePublic == null) {
            if (pa.getTypeid()!=-1) {                                        // 针对二级分类(typeid!=-1表示是二级分类的公众号，默认为-1)设置二级分类的标题和icon
                if (type!=null) {
                    newMessagePublic.setName(type.getTypename());
                    newMessagePublic.setAvatar(type.getAvatar());
                }
            }
            DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
        } else {
            int count = messagePublic.getNewcount();
            if (pa.getTypeid()!=-1 && type!=null) {
                newMessagePublic.setName(type.getTypename());
                newMessagePublic.setAvatar(type.getAvatar());
            }
            DbHelper.getDB(AppContext.getInstance()).delete(messagePublic);
            newMessagePublic.setNewcount(count + 1);
            DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); // 存入最近会话
        }
    }

    /**
     * <p>
     * 利用反射机制给对象赋值
     *
     * </p>
     * @param obj
     * @param cursor void
     */
    public static void setClassValueBycursor(Object obj, Cursor cursor) {
        int ColCount = cursor.getColumnCount();
        int i = 0;
        for (i = 0; i < ColCount; i++) {
            String ColName = cursor.getColumnName(i);
            try {
                Field f = obj.getClass().getField(ColName);
                String ret = cursor.getString(i);
                if (f == null)
                    continue;
                if (ret == null)
                    ret = "";
                f.set(obj, ret);
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void closeDb() {
        if (db !=null) {
            db.close();
        }
    }

}
