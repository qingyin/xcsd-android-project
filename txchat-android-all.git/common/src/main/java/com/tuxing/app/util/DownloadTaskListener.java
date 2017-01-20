package com.tuxing.app.util;

public interface DownloadTaskListener {
	public void onStartDownload();
	public void onProgress(long current, long total);
	public void onFinished(int resultCode, String filePath);
}
