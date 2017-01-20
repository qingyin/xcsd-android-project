package com.tuxing.app.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by wangst on 15-10-27.
 */
public class MyCheckBox extends CheckBox {

    public MyCheckBox(Context context) {
        super(context);
    }

    public MyCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getCompoundPaddingLeft() {
        int padding = super.getCompoundPaddingLeft();
        if(Build.VERSION.SDK_INT<=16){//4.1.2
//            if (!isLayoutRtl()) {
//                padding = selfHeight + padding;
//            }
            padding = 50;
        }
        return padding;
    }
}
