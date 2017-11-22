package com.yey.kindergaten.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yey.kindergaten.R;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.HuanxinController;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.TimeUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MessageRecentAdapter extends BaseListAdapter<MessageRecent> {

    public MessageRecentAdapter(Context context, List<MessageRecent> objects) {
        super(context, objects);
    }

    public void addData(List<MessageRecent> data) {
        getList().clear();
        setList(data);
        this.notifyDataSetChanged();
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        final MessageRecent mr = getList().get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_home_main_lv_item, null);
        }
        ImageView head = ViewHolder.get(convertView, R.id.iv_head);
        TextView message_num = ViewHolder.get(convertView, R.id.tv_message_num);
        TextView name = ViewHolder.get(convertView, R.id.tv_name);
        TextView time = ViewHolder.get(convertView, R.id.tv_time);
        TextView lastContent = ViewHolder.get(convertView, R.id.tv_lastcontent);
        if (mr.getNewcount() > 0) {
            message_num.setText(mr.getNewcount() + "");
            message_num.setVisibility(View.VISIBLE);
        } else {
            message_num.setVisibility(View.GONE);
        }

        if (mr.getAction() == AppConstants.PUSH_ACTION_GUIDE_TEACHER || mr.getAction() == AppConstants.PUSH_ACTION_GUIDE_MASTER) {
            // 通知的头像
            head.setImageResource(R.drawable.icon_guide);
        } else if (mr.getAction() >= AppConstants.PUSH_ACTION_SYSTEM_MESSAGE) { // 系统消息
            if (mr.getAvatar() == null || mr.getAvatar().length() == 0) {
                head.setImageResource(R.drawable.icon_msg_notice);
            } else if ((mr.getAvatar() == null || mr.getAvatar().length() == 0 ) && (mr.getAction() == AppConstants.HX_DIRECTOR_ACTION || mr.getAction() == AppConstants.HX_TEACHER_NO_KID)) {
                head.setImageResource(R.drawable.timetree_guide);
            } else {
                ImageLoader.getInstance().displayImage(mr.getAvatar(), head, ImageLoadOptions.getHeadOptions(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) { }
                });
            }
        } else {
            String avatar = mr.getAvatar();
            if (avatar!=null && !avatar.equals("")) {
                // imageLoader.init(ImageLoaderConfiguration.createDefault(AppContext.getInstance()));
                ImageLoader.getInstance().displayImage(avatar, head, ImageLoadOptions.getHeadOptions(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) { }
                });
//              BitmapCache.getInstance().displayNetBmp(head, avatar, null, ImageLoadOptions.getHeadOptions());
            } else {
                head.setImageResource(R.drawable.defaulticon);
            }
        }

        if (mr.getAction() == 0 && !mr.getHxfrom().equals("0")) {
            name.setText(HuanxinController.getRelationNameByRecent(mr));
        } else {
            if (mr.getName() == null || mr.getName().length() == 0) {
                name.setText(mr.getTitle());
            } else {
                name.setText(mr.getName());
            }
        }
        String trueMsg = "";
        trueMsg = mr.getContent() == null ? mr.getTitle() : mr.getContent();
        if (mr.getContenttype() == AppConstants.TYPE_TEXT && trueMsg.contains("face")) {
            trueMsg = trueMsg.replaceAll( "\\[/[a-z]{4}[0-9]{2}\\]", "[表情]");
        } else if (mr.getContenttype() == AppConstants.TYPE_IMAGE) {
            trueMsg = "[图片]";
        } else if (mr.getContenttype() == AppConstants.TYPE_AUDIO) {
            trueMsg = "[语音]";
        } else {
            trueMsg = mr.getTitle() == null ? "" : mr.getTitle();
        }
        if (mr.getAction() >= AppConstants.PUSH_ACTION_SYSTEM_MESSAGE) {
            lastContent.setText(mr.getContent());
        } else {
            lastContent.setText(trueMsg);
        }
        if (mr.getDate()!=null && mr.getDate().contains("天")) {
            time.setText(mr.getDate());
        } else if (mr.getDate()!=null && !mr.getDate().equals("")) {
//            time.setText(TimeUtil.getRecentTime(mr.getDate()));
            time.setText(TimeUtil.getChatTime(mr.getDate()));
        }

        return convertView;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}
