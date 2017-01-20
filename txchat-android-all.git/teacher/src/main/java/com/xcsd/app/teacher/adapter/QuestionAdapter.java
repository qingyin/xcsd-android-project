package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.modle.Question;

import java.util.List;


/**
 * Created by mo on 2015/12/1.
 */
public class QuestionAdapter extends ArrayAdapter<Question>{
    private Context mContext;
    private List<Question> questions;

    public QuestionAdapter(Context context, List<Question> questions) {
        super(context,0, questions);
        this.mContext = context;
        this.questions = questions;
    }

    public void setData(List<Question> questions){
        this.questions.clear();
        this.questions.addAll(questions);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.teacher_help_question_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.head_icon);
            holder.name = (TextView) convertView.findViewById(R.id.question_name);
            holder.time = (TextView) convertView.findViewById(R.id.question_time);
            holder.huifu = (TextView) convertView.findViewById(R.id.question_huifu);
            holder.title = (TextView) convertView.findViewById(R.id.question_title);
            holder.view = (TextView) convertView.findViewById(R.id.bottom_view);
            holder.content = (TextView) convertView.findViewById(R.id.question_content);
            holder.type = (TextView) convertView.findViewById(R.id.question_type);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
       Question data =  questions.get(position);
        ImageLoader.getInstance().displayImage(data.getAuthorUserAvatar() + SysConstants.Imgurlsuffix90,holder.icon, ImageUtils.DIO_USER_ICON_round_4);
        holder.name.setText(data.getAuthorUserName());
        holder.content.setText(data.getContent());
        holder.title.setText(data.getTitle());
        holder.type.setText(data.getTag());

        if(data.getAnswerCount() != null && data.getAnswerCount() > 0){
            holder.huifu.setVisibility(View.VISIBLE);
           holder.huifu.setText(Utils.commentCount(data.getAnswerCount()));
        }else{
            holder.huifu.setVisibility(View.GONE);
        }

        if(data.getCreateOn() != null){
            holder.time.setText(Utils.getDateTime(mContext, data.getCreateOn()));
        }
        if(position == questions.size() - 1){
            holder.view.setVisibility(View.VISIBLE);
        }else{
            holder.view.setVisibility(View.GONE);
        }



        return convertView;
    }

    class ViewHolder{
    TextView name;
    TextView time;
    TextView huifu;
    TextView type;
    TextView title;
    TextView view;
    TextView content;
    ImageView icon;
    }
}
