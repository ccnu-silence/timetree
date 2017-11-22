package com.yey.kindergaten.activity.classvideo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.FragmentAdapter;
import com.yey.kindergaten.fragment.ParentFragment;
import com.yey.kindergaten.service.UploadVideoService;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.DialogTips;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2015/7/13.
 * 班级视频主类
 */
public class ClassVideoActivity  extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup radioGroup = null;
    private RadioButton radioButton1 = null;
    private RadioButton radioButton2 = null;
    private TextView smovetv = null;
    private TextView title_tv;
    private TextView right_tv;

    private int VIDEO = 2;
    private ProgressBar progressBar;// 显示上传进度
    private TextView progressTv;    // 显示上传百分比
    private DialogTips dialog;
    private ServiceConnection serviceConnection;
    private UploadVideoService.UploadBinder binder;

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private UploadFragment uploadFragment = new UploadFragment();
    private ParentFragment parentFragment = new ParentFragment(); // 这里由于没有写看视频的fragment，随便用一个
    FragmentAdapter fragAdapter;
    ViewPager viewpager;

    private CharSequence mStrs[] = new CharSequence[]{"本地上传","录制视频","取消"};
    private String TAG = "ClassVideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_video_activity);
        ViewUtils.inject(this);
        
        initView();
        initFragments();
        initListener();

        IntentFilter filter = new IntentFilter(AppConstants.UPLOAD_VIDEO_ACTION);
        filter.setPriority(5);
        registerReceiver(broadcastReceiver,filter);
    }

    private void initFragments() {
        fragments.add(parentFragment);
        fragments.add(uploadFragment);
        fragAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(fragAdapter);
        viewpager.setOnPageChangeListener(this);
    }

    private void initListener() {
        right_tv.setOnClickListener(this);
    }

    private void initView() {
        title_tv = (TextView) findViewById(R.id.header_title);
        title_tv.setText("班级视频");
        right_tv = (TextView) findViewById(R.id.right_tv);
        right_tv.setText("传视频");
        right_tv.setVisibility(View.VISIBLE);

        viewpager = (ViewPager) findViewById(R.id.contacts_main_viewpage);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton1.setTextColor(getResources().getColor(R.color.radio_button_check_color));
        radioButton2 = (RadioButton) findViewById(R.id.radioButton3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        smovetv = (TextView)findViewById(R.id.smovetextview);
        radioGroup.setOnCheckedChangeListener(this);
    }

    private void RecordingVideo() {
        Intent intent = new Intent(this,VideoNewActivity.class);
        startActivityForResult(intent, VIDEO);
    }

    /**
     * 点击列表上传按钮显示对话框，完善视频信息
     * @param path
     */
    private void showPreUploadDialog(final String path){
        DialogTips dialog = new DialogTips(this,"视频信息","", "开始上传", false, true);
        View view = LayoutInflater.from(this).inflate(R.layout.classvideo_upload_inflater,null);
        final EditText titile_et = (EditText) view.findViewById(R.id.title_tv);
        final EditText type_et = (EditText) view.findViewById(R.id.type_et);
        final EditText jianjie_et = (EditText) view.findViewById(R.id.jianjie_et);
        dialog.setView(view);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (titile_et.getText().length() == 0) {
                    showToast("标题不能为空");
                    return;
                }
                Intent intent = new Intent(ClassVideoActivity.this, UploadVideoService.class);
                intent.putExtra("title", titile_et.getText().toString() + "");
                intent.putExtra("tag", type_et.getText().toString() + "");
                intent.putExtra("desc", jianjie_et.getText().toString() + "");
                intent.putExtra("filePath", path);
                binderService(intent);
                UtilsLog.i(TAG,"click to start upload");
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO) {
                Uri uri = data.getData();
                String filePath = null;
                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor!=null && cursor.moveToNext()) {
                    filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                    cursor.close();
                }
                showPreUploadDialog(filePath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void binderService(Intent intent) {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) { }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (UploadVideoService.UploadBinder) service;
            }
        };
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            if (intent == null)return;
            int status = intent.getIntExtra("status", 0);
            int progress = intent.getIntExtra("progress", 0);

            if (status == ShowLocalVideoActivity.start_what) {
                UtilsLog.i(TAG, "broadcastReceiver : " + " status is:" + status);
                showUploadingDialog();
                progressTv.setText("0%");
            } else if (status == ShowLocalVideoActivity.uploading_what) {
                progressBar.setProgress(progress);
                UtilsLog.i(TAG, "broadcastReceiver : " + " progress is:" + progress);
                if (progress < 100) {
                    progressTv.setText(progress+"%");
                } else {
                    progressTv.setText("处理中...");
                }
            } else if (status == ShowLocalVideoActivity.finish_what) {
                UtilsLog.i(TAG,"broadcastReceiver : "+" status is:" + status);
                dialog.dismiss();
                binder.cancle();
                showToast("上传完成");
            } else if (status == ShowLocalVideoActivity.exception_what) {
                UtilsLog.i(TAG,"broadcastReceiver : Exception");
                dialog.dismiss();
                binder.cancle();
                showToast("错误");
            }
        }
    };

    /**
     * 显示上传进度对话框
     */
    public void showUploadingDialog() {
        if (dialog == null) {
            dialog  = new DialogTips(this, "", "", "取消上传", false, false);
            View view = LayoutInflater.from(this).inflate(R.layout.progress_bar_horizontal, null);

            ImageView close_iv = (ImageView) view.findViewById(R.id.close_iv);
            progressBar = (ProgressBar) view.findViewById(R.id.show_progress_pb);
            progressTv = (TextView) view.findViewById(R.id.show_progress_tv);

            close_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binder.cancle();
                            showToast("任务已取消,上传中断");
                            dialog.dismiss();
                        }
                    });
                }
            });
            dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binder.cancle();
                            showToast("任务已取消,上传中断");
                            dialog.dismiss();
                        }
                    });
                }
            });
            dialog.setView(view);
            dialog.show();}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.right_tv:
                showDialogItems(mStrs, "上传视频", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(ClassVideoActivity.this, ShowLocalVideoActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                RecordingVideo();
                                break;
                            case 2:
                              dialogInterface.dismiss();
                                break;
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int position) { }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int x = (int)((position + positionOffset) * smovetv.getWidth());
        ((View)smovetv.getParent()).scrollTo(-x, smovetv.getScrollY());
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            radioButton1.setChecked(true);
            radioButton1.setTextColor(this.getResources().getColor(R.color.radio_button_check_color));
            radioButton2.setTextColor(getResources().getColor(R.color.contact_mainlvtitletv));
        } else if (position == 1) {
            radioButton2.setChecked(true);
            radioButton2.setTextColor(this.getResources().getColor(R.color.radio_button_check_color));
            radioButton1.setTextColor(this.getResources().getColor(R.color.contact_mainlvtitletv));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        int current = viewpager.getCurrentItem();
        switch (checkedId) {
            case R.id.radioButton1:
                if (current!=0) {
                    viewpager.setCurrentItem(0);
                }
            break;
            case R.id.radioButton3:
                if (current!=1) {
                    viewpager.setCurrentItem(1);
                }
            break;
        }
    }

}
