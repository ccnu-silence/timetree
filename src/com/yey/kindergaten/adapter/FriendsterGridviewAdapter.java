package com.yey.kindergaten.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.widget.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;

public class FriendsterGridviewAdapter extends BaseAdapter{
 
    String[] headvision;
    Context context;
    String type;
    DisplayImageOptions imageOptions;
    static List<CircleProgressBar> circlelist = new ArrayList<CircleProgressBar>();

    public FriendsterGridviewAdapter(String[] headvision, Context context, String type, DisplayImageOptions imageOptions) {
        this.headvision = headvision;
        this.context = context;
        this.type = type;
        this.imageOptions = imageOptions;
    }

    @Override
    public int getCount() {
        if (type.equals("friendster")) {
            if (headvision.length > 9) {
                return 9;
            } else {
                return headvision.length;
            }
        } else {
            return headvision.length;
        }
    }

    @Override
    public Object getItem(int position) {
        return headvision[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ServiceGridViewHold serviceGridViewHold;
        if (convertView == null) {
            serviceGridViewHold = new ServiceGridViewHold();
            if (type.equals("friendster")) {
                convertView = LayoutInflater.from(context).inflate(R.layout.service_gridview_item, null);
            } else if (type.equals("itemonclik")) {
                convertView = LayoutInflater.from(context).inflate(R.layout.service_gridview_itemclick, null);
            }
            assert convertView!=null;
            serviceGridViewHold.imageView = (ImageView) convertView.findViewById(R.id.service_gridviewitemiv);
            convertView.setTag(serviceGridViewHold);
        } else {
            serviceGridViewHold = (ServiceGridViewHold) convertView.getTag();
        }
        if (headvision.length > 0) {
            convertView.setVisibility(View.VISIBLE);
            if (type.equals("friendster")) {
                if (position < 9) {
                    if (headvision[position].length() > 4 && headvision[position].substring(0, 4).equals("http")) {
                        GlideUtils.loadFriendDataImage(context, headvision[position], serviceGridViewHold.imageView);
//                        ImageLoader.getInstance().displayImage(headvision[position], serviceGridViewHold.imageView, imageOptions);
                    } else {
                        GlideUtils.loadFriendDataImage(context, "file://" + headvision[position], serviceGridViewHold.imageView);
//                        ImageLoader.getInstance().displayImage("file://" + headvision[position], serviceGridViewHold.imageView, imageOptions);
                    }
                }
            } else if (type.equals("itemonclik")) {
                if (headvision[position].length() > 4 && headvision[position].substring(0, 4).equals("http")) {
                    GlideUtils.loadFriendDataImage(context, headvision[position], serviceGridViewHold.imageView);
//                    ImageLoader.getInstance().displayImage(headvision[position], serviceGridViewHold.imageView, imageOptions);
                } else {
                    GlideUtils.loadFriendDataImage(context, "file://" + headvision[position], serviceGridViewHold.imageView);
//                    ImageLoader.getInstance().displayImage("file://" + headvision[position], serviceGridViewHold.imageView, imageOptions);
                }
            }
        } else {
            convertView.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ServiceGridViewHold {
        ImageView imageView;
    }

}
