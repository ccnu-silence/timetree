package com.yey.kindergaten.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.GridAddImageAdapter;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.UpLoadManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 处理上传编辑的界面（生活剪影，手工作品）
 * @author zy
 */
public class BatchLifePhotoActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	// 导航栏控件
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.right_btn)ImageView right_btn;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.right_tv)TextView right_tv;

	//内部控件的
	@ViewInject(R.id.id_batch_select_child_rl)RelativeLayout addchild_rl;
	@ViewInject(R.id.id_select_image_quality_rl)RelativeLayout selectImg_rl;
	@ViewInject(R.id.id_image_quality_type)TextView imagetype_tv;
	@ViewInject(R.id.id_show_select_image_gv)GridView showiv_gv;
	@ViewInject(R.id.id_show_child_count_tv)TextView count_tv;
	@ViewInject(R.id.id_input_photo_decs_et)EditText decs_et;
    @ViewInject(R.id.show_batch_line)View viewline;
    @ViewInject(R.id.linearLayout3)LinearLayout tip_count_ll;

	private ArrayList<Photo>photolist = new ArrayList<Photo>(); // 现在的选中的图片
	private ArrayList<Photo>selectphotolist = new ArrayList<Photo>();
	private GridAddImageAdapter gridImageAdapter;
	private List<String>childlist = new ArrayList<String>();
	CharSequence[]items = {"普通(上传速度较快)", "高清HD(相片质量高,速度较慢)"};
	private static ArrayList<String>childuidlist = new ArrayList<String>();	
	private static String type;
	public Term term;
	public String terms;
	public static String decs;
	private static LifePhoto photo;
	private static String lifetype;
    private String module = null;
	private String albumId = null;
	private Album album;
	private AppContext context = AppContext.getInstance();
	private String imgType = AppConstants.COMMON_QUALITY_FOR_PHOTO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.batch_life_photo_activity);
		ViewUtils.inject(this);
        // 获取传值
		Bundle bundle = getIntent().getExtras();
		if (bundle!=null) {
			album = (Album) bundle.getSerializable("album");
//          module = bundle.getString("type");
			type = bundle.getString("type") == null ? "" : bundle.getString("type");
			if (type!=null) {
				context.setType(type);
			}

			lifetype = bundle.getString("lifetype");
            if (lifetype!=null) {
            	context.setLifetype(lifetype);
			}
			
			term = (Term) bundle.getSerializable("term");
            if (term!=null) {
            	context.setTerm(term);
			}
            
			terms = bundle.getString("terms");
            if (terms!=null) {
            	context.setTerms(terms);
			}
		
			photo = (LifePhoto) bundle.getSerializable("lifephoto");
            if (photo!=null) {
            	context.setPhoto(photo);
			}
		
			photolist = bundle.getParcelableArrayList(AppConstants.PHOTOLIST);
			if (photolist == null) {
				photolist = new ArrayList<Photo>();
			}
		
			childuidlist = getIntent().getExtras().getStringArrayList("childlist");
            if (childuidlist!=null) {
            	context.setUidlist(childuidlist);
			}
		
			albumId = bundle.getString(AppConstants.PARAM_ALBUMID);
            if (albumId!=null) {
            	context.setAlbumId(albumId);
			}
		
		}
    	initView();
    	initClick();
    	initDate();
	}

    protected  void initView(){
        // 从context获取imageType，设置图片质量
        if (context.getImgType()!=null) {
            if (context.getImgType().equals(AppConstants.COMMON_QUALITY_FOR_PHOTO)) {
                imagetype_tv.setText("普通");
            } else if (context.getImgType().equals(AppConstants.HD_QUALITY_FOR_PHOTO)) {
                imagetype_tv.setText("高清");
            } else {
                imagetype_tv.setText("原图");
            }
        } else {
            context.setImgType(AppConstants.COMMON_QUALITY_FOR_PHOTO);
        }
        // 从context获取uidlist，设置人数
        if (context.getUidlist() == null || context.getUidlist().size() == 0) {
            count_tv.setText("共0人");
        } else {
            count_tv.setText("共" + context.getUidlist().size() + "人");
        }
        // 初始化描述编辑框
        if (decs != null) {
            decs_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            decs_et.setText(decs);
            if (decs.length() > 20) {
                decs_et.setSelection(20);
            } else {
                decs_et.setSelection(decs.length());
            }
        }
        // 设置标题
        titletv.setVisibility(View.VISIBLE);
        if (type.equals(AppConstants.PARAM_UPLOAD_LIFE) || type.equals(AppConstants.PARAM_UPLOAD_WORK)) {
            if (context.getPhoto()!=null && context.getPhoto().getName()!=null) {
                titletv.setText(context.getPhoto().getName());
            }
            addchild_rl.setVisibility(View.GONE);
        } else if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
            titletv.setText("班级相册");
            addchild_rl.setVisibility(View.GONE);
        } else {
            titletv.setText("批量编辑");
            viewline.setVisibility(View.VISIBLE);
        }
        left_btn.setVisibility(View.VISIBLE);
        left_btn.setImageResource(R.drawable.icon_close);
        right_tv.setVisibility(View.VISIBLE);
        right_tv.setText("上传");
    }
	   
    protected void initClick(){
        right_tv.setOnClickListener(this);
        left_btn.setOnClickListener(this);
        addchild_rl.setOnClickListener(this);
        selectImg_rl.setOnClickListener(this);
        showiv_gv.setOnItemClickListener(this);
        showiv_gv.setHorizontalSpacing(15);
        showiv_gv.setVerticalSpacing(15);
    }
	    
    protected void initDate(){
        Photo photo = new Photo();
        photo.imgPath = "camera_default";
        photolist.add(photo);
        gridImageAdapter = new GridAddImageAdapter(this, photolist);
        if (selectphotolist!=null) {
	    	if (selectphotolist.size() == 0) {
	    		showiv_gv.setAdapter(gridImageAdapter);
	    	} else {
	    		selectphotolist.add(photo);	
	    		gridImageAdapter = new GridAddImageAdapter(this, selectphotolist);		
	    		showiv_gv.setAdapter(gridImageAdapter);
            }
        } else {
            showiv_gv.setAdapter(gridImageAdapter);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
			case R.id.left_btn:
				if (type.equals("fromlifemain")) { }
				this.finish();
				break;
			case R.id.right_tv:
				if (UpLoadManager.isupload) {
                    showToast("目前正在上传，请稍等...");
                    return;
				}
				if (AppContext.checkList.isEmpty()) {
                    showToast("请先选择相片在上传");
                    return;
				} else if ((context.getUidlist() == null || context.getUidlist().size() == 0) && type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                    showToast("请先选择小孩在上传");
                    return;
				} else if (type.equals(AppConstants.PARAM_UPLOAD_LIFE)) {
				    Intent uploadIntent = new Intent(BatchLifePhotoActivity.this, LifeWorkPhoto.class);
				    Bundle bundle = new Bundle();
				    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);			
				    decs = decs_et.getText().toString();
				    bundle.putSerializable(AppConstants.PARAM_ALBUM, context.getPhoto());
				    bundle.putString(AppConstants.INTENT_DESC, decs);
				    bundle.putString(AppConstants.INTENT_UPLOAD_TYPE, context.getType());
				    bundle.putString(AppConstants.INTENT_ALBUM_TYPE, context.getType());			    
				    bundle.putString(AppConstants.INTENT_IMAGE_TYPE, context.getImgType());
				    bundle.putSerializable("term", context.getTerm());
				    bundle.putString("upload", "upload");
				    bundle.putString("lifetype", context.getLifetype());
				    uploadIntent.putExtras(bundle);    
				    startActivity(uploadIntent);
				    finish();	
                } else if (type.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                    Intent uploadIntent = new Intent(BatchLifePhotoActivity.this, LifeWorkPhoto.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putSerializable(AppConstants.PARAM_ALBUM, context.getPhoto());
                    bundle.putSerializable("term", context.getTerm());
                    decs = decs_et.getText().toString();
                    bundle.putString(AppConstants.INTENT_ALBUM_TYPE, context.getType());
                    bundle.putString(AppConstants.INTENT_DESC, decs);
                    bundle.putString(AppConstants.INTENT_UPLOAD_TYPE, context.getType());
                    bundle.putString(AppConstants.INTENT_IMAGE_TYPE, context.getImgType());
                    bundle.putString("lifetype", context.getLifetype());
                    bundle.putString("upload", "upload");
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                    if (UpLoadManager.isupload) {
                        showToast("正在上传,请稍后...");
                        return;
                    }
                    Intent uploadIntent = new Intent(BatchLifePhotoActivity.this,LifeWorkPhoto.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    decs = decs_et.getText().toString();

//                  if (type.equals("1")) {
//                      bundle.putString("type",AppConstants.PARAM_UPLOAD_LIFE);
//                  } else {
//                      bundle.putString("type",AppConstants.PARAM_UPLOAD_WORK);
//                  }
                    bundle.putString(AppConstants.INTENT_ALBUM_TYPE, context.getType());
                    bundle.putString(AppConstants.INTENT_UPLOAD_TYPE, context.getType());
                    bundle.putString(AppConstants.INTENT_DESC,decs);
                    bundle.putString(AppConstants.INTENT_IMAGE_TYPE, context.getImgType());

                    if (context.getUidlist()!=null) {
                        Iterator<String> it = context.getUidlist().iterator();
                        if (childuidlist!=null) {
                            childuidlist.clear();
                        } else {
                            childuidlist = new ArrayList<String>();
                        }
                        while (it.hasNext()) {
                            String uid = it.next();
                            String realid = uid.substring(0, uid.indexOf(","));
                            childuidlist.add(realid);
                        }
                    }
                    bundle.putStringArrayList("childlist", childuidlist);
                    bundle.putString("lifetype", context.getLifetype());
                    bundle.putString("upload", "upload");
                    bundle.putString("terms", context.getTerms());
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                } else if(type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) { // 班级相册照片上传
                    decs = decs_et.getText().toString();
                    Intent uploadIntent = new Intent(BatchLifePhotoActivity.this, ClassPhotoDetialManager.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString(AppConstants.INTENT_UPLOAD_TYPE, context.getType());
                    bundle.putString(AppConstants.INTENT_ALBUM_TYPE, context.getType());
                    bundle.putString(AppConstants.PARAM_ALBUMID, context.getAlbumId());
                    bundle.putString(AppConstants.INTENT_IMAGE_TYPE, context.getImgType());
                    bundle.putString("upload", "upload");
                    bundle.putSerializable("album", album);
                    bundle.putString(AppConstants.INTENT_DESC, decs);
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                }
				decs = null;
				break;
			case R.id.id_batch_select_child_rl:
			    Intent intent = new Intent(this,SelectChildLifePhotoActivity.class);
			    intent.putStringArrayListExtra("checklist", (ArrayList<String>) context.getUidlist());
			    startActivityForResult(intent, 10);
				break;
			case R.id.id_select_image_quality_rl:
			    showSelectDialog();
				break;
			}
		}

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            if(!type.equals(AppConstants.PARAM_UPLOAD_BATCH)){
//                if(type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)){
//                    Intent intent = new Intent(BatchLifePhotoActivity.this,CommonBrowserWebImage.class);
//                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
//                    intent.putExtra(AppConstants.PARAM_ALBUM, (Album)object);
//                    startActivity(intent);
//                }else if(type.equals(AppConstants.PARAM_CommonBrowser)){
//                    this.finish();
//                }else if (type.equals(AppConstants.PARAM_UPLOAD_GROW)){
//                    this.finish();
//                }else{
//                    Intent intent = new Intent(BatchLifePhotoActivity.this,LifeWorkPhoto.class);
//                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
//                    intent.putExtra(AppConstants.PARAM_ALBUM, (LifePhoto)object);
//                    intent.putExtra(AppConstants.INTENT_UPLOAD_TYPE, type);
//                    startActivity(intent);
//                }
//            }else{
//                Intent intent = new Intent(BatchLifePhotoActivity.this,LifeWorkPhoto.class);
//                intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
//                intent.putExtra(AppConstants.INTENT_UPLOAD_TYPE, type);
//                startActivity(intent);
//            }
//            this.finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onPause() {
        if (AppUtils.getmem_UNUSED(this) < 1024 * 5) {
            ImageLoader.getInstance().clearMemoryCache();
            System.gc();
        }
        super.onPause();
    }

    private void showSelectDialog(){
        showDialogItems(items, "选择上传方式", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int which) {
                switch (which) {
					case 0:
						imagetype_tv.setText("普通");
						imgType = AppConstants.COMMON_QUALITY_FOR_PHOTO;
						context.setImgType(imgType);
						break;
					case 1:
						imagetype_tv.setText("高清");
						imgType = AppConstants.HD_QUALITY_FOR_PHOTO;
						context.setImgType(imgType);
						break;
					case 2:
						imagetype_tv.setText("原图");
						imgType = AppConstants.ORIGINAL_FOR_PHOTO;
						context.setImgType(imgType);
						break;
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        decs = decs_et.getText().toString();
        if (gridImageAdapter.getCount() > 1) {
            String type = context.getType();
            if (gridImageAdapter.getData().get(gridImageAdapter.getCount() - 1).imgPath.equals("camera_default")) {
			    if (position == gridImageAdapter.getCount() - 1) {
				    Intent i = new Intent(BatchLifePhotoActivity.this,GalleryActivity.class);
                    if (type.equals(AppConstants.PARAM_UPLOAD_BATCH)){
                        Bundle bundle = new Bundle();
                        decs = decs_et.getText().toString();
                        bundle.putString("decs",decs);
                        bundle.putString(AppConstants.INTENT_IMAGE_TYPE, imgType);
                        bundle.putString("lifetype", lifetype);
                        bundle.putSerializable("terms", terms);
                        bundle.putStringArrayList("childlist", childuidlist);
                        bundle.putString("typefrom", type);
                        bundle.putString("lifetype", lifetype);
                        i.putExtras(bundle);
                        startActivity(i);
                        this.finish();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("lifephoto", photo);
                        decs = decs_et.getText().toString();
                        bundle.putString("decs", decs);
                        bundle.putSerializable("term", term);
                        bundle.putString("type", type);
                        bundle.putString("lifetype", lifetype);
                        i.putExtra("typefrom", AppContext.getInstance().getType());
                        i.putExtras(bundle);
                        startActivity(i);
                        this.finish();
                    }
			    }
			}
        } else {
            Intent i = new Intent(BatchLifePhotoActivity.this, GalleryActivity.class);
            if (type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                Bundle bundle = new Bundle();
                decs = decs_et.getText().toString();
                bundle.putString("decs",decs);
                bundle.putString(AppConstants.INTENT_IMAGE_TYPE, imgType);
                bundle.putString("lifetype", lifetype);
                bundle.putSerializable("terms", terms);
                bundle.putString("typefrom", type);
                i.putExtras(bundle);
                startActivity(i);
                this.finish();
            } else {
                i.putExtra("typefrom", "fromlifemain");
                startActivity(i);
                this.finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.REQUESTCODE_TAKE_LOCAL) {
                selectphotolist.clear();
                selectphotolist = data.getParcelableArrayListExtra(AppConstants.PHOTOLIST);
                if (selectphotolist!=null && selectphotolist.size()!=0) {
                    Photo photo = new Photo();
                    photo.imgPath = "camera_default";
                    selectphotolist.add(photo);
                    ArrayList<Photo>photoselectlist = new ArrayList<Photo>();
                    int index = 0;
                    if (selectphotolist.size() == 10 || selectphotolist.size() > 10) {
                        for (int i = 0; i < selectphotolist.size(); i++) {
                            index++;
                            if (index < 10) {
                                photoselectlist.add(selectphotolist.get(i));
                                if (index == 9) {
                                    photoselectlist.add(photo);
                                    break;
                                }
                            }
                        }
                    } else {
                        photoselectlist.addAll(selectphotolist);
                    }
                    gridImageAdapter = new GridAddImageAdapter(this, photoselectlist);
                    showiv_gv.setAdapter(gridImageAdapter);
                }
            } else if (requestCode == 10) {
                childlist = data.getStringArrayListExtra("selectlist");
                count_tv.setText("共" + childlist.size() + "人");
            } else {
                childlist = new ArrayList<String>();
            }
            context.setUidlist(childlist);
        }
    }

}

