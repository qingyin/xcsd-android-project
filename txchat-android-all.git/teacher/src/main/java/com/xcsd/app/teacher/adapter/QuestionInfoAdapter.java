package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xcsd.app.teacher.activity.ExpertDetailedActivity;
import com.xcsd.app.teacher.activity.QuestionAskInfoActivity;
import com.xcsd.app.teacher.activity.QuestionInfoActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.modle.Answer;

import java.util.ArrayList;

public class QuestionInfoAdapter extends ArrayAdapter<Answer> {
    private Context mContext;
    private ArrayList<Answer> asks;
    private QuestionInfoActivity activity;
    private String msg = "确认要删除吗";
    private String title;
    private int index;
    private int expertAnswerSize;
    private PopupWindow mPopuwindows;
    private ImageView mImage;
    private AnimationSet mAnimationSet;
    private ScaleAnimation scaleAnimation;


    public QuestionInfoAdapter(Context context, ArrayList<Answer> asks, QuestionInfoActivity activity, int expertAnswerSize) {
        super(context, 0, asks);
        this.mContext = context;
        this.asks = asks;
        this.activity = activity;
        this.expertAnswerSize = expertAnswerSize;
        this.mImage = new ImageView(mContext);

        mImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.question_zan_press));
        mPopuwindows = new PopupWindow(mImage, Utils.dip2px(mContext, 20), Utils.dip2px(mContext, 20), false);
        mPopuwindows.setFocusable(true);
        mPopuwindows.setTouchable(true);
        mPopuwindows.setOutsideTouchable(true);
        mPopuwindows.setAnimationStyle(com.tuxing.app.R.style.like_anim);
        mPopuwindows.setBackgroundDrawable(new ColorDrawable());


    }

    public void setData(ArrayList<Answer> asks, int expertAnswerSize) {
        this.asks = asks;
        this.expertAnswerSize = expertAnswerSize;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.question_info_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.question_info_item_icon);
            holder.name = (TextView) convertView.findViewById(R.id.question_info_item_name);
            holder.time = (TextView) convertView.findViewById(R.id.question_info_item_time);
            holder.comment = (TextView) convertView.findViewById(R.id.question_info_item_ques_comment);
            holder.zan = (TextView) convertView.findViewById(R.id.question_info_item_zan);
            holder.job = (TextView) convertView.findViewById(R.id.question_info_item_job);
            holder.del = (TextView) convertView.findViewById(R.id.question_info_item_ques_del);
            holder.contnet = (TextView) convertView.findViewById(R.id.question_info_item_content);
            holder.rl_all = (RelativeLayout) convertView.findViewById(R.id.rl_question);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.del.setTag(position);
        holder.zan.setTag(position);
        holder.icon.setTag(position);
        holder.comment.setTag(position);
        holder.contnet.setTag(position);
        holder.contnet.setMaxLines(6);
        holder.contnet.setEllipsize(TextUtils.TruncateAt.END);

        Answer ask = asks.get(position);

        holder.rl_all.setTag(position);
         ImageLoader.getInstance().displayImage(ask.getAuthorUserAvatar() + SysConstants.Imgurlsuffix90_png,holder.icon, ImageUtils.DIO_USER_ICON_round_4);
        holder.name.setText(ask.getAuthorUserName());
        holder.contnet.setText(ask.getContent());
        holder.zan.setText(Utils.commentCount(ask.getThanksCount()));
        holder.comment.setText(Utils.commentCount(ask.getCommentCount()));
        if (ask.getUpdateOn() != null) {
            holder.time.setText(Utils.getDateTime(mContext, ask.getUpdateOn()));
        }
        if (ask.getThanked()) {
            holder.zan.setSelected(true);
        } else {
            holder.zan.setSelected(false);
        }
        if (ask.getAuthorUserId() == activity.user.getUserId()) {
            holder.del.setVisibility(View.VISIBLE);
        } else {
            holder.del.setVisibility(View.GONE);
        }
        if (ask.getExpert() && !TextUtils.isEmpty(ask.getAuthorTitle())) {
            holder.job.setVisibility(View.VISIBLE);
            holder.job.setText(ask.getAuthorTitle());
        } else {
            holder.job.setVisibility(View.GONE);
        }

        holder.zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (Integer) view.getTag();
                if (asks.get(index).getThanked()) {
                    activity.showToast(mContext.getResources().getString(R.string.question_prompt));
                } else {
                    SysConstants.isZan=true;
                    SysConstants.answerId=asks.get(position).getAnswerId()+"";

                    activity.replyComment(asks.get(index).getAnswerId());
                    int loc[] = new int[2];
                    view.getLocationInWindow(loc);
                    mPopuwindows.showAtLocation(view, Gravity.NO_GRAVITY, loc[0] - Utils.dip2px(mContext, 2), loc[1]);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPopuwindows.dismiss();
                        }
                    }, 500);
                }
            }
        });

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = (Integer) view.getTag();
                activity.showDialog(null, msg, mContext.getResources().getString(R.string.cancel), mContext.getResources().getString(R.string.ok));
            }
        });
        holder.comment.setOnClickListener(new AskInfoListener(true));
        holder.contnet.setOnClickListener(new AskInfoListener(false));
        holder.rl_all.setOnClickListener(new AskInfoListener(false));
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysConstants.answerId=asks.get(position).getAnswerId()+"";

                int index = (Integer) view.getTag();
                if (asks.get(index).getExpert()) {
                    Intent intent = new Intent(mContext, ExpertDetailedActivity.class);
                    intent.putExtra("expert_id", asks.get(index).getAuthorUserId());
                    mContext.startActivity(intent);

                } else {
                    activity.startActivity(mContext, QuestionAskInfoActivity.class, false, index);
                }
            }
        });


        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView time;
        TextView job;
        TextView zan;
        TextView comment;
        TextView contnet;
        TextView del;
        RelativeLayout rl_all;
        ImageView icon;
    }

    public int getClickIndex() {
        return index;
    }

    class AskInfoListener implements View.OnClickListener {
        private boolean isComment;

        public AskInfoListener(boolean isComment) {
            this.isComment = isComment;
        }

        @Override
        public void onClick(View view) {
            int index = (Integer) view.getTag();
            if (isComment) {
                activity.startActivity(mContext, QuestionAskInfoActivity.class, true, index);
            } else {
                activity.startActivity(mContext, QuestionAskInfoActivity.class, false, index);
            }
        }
    }


}
