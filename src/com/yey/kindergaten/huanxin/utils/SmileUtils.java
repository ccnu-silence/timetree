/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yey.kindergaten.huanxin.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.yey.kindergaten.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmileUtils {
	public static final String face0 = "[/face00]";
	public static final String face1 = "[/face01]";
	public static final String face2 = "[/face02]";
	public static final String face3 = "[/face03]";
	public static final String face4 = "[/face04]";
	public static final String face5 = "[/face05]";
	public static final String face6 = "[/face06]";
	public static final String face7 = "[/face07]";
	public static final String face8 = "[/face08]";
	public static final String face9 = "[/face09]";
	public static final String face10 = "[/face10]";
	public static final String face11 = "[/face11]";
	public static final String face12 = "[/face12]";
	public static final String face13 = "[/face13]";
	public static final String face14 = "[/face14]";
	public static final String face15 = "[/face15]";
	public static final String face16 = "[/face16]";
	public static final String face17 = "[/face17]";
	public static final String face18 = "[/face18]";
	public static final String face19 = "[/face19]";
//	public static final String ee_21 = "[:-*]";
//	public static final String ee_22 = "[^o)]";
//	public static final String ee_23 = "[8-)]";
//	public static final String ee_24 = "[(|)]";
//	public static final String ee_25 = "[(u)]";
//	public static final String ee_26 = "[(S)]";
//	public static final String ee_27 = "[(*)]";
//	public static final String ee_28 = "[(#)]";
//	public static final String ee_29 = "[(R)]";
//	public static final String ee_30 = "[({)]";
//	public static final String ee_31 = "[(})]";
//	public static final String ee_32 = "[(k)]";
//	public static final String ee_33 = "[(F)]";
//	public static final String ee_34 = "[(W)]";
//	public static final String ee_35 = "[(D)]";
	
	private static final Factory spannableFactory = Factory
	        .getInstance();
	
	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
		
	    addPattern(emoticons, face0, R.drawable.face0);
	    addPattern(emoticons, face1, R.drawable.face1);
	    addPattern(emoticons, face2, R.drawable.face2);
	    addPattern(emoticons, face3, R.drawable.face3);
	    addPattern(emoticons, face4, R.drawable.face4);
	    addPattern(emoticons, face5, R.drawable.face5);
	    addPattern(emoticons, face6, R.drawable.face6);
	    addPattern(emoticons, face7, R.drawable.face7);
	    addPattern(emoticons, face8, R.drawable.face8);
	    addPattern(emoticons, face9, R.drawable.face9);
	    addPattern(emoticons, face10, R.drawable.face10);
	    addPattern(emoticons, face11, R.drawable.face11);
	    addPattern(emoticons, face12, R.drawable.face12);
	    addPattern(emoticons, face13, R.drawable.face13);
	    addPattern(emoticons, face14, R.drawable.face14);
	    addPattern(emoticons, face15, R.drawable.face15);
	    addPattern(emoticons, face16, R.drawable.face16);
	    addPattern(emoticons, face17, R.drawable.face17);
	    addPattern(emoticons, face18, R.drawable.face18);
	    addPattern(emoticons, face19, R.drawable.face19);
//	    addPattern(emoticons, ee_21, R.drawable.face20);
//	    addPattern(emoticons, ee_22, R.drawable.ee_22);
//	    addPattern(emoticons, ee_23, R.drawable.ee_23);
//	    addPattern(emoticons, ee_24, R.drawable.ee_24);
//	    addPattern(emoticons, ee_25, R.drawable.ee_25);
//	    addPattern(emoticons, ee_26, R.drawable.ee_26);
//	    addPattern(emoticons, ee_27, R.drawable.ee_27);
//	    addPattern(emoticons, ee_28, R.drawable.ee_28);
//	    addPattern(emoticons, ee_29, R.drawable.ee_29);
//	    addPattern(emoticons, ee_30, R.drawable.ee_30);
//	    addPattern(emoticons, ee_31, R.drawable.ee_31);
//	    addPattern(emoticons, ee_32, R.drawable.ee_32);
//	    addPattern(emoticons, ee_33, R.drawable.ee_33);
//	    addPattern(emoticons, ee_34, R.drawable.ee_34);
//	    addPattern(emoticons, ee_35, R.drawable.ee_35);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
	        int resource) {
	    map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                spannable.setSpan(new ImageSpan(context, entry.getValue()),
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	
	
}
