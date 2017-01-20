package com.tuxing.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.util.Log;

public class NetUtil {

	private static final String TAG = NetUtil.class.getName();
	private static final int LONG_TIMEOUT = 10000;
	public static final int TIMEOUT = 6000;
	public static final int READTIMEOUT = 10000;
	private static final String HTTP_ERROR = "http_error";

	/**
	 * 下载远程文件到指定输出流
	 * 
	 * @param url
	 *            远程文件地址
	 * @param output
	 *            输出流
	 * @return 成功与否
	 */
	public static boolean downloadFile(String urlStr, OutputStream output) {
		InputStream input = null;
		boolean down_success = false;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(READTIMEOUT);
			conn.connect();
			input = conn.getInputStream();

			byte[] buffer = new byte[1024];
			int count;

			while ((count = input.read(buffer)) > 0) {
				output.write(buffer, 0, count);
			}
			down_success = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return down_success;
	}

	public static String syncGetContent(final Context context, String url) {

//		url = "http://121.41.101.14:8082/service/service/testlist/json?schoolAge=2&childID=2";//错误的请求
		String result = null;
		RequestParams params = null;
		final String[] reponse = {null};
		SyncHttpClient mSyncClient = new SyncHttpClient();
		final RequestHandle requestHandle = mSyncClient.get(context, url, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				//super.onSuccess(statusCode, headers, response);
				reponse[0] = response.toString();
				Log.i(DEFAULT_CHARSET, "——————————————————7777————————————-测评联网成功了");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
				//super.onFailure(statusCode, headers, e, errorResponse);
				reponse[0] = "测评失败";
				Log.i(DEFAULT_CHARSET, "——————————————————7777————————————-测评联网失败了");
			}

		});
//		SyncHttpClient client = new SyncHttpClient() {
//			@Override
//			public String onRequestFailed(Throwable error, String content) {
//				Log.e(TAG, "", error);
//				return HTTP_ERROR;
//			}
//		};
//		client.addHeader("Content-Type", "application/json");
//		client.setTimeout(TIMEOUT);
//		String content = client.get(url);
//		if (!StringUtil.isNullOrEmpty(content) && !content.equals(HTTP_ERROR)) {
//			result = content;
//		}
		result = reponse[0];
		return result;
	}

	public static String syncPostContentWithLongTimeOut(Context context,
			String url, RequestParams params) {
		return syncPost(context, url, params, LONG_TIMEOUT);
	}

	public static String syncPostContent(Context context, String url,
			RequestParams params) {
		return syncPost(context, url, params, TIMEOUT);
	}

	private static String syncPost(final Context context, String url,
			RequestParams params, int time_out) {
		String result = null;
		final String[] reponse = {null};
//		SyncHttpClient client = new SyncHttpClient() {
//			@Override
//			public String onRequestFailed(Throwable error, String content) {
//				Log.e(TAG, "", error);
//				return HTTP_ERROR;
//			}
//		};
//		client.addHeader("Content-Type", "application/json");
//		client.setTimeout(time_out);
//		String content = client.post(url, params);
//		if (!StringUtil.isNullOrEmpty(content) && !content.equals(HTTP_ERROR)) {
//			result = content;
//		}

		final SyncHttpClient mSyncClient = new SyncHttpClient();
//		final RequestHandle requestHandle = mSyncClient.get(context, url, params, new JsonHttpResponseHandler() {
		final RequestHandle requestHandle = mSyncClient.post(context, url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				super.onSuccess(statusCode, headers, responseString);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
//		mSyncClient.get("http://example.com", params, new JsonHttpResponseHandler() {
//			@Override
//			public void onStart() {
//				// you can do something here before request starts
//			}
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//				// success logic here
//			}
//
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
//				// handle failure here
//			}
//
//		});

		result = reponse[0];
		return result;
	}
	
	public static String clientSyncDelete(String url){
		String result = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
		HttpDelete httpDelete = new HttpDelete(url);
		httpDelete.addHeader("Content-Type", "text/plain");
		try {
			HttpResponse response = httpClient.execute(httpDelete);
			StringBuilder res_str = inputStreamToString(response.getEntity()
					.getContent());
			if (!StringUtil.isNullOrEmpty(res_str.toString())) {
				result = res_str.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String clientSyncPost(String url, String json) {
		String result = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/json");
		try {
			StringEntity se = new StringEntity(json, HTTP.UTF_8);
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			StringBuilder res_str = inputStreamToString(response.getEntity()
					.getContent());
			if (!StringUtil.isNullOrEmpty(res_str.toString())) {
				result = res_str.toString();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static StringBuilder inputStreamToString(InputStream is)
			throws IOException {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		while ((line = rd.readLine()) != null) {
			total.append(line);
		}

		// Return full string
		return total;
	}

}
