package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcsd.app.parent.R;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shan on 2016/4/8.
 * 刷新家庭作业列表
 */
public class StudyHomeWorkRankFrgAdapter extends BaseAdapter {

    private Context mContext;
    private List<HomeWorkUserRank> notices = new ArrayList<HomeWorkUserRank>();

    public StudyHomeWorkRankFrgAdapter(Context context, List<HomeWorkUserRank> notices) {

        mContext = context;
        this.notices = notices;
    }

    @Override
    public int getCount() {
        return notices.size();
    }

    @Override
    public Object getItem(int position) {
        return notices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_studyrecord_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.tvStuName = (TextView) convertView.findViewById(R.id.tv_home_studyrecord_name);
            viewHolder.tvStuStore = (TextView) convertView.findViewById(R.id.tv_homestudyrecord_score);
            viewHolder.ivStuPic = (RoundImageView) convertView.findViewById(R.id.iv_home_studyrecord_pic);
            viewHolder.ivEnter = (ImageView) convertView.findViewById(R.id.iv_home_studyrecord_enter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (notices.size() > 0) {
            HomeWorkUserRank data = notices.get(position);
            if (data != null) {
                viewHolder.tvStuName.setText(notices.get(position).getName());
                viewHolder.tvStuStore.setText(notices.get(position).getScore() + "分");
                viewHolder.ivStuPic.setImageUrl(notices.get(position).getAvatar(), R.drawable.default_avatar);
                //  com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(data.getAvatar() + SysConstants.Imgurlsuffix90, viewHolder.ivStuPic, ImageUtils.DIO_USER_ICON_round_4);
//viewHolder.ivStuPic=

//                if ((position + 1) == notices.size())
//                    viewHolder.line.setVisibility(View.GONE);
//                else
//                    viewHolder.line.setVisibility(View.VISIBLE);

            }

        }

        return convertView;
    }

    class ViewHolder {

        View line;
        public TextView tvStuName;
        public TextView tvStuStore;
        public RoundImageView ivStuPic;
        public ImageView ivEnter;

    }
}
