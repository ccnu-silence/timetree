package com.yey.kindergaten.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.GroupTwritte.comments;
import com.yey.kindergaten.util.FaceTextUtils;

public class FriendsterActivityItemAdapter extends BaseAdapter{

    private comments[] list ;
    private Context context;

    public FriendsterActivityItemAdapter(Context context, comments[] list) {
        this.context = context;
        this.list = list;
    }

    class Holder {
        TextView tv_name;
        TextView tv_content;
        TextView tv_anothername;
        TextView tv_repeat;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater ll = ((Activity)context).getLayoutInflater();
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = ll.inflate(R.layout.activity_service_friendster_item_item, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_activity_service_friendster_item_item_name);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_activity_service_friendster_item_item_content);
            holder.tv_anothername = (TextView) convertView.findViewById(R.id.tv_activity_service_friendster_item_item_another);
            holder.tv_repeat = (TextView) convertView.findViewById(R.id.tv_activity_service_friendster_repeat);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        SpannableString spannableString = FaceTextUtils.toSpannableString(this.context, list[position].getContent());
        holder.tv_content.setText(spannableString);
        if (list[position].getTorealname()!=null && !list[position].getTorealname().equals("")) {
            if (list[position].getTouid()!=-1) {
                holder.tv_anothername.setVisibility(View.VISIBLE);
                holder.tv_repeat.setVisibility(View.VISIBLE);
                holder.tv_repeat.setText(context.getResources().getString(R.string.friendster_repeat));
                holder.tv_anothername.setText(list[position].getTorealname() + "：");
                holder.tv_name.setText(list[position].getRealname());
            } else {
                holder.tv_anothername.setVisibility(View.GONE);
                holder.tv_repeat.setVisibility(View.GONE);
                holder.tv_name.setText(list[position].getRealname() + "：" );
            }
        } else {
            holder.tv_anothername.setVisibility(View.GONE);
            holder.tv_repeat.setVisibility(View.GONE);
            holder.tv_name.setText(list[position].getRealname() + "：");
        }

        return convertView;
    }

    public void setlist(comments[] comments) {
        this.list = comments;
        notifyDataSetChanged();
    }

}
