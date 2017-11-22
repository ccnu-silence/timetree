package com.yey.kindergaten.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.AddressBookBean;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.List;

public class ContactPuacAdapter <E> extends ServiceAdapter {

    int []state;
    PuacOnclickback puacOnclickback;
    public interface PuacOnclickback {
        public void puacClick(int id, int position, int state);
    }
    public ContactPuacAdapter(Context context, List list, String adaptertype, int []state) {
        super(context, list, adaptertype);
        this.state = state;
    }

    @Override
    public View bindViewBytype(int position, View convertView, ViewGroup parent, String type) {
        if (type.equals(AppConstants.CONTACTS_ADDPUACRESULT)) {           ////////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contacts_main_frienditem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_frienditemnametv);
            TextView discrptextView = ViewHolder.get(convertView, R.id.contact_friendphonetv);
            discrptextView.setVisibility(View.GONE);
            Button addtextView = ViewHolder.get(convertView, R.id.contact_friendaddbt);
            ImageView arrImageView = ViewHolder.get(convertView, R.id.arrowimage);
            PublicAccount publicAccount = (PublicAccount) list.get(position);
            nametextView.setText(publicAccount.getNickname());
            if (state[position] == 0) {
                addtextView.setText(R.string.contacts_puacdatacard_book);
            } else {
                addtextView.setText(R.string.contacts_puacdatacard_cancelbook);
            }
            SetPuaconclick(addtextView, position, state[position]);
            ShowLocalImage(publicAccount.getAvatar(), imageView);
            arrImageView.setVisibility(View.GONE);
        } else if (type.equals(AppConstants.CONTACTS_FRIENDREQUEST)) {              /////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contacts_friendrequestitem, null);
            }
            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_friend_request_itemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_friend_request_itemnametv);
            TextView fuyantextview = ViewHolder.get(convertView, R.id.contact_friend_request_itemfuyantv);
            Button accepttView = ViewHolder.get(convertView, R.id.contact_friend_request_itemaccepttv);
            Button refusetView = ViewHolder.get(convertView, R.id.contact_friend_request_itemrefusetv);
            LinearLayout btnly = ViewHolder.get(convertView, R.id.contact_friend_request_btnly);
            if (state[position] == 1) {
                btnly.setVisibility(View.GONE);
            }
            MessageRecent mesrecnt = (MessageRecent) list.get(position);
            nametextView.setText(mesrecnt.getName());
            fuyantextview.setText(mesrecnt.getName() + "请求添加您为好友");
            ShowNetImage(mesrecnt.getUrl(), imageView, null);
            SetPuaconclick(accepttView, position, state[position]);
            SetPuaconclick(refusetView, position, state[position]);
        } else if (type.equals(AppConstants.CONTACTS_PUACMAIN)) {
            PublicAccount puac = (PublicAccount) list.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.puacitem, null);
            }
            RelativeLayout item_rl = ViewHolder.get(convertView, R.id.publicAccount_item_rl);
            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
            TextView item_title = ViewHolder.get(convertView, R.id.item_title);

            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
            TextView desctv = ViewHolder.get(convertView, R.id.contact_puacdescatv);
            View view = ViewHolder.get(convertView, R.id.item_view);
            View longview = ViewHolder.get(convertView, R.id.item_longview);
            ImageButton statebtn = ViewHolder.get(convertView, R.id.contatc_puacstatebt);

            if (puac != null) {
                mian_ly.setVisibility(View.VISIBLE);
                item_rl.setVisibility(View.GONE);
                nametextView.setText(puac.getNickname());
                desctv.setText(puac.getDesc());
                imageView.setVisibility(View.VISIBLE);
                ShowNetImage(puac.getAvatar(), imageView, null);
                view.setVisibility(View.VISIBLE);
                longview.setVisibility(View.GONE);
                if (position == list.size()) {
                    longview.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                } else {
                    longview.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }
            }
            statebtn.setVisibility(View.VISIBLE);
            if (state[position] == 1) {
                statebtn.setBackgroundResource(R.drawable.puaccheck);
            } else if (state[position] == -1) {
                statebtn.setVisibility(View.GONE);
            } else {
                statebtn.setBackgroundResource(R.drawable.puacnocheck);
            }
            SetPuaconclick(statebtn, position, state[position]);

            return convertView;
        } else if (type.equals(AppConstants.CONTACTS_PUACMAIN1)) {                   // 通讯录幼儿园公众号列表
            PublicAccount puac = (PublicAccount) list.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.puacitem, null);
            }
            RelativeLayout item_rl = ViewHolder.get(convertView, R.id.publicAccount_item_rl);
            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
            TextView item_title = ViewHolder.get(convertView, R.id.item_title);

            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
            TextView desctv = ViewHolder.get(convertView, R.id.contact_puacdescatv);
            View view = ViewHolder.get(convertView, R.id.item_view);
            View longview = ViewHolder.get(convertView, R.id.item_longview);
            ImageButton statebtn = ViewHolder.get(convertView, R.id.contatc_puacstatebt);

            if (puac != null) {
                if (position == 0) {
                    item_rl.setVisibility(View.VISIBLE);
                    item_title.setText(puac.getNickname());
                    mian_ly.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                } else {
                    item_rl.setVisibility(View.GONE);
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(puac.getNickname());
                    desctv.setText(puac.getDesc());
                    imageView.setVisibility(View.VISIBLE);
                    ShowNetImage(puac.getAvatar(), imageView, null);
//                    view.setVisibility(View.VISIBLE);
//                    longview.setVisibility(View.GONE);
                }
                if (position == 0 || position == list.size() - 1) {
                    view.setVisibility(View.GONE);
                    longview.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    longview.setVisibility(View.GONE);
                }
            }
            statebtn.setVisibility(View.GONE);
            return convertView;
        } else if (type.equals(AppConstants.CONTACTS_PUACMAIN2)) {                   // 通讯录公众号列表
            PublicAccount puac = (PublicAccount) list.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.puacitem, null);
            }
            RelativeLayout item_rl = ViewHolder.get(convertView, R.id.publicAccount_item_rl);
            LinearLayout mian_ly = ViewHolder.get(convertView, R.id.item_mianly);
            TextView item_title = ViewHolder.get(convertView, R.id.item_title);

            CircleImageView imageView = ViewHolder.get(convertView, R.id.contact_puacitemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_puacitemnametv);
            TextView desctv = ViewHolder.get(convertView, R.id.contact_puacdescatv);
            View view = ViewHolder.get(convertView, R.id.item_view);
            View longview = ViewHolder.get(convertView, R.id.item_longview);
            ImageButton statebtn = ViewHolder.get(convertView, R.id.contatc_puacstatebt);

            if (puac != null) {
                if (position == 0) {
                    item_rl.setVisibility(View.VISIBLE);
                    item_title.setText(puac.getNickname());
                    mian_ly.setVisibility(View.GONE);
                } else {
                    item_rl.setVisibility(View.GONE);
                    mian_ly.setVisibility(View.VISIBLE);
                    nametextView.setText(puac.getNickname());
                    desctv.setText(puac.getDesc());
                    imageView.setVisibility(View.VISIBLE);
                    ShowNetImage(puac.getAvatar(), imageView, null);
                    view.setVisibility(View.VISIBLE);
                    longview.setVisibility(View.GONE);
                }
                if (position == 0) {
                    view.setVisibility(View.GONE);
                    longview.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    longview.setVisibility(View.GONE);
                }
                statebtn.setVisibility(View.VISIBLE);
                if (state[position] == 1) {
                    statebtn.setBackgroundResource(R.drawable.puaccheck);
                } else if (state[position] == -1) {
                    statebtn.setVisibility(View.GONE);
                } else {
                    statebtn.setBackgroundResource(R.drawable.puacnocheck);
                }
                SetPuaconclick(statebtn, position, state[position]);
            }
            return convertView;
        } else if (type.equals(AppConstants.SERIVCE_ADRESSBOOKSELECT)) {                   ////////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.addressitemselect, null);
            }
            AddressBookBean addressBookBean = (AddressBookBean) list.get(position);
            LinearLayout bgly = ViewHolder.get(convertView, R.id.addressitem_bgly);
            TextView nametv = ViewHolder.get(convertView, R.id.addressitem_nametv);
            TextView addresstv = ViewHolder.get(convertView, R.id.addressitem_addrestv);
            TextView phonetv = ViewHolder.get(convertView, R.id.addressitem_phonetv);
            TextView postcodetv = ViewHolder.get(convertView, R.id.addressitem_postcodetv);
            ImageView iv = ViewHolder.get(convertView, R.id.addressitem_rightiv);
            nametv.setText(addressBookBean.getReceiver());
            addresstv.setText(addressBookBean.getAddress());
            phonetv.setText(addressBookBean.getPhone());
            postcodetv.setText(addressBookBean.getCode());
            Button editbtn = ViewHolder.get(convertView, R.id.addressitem_editbtn);
            editbtn.setVisibility(View.GONE);
            if (state[position] == 1) {
                iv.setVisibility(View.VISIBLE);
                bgly.setBackgroundResource(R.drawable.addressbook_select);
            } else {
                iv.setVisibility(View.GONE);
                bgly.setBackgroundResource(R.drawable.addressbook_normal);
            }
            return convertView;
        } else if (type.equals(AppConstants.SERIVCE_ALLADRESSESSBOOK)) {                   ////////////
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.addressitemselect, null);
            }
            AddressBookBean addressBookBean = (AddressBookBean) list.get(position);
            LinearLayout bgly = ViewHolder.get(convertView, R.id.addressitem_bgly);
            TextView nametv = ViewHolder.get(convertView, R.id.addressitem_nametv);
            TextView addresstv = ViewHolder.get(convertView, R.id.addressitem_addrestv);
            TextView phonetv = ViewHolder.get(convertView, R.id.addressitem_phonetv);
            TextView postcodetv = ViewHolder.get(convertView, R.id.addressitem_postcodetv);
            ImageView arrow = ViewHolder.get(convertView, R.id.addressitem_arrowiv);
            ImageView iv = ViewHolder.get(convertView, R.id.addressitem_rightiv);
            arrow.setVisibility(View.GONE);
            nametv.setText(addressBookBean.getReceiver());
            addresstv.setText(addressBookBean.getAddress());
            phonetv.setText(addressBookBean.getPhone());
            postcodetv.setText(addressBookBean.getCode());
            Button editbtn = ViewHolder.get(convertView, R.id.addressitem_editbtn);
            SetPuaconclick(editbtn, position, state[position]);
            if (state[position] == 1) {
                iv.setVisibility(View.VISIBLE);
                bgly.setBackgroundResource(R.drawable.addressbook_select);
            } else {
                iv.setVisibility(View.GONE);
                bgly.setBackgroundResource(R.drawable.addressbook_normal);
            }
            return convertView;
        } else {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contacts_main_frienditem, null);
            }
            ImageView imageView = ViewHolder.get(convertView, R.id.contact_frienditemiv);
            TextView nametextView = ViewHolder.get(convertView, R.id.contact_frienditemnametv);
            TextView discrptextView = ViewHolder.get(convertView, R.id.contact_friendphonetv);
            Button contact_friendaddbt = ViewHolder.get(convertView, R.id.contact_friendaddbt);
            ImageView arrImageView = ViewHolder.get(convertView, R.id.arrowimage);
            Friend friend = (Friend) list.get(position);
            ShowNetImage(friend.getAvatar(), imageView, null);
            Setonclick(contact_friendaddbt, position);
            nametextView.setText(friend.getNickname());
            discrptextView.setVisibility(View.GONE);
            if (state[position] == 0) {
                contact_friendaddbt.setText(R.string.contacts_addtofriend);
            } else if (state[position] == 4) {
                contact_friendaddbt.setBackgroundResource(R.drawable.addfriend_btn_selectrue);
            } else {
                contact_friendaddbt.setVisibility(View.GONE);
            }
            SetPuaconclick(contact_friendaddbt, position, state[position]);
            arrImageView.setVisibility(View.GONE);
        }
        return convertView;

    }
    
    public void SetPuaconclick(final View view, final int theposition, final int state) {
        if (puacOnclickback!=null) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    puacOnclickback.puacClick(view.getId(), theposition, state);
                }
            });
        }
    }

    public int[] getState() {
        return state;
    }
    public void setState(int[] state) {
        this.state = state;
        this.notifyDataSetChanged();
    }

    public PuacOnclickback getPuacOnclickback() {
        return puacOnclickback;
    }
    public void setPuacOnclickback(PuacOnclickback puacOnclickback) {
        this.puacOnclickback = puacOnclickback;
    }


}
