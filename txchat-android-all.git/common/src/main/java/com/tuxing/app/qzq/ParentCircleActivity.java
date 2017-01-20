package com.tuxing.app.qzq;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.adapter.CircleListAdapter;
import com.tuxing.app.adapter.CircleNoDataAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.adapter.ExpressionAdapter;
import com.tuxing.app.easemob.adapter.ExpressionPagerAdapter;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.easemob.widget.ExpandGridView;
import com.tuxing.app.qzq.util.OnHideKeyboardListener;
import com.tuxing.app.qzq.util.ParentNoNullUtil;
import com.tuxing.app.qzq.view.MessageEditText;
import com.tuxing.app.qzq.view.MessageLinearLayout;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.ui.dialog.PopupWindowDialog;
import com.tuxing.app.util.LevelUtils;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.FeedEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

/**
 * 亲子圈
 */
public class ParentCircleActivity extends BaseActivity implements XListView.IXListViewListener,
        OnItemClickListener {

    private int mFlag = -1; // 0表示正常下载，1表示下拉刷新，2表示加载更多,3表示评论，4表示回复
    private XListView xListView;
    private CircleListAdapter adapter;
    private MessageLinearLayout rootView;
    private ImageView iv_top_bg;
    private RoundImageView iv_portrait;
    private TextView tv_name;
    private TextView tv_number;
    private TextView tv_class_pictures;
    private TextView tv_my_list;
    private View view_line;
    //回复
    public LinearLayout ll_send;
    private Button bt_add_face;
    private MessageEditText et_send;
    private Button bt_send;
    private TextView qzq_medal;
    private RelativeLayout rl_medal;
    private ImageView xing1;
    private ImageView xing2;
    private ImageView xing3;
    private ImageView xing4;
    private ImageView xing5;

    private LinearLayout llFace;
    private ViewPager viewPager;
    private LinearLayout page_select;
    private List<String> reslist;//表情
    private boolean isFace = false;
    public Feed selectedFeed;
    public Comment deletedComment;
    private Comment comment;
    private List<Comment> commentList; // 用作添加用，
    private long _replyUserId;
    private String _replyUserName;

    List<Feed> list = new ArrayList<Feed>();//列表
    List<Feed> localList = new ArrayList<Feed>();//本地缓存
    List<Feed> netList = new ArrayList<Feed>();//网络获取
    UpdateReceiver receiver;
    NewCommentReceiver newCommentReceiver;
    private View headView;
    private boolean isActive = false;
    public static boolean isNeedRefresh = false;
    private int position = 0;
    private float downY = 0;
    private float moveY = 0;
    CircleNoDataAdapter adapterNoData;
    int selectedPopup = 0;
    private List<Department> departmentList;

    private View mTopMenu;
    private View mTopMenuBg;
    private View mTopMenuLayout;
    private ValueAnimator mMenuAnim;
    private boolean isMenuAnim;
    private int mMenuLayoutH;
    private int mRotation = 135;
    private View mAddTypeThumb;
    private View mAddTypeCamera;
    private View mAddTypeShoot;
    private View mAddTypeVideo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = (MessageLinearLayout) getLayoutInflater().inflate(R.layout.activity_circle, null);
        setContentLayout(R.layout.activity_circle);
        setTitle(getResources().getString(R.string.tab_circle));
//        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
//            setRightNext(false, "", R.drawable.qzq_add_photo_p);
//        } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//            setRightNext(false, "", R.drawable.qzq_add_photo_black_t);
//        }
        setRightNext(false, "", R.drawable.circle_add_btn);

//        setRightNext(false, "", R.drawable.qzq_add_photo_black_t);
        setLeftBack("", true, false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        departmentList = getService().getContactManager().getAllDepartment();
    }


    @Override
    public void onResume() {
        showNewComment();
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("亲子圈界面");
        isActive = true;
        if (isNeedRefresh) {
            getnetlData();
            xListView.setSelection(position);
        }
        getPopWindow();
        if (mTopMenu.getVisibility() == View.VISIBLE) {
            topMenuClose();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("亲子圈界面");
    }

    @Override
    public void onStop() {
        super.onStop();
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        tv_title.setCompoundDrawables(null, null, null, null);
    }

    private void showNewComment() {
        int newCommentCount = getService().getCounterManager().getCounters().get(Constants.COUNTER.COMMENT);
        if (tv_number != null) {
            if (newCommentCount > 0) {
                tv_number.setText("您有" + newCommentCount + "条新消息");
                tv_number.setVisibility(View.VISIBLE);
            } else {
                tv_number.setVisibility(View.GONE);
            }
        }

        String medalName = getService().getUserManager().getUserProfile(Constants.SETTING_FIELD.RANK_NAME, "");
        if (rl_medal != null) {
            if (!TextUtils.isEmpty(medalName) && TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
                rl_medal.setVisibility(View.VISIBLE);
                qzq_medal.setText("恭喜" + medalName + "获得活跃达人勋章");
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
                rl_medal.setVisibility(View.GONE);
            }
        }
        String leveNum = getService().getUserManager().getUserProfile(Constants.SETTING_FIELD.USER_GRADE, "");
        showLevelIcon(leveNum);
    }

    public void initView() {
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);

        //init top menu view
        mTopMenu = findViewById(R.id.qzq_top_menu);
        mTopMenuBg = findViewById(R.id.qzq_top_menu_bg);
        mTopMenuLayout = findViewById(R.id.qzq_top_menu_layout);
        mMenuLayoutH = mContext.getResources().getDimensionPixelSize(R.dimen.dp_100);
        mAddTypeThumb = findViewById(R.id.qzq_add_type_thumb);//图片
        mAddTypeCamera = findViewById(R.id.qzq_add_type_camera);//拍照
        mAddTypeShoot = findViewById(R.id.qzq_add_type_shoot);//拍视频
        mAddTypeVideo = findViewById(R.id.qzq_add_type_video);//导入视频

        mAddTypeThumb.setOnClickListener(this);
        mAddTypeCamera.setOnClickListener(this);
        mAddTypeShoot.setOnClickListener(this);
        mAddTypeVideo.setOnClickListener(this);
        mTopMenuBg.setOnClickListener(this);

        bt_add_face = (Button) findViewById(R.id.bt_add_face);
        bt_add_face.setOnClickListener(this);
        llFace = (LinearLayout) findViewById(R.id.llFace);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        page_select = (LinearLayout) findViewById(R.id.page_select);
        reslist = getExpressionRes(SmileUtils.emoticons.size());
        initViewPager();
        xListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    moveY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    View focusV = getCurrentFocus();
                    float y = downY > moveY ? downY - moveY : moveY - downY;
                    if (Utils.isShouldHideInput(focusV, event) && y < 10) {
                        hideKeyboard();
                        return true;
                    }
                }
                return false;
            }
        });
        rootView.setListener(new OnHideKeyboardListener() {

            @Override
            public void onHide() {
                hideKeyboard();
            }

        });
        ll_send = (LinearLayout) findViewById(R.id.ll_send);
        et_send = (MessageEditText) findViewById(R.id.et_send);
        et_send.addTextChangedListener(new MaxLengthWatcher(200, et_send));
        et_send.setOnClickListener(this);
        et_send.setListener(new OnHideKeyboardListener() {
            @Override
            public void onHide() {
                hideKeyboard();
            }

        });
        et_send.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isFace) {
                        isFace = false;
                        llFace.setVisibility(View.GONE);
                    }
                }

            }
        });
        bt_send = (Button) findViewById(R.id.bt_send);

        xListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * 滚动状态改变时调用
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 不滚动时保存当前滚动到的位置
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    position = xListView.getFirstVisiblePosition();
                }
            }

            /**
             * 滚动时调用
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


        getData();
        setHeader(user);
    }


    public void getData() {
        showProgressDialog(this, "", true, null);
        getlocalData();
        getnetlData();
        mFlag = 0;
        adapter = null;
        bt_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkFeedMute()) {
                } else {
                    if (mFlag == 3) {//发表
                        if (!TextUtils.isEmpty(et_send.getText().toString().trim())) {
                            getService().getFeedManager().publishComment(selectedFeed.getFeedId(), et_send.getText().toString(),
                                    null, null, SysConstants.commentType_2); // 攒
                        } else {
                            showToast("请输入内容");
                            return;
                        }
                    } else if (mFlag == 4) {//回复
                        if (!TextUtils.isEmpty(et_send.getText().toString().trim())) {
                            getService().getFeedManager().publishComment(selectedFeed.getFeedId(), et_send.getText().toString(),
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
            }
        });
        receiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
        registerReceiver(receiver, intentFilter);

        newCommentReceiver = new NewCommentReceiver();
        registerReceiver(newCommentReceiver, new IntentFilter(SysConstants.UPDATENEWEXPLORE));
    }

    private void setHeader(final User currentUser) {
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            headView = getLayoutInflater().inflate(R.layout.qinzi_msg_fragment_header, null);
        } else {
            headView = getLayoutInflater().inflate(R.layout.qinzi_msg_fragment_lvel_header, null);
            qzq_medal = (TextView) headView.findViewById(R.id.qzq_medal);
            rl_medal = (RelativeLayout) headView.findViewById(R.id.rl_medal);
            xing1 = (ImageView) headView.findViewById(R.id.qzq_xing_1);
            xing2 = (ImageView) headView.findViewById(R.id.qzq_xing_2);
            xing3 = (ImageView) headView.findViewById(R.id.qzq_xing_3);
            xing4 = (ImageView) headView.findViewById(R.id.qzq_xing_4);
            xing5 = (ImageView) headView.findViewById(R.id.qzq_xing_5);
        }
        view_line = (View) headView.findViewById(R.id.view_line);
        tv_class_pictures = (TextView) headView.findViewById(R.id.tv_class_pictures);
        tv_class_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ClassPictureListActivity.class);
                intent.putExtra("index", selectedPopup);
                startActivity(intent);
            }
        });
        tv_my_list = (TextView) headView.findViewById(R.id.tv_my_list);
        tv_my_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳入到消息列表
                startActivity(new Intent(mContext, MessageUnreadListActivity.class));
            }
        });


        iv_top_bg = (ImageView) headView.findViewById(R.id.iv_top_bg);
        iv_portrait = (RoundImageView) headView.findViewById(R.id.iv_portrait);
        tv_name = (TextView) headView.findViewById(R.id.tv_name);

        if (currentUser != null) {// 个人头像判断性别
            iv_portrait.setImageUrl(currentUser.getAvatar() + SysConstants.Imgurlsuffix90, R.drawable.default_avatar);

//           tv_name.setText(currentUser.getNickname());
            tv_name.setText(ParentNoNullUtil.getNickName(currentUser));

        }
        iv_portrait.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //详细页面
                Intent intent = new Intent(mContext, MyCircleListActivity.class);
                intent.putExtra("userId", currentUser.getUserId());
//                intent.putExtra("userName", currentUser.getNickname());
                intent.putExtra("userName", ParentNoNullUtil.getNickName(currentUser));
                startActivity(intent);
            }
        });
        tv_number = (TextView) headView.findViewById(R.id.tv_number);
        tv_number.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳入到消息列表
                startActivity(new Intent(mContext, MessageUnreadListActivity.class));
                //tv_number.setVisibility(View.INVISIBLE);
            }
        });

    }

    //从本地获取最近20条
    private void getlocalData() {
        getService().getFeedManager().getFeedFromLocalCache();
    }

    //从net获取最近20条
    private void getnetlData() {
        if (selectedPopup == 0) {
            getService().getFeedManager().getLatestFeed(null);
        } else {
            Department department = departmentList.get(selectedPopup - 1);
            if (department != null) {
                getService().getFeedManager().getLatestFeed(department.getDepartmentId());
            }
        }
    }

    //从net获取之前20条
    private void getnetlHistoryData() {
        if (list.size() > 0) {
            if (selectedPopup == 0) {
                getService().getFeedManager().getHistoryFeed(list.get(list.size() - 1).getFeedId(), null);
            } else {
                Department department = departmentList.get(selectedPopup - 1);
                if (department != null) {
                    getService().getFeedManager().getHistoryFeed(list.get(list.size() - 1).getFeedId(), department.getDepartmentId());
                }
            }
        }
    }

    //从net获取之前20条
    private void getNewData() {
        getService().getFeedManager().getLatestMyConcernedComment();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.bt_add_face) {
            hideInput();
            if (llFace.getVisibility() == View.VISIBLE) {
                llFace.setVisibility(View.GONE);
                page_select.setVisibility(View.GONE);
                isFace = false;
            } else {
                isFace = true;
                llFace.setVisibility(View.VISIBLE);
                page_select.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.et_send) {
            llFace.setVisibility(View.GONE);
            page_select.setVisibility(View.GONE);
            isFace = false;
        } else if (v == mAddTypeThumb) {
            //相册
            Intent intent2 = new Intent(mContext, CircleReleaseActivity.class);
            intent2.putExtra("isFrom", "circleHome");//是从亲子圈进去的还是从我的亲子圈主页进去的?
            intent2.putExtra("flag", 1);
            startActivity(intent2);
            MobclickAgent.onEvent(mContext, "qzq_release_pic_from_pic", UmengData.qzq_release_pic_from_pic);
        } else if (v == mAddTypeCamera) {
            //相机
            Intent intent1 = new Intent(mContext, CircleReleaseActivity.class);
            intent1.putExtra("isFrom", "circleHome");//是从亲子圈进去的还是从我的亲子圈主页进去的?
            intent1.putExtra("flag", 0);
            startActivity(intent1);
            MobclickAgent.onEvent(mContext, "qzq_release_pic_from_camera", UmengData.qzq_release_pic_from_camera);
        } else if (v == mAddTypeShoot) {
            //拍摄视频
            Intent intent3 = new Intent(mContext, CircleViedeoReleaseActivity.class);
            intent3.putExtra("isFrom", "circleHome");//是从亲子圈进去的还是从我的亲子圈主页进去的?
            intent3.putExtra("flag", 0);
            startActivity(intent3);
//            MobclickAgent.onEvent(mContext, "qzq_release_video_from_recrd", UmengData.qzq_release_video_from_recrd);
        } else if (v == mAddTypeVideo) {
            //导入视频
            Intent intent4 = new Intent(mContext, CircleViedeoReleaseActivity.class);
            intent4.putExtra("isFrom", "circleHome");//是从亲子圈进去的还是从我的亲子圈主页进去的?
            intent4.putExtra("flag", 1);
            startActivity(intent4);
//            MobclickAgent.onEvent(mContext, "qzq_release_video_select", UmengData.qzq_release_video_select);
        } else if (v == mTopMenuBg) {
            topMenuClose();
        }

    }

    @Override
    public void onclickRightImg() {
//        if (checkFeedMute()) {
//        } else {
//            //发布新内容
//            showBtnDialog(new String[]{getString(R.string.take_video),
//                    getString(R.string.btn_info_text),
//                    getString(R.string.btn_cancel)});
//        }
        super.onclickRightImg();
        if (mTopMenu.getVisibility() == View.VISIBLE) {
            topMenuClose();
        } else if (mTopMenu.getVisibility() == View.GONE) {
            topMenuOpen();
        }
    }

    public void topMenuOpen() {
        if (!isMenuAnim) {
            isMenuAnim = true;
            mMenuAnim = ValueAnimator.ofFloat(0f, 1f);
            mMenuAnim.setDuration(150);
            mMenuAnim.setInterpolator(new LinearInterpolator());
            mMenuAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mTopMenuLayout.setTranslationY(mMenuLayoutH * (value - 1));
                    mTopMenuBg.setAlpha(value);
                    iv_right.setRotation(mRotation * value);
                }
            });
            mMenuAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mTopMenu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isMenuAnim = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mMenuAnim.start();
        }

    }

    public void topMenuClose() {

        if (!isMenuAnim) {
            isMenuAnim = true;
            mMenuAnim = ValueAnimator.ofFloat(1f, 0f);
            mMenuAnim.setDuration(150);
            mMenuAnim.setInterpolator(new LinearInterpolator());
            mMenuAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mTopMenuLayout.setTranslationY(mMenuLayoutH * (value - 1));
                    mTopMenuBg.setAlpha(value);
                    iv_right.setRotation(mRotation * value);
                }
            });
            mMenuAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mTopMenu.setVisibility(View.GONE);
                    isMenuAnim = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mMenuAnim.start();
        }

    }

    /**
     * 服务器返回
     *
     * @param event
     */
    public void onEventMainThread(FeedEvent event) {
        disProgressDialog();
        if (isActive) {
            isNeedRefresh = false;
            xListView.stopRefresh();
            switch (event.getEvent()) {
                case GET_FEED_FROM_LOCAL_CACHE:
                    if (event.getFeeds() != null) {
                        localList.clear();
                        localList = event.getFeeds();
                        list.clear();
                        list.addAll(event.getFeeds());
                        updateAdapter(list, event.isHasMore());
                    }
                    break;
                case REQUEST_LATEST_FEED_FROM_SERVER_SUCCESS:
                    if (event.getFeeds() != null) {
                        netList.clear();
                        netList = event.getFeeds();
                        list.clear();
                        list.addAll(netList);
                        updateAdapter(list, event.isHasMore());
                    }
                    getService().getCounterManager().resetCounter(Constants.COUNTER.FEED);
                    break;
                case REQUEST_LATEST_FEED_FROM_SERVER_FAILED:
                    showToast("获取数据失败");
                    break;
                case REQUEST_HISTORY_FEED_FROM_SERVER_SUCCESS:
                    if (event.getFeeds() != null && event.getFeeds().size() > 0) {
                        list.addAll(event.getFeeds());
                        updateAdapter(list, event.isHasMore());
                    }
                    break;
                case REQUEST_HISTORY_FEED_FROM_SERVER_FAILED:
                    xListView.stopLoadMore();
                    break;
                case DELETE_FEED_SUCCESS:
                    getlocalData();
                    break;
                case DELETE_FEED_FAILED:
                    showToast(event.getMsg());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        mFlag = 1;
        getnetlData();
    }

    @Override
    public void onLoadMore() {
        mFlag = 2;
        getnetlHistoryData();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disProgressDialog();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        if (newCommentReceiver != null) {
            unregisterReceiver(newCommentReceiver);
        }
    }


    // 隐藏软键盘和输入框
    public void hideKeyboard() {
        et_send.clearFocus();
        ll_send.setVisibility(View.GONE);
        if (isFace) {
            isFace = false;
            llFace.setVisibility(View.GONE);
            page_select.setVisibility(View.GONE);
        }
        hideInput();
    }

    public void hideInput() {
        final View v = getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    /***
     * 发布一个评论
     * 发布成功，触发事件CommentEvent.EventType.REPLAY_FEED_SUCCESS
     * 发布失败，触发事件CommentEvent.EventType.REPLAY_FEED_FAILED
     * @param feedId    评论的feedId
     * @param content   评论的内容
     * @param replayToUserId  评论的回复人ID，可以不填写
     * @param replayToUserName 评论的回复人名，可以不填写
     * @param commentType 评论类型，1是点赞，2是文字
     */
    /**
     * 回复评论
     *
     * @param comment
     */
    public void replyComment(Feed feed, int commentType, String content, long replyToUserid, String replyToUserName, Comment comment, List<Comment> comments) {
        if (checkFeedMute()) {
        } else {
            this.selectedFeed = feed;
            this.comment = comment;
            this.commentList = comments;
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
            if (ll_send.getVisibility() == View.GONE) {
                ll_send.setVisibility(View.VISIBLE);
                showInput(et_send);
//                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 服务器返回
     *
     * @param event
     */
    public void onEventMainThread(CommentEvent event) {
        disProgressDialog();
        if (isActive) {
            switch (event.getEvent()) {
                case REPLAY_FEED_SUCCESS:
//                    点赞，回复成功
                    if (event.getBonus() > 0)
                        showContextMenuScore(event.getBonus());
                    if (!CollectionUtils.isEmpty(event.getComments()) && selectedFeed != null) {
                        Comment comment = event.getComments().get(0);
                        if (comment.getCommentType() == Constants.COMMENT_TYPE.REPLY) {
                            getService().getDataReportManager().reportEventBid(EventType.COMMENT_FEED, comment.getTopicId() + "");
                            if (selectedFeed.getComments() == null) {
                                selectedFeed.setComments(Arrays.asList(comment));
                            } else {
                                if (selectedFeed.getComments().size() < 20) {
                                    selectedFeed.getComments().add(comment);
                                }
                            }
                        } else if (comment.getCommentType() == Constants.COMMENT_TYPE.LIKE) {
                            getService().getDataReportManager().reportEventBid(EventType.LIKE_FEED,comment.getTopicId()+"");
                            if (selectedFeed.getComments() == null) {
                                selectedFeed.setLikes(Arrays.asList(comment));
                            } else {
                                selectedFeed.getLikes().add(comment);
                            }
                        }
                        updateAdapter(list, event.isHasMore());
                    }
                    break;
                case DELETE_COMMENT_SUCCESS:
                    if (selectedFeed != null && deletedComment != null) {
                        if (deletedComment.getCommentType() == Constants.COMMENT_TYPE.REPLY) {
                            if (!CollectionUtils.isEmpty(selectedFeed.getComments())) {
                                selectedFeed.getComments().remove(deletedComment);
                            }
                        } else if (deletedComment.getCommentType() == Constants.COMMENT_TYPE.LIKE) {
                            if (!CollectionUtils.isEmpty(selectedFeed.getLikes())) {
                                selectedFeed.getLikes().remove(deletedComment);
                            }
                        }
                        updateAdapter(list, event.isHasMore());
                    }
                    break;
                case DELETE_COMMENT_FAILED:
                    showToast(event.getMsg());
                    break;
                case REPLAY_FEED_FAILED:
                    showToast(event.getMsg());
                    break;
                default:
                    break;
            }
        }
    }

    class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST)) {
                mFlag = 1;
                isNeedRefresh = true;
//                getnetlData();
                int count = intent.getIntExtra("scoreCount", 0);
                String isFrom = intent.getStringExtra("isFrom");
                if (isFrom != null && "circleHome".equals(isFrom) && count > 0) {
                    showContextMenuScore(count);
                }
            }
        }
    }

    class NewCommentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SysConstants.UPDATENEWEXPLORE)) {
                Map<String, Integer> counters = getService().getCounterManager().getCounters();
                Integer commentCounter = counters.get(Constants.COUNTER.COMMENT);
                if (commentCounter != null && commentCounter > 0) {
                    showNewComment();
                }
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

    public void initViewPager() {
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        int count = SmileUtils.emoticons.size() % 20;
        if (count != 0)
            count = SmileUtils.emoticons.size() / 20 + 1;
        else
            count = SmileUtils.emoticons.size() / 20;
        for (int i = 0; i < count; i++) {
            View gv = getGridChildView(i, count);
            views.add(gv);
        }
        viewPager.setAdapter(new ExpressionPagerAdapter(views));
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
        for (int i = 0; i < views.size(); i++) {
            ImageView image = new ImageView(this);
            if (i == 0) {
                image.setImageResource(R.drawable.page_focused);
            } else {
                image.setImageResource(R.drawable.page_unfocused);
            }
            image.setPadding(Utils.dip2px(this, 10), 0, 0, 40);
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
    private View getGridChildView(int i, int max) {
        View view = getLayoutInflater().inflate(R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (max == i + 1) {
            List<String> list1 = reslist.subList(i * 20, reslist.size());
            list.addAll(list1);
        } else {
            List<String> list1 = reslist.subList(i * 20, (i + 1) * 20);
            list.addAll(list1);
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
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
                        et_send.append(SmileUtils.getSmiledText(mContext,
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

    public void updateAdapter(List<Feed> list, boolean hashMore) {
        if (list.size() > 0)
            showNewComment();
        else {
            Feed feed = new Feed();
            feed.setFeedId(-1);
            list.add(feed);
        }
        if (hashMore) {
            xListView.setPullLoadEnable(true);
        } else {
            xListView.setPullLoadEnable(false);
            xListView.stopLoadMore();
        }
        if (adapter == null) {
            xListView.removeHeaderView(headView);
            xListView.addHeaderView(headView);
            adapter = new CircleListAdapter(mContext, list, this, getService());
            xListView.setAdapter(adapter);
//            adapter.setData(list);
        } else {
            adapter.setData(list);
        }
//                if(list.size() > 0){
//                    showNewComment();
//                    if (hashMore) {
//                        xListView.setPullLoadEnable(true);
//                    }else{
//                        xListView.setPullLoadEnable(false);
//                        xListView.stopLoadMore();
//                    }
//                    if(adapter == null){
//                       xListView.removeHeaderView(headView);
//                        xListView.addHeaderView(headView);
//                        adapter = new CircleListAdapter(mContext, list, this, getService());
//                        xListView.setAdapter(adapter);
//                        adapter.setData(list);
//                    } else{
//                        xListView.setAdapter(adapter);
//                        adapter.setData(list);
//                    }
//                }
//                else{
//                    list.clear();
//                    list.add(new Feed());
//                    xListView.setPullLoadEnable(false);
//                    if(tv_number != null){
//                        tv_number.setVisibility(View.GONE);
//                    }
//                    if(adapterNoData == null){
//                        xListView.removeHeaderView(headView);
//                        xListView.addHeaderView(headView);
//                        adapterNoData = new CircleNoDataAdapter(mContext, list,this);
//                        xListView.setAdapter(adapterNoData);
//                    }else{
//                        xListView.setAdapter(adapterNoData);
//                        adapterNoData.notifyDataSetChanged();
//                    }
//                }

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

            if (len >= maxLen) {
                showToast(getResources().getString(R.string.edit_number_count));
            }
        }

    }

    private void getPopWindow() {
        if (!TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {//教师版
            if (selectedPopup == 0) {
                tv_title.setText("全部");
            } else {
                Department department = departmentList.get(selectedPopup - 1);
                if (department != null) {
                    tv_title.setText(department.getName());
                }
            }
            arrow_show(false);
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> departs = new ArrayList<String>();
                    departs.add("全部");
                    for (Department department : departmentList) {
                        departs.add(department.getName());
                    }
                    arrow_show(true);
                    showContextMenu(departs.toArray(new CharSequence[departs.size()]));
                }
            });
        } else {
            tv_title.setText(getResources().getString(R.string.tab_circle));
        }
    }

    public void showContextMenu(final CharSequence[] departs) {
        final PopupWindowDialog dialog = DialogHelper.getPopDialogCancelable(this);
        dialog.setItemsWithoutChk(departs,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        dialog.dismiss();
                        tv_title.setText(departs[position]);
                        arrow_show(false);
                        if (position == 0) {
                            showProgressDialog(mContext, "", true, null);
                            getService().getFeedManager().getLatestFeed(null);
                        } else {
                            Department department = departmentList.get(position - 1);
                            if (department != null) {
                                showProgressDialog(mContext, "", true, null);
                                getService().getFeedManager().getLatestFeed(department.getDepartmentId());
                            }
                        }
                        dialog.setSelectIndex(selectedPopup);
                        selectedPopup = position;
                    }
                }, selectedPopup);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        lp.y = PhoneUtils.dip2px(this, 48); // 新位置Y坐标
        lp.width = PhoneUtils.dip2px(this, 200); // 宽度
        if (departs.length < 3) {
            lp.height = PhoneUtils.dip2px(mContext, 85);
            ; // 动态高度
        } else if (departs.length >= 5) {
            lp.height = PhoneUtils.dip2px(mContext, 220);
            ; // 动态高度
        } else {
            lp.height = PhoneUtils.dip2px(mContext, departs.length * 45) - PhoneUtils.dip2px(mContext, 10);
            ; // 动态高度
        }

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void arrow_show(boolean isUp) {
        Drawable drawable = null;
        if (isUp) {
            drawable = getResources().getDrawable(R.drawable.ic_arrow_up);
        } else {
            drawable = getResources().getDrawable(R.drawable.ic_arrow_down);
        }
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth() + 5, drawable.getMinimumHeight());
        tv_title.setCompoundDrawables(null, null, null, drawable);

    }


    @Override
    public void onclickBtn1() {
        Intent intent1 = new Intent(mContext, CircleViedeoReleaseActivity.class);
        intent1.putExtra("isFrom", "circleHome");//是从亲子圈进去的还是从我的亲子圈主页进去的?
        startActivity(intent1);
        super.onclickBtn1();
    }

    @Override
    public void onclickBtn2() {
        Intent intent2 = new Intent(mContext, CircleReleaseActivity.class);
        intent2.putExtra("isFrom", "circleHome");//是从亲子圈进去的还是从我的亲子圈主页进去的?
        startActivity(intent2);
        super.onclickBtn2();
    }

    /**
     * 显示键盘
     *
     * @param edit
     */
    public void showInput(final EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edit, 0);
            }
        }, 300);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }else if (ll_send.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(et_send.getText().toString())) {
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
            } else
                return super.onKeyDown(keyCode, event);
        } else
            return super.onKeyDown(keyCode, event);

    }

    public void showLevelIcon(String leve) {
        List<Integer> leveList = LevelUtils.getLevel(mContext, leve);
        if (leveList != null) {

            if (leveList.size() == 1 && xing1 != null) {
                xing1.setVisibility(View.VISIBLE);
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(0)));
            } else if (leveList.size() == 2 && xing1 != null && xing2 != null) {
                xing1.setVisibility(View.VISIBLE);
                xing2.setVisibility(View.VISIBLE);
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(0)));
                xing2.setImageDrawable(getResources().getDrawable(leveList.get(1)));
            } else if (leveList.size() == 3 && xing1 != null && xing2 != null && xing1 != null) {
                xing1.setVisibility(View.VISIBLE);
                xing2.setVisibility(View.VISIBLE);
                xing3.setVisibility(View.VISIBLE);
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(0)));
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(1)));
                xing3.setImageDrawable(getResources().getDrawable(leveList.get(2)));
            } else if (leveList.size() == 4 && xing1 != null && xing2 != null && xing3 != null && xing4 != null) {
                xing1.setVisibility(View.VISIBLE);
                xing2.setVisibility(View.VISIBLE);
                xing3.setVisibility(View.VISIBLE);
                xing4.setVisibility(View.VISIBLE);
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(0)));
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(1)));
                xing3.setImageDrawable(getResources().getDrawable(leveList.get(2)));
                xing4.setImageDrawable(getResources().getDrawable(leveList.get(3)));
            } else if (leveList.size() == 5 && xing1 != null && xing2 != null && xing3 != null && xing4 != null && xing5 != null) {
                xing1.setVisibility(View.VISIBLE);
                xing2.setVisibility(View.VISIBLE);
                xing3.setVisibility(View.VISIBLE);
                xing4.setVisibility(View.VISIBLE);
                xing5.setVisibility(View.VISIBLE);
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(0)));
                xing1.setImageDrawable(getResources().getDrawable(leveList.get(1)));
                xing3.setImageDrawable(getResources().getDrawable(leveList.get(2)));
                xing4.setImageDrawable(getResources().getDrawable(leveList.get(3)));
                xing5.setImageDrawable(getResources().getDrawable(leveList.get(4)));
            }
        }
    }



    @Override
    public void finish() {
        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "parentcircle");
        super.finish();
    }
}
