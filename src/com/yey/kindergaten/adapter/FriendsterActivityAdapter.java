package com.yey.kindergaten.adapter;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ClassPhotoDetialManager;
import com.yey.kindergaten.activity.CommonBrowser;
import com.yey.kindergaten.activity.PhotoManager_ViewPager;
import com.yey.kindergaten.activity.ServiceFriendsterActivity;
import com.yey.kindergaten.activity.ServiceItemShowActivity;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.FaceText;
import com.yey.kindergaten.bean.GroupTwritte;
import com.yey.kindergaten.bean.GroupTwritte.likers;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.EmoticonsEditText;

import java.util.ArrayList;
import java.util.List;

public class FriendsterActivityAdapter extends BaseListAdapter<GroupTwritte>{
    private Context mContext;
    private String spanString ;
    private FriendsterActivityItemAdapter adapter;
    int texesize = 0;
    int editaction;
    int gtype;
    private StarRun starrun;
    private ShowComment showcomment;
    private Disscuss discuss;
    private PopupWindow popWindow;
    private Handler handler = new Handler();
    public ShowComment getShowcomment() {
        return showcomment;
    }
    private String type = "friendster";
    private String zan;
    private DisplayImageOptions imageOptions;
    private List<likers> likersList = new ArrayList<likers>();
    private final static String TAG = "FriendsterActivityAdapter";
    List<FaceText> emos = null;

    public StarRun getStarrun() {
        return starrun;
    }
    public void setStarrun(StarRun starrun) {
        this.starrun = starrun;
    }
    public void setShowcomment(ShowComment showcomment) {
        this.showcomment = showcomment;
    }
    public void setDisscuss(Disscuss discuss) {
        this.discuss = discuss;
    }

    public FriendsterActivityAdapter(Context context, List<GroupTwritte> obList, int gtype, DisplayImageOptions imageOptions) {
        super(context, obList);
        this.mContext = context;
        this.gtype = gtype;
        this.imageOptions = imageOptions;
    }

    @Override
    public View bindView(final int position, View convertView, final ViewGroup parent) {
        Log.i("getViewposition", parent.getChildCount() + "");
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_service_friendster_item, null);
        }
        CircleImageView iv_head = ViewHolder.get(convertView, R.id.iv_activity_service_friendster_item);    // 头像
        TextView tv_name = ViewHolder.get(convertView, R.id.tv_activity_service_friendster_item_name);      // 姓名
        LinearLayout lly_discuss = ViewHolder.get(convertView, R.id.ll_activity_service_friendster_item_discuss);// 动态文字布局
        TextView tv_discuss = ViewHolder.get(convertView, R.id.tv_activity_service_friendster_item_discuss);// 动态文字
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_activity_service_friendster_item_time);      // 动态时间
        TextView tv_agin = ViewHolder.get(convertView, R.id.tv_activity_service_friendster_agin);           // 重发按钮
        ListView lv = ViewHolder.get(convertView, R.id.lv_activity_service_friendster_item);                // 评论列表
        View firendster_comment_line = ViewHolder.get(convertView, R.id.firendster_comment_line);           // 分割线
        RelativeLayout rl_icon_triangle = ViewHolder.get(convertView, R.id.rl_icon_triangle);               // 三角
        GridView gv = ViewHolder.get(convertView, R.id.gv_activity_service_friendster_item);                // 动态相片
        TextView iv_delete = ViewHolder.get(convertView, R.id.ivbtn_activity_service_friendster_delete);    // 删除按钮
        final TextView tv_zan = ViewHolder.get(convertView, R.id.tv_actvity_service_friendster_zan);        // 点赞信息
        final CheckBox cb_zan = ViewHolder.get(convertView, R.id.ivbtn_activity_service_friendster_zan);
        final RelativeLayout rl_zan = ViewHolder.get(convertView, R.id.rl_friendster_zan);                  // 点赞后信息布局
        final TextView tv_zan_false = ViewHolder.get(convertView, R.id.tv_activity_service_friendster_item);// 点赞按钮
        LinearLayout ll_zan = ViewHolder.get(convertView, R.id.ll_friendster_zan);                          // 点赞按钮布局
        LinearLayout ll_discuss = ViewHolder.get(convertView, R.id.ll_friendster_discuss);                  // 评论按钮布局
        LinearLayout ll_photonum = ViewHolder.get(convertView, R.id.ll_activity_service_friendster_item);   // 照片数量布局
        TextView tv_photonum = ViewHolder.get(convertView, R.id.tv_friendster_photonum);                    // 照片数量
        TextView tv_more = ViewHolder.get(convertView, R.id.tv_friendster_photonum_more);                   // 查看更多照片按钮

        if (getList().get(position).getUid() == -1) {
            ll_zan.setVisibility(View.GONE);
            ll_discuss.setVisibility(View.GONE);
        } else {
            ll_zan.setVisibility(View.VISIBLE);
            ll_discuss.setVisibility(View.VISIBLE);
        }

        tv_name.setText(getList().get(position).getRealname());

        GlideUtils.loadImage(mContext, getList().get(position).getAvatar(), iv_head);

//        Glide.with(mContext)
//                .load(getList().get(position).getAvatar())
//                .centerCrop()
//                .placeholder(R.drawable.icon_image_loading_default)
//                .crossFade()
//                .into(iv_head);
//        imageLoader.displayImage(getList().get(position).getAvatar(), iv_head, imageOptions);
        SpannableString spannableString = FaceTextUtils.toSpannableString(this.mContext, getList().get(position).getContent());
        if (spannableString == null || spannableString.equals("")) {
            lly_discuss.setVisibility(View.GONE);
        } else {
            lly_discuss.setVisibility(View.VISIBLE);
            tv_discuss.setText(spannableString);
        }
        tv_time.setText(TimeUtil.getChatTime(getList().get(position).getDate()));
        if (getList().get(position).getLikers()!=null && getList().get(position).getLikers().length!=0) {
            rl_zan.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < getList().get(position).getLikers().length; i++) {
                if (i!=getList().get(position).getLikers().length - 1) {
                    sb.append(getList().get(position).getLikers()[i].getRealname());
                    sb.append(",");
                } else {
                    sb.append(getList().get(position).getLikers()[i].getRealname());
                }
            }
            tv_zan.setText(sb.toString());
            if (sb.toString()!=null && (sb.toString()).contains(AppServer.getInstance().getAccountInfo().getRealname())) {
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
        if (getList().get(position).getImgs()!=null && !getList().get(position).getImgs().equals("")) {
            gv.setVisibility(View.VISIBLE);
            String[] headvision;
            if (getList().get(position).getImgs().contains("$")) {
                headvision = getList().get(position).getImgs().split("[$]");
            } else {
                headvision = getList().get(position).getImgs().split(",");
            }
            FriendsterGridviewAdapter madapter = new FriendsterGridviewAdapter(headvision, mContext, type, ImageLoadOptions.getFriendDataOptions());
            gv.setAdapter(madapter);
            if (headvision.length > 9) {
                ll_photonum.setVisibility(View.VISIBLE);
                tv_photonum.setText("共" + headvision.length + "张");
                tv_more.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, ServiceItemShowActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("groupTwritte", list.get(position));
                        i.putExtras(bundle);
                        mContext.startActivity(i);
                    }
                });
            } else {
                ll_photonum.setVisibility(View.GONE);
            }
        } else {
            gv.setVisibility(View.GONE);
            ll_photonum.setVisibility(View.GONE);
        }
        if (getList().get(position).getComment()!=null) {
            lv.setVisibility(View.VISIBLE);
//            firendster_comment_line.setVisibility(View.VISIBLE);
            adapter = new FriendsterActivityItemAdapter(mContext, getList().get(position).getComment());
            lv.setAdapter(adapter);
        } else {
//            firendster_comment_line.setVisibility(View.GONE);
            lv.setVisibility(View.GONE);
        }

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int itemposition, long id) {
                if (AppServer.getInstance().getAccountInfo().getUid()!=getList().get(position).getComment()[itemposition].getUid()) {
                    discuss.discuss(position,itemposition);
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int itemposition, long id) {
                if (getList().get(position).getUid() == AppServer.getInstance().getAccountInfo().getUid()
                        || getList().get(position).getComment()[itemposition].getUid() == AppServer.getInstance().getAccountInfo().getUid()) {
                    showDialog("友情提示：", "确认删除这条评论？", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            editaction = getList().get(position).getComment()[itemposition].getCmtid();
                            DbHelper.deletefriendstercomment(editaction);
                            ServiceFriendsterActivity.ShowData(false);
                            AppServer.getInstance().delDiscuss(getList().get(position).getUid(), editaction, new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {
                                    if (code == AppServer.REQUEST_SUCCESS) {
//                                        Toast.makeText(mContext, "评论删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                return true;
            }
        });
        emos = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 1; ++i) {
        }

        iv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("友情提示：", "确认删除这条动态？", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        editaction = getList().get(position).getTwrid();
                        DbHelper.deletefriendster(editaction);
                        ServiceFriendsterActivity.ShowData(false);
                        AppServer.getInstance().delTwitter(AppServer.getInstance().getAccountInfo().getUid(), editaction, new OnAppRequestListener() {
                            @Override
                            public void onAppRequest(int code, String message, Object obj) {
                                if (code == AppServer.REQUEST_SUCCESS) {
//                                    Toast.makeText(mContext, "动态删除成功" + editaction + " position:" + position, Toast.LENGTH_SHORT).show();
                                } else {
//                                    Toast.makeText(mContext, "动态删除失败" + editaction + " position:" + position, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        ll_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsLog.i(TAG, "1 : ischecked :" + cb_zan.isChecked());
                cb_zan.setChecked(!cb_zan.isChecked());
                UtilsLog.i(TAG, "2 : ischecked :" + cb_zan.isChecked());
                likersList.clear();
                if (cb_zan.isChecked()) {
                    UtilsLog.i(TAG,"3 : ischecked :" + cb_zan.isChecked());
                    tv_zan_false.setText("取消");
                    likers likeres = new likers();
                    likeres.setUid(AppServer.getInstance().getAccountInfo().getUid());
                    likeres.setRealname(AppServer.getInstance().getAccountInfo().getRealname());
                    likeres.setTwrid(getList().get(position).getTwrid());
                    likeres.setLikeid(-1);
                    likersList.add(likeres);
                    if (getList().get(position).getLikers()!=null && getList().get(position).getLikers().length!=0) {
                        for (int i = 0; i < getList().get(position).getLikers().length; i++) {
                            likersList.add(getList().get(position).getLikers()[i]);
                        }
                    }
                    likers[] likerlike = (likers[])likersList.toArray(new likers[likersList.size()]);
                    getList().get(position).setLikers(likerlike);
                    try {
                        DbHelper.getDB(AppContext.getInstance()).save(likeres);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    UtilsLog.i(TAG, "4 : ischecked :" + cb_zan.isChecked());
                    tv_zan_false.setText("赞");
                    for (int i = 0; i < getList().get(position).getLikers().length; i++) {
                        // 把自己从likelist中剔除
                        if (!getList().get(position).getLikers()[i].getRealname().contains(AppServer.getInstance().getAccountInfo().getRealname())) {
                            likersList.add(getList().get(position).getLikers()[i]);
                        }
                    }
                    if (likersList.size() > 0) {
                        likers[] likerlike = likersList.toArray(new likers[likersList.size()]);
                        getList().get(position).setLikers(likerlike);
                    } else {
                        getList().get(position).setLikers(null);
                    }
                    try {
                        DbHelper.getDB(AppContext.getInstance()).delete(likers.class, WhereBuilder.b("twrid", "=", (getList().get(position).getTwrid())).and("realname", "=", AppServer.getInstance().getAccountInfo().getRealname()));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    DbHelper.getDB(mContext).update(getList().get(position), WhereBuilder.b("twrid", "=", (getList().get(position).getTwrid())));
                } catch (DbException e) {
                    e.printStackTrace();
                    UtilsLog.i(TAG,"update fail because DbException:" + e.getMessage() + "/" + e.getCause());
                }
                notifyDataSetChanged();
                UtilsLog.i(TAG,"6 : ischecked :" + cb_zan.isChecked());
                AppServer.getInstance().setZan(AppServer.getInstance().getAccountInfo().getUid(), AppServer.getInstance().getAccountInfo().getRealname(), getList().get(position).getTwrid(), new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
                            UtilsLog.i(TAG, "setZan success");
                        } else {
                            ShowToast("网络异常");
                        }
                    }
                });
            }
        });

        iv_head.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> paths = new ArrayList<String>();
                ArrayList<String> titles = new ArrayList<String>();
                if (getList().get(position).getAvatar() == null || getList().get(position).getAvatar().equals("")) {
                    return;
                } else {
                    paths.add(getList().get(position).getAvatar());
                    titles.add("");
                    Intent intent = new Intent(mContext, PhotoManager_ViewPager.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", AppConstants.PARAM_FRIENDSTER_HEAD);
                    // path list
                    bundle.putStringArrayList("imglist", paths);
                    // title list
                    bundle.putStringArrayList("decslist", titles);
                    bundle.putInt("position", 0);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });
//        iv_head.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent;
//                if (getList().get(position).getUid() == AppContext.getInstance().getAccountInfo().getUid()) {
//                    intent=new Intent(mContext, MeInfoActivity.class);
//                    mContext.startActivity(intent);
//                } else {
//                    intent = new Intent(mContext, ContactFriendDatacardActivity.class);
//                    intent.putExtra("role", getList().get(position).getType());
//                    intent.putExtra("targetid", getList().get(position).getUid());
//                    intent.putExtra("state", "0");
//                    mContext.startActivity(intent);
//                }
//            }
//
//        });
        final View commonview = convertView;
        ll_discuss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showcomment.showcomment(position);
            }
        });
        tv_agin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imglist = new ArrayList<String>();
                String[] img = getList().get(position).getImgs().split(",");
                String content = getList().get(position).getContent();
                for (int j = 0; j < img.length; j++) {
                    imglist.add(img[j]);
                }
                starrun.starrun(imglist,content);
            }
        });
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int imgposition, long id) {
                String albumidStr = getList().get(position).getAlbumid();
                int albumid = -1;
                try {
                    albumid = Integer.parseInt(getList().get(position).getAlbumid());
                } catch (Exception e) {
                    UtilsLog.i(TAG, "albumid parseint exception");
                }
                if (getList().get(position).getFtype() == 1 && albumid!=-1) {
                    String albumName = "班级相册";
                    String content = getList().get(position).getContent();
                    if (!content.isEmpty() && content.contains("在") && content.contains("相册")) {
                        int beginIndex = content.indexOf("在") + 1;
                        int endIndex = content.indexOf("相册");
                        if (beginIndex < endIndex) {
                            albumName = content.substring(beginIndex, endIndex);
                        }
                    }
                    Album album = new Album(albumid + "", albumName, "", "", 0, "", "");
                    Intent classPhotoIntent = new Intent(mContext, ClassPhotoDetialManager.class);
                    classPhotoIntent.putExtra(AppConstants.INTENT_ALBUM_TYPE, AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                    classPhotoIntent.putExtra(AppConstants.PARAM_ALBUM, album);
                    mContext.startActivity(classPhotoIntent);
                } else {
                    if (getList().get(position).getImgs().contains(",")) {
                        String[] imglist = getList().get(position).getImgs().split(",");
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < imglist.length; i++) {
                            list.add(imglist[i]);
                        }
                        Intent i = new Intent(mContext, PhotoManager_ViewPager.class);
//                  i.putStringArrayListExtra("imglist", list);
                        Bundle bundler = new Bundle();
                        bundler.putStringArrayList("imglist", list);
                        bundler.putString("type", AppConstants.PARAM_UPLOAD_STER);
                        bundler.putInt("position", imgposition);
                        i.putExtras(bundler);
                        mContext.startActivity(i);
                    } else {
                        String imglist = list.get(position).getImgs();
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(imglist);
                        Intent i = new Intent(mContext, PhotoManager_ViewPager.class);
//                  i.putStringArrayListExtra("imglist", list);
                        Bundle bundler = new Bundle();
                        bundler.putStringArrayList("imglist", list);
                        bundler.putString("type", AppConstants.PARAM_UPLOAD_STER);
                        i.putExtras(bundler);
                        mContext.startActivity(i);
                    }
                }
            }
        });

        tv_discuss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getList().get(position).getFtype() == 2 || getList().get(position).getFtype() == 3) {
                    Intent i = new Intent(mContext, CommonBrowser.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.INTENT_URL, getList().get(position).getImgs());
                    bundle.putString(AppConstants.INTENT_NAME, getList().get(position).getContent());
                    i.putExtras(bundle);
                    mContext.startActivity(i);
                }
            }
        });

        if (getList().get(position).getUid() == AppServer.getInstance().getAccountInfo().getUid()) {
            iv_delete.setVisibility(View.VISIBLE);
        } else {
            iv_delete.setVisibility(View.GONE);
        }

        if (getList().get(position).getStatus() == 1) {
            tv_agin.setVisibility(View.VISIBLE);
        } else {
            tv_agin.setVisibility(View.GONE);
        }

        if (getList().get(position).getFtype() == 1) {
            ll_discuss.setVisibility(View.GONE);
            ll_zan.setVisibility(View.VISIBLE);
            lly_discuss.setBackgroundResource(R.color.white);
            tv_discuss.setTextColor(mContext.getResources().getColor(R.color.black));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tv_discuss.getLayoutParams();
            layoutParams.topMargin = 0;
            layoutParams.bottomMargin = 0; // 将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;
            tv_discuss.setLayoutParams(layoutParams);

        } else if (getList().get(position).getFtype() == 2 || getList().get(position).getFtype() == 3) {
            gv.setVisibility(View.GONE);
            lly_discuss.setBackgroundResource(R.drawable.friendster_discuss_bg);
            tv_discuss.setTextColor(mContext.getResources().getColor(R.color.discuss_photo));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tv_discuss.getLayoutParams();
            layoutParams.topMargin = 20;
            layoutParams.bottomMargin = 20; // 将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
            layoutParams.leftMargin = 20;
            layoutParams.rightMargin = 20;
            tv_discuss.setLayoutParams(layoutParams);

            rl_zan.setVisibility(View.GONE);
            ll_discuss.setVisibility(View.GONE);
        } else {
            lly_discuss.setBackgroundResource(R.color.white);
            tv_discuss.setTextColor(mContext.getResources().getColor(R.color.black));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tv_discuss.getLayoutParams();
            layoutParams.topMargin = 0;
            layoutParams.bottomMargin = 0; // 将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;
            tv_discuss.setLayoutParams(layoutParams);

            rl_zan.setVisibility(View.VISIBLE);
            ll_discuss.setVisibility(View.VISIBLE);
        }
        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            if (AppServer.getInstance().getAccountInfo().getRights() != null
                   && AppServer.getInstance().getAccountInfo().getRights().contains("112") && getList().size() > 0) {
                ll_discuss.setVisibility(View.GONE);
            } else {
                ll_discuss.setVisibility(View.VISIBLE);
            }
        } else {
            if (getList().size() == 1 && getList().get(position).getStatus() == 3 && getList().get(position).getTwrid() == -1) {
                ll_discuss.setVisibility(View.GONE);
            } else {
                ll_discuss.setVisibility(View.VISIBLE);
            }
        }

        if (getList().get(position).getLikers()!=null && getList().get(position).getLikers().length!=0) {
            rl_zan.setVisibility(View.VISIBLE);
        } else {
            rl_zan.setVisibility(View.GONE);
        }

        if (rl_zan.getVisibility() == View.VISIBLE || lv.getVisibility() == View.VISIBLE) {
            rl_icon_triangle.setVisibility(View.VISIBLE);
        } else {
            rl_icon_triangle.setVisibility(View.GONE);
        }
        if (rl_zan.getVisibility() == View.VISIBLE && lv.getVisibility() == View.VISIBLE) {
            firendster_comment_line.setVisibility(View.VISIBLE);
        } else {
            firendster_comment_line.setVisibility(View.GONE);
        }

        return convertView;
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

    public interface StarRun {
        public void starrun(ArrayList<String> imglist, String content);
    }

    public interface ShowComment {
        public void showcomment(int position);
    }

    public interface Disscuss {
        public void discuss(int position, int downposition);
    }
    private View getGridView(final int i, final EmoticonsEditText et) {
        View view = View.inflate(AppContext.getInstance(), R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(1, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(this.mContext, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (et != null && !TextUtils.isEmpty(key)) {
                        int start = et.getSelectionStart();
                        CharSequence content = et.getText().insert(start, key);
                        et.setText(content);
                        CharSequence info = et.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
        return view;
    }

    public void showDialog(String title, String message, String buttonText, DialogInterface.OnClickListener onSuccessListener) {
        DialogTips dialog = new DialogTips(mContext, title, message, buttonText, true, true);
        // 设置成功事件
        dialog.SetOnSuccessListener(onSuccessListener);
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    private void showPopup(View parent) {
        if (popWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.dialog_friendster, null);
            // 创建一个PopuWidow对象  
            popWindow = new PopupWindow(view, LinearLayout.LayoutParams.FILL_PARENT, 100, true);
        }
        // popupwindow弹出时的动画 popWindow.setAnimationStyle(R.style.popupWindowAnimation);
        // 使其聚集，要想监听菜单里控件的事件就必须要调用此方法
        popWindow.setFocusable(true);
        // 设置允许在外点击消失  
        popWindow.setOutsideTouchable(false);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        // 软键盘不会挡着popupwindow
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置菜单显示的位置
        popWindow.showAsDropDown(parent);
        // 监听菜单的关闭事件
        popWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        // 监听触屏事件
        popWindow.setTouchInterceptor(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return false;
            }
        });
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

}

