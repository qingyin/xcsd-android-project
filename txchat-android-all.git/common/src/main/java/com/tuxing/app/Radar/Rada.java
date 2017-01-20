package com.tuxing.app.Radar;

/**
 * Created by bryant on 16/7/12.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 雷达图
 * Created by Owen on 2016/1/12.
 */
public class Rada extends View {

    /** 绘制多边形的画笔 */
    private Paint mPaintPolygon;
    /** 绘制直线的画笔 */
    private Paint mPaintLine;
    /** 绘制分值区的画笔 */
    private Paint mPaintRegion;

    /**
     * 雷达的边数
     */
    private static final int NUM_OF_SIDES = 5;
    /** 角度 */
    private float mAngel = (float) (2 * Math.PI / NUM_OF_SIDES);

    /** 多边形的绘制路径 */
    private Path mPathPolygon;
    /** 多边形的起始半径 */
    private float mStartRadius = 50F;

    /** 直线的绘制路径 */
    private Path mPathLine;

    /** 分值区的绘制路径 */
    private Path mPathRegion;

    /** 中心点 x 轴坐标*/
    private int mCenterX;
    /** 中心点 y 轴坐标 */
    private int mCenterY;

    public Rada(Context context) {
        this(context, null);
    }

    public Rada(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaintPolygon = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPolygon.setStyle(Paint.Style.STROKE);
        mPaintPolygon.setColor(Color.BLACK);
        mPaintPolygon.setStrokeWidth(1F);

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setColor(Color.GRAY);
        mPaintLine.setStrokeWidth(1F);

        mPaintRegion = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRegion.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintRegion.setColor(Color.parseColor("#7f33b5e5"));
        mPaintRegion.setStrokeWidth(1F);

        mPathPolygon = new Path();
        mPathLine = new Path();
        mPathRegion = new Path();
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPolygon(canvas);
        drawLine(canvas);
        drawRegion(canvas);
    }

    /**
     * 绘制分值区
     */
    private void drawRegion(Canvas canvas) {
        float[] percents = new float[]{
                0.4F,
                0.6F,
                0.8F,
                0.5F,
                0.6F,
                0.9F
        };

        float radius = mStartRadius * 5;

        for (int i = 0; i < NUM_OF_SIDES; i++) {
            float x = (float) (Math.cos(mAngel * i) * radius * percents[i]);
            float y = (float) (Math.sin(mAngel * i) * radius * percents[i]);

            if (i == 0) {
                mPathRegion.moveTo(mCenterX + x, mCenterY);
            } else {
                mPathRegion.lineTo(mCenterX + x, mCenterY + y);
            }
        }

        mPathRegion.close();

        canvas.drawPath(mPathRegion, mPaintRegion);
    }

    /**
     * 绘制直线
     */
    private void drawLine(Canvas canvas) {
        float radius = mStartRadius * 5;

        for (int i = 0; i < NUM_OF_SIDES; i++) {
            mPathLine.moveTo(mCenterX, mCenterY);

            float x = (float) (mCenterX + Math.cos(mAngel * i) * radius);
            float y = (float) (mCenterY + Math.sin(mAngel * i) * radius);

            mPathLine.lineTo(x, y);

            canvas.drawPath(mPathLine, mPaintLine);
        }
    }

    /**
     * 绘制多边形
     */
    private void drawPolygon(Canvas canvas) {
        for (int count = 1; count <= 5; count++) {
            float newRadius = mStartRadius * count;

            mPathPolygon.moveTo(mCenterX + newRadius, mCenterY);

            for (int i = 1; i < NUM_OF_SIDES; i++) {
                float x = (float) (mCenterX + Math.cos(mAngel * i) * newRadius);
                float y = (float) (mCenterY + Math.sin(mAngel * i) * newRadius);

                mPathPolygon.lineTo(x, y);
            }

            mPathPolygon.close();

            canvas.drawPath(mPathPolygon, mPaintPolygon);

            mPathPolygon.reset();
        }
    }

}