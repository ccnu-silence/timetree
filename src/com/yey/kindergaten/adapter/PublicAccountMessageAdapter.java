package com.yey.kindergaten.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ChatLookPictureActivity;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.MessagePublicAccount;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.util.UtilsLog;
import com.yey.kindergaten.widget.AutoResizeTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PublicAccountMessageAdapter extends BaseListAdapter<MessagePublicAccount> {
    // 8种Item的类型
    // 文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    // 图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    // 图文
    private final int TYPE_SEND_IMAGE_TEXT = 4;
    // 视频
    private final int TYPE_RECEIVE_VIDEO = 5;
    private final int TYPE_SEND_NO_IMAGE_TEXT = 6;
    private LayoutInflater inflater;
    private List<MessageRecent> mData;
    private Context mContext;
    String currentObjectId = "";
    Context context;
    private final static String TAG = "PublicAccountMessageAdapter";

    public PublicAccountMessageAdapter(Context context, List<MessagePublicAccount> objects) {
        super(context, objects);
        this.context = context;
        currentObjectId = AppServer.getInstance().getAccountInfo().getUid() + "";
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        final MessagePublicAccount mr = getList().get(position);
        if (convertView == null) {
            convertView = createViewByType(mr, position);
        }
        LinearLayout ll_item = ViewHolder.get(convertView, R.id.ll_item);
        AutoResizeTextView messageTitle = ViewHolder.get(convertView, R.id.textViewMessageTitle);
        ImageView thumbnail = ViewHolder.get(convertView, R.id.imageViewThumbnail);
        AutoResizeTextView messageSummary = ViewHolder.get(convertView, R.id.textViewMessageSummary);
        TextView time = ViewHolder.get(convertView, R.id.tv_time);
        TextView content_time = ViewHolder.get(convertView, R.id.tv_item_time);
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);

        // 图片
        ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
        final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);  // 进度条
        final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);  // 失败重发
        final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);   // 发送状态

        time.setVisibility(View.VISIBLE);
        time.setText(TimeUtil.getChatTime(mr.getDate() == null ? TimeUtil.getYMDHM() : mr.getDate()));

        final String text = mr.getTitle().trim();
        final String fileurl = mr.getFileurl();
        switch (mr.getContenttype()) {
            case AppConstants.TYPE_TEXT:
                try {
                    if (mr.getAvatar() == null) {
                        PublicAccount pa = DbHelper.getDB(AppContext.getInstance()).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", mr.getPublicid()));
                        GlideUtils.loadImage(AppContext.getInstance(), pa.getAvatar(), iv_avatar);
//                        ImageLoader.getInstance().displayImage(pa.getAvatar(), iv_avatar, ImageLoadOptions.getMessagePublicOptions());
                    } else {
                        GlideUtils.loadImage(AppContext.getInstance(), mr.getAvatar(), iv_avatar);
//                        ImageLoader.getInstance().displayImage(mr.getAvatar(), iv_avatar, ImageLoadOptions.getMessagePublicOptions());
                    }

                    SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, text);
                    messageTitle.setText(spannableString);
                    messageTitle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, context.getResources().getDisplayMetrics()));
                    // messageTitle.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,17,context.getResources().getDisplayMetrics()));
                    messageTitle.setEnableSizeCache(false);
                } catch (Exception e) {
                    UtilsLog.i(TAG, e.getCause() + "");
                }
                break;
            case AppConstants.TYPE_IMAGE: // 图片类
                try {
                    if (mr.getAvatar() == null) {
                        PublicAccount pa = DbHelper.getDB(AppContext.getInstance()).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", mr.getPublicid()));
                        GlideUtils.loadImage(AppContext.getInstance(), pa.getAvatar(), iv_avatar);
//                        ImageLoader.getInstance().displayImage(pa.getAvatar(), iv_avatar, ImageLoadOptions.getMessagePublicOptions());
                    } else {
                        GlideUtils.loadImage(AppContext.getInstance(), mr.getAvatar(), iv_avatar);
//                        ImageLoader.getInstance().displayImage(mr.getAvatar(), iv_avatar, ImageLoadOptions.getMessagePublicOptions());
                    }
                    if (fileurl != null && !fileurl.equals("")) { // 发送成功之后存储的图片类型的content和接收到的是不一样的
                        dealWithImage(position, progress_load, iv_fail_resend, tv_send_status, iv_picture, mr);
                    }
                    iv_picture.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(context, ChatLookPictureActivity.class);
                            int imageposition = 0;
                            int size = 0;
                            ArrayList<String> photos = new ArrayList<String>();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getContenttype() == AppConstants.TYPE_IMAGE) {
                                    size++;
                                    MessagePublicAccount imageitem = list.get(i);
                                    photos.add(imageitem.getFileurl());
                                    if (imageitem.getFileurl().equals(text)) {
                                        imageposition = size;
                                    }
                                }
                            }
                            intent.putStringArrayListExtra("imglist", photos);
                            intent.putExtra("position", imageposition - 1);
                            context.startActivity(intent);
                        }
                    });

                } catch (Exception e) {
                    UtilsLog.i(TAG, e.getCause() + "");
                }

                break;
            case AppConstants.TYPE_IMAGE_TEXT: // 图文消息
                // time.setVisibility(View.GONE);
                if (mr.getPmid() == 0) {
                    switch (mr.getPublicid()) {
                        case 11:
                            thumbnail.setImageResource(R.drawable.directorr_pua_1);
                            break;
                        case 12:
                            thumbnail.setImageResource(R.drawable.teacher_pua_1);
                            break;
                        case 13:
                            thumbnail.setImageResource(R.drawable.parent_pua_1);
                            break;
                        case 16:
                            thumbnail.setImageResource(R.drawable.timetree_director_1);
                            break;
                        case 17:
                            thumbnail.setImageResource(R.drawable.timetree_teacher_1);
                            break;
                        case 18:
                            thumbnail.setImageResource(R.drawable.timetree_parent_1);
                            break;
                    }
                } else {
                    GlideUtils.loadImage(AppContext.getInstance(), mr.getFileurl(), thumbnail);
//                    ImageLoader.getInstance().displayImage(mr.getFileurl(), thumbnail, ImageLoadOptions.getMessagePublicOptions_view());
                }
                if (mr.getFiledesc()!=null && !mr.getFiledesc().equals("")) {
                    messageSummary.setVisibility(View.VISIBLE);
                    messageSummary.setText(mr.getFiledesc());
                } else {
                    messageSummary.setVisibility(View.GONE);
                }
                content_time.setText(TimeUtil.getYMD(mr.getDate() == null ? TimeUtil.getYMDHMS() : mr.getDate()));

                if (mr.getTitle()!=null && mr.getTitle().contains("介绍")
                       && mr.getFiledesc()!=null && mr.getFiledesc().contains("能为你做什么？")) {
                    time.setVisibility(View.GONE);
                    content_time.setVisibility(View.GONE);
                } else {
                    time.setVisibility(View.VISIBLE);
                    content_time.setVisibility(View.VISIBLE);
                }

                messageTitle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics()));
                messageTitle.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics()));
                messageTitle.setEnableSizeCache(false);
                messageTitle.setText(mr.getTitle());
                messageSummary.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));
                messageSummary.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));
                messageSummary.setEnableSizeCache(false);
                break;
            case AppConstants.TYPE_VIDEO:   // 视频
                time.setVisibility(View.GONE);
                GlideUtils.loadImage(AppContext.getInstance(), mr.getFileurl(), thumbnail);
//                ImageLoader.getInstance().displayImage(mr.getFileurl(), thumbnail, ImageLoadOptions.getOptions());
                messageTitle.setText(mr.getTitle());
                if (mr.getFiledesc()!=null && !mr.getFiledesc().equals("")) {
                    messageSummary.setText(mr.getFiledesc());
                } else {
                    messageSummary.setVisibility(View.GONE);
                }
                content_time.setText(TimeUtil.getYMD(mr.getDate() == null ? TimeUtil.getYMDHMS() : mr.getDate()));
                break;
            case AppConstants.TYPE_NO_IMAGE_TEXT:
                // time.setVisibility(View.GONE);
                TextView readfulltext = ViewHolder.get(convertView, R.id.readfulltext);
                // ImageLoader.getInstance().displayImage(mr.getFileurl(), thumbnail, ImageLoadOptions.getOptions());
                messageTitle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics()));
                messageTitle.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics()));
                messageTitle.setEnableSizeCache(false);
                messageSummary.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));
                messageSummary.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));
                messageSummary.setEnableSizeCache(false);
                messageTitle.setText(mr.getTitle());
                if (mr.getFiledesc()!=null && !mr.getFiledesc().equals("")) {
                    messageSummary.setVisibility(View.VISIBLE);
                    messageSummary.setText(mr.getFiledesc());
                } else {
                    messageSummary.setVisibility(View.GONE);
                }
                if (mr.getPmid() > 999999998) {
                   readfulltext.setVisibility(View.GONE);
                } else {
                   readfulltext.setVisibility(View.VISIBLE);
                }
                content_time.setText(TimeUtil.getYMD(mr.getDate() == null ? TimeUtil.getYMDHMS() : mr.getDate()));
               break;
            }

        return convertView;
    }

    private View createViewByType(MessagePublicAccount message, int position) {
        int type = message.getContenttype();
        if (type == AppConstants.TYPE_IMAGE) {                  // 图片类型
           return getItemViewType(position) == TYPE_RECEIVER_IMAGE
                   ? mInflater.inflate(R.layout.chat_item_received_image, null)
                   : mInflater.inflate(R.layout.chat_item_sent_image, null);
        } else if (type == AppConstants.TYPE_IMAGE_TEXT) {      // 图文
            return mInflater.inflate(R.layout.public_account_messagelist_item, null);
        } else if (type == AppConstants.TYPE_NO_IMAGE_TEXT) {   // 通知
            return mInflater.inflate(R.layout.public_account_messagelist_no_image_item, null);
        } else if (type == AppConstants.TYPE_VIDEO) {           // 视频
            return mInflater.inflate(R.layout.public_account_messagelist_item, null);
        } else {                                                // 剩下默认的都是文本
            return getItemViewType(position) == TYPE_RECEIVER_TXT
                    ? mInflater.inflate(R.layout.public_account_messagelist_item_text, null)
                    : mInflater.inflate(R.layout.public_account_messagelist_item_text, null);
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage!=null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    /**
     * 处理图片
     *
     * @param position
     * @param progress_load
     * @param iv_fail_resend
     * @param tv_send_status
     * @param iv_picture
     * @param item
     */
    private void dealWithImage(int position, final ProgressBar progress_load, ImageView iv_fail_resend, TextView tv_send_status, ImageView iv_picture, MessagePublicAccount item) {
        String text = item.getFileurl();
        ImageLoader.getInstance().displayImage(text, iv_picture, ImageLoadOptions.getContactsPuacPicOptions(), new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progress_load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progress_load.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progress_load.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progress_load.setVisibility(View.INVISIBLE);
            }

        });
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        MessagePublicAccount msg = list.get(position);
        if (msg.getContenttype() == AppConstants.TYPE_IMAGE) {
            return String.valueOf(msg.getPublicid()).equals(currentObjectId) ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
        } else if (msg.getContenttype() == AppConstants.TYPE_IMAGE_TEXT) {
            return TYPE_SEND_IMAGE_TEXT;
        } else if (msg.getContenttype() == AppConstants.TYPE_NO_IMAGE_TEXT) {
            return TYPE_SEND_NO_IMAGE_TEXT;
        } else if (msg.getContenttype() == AppConstants.TYPE_VIDEO) {
            return TYPE_RECEIVE_VIDEO;
        } else {
            return String.valueOf(msg.getPublicid()).equals(currentObjectId) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        }
    }

    /**
     * 没有标题的对话框
     *
     * @param msg
     * @param mOkOnClickListener
     */
    public void showDialogNoTitle(String msg,DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new Builder((Activity)this.context);
        builder.setMessage(msg);
        builder.setPositiveButton("确认",mOkOnClickListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


}
