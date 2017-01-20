package com.tuxing.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 可以全部显示的gridview
 * 
 */
public class MyGridView extends GridView {

	private OnTouchInvalidPositionListener mTouchInvalidPosListener;
	public MyGridView(Context context) {
		// TODO Auto-generated method stub
		super(context);
	}

	public MyGridView(Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		super(context, attrs);
	}

	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
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
	
	
	
	
	  public interface OnTouchInvalidPositionListener {
		    /**
		     * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者 MotionEvent.ACTION_UP等来按需要进行判断
		     * @return 是否要终止事件的路由
		     */
		    boolean onTouchInvalidPosition(int motionEvent);
		  }
		  /**
		   * 点击空白区域时的响应和处理接口
		   */
		  public void setOnTouchInvalidPositionListener(OnTouchInvalidPositionListener listener) {
		    mTouchInvalidPosListener = listener;
		  }
		  
		  @Override
		public boolean onTouchEvent(MotionEvent event) {
			  if(mTouchInvalidPosListener==null){
				  return super.onTouchEvent(event);
			  }
			  if (!isEnabled()) {
			      // A disabled view that is clickable still consumes the touch
			      // events, it just doesn't respond to them.
			      return isClickable() || isLongClickable();
			    }
			  
			  final int motionPosition = pointToPosition((int)event.getX(), (int)event.getY());
			    if( motionPosition == INVALID_POSITION ) {
			      super.onTouchEvent(event);
			      return mTouchInvalidPosListener.onTouchInvalidPosition(event.getActionMasked());
			    }
			    return super.onTouchEvent(event);
		}
		  @Override
		    public boolean dispatchTouchEvent(MotionEvent ev) {
		        if(ev.getAction() == MotionEvent.ACTION_MOVE){
		            return true;
		        }
		        return super.dispatchTouchEvent(ev);
		    }
		  
}