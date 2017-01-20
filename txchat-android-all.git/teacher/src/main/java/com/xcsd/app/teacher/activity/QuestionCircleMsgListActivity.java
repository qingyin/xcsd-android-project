package com.xcsd.app.teacher.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.CommunionAction;
import com.tuxing.sdk.event.quora.CommunionMessageEvent;
import com.tuxing.sdk.modle.CommunionMessage;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

/**
 * Created by wangst on 15-12-8.
 */
public class QuestionCircleMsgListActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private static final String TAG = QuestionCircleMsgListActivity.class.getSimpleName();
    private XListView listView;
    private boolean hasMore = false;
    private List<CommunionMessage> questionList = new ArrayList<>();
    private QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.question_circle_msg);
        init();
        initData();
    }

    private void init() {
        setTitle("教师成长消息");
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        listView = (XListView) findViewById(R.id.question_list);
        listView.setXListViewListener(this);
        listView.setOnItemClickListener(this);
        adapter = new QuestionAdapter(mContext, new ArrayList<CommunionMessage>());
        listView.setAdapter(adapter);
        listView.startRefresh();
    }

    private void initData() {
        getService().getQuoraManager().getLatestMessages();
    }

    private void updateAdapter() {
        if (listView.getAdapter() == null) {
            adapter = new QuestionAdapter(mContext, questionList);
            listView.setAdapter(adapter);
        } else {
            adapter.setData(questionList);
        }
        showFooterView();
    }

    public void showFooterView() {
        if (hasMore) {
            listView.setPullLoadEnable(true);
        } else {
            listView.setPullLoadEnable(false);
        }
    }

    @Override
    public void onRefresh() {
        getService().getQuoraManager().getLatestMessages();
    }

    @Override
    public void onLoadMore() {
        if(!CollectionUtils.isEmpty(questionList)){
            long maxId = questionList.get(questionList.size() - 1).getMessageId();
            getService().getQuoraManager().getHistoryMessage(maxId);
        }
    }


    private void getRefresh(List<CommunionMessage> refreshList) {
        if (!CollectionUtils.isEmpty(refreshList)) {
            questionList.clear();
            questionList.addAll(refreshList);
        }
        updateAdapter();
        listView.stopRefresh();
    }

    public void getLoadMore(List<CommunionMessage> list) {
        if (!CollectionUtils.isEmpty(list)) {
            questionList.addAll(list);
        }
        updateAdapter();
        listView.stopLoadMore();
    }

    public void onEventMainThread(CommunionMessageEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case FETCH_LATEST_MESSAGE_SUCCESS:
                    hasMore = event.isHasMore();
                    getRefresh(event.getMessages());
                    getService().getCounterManager().resetCounter(Constants.COUNTER.COMMUNION);
                    MyLog.getLogger(TAG).d("获取最热的问题列表成功 size = " + event.getMessages().size());
                    break;
                case FETCH_LATEST_MESSAGE_FAILED:
                    listView.stopRefresh();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("获取最热的问题列表失败 msg = " + event.getMsg());
                    break;
                case FETCH_HISTORY_MESSAGE_SUCCESS:
                    hasMore = event.isHasMore();
                    getLoadMore(event.getMessages());
                    MyLog.getLogger(TAG).d("获取最热的问题列表成功 size = " + event.getMessages().size());
                    break;
                case FETCH_HISTORY_MESSAGE_FAILED:
                    listView.stopRefresh();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("获取最热的问题列表失败 msg = " + event.getMsg());
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position > 0 && position <= questionList.size()) {
            CommunionMessage message = questionList.get(position - 1);

            if (message.getAction() == CommunionAction.A_ANSWER && message.getQuestion() != null) {
                Intent intent = new Intent(mContext, QuestionInfoActivity.class);
                intent.putExtra("question", message.getQuestion());
                startActivity(intent);
            } else if ((message.getAction() == CommunionAction.A_REPLY ||
                    message.getAction() == CommunionAction.A_THANK) &&
                    message.getAnswer() != null) {
                Intent intent = new Intent(mContext, QuestionAskInfoActivity.class);
                intent.putExtra("answer", message.getAnswer());
                intent.putExtra("isComment", false);
                startActivity(intent);
            }
        }

    }


    class QuestionAdapter extends ArrayAdapter<CommunionMessage> {
        private Context mContext;
        private List<CommunionMessage> questions;

        public QuestionAdapter(Context context, List<CommunionMessage> questions) {
            super(context, 0, questions);
            this.mContext = context;
            this.questions = questions;
        }

        public void setData(List<CommunionMessage> questions) {
            this.questions.clear();
            this.questions.addAll(questions);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.question_circle_msg_item, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.head_icon);
                holder.name = (TextView) convertView.findViewById(R.id.question_name);
                holder.subname = (TextView) convertView.findViewById(R.id.question_sub_name);
                holder.huifu = (TextView) convertView.findViewById(R.id.question_huifu);
                holder.title = (TextView) convertView.findViewById(R.id.question_title);
                holder.content = (TextView) convertView.findViewById(R.id.question_content);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CommunionMessage data = questions.get(position);
                ImageLoader.getInstance().displayImage(data.getUserAvatar() + SysConstants.Imgurlsuffix90,holder.icon, ImageUtils.DIO_USER_ICON);
            holder.name.setText(data.getUserName());
            String huifu = "";
            switch (data.getAction()){
                case A_ANSWER:
                    huifu = "回答了该问题";
                    break;
                case A_THANK:
                    huifu = "对你的答案表示感谢";
                    break;
                case A_FAVORITE:
                    break;
                case A_FOLLOW:
                    break;
                case A_REPLY:
                    huifu = "回复了你的答案";
                    break;
                default:
                    break;
            }
            holder.huifu.setText(huifu);
            holder.title.setText(data.getTitle());

            if(data.getAction() == CommunionAction.A_ANSWER){
                holder.content.setVisibility(View.GONE);
            }else {
                holder.content.setVisibility(View.VISIBLE);
                holder.content.setText(data.getContent());
            }
            if(!TextUtils.isEmpty(data.getUserTitle())){
                holder.subname.setVisibility(View.VISIBLE);
                holder.subname.setText(data.getUserTitle()+"");
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,0,0,0);
                holder.name.setLayoutParams(lp);
            }else{
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, Utils.dip2px(mContext, 8),0,0);
                holder.name.setLayoutParams(lp);
                holder.subname.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder {
            TextView name;
            TextView subname;
            TextView huifu;
            TextView title;
            TextView content;
            ImageView icon;
        }
    }
}
