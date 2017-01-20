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

/**
 * Created by Mingwei on 2015/12/2.
 */
public class PersonalQuestionAdapter extends BaseAdapter {

    Context mContext;
    private List<Question> mList;

    public PersonalQuestionAdapter(Context context, List<Question> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.personal_question_item, null);
            viewHolder.mQuestionType = (TextView) convertView.findViewById(R.id.personal_question_type);
            viewHolder.mQuestionTime = (TextView) convertView.findViewById(R.id.personal_question_time);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.personal_question_item_title);
            viewHolder.mCount = (TextView) convertView.findViewById(R.id.personal_question_answer_num);
            viewHolder.mBbottomView = convertView.findViewById(R.id.bottom_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mQuestionType.setText(mList.get(position).getTag());
        if(mList.get(position).getAnswerCount() <= 0){
            viewHolder.mCount.setVisibility(View.GONE);
        }else{ viewHolder.mCount.setVisibility(View.VISIBLE);
            viewHolder.mCount.setText(Utils.commentCount(mList.get(position).getAnswerCount()));}
        viewHolder.mQuestionTime.setText(Utils.getDateTime(mContext, mList.get(position).getCreateOn()));
        viewHolder.mTitle.setText(mList.get(position).getTitle().toString());
        if(position == mList.size() - 1){
            viewHolder.mBbottomView.setVisibility(View.VISIBLE);
        }else
            viewHolder.mBbottomView.setVisibility(View.GONE);
        return convertView;
    }

    class ViewHolder {
        TextView mQuestionType;
        TextView mQuestionTime;
        TextView mTitle;
        TextView mCount;
        View mBbottomView;
    }
}
