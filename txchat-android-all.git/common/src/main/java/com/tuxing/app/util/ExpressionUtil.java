package com.tuxing.app.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 显示表情
 *
 */
public class ExpressionUtil {
	public static void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start, int imageSize)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			if (matcher.start() < start) {
				continue;
			}
			int resId = 0;
			// TODO 选择表情
//			for (int i = 0; i < FaceExpressions.emoImgs.length; i++) {
//				if (FaceExpressions.emoNames[i].equals(key)) {
//					resId = FaceExpressions.emoImgs[i];
//					break;
//				}
//			}
			if (resId != 0) {
				ImageSpan imageSpan = getImageSpan(context,resId,imageSize);
				int end = matcher.start() + key.length();
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
		}
	}
	
	public static ImageSpan getImageSpan(Context mContext, int resId, int imageSize) {
		Drawable drawable = mContext.getResources().getDrawable(resId);
		drawable.setBounds(0, 0, imageSize, imageSize);
		ImageSpan imageSpan = new ImageSpan(drawable);
		return imageSpan;
	}

	public static SpannableString getExpressionString(Context context,
			String str, String zhengze, int imageSize) {
		if (str == null || str.length() < 1) {
			return new SpannableString("");
		}
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 闁俺绻冩导鐘插弳閻ㄥ嫭顒滈崚娆掋�鏉堟儳绱￠弶銉ф晸閹存劒绔存稉鐚礱ttern
		try {
			dealExpression(context, spannableString, sinaPatten, 0, imageSize);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}
}