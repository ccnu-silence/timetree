package com.yey.kindergaten.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;

import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;

import com.yey.kindergaten.bean.SendMsgChildItem;
import com.yey.kindergaten.bean.SendMsgGroupItem;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.util.ImageLoadOptions;



 @SuppressLint("ResourceAsColor")
public  class SendMsgExpandAdapter extends BaseExpandableListAdapter{
		 private LayoutInflater mInflater;
		 private List<SendMsgGroupItem>groupList;	
		 private ExpandableListView explist;
		 private List<String>checkedChildren=new ArrayList<String>();
		 private Map<String, Integer> groupCheckedStateMap = new HashMap<String, Integer>();
	     private Map<String, Boolean>childCheckedStateMap=new HashMap<String,Boolean>();
	     /**传过来的需要显示的老师的列表*/
	     private List<String>teachershowlist=new ArrayList<String>();
		 public SendMsgExpandAdapter(Context context,List<SendMsgGroupItem>groupList,ExpandableListView explist){
                mInflater = LayoutInflater.from(context);
                this.groupList=groupList;                                              
        		this.explist=explist;
                int groupCount = getGroupCount();
        		for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
        			try {
        				SendMsgGroupItem groupItem = groupList.get(groupPosition);    
        				
        				if (checkedChildren==null || checkedChildren == null
        						|| checkedChildren.isEmpty()) {
        					groupCheckedStateMap.put(groupItem.getId()+",", 2);       					
//        					continue;
        				} 
        			
        				for(int childposition=0;childposition<groupItem.
        						         getChilditem().size();childposition++){
        			     SendMsgChildItem childitem=groupItem.getChilditem().get(childposition);
        					if (checkedChildren==null || checkedChildren == null
            						|| checkedChildren.isEmpty()) {
            					childCheckedStateMap.put(childitem.getId(), false);  					
//            					continue;
            				} 
        				}
        			} catch (Exception e) {
        				
        			}
        		}
                		
            }	
	 /**选择参与者时没有刚开始没有参与者的构造函数*/
	 public SendMsgExpandAdapter(Context context,List<SendMsgGroupItem>groupList,List<String> list,ExpandableListView explist){
            mInflater = LayoutInflater.from(context);
            this.groupList=groupList; 
            checkedChildren=list;
            this.explist=explist;
            int groupCount = getGroupCount();
     		
     		for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
     			try {
     				SendMsgGroupItem groupItem = groupList.get(groupPosition); 
     				List<SendMsgChildItem> childrenItems = groupItem.getChilditem(); 
     				if (checkedChildren==null || checkedChildren == null
     						|| checkedChildren.isEmpty()) {
     					groupCheckedStateMap.put(groupItem.getId()+",", 2);
//     					continue;
     				}
    				for(int childposition=0;childposition<groupItem.
					         getChilditem().size();childposition++){
		            SendMsgChildItem childitem=groupItem.getChilditem().get(childposition);
			    	if (checkedChildren==null || checkedChildren == null
						|| checkedChildren.isEmpty()) {
					childCheckedStateMap.put(childitem.getId(), false);  					
//					continue;
				       } 
			        }			    							
    				int  checkedCount = 0;
    				for (SendMsgChildItem childrenItem : childrenItems) {
    					if (checkedChildren.contains(childrenItem.getId()+",")) {
    						checkedCount ++;
    					}
    				}   							
    				if (checkedCount==childrenItems.size()&&childrenItems.size()!=0) {   			
    					groupCheckedStateMap.put(groupItem.getId()+",", 1);
    				}else{   			
    					groupCheckedStateMap.put(groupItem.getId()+",",2);
    				}
     				
     			} catch (Exception e) {
     				
     			}
     		}
             		
         }
	 
	 
    /**选择参与者时刚开始有需要显示参与者的构造函数*/
    public SendMsgExpandAdapter(Context context,List<SendMsgGroupItem>groupList,List<String> list,List<String>showlist,ExpandableListView explist){
        mInflater = LayoutInflater.from(context);
        this.groupList=groupList; 
        checkedChildren=list;
        this.teachershowlist=showlist;
 		int groupCount = getGroupCount();
 		this.explist=explist;
 		
 		for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
 			try {
 				SendMsgGroupItem groupItem = groupList.get(groupPosition); 
 				List<SendMsgChildItem> childrenItems = groupItem.getChilditem(); 
 				if (checkedChildren==null || checkedChildren == null
 						|| checkedChildren.isEmpty()) {
 					groupCheckedStateMap.put(groupItem.getId()+",", 2);
 				} 						 		
 		 	
 				//初始化进来时的状态父控件的值
			     for(int childposition=0;childposition<groupItem.
				                   getChilditem().size();childposition++){
	               SendMsgChildItem childitem=groupItem.getChilditem().get(childposition);
		    	 if (checkedChildren==null || checkedChildren == null
					                       || checkedChildren.isEmpty()) {
			 	     childCheckedStateMap.put(childitem.getId(), false);  					
	               }else{
	            	   //如果这个有值，遍历这个数组，并记录在map中，在getview的时候判断显示
	                   for(int s=0;s<checkedChildren.size();s++){
	                	   if(childrenItems.contains(checkedChildren.get(s))){
	                		   childCheckedStateMap.put(childitem.getId(), true);  	
	                	    }else{
	                	       childCheckedStateMap.put(childitem.getId(), false);  	
	                	    }
	                   }	    	   
	               }
		          }				     			     
				//显示之前选中的状态
	 				if(checkedChildren!=null&&checkedChildren.size()!=0){		
	 			       StringBuffer buffer=new StringBuffer();
	 				   for(int index=0;index<checkedChildren.size();index++){		               
	 			                  buffer.append(checkedChildren.get(index));}	
	 				    	 String uids= buffer.toString();	
	 				    	 String[] uid=uids.split(",");
	 			        for(int n=0;n<uid.length;n++){				       
	 				         childCheckedStateMap.put(uid[n], true);   				    				         
	 				       int  checkedCount = 0;
	 	    			   for (SendMsgChildItem childrenItem : childrenItems) {
	 	    					if (checkedChildren.contains(childrenItem.getId()+",")) {
	 	    						checkedCount ++;
	 	    					  }
	 	    				  }   							
	 	    			if (checkedCount==childrenItems.size()&&childrenItems.size()!=0) {   			
	 	    					groupCheckedStateMap.put(groupItem.getId()+",", 1);
	 	    			}else{   			
	 	    					groupCheckedStateMap.put(groupItem.getId()+",",2);
	 	    			}
	 				    	  }			    	 				        		
	 		 		 
	 				}			     			     
 				//显示之前选中的状态
 				if(teachershowlist!=null&&teachershowlist.size()!=0&&checkedChildren==null){		
 			       StringBuffer buffer=new StringBuffer();
 				   for(int index=0;index<teachershowlist.size();index++){		               
 			                  buffer.append(teachershowlist.get(index));}	
 				    	 String uids= buffer.toString();	
 				    	 String[] uid=uids.split(",");
 			        for(int n=0;n<uid.length;n++){				       
 				         childCheckedStateMap.put(uid[n], true);   				    				         
 				       int  checkedCount = 0;
 	    			   for (SendMsgChildItem childrenItem : childrenItems) {
 	    					if (teachershowlist.contains(childrenItem.getId()+",")) {
 	    						checkedCount ++;
 	    					  }
 	    				  }   							
 	    			if (checkedCount==childrenItems.size()&&childrenItems.size()!=0) {   			
 	    					groupCheckedStateMap.put(groupItem.getId()+",", 1);
 	    			}else{   			
 	    					groupCheckedStateMap.put(groupItem.getId()+",",2);
 	    			}
 				    	  }			    	 				        		
 		 		 
 				}
 				
 				
 			} catch (Exception e) {
 				
 			}
 		}
         		
     }
		 @Override
		 public Object getChild(int groupPosition, int childPosition) {
			
			final SendMsgGroupItem groupItem = groupList.get(groupPosition);
			if (groupItem==null || groupItem.getChilditem()==null
					|| groupItem.getChilditem().isEmpty()) {
				return null;
			}
			return groupItem.getChilditem().get(childPosition);
		}

		@Override
		public long getChildId(int arg0, int childPosition) {
			
			return childPosition;
		}


		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
				ViewGroup arg4) {
			final ChildHolder holder;
			SendMsgChildItem childrenItem = (SendMsgChildItem)getChild(groupPosition, childPosition);
			if(convertView==null){
				holder=new ChildHolder();
				convertView=mInflater.inflate(R.layout.inflater_service_schedule_select_friend, null);
				holder.childTv=(TextView) convertView.findViewById(R.id.id_inflater_service_showname_tv);
				holder.childCb=(ImageView) convertView.findViewById(R.id.id_inflater_service_showchecked_iv);
			    holder.headiv=(ImageView)convertView.findViewById(R.id.id_inflater_service_showheadiv_cv);
				holder.click=(RelativeLayout) convertView.findViewById(R.id.id_click_inflater);
				holder.shown=(RelativeLayout) convertView.findViewById(R.id.id_service_select_people_shown);
				convertView.setTag(holder);
			}else{
				holder=(ChildHolder) convertView.getTag();
			}

			final String childrenId = childrenItem.getId();		
			holder.childTv.setText(childrenItem.getTextName());
		    ShowLocalImage(childrenItem.getImg(), holder.headiv);
		
			 holder.click.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View view) {
					if (!checkedChildren.contains(childrenId+",")) {
						 checkedChildren.add(childrenId+",");	
					     childCheckedStateMap.put(childrenId, true);
						 
					}else{
						 checkedChildren.remove(childrenId+",");
					     childCheckedStateMap.put(childrenId, false);					
					}
					setGroupItemCheckedState(groupList.get(groupPosition));				
					SendMsgExpandAdapter.this.notifyDataSetChanged();
				}
			});
			
			boolean state=childCheckedStateMap.get(childrenId);
		    if(state==true){
		    	holder.childCb.setImageResource(R.drawable.friendster_check_true);
		    }else{
		    	holder.childCb.setImageResource(R.drawable.friendster_check_false);
		    }
			
			return convertView;
		}
		public void ShowLocalImage(String path,ImageView imageView)
		{	
			ImageLoader.getInstance().displayImage(path, imageView, ImageLoadOptions.getHeadOptions());
		         
		}
		@Override
		public int getChildrenCount(int groupPosition) {
			
			final SendMsgGroupItem groupItem = groupList.get(groupPosition);
			if (groupItem== null || groupItem.getChilditem()==null
					|| groupItem.getChilditem().isEmpty()) {
				return 0;
			}
			return groupItem.getChilditem().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
		
			if (groupList==null) {
				return null;
			}
			return groupList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			
			if (groupList==null) {
				return 0;
			}
			return groupList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
		
			return groupPosition;
		}
      

		@Override
		public View getGroupView(final int groupPosition, boolean isExpand, View convertView,
				ViewGroup arg3) {
			final GroupHolder holder;
			SendMsgGroupItem groupItem = groupList.get(groupPosition);
			if(convertView==null){
				holder=new GroupHolder();
				convertView=mInflater.inflate(R.layout.inflater_sendnotice_group, null);
				holder.groupTv=(TextView) convertView.findViewById(R.id.id_sendmsg_inflater_group_nametv);
				holder.groupCb=(ImageView) convertView.findViewById(R.id.id_sendmsg_inflater_group_nameck);		
			    holder.groupRl=(RelativeLayout) convertView.findViewById(R.id.id_sendmsg_inflater_rl);
				convertView.setTag(holder);
			}else{
				holder=(GroupHolder) convertView.getTag();
			}
			    holder.groupCb.setOnClickListener(new GroupCBLayoutOnClickListener(groupItem));
		     	int state = groupCheckedStateMap.get(groupItem.getId()+",");
			    holder.groupTv.setText(groupList.get(groupPosition).getText());  
			
				switch (state) {
				case 1:
					holder.groupCb.setImageResource(R.drawable.friendster_check_true);
					break;			
				case 2:
					holder.groupCb.setImageResource(R.drawable.friendster_check_false);
					break;
				case 3:
					holder.groupCb.setImageResource(R.drawable.friendster_check_false);
					break;
				default:
					break;
				}
				groupCheckedStateMap.put(groupItem.getId()+",", state);
				SendMsgExpandAdapter.this.notifyDataSetChanged();
			return convertView;
		}
		
		private void setGroupItemCheckedState(SendMsgGroupItem groupItem){
			List<SendMsgChildItem> childrenItems = groupItem.getChilditem();
			if (childrenItems==null || childrenItems.isEmpty()) {
				groupCheckedStateMap.put(groupItem.getId()+",", 2);
				return;
			}
			
			int  checkedCount = 0;
			for (SendMsgChildItem childrenItem : childrenItems) {
				if (checkedChildren.contains(childrenItem.getId()+",")) {
					checkedCount ++;
				}
			}
			int state = 1;
			if (checkedCount==0) {
				state = 2;
			}else if (checkedCount==childrenItems.size()) {
				state = 1;
			}else{
				state=3;
			}
			
			groupCheckedStateMap.put(groupItem.getId()+",", state);
		}

		@Override
		public boolean hasStableIds() {
		
			return true;
		}

		public List<String> getCheckedChildren() {
			return checkedChildren;
		}
		public void setCheckedChildren(List<String> list) {
			 this.checkedChildren=list;
			 notifyDataSetChanged();
		}
		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
		
			return true;
		}
		 
		/**
		 * 给父控件设置点击
		 * @author zy
		 *
		 */
		public class GroupCBLayoutOnClickListener implements OnClickListener{	

			private SendMsgGroupItem groupItem;
			
			public GroupCBLayoutOnClickListener(SendMsgGroupItem groupItem){
				this.groupItem = groupItem;
			}
			
			@Override
			public void onClick(View v) {
				List<SendMsgChildItem> childrenItems = groupItem.getChilditem()==null?new ArrayList<SendMsgChildItem>():groupItem.getChilditem();
				if (childrenItems==null || childrenItems.isEmpty()) {
					groupCheckedStateMap.put(groupItem.getId()+",", 2);			
				}
				//遍历选中的list，对每一个item进行比较
				int  checkedCount = 0;
				for (SendMsgChildItem childrenItem : childrenItems) {
					if (checkedChildren.contains(childrenItem.getId()+",")) {
						checkedCount ++;
					}
				}
				//得到点击状态
				boolean checked = false;
				if (checkedCount==childrenItems.size()) {
					checked = false;
					groupCheckedStateMap.put(groupItem.getId()+",", 2);
				}else{
					checked = true;
					groupCheckedStateMap.put(groupItem.getId()+",", 1);
				}
				
				for (SendMsgChildItem childrenItem : childrenItems) {
					String holderKey = childrenItem.getId()+",";
					if (checked) {
						if (!checkedChildren.contains(holderKey)) {
							checkedChildren.add(holderKey);
							childCheckedStateMap.put(childrenItem.getId(), true);
						}
					}else {
						    checkedChildren.remove(holderKey);
						    childCheckedStateMap.put(childrenItem.getId(), false);
					}
				}
				
				SendMsgExpandAdapter.this.notifyDataSetChanged();
			}
		}
//		private void GroupCheckState(){
//			
//		}
		 class GroupHolder {
			 private TextView groupTv;
			 private ImageView groupCb;
			 private  RelativeLayout groupRl;
		 }
		 class ChildHolder{
			 private TextView childTv;
			 private TextView childPhoneTv;
			 private ImageView childCb;
			 private ImageView headiv;
			 private RelativeLayout click;
			 private RelativeLayout  shown;
		 }
		 ShowChild  showListener;
		 public interface ShowChild{
			 public void isShow(View showview);
		 }
		public ShowChild getShowListener() {
			return showListener;
		}
		public void setShowListener(ShowChild showListener) {
			this.showListener = showListener;
		}
		 
	 }