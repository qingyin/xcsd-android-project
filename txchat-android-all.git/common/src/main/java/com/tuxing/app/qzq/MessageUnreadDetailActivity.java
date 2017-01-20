package com.tuxing.app.qzq;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.adapter.ExpressionAdapter;
import com.tuxing.app.easemob.adapter.ExpressionPagerAdapter;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.easemob.widget.ExpandGridView;
import com.tuxing.app.qzq.adapter.MessageGridViewListAdapter;
import com.tuxing.app.qzq.util.OnHideKeyboardListener;
import com.tuxing.app.qzq.view.MessageEditText;
import com.tuxing.app.ui.dialog.CommonDialog;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.util.DateTimeUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.AoutLineLinearLayout;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.FeedEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;
import me.maxwin.view.XListView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessageUnreadDetailActivity extends BaseActivity implements XListView.IXListViewListener {

    private String currentTaskUri;
    TextView tv_name;
    TextView ctv;
    private MyGridView gv_img;
    LinearLayout ll_pop;
    ImageView iv_more;
    TextView tv_delete;
    TextView tv_time;
    TextView tv_praiseName;
    AoutLineLinearLayout ll_piarse;
  LinearLayout root_piarse_ll;
    MyImageView piarse_icon;

    private LinearLayout ll_send;
    private Button bt_add_face;
    private MessageEditText et_send;
    private Button bt_send;
    private Comment comment;
    private List<Comment> mComments;
    private List<Comment> mLikes;

    private LinearLayout llFace;
    private ViewPager viewPager;
    private LinearLayout page_select;
    private List<String> reslist;//表情

    String portrait = "";
    String content = "";
    List<String> picsList = new ArrayList<String>();
    private RoundImageView iv_portrait;
    private PopupWindow popupWindow;
    private Feed feed;
    private int mFlag = 0;//// 0表示正常下载，1表示下拉刷新，2表示加载更多,3表示评论，4表示回复,5删除,6 删除赞
    private int deletedIndex = 0;

    private long _replyUserId;
    private String _replyUserName;
    private ImageView iv_friend_arrow;
    private XListView xListView;
    private CommentListAdapter adapter;
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComments = new ArrayList<Comment>();
        mLikes = new ArrayList<Comment>();
        feed = (Feed) getIntent().getSerializableExtra("feed");
        showProgressDialog(mContext, "", true, null);
        MobclickAgent.onEvent(mContext, "qzq_info", UmengData.qzq_info);
        getService().getFeedManager().getLatestFeedComments(feed.getFeedId());
        LinearLayout rootView = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_my_circle_detail, null);
        headView = getLayoutInflater().inflate(R.layout.qinzi_unread_msg_detail, null);
        setContentLayout(rootView);
        initHeaderView();
        initView(rootView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(LinearLayout rootView) {
        setTitle(getResources().getString(R.string.detail));
        setRightNext(false, "", 0);
        setLeftBack("", true, false);
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setXListViewListener(this);

        bt_add_face = (Button) rootView.findViewById(R.id.bt_add_face);
        bt_add_face.setOnClickListener(this);
        ll_send = (LinearLayout) rootView.findViewById(R.id.ll_send);
        et_send = (MessageEditText) rootView.findViewById(R.id.et_send);
        et_send.addTextChangedListener(new MaxLengthWatcher(200, et_send));
        et_send.setOnClickListener(this);
        et_send.setListener(new OnHideKeyboardListener() {
            @Override
            public void onHide() {
                hideKeyboard();
                hideSoftInput();
            }

        });
        llFace = (LinearLayout) findViewById(R.id.llFace);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        page_select = (LinearLayout) findViewById(R.id.page_select);
        reslist = getExpressionRes(SmileUtils.emoticons.size());
        initViewPager();
//        rootView.setListener(new OnHideKeyboardListener() {
//            @Override
//            public void onHide() {
//                hideKeyboard();
//            }
//        });
        bt_send = (Button) rootView.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (llFace.getVisibility() == View.VISIBLE) {
                    llFace.setVisibility(View.GONE);
                    page_select.setVisibility(View.GONE);
                }
                if(checkFeedMute()){
                }else {
                    submitComment();
                }
            }
        });
        headView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ll_send.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    hideSoftInput();
                }
                return false;
            }
        });
    }

    public void updateAdapter() {
        //xListView.removeHeaderView(headView);
        fillPraise(mLikes);
        //xListView.addHeaderView(headView);
        if (adapter == null) {
            xListView.addHeaderView(headView);
            adapter = new CommentListAdapter(mContext, mComments);
            xListView.setAdapter(adapter);
        } else {
            adapter.setData(mComments);
        }
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(false);
    }

    private void initHeaderView() {
        ctv = (TextView) headView.findViewById(R.id.ctv);
        gv_img = (MyGridView) headView.findViewById(R.id.gv_img);
        tv_time = (TextView) headView.findViewById(R.id.tv_time);
        tv_praiseName = (TextView) headView.findViewById(R.id.tv_praiseName);
        tv_delete = (TextView) headView.findViewById(R.id.tv_delete);
        ll_piarse = (AoutLineLinearLayout) headView.findViewById(R.id.piarse_ll);
        root_piarse_ll = (LinearLayout) headView.findViewById(R.id.root_piarse_ll);
        iv_portrait = (RoundImageView) headView.findViewById(R.id.iv_portrait);
        tv_name = (TextView) headView.findViewById(R.id.tv_name);
        piarse_icon = (MyImageView) headView.findViewById(R.id.piarse_icon);
        iv_friend_arrow = (ImageView) headView.findViewById(R.id.iv_friend_arrow);
        ll_pop = (LinearLayout)headView.findViewById(R.id.ll_pop);
        iv_more = (ImageView) headView.findViewById(R.id.iv_more);
        piarse_line = headView.findViewById(R.id.piarse_line);
        initFeed(feed);
    }

    private void initFeed(Feed snspFeed) {
//        ctv.setText(snspFeed.getContent());
        if(TextUtils.isEmpty(snspFeed.getContent())){
            ctv.setVisibility(View.GONE);
        }else{
            ctv.setVisibility(View.VISIBLE);
            ctv.setText(SmileUtils.getSmiledText(mContext,
                    snspFeed.getContent()), TextView.BufferType.SPANNABLE);
        }
        gv_img.setSelector(new ColorDrawable(android.R.color.transparent));
        List<String> iconList = new ArrayList<String>();
        String json = snspFeed.getAttachments();
        JSONArray array = null;
        int flag = -1;
        try {
            array = new JSONArray(json);
            if (array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    iconList.add(array.getJSONObject(i).optString("url"));
                    flag = array.getJSONObject(i).optInt("type");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iconList.size() > 0) {
            gv_img.setVisibility(View.VISIBLE);
        } else
            gv_img.setVisibility(View.GONE);
        if (iconList.size() == 4) {// 如果图片个数为4个，则排列方式为上下各两个
            gv_img.setNumColumns(2);
            gv_img.setVerticalSpacing(dip2px(4));
            gv_img.setHorizontalSpacing(dip2px(4));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dip2px(160), LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, dip2px(10), 0, 0);
            gv_img.setLayoutParams(lp);
        } else if (iconList.size() == 1) {
            gv_img.setNumColumns(1);
            gv_img.setVerticalSpacing(dip2px(0));
            gv_img.setHorizontalSpacing(dip2px(0));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, dip2px(10), 0, 0);
            gv_img.setLayoutParams(lp);
        } else {
            gv_img.setNumColumns(3);
            gv_img.setVerticalSpacing(dip2px(4));
            gv_img.setHorizontalSpacing(dip2px(4));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dip2px(240), LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, dip2px(10), 0, 0);
            gv_img.setLayoutParams(lp);
        }
        gv_img.setAdapter(new MessageGridViewListAdapter(mContext, iconList, gv_img, flag));
        ll_pop.setTag(snspFeed);
        ll_pop.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Feed snspFeed = (Feed) v.getTag();

                popupWindow = new PopupWindow(getPopupWindowView(snspFeed), dip2px(160), dip2px(40), false);
                popupWindow.setFocusable(true);
                popupWindow.setTouchable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setAnimationStyle(R.style.msg_list_grid_animation);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                int[] location = new int[2];
                v.getLocationInWindow(location);
//                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, dip2px(100), location[1] - dip2px(3)); // 设置layout在PopupWindow中显示的位置
                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - popupWindow.getWidth() - 5, location[1] - popupWindow.getHeight() / 3);
            }
        });
        String value = DateTimeUtils.formatRelativeDate(DateTimeUtils.getDateTime(snspFeed.getPublishTime()));
        tv_time.setText(value);
        if (user!= null && snspFeed.getUserId() == user.getUserId()||
                    (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())&&
                            snspFeed.getUserType() == Constants.USER_TYPE.PARENT)) {//2.1加下删除家长feed的功能
            tv_delete.setVisibility(View.VISIBLE);
        } else {
            tv_delete.setVisibility(View.INVISIBLE);
        }
        tv_delete.setTag(snspFeed);
        tv_delete.setOnClickListener(delete_ClickListener);

        iv_portrait.setImageUrl(snspFeed.getUserAvatar(), R.drawable.default_avatar);
        iv_portrait.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        tv_name.setText(snspFeed.getUserName());
    }

    private void fillPraise(final List<Comment> all) {
        final List<User> praiseList = new ArrayList<User>();
        if (!CollectionUtils.isEmpty(all)) {
            for (Comment comment : all) {
                if (comment.getCommentType() == Constants.COMMENT_TYPE.LIKE) {
                    User user = new User();
                    user.setUserId(comment.getSenderId());
                    user.setUsername(comment.getSenderName());
                    user.setNickname(comment.getSenderName());
                    user.setAvatar(comment.getSenderAvatar());
                    praiseList.add(user);
                }
            }
        }
        if ((mLikes != null && mLikes.size() > 0) &&
                (mComments != null && mComments.size() > 0)) {
            piarse_line.setVisibility(View.VISIBLE);
        } else {
            piarse_line.setVisibility(View.GONE);
        }

        if ((mLikes != null && mLikes.size() > 0) ||
                (mComments != null && mComments.size() > 0)) {
            iv_friend_arrow.setVisibility(View.VISIBLE);
        } else {
            iv_friend_arrow.setVisibility(View.GONE);
        }
        if (praiseList != null && praiseList.size() > 0) {
        	root_piarse_ll.setVisibility(View.VISIBLE);
        } else {
        	root_piarse_ll.setVisibility(View.GONE);
        }
        root_piarse_ll.removeAllViews();
        ll_piarse.removeAllViews();
        if (praiseList.size() > 0) {
        	root_piarse_ll.addView(piarse_icon);
            for (int j = 0; j < praiseList.size(); j++) {
                View iconVIew = getLayoutInflater().inflate(R.layout.qinzi_unread_msg_detail_update_feed_icon_item, null);
                RoundImageView praiseIco = (RoundImageView) iconVIew.findViewById(R.id.feed_icon);
                praiseIco.setVisibility(View.VISIBLE);
                praiseIco.setImageUrl(praiseList.get(j).getAvatar(), R.drawable.default_avatar);
                iconVIew.setTag(j);

                iconVIew.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = (Integer) v.getTag();
                        Intent intent = new Intent(mContext, MyCircleListActivity.class);
                        intent.putExtra("userId", praiseList.get(index).getUserId());
                        intent.putExtra("userName", praiseList.get(index).getNickname());
                        startActivity(intent);
                    }
                });
                ll_piarse.addView(iconVIew);
            }
            root_piarse_ll.addView(ll_piarse);
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ll_send.getVisibility() == View.VISIBLE) {
//            hideKeyboard();
            if(TextUtils.isEmpty(et_send.getText().toString())){
                et_send.setText("");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getCurrentFocus() != null) {
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            hideKeyboard();
                        }
                    }
                }, 200);
                return true;
            }else
                return super.onKeyDown(keyCode, event);
        }else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_add_face) {
            hideSoftInput();
            if (llFace.getVisibility() == View.VISIBLE) {
                llFace.setVisibility(View.GONE);
                page_select.setVisibility(View.GONE);
            } else {
                llFace.setVisibility(View.VISIBLE);
                page_select.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.et_send) {
            llFace.setVisibility(View.GONE);
            page_select.setVisibility(View.GONE);
        }

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void submitComment() {
        if (mFlag == 3 || mFlag == 1) {//发表
            if (et_send.getText().toString().trim().length() > 0) {
                getService().getFeedManager().publishComment(feed.getFeedId(), et_send.getText().toString(),
                        null, null, SysConstants.commentType_2); // 攒
            } else {
                showToast("请输入内容");
                return;
            }
        } else if (mFlag == 4) {//回复
            if (et_send.getText().toString().trim().length() > 0) {

                getService().getFeedManager().publishComment(feed.getFeedId(), et_send.getText().toString(),
                        _replyUserId, _replyUserName, SysConstants.commentType_2); // 攒
                et_send.setHint("");
            } else {
                showToast("请输入内容");
                return;
            }
        }

        et_send.setText("");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (getCurrentFocus() != null) {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 200);
        hideKeyboard();
    }
    // 隐藏软键盘和输入跨国
    public void hideKeyboard() {
        et_send.clearFocus();
        et_send.setText("");
        ll_send.setVisibility(View.GONE);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        final View v = getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    long praiseId = 0;

    public View getPopupWindowView(final Feed sFeed) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_list_grid_item_popup, null);
        final CheckBox cb_praise = (CheckBox) view.findViewById(R.id.cb_praise);
        CheckBox cb_comment = (CheckBox) view.findViewById(R.id.cb_comment);
        for (Comment comment : mLikes) {
            if (user != null && comment.getCommentType() == 1 && comment.getSenderId() == user.getUserId()) {
                praiseId = comment.getCommentId();
                cb_praise.setChecked(true);
                cb_praise.setText("取消");
                break;
            }
        }
        cb_praise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    showProgressDialog(mContext,"",false,null);
                    cb_praise.setText("取消");
                    getService().getFeedManager().publishComment(feed.getFeedId(), "",
                            user.getUserId(), user.getUsername(), 1); // 攒
                } else {
                    cb_praise.setText("赞");
                    if (praiseId != 0) {
                        mFlag = 6;
                        showProgressDialog(mContext,"",false,null);
                        getService().getFeedManager().deleteFeedComment(praiseId); // 攒
                    }
                }

//                notifyDataSetChanged();
                if(ll_send.getVisibility() == View.VISIBLE){
                    hideKeyboard();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                popupWindow.dismiss();
            }
        });

        cb_comment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(checkFeedMute()){
                }else {
                    //对feed发表评论
                    popupWindow.dismiss();
                    comment = null;
                    et_send.setFocusable(true);
                    et_send.requestFocus();
                    et_send.setText("");
                    mFlag = 3;
                    if(ll_send.getVisibility() == View.GONE){
                        ll_send.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) ((Activity) mContext).getSystemService(mContext.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }, 500);
                    }
                }
            }
        });
        return view;
    }

    public void replyComment(Feed feed, int commentType, String content, long replyToUserid, String replyToUserName, Comment comment) {
        this.comment = comment;
        et_send.requestFocus();
        if (commentType == 2) {//评论
            if (replyToUserid > 0 && replyToUserName != null && !"".equals(replyToUserName)) {//回复某人
                mFlag = 4;
                et_send.setHint("回复" + comment.getSenderName() + ":");
                _replyUserId = replyToUserid;
                _replyUserName = replyToUserName;
            } else {
                mFlag = 3;
            }
        } else if (commentType == 1) {//赞
        }
        ll_send.setVisibility(View.VISIBLE);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public OnClickListener delete_ClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            showContextMenu(feed);
            MobclickAgent.onEvent(mContext,"qzq_del",UmengData.qzq_del);
        }
    };
    private View piarse_line;

    public void showContextMenu(final Feed feed) {
        final CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(mContext);
        dialog.setTitle("选择操作");
        dialog.setNegativeButton("取消", null);
        dialog.setItemsWithoutChk(
                mContext.getResources().getStringArray(R.array.message_menu_option),
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position == 0) {
                            getService().getFeedManager().deleteFeed(feed.getFeedId());
                        }
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    public void showContextMenu(final Comment comment, final int index) {
        final CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(mContext);
//        dialog.setTitle("选择操作");
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MobclickAgent.onEvent(mContext, UmengData.qzq_del_comment);
                mFlag = 5;//删除
                deletedIndex = index;
                getService().getFeedManager().deleteFeedComment(comment.getCommentId()); //删除评论
                dialog.dismiss();
            }
        });
        dialog.setMessage("确认删除吗?");
        dialog.show();
    }

    /**
     * 服务器返回
     *
     * @param event
     */
    public void onEventMainThread(FeedEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case DELETE_FEED_SUCCESS:
                    showToast("删除成功");
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
                    sendBroadcast(intent);
                    finish();
                    break;
                case DELETE_FEED_FAILED:
                    showToast("删除失败");
                    break;
                default:
                    break;
            }
        }
    }

    public String decodeUtf8(String str) {
        String value = "";
        try {
            value = new String(URLDecoder.decode(str, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    public void onEvent(final CommentEvent event) {
        if (isActivity) {
            disProgressDialog();
            Intent intent = null;
            switch (event.getEvent()) {
                case REPLAY_FEED_SUCCESS:
                	 intent = new Intent(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
                     sendBroadcast(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(event.getBonus()>0)
                                showContextMenuScore(event.getBonus());
                            if (!CollectionUtils.isEmpty(event.getComments())) {
                                Comment comment = event.getComments().get(0);
                                if (comment.getCommentType() == Constants.COMMENT_TYPE.REPLY) {
                                    mComments.add(comment);
                                } else if (comment.getCommentType() == Constants.COMMENT_TYPE.LIKE) {
                                    mLikes.add(comment);
                                }
                                updateAdapter();
                                if (event.isHasMore()) {
                                    xListView.setPullLoadEnable(true);
                                } else {
                                    xListView.setPullLoadEnable(false);
                                }
                            }
                        }
                    });
                    break;
                case REPLAY_FEED_FAILED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(event.getMsg());
                        }
                    });
                    break;
                case DELETE_COMMENT_SUCCESS:
                    intent = new Intent(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
                    sendBroadcast(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mFlag == 6) {
                                Iterator<Comment> iter = mLikes.iterator();

                                while (iter.hasNext()) {
                                    Comment comment = iter.next();

                                    if (comment.getSenderId() == user.getUserId()) {
                                        iter.remove();
                                    }
                                }
                            } else {
                                if (deletedIndex > -1 && deletedIndex < mComments.size()) {
                                    mComments.remove(deletedIndex);
                                }

                                deletedIndex = -1;
                            }
                            updateAdapter();
                            if (event.isHasMore()) {
                                xListView.setPullLoadEnable(true);
                            } else {
                                xListView.setPullLoadEnable(false);
                            }
                        }
                    });
                    break;
                case DELETE_COMMENT_FAILED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(event.getMsg());
                        }
                    });
                    break;
                case GET_LATEST_FEED_COMMENTS_SUCCESS:
                    if (!CollectionUtils.isEmpty(event.getComments())) {
                        List<Comment> comments = new ArrayList<Comment>();
                        List<Comment> likes = new ArrayList<Comment>();

                        for (Comment comment : event.getComments()) {
                            if (comment.getCommentType() == Constants.COMMENT_TYPE.REPLY) {
                                comments.add(comment);
                            } else {
                                likes.add(comment);
                            }
                        }
                        if (!CollectionUtils.isEmpty(comments)) {
                            mComments.clear();
                            mComments.addAll(comments);
                        }
                        if (!CollectionUtils.isEmpty(likes)) {
                            mLikes.clear();
                            mLikes.addAll(likes);
                        }
                    } else {
                        mComments.clear();
                        mLikes.clear();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateAdapter();
                            if (event.isHasMore()) {
                                xListView.setPullLoadEnable(true);
                            } else {
                                xListView.setPullLoadEnable(false);
                            }
                        }
                    });
                    break;
                case GET_LATEST_FEED_COMMENTS_FAILED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(event.getMsg());
                        }
                    });
                    break;
                case GET_HISTORY_FEED_COMMENTS_SUCCESS:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            xListView.stopLoadMore();
                            if (!CollectionUtils.isEmpty(event.getComments())) {
                                mComments.addAll(event.getComments());
                                updateAdapter();
                            }
                        }
                    });

                    if (event.isHasMore()) {
                        xListView.setPullLoadEnable(true);
                    } else {
                        xListView.setPullLoadEnable(false);
                    }
                    break;
                case GET_HISTORY_COMMENT_TO_ME_FAILED:
                    showToast(event.getMsg());
                    break;
                default:
                    break;
            }
        }
    }


    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;

            reslist.add(filename);

        }
        return reslist;

    }

    /**********************************
     * 表情
     **********************************/
    public void initViewPager() {
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
        viewPager.setAdapter(new ExpressionPagerAdapter(views));
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
        for (int i = 0; i < views.size(); i++) {
            ImageView image = new ImageView(MessageUnreadDetailActivity.this);
            if (i == 0) {
                image.setImageResource(R.drawable.page_focused);
            } else {
                image.setImageResource(R.drawable.page_unfocused);
            }
            image.setPadding(Utils.dip2px(MessageUnreadDetailActivity.this, 10), 0, 0, 40);
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
        View view = getLayoutInflater().inflate(R.layout.expression_gridview, null);
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
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(MessageUnreadDetailActivity.this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

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
                        et_send.append(SmileUtils.getSmiledText(MessageUnreadDetailActivity.this,
                                (String) field.get(null)));
                    } else { // 删除文字或者表情
                        if (!TextUtils.isEmpty(et_send.getText())) {

                            int selectionStart = et_send.getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = et_send.getText().toString();
                                String tempStr = body.substring(0, selectionStart);
                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                if (i != -1) {
                                    CharSequence cs = tempStr.substring(i, selectionStart);
                                    if (SmileUtils.containsKey(cs.toString()))
                                        et_send.getEditableText().delete(i, selectionStart);
                                    else
                                        et_send.getEditableText().delete(selectionStart - 1,
                                                selectionStart);
                                } else {
                                    et_send.getEditableText().delete(selectionStart - 1, selectionStart);
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

    @Override
    public void onRefresh() {
        mFlag = 1;
    }

    @Override
    public void onLoadMore() {
        mFlag = 2;
        if (mComments.size() > 0) {
            getService().getFeedManager().getHistoryFeedComments(feed.getFeedId(), mComments.get(mComments.size() - 1).getCommentId());
        }
    }


    // ** 指引页面改监听器 */
    class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            int count = page_select.getChildCount();
            for (int i = 0; i < count; i++) {
                ImageView view = (ImageView) page_select.getChildAt(i);
                if (i == arg0)
                    view.setImageResource(R.drawable.page_focused);
                else {
                    view.setImageResource(R.drawable.page_unfocused);
                }
            }
        }
    }

    class CommentListAdapter extends BaseAdapter {

        private Context mContext;
        private List<Comment> list;

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                try {
                    super.unregisterDataSetObserver(observer);
                } catch (Exception e) {
                    //ignore the exception.
                }
            }
        }

        public CommentListAdapter(Context context, List<Comment> mlist) {
            mContext = context;
            if (mlist == null)
                mlist = new ArrayList<Comment>();
            list = mlist;
        }

        public void setData(List<Comment> mlist) {
            list = mlist;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_unread_msg_detail_update_comment_item, parent, false);
                holder = new Holder();
                holder.icon = (RoundImageView) convertView.findViewById(R.id.comment_user_icon);
                holder.name = (TextView) convertView.findViewById(R.id.comment_name);
                holder.content = (TextView) convertView.findViewById(R.id.comment_content);
                holder.time = (TextView) convertView.findViewById(R.id.comment_time);
                holder.huifu = (TextView) convertView.findViewById(R.id.comment_huifu);
                holder.huifuName = (TextView) convertView.findViewById(R.id.comment_huifu_name);
                holder.line = (View) convertView.findViewById(R.id.comment_line);
                holder.comentIcon = (ImageView) convertView.findViewById(R.id.paraise_icon);
                holder.view_blank2 = (View)convertView.findViewById(R.id.view_blank2);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Comment comment = list.get(position);
            if(position==list.size()-1){
                holder.view_blank2.setVisibility(View.VISIBLE);
            }else{
                holder.view_blank2.setVisibility(View.GONE);
            }
            holder.name.setTag(position);
            holder.huifuName.setTag(position);
            holder.content.setTag(position);
            holder.icon.setTag(position);
            holder.icon.setImageUrl(comment.getSenderAvatar(), R.drawable.default_avatar);
            holder.icon.setTag(position);
//            holder.content.setText(comment.getContent());
            holder.content.setText(SmileUtils.getSmiledText(mContext,
                    comment.getContent()), TextView.BufferType.SPANNABLE);
            if (TextUtils.isEmpty(comment.getReplayToUserName())) {
                holder.name.setText(comment.getSenderName());
                holder.huifu.setVisibility(View.GONE);
                holder.huifuName.setVisibility(View.GONE);
            } else {
                holder.huifu.setVisibility(View.VISIBLE);
                holder.huifuName.setVisibility(View.VISIBLE);
                holder.name.setText(comment.getSenderName());
                holder.huifuName.setText(comment.getReplayToUserName());
            }
            if (comment.getSendTime() != null) {
                holder.time.setText(Utils.getDateTime(mContext, comment.getSendTime()));
            }
            if (position == 0) {
                holder.comentIcon.setVisibility(View.VISIBLE);
            } else {
                holder.comentIcon.setVisibility(View.GONE);
            }

            if (position == list.size() - 1) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }
            holder.icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();
                    Intent intent = new Intent(mContext, MyCircleListActivity.class);
                    intent.putExtra("userId", list.get(index).getSenderId());
                    intent.putExtra("userName", list.get(index).getSenderName());
                    startActivity(intent);
                }
            });
            holder.name.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();
                    Intent intent = new Intent(mContext, MyCircleListActivity.class);
                    intent.putExtra("userId", list.get(index).getSenderId());
                    intent.putExtra("userName", list.get(index).getSenderName());
                    startActivity(intent);
                }
            });
            holder.icon.setOnClickListener(new OnClickListener() {
            	
            	@Override
            	public void onClick(View v) {
            		int index = (Integer) v.getTag();
            		Intent intent = new Intent(mContext, MyCircleListActivity.class);
            		intent.putExtra("userId", list.get(index).getSenderId());
            		intent.putExtra("userName", list.get(index).getSenderName());
            		startActivity(intent);
            	}
            });
            holder.huifuName.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();
                    Intent intent = new Intent(mContext, MyCircleListActivity.class);
                    intent.putExtra("userId", list.get(index).getReplayToUserId());
                    intent.putExtra("userName", list.get(index).getReplayToUserName());
                    startActivity(intent);
                }
            });
            holder.content.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ll_send.getVisibility() == View.VISIBLE) {
                        hideKeyboard();
                        hideSoftInput();
                    } else {
                        int index = (Integer) v.getTag();
                        if (user != null && list.get(index).getSenderId() != user.getUserId()) {
                            replyComment(feed, SysConstants.commentType_2, "", list.get(index).getSenderId(), list.get(index).getSenderName(), list.get(index));
                        }
                    }
                }
            });
            holder.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int index = (Integer) v.getTag();
                    User userInfo = CoreService.getInstance().getUserManager().getUserInfo(list.get(index).getSenderId());
                    if ((user != null && list.get(index).getSenderId() == user.getUserId())||
                            (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())&&
                                    userInfo.getType() == Constants.USER_TYPE.PARENT)) {
                        showContextMenu(list.get(index), index);
                    }
                    return true;
                }
            });
            return convertView;
        }

        class Holder {
            private RoundImageView icon;
            private TextView name;
            private TextView content;
            private TextView time;
            private TextView huifu;
            private TextView huifuName;
            private View line;
            private ImageView comentIcon;
            private View view_blank2;
        }


        public String decodeUtf8(String str) {
            String value = "";
            try {
                value = new String(URLDecoder.decode(str, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return value;
        }

        public int dip2px(float dpValue) {
            final float scale = mContext.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
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
	          
	        if(len > maxLen){  
	            int selEndIndex = Selection.getSelectionEnd(editable);  
	            String str = editable.toString();  
	            //截取新字符串  
	            String newStr = str.substring(0,maxLen);  
	            editText.setText(newStr);  
	            editable = editText.getText();  
	              
	            //新字符串的长度  
	            int newLen = editable.length();  
	            //旧光标位置超过字符串长度  
	            if(selEndIndex > newLen)  
	            {  
	                selEndIndex = editable.length();  
	            }  
	            //设置新光标所在的位置  
	            Selection.setSelection(editable, selEndIndex);  
	        }  
	    }  
	  
	}
}
