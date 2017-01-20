package com.tuxing.app.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.polites.android.GestureImageView;

public class MyViewPager extends ViewPager {

	public MyViewPager(Context context) {
		super(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	 public boolean onTouchEvent(MotionEvent ev) {
		
		 boolean b =  super.onTouchEvent(ev);
		 return b;
	 }
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		MyImageAdapter adapter = (MyImageAdapter)this.getAdapter();
		GestureImageView imageView = (GestureImageView)adapter.getItem(this, this.getCurrentItem());

		int action = ev.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			if(imageView.imageCanDragX()){
				return false;
			}
			break;
			
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}

		return super.onInterceptTouchEvent(ev);
		
	}
	
	public static abstract class MyImageAdapter extends PagerAdapter {
		public abstract Object getItem(ViewPager viewPager, int pos);
	}

}
