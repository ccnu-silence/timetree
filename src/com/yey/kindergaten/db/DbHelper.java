/**
 * 系统项目名称
 * com.yey.kindergaten.db
 * DbHelper.java
 * 
 * 2014年7月4日-上午10:52:03
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.AccountBean;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Chat;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.DiaryHomeInfo;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.GroupTwritte;
import com.yey.kindergaten.bean.GroupTwritte.comments;
import com.yey.kindergaten.bean.MenuBean;
import com.yey.kindergaten.bean.Message;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Msgtypes;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.RelationShipBean;
import com.yey.kindergaten.bean.SchedulesBean;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.bean.Upload;
import com.yey.kindergaten.fragment.HomeFragement;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.UtilsLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 
 * DbHelper
 * chaowen
 * 511644784@qq.com
 * 2014年7月4日 上午10:52:03
 * @version 1.0.0
 * 
 */
public class DbHelper {
    private static DbUtils dbutils = null;
    public static  String DBNAME = "kindergaten_";
    public static final int DBVERSION = 37;
    private final static String TAG = "DbHelper";
    public static boolean flag = false;

    public DbHelper(final Context context) {
        AccountInfo info = AppContext.getInstance().getAccountInfo();
        try {
            if (info.getUid()!=0) {
                if (dbutils == null) {
                    DBNAME = DBNAME + info.getUid();
                    dbutils = DbUtils.create(AppContext.getInstance(), DBNAME, DBVERSION, new DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                            if (newVersion > oldVersion) {
                                // 更新数据库
                                updateDb(db, newVersion, oldVersion);
                            }
                        }
                    });
                    initSql();
                }
            } else {
                UtilsLog.i(TAG, "DbHelper info.getUid is 0");
            }
        } catch (Exception e) {
            UtilsLog.i(TAG, "DbHelper create Exception");
        }
    }

    public synchronized static DbUtils getDB(final Context context) {
        AccountInfo info = AppContext.getInstance().getAccountInfo();
        try {
            if (info.getUid()!=0) {
                if (dbutils == null) {
                    DBNAME = DBNAME + info.getUid();
                    dbutils = DbUtils.create(AppContext.getInstance(), DBNAME, DBVERSION, new DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                            if (newVersion > oldVersion) {
                                // 更新数据库
                                updateDb(db, newVersion, oldVersion);
                            }
                        }
                    });
                    initSql();
                }
            } else {
                UtilsLog.i(TAG,"info.getUid is 0");
            }
        } catch (Exception e) {
            UtilsLog.i(TAG,"getDB Exception");
        }
        if (dbutils == null) {
            dbutils = DbUtils.create(context);
        }
        return dbutils;
    }
    
    public static void updateDBUtils(AccountInfo info){
        DBNAME = "kindergaten_" + info.getUid();
        dbutils = DbUtils.create(AppContext.getInstance(), DBNAME, DBVERSION, new DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                if (newVersion > oldVersion) {
                    // 更新数据库
                    updateDb(db, newVersion, oldVersion);
                }
            }
        });
    }
 
    public static void initSql() {
        try {
            getDB(AppContext.getInstance()).createTableIfNotExist(MessageRecent.class);
            getDB(AppContext.getInstance()).createTableIfNotExist(MessagePublicAccount.class);
            getDB(AppContext.getInstance()).createTableIfNotExist(comments.class);
            getDB(AppContext.getInstance()).createTableIfNotExist(GroupTwritte.class);
            getDB(AppContext.getInstance()).createTableIfNotExist(RelationShipBean.class);
            getDB(AppContext.getInstance()).createTableIfNotExist(Classe.class);
            getDB(AppContext.getInstance()).createTableIfNotExist(MenuBean.class);
            UtilsLog.i(TAG, "initSql is start");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据sql语句查询数据库
     * @param sql
     * @param databean
     * @param <T>
     * @return
     */
    public static <T> List<T> QueryTData(String sql, Class databean) {
        List<T> list = new ArrayList<T>();
        try {
            T a = (T) databean.newInstance();
            Cursor cursor;
            cursor = DbHelper.getDB(AppContext.getInstance()).execQuery(sql);
            cursor.moveToFirst();
            list = (List<T>) getAList(a, cursor);
            cursor.close();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> List<T> getAList(T databean, Cursor cursor){
        List<T> list = new ArrayList<T>();
            for (cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {
                java.lang.Class<? extends Object> classType = databean.getClass();
                T data = null;
                try {
                    data = (T) databean.getClass().newInstance();
                    Field[] fields = classType.getDeclaredFields(); // 获取T的所有属性
                    databean.getClass().newInstance();
                    if (fields !=null) {
                        for (Field field: fields) {
                            field.setAccessible(true);
                            Object objValue = null;
                            int index= cursor.getColumnIndex(field.getName());
                            if (index > -1) {
                                objValue = cursor.getString(index); // opt方法与get方法一样。不同的是get 如果为null 的时候异常，而 opt 可以返回空值
                                if (objValue!=null) {
                                    if (field.getType() == String.class) {
                                        field.set(data, objValue);
                                    }
                                    if (field.getType() == int.class) {
                                       field.set(data, Integer.valueOf(String.valueOf(objValue)));
                                    }
                                }
                            }
                        }
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                list.add(data);
            }
       return list;
    }
    
    /**
     * 获取accountInfo表
     *
     * updateAccountInfo
     * @param account 
     * void
     */
    public static void updateAccountInfo(AccountInfo account) {
        try {
            if (dbutils!=null) {
                if (dbutils.getDaoConfig().getDbName()!=null && !dbutils.getDaoConfig().getDbName().contains(DBNAME)) {
                    dbutils.close();
                    dbutils = null;
                }
            }
            if (dbutils == null) {
                DBNAME = DBNAME + account.getUid();
                dbutils = DbUtils.create(AppContext.getInstance(), DBNAME, DBVERSION, new DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                         if (newVersion > oldVersion) {
                             // 更新数据库
                             updateDb(db, newVersion, oldVersion);
                         }
                    }
                });
            }
            AccountInfo findaccount = dbutils.findFirst(Selector.from(AccountInfo.class).where("uid", "==", account.getUid()));
            if (findaccount == null) {
                UtilsLog.i(TAG, "updateAccountInfo account is: " + account + "");
                dbutils.save(account);
            } else {
                if (findaccount.getRelationship() > 0) {
                    account.setRelationship(findaccount.getRelationship());
                }
                dbutils.delete(findaccount);
                UtilsLog.i(TAG, "updateAccountInfo,delete findaccount ok");
                dbutils.save(account);
                UtilsLog.i(TAG, "updateAccountInfo,save account ok");
            }

        } catch (DbException ex) {
            UtilsLog.i(TAG, "updateAccountInfo Exception e: " + ex.getMessage());
        }
    }

    /**
     * 获取accountInfo表
     *
     * updateAccountInfo
     * @param account
     * void
     */
    public static void updateAccountInfo(AccountBean account) {
        try {
            if (dbutils!=null) {
                if (dbutils.getDaoConfig().getDbName()!=null && !dbutils.getDaoConfig().getDbName().contains(DBNAME)) {
                    dbutils.close();
                    dbutils = null;
                }
            }
            if (dbutils == null) {
                UtilsLog.i(TAG, "dbutils is null");
                DBNAME = DBNAME + account.getUid();
                dbutils = DbUtils.create(AppContext.getInstance(), DBNAME, DBVERSION, new DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                        if (newVersion > oldVersion) {
                            // 更新数据库
                            updateDb(db, newVersion, oldVersion);
                        }
                    }
                });
            }
            AccountBean findaccount = dbutils.findFirst(Selector.from(AccountBean.class).where("uid", "==", account.getUid()));
            UtilsLog.i(TAG, "updateAccountInfo account is: " + account + "");
            if (findaccount == null) {
                dbutils.save(account);
            } else {
                if (findaccount.getRelationship() > 0) {
                    account.setRelationship(findaccount.getRelationship());
                }
                dbutils.delete(findaccount);
                UtilsLog.i(TAG, "updateAccountInfo,delete findaccount ok");
                dbutils.save(account);
                UtilsLog.i(TAG, "updateAccountInfo,save account ok");
            }
        } catch (DbException ex) {
            ex.printStackTrace();
            UtilsLog.i(TAG, "updateAccountInfo fail, because DbException:" + ex.getMessage() + "/////" + ex.getCause());
        }
    }

    /**
     * 删除公众号相关的
     *
     * deletePublicAccount
     * @param pid 
     * void
     */
    public static void deletePublicAccount(int pid) {
        try {
            dbutils.delete(Message.class, WhereBuilder.b("publicid", "=", pid));
            dbutils.delete(MessageRecent.class,WhereBuilder.b("fromId", "=", pid));
            dbutils.delete(MessagePublicAccount.class, WhereBuilder.b("publicid", "=", pid));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void update(String sql, Class databean) {
        try {
            dbutils.update(databean, sql);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void deletefriendster(int pid) {
        try {
            dbutils.delete(GroupTwritte.class, WhereBuilder.b("twrid", "=", pid));
            dbutils.delete(comments.class,WhereBuilder.b("twrid","=",pid));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void deletefriendstercomment(int pid) {
        try {
            dbutils.delete(comments.class, WhereBuilder.b("cmtid", "=", pid));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    
    public static void deletegrowthdiary(int pid) {
        try {
            dbutils.delete(DiaryHomeInfo.class, WhereBuilder.b("diaryid", "=", pid));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /* public static void createChatMessage(String targetid,String content) {
        Chat chat = new Chat(pmid, content, contenttype, uid, toid, date, status)
    }*/
   
    public static void updateDb(DbUtils db,int newVersion,int oldVersion) {
        UtilsLog.i(TAG, "updateDb is start , update new field");
        try {
            // 更新upload表。增加一个module,sourcepath字段
            if (db.tableIsExist(Upload.class)) {
                if (!checkColumnExist(db.getDatabase(),"upload","module")) {
                    db.getDatabase().execSQL("ALTER TABLE upload ADD COLUMN module text;");
                    UtilsLog.i(TAG, "updateDb the new field upload/module");
                }
                if (!checkColumnExist(db.getDatabase(),"upload","compress")) {
                    db.getDatabase().execSQL("ALTER TABLE upload ADD COLUMN compress text;");
                    UtilsLog.i(TAG, "updateDb the new field upload/compress");
                }
                if (!checkColumnExist(db.getDatabase(),"upload","sourcepath")) {
                    db.getDatabase().execSQL("ALTER TABLE upload ADD COLUMN sourcepath text;");
                    UtilsLog.i(TAG, "updateDb the new field upload/sourcepath");
                }
                if (!checkColumnExist(db.getDatabase(),"upload","param")) {
                    db.getDatabase().execSQL("ALTER TABLE upload ADD COLUMN param text;");
                    UtilsLog.i(TAG, "updateDb the new field upload/param");
                }
            }

            // 更新SchedulesBean表。增加realnames字段
            if (db.tableIsExist(SchedulesBean.class)) {
                if (!checkColumnExist(db.getDatabase(),"schedulesBean","realnames")) {
                    db.getDatabase().execSQL("ALTER TABLE schedulesBean ADD COLUMN realnames text;");
                    UtilsLog.i(TAG, "updateDb the new field schedulesBean/realnames");
                }
                if (!checkColumnExist(db.getDatabase(),"schedulesBean","uids")) {
                    db.getDatabase().execSQL("ALTER TABLE schedulesBean ADD COLUMN uids text;");
                    UtilsLog.i(TAG, "updateDb the new field schedulesBean/uids");
                }
            }

            // 更新PublicAccount表。增加一个typeid字段
            if (db.tableIsExist(MessagePublicAccount.class)) {
                if (!checkColumnExist(db.getDatabase(),"MessagePublicAccount","typeid")) {
                    db.getDatabase().execSQL("ALTER TABLE MessagePublicAccount ADD COLUMN typeid integer default -1;");
                    UtilsLog.i(TAG, "updateDb the new field MessagePublicAccount/typeid");
                }
                if (!checkColumnExist(db.getDatabase(),"MessagePublicAccount","optag")) {
                    db.getDatabase().execSQL("ALTER TABLE MessagePublicAccount ADD COLUMN optag integer default -1;");
                    UtilsLog.i(TAG, "updateDb the new field MessagePublicAccount/optag");
                }
            }

            if (db.tableIsExist(Message.class)) {
                if (!checkColumnExist(db.getDatabase(),"message","typeid")) {
                    db.getDatabase().execSQL("ALTER TABLE message ADD COLUMN typeid integer default -1;");
                    UtilsLog.i(TAG, "updateDb the new field MessagePublicAccount/typeid");
                }
            }

            if (db.tableIsExist(MessageRecent.class)) {
                if (!checkColumnExist(db.getDatabase(),"MessageRecent","typeid")) {
                    db.getDatabase().execSQL("ALTER TABLE MessageRecent ADD COLUMN typeid integer default -1;");
                    UtilsLog.i(TAG, "updateDb the new field MessageRecent/typeid");
                }
                if (!checkColumnExist(db.getDatabase(),"MessageRecent","hxfrom")) {
                    db.getDatabase().execSQL("ALTER TABLE MessageRecent ADD COLUMN hxfrom text default 0;");
                    UtilsLog.i(TAG, "updateDb the new field MessageRecent/hxfrom");
                }
                if (!checkColumnExist(db.getDatabase(),"MessageRecent","hxto")) {
                    db.getDatabase().execSQL("ALTER TABLE MessageRecent ADD COLUMN hxto text default 0;");
                    UtilsLog.i(TAG, "updateDb the new field MessageRecent/hxto");
                }
                // msgid,fromId,toId的字段为String类型
                // Cursor cursor= db.getDatabase().rawQuery("select typeof(msgid) as msgid,typeof(fromId) as fromId,typeof(toId) as toId from messageRecent;",null);
                Cursor cursor = db.getDatabase().rawQuery("PRAGMA table_info(messageRecent)", null);
                String msgid = "";
                String fromId = "";
                String toId = "";

                int typeIdx = cursor.getColumnIndexOrThrow("type");
                int nameIdx = cursor.getColumnIndexOrThrow("name");

                while (cursor.moveToNext()) {
                    String type = cursor.getString(typeIdx);
                    String name = cursor.getString(nameIdx);
                    if ("INTEGER".equals(type)&& name.equals("fromId")) {
                        msgid = "INTEGER";
                    }
                }
                if ("INTEGER".equals(msgid)) {
                    // 创建临时表
                    db.getDatabase().execSQL("ALTER TABLE messageRecent RENAME TO tmp_messageRecent;");
                    db.getDatabase().execSQL("CREATE TABLE messageRecent(id integer primary key autoincrement,msgid text,"
                            + " name text,date text,fromId text,"
                            + "toId text,title text,content text,url text,fileurl text,"
                            +"newcount integer,action integer,contenttype integer"
                            + ",avatar text,typeid integer);");
                    db.getDatabase().execSQL("insert into messageRecent (msgid,name,"
                            + "date,fromId,toId,title,content,url,fileurl,newcount,action,contenttype,avatar,typeid) select msgid,name,date,fromId,"
                            + "toid,title,content,url,file_url,newcount,action,contentType,avatar,typeid from tmp_messageRecent;");
                    db.getDatabase().execSQL("drop table tmp_messageRecent;");
                }
                cursor.close();
            }

            // 检查Services表
            if (db.tableIsExist(Services.class)) {
                if (!checkColumnExist(db.getDatabase(),"services","orderno")) {
                    db.getDatabase().execSQL("ALTER TABLE services ADD COLUMN orderno integer;");
                    UtilsLog.i(TAG, "updateDb the new field services/orderno");
                }
                if (!checkColumnExist(db.getDatabase(),"services","tip")) {
                    db.getDatabase().execSQL("ALTER TABLE services ADD COLUMN tip integer;");
                    UtilsLog.i(TAG, "updateDb the new field services/tip");
                }
                if (!checkColumnExist(db.getDatabase(),"services","isfirstlook")) {
                    db.getDatabase().execSQL("ALTER TABLE services ADD COLUMN isfirstlook integer;");
                    UtilsLog.i(TAG, "updateDb the new field services/isfirstlook");
                }
            }
            if (!db.tableIsExist(AccountBean.class)) {
                db.createTableIfNotExist(AccountBean.class);
            }

            if (db.tableIsExist(AccountBean.class)) {
                db.getDatabase().execSQL("drop table accountBean");
                UtilsLog.i(TAG, "drop table accountBean success");
//              if (!checkColumnExist(db.getDatabase(),"accountBean","rights")) {
//                  db.getDatabase().execSQL("ALTER TABLE accountBean ADD COLUMN rights text;");
//                  UtilsLog.i(TAG, "updateDb the new field accountBean/rights");
//              }
//              if (!checkColumnExist(db.getDatabase(),"accountBean","cname")) {
//                  db.getDatabase().execSQL("ALTER TABLE accountBean ADD COLUMN cname text;");
//                  UtilsLog.i(TAG, "updateDb the new field accountBean/cname");
//              }
//              if (!checkColumnExist(db.getDatabase(),"accountBean","birthday")) {
//                  db.getDatabase().execSQL("ALTER TABLE accountBean ADD COLUMN birthday text;");
//                  UtilsLog.i(TAG, "updateDb the new field accountBean/birthday");
//              }
//              if (!checkColumnExist(db.getDatabase(),"accountBean","job")) {
//                  db.getDatabase().execSQL("ALTER TABLE accountBean ADD COLUMN job text;");
//                  UtilsLog.i(TAG, "updateDb the new field accountBean/job");
//              }
            }
            // 检查AccountInfo表
            if(db.tableIsExist(AccountInfo.class)) {
                if (!checkColumnExist(db.getDatabase(),"accountInfo","maingw")) {
                    db.getDatabase().execSQL("ALTER TABLE services ADD COLUMN maingw text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/maingw");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","sysop")) {
                    db.getDatabase().execSQL("ALTER TABLE services ADD COLUMN sysop text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/sysop");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","diarygw")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN diarygw text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/diarygw");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","classalbumgw")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN classalbumgw text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/classalbumgw");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","lifephotogw")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN lifephotogw text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/lifephotogw");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","rights")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN rights text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/rights");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","cname")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN cname text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/cname");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","birthday")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN birthday text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/birthday");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","job")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN job text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/job");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","pmvurl")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN pmvurl text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/pmvurl");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","defaultrelation")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN defaultrelation text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/defaultrelation");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","cid")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN cid text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/cid");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","apptitle")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN apptitle text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/apptitle");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","paygw")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN paygw text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/paygw");
                }
                if (!checkColumnExist(db.getDatabase(),"accountInfo","paytitle")) {
                    db.getDatabase().execSQL("ALTER TABLE accountInfo ADD COLUMN paytitle text;");
                    UtilsLog.i(TAG, "updateDb the new field accountInfo/paytitle");
                }
            }

            if (db.tableIsExist(GroupTwritte.class)) {
                if (!checkColumnExist(db.getDatabase(),"GroupTwritte","ftype")) {
                    db.getDatabase().execSQL("ALTER TABLE GroupTwritte ADD COLUMN ftype text;");
                    UtilsLog.i(TAG, "updateDb the new field GroupTwritte/ftype");
                }
                if (!checkColumnExist(db.getDatabase(),"GroupTwritte","albumid")) {
                    db.getDatabase().execSQL("ALTER TABLE GroupTwritte ADD COLUMN albumid text;");
                    UtilsLog.i(TAG, "updateDb the new field GroupTwritte/albumid");
                }

                if (!checkColumnExist(db.getDatabase(),"GroupTwritte","realname")) {
                    db.getDatabase().execSQL("ALTER TABLE GroupTwritte ADD COLUMN realname text;");
                    UtilsLog.i(TAG, "updateDb the new field GroupTwritte/realname");
                }
                if (!checkColumnExist(db.getDatabase(),"comments","realname")) {
                    db.getDatabase().execSQL("ALTER TABLE comments ADD COLUMN realname text;");
                    UtilsLog.i(TAG, "updateDb the new field comments/realname");
                }
                if (!checkColumnExist(db.getDatabase(),"comments","torealname")) {
                    db.getDatabase().execSQL("ALTER TABLE comments ADD COLUMN torealname text;");
                    UtilsLog.i(TAG, "updateDb the new field comments/torealname");
                }

//              if (!checkColumnExist(db.getDatabase(),"GroupTwritte","realname")) {
//                  db.getDatabase().execSQL("alter table GroupTwritte  rename column nickname to realname;");
//              }
//              if (!checkColumnExist(db.getDatabase(),"comments","realname")) {
//                  db.getDatabase().execSQL("alter table comments rename column nickname to realname;");
//              }
//              if (!checkColumnExist(db.getDatabase(),"comments","torealname")) {
//                  db.getDatabase().execSQL("alter table comments rename column tonickname to torealname;");
//              }
            }

            if (db.tableIsExist(RelationShipBean.class)) {
                if(!checkColumnExist(db.getDatabase(),"relationship","hxregtag")) {
                    db.getDatabase().execSQL("ALTER TABLE relationship ADD COLUMN hxregtag integer");
                    UtilsLog.i(TAG, "updateDb the new field relationship/hxregtag");
                }
            }

            // 生日祝福
            if (db.tableIsExist(Parent.class)) {
                if (!checkColumnExist(db.getDatabase(),"Parent","birthday")) {
                    db.getDatabase().execSQL("ALTER TABLE Parent ADD COLUMN birthday text");
                    UtilsLog.i(TAG, "updateDb the new field Parent/birthday");
                }
                if (!checkColumnExist(db.getDatabase(),"Parent","birthdaystatus")) {
                    db.getDatabase().execSQL("ALTER TABLE Parent ADD COLUMN birthdaystatus text");
                    UtilsLog.i(TAG, "updateDb the new field Parent/birthdaystatus");
                }
            }

            if (db.tableIsExist(PublicAccount.class)) {
                if (!checkColumnExist(db.getDatabase(),"PublicAccount","isfirstlook")) {
                    db.getDatabase().execSQL("ALTER TABLE PublicAccount ADD COLUMN isfirstlook integer");
                    UtilsLog.i(TAG, "updateDb the new field PublicAccount/isfirstlook");
                }
                if (!checkColumnExist(db.getDatabase(),"PublicAccount","ismenu")) {
                    db.getDatabase().execSQL("ALTER TABLE PublicAccount ADD COLUMN ismenu integer");
                    UtilsLog.i(TAG, "updateDb the new field PublicAccount/ismenu");
                }
            }

            if (db.tableIsExist(Classe.class)) {
                if(!checkColumnExist(db.getDatabase(),"Classe","childrencount")) {
                    db.getDatabase().execSQL("ALTER TABLE Classe ADD COLUMN childrencount integer");
                    UtilsLog.i(TAG, "updateDb the new field Classe/childrencount");
                }
            }

            if (db.tableIsExist(Children.class)) {
                if (!checkColumnExist(db.getDatabase(),"Children","birthday")) {
                    db.getDatabase().execSQL("ALTER TABLE Children ADD COLUMN birthday text");
                    UtilsLog.i(TAG, "updateDb the new field Children/birthday");
                }
                if (!checkColumnExist(db.getDatabase(),"Children","birthdaystatus")) {
                    db.getDatabase().execSQL("ALTER TABLE Children ADD COLUMN birthdaystatus text");
                    UtilsLog.i(TAG, "updateDb the new field Children/birthdaystatus");
                }
            }

            if (db.tableIsExist(Teacher.class)) {
                if (!checkColumnExist(db.getDatabase(),"Teacher","job")) {
                    db.getDatabase().execSQL("ALTER TABLE Teacher ADD COLUMN job text");
                    UtilsLog.i(TAG, "updateDb the new field Teacher/job");
                }
            }

            if (db.tableIsExist(Msgtypes.class)) {
                if (!checkColumnExist(db.getDatabase(),"msgTypes","desc")) {
                    db.getDatabase().execSQL("ALTER TABLE msgTypes ADD COLUMN desc text");
                    UtilsLog.i(TAG, "updateDb the new field msgTypes/desc");
                }
            }

            db.getDatabase().setVersion(newVersion);
            flag = true;
            UtilsLog.i(TAG, "updateDb complete, set flag true");
        } catch (DbException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "updateDb the new field DbException");
        } catch (SQLException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, "updateDb the new field SQLException");
        }
    }

    /**
     * 更新数据库表
     * @author zyj
     * @param db
     * @param tableName
     * @param columns
     */
    protected static void upgradeTables(SQLiteDatabase db, String tableName, String[] columns) {
        try {
            db.beginTransaction();
            // 1, Rename table.
            String tempTableName = tableName + "_temp";
            String sql = "ALTER TABLE " + tableName +" RENAME TO " + tempTableName;
            db. execSQL(sql);
            // 2, Create table.
            sql = "INSERT INTO " + tableName + " (" + columns + ") " + " SELECT " + columns + " FROM " + tempTableName;
//          onCreateTable(db);
//          3, Load data
            db.execSQL(sql);
            // 4, Drop the temporary table.
            db.execSQL("DROP TABLE IF EXISTS " + tempTableName);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<Services> getService(){
        List<Services> services = new ArrayList<Services>();
        try {
            services = dbutils.findAll(Selector.from(Services.class).orderBy("orderno"));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return services;
    }

    public static <T> List<T> getDataList(Class  databean) {
        List<T> list = new ArrayList<T>();
        try {
            list = dbutils.findAll(databean);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void closedb() {
        if (dbutils!=null) {
            dbutils.close();
            dbutils = null;
            DBNAME = "kindergaten_";
            HomeFragement.getNewFlag = 0;
        }
    }

    public static void updatechatHead(AccountInfo account) {
        // 修改了头像要更新chat表
        // dbutils.getDatabase().execSQL("update TABLE chat set avatar ='"+account.getAvatar()+"' where uid ="+account.getUid()+";");
        try {
            List<Chat> chatlist = dbutils.findAll(Chat.class, WhereBuilder.b("uid", "=", account.getUid()));
            if (chatlist!=null && chatlist.size() > 0) {
                for (int i = 0; i < chatlist.size(); i++) {
                    Chat chat = chatlist.get(i);
                    chat.setAvatar(account.getAvatar());
                    chatlist.set(i, chat);
                }
                dbutils.updateAll(chatlist, WhereBuilder.b("uid", "=", account.getUid()), new String[]{"avatar"});
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void updateContactHead(AccountInfo account) {
        // 修改通讯录表的头像
        try {
            if (account.getRole() == 2) {
                // 家长
                Children children = dbutils.findFirst(Children.class, WhereBuilder.b("uid", "=", account.getUid()));
                if (children!=null) {
                    children.setAvatar(account.getAvatar());
                    dbutils.update(children, WhereBuilder.b("uid", "=", account.getUid()), new String[]{"avatar"});
                    Contacts contacts = AppContext.getInstance().getContacts();
                    List<Children> clist = contacts.getParents();
                    for (int i = 0; i < clist.size(); i++) {
                        Children cc = clist.get(i);
                        if (cc.getUid() == children.getUid()) {
                            clist.set(i, children);
                            contacts.setParents(clist);
                            AppContext.getInstance().setContacts(contacts);
                            postEvent(AppEvent.HOMEFRAGMENT_REFRESH_CONTACT);
                        }
                    }
                }
            } else if (account.getRole() == 1 || account.getRole() == 0) {
                // 老师
                Teacher teacher = dbutils.findFirst(Teacher.class, WhereBuilder.b("uid", "=", account.getUid()));
                if(teacher!=null){
                    teacher.setAvatar(account.getAvatar());
                    dbutils.update(teacher, WhereBuilder.b("uid", "=", account.getUid()), new String[]{"avatar"});
                    Contacts contacts = AppContext.getInstance().getContacts();
                    List<Teacher> tlist = contacts.getTeachers();
                    for (int i = 0; i < tlist.size(); i++) {
                        Teacher cc = tlist.get(i);
                        if (cc.getUid() == teacher.getUid()) {
                            tlist.set(i, teacher);
                            contacts.setTeachers(tlist);
                            AppContext.getInstance().setContacts(contacts);
                            postEvent(AppEvent.HOMEFRAGMENT_REFRESH_CONTACT);
                        }
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    
    public static void updatechatHead(Friend friend) {
        // 修改了头像要更新chat表
        // dbutils.getDatabase().execSQL("update TABLE chat set avatar ='"+account.getAvatar()+"' where uid ="+account.getUid()+";");
        try {
            List<Chat> chatlist = dbutils.findAll(Chat.class, WhereBuilder.b("uid", "=", friend.getUid()));
            if (chatlist!=null && chatlist.size() > 0) {
                for (int i = 0; i < chatlist.size(); i++) {
                    Chat chat = chatlist.get(i);
                    chat.setAvatar(friend.getAvatar());
                    chatlist.set(i, chat);
                }
                dbutils.updateAll(chatlist, WhereBuilder.b("uid", "=", friend.getUid()), new String[]{"avatar"});
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    
    // 更新公众号消息的头像
    public static void updateMessagePublicAccountHead(PublicAccount account) {
        try {
            List<MessagePublicAccount> messagelist = dbutils.findAll(MessagePublicAccount.class, WhereBuilder.b("publicid", "=", account.getPublicid()).and("typeid", "=", "-1"));
            if (messagelist!=null && messagelist.size() > 0) {
                for (int i = 0; i < messagelist.size(); i++) {
                    MessagePublicAccount messagePublicAccount = messagelist.get(i);
                    messagePublicAccount.setAvatar(account.getAvatar());
                    messagelist.set(i, messagePublicAccount);
                }
                dbutils.updateAll(messagelist, WhereBuilder.b("publicid", "=", account.getPublicid()), new String[]{"avatar"});
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    // 修改网关
    public static void updateAccountUrl(AccountInfo info){
        if (info!=null) {
            try {
                AccountInfo accountinfo = getDB(AppContext.getInstance()).findFirst(AccountInfo.class, WhereBuilder.b("uid", "=", info.getUid()));
                if (accountinfo!=null) {
                    dbutils.delete(AccountInfo.class, WhereBuilder.b("uid", "=", info.getUid()));
                    dbutils.save(info);
                } else {
                    dbutils.save(info);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    // 修改网关
    public static void updateAccountUrl(AccountBean info) {
        if (info!=null) {
            try {
                AccountBean accountinfo = getDB(AppContext.getInstance()).findFirst(AccountBean.class, WhereBuilder.b("uid", "=", info.getUid()));
                if (accountinfo!=null) {
                    dbutils.delete(AccountInfo.class, WhereBuilder.b("uid", "=", info.getUid()));
                    dbutils.save(info);
                } else {
                    dbutils.save(info);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 老师根据uid查找小朋友
     * @param uid
     * @return
     */
    public static Teacher findAvatarById(String uid){
        Teacher teacher;
        try {
             teacher = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(Teacher.class).where("uid", "=", uid));
        } catch (DbException e) {
            e.printStackTrace();
            teacher = new Teacher();
        }
        return teacher;
    }

    public static void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
            }
        }).start();
    }

    /**
     * 查找老师表中的园长
     * @return
     */
    public static List<Teacher> findDirector() {
        List<Teacher> teacherList;
        try {
            teacherList = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Teacher.class).where("role", "=", 0));
        } catch (DbException e) {
            e.printStackTrace();
            teacherList = new ArrayList<Teacher>();
        }
        return teacherList;
    }

    /**
     * 获取数据库列表，防止数据库升级数据丢失
     * @author zyj
     * @param  db
     * @param  tableName
     * @return
     */
    protected static String[] getColumnNames(DbUtils db, String tableName) {
        String[] columnNames = null;
        Cursor c = null;
        try {
//          c = db.execQuery("PRAGMA table_info(" + tableName + ")", null);
            if (null != c) {
                int columnIndex = c.getColumnIndex("id");
                if (-1 == columnIndex) {
                    return null;
                }
                int index = 0;
                columnNames = new String[c.getCount()];
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    columnNames[index] = c.getString(columnIndex);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return columnNames;
    }

    /**
     * 方法1：检查某表列是否存在
     * @param db
     * @param tableName 表名
     * @param columnName 列名
     * @return -1表示无字段
     */
    private static boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false ;
        Cursor cursor = null ;
        try {
             // 查询一行
             cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0", null );
             result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
        } catch (Exception e) {
              System.out.println(e);
        } finally {
             if (null != cursor && !cursor.isClosed()) {
                 cursor.close() ;
             }
        }
        return result ;
    }

    public static void upGuideUrl(Services service) {
        try {
            dbutils.update(service, WhereBuilder.b("type", "=", service.getType()), new String[]{"tip"});
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
