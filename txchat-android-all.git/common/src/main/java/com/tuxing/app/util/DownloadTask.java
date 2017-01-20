package com.tuxing.app.util;

import android.os.AsyncTask;
import com.tuxing.sdk.utils.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class DownloadTask  extends AsyncTask<Void, Long, Void>{
	private static final int mConnectTimeout = 15000; // default value
	private static final int mReadTimeout = 20000; // default value
	private DownloadTaskListener mLlistener = null;
	public final static int BUFFER_SIZE = 2 * 1024;
	private String TAG = DownloadTask.class.getSimpleName();

	public static final Executor DOWNLOAD_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(2, 10, 1,
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(256),
			new ThreadFactory() {
				private final AtomicLong mCount = new AtomicLong(1);

				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "download-task-" + mCount.getAndIncrement());
				}
			});

	private String mUrl;
	private String mFilePath;
	String tempPath;
	
	private int result = 0;
	
	public DownloadTask(String url, String filePath ,DownloadTaskListener listener){
		this.mUrl = url;
		this.mFilePath = filePath;
		this.mLlistener = listener;
		tempPath = SysConstants.FILE_DIR_TEMP + Utils.getImageToken(mUrl)+ "_min";

		if(!new File(SysConstants.FILE_DIR_ROOT).exists() ||
				!new File(tempPath).exists()){
			try {
				new File(SysConstants.FILE_DIR_ROOT).mkdirs();
				new File(SysConstants.FILE_DIR_TEMP).mkdirs();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onPreExecute(){
		if (mLlistener != null) {
			mLlistener.onStartDownload();
		}
	}
	
	@Override
	protected Void doInBackground(Void... objects) {
		InputStream inputStream = null;
		FileOutputStream mResponseFileStream = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			ClientConnectionManager mgr = httpClient.getConnectionManager();
			HttpParams params = httpClient.getParams();
			httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

			HttpGet httpGet = new HttpGet(mUrl);
			httpGet.setHeader("Accept","*/*");
			httpGet.setHeader("Connection", "close");

			httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, mConnectTimeout);
			httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, mReadTimeout);
		
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if(responseCode == 200){
				inputStream = httpResponse.getEntity().getContent();
				long total = httpResponse.getEntity().getContentLength();
				
			
				mResponseFileStream = new FileOutputStream(tempPath);
				
				long current = 0;
				
				int bytesRead = 0;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
					mResponseFileStream.write(buffer, 0, bytesRead);
					mResponseFileStream.flush();
					current += bytesRead;
					publishProgress(current,total);
				}
				
				mResponseFileStream.close();
				
				inputStream.close();
				
				result = 1;
			}
		} catch (ClientProtocolException e) {
			result = 0;
			MyLog.getLogger(TAG).d("mUrl = " + mUrl + "下载图片失败 msg= " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			result = 0;
			MyLog.getLogger(TAG).d("mUrl = " + mUrl +"下载图片失败 msg= " + e.toString());
			e.printStackTrace();
		}catch (Exception e) {
			result = 0;
			MyLog.getLogger(TAG).d("mUrl = " + mUrl +"下载图片失败 msg= " + e.toString());
			e.printStackTrace();
		}finally {
			IOUtils.safeClose(inputStream);
			IOUtils.safeClose(mResponseFileStream);
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		long current = (Long) values[0];
		long total = (Long) values[1];
		if (mLlistener != null) {
			mLlistener.onProgress(current, total);
		} 
	}
	
	@Override
	protected void onCancelled() {
		delete(new File(tempPath));
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Void o) {
		if (mLlistener != null) {
			if(result == 1){
				Utils.copyFile(tempPath, mFilePath);
			}
			mLlistener.onFinished(result, mFilePath);
			delete(new File(tempPath));
		}
	}
	
	
	public static void delete(File file) {  
		  if (file.isFile()) {
		      file.delete();
		      return;
		  }  
	}

}
