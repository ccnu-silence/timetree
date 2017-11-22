package com.yey.kindergaten.adapter.leaveschool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.LeaveSchoolBean;
import com.yey.kindergaten.util.ImageLoadOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zy on 2015/7/21.
 */
public class LeaveSchoolDetailAdapter extends BaseAdapter{

    private List<LeaveSchoolBean> mList;
    private Context context;
    private LayoutInflater mInfalter;
    private String type; // 表示来自未离园还是离园

    public LeaveSchoolDetailAdapter(Context context) {
        this.context = context;
        mInfalter = LayoutInflater.from(context);
    }

    public void setmList(List<LeaveSchoolBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setType(String type){
        this.type = type;
    }

    @Override
    public Object getItem(int i) {
        return  mList == null ? 0 : mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
//      View convertView = null;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInfalter.inflate(R.layout.school_detail_item, null);
            holder.head = (ImageView) convertView.findViewById(R.id.iv_head);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.cname = (TextView) convertView.findViewById(R.id.tv_cname_tv);
            holder.hourtime = (TextView) convertView.findViewById(R.id.tv_hour_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LeaveSchoolBean bean = mList.get(position);
        if (bean!=null) {
            ImageLoader.getInstance().displayImage(bean.getAvatar(), holder.head, ImageLoadOptions.getHeadOptions());
            holder.name.setText(bean.getNick() + "");
            if (type!=null && type.equals("fromLeave")) { // 已离园状态
                holder.cname.setText(bean.getCname() + "");
                try {
                    holder.hourtime.setText(parseTime(bean.getDate()) + "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (type!=null && type.equals("fromLeft")) {
                holder.cname.setVisibility(View.GONE);
                holder.hourtime.setVisibility(View.GONE);
            }
//          if (position == mList.size() - 1 || position == mList.size() - 2) {
//              holder.name.setTextColor(context.getResources().getColor(R.color.red_500));
//              holder.cname.setTextColor(context.getResources().getColor(R.color.red_500));
//              holder.hourtime.setTextColor(context.getResources().getColor(R.color.red_500));
//          } else {
                holder.name.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
                holder.cname.setTextColor(context.getResources().getColor(R.color.light_grey_v2));
                holder.hourtime.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
//          }
        }
        return convertView;
    }

    // 约定系统时间格式转化成需要显示的格式
    private String parseTime(String date) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date == null || date.length() == 0) {return "";}
        Date time = format.parse(date);
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
        return format1.format(time);
    }

    class ViewHolder{
        ImageView head ;
        TextView name;
        TextView cname;
        TextView hourtime;
    }

}
