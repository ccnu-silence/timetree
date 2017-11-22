package com.yey.kindergaten.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.AddressBean;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.bean.Children;
import com.yey.kindergaten.bean.Dir;
import com.yey.kindergaten.bean.GradeInfo;
import com.yey.kindergaten.bean.GroupMemberInfo;
import com.yey.kindergaten.bean.Items;
import com.yey.kindergaten.bean.Product;
import com.yey.kindergaten.bean.TaskBean;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.ImageManager;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.List;

public  class ServiceAdapter<E> extends BaseListAdapter{

    public DisplayImageOptions options;
    String adaptertype;
    Onclickback onclickback;
    List<E> list2;
    public AccountInfo accountInfo;
    private static final String TAG = "ServiceAdapter";
    public interface Onclickback {
        public void click(int  id,int position);
    };

    public ServiceAdapter(Context context, List<E> list,String adaptertype) {
        super(context, list);
        this.adaptertype=adaptertype;
        if (adaptertype.equals(AppConstants.CONTACTS_PUACMAIN)) {
            options = ImageLoadOptions.getContactsPuacPicOptions();
            //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        } else {
            options = ImageLoadOptions.getContactsFriendPicOptions();
            //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
    }

    public ServiceAdapter(Context context, List<E> list,List<E> list2,String adaptertype) {
        super(context, list);
        this.adaptertype=adaptertype;
        this.list2 = list2;
        if (adaptertype.equals(AppConstants.CONTACTS_PUACMAIN)) {
            options = ImageLoadOptions.getContactsPuacPicOptions();
            //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        } else {
            options = ImageLoadOptions.getContactsFriendPicOptions();
            //imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

    }

    public ServiceAdapter(Context context, List<E> list, List<E> list2, AccountInfo accountInfo, String adaptertype) {
        super(context, list);
        this.adaptertype = adaptertype;
        this.list2 = list2;
        this.accountInfo = accountInfo;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        convertView = bindViewBytype(  position,  convertView,  parent, adaptertype);
        return convertView;
    }

    public View bindViewBytype(final int position, View convertView, ViewGroup parent,String type) {
        if (type.equals(AppConstants.CONTACTS_PUACMAIN)) {                   ////////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.puacitem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
            View view = ViewHolder.get(convertView, R.id.item_view);
            View longview = ViewHolder.get(convertView, R.id.item_longview);
            Button statebtn = ViewHolder.get(convertView, R.id.contatc_puacstatebt);
            Items items = (Items) list.get(position);
            if (items!=null) {
                if (items.getType().equals(AppConstants.CONTACTS_PUAC)) {
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(items.getNickname());
                    imageView.setVisibility(View.VISIBLE);
                    ShowNetImage(items.getAvatar(), imageView, null);
                    view.setVisibility(View.VISIBLE);
                    longview.setVisibility(View.GONE);
                    if (items.getLines()) {
                        longview.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    } else {
                        longview.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }
            Setonclick(statebtn, position);
            return convertView;
        } else if (type.equals(AppConstants.CONTACTS_FRIENDMAIN)) {           ////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
//              LinearLayout title_ly = ViewHolder.get(convertView, R.id.item_titlely);
//              TextView  titletv = ViewHolder.get(convertView, R.id.item_titletv);
            View view = ViewHolder.get(convertView, R.id.item_view);
            View longview = ViewHolder.get(convertView, R.id.item_longview);
            ImageView arrowiv = ViewHolder.get(convertView, R.id.arrowimage);
            Items items = (Items) list.get(position);
            if (items!=null) {
                if (items.getType().equals(AppConstants.CONTACTS_FRIEND)) {
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(items.getNickname());
                    imageView.setVisibility(View.VISIBLE);
                    ShowNetImage(items.getAvatar(), imageView,null);
                    view.setVisibility(View.VISIBLE);
                    arrowiv.setVisibility(View.GONE);
                    longview.setVisibility(View.GONE);
                    if (items.getLines()) {
                        view.setVisibility(View.VISIBLE);
                    }
//                  title_ly.setVisibility(View.GONE);
                }
            }
            return convertView;
        } else if (type.equals(AppConstants.CONTACTS_TEACHERMAIN)) {           ////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
//          LinearLayout title_ly = ViewHolder.get(convertView, R.id.item_titlely);
//          TextView titletv = ViewHolder.get(convertView, R.id.item_titletv);
//          title_ly.setVisibility(View.VISIBLE);
            View view = ViewHolder.get(convertView,R.id.item_view);
            View longview = ViewHolder.get(convertView, R.id.item_longview);
            ImageView arrowiv = ViewHolder.get(convertView, R.id.arrowimage);
            Items items = (Items) list.get(position);
            if (items!=null) {
                mian_ly.setVisibility(View.VISIBLE);
                nametextView.setText(items.getNickname());
                imageView.setVisibility(View.VISIBLE);
                ShowNetImage(items.getAvatar(), imageView,null);
                view.setVisibility(View.VISIBLE);
                arrowiv.setVisibility(View.GONE);
                longview.setVisibility(View.GONE);
                if (items.getLines()) {
                    longview.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                } else {
                    longview.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }
    //            title_ly.setVisibility(View.GONE);
            }
            return convertView;
        } else if (adaptertype.equals(AppConstants.CONTACTS_PARENTMAIN)) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
    //      LinearLayout title_ly = ViewHolder.get(convertView, R.id.item_titlely);
    //      TextView titletv = ViewHolder.get(convertView, R.id.item_titletv);
            View view = ViewHolder.get(convertView, R.id.item_view);
            View longview = ViewHolder.get(convertView, R.id.item_longview);
            ImageView arrowiv = ViewHolder.get(convertView, R.id.arrowimage);
            Items items = (Items) list.get(position);
            if (items!=null) {
                if (items.getType().equals(AppConstants.CONTACTS_TITLE)) {
                    mian_ly.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                    longview.setVisibility(View.GONE);
    //              title_ly.setVisibility(View.VISIBLE);
    //              titletv.setText(items.getNickname());
                } else if (items.getType().equals(AppConstants.CONTACTS_PARENT)) {
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(items.getNickname());
                    view.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    arrowiv.setVisibility(View.VISIBLE);
                    longview.setVisibility(View.VISIBLE);
    //              title_ly.setVisibility(View.GONE);
                } else if (items.getType().equals(AppConstants.CONTACTS_KINDERPARENT)) {
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(items.getNickname());
                    imageView.setVisibility(View.VISIBLE);
                    ShowNetImage(items.getAvatar(), imageView,null);
                    view.setVisibility(View.VISIBLE);
                    arrowiv.setVisibility(View.GONE);
                    longview.setVisibility(View.GONE);
                    if (items.getLines()) {             //长线
                        view.setVisibility(View.VISIBLE);
                    }
    //              title_ly.setVisibility(View.GONE);
                } else if (items.getType().equals(AppConstants.CONTACTS_KINDERTEACHER)) {
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(items.getNickname());
                    imageView.setVisibility(View.VISIBLE);
                    ShowNetImage(items.getAvatar(), imageView,null);
                    view.setVisibility(View.VISIBLE);
                    arrowiv.setVisibility(View.GONE);
                    longview.setVisibility(View.GONE);
                    if (items.getLines()) {
                        longview.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    } else {
                        longview.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                    }
    //              title_ly.setVisibility(View.GONE);
                } else {
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(items.getNickname());
                    imageView.setVisibility(View.VISIBLE);
                    ShowNetImage(items.getAvatar(), imageView,null);
                    view.setVisibility(View.VISIBLE);
                    arrowiv.setVisibility(View.GONE);
                    longview.setVisibility(View.GONE);
                    if (items.getLines()) {
                        longview.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    } else {
                        longview.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                    }
        //            title_ly.setVisibility(View.GONE);
                }
            }
            return convertView;
        } else if (type.equals(AppConstants.CONTACTS_CONTACTPARENTLIST)) {           ////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.parentlistitem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView,R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertView,R.id.contact_frienditemnametv);
            RelativeLayout birthday_rl = ViewHolder.get(convertView, R.id.birthday_rl);
            TextView birthday_tv = ViewHolder.get(convertView,R.id.birthday_tv);
            final Children children = (Children) list.get(position);
            ShowNetImage(children.getAvatar(), imageView, null);
            nametextView.setText(children.getRealname());
            SetBirthdayClick(birthday_rl,position);

            /*if (children.getBirthday()!=null && children.getBirthday().length() == 10) {
                if (TimeUtil.getBirthday(children.getBirthday())) {
                    // birthday_iv.setText(children.getBirthday());
                    birthday_rl.setVisibility(View.VISIBLE);
                } else {
                    birthday_rl.setVisibility(View.GONE);
                }
            } else {
                birthday_rl.setVisibility(View.GONE);
            }*/

            if (children.getBirthdaystatus() == 0) {
                birthday_rl.setVisibility(View.GONE);
            } else if(children.getBirthdaystatus() == 1){
                birthday_tv.setBackgroundResource(R.drawable.contact_birthday_icon);
                birthday_rl.setVisibility(View.VISIBLE);
            } else {
                birthday_tv.setBackgroundResource(R.drawable.contact_hassendbirthday_icon);
                birthday_rl.setVisibility(View.VISIBLE);
            }

         return convertView;
        } else if (type.equals(AppConstants.SERVICEGROUPMEMBER)) {           ////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.parentlistitem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView,R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertView,R.id.contact_frienditemnametv);
            View view = ViewHolder.get(convertView, R.id.item_view);
            GroupMemberInfo groupMemberInfo = (GroupMemberInfo) list.get(position);
            ShowNetImage(groupMemberInfo.getAvatar(), imageView, null);
            nametextView.setText(groupMemberInfo.getRealname() == null?groupMemberInfo.getNickname():groupMemberInfo.getRealname());
            return convertView;
        } else if (type.equals(AppConstants.SERIVCE_ADRESSBOOKSELECT)) {         ///////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.service_pointexchange_addressbookitem, null);
            }
            AddressBookBean addressBookBean=(AddressBookBean) list.get(position);
            TextView reciename = ViewHolder.get(convertView,R.id.service_addressbook_itemrecnametv);
            TextView recieaddress = ViewHolder.get(convertView,R.id.service_addressbook_itemrecaddresstv);
            TextView reciephone = ViewHolder.get(convertView,R.id.service_addressbook_itemrecphonetv);
            TextView recieyoubian = ViewHolder.get(convertView,R.id.service_addressbook_itemrecyoubiantv);
            if (addressBookBean!=null) {
                reciename.setText(addressBookBean.getReceiver());
                recieaddress.setText(addressBookBean.getAddress());
                reciephone.setText(addressBookBean.getPhone());
                recieyoubian.setText(addressBookBean.getCode());
            }
            TextView delbtn = ViewHolder.get(convertView,R.id.service_addressbook_itemrecdelbt);
            LinearLayout delly = ViewHolder.get(convertView, R.id.service_addressbook_itemrecdelly);
            delly.setVisibility(View.GONE);
            return convertView;
        } else if (type.equals(AppConstants.SERIVCE_TASKMAIN)) {         //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.service_taskmain_listviewitem, null);
            }
            TaskBean taskdate = (TaskBean) list.get(position);
            ImageView imageView = ViewHolder.get(convertView, R.id.service_taskmain_itemiv);
            TextView nametext = ViewHolder.get(convertView, R.id.service_taskmain_itemnametv);
            TextView pointtextview = ViewHolder.get(convertView, R.id.service_taskmain_itempointtv);
            Button getbtn = ViewHolder.get(convertView, R.id.service_taskmain_itemgetbtn);
            nametext.setText(taskdate.getName());
            pointtextview.setText(taskdate.getPoint() + "");
            // imageLoader.displayImage(taskdate.getImg(), imageView,options);
            Setonclick(getbtn, position);
            if (taskdate.getStatus() == 1) {
                getbtn.setText("已完成");
                getbtn.setBackgroundResource(R.drawable.toastbackgroundclick);
            } else {
                getbtn.setText("做任务");
                getbtn.setBackgroundResource(R.drawable.toastselector);
            }
        } else if (type.equals(AppConstants.SERIVCE_POINTEXCHANGEHIS)) {    ////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.service_pointexchange_hisitem, null);
            }
            TextView timetext = ViewHolder.get(convertView, R.id.service_pointexchange_hisitem_timetext);
            TextView nametext = ViewHolder.get(convertView, R.id.service_pointexchange_hisitem_nametext);
            TextView counttext = ViewHolder.get(convertView, R.id.service_pointexchange_hisitem_counttext);
            return convertView;
        } else if (type.equals(AppConstants.SERIVCE_POINTEXCHANGEMAIN)) {   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.service_pointexchangeitem, null);
            }
            Product product = (Product) list.get(position);
            ImageView imageView = ViewHolder.get(convertView, R.id.service_pointexchangemian_itemiv);
            TextView goodstext = ViewHolder.get(convertView, R.id.service_pointexchangemian_itemgoodstv);
            TextView pointtext = ViewHolder.get(convertView, R.id.service_pointexchangemian_itempointtv);
            Button exchangebtn = ViewHolder.get(convertView, R.id.service_pointexchangemian_itemexchangebtn);
            Setonclick(exchangebtn,  position);
            // ShowNetImage(product.getImg(), imageView, null);
            // goodstext.setText(product.getName());
            // pointtext.setText(product.getCost()+"");
            // explaintext.setText(product.getDesc());
            return convertView;
        } else if (type.equals(AppConstants.CONTACTS_ALBUM)) {   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_albumitem, null);
            }
            ImageView imageView = ViewHolder.get(convertView,R.id.albumitem_imageview);
            TextView nametextView = ViewHolder.get(convertView,R.id.albumitem_nametextview);
            TextView counttextView = ViewHolder.get(convertView,R.id.albumitem_counttv);
            ProgressBar progressBar = ViewHolder.get(convertView,R.id.albumitem_progress);
            progressBar.setVisibility(View.GONE);
            Dir imageDir = (Dir) list.get(position);
            //String path="file://"+albumBean.getAlbumPath();
            //String text="("+albumBean.getPhotoCount()+")";
            //String name=albumBean.getAlbumName();
            nametextView.setText(imageDir.name);
            counttextView.setText("(" + imageDir.length + ")");
            imageLoader.displayImage("file:///" + imageDir.imgPath, imageView, ImageManager.options);
            return convertView;
        } else if (type.equals(AppConstants.MEINFO)) {   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contacts_main_frienditem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView,R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertView,R.id.contact_frienditemnametv);
            TextView discrptextView = ViewHolder.get(convertView,R.id.contact_friendphonetv);
            Button addbtn = ViewHolder.get(convertView,R.id.contact_friendaddbt);
            CircleImageView rightiv = ViewHolder.get(convertView,R.id.arrowimage);
            ImageView arrowImage = ViewHolder.get(convertView,R.id.thearrowimage);
            View view = ViewHolder.get(convertView,R.id.item_view);
            String keylist = (String) list.get(position);
            String valuelist = (String) list2.get(position);
            if (keylist.equals("性别")) {
                if (valuelist.equals("3")) {
                    valuelist = "男";
                } else if (valuelist.equals("2")) {
                    valuelist = "女";
                }
            }
            if (keylist.equals("头像")) {
                imageView.setVisibility(View.GONE);
                addbtn.setVisibility(View.GONE);
                rightiv.setVisibility(View.VISIBLE);
                discrptextView.setVisibility(View.GONE);
                nametextView.setText(keylist);
                if (valuelist!=null) {
                  imageLoader.displayImage(valuelist, rightiv, options);
                }
            } else {
                imageView.setVisibility(View.GONE);
                addbtn.setVisibility(View.GONE);
                rightiv.setVisibility(View.GONE);
                discrptextView.setVisibility(View.VISIBLE);
                nametextView.setText(keylist);
                if(valuelist!=null){
                    if (valuelist.length() < 13) {
                        discrptextView.setText(valuelist);
                    } else {
                        discrptextView.setText(valuelist.substring(0, 13) + "...");
                    }
                }
            }
            if (position > 1) {
                arrowImage.setVisibility(View.VISIBLE);
            } else {
                arrowImage.setVisibility(View.GONE);
            }
            view.setVisibility(View.GONE);
            return convertView;
        } else if (type.equals(AppConstants.KINDGARTENINFO)){   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contacts_main_frienditem, null);
            }
            ImageView imageView = ViewHolder.get(convertView,R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertView,R.id.contact_frienditemnametv);
            TextView discrptextView = ViewHolder.get(convertView,R.id.contact_friendphonetv);
            Button addbtn = ViewHolder.get(convertView,R.id.contact_friendaddbt);
            ImageView ImageView = ViewHolder.get(convertView,R.id.arrowimage);
            ImageView arrowImage = ViewHolder.get(convertView,R.id.thearrowimage);
            View view = ViewHolder.get(convertView,R.id.item_view);
            imageView.setVisibility(View.GONE);
            addbtn.setVisibility(View.GONE);
            ImageView.setVisibility(View.GONE);
            String keylist = (String) list.get(position);
            String valuelist = (String) list2.get(position);
            nametextView.setText(keylist);
            discrptextView.setText(valuelist);
            view.setVisibility(View.GONE);
            if (position > 0) {
                arrowImage.setVisibility(View.VISIBLE);
            } else {
                arrowImage.setVisibility(View.GONE);
            }
            return convertView;
        } else if (type.equals(AppConstants.MEMAIN_UP)){   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_me_main1_item, null);
            }
            ImageView icon = ViewHolder.get(convertView, R.id.iv_activity_me_item);
            TextView me_name = ViewHolder.get(convertView, R.id.tv_activity_me_item);
            TextView second_tv = ViewHolder.get(convertView, R.id.tv_activity_me_item_second);
            ImageView two_code = ViewHolder.get(convertView, R.id.row_img_02);
            ImageView iv_arrow = ViewHolder.get(convertView,R.id.row_img_01);
            String text = (String) list.get(position);
            Integer biticon = (Integer) list2.get(position);
            icon.setImageDrawable(this.mContext.getResources().getDrawable(biticon));
            View view = ViewHolder.get(convertView,R.id.item_view);
            if (accountInfo == null) {
                accountInfo = AppServer.getInstance().getAccountInfo();
            }

            two_code.setVisibility(View.GONE);
            if (position == 0 ){
                me_name.setText(text);
//                two_code.setVisibility(View.GONE);
                if (accountInfo.getKname()!=null && !accountInfo.getKname().equals("")) {
                    second_tv.setText(accountInfo.getKname());
                } else {
                    second_tv.setText("未加入幼儿园");
                }

                if (accountInfo.getRole()!= AppConstants.DIRECTORROLE) {
                    iv_arrow.setVisibility(View.INVISIBLE);
                } else {
                    iv_arrow.setVisibility(View.VISIBLE);
                }

                second_tv.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                me_name.setText(text);
                // two_code.setVisibility(View.GONE);
                if (accountInfo.getRole() == AppConstants.PARENTROLE) {
                   iv_arrow.setVisibility(View.INVISIBLE);
                    // cname，待添加
                    second_tv.setText(accountInfo.getCname());
                    second_tv.setVisibility(View.VISIBLE);
//                    two_code.setVisibility(View.GONE);
                } else if (accountInfo.getRole() == 1) {
                    second_tv.setVisibility(View.GONE);
//                  two_code.setVisibility(View.GONE);
                } else {
                    second_tv.setVisibility(View.GONE);
//                  two_code.setVisibility(View.GONE);
                }
            } else {
//              two_code.setVisibility(View.GONE);
                me_name.setText(text);
                second_tv.setVisibility(View.GONE);
            }
            if (position == list.size() - 1) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            return convertView;
        } else if (type.equals(AppConstants.MEMAIN_MIDDLE)) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_me_main1_item, null);
            }
            ImageView icon = ViewHolder.get(convertView, R.id.iv_activity_me_item);
            TextView me_name = ViewHolder.get(convertView, R.id.tv_activity_me_item);
            TextView second_tv = ViewHolder.get(convertView, R.id.tv_activity_me_item_second);
            ImageView two_code = ViewHolder.get(convertView, R.id.row_img_02);
            ImageView iv_arrow = ViewHolder.get(convertView,R.id.row_img_01);
            String text = (String) list.get(position);
            Integer biticon = (Integer) list2.get(position);
            icon.setImageDrawable(this.mContext.getResources().getDrawable(biticon));
            View view = ViewHolder.get(convertView,R.id.item_view);
            if (position == list.size() - 1) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            two_code.setVisibility(View.GONE);
            me_name.setText(text);
            second_tv.setVisibility(View.GONE);
            iv_arrow.setVisibility(View.VISIBLE);
            return convertView;
        } else if(type.equals(AppConstants.MEMAIN_DOWN)) {
             if (convertView == null) {
                 convertView = mInflater.inflate(R.layout.activity_me_main1_item, null);
             }
             Log.i(TAG, "type == MEMAIN_DOWN");
             ImageView icon = ViewHolder.get(convertView, R.id.iv_activity_me_item);
             TextView me_name = ViewHolder.get(convertView, R.id.tv_activity_me_item);
             TextView second_tv = ViewHolder.get(convertView, R.id.tv_activity_me_item_second);
             ImageView two_code = ViewHolder.get(convertView, R.id.row_img_02);
             two_code.setVisibility(View.GONE);

             String text=(String) list.get(position);
             Integer biticon=(Integer) list2.get(position);
             icon.setImageDrawable(this.mContext.getResources().getDrawable(biticon));
             View view=ViewHolder.get(convertView, R.id.item_view);

             Log.i(TAG,"set 版本号");
             if (text.contains("|")) {
                 String[] str = text.split("\\|");
                 me_name.setText(str[0]);
                 second_tv.setText(str[1]);
                 if (str[1].equals("发现新版本")) {
                     second_tv.setTextColor(Color.RED);
                 }
                 second_tv.setVisibility(View.VISIBLE);
             } else {
                 me_name.setText(text);
                 second_tv.setVisibility(View.GONE);
             }
             if (position == list.size() - 1) {
                 view.setVisibility(View.GONE);
             } else {
                 view.setVisibility(View.VISIBLE);
             }
             return convertView;
        } else if (type.equals(AppConstants.APP_ADDRESS)) {   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.addressitem, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.addressitemtv);
            AddressBean addressBean = (AddressBean) list.get(position);
            textView.setText(addressBean.getLocation());
            return convertView;
        } else if (type.equals(AppConstants.SERVICEGRADELIST)) {   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.addressitem, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.addressitemtv);
            GradeInfo gradeInfo = (GradeInfo) list.get(position);
            textView.setText(gradeInfo.getGradename());
            return convertView;
        } else if(type.equals(AppConstants.ADDRESSBOOKMANAGEEDIT)) {   //////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.service_pointexchange_addressbookitem, null);
            }
            AddressBookBean addressBookBean = (AddressBookBean) list.get(position);
            TextView reciename = ViewHolder.get(convertView, R.id.service_addressbook_itemrecnametv);
            TextView recieaddress = ViewHolder.get(convertView, R.id.service_addressbook_itemrecaddresstv);
            TextView reciephone = ViewHolder.get(convertView, R.id.service_addressbook_itemrecphonetv);
            TextView recieyoubian = ViewHolder.get(convertView, R.id.service_addressbook_itemrecyoubiantv);
            if (addressBookBean!=null) {
                reciename.setText(addressBookBean.getReceiver());
                recieaddress.setText(addressBookBean.getAddress());
                reciephone.setText(addressBookBean.getPhone());
                recieyoubian.setText(addressBookBean.getCode());
            }
            TextView delbtn = ViewHolder.get(convertView, R.id.service_addressbook_itemrecdelbt);
            LinearLayout delly = ViewHolder.get(convertView, R.id.service_addressbook_itemrecdelly);
            delly.setVisibility(View.GONE);
            Setonclick(delbtn,  position);
            return convertView;
        } else if (type.equals(AppConstants.GETNETADRESS)){
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.addressitem, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.addressitemtv);
            AddressBean addressBean = (AddressBean) list.get(position);
            textView.setText(addressBean.getTitle());
            return convertView;
        } else {                                   /////////////管理地址本
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.service_pointexchange_addressbookitem, null);
            }
            AddressBookBean addressBookBean=(AddressBookBean) list.get(position);
            TextView reciename = ViewHolder.get(convertView, R.id.service_addressbook_itemrecnametv);
            TextView recieaddress = ViewHolder.get(convertView, R.id.service_addressbook_itemrecaddresstv);
            TextView reciephone = ViewHolder.get(convertView, R.id.service_addressbook_itemrecphonetv);
            TextView recieyoubian = ViewHolder.get(convertView, R.id.service_addressbook_itemrecyoubiantv);
            if (addressBookBean!=null) {
                reciename.setText(addressBookBean.getReceiver());
                recieaddress.setText(addressBookBean.getAddress());
                reciephone.setText(addressBookBean.getPhone());
                recieyoubian.setText(addressBookBean.getCode());
            }
            TextView delbtn = ViewHolder.get(convertView, R.id.service_addressbook_itemrecdelbt);
            LinearLayout delly = ViewHolder.get(convertView, R.id.service_addressbook_itemrecdelly);
            delly.setVisibility(View.GONE);
            return convertView;
        }
        return convertView;
    }

    public void Setonclick(final View view,final int theposition){
        if (onclickback!=null) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickback.click(view.getId(), theposition);
                }
            });
        }
    }

    public void ShowNetImage(String path,ImageView imageView,final ProgressBar progressBar) {
        imageLoader.displayImage(path, imageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (progressBar!=null) {
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onLoadingFailed(String imageUri, View view,
                    FailReason failReason) {
                if (progressBar!=null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (progressBar!=null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                if (progressBar!=null) {
                    progressBar.setProgress(Math.round(100.0f * current / total));
                }
            }
        });
    }

    public void ShowLocalImage(String path,ImageView imageView) {
        imageLoader.displayImage(path, imageView,ImageLoadOptions.getContactsPuacPicOptions());
    }

    public Onclickback getOnclickback() {
        return onclickback;
    }
    public void setOnclickback(Onclickback onclickback) {
        this.onclickback = onclickback;
    }
//    public void NotifyDataSetChanged()
//    {
//    	this.notifyDataSetChanged();
//    }

    /**
     * 设置生日图片点击事件
     * @param view
     * @param position
     */
    public void SetBirthdayClick(final View view, final int position){
        if (onclickback!= null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickback.click(view.getId(), position);
                }
            });
        }
    }

}
