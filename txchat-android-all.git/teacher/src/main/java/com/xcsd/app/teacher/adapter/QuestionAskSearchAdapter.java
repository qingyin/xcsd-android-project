package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xcsd.app.teacher.R;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.modle.Question;

import java.util.List;


public class QuestionAskSearchAdapter extends BaseAdapter {

    Context mContext;
    private List<Question> datas;
    private String mKey;

    public QuestionAskSearchAdapter(Context context, List<Question> list, String key) {
        super();
        mContext = context;
        this.mKey = key;
        this.mContext = context;
        this.datas = list;
    }
    public void getKey(String key){
        mKey = key;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ques_ask_search_info_item, null);
            viewHolder.content = (TextView) convertView.findViewById(R.id.ask_search_content);
            viewHolder.time = (TextView) convertView.findViewById(R.id.ask_search_time);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.ask_search_comment);
            viewHolder.tile = (TextView) convertView.findViewById(R.id.ask_search_title);
            viewHolder.type = (TextView) convertView.findViewById(R.id.ask_search_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Question question = datas.get(position);
         viewHolder.tile.setText(Utils.SearchKeye(Utils.Html2Text(question.getTitleSummary()), mKey, mContext.getResources().getColor(com.tuxing.app.R.color.circle_pink)));
         viewHolder.content.setText(Utils.SearchKeye(Utils.Html2Text(question.getContentSummary()), mKey, mContext.getResources().getColor(com.tuxing.app.R.color.circle_pink)));
        viewHolder.comment.setText(Utils.commentCount(question.getAnswerCount()));
        viewHolder.type.setText(question.getTag());
        if(question.getCreateOn() != null){
            viewHolder.time.setText(Utils.getDateTime(mContext, question.getCreateOn()));
        }
        return convertView;
    }

    class ViewHolder {
        TextView tile;
        TextView content;
        TextView time;
        TextView comment;
        TextView type;
    }
}
