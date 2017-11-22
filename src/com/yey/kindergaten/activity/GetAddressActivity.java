package com.yey.kindergaten.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.ServiceAdapter;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.db.DBManager;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class GetAddressActivity extends BaseActivity implements OnItemClickListener,OnClickListener{

	@ViewInject(R.id.left_btn)ImageView left_btn;
	@ViewInject(R.id.header_title)TextView titletext;
	@ViewInject(R.id.getaddress_listview)ListView listView;
	List<AddressBean> list = new ArrayList<AddressBean>();
	int   clicknum=0;
	ServiceAdapter adapter;
	private DBManager dbm;
	private SQLiteDatabase db;
	int clickposition;
	String locationID;
	String locationText;
	String privence;
	String city;
	String district;
    //联网获取地址时参数
    String type = null;
    private int superior=0;
    private List<AddressBean> addressBeans = new ArrayList<AddressBean>();
    private int ID=0;

    private AddressBean priAddressBeans;//省级的对象
    private AddressBean cityAddressBeans;//市级的对象
    private AddressBean areaAddressBeans;//区级的对象
    private AddressBean roadAddressBeans;//街道级的对象
    private AccountInfo accountInfo = AppServer.getInstance().getAccountInfo();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getaddressactivity);
		ViewUtils.inject(this);
		if(getIntent().getExtras()!=null)
		{
			clickposition=getIntent().getExtras().getInt("clickposition");
            type = getIntent().getExtras().getString("type");
		}
		initView();
        if(type==null){
		    initDate(0);
        }else{
            getAreaFromInternet();
        }
	}

    private void getAreaFromInternet() {
        AppServer.getInstance().getArea(accountInfo.getUid()+"",ID,new OnAppRequestListener() {
            @Override
            public void onAppRequest(int code, String message, Object obj) {
                   if(code==AppServer.REQUEST_SUCCESS){
                       addressBeans.clear();
                       addressBeans= (List<AddressBean>) obj;
                       if(addressBeans==null||addressBeans.size()==0){
                           Intent intent = new Intent();
                           Bundle bundle = new Bundle();
                           bundle.putSerializable("priAddressBeans",priAddressBeans);
                           bundle.putSerializable("cityAddressBeans",cityAddressBeans);
                           bundle.putSerializable("areaAddressBeans",areaAddressBeans);
                           bundle.putSerializable("roadAddressBeans",roadAddressBeans);
                           intent.putExtra("clickposition", clickposition);
                           intent.putExtras(bundle);
                           setResult(3, intent);
                           GetAddressActivity.this.finish();
                       }
                       adapter= new ServiceAdapter(GetAddressActivity.this,addressBeans,AppConstants.GETNETADRESS);
                       listView.setAdapter(adapter);
                       listView.setOnItemClickListener(GetAddressActivity.this);
                   }else{

                   }
            }
        });

    }

    public void initView()
	{
		left_btn.setVisibility(View.VISIBLE);
		left_btn.setOnClickListener(this);
		titletext.setVisibility(View.VISIBLE);
		titletext.setText("选择地址");
	}
	
	 public void initDate(int parentid){
			dbm = new DBManager(this);
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
		 	adapter= new ServiceAdapter(this,list,AppConstants.APP_ADDRESS);
		 	listView.setAdapter(adapter);	
		 	listView.setOnItemClickListener(this);
		}
	 
	 
	 public Boolean containNext(int parentid){
		    List<AddressBean> conlist=new ArrayList<AddressBean>();
		    dbm = new DBManager(this);
		 	dbm.openDatabase();
		 	db = dbm.getDatabase();
		 	try {    
			        String sql = "select * from district where parentid='"+parentid+"'";  
			        Cursor cursor = db.rawQuery(sql,null);  
			        cursor.moveToFirst();
			        AddressBean addressBean=new AddressBean();
			        conlist=DbHelper.getAList(addressBean, cursor);
			 } catch (Exception e) {  			    	
			    }
		 	dbm.closeDatabase();
		 	db.close();	
		 	if(conlist==null||conlist.size()<1){
		 		return false;
		 	}else{
		 		return true;
		 	}
		 	
	 }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
        if(type==null) {
            switch (clicknum) {
                case 0:
                    privence = list.get(position).getLocation();
                    break;
                case 1:
                    city = list.get(position).getLocation();
                    break;
                case 2:
                    district = list.get(position).getLocation();
                    break;

                default:
                    break;
            }
            if (clicknum == 2) {
                Intent intent = new Intent();
                intent.putExtra("locationText", privence + "," + city + "," + district);
                intent.putExtra("locationID", list.get(position).getLocationid());
                intent.putExtra("clickposition", clickposition);
                setResult(3, intent);
                this.finish();
            } else if (clicknum == 1) {
                if (!containNext(list.get(position).getID())) {
                    Intent intent = new Intent();
                    intent.putExtra("locationText", privence + "," + city + "," + district);
                    intent.putExtra("locationID", list.get(position).getLocationid());
                    intent.putExtra("clickposition", clickposition);
                    setResult(3, intent);
                    this.finish();
                } else {
                    clicknum++;
                    initDate(list.get(position).getID());
                }
            } else {
                if (!containNext(list.get(position).getID())) {
                    Intent intent = new Intent();
                    intent.putExtra("locationText", privence + "," + city + "," + district);
                    intent.putExtra("locationID", list.get(position).getLocationid());
                    intent.putExtra("clickposition", clickposition);
                    setResult(3, intent);
                    this.finish();
                } else {
                    clicknum++;
                    initDate(list.get(position).getID());
                }
            }
        }else{
            switch (superior){
                case 0:
                    ID = addressBeans.get(position).getID();
                    priAddressBeans = addressBeans.get(position);
                    break;
                case 1:
                    ID = addressBeans.get(position).getID();
                    cityAddressBeans = addressBeans.get(position);
                    break;
                case 2:
                    ID = addressBeans.get(position).getID();
                    areaAddressBeans = addressBeans.get(position);
                    break;
                case 3:
                    ID = addressBeans.get(position).getID();
                    roadAddressBeans = addressBeans.get(position);
                    break;
            }
            superior++;
            if(superior>3){
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("priAddressBeans",priAddressBeans);
                bundle.putSerializable("cityAddressBeans",cityAddressBeans);
                bundle.putSerializable("areaAddressBeans",areaAddressBeans);
                bundle.putSerializable("roadAddressBeans",roadAddressBeans);
                intent.putExtra("clickposition", clickposition);
                intent.putExtras(bundle);
                setResult(3, intent);
                this.finish();
            }else{
              getAreaFromInternet();
            }
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			Intent intent=new Intent();
	    	setResult(3, intent);
	    	this.finish();
			break;

		default:
			break;
		}

		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent();
	    	setResult(3, intent);
	    	this.finish();
	    }
	  	return  false;
	}
	
	public String getLocationText() {
		return locationText;
	}

	public String getLocationID() {
		return locationID;
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
