package com.tuxing.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.view.RoundAngleImageView;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.Game_Score;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class GameScoreAdapter extends BaseAdapter {

    private Context mContext;
    private List<Game_Score> contentDatas;
    View view = null;

    public GameScoreAdapter(Context mContext, List<Game_Score> contentDatas) {
        this.mContext = mContext;
        this.contentDatas = contentDatas;
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
    public View getView(final int position, View contentView, ViewGroup mParent) {
        ViewHoler viewHolter = null;
        if (contentView == null) {
            viewHolter = new ViewHoler();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.game_score_item, null);
            viewHolter.Title = (TextView) contentView.findViewById(R.id.tv_explain_name);
            viewHolter.score = (TextView) contentView.findViewById(R.id.tv_explain_score_number);
            viewHolter.tips = (TextView) contentView.findViewById(R.id.tv_explain_tips);

            contentView.setTag(viewHolter);
        } else {
            viewHolter = (ViewHoler) contentView.getTag();
        }
        if (contentDatas.size() > 0) {
            Game_Score data = contentDatas.get(position);
            if (data != null) {
                viewHolter.Title.setText(data.getGameName() + ": " + data.getScore() + "分");
                viewHolter.Title.setTextColor(Color.parseColor(data.getColor()));
                viewHolter.score.setText(data.getBestLevel()+"");
                long score = Math.round(data.getPercentage()*100);
                viewHolter.tips.setText("超过所在年级"+score+"%的学生");

            }
        }
        return contentView;
    }

    public class ViewHoler {
        TextView tips;
        TextView score;
        TextView Title;
    }


}
