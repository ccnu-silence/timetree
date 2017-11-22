package com.yey.kindergaten.widget;

import com.yey.kindergaten.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class ImageLoadingDialog extends Dialog {

	public ImageLoadingDialog(Context context) {
		super(context, R.style.ImageloadingDialogStyle);
	}

	private ImageLoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friendster_progressbar);
	}

}
