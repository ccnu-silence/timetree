package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.SendMsgExpandAdapter;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Parent;
import com.yey.kindergaten.bean.SendMsgChildItem;
import com.yey.kindergaten.bean.SendMsgGroupItem;
import com.yey.kindergaten.bean.Term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectChildLifePhotoActivity extends BaseActivity implements OnClickListener{

	//导航栏控件
	@ViewInject(R.id.header_title)TextView titletv;
	@ViewInject(R.id.right_btn)ImageView right_btn;
	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.right_tv)TextView right_tv;
	
	@ViewInject(R.id.common_loading)LinearLayout layout_loding;
	private ExpandableListView expListView;
	
	private List<Parent>listParents=new ArrayList<Parent>();;
	private SendMsgExpandAdapter adapter;
	private SendMsgChildItem  childItem;
	private SendMsgGroupItem groupItem;
	private List<SendMsgChildItem> childlist;
	
    Contacts contants=AppContext.getInstance().getContacts();
    List<Classe> classlist=contants.getClasses();
	 /**显示之前选择的老师列表*/
    private ArrayList<String>teacherlistselelct;
    
    private ArrayList<String>teachershowlist=new ArrayList<String>();
    public List<SendMsgGroupItem>group=new ArrayList<SendMsgGroupItem>();
	      @Override
	    protected void onCreate(Bundle savedInstanceState) {	    	
	    	   super.onCreate(savedInstanceState);
	    	   setContentView(R.layout.show_child_list_activity);
	    	   
	    	   ViewUtils.inject(this);
	    	   teacherlistselelct=getIntent().getStringArrayListExtra("checklist");
	    	   if(teacherlistselelct==null){
	    		   teacherlistselelct=new ArrayList<String>();
	    	   }
	    	   initView();
	    	   initClick();		 		  
		       initData();	    	 
	    }
	      	      
	      
	  	protected  void initView(){ 
	  	   expListView=(ExpandableListView) findViewById(R.id.id_exlist);
	  	   expListView.setGroupIndicator(null);	 
	       expListView.setDivider(null);
	       titletv.setVisibility(View.VISIBLE);    	
	       titletv.setText(AppContext.getInstance().getTerm().getCname());
	       left_btn.setVisibility(View.VISIBLE);
	       right_tv.setVisibility(View.VISIBLE);
	       right_tv.setText("确定");	    		    	
	    }
	      
	    protected  void  initClick(){
	    	 right_tv.setOnClickListener(this);
	    	 left_btn.setOnClickListener(this);
	    	 //让点击收缩失效
	    	 expListView.setOnGroupClickListener(new OnGroupClickListener() {			
				@Override
				public boolean onGroupClick(ExpandableListView expandablelistview,
							View view, int i, long l) {			
						return true ;
				    	}
				   });
	  
	    }
	  	
	     void notifydate(){	
		       if(group==null||group.size()==0){    
			       for(int i=0;i<listParents.size();i++){		         		  	        		     
			           childlist = new ArrayList<SendMsgChildItem>();
			           for(int li=0;li<listParents.size();li++){
					         childItem=new SendMsgChildItem(listParents.get(li).getUid()+"", " ", listParents.get(li).getRealname(),listParents.get(li).getAvatar());
						     childlist.add(childItem);   
						   }			 																	        	  							        	 	      	               
			          }	 
			         groupItem  = new SendMsgGroupItem(111+"","全选",childlist);
				     group.add(groupItem);	
		        } 
			     adapter=new SendMsgExpandAdapter(this, group,teacherlistselelct,expListView);           
			    
				if(teachershowlist!=null){
		    	     adapter=new SendMsgExpandAdapter(this, group,teacherlistselelct,teachershowlist,expListView); 
		         }else if(teachershowlist==null){
		        	 adapter=new SendMsgExpandAdapter(this, group,teacherlistselelct,teachershowlist,expListView); 
		         }
		         else{
		        	 adapter=new SendMsgExpandAdapter(this, group,teacherlistselelct,expListView);
		        }
			     expListView.setAdapter(adapter);			 
				 expListView.expandGroup(0);
				 layout_loding.setVisibility(View.GONE);
			 }
	      
		    private void initData() {
		    	Term term = AppContext.getInstance().getTerm();
		    	List<LifePhoto> list = term.getPhoto();
		    	Iterator<LifePhoto> it = list.iterator();
		    	while (it.hasNext()) {
					LifePhoto lifePhoto = (LifePhoto) it.next();
				    Parent p = new Parent();
				    p.setCid(term.getCid());
				    p.setCname(term.getCname());
				    p.setUid(lifePhoto.getUserid());
				    p.setRealname(lifePhoto.getName());
				    p.setAvatar(lifePhoto.getHeadpic());
				    listParents.add(p);					
				}		    	
		     notifydate();				
			}


			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.left_btn:
					this.finish();
					break;
				case R.id.right_tv:
					Intent intent = new Intent(this,BatchLifePhotoActivity.class);
				    intent.putStringArrayListExtra("selectlist", teacherlistselelct);
					setResult(RESULT_OK, intent);
					this.finish();
					break;
				}
				
			}       
}
