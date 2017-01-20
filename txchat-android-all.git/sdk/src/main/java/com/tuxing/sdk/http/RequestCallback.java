package com.tuxing.sdk.http;


import java.io.IOException;

/**
 * Created by Alan on 2015/5/19.
 */
public interface RequestCallback {

    public abstract void onResponse(byte[] data) throws IOException;

    public abstract void onFailure(Throwable cause);
}
