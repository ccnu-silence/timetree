package com.yey.kindergaten.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by Administrator on 2015/2/3.
 */
public class MyWebView extends WebView {

    public float clickeventX;
    public float clickeventY;
    Boolean isFlag = true;
    public interface WebViewOnclickListener {
        public void webviewOnclick(String url, String clickImageUrl);
    }
    WebViewOnclickListener webViewOnclickListener;
    public Context context;

    @SuppressLint("NewApi")
    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.setOnLongClickListener(onLongClickListener);
        this.setOnTouchListener(onTouchListener);
    }

    @SuppressLint("NewApi")
    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setOnLongClickListener(onLongClickListener);
        this.setOnTouchListener(onTouchListener);
    }

    @SuppressLint("NewApi")
    public MyWebView(Context context) {
        super(context);
        this.context = context;
        this.setOnLongClickListener(onLongClickListener);
        this.setOnTouchListener(onTouchListener);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    clickeventX = event.getX();
                    clickeventY = event.getY();
                    isFlag = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((Math.abs(clickeventX - event.getX()) > 5) || (Math.abs(clickeventY - event.getY()) > 5)) {
                        isFlag = false;
                    } else {
                        isFlag = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isFlag) {
                        isFlag = false;
                        HitTestResult result = ((WebView) view).getHitTestResult();
                        if (result != null) {
                            int type = result.getType();
                            // 点击的是图片
                            if (type == HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                                String imgurl = result.getExtra();
                                if (imgurl!=null && imgurl.contains("http")) {
                                    if (webViewOnclickListener!=null) {
                                        webViewOnclickListener.webviewOnclick(((WebView) view).getUrl().toString(), imgurl);
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    isFlag = false;
                    break;
            }
            return false;
        }
    };

    OnLongClickListener onLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            isFlag = false;
            return false;
        }
    };

    public WebViewOnclickListener getWebViewOnclickListener() {
        return webViewOnclickListener;
    }

    public void setWebViewOnclickListener( WebViewOnclickListener webViewOnclickListener) {
        this.webViewOnclickListener = webViewOnclickListener;
    }

}

