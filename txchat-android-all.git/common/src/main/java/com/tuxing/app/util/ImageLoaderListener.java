package com.tuxing.app.util;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoaderListener implements ImageLoadingListener {
	private String TAG;

	public ImageLoaderListener(String TAG){
		this.TAG = TAG;
	}

	@Override
	public void onLoadingStarted(String url, View view) {}

	@Override
	public void onLoadingFailed(String url, View view, FailReason failReason) {
        String errorMsg = "";
        switch (failReason.getType()) {
            case IO_ERROR:
                errorMsg = "Input/Output error";
                break;
            case DECODING_ERROR:
                errorMsg = "Image can't be decoded";
                break;
            case NETWORK_DENIED:
                errorMsg = "Downloads are denied";
                break;
            case OUT_OF_MEMORY:
                errorMsg = "Out Of Memory error";
                break;
            case UNKNOWN:
                errorMsg = "Unknown error";
                break;
        }
		MyLog.getLogger(TAG).d("加载图片失败  mUrl = " + url + "  msg = " + errorMsg);
	}

	@Override
	public void onLoadingComplete(String url, View view, Bitmap bitmap) {
//		MyLog.getLogger(TAG).d("加载图片成功  mUrl = " + url);
	}

	@Override
	public void onLoadingCancelled(String url, View view) {}
}
