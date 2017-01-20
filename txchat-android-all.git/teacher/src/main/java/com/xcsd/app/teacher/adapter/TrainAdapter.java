package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuxing.app.util.Utils;
import com.xcsd.app.teacher.R;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 学能作业列表适配器
 * Created by bryant on 16/4/6.
 */
public class TrainAdapter extends BaseAdapter {

    private static final String TAG = "TrainAdapter";
    private LayoutInflater inflater;
    public Context mContext;
    public List<HomeWorkClass> contentDatas;

    public TrainAdapter(Context mContext, List<HomeWorkClass> contentDatas) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_train_item, parent, false);
        }
        holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.sorrt_item_name);
            holder.work = (TextView) convertView.findViewById(R.id.sorrt_item_work);
            holder.time = (TextView) convertView.findViewById(R.id.sorrt_item_time);
            holder.done = (TextView) convertView.findViewById(R.id.sorrt_item_done);
            holder.dones = (TextView) convertView.findViewById(R.id.sorrt_item_dones);
            holder.avatar = (RoundImageView) convertView.findViewById(R.id.sorrt_icon);

            convertView.setTag(holder);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        }

        if (contentDatas.size() > 0) {
            HomeWorkClass data = contentDatas.get(position);
            if (data != null) {
//                holder.name.setText(data.getClassName());
                if (data.getType() == Constants.HOMEWORK_TYPE.CUSTOMIZED) {
                    holder.work.setText("系统定制作业");
                    holder.avatar.setImageResource(R.drawable.icon_message_braintester);
                } else {
                    holder.work.setText("教师自主作业");
                    holder.avatar.setImageResource(R.drawable.icon_message_mentalwork);
                }
                holder.name.setText(data.getClassName());
                String datatime = data.getSendTime() + "";
                if (datatime != null && datatime != "") {
//                    String Time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getSendTime()));
                    String Time = Utils.getDateTime(mContext, data.getSendTime());
                    holder.time.setText(Time);
                }
                String dones = data.getFinishedCount() + "";
                String done = "/" + data.getTotalCount();
                holder.done.setText(done);
                holder.dones.setText(dones);

//                holder.avatar.setImageUrl("", com.tuxing.app.R.drawable.default_image);

            }
        }

//        holder.name.setText(list.get(position).);
//        数据及联网逻辑未完成,数据不对
        return convertView;
    }

    private static class ViewHolder {

        /**
         * 班级名称，作业类型，时间，完成度，完成人数
         */
        TextView name, work, time, done, dones;

        /**
         * 作业类型图片
         */
        RoundImageView avatar;

    }
}

