package com.tuxing.app.Radar;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
/**
 *
 * 
 * @author Android自定义View——实现理财类APP七日年化收益折线图效果
 *
 *相关博客地址 http://blog.csdn.net/qq_25193681
 */
public class AnnualyieldView extends View {
	private Paint mTextPaint, mLinePaint,mPathPaint,mPointPaint;

	private float mPaintRectWidth;

	private Path mPath;

	private float mWidth, mHeight;

	private final float mCount = 6;

	private final float offsets=1;

	private float mRectHeight;

	private List<Float> xline=new ArrayList<Float>();
	private List<Float> yline=new ArrayList<Float>();


//	private float []x={2.46f,2.45f,2.44f,2.43f,2.42f,2.41f,2.40f};
//	private float []x={100f,150f,200f,250f,300f,350f,400f};
	private float []x={600f,500f,400f,300f,200f,100f,0f};

	public AnnualyieldView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		initView();

	}

	public AnnualyieldView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView();
	}

	public AnnualyieldView(Context context) {
		super(context);

		initView();
	}

	private void initView() {
		//绘制线和文字的颜色
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.parseColor("#cccccc"));
		mTextPaint.setTextSize(25);
		mTextPaint.setStrokeWidth(1);

		//绘制折线图的点
		mPointPaint= new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(Color.parseColor("#000000"));
		mPointPaint.setTextSize(25);
		mPointPaint.setStrokeWidth(5);
		//绘制柱状图的画笔
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		//绘制折线图的画笔
		mPathPaint= new Paint();
		mPathPaint.setAntiAlias(true);
		mPathPaint.setColor(Color.parseColor("#fda220"));
		mPathPaint.setStyle(Style.STROKE);
		//折线图的路径
		mPath=new Path();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth=(float)(getWidth()-getWidth()*0.1);
		mHeight=(float)(getHeight()-getHeight()*0.1);
		mRectHeight=(float)(getHeight()-getHeight()*0.1);
		mPaintRectWidth=(float) (mWidth*0.8/mCount);
		mLinePaint.setStrokeWidth(mPaintRectWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		onDrawRect(canvas);		
		onDrawLine(canvas);
		canvasPath(canvas);
	}
	//绘制6个矩形
	private void onDrawRect(Canvas canvas) {
		for (int i = 0; i < 7; i++) {
//			if (i%2==0) {
//				mLinePaint.setColor(Color.parseColor("#eeeeee"));
//			}else {
//				mLinePaint.setColor(Color.parseColor("#ece1f3"));
//			}
			mLinePaint.setColor(Color.parseColor("#ffffff"));
			float left  =(float) (mWidth * 0.4 / 2 + mPaintRectWidth * i + offsets);
			float right=(float) (mWidth * 0.4 / 2 + mPaintRectWidth* (i + 1));
//			canvas.drawRect(left,(float)(mRectHeight*0.01),right, mHeight, mLinePaint);
		}

	}
	//绘制网格线
	private void onDrawLine(Canvas canvas){
		//第一条线
//		canvas.drawLine(mPaintRectWidth-mPaintRectWidth/2, (float)(mRectHeight*0.01), getWidth(),  (float)(mRectHeight*0.01), mTextPaint);

		float height;
		//横七条
		for (float i = 0; i < 7; i++) {
			//从上到下
			if (i==0) {
				height=i;
			}else {
				height=mRectHeight*(i/6);
				float size=mTextPaint.measureText(x[(int)i]+"");
				//绘制线
				if(i==6){
				canvas.drawLine(mPaintRectWidth+mPaintRectWidth/2, height, getWidth(),  height, mTextPaint);
				}
				//绘制左边Y轴的文字
				canvas.drawText(x[(int)i]+"", (float)(mPaintRectWidth-mPaintRectWidth*0.35), height+size/5, mTextPaint);
			}

		}
		//竖七条
//		canvas.drawLine((float) (mPaintRectWidth-mPaintRectWidth/2),0, (float) (mPaintRectWidth-mPaintRectWidth/2),  mHeight, mTextPaint);
		for (float i = 0; i < 7; i++) {
			//从左到右
			if(i==0){
				canvas.drawLine((float) (mWidth * 0.4 / 2 + mPaintRectWidth * i),0, (float) (mWidth * 0.4 / 2 + mPaintRectWidth * i),  mHeight, mTextPaint);
			}
			//绘制底边的日期文字
			canvas.drawText("07-0"+((int)i+1), (float) (mWidth * 0.34 / 2 + mPaintRectWidth * i), (float)(mHeight+mHeight*0.1), mTextPaint);
			//准备好下面折线图的X轴坐标
			xline.add((float) (mWidth * 0.4 / 2 + mPaintRectWidth * i));
		}
		//折线图的第一个点
		xline.add((float) (mPaintRectWidth-mPaintRectWidth/2));



	}
	//绘制折线路径
	public void canvasPath(Canvas canvas){


		for (int j = 0; j < yline.size(); j++) {

			float x=xline.get(j);
			float y =yline.get(j);
			float aftery=mRectHeight-Line_Math.initData(y);
//			float aftery=y;
			if (j==0) {
				mPath.moveTo(x,aftery );
			}else{

				mPath.lineTo(x,aftery );
			}
			canvas.drawPoint(x, aftery, mPointPaint);
			float size=mPointPaint.measureText(y+"");
			canvas.drawText(y+"", (float)(x-size/2), (float)(aftery+size*0.25), mPointPaint);
		}
		canvas.drawPath(mPath, mPathPaint);
	}
	//用于设置Y轴的坐标值
	public void setDataY( List<Float> yline) {
		this.yline.clear();
		this.yline=yline;
	}
	//一个更新UI的方法
	public void invalidata(){
		invalidate();
	}


}
