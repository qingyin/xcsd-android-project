package com.tuxing.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.view.RoundAngleImageView;
import com.tuxing.sdk.db.entity.ContentItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AnnouncementAdapter extends BaseAdapter {

    private Context mContext;
    private List<ContentItem> contentDatas;
    View view = null;

    public AnnouncementAdapter(Context mContext, List<ContentItem> contentDatas) {
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
            contentView = LayoutInflater.from(mContext).inflate(R.layout.announcement_item, null);

            viewHolter.title = (TextView) contentView.findViewById(R.id.announcement_title);
            viewHolter.sum = (TextView) contentView.findViewById(R.id.announcement_sum);
            viewHolter.time = (TextView) contentView.findViewById(R.id.announcement_time);
            viewHolter.line = contentView.findViewById(R.id.anount_line);
            contentView.setTag(viewHolter);
        } else {
            viewHolter = (ViewHoler) contentView.getTag();
        }
        if (contentDatas.size() > 0) {
            ContentItem data = contentDatas.get(position);
            if (data != null) {
                viewHolter.title.setText(data.getTitle());
                viewHolter.sum.setText(data.getSummary());
                String dataTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getPublishTime()));
                viewHolter.time.setText(dataTime);

                if ((position + 1) == contentDatas.size())
                    viewHolter.line.setVisibility(View.GONE);
                else
                    viewHolter.line.setVisibility(View.VISIBLE);
            }
        }
        return contentView;
    }

    public class ViewHoler {
        RoundAngleImageView icon;
        TextView title;
        TextView sum;
        TextView time;
        View line;
    }


}
