package com.yey.kindergaten.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.GroupInfoBean;

import java.util.ArrayList;
import java.util.List;

public class ServiceGroupAdapter extends BaseAdapter{
    private List<GroupInfoBean> grouplist = new ArrayList<GroupInfoBean>();
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private int gnum;
    private ArrayList<Album> classlist = new ArrayList<Album>();

    public ServiceGroupAdapter(Context context, ArrayList<Album> classlist) {
        this.context = context;
        this.classlist = classlist;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.classlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return this.classlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater ll = ((Activity)context).getLayoutInflater();
        final Holer holer;
        if (convertView == null) {
            holer = new Holer();
            convertView = ll.inflate(R.layout.service_share_item, null);
            holer.tv_name = (TextView) convertView.findViewById(R.id.tv_share);
//          holer.chb_group = (CheckBox) convertView.findViewById(R.id.chb_share);
            convertView.setTag(holer);
        } else {
            holer = (Holer) convertView.getTag();
        }
        holer.tv_name.setText(classlist.get(position).getAlbumName());

//      final HashMap<GroupInfoBean, Boolean> group = this.groupMapList.get(position);
//      Set<GroupInfoBean> set = group.keySet();
//      Iterator<GroupInfoBean> iterator = set.iterator();
//      while (iterator.hasNext()) {
//          GroupInfoBean bean = iterator.next();
//          holer.tv_name.setText(bean.getGname());
//          holer.chb_group.setChecked(group.get(bean));
//      }
//      holer.chb_group.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View v) {
//              Set<GroupInfoBean> set = group.keySet();
//              Iterator<GroupInfoBean> iterator = set.iterator();
//              while (iterator.hasNext()) {
//                  GroupInfoBean bean = iterator.next();
//                  group.put(bean, !group.get(bean));
//                  holer.chb_group.setChecked(!group.get(bean));
//              }
//              groupMapList.set(position, group);
//              setList(groupMapList);
//          }
//      });
        return convertView;
    }

    class Holer{
        TextView tv_name;
    }

    public void setList(ArrayList<Album> classlist) {
        this.classlist = classlist;
        notifyDataSetChanged();
    }

}
