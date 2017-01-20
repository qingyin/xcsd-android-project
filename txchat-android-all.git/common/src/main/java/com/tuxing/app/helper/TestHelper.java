package com.tuxing.app.helper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.bean.AuthorityBean;
import com.tuxing.app.bean.EvaluationBean;
import com.tuxing.app.bean.OptionBean;
import com.tuxing.app.bean.SocialArticleBean;
import com.tuxing.app.bean.SubjectBean;
import com.tuxing.app.bean.TestBean;
import com.tuxing.app.bean.TestFirstBean;
import com.tuxing.app.bean.TestListBean;
import com.tuxing.app.util.JsonUtil;
import com.tuxing.app.util.NetUtil;
import com.tuxing.app.util.PreferenceUtil;
import com.tuxing.app.util.StringUtil;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.ToastUtil;
import com.tuxing.app.view.ProgressHUD;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.entity.TestList;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.utils.Constants;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class TestHelper extends NetManager {

    private static final String TAG = TestHelper.class.getName();
    private static final String TEST_RESULT = "Test-Result-";
    private static final String SUBJECT_IDS = "Subject-Ids-";

    public static void saveTestResult(Context context, String test_result) {
//		数据未保存
//		String current_user = PreferenceUtil.getDefStr(context,
//				LoginHelper.CURRENT_LOGIN_USER);
//		int childID = GuideHelper.getCurrentBabyId(context);
        String current_user = SysConstants.childname;
        int childID = Integer.parseInt(SysConstants.childID);
        String key = TEST_RESULT + childID;
        PreferenceUtil.putUserStr(context, current_user, key, test_result);
    }

    //
    public static String getTestResult(Context context) {
        String current_user = SysConstants.childname;
        int childID = Integer.parseInt(SysConstants.childID);
//		String current_user = PreferenceUtil.getDefStr(context,
//				LoginHelper.CURRENT_LOGIN_USER);
//		int childID = GuideHelper.getCurrentBabyId(context);
        String key = TEST_RESULT + childID;
        return PreferenceUtil.getUserStr(context, current_user, key);
    }

    //
    public static void saveAnswer(Context context, int testID, int subjectID,
                                  int optionID) {
//		String current_user = PreferenceUtil.getDefStr(context,
//				LoginHelper.CURRENT_LOGIN_USER);
//		int childID = GuideHelper.getCurrentBabyId(context);
//		String key = childID + "-" + testID + "-" + subjectID;
        String current_user = SysConstants.childname;
        int childID = Integer.parseInt(SysConstants.childID);
        String key = childID + "-" + testID + "-" + subjectID;
        PreferenceUtil.putUserInt(context, current_user, key, optionID);
    }

    //
    public static int getAnswer(Context context, int testID, int subjectID) {
//		String current_user = PreferenceUtil.getDefStr(context,
//				LoginHelper.CURRENT_LOGIN_USER);
//		int childID = GuideHelper.getCurrentBabyId(context);
        String current_user = SysConstants.childname;
        int childID = Integer.parseInt(SysConstants.childID);
        String key = childID + "-" + testID + "-" + subjectID;
        return PreferenceUtil.getUserInt(context, current_user, key);
    }

    //
    private static void saveSubjectIds(Context context, int testID, String ids) {
//		String current_user = PreferenceUtil.getDefStr(context,
//				LoginHelper.CURRENT_LOGIN_USER);
//		int childID = GuideHelper.getCurrentBabyId(context);
        String current_user = SysConstants.childname;
        int childID = Integer.parseInt(SysConstants.childID);
        String key = SUBJECT_IDS + childID + "-" + testID;
        PreferenceUtil.putUserStr(context, current_user, key, ids);
    }

    //
    private static String getSubjectIds(Context context, int testID) {
//		String current_user = PreferenceUtil.getDefStr(context,
//				LoginHelper.CURRENT_LOGIN_USER);
//		int childID = GuideHelper.getCurrentBabyId(context);
        String current_user = SysConstants.childname;
        int childID = Integer.parseInt(SysConstants.childID);
        String key = SUBJECT_IDS + childID + "-" + testID;
        return PreferenceUtil.getUserStr(context, current_user, key);
    }

    //
    public static void clearAnswers(Context context, int testID) {
        String sub_ids = getSubjectIds(context, testID);
        if (!StringUtil.isNullOrEmpty(sub_ids)) {
//			String current_user = PreferenceUtil.getDefStr(context,
//					LoginHelper.CURRENT_LOGIN_USER);
//			int childID = GuideHelper.getCurrentBabyId(context);
            String current_user = SysConstants.childname;
            int childID = Integer.parseInt(SysConstants.childID);
            String[] sids = sub_ids.split(",");
            for (String sid : sids) {
                String key = childID + "-" + testID + "-" + sid;
                PreferenceUtil.removeUserKey(context, current_user, key);
            }
        }
    }

    //
//	public static void getAnswers(final Context context, final int testID,
//			final MonitorInterface mInterface) {
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
//						mInterface.getContentFromNet(null);
//					} else if (arg1 == TOKEN_EXPIRED) {
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
//					} else {
//						ToastUtil.show(context, R.string.net_error);
//					}
//				}
//			};
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Message msg = handler.obtainMessage();
//
//					String token = LoginHelper.getCurrentToken(context);
//					int childID = GuideHelper.getCurrentBabyId(context);
//					String url = String.format(context.getResources()
//							.getString(R.string.base_url)
//							+ "getanswers/json?token=%s&childID=%d&testID=%d",
//							token, childID, testID);
//					// Log.i(TAG, url);
//					String content = NetUtil.syncGetContent(context, url);
//					/*
//					 * if (!StringUtil.isNullOrEmpty(content)) { Log.i(TAG,
//					 * content); }
//					 */
//					boolean content_success = JsonUtil
//							.isContentSuccess(content);
//					if (content_success) {
//						try {
//							JSONObject content_obj = new JSONObject(content);
//							JSONArray result_array = content_obj
//									.getJSONArray("result");
//							if (result_array != null) {
//								for (int i = 0; i < result_array.length(); i++) {
//									JSONObject option_obj = result_array
//											.getJSONObject(i);
//									saveAnswer(context, testID,
//											option_obj.getInt("subjectID"),
//											option_obj.getInt("option"));
//								}
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//
//						msg.arg1 = REQUEST_SUCCESS;
//					} else if (JsonUtil.isTokenExpired(content)) {
//						msg.arg1 = TOKEN_EXPIRED;
//					} else {
//						msg.arg1 = 1;
//					}
//					msg.sendToTarget();
//				}
//			}).start();
//		}
//	}
//
    public static void getEvaluation(final Context context, final int testID,
                                     final int childID, final MonitorInterface mInterface) {
//		boolean check_net = checkNetState(context);
        boolean check_net = true;
        if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
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
                        mInterface.getContentFromNet(null);
                    } else if (arg1 == TOKEN_EXPIRED) {
//						原来逻辑跳登录
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
                    } else {
                        ToastUtil.show(context, R.string.net_error);
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    String token = null;
                    LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                    if (loginUser != null) {
                        token = loginUser.getToken();
                    }
//					String token = LoginHelper.getCurrentToken(context);
                    // int childID = GuideHelper.getCurrentBabyId(contex、t);
//					String url = String
//							.format(context.getResources().getString(
//									R.string.base_url)
//									+ "getevaluation/json?token=%s&childID=%d&testID=%d",
//									token, childID, testID);
                    String url = String
                            .format(SysConstants.KURL_THEME_TEST
                                            + "getlxtevaluation/json?token=%s&childID=%d&testID=%d&source=%s",
                                    token, childID, testID,"android");
//                            .format(context.getResources().getString(
//                                            R.string.base_url)
//                                            + "getlxtevaluation/json?token=%s&childID=%d&testID=%d&source=%s",
//                                    token, childID, testID,"android");
                    Log.i(TAG, url);
                    String content = NetUtil.syncGetContent(context, url);
                    // Log.i(TAG, content);
                    boolean content_success = JsonUtil
                            .isContentSuccess(content);
                    if (content_success) {
                        saveTestResult(context, content);
                        msg.arg1 = REQUEST_SUCCESS;
                    } else if (JsonUtil.isTokenExpired(content)) {
                        msg.arg1 = TOKEN_EXPIRED;
                    } else {
                        msg.arg1 = 1;
                    }
                    msg.sendToTarget();
                }
            }).start();
        }
    }

    //
//	public static void getEvaluationTasks(final Context context,
//			final int evaluationID, final MonitorInterface mInterface) {
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
//					} else if (arg1 == TOKEN_EXPIRED) {
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
//					} else {
//						ToastUtil.show(context, R.string.net_error);
//					}
//				}
//			};
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Message msg = handler.obtainMessage();
//
//					String token = LoginHelper.getCurrentToken(context);
//					int childID = GuideHelper.getCurrentBabyId(context);
//					String url = String
//							.format(context.getResources().getString(
//									R.string.base_url)
//									+ "getevaluationtasks/json?token=%s&childID=%d&evaluationID=%d",
//									token, childID, evaluationID);
//					Log.i(TAG, url);
//					String content = NetUtil.syncGetContent(context, url);
//					boolean content_success = JsonUtil
//							.isContentSuccess(content);
//					if (content_success) {
//						List<EvaluationTaskBean> tasks = TaskHelper
//								.parseToEvaluationTaskList(content);
//						if (tasks.size() > 0) {
//							msg.obj = tasks;
//							msg.arg1 = REQUEST_SUCCESS;
//						} else if (JsonUtil.isTokenExpired(content)) {
//							msg.arg1 = TOKEN_EXPIRED;
//						} else {
//							msg.arg1 = 1;
//						}
//					} else {
//						msg.arg1 = 1;
//					}
//					msg.sendToTarget();
//				}
//			}).start();
//		}
//	}
//
    public static EvaluationBean getEvaluation(Context context) {
        EvaluationBean evaluation = new EvaluationBean();
        try {
            String eva_content = getTestResult(context);
            JSONObject eva_content_obj = new JSONObject(eva_content);
            JSONObject eva_obj = eva_content_obj.getJSONObject("result");
            evaluation.id = eva_obj.getInt("id");
            if (StringUtil.isNotNullAndNotEqualsNull(eva_obj
                    .getString("childPic"))) {
                evaluation.childPic = eva_obj.getString("childPic");
            }
            evaluation.testSerialNumber = eva_obj.getInt("testSerialNumber");
            evaluation.description = eva_obj.getString("description");
            evaluation.evaluationType = eva_obj.getInt("evaluationType");

            if (StringUtil.isNotNullAndNotEqualsNull(eva_obj
                    .getString("socialArticle"))) {
                JSONObject socialArticle_obj = eva_obj
                        .getJSONObject("socialArticle");
                SocialArticleBean socialArticle = new SocialArticleBean();
                socialArticle.id = socialArticle_obj.getInt("id");
                socialArticle.updateTime = socialArticle_obj
                        .getString("updateTime");
                socialArticle.createTime = socialArticle_obj
                        .getString("createTime");
                socialArticle.title = socialArticle_obj.getString("title");
                socialArticle.introduction = socialArticle_obj
                        .getString("introduction");
                socialArticle.content = socialArticle_obj.getString("content");
                evaluation.socialArticle = socialArticle;
            }
//
            if (StringUtil.isNotNullAndNotEqualsNull(eva_obj
                    .getString("authority"))) {
                JSONObject authority_obj = eva_obj.getJSONObject("authority");
                AuthorityBean authority = new AuthorityBean();
                authority.id = authority_obj.getInt("id");
                authority.updateTime = authority_obj.getString("updateTime");
                authority.createTime = authority_obj.getString("createTime");
                if (StringUtil.isNotNullAndNotEqualsNull(authority_obj
                        .getString("title"))) {
                    authority.title = authority_obj.getString("title");
                }
                if (StringUtil.isNotNullAndNotEqualsNull(authority_obj
                        .getString("introduction"))) {
                    authority.introduction = authority_obj
                            .getString("introduction");
                }
                if (StringUtil.isNotNullAndNotEqualsNull(authority_obj
                        .getString("journalName"))) {
                    authority.journalName = authority_obj
                            .getString("journalName");
                }
                if (StringUtil.isNotNullAndNotEqualsNull(authority_obj
                        .getString("author"))) {
                    authority.author = authority_obj.getString("author");
                }
                if (StringUtil.isNotNullAndNotEqualsNull(authority_obj
                        .getString("summary"))) {
                    authority.summary = authority_obj.getString("summary");
                }
                if (StringUtil.isNotNullAndNotEqualsNull(authority_obj
                        .getString("detail"))) {
                    authority.detail = authority_obj.getString("detail");
                }
                if (StringUtil.isNotNullAndNotEqualsNull(authority_obj
                        .getString("keywords"))) {
                    authority.keywords = authority_obj.getString("keywords");
                }
                evaluation.authority = authority;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return evaluation;
    }

    //
    public static void addAnswers(final Context context, final int testID,
                                  final String answers, final NetManager.MonitorInterface mInterface) {
        boolean check_net = true;
//		boolean check_net = checkNetState(context);
        if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
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
                    } else if (arg1 == TOKEN_EXPIRED) {
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
                    } else {
                        ToastUtil.show(context, R.string.net_error);
                    }
                }

                ;
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    String token = null;
                    int childID = Integer.parseInt(SysConstants.childID);
                    LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                    if (loginUser != null) {
                        token = loginUser.getToken();
                    }
//					String token = LoginHelper.getCurrentToken(context);
//					int childID = GuideHelper.getCurrentBabyId(context);
//					String url = "http://121.41.101.14:8082/service/service/gettests/json?testID=1&childID=1&source=ios";
//					String url = "http://121.41.101.14:8082/service/service/addanswers/json?" +"childID="+childID+"&testID="+testID+"&answers="+answers+"&source="+"ios";
                    String url = String
                            .format(SysConstants.KURL_THEME_TEST
                                            + "addanswers/json?token=%s&childID=%d&testID=%d%s",
                                    token, childID, testID, answers);
//                    Log.i(TAG, "sdfafdsafadsfdsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + url);
                    String content = NetUtil.syncGetContent(context, url);
                    // Log.i(TAG, content);
                    if (JsonUtil.isContentSuccess(content)) {
                        // 修改二级测试缓存 设为测试已完成
                        Map<String, Object> update_info = new HashMap<String, Object>();
                        update_info.put("status", 1);
//						CacheHelper.updateSecondTest(context, testID,
//								update_info);
                        // 获取评价
//						String eva_url = "www.baidu.com";
                        String eva_url = String
                                .format(SysConstants.KURL_THEME_TEST
                                                + "getlxtevaluation/json?token=%s&childID=%d&testID=%d&source=%s",
//                                                + "getevaluation/json?source=%s&childID=%d&testID=%d",
                                        token, childID, testID,"android");
                        Log.i(TAG, eva_url);
                        String eva_content = NetUtil.syncGetContent(context,
                                eva_url);
                        //Log.i(TAG, eva_content);
                        if (JsonUtil.isContentSuccess(eva_content)) {
                            saveTestResult(context, eva_content);
//							DiaryHelper.getDiaryListAndRestore(context);
                            msg.arg1 = REQUEST_SUCCESS;
                        } else {
                            msg.arg1 = 1;
                        }
                    } else if (JsonUtil.isTokenExpired(content)) {
                        msg.arg1 = TOKEN_EXPIRED;
                    } else {
                        msg.arg1 = 1;
                    }
                    msg.sendToTarget();
                }
            }).start();
        }
    }

    //
//	public static void getRank(final Context context,
//			final NetManager.MonitorInterface mInterface) {
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
//					} else if (arg1 == TOKEN_EXPIRED) {
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
//					} else {
//						ToastUtil.show(context, R.string.net_error);
//					}
//				}
//			};
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Message msg = handler.obtainMessage();
//
//					String token = LoginHelper.getCurrentToken(context);
//					String url = String.format(context.getResources()
//							.getString(R.string.base_url)
//							+ "getrank/json?token=%s", token);
//					Log.i(TAG, url);
//					String content = NetUtil.syncGetContent(context, url);
//					// Log.i(TAG, content);
//					if (JsonUtil.isContentSuccess(content)) {
//						try {
//							Map<String, Integer> map = new HashMap<String, Integer>();
//							JSONObject content_obj = new JSONObject(content);
//							JSONObject result_obj = content_obj
//									.getJSONObject("result");
//							map.put("total", result_obj.getInt("total"));
//							map.put("rank", result_obj.getInt("rank"));
//							msg.obj = map;
//							msg.arg1 = REQUEST_SUCCESS;
//						} catch (JSONException e) {
//							e.printStackTrace();
//							msg.arg1 = 1;
//						}
//					} else if (JsonUtil.isTokenExpired(content)) {
//						msg.arg1 = TOKEN_EXPIRED;
//					} else {
//						msg.arg1 = 1;
//					}
//					msg.sendToTarget();
//				}
//			}).start();
//		}
//	}
//
    public static void getTests(final Context context, final int testID,
                                final NetManager.MonitorInterface mInterface) {
//		boolean check_net = checkNetState(context);
        boolean check_net = true;
        if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
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
                    } else if (arg1 == TOKEN_EXPIRED) {
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
                    } else {
                        ToastUtil.show(context, R.string.net_error);
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    String token = null;
                    int childID = Integer.parseInt(SysConstants.childID);
                    LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                    if (loginUser != null) {
                        token = loginUser.getToken();
                    }
//					String token = LoginHelper.getCurrentToken(context);
//					int childID = GuideHelper.getCurrentBabyId(context);
//					String url = "www.baidu.com";
//					url = "http://121.41.101.14:8082/service/service/gettests/json?testID=1&childID=1&source=ios";
                    String url = String
                            .format(SysConstants.KURL_THEME_TEST
                                            + "gettestslxt/json?token=%s&testID=%d&childID=%d&source=android",
                                    token, testID, childID);
//                    String url = String
//                            .format(context.getResources().getString(
//                                            R.string.base_url)
//                                            + "gettests/json?token=%s&testID=%d&childID=%d&source=android",
//                                    token, testID, childID);
//					Log.i(TAG, url);
                    String content = NetUtil.syncGetContent(context, url);
                    /*
					 * if(!StringUtil.isNullOrEmpty(content)){ Log.i(TAG,
					 * content); }
					 */
                    if (JsonUtil.isContentSuccess(content)) {
                        TestBean test = new TestBean();
                        try {
                            JSONObject content_obj = new JSONObject(content);
                            JSONObject result_obj = content_obj
                                    .getJSONObject("result");
                            test.id = result_obj.getInt("id");
                            test.name = result_obj.getString("name");
                            test.description = result_obj
                                    .getString("description");
                            JSONArray subject_array = result_obj
                                    .getJSONArray("subjects");
                            if (subject_array != null) {
                                List<SubjectBean> subjects = new ArrayList<SubjectBean>();
                                String subject_ids = "";
                                for (int i = 0; i < subject_array.length(); i++) {
                                    SubjectBean subject = new SubjectBean();
                                    JSONObject subject_obj = subject_array
                                            .getJSONObject(i);
                                    subject.id = subject_obj.getInt("id");
                                    subject_ids += subject.id + ",";
                                    subject.subject = subject_obj
                                            .getString("subject");
                                    subject.num = subject_obj.getInt("num");
                                    JSONArray option_array = subject_obj
                                            .getJSONArray("options");
                                    if (option_array != null) {
                                        List<OptionBean> options = new ArrayList<OptionBean>();
                                        for (int j = 0; j < option_array
                                                .length(); j++) {
                                            OptionBean option = new OptionBean();
                                            JSONObject option_obj = option_array
                                                    .getJSONObject(j);
                                            option.id = option_obj.getInt("id");
                                            option.optionName = option_obj
                                                    .getString("optionName");
                                            options.add(option);
                                        }
                                        subject.options = options;
                                    }
                                    subjects.add(subject);
                                }
                                test.subjests = subjects;
                                if (!StringUtil.isNullOrEmpty(subject_ids)) {
                                    subject_ids = subject_ids.substring(0,
                                            subject_ids.lastIndexOf(","));
                                    saveSubjectIds(context, testID, subject_ids);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        msg.obj = test;
                        msg.arg1 = REQUEST_SUCCESS;
                    } else if (JsonUtil.isTokenExpired(content)) {
                        msg.arg1 = TOKEN_EXPIRED;
                    } else {
                        msg.arg1 = 1;
                    }
                    msg.sendToTarget();
                }
            }).start();
        }
    }

    //
//	public static List<TestSecondBean> parseToTestSecondList(String content) {
//		List<TestSecondBean> list = new ArrayList<TestSecondBean>();
//		try {
//			if (!StringUtil.isNullOrEmpty(content)) {
//				JSONObject content_obj = new JSONObject(content);
//				JSONArray result_array = content_obj.getJSONArray("result");
//				if (result_array != null) {
//					for (int i = 0; i < result_array.length(); i++) {
//						JSONObject test_obj = result_array.getJSONObject(i);
//						TestSecondBean test = new TestSecondBean();
//						test.id = test_obj.getInt("id");
//						test.categoryFirstID = test_obj
//								.getInt("categoryFirstID");
//						test.name = test_obj.getString("name");
//						test.testID = test_obj.getInt("testID");
//						test.color = test_obj.getInt("color");
//						test.colorValue = test_obj.getString("colorValue");
//						test.status = test_obj.getInt("status");
//						list.add(test);
//					}
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
//
    public static void getSecondTests(final Context context, final int cf,
                                      final NetManager.MonitorInterface mInterface) {
//		boolean check_net = checkNetState(context);
        boolean check_net = true;
        if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
//					false, null);
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

//					progressHUD.dismiss();
                    int arg1 = msg.arg1;
                    if (arg1 == REQUEST_SUCCESS) {
                        mInterface.getContentFromNet(msg.obj);
                    } else if (arg1 == TOKEN_EXPIRED) {
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
                    } else {
                        ToastUtil.show(context, R.string.net_error);
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    String token = null;
                    LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                    if (loginUser != null) {
                        token = loginUser.getToken();
                    }
//					String token = LoginHelper.getCurrentToken(context);
//					int childID = GuideHelper.getCurrentBabyId(context);
                    String url = "www.baidu.com";
//					String url = String
//							.format(context.getResources().getString(
//									R.string.base_url)
//									+ "categorysecond/json?token=%s&cf=%d&childID=%d&source=android",
//									token, cf, childID);
//					Log.i(TAG, url);
                    String content = NetUtil.syncGetContent(context, url);
                    // Log.i(TAG, content);
                    if (JsonUtil.isContentSuccess(content)) {
//						CacheHelper.saveSecondTest(context, content);
//						msg.obj = parseToTestSecondList(content);
//						msg.arg1 = REQUEST_SUCCESS;
                    } else if (JsonUtil.isTokenExpired(content)) {
                        msg.arg1 = TOKEN_EXPIRED;
                    } else {
                        msg.arg1 = 1;
                    }
                    msg.sendToTarget();
                }
            }).start();
        }
    }

    public static void getFirstTests(final Context context,
                                     final NetManager.MonitorInterface mInterface) {
//		boolean check_net = checkNetState(context);
        boolean check_net = true;
        if (check_net) {
//			final ProgressHUD progressHUD = ProgressHUD.show(context, context
//					.getResources().getString(R.string.request_net), true,
//					false, null);
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

//					progressHUD.dismiss();
                    int arg1 = msg.arg1;
                    if (arg1 == REQUEST_SUCCESS) {
                        mInterface.getContentFromNet(msg.obj);
                    } else if (arg1 == TOKEN_EXPIRED) {
//						ToastUtil.show(context, R.string.token_expired);
//						tokenExpired(context);
                    } else {
                        mInterface.getContentFromNet(null);
                        ToastUtil.show(context, R.string.net_error);
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();

//					String token = LoginHelper.getCurrentToken(context);
                    String url = "";
//					String url = String.format(context.getResources()
//							.getString(R.string.base_url)
//							+ "categoryfirst/json?token=%s&schoolAge=%d",
//							token, GuideHelper.getCurrentBabySchoolAge(context));
                    Log.i(TAG, url);
                    String content = NetUtil.syncGetContent(context, url);
                    if (JsonUtil.isContentSuccess(content)) {
                        List<TestFirstBean> list = new ArrayList<TestFirstBean>();
                        try {
                            JSONObject content_obj = new JSONObject(content);
                            JSONArray result_array = content_obj
                                    .getJSONArray("result");
                            if (result_array != null) {
                                for (int i = 0; i < result_array.length(); i++) {
                                    JSONObject test_obj = result_array
                                            .getJSONObject(i);
                                    TestFirstBean test = new TestFirstBean();
                                    test.id = test_obj.getInt("id");
                                    test.schoolAge = test_obj
                                            .getInt("schoolAge");
                                    test.name = test_obj.getString("name");
                                    test.color = test_obj.getInt("color");
                                    test.colorValue = test_obj
                                            .getString("colorValue");
                                    test.type = test_obj.getInt("type");
                                    test.animalPic = test_obj
                                            .getString("animalPic");
                                    list.add(test);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        msg.obj = list;
                        msg.arg1 = REQUEST_SUCCESS;
                    } else if (JsonUtil.isTokenExpired(content)) {
                        msg.arg1 = TOKEN_EXPIRED;
                    } else {
                        msg.arg1 = 1;
                    }
                    msg.sendToTarget();
                }
            }).start();
        } else {
            mInterface.getContentFromNet(null);
        }
    }

    //
//	public static void getRelateTest(Context context, String associateTag,
//			final MonitorInterface mInterface) {
//		String url = String.format(
//				context.getResources().getString(R.string.base_url)
//						+ "relatedtest/json?schoolAge=%d&childID=%d",
//				GuideHelper.getCurrentBabySchoolAge(context),
//				GuideHelper.getCurrentSelectId(context));
//		try {
//			if (!StringUtil.isNullOrEmpty(associateTag)) {
//				url += "&associateTag="
//						+ URLEncoder.encode(associateTag, "UTF-8");
//			}
//			getTestListFromNet(context, url, mInterface);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//	}
//
    public static void getTestList(Context context,
                                   final MonitorInterface mInterface) {
        String token = null;
        int childID = Integer.parseInt(SysConstants.childID);
        int schoolAge = 2;
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            schoolAge=20;
        }
        LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            token = loginUser.getToken();
        }
//		url = "http://121.41.101.14:8082/service/service/testlist/json?schoolAge=2&childID=1";
        String url = String.format(
                SysConstants.KURL_THEME_TEST
                        + "testlistlxt/json?token=%s&schoolAge=%d&childID=%d",
                token,schoolAge,childID);
        getTestListFromNet(context, url, mInterface);
    }

    //
    private static void getTestListFromNet(Context context, String url,
                                           final MonitorInterface mInterface) {
        getContentFromNet(context, url, new MonitorContentInterface() {
            @Override
            public void getContentFromNet(Object content) {
                if (content != null) {
                    try {
                        JSONObject json_obj = new JSONObject(content.toString());
                        String result = json_obj.getString("result");
                        List<TestListBean> list = new ArrayList<TestListBean>();
                        List<TestList> lists = new ArrayList<TestList>();
                        if (StringUtil.isNotNullAndNotEqualsNull(result)) {
                            Gson g = new Gson();
                            Type lt = new TypeToken<List<TestListBean>>() {
                            }.getType();
                            list = g.fromJson(result, lt);

                            for (int i=0;i<list.size();i++){
                                TestListBean bean = list.get(i);
                                TestList testList=new TestList();
                                testList.setAnimalPic(bean.getAnimalPic());
                                testList.setName(bean.getName());
                                testList.setStatus(bean.getStatus());
                                testList.setColor(bean.getColor());
                                testList.setColorValue(bean.getColorValue());
                                testList.setAssociateTag(bean.getAssociateTag());
                                testList.setDescription(bean.getDescription());
                                lists.add(testList);
                            }
                            UserDbHelper.getInstance().updateTestList(lists);
                        }
                        mInterface.getContentFromNet(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mInterface.getContentFromNet(null);
                }
            }
        });
    }

}
