package com.xcsd.app.teacher.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.QuestionInfoAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.TargetType;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.quora.AnswerEvent;
import com.tuxing.sdk.event.quora.QuestionEvent;
import com.tuxing.sdk.modle.Answer;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.Question;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class QuestionInfoActivity extends BaseActivity implements XListView.IXListViewListener {
    private ArrayList<String> iconList;
    private QuestionInfoAdapter infoAdapter;
    private ImageView headIcon;
    private TextView headName;
    private TextView headTitle;
    private TextView headTime;
    private TextView headContent;
    private ImageView oneInage;
    private LinearLayout ll_pic;
    private RelativeLayout head_bg;
    private ArrayList<Answer> asks = new ArrayList<>();
    private XListView listView;
    private View headView;
    private boolean hasMore = false;
    private Question question;
    private String TAG = QuestionInfoActivity.class.getSimpleName();
    private boolean isReply = false;
    private ValueAnimator mShow;
    private TextView title;
    private TextView titleRight;
    private long questionId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_info_layout);
        init();
        initData();
        mShow = ValueAnimator.ofFloat(0, 100);
    }


    private void init() {
        question = (Question) getIntent().getSerializableExtra("question");
        questionId = getIntent().getLongExtra("questionId",0);
        title = (TextView)findViewById(R.id.tv_title);
        titleRight = (TextView)findViewById(R.id.tv_right);
        findViewById(R.id.tv_left).setOnClickListener(this);
        findViewById(R.id.rl_ques_info_ask).setOnClickListener(this);
        title.setText("");
        headView = getLayoutInflater().inflate(R.layout.question_info_item_headert, null);
        listView = (XListView) findViewById(R.id.question_info_ask_list);

        listView.setXListViewListener(this);
        listView.startRefresh();
        initHeadView();
        updateAdapter(0);

    }

    private void initHeadView() {
        headIcon = (ImageView) headView.findViewById(R.id.ques_info_head_icon);
        headName = (TextView) headView.findViewById(R.id.ques_item_head_name);
        headTime = (TextView) headView.findViewById(R.id.ques_info_item_head_time);
        headTitle = (TextView) headView.findViewById(R.id.ques_info_item_head_title);
        headContent = (TextView) headView.findViewById(R.id.ques_info_item_head_content);
        ll_pic = (LinearLayout) headView.findViewById(R.id.ll_pic);
        head_bg = (RelativeLayout) headView.findViewById(R.id.question_info_header__bg);
        ll_pic.removeAllViews();
        iconList = new ArrayList<>();
    }

    private void initData() {
        asks.clear();
        if(questionId != 0){
            getService().getQuoraManager().getQuestionById(questionId);
            getService().getQuoraManager().getLatestAnswers(questionId, null, UserType.EXPERT);
        }else{
            getService().getQuoraManager().getQuestionById(question.getQuestionId());
            getService().getQuoraManager().getLatestAnswers(question.getQuestionId(), null, UserType.EXPERT);
            showData(question);
        }
    }

    public void showData(Question mQuestion) {
        try {
            iconList.clear();
                ImageLoader.getInstance().displayImage(mQuestion.getAuthorUserAvatar() + SysConstants.Imgurlsuffix90, headIcon, ImageUtils.DIO_USER_ICON_round_4);
            headName.setText(mQuestion.getAuthorUserName());
            headTitle.setText(mQuestion.getTitle());
            titleRight.setText(Utils.commentCount(mQuestion.getAnswerCount()) + "人回答");
            isShowTitleRightIcon(mQuestion.getAnswerCount());
            headContent.setText(mQuestion.getContent());
            if(mQuestion.getCreateOn() != null){
                headTime.setText(Utils.getDateTime(mContext, mQuestion.getCreateOn()));
             }
            List<Attachment> attmachents= mQuestion.getAttachments();
            if (attmachents != null && attmachents.size() > 0) {
                for (int i = 0; i < attmachents.size(); i++) {
                    String imageUrl = attmachents.get(i).getFileUrl();
                    iconList.add(imageUrl + SysConstants.ImgurlJpg);
                }
            }

            if (iconList.size() >= 1) {

                for(int i = 0; i < iconList.size(); i++){
                    View imageCountView = LayoutInflater.from(mContext).inflate(R.layout.question_headert_item, null);
                    oneInage = (ImageView) imageCountView.findViewById(R.id.ques_info_one_image);
                    oneInage.setTag(i);
                    ImageLoader.getInstance().displayImage(iconList.get(i),oneInage,ImageUtils.DIO_DOWN_LYM);
                    oneInage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = (Integer) view.getTag();
                            NewPicActivity.invoke(QuestionInfoActivity.this, iconList.get(index), true, iconList);
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

    public void updateAdapter(int expertAnswerSize) {
        if (asks != null && asks.size() > 0) {
            head_bg.setVisibility(View.GONE);
        } else {
            head_bg.setVisibility(View.VISIBLE);
        }
        if (infoAdapter == null) {
            listView.addHeaderView(headView);
            infoAdapter = new QuestionInfoAdapter(mContext, asks, this, expertAnswerSize);
            listView.setAdapter(infoAdapter);
            listView.setFootBack(getResources().getColor(R.color.qzq_gray));
        } else {
            infoAdapter.setData(asks, expertAnswerSize);
        }
        showFooterView();
    }

    private void getResresh(List<Answer> expertAnswer, List<Answer> answer) {
        int experAnswerSize = 0;
        if (expertAnswer != null) {
            asks.clear();
            experAnswerSize = expertAnswer.size();
            asks.addAll(expertAnswer);

        }
        if (expertAnswer != null && answer != null) {
            asks.addAll(answer);
        } else if (expertAnswer == null && answer != null) {
            asks.clear();
            asks.addAll(answer);
        }

        updateAdapter(experAnswerSize);
        listView.stopRefresh();
    }

    public void getLoadMore(List<Answer> expertAnswer, List<Answer> answer) {
        int experAnswerSize = 0;
        if (expertAnswer != null && expertAnswer.size() > 0) {
            asks.addAll(expertAnswer);
            experAnswerSize = asks.size();
        }

        if (answer != null && answer.size() > 0) {
            asks.addAll(answer);
        }

        updateAdapter(experAnswerSize);
        listView.stopLoadMore();

    }

    @Override
    public void onRefresh() {
        if(questionId != 0){
            getService().getQuoraManager().getQuestionById(questionId);
            getService().getQuoraManager().getLatestAnswers(questionId, null, UserType.EXPERT);
        }else{
            getService().getQuoraManager().getQuestionById(question.getQuestionId());
            getService().getQuoraManager().getLatestAnswers(question.getQuestionId(), null, UserType.EXPERT);
        }

    }

    @Override
    public void onLoadMore() {
        long maxId = asks.get(asks.size() - 1).getAnswerId();
        getService().getQuoraManager().getHistoryAnswers(question.getQuestionId(), null, UserType.EXPERT, maxId);
    }

    public void showFooterView() {
        if (hasMore) {
            listView.setPullLoadEnable(true);
        } else {
            listView.setPullLoadEnable(false);
        }
    }


    @Override
    public void onConfirm() {
        super.onConfirm();
        int index = infoAdapter.getClickIndex();
        getService().getQuoraManager().deleteAnswer(asks.get(index).getAnswerId());
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_ques_info_ask:
                Intent intent = new Intent(mContext, QuestionAskActivity.class);
                intent.putExtra("questionId", question.getQuestionId());
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_left:
                finish();
                break;

        }
    }

    public void replyComment(long answerId) {
        //评论类型，1是点赞，2是文字
        showProgressDialog(mContext, null, true, null);
        getService().getCommentManager().publishComment(answerId, TargetType.ANSWER, null, null, null, CommentType.LIKE);
    }

    public void startActivity(Context mContext, Class clazz, boolean isComment, int index) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("answer", asks.get(index));
        intent.putExtra("qutestionId",question.getQuestionId());
        intent.putExtra("answers",asks);
        intent.putExtra("answerSize",question.getAnswerCount());
        intent.putExtra("currentIndex",index);
        intent.putExtra("isLaseAnswer",hasMore);
        intent.putExtra("isComment", isComment);
        intent.putExtra("fromQuestionInfo",true);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getService().getDataReportManager().reportEventBid(EventType.ANSWER_QUESTION,question.getQuestionId()+"");
            if (data.getBooleanExtra("isComment", false)) {
                isActivity = true;
                getService().getQuoraManager().getLatestAnswers(question.getQuestionId(), null, UserType.EXPERT);
            }
            else if(data.getBooleanExtra("isAsk", false)){
                isReply = true;
                int huifuCount = Integer.valueOf(titleRight.getText().toString().replace("人回答", "")) + 1;
                titleRight.setText(Utils.commentCount(huifuCount) + "人回答");
                isShowTitleRightIcon(huifuCount);
                getService().getQuoraManager().getLatestAnswers(question.getQuestionId(), null, UserType.EXPERT);
            }
        }
    }

    public void onEventMainThread(AnswerEvent event) {
        if (isActivity) {
            switch (event.getEvent()){
                case GET_LATEST_ANSWER_SUCCESS:
                    hasMore = event.isHasMore();
                    getResresh(event.getExpertAnswers(),event.getAnswers());
                    showAndSaveLog(TAG,"获取问题的回答列表成功 size = " + event.getAnswers().size(),false);
                    break;
                case GET_LATEST_ANSWER_FAILED:
                    listView.stopRefresh();
                    showToast(event.getMsg());
                    showAndSaveLog(TAG,"获取问题的回答列表失败 msg = " + event.getMsg(),false);
                    break;
                case GET_HISTORY_ANSWER_SUCCESS:
                    hasMore = event.isHasMore();
                    getLoadMore(event.getExpertAnswers(),event.getAnswers());
                    showAndSaveLog(TAG, "获取问题的历史回答列表成功 size = " + event.getAnswers().size(), false);
                    break;
                case GET_HISTORY_ANSWER_FAILED:
                    listView.stopLoadMore();
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获取问题的历史回答列表失败 msg = " + event.getMsg(),false);
                    break;
                case DELETE_ANSWER_SUCCESS:
                    isReply = true;
                    int huifuCount = Integer.valueOf(titleRight.getText().toString().replace("人回答","")) - 1;
                    titleRight.setText(Utils.commentCount(huifuCount) + "人回答");
                    isShowTitleRightIcon(huifuCount);
                    getService().getQuoraManager().getLatestAnswers(question.getQuestionId(),null, UserType.EXPERT);
                    showAndSaveLog(TAG, "删除回答成功 questionId = " + question.getQuestionId(),false);
                    break;
                case DELETE_ANSWER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "删除回答失败  msg + " + event.getMsg() + "  questionId = " + question.getQuestionId(), false);
                    break;
            }
        }
    }

    public void onEventMainThread(CommentEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case REPLAY_ANSWER_SUCCESS:
                    if (!SysConstants.answerId.equals("")){
                        if (SysConstants.isZan){
                            getService().getDataReportManager().reportEventBid(EventType.LIKE_ANSWER,SysConstants.answerId+"");
//                            showToast("答案点赞统计成功"+SysConstants.answerId);
//                        }else{
//                            getService().getDataReportManager().reportEventBid(EventType.COMMENT_ANSWER,SysConstants.answerId+"");
//                            showToast("答案回复统计成功"+SysConstants.answerId);
                        }
                    }
                    getService().getQuoraManager().getLatestAnswers(question.getQuestionId(), null, UserType.EXPERT);
                    showAndSaveLog(TAG, "赞成功 questionId = " + question.getQuestionId()  ,false);
                    break;
                case REPLAY_ANSWER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "赞失败 questionId = " + question.getQuestionId()  ,false);
                    break;
            }
        }
    }
    public void onEventMainThread(QuestionEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_QUESTION_BY_ID_SUCCESS:
                    if(questionId != 0){
                        question = event.getQuestions().get(0);
                        showData(question);
                    }

                    int huifuCount = Integer.valueOf(titleRight.getText().toString().replace("人回答","").trim());
                   int answerCount =  event.getQuestions().get(0).getAnswerCount();
                    if(huifuCount != answerCount){
                       isReply = true;
                    }
                    titleRight.setText(Utils.commentCount(answerCount) + "人回答");
                    isShowTitleRightIcon(answerCount);
                    showAndSaveLog(TAG, "获取问题详情成功 questionId = " + question.getQuestionId()  ,false);
                    break;
                case GET_QUESTION_BY_ID_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获取问题详情失败 questionId = " + question.getQuestionId()  ,false);
                    break;
            }
        }
    }

    private void isShowTitleRightIcon(Integer mCommentCount){
        if(mCommentCount != 0){
            titleRight.setVisibility(View.VISIBLE);
        }else
            titleRight.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        if (isReply) {
            sendBroadcast(new Intent(SysConstants.UPDATEQUESTION));
        }
        super.finish();
    }
}
