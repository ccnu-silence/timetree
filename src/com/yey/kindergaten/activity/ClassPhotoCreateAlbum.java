/**
 * 
 */
package com.yey.kindergaten.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.receive.AppEvent;
import com.yey.kindergaten.util.AppConstants;

import de.greenrobot.event.EventBus;

/**
 * 创建相册
 * @author Administrator
 *
 */
public class ClassPhotoCreateAlbum extends BaseActivity {

    @ViewInject(R.id.left_btn)ImageView left_btn;
    @ViewInject(R.id.right_tv)TextView  right_text;
    @ViewInject(R.id.header_title)TextView  title_tv;
    @ViewInject(R.id.line_ev)EditText line_ev;
    private int cid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classphoto_createalbum);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        title_tv.setText("新建相册");
        cid = getIntent().getIntExtra(AppConstants.PARAM_CID, 0);
        left_btn.setVisibility(View.VISIBLE);
        right_text.setVisibility(View.VISIBLE);
        right_text.setText("保存");
    }

    @OnClick(value={R.id.left_btn,R.id.right_tv})
    public void setonClick(View view){
        switch (view.getId()) {
        case R.id.left_btn:
            finish();
            break;
        case R.id.right_tv:
            final String edit = line_ev.getText().toString();
            if (edit.trim().equals("")) {
                showToast("请输入相册名字!");
                return;
            }

            showLoadingDialog("请稍候");
            AccountInfo info = AppServer.getInstance().getAccountInfo();

            AppServer.getInstance().createClassPhoto(info.getRealname() + "", cid, "", edit, info.getUid(), info.getKid(), new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    cancelLoadingDialog();
                    if (code == AppServer.REQUEST_SUCCESS) {
//                      showToast("新建成功");
                        Album album = new Album();
                        album.setAlbumid((String)obj);
                        album.setAlbumName(edit);
                        postEvent(AppEvent.HOMEFRAGMENT_REFRESH_ADDALBUM);
                        finish();
                    } else {
                        showToast("新建失败");
                    }
                }
            });
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
            }
        }).start();
    }

}
