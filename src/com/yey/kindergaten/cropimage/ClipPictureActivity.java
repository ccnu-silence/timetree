/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yey.kindergaten.cropimage;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.util.UtilsLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClipPictureActivity extends BaseActivity implements OnTouchListener,
        OnClickListener {

    private static final String TAG = "ClipPictureActivity";
    private ImageView srcPic;
    private TextView sure;
    private ImageView leftiv;
    private ClipView clipview;
    private TextView header_title;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /** 动作标志：无 */
    private static final int NONE = 0;
    /** 动作标志：拖动 */
    private static final int DRAG = 1;
    /** 动作标志：缩放 */
    private static final int ZOOM = 2;
    /** 初始化动作标志 */
    private int mode = NONE;

    /** 记录起始坐标 */
    private PointF start = new PointF();
    /** 记录缩放时两指中间点坐标 */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;
    private Uri mSaveUri = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap!=null && !bitmap.isRecycled()) {
            bitmap.recycle();   // 回收图片所占的内存
            System.gc();   // 提醒系统及时回收
        }
        srcPic = null;
        sure = null;
        leftiv = null;
        clipview = null;
        header_title = null;
        start = null;
        mid = null;
        matrix = null;
        savedMatrix = null;

        bitmap = null;
        mSaveUri = null;

//        setContentView(R.layout.activity_null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clippicture);

        mContentResolver = getContentResolver();
        srcPic = (ImageView) this.findViewById(R.id.src_pic);
        srcPic.setOnTouchListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mImagePath = extras.getString(IMAGE_PATH);
        }
        mSaveUri = getImageUri(mImagePath); // 获取uri
        bitmap = getBitmap(mImagePath); // 获取bitmap

        if (bitmap == null) {
            UtilsLog.d(TAG, "finish!!!");
            finish();
            return;
        }

        ViewTreeObserver observer = srcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                srcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initClipView(srcPic.getTop());
            }
        });

        sure = (TextView) ClipPictureActivity.this.findViewById(R.id.right_tv);
        sure.setText("确定");
        sure.setVisibility(View.VISIBLE);
        sure.setOnClickListener(this);

        leftiv = (ImageView) ClipPictureActivity.this.findViewById(R.id.left_btn);
        leftiv.setVisibility(View.VISIBLE);
        leftiv.setOnClickListener(this);

        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("裁剪照片");
    }

    private ContentResolver mContentResolver;
    public static final String IMAGE_PATH = "image-path";
    final int IMAGE_MAX_SIZE = 1024;
    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }
    private String mImagePath;
    private Bitmap getBitmap(String path) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = false; // 重新读入图片，注意此时已经把options.inJustDecodeBounds设回false
            o.inPreferredConfig = Bitmap.Config.RGB_565; // 默认值ARGB_8888改为RGB_565,节约一半内存
            o.inDither = true;
            o.inSampleSize = 2; // 设置Options.inSampleSize 缩放比例，对大图片进行压缩

            // 设置Options.inPurgeable和inInputShareable：让系统能及时回 收内存
            o.inPurgeable = true; // 设置为True时，表示系统内存不足时可以被回 收，设置为False时，表示不能被回收。
            o.inInputShareable = true; // 设置是否深拷贝，与inPurgeable结合使用，inPurgeable为false时，该参数无意义。

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            o2.inJustDecodeBounds = false; // 重新读入图片，注意此时已经把options.inJustDecodeBounds设回false
            o2.inPreferredConfig = Bitmap.Config.RGB_565; // 默认值ARGB_8888改为RGB_565,节约一半内存
            o2.inDither = true; // inDither为false的确使得图片不被抖动(Dither)处理

            // 设置Options.inPurgeable和inInputShareable：让系统能及时回 收内存
            o2.inPurgeable = true; // 设置为True时，表示系统内存不足时可以被回 收，设置为False时，表示不能被回收。
            o2.inInputShareable = true; // 设置是否深拷贝，与inPurgeable结合使用，inPurgeable为false时，该参数无意义。

            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            UtilsLog.e(TAG, "file " + path + " not found");
        } catch (IOException e) {
            UtilsLog.e(TAG, "file " + path + " not found");
        }
        return null;
    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top
     */
    private void initClipView(int top) {
//        bitmap = BitmapFactory.decodeResource(this.getResources(),
//                R.drawable.pic);

        clipview = new ClipView(ClipPictureActivity.this);
        clipview.setCustomTopBarHeight(top);
        clipview.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                // 按裁剪框求缩放比例, 设置初始图片显示大小，以裁剪框为参照
                float scale = (clipWidth * 1.5f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.5f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                srcPic.setScaleType(ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(bitmap);
            }
        });

        this.addContentView(clipview, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public static final String RETURN_DATA_AS_BITMAP  = "data";
    public static final String ACTION_INLINE_DATA     = "inline-data";
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_tv:
                Bitmap clipBitmap = getBitmap();
                if (clipBitmap == null) {
                    showToast("无法获取图片，请返回重新选择图片");
                    return;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitmapByte = baos.toByteArray();

                Bitmap resultBitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
                saveOutput(resultBitmap);

                Intent intent = new Intent();
                intent.putExtra(RETURN_DATA_AS_BITMAP, bitmapByte);
                break;
            case R.id.left_btn:
                ClipPictureActivity.this.finish();
            default:
                break;
        }
//        Bundle extras = new Bundle();
//        extras.putExtra(RETURN_DATA_AS_BITMAP, resultBitmap);
//        setResult(RESULT_OK,
//                (new Intent()).setAction(ACTION_INLINE_DATA).putExtras(extras));

//        setResult(9, intent);
//        finish();

    }

    private final Handler mHandler = new Handler();
    public static final String ORIENTATION_IN_DEGREES = "orientation_in_degrees";
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    private void saveOutput(Bitmap croppedImage) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 90, outputStream);
                }
            } catch (IOException ex) {
                UtilsLog.e(TAG, "Cannot open file: " + mSaveUri + "/////" + ex);
                setResult(RESULT_CANCELED);
                finish();
                return;
            } finally {
                Util.closeSilently(outputStream);
            }

            Bundle extras = new Bundle();
            Intent intent = new Intent(mSaveUri.toString());
            intent.putExtras(extras);
            intent.putExtra(IMAGE_PATH, mImagePath);
            intent.putExtra(ORIENTATION_IN_DEGREES, Util.getOrientationInDegree(this));
            setResult(RESULT_OK, intent);
        } else {
            UtilsLog.e(TAG, "not defined image url");
        }
        croppedImage.recycle();
        finish();
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap finalBitmap = null;
        if (clipview!=null) {
            try {
                finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                        clipview.getClipLeftMargin(), clipview.getClipTopMargin()
                                + statusBarHeight, clipview.getClipWidth(),
                        clipview.getClipHeight());
            } catch (Exception e) {
                UtilsLog.i(TAG, "getBitmap fail because excetion; " + e.getMessage() + e.getCause());
            }
        }

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

}


