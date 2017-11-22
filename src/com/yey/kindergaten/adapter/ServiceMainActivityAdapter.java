package com.yey.kindergaten.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ServiceMainActivityAdapter extends BaseAdapter{
    private Context context;
    private List<Services> datalist = new ArrayList<Services>();
    private ArrayList<Integer> iconlist = new ArrayList<Integer>();
    public LayoutInflater mInflater;
    private TreeSet mSeparatorsSet = new TreeSet();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
  /*1任务
    2日程
    3报表
    4积分兑换
    5健康中心
    6成长档案
    7家园联系
    8园长信箱
    9幼儿园文档
    10微网站
    11班级主页
    12生活剪影
    13手工作品
    14班级相册
    15成长日记
    16我的文档
    18班级动态
    19离园播报
    20一日流程
    21借阅服务
    22微课堂
    23成长评估*/
    public ServiceMainActivityAdapter(Context context) {
        this.context = context;
        // addicon(this.datalist);
        mInflater = LayoutInflater.from(context);
    }
     
    public ServiceMainActivityAdapter(Context context, List<Services> list) {
        this.context = context;
        this.datalist = list;
        addicon(this.datalist);
        mInflater = LayoutInflater.from(context);
    }

    public void addicon(List<Services> serlist){
        if (serlist!=null && serlist.size() > 0) {
            for (int i = 0; i < serlist.size(); i++) {
                Services s = serlist.get(i);
                if (s.getType() == 0) {  // 朋友圈
                    iconlist.add(R.drawable.icon_service_main_friend);
                } else if (s.getType() == 1) { // 任务
                    iconlist.add(R.drawable.icon_service_main_task);
                } else if (s.getType() == 2) { // 日程
                    iconlist.add(R.drawable.icon_serivce_main_schedule);
                } else if (s.getType() == 3) { // 报表
                    iconlist.add(R.drawable.icon_service_main_report);
                } else if (s.getType() == 4) { // 积分兑换
                    iconlist.add(R.drawable.icon_service_main_jifen);
                } else if (s.getType() == 5) { // 健康中心
                    iconlist.add(R.drawable.icon_service_main_health);
                } else if (s.getType() == 6) { // 成长档案
                    iconlist.add(R.drawable.icon_service_main_growth);
                } else if (s.getType() == 7) { // 家园联系
                    iconlist.add(R.drawable.icon_service_main_growth);
                } else if (s.getType() == 8) { // 园长信箱
                    iconlist.add(R.drawable.icon_service_main_mail);
                } else if (s.getType() == 9) { // 文档中心
                    iconlist.add(R.drawable.icon_service_main_file);
                } else if (s.getType() == 10) { // 微网站
                    iconlist.add(R.drawable.icon_service_main_site);
                } else if (s.getType() == 11) { // 班级主页
                    iconlist.add(R.drawable.icon_service_main_classhome);
                } else if (s.getType() == 15) { // 成长日记
                    iconlist.add(R.drawable.icon_growthdairy);
                } else if (s.getType() == 14) { // 班级相册
                    iconlist.add(R.drawable.icon_service_main_classphoto);
                } else if (s.getType() == 16) { // 我的文档
                    iconlist.add(R.drawable.icon_service_main_myfile);
                } else if (s.getType() == 18) { // 班级动态
                    iconlist.add(R.drawable.icon_service_main_friendster);
                } else if (s.getType() == 19) { // 离园播报
                    iconlist.add(R.drawable.icon_service_main_leaveschool);
                } else if (s.getType() == 20) { // 一日流程
                    iconlist.add(R.drawable.icon_service_main_oneday);
                } else if (s.getType() == 21) { // 借阅服务
                    iconlist.add(R.drawable.icon_service_main_borrowbook);
                } else if (s.getType() == 22) { // 微课堂
                    iconlist.add(R.drawable.icon_service_main_microclassroom);
                } else if (s.getType() == 23) { // 成长评估
                    iconlist.add(R.drawable.icon_service_main_growthassessment);
                } else {
                    iconlist.add(R.drawable.icon_service_main_default);
                }
            }
        }
    }

    public void addData(List<Services> list){
        datalist.addAll(list);
        notifyDataSetChanged();
    }
  
    public void addSeparatorItem(Services item) {
        datalist.add(item);
        mSeparatorsSet.add(datalist.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    public List<Services> getData(){
        return datalist;
    }
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }
    @Override
    public int getCount() {
        return this.datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return this.datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItemViewType(position) == TYPE_SEPARATOR) {
            return false;
        }
        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewgroup) {
        int type = getItemViewType(position);
        TextView tv_serviceName = null;
        ImageView iv_serviceIcon = null;
        if (type == TYPE_ITEM) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_me_main_item, null);
            }
            tv_serviceName = ViewHolder.get(convertView, R.id.tv_activity_me_item);
            iv_serviceIcon = ViewHolder.get(convertView, R.id.iv_activity_me_item);
            View view = ViewHolder.get(convertView, R.id.item_view);
            tv_serviceName.setText(this.datalist.get(position).getName());
            if (getItemViewType(position + 1) == 1) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            int icontype = datalist.get(position).getType();
            switch (icontype) {
            case 0:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_friend));
                break;
            case 1:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_task));
                break;
            case 2:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_serivce_main_schedule));
                break;
            case 3:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_report));
                break;
            case 4:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_jifen));
                break;
            case 5:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_health));
                break;
            case 6:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_growth));
                break;
            case 7:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_homecontact));
                break;
            case 8:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_mail));
                break;
            case 9:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_file));
                break;
            case 10:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_site));
                break;
            case 11:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_classhome));
                break;
            case 15:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_growthdairy));
                break;
            case 14:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_classphoto));
                break;
            case 13:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.work_icon));
                break;
            case 12:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.life_icon));
                break;
            case 16:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_myfile));
                break;
            case 18:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_friendster));
                break;
            case 19:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_leaveschool));
                break;
            case 20:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_oneday));
                break;
            case 21:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_borrowbook));
                break;
            case 22:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_microclassroom));
                break;
            case 23:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_growthassessment));
                break;
            default:
                iv_serviceIcon.setImageDrawable(this.context.getResources().getDrawable(R.drawable.icon_service_main_default));
                break;
            }
        } else {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_service_cut_show, null);
            }
            View view = ViewHolder.get(convertView, R.id.cutitem);
            View topview = ViewHolder.get(convertView, R.id.cuttopitem);
            View titleview = ViewHolder.get(convertView, R.id.cut_title);
            if (position == 0) {
                topview.setVisibility(View.GONE);
                titleview.setVisibility(View.GONE);
            } else {
                topview.setVisibility(View.VISIBLE);
                titleview.setVisibility(View.VISIBLE);
            }
            if (position + 1 == this.getCount()) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

}
