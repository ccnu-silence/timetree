package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceGroupAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Album;
import com.yey.kindergaten.bean.ClassPhoto;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

import java.util.ArrayList;

public class ServiceShareActivity extends BaseActivity{
	private ServiceGroupAdapter adapter;
	private AccountInfo accountinfo;
    private ArrayList<Album>  classlist=new ArrayList<Album>();
	@ViewInject(R.id.lv_serviceshare)ListView lv_share;
	@ViewInject(R.id.right_btn)ImageView iv_right;
	@ViewInject(R.id.left_btn)ImageView iv_left;
	@ViewInject(R.id.header_title)TextView tv_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serviceshare_main);
		ViewUtils.inject(this);
		initView();
		initData();
	}

	private void initData() {
        AppServer.getInstance().loadClassPhoto(AppServer.getInstance().getAccountInfo().getUid(),new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                ArrayList<ClassPhoto> requestlist= (ArrayList<ClassPhoto>) obj;
                for (int i=0;i<requestlist.size();i++){
                    classlist.addAll(requestlist.get(i).getAlbumlist());
                }
                adapter.setList(classlist);

            }
        });

	}

	private void initView() {
		tv_title.setText("相册选择");
		iv_left.setVisibility(View.VISIBLE);
		iv_right.setVisibility(View.VISIBLE);
		accountinfo=AppServer.getInstance().getAccountInfo();
		Bundle bundle = getIntent().getExtras();
		adapter=new ServiceGroupAdapter(ServiceShareActivity.this, classlist);
		lv_share.setAdapter(adapter);
		lv_share.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
                Intent a=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("photolistid", classlist.get(position).getAlbumid());
                a.putExtras(bundle);
                setResult(RESULT_OK, a);
                finish();
			}
		});
		
		
	}
	@OnClick({(R.id.right_btn),(R.id.left_btn)})
	public  void  onclik(View v){
		switch (v.getId()) {
		case R.id.right_btn:
			Intent a=new Intent();
			Bundle bundle = new Bundle();
//			bundle.putSerializable("sharelist", adapter.getGroupMap());
			a.putExtras(bundle);
			setResult(RESULT_OK, a);
			finish();
			break;
		case R.id.left_btn:
			finish();
			break;
		default:
			break;
		}
	}
}
