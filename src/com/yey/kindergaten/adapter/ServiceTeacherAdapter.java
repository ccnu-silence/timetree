package com.yey.kindergaten.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.widget.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceTeacherAdapter extends BaseAdapter{

    private Context context;
    private List<Teacher>list;
    private List<String>friendlist = new ArrayList<String>();
    private LayoutInflater mInflater;
    HashMap<String, Boolean>checkmap = new HashMap<String, Boolean>();
    private boolean groupState = false;
    private ListView view;

    TextView nickNameTv = null;
    CircleImageView headiv = null;
    ImageView checkiv = null;

    public ServiceTeacherAdapter(Context context ,List<Teacher>list,List<String>liststr) {
        this.context = context;
        this.list = list;
        this.friendlist = liststr;
        if (liststr == null || liststr.size() == 0) {
            groupState = false;
        }
        mInflater = LayoutInflater.from(context);
        for (int index = 0; index < list.size(); index++) {
            checkmap.put(list.get(index).getUid() + "//", false);
            for (int i = 0; i < friendlist.size(); i++) {
                if ((list.get(index).getUid() + "//").contains(friendlist.get(i))) {
                    checkmap.put(friendlist.get(i), true);
                }
            }
        }
    }

    public ServiceTeacherAdapter(Context context, List<Teacher>list, ListView view) {
        this.context = context;
        this.list = list;
        this.view = view;
        mInflater = LayoutInflater.from(context);
        for (int i = 0; i < list.size(); i++) {
            if (friendlist == null || friendlist.size() == 0) {
                checkmap.put(list.get(i).getUid() + "//", false);
            } else {
                StringBuffer buffer = new StringBuffer();
                for (int index = 0; index < friendlist.size(); index++) {
                    buffer.append(friendlist.get(index));
                }
                String uids = buffer.toString();
                String[] uid = uids.split("//");
                // 如果这个有值，遍历这个数组，并记录在map中，在getview的时候判断显示
                for (int s = 0; s < uid.length; s++) {
                    if (list.contains(uid[i])) {
                        checkmap.put(uid[i], true);
                    } else {
                        checkmap.put(uid[i], false);
                    }
                }
            }
            if (friendlist!=null || friendlist.size()!=0) {
                StringBuffer buffer = new StringBuffer();
                for (int index = 0; index < friendlist.size(); index++) {
                    buffer.append(friendlist.get(index));
                }
                String uids = buffer.toString();
                String[] uid = uids.split("//");
//              for (int x = 0; x < uid.length; x++) {
//                  checkmap.put(uid[x], false);
//              }
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    public void setGroupState(boolean state){
        this.groupState = state;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCheckedChildren(List<String>list){
        this.friendlist=list;
    }

    public List<String> getCheckedChildren(){
        return friendlist;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        RelativeLayout click;
        final String childrenId;
        if (convertView == null) {
            convertView=mInflater.inflate(R.layout.inflater_service_schedule_select_friend, null);
        }
        nickNameTv = (TextView) convertView.findViewById(R.id.id_inflater_service_showname_tv);
        headiv = (CircleImageView) convertView.findViewById(R.id.id_inflater_service_showheadiv_cv);
        checkiv = (ImageView) convertView.findViewById(R.id.id_inflater_service_showchecked_iv);
        click = (RelativeLayout) convertView.findViewById(R.id.id_click_inflater);
        if (list.get(position).getRealname()!=null) {
            nickNameTv.setText(list.get(position).getRealname());
        }
        if (list.get(position).getAvatar()!=null) {
            ShowLocalImage(list.get(position).getAvatar(), headiv);
        }
        childrenId = list.get(position).getUid() + "//";

        click.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!friendlist.contains(childrenId)) {
                     friendlist.add(childrenId);
                     checkmap.put(childrenId, true);
                } else {
                    friendlist.remove(childrenId);
                    checkmap.put(childrenId, false);
                }
                ServiceTeacherAdapter.this.notifyDataSetChanged();
            }
        });
          
        boolean state = checkmap.get(childrenId);
            if (state == true) {
                checkiv.setImageResource(R.drawable.friendster_check_true);
            } else {
                checkiv.setImageResource(R.drawable.friendster_check_false);
            }
        return convertView;
    }

    public void ShowLocalImage(String path,ImageView imageView) {
        ImageLoader.getInstance().displayImage(path, imageView, ImageLoadOptions.getHeadOptions());
    }


}
