package com.xcsd.app.teacher.tea;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.event.NoticeReceiverEvent;
import com.tuxing.sdk.modle.NoticeUserReceiver;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


public class NoticeStatusActivity extends BaseActivity {
    private List<String> iconList = new ArrayList<String>();
    private UnReadAdapter unReadAdapter;
    private ReadAdapter readAdapter;
    private MyGridView unReadView;
    private MyGridView readView;
    private List<NoticeUserReceiver> readList;
    private List<NoticeUserReceiver> unReadList;
    private long noticeId;
    private long departmentId;
    private String TAG = NoticeStatusActivity.class.getSimpleName();
    private TextView unreadNum;
    private TextView readNum;
    private List<NoticeUserReceiver> receivers;
    private String deparementName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.tea_notice_status_layout);
        init();
        initData();
    }

    private void initData() {
        showProgressDialog(mContext, "", true, null);
        //TODO 获取通知详情
        getService().getNoticeManager().getNoticeReceiverDetail(noticeId, departmentId);
    }

    private void init() {
        setLeftBack("取消", false, false);
        setRightNext(true, getString(R.string.btn_frash), 0);
        deparementName = getIntent().getStringExtra("departmentName");
        setTitle(deparementName);
        noticeId = getIntent().getLongExtra("noticeId", 0);
        departmentId = getIntent().getLongExtra("departmentId", 0);
        unreadNum = (TextView) findViewById(R.id.unread_number);
        readNum = (TextView) findViewById(R.id.read_number);

        unReadView = (MyGridView) findViewById(R.id.unread_grid);
        readView = (MyGridView) findViewById(R.id.read_grid);
        readList = new ArrayList<NoticeUserReceiver>();
        unReadList = new ArrayList<NoticeUserReceiver>();
    }


    public void onEventMainThread(NoticeReceiverEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case NOTICE_RECEIVER_GET_DETAIL_SUCCESS:
                    receivers = (List<NoticeUserReceiver>) event.getReceivers();
                    if (!CollectionUtils.isEmpty(receivers)) {
                        readList.clear();
                        unReadList.clear();
                        reanAndUnreanNum(receivers);
                    }
                    showAndSaveLog(TAG, "获取收件人的状态成功", false);
                    break;
                case NOTICE_RECEIVER_FAILED:
                    showAndSaveLog(TAG, "获取收件人的状态失败 --" + event.getMsg(), false);
                    break;
            }
        }
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        initData();
    }

    public void reanAndUnreanNum(List<NoticeUserReceiver> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isRead()) {
                readList.add(list.get(i));
            } else {
                unReadList.add(list.get(i));
            }
        }
        readNum.setText(String.valueOf(readList.size()));
        unreadNum.setText(String.valueOf(unReadList.size()));
        updateunReadAdapter();
        updateReadAdapter();
    }


    public void updateunReadAdapter() {
        if (unReadAdapter == null) {
            unReadAdapter = new UnReadAdapter(mContext, unReadList);
            unReadView.setAdapter(unReadAdapter);
        } else {
            unReadAdapter.notifyDataSetChanged();
        }
    }

    public void updateReadAdapter() {
        if (readAdapter == null) {
            readAdapter = new ReadAdapter(mContext, readList);
            readView.setAdapter(readAdapter);
        } else {
            readAdapter.notifyDataSetChanged();
        }
    }


    public class UnReadAdapter extends BaseAdapter {
        private Context context;
        private List<NoticeUserReceiver> list;

        public UnReadAdapter(Context context, List<NoticeUserReceiver> list) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.grideview_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.unReadIcon = (RoundImageView) convertView.findViewById(R.id.grid_item_icon);
                viewHolder.unReadName = (TextView) convertView.findViewById(R.id.grid_item_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.unReadIcon.setImageUrl(list.get(position).getAvatar()+ SysConstants.Imgurlsuffix90, R.drawable.default_avatar);
            viewHolder.unReadName.setText(list.get(position).getUserName());
            return convertView;
        }

    }

    public class ReadAdapter extends BaseAdapter {
        private Context context;
        private List<NoticeUserReceiver> list;

        public ReadAdapter(Context context, List<NoticeUserReceiver> list) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.grideview_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.readIcon = (RoundImageView) convertView.findViewById(R.id.grid_item_icon);
                viewHolder.readName = (TextView) convertView.findViewById(R.id.grid_item_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.readIcon.setImageUrl(list.get(position).getAvatar()+ SysConstants.Imgurlsuffix90, R.drawable.default_avatar);
            viewHolder.readName.setText(list.get(position).getUserName());
            return convertView;
        }

    }

    public class ViewHolder {
        RoundImageView unReadIcon;
        RoundImageView readIcon;
        TextView unReadName;
        TextView readName;
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
        isActivity = false;
    }
}
