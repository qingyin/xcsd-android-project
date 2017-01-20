package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tuxing.app.activity.NewPicActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.AttendanceRecord;
import com.tuxing.sdk.db.entity.CheckInRecord;
import com.tuxing.sdk.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 刷卡列表
 */
public class AttendanceAdapter extends ArrayAdapter<AttendanceRecord> {

    private Context context;
    private List<AttendanceRecord> list;
    private String TAG = AttendanceAdapter.class.getSimpleName();

    public AttendanceAdapter(Context context, List<AttendanceRecord> msgs) {
        super(context, 0, msgs);
        this.context = context;
        this.list = msgs;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_attendance_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.cardName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.cardTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.cardStatus = (TextView) convertView.findViewById(R.id.tv_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AttendanceRecord info = list.get(position);
        if(info!=null){
            viewHolder.cardName.setText(info.getUserName());
            String state = "";
            switch (info.getStatus()){
                case Constants.CHECK_IN_STATE.SAVED:
                    state = "正在上传";
                    viewHolder.cardStatus.setTextColor(context.getResources().getColor(R.color.skin_text1));
                    break;
                case Constants.CHECK_IN_STATE.UPLOADED:
                    state = "已上传";
                    viewHolder.cardStatus.setTextColor(context.getResources().getColor(R.color.btn_blue_normal));
                    break;
                case Constants.CHECK_IN_STATE.ERROR:
                    state = "上传失败";
                    viewHolder.cardStatus.setTextColor(context.getResources().getColor(R.color.tv_red));
                    break;
            }
            viewHolder.cardStatus.setText(state + "");
            if(info.getCheckInTime()!=null&&info.getCheckInTime()!=0l){
                String timeStame = new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(info.getCheckInTime()));
                viewHolder.cardTime.setText(timeStame + "");
            }
        }
        return convertView;
    }

    public class ViewHolder {
        public TextView cardName;
        public TextView cardTime;
        public TextView cardStatus;
    }
}
