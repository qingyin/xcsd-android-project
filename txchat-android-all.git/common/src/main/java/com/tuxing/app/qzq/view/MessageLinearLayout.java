package com.tuxing.app.qzq.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.tuxing.app.R;
import com.tuxing.app.qzq.util.OnHideKeyboardListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MessageLinearLayout extends LinearLayout {

	private Context mContext;
	private OnHideKeyboardListener mListener;
	private static final int SOFTKEYPAD_MIN_HEIGHT = 300; 
	public void setListener(OnHideKeyboardListener listener) {
		mListener = listener;
	}

	public MessageLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public MessageLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public MessageLinearLayout(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		// TODO Auto-generated method stub
		if (mContext != null) {
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				// 释放焦点
				for (int i = 0; i < getChildCount(); i++) {
					View view = getChildAt(i);
					EditText editText1 = (EditText) view.findViewById(R.id.et_send);
					if(editText1 != null && editText1.isFocused()){
						if(mListener!= null){
							mListener.onHide();
							return true;
						}
					}
				}
			}
		}
		return super.dispatchKeyEventPreIme(event);
	}
	
//	protected void onSizeChanged(int w,final int h, int oldw,final int oldh) {
//            if (oldh - h > SOFTKEYPAD_MIN_HEIGHT){
//            	 if(mListener != null){
//                 }
//            }else if( mListener != null && oldh - h > 0 && h - oldh > SOFTKEYPAD_MIN_HEIGHT){
//            }else if(mListener != null && oldh - h < -400){
//            }
//            super.onSizeChanged(w, h, oldw, oldh);
//    }
}
