package com.yey.kindergaten.activity.classvideo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bokecc.sdk.mobile.upload.VideoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSet {
	private final static String DOWNLOADINFO = "downloadinfo";
	private final static String UPLOADINFO = "uploadinfo";
	
	private static Map<String, UploadInfo> uploadInfoMap;

	private static SQLiteOpenHelper sqLiteOpenHelper;
	
	public static void init(Context context){
		sqLiteOpenHelper = new SQLiteOpenHelper(context, "demo", null, 1) {
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {
				String sql = "CREATE TABLE IF NOT EXISTS downloadinfo(" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"videoId VERCHAR, " +
						"title VERCHAR, " +
						"progress INTEGER, " +
						"progressText VERCHAR, " +
						"status INTEGER, " +
						"createTime DATETIME, " +
						"definition INTEGER)";
				
				String videoSql = "CREATE TABLE IF NOT EXISTS uploadinfo(" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"uploadId VERCHAR, status INTEGER, progress INTEGER, progressText VERCHAR, " +
						"videoId VERCHAR, title VERCHAR, tags VERCHAR, description VERCHAR, " +
						"filePath VERCHAR, fileName VERCHAR, fileByteSize VERCHAR, md5 VERCHAR, " +
						"uploadServer VERCHAR, serviceType VERCHAR, priority VERCHAR, encodeType VERCHAR, " +
						"uploadOrResume VERCHAR, createTime DATETIME)";
			
				db.execSQL(sql);
				db.execSQL(videoSql);
				
			}
		};
		
		uploadInfoMap = new HashMap<String, UploadInfo>();
		reloadData();
	}
	
	public static List<UploadInfo> getUploadInfos(){
		
		return new ArrayList<UploadInfo>(uploadInfoMap.values());
	}
	
	private static void reloadData(){
		
		SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
		Cursor cursor = null; 
		try {
			// 重载上传信息
			synchronized(uploadInfoMap){
				cursor = db.rawQuery("SELECT * FROM " + UPLOADINFO, null);
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					
					UploadInfo uploadInfo = buildUploadInfo(cursor);
					uploadInfoMap.put(uploadInfo.getUploadId(), uploadInfo);
				}
			}

		} catch (Exception e) {
			Log.e("cursor error", e.getMessage());
		} finally{
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public static UploadInfo getUploadInfo(String uploadId){
		
		return uploadInfoMap.get(uploadId);
	}
	
	public static void addUploadInfo(UploadInfo uploadInfo){
		synchronized (uploadInfoMap) {
			
			if (uploadInfoMap.containsKey(uploadInfo.getUploadId())) {
				return;
			}
			
			uploadInfoMap.put(uploadInfo.getUploadId(), uploadInfo);
		}
	}
	
	public static void updateUploadInfo(UploadInfo uploadInfo){
		synchronized (uploadInfoMap) {
			uploadInfoMap.put(uploadInfo.getUploadId(), uploadInfo);
		}
	}
	
	public static void removeUploadInfo(String uploadId){
		synchronized(uploadInfoMap){
			uploadInfoMap.remove(uploadId);
		}
	}
	
	public static void saveData(){
		SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
		db.beginTransaction();
		
		try {
			//清除当前数据
			db.delete(UPLOADINFO, null, null);
			for (UploadInfo uploadInfo : uploadInfoMap.values()) {
				
				ContentValues values = new ContentValues();
				values.put("uploadId", uploadInfo.getUploadId());
				values.put("status", uploadInfo.getStatus());
				values.put("progress", uploadInfo.getProgress());
				values.put("progressText", uploadInfo.getProgressText());
				
				VideoInfo videoInfo = uploadInfo.getVideoInfo();
				//video
				values.put("videoId", videoInfo.getVideoId());
				values.put("title", videoInfo.getTitle());
				values.put("tags", videoInfo.getDescription());
				values.put("description", videoInfo.getDescription());
				values.put("filePath", videoInfo.getFilePath());
				values.put("fileName", videoInfo.getFileName());
				values.put("fileByteSize", videoInfo.getFileByteSize());
				values.put("md5", videoInfo.getMd5());
				values.put("uploadServer", videoInfo.getServer());
				values.put("serviceType", videoInfo.getServicetype());
				values.put("priority", videoInfo.getPriority());
				values.put("encodeType", videoInfo.getEncodetype());
				values.put("uploadOrResume", videoInfo.getUploadOrResume());
				values.put("createTime", videoInfo.getCreationTime());
				
				db.insert(UPLOADINFO, null, values);
			}
			
			//清除当前数据
			db.delete(DOWNLOADINFO, null, null);

			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("db error", e.getMessage());
		} finally {
			db.endTransaction();
		}
		
		db.close();
	}
	
	private static UploadInfo buildUploadInfo(Cursor cursor) {
		String uploadId = cursor.getString(cursor.getColumnIndex("uploadId"));
		int status = cursor.getInt(cursor.getColumnIndex("status"));
		int progress = cursor.getInt(cursor.getColumnIndex("progress"));
		String progressText = cursor.getString(cursor.getColumnIndex("progressText"));
		
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.setVideoId(cursor.getString(cursor.getColumnIndex("videoId")));
		videoInfo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		videoInfo.setTags(cursor.getString(cursor.getColumnIndex("tags")));
		videoInfo.setDescription(cursor.getString(cursor.getColumnIndex("description")));
		videoInfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
		videoInfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
		videoInfo.setFileByteSize(cursor.getString(cursor.getColumnIndex("fileByteSize")));
		videoInfo.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
		videoInfo.setServer(cursor.getString(cursor.getColumnIndex("uploadServer")));
		videoInfo.setServicetype(cursor.getString(cursor.getColumnIndex("serviceType")));
		videoInfo.setPriority(cursor.getString(cursor.getColumnIndex("priority")));
		videoInfo.setEncodetype(cursor.getString(cursor.getColumnIndex("encodeType")));
		videoInfo.setUploadOrResume(cursor.getString(cursor.getColumnIndex("uploadOrResume")));
		videoInfo.setCreationTime(cursor.getString(cursor.getColumnIndex("createTime")));
		
		return new UploadInfo(uploadId, videoInfo, status, progress, progressText);
	}


}
