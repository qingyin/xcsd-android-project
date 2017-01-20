package com.tuxing.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyListView extends ListView {
	/**
	 * ScrollVIew嵌套listview
	 * @author zhaomeng
	 * 
	 */
	public MyListView(Context context) {
		// TODO Auto-generated method stub
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		super(context, attrs);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		// TODO Auto-generated method stub
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	                //下面这句话是关键
	if (ev.getAction()==MotionEvent.ACTION_MOVE) {
	return true;
	}
	return super.dispatchTouchEvent(ev);//也有所不同哦
	}
}