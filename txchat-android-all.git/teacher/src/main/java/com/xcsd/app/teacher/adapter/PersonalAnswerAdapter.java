package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xcsd.app.teacher.R;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.modle.Answer;

import java.util.List;

/**
 * Created by Mingwei on 2015/12/2.
 */
public class PersonalAnswerAdapter extends BaseAdapter {

    Context mContext;
    private List<Answer> mList;

    public PersonalAnswerAdapter(Context context, List<Answer> list) {
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.personal_answer_item, null);
            viewHolder.mAnswerType = (TextView) convertView.findViewById(R.id.personal_answer_type);
            viewHolder.mAnswerNum = (TextView) convertView.findViewById(R.id.personal_answer_num);
            viewHolder.mAnswerTime = (TextView) convertView.findViewById(R.id.personal_answer_time);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.personal_answer_item_title);
            viewHolder.mBbottomView = convertView.findViewById(R.id.bottom_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mAnswerType.setText(mList.get(position).getQuestionTagName());
        if(mList.get(position).getCommentCount() <= 0){
            viewHolder.mAnswerNum.setVisibility(View.GONE);
        }else{ viewHolder.mAnswerNum.setVisibility(View.VISIBLE);
            viewHolder.mAnswerNum.setText(Utils.commentCount(mList.get(position).getCommentCount()));}
        viewHolder.mAnswerTime.setText(Utils.getDateTime(mContext, mList.get(position).getCreateOn()));
        viewHolder.mTitle.setText(mList.get(position).getQuestionTitle().toString().trim());
        if(position == mList.size() - 1){
            viewHolder.mBbottomView.setVisibility(View.VISIBLE);
        }else
            viewHolder.mBbottomView.setVisibility(View.GONE);

        return convertView;
    }

    class ViewHolder {
        TextView mAnswerType;
        TextView mAnswerNum;
        TextView mAnswerTime;
        TextView mTitle;
        View mBbottomView;
    }
}
