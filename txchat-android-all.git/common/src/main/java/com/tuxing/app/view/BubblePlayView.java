package com.tuxing.app.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.Utils;

import java.lang.reflect.Field;

/**
 * Created by mingwei on 1/13/16.
 */
public class BubblePlayView extends ViewGroup {

    private int mDefaultHeight = 50;

    private int mDefaultWidth = 56;

    private ImageView mCDImg;

    private ImageView mCDImgReplace;

    private int mOffsetOut = 5;

    private int mOffsetIn = 2;

    private int mArcWidth = 3;

    private int mArcColor = Color.GRAY;

    private int mCDImgCentX;

    private int mCDImgCentY;

    private int mRdius;
    /**
     * 进度条圆弧相关
     */
    private Paint mArcPaint = new Paint();

    private RectF mArcRectF = new RectF();

    private float mStart = 270f;

    private float mSweep;

    private ValueAnimator mAnimator;
    /**
     * WindownManager相关
     */
    private WindowManager.LayoutParams mParams;

    private WindowManager mManager;

    private float mX;

    private float mY;

    private float mStartX;

    private float mStartY;

    private int mStatusH;

    private int x;

    private int y;

    private String PREFERENCE_NAME = "preference";

    private String PREFERENCE_SAVED = "issaved";

    private String PREFERENCE_X = "position_x";

    private String PREFERENCE_Y = "position_y";

    public BubblePlayView(Context context) {
        this(context, null);
    }

    public BubblePlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubblePlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDefaultHeight = Utils.dip2px(context, mDefaultHeight);
        mDefaultWidth = Utils.dip2px(context, mDefaultWidth);
        init(context, attrs, defStyleAttr);
        mCDImg = new ImageView(context);
        mCDImgReplace = new ImageView(context);
        addView(mCDImg);
        addView(mCDImgReplace);
        this.setBackgroundResource(R.drawable.bubble_play_bg);
        setup();
        mStatusH = getStatusBarHeight(context);
        x = context.getResources().getDisplayMetrics().widthPixels - mDefaultWidth + 60;
        y = context.getResources().getDisplayMetrics().widthPixels + mStatusH - 150;
        if (isSaved()) {
            int xy[] = load(x, y);
            initManager(xy[0], xy[1]);
        } else {
            initManager(x, y);
        }
        initAnimation();

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BubblePlay, defStyleAttr, 0);
        mOffsetOut = array.getDimensionPixelOffset(R.styleable.BubblePlay_bubble_outer_width, mOffsetOut);
        mOffsetIn = array.getDimensionPixelOffset(R.styleable.BubblePlay_bubble_inner_width, mOffsetIn);
        mArcWidth = array.getDimensionPixelOffset(R.styleable.BubblePlay_bubble_arc_width, mArcWidth);
        mArcColor = array.getColor(R.styleable.BubblePlay_bubble_arc_color, mArcColor);
        array.recycle();
    }

    public void setup() {
        /**
         * 进度条圆弧画笔初始化
         */
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(mArcColor);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureW(widthMeasureSpec), measureH(heightMeasureSpec));
        int height = getMeasuredHeight();
        mRdius = (height - (mOffsetOut + mOffsetIn + mArcWidth) * 2) / 2;
        mCDImgCentX = height / 2;
        mCDImgCentY = height / 2;
        int offset = mRdius + mArcWidth / 2 + mOffsetIn;
        mArcRectF.set(mCDImgCentX - offset, mCDImgCentY - offset, mCDImgCentX + offset, mCDImgCentY + offset);
    }

    protected int measureW(int wms) {
        int r;
        int sm = MeasureSpec.getMode(wms);
        int ss = MeasureSpec.getSize(wms);
        int defaultS = mDefaultWidth;
        if (sm == MeasureSpec.EXACTLY) {
            r = ss;
        } else {
            r = defaultS;
            if (sm == MeasureSpec.AT_MOST) {
                r = Math.min(defaultS, ss);
            }
        }
        return r;
    }

    protected int measureH(int wms) {
        int r;
        int sm = MeasureSpec.getMode(wms);
        int ss = MeasureSpec.getSize(wms);
        int defaultS = mDefaultHeight;
        if (sm == MeasureSpec.EXACTLY) {
            r = ss;
        } else {
            r = defaultS;
            if (sm == MeasureSpec.AT_MOST) {
                r = Math.min(defaultS, ss);
            }
        }
        return r;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mCDImg.layout(mCDImgCentX - mRdius, mCDImgCentY - mRdius, mCDImgCentX + mRdius, mCDImgCentY + mRdius);
        mCDImgReplace.layout(mCDImgCentX - mRdius, mCDImgCentY - mRdius, mCDImgCentX + mRdius, mCDImgCentY + mRdius);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mX = e.getRawX();
        mY = e.getRawY() - mStatusH;
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = e.getX();
                mStartY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //mParams.x = (int) (mX - mStartX);
                mParams.x = x;
                mParams.y = (int) (mY - mStartY);
                mManager.updateViewLayout(this, mParams);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                save(mParams.x, mParams.y);
                save();
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.drawArc(mArcRectF, mStart, mSweep, false, mArcPaint);
        canvas.restore();
    }

    public void setProgressPercent(float p) {
        if (p >= 0f && p <= 1.0f) {
            mSweep = p * 360;
            requestLayout();
        } /*else {
            throw new IllegalArgumentException("p must >=0f && p<=1.0f");
        }*/
    }

    public void setOffsetOuter(int out) {
        mOffsetOut = out;
        setup();
        requestLayout();
    }

    public void setOffsetInner(int inner) {
        mOffsetIn = inner;
        setup();
        requestLayout();
    }

    public void setArcWidth(int arc) {
        mArcWidth = arc;
        setup();
        requestLayout();
    }

    public void setArcColor(int color) {
        mArcColor = color;
        setup();
        requestLayout();
    }

    public void startRotation() {
        mAnimator.start();
        mCDImgReplace.setVisibility(View.GONE);
        mCDImgReplace.setRotation(mCDImg.getRotation());
    }

    public void initAnimation() {
        mAnimator = ValueAnimator.ofFloat(0f, 360f);
        mAnimator.setDuration(10000);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(Integer.MAX_VALUE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (Float) animation.getAnimatedValue();
                mCDImg.setRotation(v);
            }
        });
    }

    public void stopRotation() {
        //mAnimator.end();
        mCDImgReplace.setVisibility(View.VISIBLE);
    }


    public void setCDImg(int drawable) {
        mCDImg.setImageDrawable(getResources().getDrawable(drawable));
    }

    public void setDisplayImage(String url) {

        ImageLoader.getInstance().displayImage(url, mCDImg, ImageUtils.DIO_BUBBLE);
        ImageLoader.getInstance().displayImage(url, mCDImgReplace, ImageUtils.DIO_BUBBLE);
    }

    public void setCDImg(Drawable drawable) {
        mCDImg.setImageDrawable(drawable);
    }

    public ImageView getCDImg() {
        return mCDImg;
    }

    private void initManager(int x, int y) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = x;
        mParams.y = y;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public boolean isManager() {
        return mManager != null;
    }

    public void show() {
        if (isManager()) {
            if (this.getParent() == null) {
                mManager.addView(this, mParams);
            }
        }
    }

    public void dismiss() {
        if (isManager()) {
            if (this.getParent() != null) {
                mManager.removeView(this);
            }
        }
    }

    public void save(int x, int y) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREFERENCE_X, x);
        editor.putInt(PREFERENCE_Y, y);
        editor.commit();
    }

    public int[] load(int defaultx, int defaulty) {
        int[] xy = new int[2];
        SharedPreferences share = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
        xy[0] = share.getInt(PREFERENCE_X, defaultx);
        xy[1] = share.getInt(PREFERENCE_Y, defaulty);
        return xy;
    }

    public boolean isSaved() {
        SharedPreferences share = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
        return share.getBoolean(PREFERENCE_SAVED, false);
    }

    public void save() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFERENCE_SAVED, true);
        editor.commit();
    }

    public static int getStatusBarHeight(Context c) {
        int h = 0;
        try {
            Class<?> z = Class.forName("com.android.internal.R$dimen");
            Object o = z.newInstance();
            Field f = z.getField("status_bar_height");
            int x = (Integer) f.get(o);
            h = c.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }
}