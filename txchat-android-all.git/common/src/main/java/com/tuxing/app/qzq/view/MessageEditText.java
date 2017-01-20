package com.tuxing.app.qzq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.tuxing.app.qzq.util.OnHideKeyboardListener;

public class MessageEditText extends EditText {

    private Context mContext;
    private OnHideKeyboardListener mListener;


    public MessageEditText(Context context) {
        super(context);
        this.mContext = context;
    }

    public MessageEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public MessageEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void setListener(OnHideKeyboardListener listener) {
        mListener = listener;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (mContext != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                // 释放焦点
                if (mListener != null) {
                    mListener.onHide();
                    return true;
                }
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }
}
