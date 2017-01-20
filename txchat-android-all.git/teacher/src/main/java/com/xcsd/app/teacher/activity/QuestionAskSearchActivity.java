package com.xcsd.app.teacher.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.QuestionAskSearchAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.quora.SearchQuestionEvent;
import com.tuxing.sdk.modle.Question;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class QuestionAskSearchActivity extends BaseActivity implements XListView.IXListViewListener,XListView.OnItemClickListener {
    private EditText etTitle;
    private XListView mList;
    private QuestionAskSearchAdapter adapter;
    private List<Question> datas = new ArrayList<Question>();
    private boolean hasMore = false;
    private int currentPage = 1;
    private ReplaceReceiver questionReceiver;
    private boolean isRefresh = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.ques_ask_search_info_layout);
        init();
    }

    private void init() {
        setLeftBack("", true, false);
        setRightNext(true,"下一步", 0, false);
        etTitle = (EditText) findViewById(R.id.question_title);
        mList = (XListView)findViewById(R.id.ask_search_list);
        mList.setPullRefreshEnable(false);
        mList.setXListViewListener(this);
        mList.setOnItemClickListener(this);
        questionReceiver = new ReplaceReceiver();
        registerReceiver(questionReceiver, new IntentFilter(SysConstants.UPDATEQUESTION));

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(10 >= 20 - s.length() && 20 - s.length() > 0){
                    setTitle("还可以输入" + (20 - s.length()) + "字");
                }else{
                    setTitle(" ");
                }}

            @Override
            public void afterTextChanged(Editable editable) {
                if (etTitle.getText().toString().trim().length() >= 5) {
                    setRightNext(true, getString(R.string.question_next), 0, true);
                    setsetRightNextColor(R.color.text_teacher);
                } else
                    setRightNext(true,getString(R.string.question_next), 0, false);
                initData(editable.toString(),0);
            }
        });
        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    setTitle("");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isRefresh){
            isActivity = true;
            isRefresh = false;
            initData(etTitle.getText().toString().trim(),0);
        }
    }

    private void initData(String key, int page){
        getService().getQuoraManager().searchQuestion(key,page);
    }


    public void updateAdapter(){
        if(adapter == null){
            adapter = new QuestionAskSearchAdapter(mContext,datas,etTitle.getText().toString().trim());
            mList.setAdapter(adapter);
        }else{
            adapter.getKey(etTitle.getText().toString().trim());
        }
        showFooterView();
    }

    private void getRefresh(List<Question> refreshList) {
        if(refreshList != null){
            datas.clear();
            datas.addAll(refreshList);
        }
        mList.stopRefresh();
        updateAdapter();

    }


    public void getLoadMore(List<Question> list) {
        if(!CollectionUtils.isEmpty(list)){
           datas.addAll(list);
        }
        updateAdapter();
        mList.stopLoadMore();
    }

    public void showFooterView() {
        if (hasMore) {
           mList.setPullLoadEnable(true);
        } else {
            mList.setPullLoadEnable(false);
        }
    }

    @Override
    public void onRefresh() {}

    @Override
    public void onLoadMore() {
        initData(etTitle.getText().toString().trim(),currentPage++);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);


    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        String title = etTitle.getText().toString().trim();
       if(StringUtils.isBlank(title) || title.length() < 5){
            showToast("请输入问题标题(5-20字)");
        }else{
           Intent intent = new Intent(QuestionAskSearchActivity.this,QuestionReleaseActivity.class);
           intent.putExtra("questionTitle",etTitle.getText().toString().trim());
           startActivity(intent);
       }
    }

    public void onEventMainThread(SearchQuestionEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case SEARCH_QUESTION_SUCCESS:
                    hasMore = event.ishasMore();
                    if (currentPage == 1) {
                        getRefresh(event.getQuestions());
                    } else {
                        getLoadMore(event.getQuestions());
                    }
                    MyLog.getLogger(TAG).d("搜索问题列表成功 size = " + event.getQuestions().size());
                    break;
                case SEARCH_QUESTION_FAILED:
                    mList.stopRefresh();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("搜索问题列表失败 msg = " + event.getMsg());
                    break;

            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        position = position - 1;
        if(position >= 0){
            Intent intent = new Intent(QuestionAskSearchActivity.this,QuestionInfoActivity.class);
            intent.putExtra("question",datas.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if(questionReceiver != null){
            unregisterReceiver(questionReceiver);
        }
    }

    class ReplaceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("isReplase",false)){
                finish();
            }else{
                isRefresh = true;
            }

        }
    }
}
