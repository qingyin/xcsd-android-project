package com.tuxing.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tuxing.app.R;
import com.tuxing.app.util.Utils;

/**
 * Created by alan on 15/12/16.
 */
@SuppressLint("DrawAllocation")
public class ArcView extends View{
    private static final float ARC_HEIGHT_DP = 13f;
    private Context mContext;
    private float arcHeight;

    public ArcView(Context context) {
        super(context);
        init(context);
    }

    public ArcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        setFocusable(true);
        setFocusableInTouchMode(true);
        mContext = context;
        arcHeight = Utils.dip2px(mContext, ARC_HEIGHT_DP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        float r = ((width / 2) * (width / 2) + arcHeight * arcHeight) / (arcHeight * 2);
        float angle = (float) (Math.asin((width / 2) / r) * 180 / Math.PI);
        RectF rect2 = new RectF(width / 2 - r, height - arcHeight, r + width / 2, height - arcHeight + r * 2);
        canvas.drawArc(rect2, 270 - angle, angle * 2, true, paint);

        float strokeWidth = Utils.dip2px(mContext, 2.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.arc_separator_color));
        paint.setStrokeWidth(strokeWidth);

        canvas.drawArc(rect2, 270 - angle, angle * 2, true, paint);
    }
}
