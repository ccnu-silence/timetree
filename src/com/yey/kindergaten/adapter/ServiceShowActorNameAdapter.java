package com.yey.kindergaten.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yey.kindergaten.R;

import java.util.ArrayList;

public class ServiceShowActorNameAdapter extends BaseAdapter{

	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<String>list;
	private ArrayList<String>list2 = new ArrayList<String>();

    public ServiceShowActorNameAdapter(Context context,ArrayList<String>list) {
        this.context = context;
        if (list!=null || list.size()!=0) {
            if (list.size() > 3) {
                list2.add(list.get(0));
                list2.add(list.get(1));
                list2.add(list.get(2));
                this.list=list2;
            } else {
                this.list = list;
            }
        }
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.inflater_sendmsg_showname, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.id_inflater_sendmsg_showname_tv);
        TextView name0 = (TextView) convertView.findViewById(R.id.id_inflater_sendmsg_showname_tv0);
        TextView name2 = (TextView) convertView.findViewById(R.id.id_inflater_sendmsg_showname_tv2);
        name0.setTextColor(context.getResources().getColor(R.color.service_write_schedule_show_tv));
        name0.setTextSize(17);
        name.setTextColor(context.getResources().getColor(R.color.service_write_schedule_show_tv));
        name.setTextSize(17);
        name2.setTextColor(context.getResources().getColor(R.color.service_write_schedule_show_tv));
        name2.setTextSize(17);
        int n = list.size();
        if (n == 0) {
            name2.setText("请选择参与者");
            name0.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
        } else if (n == 1) {
            name2.setText(list.get(0));
            name0.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
        } else if (n == 2) {
            name2.setText(list.get(0));
            name.setText(list.get(1) + " ,");
            name0.setVisibility(View.GONE);
        } else {
            name2.setText(list.get(0));
            name.setText(list.get(1) + " ,");
            name0.setText(list.get(2) + " ,");
        }
        if (position == 0) {
            if (listener!=null) {
               listener.selector(convertView);
            }
        }
//      name2.setText(list.get(position));
        return convertView;
    }

    public interface ChangeItemView {
        public void selector(View view);
    }

    ChangeItemView listener;
    public ChangeItemView getListener() {
        return listener;
    }
    public void setListener(ChangeItemView listener) {
        this.listener = listener;
    }

}
