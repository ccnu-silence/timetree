package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.adapter.ServiceAdapter.Onclickback;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.inter.OnAooRequestParentListener;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.PhotoDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ContactsParentList extends BaseActivity implements OnClickListener,OnItemClickListener,Onclickback{

    TextView titletextview;   //通讯录
    ImageView leftbtn;
    ListView listView;
    ServiceAdapter adapter;
    AppContext appcontext = null;
    AccountInfo accountInfo;
    List<Children> datalist = new ArrayList<Children>();

    int cid;
    String myclass = "";
    private int birthdaypositon;
    private final static int FROMCOMMONBROWSER_RESULT = 1;
    private final static String TAG = "ContactsParentList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list);
        appcontext = AppContext.getInstance();
        accountInfo=AppServer.getInstance().getAccountInfo();
        if (getIntent().getExtras()!=null) {
            cid = getIntent().getExtras().getInt("cid");
            myclass = getIntent().getExtras().getString("mycalss");
        }
        initdata();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this); // 7- 27 龙衡东修改 BaseActivity已注册
        }
    }

    public void initdata() {
        showLoadingDialog("正在加载");
        AppServer.getInstance().GetParentByCid(accountInfo.getUid(), cid, new OnAooRequestParentListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj, Object obj2) {
                if (code == 0) {
                    Children []children=(Children[]) obj;
                    if (children!=null && children.length!=0) {
    //                        C
                    }
                    datalist = Arrays.asList(children);
                } else {
                    datalist = new ArrayList<Children>();
                }
                cancelLoadingDialog();
                initview();
            }
        });
    }

    public void initview() {
        titletextview = (TextView) findViewById(R.id.header_title);
        leftbtn = (ImageView) findViewById(R.id.left_btn);
        leftbtn.setVisibility(View.VISIBLE);
        leftbtn.setOnClickListener(this);
        titletextview.setText(myclass);
        listView = (ListView) findViewById(R.id.contact_list_lv);

        adapter = new ServiceAdapter(this, datalist, AppConstants.CONTACTS_CONTACTPARENTLIST);
        adapter.setOnclickback(ContactsParentList.this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.left_btn:
            this.finish();
            break;
        }
    }

    @Override
    public void click(int id, int position) {
        // to do
        if (cid != 0 && datalist!=null && datalist.size()!=0 && datalist.get(position)!=null) {
            if (datalist.get(position).getBirthdaystatus() == 1) {
                UtilsLog.i(TAG, "leader start to birthday wishes, birthday/cid/toid/toName is: "
                        + datalist.get(position).getBirthday() + " /" + cid + " /" + datalist.get(position).getUid()
                        + " /" + datalist.get(position).getRealname());
                startWebUrlForBirthday(cid, "" + datalist.get(position).getUid(),
                        datalist.get(position).getRealname(), datalist.get(position).getBirthday(), "ContactsParentList", position, 0);
                birthdaypositon = position;
                UtilsLog.i(TAG, "birthdaystatus is 1, position: " + position);
            } else if (datalist.get(position).getBirthdaystatus() == 2) {
                new PhotoDialog(this, "我们已给" + datalist.get(position).getRealname() + "小朋友\n发送过生日祝福啦", AppConstants.DIALOG_TYPE_BIRTHDAY).show();
                // Toast.makeText(this, "已发送生日祝福", Toast.LENGTH_SHORT).show();
            } else {
                UtilsLog.i(TAG, "the birthdaystatus is 0 or other,birthdaystatus :" + datalist.get(position).getBirthdaystatus());
                return;
            }
        } else {
            UtilsLog.i(TAG, "hava a problem of data");
        }
    }

    /**
     * 专门为生日祝福打开url
     *
     * @param
     * @param
     */
    private void startWebUrlForBirthday(int cid, String toid, String toName,
                                              String birthday, String birthdayfrom, int grounpPosition, int childPosition) {
        String birthdayurl = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getString("birthdayurl", "");
        if (birthdayurl == null) {
            birthdayurl = "http://ydsence.zgyey.com/Communicate/Birthday_Temp?kid={kid}&" +
                    "client={client}&appver={appver}&uid={uid}&hxuid={hxuid}&role={role}" +
                    "&cid={cid}&ids={ids}&names={names}&births={births}&key={key}";
        }
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        String contansKey = info.getUid() + "" + info.getKid() + URL.urlkey;
        birthdayurl =  birthdayurl.replace("{kid}", info.getKid() + "")
                .replace("{client}", "1").replace("{appver}", AppUtils.getVersionName(AppContext.getInstance()))
                .replace("{uid}", info.getUid() + "")
                .replace("{hxuid}", info.getUid() + "a" + info.getRelationship())
                .replace("{role}", info.getRole() + "")
                .replace("{cid}", cid + "").replace("{ids}", toid + "").replace("{names}", toName + "")
                .replace("{births}", birthday + "").replace("{key}", contansKey);

        Intent intent = new Intent(ContactsParentList.this, CommonBrowser.class);
        Bundle noticebundle = new Bundle();
        noticebundle.putString(AppConstants.INTENT_URL, birthdayurl);
        noticebundle.putString("birthdayfrom", birthdayfrom);
        noticebundle.putInt("grounpPosition", grounpPosition);
        noticebundle.putInt("childPosition", childPosition);
        noticebundle.putInt("cidBirthday", cid);
        noticebundle.putString("toidBirthday", toid);
        noticebundle.putString(AppConstants.INTENT_NAME, "生日祝福");
        intent.putExtras(noticebundle);
        startActivityForResult(intent, FROMCOMMONBROWSER_RESULT);
    }

    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.CONTACTSPARENTLIST_BIRTHDAY) {
            UtilsLog.i(TAG, "accepte sended birthday message,bigin to refresh Fragment");
            if (adapter !=null && datalist!=null) {
                List<Children> datalistNew = new ArrayList<Children>();
                Children child = new Children();
                for (int i=0; i < datalist.size(); i++) {
                    child = datalist.get(i);
                    if (i == birthdaypositon) {
                        child.setBirthdaystatus(2);
                        datalistNew.add(child);
                    } else {
                        datalistNew.add(child);
                    }
                }
                datalist = datalistNew;
                adapter.setList(datalist);
            } else {
                initdata();
            }
        } else {
            return;
        }
    }

/*    private void refreshFragment() {
        List<Parent> parents = new ArrayList<Parent>();
        List<Children> childrens = new ArrayList<Children>();
        try{
            parents = DbHelper.getDB(this).findAll(Parent.class, WhereBuilder.b("cid","=",cid));
            if (parents!=null && parents.size()!=0){
                for (int i=0;i<parents.size();i++){
                    Children children = new Children(parents.get(i));
                    childrens.add(children);
                }
            }
            if (childrens!=null && childrens.size()!=0){
                if (adapter!=null) {
                    adapter.setList(childrens);
                } else {
                    adapter = new ServiceAdapter(this, childrens, AppConstants.CONTACTS_CONTACTPARENTLIST);
                    adapter.setOnclickback(ContactsParentList.this);
                    listView.setAdapter(adapter);
                }
            }
        } catch (DbException e){
            e.printStackTrace();
            UtilsLog.i(TAG,e.getMessage() + "/" + e.getCause());
        }
    }*/

    public class MyWebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ContactFriendDatacardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("state", AppConstants.CONTACTS_PARENT);
        bundle.putInt("role", 2);
        bundle.putInt("targetid", datalist.get(position).getUid());
        bundle.putString("birthday", datalist.get(position).getBirthday());
        bundle.putInt(AppConstants.PARAM_CID, cid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
