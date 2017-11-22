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
import com.yey.kindergaten.bean.TwitterSelf.CommentsSelf;
import com.yey.kindergaten.util.FaceTextUtils;

public class FriendsterActivityItemAdapterSelf extends BaseAdapter{
    private CommentsSelf[] list ;
    private Context context;
    public FriendsterActivityItemAdapterSelf(Context context, CommentsSelf[] list) {
        this.context = context;
        this.list = list;
    }

    class Holder {
        TextView tv_name;
        TextView tv_content;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater ll=((Activity)context).getLayoutInflater();
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = ll.inflate(R.layout.activity_service_friendster_item_item, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_activity_service_friendster_item_item_name);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_activity_service_friendster_item_item_content);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        SpannableString spannableString = FaceTextUtils.toSpannableString(this.context, list[position].getContent());
        holder.tv_content.setText(spannableString);
        holder.tv_name.setText(list[position].getCmtername() + "说：");
        return convertView;
    }

    public void setlist(CommentsSelf[] comments) {
        this.list = comments;
        notifyDataSetChanged();
    }

}
