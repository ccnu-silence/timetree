package com.yey.kindergaten.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * 垂直居中的ImageSpan(图片 + 表情)
 *
 * @author KenChung
 */
public class VerticalImageSpan extends ImageSpan {

    public VerticalImageSpan(Drawable drawable) {
        super(drawable);
    }

    public VerticalImageSpan(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent=top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = 0;
        // 获得将要显示的文本高度-图片高度除2等居中位置+top(换行情况)
        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        // 偏移画布后开始绘制
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

}
