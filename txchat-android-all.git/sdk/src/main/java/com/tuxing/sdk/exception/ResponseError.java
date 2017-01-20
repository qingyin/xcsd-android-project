package com.tuxing.sdk.exception;

/**
 * Created by Alan on 2015/6/9.
 */
public class ResponseError extends Exception {
    private String url;
    private int status;

    public ResponseError(String url, int status, String msg) {
        super(msg);
        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return status;
    }
}