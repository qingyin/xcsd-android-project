package com.xcsd.app.parent.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.tuxing.app.base.BaseFragment;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.AttendanceCalendarAdapter;
import com.xcsd.app.parent.util.SysUtil;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.sdk.event.HomeworkEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

/**
 * Created by shan on 2016/4/11.
 * 学能作业考勤
 */

public class StudyHomeWorkCheckedFragment extends BaseFragment implements XListView.OnItemLongClickListener {
    View rootView;
    private boolean isActivity = false;
    private AttendanceCalendarAdapter adapter;
    private ViewFlipper flipper;
    private MyGridView gridView;
    private TextView presentDays;
    private ImageView iv_bg;
    private List<Integer> checkList;
    private List<Integer> finishList;
    private List<Integer> unfinishList;
    private boolean first = true;
    private TextView tvCheckMonth;
    SimpleDateFormat sdf = new SimpleDateFormat("MM月");
    private String monthAfter;
    private  String monthBefore;
    /**
     * 每次添加gridview到viewflipper中时给的标记
     */
    private int gvFlag = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.home_study_homework_frg_checked_layout, null);
            tvCheckMonth = (TextView) rootView.findViewById(R.id.tv_home_frg_check_month);
            monthBefore=sdf.format(new Date());
            monthAfter=transMonth(monthBefore);
            // intView();

        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        intView();
        if (first) {
            getService().getHomeWorkManager().HomeworkCalendarRequest(SysUtil.getUserId());
            showProgressDialog(getActivity(), "", true, null);
            first = false;
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置显示数据
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void intView() {
        tvCheckMonth.setText(monthAfter);
        //日历正文
        flipper = (ViewFlipper) rootView.findViewById(R.id.flipper);
        //ViewFlipper移除所有的view
        flipper.removeAllViews();
        // initData();


    }

    public String transMonth(String string) {
        if ("01月".equals(string)) {
            monthAfter = "一月份的学能作业";
        } else if ("02月".equals(string)) {
            monthAfter = "二月份的学能作业";

        } else if ("03月".equals(string)) {
            monthAfter = "三月份的学能作业";

        } else if ("04月".equals(string)) {
            monthAfter = "四月份的学能作业";

        } else if ("05月".equals(string)) {

            monthAfter = "五月份的学能作业";
        } else if ("06月".equals(string)) {

            monthAfter = "六月份的学能作业";
        } else if ("07月".equals(string)) {
            monthAfter = "七月份的学能作业";

        } else if ("08月".equals(string)) {
            monthAfter = "八月份的学能作业";

        } else if ("09月".equals(string)) {
            monthAfter = "九月份的学能作业";

        } else if ("10月".equals(string)) {

            monthAfter = "十月份的学能作业";
        } else if ("11月".equals(string)) {

            monthAfter = "十一月份的学能作业";
        } else if ("12月".equals(string)) {

            monthAfter = "十二月份的学能作业";
        }
        return monthAfter;
    }

    public void onEventMainThread(HomeworkEvent event) {
        finishList = new ArrayList<>();
        unfinishList = new ArrayList<>();
        switch (event.getEvent()) {

            case HOMEWORK_CALENDAR_SUCCESS:
                disProgressDialog();
                finishList = event.getFinishedList();
                unfinishList = event.getUnfinishedList();
                initGridView();
                break;
            case HOMEWORK_CALENDAR_FAILED:

                break;
        }


    }
//    private void initData() {
//        Bundle data = getArguments();
//        finishList = (List<Integer>) data.get("homecheckfinish");
//        unfinishList = (List<Integer>) data.get("homecheckunfinish");
//    }

    private void initGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
        // 取得屏幕的宽度和高度
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth();
        int Height = display.getHeight();

        gridView = new MyGridView(getActivity());
        gridView.setNumColumns(7);
//         gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setColumnWidth(Width / 8);
        gridView.setVerticalSpacing(Utils.dip2px(getActivity(), 0));
        gridView.setHorizontalSpacing(Utils.dip2px(getActivity(), 5));
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setLayoutParams(params);
        gridView.setVisibility(View.INVISIBLE);

        adapter = new AttendanceCalendarAdapter(getActivity());
        Calendar calendar = Calendar.getInstance();
//        Date date = new Date();
//        Long lon = date.getTime();
//        List<Long> listLeave = new ArrayList<>();
//        listLeave.add(lon);
        calendar.setTimeInMillis(System.currentTimeMillis());
//        adapter.setDates(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                null, finishList, null, unfinishList);
        adapter.setDates(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                finishList, unfinishList, null, null);
        gridView.setAdapter(adapter);
        flipper.addView(gridView, gvFlag);


    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }


}
