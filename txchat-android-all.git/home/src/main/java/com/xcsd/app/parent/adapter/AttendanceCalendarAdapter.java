package com.xcsd.app.parent.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcsd.app.parent.R;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shan on 2016/4/11.
 * <p>
 * 日历gridview中的每一个item显示的textview
 *
 * @author Vincent Lee
 */
public class AttendanceCalendarAdapter extends BaseAdapter {


    private Context context;
    private List<String> dates;// 一个gridview中的日期存入此数组中

    private Calendar calendar = Calendar.getInstance();

    private Set<String> presentDate;
    private Set<String> absentDate;
    private Set<String> leaveDate;
    private Set<String> holidayDate;

    private String todayOfMonth = "-";

    public AttendanceCalendarAdapter(Context context) {
        this.context = context;
        dates = new ArrayList<>(42);
        presentDate = new HashSet<>();
        absentDate = new HashSet<>();
        leaveDate = new HashSet<>();
        holidayDate = new HashSet<>();

    }

    public void setDates(int year, int month, List<Integer> present, List<Integer> absent, List<Integer> leave, List<Integer> holiday) {
        calendar.set(year, month, 1);

        dates.clear();
        presentDate.clear();
        absentDate.clear();
        leaveDate.clear();
        holidayDate.clear();

        int daysOfMonth = calendar.getActualMaximum(Calendar.DATE);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.setTime(new Date());

        if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month) {
            todayOfMonth = String.valueOf(calendar.get(Calendar.DATE));
        } else {
            todayOfMonth = "-";
        }

        for (int i = 0; i < firstDayOfWeek - 1; i++) {
            dates.add("");
        }

        for (int i = 1; i <= daysOfMonth; i++) {
            dates.add(String.valueOf(i));
        }

        if (!CollectionUtils.isEmpty(present)) {
            for (Integer date : present) {
                presentDate.add(String.valueOf(date));
            }
        }

        if (!CollectionUtils.isEmpty(absent)) {
            for (Integer date : absent) {
                absentDate.add(String.valueOf(date));
            }
        }

        if (!CollectionUtils.isEmpty(leave)) {
            for (Integer date : leave) {
                leaveDate.add(String.valueOf(date));
            }
        }

        if (!CollectionUtils.isEmpty(holiday)) {
            for (Integer date : holiday) {
                holidayDate.add(String.valueOf(date));
            }
        }

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.attendance_calendar_item, null);
            holder = new ViewHolder();
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvtext);
            holder.tvToday = (TextView) convertView.findViewById(R.id.tv_today_label);
            Display defaultDisplay = ((Activity) context).getWindowManager().getDefaultDisplay();
            int width = defaultDisplay.getWidth();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.tvDate.getLayoutParams());
            params.height = width / 10;
            params.width = width / 10;
            params.setMargins(10, 10, 10, 10);
            holder.tvDate.setLayoutParams(params);
            holder.tvDate.setGravity(Gravity.CENTER);
            holder.tvDate.setTypeface(Typeface.DEFAULT_BOLD);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();


        }

        if (position >= 0 && position < dates.size()) {
            String date = dates.get(position);

            holder.tvDate.setText(date);

            if (date.equals(todayOfMonth)) {
                holder.tvToday.setVisibility(View.VISIBLE);
            } else {
                holder.tvToday.setVisibility(View.INVISIBLE);
            }

            if (presentDate.contains(date)) {
                holder.tvDate.setBackgroundResource(com.tuxing.app.R.drawable.bg_calendar_circular);
                holder.tvDate.setTextColor(Color.WHITE);
            } else if (absentDate.contains(date)) {
                holder.tvDate.setBackgroundResource(com.tuxing.app.R.drawable.bg_pink_circular);
                holder.tvDate.setTextColor(Color.WHITE);
            } else if (holidayDate.contains(date)) {
                holder.tvDate.setBackgroundResource(com.tuxing.app.R.drawable.bg_calendar_circular);
                holder.tvDate.setTextColor(context.getResources().getColor(com.tuxing.app.R.color.black_text));
            } else {
                holder.tvDate.setBackgroundColor(context.getResources().getColor(com.tuxing.app.R.color.transparent));
                holder.tvDate.setTextColor(context.getResources().getColor(com.tuxing.app.R.color.black));
            }
        }


        return convertView;
    }


    public class ViewHolder {
        TextView tvDate;
        TextView tvToday;
    }
}
