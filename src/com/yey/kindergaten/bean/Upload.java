package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.Map;

/**
 * 上传文件模块
 * 
 * @author chaowen
 * 
 */
@Table(name="upload")
public class Upload extends EntityBase implements Serializable{

	@Column(column="fileId")
	private long fileId;	        // 文件标识与服务器的文件一样
    @Column(column="uploadfilepath")
	private String uploadfilepath;  // 上传文件路径
    @Column(column="uploadSize")
    private long uploadSize;        // 已上传文件大小
    @Column(column="module")
    private String module;          // 上传的图片对应的模块:
    @Column(column="compress")
    private String compress;        // 压缩
    @Column(column="param")
    private String param;           // 自定义的提交参数
    @Column(column="sourcepath")
    private String sourcepath;      // 原图

    public int position;
    private Map<String ,String>map;

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getUploadfilepath() {
		return uploadfilepath;
	}

	public void setUploadfilepath(String uploadfilepath) {
		this.uploadfilepath = uploadfilepath;
	}

	public long getUploadSize() {
		return uploadSize;
	}

	public void setUploadSize(long uploadSize) {
		this.uploadSize = uploadSize;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCompress() {
		return compress;
	}

	public void setCompress(String compress) {
		this.compress = compress;
	}

	public String getSourcepath() {
		return sourcepath;
	}

	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
