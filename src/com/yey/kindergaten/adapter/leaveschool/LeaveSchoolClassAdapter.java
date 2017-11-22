package com.yey.kindergaten.adapter.leaveschool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yey.kindergaten.R;

import java.util.List;

/**
 * Created by zy on 2015/7/21.
 */
public class LeaveSchoolClassAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Context context;
    private List<LeaveSchoolClassBean> mList;

    public LeaveSchoolClassAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setmList(List<LeaveSchoolClassBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList == null ? 0 : mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View convertView = null;
        ViewHolder holder = null;

        if (convertView == null) {
           convertView = mInflater.inflate(R.layout.leaveschool_class_item,null);
           holder = new ViewHolder();
           holder.leaveTv = (TextView) convertView.findViewById(R.id.has_leaved_child_count_tv);
           holder.nameTv = (TextView) convertView.findViewById(R.id.leave_school_cname_tv);
           holder.leftTv = (TextView) convertView.findViewById(R.id.has_not_leaved_child_count_tv);
           convertView.setTag(holder);
        } else {
           holder = (ViewHolder) convertView.getTag();
        }
        LeaveSchoolClassBean bean = mList.get(i);
        if (bean!=null) {
            holder.nameTv.setText(bean.getCname() + "");
            holder.leaveTv.setText(bean.getHasLeavedCount() + "");
            holder.leftTv.setText(bean.getNoLeavedCount() + "");
        }
        return convertView;
    }

    class ViewHolder {
        private TextView nameTv;
        private TextView leaveTv;
        private TextView leftTv;
    }

}
