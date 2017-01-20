package com.xcsd.app.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.adapter.ViewPagerAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.adapter.ExpressionAdapter;
import com.tuxing.app.easemob.adapter.ExpressionPagerAdapter;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.easemob.widget.ExpandGridView;
import com.xcsd.app.teacher.adapter.AskInfoAdapter;
import com.tuxing.app.qzq.util.OnHideKeyboardListener;
import com.tuxing.app.qzq.view.MessageEditText;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageLoaderListener;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.TargetType;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.quora.AnswerEvent;
import com.tuxing.sdk.modle.Answer;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.utils.CollectionUtils;
import com.xcsd.rpc.proto.EventType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.maxwin.view.XListView;

public class QuestionAskInfoActivity extends BaseActivity implements XListView.IXListViewListener {
    private ArrayList<String> iconList;
    private AskInfoAdapter currInfoAdapter;
    private ImageView headIcon;
    private TextView headName;
    private TextView headJob;
    private TextView headTitle;
    private TextView headrightTime;
    private TextView headContent;
    private LinearLayout ll_pic;
    private String TAG = QuestionAskInfoActivity.class.getSimpleName();
    private List<Comment> datas = new ArrayList<>();
    private XListView currListView;
    private View headView;
    private boolean hasMore = false;
    private TextView ask_zan;
    private boolean isComment = false;
    public Answer answer;
    public int currentType = 0;  //评论类型，1是点赞，2是文字
    private long replayAnswerId;
    private long replayToUserId;
    private String replayToUserName;
    private CommentType mCommentTyp;
    private TargetType mTargetType;

    private Button faceBtn;
    private MessageEditText msgEt;
    private ViewPager facePager;
    private LinearLayout faceLl;
    private LinearLayout bar_bottom;
    private LinearLayout page_select;
    private Button send;
    private List<String> reslist;//表情
    private InputMethodManager manager;
    private boolean isShowInput= false;
    private ImageView question_image;
    private boolean fromQuestionInfo = false;
    private long qutestionId;
    private ArrayList<Answer> asks;
    private int currentIndex;
    private boolean isLaseAnswer;
    private List<XListView> mViewList = new ArrayList<>();
    private List<View> mViews = new ArrayList<>();
    private List<AskInfoAdapter> adpaterList = new ArrayList<>();
    private List<TextView> zanList = new ArrayList<>();
    private ViewPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private int answerSize;
    private float StartX, SlipX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.ques_ask_info_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        initDataPager();
        if (isComment) {
            currentType = 2;
        }else{
            hideKeyboard();
        }
        replyComment(answer.getAnswerId(), answer.getAuthorUserId(), "",
                CommentType.REPLY, TargetType.ANSWER, currentType);
    }

    private void init() {
        answer = (Answer) getIntent().getSerializableExtra("answer");
        isComment = getIntent().getBooleanExtra("isComment", false);
        fromQuestionInfo = getIntent().getBooleanExtra("fromQuestionInfo",false);
        qutestionId = getIntent().getLongExtra("qutestionId",0);
        asks = (ArrayList<Answer>) getIntent().getSerializableExtra("answers");
        currentIndex = getIntent().getIntExtra("currentIndex",0);
        isLaseAnswer = getIntent().getBooleanExtra("isLaseAnswer",false);
        answerSize = getIntent().getIntExtra("answerSize",0);

        setTitle("");
        setLeftBack("", true, false);
        setRightNext(false, "", 0);

        mViewPager = (ViewPager)findViewById(R.id.question_askInfo_viewpager);
        initFace();
    }

    private void initHeadView() {
        headIcon = (ImageView) headView.findViewById(R.id.ques_info_head_icon);
        headName = (TextView) headView.findViewById(R.id.ques_item_head_name);
        headJob = (TextView) headView.findViewById(R.id.ques_item_head_job);
        headrightTime = (TextView) headView.findViewById(R.id.ques_info_item_head_right_time);
        headTitle = (TextView) headView.findViewById(R.id.ques_info_item_head_title);
        headContent = (TextView) headView.findViewById(R.id.ques_info_item_head_content);
        ask_zan = (TextView) headView.findViewById(R.id.question_info_item_zan);
        question_image = (ImageView)headView.findViewById(R.id.ask_header_ques_icon);
        ll_pic = (LinearLayout) headView.findViewById(R.id.ll_pic);
        headView.findViewById(R.id.question_title_rl).setOnClickListener(this);
        ask_zan.setOnClickListener(this);
        iconList = new ArrayList<>();
    }

    private void initData() {
        showData(answer);
        getService().getCommentManager().getLatestComment(answer.getAnswerId(), TargetType.ANSWER);
    }

    private void initDataPager(){
        if(CollectionUtils.isEmpty(asks) && answer != null){
            answerSize = 1;
            asks = new ArrayList<>();
            asks.add(answer);
        }
        mViews.clear();
        mViewList.clear();
        adpaterList.clear();
        zanList.clear();
        for (int i = 0; i < answerSize; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.question_ask_info_listview, null);
             currListView = (XListView) view.findViewById(R.id.question_info_ask_list);
             currListView.setXListViewListener(this);
            headView = getLayoutInflater().inflate(R.layout.question_ask_info_item_headert, null);
            currInfoAdapter = new AskInfoAdapter(mContext, datas, QuestionAskInfoActivity.this);
            initHeadView();
            if(i <= asks.size() - 1){
                Answer mAnswer = asks.get(i);
                showData(mAnswer);
                currListView.addHeaderView(headView);
            }
            currListView.setAdapter(currInfoAdapter);
            adpaterList.add(currInfoAdapter);
            zanList.add(ask_zan);
            mViewList.add(currListView);
            mViews.add(view);
        }

        mViewPager.setOffscreenPageLimit(0);
        mAdapter = new ViewPagerAdapter(mViews);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        StartX =  event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        SlipX =  event.getX();
                        if (currentIndex == 0 &&  (SlipX - StartX) > 100) {
                            finish();
                        }
                        break;
                }
                return false;
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(currentIndex);
        if(currentIndex == 0){
            currListView = mViewList.get(currentIndex);
            currInfoAdapter = adpaterList.get(currentIndex);
            ask_zan = zanList.get(currentIndex);
            answer = asks.get(currentIndex);
            initData();
            updateAdapter();
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                StartX = (int) ev.getX();
                break;
        }
        return true;
    }

    public void initFace() {
        faceBtn = (Button) findViewById(R.id.mailbox_emoticons);
        msgEt = (MessageEditText) findViewById(R.id.mailbox_info_et);
        facePager = (ViewPager) findViewById(R.id.mailbox_Pager);
        faceLl = (LinearLayout) findViewById(R.id.ll_face_container);
        bar_bottom = (LinearLayout) findViewById(R.id.bar_bottom);
        page_select = (LinearLayout) findViewById(R.id.mailbox_select);
        send = (Button) findViewById(R.id.mailbox_send);
        faceBtn.setOnClickListener(this);
        send.setOnClickListener(this);
        msgEt.setOnClickListener(this);
        msgEt.clearFocus();
        msgEt.addTextChangedListener(new MaxLengthWatcher(200, msgEt));
        reslist = getExpressionRes(SmileUtils.emoticons.size());
        msgEt.setListener(new OnHideKeyboardListener() {
            @Override
            public void onHide() {
                currentType = 0;
                replyComment(answer.getAnswerId(), answer.getAuthorUserId(), "", CommentType.REPLY, TargetType.ANSWER, currentType);
                msgEt.setText("");
                msgEt.setHint("写评论");
                hideKeyboard();
                msgEt.clearFocus();
                page_select.setVisibility(View.GONE);
                faceLl.setVisibility(View.GONE);
            }
        });
        msgEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    faceLl.setVisibility(View.GONE);
                    page_select.setVisibility(View.GONE);
                }
            }
        });
        initViewPager();
    }

    public void showData(Answer mAnswer) {
            if (mAnswer.getThanked()) {
                ask_zan.setSelected(true);
            } else {
                ask_zan.setSelected(false);
            }
        try {
            if(mAnswer.getThanksCount() > 0){
                ask_zan.setText(String.valueOf(mAnswer.getThanksCount()));
            }else{
                ask_zan.setText("");
            }
            iconList.clear();
            ll_pic.removeAllViews();
            ImageLoader.getInstance().displayImage(mAnswer.getAuthorUserAvatar() + SysConstants.Imgurlsuffix90_png, headIcon, ImageUtils.DIO_USER_ICON_round_4);
            headName.setText(mAnswer.getAuthorUserName());
            headContent.setText(String.valueOf(mAnswer.getContent()));
            headTitle.setText(mAnswer.getQuestionTitle());
            if(answer.getQuestionAttaches() != null && answer.getQuestionAttaches().size() > 0){
                ImageLoader.getInstance().displayImage(answer.getQuestionAttaches().get(0).getFileUrl()+ SysConstants.Imgurlsuffix90, question_image, ImageUtils.DIO_USER_ICON_round_4,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        ImageView mView = (ImageView)view;
                        mView.setImageBitmap(loadedImage);
                    }
                });
            }else{
                question_image.setImageResource(R.drawable.question_ask_icon);
            }

            if(!TextUtils.isEmpty(mAnswer.getAuthorTitle())){
                headJob.setVisibility(View.VISIBLE);
                headJob.setText(mAnswer.getAuthorTitle());
            }else{
                headJob.setVisibility(View.GONE);
            }
            if (mAnswer.getCreateOn() != null) {
                headrightTime.setText(Utils.getDateTime(mContext, mAnswer.getCreateOn()));
            }
            List<Attachment> attmachents = mAnswer.getAttachments();
            if (attmachents != null && attmachents.size() > 0) {
                for (int i = 0; i < attmachents.size(); i++) {
                    String imageUrl = attmachents.get(i).getFileUrl();
                    iconList.add(imageUrl + SysConstants.ImgurlJpg);
                }
            }
            if (iconList.size() >= 1) {
                for(int i = 0; i < iconList.size(); i++){
                    View imageCountView = LayoutInflater.from(mContext).inflate(R.layout.question_headert_item, null);
                    ImageView oneInage = (ImageView) imageCountView.findViewById(R.id.ques_info_one_image);
                    oneInage.setTag(i);

                    ImageLoader.getInstance().displayImage(iconList.get(i),oneInage,ImageUtils.DIO_DOWN_LYM,new ImageLoaderListener(TAG));
                    oneInage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = (Integer) view.getTag();
                            NewPicActivity.invoke(QuestionAskInfoActivity.this, iconList.get(index), true, iconList);
                        }
                    });
                    ll_pic.addView(imageCountView);
                }
                ll_pic.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAdapter() {
        if (currInfoAdapter == null) {
            currListView.addHeaderView(headView);
            currInfoAdapter = new AskInfoAdapter(mContext, datas, this);
            currListView.setAdapter(currInfoAdapter);
        } else {
            currInfoAdapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    private void getResresh(List<Comment> comments) {
        if (comments != null) {
            datas.clear();
            datas.addAll(comments);
        }
        updateAdapter();
        currListView.stopRefresh();
    }

    public void getLoadMore(List<Comment> comments) {
        if (comments != null && comments.size() > 0) {
            datas.addAll(comments);
        }
        updateAdapter();
        currListView.stopLoadMore();
    }


    @Override
    public void onRefresh() {
        getService().getCommentManager().getLatestComment(answer.getAnswerId(), TargetType.ANSWER);
    }

    @Override
    public void onLoadMore() {
        if (datas.size()>0){
            long maxId = datas.get(datas.size() - 1).getCommentId();
            getService().getCommentManager().getHistoryComment(answer.getAnswerId(), TargetType.ANSWER,maxId);
        }
    }

    public void showFooterView() {
        if (hasMore) {
            currListView.setPullLoadEnable(true);
        } else {
            currListView.setPullLoadEnable(false);
        }
    }


    @Override
    public void onConfirm() {
        super.onConfirm();
        Comment delComment = currInfoAdapter.getDelCommnet();
        getService().getCommentManager().deleteComment(delComment.getCommentId());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.question_info_item_zan:
                if(!answer.getThanked()){
                    currentType = 1;
                    showPopupWindow(v);
                    mCommentTyp = CommentType.LIKE;
                    getService().getCommentManager().publishComment(answer.getAnswerId(), TargetType.ANSWER, null,
                            answer.getAuthorUserId(), answer.getAuthorUserName(), CommentType.LIKE);
                }else{
                    showToast(mContext.getResources().getString(R.string.question_prompt));
                }
                break;
            case R.id.mailbox_send:
                showProgressDialog(mContext,"",true,null);
                faceLl.setVisibility(View.GONE);
                page_select.setVisibility(View.GONE);
                SendContent();
                break;
            case R.id.mailbox_info_et:
                if(faceLl.getVisibility() == View.VISIBLE){
                    faceLl.setVisibility(View.GONE);
                    page_select.setVisibility(View.GONE);
                }
                if(TextUtils.isEmpty(msgEt.getText()) && msgEt.getHint().equals("写评论")){
                    currentType = 2;
                    replyComment(answer.getAnswerId(),answer.getAuthorUserId(),"", CommentType.REPLY, TargetType.ANSWER,currentType);
                }
                break;
            case R.id.mailbox_emoticons:
                if(faceLl.getVisibility() == View.VISIBLE){
                    showInput(msgEt);
                    faceLl.setVisibility(View.GONE);
                    page_select.setVisibility(View.GONE);
                }else{
                    hideKeyboard();
                    faceLl.setVisibility(View.VISIBLE);
                    page_select.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.question_title_rl:
                if(fromQuestionInfo){
                    finish();
                }else{
                    Intent intent = new Intent(mContext,QuestionInfoActivity.class);
                    intent.putExtra("questionId",answer.getQuestionId());
                    startActivity(intent);
                }
                break;
        }
    }



    public void replyComment(long answerId, long replayToUserId, String replayToUserName, CommentType commentType,TargetType targetType,int currentType){
        this.replayAnswerId = answerId;
        this.mCommentTyp = commentType;
        this.mTargetType = targetType;
        this.replayToUserId = replayToUserId;
        this.replayToUserName = replayToUserName;

        if(currentType == 2){
            showInput(msgEt);
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        isShowInput = false;
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null){
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }}

    }

    public void SendContent(){
        String content = msgEt.getText().toString().trim();
        if(content.length() > 0){
            mCommentTyp = CommentType.REPLY;
            getService().getCommentManager().publishComment(replayAnswerId,mTargetType,content,replayToUserId, replayToUserName,mCommentTyp);
            msgEt.setText("");
            msgEt.setHint("写评论");
            msgEt.clearFocus();
            hideKeyboard();
        }else{
            showToast("发送的内容不能为空");
        }
    }

    /**
     * 显示键盘
     *
     * @param edit
     */
    public void showInput(final EditText edit) {
        isShowInput = true;
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        if(user != null && replayToUserId == user.getUserId()){
            edit.setHint("回复自己");
        }else if(!TextUtils.isEmpty(replayToUserName)){
            edit.setHint("回复" + replayToUserName);
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edit, 0);
            }
        }, 300);
    }

    public void onEventMainThread(CommentEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()){
                case GET_LATEST_ANSWER_COMMENTS_SUCCESS:
                    hasMore = event.isHasMore();
                    if(datas != null && event.getComments() != null && datas.size() != event.getComments().size()){
                        isComment = true;
                    }
                    getResresh(event.getComments());
                    showAndSaveLog(TAG, "获取最新的回答评论成功",false);
                    break;
                case GET_LATEST_ANSWER_COMMENTS_FAILED:
                    showAndSaveLog(TAG,"获取最新的回答评论失败 msg = " + event.getMsg(),false);
                    break;
                case GET_HISTORY_ANSWER_COMMENTS_SUCCESS:
                    hasMore = event.isHasMore();
                    getLoadMore(event.getComments());
                    showAndSaveLog(TAG,"获取历史的回答评论成功" ,false);
                    break;
                case GET_HISTORY_ANSWER_COMMENTS_FAILED:
                    showAndSaveLog(TAG,"获取历史的回答评论失败 msg = " + event.getMsg(),false);
                    break;
                case DELETE_COMMENT_SUCCESS:
                    isComment = true;
                    Comment delComment = currInfoAdapter.getDelCommnet();

                    if(delComment != null){
                        datas.remove(delComment);
                        updateAdapter();
                    }
                    showAndSaveLog(TAG,"删除成功",false);
                    break;
                case DELETE_COMMENT_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG,"删除失败 msg = " + event.getMsg(),false);
                    break;
                case REPLAY_ANSWER_SUCCESS:
                    isComment = true;
                            if(mCommentTyp == CommentType.LIKE){
                                answer.setThanked(true);
                                answer.setThanksCount(answer.getThanksCount() + 1);
                                ask_zan.setSelected(true);
                                ask_zan.setText(String.valueOf(answer.getThanksCount()));
                                if(!CollectionUtils.isEmpty(asks)){
                                    asks.remove(currentIndex);
                                    asks.add(currentIndex,answer);
                                }

                    }else if(mCommentTyp == CommentType.REPLY){
                        if(!hasMore){
                            datas.addAll(event.getComments());
                            updateAdapter();
                        }
                    }
                    currentType = 0;

                    getService().getDataReportManager().reportEventBid(EventType.COMMENT_ANSWER, answer.getAnswerId() + "");
//                    showToast("答案回复统计成功"+answer.getAnswerId()+"");

                    showAndSaveLog(TAG,"回复成功",false);
                    break;
                case REPLAY_ANSWER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG,"回复失败 msg = " + event.getMsg(),false);
                    break;
            }
        }
    }

    public void onEventMainThread(AnswerEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_HISTORY_ANSWER_SUCCESS:
                    isLaseAnswer = event.isHasMore();
                    asks.addAll(event.getAnswers());
                    initDataPager();
                    showAndSaveLog(TAG, "获取问题的历史回答列表成功 size = " + event.getAnswers().size(), false);
                    break;
                case GET_HISTORY_ANSWER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获取问题的历史回答列表失败 msg = " + event.getMsg(),false);
                    break;
            }
        }
    }
    @Override
    public void finish() {
        if (isShowInput) {
            hideKeyboard();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("isComment",isComment);
        setResult(RESULT_OK,intent);

        super.finish();
    }

    /******************************************************************/
    public class MaxLengthWatcher implements TextWatcher {
        private int maxLen = 0;
        private EditText editText = null;
        public MaxLengthWatcher(int maxLen, EditText editText) {
            this.maxLen = maxLen;
            this.editText = editText;
        }
        public void afterTextChanged(Editable arg0) { }
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }

        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            Editable editable = editText.getText();
            int len = editable.length();
            if (!TextUtils.isEmpty(s) && s.toString().trim().length() > 0) {
                send.setVisibility(View.VISIBLE);
            } else {
                send.setVisibility(View.GONE);
            }
            if(len >= maxLen){
                showToast(getResources().getString(R.string.edit_number_count));
            }
        }
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    public void initViewPager(){
        // 初始化表情viewpager
        List<View> views = new ArrayList<>();
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
        List<String> list = new ArrayList<>();
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
                        msgEt.append(SmileUtils.getSmiledText(QuestionAskInfoActivity.this,
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

    // ** 指引页面改监听器 */
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) { }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

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

    class OnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
        @Override
        public void onPageSelected(int i) {
            currentIndex = i;
            currentType = 0;
            currListView = mViewList.get(currentIndex);
            currInfoAdapter = adpaterList.get(currentIndex);
            ask_zan = zanList.get(currentIndex);
            if(!CollectionUtils.isEmpty(asks) && (currentIndex + 1) <= answerSize){
                if((currentIndex + 1) > asks.size()){
                    if(isLaseAnswer ) {
                        showProgressDialog(mContext,"",true,null);
                        long maxId = asks.get(asks.size() - 1).getAnswerId();
                        getService().getQuoraManager().getHistoryAnswers(qutestionId, null, UserType.EXPERT, maxId);
                    }
                    else{
                        answer = asks.get(currentIndex);
                        replyComment(answer.getAnswerId(), answer.getAuthorUserId(), "", CommentType.REPLY, TargetType.ANSWER, currentType);
                        showData(answer);
                        getService().getCommentManager().getLatestComment(answer.getAnswerId(), TargetType.ANSWER);
                    }
                }else{
                    answer = asks.get(currentIndex);
                    replyComment(answer.getAnswerId(), answer.getAuthorUserId(), "", CommentType.REPLY, TargetType.ANSWER, currentType);
                    showData(answer);
                    getService().getCommentManager().getLatestComment(answer.getAnswerId(), TargetType.ANSWER);
                }
            }
            msgEt.setHint("写评论");
            msgEt.clearFocus();
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    }

}

