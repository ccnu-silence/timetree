package com.yey.kindergaten.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 本地端显示网页照片类
 * Created by longhengdong on 2015/9/22.
 */
public class PhotoShow implements Parcelable {

    public String url;     // 小图绝对路径的url
    public String desc;    // 图片描述。当无描述时请留空字符。
    public int source;       // 图片来源。0:我们自己的服务器。1:又拍云

    /** 下面是固定写法 */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(desc);
        dest.writeInt(source);
    }

    public static <T extends PhotoShow> T readFromParcel(Parcel in, T t) {
        if (t == null) {
            return null;
        }

        if (in == null) {
            return null;
        }

        t.url = in.readString();
        t.desc = in.readString();
        t.source = in.readInt();
        return t;
    }

    public static final Parcelable.Creator<PhotoShow> CREATOR = new Parcelable.Creator<PhotoShow>() {

        public PhotoShow createFromParcel(Parcel in) {
            return readFromParcel(in, new PhotoShow());
        }

        public PhotoShow[] newArray(int size) {
            return new PhotoShow[size];
        }

    };

}
