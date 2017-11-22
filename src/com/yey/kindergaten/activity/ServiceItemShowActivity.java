package com.yey.kindergaten.activity;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.EmoViewPagerAdapter;
import com.yey.kindergaten.adapter.EmoteAdapter;
import com.yey.kindergaten.adapter.FriendsterActivityItemAdapter;
import com.yey.kindergaten.adapter.FriendsterGridviewAdapter;
import com.yey.kindergaten.bean.FaceText;
import com.yey.kindergaten.bean.GroupTwritte;
import com.yey.kindergaten.bean.GroupTwritte.likers;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.EmoticonsEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cm_pc2 on 2015/3/26.
 */
public class ServiceItemShowActivity extends BaseActivity{

    private CircleImageView iv_head;
    private TextView tv_name;
    private TextView tv_discuss;
    private TextView tv_time;
    private ListView lv;
    private GridView gv;
    private ImageButton iv_discuss;
    private TextView iv_delete;
    private TextView tv_zan;
    private CheckBox cb_zan;
    private ImageView iv_bq;
    private LinearLayout ll_bq;
    private Button send_btn;
    private RelativeLayout rl_zan;
    private TextView tv_zan_false;
    private TextView tv_discuss_true;
    private LinearLayout ll_zan;
    private LinearLayout ll_discuss;
    private GroupTwritte groupTwritte;
    private List<FaceText> emos = null;
    private int editaction;
    private Handler handler = new Handler();
    private FriendsterActivityItemAdapter adapter;
    private String type = "itemonclik";
    private TextView tv_title;
    private ImageView iv_left;
    private LinearLayout ll_input;
    private EmoticonsEditText et;
    private LinearLayout bq_ll;
    private ViewPager vp_face;
    private int touid;
    private ImageButton btn_bq;
    private List<likers> likersList = new ArrayList<likers>();
    List<GroupTwritte.comments> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_show);
        groupTwritte = (GroupTwritte) getIntent().getSerializableExtra("groupTwritte");

        list = new ArrayList<GroupTwritte.comments>();
        if (groupTwritte.getComment()!=null) {
            for (int i = 0; i < groupTwritte.getComment().length; i++) {
                list.add(groupTwritte.getComment()[i]);
            }
        }
        intiView();
    }

    private void intiView() {
        bq_ll = (LinearLayout) findViewById(R.id.service_publishspeak_facely);
        vp_face = (ViewPager) findViewById(R.id.service_publishspeak_face);
        et = (EmoticonsEditText) findViewById(R.id.input_activity_service_friendster_item);
        ll_input = (LinearLayout) findViewById(R.id.ll_activity_service_friendster_item_input);
        iv_head = (CircleImageView) findViewById(R.id.iv_activity_service_friendster_item);
        tv_name = (TextView) findViewById(R.id.tv_activity_service_friendster_item_name);
        tv_discuss = (TextView) findViewById(R.id.tv_activity_service_friendster_item_discuss);
        tv_time = (TextView) findViewById(R.id.tv_activity_service_friendster_item_time);
        lv = (ListView) findViewById(R.id.lv_activity_service_friendster_item);
        gv = (GridView) findViewById(R.id.gv_activity_service_friendster_item);
        iv_discuss = (ImageButton) findViewById(R.id.ivbtn_activity_service_friendster_item);
        iv_delete = (TextView) findViewById(R.id.ivbtn_activity_service_friendster_delete);
        tv_zan = (TextView) findViewById(R.id.tv_actvity_service_friendster_zan);
        cb_zan = (CheckBox) findViewById(R.id.ivbtn_activity_service_friendster_zan);
        iv_bq = (ImageView) findViewById(R.id.biaoqing_activity_service_friendster_item);
        ll_bq = (LinearLayout) findViewById(R.id.service_publishspeak_facely);
        send_btn = (Button) findViewById(R.id.btn_activity_service_friendster_item);
        rl_zan = (RelativeLayout) findViewById(R.id.rl_friendster_zan);
        tv_zan_false = (TextView) findViewById(R.id.tv_activity_service_friendster_item);
        tv_discuss_true = (TextView) findViewById(R.id.tv_activity_service_friendster_discuss);
        ll_zan = (LinearLayout) findViewById(R.id.ll_friendster_zan);
        ll_discuss = (LinearLayout) findViewById(R.id.ll_friendster_discuss);
        tv_title = (TextView) findViewById(R.id.header_title);
        tv_title.setText("个人动态");
        iv_left = (ImageView) findViewById(R.id.left_btn);
        iv_left.setVisibility(View.VISIBLE);
        emos = FaceTextUtils.faceTexts;
        btn_bq = (ImageButton) findViewById(R.id.biaoqing_activity_service_friendster_item);
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 1; ++i) {
            views.add(getGridView(i,et));
        }
        vp_face.setAdapter(new EmoViewPagerAdapter(views));

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et.getText().toString().trim().equals("")) {
                    bq_ll.setVisibility(View.GONE);
                    ll_input.setVisibility(View.GONE);
                    hideSoftInput(et);
                    AppServer.getInstance().sentCommment(AppServer.getInstance().getAccountInfo().getUid(), touid, groupTwritte.getTwrid(), et.getText().toString(), new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if (code == AppServer.REQUEST_SUCCESS) {
                                GroupTwritte.comments cm = (GroupTwritte.comments) obj;
                                try {
                                    DbHelper.getDB(ServiceItemShowActivity.this).save(cm);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                et.setText("");
                                List<GroupTwritte.comments> allist = DbHelper.QueryTData("select * from comments where twrid='" + groupTwritte.getTwrid() + "'order by cmtid asc", GroupTwritte.comments.class);
                                GroupTwritte.comments[] comments = allist.toArray(new GroupTwritte.comments[allist.size()]);
                                groupTwritte.setComment(comments);
                                try {
                                    DbHelper.getDB(ServiceItemShowActivity.this).update(groupTwritte, WhereBuilder.b("twrid", "=", (groupTwritte.getTwrid())), "comments");
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                if (adapter!=null && comments!=null) {
                                    adapter.setlist(comments);
                                    lv.setSelection(comments.length);
                                }
                            }
                        }
                    });
                } else {
                    showToast("请输入评论内容");
                }
            }
        });

        btn_bq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bq_ll.isShown()) {
                    et.requestFocus();
                    bq_ll.setVisibility(View.GONE);
                    showSoftInput(et);
                } else {
                    et.requestFocus();
                    bq_ll.setVisibility(View.VISIBLE);
                    hideSoftInput(et);
                }
            }
        });

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (groupTwritte.getUid() == -1) {
            ll_zan.setVisibility(View.GONE);
            ll_discuss.setVisibility(View.GONE);
        } else {
            ll_zan.setVisibility(View.VISIBLE);
            ll_discuss.setVisibility(View.VISIBLE);
        }
        tv_name.setText(groupTwritte.getRealname());
        imageLoader.displayImage(groupTwritte.getAvatar(), iv_head, ImageLoadOptions.getFriendOptions());
        SpannableString spannableString = FaceTextUtils.toSpannableString(ServiceItemShowActivity.this,groupTwritte.getContent());
        tv_discuss.setText(spannableString);
        tv_time.setText(TimeUtil.getChatTime(groupTwritte.getDate()));
        if (groupTwritte.getLikers()!=null && groupTwritte.getLikers().length!=0) {
            rl_zan.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < groupTwritte.getLikers().length; i++) {
                if (i!=groupTwritte.getLikers().length - 1) {
                    sb.append(groupTwritte.getLikers()[i].getRealname());
                    sb.append(",");
                } else {
                    sb.append(groupTwritte.getLikers()[i].getRealname());
                }
            }
            tv_zan.setText(sb.toString());
            if (sb.toString().contains(AppServer.getInstance().getAccountInfo().getRealname())) {
                cb_zan.setChecked(true);
                tv_zan_false.setText("取消");
            } else {
                cb_zan.setChecked(false);
                tv_zan_false.setText("赞");
            }
        } else {
            rl_zan.setVisibility(View.GONE);
            cb_zan.setChecked(false);
            tv_zan_false.setText("赞");
        }
        if ((!groupTwritte.getImgs().equals("")) && groupTwritte.getImgs()!=null) {
            gv.setVisibility(View.VISIBLE);
            String[] headvision = groupTwritte.getImgs().split(",");
            FriendsterGridviewAdapter madapter = new FriendsterGridviewAdapter(headvision, this, type, ImageLoadOptions.getFriendDataOptions());
            gv.setAdapter(madapter);
        } else {
            gv.setVisibility(View.GONE);
        }
        if (groupTwritte.getComment()!=null) {
            lv.setVisibility(View.VISIBLE);
            adapter = new FriendsterActivityItemAdapter(this, groupTwritte.getComment());
            lv.setAdapter(adapter);
        } else {
            lv.setVisibility(View.GONE);
        }
        if (groupTwritte.getUid() == AppServer.getInstance().getAccountInfo().getUid()) {
            iv_delete.setVisibility(View.VISIBLE);
        } else {
            iv_delete.setVisibility(View.GONE);
        }
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("友情提示：", "删除动态", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editaction=groupTwritte.getTwrid();
                        DbHelper.deletefriendster(editaction);
                        ServiceFriendsterActivity.ShowData(false);
                        AppServer.getInstance().delTwitter(AppServer.getInstance().getAccountInfo().getUid(), editaction, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == AppServer.REQUEST_SUCCESS) {
                                        finish();
                                }
                            }
                        });
                    }
                });
            }
        });

        ll_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_zan.setChecked(!cb_zan.isChecked());
                likersList.clear();
                if (cb_zan.isChecked()) {
                    tv_zan_false.setText("取消");
                    likers likeres = new likers();
                    likeres.setUid(AppServer.getInstance().getAccountInfo().getUid());
                    likeres.setRealname(AppServer.getInstance().getAccountInfo().getRealname());
                    likeres.setTwrid(groupTwritte.getTwrid());
                    likeres.setLikeid(-1);
                    likersList.add(likeres);
                    if (groupTwritte.getLikers()!=null && groupTwritte.getLikers().length!=0) {
                        for (int i = 0; i < groupTwritte.getLikers().length; i++) {
                            likersList.add(groupTwritte.getLikers()[i]);
                        }
                    }
                    likers[] likerlike = (likers[])likersList.toArray(new likers[likersList.size()]);
                    groupTwritte.setLikers(likerlike);
                } else {
                    tv_zan_false.setText("赞");
                    for (int i = 0; i < groupTwritte.getLikers().length; i++) {
                        if (!groupTwritte.getLikers()[i].getRealname().contains(AppServer.getInstance().getAccountInfo().getRealname())) {
                            likersList.add(groupTwritte.getLikers()[i]);
                        }
                    }
                    if (likersList.size() > 0) {
                        likers[] likerlike = (likers[]) likersList.toArray(new likers[likersList.size()]);
                        groupTwritte.setLikers(likerlike);
                    } else {
                        groupTwritte.setLikers(null);
                    }
                }
                if (groupTwritte.getLikers()!=null && groupTwritte.getLikers().length!=0) {
                    rl_zan.setVisibility(View.VISIBLE);
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < groupTwritte.getLikers().length; i++) {
                        if (i!=groupTwritte.getLikers().length-1){
                            sb.append(groupTwritte.getLikers()[i].getRealname());
                            sb.append(",");
                        } else {
                            sb.append(groupTwritte.getLikers()[i].getRealname());
                        }
                    }
                    tv_zan.setText(sb.toString());
                    if (sb.toString().contains(AppServer.getInstance().getAccountInfo().getRealname())) {
                        cb_zan.setChecked(true);
                        tv_zan_false.setText("取消");
                    } else {
                        cb_zan.setChecked(false);
                        tv_zan_false.setText("赞");
                    }
                } else {
                    rl_zan.setVisibility(View.GONE);
                    cb_zan.setChecked(false);
                    tv_zan_false.setText("赞");
                }
                try {
                    DbHelper.getDB(ServiceItemShowActivity.this).update(groupTwritte, WhereBuilder.b("twrid", "=", (groupTwritte.getTwrid())), "likers");
                } catch (DbException e) {
                    e.printStackTrace();
                }
                AppServer.getInstance().setZan(AppServer.getInstance().getAccountInfo().getUid(), AppServer.getInstance().getAccountInfo().getRealname(),groupTwritte.getTwrid(),new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {

                        }
                    }
                });
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int imgposition, long id) {
                if (groupTwritte.getImgs().contains(",")) {
                    String[] imglist = groupTwritte.getImgs().split(",");
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < imglist.length; i++) {
                        list.add(imglist[i]);
                    }
                    Intent i = new Intent(ServiceItemShowActivity.this,PhotoManager_ViewPager.class);
                    Bundle bundler = new Bundle();
                    bundler.putStringArrayList("imglist", list);
                    bundler.putString("type", AppConstants.PARAM_UPLOAD_STER);
                    bundler.putInt("position", imgposition);
                    i.putExtras(bundler);
                    startActivity(i);
                } else {
                    String imglist = groupTwritte.getImgs();
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(imglist);
                    Intent i = new Intent(ServiceItemShowActivity.this, PhotoManager_ViewPager.class);
//                  i.putStringArrayListExtra("imglist", list);
                    Bundle bundler = new Bundle();
                    bundler.putStringArrayList("imglist", list);
                    bundler.putString("type", AppConstants.PARAM_UPLOAD_GROW);
                    i.putExtras(bundler);
                    startActivity(i);
                }
            }
        });

        ll_discuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setFocusable(true);
                ll_input.setVisibility(View.VISIBLE);
                et.requestFocus();
                showSoftInput(et);
                et.setHint("");
                touid = -1;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int itemposition, long id) {
                et.setFocusable(true);
                et.requestFocus();
                ll_input.setVisibility(View.VISIBLE);
                showSoftInput(et);
                et.setHint("回复" + groupTwritte.getComment()[itemposition].getRealname());
                touid=groupTwritte.getComment()[itemposition].getUid();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int itemposition, long id) {
                if (groupTwritte.getUid() == AppServer.getInstance().getAccountInfo().getUid()) {
                    showDialog("友情提示：", "删除评论", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            editaction = groupTwritte.getComment()[itemposition].getCmtid();
                            DbHelper.deletefriendstercomment(editaction);
                            ServiceFriendsterActivity.ShowData(false);

                            AppServer.getInstance().delDiscuss(groupTwritte.getUid(), editaction, new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {
                                    if (code == AppServer.REQUEST_SUCCESS) {
                                        for (int i = 0; i < list.size(); i++) {
                                            if (editaction == list.get(i).getCmtid()) {
                                                list.remove(i);
                                            }
                                        }

                                        //初始化GroupTwitte对象
                                        GroupTwritte.comments[] arr = new GroupTwritte.comments[list.size()];
                                        // 转化成数组并刷新
                                        adapter = new FriendsterActivityItemAdapter(ServiceItemShowActivity.this, list.toArray(arr));
                                        lv.setAdapter(adapter);
                                    } else {
                                        Toast.makeText(ServiceItemShowActivity.this, "删除失败" + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                return true;
            }
        });

        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            if (AppServer.getInstance().getAccountInfo().getRights() != null && AppServer.getInstance().getAccountInfo().getRights().contains("112")) {
                ll_discuss.setVisibility(View.GONE);
            } else {
                ll_discuss.setVisibility(View.VISIBLE);
            }
        }
    }

    public  void showSoftInput(EditText et) {
        InputMethodManager imm = (InputMethodManager) et.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public  void hideSoftInput(EditText et) {
        InputMethodManager imm = (InputMethodManager) et.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
    private View getGridView(final int i,final EmoticonsEditText et) {
        View view = View.inflate(AppContext.getInstance(), R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(1, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ServiceItemShowActivity.this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (et != null && !TextUtils.isEmpty(key)) {
                        int start = et.getSelectionStart();
                        CharSequence content = et.getText()
                                .insert(start, key);
                        et.setText(content);
                        CharSequence info = et.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
        return view;
    }

    public void showDialog(String title,String message,String buttonText,DialogInterface.OnClickListener onSuccessListener) {
        DialogTips dialog = new DialogTips(ServiceItemShowActivity.this,title,message, buttonText,true,true);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    private void popupInputMethodWindow() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) AppContext.getInstance().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           if (ll_input.isShown()) {
               ll_input.setVisibility(View.GONE);
               bq_ll.setVisibility(View.GONE);
           } else {
               finish();
           }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
  }
