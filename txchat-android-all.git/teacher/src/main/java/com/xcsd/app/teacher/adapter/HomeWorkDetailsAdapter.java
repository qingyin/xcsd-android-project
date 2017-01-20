package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.GameLevel;
import com.xcsd.app.teacher.R;
import com.xcsd.app.teacher.activity.SetUniteWorkActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by bryant on 16/6/21.
 */
public class HomeWorkDetailsAdapter extends BaseAdapter {

    private static final String TAG = "HomeWorkDetailsAdapter";
    private LayoutInflater inflater;
    public Context mContext;
    public List<GameLevel> contentDatas;
    public boolean bl = false;
    public boolean isshowscore = false;

    public HomeWorkDetailsAdapter(Context mContext, List<GameLevel> contentDatas, boolean b, boolean isshowscore) {
        this.mContext = mContext;
        this.contentDatas = contentDatas;
        this.bl = b;
        this.isshowscore = isshowscore;
    }

    @Override
    public int getCount() {
        return contentDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return contentDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_work_details, parent, false);
        }
        holder = (ViewHolder) convertView.getTag();
        if (holder == null) {

//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        HashMap<Integer, View> cuoluan = new HashMap<Integer, View>();
//        ViewHolder holder = null;
//        if (cuoluan.get(position) != null) {
//            convertView = cuoluan.get(position);
//            holder = (ViewHolder) convertView.getTag();
//        } else {
            holder = new ViewHolder();
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_work_details, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.sorrt_item_name);
            holder.work = (TextView) convertView.findViewById(R.id.sorrt_item_work);
            holder.time = (TextView) convertView.findViewById(R.id.sorrt_item_time);
            holder.done = (TextView) convertView.findViewById(R.id.sorrt_item_done);
            holder.avatar = (RoundImageView) convertView.findViewById(R.id.sorrt_icon);
            holder.avatar_one = (RoundImageView) convertView.findViewById(R.id.sorrt_icon_one);
            holder.name_one = (TextView) convertView.findViewById(R.id.sorrt_item_name_one);
            holder.work_one = (TextView) convertView.findViewById(R.id.sorrt_item_work_one);
            holder.time_one = (TextView) convertView.findViewById(R.id.sorrt_item_time_one);
            holder.sorrt_item_done_level = (TextView) convertView.findViewById(R.id.sorrt_item_done_level);
            holder.xing_ll = (LinearLayout) convertView.findViewById(R.id.xing_ll);

            holder.rl_one = (RelativeLayout) convertView.findViewById(R.id.rl_one);
            holder.rl_show = (RelativeLayout) convertView.findViewById(R.id.rl_show);
            holder.sorrt_item_done_level_one = (TextView) convertView.findViewById(R.id.sorrt_item_done_level_one);
            holder.rl_change_color = (LinearLayout) convertView.findViewById(R.id.rl_change_color);
            holder.comment_dialog_xing_1 = (ImageView) convertView.findViewById(R.id.comment_dialog_xing_1);
            holder.comment_dialog_xing_2 = (ImageView) convertView.findViewById(R.id.comment_dialog_xing_2);
            holder.comment_dialog_xing_3 = (ImageView) convertView.findViewById(R.id.comment_dialog_xing_3);
//            cuoluan.put(position, convertView);
            convertView.setTag(holder);
        }

        if (contentDatas.size() > 0) {
            GameLevel data = contentDatas.get(position);
//            if (data.getGameId() == 1) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_1));
//            } else if (data.getGameId() == 2) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_2));
//            } else if (data.getGameId() == 3) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_3));
//            } else if (data.getGameId() == 4) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_4));
//            } else if (data.getGameId() == 5) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_5));
//            } else if (data.getGameId() == 6) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_6));
//            } else if (data.getGameId() == 7) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_7));
//            } else if (data.getGameId() == 8) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_8));
//            } else if (data.getGameId() == 9) {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_9));
//            } else {
//                holder.name.setTextColor(mContext.getResources().getColor(R.color.game_1));
//            }
            if (data != null) {
                if (!data.getChoicelevel().isEmpty() && !data.getChoicelevel().equals("") && !data.getChoicelevel().equals("null")) {
                    holder.rl_one.setVisibility(View.VISIBLE);
                    holder.rl_show.setVisibility(View.GONE);
                    holder.name_one.setText(data.getAbilityName());
                    holder.work_one.setText(data.getGameName());
                    holder.time_one.setText("共" + data.getLevel() + "关");
                    holder.sorrt_item_done_level_one.setText(data.getChoicelevel());
                    holder.avatar_one.setImageUrl(data.getPicUrl(), com.tuxing.app.R.drawable.default_image);
                } else {
                    holder.rl_one.setVisibility(View.GONE);
                    holder.rl_show.setVisibility(View.VISIBLE);
                    holder.name.setText(data.getAbilityName());
                    holder.work.setText(data.getGameName());
                    if (bl) {
                        holder.time.setText("共" + data.getLevel() + "关");
//                    if (!data.getChoicelevel().isEmpty() && !data.getChoicelevel().equals("")) {
//                        holder.sorrt_item_done_level.setText(data.getChoicelevel());
//                        holder.rl_one.setVisibility(View.VISIBLE);
//                        holder.rl_show.setVisibility(View.GONE);
//                        holder.sorrt_item_done_level_one.setText(data.getChoicelevel());
////                        holder.rl_change_color.setBackgroundColor(Color.parseColor("#f9b44b"));
//
//                    }
                    } else {
                        holder.time.setText("第" + data.getLevel() + "关");
                        holder.sorrt_item_done_level.setVisibility(View.GONE);
                    }
                    if (!data.getColor().isEmpty()){
                        holder.name.setTextColor(Color.parseColor(data.getColor()));
                    }

                    holder.xing_ll.setVisibility(View.GONE);
                    if (isshowscore) {
                        holder.xing_ll.setVisibility(View.VISIBLE);
                        holder.done.setVisibility(View.GONE);
                        if (data.getStars().equals(1)) {
                            holder.comment_dialog_xing_1.setImageResource(R.drawable.game_star_big_01);
                            holder.comment_dialog_xing_2.setClickable(false);
                            holder.comment_dialog_xing_3.setClickable(false);
                        } else if (data.getStars().equals(2)) {
                            holder.comment_dialog_xing_1.setImageResource(R.drawable.game_star_big_01);
                            holder.comment_dialog_xing_2.setImageResource(R.drawable.game_star_big_01);
                        } else if (data.getStars().equals(3)) {
                            holder.comment_dialog_xing_1.setImageResource(R.drawable.game_star_big_01);
                            holder.comment_dialog_xing_2.setImageResource(R.drawable.game_star_big_01);
                            holder.comment_dialog_xing_3.setImageResource(R.drawable.game_star_big_01);
                        } else if (data.getStars().equals(0)) {
                            holder.xing_ll.setVisibility(View.GONE);
                            holder.done.setVisibility(View.VISIBLE);
                        }
                    }
                    holder.avatar.setImageUrl(data.getPicUrl(), com.tuxing.app.R.drawable.default_image);

                }
            }
        }


//        holder.name.setText(list.get(position).);
//        数据及联网逻辑未完成,数据不对
        return convertView;
    }

    private static class ViewHolder {

        /**
         *
         */
        TextView name, work, time, done, dones, sorrt_item_done_level;
        TextView name_one, work_one, time_one, done_one, sorrt_item_done_level_one;
        LinearLayout xing_ll;
        LinearLayout rl_change_color;
        RelativeLayout rl_one,rl_show;
        ImageView comment_dialog_xing_1;
        ImageView comment_dialog_xing_2;
        ImageView comment_dialog_xing_3;

        /**
         * 图片
         */
        RoundImageView avatar;
        RoundImageView avatar_one;

    }

}
