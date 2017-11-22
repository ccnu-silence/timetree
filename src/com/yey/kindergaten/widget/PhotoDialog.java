package com.yey.kindergaten.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.RelationShipBean;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zyj
 * To change this template use File | Settings | File Templates.
 */
public class PhotoDialog extends Dialog {

    public static final int CAPTURE_REQUEST = 1;
    public static final int PICK_REQUEST = 2;
    private static  Dialog mDialog;
    private Activity mActivity; 
    private SpinnerAdapter adapter;
    private List<String>list = new ArrayList<String>();

    private  TextView title_tv;
    private Handler handler = null;
    private ListView listview;
    List<RelationShipBean> listbean;

    private boolean isLogin;
    private final static String TAG = "PhotoDialog";

    //private String[]relation_name = {"我是爸爸(已选)","我是妈妈(已选)","我是爷爷(已选)","我是奶奶(已选)","我是外公(已选)","我是外婆(已选)",
    //                                  "我是叔叔(已选)","我是阿姨(已选)"};
    public interface RelationChoosed {
        public void loginhuanxin(int relation);
    }

    public  RelationChoosed relationChoosed;

    public void setRelationChoosed(RelationChoosed relationChoosed) {
        this.relationChoosed = relationChoosed;
    }

    public PhotoDialog(Activity activity,final Handler handler) {
        super(activity, R.style.no_frame_dialog);
        this.handler = handler;
        mActivity = activity;
        this.setCancelable(true);
		this.setCanceledOnTouchOutside(true); // 外部点击无效
        this.getWindow().setGravity(Gravity.CENTER_VERTICAL);
		this.getWindow().setWindowAnimations(R.style.servicescheduledialog);	    
        this.setContentView(R.layout.ppw_sendnotice_timepicker);             
        listview = (ListView) findViewById(R.id.id_service_schedule_remind_lv);
        initdata();
        adapter = new SpinnerAdapter();
        listview.setAdapter(adapter);      
    	listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = list.get(position);
                handler.sendMessage(msg);
                PhotoDialog.this.dismiss();
			}
		});
    }

    public PhotoDialog(Activity activity) {
        super(activity,R.style.no_frame_dialog);
        mActivity = activity;
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true); // 外部点击无效
        this.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        this.getWindow().setWindowAnimations(R.style.servicescheduledialog);
        this.setContentView(R.layout.ppw_sendnotice_timepicker);
        listview = (ListView) findViewById(R.id.id_service_schedule_remind_lv);
        title_tv = (TextView) findViewById(R.id.dialog_titile_text_tv);
        initdata2();
        adapter = new SpinnerAdapter();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                PhotoDialog.this.dismiss();
                String clientid = SharedPreferencesHelper.getInstance(mActivity).getString(AppConstants.CLIENTID, "");
                final LoadingDialog dialog = new LoadingDialog(mActivity);
                AppServer.getInstance().updateDeviceId(AppServer.getInstance().getAccountInfo().getUid(), clientid, position + 1, 0, new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
                            int hxState = ((Integer)obj).intValue();
//                          AccountBean accountBean = AppServer.getInstance().getAccountBean();
//                          accountBean.setRelationship(position+1);
                            AccountInfo accountInfo = AppServer.getInstance().getAccountInfo();
                            accountInfo.setRelationship(position + 1);
                            try {
                                List<RelationShipBean> list = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
                                boolean flag = false;
                                if (list!=null && list.size()!=0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (list.get(i).getRelationship() == position + 1) {
                                            RelationShipBean bean = new RelationShipBean();
                                            bean = list.get(i);
                                            bean.setHxregtag(hxState);
                                            list.set(i,bean);
                                            flag = true;
                                        }
                                    }
                                }
                                if (!flag) {
                                    RelationShipBean bean = new RelationShipBean();
                                    bean.setHxregtag(hxState);
                                    bean.setRelationship(position + 1);
                                    bean.setDefaultrelation(position + 1);
                                    list = new ArrayList<RelationShipBean>();
                                    list.add(bean);
                                }

                                DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
                                DbHelper.getDB(AppContext.getInstance()).saveAll(list);
    //                                DbHelper.getDB(mActivity).update(accountBean, WhereBuilder.b("uid", "=", accountBean.getUid()),new String[]{"relationship"});
                                DbHelper.getDB(mActivity).update(accountInfo, WhereBuilder.b("uid", "=", accountInfo.getUid()),new String[]{"relationship"});
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            relationChoosed.loginhuanxin(position + 1);
                        } else {
                            Toast.makeText(mActivity, message + "", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
    }

    public PhotoDialog(final Context activity, final int role ){
        super(activity, R.style.no_frame_dialog);
        this.setCanceledOnTouchOutside(false); // 外部点击无效
        this.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        this.getWindow().setWindowAnimations(R.style.servicescheduledialog);
        this.setContentView(R.layout.service_click_remind);

        final AccountInfo info = AppServer.getInstance().getAccountInfo();
        TextView remind_tv = (TextView) findViewById(R.id.show_service_remind_dialog_text);
        ImageView remind_ic = (ImageView) findViewById(R.id.show_service_remind_icon);
        ImageView close_ic = (ImageView) findViewById(R.id.close_service_dialog_remind);
        Button regedit_ic = (Button) findViewById(R.id.click_to_shut_down_dialog);

        close_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   dismiss();
            }
        });
        final List<Classe>classeList = AppContext.getInstance().getContacts().getClasses();
        List<Parent>parents = null;
        try {
            parents = DbHelper.getDB(AppContext.getInstance()).findAll(Parent.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        regedit_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (role) {
                    case 0:
                        AppUtils.startWebUrlForGuide(activity, AppUtils.replaceUrlByUrl(71, 0));
                        break;
                    case 1:
                        if (info.getKid() == 0) {
                            AppUtils.startWebUrlForGuide(activity, AppUtils.replaceUrlByUrl(73, 0));
                        } else {
                            if (classeList == null || classeList.size() == 0) {
                                AppUtils.startWebUrlForGuide(activity, AppUtils.replaceUrlByUrl(72, 0));
                            }
                        }
                        break;
                }
                dismiss();
            }
        });

        switch (role) {
            case 0:
                remind_tv.setText("      此服务需要\n登记幼儿园才能使用");
                remind_ic.setImageResource(R.drawable.icon_joinkindergater);
                regedit_ic.setText("登记幼儿园");
                break;
            case 1:
                if (info.getKid() == 0) {
                    remind_tv.setText("      此服务需要\n加入幼儿园才能使用");
                    remind_ic.setImageResource(R.drawable.icon_joinkindergater);
                    regedit_ic.setText("加入幼儿园");
                } else {
                    if (classeList == null || classeList.size() == 0) {
                        remind_tv.setText("      此服务需要\n登记班级才能使用");
                        remind_ic.setImageResource(R.drawable.icon_joinkindergater);
                        regedit_ic.setText("登记班级");
                    }
                }
                break;
            case 2:
                if (info.getKid() == 0) {
                    remind_tv.setText("      此服务需要\n加入幼儿园才能使用");
                    remind_ic.setImageResource(R.drawable.icon_joinkindergater);
                    regedit_ic.setText("加入幼儿园");
                } else if (parents == null || parents.size() == 0) {
                    remind_tv.setText("      此服务需要\n加入班级才能使用");
                    remind_ic.setImageResource(R.drawable.icon_joinkindergater);
                    regedit_ic.setText("加入班级");
                    return;
                }
                break;
        }
    }

    public PhotoDialog(final Context activity, final String message, String type){
        super(activity, R.style.no_frame_dialog);
        this.setCanceledOnTouchOutside(false); // 外部点击无效
        this.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        this.getWindow().setWindowAnimations(R.style.servicescheduledialog);
//      WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
//      layoutParams.width = 200;
//      layoutParams.height = 100;
//      this.getWindow().setAttributes(layoutParams);
        this.setContentView(R.layout.service_click_remind);

        TextView remind_tv = (TextView) findViewById(R.id.show_service_remind_dialog_text);
        ImageView remind_ic = (ImageView) findViewById(R.id.show_service_remind_icon);
        ImageView close_ic = (ImageView) findViewById(R.id.close_service_dialog_remind);
        Button regedit_ic = (Button) findViewById(R.id.click_to_shut_down_dialog);
        RelativeLayout imageiv_rl = (RelativeLayout) findViewById(R.id.imageiv_rl);

        close_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        regedit_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if (message!=null) {
            remind_tv.setText(message);
        } else {
            UtilsLog.i(TAG, "message is null");
        }
        if (type!=null && type.equals(AppConstants.DIALOG_TYPE_BIRTHDAY)) {
            remind_ic.setImageBitmap(GlideUtils.readBitMap(activity, R.drawable.contact_birthday_icon));
//            remind_ic.setImageResource(R.drawable.contact_birthday_icon);
        } else if (type!=null && type.equals(AppConstants.DIALOG_TYPE_PARENT)) {
            remind_ic.setImageBitmap(GlideUtils.readBitMap(activity, R.drawable.pointexchange_changeexplan));
//            remind_ic.setImageResource(R.drawable.pointexchange_changeexplan);
        }
        regedit_ic.setText("确      定");
    }

    private void initdata2() {
        title_tv.setText("选择您的身份");
        list = new ArrayList<String>();

        list.add("我是爸爸"); // relationship == 1
        list.add("我是妈妈"); // 后面
        list.add("我是爷爷");
        list.add("我是奶奶");
        list.add("我是外公");
        list.add("我是外婆");
        list.add("我是叔叔");
        list.add("我是阿姨");

//      try {
//          listbean = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
//          if (listbean!=null && listbean.size()!=0) {
//              for (int i = 0; i < listbean.size(); i++) {
//                  int relation = listbean.get(i).getRelationship();
//                  if (relation == 0) {
//                      relation = 1;
//                  }
//                  list.set(relation - 1, relation_name[relation - 1]);
//              }
//          }
//      } catch (DbException e) {
//            e.printStackTrace();
//      }

    }
  
	private void initdata() {
        list = new ArrayList<String>();
        list.add("不提醒");
        list.add("准时提醒");
        list.add("提前20分钟提醒");
        list.add("提前30分钟提醒");
        list.add("提前1小时提醒");
        list.add("提前1.5小时提醒");
        list.add("提前2小时提醒");
	}

    class SpinnerAdapter extends BaseAdapter{
    	
    	private int position;

      	@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}
		
		public int getposition(){
			return position;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			View v = LayoutInflater.from(mActivity).inflate(R.layout.inflater_service_schedule_remindtype, null);
            TextView tv = (TextView) v.findViewById(R.id.id_inflater_sendmsg_showname_tv);
            tv.setText(list.get(position));
            return v;
		}    
    }
  
}