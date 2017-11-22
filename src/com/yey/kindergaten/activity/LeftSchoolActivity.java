package com.yey.kindergaten.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.adapter.leaveschool.LeaveSchoolClassBean;
import com.yey.kindergaten.bean.LeaveSchoolBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2015/7/23.
 * 未离园详情
 */
public class LeftSchoolActivity extends BaseActivity{


    private TextView titleTv;
    private ImageView left_iv;

    //显示小朋友列表的listview
    private GridView mChildListListView;
    //暂无小朋友离园时显示界面
    private LinearLayout nochild_ll;

    //小朋友离园明细数据
    private List<LeaveSchoolBean> mList = new ArrayList<LeaveSchoolBean>();
    private MyGirdViewadapter mDetailAdapter;//小朋友明细
    private LeaveSchoolClassBean classe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_school_activity);

        classe = (LeaveSchoolClassBean) getIntent().getSerializableExtra("class");
        initView();
        showDatas();
    }

    private void initView() {

        mChildListListView = (GridView) findViewById(R.id.leave_school_child_detail_gv);
        if (mList !=null && mList.size()!=0) {
            mDetailAdapter = new MyGirdViewadapter(this, mList);
            mChildListListView.setAdapter(mDetailAdapter);
        }
        nochild_ll = (LinearLayout) findViewById(R.id.show_no_child_leave_ll);

        titleTv = (TextView) findViewById(R.id.header_title);
        left_iv = (ImageView) findViewById(R.id.left_btn);
        left_iv.setVisibility(View.VISIBLE);
        left_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (classe!=null) {
            titleTv.setText(classe.getCname() +"  离园状态  ");
        }
    }

    private void showDatas() {

        if (classe!=null) {
            try {
                //查询未离园小朋友列表
                mList =  DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(LeaveSchoolBean.class)
                                                              .where("isLeave", "=",0).and("cid","=",classe.getCid()));

                if (mList == null || mList.size() == 0) {
                    nochild_ll.setVisibility(View.VISIBLE);
                } else {
                    if (mDetailAdapter == null) {
                        mDetailAdapter = new MyGirdViewadapter(this, mList);
                        mChildListListView.setAdapter(mDetailAdapter);
                        mDetailAdapter.setmList(mList);
                    } else {
                        mDetailAdapter.setmList(mList);
                    }
                    nochild_ll.setVisibility(View.GONE);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
    }

    class MyGirdViewadapter extends BaseAdapter {
        private Context context;
        private List<LeaveSchoolBean> list = new ArrayList<LeaveSchoolBean>();

        public MyGirdViewadapter (Context context, List<LeaveSchoolBean> mList) {
            this.context = context;
            this.list = mList;
        }

        public void setmList(List<LeaveSchoolBean> mList) {
            this.list = mList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.leaveschool_detail_item, null);
            }
            CircleImageView user_head = ViewHolder.get(convertView, R.id.iv_head_img);
            TextView user_name = ViewHolder.get(convertView, R.id.tv_user_name);

            LeaveSchoolBean bean = list.get(position);

            if (bean!=null) {
                ImageLoader.getInstance().displayImage(bean.getAvatar(), user_head, ImageLoadOptions.getHeadOptions());
                user_name.setText(bean.getNick() + "");

//                if(position==mList.size()-1||position==mList.size()-2){
//                    holder.name.setTextColor(context.getResources().getColor(R.color.red_500));
//                    holder.cname.setTextColor(context.getResources().getColor(R.color.red_500));
//                    holder.hourtime.setTextColor(context.getResources().getColor(R.color.red_500));
//                }else {
//                    holder.name.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
//                    holder.cname.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
//                    holder.hourtime.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
//                }

            }
            return convertView;
        }

    }



}
