package com.xcsd.app.teacher.activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.adapter.ExpressionAdapter;
import com.tuxing.app.easemob.adapter.ExpressionPagerAdapter;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.easemob.widget.ExpandGridView;
import com.xcsd.app.teacher.adapter.BaoDianInfoAdapter;
import com.tuxing.app.qzq.util.OnHideKeyboardListener;
import com.tuxing.app.qzq.view.MessageEditText;
import com.tuxing.app.share.widget.ShareDialog;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyToast;
import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.TargetType;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.modle.Knowledge;
import com.tuxing.sdk.utils.CollectionUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.maxwin.view.XListView;

/**
 * 宝典详情
 * Created by liangyanqiao on 15/12/4.
 */
public class BaoDianInfoActivity extends BaseActivity implements View.OnClickListener,
        XListView.IXListViewListener{

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private RelativeLayout webView_Error_Rl;
    private XListView mListView;
    private BaoDianInfoAdapter mBaoDianInfoAdapter;
    private CheckBox mCheckBoxComment, mCheckBoxSupport, mCheckBoxShare;
    private final UMSocialService mController = UMServiceFactory.getUMSocialService(SysConstants.DESCRIPTOR);
    private String currentPageUrl = "";
    private String shareTitle = "";
    private String webTitle;
    private LinearLayout mCheckBox_ll;
    private EditText mComment_et;
    private Button faceBtn;
    private MessageEditText msgEt;
    private ViewPager facePager;
    private LinearLayout faceLl;
    private LinearLayout page_select;
    private Button send;
    private LinearLayout mCommentViews_ll;
    private List<String> reslist;
    private LinearLayout mCommentInput_ll;
    private int sendTag = -1;
    private long knowledgeId;
    private List<Comment> commentList;
    private Knowledge knowledge;
    private RelativeLayout mBottomRl;
    private View headerView;
    private boolean hasMore = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 404:
                    // 主页不存在
                    // 载入本地assets文件夹下面的错误提示页面404.html
                    webView_Error_Rl.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    break;
                default:
                    mWebView.setVisibility(View.VISIBLE);
                    webView_Error_Rl.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.baodian_info_layout);
        initData();
        initView();
        initFaceView();

    }

    private void initData() {
        Intent intent = getIntent();
        currentPageUrl = intent.getStringExtra("itemUrl");
        knowledgeId = intent.getLongExtra("itemId", 0l);
        knowledge = (Knowledge) intent.getSerializableExtra("knowledge");
        commentList = new ArrayList<>();
        getKnowledgeData();

    }

    private void getKnowledgeData() {
        getService().getCommentManager().getLatestComment(knowledgeId, TargetType.KNOWLEDGE);

    }

    private void initView() {
        setTitle(getString(R.string.baodian_info_title));
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        headerView = LayoutInflater.from(this).inflate(R.layout.baodian_info_header, null);
        mWebView = (WebView) headerView.findViewById(R.id.bao_dian_info_webView);
        mProgressBar = (ProgressBar) headerView.findViewById(R.id.web_view_progressbar);
        webView_Error_Rl = (RelativeLayout) headerView.findViewById(R.id.homepage_web_view_error_rl);
        mListView = (XListView) findViewById(R.id.bao_dian_info_listview);
        mBottomRl = (RelativeLayout) findViewById(R.id.bao_dian_info_bottom_rl);

        mBaoDianInfoAdapter = new BaoDianInfoAdapter(BaoDianInfoActivity.this, commentList);

        mCommentViews_ll = (LinearLayout) findViewById(R.id.comment_views_ll);//正常展示在底部的view布局
        mCheckBoxComment = (CheckBox) findViewById(R.id.bao_dian_comment);
        mCheckBoxSupport = (CheckBox) findViewById(R.id.bao_dian_support);
        mCheckBoxShare = (CheckBox) findViewById(R.id.bao_dian_share);
        mComment_et = (EditText) findViewById(R.id.comment_et);
        mCheckBox_ll = (LinearLayout) findViewById(R.id.three_checkbox_ll);

        //设置view属性
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
//      mWebView.setBackgroundColor(0); // 设置背景色
//      mWebView.getBackground().setAlpha(255); // 设置填充透明度 范围：0-255
        mListView.addHeaderView(headerView);
        mListView.setAdapter(mBaoDianInfoAdapter);

        if(knowledge != null){
            mCheckBoxComment.setText(knowledge.getCommentCount()+"");
            mCheckBoxSupport.setText(knowledge.getThankCount() + "");
            //设置check，但不设置selected，则在获取checkbox选择状态时，获取失败，
            mCheckBoxSupport.setChecked(knowledge.isThanked());
        }

        //初始化界面分享
        initShareConfig(currentPageUrl);

        //注册监听
        webView_Error_Rl.setOnClickListener(this);
        mCheckBoxComment.setOnClickListener(this);
        mCheckBoxSupport.setOnCheckedChangeListener(new onSupportCheckListener());
        mCheckBoxShare.setOnClickListener(this);
        mComment_et.setOnClickListener(this);
        mListView.setXListViewListener(this);
        mListView.setPullRefreshEnable(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //把网页的title作为分享时的内容
                webTitle = title;
                if (TextUtils.isEmpty(webTitle)) {
                    webTitle = shareTitle;
                }
            }
        });
        if (PhoneUtils.isNetworkAvailable(mContext)) {
            updateWebView(currentPageUrl);
        } else {
            updateWebView("");
            Toast.makeText(mContext, mContext.getString(com.tuxing.app.R.string.net_slowly), Toast.LENGTH_SHORT).show();
        }
    }

    public void initFaceView(){
        faceBtn = (Button) findViewById(R.id.mailbox_emoticons);
        msgEt = (MessageEditText) findViewById(R.id.mailbox_info_et);
        facePager = (ViewPager) findViewById(R.id.mailbox_Pager);
        faceLl = (LinearLayout) findViewById(R.id.ll_face_container);
        mCommentInput_ll = (LinearLayout) findViewById(R.id.comment_input_ll);
        page_select = (LinearLayout) findViewById(R.id.mailbox_select);
        send = (Button) findViewById(R.id.mailbox_send);
        faceBtn.setOnClickListener(this);
        send.setOnClickListener(this);
        msgEt.setOnClickListener(this);
        msgEt.requestFocus();
        msgEt.addTextChangedListener(new MaxLengthWatcher(200, msgEt));
        // 表情list
        reslist = getExpressionRes(SmileUtils.emoticons.size());
        msgEt.setListener(new OnHideKeyboardListener() {
            @Override
            public void onHide() {
                hideKeyboard();

                page_select.setVisibility(View.GONE);
                faceLl.setVisibility(View.GONE);
                mCommentInput_ll.setVisibility(View.GONE);//带表情输入框的布局
                mCommentViews_ll.setVisibility(View.VISIBLE);//原始底部布局
            }

        });
        msgEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    faceLl.setVisibility(View.GONE);
                }
            }
        });
        initFaceViewPager();
    }

    public void initFaceViewPager(){
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        int count = SmileUtils.emoticons.size()%20;
        if(count!=0)
            count = SmileUtils.emoticons.size()/20+1;
        else
            count = SmileUtils.emoticons.size()/20;
        for(int i=0;i<count;i++){
            View gv = getGridChildView(i,count);
            views.add(gv);
        }
        facePager.setAdapter(new ExpressionPagerAdapter(views));
        facePager.setOnPageChangeListener(new GuidePageChangeListener());
        for(int i =0;i<views.size();i++){
            ImageView image = new ImageView(mContext);
            if(i == 0){
                image.setImageResource(R.drawable.page_focused);
            }
            else{
                image.setImageResource(R.drawable.page_unfocused);
            }
            image.setPadding(Utils.dip2px(mContext, 10), 0, 0, 40);
            image.setId(i);
            page_select.addView(image);
        }
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i,int max) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if(max==i+1){
            List<String> list1 = reslist.subList(i*20,reslist.size());
            list.addAll(list1);
        }else{
            List<String> list1 = reslist.subList(i*20,(i+1)*20);
            list.addAll(list1);
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情

                    if (filename != "delete_expression") { // 不是删除键，显示表情
                        // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                        Class clz = Class.forName("com.tuxing.app.easemob.util.SmileUtils");
                        Field field = clz.getField(filename);
                        msgEt.append(SmileUtils.getSmiledText(BaoDianInfoActivity.this,
                                (String) field.get(null)));
                    } else { // 删除文字或者表情
                        if (!TextUtils.isEmpty(msgEt.getText())) {

                            int selectionStart = msgEt.getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = msgEt.getText().toString();
                                String tempStr = body.substring(0, selectionStart);
                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                if (i != -1) {
                                    CharSequence cs = tempStr.substring(i, selectionStart);
                                    if (SmileUtils.containsKey(cs.toString()))
                                        msgEt.getEditableText().delete(i, selectionStart);
                                    else
                                        msgEt.getEditableText().delete(selectionStart - 1,
                                                selectionStart);
                                } else {
                                    msgEt.getEditableText().delete(selectionStart - 1, selectionStart);
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null){
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }}

    }

    private void initShareConfig(String url) {
        // 设置分享的内容
        setShareContent(url);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        Long maxCommentId = Long.MAX_VALUE;
        if(!CollectionUtils.isEmpty(commentList)) {
            maxCommentId = commentList.get(commentList.size() - 1).getCommentId();
        }

        getService().getCommentManager().getHistoryComment(knowledgeId, TargetType.KNOWLEDGE, maxCommentId);
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
            mBottomRl.setVisibility(View.VISIBLE);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_web_view_error_rl:
                if (PhoneUtils.isNetworkAvailable(mContext)) {
                    mWebView.reload();
                    showProgressDialog(mContext, "", true, null);
                    mWebView.clearCache(true);
                    mWebView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.clearHistory();
                        }
                    }, 1000);
                    updateWebView(currentPageUrl);
                } else {
                    MyToast.showToast(this, this.getString(com.tuxing.app.R.string.net_slowly), 1000);
                }
                break;
            case R.id.comment_et:
            case R.id.bao_dian_comment:
                mCommentViews_ll.setVisibility(View.GONE);
                mCommentInput_ll.setVisibility(View.VISIBLE);
                showInput(msgEt);
                break;
            case R.id.bao_dian_share:
                shareCurrentPage(currentPageUrl);
                break;
            case R.id.mailbox_emoticons:
                msgEt.setFocusable(true);
                if(faceLl.getVisibility() == View.VISIBLE){
                    faceLl.setVisibility(View.GONE);
                    page_select.setVisibility(View.GONE);
                }else{
                    hideKeyboard();
                    faceLl.setVisibility(View.VISIBLE);
                    page_select.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.mailbox_send:
                if(msgEt.getText().toString().trim().length() > 0){
                    hideKeyboard();
                    mCommentViews_ll.setVisibility(View.VISIBLE);
                    mCommentInput_ll.setVisibility(View.GONE);
                    faceLl.setVisibility(View.GONE);
                    page_select.setVisibility(View.GONE);
                    SendContent();
                }else{
                    showToast("发送内容不能为空");
                }
                break;
            case R.id.mailbox_info_et:
                if(faceLl.getVisibility() == View.VISIBLE){
                    faceLl.setVisibility(View.GONE);
                    page_select.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mProgressBar.setProgressDrawable(null);
        mWebView.reload();
    }


    /**
     * 根据不同的平台设置不同的分享内容
     */
    private void setShareContent(String linkUrl) {

        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        UMImage localImage = new UMImage(BaoDianInfoActivity.this, com.tuxing.app.R.drawable.logo);
        localImage = new UMImage(BaoDianInfoActivity.this, com.tuxing.app.R.drawable.logo_teacher);
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(shareTitle);
        weixinContent.setShareContent(shareTitle + "," + linkUrl);
        weixinContent.setShareImage(localImage);
        weixinContent.setAppWebSite("http://");
        weixinContent.setTargetUrl(linkUrl);
        //weixinContent.setShareMedia(localImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setTitle(shareTitle);
        circleMedia.setShareContent(shareTitle + "," + linkUrl);
        circleMedia.setShareImage(localImage);
        circleMedia.setAppWebSite("http://");
        circleMedia.setTargetUrl(linkUrl);
        mController.setShareMedia(circleMedia);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent("");
        mController.setShareMedia(sinaContent);
    }
    /**
     * 分享
     * **/
    public void shareCurrentPage(final String linkUrl) {
        final ShareDialog dialog = new ShareDialog(BaoDianInfoActivity.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(getResources().getString(com.tuxing.app.R.string.share_to));
        dialog.setOnPlatformClickListener(new ShareDialog.OnSharePlatformClick() {
            @Override
            public void onPlatformClick(int id) {
                if (id == com.tuxing.app.R.id.ly_share_weichat_circle) {
                    shareToWeiChatCircle(linkUrl);
                } else if (id == com.tuxing.app.R.id.ly_share_weichat) {
                    shareToWeiChat(linkUrl);
                } else if (id == com.tuxing.app.R.id.ly_share_qq) {
                    shareToQQPlatform(linkUrl);
                } else if (id == com.tuxing.app.R.id.ly_share_copy_link) {
                    copyTextToBoard(linkUrl);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void updateWebView(String url) {
        disProgressDialog();
        Message msg = new Message();
        if (TextUtils.isEmpty(url)) {
            msg.what = 404;
        } else {
            msg.what = 405;
            Map<String,String> extraHeaders = new HashMap<String, String>();
            extraHeaders.put("token", getService().getUserToken());
            mWebView.loadUrl(url, extraHeaders);
        }
        handler.sendMessage(msg);
    }

    /**
     * @功能描述 : 朋友圈
     * @return
     */
    private void shareToWeiChatCircle(String linkUrl) {

        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        // 添加微信平台
        try {
            UMWXHandler wxHandler = new UMWXHandler(BaoDianInfoActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxHandler.addToSocialSDK();
            // 支持微信朋友圈
            UMWXHandler wxCircleHandler = new UMWXHandler(BaoDianInfoActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxCircleHandler.setToCircle(true);
            wxCircleHandler.addToSocialSDK();

            // 设置微信朋友圈分享内容
            CircleShareContent circleMedia = new CircleShareContent();
            circleMedia.setShareContent(webTitle);
            // 设置朋友圈title
            circleMedia.setTitle(webTitle);
            circleMedia.setShareImage(new UMImage(this, com.tuxing.app.R.drawable.logo));
            circleMedia.setShareImage(new UMImage(this, com.tuxing.app.R.drawable.logo_teacher));
            circleMedia.setTargetUrl(linkUrl);
            mController.setShareMedia(circleMedia);
            mController.postShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, null);
        }catch (Exception e){
            MyLog.getLogger(TAG).d("分享调用异常 weixin Circle" + e.getMessage());
        }
    }
    /**
     * @功能描述 : 朋微信好友
     * @return
     */
    private void shareToWeiChat(String linkUrl) {
        // 添加微信平台
        try {
            UMWXHandler wxHandler = new UMWXHandler(BaoDianInfoActivity.this,
                    SysConstants.WEICHAT_APPID, SysConstants.WEICHAT_SECRET);
            wxHandler.addToSocialSDK();
            // 设置微信好友分享内容
            WeiXinShareContent weixinContent = new WeiXinShareContent();
            // 设置分享文字
            weixinContent.setShareContent(webTitle);
            // 设置title
            weixinContent.setTitle(shareTitle);
            // 设置分享内容跳转URL
            weixinContent.setTargetUrl(linkUrl);
            // 设置分享图片
            UMImage img = new UMImage(this, com.tuxing.app.R.drawable.logo);
            img = new UMImage(this, com.tuxing.app.R.drawable.logo_teacher);
            weixinContent.setShareImage(img);
            mController.setShareMedia(weixinContent);
            mController.postShare(this, SHARE_MEDIA.WEIXIN, null);
        }catch (Exception e){
            MyLog.getLogger(TAG).d("分享调用异常 weixin" + e.getMessage());
        }
    }
    /**
     * @功能描述 : QQ
     * @return
     */
    private void shareToQQPlatform(String linkUrl) {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        try {
            UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(BaoDianInfoActivity.this,
                    SysConstants.QQ_APPID, SysConstants.QQ_APPKEY);
            qqSsoHandler.setTargetUrl(linkUrl);
            qqSsoHandler.addToSocialSDK();

            QQShareContent qqShareContent = new QQShareContent();
            // 设置分享图片
            UMImage img = new UMImage(this, com.tuxing.app.R.drawable.logo);
            img = new UMImage(this, com.tuxing.app.R.drawable.logo_teacher);
            qqShareContent.setShareImage(img);
            // 设置分享文字
            qqShareContent.setShareContent(webTitle);
            qqShareContent.setTitle(shareTitle);
            qqShareContent.setTargetUrl(linkUrl);
            mController.setShareMedia(qqShareContent);
            mController.postShare(this, SHARE_MEDIA.QQ, null);
        }catch (Exception e){
            MyLog.getLogger(TAG).d("分享调用异常 qq" + e.getMessage());
        }

    }

    /**
     * @功能描述 : 张贴板
     * @return
     */
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
    /**
     * @功能描述 : 点赞
     * @return
     */
    public void sendSupport(long targetId) {
        //评论类型，1是点赞，2是文字
        showProgressDialog(BaoDianInfoActivity.this, null, true, null);
        getService().getCommentManager().publishComment(targetId, TargetType.KNOWLEDGE, null, null, null, CommentType.LIKE);
        sendTag = 0;
    }
    /**
     * 评论
     * */
    public void SendContent(){
        sendTag = 1;
        showProgressDialog(mContext, "", true, null);
        String content = msgEt.getText().toString().trim();
        if(content.length() > 0){
            getService().getCommentManager().publishComment(knowledgeId, TargetType.KNOWLEDGE, content,
                    null,null, CommentType.REPLY);
            msgEt.setText("");
        }else
            showToast("发送内容不能为空");
    }

    public void onEventMainThread(CommentEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case REPLAY_KNOWLEDGE_SUCCESS:
                    if(sendTag == 0){
                        mCheckBoxSupport.setSelected(true);
                        int supportNum = Integer.parseInt(mCheckBoxSupport.getText().toString());
                        mCheckBoxSupport.setText(supportNum+1+ "");
                        showAndSaveLog(TAG, "REPLAY_ANSWER_SUCCESS 赞／回复 成功 ", false);
                        Intent broadCastIntent = new Intent(SysConstants.UPDATBAODIANLIST);
                        broadCastIntent.putExtra("knowledgeId", knowledgeId);
                        sendBroadcast(broadCastIntent);
                    }else{
                        int commentNum = Integer.parseInt(mCheckBoxComment.getText().toString());
                        mCheckBoxComment.setText(commentNum+1+"");

                        showToast("发布评论成功");
                        if(!hasMore){
                            commentList.addAll(event.getComments());
                            mBaoDianInfoAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case REPLAY_KNOWLEDGE_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "REPLAY_ANSWER_SUCCESS 赞／回复 失败 ", false);
                    break;
                case GET_LATEST_KNOWLEDGE_COMMENTS_SUCCESS://显示评论列表数据
                    mBaoDianInfoAdapter.setData(event.getComments());
                    hasMore = event.isHasMore();
                    mListView.setPullLoadEnable(hasMore);
                    break;
                case GET_LATEST_KNOWLEDGE_COMMENTS_FAILED:
                    showToast(event.getMsg());
                    break;
                case GET_HISTORY_KNOWLEDGE_COMMENTS_SUCCESS:
                    commentList.addAll(event.getComments());
                    mBaoDianInfoAdapter.notifyDataSetChanged();
                    hasMore = event.isHasMore();
                    mListView.setPullLoadEnable(hasMore);
                    break;
                case GET_HISTORY_KNOWLEDGE_COMMENTS_FAILED:
                    showToast(event.getMsg());
                    break;
            }

        }
    }

    public class MaxLengthWatcher implements TextWatcher {
        private int maxLen = 0;
        private EditText editText = null;
        public MaxLengthWatcher(int maxLen, EditText editText) {
            this.maxLen = maxLen;
            this.editText = editText;
        }

        public void afterTextChanged(Editable arg0) {
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            Editable editable = editText.getText();
            int len = editable.length();
            if(len >= maxLen){
                showToast(getResources().getString(R.string.edit_number_count));
            }
        }

    }
    /**
     *  指引页面改监听器
     *  */
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            int count = page_select.getChildCount();
            for(int i=0; i< count; i++){
                ImageView view = (ImageView) page_select.getChildAt(i);
                if(i == arg0 )
                    view.setImageResource(R.drawable.page_focused);
                else{
                    view.setImageResource(R.drawable.page_unfocused);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(View.GONE == mCheckBox_ll.getVisibility()){
                mCheckBox_ll.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(BaoDianInfoActivity.this, 0f),
                        Utils.dip2px(BaoDianInfoActivity.this, 31f));
                params.weight = 1;
                mComment_et.setLayoutParams(params);
            }else
                finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private class onSupportCheckListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
            if(!compoundButton.isChecked()){
                showToast(getResources().getString(R.string.question_prompt));
                compoundButton.setChecked(true);
            }else {
                sendSupport(knowledgeId);
            }
        }
    }
}