package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.QuestionSelectAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.rpc.proto.TagType;
import com.tuxing.sdk.event.quora.QuestionEvent;
import com.tuxing.sdk.event.quora.QuestionTagEvent;
import com.tuxing.sdk.event.RelativeEvent;
import com.tuxing.sdk.modle.QuestionTag;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;


public class QuestionSelectActivity extends BaseActivity {

	private ListView list;
	private QuestionSelectAdapter adapter;
	private String TAG = QuestionSelectActivity.class.getSimpleName();
	private List<QuestionTag> datas;
	private int selectIndex = 100;
	private boolean isQuesFragement = false;
    private String questionTitle;
    private String questionContent;
    private long expertId;
    private QuestionTag currentTag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.question_select_layout);
		showProgressDialog(mContext, "", true, null);
		init();
	}
	
	private void init() {
		setTitle(getString(R.string.question_select_title));
		setLeftBack("", true, false);

		isQuesFragement = getIntent().getBooleanExtra("isQuesFragement",false);
        questionTitle = getIntent().getStringExtra("questionTitle");
        questionContent = getIntent().getStringExtra("questionContent");
        expertId = getIntent().getLongExtra("expertId",0);

		selectIndex = getIntent().getIntExtra("selectIndex",100);
		list = (ListView) findViewById(R.id.question_list);
		datas = new ArrayList<QuestionTag>();
		getService().getQuoraManager().getQuestionTags(TagType.TAG_QUESTION);
	}
	
	 public void onEventMainThread(RelativeEvent event) {
	        disProgressDialog();
	        switch (event.getEvent()) {

	        }
	    }
	
	
	 
	public void updateAdapter(){
		if(adapter == null){
			adapter = new QuestionSelectAdapter(mContext,datas,QuestionSelectActivity.this,selectIndex);
			list.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}
	


	public void onclickSelect() {
		super.onclickRightNext();
		int index = adapter.getIndex();
		if(index != 100){
            setRightNext(true,getString(R.string.question_replase), 0, true);
            setsetRightNextColor(R.color.teacher_help_theme_color);
            currentTag = datas.get(index);

		}else{
            setRightNext(true,getString(R.string.question_replase), 0, false);
			showToast("请选择分类");
		}

	}

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
            try {
                showProgressDialog(mContext, "", true, null);
                getService().getQuoraManager().askQuestion(expertId, currentTag.getTagId(),questionTitle,questionContent, SysConstants.attachments);
            } catch (Exception e) {
                disProgressDialog();
                e.printStackTrace();
            }
    }


    /**
     * 服务器返回
     * @param event
     */
    public void onEventMainThread(QuestionEvent event){
        switch (event.getEvent()) {
            case ASK_QUESTION_SUCCESS:
                disProgressDialog();
                Intent intent = new Intent(SysConstants.UPDATEQUESTION);
                intent.putExtra("isReplase",true);
                intent.putExtra("isQuesFragement",isQuesFragement);
                sendBroadcast(intent);
                showAndSaveLog(TAG, "发布问题成功  ", false);
				getService().getDataReportManager().reportEvent(EventType.ASK_QUESTION);
                SysConstants.attachments.clear();
                finish();
                break;
            case ASK_QUESTION_FAILED:
                showToast(event.getMsg());
                disProgressDialog();
                showAndSaveLog(TAG, "发布失败  ", false);
                break;
        }
    }

	public void onEventMainThread(QuestionTagEvent event) {
		if (isActivity) {
			disProgressDialog();
			switch (event.getEvent()){
				case FETCH_TAGS_SUCCESS:
					if(event.getTags() != null){
						datas = event.getTags();
					}
					updateAdapter();
					showAndSaveLog(TAG,"获取问题分类成功",false);
					break;
				case FETCH_TAGS_FAILED:
					showToast(event.getMsg());
					showAndSaveLog(TAG,"获取问题分类失败 msg = " + event.getMsg(),false);
					break;
			}
		}
		}
}
