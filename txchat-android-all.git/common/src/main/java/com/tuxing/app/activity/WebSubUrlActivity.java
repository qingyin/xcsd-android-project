package com.tuxing.app.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.share.widget.ShareDialog;
import com.tuxing.app.util.*;

import com.tuxing.sdk.utils.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSubUrlActivity extends BaseActivity implements OnClickListener{

    private String intentTitle = "";
    private String itemUrl = "";
    TextView tvTitle;
    WebView webView;
    ProgressBar pb;
    LinearLayout btnTitleLeft;
    public RelativeLayout iv_homepage_webview_error;
    private Button tv_right;
//    private Button tv_right_close;
    private String webTitle = "";
    List<String> urlList = new ArrayList<String>();
    private boolean isChoujiangJlu = false;
    private int index = 0;
    ImageButton right_button;
    private boolean isteacher = false;
    private boolean isparent = false;
    TextView tv_back;

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
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        intentTitle = intent.getStringExtra("title");
        itemUrl = intent.getStringExtra("itemUrl");
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(intentTitle);

        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        btnTitleLeft.setVisibility(View.VISIBLE);
        btnTitleLeft.setOnClickListener(this);
        tv_right = (Button)findViewById(R.id.tv_right);
        tv_right.setVisibility(View.GONE);
        tv_right.setOnClickListener(this);

        tv_back = (TextView) findViewById(R.id.tv_back);

        isteacher = TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion());
        isparent = TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion());
        if (isteacher) {
            tv_back.setTextColor(getResources().getColor(R.color.text_teacher));
            Drawable drawable = null;
            drawable = getResources().getDrawable(R.drawable.ic_back_title_t);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_back.setCompoundDrawables(drawable, null, null, null);
        } else if (isparent) {
            tv_back.setTextColor(getResources().getColor(R.color.text_parent));
            Drawable drawable = null;
            drawable = getResources().getDrawable(R.drawable.ic_back_title_p);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_back.setCompoundDrawables(drawable, null, null, null);
        }

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
                if(!TextUtils.isEmpty(title)){
//                    tvTitle.setText(title);
                }

                if(intentTitle.equals(getResources().getString(R.string.find_activity))){
                    if (view.getUrl().equals(SysConstants.HUODONGZQ) || !isChoujiangJlu){
                        tv_right.setText(getResources().getString(R.string.cj_tecord));
                }else{
                        tv_right.setText(getResources().getString(R.string.close));
                }
                }else if(intentTitle.equals(getResources().getString(R.string.integral_shop))){
                    tv_right.setText(getResources().getString(R.string.close));
                }else{
                    tv_right.setText(getResources().getString(R.string.close));
                }
            }
        });
        webView.setWebViewClient(new MyWebViewClient());
        iv_homepage_webview_error = (RelativeLayout) findViewById(R.id.iv_homepage_webview_error);
        iv_homepage_webview_error.setOnClickListener(this);
        if (PhoneUtils.isNetworkAvailable(mContext)) {
            if(urlList.contains(itemUrl)){
                urlList.remove(itemUrl);
                urlList.add(itemUrl);
            }else{
                urlList.add(itemUrl);
            }
            updateWebView(urlList);
        } else {
            updateWebView(null);
            Toast.makeText(mContext, mContext.getString(R.string.net_slowly),
                    Toast.LENGTH_SHORT).show();
        }
        initShareConfig(itemUrl);
    }

    public void updateWebView(List<String> dataList) {
        Message msg = new Message();
        disProgressDialog();
        if (dataList == null || 0 >= dataList.size()) {
            msg.what = 404;
        } else {
            msg.what = 405;
            String data = dataList.get(dataList.size() - 1);
            if(!TextUtils.isEmpty(data)){
                    Map<String,String> extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("token", getService().getUserToken());
                    webView.loadUrl(data, extraHeaders);
                hideInput();

                if(intentTitle.equals(getResources().getString(R.string.find_activity))){
                    if (data.equals(SysConstants.HUODONGZQ) || !isChoujiangJlu){
                        tv_right.setText(getResources().getString(R.string.cj_tecord));
                    }else{
                        tv_right.setText(getResources().getString(R.string.close));
                    }
                }else if(intentTitle.equals(getResources().getString(R.string.integral_shop))){
                    tv_right.setText(getResources().getString(R.string.close));
                }else{
                    tv_right.setText(getResources().getString(R.string.close));
                }

            }
        }
        handler.sendMessage(msg);
    }


    /**
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                if(url.startsWith("weixin")){
                    try {
                        Uri uri =Uri.parse(url);
                        Intent it = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(it);
                    }catch (Exception e){
                        Log.e("weixin error:", e.getMessage());
                        showToast("微信打开失败,请检查");
                    }
                }else{
                    if(urlList.contains(url)){
                        urlList.remove(url);
                        urlList.add(url);
                    }else{
                        urlList.add(url);
                    }
                    updateWebView(urlList);
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mContext != null && !WebSubUrlActivity.this.isFinishing()) {
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
                if(urlList != null && urlList.size() > 0){
                    urlList.remove(urlList.size() - 1);
                    if(urlList.size() > 0){
                        if(index >= urlList.size()){
                            isChoujiangJlu = false;
                        }
                        updateWebView(urlList);
                    }else{
                        finish();
                    }
                }
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
                if(urlList != null && urlList.size() > 0){
                    urlList.remove(urlList.size() - 1);
                    if(urlList.size() > 0){
                        if(index >= urlList.size()){
                            isChoujiangJlu = false;
                        }
                        updateWebView(urlList);
                    }else{
                        finish();
                    }
                }
//                webView.goBack();
            } else {
                finish();
            }
        }else if(id == R.id.tv_right){
            if( tv_right.getText().toString().equals(getResources().getString(R.string.close))){
                finish();
            }else{
                isChoujiangJlu = true;
                index = urlList.size();
                itemUrl = SysConstants.CHOUJIANGJL;
                if(urlList.contains(itemUrl)){
                    urlList.remove(itemUrl);
                    urlList.add(itemUrl);
                }else{
                    urlList.add(itemUrl);
                }
                updateWebView(urlList);
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
                if(urlList.contains(itemUrl)){
                    urlList.remove(itemUrl);
                    urlList.add(itemUrl);
                }else{
                    urlList.add(itemUrl);
                }
                updateWebView(urlList);
            } else {
                Toast.makeText(mContext,
                        mContext.getString(R.string.net_slowly),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }



    /*** 分享 **/

    public void handleShare(final String linkUrl) {
        final ShareDialog dialog = new ShareDialog(WebSubUrlActivity.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(getResources().getString(R.string.share_to));
        dialog.setOnPlatformClickListener(new ShareDialog.OnSharePlatformClick() {
            @Override
            public void onPlatformClick(int id) {
                if (id == R.id.ly_share_weichat_circle) {
                    shareToWeiChatCircle(linkUrl);
                } else if (id == R.id.ly_share_weichat) {
                    shareToWeiChat(linkUrl);
                } else if (id == R.id.ly_share_qq) {
                    shareToQQPlatform(linkUrl);
                } else if (id == R.id.ly_share_sina_weibo) {
                    shareToSinaWeibo(linkUrl);
                } else if (id == R.id.ly_share_copy_link) {
                    copyTextToBoard(linkUrl);
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

        UMImage localImage = new UMImage(WebSubUrlActivity.this, R.drawable.logo);
        if(TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
            localImage = new UMImage(WebSubUrlActivity.this, R.drawable.logo_teacher);
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
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void shareToWeiChatCircle(String linkUrl) {

        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        // 添加微信平台
        try {
            UMWXHandler wxHandler = new UMWXHandler(WebSubUrlActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxHandler.addToSocialSDK();
            // 支持微信朋友圈
            UMWXHandler wxCircleHandler = new UMWXHandler(WebSubUrlActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxCircleHandler.setToCircle(true);
            wxCircleHandler.addToSocialSDK();

            // 设置微信朋友圈分享内容
            CircleShareContent circleMedia = new CircleShareContent();
            circleMedia.setShareContent(webTitle);
            // 设置朋友圈title
            circleMedia.setTitle(webTitle);
            circleMedia.setShareImage(new UMImage(this, R.drawable.logo));
            if(TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
                circleMedia.setShareImage(new UMImage(this, R.drawable.logo_teacher));
            }
            circleMedia.setTargetUrl(linkUrl);
            mController.setShareMedia(circleMedia);
            mController.postShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, null);
        }catch (Exception e){
            MyLog.getLogger(TAG).d("分享调用异常 weixin Circle" + e.getMessage());
        }
    }

    private void shareToWeiChat(String linkUrl) {
        // 添加微信平台
        try {
            UMWXHandler wxHandler = new UMWXHandler(WebSubUrlActivity.this,
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
            if(TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
                img = new UMImage(this, R.drawable.logo_teacher);
            }
            weixinContent.setShareImage(img);
            mController.setShareMedia(weixinContent);
            mController.postShare(this, SHARE_MEDIA.WEIXIN, null);
        }catch (Exception e){
            MyLog.getLogger(TAG).d("分享调用异常 weixin" + e.getMessage());
        }
    }
    /**
     * @return
     */
    private void shareToQQPlatform(String linkUrl) {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        try {
            UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(WebSubUrlActivity.this,
                    SysConstants.QQ_APPID, SysConstants.QQ_APPKEY);
            qqSsoHandler.setTargetUrl(linkUrl);
            qqSsoHandler.addToSocialSDK();

            QQShareContent qqShareContent = new QQShareContent();
            // 设置分享图片
            UMImage img = new UMImage(this, R.drawable.logo);
            if(TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
                img = new UMImage(this, R.drawable.logo_teacher);
            }
            qqShareContent.setShareImage(img);
            // 设置分享文字
            qqShareContent.setShareContent(webTitle);
            qqShareContent.setTitle(intentTitle);
            qqShareContent.setTargetUrl(linkUrl);
            mController.setShareMedia(qqShareContent);
            mController.postShare(this, SHARE_MEDIA.QQ, null);
        }catch (Exception e){
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
        }catch (Exception e){
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
        if(sdk < android.os.Build.VERSION_CODES. HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(string);
        } else {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            // 将内容写入剪贴板
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, string));
        }
        Toast.makeText(this, "复制链接成功", Toast.LENGTH_SHORT).show();
    }

    public static void invoke(Context context,String itemUrl,String title) {
        Intent intent = new Intent(context, WebSubUrlActivity.class);
        intent.putExtra("itemUrl", itemUrl);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public void hideInput() {
        final View v = getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

}