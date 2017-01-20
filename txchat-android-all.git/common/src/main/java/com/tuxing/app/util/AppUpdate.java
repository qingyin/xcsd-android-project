package com.tuxing.app.util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.tuxing.sdk.modle.UpgradeInfo;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class AppUpdate {
	private final static String LOG_TAG = AppUpdate.class
			.getSimpleName();
//	更新地址
	private final static String UPDATE_URL = "http://wjydown.tx2010.com.cn/default.aspx";
	private final static int UPDATE_DIALOG = 1000;
	public final static int CONNECTION_TIMEOUT = 30 * 1000;
	
	private Context mContext;
//	下载文件的id
	private long downId;
	
//	传递网路参数
	private HttpParams params = new BasicHttpParams();
	
//	下载管理
	private DownloadManager downManager;
	
//	下载文件
	private File existFile;
	
	public AppUpdate(Context ctx){
		mContext = ctx;
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
//		HttpProtocolParams.setUseExpectContinue(params, false);
		downManager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	private DefaultHttpClient getHttpClient() throws UnknownHostException {
		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		return httpclient;
	}
	
	/**
	 * 
	 * 请求更新软件
	 * 
	 */
	public UpdateInfo appUpdate(Context context) {
		StringBuffer sb = new StringBuffer();
		sb.append(UPDATE_URL);
		sb.append("?versionCode=").append(getVersionName(mContext));
		//sb.append("&versionCode=").append(getVersionCode(mContext));
		//sb.append("&app=").append(getPacakgeName(mContext));
		try {
			sb.append("&sysName=").append(URLEncoder.encode(PreferenceUtils.getPrefString(mContext, SysConstants.userName,""), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			HttpClient client = getHttpClient();
			String result = null;
			HttpGet httpGet = new HttpGet(sb.toString());
			HttpResponse response = client.execute(httpGet);
			if (response != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			}
			response = null;
			httpGet = null;
			Log.w(LOG_TAG, "appUpdate result:" + result);
			if(result != null){
				PreferenceUtils.setPrefString(mContext, SysConstants.UpdateJson,result);
			}
			return new UpdateInfo(new JSONObject(result));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	public File download(UpdateInfo updateInfo) throws UnsupportedEncodingException{
		if (updateInfo != null) {
			if (updateInfo.mustUpdate || updateInfo.isUpdate) {
				existFile = null;
				if (updateInfo.updateUrl != null) {
					String title = URLDecoder.decode(
							updateInfo.updateUrl.substring(
									updateInfo.updateUrl
											.lastIndexOf("/") + 1,
									updateInfo.updateUrl.length()),
							"utf-8");
					if (isDownLoad(title)) {
						return existFile;
					}
					downApk(updateInfo.updateUrl, title,updateInfo.updateMsg);
				}
			}
		}
		
		return null;
	}
	public File download(UpgradeInfo upgradeInfo) throws UnsupportedEncodingException {
		existFile = null;
		if (!android.text.TextUtils.isEmpty(upgradeInfo.getUpgradeUrl())) {
			String title = URLDecoder.decode(
					upgradeInfo.getUpgradeUrl().substring(
							upgradeInfo.getUpgradeUrl()
									.lastIndexOf("/") + 1,
							upgradeInfo.getUpgradeUrl().length()),
					"utf-8");
			if (isDownLoad(title)) {
				return existFile;
			}
			downApk(upgradeInfo.getUpgradeUrl(), title, upgradeInfo.getUpgradeMsg());
		}
		return null;
	}

	public void downloadUrl(String updateUrl) {
		try {
			downApk(updateUrl, "test", "test");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 下载apk
	 * 
	 */
	@SuppressLint("NewApi")
	public void downApk(String apkUrl, String fileName, String description)
			throws UnsupportedEncodingException {
		
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(apkUrl));
		request.setTitle(fileName);
		request.setDescription(description);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// request.allowScanningByMediaScanner();
			// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
		}
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			request.setDestinationInExternalPublicDir(
					Environment.DIRECTORY_DOWNLOADS, fileName);
		}
		try {
			if(downManager != null){
				downId = downManager.enqueue(request);
				PreferenceUtils.setPrefLong(mContext, SysConstants.DownloadId, downId);
			}
		} catch (Exception e) {
			Uri uri = Uri.parse(apkUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			mContext.startActivity(intent);
		}
		
	}
	
	
	/**
	 * 
	 * 判断是否下载了
	 * 
	 */
	
	@SuppressLint("NewApi")
	protected boolean isDownLoad(String title) {
		Query query = new Query();
		query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
		Cursor c = downManager.query(query);
		if(c == null){
			return false;	
		}
		while (c.moveToNext()) {
			if (title.equals(c.getString(c
					.getColumnIndex(DownloadManager.COLUMN_TITLE)))) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					String str = c
							.getString(c
									.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
					if (str == null)
						return false;
					existFile = new File(str);

				} else {
					existFile = new File(
							Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
									+ "/" + title);
				}
				break;
			}
		}
		if(c != null){
			c.close();
		}
		
		return existFile == null ? false : existFile.exists();
	}
	

	
	
	/**
	 * 
	 * 获得包名
	 * 
	 */
	public static String getPacakgeName(Context context) {
		return context.getPackageName();
	}
	/**
	 * 
	 * 获得软件版本 
	 * 
	 */
	public static String getVersionName(Context context) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionName;
		} catch (NameNotFoundException e) {
		}
		return "";
	}
	/**
	 * 
	 *获得软件code 
	 * 
	 */
	public static int getVersionCode(Context context) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
		}
		return 0;
	}
	
	/**
	 * 
	 *安装软件 
	 * 
	 */
	public static void setupApk(Context context, File file) {
		if (file.exists()) {
			Intent intentApk = new Intent(Intent.ACTION_VIEW);
			intentApk.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentApk.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentApk.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			context.startActivity(intentApk);
		}
	}
	
	/**
	 * 
	 * 下载对象
	 * 
	 */
	public static class UpdateInfo {
		public boolean showAtMain;
		public boolean mustUpdate;
		public boolean isUpdate;
		public String showMsg;
		public String updateMsg;
		public String updateUrl;
		public String versionCode;
		
	
		public UpdateInfo(JSONObject jsonObject) {
			
			try {
				showAtMain = jsonObject.getBoolean("showAtMain");
			} catch (JSONException e) {

			}
			
			try {
				mustUpdate = jsonObject.getBoolean("mustUpdate");
			} catch (JSONException e) {

			}
			try {
				versionCode = jsonObject.getString("versionCode");
			} catch (JSONException e) {

			}
			try {
				isUpdate = jsonObject.getBoolean("isUpdate");
			} catch (JSONException e) {

			}
			try {
				showMsg = jsonObject.getString("showMsg");
			} catch (JSONException e) {

			}
			try {
				updateMsg = jsonObject.getString("updateMsg");
			} catch (JSONException e) {

			}
			try {
				updateUrl = jsonObject.getString("updateUrl");
			} catch (JSONException e) {

			}
		}
	}
}
