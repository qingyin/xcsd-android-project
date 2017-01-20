package com.tuxing.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.utils.Constants;

import java.util.List;

/**
 * 权威解释适配器
 * Created by bryant on 16/4/6.
 */
public class ExplainAdapter extends BaseAdapter {

    private static final String TAG = "TrainAdapter";
    private LayoutInflater inflater;
    public Context mContext;
    public List<HomeWorkClass> contentDatas;

    public ExplainAdapter(Context mContext, List<HomeWorkClass> contentDatas) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_explain_item, parent, false);
        }
        holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.sorrt_item_name);
            holder.avatar = (RoundImageView) convertView.findViewById(R.id.sorrt_icon);

            convertView.setTag(holder);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        }

        if (contentDatas.size() > 0) {
            HomeWorkClass data = contentDatas.get(position);
            if (data != null) {

                holder.avatar.setImageUrl("", com.tuxing.app.R.drawable.default_image);

            }
        }

//        holder.name.setText(list.get(position).);
//        数据及联网逻辑未完成,数据不对
        return convertView;
    }

    private static class ViewHolder {

        /**
         * 名称
         */
        TextView name;

        /**
         * 图片
         */
        RoundImageView avatar;

    }
}

