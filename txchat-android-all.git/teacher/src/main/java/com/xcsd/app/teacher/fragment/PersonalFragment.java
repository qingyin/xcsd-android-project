package com.xcsd.app.teacher.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuxing.app.adapter.ViewPagerAdapter;
import com.tuxing.app.base.BaseFragment;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.app.teacher.activity.QuestionAskInfoActivity;
import com.xcsd.app.teacher.activity.QuestionAskSearchActivity;
import com.xcsd.app.teacher.activity.QuestionInfoActivity;
import com.xcsd.app.teacher.adapter.PersonalAnswerAdapter;
import com.xcsd.app.teacher.adapter.PersonalQuestionAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RequestErrorLayout;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.event.quora.AnswerEvent;
import com.tuxing.sdk.event.quora.QuestionEvent;
import com.tuxing.sdk.modle.Answer;
import com.tuxing.sdk.modle.Question;
import com.tuxing.sdk.utils.CollectionUtils;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

/**
 * 教师成长（个人中心）
 * */

public class PersonalFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
       XListView.IXListViewListener /** ,IndicatorLayout.OnIndicatorChangeListener*/ {

    private static final String TAG = PersonalFragment.class.getSimpleName();
    private View mContentView;
    /**title text */
    private TextView mTitle;
    private ViewPager mViewPager;
//    private IndicatorLayout mIndicator;
    private List<View> mViewList = new ArrayList<>();
    private XListView mQuestionListView;
    private XListView mAnswerListview;
    private XListView mListView;
    private RequestErrorLayout mErrorLayout;
    private ViewPagerAdapter mAdapter;
    private List<Question> mQuestions = new ArrayList<>();
    private List<Answer> mAnswers = new ArrayList<>();
    private PersonalQuestionAdapter mQAdapter;
    private PersonalAnswerAdapter mAAdapter;
    private String mTabs[] = new String[]{"问题", "回答"};
    private boolean isActivity = false;
    private boolean mQuestionhasMore = false;
    private boolean mAnswerhasMore = false;
    boolean refresh = false;
    private LinearLayout mPersonall;//提问  回答
    private NewQuestionReceiver questionReceiver;
    private TextView mPersonalQuestionBt;
    private TextView mPersonalAnswerBt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActivity = true;
        for (int i = 0; i < mTabs.length; i++) {
            mListView = (XListView) LayoutInflater.from(getActivity()).inflate(R.layout.answer_question_listview, null);
            mListView.setPadding(Utils.dip2px(getActivity(), 7),0, Utils.dip2px(getActivity(), 7),0);
            mListView.setId(i);
            mListView.setFootBack(getResources().getColor(R.color.bg));
            mViewList.add(mListView);
        }
        mQuestionListView = (XListView) mViewList.get(0);
        mAnswerListview = (XListView) mViewList.get(1);
        mQuestionListView.setPullLoadEnable(false);
        mAnswerListview.setPullLoadEnable(false);
        mQuestionListView.startRefresh();
        mAnswerListview.startRefresh();
        mQuestionListView.setXListViewListener(this);
        mAnswerListview.setXListViewListener(this);
        mAdapter = new ViewPagerAdapter(mViewList);
        mQAdapter = new PersonalQuestionAdapter(getActivity(), mQuestions);
        mAAdapter = new PersonalAnswerAdapter(getActivity(), mAnswers);
        mQuestionListView.setAdapter(mQAdapter);
        mAnswerListview.setAdapter(mAAdapter);

        mQuestionListView.setOnItemClickListener(this);
        mAnswerListview.setOnItemClickListener(this);
        if (!EventBus.getDefault().isRegistered(PersonalFragment.this)) {
            EventBus.getDefault().register(this);
        }

        questionReceiver = new NewQuestionReceiver();
        getActivity().registerReceiver(questionReceiver, new IntentFilter(SysConstants.UPDATEQUESTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        isActivity = true;
        mContentView = inflater.inflate(R.layout.teacher_help_personal, null);
        mContentView.findViewById(R.id.ll_left).setOnClickListener(this);
        mContentView.findViewById(R.id.tv_right).setOnClickListener(this);
        mTitle = (TextView) mContentView.findViewById(R.id.tv_title);
        mPersonall = (LinearLayout) mContentView.findViewById(R.id.personal_ll);
        mPersonalQuestionBt = (TextView)mContentView.findViewById(R.id.personal_question_bt);
        mPersonalAnswerBt = (TextView)mContentView.findViewById(R.id.personal_answer_bt);
        mViewPager = (ViewPager) mContentView.findViewById(R.id.expert_personal_viewpager);
        mErrorLayout = (RequestErrorLayout) mContentView.findViewById(R.id.request_error_layout);
//        mIndicator = (IndicatorLayout) mContentView.findViewById(R.id.indicator); //老样式的滑动
//        mIndicator.setVisiableTabCount(2);
//        mIndicator.setTabs(mTabs);
//        mIndicator.setViewPager(mViewPager);
//        mIndicator.setOnIndicatorChangeListener(this);
        mTitle.setVisibility(View.GONE);
        mPersonall.setVisibility(View.VISIBLE);
        mViewPager.setAdapter(mAdapter);
        mPersonalQuestionBt.setSelected(true);
        mPersonalAnswerBt.setSelected(false);
        mPersonalQuestionBt.setOnClickListener(this);
        mPersonalAnswerBt.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener());
        if (mViewPager.getCurrentItem() == 0) {
            if (CollectionUtils.isEmpty(mQuestions)) {
                getService().getQuoraManager().getLatestQuestions(currentUser.getUserId());
            }
        } else if (mViewPager.getCurrentItem() == 1) {
            if (CollectionUtils.isEmpty(mAnswers)) {
                getService().getQuoraManager().getLatestAnswers(null, currentUser.getUserId(), UserType.TEACHER);
            }
        }
        return mContentView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (refresh) {
            isActivity = true;
            refresh = false;
            if (mViewPager.getCurrentItem() == 0) {
                getService().getQuoraManager().getLatestQuestions(currentUser.getUserId());

            } else if (mViewPager.getCurrentItem() == 1) {
                getService().getQuoraManager().getLatestAnswers(null, currentUser.getUserId(), UserType.TEACHER);

            }
        }
        updataError();

    }


    @Override
    public void onPause() {
        super.onPause();
        isActivity = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_left:
                getActivity().finish();
                break;
            case R.id.tv_right:
                Intent intent = new Intent(getActivity(), QuestionAskSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.personal_question_bt:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.personal_answer_bt:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onRefresh() {
        isActivity = true;
        if (mViewPager.getCurrentItem() == 0) {
            getService().getQuoraManager().getLatestQuestions(currentUser.getUserId());
        } else if (mViewPager.getCurrentItem() == 1) {
            getService().getQuoraManager().getLatestAnswers(null, currentUser.getUserId(), UserType.TEACHER);
        }
    }

    @Override
    public void onLoadMore() {
        if (mViewPager.getCurrentItem() == 0) {
            if (!CollectionUtils.isEmpty(mQuestions)) {
                Question question = mQuestions.get(mQuestions.size() - 1);
                getService().getQuoraManager().getHistoryQuestions(currentUser.getUserId(), question.getQuestionId());
            }
        } else if (mViewPager.getCurrentItem() == 1) {
            if (!CollectionUtils.isEmpty(mAnswers)) {
                Answer answer = mAnswers.get(mAnswers.size() - 1);
                getService().getQuoraManager().getHistoryAnswers(null, currentUser.getUserId(), UserType.TEACHER, answer.getAnswerId());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position >= 1){
            if (mViewPager.getCurrentItem() == 0) {
                Intent intent = new Intent(getActivity(), QuestionInfoActivity.class);
                intent.putExtra("question", mQuestions.get(position - 1));
                startActivity(intent);
            } else if (mViewPager.getCurrentItem() == 1) {
                Intent intent = new Intent(getActivity(), QuestionAskInfoActivity.class);
                intent.putExtra("answer", mAnswers.get(position - 1));
                intent.putExtra("isComment", false);
                startActivity(intent);
            }
        }
    }

//    @Override
//    public void onChangeed(int index) {
//        if (index == 0) {
//            if (CollectionUtils.isEmpty(mQuestions)) {
//                getService().getQuoraManager().getLatestQuestions(currentUser.getUserId());
//            }
//        } else if (index == 1) {
//            if (CollectionUtils.isEmpty(mAnswers)) {
//                getService().getQuoraManager().getLatestAnswers(null, currentUser.getUserId(), UserType.TEACHER);
//            }
//        }
//        updataError();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(PersonalFragment.this);
    }


    class NewQuestionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SysConstants.UPDATEQUESTION)) {
                refresh = true;
                //isActivity = true;
            }
        }
    }

    public void onEventMainThread(QuestionEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case GET_LATEST_QUESTION_SUCCESS:
                    mQuestionhasMore = event.isHasMore();
                    refreshQuestion(event.getQuestions());
                    mErrorLayout.setVisibility(View.GONE);
                    if (mQuestions.size() == 0) {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mErrorLayout.setErrorText("还没有提问哦");
                    }
                    MyLog.getLogger(TAG).d("获取当前用户的最新提问列表 size = " + event.getQuestions().size());
                    break;
                case GET_LATEST_QUESTION_FAILED:
                    mQuestionListView.stopRefresh();
                    showToast(event.getMsg());
                    showFooterView(mQuestionListView, false);
                    if (mQuestions.size() == 0) {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mErrorLayout.setErrorText("获取提问失败");
                    }
                    MyLog.getLogger(TAG).d("获取当前用户的最新提问列表失败 msg = " + event.getMsg());
                    break;
                case GET_HISTORY_QUESTION_SUCCESS:
                    mQuestionhasMore = event.isHasMore();
                    loadMorQuestion(event.getQuestions());
                    MyLog.getLogger(TAG).d("获取用户的历史记录成功 size = " + event.getQuestions().size());
                    break;
                case GET_HISTORY_QUESTION_FAILED:
                    mQuestionListView.stopLoadMore();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("获取用户的历史记录失败 msg = " + event.getMsg());
                    break;
            }
        }
    }

    public void onEventMainThread(AnswerEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case GET_LATEST_ANSWER_SUCCESS:
                    mAnswerhasMore = event.isHasMore();
                    resreshAnswer(event.getAnswers());
                    mErrorLayout.setVisibility(View.GONE);
                    if (mAnswers.size() == 0) {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mErrorLayout.setErrorText("还没有回答过问题哦");
                    }
                    MyLog.getLogger(TAG).d("获取当前用户的最新回答列表 size = " + event.getAnswers().size());
                    break;
                case GET_LATEST_ANSWER_FAILED:
                    mAnswerListview.stopRefresh();
                    showToast(event.getMsg());
                    showFooterView(mAnswerListview, event.isHasMore());
                    if (mAnswers.size() == 0) {
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mErrorLayout.setErrorText("获取回答失败");
                    }
                    MyLog.getLogger(TAG).d("获取当前用户的最新回答列表失败 msg = " + event.getMsg());
                    break;
                case GET_HISTORY_ANSWER_SUCCESS:
                    mAnswerhasMore = event.isHasMore();
                    loadMorAnswer(event.getAnswers());
                    MyLog.getLogger(TAG).d("获取用户的历史回答记录成功 size = " + event.getAnswers().size());
                    break;
                case GET_HISTORY_ANSWER_FAILED:
                    mAnswerListview.stopLoadMore();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("获取用户的历史回答记录失败 msg = " + event.getMsg());
                    break;
            }

        }


    }

    private void refreshQuestion(List<Question> refreshList) {
        if (refreshList != null && refreshList.size() > 0) {
            mQuestions.clear();
            mQuestions.addAll(refreshList);
        }
        updataQuestionAdapter();
        mQuestionListView.stopRefresh();
    }

    private void resreshAnswer(List<Answer> refreshList) {
        if (refreshList != null && refreshList.size() > 0) {
            mAnswers.clear();
            mAnswers.addAll(refreshList);
        }
        updataAnswerAdapter();
        mAnswerListview.stopRefresh();
    }

    public void loadMorQuestion(List<Question> list) {
        if (list != null && list.size() > 0) {
            mQuestions.addAll(list);
        }
        mQuestionListView.stopLoadMore();
        updataQuestionAdapter();
    }

    public void loadMorAnswer(List<Answer> list) {
        if (list != null && list.size() > 0) {
            mAnswers.addAll(list);
        }
        mAnswerListview.stopLoadMore();
        updataAnswerAdapter();
    }

    private void updataQuestionAdapter() {
        mQAdapter.notifyDataSetChanged();
        showFooterView(mQuestionListView, mQuestionhasMore);
    }

    private void updataAnswerAdapter() {
        mQAdapter.notifyDataSetChanged();
        showFooterView(mAnswerListview, mAnswerhasMore);
    }


    public void showFooterView(XListView listview, boolean hasMore) {
        if (hasMore) {
            listview.setPullLoadEnable(true);
        } else {
            listview.setPullLoadEnable(false);
        }
    }

    private void updataError(){
        if (mViewPager.getCurrentItem() == 0) {
            if (mQuestions.size() == 0) {
                mErrorLayout.setVisibility(View.VISIBLE);
                mErrorLayout.setErrorText("还没有提问哦");
            } else {
                mErrorLayout.setVisibility(View.GONE);
            }
        } else if (mViewPager.getCurrentItem() == 1) {
            if (mQuestions.size() == 0) {
                mErrorLayout.setVisibility(View.VISIBLE);
                mErrorLayout.setErrorText("还没有回答过问题哦");
            } else {
                mErrorLayout.setVisibility(View.GONE);
            }
        }
    }


    class OnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            if(i == 1){//回答
                mPersonalAnswerBt.setSelected(true);
                mPersonalQuestionBt.setSelected(false);
                if (CollectionUtils.isEmpty(mAnswers)) {
                    getService().getQuoraManager().getLatestAnswers(null, currentUser.getUserId(), UserType.TEACHER);
                }
            }else if(i == 0){//问题
                mPersonalAnswerBt.setSelected(false);
                mPersonalQuestionBt.setSelected(true);
                if (CollectionUtils.isEmpty(mQuestions)) {
                    getService().getQuoraManager().getLatestQuestions(currentUser.getUserId());
                }
            }

            updataError();
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }
}
