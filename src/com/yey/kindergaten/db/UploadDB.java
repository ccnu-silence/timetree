/**
 * 
 */
package com.yey.kindergaten.db;

import android.content.Context;
import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.Upload;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chaowen
 *
 */
public class UploadDB {
    private static final String DB_NBAM = "upload";

    private DbUtils db;
    private static UploadDB uploadDB = null;

    public UploadDB(Context context) {
        try {
            DbHelper.getDB(context).createTableIfNotExist(Upload.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static UploadDB getInstnce(){
        if (uploadDB == null) {
            uploadDB = new UploadDB(AppContext.getInstance());
        }
        return uploadDB;
    }

    /**
     * 保存上传信息
     *
     * @param upload
     * @return
     */
    public boolean saveUpload(Upload upload) {
        try {
            DbHelper.getDB(AppContext.getInstance()).save(upload);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除上传信息
     *
     * @param uploadfilepath
     * @return
     */
    public boolean delUpload(String uploadfilepath) {
        Selector selector = Selector.from(Upload.class);
        selector.where(WhereBuilder.b("uploadfilepath", "=", uploadfilepath));
        try {
            Upload upload = DbHelper.getDB(AppContext.getInstance()).findFirst(selector);
            if (upload!=null) {
                DbHelper.getDB(AppContext.getInstance()).delete(upload);
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取上传资源Id
     *
     * @param sourcepath
     * @return
     */
    public long getBindId(String sourcepath) {
        Selector selector = Selector.from(Upload.class);
        selector.where(WhereBuilder.b("sourcepath", "=", sourcepath));
        long bindId = 0;
        try {
            Upload upload = DbHelper.getDB(AppContext.getInstance()).findFirst(selector);
            if (upload == null) {
                return 0;
            }
            bindId = upload.getFileId();
        } catch (DbException e) {
            e.printStackTrace();
            return 0;
        }
        return bindId;
    }

    /**
     * 获取上传资源断点位置
     *
     * @param uploadfilepath
     * @return
     */
    public long getFileCurrentSize(String uploadfilepath) {
        Selector selector = Selector.from(Upload.class);
        selector.where(WhereBuilder.b("uploadfilepath", "=", uploadfilepath));
        long size = 0;
        try {
            Upload upload = DbHelper.getDB(AppContext.getInstance()).findFirst(selector);
            if (upload == null) {
                return 0;
            }
            size = upload.getUploadSize();
        } catch (DbException e) {
            e.printStackTrace();
            return 0;
        }
        return size;
    }

    /**
     * 保存已传的大小到数据库
     * @param fileid
     * @return
     */
    public boolean updateUpload(long fileid,long currentSize) {
        Selector selector = Selector.from(Upload.class);
        selector.where(WhereBuilder.b("fileId", "=", fileid));
        try {
            Upload upload = DbHelper.getDB(AppContext.getInstance()).findFirst(selector);
            if (upload!=null) {
                upload.setUploadSize(currentSize);
                DbHelper.getDB(AppContext.getInstance()).update(upload, WhereBuilder.b("fileId", "=", fileid), new String[]{"uploadSize"});
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新路径
     *
     * @param upload
     * @return
     */
    public boolean updateUploadPath(Upload upload) {
        Selector selector = Selector.from(Upload.class);
        selector.where(WhereBuilder.b("fileId", "=", upload.getFileId()));
        try {
            Upload queryupload = DbHelper.getDB(AppContext.getInstance()).findFirst(selector);
            if (queryupload!=null) {
                queryupload.setUploadfilepath(upload.getUploadfilepath());
                DbHelper.getDB(AppContext.getInstance()).update(upload, WhereBuilder.b("fileId", "=", upload.getFileId()), new String[]{"uploadfilepath"});
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateUploadParam(Upload upload) {
        Selector selector = Selector.from(Upload.class);
        selector.where(WhereBuilder.b("fileId", "=", upload.getFileId()));
        try {
            Upload queryupload = DbHelper.getDB(AppContext.getInstance()).findFirst(selector);
            if (queryupload!=null) {
                queryupload.setParam(upload.getParam());
                DbHelper.getDB(AppContext.getInstance()).update(upload, WhereBuilder.b("fileId", "=", upload.getFileId()), new String[]{"param"});
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getId() {
        int strid = 0;
        try {
            Cursor cursor = DbHelper.getDB(AppContext.getInstance()).execQuery("select max(id) from upload");
            if (cursor.moveToFirst()) {
                 strid = cursor.getInt(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return strid + 1;
    }

    public List<Upload> getFileList(String type) {
        List<Upload> list = new ArrayList<Upload>();
        try {
            list = DbHelper.getDB(AppContext.getInstance()).findAll(Upload.class, WhereBuilder.b("module", "=", type));
            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Upload getUpload(long fileid) {
        Selector selector = Selector.from(Upload.class);
        selector.where(WhereBuilder.b("fileId", "=", fileid));
        try {
            Upload queryupload = DbHelper.getDB(AppContext.getInstance()).findFirst(selector);
            return queryupload;
        } catch (DbException e) {
            return null;
        }
    }

}
