package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.app.parent.R;

import java.util.List;

/**
 * Created by bryant on 16/6/21.
 */
public class HomeWorkDetailsAdapter extends BaseAdapter {

    private static final String TAG = "HomeWorkDetailsAdapter";
    private LayoutInflater inflater;
    public Context mContext;
    public List<GameLevel> contentDatas;
    public boolean isshowscore;

    public HomeWorkDetailsAdapter(Context mContext, List<GameLevel> contentDatas ,boolean isshowscore) {
        this.mContext = mContext;
        this.contentDatas = contentDatas;
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
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.sorrt_item_name);
            holder.work = (TextView) convertView.findViewById(R.id.sorrt_item_work);
            holder.time = (TextView) convertView.findViewById(R.id.sorrt_item_time);
            holder.done = (TextView) convertView.findViewById(R.id.sorrt_item_done);
            holder.avatar = (RoundImageView) convertView.findViewById(R.id.sorrt_icon);
            holder.xing_ll = (LinearLayout) convertView.findViewById(R.id.xing_ll);
            holder.comment_dialog_xing_1 = (ImageView) convertView.findViewById(R.id.comment_dialog_xing_1);
            holder.comment_dialog_xing_2 = (ImageView) convertView.findViewById(R.id.comment_dialog_xing_2);
            holder.comment_dialog_xing_3 = (ImageView) convertView.findViewById(R.id.comment_dialog_xing_3);
            convertView.setTag(holder);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        }

        if (contentDatas.size() > 0) {
            GameLevel data = contentDatas.get(position);
            if (data != null) {
                holder.name.setText(data.getAbilityName());
                if (!data.getColor().isEmpty()){
                    holder.name.setTextColor( Color.parseColor(data.getColor()));
                }
                holder.work.setText(data.getGameName());
                holder.time.setText("第" + data.getLevel() + "关");

                holder.xing_ll.setVisibility(View.GONE);
                if (isshowscore) {
                    holder.xing_ll.setVisibility(View.VISIBLE);
//                    holder.done.setVisibility(View.GONE);
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

        return convertView;
    }

    private static class ViewHolder {

        /**
         * 班级名称，作业类型，时间，完成度，完成人数
         */
        TextView name, work, time, done, dones;
        LinearLayout xing_ll;
        ImageView comment_dialog_xing_1;
        ImageView comment_dialog_xing_2;
        ImageView comment_dialog_xing_3;

        /**
         * 作业类型图片
         */
        RoundImageView avatar;

    }
}
