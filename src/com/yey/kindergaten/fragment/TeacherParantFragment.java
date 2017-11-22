package com.yey.kindergaten.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.ContactFriendDatacardActivity;
import com.yey.kindergaten.activity.MeInfoActivity;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Items;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.SendMsgChildItem;
import com.yey.kindergaten.bean.SendMsgGroupItem;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.BirthdayOnclickback;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.PhotoDialog;
import com.yey.kindergaten.widget.PinnedSectionListView;
import com.yey.kindergaten.widget.PullToRefreshHeaderView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 老师身份的家长界面
 *
 * Created by zy on 2015/1/21.
 *
 */
public class TeacherParantFragment extends  FragmentBase implements AdapterView.OnItemClickListener, PullToRefreshHeaderView.OnHeaderRefreshListener ,BirthdayOnclickback{

    FrameLayout nokinderly;
    PinnedSectionListView  listview;
    AppContext appcontext = null;
    AccountInfo accountInfo;
    Contacts contacts;
    List<Parent>list;

    private List<Parent>listbean = null;
    private List<Items> listitem = null;
    private List<Classe>classeList = null;
    private List<SendMsgGroupItem> sendMsgGroupItems;
    private ParantAdapter adapter;
    private boolean isRefresh = true;
    @ViewInject(R.id.has_kid_no_teacher_fl)RelativeLayout noteacherRl;
    @ViewInject(R.id.no_teacher_join_btn)Button noteacherBtn;
    @ViewInject(R.id.id_load_guade_url)Button loadUrl_btn;
    @ViewInject(R.id.show_guide_image_teacherparent)ImageView guide_iv;
    @ViewInject(R.id.pull_torefresh_contact)PullToRefreshHeaderView mPullToRefreshView;
    @ViewInject(R.id.activity_contacts_main_parant_expandlv)ExpandableListView expandableListView;

    private final static  String TAG = "TeacherParantFragment";
    private final static int FROMCOMMONBROWSER_RESULT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.kinderfragmently, null);
        ViewUtils.inject(this, view);
        listview = (PinnedSectionListView) view.findViewById(R.id.activity_contacts_main_puaclistview);
        listview.setVisibility(View.GONE);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setStartAnimation(new PullToRefreshHeaderView.StartAnimationListener() {
            @Override
            public void startAnimation() {
                final View pull_iv = updateView(0);
                if (pull_iv!=null) {
                    UtilsLog.i(TAG,"StartAnimationListener 回调startAnimation--->pull_iv.startAnimation( ");
                    pull_iv.clearAnimation();
                    pull_iv.startAnimation(mPullToRefreshView.getmReverseFlipAnimation());
                }
            }
        });
        Time time = new Time();
        time.setToNow();
        mPullToRefreshView.setLastUpdated("上次更新时间 : " + time.format("%Y-%m-%d %T"));
        nokinderly = (FrameLayout) view.findViewById(R.id.teacher_framelayout);
        int cid = 0;
        appcontext = AppContext.getInstance();
        contacts = appcontext.getContacts();
        if (contacts.getClasses()!=null && contacts.getClasses().size()!=0) { // 有Kid,有班级
            cid = contacts.getClasses().get(0).getCid();
        } else {
            cid = getCidFromClasseDb();
        }
        final int finalCid = cid;
        noteacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url ;
                if (accountInfo.getKid() == 0) {
                    url = AppUtils.replaceUrlByUrl(AppConstants.TEACHER_INVITE_PARENT, accountInfo.getCid());
                } else {
                    url = AppUtils.replaceUrlByUrl(AppConstants.TEACHER_INVITE_PARENT, finalCid);
                }
                AppUtils.startWebUrlForGuide(TeacherParantFragment.this.getActivity(), url);
            }
        });
        loadUrl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url ;
                if (accountInfo.getKid() == 0) {
                    url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_NO_KID, 0);
                } else {
                    url = AppUtils.replaceUrlByUrl(AppConstants.HX_TEACHER_HAS_KID, accountInfo.getCid());
                }
                AppUtils.startWebUrlForGuide(TeacherParantFragment.this.getActivity(), url);
            }
        });
        accountInfo = AppServer.getInstance().getAccountInfo();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        accountInfo = AppServer.getInstance().getAccountInfo();
        try {
            if (DbHelper.getDB(this.getActivity()).tableIsExist(Parent.class)) {
                list = DbHelper.QueryTData("select * from Parent group by cid", Parent.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contacts.getClasses() == null) {
            try {
                List<Classe>classes = DbHelper.getDB(TeacherParantFragment.this.getActivity()).findAll(Classe.class);
                if (classes == null) {
                    classes = new ArrayList<Classe>();
                }
                contacts.setClasses(classes);
                AppContext.getInstance().setContacts(contacts);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        classeList = AppContext.getInstance().getContacts().getClasses();
        sendMsgGroupItems = parseListbean();
        adapter = new ParantAdapter(sendMsgGroupItems,this.getActivity());
        adapter.setBirthdayOnclickback(TeacherParantFragment.this);
        expandableListView.setAdapter(adapter);

        if (parseListbean().size()!=0) {
            expandableListView.expandGroup(0); // 展开第一组
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                List<SendMsgChildItem> list = adapter.grounpList.get(groupPosition).getChilditem();
                if (list == null|| list.size() == 0) {
                    showRemindDialog(adapter.grounpList.get(groupPosition).getId(), adapter.grounpList.get(groupPosition).getText());
                }
                return false;
            }
        });
        showEmptyView();
    }

    @Override
    public void onHeaderRefresh(PullToRefreshHeaderView view) {
        getParentsAndClassByKid(accountInfo.getUid(), accountInfo.getKid(), view);
    }

    /**
     * 这个方法已经作废
     *
     * Mark by lhd
     */
//    class SimpleAdapter extends ArrayAdapter<Items> implements PinnedSectionListView.PinnedSectionListAdapter {
//        private Context context;
//        public  DisplayImageOptions options = ImageLoadOptions.getContactsFriendPicOptions();
//        public SimpleAdapter(Context context, int resource, int textViewResourceId) {
//            super(context, resource, textViewResourceId);
//            this.context = context;
//            List<String>ItemName = new ArrayList<String>();
//            ItemName.add("开通家长账号");
//            if (classeList!=null && classeList.size()!=0) {
//                for (int i = 0; i < classeList.size(); i++) {
//                    ItemName.add(classeList.get(i).getCname());
//                }
//            }
//            final int sectionsNumber = ItemName.size();
//            prepareSections(sectionsNumber);
//            int sectionPosition = 0;
//            int listPosition = 0;
//            for (int i = 0; i < sectionsNumber; i++) {
//                if (i == 0) {
////                  Items section = new Items();
////                  section.setViewtype(0);
////                  section.setNickname(ItemName.get(i));
////                  section.sectionPosition = sectionPosition;
////                  section.listPosition = listPosition++;
////                  onSectionAdded(section, sectionPosition);
////                  add(section);
//                } else if (i > 0) {
//                    try {
//                        if (list!=null && list.size()!=0) {
//                            if (i<=list.size()) {
//                                listbean = DbHelper.getDB(TeacherParantFragment.this.getActivity()).findAll(Parent.class, WhereBuilder.b("cname", "=", list.get(i - 1).getCname()));
//                            }
//                        }
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    } catch (ArrayIndexOutOfBoundsException e) {
//                        e.printStackTrace();
//                    }
//                    if (listbean ==null || listbean.size() == 0) {
//                        listbean = new ArrayList<Parent>();
//                    } else {
//                        listitem = AppUtils.GetListItem(listbean);
//                    }
//                    final int itemsNumber = listbean.size();
//                    if (itemsNumber > 0) {
//                        Items section = new Items();
//                        section.setViewtype(0);
//                        section.setNickname(list.get(i - 1).getCname() + "(" + itemsNumber + "人)");
//                        section.sectionPosition = sectionPosition;
//                        section.listPosition = listPosition++;
//                        onSectionAdded(section, sectionPosition);
//                        add(section);
//                    } else {
//                        if (classeList == null || classeList.size() == 0) {
//                            return;
//                        } else {
//                            for (int n = 1; n < classeList.size() + 1; n++) {
//                                Items section = new Items();
//                                section.setViewtype(0);
//                                section.setNickname(ItemName.get(n) + "(0人)");
//                                section.sectionPosition = sectionPosition;
//                                section.listPosition = listPosition++;
//                                onSectionAdded(section, sectionPosition);
//                                add(section);
//                            }
//                        }
//                    }
//                    for (int j = 0; j < itemsNumber; j++) {
//                        Items item = listitem.get(j);
//                        item.setViewtype(1);
//                        item.sectionPosition = sectionPosition;
//                        item.listPosition = listPosition++;
//                        add(item);
//                    }
//                }
//            }
//        }
//
//        protected void prepareSections(int sectionsNumber) { }
//        protected void onSectionAdded(Items section, int sectionPosition) { }
//
//        @Override public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater ll = ((Activity) context).getLayoutInflater();
//            if (convertView == null) {
//                convertView = ll.inflate(R.layout.item, null);
//            }
//            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
//            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
//            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
//            View view = ViewHolder.get(convertView, R.id.item_view);
//            View longview = ViewHolder.get(convertView, R.id.item_longview);
//            ImageView arrowiv = ViewHolder.get(convertView, R.id.arrowimage);
//            LinearLayout title_ly = ViewHolder.get(convertView, R.id.item_titlely);
//            TextView titletv = ViewHolder.get(convertView, R.id.item_titletv);
//            Items items = getItem(position);
//            if (items!=null) {
//                if (items.getViewtype() == 0) {
//                    if (position == 0) {
////                      mian_ly.setVisibility(View.VISIBLE);
////                      nametextView.setText(items.getNickname());
////                      imageView.setImageResource(R.drawable.contact_kinderbox);
////                      view.setVisibility(View.VISIBLE);
////                      arrowiv.setVisibility(View.GONE);
////                      longview.setVisibility(View.GONE);
////                      view.setVisibility(View.GONE);
////                      longview.setVisibility(View.GONE);
////                      title_ly.setVisibility(View.GONE);
//                        mian_ly.setVisibility(View.GONE);
//                        view.setVisibility(View.GONE);
//                        longview.setVisibility(View.GONE);
//                        title_ly.setVisibility(View.VISIBLE);
//                        titletv.setText(items.getNickname());
//                    } else {
//                        mian_ly.setVisibility(View.GONE);
//                        view.setVisibility(View.GONE);
//                        longview.setVisibility(View.GONE);
//                        title_ly.setVisibility(View.VISIBLE);
//                        titletv.setText(items.getNickname());
//                    }
//                } else {
//                    mian_ly.setVisibility(View.VISIBLE);
//                    nametextView.setText(items.getNickname());
//                    ImageLoader.getInstance().displayImage(items.getAvatar(), imageView, options);
//                    view.setVisibility(View.VISIBLE);
//                    arrowiv.setVisibility(View.GONE);
//                    longview.setVisibility(View.GONE);
//                    if (items.getLines()) {
//                        if (items.getType().equals(AppConstants.CONTACTS_KINDERTEACHER)) {
//                            view.setVisibility(View.GONE);
//                            longview.setVisibility(View.GONE);
//                        } else {
//                            view.setVisibility(View.VISIBLE);
//                            longview.setVisibility(View.GONE);
//                        }
//                    } else {
//                        view.setVisibility(View.VISIBLE);
//                        longview.setVisibility(View.GONE);
//                    }
//                    title_ly.setVisibility(View.GONE);
//                }
//            }
//            return convertView;
//        }
//
//        @Override public int getViewTypeCount() {
//            return 3;
//        }
//
//        @Override public int getItemViewType(int position) {
//            return getItem(position).viewtype;
//        }
//
//        @Override
//        public boolean isItemViewTypePinned(int viewType) {
//            return viewType == 0;
//        }
//
//    }

    @Override
    public void birthdayClick(int grounpPosition, int childPosition, String toId, String toName, String birthday) {
        int cid = classeList.get(grounpPosition).getCid();
        if (cid != 0) {
            if (parseListbean().get(grounpPosition).getChilditem().get(childPosition).getBirthdaystatus() == 1) {
                UtilsLog.i(TAG, "teacher start to birthday wishes, birthday/cid/toid/toName is: " + birthday + " /" + cid + " /" + toId + " /" + toName);
                startWebUrlForBirthday(cid, toId, toName, birthday, "TeacherParantFragment", grounpPosition, childPosition);
            } else if (parseListbean().get(grounpPosition).getChilditem().get(childPosition).getBirthdaystatus() == 2) {
                new PhotoDialog(getActivity(), "我们已给" + toName + "小朋友\n发送过生日祝福啦", AppConstants.DIALOG_TYPE_BIRTHDAY).show();
                // Toast.makeText(TeacherParantFragment.this.getActivity(),"已发送生日祝福",Toast.LENGTH_SHORT).show();
            } else {
                UtilsLog.i(TAG, "the birthdaystatus is 0 or other, birthdaystatus :" + parseListbean().get(grounpPosition).getChilditem().get(childPosition).getBirthdaystatus());
                return;
            }
        } else {
            UtilsLog.i(TAG, "start to send birthday, but cid is 0,so return");
            return;
        }
    }

    /**
     *专门为生日祝福打开url
     * @param cid
     */
    private void startWebUrlForBirthday(int cid, String toid, String toName, String birthday,
                                        String birthdayfrom, int grounpPosition, int childPosition) {
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
        noticebundle.putInt("grounpPosition", grounpPosition);
        noticebundle.putInt("childPosition", childPosition);
        noticebundle.putInt("cidBirthday", cid);
        noticebundle.putString("toidBirthday", toid);

        noticebundle.putString(AppConstants.INTENT_NAME, "生日祝福");
        intent.putExtras(noticebundle);
        //startActivityForResult(intent, FROMCOMMONBROWSER_RESULT);
        startActivityForResult(intent, FROMCOMMONBROWSER_RESULT);
    }

    private void showRemindDialog(final String cid,String cname) {
        showDialog("提示", "去邀请",  cname + "暂时没有小朋友，去邀请家长加入吧。", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AppUtils.startWebUrlForGuide(TeacherParantFragment.this.getActivity(), AppUtils.replaceUrlByUrl(76, Integer.valueOf(cid)));
            }
        });
    }

    private void refreshFragment() {
        contacts = appcontext.getContacts();
        classeList = AppContext.getInstance().getContacts().getClasses();
        if (contacts.getClasses() == null) {
            try {
                List<Classe>classes = DbHelper.getDB(TeacherParantFragment.this.getActivity()).findAll(Classe.class);
                if (classes == null) {
                    classes = new ArrayList<Classe>();
                }
                contacts.setClasses(classes);
                AppContext.getInstance().setContacts(contacts);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        showEmptyView();
        if (classeList!=null) {
            adapter.SetData(parseListbean());
        }
    }

    private List<SendMsgGroupItem> parseListbean() {
        List<SendMsgGroupItem>group = new ArrayList<SendMsgGroupItem>();
        List<SendMsgChildItem> childlist = null;
        SendMsgChildItem childItem = null;
        SendMsgGroupItem groupItem = null;
        for (int i = 0; i < classeList.size(); i++) {
            try {
                childlist = new ArrayList<SendMsgChildItem>();
                childlist.clear();
                listbean = DbHelper.getDB(this.getActivity()).findAll(Parent.class, WhereBuilder.b("cname", "=", classeList.get(i).getCname()));
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (listbean == null) {
                listbean = new ArrayList<Parent>();
            }
            for (int li = 0; li < listbean.size(); li++) {
                childItem = new SendMsgChildItem(listbean.get(li).getUid() + "", listbean.get(li).getPhone(),
                        listbean.get(li).getRealname(), listbean.get(li).getAvatar(),
                        listbean.get(li).getBirthday(), listbean.get(li).getBirthdaystatus());
                childlist.add(childItem);
            }
            groupItem = new SendMsgGroupItem(classeList.get(i).getCid() + "", classeList.get(i).getCname(), childlist == null ? new ArrayList<SendMsgChildItem>() : childlist);
            group.add(groupItem);
        }
        if (group == null) {
            return new ArrayList<SendMsgGroupItem>();
        } else {
            return group;
        }
    }

    private void showEmptyView() {
        if (accountInfo.getKid() == 0) {
            expandableListView.setVisibility(View.GONE);
            nokinderly.setVisibility(View.VISIBLE);
            AppUtils.setBackground(getActivity(), guide_iv, R.drawable.teacher_no_kid);
        } else {
            if (contacts == null) {
                return;
            }
            int childrenCount = 0; // 所有班级小朋友总数
            if (contacts.getClasses()!=null) {
                for (int i = 0; i < contacts.getClasses().size(); i++) {
                    childrenCount = childrenCount + contacts.getClasses().get(i).getChildrencount();
                }
            }
            if (contacts.getClasses() == null || contacts.getClasses().size() == 0) { // 有Kid没有班级
                expandableListView.setVisibility(View.GONE);
                nokinderly.setVisibility(View.VISIBLE);
                AppUtils.setBackground(getActivity(), guide_iv, R.drawable.teacher_has_kid);
            } else {
                if (childrenCount == 0) { // 有班级但没有小朋友
                    expandableListView.setVisibility(View.GONE);
                    nokinderly.setVisibility(View.GONE);
                    noteacherRl.setVisibility(View.VISIBLE);
                } else if (childrenCount > 0) {
                    expandableListView.setVisibility(View.VISIBLE);
                    nokinderly.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        accountInfo = AppServer.getInstance().getAccountInfo();
        // refreshFragment();
    }

    /**
     * 老师刷新通讯录
     *
     * @param uid
     * @param kid
     */
    private void getParentsAndClassByKid(int uid, int kid, final PullToRefreshHeaderView view) {
        AppServer.getInstance().getParentsByTeacherKid(uid, kid, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                view.onHeaderRefreshComplete();
                if (code == AppServer.REQUEST_SUCCESS) {
                    List<Parent>list = (List<Parent>) obj;
                    if (list!=null) {
                        try {
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Parent.class);
                            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        refreshFragment();
                    }
                } else {
                    ShowToast("获取失败");
                }
            }
        });
    }

    int getCidFromClasseDb() {
        int cid = 0;
        try {
            List<Classe> list = DbHelper.getDB(AppContext.getInstance()).findAll(Classe.class);
            if (list!=null && list.size()!=0) {
                cid = list.get(0).getCid();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cid;
    }

    /**
     * 获取item中的view
     *
     * @param itemIndex
     * @return ImageView(getActivity())
     */
    public View updateView(int itemIndex) {
        // 得到第一个可显示控件的位置，
        int visiblePosition = expandableListView.getFirstVisiblePosition();
        // 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition >= 0) {
            // 得到要更新的item的view
            View view = expandableListView.getChildAt(itemIndex - visiblePosition);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent;
        Bundle bundle = new Bundle();
        Items item = (Items) listview.getAdapter().getItem(position);
        switch (item.getViewtype()) {
            case 0:
                /* Bundle bundles= new Bundle();
                bundles.putString(AppConstants.BUNDLE_INVITE, AppConstants.INVITEPARENT);
                startAnimActivity(Invite_add_Activity.class, bundles); */
                break;
            default:
                if (item.getId() == accountInfo.getUid()) {
                    intent = new Intent(getActivity(), MeInfoActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(),ContactFriendDatacardActivity.class);
                    bundle.putString("state", AppConstants.CONTACTS_PARENT);
                    bundle.putInt("role", 2);
                    bundle.putInt("targetid", item.getId());
                    intent.putExtras(bundle);
                    UtilsLog.i(TAG, "is not my holp");
                    startActivity(intent);
                }
                break;
        }
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.TEACHERFRFRAGMENT_RELOADDATA) {
            refreshFragment();
        } else if (event.getType() == AppEvent.TEACHERPARANTFRAGMENT_BIRTHDAY) {
            UtilsLog.i(TAG,"has sended birthday, start to refreshFragment");
            refreshFragment();
        }
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

        private List<SendMsgGroupItem> grounpList;
        private Context context;
        private LayoutInflater inflater;
        BirthdayOnclickback birthdayOnclickback;

        public BirthdayOnclickback getBrithdayOnclickback() {
            return birthdayOnclickback;
        }

        public void setBirthdayOnclickback(BirthdayOnclickback birthdayOnclickback) {
            this.birthdayOnclickback = birthdayOnclickback;
        }

        private void  SetData(List<SendMsgGroupItem>classeList){
            this.grounpList=classeList;
            notifyDataSetChanged();
        }

        private List<SendMsgGroupItem> getChildrenList(){
            return grounpList;
        }

        ParantAdapter(List<SendMsgGroupItem> grounpList, Context context) {
            this.grounpList = grounpList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return grounpList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return grounpList.get(i).getChilditem().size();
        }

        @Override
        public Object getGroup(int i) {
            return grounpList.get(i);
        }

        @Override
        public Object getChild(int groupPostion, int childPosition) {
            return grounpList.get(childPosition);
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
//              holder. pull_iv.setVisibility(View.VISIBLE);
//          } else {
//              holder. pull_iv.setVisibility(View.GONE);
//          }
            SendMsgGroupItem classe = grounpList.get(groupPositoin);
            holder.mian_ly.setVisibility(View.VISIBLE);
            holder.nametextView.setText(classe.getText() + " (" + grounpList.get(groupPositoin).getChilditem().size() + "人" + ") ");
            holder.view.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.arrowiv.setVisibility(View.GONE);
            holder.titletv.setText(grounpList.get(groupPositoin).getChilditem().size() + "人");

            return convertView;
        }

        /**
         * 生日点击
         *
         * @param toId
         * @param toName
         * @param birthday
         */
        public void SetBirthdayClick(final View view, final int grounpPosition, final int childPosition, final String toId, final String toName, final String birthday) {
            if (birthdayOnclickback!= null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        birthdayOnclickback.birthdayClick(grounpPosition, childPosition, toId, toName, birthday);
                    }
                });
            }
        }

        @Override
        public View getChildView( final int grounpPosition,  final int childPosition, boolean lsLastChild, View convertview, ViewGroup viewGroup) {
            if (convertview == null) {
                convertview = mInflater.inflate(R.layout.parentlistitem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertview, R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertview, R.id.contact_frienditemnametv);
            final SendMsgChildItem children = grounpList.get(grounpPosition).getChilditem().get(childPosition);

            GlideUtils.loadHeadImage(AppContext.getInstance(), children.getImg(), imageView);
//            ImageLoader.getInstance().displayImage(children.getImg(), imageView, ImageLoadOptions.getHeadOptions());
            nametextView.setText(children.getTextName());
            LinearLayout content = ViewHolder.get(convertview, R.id.show_realname_ll);
            View itemview = ViewHolder.get(convertview, R.id.item_view);
            RelativeLayout birthday_rl = ViewHolder.get(convertview, R.id.birthday_rl);
            TextView birthday_tv = ViewHolder.get(convertview, R.id.birthday_tv);
            content.setVisibility(View.VISIBLE);
            itemview.setVisibility(View.VISIBLE);
            // final String birthday = grounpList.get(grounpPosition).getChilditem().get(childPosition).getBirthday();
            final String birthday = children.getBirthday();
            final int birthdaystatus = children.getBirthdaystatus();

            // UtilsLog.i(TAG,"birthday is :" + birthday + "");
            // final String childrenTextPhone = children.getTextPhone();
            // final String childrenImg = children.getImg();

            // final String toId = grounpList.get(grounpPosition).getChilditem().get(childPosition).getId();
            final String toId = children.getId();
            // final String name = grounpList.get(grounpPosition).getChilditem().get(childPosition).getTextName();
            final String toName = children.getTextName();
//          if (birthday!=null && birthday.length() == 10) {
//              if (TimeUtil.getBirthday(birthday)) {
//                  // birthday_iv.setText(birthday);
//                  birthday_rl.setVisibility(View.VISIBLE);
//                  UtilsLog.i(TAG,"birthday is VISIBLE:" + birthday + toName +"");
//              } else {
//                  birthday_rl.setVisibility(View.GONE);
//                  UtilsLog.i(TAG,"birthday is GONE:" + birthday + toName + "");
//              }
//          } else {
//              UtilsLog.i(TAG,"birthday is null or length low 10 :" + birthday + toName + "");
//              birthday_rl.setVisibility(View.GONE);
//          }
            if (birthdaystatus == 0) {
                birthday_rl.setVisibility(View.GONE);
            } else if (birthdaystatus == 1) {
                birthday_tv.setBackgroundResource(R.drawable.contact_birthday_icon);
                birthday_rl.setVisibility(View.VISIBLE);
            } else {
                birthday_tv.setBackgroundResource(R.drawable.contact_hassendbirthday_icon);
                birthday_rl.setVisibility(View.VISIBLE);
            }

            SetBirthdayClick(birthday_rl, grounpPosition, childPosition,toId, toName, birthday);

            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherParantFragment.this.getActivity(), ContactFriendDatacardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("state", AppConstants.CONTACTS_PARENT);
                    bundle.putInt("role", 2);
                    bundle.putInt("targetid", Integer.valueOf(children.getId()));
                    bundle.putString("birthday", birthday);
                    UtilsLog.i(TAG, "put putStringExtras birthday :" + birthday);
                    //bundle.putString("childrenTextPhone",childrenTextPhone );
                    //bundle.putString("childrenImg",childrenImg );
                    //bundle.putString("childrenname",toName );
                    bundle.putInt(AppConstants.PARAM_CID, Integer.valueOf(grounpList.get(grounpPosition).getId()));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return convertview;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return true;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
