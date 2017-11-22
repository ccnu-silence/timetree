package com.yey.kindergaten.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.MyBaseAdapter;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.util.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

public class ServiceGridviewAdapter extends MyBaseAdapter<Photo>{
    private int mcount = 0;
    private Context context;
    private List<Photo> list = new ArrayList<Photo>();
    public ServiceGridviewAdapter(Context context, List<Photo> list) {
        super(context, list);
        this.context = context;
        this.list = list;
    }

//  @Override
//  public View bindView(int position, View convertView, ViewGroup parent) {
//      // TODO Auto-generated method stub
//      if (convertView == null) {
//          convertView = mInflater.inflate(R.layout.service_gridview_item, null);
//      }
//      ImageView iv = ViewHolder.get(convertView, R.id.service_gridviewitemiv);
//      if (position == 0 && mcount == 0) {
//          if (!getList().get(position).equals("last")) {
//              ImageLoader.getInstance().displayImage("file://" + getList().get(position), iv, ImageLoadOptions.getOptions());
//          } else {
//              iv.setImageDrawable(context.getResources().getDrawable(R.drawable.friendster_gridview));
//              mcount++;
//          }
//      } else if (position == 0 && mcount>=1) {
//          return convertView;
//      } else {
//          if (!getList().get(position).equals("last")) {
//              ImageLoader.getInstance().displayImage("file://" + getList().get(position), iv, ImageLoadOptions.getOptions());
//          } else {
//              iv.setImageDrawable(context.getResources().getDrawable(R.drawable.friendster_gridview));
//          }
//          mcount = 0;
//      }
//      if (!getList().get(position).equals("last")) {
//          ImageLoader.getInstance().displayImage("file://" + getList().get(position), iv, ImageLoadOptions.getOptions());
//      } else {
//          iv.setImageDrawable(context.getResources().getDrawable(R.drawable.friendster_gridview));
//      }
//
//      return convertView;
//  }

    static class Holer{
        ImageView  iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater ll = ((Activity)context).getLayoutInflater();
        Holer holer;
        if (convertView == null) {
            holer = new Holer();
            convertView = ll.inflate(R.layout.service_gridview_item, parent,false);
            holer.iv = (ImageView) convertView.findViewById(R.id.service_gridviewitemiv);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }
        String path = mList.get(position).imgPath;
        System.out.println("p" + parent.getChildCount());
        if (position == parent.getChildCount()) {
            if (path.equals("local") && (mList.size() - 1) == position) {
                holer.iv.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_add_photo));
            } else {
                ImageLoader.getInstance().displayImage("file:///" + path, holer.iv, ImageLoadOptions.getOptions());
            }
        } else {

        }
        return convertView;
    }

    public void setList( List<Photo> list){
        this.mList = list;
        mcount = 0;
        notifyDataSetChanged();
    }


}
