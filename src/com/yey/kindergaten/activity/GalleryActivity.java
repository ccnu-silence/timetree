package com.yey.kindergaten.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ImgChooserAdapter;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.bean.ThreadsBean;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.ImageLoadOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
//dlf
public class GalleryActivity extends BaseActivity{

	private static final String INTENT_KEY_FOLDER_NAME = "folder_name";
	private static final String INTENT_KEY_PICTURE_NAME = "picture_name";
	
	protected static final int REQUEST_CODE_CHOOSE_FOLDER = 0x01;
	private ProgressDialog mProgressDialog;
	private GridView mGridView;
	private ImgChooserAdapter mGalleryImgAdapter;
	private ArrayList<Photo> mlistData;

    String type;
    int cut; // 是否需要在客户端选择裁剪：  0 表示照片不需要，1 表示需要
    List<ThreadsBean>listbean = null;
    private String albumid = null;
    private String typefrom;
    private LifePhoto photo;
    private Term term;
    private String lifetype;
    private Album album;
    private String terms;
    private String desc;
    private String imgType;
    private List<String> uidlist = new ArrayList<String>();
    private TextView header_tv;
    private TextView right_btn;
    private TextView left_btn;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGalleryImgAdapter != null) {
            mGalleryImgAdapter.clearCache();
        }
        mProgressDialog = null;
        mGridView = null;
        mGalleryImgAdapter = null;
        mlistData = null;

        type = null;
        listbean = null;
        albumid = null;
        typefrom = null;
        photo = null;
        term = null;
        lifetype = null;
        album = null;
        terms = null;
        desc = null;
        imgType = null;
        uidlist = null;
        header_tv = null;
        right_btn = null;
        left_btn = null;
        setContentView(R.layout.activity_null);

    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
        if (getIntent().getExtras()!=null) {
            type = getIntent().getExtras().getString("typefrom");
            cut = getIntent().getExtras().getInt("cut");
            if (type == null) {
                type = AppContext.getInstance().getType();
            }
            albumid = getIntent().getExtras().getString(AppConstants.PARAM_ALBUMID);
            term = (Term) getIntent().getExtras().getSerializable("term");
            lifetype = getIntent().getStringExtra("lifetype");
            photo = (LifePhoto) getIntent().getSerializableExtra("photo");
            uidlist = getIntent().getExtras().getStringArrayList("childlist");
            desc = getIntent().getExtras().getString("decs");
            terms = getIntent().getExtras().getString("terms");
            imgType = getIntent().getExtras().getString(AppConstants.INTENT_IMAGE_TYPE);
            album = (Album)getIntent().getExtras().getSerializable("album");
//          AppContext.checkList.clear();
        }
        initViews();
		new LoadImageGalleryTask(this,"").execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

    /*@Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && data!=null) {
            header_tv.setText(data.getStringExtra(INTENT_KEY_FOLDER_NAME));
			new LoadImageGalleryTask(this,data.getStringExtra(INTENT_KEY_FOLDER_NAME)).execute();
		}
	}
	
	private void initViews() {
		initProgressDialog();
		initGridView();
		initFolderBtn();
		initTitleText();
		initBackBtn();
	}
	
	private void initBackBtn() {
		left_btn = (TextView)findViewById(R.id.left_tv);
        left_btn.setText("相册选择");
        left_btn.setVisibility(View.VISIBLE);
		left_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, GalleryFolderActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_FOLDER);
            }
		});
	}

	private void initTitleText() {
        header_tv = (TextView)findViewById(R.id.header_title);
        header_tv.setText("全部图片");
        header_tv.setVisibility(View.VISIBLE);
    }

	private void initFolderBtn() {
		right_btn = (TextView)findViewById(R.id.right_tv);
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setText("确定");
        if (type.equals(AppConstants.FROMMEINFO) || type.equals(AppConstants.FROMDAIRY)) {
            right_btn.setVisibility(View.INVISIBLE);
        } else {
            right_btn.setVisibility(View.VISIBLE);
        }
        right_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if (AppContext.checkList == null || AppContext.checkList.size() == 0) {
                    showToast("请先选择图片");
                    return;
                }
                if (type.equals(AppConstants.FROMSPEAK)) {
                    Intent intent = new Intent(GalleryActivity.this, ServicePublishSpeakActivity.class);
                    intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                    startActivity(intent);
                } else if (type.equals(AppConstants.FROMWEB)) {
                    Intent intent = new Intent(GalleryActivity.this, CommonBrowser.class);
                    intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                    startActivity(intent);
                } else if (type.equals(AppConstants.FROMChat)) {
                    Intent chatintent = new Intent(GalleryActivity.this, ChatActivity.class);
                    chatintent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
			        /* setResult(RESULT_OK, chatintent);
				    finish();*/
                    startActivity(chatintent);
                    finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_LIFE)) {
                    Intent chatintent = new Intent(GalleryActivity.this, BatchLifePhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("lifetype", lifetype);
                    bundle.putSerializable("lifephoto", photo);
                    bundle.putSerializable("term", term);
                    bundle.putString("type", type);
                    chatintent.putExtras(bundle);
                    startActivity(chatintent);
                    finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                    Intent uploadIntent = new Intent(GalleryActivity.this, BatchLifePhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("type", type);
                    bundle.putString("lifetype", lifetype);
                    bundle.putSerializable("lifephoto", photo);
                    bundle.putSerializable("term", term);
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                    Intent uploadIntent = new Intent(GalleryActivity.this, BatchLifePhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("type", type);
                    bundle.putString("lifetype", lifetype);
                    bundle.putStringArrayList("childlist", (ArrayList<String>) uidlist);
                    bundle.putString("desc", desc);
                    bundle.putString("terms", terms);
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    Intent uploadIntent = new Intent(GalleryActivity.this, BatchLifePhotoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("type", type);
                    bundle.putSerializable("album", album);
                    bundle.putString(AppConstants.PARAM_ALBUMID, albumid);
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                }
			}
		});
	}

	private void initGridView() {
		mGridView = (GridView)findViewById(R.id.gallry_gridview);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
                Photo photo = mGalleryImgAdapter.getItem().get(position);
                if (photo == null) {
                    return;
                }
//              if (!AppContext.checkList.contains(photo)) {
//                  if (AppContext.checkList.size() >= 20) {
//                      Toast.makeText(getApplicationContext(),
//                          "最多只能选择20张照片进行上传哦",
//                          Toast.LENGTH_SHORT)
//                          .show();
//                      return;
//                  }
//              }
                if (type.equals(AppConstants.FROMSPEAK)) {
//                  if (AppContext.checkList.size() >= 20) {
//                      Toast.makeText(getApplicationContext(),
//                          "最多只能选择20张照片进行上传哦",
//                          Toast.LENGTH_SHORT)
//                          .show();
//                      return;
//                  } else {
//
//                  }
                    mGalleryImgAdapter.setCheck(position, view, false);
                } else if (type.equals(AppConstants.FROMDAIRY)) {
                    showDialog("友情提示：", "确认将这张照片作为成长日记动态上传？", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            AppContext.checkList.add(mlistData.get(position));
                            Intent intent = new Intent(GalleryActivity.this, KeepDiaryActivity.class);
                            intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                            startActivity(intent);
                        }
                    });
                } else if (type.equals(AppConstants.FROMMEINFO)) {
                    AppContext.checkList.add(mlistData.get(position));
                    Intent intent = new Intent(GalleryActivity.this, MeInfoActivity.class);
                    intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                    startActivity(intent);

//                    showDialog("友情提示：", "确认将这张照片作为头像上传？", "确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            AppContext.checkList.add(mlistData.get(position));
//                            Intent intent = new Intent(GalleryActivity.this, MeInfoActivity.class);
//                            intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
//                            startActivity(intent);
//                        }
//                    });
                } else if (type.equals(AppConstants.FROMChat)) {
                    if (!AppContext.checkList.contains(photo)) {
//                      if (AppContext.checkList.size()>=20) {
//                          Toast.makeText(getApplicationContext(),
//                              "最多只能选择20张照片进行上传哦",
//                              Toast.LENGTH_SHORT)
//                              .show();
//                          return;
//                      }
                        mGalleryImgAdapter.setCheck(position, view, false);
                    }
                } else if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
//                  if (AppContext.checkList.size() >= 20) {
//                      Toast.makeText(getApplicationContext(),
//                          "最多只能选择20张照片进行上传哦",
//                          Toast.LENGTH_SHORT)
//                          .show();
//                      return;
//                  } else {
//                      mGalleryImgAdapter.setCheck(position, view);
//                  }
                    mGalleryImgAdapter.setCheck(position, view, false);
                } else if (type.equals(AppConstants.FROMWEB)) {
                    if (cut == 1) { // 需要裁剪
                        AppContext.checkList.add(mlistData.get(position));
                        Intent intent = new Intent(GalleryActivity.this, CommonBrowser.class);
                        intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                        intent.putExtra("cut_type", "yes");
                        startActivity(intent);
                    } else { // 不需要裁剪
                        mGalleryImgAdapter.setCheck(position, view, false);
                    }
                } else {
                    mGalleryImgAdapter.setCheck(position, view, false);
                }
			}
		});
	}

	private void initProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("加载... 请等待...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(true);		
	}

	private class LoadImageGalleryTask extends AsyncTask<Void, Void, Void>{		
		private Context context;
		private Cursor imagecursor = null;
		private int image_column_index;
		private String folderName;
		
		public LoadImageGalleryTask(Context context, String folderName) {
			this.context = context;
			this.folderName = folderName;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
			mlistData = new ArrayList<Photo>();
			CursorLoader loader;
			String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			String orderBy = MediaStore.Images.Media._ID;	
			
			if (folderName.equals("")) {
				loader = new CursorLoader(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,null, null, orderBy + " DESC");
			} else {
				loader = new CursorLoader(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?",
                    new String[]{folderName}, orderBy + " DESC");
			}
			imagecursor = loader.loadInBackground();			
		}

		@Override
		protected Void doInBackground(Void... params) {	
			if (imagecursor == null) return null;
			image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
			int count = imagecursor.getCount();
			for (int i = 0; i < count; i++) {
				imagecursor.moveToPosition(i);
				int id = imagecursor.getInt(image_column_index);
				int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);				
				String arrPath = imagecursor.getString(dataColumnIndex);
				if (!new File(arrPath).exists()){
                    continue;
                }
				Photo item = new Photo();
				item.id = String.valueOf(id);
				item.dataColumnIndex = dataColumnIndex;
				item.imgPath = arrPath;
				mlistData.add(item);
			}												
			return null;
		}	
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (imagecursor == null) return ;
			mGalleryImgAdapter = new ImgChooserAdapter(GalleryActivity.this, mlistData, AppContext.checkList, ImageLoadOptions.getClassPhotoOptions());
			mGridView.setAdapter(mGalleryImgAdapter);
            mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),true,true));
			imagecursor.close();
			mProgressDialog.dismiss();
		}

	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mGalleryImgAdapter.clearCache();
        System.gc();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                AppContext.checkList.clear();
                if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                    Intent intent = new Intent(GalleryActivity.this, ClassPhotoDetialManager.class);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                    intent.putExtra(AppConstants.PARAM_ALBUM, album);
                    startActivity(intent);
                } else if (type.equals(AppConstants.PARAM_CommonBrowser)) {
                    GalleryActivity.this.finish();
                } else if (type.equals(AppConstants.PARAM_UPLOAD_GROW)) {
                    GalleryActivity.this.finish();
                } else if (type.equals(AppConstants.FROMSPEAK)) {
                    GalleryActivity.this.finish();
                } else if (type.equals(AppConstants.FROMDAIRY)) {
                    GalleryActivity.this.finish();
                } else if (type.equals(AppConstants.FROMWEB)) {
                    AppContext.checkList.clear();
                    GalleryActivity.this.finish();
                } else if (type.equals(AppConstants.FROMMEINFO)) {
                    AppContext.checkList.clear();
                    GalleryActivity.this.finish();
                } else {
                    Intent intent = new Intent(GalleryActivity.this, LifeWorkPhoto.class);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, type);
                    intent.putExtra(AppConstants.PARAM_ALBUM, photo);
                    intent.putExtra(AppConstants.INTENT_UPLOAD_TYPE, type);
                    startActivity(intent);
                }
            }
            GalleryActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
