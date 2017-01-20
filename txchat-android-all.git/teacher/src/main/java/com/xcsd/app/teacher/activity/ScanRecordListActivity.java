package com.xcsd.app.teacher.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.AttendanceAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.db.entity.AttendanceRecord;
import com.tuxing.sdk.event.UploadCheckInEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import me.maxwin.view.XListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangst on 15-10-23.
 */
public class ScanRecordListActivity extends BaseActivity implements XListView.IXListViewListener {

    private SwipeListView swipeListView;
    private AttendanceAdapter adapter;
    private List<AttendanceRecord> records;
    private String TAG = ScanRecordListActivity.class.getSimpleName();
    private RelativeLayout card_bg;
    private View headerView;
    private Map<Long, AttendanceRecord> recordMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.home_card_layout);
        headerView = getLayoutInflater().inflate(R.layout.listview_attendance_item_layout, null);
        init();
    }
    private void init() {
        setTitle("幼儿扫描记录");
        setLeftBack("", true, false);
        setRightNext(false, "", R.drawable.ic_circle_more);
        records = new ArrayList<AttendanceRecord>();
        recordMap = new ConcurrentHashMap<>();
        swipeListView = (SwipeListView) findViewById(R.id.home_card_list);
        card_bg = (RelativeLayout) findViewById(R.id.card_bg);
        swipeListView.setXListViewListener(this);
        onRefresh();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    /**
     * 下拉刷新服务器数据
     */
    @Override
    public void onRefresh() {
        //swipeListView.startRefresh();

        List<AttendanceRecord> recordList = getService().getAttendanceManager().getRecordList(Integer.MAX_VALUE);
        records.clear();
        recordMap.clear();
        loadData(recordList);

        swipeListView.stopRefresh();
    }
    @Override
    public void onLoadMore() {
        if (records.size() > 0) {
            long lastId = records.get(records.size() - 1).getId();

            List<AttendanceRecord> recordList = getService().getAttendanceManager().getRecordList(lastId);

            loadData(recordList);
        }

        swipeListView.stopLoadMore();
    }

    private void loadData(List<AttendanceRecord> recordList){
        if(recordList == null || recordList.size() <= Constants.DEFAULT_LIST_COUNT){
            swipeListView.setPullLoadEnable(false);
        }else{
            recordList.remove(Constants.DEFAULT_LIST_COUNT);
            swipeListView.setPullLoadEnable(true);
        }

        if(!CollectionUtils.isEmpty(recordList)){
            for(AttendanceRecord record :recordList){
                records.add(record);
                recordMap.put(record.getId(), record);
            }
        }

        updateAdapter();
    }

    public void updateAdapter() {
        if (records != null && records.size() > 0) {
            card_bg.setVisibility(View.GONE);
        } else {
            card_bg.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            swipeListView.addHeaderView(headerView);
            adapter = new AttendanceAdapter(mContext, records);
            swipeListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onclickRightImg() {
        showBtnDialog(new String[]{getString(R.string.btn_retry), getString(R.string.btn_clear), getString(R.string.btn_cancel)});
    }

    @Override
    public void onclickBtn1() {
        super.onclickBtn1();
        if (records != null && records.size() > 0) {
            showProgressDialog(mContext, "上传中...", true, null);
            getService().getAttendanceManager().submitFailedRecord();
            onRefresh();
            disProgressDialog();
        }
    }

    @Override
    public void onclickBtn2() {
        super.onclickBtn2();
        // TODO 清空
        if (records != null && records.size() > 0) {
            showProgressDialog(mContext, "清除中...", true, null);
            getService().getAttendanceManager().clearSuccessRecord();
            onRefresh();
            disProgressDialog();
        }
    }

    public void onEventMainThread(UploadCheckInEvent event){
        AttendanceRecord record = event.getRecord();
        if(record != null && recordMap.containsKey(record.getId())){
            recordMap.get(record.getId()).setStatus(record.getStatus());
            if(adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
