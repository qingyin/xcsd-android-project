package com.xcsd.app.teacher.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.SysConstants;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.widget.ViewFlipper;

public class ShowWebSubUrlActivity extends BaseActivity implements OnClickListener{

    private String intentTitle = "";
    private String itemUrl = "";
    WebView webView;
    ProgressBar pb;
    public RelativeLayout iv_homepage_webview_error;

    List<String> urlList = new ArrayList<String>();
    private boolean isChoujiangJlu = false;
    private int index = 0;


    private WindowManager wm=null;
    private WindowManager.LayoutParams wmParams=null;

    private ImageView leftbtn=null;

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
        setContentView(R.layout.activity_game_webview);

        Intent intent = getIntent();
        itemUrl = intent.getStringExtra("itemUrl");

        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setMax(100);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setSavePassword(false);
//        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);



        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if (newProgress == 100) {
                    pb.setVisibility(View.GONE);
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

    }


    @Override
    public void onCancelAlert() {
        super.onCancelAlert();

    }

    @Override
    public void onConfirm() {
        super.onConfirm();
        SetClassWorkActivity.instance.finish();
        finish();
    }

    private void createLeftFloatView(){
        leftbtn=new ImageView(this);
        leftbtn.setImageResource(R.drawable.account_safety);
        leftbtn.setAlpha(0);
        leftbtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

            }
        });

        wmParams.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;
        wm.addView(leftbtn, wmParams);
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
            if (mContext != null && !ShowWebSubUrlActivity.this.isFinishing()) {
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
                        showDialog("提示", "你确定要离开，回到首页吗？", "取消", "确定");
                    }
                }
                return true;
            } else {
                showDialog("提示", "你确定要离开，回到首页吗？", "取消", "确定");
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

    private final UMSocialService mController = UMServiceFactory
            .getUMSocialService(SysConstants.DESCRIPTOR);
    private boolean garden;



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
        Intent intent = new Intent(context, ShowWebSubUrlActivity.class);
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