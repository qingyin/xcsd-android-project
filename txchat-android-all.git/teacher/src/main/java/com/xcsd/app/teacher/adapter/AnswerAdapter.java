package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xcsd.app.teacher.R;
import com.tuxing.sdk.modle.Answer;
import com.tuxing.sdk.modle.Expert;

import java.util.List;

/**
 * Created by Mingwei on 2015/12/1.
 */
public class AnswerAdapter extends BaseAdapter {
    Context mContext;
    private List<Answer> mList;
    private Expert mExpert;


    public AnswerAdapter(Context context, List<Answer> list, Expert expert) {
        super();
        this.mContext = context;
        this.mList = list;
        this.mExpert = expert;
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expert_detailed_item_title_summary, null);

            viewHolder.vCount = (TextView) convertView.findViewById(R.id.expert_detailed_count);
            viewHolder.vTitle = (TextView) convertView.findViewById(R.id.expert_detailed_title);
            viewHolder.vChecked = (TextView) convertView.findViewById(R.id.expert_detailed_item_qa_like);
            viewHolder.vLine = convertView.findViewById(R.id.expert_detailed_item_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.vCount.setText(mList.get(position).getQuestionTitle().toString());
        if(mExpert != null){
            viewHolder.vTitle.setText(mExpert.getName()+"回答了"+mList.get(position).getQuestionAuthor()+"的问题");
        }

        viewHolder.vChecked.setText(mList.get(position).getThanksCount().toString());
        viewHolder.vChecked.setSelected(mList.get(position).getThanked());
        viewHolder.vChecked.setEnabled(false);
        return convertView;
    }

    class ViewHolder {
        TextView vCount;
        TextView vTitle;
        TextView vChecked;
        View vLine;
    }
}
