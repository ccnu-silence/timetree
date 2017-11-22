package com.yey.kindergaten.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.FriendsterActivityItemAdapter.Holder;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.GroupList;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.widget.CircleImageView;

public class GroupListAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<GroupInfoBean>  grouplist =new ArrayList<GroupInfoBean>();
	
	private TreeSet mSeparatorsSet = new TreeSet();	
	LayoutInflater mInflater=null;
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
	public GroupListAdapter(Context context, ArrayList<GroupInfoBean> grouplist) {
		this.context=context;
		this.grouplist=grouplist;
	}
	
	
	class ViewHolder {
		private TextView tv_grouplist;
		private ImageView groupHead;
		private TextView tv_groupdecs;
		private View view;
		private View longview;
	}
	
	public GroupListAdapter(Context context) {
		this.context=context;
		mInflater=((Activity)context).getLayoutInflater();
	}

	
	public void addData(List<GroupInfoBean> list){
		grouplist.addAll(list);
        notifyDataSetChanged();
	}
 
	public void addSeparatorItem(GroupInfoBean item) {
	   grouplist.add(item);
       mSeparatorsSet.add(grouplist.size() - 1);
       notifyDataSetChanged();
   }



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return grouplist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return grouplist.get(position);
	}

	@Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }
    
    
    public List<GroupInfoBean> getData(){
    	return grouplist;
    }
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }
	@Override
	public long getItemId(int position) {

		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(getItemViewType(position)==TYPE_SEPARATOR){
			return false;
		}
		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		ViewHolder holder=null;
		int type = getItemViewType(position);
		if (convertView==null) {
			holder=new ViewHolder();
		  if(type==TYPE_ITEM){
			convertView=mInflater.inflate(R.layout.activity_getgroup_item, null);
			holder.tv_grouplist=(TextView) convertView.findViewById(R.id.grouplist_name);
			holder.groupHead = (ImageView) convertView.findViewById(R.id.grouplist_head);	   		 	
			holder.tv_groupdecs=(TextView)convertView.findViewById(R.id.decs_group);
			holder.view=convertView.findViewById(R.id.item_view);
			holder.longview=convertView.findViewById(R.id.item_view_long);
		   if(position==1){
			   holder.view.setVisibility(View.GONE);
		   }
		  }
		  else{
			   convertView=mInflater.inflate(R.layout.activity_service_cut_show, null);	
			   TextView title=(TextView) convertView.findViewById(R.id.show_title_separator_tv);  
		       title.setVisibility(View.VISIBLE);
		  }
		       convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}		
		if(type==TYPE_ITEM){
		  
			if(position!=1){
				holder.view.setVisibility(View.VISIBLE);
			}else{
				holder.view.setVisibility(View.GONE);
			}
            if(getCount()==1){
            	holder.view.setVisibility(View.GONE);
            	holder.longview.setVisibility(View.VISIBLE);
            	
            }
            if(getCount()==2){
            if(position==1){
            	holder.view.setVisibility(View.GONE);
            	holder.longview.setVisibility(View.VISIBLE);
            }
            }else{
            	if(position==grouplist.size()-1){
            		if(convertView!=null){
            		holder.view.setVisibility(View.GONE);
                	holder.longview.setVisibility(View.VISIBLE);
                	}
            	}
            }
			    GroupInfoBean group = grouplist.get(position);
			    holder.tv_grouplist.setText(group.getGname());
			    if(group.getDesc()!=null){
			    	  holder.tv_groupdecs.setText(group.getDesc());
			    }else{
			    	 holder.tv_groupdecs.setText("暂无群介绍");
			    }		  
			    if(group.getGtype() == 0){//好友群
			    	holder.groupHead.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_group_friends));
		    	}else if(group.getGtype() == 1){//幼儿园群
		    		holder.groupHead.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_group_km));
			    }else if(group.getGtype() == 2){//班级群
			    	holder.groupHead.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_group_class));
			    }
		}
		return convertView;				
		}
}
