package com.yey.kindergaten.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ClassPhotoCreateAlbum;
import com.yey.kindergaten.activity.ClassPhotoDetialManager;
import com.yey.kindergaten.activity.ServicePublishSpeakActivity;
import com.yey.kindergaten.adapter.ClassPhotoAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.ClassPhoto;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.UtilsLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 班级相册Fragment
 *
 * @author
 *
 */
public class ClassPhotoFragment extends FragmentBase implements OnItemClickListener {

    private static final String TAG = "ClassPhotoFragment";
    @ViewInject(R.id.classphoto_gridview)GridView gv;
    ClassPhotoAdapter adapter ;
    List<ClassPhoto> list = new ArrayList<ClassPhoto>();
    List<Album> albumlist = new ArrayList<Album>();
    public boolean editAction = false;
    protected static ArrayList<ClassPhoto> checkList = new ArrayList<ClassPhoto>();
    private int index = 0;
    private String typefrom;
    private ImageLoader imageLoader;
    private AccountInfo accountInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_classphoto, container, false);
        ViewUtils.inject(this, rootView);
        imageLoader = ImageLoader.getInstance();

        if (adapter == null) {
            adapter = new ClassPhotoAdapter(AppContext.getInstance(), albumlist, ImageLoadOptions.getClassPhotoOptions(), imageLoader);
        }
        gv.setAdapter(adapter);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv.setOnItemClickListener(this);

        adapter.setOnInViewClickListener(R.id.select, new ClassPhotoAdapter.onInternalClickListener() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, Object values) {
                adapter.setCheck(position, parentV);
            }
        });
        adapter.setOnInViewClickListener(R.id.unselect, new ClassPhotoAdapter.onInternalClickListener() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, Object values) {
                adapter.setCheck(position, parentV);
            }
        });

        Bundle bundle = getArguments();
        list = (List<ClassPhoto>) bundle.getSerializable(AppConstants.BUNDLE_ALBUM);
        typefrom = bundle.getString("typefrom");
        index = bundle.getInt(AppConstants.BUNDLE_INDEX);

        albumlist.clear();
        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            if (list.get(index).getAlbumlist()!=null && list.get(index).getAlbumlist().size() > 0) {
                albumlist.addAll(list.get(index).getAlbumlist()); // 第几个班的班级相册
            }
        } else {
            Album add = new Album();
            add.setAlbumCover("add");
            albumlist.add(add);
            if (list.get(index).getAlbumlist()!=null && list.get(index).getAlbumlist().size() > 0) {
                albumlist.addAll(list.get(index).getAlbumlist());  // 第几个班的班级相册（增加上传照片）
            }
        }
        adapter.setList(albumlist);
        return rootView;
    }

//  @Override
//  public void onActivityCreated(Bundle savedInstanceState) {
//      super.onActivityCreated(savedInstanceState);
//      initdata();
//  }
//  private void initdata() {
//      Album add = new Album();
//      add.setAlbumCover("add");
//      albumlist.add(add);
//  }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView itemImg = (ImageView)view.findViewById(R.id.classphoto_gv_album_iv);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.common_photo_selecter);
        itemImg.startAnimation(animation);
        if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
            if (editAction) { // 编辑状态，打钩
                Album photo = (Album) parent.getItemAtPosition(position);
                if (photo == null) {
                    return;
                }
                adapter.setCheck(position, view);
            } else { // 非编辑状态，进入相册
                Album album = albumlist.get(position);
                if (typefrom.equals(AppConstants.FROMSPEAK)) { // 发动态
                    Intent intent = new Intent(getActivity(), ServicePublishSpeakActivity.class);
                    intent.putExtra(AppConstants.PARAM_ALBUM, album);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                    getActivity().startActivity(intent);
                } else {
                    // 打开相册
                    Intent intent = new Intent(getActivity(), ClassPhotoDetialManager.class);
                    intent.putExtra(AppConstants.PARAM_ALBUM, album);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                    int cid = list.get(index).getCid();
                    intent.putExtra(AppConstants.PARAM_CID, cid);
                    getActivity().startActivity(intent);
                }
            }
        } else {
            if (position == 0) {
                Intent intent = new Intent(getActivity(), ClassPhotoCreateAlbum.class);
                if (list == null || list.get(index) == null) {
                    UtilsLog.i(TAG, "bundle album is null");
                    Bundle bundle = getArguments();
                    list = (List<ClassPhoto>) bundle.getSerializable(AppConstants.BUNDLE_ALBUM);
                }
                // 修复list.get(index).getCid()空指针问题
                accountInfo = AppServer.getInstance().getAccountInfo();
                int cid = accountInfo.getCid();
                if (list!=null && list.get(index)!=null) {
                    cid = list.get(index).getCid();
                }
                intent.putExtra(AppConstants.PARAM_CID, cid);
                getActivity().startActivity(intent);
            } else if (editAction) {
                Album photo = (Album) parent.getItemAtPosition(position);
                if (photo == null) {
                    return;
                }
                adapter.setCheck(position, view);
            } else {
                Album album = albumlist.get(position);
                if (typefrom.equals(AppConstants.FROMSPEAK)) {
                    Intent intent = new Intent(getActivity(), ServicePublishSpeakActivity.class);
                    intent.putExtra(AppConstants.PARAM_ALBUM, album);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                    getActivity().startActivity(intent);
                } else {
                    // 打开相册
                    Intent intent = new Intent(getActivity(), ClassPhotoDetialManager.class);
                    intent.putExtra(AppConstants.PARAM_ALBUM, album);
                    intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, AppConstants.PARAM_UPLOAD_CLASSPHOTO);
                    int cid = list.get(index).getCid();
                    intent.putExtra(AppConstants.PARAM_CID, cid);
                    getActivity().startActivity(intent);
                }
            }
        }
    }

    @OnClick(value={})
    public void setonClick(View view){ }

    public void showDelView(boolean action) {
        if (action) {
            if (adapter!=null) {
                adapter.setAction(true);
            }
            editAction = true;
        } else {
            AppConstants.checkList.clear();
            adapter.setAction(false);
            editAction = false;
        }
    }

    /**
     * 提交删除
     */
    String array = null;
    public void sumitDel() {
        if (AppConstants.checkList.isEmpty()) {
            ShowToast("请选择相册");
            showDelView(false);
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (Album album : AppConstants.checkList) {
            sb.append(album.getAlbumid() + ",");
        }
        array = sb.toString();
        array = array.toString().substring(0, array.toString().length() - 1);
        final AccountInfo info = AppServer.getInstance().getAccountInfo();
        showDialog("删除相册提示", "你选择了删除" + AppConstants.checkList.size() + "个相册", "删除",  new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                AppServer.getInstance().deleteClassPhoto(array, info.getUid(), new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
                            ShowToast("删除成功");
                            for (Album album:  AppConstants.checkList) {
                                albumlist.remove(album);
                            }
                            adapter.setList(albumlist);
                            showDelView(false);
                            AppConstants.checkList.clear();
                        } else {
                            ShowToast("删除失败");
                            showDelView(false);
                        }
                    }

                });

            }

        }, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.setList(albumlist);
                showDelView(false);
                AppConstants.checkList.clear();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh(list);
        }
    }

    private boolean hidden;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh(list);
        }
    }

    public void refresh(final List<ClassPhoto> rlist) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    albumlist.clear();
                    if (AppServer.getInstance().getAccountInfo().getRole() == 2) {
                        albumlist.addAll(rlist.get(index).getAlbumlist());
                        adapter.setList(albumlist);
                    } else {
                        Album add = new Album();
                        add.setAlbumCover("add");

                        albumlist.add(add);
                        if (rlist!=null && rlist.size()!=0) {
                            albumlist.addAll(rlist.get(index).getAlbumlist());
                            adapter.setList(albumlist);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
        super.onDestroy();
    }
}
