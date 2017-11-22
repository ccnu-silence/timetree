package com.yey.kindergaten.widget;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.db.DBManager;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.util.AppConstants;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;


public class AddressPickWight extends LinearLayout implements OnItemClickListener{
	LinearLayout layout;
	ListView listView;
	private DBManager dbm;
	private SQLiteDatabase db;
	List<AddressBean> list = new ArrayList<AddressBean>();
	ServiceAdapter adapter;
	Context context;
	String locationID;
	String locationText;
	String privence;
	String city;
	String district;
	public AddressPickWight(Context context, AttributeSet attrs) {	
		super(context, attrs);  
		this.context=context;
		initview();
	}

	public AddressPickWight(Context context) {
		super(context);		
		this.context=context;
		initview();
	}
	public void initview()
	{
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, 1.0f);
        listView=new ListView(context);
        listView.setLayoutParams(params);
        initDate(0);
		this.addView(listView);
	}
	 public void initDate(int parentid){
			dbm = new DBManager(context);
		 	dbm.openDatabase();
		 	db = dbm.getDatabase();
		 	if(parentid==0){
		 		try {    
			        String sql = "select * from district where level='"+0+"'";  	 
			        Cursor cursor = db.rawQuery(sql,null);  
			        cursor.moveToFirst();
			        AddressBean addressBean=new AddressBean();
			        list=DbHelper.getAList(addressBean, cursor); 	       
			     } catch (Exception e) {  
			    } 
		 	}else{
		 		try {    
			        String sql = "select * from district where parentid='"+parentid+"'";  
			        Cursor cursor = db.rawQuery(sql,null);  
			        cursor.moveToFirst();
			        AddressBean addressBean=new AddressBean();
			        list=DbHelper.getAList(addressBean, cursor);
			    } catch (Exception e) {  			    	
			    }
		 	}
		 	dbm.closeDatabase();
		 	db.close();		 	
		 	adapter= new ServiceAdapter(context,list,AppConstants.APP_ADDRESS);
		 	listView.setAdapter(adapter);	
		 	listView.setOnItemClickListener(this);
		}
	    

		public String getLocationID() {
			return locationID;
		}
		public String getLocationText() {
			return privence+","+city+","+district;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {		
			initDate(list.get(position).getID());
		}
}

