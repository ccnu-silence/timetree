package com.yey.kindergaten.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.ClassVideo;

import java.util.List;

/**
 * Created by zy on 2015/7/14.
 */
public class ClassVideoAdapter extends BaseAdapter {

    private Context context;
    private List<ClassVideo> list;
    private LayoutInflater mInflater;

    public interface onClickListener{
        void onClick(View view,ClassVideo video);
    }

    onClickListener onClickListener;

    public void setOnClickListener(ClassVideoAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ClassVideoAdapter(Context context ,List<ClassVideo> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View convertView = mInflater.inflate(R.layout.classvideoitem, null);
        ViewHolder holder = null;

        final ClassVideo video = list.get(position);
        if (holder == null) {
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.show_thumb_img_iv);
            holder.btn = (TextView) convertView.findViewById(R.id.class_video_up_btn);
            holder.contet_tv = (TextView) convertView.findViewById(R.id.class_duration_and_size_tv);
            holder.long_view = convertView.findViewById(R.id.long_line);
            holder.short_view = convertView.findViewById(R.id.short_line);
            holder.title_tv = (TextView) convertView.findViewById(R.id.class_title_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == getCount() - 1) {
            holder.long_view.setVisibility(View.VISIBLE);
            holder.short_view.setVisibility(View.GONE);
        } else {
            holder.long_view.setVisibility(View.GONE);
            holder.short_view.setVisibility(View.VISIBLE);
        }

        holder.iv.setImageBitmap(getVideoThumbnail(video.getPath()));
        String title = null;
        if (video.getContent().length() > 15) {
            title = video.getContent().substring(0, 15) + "...";
        } else {
            title = video.getContent();
        }
        holder.title_tv.setText(title);

        // 转换成浮点的类型
        double duration = video.getDuration();
        double length = video.getSize();
        double second = (duration / 1000.00);
        double size = (length / 1024.00 / 1024.00);

        holder.contet_tv.setText(second + " 秒" + " / " + size + " Mb");
        // 注：这里还需要限制上传的大小，这里由于不知道约定，暂时没写
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener!=null) {
                    onClickListener.onClick(view, video);
                }
            }
        });
        return convertView;
    }

    /**
     * 根据路径获取视频缩略图
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    class ViewHolder {
        ImageView iv;
        TextView title_tv;
        TextView contet_tv;
        TextView btn;
        View long_view;
        View short_view;
    }

}
