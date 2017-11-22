package com.yey.kindergaten.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by zy on 2015/2/12.
 */
@Table(name="relationship")
public class RelationShipBean extends EntityBase{

    @Column(column="defaultrelation")
    private int defaultrelation;
    @Column(column="relationship")
    private int relationship;
    @Column(column = "hxregtag")
    private int hxregtag;

    public int getHxregtag() {
        return hxregtag;
    }

    public void setHxregtag(int hxregtag) {
        this.hxregtag = hxregtag;
    }

    public int getDefaultrelation() {
        return defaultrelation;
    }

    public void setDefaultrelation(int defaultrelation) {
        this.defaultrelation = defaultrelation;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }
}
