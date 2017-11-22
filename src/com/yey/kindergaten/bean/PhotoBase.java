package com.yey.kindergaten.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * JavaBean的基类。<br/>
 * <br/>
 * Created by yanglw on 2014/8/15.
 */
public class PhotoBase implements Parcelable {

    public String id;
    public String text;
    public String imgPath;
    public int dataColumnIndex;

    /** 下面是固定写法 */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(imgPath);
        dest.writeInt(dataColumnIndex);
    }

    public static <T extends PhotoBase> T readFromParcel(Parcel in, T t) {
        if (t == null) {
            return null;
        }

        if (in == null) {
            return null;
        }

        t.id = in.readString();
        t.text = in.readString();
        t.imgPath = in.readString();
        t.dataColumnIndex = in.readInt();
        return t;
    }

    public static final Parcelable.Creator<PhotoBase> CREATOR = new Parcelable.Creator<PhotoBase>() {

        public PhotoBase createFromParcel(Parcel in) {
            return readFromParcel(in, new PhotoBase());
        }

        public PhotoBase[] newArray(int size) {
                return new PhotoBase[size];
        }

    };

}
