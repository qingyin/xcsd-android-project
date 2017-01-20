package com.tuxing.app.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class ShapeUtil {

	public static final GradientDrawable getGradientTopDrawable(
			Context context, int color) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(context.getResources().getColor(color));
		float radius = PixUtil.convertDpToPixel(8, context);
		gd.setCornerRadii(new float[] { radius, radius, radius, radius, 0, 0,
				0, 0 });
		return gd;
	}

	public static final GradientDrawable getGradientTopDrawable(
			Context context, String color) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.parseColor(color));
		float radius = PixUtil.convertDpToPixel(8, context);
		gd.setCornerRadii(new float[] { radius, radius, radius, radius, 0, 0,
				0, 0 });
		return gd;
	}

	public static final GradientDrawable getGradientLeftDrawable(
			Context context, int color) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(context.getResources().getColor(color));
		float radius = PixUtil.convertDpToPixel(8, context);
		gd.setCornerRadii(new float[] { radius, radius, 0, 0, 0, 0, radius,
				radius });
		return gd;
	}

	public static final GradientDrawable getGradientLeftDrawable(
			Context context, String color) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.parseColor(color));
		float radius = PixUtil.convertDpToPixel(8, context);
		gd.setCornerRadii(new float[] { radius, radius, 0, 0, 0, 0, radius,
				radius });
		return gd;
	}

	public static final GradientDrawable getGradientTestDrawable(
			Context context, int color) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(context.getResources().getColor(color));
		float radius = PixUtil.convertDpToPixel(10f, context);
		gd.setCornerRadii(new float[] { 0, 0, radius, radius, radius, radius,
				radius, radius });
		return gd;
	}

	public static final GradientDrawable getGradientTestDrawable(
			Context context, String color) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.parseColor(color));
		float radius = PixUtil.convertDpToPixel(10f, context);
		gd.setCornerRadii(new float[] { 0, 0, radius, radius, radius, radius,
				radius, radius });
		return gd;
	}
	
	public static final GradientDrawable getGradientTestListDrawable(Context context, String color){
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.parseColor(color));
		return gd;
	}

	public static final GradientDrawable getCornerGradientDrawable(
			Context context, int color, float degree) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(context.getResources().getColor(color));
		float radius = PixUtil.convertDpToPixel(degree, context);
		gd.setCornerRadii(new float[] { radius, radius, radius, radius, radius,
				radius, radius, radius });
		return gd;
	}
	
	public static final GradientDrawable getCornerWithStrokeGradientDrawable(
			Context context, int bg_color, float degree, float stroke_width, int stroke_color) {
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(context.getResources().getColor(bg_color));
		float radius = PixUtil.convertDpToPixel(degree, context);
		gd.setCornerRadii(new float[] { radius, radius, radius, radius, radius,
				radius, radius, radius });
		int swidth = PixUtil.convertDpToPixel(stroke_width, context);
		gd.setStroke(swidth, context.getResources().getColor(stroke_color));
		return gd;
	}

}
