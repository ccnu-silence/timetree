package com.yey.kindergaten.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.MeinfoItemBean;
import com.yey.kindergaten.db.DBManager;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.List;

public class MeInfoAdapter extends BaseAdapter{
    Context context;
    private int  clickposition;
    List<MeinfoItemBean> list;
    private DBManager dbm;
    private SQLiteDatabase sqlite;
    private AccountInfo accountInfo = AppServer.getInstance().getAccountInfo();
    public MeInfoAdapter(Context context,List<MeinfoItemBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public int getPosition(){
        return clickposition;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//  @Override
//  public int getViewTypeCount() {
//      return 2;
//  }
//
//  @Override
//  public int getItemViewType(int position) {
//      return list.get(position).getType()==1?1:0;
//  }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//      int type = getItemViewType(position);
//      if (type == 1) {
        clickposition = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_meinfoitem, null);
        }
        RelativeLayout uprl = ViewHolder.get(convertView, R.id.meinfo_uprl) ;  // 带头像
        RelativeLayout downrl = ViewHolder.get(convertView, R.id.meinfo_downrl); // 主体item
        RelativeLayout spertor = ViewHolder.get(convertView, R.id.spetor_line); // 间隔布局
        RelativeLayout baby_info = ViewHolder.get(convertView, R.id.baby_info_ll); // 宝贝基本信息

        TextView uptitle = ViewHolder.get(convertView, R.id.meinfo_uptitletv);
        CircleImageView image = ViewHolder.get(convertView, R.id.meinfo_iv);
        TextView downtitle = ViewHolder.get(convertView, R.id.meinfo_downtitletv);
        TextView value = ViewHolder.get(convertView, R.id.meinfo_valuetv);
        ImageView arrowiv = ViewHolder.get(convertView, R.id.arrowimage);
        //          View view = convertView.findViewById(R.id.id_item_view_line);
        MeinfoItemBean itemBean = list.get(position);

        // 设置间隔布局
        if (itemBean.getType() == 3) {
            if (accountInfo!=null && accountInfo.getRole() == AppConstants.PARENTROLE && position == 3) {
                spertor.setVisibility(View.GONE);
                baby_info.setVisibility(View.GONE);
            } else {
                spertor.setVisibility(View.VISIBLE);
                baby_info.setVisibility(View.GONE);
            }
        } else if (itemBean.getType() == 2) {
            spertor.setVisibility(View.GONE);
            baby_info.setVisibility(View.VISIBLE);
        } else {
            spertor.setVisibility(View.GONE);
            baby_info.setVisibility(View.GONE);
        }
        // 设置右箭头
        if (itemBean.getTitle().equals("账号") || itemBean.getTitle().equals("账号类型") || itemBean.getTitle().equals("职务")){
            arrowiv.setVisibility(View.GONE);
        } else {
            arrowiv.setVisibility(View.VISIBLE);
        }

        if (itemBean.getTitle().equals("头像")) {
            uprl.setVisibility(View.VISIBLE);
            downrl.setVisibility(View.GONE);
            arrowiv.setVisibility(View.VISIBLE);
            if (itemBean.getImageurl()!=null && itemBean.getImageurl().contains("http")) {
                ImageLoader.getInstance().displayImage(itemBean.getImageurl(), image, ImageLoadOptions.getContactsFriendPicOptions());
            } else {
                ImageLoader.getInstance().displayImage("file:///" + itemBean.getImageurl(), image, ImageLoadOptions.getContactsFriendPicOptions());
            }
            uptitle.setText(itemBean.getTitle());
        } else if (itemBean.getTitle().equals("性别")) {
            uprl.setVisibility(View.GONE);
            downrl.setVisibility(View.VISIBLE);
            downtitle.setText(itemBean.getTitle());
            if (itemBean.getValue().equals("3")) {
                value.setText("男");
            } else if(itemBean.getValue().equals("2")) {
                value.setText("女");
            } else {
                value.setText("未填写");
            }
        } else if(itemBean.getTitle().equals("所在地区")) {
            uprl.setVisibility(View.GONE);
            downrl.setVisibility(View.VISIBLE);
            downtitle.setText(itemBean.getTitle());
            String address = getlocationByid(itemBean.getValue());
            if (address.length() > 11) {
                value.setText(address.substring(0, 11) + "...");
            } else {
                value.setText(address);
            }
        } else if (itemBean.getTitle().equals("宝贝基本信息")) {
            uprl.setVisibility(View.GONE);
            downrl.setVisibility(View.GONE);
        } else {
            uprl.setVisibility(View.GONE);
            if (itemBean.getType() == 1) {
                downrl.setVisibility(View.VISIBLE);
            } else {
                downrl.setVisibility(View.GONE);
            }
            downtitle.setText(itemBean.getTitle());
            value.setText(itemBean.getValue());
        }
        return convertView;
        //		}else{
        //			if(convertView==null){
        //				convertView=LayoutInflater.from(context).inflate(R.layout.activity_service_cut_show, null);
        //				 View view=ViewHolder.get(convertView, R.id.cutitem);
        //				    View topview=ViewHolder.get(convertView, R.id.cuttopitem);
        //				    if(position==0){
        //				    	topview.setVisibility(View.GONE);
        //				    }
        //				    if(position+1==this.getCount()){
        //				    	view.setVisibility(View.GONE);
        //				    }
        //			}
        //			return convertView;
        //		}
    }

    public String getlocationByid(String  id) {
        if (id.equals("0") || id.equals("-1")) {
            return "";
        }
        dbm = new DBManager(context);
        dbm.openDatabase();
        sqlite = dbm.getDatabase();
        String sql = "select * from district where locationid='"+id+"'";
        Cursor cursor = sqlite.rawQuery(sql,null);
        cursor.moveToFirst();
        AddressBean addressBean = new AddressBean();
        List<AddressBean> list = DbHelper.getAList(addressBean, cursor);
        if (list!=null && list.size()!=0) {
             String address = "";
             if (list.get(0).getProvince()!=null && !list.get(0).getProvince().equals("")){
                 address = address + list.get(0).getProvince();
             }
             if (list.get(0).getCity()!=null && !list.get(0).getCity().equals("")){
                 address = address + "," + list.get(0).getCity();
             }
             if (list.get(0).getLocation()!=null && !list.get(0).getLocation().equals("")){
                 address=address + "," + list.get(0).getLocation();
             }
             return address;
        }
        return "";
    }

}
