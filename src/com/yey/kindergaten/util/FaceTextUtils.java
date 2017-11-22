package com.yey.kindergaten.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.yey.kindergaten.bean.FaceText;
import com.yey.kindergaten.widget.VerticalImageSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FaceTextUtils {

	public static List<FaceText> faceTexts = new ArrayList<FaceText>();

	static {
		faceTexts.add(new FaceText("[/face00]"));
		faceTexts.add(new FaceText("[/face01]"));
		faceTexts.add(new FaceText("[/face02]"));
		faceTexts.add(new FaceText("[/face03]"));
		faceTexts.add(new FaceText("[/face04]"));
		faceTexts.add(new FaceText("[/face05]"));
		faceTexts.add(new FaceText("[/face06]"));
		faceTexts.add(new FaceText("[/face07]"));
		faceTexts.add(new FaceText("[/face08]"));
		faceTexts.add(new FaceText("[/face09]"));
		faceTexts.add(new FaceText("[/face10]"));
		faceTexts.add(new FaceText("[/face11]"));
		faceTexts.add(new FaceText("[/face12]"));
		faceTexts.add(new FaceText("[/face13]"));
		faceTexts.add(new FaceText("[/face14]"));
		faceTexts.add(new FaceText("[/face15]"));
		faceTexts.add(new FaceText("[/face16]"));
		faceTexts.add(new FaceText("[/face17]"));
		faceTexts.add(new FaceText("[/face18]"));
		faceTexts.add(new FaceText("[/face19]"));
		faceTexts.add(new FaceText("[/face20]"));
	}

	public static String parse(String s) {
		for (FaceText faceText : faceTexts) {
			s = s.replace("\\" + faceText.text, faceText.text);
			s = s.replace(faceText.text, "\\" + faceText.text);
		}
		return s;
	}

	/** 
	  * toSpannableString
	  * @return SpannableString
	  * @throws
	  */
	public static SpannableString toSpannableString(Context context, String text) {
		if (!TextUtils.isEmpty(text)) {
			SpannableString spannableString = new SpannableString(text);
			int start = 0;
			Pattern pattern = Pattern.compile("\\[/face[a-z0-9]{2}\\]", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(2, faceText.length() - 1);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
						context.getResources().getIdentifier(key, "drawable", context.getPackageName()), options);
//				ImageSpan imageSpan = new ImageSpan(context, bitmap);
                VerticalImageSpan imageSpan = new VerticalImageSpan(context, bitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}
			return spannableString;
		} else {
			return new SpannableString("");
		}
	}

	public static SpannableString toSpannableString(Context context, String text, SpannableString spannableString) {
		int start = 0;
		Pattern pattern = Pattern.compile("\\[/face[a-z0-9]{2}\\]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String faceText = matcher.group();
			String key = faceText.substring(2, faceText.length() - 1);
			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources()
					.getIdentifier(key, "drawable", context.getPackageName()), options);
//			ImageSpan imageSpan = new ImageSpan(context, bitmap);
            VerticalImageSpan imageSpan = new VerticalImageSpan(context, bitmap);
			int startIndex = text.indexOf(faceText, start);
			int endIndex = startIndex + faceText.length();
			if (startIndex >= 0)
				spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			start = (endIndex - 1);
		}
		return spannableString;
	}

}
