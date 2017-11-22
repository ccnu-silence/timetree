package com.yey.kindergaten.activity;


import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.MeInfoAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.KindergartenInfo;
import com.yey.kindergaten.bean.MeinfoItemBean;
import com.yey.kindergaten.bean.WLImage;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.fragment.ServiceCreateKinderFragment;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class JoinClassActivity extends BaseActivity implements View.OnClickListener {

    //导航栏控件
    @ViewInject(R.id.header_title)TextView titletv;
    @ViewInject(R.id.left_btn)ImageView left_iv;
    @ViewInject(R.id.right_btn)ImageView right_iv;
    @ViewInject(R.id.right_tv)TextView right_tv;

    //内部控件
    @ViewInject(R.id.show_kindergaten_name_tv)TextView kname_tv;
    @ViewInject(R.id.show_result_class_lv)ListView class_lv;
    @ViewInject(R.id.call_phone_rl)RelativeLayout call_rl;

    private AccountInfo mAccountInfo;
    private MyAdapter adapter;
    private List<Classe>list;
    private String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        ViewUtils.inject(this);

        mAccountInfo = AppServer.getInstance().getAccountInfo();
        state = getIntent().getStringExtra("state");
        initView();
        initData();
        initClick();
    }

    private void initData() {
        kname_tv.setText("幼儿园名称："+mAccountInfo.getKname());
        AppServer.getInstance().getClassList(mAccountInfo.getUid(), mAccountInfo.getKid(), 1, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if(code==AppServer.REQUEST_SUCCESS){
                    list= (List<Classe>) obj;
                    adapter = new MyAdapter(JoinClassActivity.this,list);
                    class_lv.setAdapter(adapter);
                }else{

                }
            }
        });
    }




    private void writeRealName(final Classe classe){

        final EditText et = new EditText(this);
        et.setMinHeight(80);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        et.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        et.setPadding(10, 5, 0, 0);
        et.setHint("请输入少于20个字符");
        et.setBackground(null);
        showLoadingDialog("正在为您跳转...");
        showDialogs("请填写您的真实姓名", et,false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if(et.getText().toString()==null||et.getText().toString().length()==0){
                    showToast("请填写您的真实姓名");
                    return;
                }
                mAccountInfo.setRealname(et.getText().toString());
                DbHelper.updateAccountInfo(mAccountInfo);

            }
        });
    }

    private void initClick() {
        call_rl.setOnClickListener(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
                showExitDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        titletv.setText("加入班级");
        right_iv.setOnClickListener(this);
        right_tv.setVisibility(View.VISIBLE);
        right_tv.setOnClickListener(this);
        right_tv.setText("跳过");
        if(state==null){
            call_rl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
          switch (view.getId()){
              case R.id.right_tv:
                  showExitDialog();
                  break;
              case R.id.call_phone_rl:
                    Intent phoneIntent = new Intent(
                    "android.intent.action.CALL", Uri.parse("tel:"  + "4006011063"));
                     startActivity(phoneIntent);
                  break;
          }
    }


    private void showExitDialog(){

        showDialog("提示", "如果您不是带班老师，或没有您所在的班级，请先跳过之后可以在【通讯录】中加入。", "跳过", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(JoinClassActivity.this, MainActivity.class);
                SharedPreferencesHelper.getInstance(JoinClassActivity.this).setInt(AppConstants.PREF_ISLOGIN, 1);
                JoinClassActivity.this.startActivity(intent);
                JoinClassActivity.this.finish();
            }
        });
    }

    class MyAdapter extends BaseAdapter{

        private Context context;
        private LayoutInflater mInflater;
        private List<Classe>list ;
        public  MyAdapter(Context context,List<Classe>list){
            this.context = context;
            mInflater = LayoutInflater.from(context);
            this.list=list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int positiom) {
            return positiom;
        }

        @Override
        public long getItemId(int positiom) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
             view =mInflater.inflate(R.layout.inflater_show_search_class_item,null);
             TextView tv = ViewHolder.get(view,R.id.show_class_inflater_tv);
             final Button   btn =ViewHolder.get(view,R.id.add_class_inflater_btn);
             final Classe  classe = list.get(position);
             tv.setText(classe.getCname());
             btn.setOnClickListener(new View.OnClickListener() {
               @Override
                 public void onClick(View view) {
                   showLoadingDialog("正在跳转...");
                   btn.setBackgroundResource(R.drawable.puaccheck);
                               AppServer.getInstance().joinClass(mAccountInfo.getUid(), classe.getCid(), mAccountInfo.getKid(), new OnAppRequestListener() {
                                   @Override
                                   public void onAppRequest(int code, String message, Object obj) {
                                       if (code == AppServer.REQUEST_SUCCESS) {

                                           AppServer.getInstance().getContacts(mAccountInfo.getUid(), new OnAppRequestListener() {
                                               @Override
                                               public void onAppRequest(int code, String message, Object obj) {
                                                   postEvent(AppEvent.PARENTFRAGMENT_RELOADDATA);
                                                   Contacts contacts = new Contacts();
                                                   List<Classe>classes = new ArrayList<Classe>();
                                                   classes.add(classe);
                                                   contacts.setClasses(classes);
                                                   AppContext.getInstance().setContacts(contacts);
                                                   try {
                                                       DbHelper.getDB(JoinClassActivity.this).saveAll(classes);
                                                   } catch (DbException e) {
                                                       e.printStackTrace();
                                                   }
                                                   cancelLoadingDialog();
                                                   Intent intent = new Intent(JoinClassActivity.this, MainActivity.class);
                                                   SharedPreferencesHelper.getInstance(JoinClassActivity.this).setInt(AppConstants.PREF_ISLOGIN, 1);
                                                   JoinClassActivity.this.startActivity(intent);
                                                   JoinClassActivity.this.finish();
                                               }
                                           });
                                       } else {
                                           Toast.makeText(JoinClassActivity.this, "加入失败", Toast.LENGTH_LONG).show();
                                       }
                                   }
                               });

                           }
             });

            return view;
        }
    }
    public void postEvent(final int type) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                System.out.println("PostThreadId:"
                        + Thread.currentThread().getId());

            }
        }).start();

    }
}
