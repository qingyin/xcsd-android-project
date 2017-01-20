package com.tuxing.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.tuxing.app.R;
import com.tuxing.app.activity.RadaActivity;
import com.tuxing.app.util.Utils;

/**
 * Created by alan on 15/12/16.
 */
public class NewScrollView extends ScrollView {
    boolean load1 = false;
    boolean load2 = false;
    boolean load3 = false;

    public NewScrollView(Context context) {
        super(context);
    }

    public NewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (t>=250){
            if (!load1){
                RadaActivity.instance.setProgressNext();
                load1 = true;
            }
        }

        if (t>=700){
            if (!load2){
                RadaActivity.instance.setProgressNexts();
                load2 = true;
            }
        }
        if (t>=1200){
            if (!load3){
                RadaActivity.instance.setProgressEnd();
                load3 = true;
            }
        }
        if (t>=2000){

        }
        Log.d("XXXXXXXXXXXXXX",t+"");
    }
}
