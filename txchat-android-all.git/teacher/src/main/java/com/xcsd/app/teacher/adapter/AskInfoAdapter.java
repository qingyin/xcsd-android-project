package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.easemob.util.SmileUtils;
import com.xcsd.app.teacher.activity.ExpertDetailedActivity;
import com.xcsd.app.teacher.activity.QuestionAskInfoActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.TargetType;
import com.tuxing.rpc.proto.UserType;
import com.tuxing.sdk.db.entity.Comment;

import java.util.List;

public class AskInfoAdapter extends ArrayAdapter<Comment> {
    private Context mContext;
    private List<Comment> comments;
    private QuestionAskInfoActivity activity;
    private String msg = "确认要删除吗";
    private Comment delCommnet;

    public AskInfoAdapter(Context context, List<Comment> comments, QuestionAskInfoActivity activity) {
        super(context, 0, comments);
        this.mContext = context;
        this.comments = comments;
        this.activity = activity;
    }

    public void setData(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ask_info_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.question_info_item_icon);
            holder.name = (TextView) convertView.findViewById(R.id.question_info_item_name);
            holder.time = (TextView) convertView.findViewById(R.id.question_info_item_time);
            holder.ques_comment = (TextView) convertView.findViewById(R.id.ques_comment);
            holder.comment = (TextView) convertView.findViewById(R.id.question_info_item_ask_comment);
            holder.del = (TextView) convertView.findViewById(R.id.question_info_item_ask_del);
            holder.contnet = (TextView) convertView.findViewById(R.id.question_info_item_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.comment.setTag(position);
        holder.contnet.setTag(position);
        holder.icon.setTag(position);
        holder.del.setTag(position);
        if(position == 0){
            holder.ques_comment.setVisibility(View.VISIBLE);
        }else{
            holder.ques_comment.setVisibility(View.GONE);
        }

        Comment comment = comments.get(position);
        if(comment.getSenderId() == activity.user.getUserId()){
            holder.del.setVisibility(View.VISIBLE);
        }else{
            holder.del.setVisibility(View.GONE);
        }
            ImageLoader.getInstance().displayImage(comment.getSenderAvatar() + SysConstants.Imgurlsuffix90_png, holder.icon, ImageUtils.DIO_USER_ICON_round_4);
        holder.name.setText(comment.getSenderName());
        if (comment.getSendTime() != null) {
            holder.time.setText(Utils.getDateTime(mContext, comment.getSendTime()));
        }

        if (!activity.answer.getAuthorUserId().equals(comment.getReplayToUserId()) && !TextUtils.isEmpty(comment.getReplayToUserName())) {
            String content = comment.getContent();
            String toUserName;
            if(comment.getReplayToUserId() == activity.user.getUserId()){
                 toUserName = "自己";
            }else{
                 toUserName = comment.getReplayToUserName();
            }
            SpannableString spanStr = new SpannableString("回复" + toUserName + ":" +  content);

            spanStr.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.qzq_name)), 2, 2 + toUserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contnet.setText(SmileUtils.getSmiledText(mContext, spanStr));
        }else{
            holder.contnet.setText(SmileUtils.getSmiledText(mContext, comment.getContent()));
        }


        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (Integer) view.getTag();
                delCommnet = comments.get(index);
                activity.showDialog(null, msg, mContext.getResources().getString(R.string.cancel), mContext.getResources().getString(R.string.ok));
            }
        });
        holder.comment.setOnClickListener(new AskInfoListener());
        holder.contnet.setOnClickListener(new AskInfoListener());
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (Integer)view.getTag();
                if(comments.get(index).getUserType() == UserType.EXPERT){
                    Intent intent = new Intent(mContext,ExpertDetailedActivity.class);
                    intent.putExtra("expert_id", comments.get(index).getSenderId());
                    mContext.startActivity(intent);

                }else{
                    Comment clickComent = comments.get(index);
                    activity.currentType = 2;
                    activity.replyComment(activity.answer.getAnswerId(), clickComent.getSenderId(), clickComent.getSenderName(), CommentType.REPLY, TargetType.ANSWER, activity.currentType);
                }
            }
        });


        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView time;
        TextView comment;
        TextView contnet;
        TextView del;
        TextView ques_comment;
        ImageView icon;
    }

    public Comment getDelCommnet() {
        return delCommnet;
    }

    class AskInfoListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {


                // 进入回复详情页
                int index = (Integer) view.getTag();
                Comment clickComent = comments.get(index);
                activity.currentType = 2;
                activity.replyComment(activity.answer.getAnswerId(), clickComent.getSenderId(),clickComent.getSenderName(), CommentType.REPLY, TargetType.ANSWER, activity.currentType);
        }
    }
}
