package com.tuxing.app.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.share.widget.ShareDialog;
import com.tuxing.app.util.DownloadTaskListener;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.utils.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xcsd.rpc.proto.EventType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WebSubDataActivity extends BaseActivity implements OnClickListener, DownloadTaskListener {

    private String intentTitle = "";
    private String itemUrl = "";
    TextView tvTitle;
    TextView tv_back;
    WebView webView;
    boolean isWuyou;
    ProgressBar pb;
    LinearLayout btnTitleLeft;
    public RelativeLayout iv_homepage_webview_error;
    private ImageView right_button;
    private Button tv_right;
    private String webTitle = "";

    private boolean isteacher = false;
    private boolean isparent = false;
    SocializeListeners.SnsPostListener SNSP;

    public static WebSubDataActivity instance = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 404) {// 主页不存在
                // 载入本地assets文件夹下面的错误提示页面404.html
                iv_homepage_webview_error.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            } else {
                iv_homepage_webview_error.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        instance = this;

        Intent intent = getIntent();
        intentTitle = intent.getStringExtra("title");
        isWuyou = intent.getBooleanExtra("isWuyou", false);
        itemUrl = intent.getStringExtra("itemUrl");
        tvTitle = (TextView) findViewById(R.id.tvTitleLeft);
        if (!TextUtils.isEmpty(intentTitle))
            tvTitle.setText(intentTitle);

        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        btnTitleLeft.setVisibility(View.VISIBLE);
        btnTitleLeft.setOnClickListener(this);
        tv_right = (Button) findViewById(R.id.tv_right);
        tv_back = (TextView) findViewById(R.id.tv_back);

        tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setMax(100);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        isteacher = TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion());
        isparent = TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion());
        if (isteacher) {
            tv_back.setTextColor(getResources().getColor(R.color.text_teacher));
            tv_right.setTextColor(getResources().getColor(R.color.text_teacher));
            Drawable drawable = null;
            drawable = getResources().getDrawable(R.drawable.ic_back_title_t);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_back.setCompoundDrawables(drawable, null, null, null);
        } else if (isparent) {
            tv_back.setTextColor(getResources().getColor(R.color.text_parent));
//            tv_right.setTextColor(getResources().getColor(R.color.text_parent));
            Drawable drawable = null;
            drawable = getResources().getDrawable(R.drawable.ic_back_title_p);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_back.setCompoundDrawables(drawable, null, null, null);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if (newProgress == 100) {
                    pb.setVisibility(View.GONE);
                }
            }

            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(intentTitle)) {
                    tvTitle.setText(title);
                }
                webTitle = title;
                if (TextUtils.isEmpty(webTitle)) {
                    webTitle = intentTitle;
                }
            }
        });
        webView.setWebViewClient(new MyWebViewClient());
        iv_homepage_webview_error = (RelativeLayout) findViewById(R.id.iv_homepage_webview_error);
        iv_homepage_webview_error.setOnClickListener(this);
        right_button = (ImageView) findViewById(R.id.right_button);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            right_button.setImageResource(R.drawable.ic_share_p);
        }
//        right_button.setVisibility(View.GONE);
        right_button.setOnClickListener(this);
        if (PhoneUtils.isNetworkAvailable(mContext)) {
            updateWebView(itemUrl);
        } else {
            updateWebView("");
            Toast.makeText(mContext, mContext.getString(R.string.net_slowly),
                    Toast.LENGTH_SHORT).show();
        }
        initShareConfig(itemUrl);

        if (intentTitle.equals("理解孩子")){
            String str = itemUrl+"";
            String spStr[] = str.split("&");
            String id = spStr[1];
            String ID = id.substring(3);
// String newStr = str.subString(str.indexOf("\\"),str.length());
            getService().getDataReportManager().reportEventBid(EventType.ARTICLE_IN, ID+"");
        }
    }

    public void updateWebView(String data) {
        Message msg = new Message();
        disProgressDialog();
        if (TextUtils.isEmpty(data)) {
            msg.what = 404;
        } else {
            msg.what = 405;
            if (!TextUtils.isEmpty(data)) {
                if (isWuyou) {
                    Map<String, String> extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("token", getService().getUserToken());
                    webView.loadUrl(data, extraHeaders);
//                   tv_right.setVisibility(View.VISIBLE);
                    right_button.setVisibility(View.GONE);
                } else {
                    tv_right.setVisibility(View.GONE);
                    right_button.setVisibility(View.VISIBLE);
                    webView.loadUrl(data);
                }
            }
        }
        handler.sendMessage(msg);
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        finish();
    }

    /**
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                if (url.startsWith("weixin")) {
                    try {
                        Uri uri = Uri.parse(url);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                    } catch (Exception e) {
                        Log.e("weixin error:", e.getMessage());
                        showToast("微信打开失败,请检查");
                    }
                } else {
                    updateWebView(url);
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mContext != null && !WebSubDataActivity.this.isFinishing()) {
            }
        }

        /**
         **/
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v("TESTLOG", "WebViewHeight=" + webView.getContentHeight());
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Message msg = new Message();
            // 此处判断主页是否存在，因为主页是通过loadUrl加载的，
            // 此时不会执行shouldOverrideUrlLoading进行页面是否存在的判断
            // 进入主页后，点主页里面的链接，链接到其他页面就一定会执行shouldOverrideUrlLoading方法了
            msg.what = 404;
            handler.sendMessage(msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
        int id = arg0.getId();
        if (id == R.id.btnTitleLeft) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        } else if (id == R.id.iv_homepage_webview_error) {
            if (PhoneUtils.isNetworkAvailable(mContext)) {
                webView.reload();
                showProgressDialog(mContext, "", true, null);
                webView.clearCache(true);
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.clearHistory();
                    }
                }, 1000);
                updateWebView(itemUrl);
            } else {
                Toast.makeText(mContext,
                        mContext.getString(R.string.net_slowly),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.right_button) {
            handleShare(itemUrl);
        }
    }

//	public void onEventMainThread(ContentItemEvent event) {
//		if (isActivity) {
//			switch (event.getEvent()) {
//			case FETCH_CONTENT_SUCCESS:
//				updateWebView(event.getItems().get(0).getHtml());
//				MyLog.getLogger(TAG).d("获取html成功"+ event.getMsg());
//				break;
//			case FETCH_CONTENT_FAILED:
//				updateWebView("");
//				MyLog.getLogger(TAG).d("获取html失败"+ event.getMsg());
//				break;
//			case FETCH_GARDEN_INTRO_SUCCESS:
//				updateWebView(event.getItems().get(0).getHtml());
//				MyLog.getLogger(TAG).d("获取幼儿园html成功"+ event.getMsg());
//				break;
//			case FETCH_GARDEN_INTRO_FAILED:
//				updateWebView("");
//				MyLog.getLogger(TAG).d("获取幼儿园html失败"+ event.getMsg());
//				break;
//			case FETCH_AGREEMENT_SUCCESS:
//				updateWebView(event.getItems().get(0).getHtml());
//                showAndSaveLog(TAG, "获取协议html成功", false);
//                break;
//            case FETCH_AGREEMENT_FAILED:
//            	updateWebView("");
//                showAndSaveLog(TAG, "获取协议html失败"+ event.getMsg(), false);
//                break;
//			}
//		}
//	}


    /***
     * 分享
     **/

    public void handleShare(final String linkUrl) {
        final ShareDialog dialog = new ShareDialog(WebSubDataActivity.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(getResources().getString(R.string.share_to));
        dialog.setOnPlatformClickListener(new ShareDialog.OnSharePlatformClick() {
            @Override
            public void onPlatformClick(int id) {
                String str = itemUrl + "";
                String spStr[] = str.split("&");
                String ids = spStr[1]+"";
                String ID = ids.substring(3)+"";

                JSONObject json = new JSONObject();

                if (id == R.id.ly_share_weichat_circle) {
                    try {
                        json.put("type", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getService().getDataReportManager().reportExtendedInfo(EventType.SHARE_ARTICLE, ID + "", json.toString());
                    shareToWeiChatCircle(linkUrl);
                } else if (id == R.id.ly_share_weichat) {
                    try {
                        json.put("type", 2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getService().getDataReportManager().reportExtendedInfo(EventType.SHARE_ARTICLE, ID + "", json.toString());
                    shareToWeiChat(linkUrl);
                } else if (id == R.id.ly_share_qq) {
                    try {
                        json.put("type", 3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getService().getDataReportManager().reportExtendedInfo(EventType.SHARE_ARTICLE, ID + "", json.toString());
                    shareToQQPlatform(linkUrl);
                } else if (id == R.id.ly_share_sina_weibo) {
                    shareToSinaWeibo(linkUrl);
                } else if (id == R.id.ly_share_copy_link) {
                    try {
                        json.put("type", 4);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getService().getDataReportManager().reportExtendedInfo(EventType.SHARE_ARTICLE, ID + "", json.toString());
                    copyTextToBoard(linkUrl);
                } else if (id == R.id.ly_share_class) {
//                    try {
//                        json.put("type", 4);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    getService().getDataReportManager().reportExtendedInfo(EventType.SHARE_ARTICLE, ID + "", json.toString());
                    Intent intent = new Intent(mContext, ShareChooseClassActivity.class).putExtra("url",itemUrl);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private final UMSocialService mController = UMServiceFactory
            .getUMSocialService(SysConstants.DESCRIPTOR);
    private boolean garden;

    private void initShareConfig(String linkUrl) {
        // 设置分享的内容
        setShareContent(linkUrl);
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent(String linkUrl) {

        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        UMImage localImage = new UMImage(WebSubDataActivity.this, R.drawable.logo);
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            localImage = new UMImage(WebSubDataActivity.this, R.drawable.logo_teacher);
        }
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(intentTitle);
        weixinContent.setShareContent(intentTitle + "," + linkUrl);
        weixinContent.setShareImage(localImage);
        weixinContent.setAppWebSite("http://");
        weixinContent.setTargetUrl(linkUrl);
        // weixinContent.setShareMedia(localImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setTitle(intentTitle);
        circleMedia.setShareContent(intentTitle + "," + linkUrl);
        circleMedia.setShareImage(localImage);
        circleMedia.setAppWebSite("http://");
        circleMedia.setTargetUrl(linkUrl);
        mController.setShareMedia(circleMedia);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent("");
        mController.setShareMedia(sinaContent);
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private void shareToWeiChatCircle(String linkUrl) {

        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        // 添加微信平台
        try {
            UMWXHandler wxHandler = new UMWXHandler(WebSubDataActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxHandler.addToSocialSDK();
            // 支持微信朋友圈
            UMWXHandler wxCircleHandler = new UMWXHandler(WebSubDataActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxCircleHandler.setToCircle(true);
            wxCircleHandler.addToSocialSDK();

            // 设置微信朋友圈分享内容
            CircleShareContent circleMedia = new CircleShareContent();
            circleMedia.setShareContent(webTitle);
            // 设置朋友圈title
            circleMedia.setTitle(webTitle);
            circleMedia.setShareImage(new UMImage(this, R.drawable.logo));
            if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
                circleMedia.setShareImage(new UMImage(this, R.drawable.logo_teacher));
            }
            circleMedia.setTargetUrl(linkUrl);
            mController.setShareMedia(circleMedia);
            SNSP = new SocializeListeners.SnsPostListener() {
                @Override
                public void onStart() {
                    MyLog.getLogger(TAG).d("分享调用kais weixin2222222222222---");
                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
                    MyLog.getLogger(TAG).d("分享调用异常 weixin77777777777---"+i);
                }
            };
            mController.postShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, null);

        } catch (Exception e) {
            MyLog.getLogger(TAG).d("分享调用异常 weixin Circle" + e.getMessage());
        }
    }

    private void shareToWeiChat(String linkUrl) {
        // 添加微信平台
        try {
            UMWXHandler wxHandler = new UMWXHandler(WebSubDataActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxHandler.addToSocialSDK();
            // 设置微信好友分享内容
            WeiXinShareContent weixinContent = new WeiXinShareContent();
            // 设置分享文字
            weixinContent.setShareContent(webTitle);
            // 设置title
            weixinContent.setTitle(intentTitle);
            // 设置分享内容跳转URL
            weixinContent.setTargetUrl(linkUrl);
            // 设置分享图片
            UMImage img = new UMImage(this, R.drawable.logo);
            if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
                img = new UMImage(this, R.drawable.logo_teacher);
            }
            weixinContent.setShareImage(img);
            mController.setShareMedia(weixinContent);
            mController.postShare(this, SHARE_MEDIA.WEIXIN, null);
        } catch (Exception e) {
            MyLog.getLogger(TAG).d("分享调用异常 weixin" + e.getMessage());
        }
    }

    /**
     * @return
     */
    private void shareToQQPlatform(String linkUrl) {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        try {
            UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(WebSubDataActivity.this,
                    SysConstants.QQ_APPID, SysConstants.QQ_APPKEY);
            qqSsoHandler.setTargetUrl(linkUrl);
            qqSsoHandler.addToSocialSDK();

            QQShareContent qqShareContent = new QQShareContent();
            // 设置分享图片
            UMImage img = new UMImage(this, R.drawable.logo);
            if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
                img = new UMImage(this, R.drawable.logo_teacher);
            }
            qqShareContent.setShareImage(img);
            // 设置分享文字
            qqShareContent.setShareContent(webTitle);
            qqShareContent.setTitle(intentTitle);
            qqShareContent.setTargetUrl(linkUrl);
            mController.setShareMedia(qqShareContent);
            mController.postShare(this, SHARE_MEDIA.QQ, null);
        } catch (Exception e) {
            MyLog.getLogger(TAG).d("分享调用异常 qq" + e.getMessage());
        }

    }

    private void shareToSinaWeibo(final String linkUrl) {
        // 设置新浪微博SSO handler
        try {
            mController.getConfig().setSsoHandler(new SinaSsoHandler());
            if (OauthHelper.isAuthenticated(this, SHARE_MEDIA.SINA)) {
                shareContent(SHARE_MEDIA.SINA, linkUrl);
            } else {
                mController.doOauthVerify(this, SHARE_MEDIA.SINA,
                        new SocializeListeners.UMAuthListener() {

                            @Override
                            public void onStart(SHARE_MEDIA arg0) {
                            }

                            @Override
                            public void onError(SocializeException arg0,
                                                SHARE_MEDIA arg1) {
                            }

                            @Override
                            public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
                                shareContent(SHARE_MEDIA.SINA, linkUrl);
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA arg0) {
                            }
                        });
            }
        } catch (Exception e) {
            MyLog.getLogger(TAG).d("分享调用异常 weibo" + e.getMessage());
        }

    }

    private void shareContent(SHARE_MEDIA media, String linkUrl) {
        mController.setShareContent(intentTitle + "," + linkUrl);
        mController.directShare(this, media, null);
    }


    @SuppressLint("NewApi")
    public void copyTextToBoard(String string) {
        if (TextUtils.isEmpty(string))
            return;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(string);
        } else {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 将内容写入剪贴板
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, string));
        }
        Toast.makeText(this, "复制链接成功", Toast.LENGTH_SHORT).show();
    }

    public static void invoke(Context context, String itemUrl, String title) {
        Intent intent = new Intent(context, WebSubDataActivity.class);
        intent.putExtra("itemUrl", itemUrl);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void invoke(Context context, boolean isWuyou, String itemUrl, String title) {
        Intent intent = new Intent(context, WebSubDataActivity.class);
        intent.putExtra("itemUrl", itemUrl);
        intent.putExtra("isWuyou", isWuyou);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    public void onStartDownload() {
    }

    @Override
    public void onProgress(long current, long total) {
    }

    @Override
    public void onFinished(int resultCode, String filePath) {
        if (resultCode == 1) {
            updateWebView(filePath);
        } else {
            updateWebView("");

        }
    }

    protected void onDestroy() {
        if (webView != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (intentTitle.equals("理解孩子")){
            String str = itemUrl+"";
            String spStr[] = str.split("&");
            String id = spStr[1];
            String ID = id.substring(3);
            getService().getDataReportManager().reportEventBid(EventType.ARTICLE_OUT, ID+"");
        }
        super.finish();
    }
}
