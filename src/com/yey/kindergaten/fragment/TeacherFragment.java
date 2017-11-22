package com.yey.kindergaten.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.ContactFriendDatacardActivity;
import com.yey.kindergaten.activity.MeInfoActivity;
import com.yey.kindergaten.activity.ParentActivity;
import com.yey.kindergaten.activity.ServiceAddKinderActivity;
import com.yey.kindergaten.activity.TeacherActivity;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Items;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.BirthdayOnclickback;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.MyListViewWithScrollView;
import com.yey.kindergaten.widget.PhotoDialog;
import com.yey.kindergaten.widget.PinnedSectionListView;
import com.yey.kindergaten.widget.PullToRefreshHeaderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 园长身份的家长界面
 *
 * Created by zy on 2015/1/21.
 *
 */
public class TeacherFragment extends FragmentBase implements OnItemClickListener, OnClickListener, BirthdayOnclickback, PullToRefreshHeaderView.OnHeaderRefreshListener {

    MyListViewWithScrollView listview;
    ServiceAdapter contactPuacAdapetr;
    List<Items> teacherlist = new ArrayList<Items>();
    List<Items> datalist = new ArrayList<Items>();
    AppContext appcontext = null;
    AccountInfo accountInfo;
    Contacts contacts;
    PinnedSectionListView teacher_list; // listView
    private String TAG = "TeacherFragment";

//    @ViewInject(R.id.item1_publicnumber)LinearLayout item1_publicnumber; // 公众号
//    @ViewInject(R.id.item2_classlist)LinearLayout item2_classlist; // 班级列表
//    @ViewInject(R.id.item3_teacher)LinearLayout item3_teacher; // 幼儿园老师
//    @ViewInject(R.id.item4_joininto)LinearLayout item4_joininto; // 邀请加入

    @ViewInject(R.id.header_title)TextView header_title;
    @ViewInject(R.id.right_tv) TextView right_tv;
    @ViewInject(R.id.item_titletv)TextView title_tv;
    @ViewInject(R.id.id_load_guade_url)Button loadUrl_btn; // 玩转时光树的 "下一步"
    @ViewInject(R.id.no_teacher_join_btn)Button noteacherBtn; // 邀请老师按钮
    @ViewInject(R.id.no_parent_join_btn)Button noparentBtn; // 邀请老师按钮
    @ViewInject(R.id.shou_guide_image)ImageView guide_iv;
    @ViewInject(R.id.teacher_framelayout)FrameLayout teacher_framelayout;
    @ViewInject(R.id.pull_torefresh_contact)PullToRefreshHeaderView mPullToRefreshView;
    @ViewInject(R.id.has_kid_no_teacher_fl)RelativeLayout noteacherRl;
    @ViewInject(R.id.has_kid_no_parent_fl)RelativeLayout noparentRl;

    @ViewInject(R.id.no_data_layout)LinearLayout no_data_layout;                //
    @ViewInject(R.id.public_account_ll)LinearLayout public_account_ll;          // 公众号
    @ViewInject(R.id.kindergaten_teacher_ll)LinearLayout kindergaten_teacher_ll;// 幼儿园老师
    @ViewInject(R.id.classes_list_ll)LinearLayout classes_list_ll;              // 班级列表
    @ViewInject(R.id.invite_teacher_ll)LinearLayout invite_teacher_ll;          // 邀请老师
    @ViewInject(R.id.item_view_pub)View item_view_pub;                          // 公众号line
    @ViewInject(R.id.item_view_class)View item_view_class;                      // 班级列表line

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        UtilsLog.i(TAG, "intp onCreateView");
//      View view = inflater.inflate(R.layout.fragement_contacts, null);
        View view = inflater.inflate(R.layout.fragement_contacts, container, false);
        ViewUtils.inject(this, view);
        accountInfo = AppServer.getInstance().getAccountInfo();
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setStartAnimation(new PullToRefreshHeaderView.StartAnimationListener() {
            @Override
            public void startAnimation() {
                View pull_iv = updateView(1);
                if (pull_iv!=null) {
                    pull_iv.clearAnimation();
                    pull_iv.startAnimation(mPullToRefreshView.getmFlipAnimation());
                    pull_iv.startAnimation(mPullToRefreshView.getmReverseFlipAnimation());
                }
            }
        });
        Time time = new Time();
        time.setToNow();
        mPullToRefreshView.setLastUpdated("上次更新时间 : " + time.format("%Y-%m-%d %T"));
        header_title.setText("通讯录");
        right_tv.setText("发通知");
        if (accountInfo.getRole() == AppConstants.PARENTROLE) {
            right_tv.setVisibility(View.GONE);
        } else {
            right_tv.setVisibility(View.VISIBLE);
        }
        right_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountInfo.getNoticeurl() != null && !accountInfo.getNoticeurl().equals("")) {
                    Bundle noticebundle = new Bundle();
                    noticebundle.putString(AppConstants.INTENT_URL, accountInfo.getNoticeurl());
                    noticebundle.putString(AppConstants.INTENT_NAME, "发通知");
                    startAnimActivity(CommonBrowser.class, noticebundle);
                }
            }
        });
//        item1_publicnumber.setVisibility(View.VISIBLE);
//        if (accountInfo!=null && accountInfo.getRole() == 0) {
//            item2_classlist.setVisibility(View.VISIBLE);
//            item4_joininto.setVisibility(View.VISIBLE);
//            item3_teacher.setVisibility(View.GONE);
//        } else if (accountInfo!=null && accountInfo.getRole() == 1) {
//            item3_teacher.setVisibility(View.VISIBLE);
//            item2_classlist.setVisibility(View.GONE);
//            item4_joininto.setVisibility(View.GONE);
//        }
        public_account_ll.setOnClickListener(new OnClickListener() {    // 公众号
            @Override
            public void onClick(View view) {
                Intent intent_pub = new Intent(TeacherFragment.this.getActivity(), PuacActivity.class);
                startAnimActivity(intent_pub);
            }
        });
        kindergaten_teacher_ll.setOnClickListener(new OnClickListener() {// 幼儿园老师
            @Override
            public void onClick(View view) {
                Intent intent_teacher = new Intent(TeacherFragment.this.getActivity(), TeacherActivity.class);
                startAnimActivity(intent_teacher);
            }
        });
        classes_list_ll.setOnClickListener(new OnClickListener() {      // 班级列表
            @Override
            public void onClick(View view) {
                Intent intent_parent = new Intent(TeacherFragment.this.getActivity(), ParentActivity.class);
                startAnimActivity(intent_parent);
            }
        });
        invite_teacher_ll.setOnClickListener(new OnClickListener() {    // 邀请老师
            @Override
            public void onClick(View view) {
                if (accountInfo.getKid()!=0) {
                    String url= AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_TEACHER, 0);
                    UtilsLog.i(TAG, url + "");
                    AppUtils.startWebUrlForGuide(TeacherFragment.this.getActivity(), url);
                } else {
                    // 需添加引导页
                    showWaringDialog(0);
                }
            }
        });

        if (accountInfo!=null && accountInfo.getRole() == AppConstants.DIRECTORROLE) {
            classes_list_ll.setVisibility(View.VISIBLE);
            item_view_class.setVisibility(View.VISIBLE);
            invite_teacher_ll.setVisibility(View.VISIBLE);

            kindergaten_teacher_ll.setVisibility(View.GONE);
        } else {
            kindergaten_teacher_ll.setVisibility(View.VISIBLE);

            classes_list_ll.setVisibility(View.GONE);
            item_view_class.setVisibility(View.GONE);
            invite_teacher_ll.setVisibility(View.GONE);
        }
        teacher_list = (PinnedSectionListView) view.findViewById(R.id.activity_contacts_main_teacherlistview);
        teacher_list.setOnItemClickListener(this);
        noteacherBtn.setOnClickListener(new OnClickListener() { // 邀请老师
            @Override
            public void onClick(View view) {
                String url = null;
                if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
//                  if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_DIRECTOR_ACTION, 0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_TEACHER, 0);
                    }
//                  }
                } else if (accountInfo.getRole() == AppConstants.TEACHERROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_NO_KID, 0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_HAS_KID, 0);
                    }
                }
                AppUtils.startWebUrlForGuide(TeacherFragment.this.getActivity(), url);
            }
        });
        int cid = 0;
        appcontext = AppContext.getInstance();
        contacts = appcontext.getContacts();
        if (contacts.getClasses()!=null && contacts.getClasses().size()!=0) { // 有Kid,有班级
            cid = contacts.getClasses().get(0).getCid();
        } else {
            cid = getCidFromClasseDb();
        }
        final int finalCid = cid;
        noparentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url ;
                if (accountInfo.getKid() == 0) {
                    url = AppUtils.replaceUrlByUrl(AppConstants.TEACHER_INVITE_PARENT, accountInfo.getCid());
                } else {
                    url = AppUtils.replaceUrlByUrl(AppConstants.TEACHER_INVITE_PARENT, finalCid);
                }
                AppUtils.startWebUrlForGuide(TeacherFragment.this.getActivity(), url);
            }
        });
        loadUrl_btn.setOnClickListener(new OnClickListener() { // 玩转时光树的 "下一步"
            @Override
            public void onClick(View view) {
                String url = null;
                if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
//                  if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_DIRECTOR_ACTION, 0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_TEACHER, 0);
                    }
//                  }
                } else if (accountInfo.getRole() == AppConstants.TEACHERROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_NO_KID, 0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_HAS_KID, 0);
                    }
                }
                AppUtils.startWebUrlForGuide(TeacherFragment.this.getActivity(), url);
            }
        });
        // ** 已没用到
        listview = (MyListViewWithScrollView) view.findViewById(R.id.activity_contacts_main_puaclistview);
        listview.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UtilsLog.i(TAG, "intp onActivityCreated");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        appcontext = AppContext.getInstance();
        accountInfo = AppServer.getInstance().getAccountInfo();
        contacts = appcontext.getContacts();
        if (contacts!=null) {
            if (accountInfo!=null && accountInfo.getRole() == 0) {
                datalist.clear();
                if (contacts.getTeachers() != null && contacts.getTeachers().size() > 0) {
                    teacherlist = AppUtils.GetListItem(contacts.getTeachers()); // 获取数据（教师列表）
                    datalist.addAll(teacherlist);
                } else {
                    getEmptyUI();
                }
            } else if (accountInfo!=null && accountInfo.getRole() == 1) { //---------------------
                try {  // 对于老师的Parent来说，contacts里的数据其实是children
                    List<Parent> parents = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
                    datalist.clear();
                    if (parents!=null && parents.size() > 0) {
                        teacherlist = AppUtils.GetListItem(parents); // 获取数据（教师列表）
                        datalist.addAll(teacherlist);
                    } else {
                        getEmptyUI();
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                    UtilsLog.i(TAG, e.getMessage() + e.getCause() + "");
                }
            }
         }
        initializeAdapter();
        listview.setOnItemClickListener(this);
        getEmptyUI();

    }

    private int getCidFromClasseDb() {
        int cid = 0;
        try {
            List<Classe> list = DbHelper.getDB(AppContext.getInstance()).findAll(Classe.class);
            if (list!=null && list.size()!=0) {
                cid = list.get(0).getCid();
            }
        } catch (DbException e) {
            e.printStackTrace();
            UtilsLog.i(TAG, e.getMessage() + e.getCause() + "");
        }
        return cid;
    }

    @SuppressLint("NewApi")
    private void initializeAdapter() {
        SimpleAdapter teacherAdapter = new SimpleAdapter(getActivity(), R.layout.item, R.id.contact_puacitemnametv);
        teacher_list.setAdapter(teacherAdapter);
        teacherAdapter.setBirthdayOnclickback(this);
        teacher_list.setShadowVisible(false);
    }

    private void reFreshTeachers(final PullToRefreshHeaderView view) {
        Log.i("account", "构造函数： kname------>" + accountInfo.getKname());
        Log.i("account", "构造函数： kid------>" + accountInfo.getKid());
        if (accountInfo!=null && accountInfo.getRole() == 0) {
            AppServer.getInstance().getTeachersByKid(accountInfo.getUid(), accountInfo.getKid(), new OnAppRequestListener() {
                    @Override
                public void onAppRequest(int code, String message, Object obj) {
                    view.onHeaderRefreshComplete();
                    if (code == AppServer.REQUEST_SUCCESS) {
                        List<Teacher> list = (List<Teacher>) obj;
                        if (list != null) {
                            try {
                                teacherlist.clear();
                                List<Items> itemsList = new ArrayList<Items>();
                                itemsList = AppUtils.GetListItem(list);
                                teacherlist.addAll(itemsList);
                                if (teacherlist != null && teacherlist.size() != 0) {
                                    List<Teacher> teacherList = DbHelper.getDB(TeacherFragment.this.getActivity()).findAll(Teacher.class);
                                    Contacts contacts = AppContext.getInstance().getContacts();
                                    contacts.setTeachers(list);
                                    DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                                    DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                                    if (teacherList != null) {
                                        showToast();
                                    }
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        ShowToast("获取失败" + "   " + message);
                    }
                }
            });
        } else if (accountInfo!=null && accountInfo.getRole() == 1) { //////////////////////////
            AppServer.getInstance().getParentsByTeacherKid(accountInfo.getUid(), accountInfo.getKid(), new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    view.onHeaderRefreshComplete();
                    if (code == AppServer.REQUEST_SUCCESS) {
                        List<Parent> list = (List<Parent>) obj;
                        if (list != null) {
                            try {
                                teacherlist.clear();
                                List<Items> itemsList = new ArrayList<Items>();
                                itemsList = AppUtils.GetListItem(list);
                                teacherlist.addAll(itemsList);
                                if (teacherlist != null && teacherlist.size() != 0) {
                                    List<Parent> teacherList = DbHelper.getDB(TeacherFragment.this.getActivity()).findAll(Parent.class);
                                    DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                                    DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                                    if (teacherList != null) {
                                        showToast();
                                    }
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                                UtilsLog.i(TAG, e.getMessage() + e.getCause() + "");
                            }
                        }
                    } else {
                        ShowToast("获取失败");
                    }
                }
            });
        }

    }

    @Override
    public void onHeaderRefresh(PullToRefreshHeaderView view) {
        reFreshTeachers(view);
    }

    public void showToast() {
        initializeAdapter();
        getEmptyUI();
    }

    class SimpleAdapter extends ArrayAdapter<Items> implements PinnedSectionListView.PinnedSectionListAdapter {
        private Context context;

        BirthdayOnclickback birthdayOnclickback;

        public BirthdayOnclickback getBrithdayOnclickback() {
            return birthdayOnclickback;
        }

        public void setBirthdayOnclickback(BirthdayOnclickback birthdayOnclickback) {
            this.birthdayOnclickback = birthdayOnclickback;
        }

        public DisplayImageOptions options = ImageLoadOptions.getContactsFriendPicOptions();
        public SimpleAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            this.context = context;

            List<HashMap<Integer, String>>ItemName = new ArrayList<HashMap<Integer,String>>();
//          ItemName.add("开通老师账号");
//          ItemName.add(accountInfo.getKname() + "(" + teacherlist.size() + "人)");
            if (accountInfo!=null && accountInfo.getRole() == 0) {
                HashMap<Integer, String> map1 = new HashMap<Integer, String>();
                map1.put(0, "公众号");
                ItemName.add(map1);
                HashMap<Integer, String> map2 = new HashMap<Integer, String>();
                map2.put(0, "班级列表");
                ItemName.add(map2);
                HashMap<Integer, String> map3 = new HashMap<Integer, String>();
                map3.put(0, "邀请老师");
                ItemName.add(map3);

                String sql = "select * from Teacher group by role";
                List<Teacher> teacherList = DbHelper.QueryTData(sql, Teacher.class);
                if (teacherList != null) {
                    Iterator<Teacher> iterator = teacherList.iterator();
                    while (iterator.hasNext()) {
                        Teacher teacher = iterator.next();
                        switch (teacher.getRole()) {
                            case AppConstants.DIRECTORROLE:
                                HashMap<Integer, String> mapy = new HashMap<Integer, String>();
                                mapy.put(teacher.getRole(), "园长");
                                ItemName.add(mapy);
                                break;
                            case AppConstants.TEACHERROLE:
                                HashMap<Integer, String> mapt = new HashMap<Integer, String>();
                                mapt.put(teacher.getRole(), "老师");
                                ItemName.add(mapt);
                                break;
                        }
                    }
                }
            } else if (accountInfo!=null && accountInfo.getRole() == 1) { ///////////////////
                HashMap<Integer, String> map1 = new HashMap<Integer, String>();
                map1.put(0, "公众号");
                ItemName.add(map1);
                HashMap<Integer, String> map2 = new HashMap<Integer, String>();
                map2.put(0, "幼儿园老师");
                ItemName.add(map2);

                String sql = "select * from Parent group by cid";
                List<Parent> parentList = DbHelper.QueryTData(sql, Parent.class);
                if (parentList != null) {
                    Iterator<Parent> iterator = parentList.iterator();
                    while (iterator.hasNext()) {
                        Parent parent = iterator.next();
                        HashMap<Integer, String> map = new HashMap<Integer, String>();
                        map.put(parent.getCid(), parent.getCname());
                        ItemName.add(map);
                    }
                }
            }

            final int sectionsNumber = ItemName.size();
            prepareSections(sectionsNumber);
            int sectionPosition = 0, listPosition = 0;

            for (int i = 0; i < sectionsNumber; i++) {
                if (i == 0) {                                   // 公众号
                    Items section = new Items();
                    section.setViewtype(0);
                    section.setNickname(ItemName.get(i).get(0)); //(put时为0)
                    section.sectionPosition = sectionPosition;
                    section.listPosition = listPosition++;
//                    onSectionAdded(section, sectionPosition);
                    add(section);
                } else if (i == 1) {                             // 幼儿园老师 | 班级列表
                    Items section = new Items();
                    section.setViewtype(0);
                    section.setNickname(ItemName.get(i).get(0)); //(put时为0)
                    section.sectionPosition = sectionPosition;
                    section.listPosition = listPosition++;
//                    onSectionAdded(section, sectionPosition);
                    add(section);
                } else {
                    if (i == 2 && accountInfo != null && accountInfo.getRole() == 0) {  // 邀请加入
                        Items section = new Items();
                        section.setViewtype(0);
                        section.setNickname(ItemName.get(i).get(0)); //(put时为0)
                        section.sectionPosition = sectionPosition;
                        section.listPosition = listPosition++;
//                        onSectionAdded(section, sectionPosition);
                        add(section);
                    } else {
                        UtilsLog.i(TAG, "职位：" + ItemName.get(i));
                        List<Items> list = new ArrayList<Items>();
                        for (int index = 0; index < teacherlist.size(); index++) {          // ## 先加纯数据
                            if (accountInfo != null && accountInfo.getRole() == 0) {
                                if (ItemName.get(i).containsKey(teacherlist.get(index).getRole())) {
                                    list.add(teacherlist.get(index));
                                }
                            } else if (accountInfo != null && accountInfo.getRole() == 1) { ////////////////////////
                                if (ItemName.get(i).containsKey(teacherlist.get(index).getCid())) {
                                    list.add(teacherlist.get(index));
                                }
                            }
                        }

                        final int itemsNumber = list.size();
                        if (itemsNumber > 0) { // 先加groupItem
                            Items section = new Items();
                            section.setViewtype(0); // 表示显示标题内容
                            if (accountInfo != null && accountInfo.getRole() == 0) {
                                section.setNickname(ItemName.get(i).get(list.get(0).getRole()));
                            } else if (accountInfo != null && accountInfo.getRole() == 1) { ///////////////////////
                                section.setNickname(ItemName.get(i).get(list.get(0).getCid()));
                            }
                            section.sectionPosition = sectionPosition;
                            section.listPosition = listPosition++;
                            onSectionAdded(section, sectionPosition);
                            add(section);
                        }

                        for (int j = 0; j < itemsNumber; j++) { // 再加childItem
                            Items item = list.get(j);
                            item.setViewtype(1);
                            if (j == itemsNumber - 1) {
                                item.setLines(true);
                            }
                            item.sectionPosition = sectionPosition;
                            item.listPosition = listPosition++;
                            add(item);
                        }
                    }
                }
            }
        }

        protected void prepareSections(int sectionsNumber) { }
        protected void onSectionAdded(Items section, int sectionPosition) { }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater ll = ((Activity) context).getLayoutInflater();
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = ll.inflate(R.layout.item, null);
                holder.imageView = (CircleImageView)convertView.findViewById(R.id.contact_puacitemiv);
                holder.nametextView = (TextView)convertView.findViewById(R.id.contact_puacitemnametv);
                holder.mian_ly = (LinearLayout)convertView.findViewById(R.id.item_mianly);
                holder.view = convertView.findViewById(R.id.item_view);
                holder.longview = convertView.findViewById(R.id.item_longview);
                holder.arrowiv = (ImageView)convertView.findViewById(R.id.arrowimage);
                holder.title_ly = (LinearLayout)convertView.findViewById(R.id.item_titlely);
                holder.titletv = (TextView)convertView.findViewById(R.id.item_titletv);
                holder.pull_iv = (ImageView)convertView.findViewById(R.id.id_show_pull_iv);
                holder.job_tv = (TextView)convertView.findViewById(R.id.childrenCount);
                holder.invite_parent = (TextView)convertView.findViewById(R.id.invite_parent);
                holder.birthday_tv = (TextView)convertView.findViewById(R.id.birthday_tv); // 生日祝福
                holder.birthday_rl = (RelativeLayout)convertView.findViewById(R.id.birthday_rl); // 生日祝福布局
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Items items = getItem(position);
            if (items!=null) {
                final int birthdaystatus = items.getBirthdaystatus();
                final int cid = items.getCid();
                final String toId = items.getId() + "";
                final String toName = items.getNickname();
                final String birthday = items.getBirthday();

                if (accountInfo != null && accountInfo.getRole() == 1) {
                    if (birthdaystatus == 0) {
                        holder.birthday_rl.setVisibility(View.GONE);
                    } else if (birthdaystatus == 1) {
                        holder.birthday_tv.setBackgroundResource(R.drawable.contact_birthday_icon);
                        holder.birthday_rl.setVisibility(View.VISIBLE);
                    } else {
                        holder.birthday_tv.setBackgroundResource(R.drawable.contact_hassendbirthday_icon);
                        holder.birthday_rl.setVisibility(View.VISIBLE);
                    }
                    SetBirthdayClick(holder.birthday_rl, birthdaystatus, cid, toId, toName, birthday);
                }

                if (items.getViewtype() == 0) {
                    if (accountInfo!=null && accountInfo.getRole() == 1) {
                        if (accountInfo.getRights()!=null && accountInfo.getRights().contains("120")) {
                            holder.invite_parent.setVisibility(View.GONE);
                        } else {
                            holder.invite_parent.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.invite_parent.setVisibility(View.GONE);
                    }
                    if (position == 0 || position == 1 || (accountInfo != null && accountInfo.getRole() == 0 && position == 2)) {
                        holder.mian_ly.setVisibility(View.VISIBLE);
                        holder.view.setVisibility(View.VISIBLE);
                        holder.longview.setVisibility(View.GONE);
                        holder.title_ly.setVisibility(View.GONE);
                        holder.nametextView.setText(items.getNickname());
                        if (position == 0) {
                            holder.imageView.setImageResource(R.drawable.contact_publicaccount);
                        } else if (position == 1) {
                            if (accountInfo != null && accountInfo.getRole() == 0) {
                                holder.imageView.setImageResource(R.drawable.contact_classeslist);
                            } else {
                                holder.imageView.setImageResource(R.drawable.contact_teacher);
                                holder.view.setVisibility(View.GONE);
                            }
                        } else {
                            holder.imageView.setImageResource(R.drawable.contact_joininto);
                            holder.view.setVisibility(View.GONE);
                        }
                        holder.arrowiv.setVisibility(View.VISIBLE);
                        holder.job_tv.setVisibility(View.GONE);
                    } else {
                        holder.mian_ly.setVisibility(View.GONE);
                        holder.view.setVisibility(View.GONE);
                        holder.longview.setVisibility(View.GONE);
                        holder.title_ly.setVisibility(View.VISIBLE);
                        holder.titletv.setText(items.getNickname());
                        final Items newItems = items;
                        holder.invite_parent.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (newItems == null) {
                                    return;
                                }
                                Classe classe = null;
                                try {
                                    classe = DbHelper.getDB(AppContext.getInstance()).findFirst(Classe.class, WhereBuilder.b("cname", "=", newItems.getNickname()));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                if (classe!=null && classe.getCid()!=0) {
                                    UtilsLog.i(TAG, "start invete parent,classid is :" + classe.getCid() + "");
                                    String url = AppUtils.replaceUrlByUrl(AppConstants.TEACHER_INVITE_PARENT, classe.getCid());
                                    AppUtils.startWebUrlForGuide(TeacherFragment.this.getActivity(), url);
                                } else {
                                    UtilsLog.i(TAG, "start invete parent fail,classid is :" + classe.getCid() +"");
                                }
                            }
                        });
                    }
                } else {
//                  if (position == 1) {
//                      holder.pull_iv.setVisibility(View.VISIBLE);
//                  } else {
//                      holder.pull_iv.setVisibility(View.GONE);
//                  }
                    holder.mian_ly.setVisibility(View.VISIBLE);
                    holder.nametextView.setText(items.getNickname());
                    GlideUtils.loadHeadImage(AppContext.getInstance(), items.getAvatar(), holder.imageView);
//                    ImageLoader.getInstance().displayImage(items.getAvatar(), holder.imageView, options);
                    holder.view.setVisibility(View.VISIBLE);
                    holder.arrowiv.setVisibility(View.INVISIBLE);
                    holder.longview.setVisibility(View.GONE);
                    if (accountInfo != null && accountInfo.getRole() == 0) {
                        holder.job_tv.setVisibility(View.VISIBLE);
                        holder.job_tv.setText((items.getJob() == null || items.getJob().equals("")) ? "园长" : items.getJob());
                    } else {
                        holder.job_tv.setVisibility(View.GONE);
                    }
                    if (items.getLines()) {
//                      if (items.getType().equals(AppConstants.CONTACTS_KINDERTEACHER)) {
//                          holder.view.setVisibility(View.GONE);
//                          if (position == getCount() - 1) {
//                              holder.longview.setVisibility(View.VISIBLE);
//                          } else {
//                              holder.longview.setVisibility(View.GONE);
//                          }
//                      } else {
//                          holder.view.setVisibility(View.GONE);
                        holder.longview.setVisibility(View.GONE);
                        if (position == getCount() - 1) {
                            holder.view.setVisibility(View.VISIBLE);
                        } else {
                            holder.view.setVisibility(View.GONE);
                        }
                    } else {
                        holder.view.setVisibility(View.VISIBLE);
                        holder.longview.setVisibility(View.GONE);
                    }
                    holder.title_ly.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        /**
         * 生日点击
         *
         * @param toId
         * @param toName
         * @param birthday
         */
        public void SetBirthdayClick(final View view, final int birthdaystatus, final int cid, final String toId, final String toName, final String birthday) {
            if (birthdayOnclickback!= null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        birthdayOnclickback.birthdayClick(birthdaystatus, cid, toId, toName, birthday);
                    }
                });
            }
        }

        @Override public int getViewTypeCount() {
            return 3;
        }

        @Override public int getItemViewType(int position) {
            return getItem(position).viewtype;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == 0;
        }

    }

    @Override
    public void birthdayClick(int birthdaystatus, int cid, String toId, String toName, String birthday) {
        if (birthdaystatus == 1) {
            UtilsLog.i(TAG, "teacher start to birthday wishes, birthday/cid/toid/toName is: " + birthday + " /" + cid + " /" + toId + " /" + toName);
            startWebUrlForBirthday(cid, toId, toName, birthday, "TeacherParantFragment");
        } else if (birthdaystatus == 2) {
            new PhotoDialog(getActivity(), "我们已给" + toName + "小朋友\n发送过生日祝福啦", AppConstants.DIALOG_TYPE_BIRTHDAY).show();
            // Toast.makeText(TeacherParantFragment.this.getActivity(),"已发送生日祝福",Toast.LENGTH_SHORT).show();
        } else {
            UtilsLog.i(TAG, "the birthdaystatus is 0 or other, birthdaystatus :" + birthdaystatus);
            return;
        }
    }

    /**
     * 专门为生日祝福打开url
     *
     * @param cid
     * @param toid
     */
    private void startWebUrlForBirthday(int cid, String toid, String toName, String birthday, String birthdayfrom) {
        String birthdayurl = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getString("birthdayurl", "");
        if (birthdayurl == null) {
            birthdayurl = "http://ydsence.zgyey.com/Communicate/Birthday_Temp?kid={kid}&" +
                    "client={client}&appver={appver}&uid={uid}&hxuid={hxuid}&role={role}" +
                    "&cid={cid}&ids={ids}&names={names}&births={births}&key={key}";
        }
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        String contansKey = info.getUid() + "" + info.getKid() + URL.urlkey;
        birthdayurl = birthdayurl.replace("{kid}", info.getKid() + "")
                .replace("{client}", "1").replace("{appver}", AppUtils.getVersionName(AppContext.getInstance()))
                .replace("{uid}", info.getUid() + "")
                .replace("{hxuid}", info.getUid() + "a" + info.getRelationship())
                .replace("{role}", info.getRole() + "")
                .replace("{cid}", cid + "")
                .replace("{ids}", toid + "")
                .replace("{names}", toName + "")
                .replace("{births}", birthday + "")
                .replace("{key}", contansKey);
        Intent intent = new Intent(getActivity(), CommonBrowser.class);
        Bundle noticebundle = new Bundle();
        noticebundle.putString(AppConstants.INTENT_URL, birthdayurl);
        noticebundle.putString("birthdayfrom", birthdayfrom);
        noticebundle.putInt("cidBirthday", cid);
        noticebundle.putString("toidBirthday", toid);

        noticebundle.putString(AppConstants.INTENT_NAME, "生日祝福");
        intent.putExtras(noticebundle);
        //startActivityForResult(intent, FROMCOMMONBROWSER_RESULT);
        startActivity(intent);
    }

    class ViewHolder {
        CircleImageView imageView;
        TextView nametextView;
        LinearLayout mian_ly;
        View view;
        View longview;
        ImageView arrowiv;
        LinearLayout title_ly;
        TextView titletv;
        ImageView pull_iv;
        TextView job_tv;
        TextView invite_parent;
        TextView birthday_tv;
        RelativeLayout birthday_rl;
    }

    private void getEmptyUI() {
        // 显示幼儿园名称
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        title_tv.setText(info.getKname() + "(" + teacherlist.size() + "人)");

        if (info.getRole() == 0) {
            if (info.getKid() > 0) { // 有幼儿园
                if (teacherlist.size() > 0) { // 有幼儿园，园长有幼儿园有老师
                    teacher_framelayout.setVisibility(View.GONE);
                    noteacherRl.setVisibility(View.GONE);
                    noparentRl.setVisibility(View.GONE);
                    mPullToRefreshView.setVisibility(View.VISIBLE);

                    no_data_layout.setVisibility(View.GONE);
                } else { // 有幼儿园，园长没老师
                    // 增加缓存被清掉时的保护
                    try {
                        List<Teacher> teacherList = DbHelper.getDB(TeacherFragment.this.getActivity()).findAll(Teacher.class);
                        contacts = AppContext.getInstance().getContacts();
                        if (teacherList!=null && teacherList.size() > 0) {
                            contacts.setTeachers(teacherList);
                            teacherlist = AppUtils.GetListItem(teacherList); // 获取数据（教师列表）
                            datalist.addAll(teacherlist);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (teacherlist == null || teacherlist.size() == 0) {
                        teacher_framelayout.setVisibility(View.GONE);
                        mPullToRefreshView.setVisibility(View.GONE);
                        noteacherRl.setVisibility(View.VISIBLE);
                        noparentRl.setVisibility(View.GONE);

                        no_data_layout.setVisibility(View.VISIBLE);
                    } else {
                        teacher_framelayout.setVisibility(View.GONE);
                        noteacherRl.setVisibility(View.GONE);
                        noparentRl.setVisibility(View.GONE);
                        mPullToRefreshView.setVisibility(View.VISIBLE);

                        no_data_layout.setVisibility(View.GONE);
                    }
//                  AppUtils.setBackground(getActivity(),guide_iv,R.drawable.director_guide);
                }
            } else {  // 没有幼儿园
                teacher_framelayout.setVisibility(View.VISIBLE);
                mPullToRefreshView.setVisibility(View.GONE);
                noteacherRl.setVisibility(View.GONE);
                noparentRl.setVisibility(View.GONE);
                AppUtils.setBackground(getActivity(), guide_iv, R.drawable.director_guide);

                no_data_layout.setVisibility(View.VISIBLE);
            }
        } else if (info.getRole() == AppConstants.TEACHERROLE) {
            if (accountInfo.getKid() == 0) {
                mPullToRefreshView.setVisibility(View.GONE);
                teacher_framelayout.setVisibility(View.VISIBLE);
                noteacherRl.setVisibility(View.GONE);
                noparentRl.setVisibility(View.GONE);
                AppUtils.setBackground(getActivity(), guide_iv, R.drawable.teacher_no_kid);

                no_data_layout.setVisibility(View.VISIBLE);
            } else {
                if (contacts == null) {
                    return;
                }
                int childrenCount = 0; // 所有班级小朋友总数
                if (contacts.getClasses() == null || contacts.getClasses().size() == 0) {
                    try {
                        List<Classe> classList = DbHelper.getDB(TeacherFragment.this.getActivity()).findAll(Classe.class);
                        contacts = AppContext.getInstance().getContacts();
                        if (classList!=null && classList.size()!=0) {
                            contacts.setClasses(classList);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                if (contacts.getClasses()!=null) {
                    for (int i = 0; i < contacts.getClasses().size(); i++) {
                        childrenCount = childrenCount + contacts.getClasses().get(i).getChildrencount();
                    }
                }
                if (contacts.getClasses() == null || contacts.getClasses().size() == 0) { // 有Kid没有班级
                    mPullToRefreshView.setVisibility(View.GONE);
                    noteacherRl.setVisibility(View.GONE);
                    noparentRl.setVisibility(View.GONE);
                    teacher_framelayout.setVisibility(View.VISIBLE);
                    AppUtils.setBackground(getActivity(), guide_iv, R.drawable.teacher_has_kid);

                    no_data_layout.setVisibility(View.VISIBLE);
                } else {
                    if (childrenCount == 0) { // 有班级但没有小朋友
                        mPullToRefreshView.setVisibility(View.GONE);
                        teacher_framelayout.setVisibility(View.GONE);
                        noteacherRl.setVisibility(View.GONE);
                        noparentRl.setVisibility(View.VISIBLE);

                        no_data_layout.setVisibility(View.VISIBLE);
                    } else if (childrenCount > 0) {
                        mPullToRefreshView.setVisibility(View.VISIBLE);
                        teacher_framelayout.setVisibility(View.GONE);
                        noteacherRl.setVisibility(View.GONE);
                        noparentRl.setVisibility(View.GONE);

                        no_data_layout.setVisibility(View.GONE);
                    }
                }
            }
//            if (info.getKid() == 0) { // 没有幼儿园
//                teacher_framelayout.setVisibility(View.VISIBLE);
//                mPullToRefreshView.setVisibility(View.GONE);
//                AppUtils.setBackground(getActivity(), guide_iv, R.drawable.teacher_no_kid);
//            } else { // 有幼儿园
//                if (teacherlist == null || teacherlist.size() == 0) { // 有幼儿园，老师没有家长
//                    teacher_framelayout.setVisibility(View.VISIBLE);
//                    mPullToRefreshView.setVisibility(View.GONE);
//                    AppUtils.setBackground(getActivity(), guide_iv, R.drawable.teacher_has_kid);
//                } else { // 有幼儿园，老师有家长
//                    teacher_framelayout.setVisibility(View.GONE);
//                    mPullToRefreshView.setVisibility(View.VISIBLE);
////                  noteacherRl.setVisibility(View.VISIBLE);
//                }
//            }
        }
        initializeAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        Bundle bundle = new Bundle();
        Items item = (Items) teacher_list.getAdapter().getItem(position);
        switch (item.getViewtype()) {
            case 0:
                if (accountInfo!=null && accountInfo.getRole() == 0) {
                    switch (position) {
                        case 0: // 公众号
                            Intent intent_pub = new Intent(TeacherFragment.this.getActivity(), PuacActivity.class);
                            startAnimActivity(intent_pub);
                            break;
                        case 1: // 班级列表
                            Intent intent_parent = new Intent(TeacherFragment.this.getActivity(), ParentActivity.class);
                            startAnimActivity(intent_parent);
                            break;
                        case 2: // 邀请老师
                            if (accountInfo.getKid()!=0) {
                                String url= AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_TEACHER, 0);
                                UtilsLog.i(TAG, url + "");
                                AppUtils.startWebUrlForGuide(TeacherFragment.this.getActivity(), url);
                            } else {
                                // 需添加引导页
                                showWaringDialog(0);
                            }
                            break;
                    }
                } else {
                    switch (position) {
                        case 0: // 公众号
                            Intent intent_pub = new Intent(TeacherFragment.this.getActivity(), PuacActivity.class);
                            startAnimActivity(intent_pub);
                            break;
                        case 1: // 幼儿园老师
                            Intent intent_teacher = new Intent(TeacherFragment.this.getActivity(), TeacherActivity.class);
                            startAnimActivity(intent_teacher);
                            break;
                        default:
                            break;
                    }
                }
                /*Bundle bundles= new Bundle();
                bundles.putString(AppConstants.BUNDLE_INVITE, AppConstants.INVITETEACHER);
                startAnimActivity(Invite_add_Activity.class, bundles);*/
                break;
            default:
                if (item.getId() == accountInfo.getUid()) {
                    intent = new Intent(getActivity(), MeInfoActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), ContactFriendDatacardActivity.class);
                    bundle.putString("state", AppConstants.CONTACTS_PARENT);
                    bundle.putInt("role", 2);
                    bundle.putInt("targetid", item.getId());
                    bundle.putString("birthday", item.getBirthday());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    public void showWaringDialog(int role) {
        PhotoDialog dialog = new PhotoDialog(this.getActivity(), role);
        dialog.show();
    }

    /**
     * 获取item中的view
     *
     * @param itemIndex
     * @return
     */
    public View updateView(int itemIndex) {
        // 得到第一个可显示控件的位置，
        int visiblePosition = teacher_list.getFirstVisiblePosition();
        // 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition>=0) {
            // 得到要更新的item的view
            View view = teacher_list.getChildAt(itemIndex - visiblePosition);
            ViewHolder holder;
            if (view!=null) {
                // 从view中取得holder
                holder = (ViewHolder) view.getTag();
//                holder.pull_iv = (ImageView)view.findViewById(R.id.id_show_pull_iv);
            } else {
                return new ImageView(getActivity());
            }
            return holder.pull_iv;
        } else {
            return new ImageView(getActivity());
        }
    }

    public void refreshFrament() {
        appcontext = AppContext.getInstance();
        contacts = appcontext.getContacts();
        if (accountInfo!=null && accountInfo.getRole() == 0) {
            if (contacts != null) {  //////////////////////
                datalist.clear();
                if (contacts.getTeachers() != null && contacts.getTeachers().size() > 0) {
                    teacherlist = AppUtils.GetListItem(contacts.getTeachers());
                    datalist.addAll(teacherlist);
                } else {
                    getEmptyUI();
                }
            }
        } else if (accountInfo!=null && accountInfo.getRole() == 1) {   //////////////////
            try {  // 对于老师的Parent来说，contacts里的数据其实是children
                datalist.clear();
                List<Parent> parents = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
                if (parents!=null && parents.size() > 0) {
                    teacherlist = AppUtils.GetListItem(parents); // 获取数据（教师列表）
                    datalist.addAll(teacherlist);
                } else {
                    getEmptyUI();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        if (contactPuacAdapetr!=null) {
            contactPuacAdapetr.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.fragmnetly_notitaddly:
            Intent intent = new Intent(getActivity(), ServiceAddKinderActivity.class);
            startActivity(intent);
            break;
        }
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.TEACHERFRFRAGMENT_RELOADDATA) {
            refreshFrament();
            getEmptyUI();
        } else if (event.getType() == AppEvent.TEACHERPARANTFRAGMENT_BIRTHDAY) {
            UtilsLog.i(TAG, "has sended birthday, start to refreshFragment");
            refreshFrament();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        accountInfo = AppServer.getInstance().getAccountInfo();
        refreshFrament();
        getEmptyUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
