package com.tuxing.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 可以监听滚动的ScrollView
 */
public class ScrollListenerView extends ScrollView {

    private OnScrollListener mListener;

    public ScrollListenerView(Context context) {
        super(context);
    }

    public ScrollListenerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollListenerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mListener != null){
            mListener.onScroll(l, t, oldl, oldt);
        }
    }

    public void setOnScrollListener(OnScrollListener mListener){
        this.mListener = mListener;
    }

    public interface OnScrollListener{
        void onScroll(int l, int t, int oldl, int oldt);
    }
}
