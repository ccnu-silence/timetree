package com.yey.kindergaten.activity;

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
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Items;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.MyListViewWithScrollView;
import com.yey.kindergaten.widget.PinnedSectionListView;
import com.yey.kindergaten.widget.PullToRefreshHeaderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

public class TeacherActivity extends BaseActivity implements OnItemClickListener, OnClickListener, PullToRefreshHeaderView.OnHeaderRefreshListener {

    MyListViewWithScrollView listview;
    ServiceAdapter contactPuacAdapetr;
    List<Items> teacherlist = new ArrayList<Items>();
    List<Items> datalist = new ArrayList<Items>();
    AppContext appcontext = null;
    AccountInfo accountInfo;
    Contacts contacts;
    private String TAG = "TeacherFragment";

    @ViewInject(R.id.parent_head_title_ll)LinearLayout head_title_ll;
    @ViewInject(R.id.header_title)TextView head_title_tv;
    @ViewInject(R.id.left_btn) ImageView left_btn;

    @ViewInject(R.id.item_titlely)LinearLayout titlt_ly;
    @ViewInject(R.id.item_titletv)TextView title_tv;
    @ViewInject(R.id.id_load_guade_url)Button loadUrl_btn;
    @ViewInject(R.id.teacher_framelayout)FrameLayout teacher_framelayout;
    @ViewInject(R.id.pull_torefresh_contact)PullToRefreshHeaderView mPullToRefreshView;

    @ViewInject(R.id.has_kid_no_teacher_fl)RelativeLayout noteacherRl;
    @ViewInject(R.id.no_teacher_join_btn)Button noteacherBtn;

    @ViewInject(R.id.shou_guide_image)ImageView guide_iv;
    PinnedSectionListView teacher_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puacfragmently);
        ViewUtils.inject(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        head_title_ll.setVisibility(View.VISIBLE);
        head_title_tv.setText("幼儿园老师");
        listview = (MyListViewWithScrollView) findViewById(R.id.activity_contacts_main_puaclistview);
        listview.setVisibility(View.GONE);

        appcontext = AppContext.getInstance();
        accountInfo = AppServer.getInstance().getAccountInfo();
        contacts = appcontext.getContacts();
        if (contacts!=null) {
            if (contacts.getTeachers()!=null && contacts.getTeachers().size() > 0) {
                teacherlist = AppUtils.GetListItem(contacts.getTeachers());
                datalist.clear();
                datalist.addAll(teacherlist);
            }
        }
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherActivity.this.finish();
            }
        });
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

        teacher_list = (PinnedSectionListView) findViewById(R.id.activity_contacts_main_teacherlistview);
        teacher_list.setOnItemClickListener(this);
        noteacherBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = null;
                if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_DIRECTOR_ACTION, 0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_TEACHER, 0);
                    }
                } else if (accountInfo.getRole() == AppConstants.TEACHERROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_NO_KID, 0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_HAS_KID, 0);
                    }
                }
                AppUtils.startWebUrlForGuide(TeacherActivity.this,url);
            }
        });
        loadUrl_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = null;
                if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_DIRECTOR_ACTION, 0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_TEACHER, 0);
                    }
                } else if (accountInfo.getRole() == AppConstants.TEACHERROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_NO_KID,0);
                    } else if (teacherlist == null || teacherlist.size() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_HAS_KID, 0);
                    }
                }
                AppUtils.startWebUrlForGuide(TeacherActivity.this, url);
            }
        });

        initializeAdapter();
        listview.setOnItemClickListener(this);
        getEmptyUI();
    }

    @SuppressLint("NewApi")
    private void initializeAdapter() {
        teacher_list.setAdapter(new SimpleAdapter(TeacherActivity.this, R.layout.item, R.id.contact_puacitemnametv));
        teacher_list.setShadowVisible(false);
    }

    private void  reFreshTeachers(final PullToRefreshHeaderView view) {
        Log.i("account", "构造函数： kname------>" + accountInfo.getKname());
        Log.i("account", "构造函数： kid------>" + accountInfo.getKid());
        AppServer.getInstance().getTeachersByKid(accountInfo.getUid(), accountInfo.getKid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                view.onHeaderRefreshComplete();
                if (code == AppServer.REQUEST_SUCCESS) {
                    List<Teacher>list = (List<Teacher>) obj;
                    if (list!=null) {
                        try {
                            teacherlist.clear();
                            List<Items> itemsList = new ArrayList<Items>();
                            itemsList = AppUtils.GetListItem(list);
                            teacherlist.addAll(itemsList);
                            if (teacherlist!=null && teacherlist.size()!=0) {
                                List<Teacher> teacherList = DbHelper.getDB(TeacherActivity.this).findAll(Teacher.class);
                                Contacts contacts = AppContext.getInstance().getContacts();
                                contacts.setTeachers(list);
                                DbHelper.getDB(AppContext.getInstance()).deleteAll(Teacher.class);
                                DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                                if (teacherList != null) {
                                    showToast();
                                }
                            }
                        } catch(DbException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    showToast("获取失败" + "   " + message);
                }
            }
        });
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
        public DisplayImageOptions options = ImageLoadOptions.getContactsFriendPicOptions();

        public SimpleAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            this.context = context;
            List<HashMap<Integer,String>>ItemName = new ArrayList<HashMap<Integer,String>>();
//            ItemName.add("开通老师账号");
//            ItemName.add(accountInfo.getKname() + "(" + teacherlist.size() + "人)");
            String sql = "select * from Teacher group by role";
            List<Teacher> teacherList = DbHelper.QueryTData(sql, Teacher.class);;
            if (teacherList!=null) {
                Iterator<Teacher> iterator = teacherList.iterator();
                while (iterator.hasNext()) {
                    Teacher teacher = iterator.next();
                    switch (teacher.getRole()) {
                        case AppConstants.DIRECTORROLE:
                            HashMap<Integer, String> map1 = new HashMap<Integer,String>();
                            map1.put(teacher.getRole(), "园长");
                            ItemName.add(map1);
                            break;
                        case AppConstants.TEACHERROLE:
                            HashMap<Integer, String> map2 = new HashMap<Integer,String>();
                            map2.put(teacher.getRole(), "老师");
                            ItemName.add(map2);
                            break;
                    }
                }
            }

            final int sectionsNumber = ItemName.size();
            prepareSections(sectionsNumber);
            int sectionPosition = 0, listPosition = 0;

            for (int i = 0; i < sectionsNumber; i++) {
                UtilsLog.i(TAG,"职位：" + ItemName.get(i));
                List<Items> list = new ArrayList<Items>();
                for (int index = 0; index < teacherlist.size(); index++) {
                    if (ItemName.get(i).containsKey(teacherlist.get(index).getRole())) {
                        list.add(teacherlist.get(index));
                    }
                }
                final int itemsNumber = list.size();
                if (itemsNumber > 0) {
                    Items section = new Items();
                    section.setViewtype(0); // 表示显示标题内容
                    section.setNickname(ItemName.get(i).get(list.get(0).getRole()));
                    section.sectionPosition = sectionPosition;
                    section.listPosition = listPosition++;
                    onSectionAdded(section, sectionPosition);
                    add(section);
                }
                for (int j = 0; j < itemsNumber; j++) {
                    Items item = list.get(j);
                    item.setViewtype(1);
                    item.sectionPosition = sectionPosition;
                    item.listPosition = listPosition++;
                    add(item);
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Items items = getItem(position);
            if (items!=null) {
                if (items.getViewtype() == 0) {
                    if (position == 0) {
                        holder.mian_ly.setVisibility(View.GONE);
                        holder.view.setVisibility(View.GONE);
                        holder.longview.setVisibility(View.GONE);
                        holder.title_ly.setVisibility(View.VISIBLE);
                        holder.titletv.setText(items.getNickname());
                    } else {
                        holder.mian_ly.setVisibility(View.GONE);
                        holder.view.setVisibility(View.GONE);
                        holder.longview.setVisibility(View.GONE);
                        holder.title_ly.setVisibility(View.VISIBLE);
                        holder.titletv.setText(items.getNickname());
                    }
                } else {
//                  if (position == 1) {
//                      holder.pull_iv.setVisibility(View.VISIBLE);
//
//                  } else {
//                      holder.pull_iv.setVisibility(View.GONE);
//                  }
                    holder.mian_ly.setVisibility(View.VISIBLE);
                    holder.nametextView.setText(items.getNickname());
                    ImageLoader.getInstance().displayImage(items.getAvatar(), holder.imageView, options);
                    holder.view.setVisibility(View.VISIBLE);
                    holder.arrowiv.setVisibility(View.INVISIBLE);
                    holder.longview.setVisibility(View.GONE);
                    holder.job_tv.setVisibility(View.VISIBLE);
                    holder.job_tv.setText(items.getJob());
                    if (items.getLines()) {
                        if (items.getType().equals(AppConstants.CONTACTS_KINDERTEACHER)) {
                            holder.view.setVisibility(View.GONE);
                            holder.longview.setVisibility(View.GONE);
                        } else {
                            holder.view.setVisibility(View.VISIBLE);
                            holder.longview.setVisibility(View.GONE);
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

    class ViewHolder {
        CircleImageView imageView;
        TextView nametextView;
        LinearLayout mian_ly;
        View view;
        View longview;
        ImageView arrowiv;
        LinearLayout title_ly;
        TextView  titletv;
        ImageView pull_iv;
        TextView job_tv;
    }

    private void getEmptyUI() {
        // 显示幼儿园名称
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        title_tv.setText(info.getKname() + "(" + teacherlist.size() + "人)");

        if (info.getRole() == 0) {
            if (info.getKid() > 0) {
                if (teacherlist.size() > 0) { // 园长有幼儿园有老师
                    teacher_framelayout.setVisibility(View.GONE);
                    mPullToRefreshView.setVisibility(View.VISIBLE);
                } else { // 有幼儿园没老师
                    teacher_framelayout.setVisibility(View.GONE);
                    mPullToRefreshView.setVisibility(View.GONE);
                    noteacherRl.setVisibility(View.VISIBLE);
//                  AppUtils.setBackground(getActivity(),guide_iv,R.drawable.director_guide);
                }
            } else {
                teacher_framelayout.setVisibility(View.VISIBLE);
                mPullToRefreshView.setVisibility(View.GONE);
                AppUtils.setBackground(TeacherActivity.this, guide_iv, R.drawable.director_guide);
            }
        } else if (info.getRole() == AppConstants.TEACHERROLE) {
            if (info.getKid() == 0) {
                teacher_framelayout.setVisibility(View.VISIBLE);
                mPullToRefreshView.setVisibility(View.GONE);
                AppUtils.setBackground(TeacherActivity.this, guide_iv, R.drawable.teacher_no_kid);
            } else {
                if (teacherlist == null || teacherlist.size() == 0) { // 有幼儿园没有老师
                    teacher_framelayout.setVisibility(View.VISIBLE);
                    mPullToRefreshView.setVisibility(View.GONE);
                    AppUtils.setBackground(TeacherActivity.this, guide_iv, R.drawable.teacher_has_kid);
                } else {
                    teacher_framelayout.setVisibility(View.GONE);
                    mPullToRefreshView.setVisibility(View.VISIBLE);
//                  noteacherRl.setVisibility(View.VISIBLE);
                }
            }
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
/*                Bundle bundles= new Bundle();
                bundles.putString(AppConstants.BUNDLE_INVITE, AppConstants.INVITETEACHER);
                startAnimActivity(Invite_add_Activity.class, bundles);*/
                break;
            default:
                if (item.getId() == accountInfo.getUid()) {
                    intent = new Intent(TeacherActivity.this, MeInfoActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(TeacherActivity.this,ContactFriendDatacardActivity.class);
                    bundle.putString("state", AppConstants.CONTACTS_PARENT);
                    bundle.putInt("role", 2);
                    bundle.putInt("targetid", item.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
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
        if (itemIndex - visiblePosition >= 0) {
            // 得到要更新的item的view
            View view = teacher_list.getChildAt(itemIndex - visiblePosition);
            ViewHolder holder;
            if (view!=null) {
                // 从view中取得holder
                holder = (ViewHolder) view.getTag();
//              holder.pull_iv =(ImageView)view.findViewById(R.id.id_show_pull_iv);
            } else {
                return new ImageView(TeacherActivity.this);
            }
            return holder.pull_iv;
        } else {
            return new ImageView(TeacherActivity.this);
        }
    }

    public void refreshFrament() {
        appcontext = AppContext.getInstance();
        contacts = appcontext.getContacts();
        if (contacts!=null) {
            if (contacts.getTeachers()!=null && contacts.getTeachers().size() > 0) {
                teacherlist = AppUtils.GetListItem(contacts.getTeachers());
                datalist.clear();
                datalist.addAll(teacherlist);
            } else {
                datalist.clear();
            }
        }
//      contactPuacAdapetr.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragmnetly_notitaddly:
                Intent intent = new Intent(TeacherActivity.this, ServiceAddKinderActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.TEACHERFRFRAGMENT_RELOADDATA) {
            refreshFrament();
            getEmptyUI();
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
