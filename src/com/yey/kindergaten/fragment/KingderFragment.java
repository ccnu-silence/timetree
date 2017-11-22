package com.yey.kindergaten.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.ContactFriendDatacardActivity;
import com.yey.kindergaten.activity.MeInfoActivity;
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
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.PinnedSectionListView;
import com.yey.kindergaten.widget.PinnedSectionListView.PinnedSectionListAdapter;
import com.yey.kindergaten.widget.PullToRefreshHeaderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 通讯录：老师身份
 *
 * Modified by lhd on 2015/08/20.
 *
 */
public class KingderFragment extends FragmentBase implements OnItemClickListener, OnClickListener, PullToRefreshHeaderView.OnHeaderRefreshListener {
    PinnedSectionListView listview;
    private List<Items> teacherlist = new ArrayList<Items>();
    private List<Items> parentlist = new ArrayList<Items>();
    private final static String TAG = "KingderFragment";
    AppContext appcontext = null;
    AccountInfo accountInfo;
    Contacts contacts;
    @ViewInject(R.id.header_title) TextView tv_title;
    @ViewInject(R.id.pull_torefresh_contact)PullToRefreshHeaderView mPullToRefreshView;
    public static DisplayImageOptions options = ImageLoadOptions.getContactsFriendPicOptions();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//      View view = inflater.inflate(R.layout.parentkinderfragment, null);
        View view = inflater.inflate(R.layout.parentkinderfragment, container, false);
        ViewUtils.inject(this, view);
        listview = (PinnedSectionListView) view.findViewById(R.id.activity_contacts_main_puaclistview);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        Time time = new Time();
        time.setToNow();
        tv_title.setText("通讯录");
        mPullToRefreshView.setLastUpdated("上次更新时间 : " + time.format("%Y-%m-%d %T"));
        mPullToRefreshView.setStartAnimation(new PullToRefreshHeaderView.StartAnimationListener() {
            @Override
            public void startAnimation() {
                View pull_iv = updateView(0);
                if (pull_iv!=null) {
                    pull_iv.clearAnimation();
                    pull_iv.startAnimation(mPullToRefreshView.getmReverseFlipAnimation());
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        appcontext = AppContext.getInstance();
        accountInfo = AppServer.getInstance().getAccountInfo();
        if (accountInfo.getRole() == AppConstants.PARENTROLE) {
            listview.setVisibility(View.VISIBLE);
//          expandableListView.setVisibility(View.GONE);
        }
        contacts = appcontext.getContacts();
        if (contacts.getTeachers()!=null && contacts.getTeachers().size() > 0) {
            if (teacherlist!=null) {
                teacherlist.clear();
            }
            teacherlist = AppUtils.GetListItem(contacts.getTeachers(), AppConstants.CONTACTS_KINDERTEACHER);
        }
        if (contacts.getParents()!=null && contacts.getParents().size() > 0) {
            UtilsLog.i(TAG, "contacts.getParents() is not null");
            parentlist = AppUtils.GetListItem(contacts.getParents(), AppConstants.CONTACTS_KINDERPARENT);
        } else {
            UtilsLog.i(TAG, "contacts.getParents() is null or null value");
        }
        initializeAdapter();
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onHeaderRefresh(final PullToRefreshHeaderView view) {
        AccountInfo account = AppContext.getInstance().getAccountInfo();
        AppServer.getInstance().getTeachersAndParentsByCid(account.getUid(), account.getCid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                view.onHeaderRefreshComplete();
                if (code == 0) {
//                  ShowToast("获取成功");
                    refreshFrament();
                } else {
                    ShowToast(message);
                }
            }
        });
    }

    class Holder {
        CircleImageView imageView;
        TextView nametextView;
        LinearLayout mian_ly;
        View view;
        View longview;
        ImageView arrowiv;
        LinearLayout title_ly;
        TextView titletv;
        TextView job_tv;
        ImageView pull_iv;
    }
    class SimpleAdapter extends ArrayAdapter<Items> implements PinnedSectionListAdapter {
        private Context context;
        public SimpleAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            this.context = context;
            int sectionPosition = 0, listPosition = 0;
//          String ItemName[] = new String[]{"园长信箱","老师们","小伙伴们"};
            List<HashMap<Integer,String>>ItemName = new ArrayList<HashMap<Integer,String>>();

            HashMap<Integer, String> mapp = new HashMap<Integer, String>();
            mapp.put(0, "公众号");
            ItemName.add(mapp);
            HashMap<Integer, String> mapk = new HashMap<Integer, String>();
            mapk.put(0, "园长信箱");
            ItemName.add(mapk);

            String sql = "select * from Teacher group by role";
            List<Teacher> teacherList = DbHelper.QueryTData(sql, Teacher.class);
            if (teacherList!=null && teacherList.size()!=0) {
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
                            HashMap<Integer,String> map2 = new HashMap<Integer,String>();
                            map2.put(teacher.getRole(), "老师");
                            ItemName.add(map2);
                            break;
                    }
                }
            } else {
                HashMap<Integer, String> map_teac = new HashMap<Integer, String>();
                map_teac.put(1, "老师");
                ItemName.add(map_teac);
            }
            HashMap<Integer, String> map_paremt = new HashMap<Integer, String>();
            map_paremt.put(2, "小伙伴们");
            ItemName.add(map_paremt);
            final int sectionsNumber = ItemName.size(); // 分类总数：ItemNames （ItemName / Items）
            prepareSections(sectionsNumber);
            for (char i = 0; i < sectionsNumber; i++) {
                if (i == 0) {                                   // 公众号
                    Items section = new Items();
                    section.setViewtype(0);
                    section.setNickname(ItemName.get(i).get(0)); //(put时为0)
                    section.sectionPosition = sectionPosition;
                    section.listPosition = listPosition++;
                    onSectionAdded(section, sectionPosition);
                    add(section); 
                } else if (i == 1) {                            // 园长信箱
                    Items section = new Items();
                    section.setViewtype(0);
                    section.setNickname(ItemName.get(i).get(0)); //(put时为0)
                    section.sectionPosition = sectionPosition;
                    section.listPosition = listPosition++;
                    onSectionAdded(section, sectionPosition);
                    add(section);
                } else if (i < ItemName.size() - 1) { /** 老师 */
                    List<Items> list = new ArrayList<Items>();
                    for (int index = 0; index < teacherlist.size(); index++) {
                        if (ItemName.get(i).containsKey(teacherlist.get(index).getRole())) {
                            list.add(teacherlist.get(index));
                        }
                    }
                    final int itemsNumber = list.size();
                    if (itemsNumber > 0) {                  // 添加老师item
                        Items section = new Items();
                        section.setViewtype(0);
                        section.setNickname(ItemName.get(i).get(list.get(0).getRole()));
                        section.sectionPosition = sectionPosition;
                        section.listPosition = listPosition++;
                        onSectionAdded(section, sectionPosition);
                        add(section);
                    }
                    for (int j = 0; j < itemsNumber; j++) { // 添加老师
                        Items item = list.get(j);
                        item.setViewtype(1);
                        if (j == itemsNumber - 1) {
                            item.setLines(true);
                        }
                        item.sectionPosition = sectionPosition;
                        item.listPosition = listPosition++;
                        add(item);
                    }
                } else if (i == ItemName.size() - 1) {
                    final int itemsNumber = parentlist.size();
                    if (itemsNumber > 0) {                  // 添加小朋友item
                        Items section = new Items();
                        section.setViewtype(0);
                        section.setNickname("小伙伴们");
                        section.sectionPosition = sectionPosition;
                        section.listPosition = listPosition++;
                        onSectionAdded(section, sectionPosition);
                        add(section);
                    }
                    for (int j = 0; j < itemsNumber; j++) { // 添加小朋友
                        Items item = parentlist.get(j);
                        item.setViewtype(2);
                        if (j == itemsNumber - 1) {
                            item.setLines(true);
                        }
                        item.sectionPosition = sectionPosition;
                        item.listPosition = listPosition++;
                        add(item);
                    }
                }
                sectionPosition++;
            }
        }

        protected void prepareSections(int sectionsNumber) { }
        protected void onSectionAdded(Items section, int sectionPosition) { }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater ll = ((Activity) context).getLayoutInflater();
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = mInflater.inflate(R.layout.item, null);
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
                holder = (Holder) convertView.getTag();
            }
            Items items = getItem(position);
            if (items!=null) {
                if (items.getViewtype() == 0) {
                    holder.job_tv.setVisibility(View.GONE);
                    if (position == 0 || position == 1) {
                        holder.mian_ly.setVisibility(View.VISIBLE);
                        holder.nametextView.setText(items.getNickname());
                        holder.titletv.setText(items.getNickname());
                        UtilsLog.i(TAG, "position 1" + items.getNickname() + "");
                        if (position == 0) {
                            holder.imageView.setImageResource(R.drawable.contact_publicaccount);
                        } else {
                            holder.imageView.setImageResource(R.drawable.contact_kinderbox);
                        }
                        if (position == 0) {
                            holder.view.setVisibility(View.VISIBLE);
                        } else {
                            holder.view.setVisibility(View.GONE);
                        }
                        holder.arrowiv.setVisibility(View.VISIBLE);
                        holder.longview.setVisibility(View.GONE);
                        holder.title_ly.setVisibility(View.GONE);
                    } else {
                        holder.mian_ly.setVisibility(View.GONE);
                        holder.view.setVisibility(View.GONE);
                        holder.longview.setVisibility(View.GONE);
                        holder.title_ly.setVisibility(View.VISIBLE);
                        // holder.nametextView.setText(items.getNickname());
                        holder.titletv.setText(items.getNickname());
                        UtilsLog.i(TAG, "position is not 1" + items.getNickname() + "");
                    }
                } else {
                    holder.mian_ly.setVisibility(View.VISIBLE);
                    holder.nametextView.setText(items.getNickname());
                    GlideUtils.loadHeadImage(AppContext.getInstance(), items.getAvatar(), holder.imageView);
//                    ImageLoader.getInstance().displayImage(items.getAvatar(), holder.imageView, options);
                    holder.view.setVisibility(View.VISIBLE);
                    holder.arrowiv.setVisibility(View.INVISIBLE);
//                    holder.arrowiv.setVisibility(View.GONE);
                    if (items.getRole()!=AppConstants.PARENTROLE) {
                        holder.job_tv.setVisibility(View.VISIBLE);
                        holder.job_tv.setText(items.getJob());
                    } else {
                        holder.job_tv.setVisibility(View.GONE);
                    }
                    holder.longview.setVisibility(View.GONE);
                    if (items.getLines()) {
//                      if (items.getType().equals(AppConstants.CONTACTS_KINDERTEACHER)) {
//                          holder.view.setVisibility(View.GONE);
//                          holder.longview.setVisibility(View.GONE);
//                      } else {
//                          holder.view.setVisibility(View.VISIBLE);
//                          holder.longview.setVisibility(View.GONE);
//                      }
                        if (items.getViewtype() == 1 || items.getViewtype() == 2) {
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

    @SuppressLint("NewApi")
    private void initializeAdapter() {
        listview.setAdapter(new SimpleAdapter(getActivity(), R.layout.item, R.id.contact_puacitemnametv));
        listview.setShadowVisible(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        Bundle bundle = new Bundle();
        Items item = (Items) listview.getAdapter().getItem(position);
        switch (item.getViewtype()) {
            case 0:
                if (item.getSectionPosition() == 0) {
                    Intent intent_pub = new Intent(KingderFragment.this.getActivity(), PuacActivity.class);
                    startAnimActivity(intent_pub);
                } else if (item.getSectionPosition() == 1) {
                    startWebLooktask(accountInfo.getUid());
                }
                break;
            case 1:
                if (item.getId() == accountInfo.getUid()) {
                    intent = new Intent(getActivity(), MeInfoActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), ContactFriendDatacardActivity.class);
                    bundle.putString("state", AppConstants.CONTACTS_TEACHER);
                    bundle.putInt("role", 2);
                    bundle.putInt("targetid", item.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case 2:
                if (item.getId() == accountInfo.getUid()) {
                    intent = new Intent(getActivity(), MeInfoActivity.class);
                    startActivity(intent);
                } else {
                    String birthday = item.getBirthday();
                    intent = new Intent(getActivity(), ContactFriendDatacardActivity.class);
                    bundle.putString("state", AppConstants.CONTACTS_PARENT);
                    bundle.putString("birthday", birthday);
                    bundle.putInt("role", 2);
                    bundle.putInt("targetid", item.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取item中的view
     * @param itemIndex
     * @return
     */
    public View updateView(int itemIndex) {
        // 得到第一个可显示控件的位置，
        int visiblePosition = listview.getFirstVisiblePosition();
        // 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition >= 0) {
            // 得到要更新的item的view
            View view = listview.getChildAt(itemIndex - visiblePosition);
            Holder holder;
            if (view!=null) {
                // 从view中取得holder
                holder = (Holder) view.getTag();
//              holder.pull_iv = (ImageView)view.findViewById(R.id.id_show_pull_iv);
            } else {
                return new ImageView(getActivity());
            }
            return holder.pull_iv;
        } else {
            return new ImageView(getActivity());
        }
    }

    public void startWebLooktask(int uid) {
        Intent intent = new Intent(getActivity(), CommonBrowser.class);
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.INTENT_URL, accountInfo.getMasterletterurl() + "?uid=" + uid);
        bundle.putString(AppConstants.INTENT_NAME, "园长信箱");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void refreshFrament() {
        appcontext = AppContext.getInstance();
        contacts = appcontext.getContacts();
        if (contacts!=null) {
            if (contacts.getTeachers()!=null && contacts.getTeachers().size() > 0) {
                teacherlist = AppUtils.GetListItem(contacts.getTeachers(), AppConstants.CONTACTS_KINDERTEACHER);
            } else {
                teacherlist = new ArrayList<Items>();
            }
            if (contacts.getParents()!=null && contacts.getParents().size() > 0) {
                parentlist = AppUtils.GetListItem(contacts.getParents(), AppConstants.CONTACTS_KINDERPARENT);
            } else {
                parentlist = new ArrayList<Items>();
            }
        }
        initializeAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.KINDERFRAGMENT_RELOADDATA) {
            refreshFrament();
        } else if (event.getType() == AppEvent.HOMEFRAGMENT_REFRESH_CONTACT) {
            refreshFrament();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
