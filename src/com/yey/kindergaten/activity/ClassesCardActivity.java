package com.yey.kindergaten.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.MeClassesCardAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;

import java.util.ArrayList;
import java.util.List;

/**
 * 班级二维码:老师身份进入我/我的班级
 * longhengdong
 */
public class ClassesCardActivity extends BaseActivity implements OnClickListener{
    @ViewInject(R.id.header_title)TextView titletv;
    @ViewInject(R.id.list_classes)ListView list_classes;
    @ViewInject(R.id.left_btn)ImageView iv_left;

    AccountInfo accountInfo;
    private MeClassesCardAdapter meClassesCardAdapter;
    List<Classe> classeslist = new ArrayList<Classe>();
    //private Contacts contants = null;

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
            }catch (DbException e){
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        iv_left.setVisibility(View.VISIBLE);
        titletv.setText("我的班级");
        meClassesCardAdapter = new MeClassesCardAdapter(this,classeslist);
        list_classes.setAdapter(meClassesCardAdapter);
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
