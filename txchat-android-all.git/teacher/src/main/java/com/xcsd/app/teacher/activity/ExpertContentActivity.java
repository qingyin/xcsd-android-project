package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.AnswerAdapter;
import com.xcsd.app.teacher.adapter.BaoDianAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.event.quora.AnswerEvent;
import com.tuxing.sdk.event.quora.KnowledgeEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.modle.Answer;
import com.tuxing.sdk.modle.Knowledge;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

/**
 * Created by Mingwei on 2015/12/1.BaoDianAdapter
 */
public class ExpertContentActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private XListView mListView;

    private AnswerAdapter mAnswerAdapter;

    private BaoDianAdapter mKnowledgeAdapter;
    /**
     * 用来区分获取的是回答还是文章
     */
    private String mContentType;

    private Long mExpertId;

    private List<Answer> mAnswers = new ArrayList<>();

    private List<Knowledge> mKnowledges = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        isActivity = true;
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.expert_content_layout);
        init();
        initView();
        initData();
    }

    @Override
    public CoreService getService() {
        return super.getService();
    }

    private void init() {
        mContentType = getIntent().getStringExtra("type");
        mExpertId = getIntent().getLongExtra("expert_id", 0);
        if (!mContentType.isEmpty()) {
            setTitle(mContentType);
        } else {
            setTitle(getResources().getString(R.string.expert_detailed_title));
        }
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
    }

    private void initView() {
        mListView = (XListView) findViewById(R.id.xListView);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void initData() {
        if (!mContentType.isEmpty()) {
            if (matchType(R.string.expert_detailed_answer)) {
                mAnswerAdapter = new AnswerAdapter(this, mAnswers,null);
                mListView.setAdapter(mAnswerAdapter);
                getService().getQuoraManager().getLatestAnswers(null, mExpertId, UserType.EXPERT);
            } else if (matchType(R.string.expert_detailed_article)) {
                mKnowledgeAdapter = new BaoDianAdapter(this, mKnowledges);
                mListView.setAdapter(mKnowledgeAdapter);
                getService().getQuoraManager().getLatestKnowledge(null, mExpertId);
            }
        }

    }

    @Override
    protected void onResume() {
        isActivity = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isActivity = false;
        super.onPause();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position >= 1){
            if (matchType(R.string.expert_detailed_answer)) {
                Answer answer = mAnswers.get(position - 1);
                Intent intent = new Intent(mContext, QuestionAskInfoActivity.class);
                intent.putExtra("answer", answer);
                intent.putExtra("isComment", false);
                startActivityForResult(intent, 100);
            } else if (matchType(R.string.expert_detailed_article)) {
                Knowledge knowledge = mKnowledges.get(position - 1);
                Intent intent = new Intent();
                intent.setClass(this, BaoDianInfoActivity.class);
                intent.putExtra("itemUrl", knowledge.getContentUrl());
                intent.putExtra("itemId", knowledge.getKnowledgeId());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (matchType(R.string.expert_detailed_answer)) {
            getService().getQuoraManager().getLatestAnswers(null, mExpertId, UserType.EXPERT);
        } else if (matchType(R.string.expert_detailed_article)) {
            getService().getQuoraManager().getLatestKnowledge(null, mExpertId);
        }
    }

    @Override
    public void onLoadMore() {
        if (matchType(R.string.expert_detailed_answer)) {
            if (!CollectionUtils.isEmpty(mAnswers)) {
                Answer answer = mAnswers.get(mAnswers.size() - 1);
                getService().getQuoraManager().getHistoryAnswers(null, mExpertId, UserType.EXPERT, answer.getAnswerId());
            }
        } else if (matchType(R.string.expert_detailed_article)) {
            if (!CollectionUtils.isEmpty(mKnowledges)) {
                Knowledge knowledge = mKnowledges.get(mKnowledges.size() - 1);
                getService().getQuoraManager().getHistoryKnowledge(null, mExpertId, knowledge.getKnowledgeId());
            }
        }
    }

    private boolean matchType(int string) {
        return mContentType.equals(mContext.getResources().getString(string));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            boolean iscomment = data.getBooleanExtra("isComment", false);
            if (iscomment) {
                getService().getQuoraManager().getLatestAnswers(null, mExpertId, UserType.EXPERT);
            }
        }
    }

    public void onEventMainThread(AnswerEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_LATEST_ANSWER_SUCCESS:
                    mAnswers.clear();
                    mAnswers.addAll(event.getAnswers());
                    mListView.setPullLoadEnable(event.isHasMore());
                    mListView.stopRefresh();
                    mAnswerAdapter.notifyDataSetChanged();
                    break;
                case GET_LATEST_ANSWER_FAILED:
                    mListView.setPullLoadEnable(false);
                    mListView.stopRefresh();
                    showToast(event.getMsg());
                    break;
                case GET_HISTORY_ANSWER_SUCCESS:
                    mAnswers.addAll(event.getAnswers());
                    mListView.setPullLoadEnable(event.isHasMore());
                    mListView.stopLoadMore();
                    mAnswerAdapter.notifyDataSetChanged();
                    break;
                case GET_HISTORY_ANSWER_FAILED:
                    mListView.stopLoadMore();
                    showToast(event.getMsg());
                    break;
            }
        }
    }

    public void onEventMainThread(KnowledgeEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_LATEST_KNOWLEDGE_SUCCESS:
                    mKnowledges.clear();
                    mKnowledges.addAll(event.getKnowledges());
                    mListView.setPullLoadEnable(event.getHasMore());
                    mListView.stopRefresh();
                    mKnowledgeAdapter.notifyDataSetChanged();
                    break;
                case GET_LATEST_KNOWLEDGE_FAILED:
                    mListView.setPullLoadEnable(false);
                    mListView.stopRefresh();
                    showToast(event.getMsg());
                    break;
                case GET_HISTORY_KNOWLEDGE_SUCCESS:
                    mKnowledges.addAll(event.getKnowledges());
                    mListView.setPullLoadEnable(event.getHasMore());
                    mListView.stopLoadMore();
                    mKnowledgeAdapter.notifyDataSetChanged();
                    break;
                case GET_HISTORY_KNOWLEDGE_FAILED:
                    mListView.stopLoadMore();
                    showToast(event.getMsg());
                    break;
            }
        }
    }


}
