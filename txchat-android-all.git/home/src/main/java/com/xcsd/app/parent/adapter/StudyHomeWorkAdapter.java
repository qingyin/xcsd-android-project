package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.app.parent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shan on 2016/4/8.
 * 刷新家庭作业列表
 */
public class StudyHomeWorkAdapter extends BaseAdapter {

    private Context mContext;
    private List<HomeWorkRecord> notices = new ArrayList<HomeWorkRecord>();

    public StudyHomeWorkAdapter(Context context, List<HomeWorkRecord> notices) {

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_study_homework, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvMustDoContent = (TextView) convertView.findViewById(R.id.tv_test_mustdo);
            viewHolder.tvSet = (TextView) convertView.findViewById(R.id.tv_home_set);
            viewHolder.tvStringTo = (TextView) convertView.findViewById(R.id.tv_home_string_to);
            viewHolder.tvStuName = (TextView) convertView.findViewById(R.id.tv_home_stu_name);
            viewHolder.tvTeachName = (TextView) convertView.findViewById(R.id.tv_home_teach_name);
            viewHolder.tvTimeDay = (TextView) convertView.findViewById(R.id.tv_home_time_day);
            viewHolder.tvTimeHour = (TextView) convertView.findViewById(R.id.tv_home_time_hour);
            viewHolder.ivUndone = (ImageView) convertView.findViewById(R.id.im_home_homework_undone);
            viewHolder.ivDone = (ImageView) convertView.findViewById(R.id.im_home_homework_done);
            viewHolder.btDelete = (Button) convertView.findViewById(R.id.bt_study_homework_delete);
            viewHolder.line = convertView.findViewById(R.id.item_line_two);
            viewHolder.ivReadOrNO = (ImageView) convertView.findViewById(R.id.iv_home_homework_read);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (notices.size() > 0) {
            HomeWorkRecord data = notices.get(position);
            if (data != null) {
                //  viewHolder.tvName.setText(data.getSenderName());
                viewHolder.tvTeachName.setText(data.getSenderName());


                // String dateTimeDay = new SimpleDateFormat("HH:mm").format(new Date(data.getSendTime()));"yyyy-MM-dd  HH:mm"
//                String dateTimeDay = new SimpleDateFormat("yyyy-MM-dd  ").format(new Date(data.getSendTime()));
//                String dateTimeHour = new SimpleDateFormat("HH:mm").format(new Date(data.getSendTime()));
//                viewHolder.tvTimeDay.setText(dateTimeDay);
               viewHolder.tvTimeDay.setText(Utils.getStudyDateTime(mContext, data.getSendTime()));


                viewHolder.tvStuName.setText(data.getTargetName());
                if (!data.getHasRead()) {
                    viewHolder.ivReadOrNO.setVisibility(View.VISIBLE);

                } else {
                    viewHolder.ivReadOrNO.setVisibility(View.GONE);
                }
                if (data.getStatus() == Constants.HOMEWORK_STATUS.FINISHED) {
                    viewHolder.ivDone.setVisibility(View.VISIBLE);
                    viewHolder.ivUndone.setVisibility(View.GONE);
                    //   viewHolder.ivDone.setImageResource(R.drawable.learnwork_page_havetodo);


                } else {

                    viewHolder.ivDone.setVisibility(View.GONE);
                    viewHolder.ivUndone.setVisibility(View.VISIBLE);

                }

                if ((position + 1) == notices.size())
                    viewHolder.line.setVisibility(View.VISIBLE);
                else
                    viewHolder.line.setVisibility(View.GONE);

            }

        }

        return convertView;
    }

    class ViewHolder {

        View line;
        public TextView tvTeachName;
        public TextView tvSet;
        public TextView tvStringTo;
        public TextView tvStuName;
        public TextView tvMustDoContent;
        public TextView tvTimeDay;
        public TextView tvTimeHour;
        public ImageView ivUndone;
        public ImageView ivDone;
        public Button btDelete;
        public ImageView ivReadOrNO;

    }
}
