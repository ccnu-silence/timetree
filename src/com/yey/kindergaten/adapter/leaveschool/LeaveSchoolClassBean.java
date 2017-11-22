package com.yey.kindergaten.adapter.leaveschool;

import java.io.Serializable;

/**
 * Created by zy on 2015/7/21.
 */
public class LeaveSchoolClassBean implements Serializable{

    private int cid;

    private String cname;

    private int hasLeavedCount; //已经离园人数

    private int noLeavedCount; //还未离园人数



    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public int getHasLeavedCount() {
        return hasLeavedCount;
    }

    public void setHasLeavedCount(int hasLeavedCount) {
        this.hasLeavedCount = hasLeavedCount;
    }

    public int getNoLeavedCount() {
        return noLeavedCount;
    }

    public void setNoLeavedCount(int noLeavedCount) {
        this.noLeavedCount = noLeavedCount;
    }
}
