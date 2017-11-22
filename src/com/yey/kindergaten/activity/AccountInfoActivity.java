package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.MeInfoAdapter;
import com.yey.kindergaten.bean.AccountBean;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.MeinfoItemBean;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.RelationShipBean;
import com.yey.kindergaten.cropimage.CropImage;
import com.yey.kindergaten.db.DBManager;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.huanxin.DemoHXSDKHelper;
import com.yey.kindergaten.huanxin.controller.HXSDKHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.IntArraySortUtil;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.AddressPickWight;
import com.yey.kindergaten.widget.MyListViewWithScrollView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

//dlf
public class AccountInfoActivity extends BaseActivity{

    @ViewInject(R.id.left_btn)ImageView leftbtn;
    @ViewInject(R.id.header_title)TextView tv;
    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/uploadimg/";
    private String name;
    private MeInfoAdapter adapter;
    private MyListViewWithScrollView lv;
    private EditText et;
    private AddressPickWight addressPickWight;
    CharSequence[] items = { "相册", "拍照" };
    private static final int PHOTO_SUCCESS = 1;
    private static final int CAMERA_SUCCESS = 2;
    private static final int PHOTO_CROP = 9;
    AccountInfo accountInfo;
    List<MeinfoItemBean> datalist=new ArrayList<MeinfoItemBean>();
    int clickposition;
    private DBManager dbm;
    private SQLiteDatabase sqlite;
    MeinfoItemBean meinfoItem0;
    MeinfoItemBean meinfoItem01;
    MeinfoItemBean meinfoItem1;
    MeinfoItemBean meinfoItem2;
    MeinfoItemBean meinfoItem3;
    MeinfoItemBean meinfoItem02;
    MeinfoItemBean meinfoItem03;
    MeinfoItemBean meinfoItem4;
    MeinfoItemBean meinfoItem5;
    MeinfoItemBean meinfoItem6;
    MeinfoItemBean meinfoItem7;
    MeinfoItemBean meinfoItem8;
    SharedPreferencesHelper helper;
    private int[]relation_number = {0,1,2,3,4,99};
    private String[]relation_name = {"爸爸","妈妈","爷爷","奶奶","外公","外婆","叔叔","阿姨"};
    private  String strings[];
    boolean isFromCamera = false;// 区分拍照旋转

    private final static String TAG = "MeInfoActivity";
    int hxState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_main_info);
        helper = SharedPreferencesHelper.getInstance(this);
        accountInfo=AppServer.getInstance().getAccountInfo();
        ViewUtils.inject(this);
        findview();
        initdata();
    }

    private String  relationShip(int index){
        if(index!=0){
            return relation_name[index-1];
        }else {
            return relation_name[0];
        }
    }

    public void initdata(){
        int role = accountInfo.getRole();
        meinfoItem0=new MeinfoItemBean("账号", "", accountInfo.getAccount(), 1);
        meinfoItem01=new MeinfoItemBean(" ", "", accountInfo.getAccount(), 3);
        meinfoItem1=new MeinfoItemBean("头像", accountInfo.getAvatar(), "", 1);
        //meinfoItem2=new MeinfoItemBean("昵称", "", accountInfo.getNickname(), 1);
        meinfoItem3=new MeinfoItemBean(role>1?"孩子姓名":"姓名", "", accountInfo.getRealname()==null?"":accountInfo.getRealname(), 1);
        meinfoItem02=new MeinfoItemBean(" ", "", accountInfo.getAccount(), 3);
        meinfoItem03=new MeinfoItemBean(" ", "", accountInfo.getAccount(), 3);
        meinfoItem4=new MeinfoItemBean("电话", "", accountInfo.getTelephone()==null?"":accountInfo.getTelephone(), 1);

//        try {
//            List<RelationShipBean>list = DbHelper.getDB(this).findAll(RelationShipBean.class);
//            if(list==null||list.size()==0){
//                meinfoItem7=new MeinfoItemBean("消息接收人", "", relationShip(1), 1);
//            }else{
//                int relation =list.get(0).getDefaultrelation() ;
//                if(relation==0){
//                    relation=1;
//                }
//                meinfoItem7=new MeinfoItemBean("消息接收人", "", relationShip(relation), 1);
//            }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }

        meinfoItem8=new MeinfoItemBean("家长身份", "", relationShip(accountInfo.getRelationship()), 1);
        if(accountInfo.getTelephone()==null||accountInfo.getTelephone().equals("")){
            meinfoItem4=new MeinfoItemBean("电话", "", "请填写您的电话", 1);
        }
        if(accountInfo.getRealname()==null||accountInfo.getRealname().equals("")){
            meinfoItem3=new MeinfoItemBean("姓名", "", "请填写您的姓名", 1);
        }
        meinfoItem5=new MeinfoItemBean("性别", "", accountInfo.getGender()==null?"":accountInfo.getGender(), 1);
        if(accountInfo.getGender()==null||accountInfo.getGender().equals("-1")){
            meinfoItem5=new MeinfoItemBean("性别", "","1", 1);
        }
        if(accountInfo.getLocation()!=null && !accountInfo.getLocation().equals("0")){
            meinfoItem6=new MeinfoItemBean("所在地区", "", accountInfo.getLocation(), 1);
        }else{
            meinfoItem6=new MeinfoItemBean("所在地区", "", "请填写地址", 1);
        }
        if(accountInfo.getLocation()==null||accountInfo.getLocation().equals("-1")){
            meinfoItem6=new MeinfoItemBean("所在地区", "", "请填写您的地址", 1);
        }
//		 if(accountInfo.getNickname().equals("")){
//			 meinfoItem2=new MeinfoItemBean("昵称", "", "给自己取个昵称吧", 1);
//		 }
        datalist.add(meinfoItem01);
        datalist.add(meinfoItem1);
        datalist.add(meinfoItem02);
        datalist.add(meinfoItem0);
        //datalist.add(meinfoItem2);
        datalist.add(meinfoItem3);
        datalist.add(meinfoItem03);
//		datalist.add(meinfoItem4);
        datalist.add(meinfoItem5);
//      datalist.add(meinfoItem6);
        if(accountInfo.getRole()==2){
            datalist.add(meinfoItem01);
            datalist.add(meinfoItem8);
//            datalist.add(meinfoItem7);
        }
        initView();
    }

    public void findview(){
        leftbtn.setVisibility(View.VISIBLE);
        tv.setText("个人资料");
    }
    private void initView() {
        lv = (MyListViewWithScrollView) findViewById(R.id.lv_activity_me_main_info);
        adapter=new MeInfoAdapter(this, datalist);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String title="";
                Intent intent;
                clickposition=position;
                switch (position) {
                    case 1:
                        showDialogItems(items, "选择图片", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if(item==0){   //相册
                                    Intent intent=new Intent(AccountInfoActivity.this,GalleryActivity.class);
                                    intent.putExtra("typefrom", AppConstants.FROMMEINFO);
                                    startActivity(intent);
//                                    Intent getImage = new Intent(Intent.ACTION_PICK);
//			                       /* getImage.addCategory(Intent.CATEGORY_OPENABLE);
//			                        getImage.setType("image/*");
//			                        getImage.putExtra("return-data", true);*/
//                                    getImage.setDataAndType(
//                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                                    startActivityForResult(getImage, PHOTO_SUCCESS);   	//1
                                }else{ 		   //相机
                                    Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    name = DateFormat.format("yyyyMMddhhmmss",
                                            Calendar.getInstance(Locale.CHINA))
                                            + ".jpg";
                                    File file = new File(PATH+"takephoto/");
                                    if(!file.exists()){
                                        file.mkdirs();// 创建文件夹
                                    }
                                    Uri imageUri = Uri.fromFile(new File(PATH, name));
                                    System.out.println("imageUri----"+imageUri.toString());
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    startActivityForResult(intent, CAMERA_SUCCESS);         //2
                                }
                            }
                        });
                        break;
                    case 0:
                    case 2:
                        return;
                    case 3:            //昵称
                        //暂时隐藏编辑功能
					/*intent=new Intent(MeInfoActivity.this,EditActivity.class);
					intent.putExtra("clickposition", clickposition);
					if(datalist.get(clickposition)!=null&&
							!datalist.get(clickposition).getValue().equals("给自己取个昵称吧")){
						intent.putExtra("text", datalist.get(clickposition).getValue());
					}else{
						intent.putExtra("text", "");
					}
					intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
					intent.putExtra(AppConstants.TITLE,"昵称");
					startActivityForResult(intent, 3);//requestCode;
*/					break;
                    case 4:      //真实姓名
                        intent=new Intent(AccountInfoActivity.this,EditActivity.class);
                        intent.putExtra("clickposition", clickposition);
                        if(datalist.get(clickposition)!=null&&
                                !datalist.get(clickposition).getValue().equals("请填写您的姓名")){
                            intent.putExtra("text", datalist.get(clickposition).getValue());
                        }else{
                            intent.putExtra("text", "");
                        }
                        intent.putExtra(AppConstants.TITLE,"真实姓名");
                        intent.putExtra(AppConstants.INPUTTYPE,AppConstants.INPUTTYPE_STRING);
                        startActivityForResult(intent, 4);//requestCode;
                        break;
                    case 6:
                        final String choose="性别";
                        String[] sex={"男","女"};
                        if((accountInfo.getGender()!=null)){
                            if(accountInfo.getGender().equals("2")||accountInfo.getGender().equals("-1")){
                                showDialog(choose, sex, mOkOnClickListener,1);
                            }else{
                                showDialog(choose, sex, mOkOnClickListener,0);
                            }
                        }
                        break;
                    case 7:
                        intent=new Intent(AccountInfoActivity.this,GetAddressActivity.class);
                        intent.putExtra("clickposition", clickposition);
                        startActivityForResult(intent, 8);//requestCode;
                        break;
                    case 8:
                        final String chooseRelation="请选择您的身份";
                        AccountInfo bean = AppServer.getInstance().getAccountInfo();
                        showDialog(chooseRelation, relation_name, mOkOnClickListener,bean.getRelationship()-1);
                        break;
                    case 10:
                        final String chooseDefRelation="请设置您的默认消息接收人";
                        int relationDefalut = 1;
                        List<Integer>integersrelation = new ArrayList<Integer>();
                        try {
                            List<RelationShipBean> relationShipBeans = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
                            if(relationShipBeans!=null&&relationShipBeans.size()!=0){
                                for(int i=0;i<relationShipBeans.size();i++){
                                    if(relationShipBeans.get(i).getRelationship()!=0){
                                        integersrelation.add(relationShipBeans.get(i).getRelationship()-1);
                                    }
                                    relationDefalut = relationShipBeans.get(i).getDefaultrelation();
                                }
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if(integersrelation==null||integersrelation.size()==0){
                            showDialog(chooseDefRelation, relation_name, mOkOnClickListener,relationDefalut-1);
                        }else{
                            IntArraySortUtil comSort = new IntArraySortUtil();
                            Collections.sort(integersrelation,comSort);
                            /**
                             * list.toArray向下转型时有时会报异常
                             * 数组也可以先给数组地址空间，在给数组中的值赋引用。
                             */
                            strings=  new String[integersrelation.size()];
                            for(int i=0,j=integersrelation.size();i<j;i++){
                                strings[i]= relation_name[integersrelation.get(i)];
                            }
                            if(relationDefalut==0){
                                showDialog(chooseDefRelation, strings, mOkOnClickListener,0);
                            }else{
                                int relation_index = 0;
                                String relationname = null;
                                for(int i=0;i<relation_name.length;i++){
                                    if(i==relationDefalut-1){
                                        relationname = relation_name[i];
                                    }
                                }//查出defaultRelation对应的名字
                                for(int i=0;i<strings.length;i++){
                                    if(strings[i].equals(relationname)){
                                        relation_index = i;
                                    }
                                }//根据名字查询对应后数组中的位置
                                showDialog(chooseDefRelation, strings, mOkOnClickListener,relation_index);
                            }
                        }
                        break;
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==CAMERA_SUCCESS){     //2
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                showToast("SD不可用");
                return;
            }
            isFromCamera = true;
            startCropImage(PATH+name);

            accountInfo.setAvatar(PATH+name);
            AppServer.getInstance().getAccountInfo().setAvatar(PATH+name);
            UtilsLog.i(TAG,"requestCode==CAMERA_SUCCESS set accountBean success,path is: " + PATH + name + "");

		    /*Bitmap bitmap = BitmapUtil.getImageByPath(PATH+name, false);
		    if(bitmap!=null){
		       	 showLoadingDialog("正在上传...");
		    	 uploadImage(accountInfo.getUid(),save(PATH+name));		
		    }*/
        }else if(requestCode==3){      //昵称
            if(intent.getExtras()!=null){
                int clickpotion=intent.getExtras().getInt("clickposition");
                String edittext=intent.getExtras().getString("edittext");
                UpdateMessage(clickpotion, edittext);
            }
        }else if(requestCode==4){
            if(intent.getExtras()!=null){//真实姓名
                int clickpotion=intent.getExtras().getInt("clickposition");
                String edittext=intent.getExtras().getString("edittext");
                UpdateMessage(clickpotion, edittext);
            }

        }else if(requestCode==6){
            if(intent.getExtras()!=null){//联系电话
                int clickpotion=intent.getExtras().getInt("clickposition");
                String edittext=intent.getExtras().getString("edittext");
                UpdateMessage(clickpotion, edittext);
            }
        }
        else if(requestCode==8){
            if(intent==null){
                return;
            }
            if(intent.getExtras()!=null){//选择地址
                String locationID=intent.getExtras().getString("locationID");
                String locationText=intent.getExtras().getString("locationText");
                int clickpotion=intent.getExtras().getInt("clickposition");
                UpdateMessage(clickpotion,locationID);
            }
        }
        else if(requestCode == PHOTO_CROP){
            if (intent == null) {
                // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                return;
            } else {
                String path = intent.getStringExtra(CropImage.IMAGE_PATH);
                Bitmap bitmap = BitmapUtil.getImageByPath(path, false);
                if(bitmap!=null){
                    showLoadingDialog("正在上传...");
                    uploadImage(accountInfo.getUid(),save(path));

                    accountInfo.setAvatar(path);
                    AppServer.getInstance().getAccountInfo().setAvatar(path);
                    UtilsLog.i(TAG,"requestCode==CAMERA_SUCCESS set accountBean success,path is: " + path + "");

                    meinfoItem1.setImageurl(path);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        if (intent.getExtras()!=null) {
            ArrayList<Photo> list = intent.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
            String path = list.get(0).imgPath;
            BitmapUtil.createSDCardDir();
            File f= new File(path);
            name=f.getName();
            BitmapUtil.save(path,name,PATH);
            showLoadingDialog("正在上传...");
            uploadImage(accountInfo.getUid(),PATH+name);
            meinfoItem1.setImageurl(PATH+name);
            adapter.notifyDataSetChanged();
//                if (bitmap != null && bitmap.isRecycled()) {
//                    bitmap.recycle();
//                }
        }
        //serviceGridviewAdapter.notifyDataSetChanged();
    }

    private void uploadImage(final int uid,String url) {
        RequestParams params = new RequestParams();
        params.addBodyParameter(AppConstants.PARAM_UID, AppServer.getInstance().getAccountInfo().getUid()+"");
        params.addBodyParameter("file", new File(url));
        params.addBodyParameter("type",AppConstants.PARAM_UPLOAD_AVATAR);
        String timestamp = URL.urlkey;
        params.addBodyParameter(AppConstants.PARAM_KEY, AppUtils.Md5(AppServer.getInstance().getAccountInfo().getUid()+""+timestamp));
        HttpUtils  http=new HttpUtils();
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        String upload = info.getSysgw()+ URL.UPLOADFILE;
        http.send(HttpMethod.POST,upload, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
            }
            @Override
            public void onLoading(long total, long current,boolean isUploading) {
            }
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                cancelLoadingDialog();
                Toast.makeText(AccountInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result  = responseInfo.result;
                JsonParser parser = new JsonParser();
                JsonElement  elements = parser.parse(result);
                if(elements.isJsonArray()){
                    JsonArray  array = elements.getAsJsonArray();
                }else{
                    JsonObject object =	elements.getAsJsonObject();
                    String code =  object.get("code").getAsString();
                    JsonObject rejsonobject;
                    if(object!=null&&object.isJsonObject()){
                        rejsonobject  = object.get("result").getAsJsonObject();
                    }else{
                        showToast("上传失败");
                        return;
                    }
                    String resulturl = rejsonobject.get("url").getAsString();
                    UploadHeadPic(uid, resulturl);//更新本地头像url
                    SharedPreferences setting=AccountInfoActivity.this.getSharedPreferences
                            (AppConfig.LOGIN_DEFALUTE_VALUE, Context.MODE_PRIVATE);
                    String uname = setting.getString(AppConfig.LOGIN_DEFAULTE_ACCOUNT, "");
                    String upassword = setting.getString(AppConfig.LOGIN_DEFAULTE_PASSWORD, "");
                    SharedPreferences.Editor editors=setting.edit();
                    if(accountInfo.getPassword()!=null){
                        editors.putString(AppConfig.LOGIN_DEFAULTE_PASSWORD, accountInfo.getPassword()+"zgyey");}
                    if(resulturl!=null){
                        editors.putString(AppConfig.LOGIN_DEFAULTE_AVATER, resulturl);
                    }
                    editors.commit();

                    String accouts=uname+"||"+(upassword+"zgyey")+"||"+resulturl;
                    SharedPreferences settings=AppContext.getInstance().getSharedPreferences(SharedPreferencesHelper.PREF_LOGIN_FILE,  Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putString(uname.trim(), accouts);
                    editor.commit();
                }
            }
        });
    }

    public void UploadHeadPic(int uid,final String url){
        AppServer.getInstance().UploadAvatar(uid, url, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if(code==0){
                    //showToast("修改成功");
                    datalist.set(1, meinfoItem1);
                    accountInfo.setAvatar(url);
                    DbHelper.updateAccountInfo(accountInfo);
                    DbHelper.updatechatHead(accountInfo);
                    DbHelper.updateContactHead(accountInfo);
                    AppServer.getInstance().setmAccountInfo(accountInfo);
                    AppServer.getInstance().setmAccountBean(new AccountBean(accountInfo));
                    AppContext.getInstance().setAccountInfo(accountInfo);
//                    adapter=new MeInfoAdapter(MeInfoActivity.this, datalist);
//                    lv.setAdapter(adapter);
                }else{
                    showToast("修改失败");
                }
            }
        });
    }

    private DialogInterface.OnClickListener mOkOnClickListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            String changeText="";
            if(et!=null){
                changeText= et.getText().toString();
            }
            switch (clickposition) {
                case 6://性别
                    if(which==0){
                        UpdateMessage(6,"3"); //男
                    }else{
                        UpdateMessage(6,"2");  //女
                    }
                    dialog.dismiss();
                    break;
                case 8:
                    String clientid = SharedPreferencesHelper.getInstance(AccountInfoActivity.this).getString(AppConstants.CLIENTID, "");
                    showLoadingDialog("加载中...");
                    AppServer.getInstance().updateDeviceId(AppServer.getInstance().getAccountInfo().getUid(), clientid,which+1,0,new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if(code == AppServer.REQUEST_SUCCESS){
                                postEvent(AppEvent.HOMEFRAGMENT_REFRESH_CHAT);
                                hxState= ((Integer)obj).intValue();
                                AccountInfo info = AppServer.getInstance().getAccountInfo();
                                boolean isLogin=false;
                                info.setRelationship(which+1);
                                final String relationShip = String.valueOf(which+1);
                                try {
                                    List<RelationShipBean> list=  DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
                                    boolean flag = false;
                                    if(list!=null&&list.size()!=0){
                                        for(int i=0;i<list.size();i++){
                                            if(list.get(i).getRelationship()==which+1){
                                                RelationShipBean bean = new RelationShipBean();
                                                bean = list.get(i);
                                                bean.setHxregtag(hxState);
                                                list.set(i,bean);
                                                flag = true;
                                            }
                                        }
                                        if(!flag){
                                            RelationShipBean bean = new RelationShipBean();
                                            bean.setHxregtag(hxState);
                                            bean.setRelationship(which+1);
                                            bean.setDefaultrelation(list.get(0).getDefaultrelation());
                                            list.add(bean);
                                        }
                                    }

                                    DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
                                    DbHelper.getDB(AppContext.getInstance()).saveAll(list);
                                    AppServer.getInstance().setmAccountBean(new AccountBean(info));
                                    AppServer.getInstance().setmAccountInfo(info);

                                    DbHelper.getDB(AppContext.getInstance()).update(new AccountBean(info), WhereBuilder.b("uid", "=", info.getUid()),new String[]{"relationship"});

                                    AppContext.getInstance().logout(new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            HXSDKHelper hxsdkHelper = new DemoHXSDKHelper();
                                            hxsdkHelper.onInit(AppContext.getInstance());
                                            if(hxState==1){
                                                AppServer.getInstance().updateHxState(AppServer.getInstance().getAccountInfo().getUid(),Integer.valueOf(relationShip),1,"注册成功",new OnAppRequestListener() {
                                                    @Override
                                                    public void onAppRequest(int code, String message, Object obj) {

                                                    }
                                                });
                                                huanxinLogin(AppServer.getInstance().getAccountInfo().getUid() +"A"+ relationShip, "al1M0Ak3sG6");
                                            }else{
                                                regeditHuanxin(AppServer.getInstance().getAccountInfo().getUid(),"al1M0Ak3sG6",which+1);
                                            }
                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            showToast("修改失败");
                                            return;
                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    });
                                    AccountInfo bean = AppServer.getInstance().getAccountInfo();
                                    meinfoItem8=new MeinfoItemBean("家长身份", "", relationShip(bean.getRelationship()), 1);
                                    datalist.set(8, meinfoItem8);
                                    adapter=new MeInfoAdapter(AccountInfoActivity.this, datalist);
                                    lv.setAdapter(adapter);

                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(AccountInfoActivity.this,message+"",Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    break;
                case 10:
                    int relation_index = which;
                    String relationname = null;
                    for(int i=0;i<strings.length;i++){
                        if(i==relation_index){
                            relationname = strings[i];
                        }
                    }//查出defaultRelation对应的名字
                    for(int i=0;i<relation_name.length;i++){
                        if(relation_name[i].equals(relationname)){
                            relation_index = i;
                        }
                    }//根据名字查询对应后数组中的位置

                    final int finalIndex = relation_index;
                    AppServer.getInstance().updateDefaultRelation(accountInfo.getUid(), relation_index+1, new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if (code == AppServer.REQUEST_SUCCESS) {

                                meinfoItem7=new MeinfoItemBean("消息接收人", "", relationShip(finalIndex+1), 1);
                                try {
                                    List<RelationShipBean>  listbean = DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
                                    List<RelationShipBean> relationShipBeans = new ArrayList<RelationShipBean>();
                                    for(int i=0;i<listbean.size();i++){
                                        RelationShipBean bean = listbean.get(i);
                                        bean.setDefaultrelation(finalIndex+1);
                                        relationShipBeans.add(bean);
                                    }
                                    DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
                                    DbHelper.getDB(AppContext.getInstance()).saveAll(relationShipBeans);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                datalist.set(10, meinfoItem7);
                                adapter=new MeInfoAdapter(AccountInfoActivity.this, datalist);
                                lv.setAdapter(adapter);
                                showToast(message);
                            }else{
                                Toast.makeText(AccountInfoActivity.this ,message+"",Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    break;
            }
        }
    };

    public void UpdateMessage(final int position,final String text) {
        switch (position){
            case 3://昵称
                accountInfo.setNickname(text);
                AppServer.getInstance().getAccountInfo().setNickname(text);
                break;
            case 4://姓名
                accountInfo.setRealname(text);
                AppServer.getInstance().getAccountInfo().setRealname(text);
                break;
            case 5://电话
                accountInfo.setTelephone(text);
                AppServer.getInstance().getAccountInfo().setTelephone(text);
                break;
            case 6://性别
                accountInfo.setGender(text);
                AppServer.getInstance().getAccountInfo().setGender(text);

                break;
            case 7://住址
                accountInfo.setLocation(text);
                AppServer.getInstance().getAccountInfo().setLocation(text);
                break;
        }
        showLoadingDialog("正在修改...");
        String locationID=getlocationId(accountInfo.getLocation());
        AppServer.getInstance().modifySelfInfo(accountInfo.getUid(),accountInfo.getAvatar(), accountInfo.getNickname(),accountInfo.getGender(), accountInfo.getLocation(),accountInfo.getRealname(),accountInfo.getTelephone(), accountInfo.getBirthday(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if(code==0){
                    //showToast("修改成功");
                    MeinfoItemBean meinfoItem=datalist.get(position);
                    meinfoItem.setValue(text);
                    datalist.set(position, meinfoItem);
                    AppServer.getInstance().setmAccountInfo(accountInfo);
                    AppContext.getInstance().setAccountInfo(accountInfo);
                    DbHelper.updateAccountInfo(accountInfo);
                    adapter=new MeInfoAdapter(AccountInfoActivity.this, datalist);
                    lv.setAdapter(adapter);
                }else{
                    showToast( "修改失败");
                }
                cancelLoadingDialog();
            }
        });

    }

    public String getlocationId(String location){
        if(location==null){
            return "";
        }
        String []items=location.split(",");
        if(items.length!=3){
            return "";
        }
        dbm = new DBManager(this);
        dbm.openDatabase();
        sqlite = dbm.getDatabase();
        String sql = "select * from district where province='"+items[0]+"' and  city='"+items[1]+"' and location='"+items[2]+"'";
        Cursor cursor = sqlite.rawQuery(sql,null);
        cursor.moveToFirst();
        AddressBean addressBean=new AddressBean();
        List<AddressBean> list=DbHelper.getAList(addressBean, cursor);
        if(list!=null){
            return list.get(0).getLocationid();
        }
        return "";
    }

    public String getlocationByid(String  id){
        if(id.equals("0")||id.equals("-1")){
            return "";
        }
        dbm = new DBManager(this);
        dbm.openDatabase();
        sqlite = dbm.getDatabase();
        String sql = "select * from district where locationid='"+id+"'";
        Cursor cursor = sqlite.rawQuery(sql,null);
        cursor.moveToFirst();
        AddressBean addressBean=new AddressBean();
        List<AddressBean> list=DbHelper.getAList(addressBean, cursor);
        if(list!=null&&list.size()!=0){
            String address="";
            if(list.get(0).getProvince()!=null&&!list.get(0).getProvince().equals("")){
                address=address+list.get(0).getProvince();
            }
            if(list.get(0).getCity()!=null&&!list.get(0).getCity().equals("")){
                address=address+","+list.get(0).getCity();
            }
            if(list.get(0).getLocation()!=null&&!list.get(0).getLocation().equals("")){
                address=address+","+list.get(0).getLocation();
            }
            return address;
//        	 return list.get(0).getProvince()+","+list.get(0).getCity()+","+list.get(0).getLocation();
        }
        return "";
    }

    @OnClick({(R.id.left_btn)})
    public void onclick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            default:
                break;
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

    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    private String save(String path) {
        int degree = readPictureDegree(path);
        BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
        opts.inSampleSize=2;
        Bitmap cbitmap=BitmapFactory.decodeFile(path,opts);
        if(degree>0){
            cbitmap=rotaingImageView(degree, cbitmap);
        }

        File f = new File(path);
        name=f.getName();
        Boolean contents=false;
        File root=new File(path);
        File[] fils=root.listFiles();
        if(fils != null){
            for (File af : fils){
                if(af.isDirectory()){
                    af.getName().equals(name);
                    contents=true;
                    break;
                }
            }
        }
        if(!contents){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(cbitmap!=null){
                cbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(new File(PATH,
                        name));
                int options = 100;
                while (baos.toByteArray().length / 1024 > 80 && options != 10) {
                    baos.reset();
                    cbitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    options -= 30;
                }
                fos.write(baos.toByteArray());
                fos.close();
                baos.close();
                cbitmap=null;
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return PATH+name;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(accountInfo == null){
            accountInfo=AppServer.getInstance().getAccountInfo();
        }
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Configuration o = newConfig;
            o.orientation = Configuration.ORIENTATION_PORTRAIT;
            newConfig.setTo(o);
        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }
        super.onConfigurationChanged(newConfig);
    }

    private void startCropImage(String path) {

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, path);
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);

        startActivityForResult(intent, PHOTO_CROP);
    }

    public void huanxinLogin(final String currentUsername,final String currentPassword){
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                AppContext.getInstance().setUserName(currentUsername);
                AppContext.getInstance().setPassword(currentPassword);
                try {
                    EMChatManager.getInstance().loadAllConversations();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (!AccountInfoActivity.this.isFinishing())
                    cancelLoadingDialog();
            }

            @Override
            public void onProgress(int progress, String status) {

            }
            @Override
            public void onError(final int code, final String message) {
                if(!AccountInfoActivity.this.isFinishing())
                    cancelLoadingDialog();

            }
        });
    }
    /**
     * 更新本地数据库中的环信注册状态
     * @param relationShip
     */
    private  void updateLocalHxState(int relationShip){
        try {
            List<RelationShipBean> list=  DbHelper.getDB(AppContext.getInstance()).findAll(RelationShipBean.class);
            if(list!=null&&list.size()!=0){
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getRelationship()==relationShip){
                        RelationShipBean bean = new RelationShipBean();
                        bean = list.get(i);
                        bean.setHxregtag(hxState);
                        list.set(i,bean);
                    }
                }
            }
            DbHelper.getDB(AppContext.getInstance()).deleteAll(RelationShipBean.class);
            DbHelper.getDB(AppContext.getInstance()).saveAll(list);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册环信
     */
    private void  regeditHuanxin(final int  account , final String password, final int relationShip){

        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    String name = account+"A"+String.valueOf(relationShip);
                    EMChatManager.getInstance().createAccountOnServer(name, password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            hxState =1;//表示注册环信成功
                            updateLocalHxState(relationShip);
                            huanxinLogin(account +"A"+ String.valueOf(relationShip), password);
                            AppServer.getInstance().updateHxState(account,relationShip,hxState,"注册成功",new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {

                                }
                            });
                        }
                    });
                } catch (final EaseMobException e) {
                    hxState =0;//表示注册环信失败
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String message=null;
                            if(e!=null) {
                                message  = e.getMessage();
                                int errorCode=e.getErrorCode();
                                if(errorCode== EMError.NONETWORK_ERROR){
                                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                                }else if(errorCode==EMError.USER_ALREADY_EXISTS){
                                    hxState =1;//表示注册环信成功
                                }else if(errorCode==EMError.UNAUTHORIZED){

                                }else{
                                    Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }}
                            updateLocalHxState(relationShip);
                            huanxinLogin(account +"A"+ String.valueOf(relationShip), password);
                            AppServer.getInstance().updateHxState(account,relationShip,hxState, message+" ",new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {

                                }
                            });
                        }
                    });
                }
            }
        }).start();

    }

}
