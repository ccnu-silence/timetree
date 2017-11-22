package com.yey.kindergaten.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ContactFriendDatacardActivity;
import com.yey.kindergaten.activity.ContactsParentList;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Items;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.inter.OnAooRequestParentListener;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.PullToRefreshHeaderView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ParentFragment extends FragmentBase implements OnItemClickListener, OnClickListener, PullToRefreshHeaderView.OnHeaderRefreshListener {

    ExpandableListView exListview;
    List<Items> parentlist = new ArrayList<Items>();
    List<Items> datalist = new ArrayList<Items>();
    AppContext appcontext = null;
    AccountInfo accountInfo;
    Contacts contacts;

    FrameLayout nodataddly;
    @ViewInject(R.id.pull_torefresh_contact)PullToRefreshHeaderView mPullToRefreshView;
    @ViewInject(R.id.id_load_guade_url)Button loadUrl_btn;

    @ViewInject(R.id.has_kid_no_teacher_fl)RelativeLayout noteacherRl;
    @ViewInject(R.id.no_teacher_join_btn)Button noteacherBtn;
    private int types = 0;
    private ParantAdapter parentAdapter;
    private int x = 0;
    private List<Children> childrenList = new ArrayList<Children>();
    int childrenCount = 0; // 所有班级小朋友总数
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        appcontext = AppContext.getInstance();
        accountInfo = AppServer.getInstance().getAccountInfo();
        contacts = appcontext.getContacts();

        if (contacts != null) {
            if (contacts.getClasses() != null && contacts.getClasses().size() > 0) {
                parentlist = AppUtils.GetListItem(contacts.getClasses());
                datalist.clear();
                datalist.addAll(parentlist);
//                if(contacts.getClasses()!=null&&contacts.getClasses().size()>0){
//                    getClassListByKid(null);
//                    refreshFrament();
//                }
            }
        }
        if (contacts.getClasses() == null) {
            try {
                List<Classe>classes = DbHelper.getDB(this.getActivity()).findAll(Classe.class);
                if (classes == null) {
                    classes = new ArrayList<Classe>();
                }
                contacts.setClasses(classes);
                AppContext.getInstance().setContacts(contacts);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        parentAdapter = new ParantAdapter(contacts.getClasses(), childrenList, this.getActivity());
        if (childrenList == null || childrenList.size() == 0) {
            Children child = new Children();
            child.setRealname("loading");
            childrenList.add(child);
        }
        exListview.setAdapter(parentAdapter);
        try {
            List<Children> list = DbHelper.getDB(this.getActivity()).findAll(Children.class);
            if (list == null || list.size() == 0) {
                getNetParent();
            } else {
                childrenList.clear();
                childrenList.addAll(list);
                parentAdapter.notifyDataSetChanged();
                exListview.expandGroup(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        exListview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int n) {
                for (int i = 0; i < parentAdapter.getGroupCount(); i++) {
                    // ensure only one expanded Group exists at every time
                    if (n != i && exListview.isGroupExpanded(n)) {
                        exListview.collapseGroup(i);
                    }
                }
                if (exListview.isGroupExpanded(n)) {
                    if (childrenList != null && x != 0 && accountInfo.getRole() == 1) {
                        childrenList.clear();
                        Children child = new Children();
                        child.setRealname("loading");
                        childrenList.add(child);
                        parentAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        exListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(final ExpandableListView expandableListView, View view, final int i, long l) {
                if (accountInfo.getRole() == 0) {
                    childrenList.clear();
                    Classe item = contacts.getClasses().get(i);
                    initdata(item);
                    return true;
                }
                x = 1;
                boolean flag = !exListview.isGroupExpanded(i);
                if (!exListview.isGroupExpanded(i)) {
                    try {
                        List<Children> list = DbHelper.getDB(ParentFragment.this.getActivity()).findAll(Children.class);
                        if ((list == null || list.size() == 0) || i != 0) {
                            childrenList.clear();
                            AppServer.getInstance().GetParentByCid(accountInfo.getUid(), contacts.getClasses().get(i).getCid(), new OnAooRequestParentListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj, Object obj2) {
                                    if (code == AppServer.REQUEST_SUCCESS) {
                                        childrenList.clear();
                                        Children[] childrens = (Children[]) obj;
                                        for (Children children : childrens) {
                                            childrenList.add(children);
                                        }
                                        if (childrenList == null || childrenList.size() == 0) {
                                            return;
                                        } else {
                                            parentAdapter.notifyDataSetChanged();
                                            parentAdapter.notifyDataSetInvalidated();
                                        }
                                    } else {
                                        ShowToast("获取学生列表失败");
                                        exListview.collapseGroup(i);
                                    }
                                }
                            });
                        } else {
                            x = 0;
                            childrenList.clear();
                            childrenList.addAll(list);
                            parentAdapter.notifyDataSetChanged();
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        exListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                return false;
            }
        });
        EmptyDataType();
    }

    private void showRemindDialog(final Classe item){
        showDialog("提示", "去邀请", item.getCname() + "暂无家长加入，快去邀请吧。", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AppUtils.startWebUrlForGuide(ParentFragment.this.getActivity(), AppUtils.replaceUrlByUrl(75, item.getCid()));
            }
        });
    }

    public void initdata(final Classe item){
        if (item!=null && item.getChildrencount() == 0) {
            showRemindDialog(item);
        } else if (item.getChildrencount() > 0) {
            Intent intent = new Intent(getActivity(), ContactsParentList.class);
            intent.putExtra("cid", item.getCid());
            intent.putExtra("mycalss", item.getCname());
            startActivity(intent);
        }
    }

    /**
     * 通讯录刷新显示界面
     */
    private void EmptyDataType() {
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        if (info.getKid() == 0) {
            nodataddly.setVisibility(View.VISIBLE);
            mPullToRefreshView.setVisibility(View.GONE);
        } else {
            if (contacts!=null && contacts.getClasses()!=null) {
                for (int i = 0; i < contacts.getClasses().size(); i++){
                   childrenCount = childrenCount + contacts.getClasses().get(i).getChildrencount();
                }
            }
            if (childrenCount == 0) {
                nodataddly.setVisibility(View.GONE);
                mPullToRefreshView.setVisibility(View.GONE);
                noteacherRl.setVisibility(View.VISIBLE);
            } else if (childrenCount > 0) {
                nodataddly.setVisibility(View.GONE);
                mPullToRefreshView.setVisibility(View.VISIBLE);
                types = 1;
            }
       }
    }

    /**
     * 园长刷新班级列表
     *
     * @throws JSONException
     */
    public void getClassListByKid(final PullToRefreshHeaderView view) {
        AppServer.getInstance().getClassesByKid(accountInfo.getUid(), accountInfo.getKid(), accountInfo.getRole(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                view.onHeaderRefreshComplete();
                if (code == AppServer.REQUEST_SUCCESS) {
                    List<Classe> list = (List<Classe>) obj;
                    Contacts contacts = AppContext.getInstance().getContacts();
                    if (list!=null) {
                        contacts.setClasses(list);
                        try {
                            DbHelper.getDB(ParentFragment.this.getActivity()).deleteAll(Classe.class);
                            DbHelper.getDB(ParentFragment.this.getActivity()).saveAll(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    AppContext.getInstance().setContacts(contacts);
                } else {
                    ShowToast(message);
                }
            }
        });
    }

    private void getNetParent() {
        if (accountInfo.getRole() == 1 && contacts.getClasses().size() != 0) {
            AppServer.getInstance().GetParentByCid(accountInfo.getUid(), contacts.getClasses().get(0).getCid(), new OnAooRequestParentListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj, Object obj2) {
                    if (code == AppServer.REQUEST_SUCCESS) {
                        childrenList.clear();
                        Children[] childrens = (Children[]) obj;
                        for (Children children : childrens) {
                            childrenList.add(children);
                        }
                        try {
                            DbHelper.getDB(ParentFragment.this.getActivity()).saveAll(childrenList);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if (childrenList != null || childrenList.size() != 0) {
                            parentAdapter.notifyDataSetChanged();
                            exListview.expandGroup(0);
                        } else {

                        }
                    } else {
                        ShowToast("获取学生列表失败");
                        exListview.collapseGroup(0);
                    }
                }
            });
        }
    }

    /**
     * 获取item中的view
     * @param itemIndex
     * @return
     */
    public View updateView(int itemIndex) {
        // 得到第一个可显示控件的位置，
        int visiblePosition = exListview.getFirstVisiblePosition();
        // 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition >= 0) {
            // 得到要更新的item的view
            View view = exListview.getChildAt(itemIndex - visiblePosition);
            Holder holder;
            if (view!=null) {
                // 从view中取得holder
                holder = (Holder) view.getTag();
            } else {
                return new ImageView(getActivity());
            }
            return  holder.pull_iv;
        } else {
            return new ImageView(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.parentfragment, null);
        ViewUtils.inject(this, view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        Time time = new Time();
        time.setToNow();
        mPullToRefreshView.setLastUpdated("上次更新时间 : " + time.format("%Y-%m-%d %T"));
        mPullToRefreshView.setStartAnimation(new PullToRefreshHeaderView.StartAnimationListener() {
            @Override
            public void startAnimation() {
                final View pull_iv = updateView(0);
                if (pull_iv!=null) {
                    pull_iv.clearAnimation();
                    pull_iv.startAnimation(mPullToRefreshView.getmReverseFlipAnimation());
                }
            }
        });

        nodataddly = (FrameLayout) view.findViewById(R.id.fragmnetly_notitaddly);
        exListview = (ExpandableListView) view.findViewById(R.id.activity_contacts_main_parant_expandlv);
        noteacherBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = null;
                if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                 if (childrenCount == 0) {
                        url= AppUtils.replaceUrlByUrl(AppConstants.DIRCOTER_INVITE_PARENT, 0);
                    }
                }
                AppUtils.startWebUrlForGuide(ParentFragment.this.getActivity(), url);
            }
        });

        loadUrl_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = null;
                if (accountInfo.getRole() == AppConstants.DIRECTORROLE) {
                    if (accountInfo.getKid() == 0) {
                        url = AppUtils.replaceUrlByUrl(AppConstants.HX_DIRECTOR_ACTION, 0);
                    }
                }
                AppUtils.startWebUrlForGuide(ParentFragment.this.getActivity(), url);
            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }

    @Override
    public void onPause() {
        super.onPause();
        x = 0;
    }

    public void refreshFrament() {
        appcontext = AppContext.getInstance();
        contacts = appcontext.getContacts();
        if (AppServer.getInstance().getAccountInfo().getRole() == 0) {
            // 排序class
            try {
                List<Classe> classe = DbHelper.getDB(AppContext.getInstance()).findAll(Selector.from(Classe.class).orderBy("OrderNo", false));
                if (classe == null) {
                    classe = new ArrayList<Classe>();
                }
                contacts.setClasses(classe);
                AppContext.getInstance().setContacts(contacts);
            } catch (DbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (parentAdapter!=null) {
            parentAdapter.SetData(contacts.getClasses());
            try {
             List<Children>list = DbHelper.getDB(this.getActivity()).findAll(Children.class);
                if (list!=null) {
                    childrenList = list;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            parentAdapter.notifyDataSetChanged();
        }
        EmptyDataType();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private boolean hidden;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refreshFrament();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFrament();
    }

    @Override
    public void onHeaderRefresh(PullToRefreshHeaderView view) {
        getClassListByKid(view);
        refreshFrament();
    }

    class Holder {
        CircleImageView imageView;
        TextView nametextView;
        LinearLayout mian_ly;
        View view;
        View longview;
        ImageView arrowiv;
        LinearLayout title_ly;
        TextView  titletv;
        ImageView pull_iv;
    }

    class ParantAdapter extends BaseExpandableListAdapter {

        private List<Classe> grounpList;
        private List<Children> childrenList;
        private Context context;
        private LayoutInflater inflater;

        private void SetData(List<Classe>classeList) {
            this.grounpList = classeList;
        }

        ParantAdapter(List<Classe> grounpList, List<Children> childrenList, Context context) {
            this.grounpList = grounpList;
            this.childrenList = childrenList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return grounpList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return childrenList.size();
        }

        @Override
        public Object getGroup(int i) {
            return grounpList.get(i);
        }

        @Override
        public Object getChild(int groupPostion, int childPosition) {
            return childrenList.get(childPosition);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPositoin, boolean isExpanded, View convertView, ViewGroup viewGroup) {
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
                holder.titletv = (TextView)convertView.findViewById(R.id.childrenCount);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

//          if (groupPositoin == 0) {
//              holder.pull_iv.setVisibility(View.VISIBLE);
//          } else {
//              holder.pull_iv.setVisibility(View.GONE);
//          }
            holder.titletv.setVisibility(View.GONE);
            Classe classe = grounpList.get(groupPositoin);
            holder.mian_ly.setVisibility(View.VISIBLE);
            holder.nametextView.setText(classe.getCname() + " (" + classe.getChildrencount() + "人" + ") ");
            holder.view.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.arrowiv.setVisibility(View.GONE);
            holder.longview.setVisibility(View.GONE);
            return convertView;
        }

        @Override
        public View getChildView(final int grounpPosition, final int childPosition, boolean lsLastChild, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.parentlistitem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_frienditemnametv);
            final Children children = (Children) childrenList.get(childPosition);
            ImageLoader.getInstance().displayImage(children.getAvatar(), imageView, ImageLoadOptions.getHeadOptions());
            nametextView.setText(children.getRealname());
            RelativeLayout loading = ViewHolder.get(convertView, R.id.show_loading_parent_rl);
            LinearLayout content = ViewHolder.get(convertView, R.id.show_realname_ll);
            // RelativeLayout birthday_rl = ViewHolder.get(convertView, R.id.birthday_rl);
            View itemview = ViewHolder.get(convertView, R.id.item_view);
            if (children.getRealname().equals("loading")) {
                loading.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                itemview.setVisibility(View.GONE);
            } else {
                loading.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                itemview.setVisibility(View.VISIBLE);
            }

            content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ParentFragment.this.getActivity(), ContactFriendDatacardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("state", AppConstants.CONTACTS_PARENT);
                    bundle.putInt("role", 2);
                    bundle.putInt("targetid", childrenList.get(childPosition).getUid());
                    bundle.putInt(AppConstants.PARAM_CID, grounpList.get(grounpPosition).getCid());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return false;
        }

    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.PARENTFRAGMENT_RELOADDATA) {
            refreshFrament();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}