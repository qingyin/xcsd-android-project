package com.tuxing.app.helper;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;
import com.tuxing.app.R;
import com.tuxing.app.util.NetUtil;
import com.tuxing.app.util.StringUtil;
import com.tuxing.app.util.ToastUtil;
import com.tuxing.app.view.ProgressHUD;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public abstract class NetManager {

	public static final int REQUEST_SUCCESS = 0;
	public static final int TOKEN_EXPIRED_CODE = 600;
	public static final int NET_ERROR = 1;
	public static final int API_ERROR = 2;
	public static final int TOKEN_EXPIRED = 9;
	private static final String TAG = NetManager.class.getName();
	public static final String GET_SUCCESS = "Get_Success";
	public static final String POST_SUCCESS = "Post_Success";

	public interface MonitorInterface {
		public void getContentFromNet(Object content);
	}

//	public static boolean checkNetState(Context context) {
//		if (!TripEducationApplication.AppInstance.getNetworkBean().isConnected) {
//			PromptDialog dialog = new PromptDialog(context, R.style.MyDialog);
//			dialog.setContent(R.string.no_net);
//			dialog.setCancelable(false);
//			dialog.show();
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	public static boolean checkNetStateWithoutPrompt(Context context) {
//		if (!TripEducationApplication.AppInstance.getNetworkBean().isConnected) {
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	protected static boolean checkNetStateOnRefresh(Context context) {
//		if (!TripEducationApplication.AppInstance.getNetworkBean().isConnected) {
//			ToastUtil.show(context, R.string.no_net);
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	public static void showPromptDialog(Context context, int string) {
//		PromptDialog dialog = new PromptDialog(context, R.style.MyDialog);
//		dialog.setContent(string);
//		dialog.show();
//	}
//
//	protected static void showPromptDialog(Context context, int string,
//			PromptDialog.OnClickConfirmListener listener) {
//		PromptDialog dialog = new PromptDialog(context, R.style.MyDialog,
//				listener);
//		dialog.setContent(string);
//		dialog.setCancelable(false);
//		dialog.show();
//	}
//
//	protected static void showPromptDialog(Context context, int string,
//			boolean is_cancelable, PromptDialog.OnClickConfirmListener listener) {
//		PromptDialog dialog = new PromptDialog(context, R.style.MyDialog,
//				listener);
//		dialog.setContent(string);
//		dialog.setCancelable(is_cancelable);
//		dialog.show();
//	}
//
//	protected static void postContentToNet(final Context context,
//			final String url, final RequestParams params,
//			final MonitorContentInterface mInterface) {
//		boolean check_net = checkNetState(context);
//		if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
//					false, null);
//			final Handler handler = new Handler() {
//				@Override
//				public void handleMessage(Message msg) {
//					super.handleMessage(msg);
//
//					progressHUD.dismiss();
//					int arg1 = msg.arg1;
//					if (arg1 == REQUEST_SUCCESS) {
//						mInterface.getContentFromNet(msg.obj);
//					} else if (arg1 == NET_ERROR) {
//						mInterface.getContentFromNet(null);
//						ToastUtil.show(context, R.string.net_error);
//					} else if (arg1 == API_ERROR) {
//						mInterface.getContentFromNet(null);
//						if(msg.obj!=null){
//							ToastUtil.show(context, msg.obj.toString());
//						}
//					} else if (arg1 == TOKEN_EXPIRED) {
//						mInterface.getContentFromNet(null);
//						if(msg.obj!=null){
//							ToastUtil.show(context, msg.obj.toString());
//						}
//						// 跳到login界面
//						tokenExpired(context);
//					}
//				}
//			};
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Message msg = handler.obtainMessage();
//					Log.i(TAG, url);
//					String content = NetUtil.syncPostContent(context, url,
//							params);
//					if (!StringUtil.isNullOrEmpty(content)) {
//						try {
//							JSONObject obj = new JSONObject(content);
//							int errorCode = obj.getInt("errorCode");
//							if (errorCode == 0) {
//								msg.arg1 = REQUEST_SUCCESS;
//								msg.obj = content;
//							} else if (errorCode == TOKEN_EXPIRED_CODE) {
//								msg.arg1 = TOKEN_EXPIRED;
//								msg.obj = obj.getString("message");
//							} else {
//								msg.arg1 = API_ERROR;
//								msg.obj = obj.getString("message");
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//							msg.arg1 = API_ERROR;
//							msg.obj = context.getString(R.string.net_error);
//						}
//					} else {
//						msg.arg1 = NET_ERROR;
//					}
//					msg.sendToTarget();
//				}
//			}).start();
//		}
//	}
//
	protected static void getContentFromNet(final Context context,
			final String url, final MonitorContentInterface mInterface) {
//		boolean check_net = checkNetState(context);
		boolean check_net = true;
		if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//							.getResources().getString(R.string.request_net), true,
//					false, null);
			final ProgressHUD progressHUD = ProgressHUD.show(context, "", true,
					null);
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);

					progressHUD.dismiss();
					int arg1 = msg.arg1;
					if (arg1 == REQUEST_SUCCESS) {
						mInterface.getContentFromNet(msg.obj);
					} else if (arg1 == NET_ERROR) {
						mInterface.getContentFromNet(null);
						ToastUtil.show(context, R.string.net_error);
					} else if (arg1 == API_ERROR) {
						mInterface.getContentFromNet(null);
						if(msg.obj!=null){
							ToastUtil.show(context, msg.obj.toString());
						}
					} else if (arg1 == TOKEN_EXPIRED) {
						mInterface.getContentFromNet(null);
						if(msg.obj!=null){
							ToastUtil.show(context, msg.obj.toString());
						}
						// 跳到login界面
//						tokenExpired(context);
					}
				}
			};

			new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = handler.obtainMessage();
					Log.i(TAG, url);
					String content = NetUtil.syncGetContent(context, url);
					if (!StringUtil.isNullOrEmpty(content)) {
						try {
							JSONObject obj = new JSONObject(content);
							int errorCode = obj.getInt("errorCode");
							if (errorCode == 0) {
								msg.arg1 = REQUEST_SUCCESS;
								msg.obj = content;
							} else if (errorCode == TOKEN_EXPIRED_CODE) {
								msg.arg1 = TOKEN_EXPIRED;
								msg.obj = obj.getString("message");
							} else {
								msg.arg1 = API_ERROR;
								msg.obj = obj.getString("message");
							}
						} catch (JSONException e) {
							e.printStackTrace();
							msg.arg1 = API_ERROR;
							msg.obj = context.getString(R.string.net_error);
						}
					} else {
						msg.arg1 = NET_ERROR;
					}
					msg.sendToTarget();
				}
			}).start();
		}
	}
//
//	protected static void clientDeleteContentToNet(final Context context,
//			final String url, final MonitorContentInterface mInterface) {
//		boolean check_net = checkNetState(context);
//		if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
//					false, null);
//			final Handler handler = new Handler() {
//				@Override
//				public void handleMessage(Message msg) {
//					super.handleMessage(msg);
//
//					progressHUD.dismiss();
//					int arg1 = msg.arg1;
//					if (arg1 == REQUEST_SUCCESS) {
//						mInterface.getContentFromNet(msg.obj);
//					} else if (arg1 == NET_ERROR) {
//						mInterface.getContentFromNet(null);
//						ToastUtil.show(context, R.string.net_error);
//					} else if (arg1 == API_ERROR) {
//						mInterface.getContentFromNet(null);
//						if(msg.obj!=null){
//							ToastUtil.show(context, msg.obj.toString());
//						}
//					} else if (arg1 == TOKEN_EXPIRED) {
//						mInterface.getContentFromNet(null);
//						if(msg.obj!=null){
//							ToastUtil.show(context, msg.obj.toString());
//						}
//						// 跳到login界面
//						tokenExpired(context);
//					}
//				}
//			};
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Message msg = handler.obtainMessage();
//					Log.i(TAG, url);
//					String content = NetUtil.clientSyncDelete(url);
//					if (!StringUtil.isNullOrEmpty(content)) {
//						try {
//							JSONObject obj = new JSONObject(content);
//							int errorCode = obj.getInt("errorCode");
//							if (errorCode == 0) {
//								msg.arg1 = REQUEST_SUCCESS;
//								msg.obj = content;
//							} else if (errorCode == TOKEN_EXPIRED_CODE) {
//								msg.arg1 = TOKEN_EXPIRED;
//								msg.obj = obj.getString("message");
//							} else {
//								msg.arg1 = API_ERROR;
//								msg.obj = obj.getString("message");
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//							msg.arg1 = API_ERROR;
//							msg.obj = context.getString(R.string.net_error);
//						}
//					} else {
//						msg.arg1 = NET_ERROR;
//					}
//					msg.sendToTarget();
//				}
//			}).start();
//		}
//	}
//
//	protected static void clientPostContentToNet(final Context context,
//			final String url, final String json,
//			final MonitorContentInterface mInterface) {
//		boolean check_net = checkNetState(context);
//		if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
//					false, null);
//			final Handler handler = new Handler() {
//				@Override
//				public void handleMessage(Message msg) {
//					super.handleMessage(msg);
//
//					progressHUD.dismiss();
//					int arg1 = msg.arg1;
//					if (arg1 == REQUEST_SUCCESS) {
//						mInterface.getContentFromNet(msg.obj);
//					} else if (arg1 == NET_ERROR) {
//						mInterface.getContentFromNet(null);
//						ToastUtil.show(context, R.string.net_error);
//					} else if (arg1 == API_ERROR) {
//						mInterface.getContentFromNet(null);
//						if(msg.obj!=null){
//							ToastUtil.show(context, msg.obj.toString());
//						}
//					} else if (arg1 == TOKEN_EXPIRED) {
//						mInterface.getContentFromNet(null);
//						if(msg.obj!=null){
//							ToastUtil.show(context, msg.obj.toString());
//						}
//						// 跳到login界面
//						tokenExpired(context);
//					}
//				}
//			};
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Message msg = handler.obtainMessage();
//					Log.i(TAG, url);
//					String content = NetUtil.clientSyncPost(url, json);
//					if (!StringUtil.isNullOrEmpty(content)) {
//						try {
//							JSONObject obj = new JSONObject(content);
//							int errorCode = obj.getInt("errorCode");
//							if (errorCode == 0) {
//								msg.arg1 = REQUEST_SUCCESS;
//								msg.obj = content;
//							} else if (errorCode == TOKEN_EXPIRED_CODE) {
//								msg.arg1 = TOKEN_EXPIRED;
//								msg.obj = obj.getString("message");
//							} else {
//								msg.arg1 = API_ERROR;
//								msg.obj = obj.getString("message");
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//							msg.arg1 = API_ERROR;
//							msg.obj = context.getString(R.string.net_error);
//						}
//					} else {
//						msg.arg1 = NET_ERROR;
//					}
//					msg.sendToTarget();
//				}
//			}).start();
//		}
//	}
//
	public interface MonitorContentInterface {
		public void getContentFromNet(Object content);
	}
//
//	public static void tokenExpired(Context context) {
//		/*
//		 * LoginHelper.clear_login_info(context); Intent intent = new
//		 * Intent(context, LoginActivity.class); context.startActivity(intent);
//		 */
//
//		Intent intent = new Intent(context, MainTabActivity.class);
//		intent.putExtra(MainTabActivity.EXIT_TYPE,
//				MainTabActivity.EXIT_TO_LOGIN);
//		context.startActivity(intent);
//	}

}
