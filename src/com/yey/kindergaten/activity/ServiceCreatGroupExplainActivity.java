package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.AppConstants;

public class ServiceCreatGroupExplainActivity extends BaseActivity implements OnClickListener{

	@ViewInject(R.id.groupexpalin_headiv)ImageView headiv;
	@ViewInject(R.id.groupexpalin_headcreattv)TextView titletv;
	@ViewInject(R.id.groupexpalin_headcreatmiaosohu)TextView titlemiaoshu;
	@ViewInject(R.id.groupexpalin_contenttv)TextView contentmiaoshu;
	@ViewInject(R.id.groupexpalin_nextbtn)Button nextbtn;
	@ViewInject(R.id.header_title)TextView headtitle;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	String state="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_creategroupexplain);
		ViewUtils.inject(this);		
		if(getIntent().getExtras()!=null){
			state=getIntent().getExtras().getString(AppConstants.SERVICECREATESTATE);
		}
		initview();
		setOnclik();
	}
	
	
	public void initview(){
		left_btn.setVisibility(View.VISIBLE);
		headtitle.setText("创建群");
		if(state.equals(AppConstants.CREATEKINDER)){
			headiv.setImageResource(R.drawable.creategroupex_kind);
			titletv.setText("创建幼儿园群");
			titlemiaoshu.setText("全国的老师,可以通过群号查找加入");
			String text="幼儿园群是幼儿园真是的互动社区,方便园长,老师和家长能够及时的交流与分享和掌握园所动态 <br><br>功能特色:<br>1.电子文档共享;<br>2.群发消息;<br>3.智能通讯录;<br>4.在线管理;";
		    contentmiaoshu.setText(Html.fromHtml(text));
		}else if(state.equals(AppConstants.CREATECLASS)){
			headiv.setImageResource(R.drawable.creategroupex_kind);
			titletv.setText("创建班级群");
			titlemiaoshu.setText("方便查看全员短信,公告");
			String text="班级群是班级真是的互动社区,方便老师和家长能够及时的交流沟通与分享 <br><br>功能特色:<br>1.班级主页;<br>2.网络家园练习册;<br>3.群发消息;";
		    contentmiaoshu.setText(Html.fromHtml(text));
		}else{
			headiv.setImageResource(R.drawable.creategroupex_class);
			titletv.setText("创建交流群");
			titlemiaoshu.setText("方便查看大家甲流");
			String text="幼儿园群是幼儿园真是的互动社区,方便园长,老师和家长能够及时的交流与分享和掌握园所动态 <br><br>功能特色:<br>1.电子文档共享;<br>2.群发消息;<br>3.智能通讯录;<br>4.在线管理;";
		    contentmiaoshu.setText(Html.fromHtml(text));
		}
	}
	
	public void setOnclik(){
		nextbtn.setOnClickListener(this);
		left_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.groupexpalin_nextbtn:
			Intent intent;
			if(state.equals(AppConstants.CREATEKINDER)){
				intent=new Intent(this,ServiceCreateKinderActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEKINDER);
				startActivity(intent);
			}else if(state.equals(AppConstants.CREATECLASS)){
				intent=new Intent(this,ServiceCreateKinderActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATECLASS);
				startActivity(intent);
			}else{
				intent=new Intent(this,ServiceCreateKinderActivity.class);
				intent.putExtra(AppConstants.SERVICECREATESTATE, AppConstants.CREATEGENERALGROUP);
				startActivity(intent);
			}
			
			break;

		default:
			break;
		}
		
	}
}
