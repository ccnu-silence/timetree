package com.yey.kindergaten.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.BitmapUtil;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.TouchImageView;

/**
 * Created by cm_pc2 on 2015/3/24.
 */
public class PhotoShowActivity extends BaseActivity implements View.OnClickListener {
    private TouchImageView iv_show;
    private String path;
    private TextView tv_title;
    private TextView tv_right;
    private  Bitmap newBitmap;
    private String name;
    private static final String PATHA = Environment
            .getExternalStorageDirectory() + "/yey/kindergaten/readyuoload/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendster_photo);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.header_title);
        tv_title.setText("预览");
        tv_right = (TextView) findViewById(R.id.right_tv);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("确定");
        iv_show = (TouchImageView) findViewById(R.id.iv_friendster_show);
        path = getIntent().getExtras().getString("path");
        name = getIntent().getExtras().getString("name");
        newBitmap = BitmapUtil.getImageByPath(path, false);
        ImageLoader.getInstance().displayImage("file:///" + path, iv_show, ImageLoadOptions.getOptions());
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_tv:
                BitmapUtil.savePhotoToSDCard(newBitmap, PATHA, name);
                Intent back = new Intent();
                setResult(RESULT_OK, back);
                finish();
                break;
            default:
                break;
        }
    }
}
