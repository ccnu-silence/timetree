package com.yey.kindergaten.activity;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.UtilsLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 班级二维码:老师身份进入我/我的班级
 * longhengdong
 */
public class ClassesInviteActivity extends BaseActivity implements OnClickListener{
    @ViewInject(R.id.header_title)TextView titletv;
    @ViewInject(R.id.list_classes)ListView list_classes;
    @ViewInject(R.id.left_btn)ImageView iv_left;

    AccountInfo accountInfo;
    private ClassesInveteAdapter classesInveteAdapter;
    List<Classe> classeslist = new ArrayList<Classe>();
    //private Contacts contants = null;
    private final static String TAG = "ClassesInviteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_classes_card);
        ViewUtils.inject(this);
        accountInfo = AppServer.getInstance().getAccountInfo();
        initData();
        initView();
    }

    private void initData() {
        Contacts contants = AppContext.getInstance().getContacts();
        classeslist = contants.getClasses();
        if (classeslist == null || classeslist.size() == 0) {
            try {
                List<Classe> classes = DbHelper.getDB(this).findAll(Classe.class);
                if (classes == null) {
                    classes = new ArrayList<Classe>();
                }
                contants.setClasses(classes);
                if (classeslist!=null){
                    classeslist.clear();
                }
                classeslist.addAll(classes);
                AppContext.getInstance().setContacts(contants);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        iv_left.setVisibility(View.VISIBLE);
        titletv.setText("邀请家长加入");
        classesInveteAdapter = new ClassesInveteAdapter(this, classeslist);
        list_classes.setAdapter(classesInveteAdapter);
    }

    class ClassesInveteAdapter extends BaseAdapter{

        Context context;
        private int  clickposition;
        List<Classe> list;

        public ClassesInveteAdapter(Context context, List<Classe> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public int getPosition(){
            return clickposition;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            clickposition = position;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_me_main1_item,null);
            }
            ImageView head_iv = ViewHolder.get(convertView, R.id.iv_activity_me_item);
            TextView class_name = ViewHolder.get(convertView, R.id.tv_activity_me_item);
            TextView name = ViewHolder.get(convertView, R.id.tv_activity_me_item_second);
            ImageView row_img = ViewHolder.get(convertView, R.id.row_img_02);
            RelativeLayout class_item = ViewHolder.get(convertView, R.id.class_item);
            View item_view = ViewHolder.get(convertView, R.id.item_view);
            View item_longline = ViewHolder.get(convertView, R.id.item_longline);

            if (position == getCount() - 1) {
                item_longline.setVisibility(View.VISIBLE);
                item_view.setVisibility(View.GONE);
            } else {
                item_longline.setVisibility(View.GONE);
                item_view.setVisibility(View.VISIBLE);
            }

            head_iv.setImageResource(R.drawable.me_main_myclass);
            row_img.setVisibility(View.GONE);
            name.setText("邀请家长");

            //View view=convertView.findViewById(R.id.id_item_view_line);
            String className = "";
            //int classId = 0;
            if (list!=null && list.size()!=0) {
                className = list.get(position).getCname();
                //classId = list.get(position).getCid();
                if (className!=null && !className.equals("")) {
                    UtilsLog.i(TAG, "set className,className is :" + className + "");
                    class_name.setText(className);
                } else {
                    UtilsLog.i(TAG, "set className fail,className is :" + className + "");
                    class_name.setText("");
                }
            } else {
                UtilsLog.i(TAG, "set className fail,list is null or null value");
            }

            class_item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list!=null && list.size()!=0) {
                        int classId = list.get(position).getCid();
                        if (classId!=0) {
                            UtilsLog.i(TAG, "start invete parent,classid is :" + classId + "");
                            String url = AppUtils.replaceUrlByUrl(AppConstants.TEACHER_INVITE_PARENT, classId);
                            AppUtils.startWebUrlForGuide(ClassesInviteActivity.this, url);
                        } else {
                            UtilsLog.i(TAG, "start invete parent fail,classid is :" + classId +"");
                        }
                    }
                }
            });
            return convertView;
        }
    }

    @OnClick({(R.id.left_btn)})
    public void onclick(View v){
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            default:
                break;
        }
    }

     public void onResume() {
         super.onResume();
     }

     public void onPause() {
         super.onPause();
     }

     @Override
     public void onClick(View arg0) {
         switch (arg0.getId()) {
         case R.id.down_to_phone_tv:
             break;
         }
     }

} 
