package com.yey.kindergaten.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ContactFriendDatacardActivity;
import com.yey.kindergaten.activity.ContactsAddFriendActivity;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.Items;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.widget.MyListViewWithScrollView;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends FragmentBase implements OnItemClickListener ,OnClickListener{

    MyListViewWithScrollView listview;
	ServiceAdapter contactPuacAdapetr;
	List<Items> puaclist=new ArrayList<Items>();
	List<Items> datalist=new ArrayList<Items>();
	AppContext appcontext = null;
	AccountInfo accountInfo;
	Contacts contacts;
	List<Items> friendlist=new ArrayList<Items>();
	LinearLayout  addly;
	LinearLayout  nodataly;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 appcontext = AppContext.getInstance();
    	 accountInfo=AppServer.getInstance().getAccountInfo();
    	 contacts=appcontext.getContacts();		 
	   	 if(contacts.getFriends()!=null&&contacts.getFriends().size()>0){
	   		friendlist.clear();
	   	    friendlist=AppUtils.GetListItem(contacts.getFriends()); 
	        datalist.clear();
	   	    datalist.addAll(friendlist);
	   	 }
	     contactPuacAdapetr=new ServiceAdapter(getActivity(), datalist, AppConstants.CONTACTS_FRIENDMAIN);
			listview.setAdapter(contactPuacAdapetr);
			listview.setOnItemClickListener(this);
			if(datalist.size()<1){
				nodataly.setVisibility(View.VISIBLE);
			}else{
				nodataly.setVisibility(View.GONE);
			}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view=inflater.inflate(R.layout.puacfragmently, null);
		nodataly=(LinearLayout) view.findViewById(R.id.fragmnetly_notitaddly);
//		addtv=(TextView) view.findViewById(R.id.fragmnetly_notitle);
//		addtv.setText("你还没好友,加好友去吧");
		addly=(LinearLayout) view.findViewById(R.id.fragmnetly_notitaddly);
		addly.setOnClickListener(this);
		listview=(MyListViewWithScrollView) view.findViewById(R.id.activity_contacts_main_puaclistview);
		return view;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {		
		  Items item=datalist.get(position);	
		  Intent  intent=new Intent(getActivity(),ContactFriendDatacardActivity.class);
		  Bundle bundle=new Bundle();
		  bundle.putString("state", AppConstants.CONTACTS_FRIEND);	 		
		  bundle.putInt("role", 2);
		  bundle.putInt("targetid", item.getId()); 
		  intent.putExtras(bundle);
		  startActivity(intent);
	}
	
	
	public void refreshFrament()
	{
		appcontext = AppContext.getInstance();
		contacts=appcontext.getContacts();
		if(contacts!=null){
			 if(contacts.getFriends()!=null&&contacts.getFriends().size()>0){
			   	    friendlist=AppUtils.GetListItem(contacts.getFriends()); 
			     	datalist.clear();
			   	    datalist.addAll(friendlist);
			   	 }else{
			   		 datalist.clear();
			   }
		}		
		 if(datalist.size()<1){
			   nodataly.setVisibility(View.VISIBLE);
			}else{
				nodataly.setVisibility(View.GONE);
			}
		 contactPuacAdapetr.notifyDataSetChanged();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragmnetly_notitaddly:
			Intent intent=new Intent(getActivity(),ContactsAddFriendActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}
}

