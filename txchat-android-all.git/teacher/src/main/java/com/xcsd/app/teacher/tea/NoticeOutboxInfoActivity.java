package com.xcsd.app.teacher.tea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.MyListView;
import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.event.NoticeReceiverEvent;
import com.tuxing.sdk.modle.NoticeDepartmentReceiver;
import com.tuxing.sdk.modle.NoticeReceiver;
import com.tuxing.sdk.utils.CollectionUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class NoticeOutboxInfoActivity extends BaseActivity implements OnItemClickListener {
    private ArrayList<String> iconList;
    private List<NoticeDepartmentReceiver> classList;
    private IconAdapter iconAdapter;
    private NoticeOutboxInfoClassAdapter classAdapter;
    private MyGridView iconView;
    private MyListView classView;
    private List<? extends NoticeReceiver> noticeReceivers;
    private TextView content;
    private String TAG = NoticeOutboxInfoActivity.class.getSimpleName();
    private Notice notice;
    private TextView sendTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.tea_send_notice_info_layout);
        init();
        initData();
    }

    private void init() {

        setTitle(getString(R.string.notice_send));
        setLeftBack("", true, false);
        setRightNext(true, getString(R.string.btn_frash), 0);
        notice = (Notice) getIntent().getSerializableExtra("notice");
        content = (TextView) findViewById(R.id.send_notice_info_content);
        sendTime = (TextView)findViewById(R.id.send_notice_info_time);
        iconView = (MyGridView) findViewById(R.id.send_notice_info_grid);
        classView = (MyListView) findViewById(R.id.send_notice_info_list);
        iconList = new ArrayList<String>();
        classList = new ArrayList<NoticeDepartmentReceiver>();
        iconView.setOnItemClickListener(this);
        classView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(mContext, NoticeStatusActivity.class);
                intent.putExtra("departmentId", classList.get(position).getDepartmentId());
                intent.putExtra("departmentName", classList.get(position).getDepartmentName());
                intent.putExtra("noticeId", notice.getNoticeId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getService().getNoticeManager().getNoticeReceiverSummary(notice.getNoticeId());
    }

    private void initData() {
        //TODO 获取通知详情
        showProgressDialog(mContext, "", true, null);
        showData(notice);
    }
    public void showData(Notice mNotice) {
        try {
            String json = mNotice.getAttachments();
            JSONArray array = new JSONArray(json);
            if (array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    iconList.add(array.getJSONObject(i).getString("url"));
                }
                updateIconAdapter();
            }
            if (notice.getSendTime() != null) {
                sendTime.setText(Utils.getDateTime(mContext, notice.getSendTime()));
            }

            content.setText(mNotice.getContent());
        } catch (Exception e) {
            content.setText(mNotice.getContent());
            e.printStackTrace();
        }
    }
    /**
     * 服务器通知状态返回
     *
     * @param event
     */
    public void onEventMainThread(NoticeReceiverEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case NOTICE_RECEIVER_GET_SUMMARY_SUCCESS:
                    List<NoticeDepartmentReceiver> receivers = (List<NoticeDepartmentReceiver>) event.getReceivers();
                    if (!CollectionUtils.isEmpty(receivers)) {
                        classList.clear();
                        classList.addAll(receivers);
                        showAndSaveLog(TAG, "获取接收通知的班级成功  size = " + receivers.size(), false);
                    }
                    updateClassAdapter();
                    showAndSaveLog(TAG, "获取接收通知的班级为空", false);
                    break;
                case NOTICE_RECEIVER_FAILED:
                    showAndSaveLog(TAG, "获取接收通知的班级失败 --" + event.getMsg(), false);
                    disProgressDialog();
                    break;
            }
        }
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        showProgressDialog(mContext, "", true, null);
        getService().getNoticeManager().getNoticeReceiverSummary(notice.getNoticeId());
    }

    
    public void updateIconAdapter() {
        if (iconAdapter == null) {
            iconAdapter = new IconAdapter(mContext, iconList);
            iconView.setAdapter(iconAdapter);
        } else {
            iconAdapter.notifyDataSetChanged();
        }
    }

    public void updateClassAdapter() {
        if (classAdapter == null) {
            classAdapter = new NoticeOutboxInfoClassAdapter(mContext, classList);
            classView.setAdapter(classAdapter);
        } else {
            classAdapter.notifyDataSetChanged();
        }
    }


    public class IconAdapter extends BaseAdapter {
        private Context context;
        private List<String> list;

        public IconAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.grideview_item1_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = (MyImageView) convertView
                        .findViewById(R.id.grid_item1_icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.icon.setImageUrl(iconList.get(position) + SysConstants.Imgurlsuffix90, R.drawable.defal_down_proress,false);
            return convertView;
        }

    }

    public class ViewHolder {
        MyImageView icon;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO
        NewPicActivity.invoke(NoticeOutboxInfoActivity.this, iconList.get(position), true, iconList, false);
    }

}
