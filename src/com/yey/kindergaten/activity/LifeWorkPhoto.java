/**
 * 
 */
package com.yey.kindergaten.activity;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.yey.kindergaten.bean.WLImage;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.db.UploadDB;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.net.URL;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.task.SimpleTask;
import com.yey.kindergaten.task.TaskExecutor;
import com.yey.kindergaten.task.TaskExecutor.OrderedTaskExecutor;
import com.yey.kindergaten.util.AppConfig;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.FileUtils;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.StringUtils;
import com.yey.kindergaten.util.UpLoadManager;
import com.yey.kindergaten.util.UploadThread;
import com.yey.kindergaten.widget.PullToRefreshView;
import com.yey.kindergaten.widget.PullToRefreshView.OnFooterRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * @author chaowen
 *公用的加载网络图片界面
 */
public class LifeWorkPhoto extends BaseActivity implements OnItemClickListener, OnFooterRefreshListener {

	@ViewInject(R.id.right_tv)TextView right_tv;
	@ViewInject(R.id.header_title)TextView tv_headerTitle ;
	@ViewInject(R.id.left_btn)ImageView left_iv;
	@ViewInject(R.id.gridview)GridView gridview;
	@ViewInject(R.id.albumupload_iv)ImageView albumupload_iv;
	@ViewInject(R.id.upload_filename)TextView upload_tv;
	@ViewInject(R.id.upload_progress)ProgressBar upload_progress;
	@ViewInject(R.id.tv_uploadfail)TextView tv_uploadfail;
	@ViewInject(R.id.ll_upload_process)LinearLayout ll_upload_process;
	@ViewInject(R.id.close_upload)ImageView iv_close_upload;
	@ViewInject(R.id.retry_upload)ImageView iv_retry_upload;
	@ViewInject(R.id.uploadlayout)View uploadView;
	
	@ViewInject(R.id.id_show_edit_ll)LinearLayout showedit_rl;
	@ViewInject(R.id.id_edit_decs_btn)Button decs_btn;
	@ViewInject(R.id.id_edit_delete_btn)Button del_btn;
	private ImagesAdapter mAdapter;
	private List<Object> albumlist = new ArrayList<Object>();
	private List<Object> newalbumlist = new ArrayList<Object>();
	public boolean editAction  = false;
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
	private int page = 1;
	private int size;
    private List<String>decs_list = new ArrayList<String>();
    private View bottomView;
    private List<String> photoidlist= new ArrayList<String>();
    private ArrayList<WLImage> checkList;
    private Term term;
	String desc = "";
	private String flag = "2"; // 0表示全选，1表示取消，2表示都不显示
	private String terms;
	private boolean load_more = true;
	List<Object> lis = new ArrayList<Object>();
	List<WLImage>wl_list = new ArrayList<WLImage>();
    private List<String>uidlist = new ArrayList<String>();

    static {
        uploadDB = UploadDB.getInstnce();
    }
    public TaskExecutor.UpPhotoCallback upPhotoCallback;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                uploadView.setVisibility(View.VISIBLE);
            } else if (msg.what == 1) {
                uploadTask((List<Upload>) msg.obj);
            } else if (msg.what == 2) {
                Toast.makeText(LifeWorkPhoto.this,((List<Photo>)msg.obj).get(msg.arg1).imgPath + "照片不可用", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.common_browser_webimage);
    	ViewUtils.inject(this);
        if (!EventBus.getDefault().isRegistered(this)){
             EventBus.getDefault().register(this); // 7- 27 龙衡东修改 BaseActivity已注册
        }
    	initView();   
    }

	private void initView() {
		left_iv.setVisibility(View.VISIBLE);
		right_tv.setVisibility(View.VISIBLE);
		right_tv.setText("编辑");
		object = getIntent().getSerializableExtra(AppConstants.PARAM_ALBUM);
		albumtype = getIntent().getStringExtra(AppConstants.INTENT_ALBUM_TYPE);
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.main_pull_refresh_view);
	    mPullToRefreshView.setOnFooterRefreshListener(this);
        mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
		lifetype = getIntent().getStringExtra("lifetype");
		term = (Term) getIntent().getSerializableExtra("term");
		mAdapter = new ImagesAdapter(LifeWorkPhoto.this, albumlist, null, albumtype, ImageLoadOptions.getGalleryOptions(),ImageLoader.getInstance());
		gridview.setAdapter(mAdapter);
		gridview.setOnItemClickListener(this);

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

		if (albumtype.equals(AppConstants.PARAM_UPLOAD_BATCH) && getIntent().getExtras()!=null) {
			right_tv.setVisibility(View.GONE);
			ArrayList<Object> temp = new ArrayList<Object>();
			terms = getIntent().getExtras().getString("terms");
			ArrayList<Photo> newPhotoList =  getIntent().getExtras().getParcelableArrayList(AppConstants.PHOTOLIST);
			imageType = getIntent().getExtras().getString(AppConstants.INTENT_IMAGE_TYPE);
			module = getIntent().getExtras().getString(AppConstants.INTENT_UPLOAD_TYPE);
			if (module!=null) {
				uploadView.setVisibility(View.VISIBLE);
			}
			desc = getIntent().getExtras().getString(AppConstants.INTENT_DESC);
			tv_headerTitle.setText("批量上传");
			uidlist = getIntent().getExtras().getStringArrayList("childlist");

			if (module!=null && module.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
				for (Photo p : newPhotoList) {
//					String path = FileUtils.saveAsCommon(p.imgPath, AppConfig.UPLOAD_PATH);
					temp.add(new WLImage(0, p.imgPath,desc,""));
				}
				for (int i = 0; i < temp.size(); i++) {
					WLImage imageitem = (WLImage) temp.get(i);
					photos.add(imageitem.getM_path());
					descs.add(desc);
				}
			}
			mAdapter.setType(module);
			mAdapter.setList(temp);

			if (newPhotoList!=null && newPhotoList.size() > 0) {
				retryUpload(newPhotoList);
			}

            if (uploadList.size() > 0) {
                uploadTask(uploadList);
            } else {
                uploadView.setVisibility(View.GONE);
                return;
            }
		}
		loadData();
	}

	private void loadData() {
		AccountInfo info = AppServer.getInstance().getAccountInfo();
		if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
			showFailData(albumtype);
			Album album = (Album) object;
			albumid = album.getAlbumid();
			tv_headerTitle.setText(album.getAlbumName());
			loadPhoto(info, album);
		} else if (albumtype.equals(AppConstants.PARAM_UPLOAD_LIFE) || albumtype.equals(AppConstants.PARAM_UPLOAD_WORK)) {
            if (object!=null) {
                LifePhoto photo = (LifePhoto) object;
                tv_headerTitle.setText(photo.getName());
                showLoadingDialog("正在加载...");
                loadPhoto(photo,page,17);
            }
//          List<Upload> faillist = uploadDB.getFileList("7");
//          if(faillist!=null&&faillist.size()!=0){
//              showFailData("7");
//              return;
//          }
			showFailData(albumtype);
		}
	}
	
	/**
	 * 相册数据加载方法
	 * @param info
	 */
	private void loadPhoto(AccountInfo info, final Album palbum) {
		AppServer.getInstance().getPhotoByAlbumId(info.getUid() + "", palbum.getAlbumid(), new OnAppRequestListener() {
			@Override
			public void onAppRequest(int code, String message, Object obj) {
				newalbumlist.clear();
				if (code == AppServer.REQUEST_SUCCESS) {
					albumlist = (List<Object>) obj;
					Album album = new Album();
					album.setAlbumName("add");
					album.setAlbumid(palbum.getAlbumid());
					newalbumlist.add(album);
					newalbumlist.addAll(albumlist);
					mAdapter.setType(AppConstants.PARAM_UPLOAD_CLASSPHOTO);
					mAdapter.setList(newalbumlist);
					photos.clear();
					descs.clear();

                    for (int i = 0; i < albumlist.size(); i++) {
                        Album imageitem=(Album) albumlist.get(i);
                        photos.add(imageitem.getFilepath());
                        descs.add(imageitem.getTitle());
                    }
				 } else {
				 }
			}
		});
	}
	
	/**
	 * 生活剪影|手工作品分页显示
	 * @param photo
	 * @param pages
	 * @param size
	 */
	private void loadPhoto(LifePhoto photo,final int pages,int size) {
		AppServer.getInstance().getChildLifePhoto(lifetype,page,size, photo.getGbid(), new OnAppRequestListener() {
			@Override
		    public void onAppRequest(int code, String message, Object obj) {
                if (code == 0) {
                    page++;
            	    newalbumlist.clear();
            	    if (photos!=null) {
            		    photos.clear();
            	    } else {
            		    photos = new ArrayList<String>();
            	    }
            	    if (decs_list!=null) {
            		    decs_list.clear();
            	    } else {
            		    decs_list = new ArrayList<String>();
            	    }
            			 
            	    WLImage wlphoto = null;
            	    albumlist = (List<Object>) obj;
            	    if (pages == 1 && !newalbumlist.contains("add")) {
            		    wlphoto = new WLImage();
    				    wlphoto.setM_path("add");
    				    newalbumlist.add(wlphoto);
            	    }
//				    mAdapter.setType(AppConstants.PARAM_UPLOAD_LIFE);
				    Iterator<Object>it = albumlist.iterator();
				    while (it.hasNext()) {
					    wlphoto = (WLImage) it.next();
					    newalbumlist.add(wlphoto);
				    }
				    for (int i = 0; i < newalbumlist.size(); i++) {
					    WLImage wl= (WLImage) newalbumlist.get(i);
                        if (wl_list!=null && wl!=null) {
                            wl_list.add(wl);
                        } else {
                            return;
                        }
				    }
 				    if (pages > 1) {
					    mAdapter.setLists(wl_list);
					    gridview.smoothScrollToPosition(wl_list.size() - 15);
                        if (checkList!=null) {
                            checkList.clear();
                        }
//                      mAdapter.getCheckImageList().clear();
                        if (right_tv.getText().toString().equals("取消")) {
                            showEditView("0");
                        } else if (right_tv.getText().toString().equals("全选")) {
                            showEditView("1");
                        }
				    } else {
					    mAdapter.setList(newalbumlist);
				    }
				    mPullToRefreshView.onFooterRefreshComplete();
					for (int i = 0; i < mAdapter.getList().size(); i++) {
                        WLImage imageitem = (WLImage) mAdapter.getList().get(i);
                        photos.add(imageitem.getM_path());
                        decs_list.add(imageitem.getPhoto_desc());
					}
                    cancelLoadingDialog();
                } else if (code == 1) {
            	    if (wl_list == null || wl_list.size() == 0) {
            		    newalbumlist.clear();
            		    mAdapter.setType(AppConstants.PARAM_UPLOAD_LIFE);
            		    albumlist = (List<Object>) obj;
      				    WLImage wlphoto = new WLImage();
      				    wlphoto.setM_path("add");
      				    newalbumlist.add(wlphoto);
      				    mAdapter.setList(newalbumlist);
            	    } else {
               		    mPullToRefreshView.onFooterRefreshComplete();
            		    showToast("已经没有数据了");
            	    }
                    cancelLoadingDialog();
                } else if (code == -2) {
                    cancelLoadingDialog();
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
				    if (!UpLoadManager.isupload) {
				    	this.finish();
                    } else {
                        openActivity(ClassPhotoMainActivity.class);
                    }
				}
			} else {
				if (flag.equals("0") || flag.equals("1")) {
					editAction = true;
				} else {
                    editAction = false;
                }
                if (editAction) {
                    AppConstants.photocheckList.clear();
                    showEditView("2");
                    right_tv.setText("编辑");
                    left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
                } else {
                    Intent intent = new Intent(this,ServiceLifePhotoMainActivity.class);
                    intent.putExtra("type", lifetype);
                    startActivity(intent);
                    if (!UpLoadManager.isupload) {
                        this.finish();
                    }
                }
            }
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@SuppressLint("InlinedApi")
	@OnClick(value={R.id.left_btn,R.id.right_tv,R.id.close_upload,R.id.retry_upload,
			  R.id.id_edit_decs_btn,R.id.id_edit_delete_btn})
	public void setOnClick(View view){
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
                        } else {
                            openActivity(ClassPhotoMainActivity.class);
                        }
                    }
                } else {
                    if (flag.equals("0") || flag.equals("1")) {
                        editAction = true;
                    } else {
                        editAction = false;
                    }
                    if (editAction) {
                        AppConstants.photocheckList.clear();
                        showEditView("2");
                        right_tv.setText("编辑");
                        left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
                    } else {
                        Intent intent = new Intent(this,ServiceLifePhotoMainActivity.class);
                        intent.putExtra("type", lifetype);
                        startActivity(intent);
                        if (!UpLoadManager.isupload) {
                            this.finish();
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
                } else {
                    if (right_tv.getText().toString().equals("编辑")) {
                        showEditView("1");
                        return;
                    }
                    if (right_tv.getText().toString().equals("取消")) {
                        showEditView("1");
                    } else {
                        showEditView("0");
                    }
                }
                break;
            case R.id.id_edit_decs_btn:
                checkList = mAdapter.getCheckImageList();
                photoidlist.clear();
                if (checkList!=null && checkList.isEmpty()) {
                    showToast("请选择相片后在编辑");
                } else {
                    Iterator<WLImage> it = checkList.iterator();
                    while (it.hasNext()) {
                        WLImage wlImage = (WLImage) it.next();
                        photoidlist.add(wlImage.getPhotoid()+"");
                    }
                    final EditText et = new EditText(this);
                    et.setMinHeight(80);
                    et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

                    et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                    et.setPadding(10, 5, 0, 0);
                    et.setHint("请输入少于20个字符");
                    et.setBackground(null);

                    showDialogs("请输入描述内容", et, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showLoadingDialog("正在加载");
                            String photoids = null;
                            StringBuffer buffer = new StringBuffer();
                            for (int i = 0; i < photoidlist.size(); i++) {
                                buffer.append(photoidlist.get(i)).append(",");
                                if (i == photoidlist.size() - 1) {
                                    buffer.append(photoidlist.get(i));
                                }
                            }
                            photoids = buffer.toString();
                            AppServer.getInstance().editChldPhoto(lifetype,photoids, et.getText().toString(), new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {
                                    if (code == 0) {
                                        List<Object> temp = mAdapter.getList();
                                        for (Object album : AppConstants.checklist) {
                                            ((WLImage) album).setPhoto_desc(et.getText().toString());
                                        }
                                        mAdapter.setList(temp);
                                        RefreshPhoto();
                                        showToast("描述修改成功");
                                        AppConstants.checklist.clear();
                                        photos.clear();
                                        if (descs!=null) {
                                            descs.clear();
                                        }
                                        if (decs_list!=null) {
                                            decs_list.clear();
                                        }
                                        for (int i = 0; i < temp.size(); i++) {
                                            WLImage imageitem = (WLImage) temp.get(i);
                                            photos.add(imageitem.getM_path());
                                            decs_list.add(imageitem.getPhoto_desc());
                                        }
                                    } else {
                                        RefreshPhoto();
                                        showToast("修改失败");
                                        AppConstants.checklist.clear();
                                    }
                                }
                            });
                        }
                    });
                }
                break;
            case R.id.id_edit_delete_btn:
                photoidlist.clear();
                checkList = mAdapter.getCheckImageList();
                if (checkList!=null && checkList.isEmpty()) {
                    showToast("请选择相片后在删");
                } else {
                    Iterator<WLImage> it = checkList.iterator();
                    while (it.hasNext()) {
                        WLImage wlImage = (WLImage) it.next();
                        photoidlist.add(wlImage.getPhotoid() + "");
                    }
                    final String photoids;
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < photoidlist.size(); i++) {
                        buffer.append(photoidlist.get(i)).append(",");
                        if (i == photoidlist.size() - 1) {
                            buffer.append(photoidlist.get(i));
                        }
                    }
                    photoids = buffer.toString();
                    showDialog("删除照片", "您选择了" + photoidlist.size() + "张图片，确定删除吗？", "确定", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            showLoadingDialog("正在加载");
                            AppServer.getInstance().deleteChildPhoto(photoids, new OnAppRequestListener() {
                                @Override
                                public void onAppRequest(int code, String message, Object obj) {
                                    if (code == 0) {
        //						    	List<Object> temp = mAdapter.getList();
        //								for(Object album:  AppConstants.checklist){
        //									temp.remove((Object)album);
        //								}
        //								mAdapter.setList(temp);
                                        page = 1;
                                        mAdapter.delAdpaterPhoto((List)AppConstants.checklist);
                                        if (wl_list!=null && wl_list.containsAll(AppConstants.checklist)) {
                                            wl_list.removeAll((List)AppConstants.checklist);
                                        }
                                        showLoadingDialog("正在加载...");
                                        loadPhoto((LifePhoto)object, 1, 17);
                                        photoidlist.clear();
                                        RefreshPhoto();
                                        AppConstants.checklist.clear();
                                        showToast("删除成功");
                                    } else {
                                        RefreshPhoto();
                                        showToast("删除失败");
                                        AppConstants.checklist.clear();
                                    }
                                }
                            });
                        }
                    });
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
                            DbHelper.getDB(AppContext.getInstance()).deleteAll(Upload.class);
                            FileUtils.deleteDirectory(AppConfig.UPLOAD_PATH);
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
                uploadList.clear();
                listTask.clear();
                listThread.clear();

                uploadList.addAll(uploadDB.getFileList(albumtype));
                uploadTask(uploadList);
                break;
            default:
                break;
            }
	}
	
	/**
	 * 刷新动作
	 */
	public void RefreshPhoto(){
        right_tv.setText("编辑");
        showedit_rl.setVisibility(View.GONE);
        editAction = false;
        showEditView("2");
		loadingdialog.dismiss();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
	    AppContext.checkList.clear();
        if (AppUtils.getmem_UNUSED(this) < 1024 * 500) {
            ImageLoader.getInstance().clearMemoryCache();
            System.gc();
        }
		if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
		    Album photo = (Album) parent.getItemAtPosition(position);
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
                Intent i = new Intent(LifeWorkPhoto.this, GalleryActivity.class);
                i.putExtra("typefrom", AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                i.putExtra("album", photo);
                i.putExtra(AppConstants.PARAM_ALBUMID, photo.getAlbumid());
                startActivityForResult(i, AppConstants.REQUESTCODE_TAKE_LOCAL);
            } else {
                Intent intent = new Intent(LifeWorkPhoto.this, PhotoManager_ViewPager.class);
                Bundle bundler = new Bundle();
                bundler.putStringArrayList("imglist", photos);
                if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    bundler.putSerializable(AppConstants.PARAM_ALBUM, (Album)object);
                } else {
                    bundler.putSerializable(AppConstants.PARAM_ALBUM, (LifePhoto)object);
                }
                bundler.putString("type", albumtype);
                bundler.putInt("position", position-1);
                bundler.putStringArrayList("decslist", descs);
                intent.putExtras(bundler);
                startActivity(intent);
            }
	    } else if (albumtype.equals(AppConstants.PARAM_UPLOAD_LIFE) ||
				     albumtype.equals(AppConstants.PARAM_UPLOAD_WORK)) {
		    if (flag.equals("0") || flag.equals("1")) {
				editAction = true;
		    } else {
				editAction = false;
			}
			if (editAction) {
	            mAdapter.setCheck(position, view);
                del_btn.setText("删除照片" + "(" + mAdapter.getCheckImageList().size() + ")");
                decs_btn.setText("编辑描述" + "(" + mAdapter.getCheckImageList().size() + ")");
			} else if (position == 0) {
				if (UpLoadManager.isupload) {
					showToast("图片正在上传！");
					return;
				}
				AppContext.checkList.clear();
				Intent i = new Intent(LifeWorkPhoto.this, GalleryActivity.class);
				i.putExtra("typefrom", albumtype);
                i.putExtra("lifetype", flag);
				i.putExtra("photo", (LifePhoto)object);	
				i.putExtra("term", term);
				startActivity(i);
			} else {
				Intent intent = new Intent(LifeWorkPhoto.this, PhotoManager_ViewPager.class);
				Bundle bundler = new Bundle();
				if (photos.get(0).contains("add")) {
					photos.remove(0);
					decs_list.remove(0);
				}
				bundler.putStringArrayList("imglist", photos);
				bundler.putInt("page", page);
				bundler.putStringArrayList("decslist", (ArrayList<String>) decs_list);
				if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
					bundler.putSerializable(AppConstants.PARAM_ALBUM, (Album)object);
				} else {
					bundler.putSerializable(AppConstants.PARAM_ALBUM, (LifePhoto)object);
				}		
				bundler.putString("type", albumtype);
				bundler.putInt("position", position - 1);
				intent.putExtras(bundler);
				startActivity(intent);
			}
	    } else {
			Intent intent = new Intent(LifeWorkPhoto.this, PhotoManager_ViewPager.class);
			Bundle bundler = new Bundle();
			bundler.putStringArrayList("imglist", photos);
			bundler.putStringArrayList("decslist", descs);				
			bundler.putString("type", albumtype);		
			intent.putExtras(bundler);
			startActivity(intent);
	    }
	}

	/**
     * 当选择全部的图片时操作
	 * 编辑生活剪影|手工作品
	 * @param action
	 */
	public void showEditView(String action){
		if (action.equals("0")) {
			right_tv.setText("取消");
			flag = "1";
			mAdapter.setAllCheck("0");
            del_btn.setText("删除照片" + "(" + mAdapter.getCheckImageList().size() + ")");
            decs_btn.setText("编辑描述" + "(" + mAdapter.getCheckImageList().size() + ")");
			left_iv.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
			showedit_rl.setVisibility(View.VISIBLE);
            gridview.smoothScrollToPosition(mAdapter.getCount());
		} else if (action.equals("1")) {
			right_tv.setText("全选");
			flag = "0";
			left_iv.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
			mAdapter.setAllCheck("1");
            del_btn.setText("删除照片" + "(" + mAdapter.getCheckImageList().size() + ")");
            decs_btn.setText("编辑描述" + "(" + mAdapter.getCheckImageList().size() + ")");
			showedit_rl.setVisibility(View.VISIBLE);
            gridview.smoothScrollToPosition(mAdapter.getCount());
		} else if (action.equals("2")) {
			right_tv.setText("编辑");
			showedit_rl.setVisibility(View.GONE);
			flag = "2";
			mAdapter.setAllCheck("2");
            del_btn.setText("删除照片" + "(" + mAdapter.getCheckImageList().size() + ")");
            decs_btn.setText("编辑描述" + "(" + mAdapter.getCheckImageList().size() + ")");
			left_iv.setImageDrawable(getResources().getDrawable(R.drawable.selector_header_top_left));
		}
	}
	
	public void showDelView(boolean action){
		if (action) {
			mAdapter.setAction(true);
			editAction = true;
			left_iv.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));		
		} else {
			mAdapter.setAction(false);
			editAction = false;
		}	
	}
	
	/**
	 * 提交删除
	 */
	public void sumitDel(){
		if (AppConstants.photocheckList.isEmpty()) {
			showToast("请选择相片");
			showDelView(false);
			return;
		}
		showDialog("删除相册提示", "你选择了删除" + AppConstants.photocheckList.size() + "个相片", "删除", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				StringBuffer sb = new StringBuffer();
				for (Object album:  AppConstants.photocheckList) {
					sb.append(((Album)album).getPhotoid() + ",");
				}
				String array = sb.toString();
				array = array.toString().substring(0, array.toString().length() - 1);
				AccountInfo info = AppServer.getInstance().getAccountInfo();
				AppServer.getInstance().deleteClassPhotoGalley(array, info.getUid(), new OnAppRequestListener() {
					@Override
					public void onAppRequest(int code, String message, Object obj) {
						if (code == AppServer.REQUEST_SUCCESS) {
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
		},new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				showDelView(false);
				AppConstants.photocheckList.clear();
			}
		});
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent!=null) {
			object = intent.getSerializableExtra(AppConstants.PARAM_ALBUM);
			Bundle bundle = intent.getExtras();
		    albumtype = bundle.getString(AppConstants.INTENT_ALBUM_TYPE);
		    if (!albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
		        term  = (Term) bundle.getSerializable("term");
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
                    right_tv.setVisibility(View.VISIBLE);
				} else if (albumtype.equals(AppConstants.PARAM_UPLOAD_LIFE) ||
						   albumtype.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                    newalbumlist.clear();
                    wl_list.clear();
                    LifePhoto photo = (LifePhoto) object;
                    tv_headerTitle.setText(photo.getName());

                    photos = bundle.getStringArrayList("photos");
                    descs = bundle.getStringArrayList("descs");
                    int index = bundle.getInt("index");
                    page = bundle.getInt("page");
                    WLImage wls = new WLImage();
                    wls.setM_path("add");
                    newalbumlist.add(wls);
                    if (photos!=null && photos.size()!=0) {
                        for (int i = 0; i < photos.size(); i++) {
                            WLImage wl = new WLImage();
                            wl.setM_path(photos.get(i));
                            if (descs.get(i)!=null) {
                                wl.setPhoto_desc(descs.get(i));
                            }
                            newalbumlist.add(wl);
                        }

                        for (int i = 0; i < newalbumlist.size(); i++) {
                            WLImage wl= (WLImage) newalbumlist.get(i);
                            wl_list.add(wl);
                        }
                        mAdapter.setList(newalbumlist);
                    } else {
                        if (albumtype.equals("4")) {
                            lifetype = "1";
                        } else {
                            lifetype = "2";
                        }
                        page = 1;
                        showLoadingDialog("正在加载...");
                        loadPhoto(photo,1,17);
                    }
                    gridview.setSelection(index);
                    right_tv.setVisibility(View.VISIBLE);
               }
			} else {
				// 加载相片
				if (bundle!=null) {
                    newalbumlist= mAdapter.getList();
                    ArrayList<Object> temp = new ArrayList<Object>();
                    temp.add(newalbumlist.get(0));
                    ArrayList<Photo> newPhotoList = bundle.getParcelableArrayList(AppConstants.PHOTOLIST);
                    imageType = bundle.getString(AppConstants.INTENT_IMAGE_TYPE);
                    module = bundle.getString(AppConstants.INTENT_UPLOAD_TYPE);
                    if (module.equals(AppConstants.PARAM_UPLOAD_LIFE) ||
                            module.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                        term  = (Term) bundle.getSerializable("term");
                    }
                    albumtype = module;
                    desc = bundle.getString(AppConstants.INTENT_DESC);
                    if (newPhotoList!=null && newPhotoList.size() > 0) {
                        uploadView.setVisibility(View.VISIBLE);
                        if (!module.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                            if (module.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                                // 班级相册
                                for (Photo p : newPhotoList) {
                                   temp.add(new Album("", "", "", "", 0, "", p.imgPath));
                                }
                            } else if (module.equals(AppConstants.PARAM_UPLOAD_LIFE) ||
                                      module.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                                // 生活剪影|手工作品
                                for(Photo p:newPhotoList){
                                    temp.add(new WLImage(0, p.imgPath,desc, ""));
                                }
                            }
                            if (newPhotoList!=null && newPhotoList.size() > 0) {
                               retryUpload(newPhotoList);
                            }
                            if (uploadList.size() > 0) {
                                uploadTask(uploadList);
                            } else {
                                uploadView.setVisibility(View.GONE);
                                return;
                            }
                            if (photos!=null) {
                                photos.clear();
                            }
                            if (descs!=null) {
                                descs.clear();
                            }
                            if (module.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                                for (int i = 1; i < temp.size(); i++) {
                                    Album imageitem = (Album) temp.get(i);
                                    photos.add(imageitem.getFilepath());
                                    descs.add(imageitem.getTitle());
                                }
                            } else {
                                for (int i = 1; i < temp.size(); i++) {
                                    WLImage imageitem = (WLImage) temp.get(i);
                                    if (imageitem.getM_path()!=null && photos!=null) {
                                        photos.add(imageitem.getM_path());
                                    }
                                    if (imageitem.getPhoto_desc()!=null && descs!=null) {
                                        descs.add(imageitem.getPhoto_desc());
                                    }
                                }
                            }
                           right_tv.setVisibility(View.VISIBLE);
                        } else { // 批量上传
                            photos = bundle.getStringArrayList("photos");
                            descs = bundle.getStringArrayList("descs");
                            right_tv.setVisibility(View.GONE);
					    }
			        }
				}  
			}
		}
	}

	private void retryUpload(final List<Photo> newPhotoList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UpLoadManager.isupload = true;
                uploadList.clear();
                listTask.clear();
                listThread.clear();
                handler.sendEmptyMessage(0);
                for (int i = 0; i < newPhotoList.size(); i++) {
                    Photo photo = newPhotoList.get(i);
                    String path = photo.imgPath;
                    long id = uploadDB.getBindId(path);
                    if (id !=0) {
                        uploadDB.delUpload(path);
                    }
                    Upload upload = new Upload();
                    upload.setUploadSize(0);
                    upload.setUploadfilepath(path);
                    upload.setFileId(uploadDB.getId());
                    upload.setModule(module);
                    upload.setCompress(imageType);
                    upload.setSourcepath(photo.imgPath);

                    if (imageType!=null) {
                        if (imageType.equals(AppConstants.COMMON_QUALITY_FOR_PHOTO)) {
                            if (path!=null) {
                                String baos = FileUtils.saveAsCommon(path, AppConfig.UPLOAD_PATH);
                                if (baos!=null) {
                                    upload.setUploadfilepath(baos);
                                } else {
                                    upload.setUploadfilepath("");
                                }
                            }
                        } else if (imageType.equals(AppConstants.HD_QUALITY_FOR_PHOTO)) {
                            if ((path!=null) && (path.length() > 0)) {
                                String baos = FileUtils.saveAsHd(LifeWorkPhoto.this, path, AppConfig.UPLOAD_PATH);
                                if (baos!=null) {
                                    upload.setUploadfilepath(baos);
                                } else {
                                    upload.setUploadfilepath("");
                                }
                            }
                        }
                    }
                    if (upload.getUploadfilepath()!=null && upload.getUploadfilepath().length() > 0) {
                        file = new File(upload.getUploadfilepath());
                        String param = null;
                        if (module.equals(AppConstants.PARAM_UPLOAD_LIFE) || module.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                            LifePhoto photos = (LifePhoto) object;
                            if (photos!=null) {
                                if (term == null) {
                                    term = new Term();
                                }
                                String conskey = photos.getUserid() + term.getTerm() + lifetype + URL.urlkey;
                                String photokey = AppUtils.Md5(conskey);
                                param = StringUtils.StringToUnicode(desc) + "$" + photos.getUserid() + "$"
                                        + term.getTerm() + "$" + lifetype + "$" + StringUtils.StringToUnicode(file.getName()) + "$" + 0 + "$" + file.length() + "$" + photokey;
                                Map<String, String> map = new HashMap<String,String>();
                                map.put("term", term.getTerm());
                                map.put("type", lifetype);
                                map.put("userids", photos.getUserid() + "");
                                map.put("photo_desc",desc);
                                map.put("key", photokey);
                                upload.setMap(map);
                            }
                        } else if (module.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                            String key = AppUtils.Md5(albumid + URL.urlkey);
//                          long uploadsize = uploadItem.getUploadSize();
                            param = albumid + "$" + file.getName() + "$" + 0 + "$" + file.length() + "$" + key + "$" + StringUtils.StringToUnicode(desc);
                            Map<String,String> map = new HashMap<String,String>();
                            map.put("albumid", albumid);
                            map.put("key", key);
                            map.put("description", desc);
                            upload.setMap(map);
                        } else if (module.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                            Iterator<String> it = uidlist.iterator();
                            StringBuffer sbffer = new StringBuffer();
                            while (it.hasNext()) {
                                sbffer.append(it.next()).append(",");
                            }
                            if (desc == null) {
                                desc = "";
                            }
                            String uids = sbffer.substring(0, sbffer.lastIndexOf(","));
                            String conskey = uids + terms + lifetype + URL.urlkey;
                            String photokey = AppUtils.Md5(conskey);
                            param = StringUtils.StringToUnicode(desc) + "$" + uids + "$"
                                    + terms + "$" + lifetype + "$" + StringUtils.StringToUnicode(file.getName()) + "$" + 0 + "$" + file.length() + "$" + photokey;
                            Map<String ,String> map = new HashMap<String,String>();
                            map.put("term", terms);
                            map.put("type", lifetype);
                            map.put("userids", uids);
                            map.put("key", photokey);
                            map.put("photo_desc", desc);
                            upload.setMap(map);
                        }
                        upload.setParam(param);
                        uploadDB.saveUpload(upload);
                        uploadList.add(upload);

                    } else {
                        Message msg = new Message();
                        msg.what = 2;
                        msg.arg1 = i;
                        msg.obj = newPhotoList;
                        handler.sendMessage(msg);
                    }
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = uploadList;
                handler.sendMessage(msg);
            }
        }).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        if (AppUtils.getmem_UNUSED(this) < 1024 * 5) {
            ImageLoader.getInstance().clearMemoryCache();
            System.gc();
        }
		EventBus.getDefault().unregister(this);
	}

    public int alreadUp = 0; // 上传成功数
    public int failCount = 0; // 失败的数;
    public int allUpCount = 0; // 上传成功总数
	public void onEventMainThread(AppEvent event) {
		Message msg = event.getMsg();
        if (event.getPosition() < uploadList.size()) {
            Upload upload = uploadList.get(event.getPosition());
            file = new File(upload.getUploadfilepath());
            if (event.getType() == AppEvent.SERVICE_CLASSPHOTO_UPLOADING) {

                if (msg.what == UpLoadManager.UPLOAD_FAILL) {
                    allUpCount++;
                    failCount++;
                    if ((event.getPosition() + 1) == uploadList.size()) {
                        showToast("上传失败" + failCount + "张");
                        uploadView.setVisibility(View.GONE);
                        iv_close_upload.setVisibility(View.GONE);
                        iv_retry_upload.setVisibility(View.GONE);
    //                    showFailData(upload.getModule());
                    }
                    UpLoadManager.isupload = false;
                    Long fileid = uploadDB.getBindId(file.getAbsolutePath());
                    Long length = (Long) msg.obj;
    //				uploadDB.updateUpload(fileid, length);
                    // 显示重试和关闭按钮
                } else if (msg.what ==  UpLoadManager.UPLOAD_SUCCESS) {
                    allUpCount++;
                    alreadUp++;
                    uploadDB.delUpload(file.getAbsolutePath()); // 上传成功一张删除一张
                    // if ((event.getPosition() + 1) == uploadList.size()){
                    if (alreadUp == uploadList.size()) {      // 超哥写的
                        alreadUp = 0;
                        UpLoadManager.isupload = false;
//                        uploadList.clear();
                        uploadView.setVisibility(View.GONE);
                        iv_close_upload.setVisibility(View.GONE);
                        iv_retry_upload.setVisibility(View.GONE);
                        EventBus.getDefault().post(new AppEvent(AppEvent.UPLOAD_FINISH));
                        FileUtils.deleteDirectory(AppConfig.UPLOAD_PATH);

                        AccountInfo info = AppServer.getInstance().getAccountInfo();
                        if (albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                            Album album = new Album();
                            album.setAlbumid(albumid);
                            loadPhoto(info, album);
                        } else if (albumtype.equals(AppConstants.PARAM_UPLOAD_LIFE)
                                  || albumtype.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                             LifePhoto photo = (LifePhoto) object;
                             wl_list.clear();
                             page = 1;
                             loadPhoto(photo, page, 17);
                        }
                    } else {
                        int position = event.getPosition() + 1;
                        if (uploadList!=null) {
                            if (position > uploadList.size()) {
                                position = uploadList.size() - 1;
                            }
                        }
                        GlideUtils.loadGeneralImage(AppContext.getInstance(), "file:///" + uploadList.get(event.getPosition() + 1).getUploadfilepath(), albumupload_iv);
//                        ImageLoader.getInstance().displayImage("file:///" + uploadList.get(event.getPosition() + 1).getUploadfilepath(), albumupload_iv, ImageLoadOptions.getOptions());
                    }
                } else if (msg.what ==  UpLoadManager.UPLOAD_UPDATE) {
                    if (!isPause) {
                        // upload_tv.setText("正在上传"+(event.getPosition()+1)+"/"+uploadList.size());
                        upload_tv.setText("正在上传中......共" + uploadList.size() + "张");
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
                }
            } else if (event.getType() == AppEvent.UPLOAD_PAUSE) {
                allUpCount = 0;
                failCount = 0;
                alreadUp = 0;
                isPause = true;
                UpLoadManager.isupload = false;
            } else if (event.getType() == AppEvent.NetERROR) {
                allUpCount = 0;
                failCount = 0;
                alreadUp = 0;
                isPause = true;
                showFailData(upload.getModule());
                UpLoadManager.isupload = false;
            }
            if (allUpCount == uploadList.size()) {
                UpLoadManager.isupload = false;
                uploadList.clear();
                allUpCount = 0;
                failCount = 0;
                alreadUp = 0;
                showFailData(upload.getModule());
            }
        }
	}

	private void showFailData(String type) {
		List<Upload> faillist = uploadDB.getFileList(type);
		if (faillist!=null && faillist.size() > 0) {
			uploadView.setVisibility(View.VISIBLE);
			tv_uploadfail.setText(faillist.size() + "张上传失败,请重试");
			tv_uploadfail.setVisibility(View.VISIBLE);
			upload_progress.setVisibility(View.GONE);
			upload_tv.setVisibility(View.GONE);
			iv_close_upload.setVisibility(View.VISIBLE);
			iv_retry_upload.setVisibility(View.VISIBLE);
		}
	}
	
	File file = null;
	private void uploadTask(List<Upload> taskList) {
        allUpCount = 0;
        failCount = 0;
        alreadUp = 0;
        EventBus.getDefault().post(new AppEvent(AppEvent.UPLOAD_START));
        for (int i = 0;i < taskList.size(); i++) {
            Upload uploadItem = taskList.get(i);
            listTask.add(getTask(i, uploadItem));
            file = new File(uploadItem.getUploadfilepath());
            UploadThread runnable = new UploadThread(AppContext.getInstance(), uploadDB, null, new File(uploadItem.getUploadfilepath()),
                    uploadItem.getUploadfilepath(),null,i);
            listThread.add(runnable);
        }
        OrderedTaskExecutor executor =  TaskExecutor.newOrderedExecutor();
        for (int i = 0; i < listTask.size(); i++) {
            executor.put(listTask.get(i));
        }
        upPhotoCallback = executor.getUpPhotoCallback();
        executor.start();
	}
	
    private SimpleTask<Integer> getTask(final int position, final Upload uploadItem){
        SimpleTask<Integer> simple = new SimpleTask<Integer>() {
            @Override
            protected Integer doInBackground() {
                String param = null;
                file = new File(uploadItem.getUploadfilepath());
                String key = AppUtils.Md5(albumid + URL.urlkey);
                long uploadsize = uploadItem.getUploadSize();
                if (uploadItem.getModule().equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    // 班级相册
                    if (uploadItem.getParam() != null && uploadItem.getParam().equals("")) {
                        param = uploadItem.getParam();
                    } else {
                        param = albumid + "$" + file.getName() + "$" + uploadsize + "$" + file.length() + "$" + key + "$" + StringUtils.StringToUnicode(desc);
                        uploadItem.setParam(param);
                        uploadDB.updateUploadParam(uploadItem);
                    }
                } else if (uploadItem.getModule().equals(AppConstants.PARAM_UPLOAD_LIFE) ||
                        uploadItem.getModule().equals(AppConstants.PARAM_UPLOAD_WORK)) {
                    // 生活剪影|手工作品
                    LifePhoto photo = (LifePhoto) object;
                    if (uploadItem.getParam() != null && !uploadItem.getParam().equals("")) {
                        param = uploadItem.getParam();
                    } else {
                        String conskey = photo.getUserid() + term.getTerm() + lifetype + URL.urlkey;
                        String photokey = AppUtils.Md5(conskey);
                        param = StringUtils.StringToUnicode(desc) + "$" + photo.getUserid() + "$"
                                + term.getTerm() + "$" + lifetype + "$" + StringUtils.StringToUnicode(file.getName()) + "$" + 0 + "$" + file.length() + "$" + photokey;
                        uploadItem.setParam(param);
                        uploadDB.updateUploadParam(uploadItem);
                    }
                } else if (uploadItem.getModule().equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                    Iterator<String> it = uidlist.iterator();
                    StringBuffer sbffer = new StringBuffer();
                    while (it.hasNext()) {
                        sbffer.append(it.next()).append(",");
                    }
                    if (desc == null) {
                        desc = "";
                    }
                    if (uploadItem.getParam() != null && uploadItem.getParam().equals("")) {
                        param = uploadItem.getParam();
                    } else {
                        String uids = sbffer.substring(0, sbffer.lastIndexOf(","));
                        String conskey = uids + terms + lifetype + URL.urlkey;
                        String photokey = AppUtils.Md5(conskey);
                        param = StringUtils.StringToUnicode(desc) + "$" + uids + "$"
                                + terms + "$" + lifetype + "$" + StringUtils.StringToUnicode(file.getName()) + "$" + 0 + "$" + file.length() + "$" + photokey;
                        uploadItem.setParam(param);
                        uploadDB.updateUploadParam(uploadItem);
                    }
                }
                if (listThread != null && listThread.size() != 0) {
                    listThread.get(position).upload(position, uploadItem, param, uploadItem.getModule(), upPhotoCallback);
                }
                return position;
            }

            @Override
            protected void onPostExecute(Integer result) { }

            @Override
            protected void onPreExecute() {
                UpLoadManager.isupload = true;
                if (position == 0) {
                    GlideUtils.loadGeneralImage(AppContext.getInstance(), "file:///" + uploadItem.getUploadfilepath(), albumupload_iv);
//                            ImageLoader.getInstance().displayImage("file:///" + uploadItem.getUploadfilepath(), albumupload_iv, ImageLoadOptions.getOptions());
                }
                File file = new File(uploadItem.getUploadfilepath());
                int progress = (int) (((float) uploadItem.getUploadSize() / file.length()) * 100);
                upload_progress.setMax(100);
                upload_progress.setProgress(progress);
                upload_progress.setVisibility(View.VISIBLE);
                tv_uploadfail.setText("");
            }
        };
        return simple;
    }
	
    public List<Object> removeDuplicate( List<Object>  list)  {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
        return list;
    }
        
    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (!albumtype.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
            LifePhoto photo = (LifePhoto) object;
            if (photo!=null) {
                tv_headerTitle.setText(photo.getName());
                loadPhoto(photo, page, 17);
            }
        } else {
            showToast("没有数据了");
            mPullToRefreshView.onFooterRefreshComplete();
        }
    }
       
}
