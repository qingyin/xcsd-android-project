package com.tuxing.sdk.http;

import java.io.IOException;

/**
 * Created by apple on 16/8/5.
 */
public interface MyRequestCallback {
    public abstract void onResponse(final String data) throws IOException;
    public abstract void onFailure(Throwable cause);
}
