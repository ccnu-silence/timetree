package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.bean.ThreadsBean;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

public class SelectPhotoActivityActivity extends BaseActivity implements OnClickListener,OnItemClickListener,LoaderManager.LoaderCallbacks<Cursor>{

    TextView titletextview;
    ImageView leftbtn;
    TextView righttv;
    GridView gridView;
    SelectPhotoAdapter adapter;
    List<Photo> datalist = new ArrayList<Photo>();
    String type;
    private String mDirId;
    private String dirname;
    List<ThreadsBean>listbean = null;
    private String isSuccess;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectphoto);
        if(getIntent().getExtras()!=null)
        {
            type=getIntent().getExtras().getString("typefrom");
            if(type == null){
                type = AppContext.getInstance().getType();
            }
            dirname = getIntent().getExtras().getString(AppConstants.DIR_NAME);
            albumid = getIntent().getExtras().getString(AppConstants.PARAM_ALBUMID);
            term= (Term) getIntent().getExtras().getSerializable("term");
            lifetype=getIntent().getStringExtra("lifetype");
            photo= (LifePhoto) getIntent().getSerializableExtra("lifephoto");
            uidlist= getIntent().getExtras().getStringArrayList("childlist");
            desc = getIntent().getExtras().getString("decs");
            terms = getIntent().getExtras().getString("terms");
            imgType=getIntent().getExtras().getString(AppConstants.INTENT_IMAGE_TYPE);
            album=(Album)getIntent().getExtras().getSerializable("album");
        }

        prepare();
        initview();
    }
    public void prepare()
    {
        titletextview=(TextView) findViewById(R.id.header_title);
        titletextview.setText(dirname);
        leftbtn=(ImageView) findViewById(R.id.left_btn);
        leftbtn.setVisibility(View.VISIBLE);
        leftbtn.setOnClickListener(this);
        righttv=(TextView) findViewById(R.id.right_tv);
        righttv.setVisibility(View.VISIBLE);
        righttv.setText("确定");
        righttv.setOnClickListener(this);
        gridView=(GridView) findViewById(R.id.selectphoto_gridview);
        gridView.setOnScrollListener(ImageManager.pauseScrollListener);
        Intent intent = getIntent();
        mDirId = intent.getStringExtra(AppConstants.DIR_ID);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void initview()
    {
        adapter=new SelectPhotoAdapter(this,datalist,AppContext.checkList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.right_tv:
                if (type.equals(AppConstants.FROMSPEAK)) {
                    Intent intent=new Intent(SelectPhotoActivityActivity.this,ServicePublishSpeakActivity.class);
                    intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                    startActivity(intent);
                }else if(type.equals(AppConstants.FROMDAIRY)){
                    Intent intent=new Intent(SelectPhotoActivityActivity.this,KeepDiaryActivity.class);
                    intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                    startActivity(intent);
                }else if(type.equals(AppConstants.FROMWEB)){
                    Intent intent=new Intent(SelectPhotoActivityActivity.this,CommonBrowser.class);
                    intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
                    startActivity(intent);
                }else if(type.equals(AppConstants.FROMChat)){
                    Intent chatintent=new Intent(SelectPhotoActivityActivity.this,ChatActivity.class);
                    chatintent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
			/*	setResult(RESULT_OK, chatintent);
				finish();*/
                    startActivity(chatintent);
                    finish();
                }else if(type.equals(AppConstants.PARAM_UPLOAD_LIFE)){
                    Intent chatintent=new Intent(SelectPhotoActivityActivity.this,BatchLifePhotoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("lifetype", lifetype);
                    bundle.putSerializable("lifephoto", photo);
                    bundle.putSerializable("term", term);
                    bundle.putString("type", type);
                    chatintent.putExtras(bundle);
                    startActivity(chatintent);
                    finish();
                }else if(type.equals(AppConstants.PARAM_UPLOAD_WORK)){
                    Intent uploadIntent=new Intent(SelectPhotoActivityActivity.this,BatchLifePhotoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("type", type);
                    bundle.putString("lifetype", lifetype);
                    bundle.putSerializable("lifephoto", photo);
                    bundle.putSerializable("term", term);
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                }else if(type.equals(AppConstants.PARAM_UPLOAD_BATCH)){
                    Intent uploadIntent=new Intent(SelectPhotoActivityActivity.this,BatchLifePhotoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("type", type);
                    bundle.putString("lifetype", lifetype);
                    bundle.putStringArrayList("childlist", (ArrayList<String>) uidlist);
                    bundle.putString("desc", desc);
                    bundle.putString("terms", terms);
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                }else if(type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)){
                    Intent uploadIntent=new Intent(SelectPhotoActivityActivity.this,BatchLifePhotoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putParcelableArrayList(AppConstants.PHOTOLIST, AppContext.checkList);
                    bundle.putString("type", type);
                    bundle.putSerializable("album", album);
                    bundle.putString(AppConstants.PARAM_ALBUMID, albumid);
                    uploadIntent.putExtras(bundle);
                    startActivity(uploadIntent);
                    finish();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Photo photo = (Photo) parent.getItemAtPosition(position);
        if (photo == null)
        {
            return;
        }
        if (!AppContext.checkList.contains(photo))
        {
            if (AppContext.checkList.size() >=9)
            {
                Toast.makeText(getApplicationContext(),
                        "最多只能选择9张照片进行上传哦",
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }
        if (type.equals(AppConstants.FROMSPEAK)) {
            if (!AppContext.checkList.contains(photo))
            {
                if (AppContext.checkList.size() >= 8)
                {
                    Toast.makeText(getApplicationContext(),
                            "最多只能选择8张照片进行上传哦",
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        }else if(type.equals(AppConstants.FROMDAIRY)){
            adapter.setCheck(position, view);
            Intent intent=new Intent(SelectPhotoActivityActivity.this,KeepDiaryActivity.class);
            intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
            startActivity(intent);
        }else if (type.equals(AppConstants.FROMChat)) {
            if (!AppContext.checkList.contains(photo))
            {
                if (AppContext.checkList.size() >= 8)
                {
                    Toast.makeText(getApplicationContext(),
                            "最多只能选择8张照片进行上传哦",
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        }else if(type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)){
            if (!AppContext.checkList.contains(photo))
            {
                if (AppContext.checkList.size() >= 8)
                {
                    Toast.makeText(getApplicationContext(),
                            "最多只能选择8张照片进行上传哦",
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        }else if(type.equals(AppConstants.FROMWEB)){
            adapter.setCheck(position, view);
            Intent intent=new Intent(SelectPhotoActivityActivity.this,CommonBrowser.class);
            intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
            startActivity(intent);
        }

        adapter.setCheck(position, view);
    }

    class SelectPhotoAdapter extends BaseListAdapter<Photo>{
        private class Holder
        {
            public ImageView mImageView, selectImage,unselectImage;
            public  ProgressBar par;
        }
        private List<Photo> mCheckList;
        public SelectPhotoAdapter(Context context, List<Photo> list) {
            super(context, list);
        }
        public SelectPhotoAdapter(Context context, List<Photo> list, List<Photo> checkList)
        {
            super(context, list);
            mCheckList = checkList;
        }

        public void setCheck(int postion, View view)
        {
            Photo photo = list.get(postion);

            boolean checked = mCheckList.contains(photo);

            Holder holder = (Holder) view.getTag();

            if (checked)
            {
                mCheckList.remove(photo);
                holder.selectImage.setVisibility(View.GONE);
                holder.unselectImage.setVisibility(View.VISIBLE);
            }
            else
            {
                mCheckList.add(photo);
                holder.selectImage.setVisibility(View.VISIBLE);
                holder.unselectImage.setVisibility(View.GONE);
            }
        }

        @Override
        public View bindView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_selectphotoitem, null);
                holder = new Holder();
                holder.mImageView = (ImageView) convertView.findViewById(R.id.selectphoto_image);
                holder.selectImage = (ImageView) convertView.findViewById(R.id.selectphoto_select);
                holder.unselectImage = (ImageView) convertView.findViewById(R.id.selectphoto_unselect);
                holder.par = (ProgressBar)convertView.findViewById(R.id.selectphoto_probar);
                convertView.setTag(holder);
            } else
            {
                holder = (Holder) convertView.getTag();
            }
            holder.par.setVisibility(View.GONE);
            Photo photo = list.get(position);

            if(AppContext.checkList.contains(photo)){
                holder.selectImage.setVisibility(View.VISIBLE);
                holder.unselectImage.setVisibility(View.GONE);
            }else{
                holder.selectImage.setVisibility(View.GONE);
                holder.unselectImage.setVisibility(View.VISIBLE);
            }
            String path="file:///"+photo.imgPath;
//				File file = new File(path);
//				try {
//					InputStream is = new FileInputStream(file);
//				    BitmapDrawable drawable = new BitmapDrawable(is);  
//			        //通过BitmapDrawable对象获得Bitmap对象  
//			        Bitmap bitmap = drawable.getBitmap();  
//			        //利用Bitmap对象创建缩略图  
//			        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 51, 108);  
//			        //imageView 显示缩略图的ImageView  
//			        holder.mImageView.setImageBitmap(bitmap);  
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}						
            ImageLoader.getInstance().displayImage(path, holder.mImageView,ImageLoadOptions.getGalleryOptions());

            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new CursorLoader(getApplicationContext(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaColumns.DATA//图片地址
                },
                mDirId == null ? null : ImageColumns.BUCKET_ID + "=" + mDirId,
                null,
                MediaColumns.DATE_MODIFIED + " desc"
        );
    }
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.getCount() > 0)
        {
            ArrayList<Photo> list = new ArrayList<Photo>();

            cursor.moveToPosition(-1);
            while (cursor.moveToNext())
            {
                Photo photo = new Photo();

                photo.imgPath = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
                list.add(photo);
            }
            adapter=new SelectPhotoAdapter(this,list,AppContext.checkList);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
        }

    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }
}
