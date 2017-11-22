package com.yey.kindergaten.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.EmoViewPagerAdapter;
import com.yey.kindergaten.adapter.EmoteAdapter;
import com.yey.kindergaten.adapter.ServiceGridviewAdapter;
import com.yey.kindergaten.adapter.ServiceGroupAdapter;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.FaceText;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.GroupTwritte;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.cropimage.CropImage;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.CirclePageIndicator;
import com.yey.kindergaten.widget.EmoticonsEditText;
import com.yey.kindergaten.widget.MyGridviewWithScrollView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServicePublishSpeakActivity extends BaseActivity implements OnClickListener,OnItemClickListener{

    TextView sharetipsTv;
    MyGridviewWithScrollView gridview ;
    ServiceGridviewAdapter serviceGridviewAdapter;
    TextView titletextview; // 通讯录
    TextView righttv;       // 右边点击的
    ImageView leftbtn;      // 右边点击的
    LinearLayout facely;
    Button facebtn,photobtn;
    LinearLayout ll_gc;
    EmoticonsEditText editText;
    ListView lv;
    ArrayList<String> list = new ArrayList<String>();     // 照片list
    ArrayList<String> imagelis = new ArrayList<String>();
    ArrayList<String> sharelist = new ArrayList<String>();// 分享list
    CirclePageIndicator circlePageIndicator;
    CharSequence[] items = { "相册","拍照" };
    String textString = "";
    int inputtype = 1;
    int texesize = 0;
    List<Map<Integer, Integer>> facesize = new ArrayList<Map<Integer,Integer>>();
    List<FaceText> emos = null;
    private List<GroupInfoBean> grouplist = new ArrayList<GroupInfoBean>();
    private StringBuffer headvision, share;
    private ServiceGroupAdapter adapter;
    private StringBuffer url = new StringBuffer();
    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/uploadimg/";
    private static final String PATHA = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/readyupload/";
    private String name;
    private int gnum;
    private ArrayList<Photo> alist = new ArrayList<Photo>();
    private ArrayList<Photo> photolist = new ArrayList<Photo>();
    private static final int CAMERA_SUCCESS = 2;
    private static final int SCALE = 2;
    private RelativeLayout rl_share;
    private int cid;
    private Boolean  isfirst;
    private ArrayList<HashMap<GroupInfoBean, Boolean>> groupMapList = new ArrayList<HashMap<GroupInfoBean,Boolean>>();
    private String albumid = "-1";
    private static final int PHOTO_CROP = 9;
    private String cname;
    private String from;

    @ViewInject(R.id.network_listener_ll)RelativeLayout netCheckRL;
    @ViewInject(R.id.network_listener_tv)TextView netCheckTv;


    NetWorkStateReceive mReceiver;
    private List<Photo>checkList = new ArrayList<Photo>();
    private final static String TAG = "ServicePublishSpeakActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_publishspeak);
        ViewUtils.inject(this);

        FindViewById();
        initData();
        initView();
        setOnClick();
    }

    public class NetWorkStateReceive extends BroadcastReceiver {
        private ConnectivityManager connectivityManager;
        private NetworkInfo info;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    netCheckRL.setVisibility(View.GONE);
                } else {
                    netCheckRL.setVisibility(View.VISIBLE);
                    netCheckTv.setText("网络不可用，请检查您的网络设置。");
                }
            }
        }
    }

    public void FindViewById() {
        sharetipsTv = (TextView)findViewById(R.id.sharetips);
        rl_share = (RelativeLayout) findViewById(R.id.ll_publicspeak_share);
        rl_share.setOnClickListener(this);
        ll_gc = (LinearLayout) findViewById(R.id.service_publishspeak_gridviewly);
        titletextview = (TextView) findViewById(R.id.header_title);
        titletextview.setText(R.string.service_publishspeak);
        righttv = (TextView) findViewById(R.id.right_tv);
        righttv.setVisibility(View.VISIBLE);
        righttv.setText("发送");
        leftbtn = (ImageView) findViewById(R.id.left_btn);
        leftbtn.setVisibility(View.VISIBLE);
        gridview = (MyGridviewWithScrollView) findViewById(R.id.service_publishspeak_gv);
        facely = (LinearLayout) findViewById(R.id.service_publishspeak_facely);
        facebtn = (Button) findViewById(R.id.service_publishspeak_facebtn);
        photobtn = (Button) findViewById(R.id.service_publishspeak_photobtn);
        editText = (EmoticonsEditText) findViewById(R.id.service_publishspeak_ed);
//      lv = (ListView) findViewById(R.id.service_publicspeak_share);
        ViewPager faceViewPage = (ViewPager) findViewById(R.id.service_publishspeak_face);
        emos = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i,editText));
        }
        faceViewPage.setAdapter(new EmoViewPagerAdapter(views));
        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            editText.setHint("分享孩子成长变化，交流育儿经验、得到老师的育儿指引");
        } else {
            editText.setHint("传递孩子成长变化，分享育儿经验、解答家长问题");
        }
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isGetFocus) {
                if (isGetFocus) {
                    editText.setHint("");
                } else {
                    if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
                        editText.setHint("分享孩子成长变化，交流育儿经验、得到老师的育儿指引");
                    } else {
                        editText.setHint("传递孩子成长变化，分享育儿经验、解答家长问题");
                    }
                }
            }
        });
    }

    public void initData() {
        AppContext.checkList.clear();
        UtilsLog.i(TAG,"initData AppContext.checkList.clear() start..");
        Photo photo = new Photo();
        photo.imgPath = "local";
        alist.add(photo);
        serviceGridviewAdapter = new ServiceGridviewAdapter(this, alist);

        mReceiver = new NetWorkStateReceive();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    public void setOnClick() {
        righttv.setOnClickListener(this);
        leftbtn.setOnClickListener(this);
        gridview.setOnItemClickListener(this);
        facebtn.setOnClickListener(this);
        photobtn.setOnClickListener(this);
    }

    public void initView() {
        cid = getIntent().getExtras().getInt("cid");
        cname = getIntent().getExtras().getString("cname");
        from = getIntent().getExtras().getString("from");
        gridview.setAdapter(serviceGridviewAdapter);
        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            rl_share.setVisibility(View.GONE);
        } else {
            rl_share.setVisibility(View.VISIBLE);
        }
        netCheckRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                UtilsLog.i(TAG, "wifiSettingIntent to settings.WIFI_SETTINGS");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                AppContext.selectphoto.clear();
                if (!editText.getText().toString().equals("")) {
                    showDialog("友情提示", "是否保存这条动态？", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savedata();
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                } else {
                    finish();
                }
                break;
            case R.id.ll_publicspeak_share:
                if (alist.size() > 1) {
                    Intent c = new Intent(ServicePublishSpeakActivity.this, ClassPhotoMainActivity.class);
                    c.putExtra("typefrom", AppConstants.FROMSPEAK);
                    c.putExtra("classphototype", AppConstants.FROMFRIENDSTER);
                    c.putExtra("cid", cid);
                    c.putExtra("cname", cname);
                    startActivity(c);
                } else {
                    showToast("请选择要上传的照片");
                }
                break;
            case R.id.right_tv:
                if (!AppUtils.isNetworkAvailable(ServicePublishSpeakActivity.this)) {
                    showToast("现在网络不给力，等一会哦");
                    return;
                }
                savedata();
                break;
            case R.id.service_publishspeak_facebtn:
                if (facely.isShown()) {
                    facely.setVisibility(View.GONE);
                } else {
                    facely.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.service_publishspeak_photobtn:
                Intent i = new Intent(ServicePublishSpeakActivity.this, GalleryActivity.class);
                i.putExtra("typefrom", AppConstants.FROMSPEAK);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private void savedata() {
        isfirst = true;
        AppContext.selectphoto.clear();
        StringBuffer sb = new StringBuffer();
        share = new StringBuffer();
        if (alist.size() > 1) {
            headvision = new StringBuffer();
            for (int i = 0; i < alist.size() - 1; i++) {
                headvision.append(alist.get(i).imgPath);
//              headvision.append("$");
                headvision.append(",");
            }
//          StringBuffer headvisonnew = new StringBuffer(headvision.substring(0, headvision.length() - 1));
            StringBuffer headvisonnew = new StringBuffer(headvision);
            headvision = headvisonnew;
            // headvision.append(alist.get(alist.size() - 1));
        }
        GroupTwritte newValues = new GroupTwritte();
        newValues.setContent(editText.getText().toString().trim());
        newValues.setRealname(AppServer.getInstance().getAccountInfo().getRealname());
        String name = AppServer.getInstance().getAccountInfo().getRealname();
        newValues.setUid(AppServer.getInstance().getAccountInfo().getUid());
        newValues.setDate(TimeUtil.getYMDHMSS());
        newValues.setTwrid(-1);
        newValues.setStatus(3);
        newValues.setCid(cid);
        newValues.setZan("");
        newValues.setAlbumid(albumid);
        newValues.setFtype(0);
        newValues.setAvatar(AppServer.getInstance().getAccountInfo().getAvatar());
        Intent a = new Intent(ServicePublishSpeakActivity.this, ServiceFriendsterActivity.class);
        if (headvision!=null) {
            newValues.setImgs(headvision.toString());
            a.putExtra("imglist", headvision.toString());
        } else {
            newValues.setImgs("");
            a.putExtra("imglist", "");
        }
        if (!editText.getText().toString().trim().equals("")) {
            a.putExtra("typefrom", AppConstants.GETGROUP);
            try {
                DbHelper.getDB(ServicePublishSpeakActivity.this).save(newValues);
                UtilsLog.i(TAG, "save newValues seccuce");
            } catch (DbException e) {
                e.printStackTrace();
                UtilsLog.i(TAG, "save newValues fail because DbException");
            }
            a.putExtra("content", editText.getText().toString().trim());
            ArrayList<String> photos = new ArrayList<String>();

            for (int i = 0; i < alist.size(); i++) {
                Photo photo = alist.get(i);
                if (!photo.imgPath.equals("local")) {
                    photos.add(photo.imgPath);
                }
            }
            a.putExtra("imgalist", photos);
            a.putExtra("albumid", albumid);
            startActivity(a);
            alist.clear();
            this.finish();
            hideSoftInput(righttv);
        } else {
            showToast("请输入动态内容");
            return ;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (from!=null && from.equals("home")) {
                this.finish();
                return false;
            }
            AppContext.selectphoto.clear();
            Intent intent = new Intent(ServicePublishSpeakActivity.this, ServiceFriendsterActivity.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == alist.size() - 1) {
            showDialogItems(items, "选择照片", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 1) {   // 拍照
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        name = DateFormat.format("yyyyMMddhhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                        File file = new File(PATH + "takephoto/");
                        if (!file.exists()) {
                            file.mkdirs(); // 创建文件夹
                        }
                        Uri imageUri = Uri.fromFile(new File(PATH, name));
                        System.out.println("imageUri----" + imageUri.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, CAMERA_SUCCESS);
                    } else {    // 相册
                        Intent intent = new Intent(ServicePublishSpeakActivity.this, GalleryActivity.class);
                        intent.putExtra("typefrom", AppConstants.FROMSPEAK);
                        startActivity(intent); // 表示启动
                    }
                }
            });
        } else {
            Intent i = new Intent(ServicePublishSpeakActivity.this, FriendsterShowImage.class);
            i.putExtra("imglist", alist);
            i.putExtra("position", position);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CAMERA_SUCCESS) {
            if (resultCode!=0) {
                startCropImage(PATH + name);
            }
        } else if (requestCode == PHOTO_CROP) {
            if (intent!=null) {
                String path = intent.getStringExtra(CropImage.IMAGE_PATH);
                BitmapUtil.createSDCardDir();

                File f = new File(path);
                name = f.getName();

                BitmapUtil.save(path, name, PATHA);
                Photo photo = new Photo();
                photo.imgPath = PATHA + name;
                alist.add(photo);

                // 把第一个local位置显示在最后
                Photo loclalphoto = new Photo();
                loclalphoto.imgPath = "local";
                alist.remove(loclalphoto);
                alist.add(loclalphoto);

                serviceGridviewAdapter.setList(alist);
            }
        } else if (requestCode == 2) { // 选择相册

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void startCropImage(String path) {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, path);
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);

        startActivityForResult(intent, PHOTO_CROP);
    }

    private String save(String path) {
        int degree = readPictureDegree(path);
        BitmapFactory.Options opts=new BitmapFactory.Options(); // 获取缩略图显示到屏幕上
        opts.inSampleSize = 2;
        Bitmap cbitmap = BitmapFactory.decodeFile(path,opts);
        if (degree > 0) {
            cbitmap = rotaingImageView(degree, cbitmap);
        }

        File f = new File(path);
        name = f.getName();
        Boolean contents = false;
        File root = new File(path);
        File[] fils = root.listFiles();
        if (fils!=null) {
            for (File af : fils) {
                if (af.isDirectory()) {
                    af.getName().equals(name);
                    contents = true;
                    break;
                }
            }
        }
        if (!contents) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (cbitmap!=null) {
                cbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(new File(PATHA, name));
                int options = 100;
                while (baos.toByteArray().length / 1024 > 80 && options!=10) {
                    baos.reset();
                    cbitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    options -= 30;
                }
                fos.write(baos.toByteArray());
                fos.close();
                baos.close();
                cbitmap = null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return PATHA + name;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
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
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    @Override
    protected void onNewIntent(Intent intent){
        if (intent.getExtras()!=null) {
            Object object = intent.getSerializableExtra(AppConstants.PARAM_ALBUM);
            if (object != null) {
                Album album = (Album) object;
                albumid = album.getAlbumid();
                sharetipsTv.setText(album.getAlbumName());
            } else {
                ll_gc.setVisibility(View.VISIBLE);
                ArrayList<Photo> list = intent.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
                String deleteimage = intent.getStringExtra("deleteimage");
                if (list!=null) {
                    // alist保存的是全部的相片
                    alist.removeAll(checkList);
                    // checkList保存相册的相片
                    checkList.clear();
                    // 每次使用，先清除总相片中的checkList，在清空checkList
                    checkList.addAll(list);
                    alist.addAll(checkList);

                    Photo photo = new Photo();
                    photo.imgPath = "local";
                    alist.remove(photo);
                    alist.add(photo);

                    UtilsLog.i(TAG,"initData AppContext.checkList.size: "+AppContext.checkList.size());
                }
                serviceGridviewAdapter.setList(alist);
            }
        }
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
        final EmoteAdapter gridAdapter = new EmoteAdapter(ServicePublishSpeakActivity.this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                facely.setVisibility(View.GONE);
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
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    public  void hideSoftInput(TextView righttv) {
        InputMethodManager imm = (InputMethodManager) righttv.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(righttv.getWindowToken(), 0);
    }


}
