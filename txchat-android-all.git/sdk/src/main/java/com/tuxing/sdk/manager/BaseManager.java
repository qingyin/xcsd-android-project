package com.tuxing.sdk.manager;

import android.content.Context;

/**
 * Created by Alan on 2015/5/17.
 */
public interface BaseManager {
    public void init(Context context);
    
    public abstract void destroy();
}
