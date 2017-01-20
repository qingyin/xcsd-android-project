package com.xcsd.app.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.AnswerAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.ExpandTextView;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.quora.AnswerEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.modle.Answer;
import com.tuxing.sdk.modle.Expert;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

/**
 * Created by Mingwei on 2015/11/30.
 * 专家详情页
 */
public class ExpertDetailedActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener, AdapterView.OnItemClickListener {


    /**
     * 专家详情页面的Banner
     */

    Long expert_id;
    private ImageView mExpertImage;
    private TextView mExpertName;
    private TextView mExpertLevel;
    private TextView mExpertSign;
    private int backColorid = 0;
    private RelativeLayout title_bar;
    private TextView title;
    private View headView;
    private LinearLayout ll_left;
    private Button btRight;
    private View line_view;

    /**
     * 个人简介
     */
    private ExpandTextView mExpandTextView;
    private ImageView mIntroMore;
    private boolean hasMore = false;

    private XListView mListView;
    private AnswerAdapter mAnswerAdapter;
    private List<Answer> mAnswers = new ArrayList<>();
    private Expert mExpert;
    private LinearLayout expert_head;
    private int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isActivity = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_detailed);
        mExpert = (Expert) getIntent().getSerializableExtra("expert");
        backColorid = getIntent().getIntExtra("backColorid", R.color.bg);
        title_bar = (RelativeLayout) findViewById(R.id.rl_title_bar);
        headView = getLayoutInflater().inflate(R.layout.expert_detailed_head_layout, null);
        ll_left = (LinearLayout) findViewById(R.id.ll_left);
        mListView = (XListView) findViewById(R.id.xListView);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new onListViewScrollListener());
        title = (TextView) findViewById(R.id.tv_title);
        btRight = (Button) findViewById(R.id.tv_right);

        initHeadView();
        setExpert(mExpert);
        title.setText(getResources().getString(R.string.expert_detailed_title));
        findViewById(R.id.tv_right).setOnClickListener(this);
        findViewById(R.id.ll_left).setOnClickListener(this);
        title_bar.setBackgroundResource(backColorid);
        headView.setBackgroundResource(backColorid);
        ll_left.setBackgroundResource(backColorid);
        btRight.setBackgroundResource(backColorid);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        updateAdapter();
        initData();
    }

    private void initHeadView(){
        /**
         * 个人简介
         */
        mExpertSign = (TextView) headView.findViewById(R.id.expert_detailed_introduction);
        mExpandTextView = (ExpandTextView) headView.findViewById(R.id.expert_detailed_intro_content_layout);
        expert_head = (LinearLayout)headView.findViewById(R.id.expert_head);
        mExpandTextView.setTextColor(getResources().getColor(R.color.gray));
        mExpandTextView.setTextSize(14);
        mExpandTextView.setTextMaxLine(3);
        mExpandTextView.setTextLineSpacingExtra(10);
        mIntroMore = (ImageView) headView.findViewById(R.id.expert_detailed_intro_more);
        line_view = headView.findViewById(R.id.line_view);
        mExpertName = (TextView) headView.findViewById(R.id.expert_detailed_name);
        mExpertLevel = (TextView) headView.findViewById(R.id.expert_detailed_level);
        mExpertImage = (ImageView) headView.findViewById(R.id.expert_detailed_head);



    }




    private void initData() {
        expert_id = getIntent().getLongExtra("expert_id", 0);
        getService().getQuoraManager().getLatestAnswers(null, expert_id, UserType.EXPERT);
    }

    public void setExpert(Expert expert) {
        mExpertName.setText(expert.getName());
        mExpertLevel.setText(expert.getTitle());
        mExpandTextView.setText(expert.getDescription());
        mExpertSign.setText(expert.getSign());
        int screenWidth = Utils.getDisplayWidth(mContext);
        int width  = screenWidth /2;
         height = width*566/621;
        String format = "?imageView2/1/w/" + width + "/h/" +height + "/format/png";
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        expert_head.setLayoutParams(lp);
        String avatarUrl = expert.getAvatar() + format;
        ImageLoader.getInstance().displayImage(avatarUrl,mExpertImage   , com.tuxing.app.util.ImageUtils.DIO_EXPERT_AVATAR);

//        mIntroMore.setTag(this.getString(R.string.expert_detailed_introduction));
        mExpandTextView.post(new Runnable() {
            @Override
            public void run() {
                if (mExpandTextView.text().getLineCount() < mExpandTextView.line()) {
                    mIntroMore.setVisibility(View.GONE);
                } else {
                    mIntroMore.setVisibility(View.VISIBLE);
                }
            }
        });
        mIntroMore.setImageResource(R.drawable.show_more_content);
        mIntroMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandTextView.switchs();
                if (mExpandTextView.isExpand()) {
                    mIntroMore.setImageResource(R.drawable.show_more_down);
                } else {
                    mIntroMore.setImageResource(R.drawable.show_more_content);
                }
            }
        });
    }

    @Override
    public CoreService getService() {
        return super.getService();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_right) {
            Intent intent = null;
            if (mExpert != null) {
                if (mExpert.getSpecialities() != null && mExpert.getSpecialities().size() > 0) {
                    intent = new Intent(this, QuestionAskSearchActivity.class);
                    intent.putExtra("selectIndex", Integer.MAX_VALUE);
                    intent.putExtra("questionTag", mExpert.getSpecialities().get(0));
                    intent.putExtra("expert", mExpert.getExpertId());
                } else {
                    intent = new Intent(this, QuestionAskSearchActivity.class);
                }
                startActivity(intent);
            }

        } else if (v.getId() == R.id.ll_left) {
            finish();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            boolean iscomment = data.getBooleanExtra("isComment", false);
            if (iscomment) {
                getService().getQuoraManager().getExpertDetail(expert_id);
            }
        }
    }

    public void onEventMainThread(AnswerEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_LATEST_ANSWER_SUCCESS:
                    getResresh(event.getAnswers());
                    hasMore = event.isHasMore();
                    updateAdapter();
                    break;
                case GET_LATEST_ANSWER_FAILED:
                    mListView.setPullLoadEnable(false);
                    mListView.stopRefresh();
                    showToast(event.getMsg());
                    break;
                case GET_HISTORY_ANSWER_SUCCESS:
                    getLoadMore(event.getAnswers());
                    hasMore = event.isHasMore();
                    break;
                case GET_HISTORY_ANSWER_FAILED:
                    mListView.stopLoadMore();
                    showToast(event.getMsg());
                    break;
            }
        }
    }

    public void onEventMainThread(CommentEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case REPLAY_ANSWER_SUCCESS:
                    //getService().getQuoraManager().getExpertDetail(expert_id);
                    showAndSaveLog(TAG, "赞成功 ExpertId = " + expert_id, false);
                    break;
                case REPLAY_ANSWER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "赞失败 ExpertId = " + expert_id, false);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivity = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivity = false;
    }

    @Override
    public void onRefresh() {
        getService().getQuoraManager().getLatestAnswers(null, expert_id, UserType.EXPERT);
    }

    private void getResresh(List<Answer> mAnswer) {
        if (mAnswer != null) {
            mAnswers.clear();
            mAnswers.addAll(mAnswer);
        }
        updateAdapter();
        mListView.stopRefresh();
    }

    public void getLoadMore(List<Answer> mAnswer) {
        if (mAnswer != null && mAnswer.size() > 0) {
            mAnswers.addAll(mAnswer);
        }
        updateAdapter();
        mListView.stopLoadMore();

    }

    @Override
    public void onLoadMore() {
        if (!CollectionUtils.isEmpty(mAnswers)) {
            Answer answer = mAnswers.get(mAnswers.size() - 1);
            getService().getQuoraManager().getHistoryAnswers(null, expert_id, UserType.EXPERT, answer.getAnswerId());
        }
    }

    public void updateAdapter() {
        if (!CollectionUtils.isEmpty(mAnswers)) {
            line_view.setVisibility(View.VISIBLE);
        }else{
            line_view.setVisibility(View.GONE);
        }
        if(mAnswerAdapter == null){
            mListView.addHeaderView(headView);
            mAnswerAdapter = new AnswerAdapter(this, mAnswers,mExpert);
            mListView.setAdapter(mAnswerAdapter);
        }else{
            mAnswerAdapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    public void showFooterView() {
        if (hasMore) {
            mListView.setPullLoadEnable(true);
        } else {
            mListView.setPullLoadEnable(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        position = position - 2;
        if(position >= 0){
        Answer answer = mAnswers.get(position);
        Intent intent = new Intent(mContext, QuestionAskInfoActivity.class);
        intent.putExtra("answer", answer);
        intent.putExtra("isComment", false);
        startActivityForResult(intent, 100);
    }}

    class AnswerListener implements View.OnClickListener {
        Answer mAnswer;


        public AnswerListener(Answer answer) {
            this.mAnswer = answer;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), QuestionAskInfoActivity.class);
            intent.putExtra("answer", mAnswer);
            intent.putExtra("isComment", false);
            startActivityForResult(intent, 100);
        }
    }

    class MoreClickListener implements View.OnClickListener {

        Context mContext;
        Long mExpertId;

        public MoreClickListener(Context context, Long expertId) {
            this.mContext = context;
            this.mExpertId = expertId;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, ExpertContentActivity.class);
            intent.putExtra("type", mContext.getResources().getString(R.string.expert_detailed_answer));
            intent.putExtra("expert_id", mExpertId);
            mContext.startActivity(intent);
        }
    }


    private class onListViewScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 1 || firstVisibleItem == 0) {
                    ll_left.setBackgroundResource(backColorid);
                    title_bar.setBackgroundResource(backColorid);
                    btRight.setBackgroundResource(backColorid);
                } else {
                    ll_left.setBackgroundResource(R.color.white);
                    btRight.setBackgroundResource(R.color.white);
                    title_bar.setBackgroundResource(R.color.white);

            }
        }
    }
}
