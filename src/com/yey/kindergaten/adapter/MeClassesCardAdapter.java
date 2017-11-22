package com.yey.kindergaten.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.MeShareActivity;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.Classe;
import com.yey.kindergaten.util.AppUtils;

import java.util.Hashtable;
import java.util.List;


public class MeClassesCardAdapter extends BaseAdapter{

    Context context;
    private int  clickposition;
    Bitmap bitmap;

    List<Classe> list;
    public MeClassesCardAdapter(Context context, List<Classe> list)
    {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        clickposition = position;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_me_classes_card_item,null);
        }
        TextView nametv=ViewHolder.get(convertView, R.id.mecard_nametv) ;
        ImageView iv_classescard = ViewHolder.get(convertView, R.id.me_classescard_bariv);
        LinearLayout sharr_weixin_ll = ViewHolder.get(convertView, R.id.sharr_weixin_ll);
        TextView sharr_weixin_tv = ViewHolder.get(convertView, R.id.sharr_weixin_tv);

        if (position == getCount() - 1){
            sharr_weixin_ll.setVisibility(View.VISIBLE);
        } else {
            sharr_weixin_ll.setVisibility(View.GONE);
        }

        sharr_weixin_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeShareActivity.class);
                context.startActivity(intent);
            }
        });

        //View view=convertView.findViewById(R.id.id_item_view_line);
        String className = "";
        int classId;
        if (list!=null && list.size()!=0) {
            className = list.get(position).getCname();
            classId = list.get(position).getCid();
            if (className!=null && !className.equals("")){
                nametv.setText(className);
            } else {
                nametv.setText("");
            }
            if (classId!=0) {
                setClassesCard(iv_classescard, classId);
            } else {
                iv_classescard.setImageBitmap(null);
            }
        }
        return convertView;
    }
    private void setClassesCard(ImageView iv, int classId){
        try {
            //bitmap = Create2DCode("TIMES_TREE_QRCODE_0#" + classId);
            bitmap = Create2DCode(AppUtils.replaceAddClassUrl(classId));

            iv.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    public Bitmap Create2DCode(String str) throws WriterException {
        // 用于设置QR二维码参数
        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
        // 设置QR二维码的纠错级别——这里选择最高H级别
        qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //创建二维码
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }  else {                              //无信息设置像素点为白色
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
