package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.Dir;
import com.yey.kindergaten.bean.FolderItem;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.ImageDir;
import com.yey.kindergaten.util.ImageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetSDCardAlbumActivity extends BaseActivity implements OnClickListener,OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    public static Bitmap bimap;
    TextView titletextview;
    ImageView leftbtn;
    ServiceAdapter albumAdapter;
    ListView listview;
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private LifePhoto photo;
    private Album album;
    String type; // 跳转标志位："fromlife",表示是从主界面来，是批量编辑："fromlifemain"是指从照片主界面跳转
    List<ImageDir> dirList;
    String albumid = null; // 相册id
    Term term;//学期
    private String terms;
    private String desc;
    private String imgType;
    private List<String> uidlist = new ArrayList<String>();
    private ArrayList<FolderItem> mlistData;
    String lifetype; // 1，生活剪影 | 2，手工作品

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            System.exit(1);
            finish();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getsdcardalbum);

        type = getIntent().getExtras().getString("typefrom");
        albumid = getIntent().getExtras().getString(AppConstants.PARAM_ALBUMID);
        album = (Album)getIntent().getExtras().getSerializable("album");
        term = (Term) getIntent().getExtras().getSerializable("term");
        desc = getIntent().getExtras().getString("decs");
        terms = getIntent().getExtras().getString("terms");
        imgType = getIntent().getExtras().getString(AppConstants.INTENT_IMAGE_TYPE);
        lifetype = getIntent().getExtras().getString("lifetype");
        photo = (LifePhoto) getIntent().getExtras().getSerializable("photo");
        uidlist = getIntent().getExtras().getStringArrayList("childlist");
        prepare();
    }

    public void prepare() {
        dirList = new ArrayList<ImageDir>();
        listview = (ListView) findViewById(R.id.activity_getalbumgrid);
        titletextview = (TextView) findViewById(R.id.header_title);
        titletextview.setText(R.string.service_phonealbum);
        leftbtn = (ImageView) findViewById(R.id.left_btn);
        leftbtn.setVisibility(View.VISIBLE);
        leftbtn.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        albumAdapter = new ServiceAdapter(GetSDCardAlbumActivity.this, dirList, AppConstants.CONTACTS_ALBUM);
        listview.setAdapter(albumAdapter);
        listview.setOnScrollListener(ImageManager.pauseScrollListener);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                AppContext.checkList.clear();
                break;
        }
    }

    private void showToast(final Context context, final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (AppUtils.getmem_UNUSED(AppContext.getInstance()) < 1024 * 50) {
            showToast(AppContext.getInstance(), "抱歉，您手机内存已满");
            handler.sendEmptyMessage(0);
            return;
        }
        Dir dir = (Dir) parent.getItemAtPosition(position);
        if (dir != null) {
            Intent intent = new Intent(getApplicationContext(), SelectPhotoActivityActivity.class);
            intent.putExtra(AppConstants.DIR_ID, dir.id);
            intent.putExtra(AppConstants.DIR_NAME, dir.name);
            intent.putExtra(AppConstants.PHOTOLIST, AppContext.checkList);
            intent.putExtra("typefrom", type);
            if (type.equals(AppConstants.PARAM_UPLOAD_LIFE)) {
                intent.putExtra("lifephoto", photo);
                intent.putExtra("term", term);
                intent.putExtra("lifetype", "1");
                startActivity(intent);
            } else if (type.equals(AppConstants.PARAM_UPLOAD_WORK)) {
                intent.putExtra("lifephoto", photo);
                intent.putExtra("term", term);
                intent.putExtra("lifetype", "2");
                startActivity(intent);
            } else if (type.equals(AppConstants.PARAM_UPLOAD_CLASSPHOTO)) {
                intent.putExtra(AppConstants.PARAM_ALBUMID, albumid);
                intent.putExtra("album", album);
                startActivity(intent);
                GetSDCardAlbumActivity.this.finish();
            } else if (type.equals(AppConstants.PARAM_UPLOAD_BATCH)) {
                intent.putExtra("type", type);
                intent.putExtra("decs", desc);
                intent.putExtra(AppConstants.INTENT_IMAGE_TYPE, imgType);
                intent.putExtra("terms", terms);
                intent.putStringArrayListExtra("childlist", (ArrayList<String>) uidlist);
                intent.putExtra("lifetype", lifetype);
                startActivity(intent);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivityForResult(intent, 1);
            }
        }
		
		/*Intent intent=new Intent(GetSDCardAlbumActivity.this,SelectPhotoActivityActivity.class);
	    intent.putExtra("datalist", (Serializable) dirList.get(position).imageList);	
	    intent.putExtra("typefrom", type);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
	    startActivity(intent);*/
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

    private class LoadFolderGalleryTask extends AsyncTask<Void, Void, Void> {
        private Cursor imagecursor = null;
        private Context mContext;

        public LoadFolderGalleryTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mlistData = new ArrayList<FolderItem>();
            String[] columns = { MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "max(" + MediaStore.Images.Media._ID + ")as maxid",
                    MediaStore.Images.Media.DATA + " as maxid_url"};
            CursorLoader loader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    "1=1) group by(" + MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    null,
                    MediaStore.Images.Media._ID + " desc");
            imagecursor = loader.loadInBackground();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int icon_id_url = imagecursor.getColumnIndex("maxid_url");
            int count = imagecursor.getCount();

            for (int i = 0; i < count; i++) {
                FolderItem folderItem = new FolderItem();
                imagecursor.moveToPosition(i);
                folderItem.setFolderName(imagecursor.getString(image_column_index));
                folderItem.setFolderIconUrl(imagecursor.getString(icon_id_url));
                mlistData.add(folderItem);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (imagecursor == null) return ;
            albumAdapter = new ServiceAdapter(GetSDCardAlbumActivity.this, mlistData, AppConstants.CONTACTS_ALBUM);
            listview.setAdapter(albumAdapter);
            imagecursor.close();
            mProgressDialog.dismiss();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
//        CursorLoader loader;
//        String foldername = null;
//
//        String[]columns = {Media.DATA,Media._ID};
//        String orderBy = MediaColumns.DATE_MODIFIED + " desc";
//        if (foldername.equals("")) {
//            loader = new CursorLoader(getApplicationContext(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns
//                     ,null,null,orderBy);
//        } else {
//
//        }

        return new CursorLoader(getApplicationContext(),
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            new String[] {
                ImageColumns.BUCKET_ID,
                MediaColumns.DATA, // 图片地址
                ImageColumns.BUCKET_DISPLAY_NAME, // 图片所在目录的名称
                BaseColumns._ID
            },
            MediaColumns.SIZE + ">=30720",
            null,
            // MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " asc," +
            MediaColumns.DATE_MODIFIED + " desc"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor!=null) {
            if (cursor.getCount() > 0) {
                ArrayList<Dir> list = new ArrayList<Dir>();
                SparseArray<Dir> dirs = new SparseArray<Dir>();

                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(ImageColumns.BUCKET_ID));
                    String path = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
                    String dirPath;
                    int index = path.lastIndexOf('/');
                    if (index > 0) {
                        dirPath = path.substring(0, index);
                    } else {
                        dirPath = path;
                    }
                    Dir dir = dirs.get(id);
                    if (dir == null) {
                        dir = new Dir();
                        dir.id = String.valueOf(id);
                        dir.name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
                        dir.text = dirPath;
                        dir.imgPath = path;
                        dir.length = 1;
                        list.add(dir);

                        dirs.put(id, dir);
                    } else {
                        dir.length++;
                    }
                }

                albumAdapter = new ServiceAdapter(GetSDCardAlbumActivity.this, list, AppConstants.CONTACTS_ALBUM);
                listview.setAdapter(albumAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }


}
