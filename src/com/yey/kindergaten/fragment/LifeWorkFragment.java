package com.yey.kindergaten.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.LifeWorkPhoto;
import com.yey.kindergaten.bean.LifePhoto;
import com.yey.kindergaten.bean.Term;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class LifeWorkFragment extends FragmentBase implements OnItemClickListener{

    @ViewInject(R.id.id_show_child_lifephoto_lv)ListView lifework_lv;
    private MyAdapter adapter;
    private int index = 0;
    private String type;
    private List<Term>termlist = new ArrayList<Term>();
    private List<LifePhoto> photolist = new ArrayList<LifePhoto>();
    @SuppressWarnings("unchecked")

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_life_work_main, null);
        ViewUtils.inject(this,view);
        Bundle bundle = getArguments();
        termlist = (List<Term>) bundle.getSerializable(AppConstants.BUNDLE_ALBUM);
        index = bundle.getInt(AppConstants.BUNDLE_INDEX);
        type = bundle.getString("type");
        photolist = termlist.get(index).getPhoto();
        adapter = new MyAdapter(photolist, AppContext.getInstance());
        lifework_lv.setAdapter(adapter);
        lifework_lv.setOnItemClickListener(this);
        return view;
    }

    class MyAdapter extends BaseAdapter {

        private List<LifePhoto> photolist;
        private LayoutInflater mInflater;

        public MyAdapter(List<LifePhoto> photolist, Context context) {
            this.photolist = photolist;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return photolist.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewgroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.inflater_show_lifephoto_main_child, null);
                holder.headImgiv = (CircleImageView) convertView.findViewById(R.id.id_inflater_service_showheadiv_cv);
                holder.showNametv = (TextView) convertView.findViewById(R.id.id_inflater_service_showname_tv);
                holder.photoNumtv = (TextView) convertView.findViewById(R.id.id_inflater_service_photonum_tc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LifePhoto count = photolist.get(position);
            holder.photoNumtv.setText(count.getPhotocount() + " 张");
            holder.showNametv.setText(count.getName());
            GlideUtils.loadHeadImage(LifeWorkFragment.this.getActivity(), count.getHeadpic(), holder.headImgiv);
//            ImageLoader.getInstance().displayImage(count.getHeadpic(), holder.headImgiv, ImageLoadOptions.getHeadOptions());
            return convertView;
        }

        public void setList(List<LifePhoto> photolist) {
            this.photolist = photolist;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        private TextView showNametv;
        private CircleImageView headImgiv;
        private TextView  photoNumtv;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        ImageView itemImg = (ImageView)view.findViewById(R.id.id_inflater_service_showheadiv_cv);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.common_photo_selecter);
        itemImg.startAnimation(animation);
        Intent intent = new Intent(this.getActivity(), LifeWorkPhoto.class);
        LifePhoto lifePhoto = photolist.get(position);
        intent.putExtra(AppConstants.PARAM_ALBUM, lifePhoto);
        intent.putExtra("term", termlist.get(index));
        intent.putExtra("type", "fromlifemain");
        intent.putExtra("lifetype", type);
        if (type.equals("1")) {
            intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, AppConstants.PARAM_UPLOAD_LIFE);
        } else {
            intent.putExtra(AppConstants.INTENT_ALBUM_TYPE, AppConstants.PARAM_UPLOAD_WORK);
        }
        startActivity(intent);
    }

    public void refresh(final List<Term> rlist) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    photolist.clear();
                    photolist.addAll(rlist.get(index).getPhoto());
                    adapter.setList(photolist);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
