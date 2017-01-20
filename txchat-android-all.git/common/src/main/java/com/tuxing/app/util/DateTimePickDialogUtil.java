package com.tuxing.app.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.sdk.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 *
 * @author
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 * dateTimePicKDialog=new
 * DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 * dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * <p/>
 * } });
 */
public class DateTimePickDialogUtil implements OnDateChangedListener {
    private Button btOk;
    private Button btCancle;
    private DatePicker datePicker;
    private String initDateTime;
    private Context mContext;
    private TextView textTime;
    private long longTime;
    private long curretTime;
    private boolean isBirth;
    private SelectListener select;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");

    /**
     * 日期时间弹出选择框构造函数
     *
     * @param activity     ：调用的父activity
     * @param initDateTime 初始日期时间值，作为弹出窗口的标题和日期时间初始值
     */
    public DateTimePickDialogUtil(Context mContext, String initDateTime, TextView textTime, boolean isBirth, SelectListener select) {
        try {
            this.mContext = mContext;
            this.initDateTime = initDateTime;
            this.textTime = textTime;
            this.isBirth = isBirth;
            this.select = select;
            this.curretTime = sdf.parse(sdf.format(new Date(System.currentTimeMillis()))).getTime();
            this.longTime = this.curretTime;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void init(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        try {

            if (!(null == initDateTime || "".equals(initDateTime))) {
                calendar = this.getCalendarByInintData(initDateTime);
            } else {
                if (isBirth) {
                    long currentYear = sdf1.parse(sdf.format(new Date(System.currentTimeMillis()))).getTime();
//			long threeYear = currentYear - 94694400000l;
//                    long threeYear = currentYear - 578254000000l;
                    long threeYear = currentYear;
                    initDateTime = sdf1.format(threeYear) + "-01-01";
                    calendar = this.getCalendarByInintData(initDateTime);
                } else {
                    initDateTime = calendar.get(Calendar.YEAR) + "-"
                            + calendar.get(Calendar.MONTH) + "-"
                            + calendar.get(Calendar.DAY_OF_MONTH);

                }
            }
            datePicker.init(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出日期时间选择框方法
     *
     * @param inputDate :为需要设置的日期时间文本编辑框
     * @return
     */
    public void dateTimePicKDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_date_layout, null);
        datePicker = (DatePicker) view.findViewById(R.id.datepicker);
        init(datePicker);
        final Dialog dialog = new Dialog(mContext, R.style.dialog_common);
        btOk = (Button) view.findViewById(R.id.time_bt_ok);
        btCancle = (Button) view.findViewById(R.id.time_bt_cancle);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            btOk.setBackgroundResource(R.drawable.btn_bg_selector_p);
            btCancle.setBackgroundResource(R.drawable.btn_bg_selector_p);
        } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {


        }

        btOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				long value = 332640000000l;
                long value = 578254000000l;//18年
                long values = 321252000000l;//10年
                if (isBirth) {
                    if (curretTime >= longTime && (curretTime - longTime) < value) {
                        textTime.setText(initDateTime);
                        if (select != null) {
                            select.updateInfo();
                        }
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "生日只能选择当前日期之前18年内的日期", 0).show();
                    }
                } else {
                    if (longTime > curretTime) {
                        textTime.setText(initDateTime);
                        dialog.dismiss();
                    } else if (longTime == curretTime) {
                        textTime.setText(sdf.format(new Date(System.currentTimeMillis())));
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "不能选择当前之前的日期", 0).show();
                    }
                }
            }
        });

        btCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // 获得日历实例
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

            initDateTime = sdf.format(calendar.getTime());
            longTime = sdf.parse(initDateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//		textTime.setText(initDateTime);
    }

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime 初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();
        String[] split = initDateTime.split("-");
        int currentYear = Integer.valueOf(split[0]).intValue();
        int currentMonth = Integer.valueOf(split[1]).intValue() - 1;
        int currentDay = Integer.valueOf(split[2]).intValue();
        calendar.set(currentYear, currentMonth, currentDay);
        return calendar;
    }

    /**
     * 截取子串
     *
     * @param srcStr      源串
     * @param pattern     匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }

    public interface SelectListener {
        void updateInfo();
    }


}
