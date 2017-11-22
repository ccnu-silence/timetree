/**
 * 
 */
package com.yey.kindergaten.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ImagesAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.bean.Upload;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.db.UploadDB;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.service.UploadPhotosService;
import com.yey.kindergaten.task.SimpleTask;
import com.yey.kindergaten.task.TaskExecutor;
import com.yey.kindergaten.upyun.UpYunUtils;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.StringUtils;
import com.yey.kindergaten.util.UpLoadManager;
import com.yey.kindergaten.util.UploadThread;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.OptimizeGridView;
import com.yey.kindergaten.widget.PullToRefreshView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author chaowen
 * 公用的加载网络图片界面
 */
public class ClassPhotoDetialManager extends BaseActivity implements OnItemClickListener{

    @ViewInject(R.id.right_tv)TextView right_tv;
    @ViewInject(R.id.header_title)TextView tv_headerTitle ;
    @ViewInject(R.id.left_btn)ImageView left_iv;

    @ViewInject(R.id.albumupload_iv)ImageView albumupload_iv;
    @ViewInject(R.id.upload_filename)TextView upload_tv;
    @ViewInject(R.id.upload_progress)ProgressBar upload_progress;
    @ViewInject(R.id.tv_uploadfail)TextView tv_uploadfail;
    @ViewInject(R.id.ll_upload_process)LinearLayout ll_upload_process;
    @ViewInject(R.id.close_upload)ImageView iv_close_upload;
    @ViewInject(R.id.retry_upload)ImageView iv_retry_upload;
    @ViewInject(R.id.uploadlayout)View uploadView;

    @ViewInject(R.id.classphoto)OptimizeGridView gridView;
    @ViewInject(R.id.show_edit_albumname)LinearLayout ll_show_edit;
    @ViewInject(R.id.show_albumname_edit)EditText edit_album_name;
    @ViewInject(R.id.save_ablum_name_btn)Button btn_album_name;

    private ImagesAdapter mAdapter;
    private List<Object> albumlist = new ArrayList<Object>();
    private List<Object> newalbumlist = new ArrayList<Object>();
    public boolean editAction = false;
    public String albumtype = null;
    private Object object = null;
    private String albumid = null;
    private List<Upload> uploadList = new ArrayList<Upload>();
    List<SimpleTask> listTask = new ArrayList<SimpleTask>();
    List<UploadThread> listThread = new ArrayList<UploadThread>();
    static UploadDB uploadDB = null;
    private boolean isPause = false;
    ArrayList<String> photos = new ArrayList<String>();
    ArrayList<String> descs = new ArrayList<String>();
    String imageType = null;
    String module = null;
    private PullToRefreshView mPullToRefreshView;
    /**生活剪影|手工作品参数*/
    private String lifetype;

    private Term term;
    private List<String>uidlist = new ArrayList<String>();

    static {
        uploadDB = UploadDB.getInstnce();
    }
    public TaskExecutor.UpPhotoCallback upPhotoCallback;
    public int alreadUp;

    private UploadPhotosService upload_service;

//  public static final String UP_URL = "http://" + UpYunUtils.ClASSPHOTO_BUCKET
//            + ".b0.upaiyun.com/";

    // 换新的域名
    public static final String UP_URL = "http://" + UpYunUtils.ClASSPHOTO_BUCKET
              + ".yp.yeyimg.com";

    private int cid;
    private ImageLoader imageLoader;
    private final static String TAG = "ClassPhotoDetialManager";
    private boolean isActivityExist = true;

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActivityExist = false;
        EventBus.getDefault().unregister(this);
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();

        right_tv = null;
        tv_headerTitle = null;
        left_iv = null;

        albumupload_iv = null;
        upload_tv = null;
        upload_progress = null;
        tv_uploadfail = null;
        ll_upload_process = null;
        iv_close_upload = null;
        iv_retry_upload = null;
        uploadView = null;

        gridView = null;
        ll_show_edit = null;
        edit_album_name = null;
        btn_album_name = null;

        mAdapter = null;
        albumlist = null;
        object = null;
        albumid = null;
        uploadList = null;
        listTask = null;
        listThread = null;
        photos = null;
        descs = null;
        imageType = null;
        module = null;
        mPullToRefreshView = null;
        lifetype = null;
        term = null;
        imageLoader = null;
        setContentView(R.layout.activity_null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_browser_webimage);
        ViewUtils.inject(this);
        imageLoader = ImageLoader.getInstance();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//      EventBus.getDefault().register(this);  // 7- 27 龙衡东修改 BaseActivity已注册
        UtilsLog.i(UploadPhotosService.TAG, "onCreate Class Photo");
        initView();
    }

    private void initView() {
        left_iv.setVisibility(View.VISIBLE);
        right_tv.setText("编辑");
        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            right_tv.setVisibility(View.GONE);
        } else {
            right_tv.setVisibility(View.VISIBLE);
        }
        object = getIntent().getSerializableExtra(AppConstants.PARAM_ALBUM);    // 相册Album
        cid = getIntent().getIntExtra(AppConstants.PARAM_CID, 0);               // 班级id
        albumtype = getIntent().getStringExtra(AppConstants.INTENT_ALBUM_TYPE); // type：比如班级相册；生活剪影和手工作品等
        lifetype = getIntent().getStringExtra("lifetype");                      // 暂没用到
        term = (Term) getIntent().getSerializableExtra("term");                 //

        mPullToRefreshView = (PullToRefreshView)findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setVisibility(View.GONE);
        mAdapter = new ImagesAdapter(ClassPhotoDetialManager.this, albumlist, null, albumtype, ImageLoadOptions.getGalleryOptions(), imageLoader);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(this);
        // 内部点击事件，点击中
        mAdapter.setOnInViewClickListener(R.id.selectphoto_select, new ImagesAdapter.onInternalClickListener() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, Object values) {
                mAdapter.setCheck(position, parentV);
            }
        });
        mAdapter.setOnInViewClickListener(R.id.selectphoto_unselect, new ImagesAdapter.onInternalClickListener() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, Object values) {
                mAdapter.setCheck(position, parentV);
            }
        });
        loadData();
    }

    private void loadData() {
        AccountInfo info = AppServer.getInstance().getAccountInfo();
        showFailData(albumtype);        // 初始化下载条
        Album album = (Album) object;   // 获取相册
        albumid = album.getAlbumid();   // 相册id
        tv_headerTitle.setText(album.getAlbumName()); // 设置标题为相册名称
        loadPhoto(info, album);                       // 调接口加载相册数据
    }

    /**
     * 相册数据加载方法：通过相册id获取相册照片
     * @param info
     */
    private void loadPhoto(AccountInfo info, final Album palbum) {
        AppServer.getInstance().getPhotoByAlbumId(info.getUid() + "", palbum.getAlbumid(), new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (!isActivityExist) {
                    return;
                }
                if (newalbumlist!=null) {
                    newalbumlist.clear();
                }
                if (code == AppServer.REQUEST_SUCCESS) {
                    albumlist = (List<Object>) obj; // List<Album> ()
                    if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
                        newalbumlist.addAll(albumlist); // 这是为啥？
                    } else {
                        Album album = new Album();
                        album.setAlbumName("add");
                        album.setAlbumid(palbum.getAlbumid());
                        newalbumlist.add(album);
                        newalbumlist.addAll(albumlist);
                    }
                    if (mAdapter!=null) {
                        mAdapter.setType(AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                        mAdapter.setList(newalbumlist);
                        photos.clear();
                        descs.clear();
                        for (int i = 0; i < albumlist.size(); i++) {
                            Album imageitem = (Album) albumlist.get(i);
                            photos.add(imageitem.getFilepath());
                            descs.add(imageitem.getTitle());
                        }
                    }
                } else {
                    UtilsLog.i(TAG, "getPhotoByAlbumId fail ： " + message);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                if (editAction) {
                    // 取消编辑
                    editAction = false;
                    AppConstants.photocheckList.clear();
                    showDelView(editAction);
                    right_tv.setText("编辑");
                    left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
                } else {
                    if (!UpLoadManager.isupload) { // 如果不在下载中，直接关闭
                        Log.i(UploadPhotosService.TAG, "photos isuploading finish");
                        this.finish();
                    } else { // 如果处于下载中，进入ClassPhotoMainActivity界面
                        Log.i(UploadPhotosService.TAG, "photos isuploading ");
                        openActivity(ClassPhotoMainActivity.class);
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("InlinedApi")
    @OnClick(value={R.id.left_btn,R.id.right_tv,R.id.close_upload,R.id.retry_upload,
            R.id.id_edit_decs_btn,R.id.id_edit_delete_btn,R.id.save_ablum_name_btn})
    public void setOnClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    if (editAction) {
                        // 取消编辑
                        editAction = false;
                        AppConstants.photocheckList.clear();
                        showDelView(editAction);
                        right_tv.setText("编辑");
                        left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
                    } else {
                        if (!UpLoadManager.isupload) {
                            this.finish();
                            postEvent(AppEvent.HOMEFRAGMENT_REFRESH_ADDALBUM);
                        } else {
                            openActivity(ClassPhotoMainActivity.class);
                            postEvent(AppEvent.HOMEFRAGMENT_REFRESH_ADDALBUM);
                        }
                    }
                }
                break;
            case R.id.right_tv:
                if (UpLoadManager.isupload) {
                    showToast("图片正在上传！");
                    return;
                }
                if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    if (!editAction) {
                        editAction = true;
                        // 通知当前的fragment切换编辑状态
                        showDelView(editAction);
                        right_tv.setText("删除");
                        left_iv.setImageResource(R.drawable.icon_close);
                    } else {
                        // 提示删除并提交
                        final ArrayList<Album> checkList = mAdapter.getCheckList();
                        if (checkList.isEmpty()) {
                            showToast("请选择相册");
                            return;
                        } else {
                            editAction = false;
                            right_tv.setText("编辑");
                            left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
                            sumitDel();
                        }
                    }
                }
                break;
            case R.id.close_upload:
                showDialog("提示", "取消上传照片吗?", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        UpLoadManager.isupload = false;
                        uploadView.setVisibility(View.GONE);
                        try {
                            uploadList.clear();
                            listTask.clear();
                            listThread.clear();
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Upload.class); // 删除数据库
                            FileUtils.deleteDirectory(AppConfig.UPLOAD_PATH); // 删除目录
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.retry_upload:
                isPause = false;
                iv_close_upload.setVisibility(View.GONE);
                iv_retry_upload.setVisibility(View.GONE);
                tv_uploadfail.setVisibility(View.GONE);
                bindService(null, UploadPhotosService.Retry_Start_Action);
                break;
            case R.id.save_ablum_name_btn:  // 修改相册名称
                String type = btn_album_name.getText().toString();
                final String title = edit_album_name.getText().toString() == null ? "" : edit_album_name.getText().toString();
                if (type.equals("保存") && title.length() > 0) {
                    Album album = (Album) object;
                    btn_album_name.setText("编辑");
                    AppServer.getInstance().updateClassPhoto(album.getAlbumid(), cid, "", title, new OnAppRequestListener() {
                        @Override
                        public void onAppRequest(int code, String message, Object obj) {
                            if (!isActivityExist) {
                                return;
                            }
                            if (code == AppServer.REQUEST_SUCCESS) {
                                edit_album_name.setText(title);
                                tv_headerTitle.setText(title);
                                EditTextEditable(edit_album_name, false);
                            } else {
                                showToast("修改失败");
                                edit_album_name.setText("");
                                EditTextEditable(edit_album_name, true);
                                btn_album_name.setText("保存");
                            }
                        }
                    });
                } else {
                    edit_album_name.setText("");
                    EditTextEditable(edit_album_name, true);
                    btn_album_name.setText("保存");
                }
                break;
            default:
                break;
        }
    }

    /** 照片点击 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
        AppContext.checkList.clear();
        if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
            Album photo = (Album) parent.getItemAtPosition(position);
            if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
                // 编辑模式选择照片，否则浏览照片
                if (editAction) {
                    if (photo == null) {
                        return;
                    }
                    mAdapter.setCheck(position, view);
                } else {
                    Intent intent = new Intent(ClassPhotoDetialManager.this, PhotoManager_ViewPager.class);
                    Bundle bundler = new Bundle();
                    bundler.putStringArrayList("imglist", photos);
                    if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                        bundler.putSerializable(AppConstants.PARAM_ALBUM, (Album)object);
                    } else {
                        bundler.putSerializable(AppConstants.PARAM_ALBUM, (LifePhoto)object);
                    }
                    bundler.putString("type", albumtype);
                    bundler.putInt("position", position);
                    bundler.putStringArrayList("decslist", descs);
                    intent.putExtras(bundler);
                    startActivity(intent);
                }
            } else {
                if (editAction) {
                    if (photo == null) {
                        return;
                    }
                    mAdapter.setCheck(position, view);
                } else if (position == 0) {
                    if (UpLoadManager.isupload) {
                        showToast("图片正在上传！");
                        return;
                    }
                    AppContext.checkList.clear();
//                  Intent i = new Intent(CommonBrowserWebImage.this, GetSDCardAlbumActivity.class);
                    Intent i = new Intent(ClassPhotoDetialManager.this, GalleryActivity.class);
                    i.putExtra("typefrom", AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                    i.putExtra("album", (Album)object);
                    i.putExtra(AppConstants.PARAM_ALBUMID, photo.getAlbumid());
                    startActivity(i);
                } else {
                    Intent intent = new Intent(ClassPhotoDetialManager.this, PhotoManager_ViewPager.class);
                    Bundle bundler = new Bundle();
                    bundler.putStringArrayList("imglist", photos);
                    if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                        bundler.putSerializable(AppConstants.PARAM_ALBUM, (Album)object);
                    } else {
                        bundler.putSerializable(AppConstants.PARAM_ALBUM, (LifePhoto)object);
                    }
                    bundler.putString("type", albumtype);
                    bundler.putInt("position", position - 1);
                    bundler.putStringArrayList("decslist", descs);
                    intent.putExtras(bundler);
                    startActivity(intent);
                }
            }
        } else {
            Intent intent = new Intent(ClassPhotoDetialManager.this, PhotoManager_ViewPager.class);
            Bundle bundler = new Bundle();
            bundler.putStringArrayList("imglist", photos);
            bundler.putStringArrayList("decslist", descs);
            bundler.putString("type", albumtype);
            intent.putExtras(bundler);
            startActivity(intent);
        }
    }

    public void showDelView(boolean action) {
        if (action) {
            mAdapter.setAction(true);
            editAction = true;
            left_iv.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
            ll_show_edit.setVisibility(View.VISIBLE);
            EditTextEditable(edit_album_name,true);
            btn_album_name.setText("保存");
        } else {
            mAdapter.setAction(false);
            editAction = false;
            ll_show_edit.setVisibility(View.GONE);
            EditTextEditable(edit_album_name,false);
            btn_album_name.setText("保存");
        }
    }

    public void EditTextEditable(EditText editText, boolean editAction) {
        editText.setFocusable(editAction);
        editText.setEnabled(editAction);
        editText.setFocusableInTouchMode(editAction);
    }

    /**
     * 提交删除
     */
    public void sumitDel() {
        if (AppConstants.photocheckList.isEmpty() || AppConstants.photocheckList.size() == 0) {
            showToast("请选择相片");
            showDelView(false);
            return;
        }

        showDialog("删除相册提示", "你选择了删除" + AppConstants.photocheckList.size() + "个相片", "删除", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                StringBuffer sb = new StringBuffer();
                for (Object album : AppConstants.photocheckList) {
                    sb.append(((Album)album).getPhotoid() + ",");
                }
                String array = sb.toString();
                if (array.length() == 0) {
                    showToast("删除失败");
                    UtilsLog.i(TAG, "delete fail because photoid is null value");
                    return;
                }
                array = array.substring(0, array.length() - 1);
                AccountInfo info = AppServer.getInstance().getAccountInfo();
                AppServer.getInstance().deleteClassPhotoGalley(array, info.getUid(), new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (!isActivityExist) {
                            return;
                        }
                        if (code == AppServer.REQUEST_SUCCESS){
                            showToast("删除成功");
                            List<Object> temp = mAdapter.getList();
                            for (Object album : AppConstants.photocheckList) {
                                temp.remove((Object)album);
                            }
                            mAdapter.setList(temp);
                            showDelView(false);
                            AppConstants.photocheckList.clear();
                            photos.clear();
                            descs.clear();
                            for (int i = 0; i < temp.size(); i++) {
                                    Album imageitem = (Album) temp.get(i);
                                    photos.add(imageitem.getFilepath());
                                    descs.add(imageitem.getTitle());
                            }
                        } else {
                            showToast("删除失败");
                            showDelView(false);
                        }
                    }
                });
            }
        }, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                showDelView(false);
                ImagesAdapter.getCheckList().clear();
                AppConstants.photocheckList.clear();
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            upload_service = ((UploadPhotosService.MyBinder)iBinder).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            upload_service = null;
        }
    };

    private String desc = ""; // 照片评论
    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(UploadPhotosService.TAG, "onNewIntent Class Photo");
        if (intent!=null) {
            object = intent.getSerializableExtra(AppConstants.PARAM_ALBUM);
            Bundle bundle = intent.getExtras();
            albumtype = bundle.getString(AppConstants.INTENT_ALBUM_TYPE);
            if (!albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                term = (Term) bundle.getSerializable("term");
            }
            String type = intent.getStringExtra("upload");
            if (object!=null && type == null) {
                if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    // 加载相册
                    Album album = (Album) object;
                    albumid = album.getAlbumid();
                    tv_headerTitle.setText(album.getAlbumName());
                    AccountInfo info = AppServer.getInstance().getAccountInfo();
                    loadPhoto(info, album);
                    if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
                        right_tv.setVisibility(View.GONE);
                    } else {
                        right_tv.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                // 上传照片
                if (bundle!=null) {
                    newalbumlist = mAdapter.getList();
                    ArrayList<Object> temp = new ArrayList<Object>();
                    temp.add(newalbumlist.get(0));
                    ArrayList<Photo> newPhotoList = bundle.getParcelableArrayList(AppConstants.PHOTOLIST);
                    imageType = bundle.getString(AppConstants.INTENT_IMAGE_TYPE);
                    desc = bundle.getString(AppConstants.INTENT_DESC);
                    bindService(newPhotoList, UploadPhotosService.First_Start_Action);
                    uploadView.setVisibility(View.VISIBLE);

//                  module = bundle.getString(AppConstants.INTENT_UPLOAD_TYPE) == null ? "" : bundle.getString(AppConstants.INTENT_UPLOAD_TYPE);
//                  if (module.equals(AppConstants.PARAM_UPLOAD_LIFE) || module.equals(AppConstants.PARAM_UPLOAD_WORK)) {
//                      term = (Term) bundle.getSerializable("term");
//                  }
//                  albumtype = module;
//                  desc = bundle.getString(AppConstants.INTENT_DESC);
//                  if (newPhotoList!=null && newPhotoList.size() > 0) {
//                      if (!module.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
//                          if (module.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
//                              // 班级相册
//                              for (Photo p : newPhotoList) {
//                              temp.add(new Album("", "", "", "", 0, "", p.imgPath));
//                          }
//                      } else if (module.equals(AppConstants.PARAM_UPLOAD_LIFE)|| module.equals(AppConstants.PARAM_UPLOAD_WORK)) {
//                          // 生活剪影|手工作品
//                          for (Photo p : newPhotoList) {
//                              temp.add(new WLImage(0, p.imgPath, desc, ""));
//                          }
//                      }
//                      if (newPhotoList!=null && newPhotoList.size() > 0) {
//                          retryUpload(newPhotoList);
//                          issave = true;
//                      }
//                      if (uploadList.size() > 0) {
//                          try {
//                              uploadTask(uploadList);
//                          } catch (IllegalStateException e) {
//                              e.printStackTrace();
//                              Toast.makeText(this, "上传失败", Toast.LENGTH_LONG).show();
//                          }
//                      } else {
//                          uploadView.setVisibility(View.GONE);
//                          return;
//                      }
//                      if (photos!=null) {
//                          photos.clear();
//                      }
//                      if (descs!=null) {
//                          descs.clear();
//                      }
//                      if (module.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
//                          for(int i = 1; i < temp.size(); i++) {
//                              Album imageitem = (Album) temp.get(i);
//                              photos.add(imageitem.getFilepath());
//                              descs.add(imageitem.getTitle());
//                          }
//                      } else {
//                          for (int i = 1; i < temp.size(); i++) {
//                              WLImage imageitem = (WLImage) temp.get(i);
//                              if (imageitem.getM_path()!=null && photos!=null) {
//                                  photos.add(imageitem.getM_path());
//                              }
//                              if (imageitem.getPhoto_desc()!=null) {
//                                  descs.add(imageitem.getPhoto_desc());
//                              }
//                          }
//                      }
//                      right_tv.setVisibility(View.VISIBLE);
//                  }
//              }

                }
            }
        }
    }

    /**
     * 上传完成，发送信息给服务器
     * @param info
     * @param album
     */
    public void postAlfterUpload(final AccountInfo info, final Album album) {
        AppServer.getInstance().insertAlbumParams(info.getUid(), albumid, StringUtils.changeListToString(upload_service.getUploadList(), upload_service.getPhotos()), desc, new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                if (!isActivityExist) {
                    return;
                }
                 if (code == AppServer.REQUEST_SUCCESS) {
                     desc = "";
                     loadPhoto(info, album);
                     postEvent(AppEvent.HOMEFRAGMENT_REFRESH_ADDALBUM);
                 }
            }
        });
    }

    private int uploadCout = 0;
    public void onEventMainThread(AppEvent event) {
        if (event.getType() == AppEvent.SERVICE_CLASSPHOTO_UPLOADING) {
            Message msg = event.getMsg();
            String upload = upload_service.getPhotos().get(event.getPosition()).imgPath;
            File file = new File(upload);
            UtilsLog.i(TAG, "accepte msg, msg.what : " + msg.what + "uploadCout : " + uploadCout + " alreadUp : " + alreadUp + "upload_service.getPhotos().size()" + upload_service.getPhotos().size());
            if (msg.what == UpLoadManager.UPLOAD_FAILL) {
                uploadCout++;
                UtilsLog.i(TAG, "上传失败");
                Log.i(UploadPhotosService.TAG, "OnEventMainThread is UploadFaild and position = " + event.getPosition() + "first");
//              UpLoadManager.isupload = false;
//              Long fileid = uploadDB.getBindId(file.getAbsolutePath());
//              Long length = (Long) msg.obj;
//              uploadDB.updateUpload(fileid, length);

//                if ((event.getPosition() + 1) == AppContext.checkList.size()) {
//                    Log.i(UploadPhotosService.TAG, "OnEventMainThread is UploadFaild  ShowFailData and position = " + event.getPosition());
//                    AccountInfo info = AppServer.getInstance().getAccountInfo();
//                    showFailData(AppConstants.PARAM_UPLOAD_CLASSPHOTO);
//                    unbindService(conn);
//                    Album album = new Album();
//                    album.setAlbumid(albumid);
//                    postAlfterUpload(info, album);
//                }

            } else if (msg.what ==  UpLoadManager.UPLOAD_SUCCESS) {
                UtilsLog.i(TAG, "上传成功");
                uploadCout++;
                alreadUp++;
                // 删除已上传的数据
                uploadDB.delUpload(file.getAbsolutePath());
                UtilsLog.i(TAG, "OnEventMainThread is UoloadSuceess and position = " + alreadUp);
                // if ((event.getPosition() + 1) == uploadList.size()) {
                if (alreadUp == upload_service.getPhotos().size()) {      // 超哥写的 && UploadPhotosService.loadFailFlag
                    alreadUp = 0;
                    uploadCout = 0;
                    UtilsLog.i(TAG, "OnEventMainThread is UoloadSuceess finish");
                    UpLoadManager.isupload = false;
                    uploadView.setVisibility(View.GONE);
                    tv_uploadfail.setVisibility(View.GONE);
                    iv_close_upload.setVisibility(View.GONE);
                    iv_retry_upload.setVisibility(View.GONE);
                    uploadList.clear();
                    EventBus.getDefault().post(new AppEvent(AppEvent.UPLOAD_FINISH));
                    FileUtils.deleteDirectory(AppConfig.UPLOAD_PATH);

                    AccountInfo info = AppServer.getInstance().getAccountInfo();
                    if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                        unbindService(conn);
                        Album album = new Album();
                        album.setAlbumid(albumid);
                        // 上传完成后，发送消息给服务器
                        postAlfterUpload(info, album);
                    }
                } else {
                    upload_tv.setText("已上传" + uploadCout + "张，共" + upload_service.getPhotos().size() + "张 .");
                    GlideUtils.loadGeneralImage(AppContext.getInstance(), "file:///" + upload_service.getPhotos().get(event.getPosition() + 1).imgPath, albumupload_iv);
//                    ImageLoader.getInstance().displayImage("file:///" + upload_service.getPhotos().get(event.getPosition() + 1).imgPath, albumupload_iv, ImageLoadOptions.getOptions());
                }
            } else if (msg.what == UpLoadManager.UPLOAD_UPDATE) {
                UtilsLog.i(TAG, "正在上传");
                if (!isPause) {
//                  upload_tv.setText("正在上传" + (event.getPosition() + 1) + "/" + uploadList.size());
//                    upload_tv.setText("正在上传中......共" + upload_service.getPhotos().size() + "张");
                    upload_tv.setText("已上传" + uploadCout + "张，共" + upload_service.getPhotos().size() + "张");
                    upload_progress.setVisibility(View.VISIBLE);
                    upload_tv.setVisibility(View.VISIBLE);
                    tv_uploadfail.setVisibility(View.GONE);
                    Long length = (Long) msg.obj;
                    Long fileid = uploadDB.getBindId(file.getAbsolutePath());
                    uploadDB.updateUpload(fileid, length);
                    // 当前进度值
                    int progress = (int) (((float) length / file.length()) * 100);
                    upload_progress.setProgress(progress);
                }
            } else {
                UtilsLog.i(TAG, "其他");
                uploadCout++;
            }
        } else if (event.getType() == AppEvent.UPLOAD_PAUSE) {
            uploadCout++;
            UtilsLog.i(TAG, "上传暂停");
            Message msg = event.getMsg();
            Upload upload = uploadList.get(event.getPosition());
            File file = new File(upload.getUploadfilepath());
            isPause = true;
            showFailData(upload.getModule());
            UpLoadManager.isupload = false;
        } else if (event.getType() == AppEvent.NetERROR) {
            uploadCout++;
            UtilsLog.i(TAG, "网络错误");
            Message msg = event.getMsg();
            Upload upload = uploadList.get(event.getPosition());
            File file = new File(upload.getUploadfilepath());
            isPause = true;
            showFailData(upload.getModule());
            UpLoadManager.isupload = false;
        }
        UtilsLog.i(TAG, "准备处理结果：uploadCout is : " + uploadCout + "photos.size is " + upload_service.getPhotos().size());
        if (uploadCout == upload_service.getPhotos().size() || UploadPhotosService.loadFailFlag) { // (event.getPosition() + 1) == AppContext.checkList.size()
            uploadCout = 0;
            UpLoadManager.isupload = false;
            Log.i(UploadPhotosService.TAG, "OnEventMainThread is UploadFaild  ShowFailData and position = " + event.getPosition());
            UtilsLog.i(TAG, "图片张数： " + upload_service.getPhotos().size());
            AccountInfo info = AppServer.getInstance().getAccountInfo();
            showFailData(AppConstants.PARAM_UPLOAD_CLASSPHOTO);
            unbindService(conn);
            Album album = new Album();
            album.setAlbumid(albumid);
            postAlfterUpload(info, album);
        }

    }

    /** Upload数据库如果对应的模块还有数据，则显示上传失败的view */
    private void showFailData(String type) {
        List<Upload> faillist = UploadDB.getInstnce().getFileList(type); // 上传的图片对应的模块为type的uploads
        if (faillist!=null && faillist.size() > 0) {
            uploadView.setVisibility(View.VISIBLE);
            tv_uploadfail.setText(faillist.size() + "张上传失败,请重试");
            tv_uploadfail.setVisibility(View.VISIBLE);
            upload_progress.setVisibility(View.GONE);
            upload_tv.setVisibility(View.GONE);
            iv_close_upload.setVisibility(View.VISIBLE);
            iv_retry_upload.setVisibility(View.VISIBLE);
        } else {
            uploadView.setVisibility(View.GONE);
        }
    }

    public void postEvent(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent(type));
                System.out.println("PostThreadId:" + Thread.currentThread().getId());
            }
        }).start();
    }

    /**
     * 绑定服务
     */
    private void bindService(ArrayList<Photo>newPhotoList, String type) {
        uploadCout = 0;
        alreadUp = 0;
        UploadPhotosService.loadFailFlag = false;
        Intent intent = new Intent(ClassPhotoDetialManager.this, UploadPhotosService.class);
        Bundle bundle = new Bundle();

        if (type.equals(UploadPhotosService.First_Start_Action)) {
            // 第一次上传需要的参数
            bundle.putParcelableArrayList(AppConstants.PHOTOLIST, newPhotoList);
            intent.putExtra(AppConstants.INTENT_IMAGE_TYPE, imageType);
        }

        intent.putExtra(AppConstants.PARAM_ALBUMID, albumid);
        intent.putExtra("type", type);
        intent.putExtras(bundle);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

}
