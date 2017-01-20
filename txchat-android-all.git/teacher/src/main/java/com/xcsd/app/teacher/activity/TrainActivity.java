package com.xcsd.app.teacher.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.activity.GameWebSubUrlActivity;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.adapter.HomeActicityAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.app.teacher.adapter.TrainAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.db.entity.ClassHomeworkInfo;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.event.HomeworkEvent;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;


public class TrainActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String LOG_TAG = TrainActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private TextView tv_right;
    private TextView tvTitle;
    private SwipeListView xListView;
    private TrainAdapter adapter;
    private long gardenId;
    private long workId;
    private boolean hasMore = false;
    private RelativeLayout activity_bg;
    private List<HomeWorkClass> contentDatas;
    ArrayList<String> list;
    public static TrainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        instance = this;

        init();
        getData();

//        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "homework");
    }

    private void init() {
//        setTitle("学能作业");
//        setLeftBack("", true, false);
//        setRightNext(true, "布置",0);
        TAG = TrainActivity.class.getSimpleName();
        contentDatas = new ArrayList<HomeWorkClass>();
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        tv_right = (TextView) findViewById(R.id.tv_right);
        xListView = (SwipeListView) findViewById(R.id.home_acticity_list);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        tv_right.setOnClickListener(this);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        String titlename="";
        titlename = getIntent().getStringExtra("name");
        if (!titlename.equals("")){
            tvTitle.setText(titlename);
        }
        updateAdapter();


    }


    @Override
    protected void onResume() {
        isActivity = true;
        initData();
        super.onResume();
    }

    public void initData() {
//        获取服务器数据
//        xListView.startRefresh();
        getService().getHomeWorkManager().getHomeWorkSendListFromLocal();
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            List<HomeWorkClass> tempDatas = new ArrayList<HomeWorkClass>();
            switch (event.getEvent()) {
                case HOMEWORK_SENT_LIST_FROM_LOCAL:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkClassList();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        contentDatas.addAll(tempDatas);
                        updateAdapter();
                    }
                    getService().getHomeWorkManager().getHomeWorkSendListLatest();
                    showAndSaveLog(TAG, "获取本地活动数据成功 size = " + contentDatas.size(),
                            false);
                    break;
                case HOMEWORK_SENT_LIST_LATEST_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkClassList();
                    getResresh(tempDatas);
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);

//                    getService().getHomeWorkManager().getHistoryHomeWorkList();
                    break;
                case HOMEWORK_SENT_LIST_LATEST_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    xListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;
                case HOMEWORK_SENT_LIST_HISTORY_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkClassList();
                    getLoadMore(tempDatas);
                    showAndSaveLog(TAG, "获取历史活动成功 size = " + contentDatas.size(),
                            false);
                    break;
                case HOMEWORK_SENT_LIST_HISTORY_FAILED:
                    xListView.stopLoadMore();
                    updateAdapter();
                    showAndSaveLog(TAG, "获取历史活动失败" + event.getMsg(), false);
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        getService().getHomeWorkManager().getHomeWorkSendListLatest();
        showProgressDialog(this,"",true,null);
    }

    private void getResresh(List<HomeWorkClass> refreshList) {
        // TODO 加载数据
        if (refreshList != null && refreshList.size() > 0) {
            contentDatas.clear();
            contentDatas.addAll(0, refreshList);
        }
        updateAdapter();
        xListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        if (contentDatas.size() > 0) {
            long lastId = contentDatas.get(contentDatas.size() - 1).getHomeworkId();
//            加载更多，提交参数不明
            getService().getHomeWorkManager().getHomeWorkSendListHistory(lastId);
        } else {
            xListView.stopLoadMore();
        }
        xListView.stopLoadMore();
    }

    public void getLoadMore(List<HomeWorkClass> list) {
        if (list != null && list.size() > 0) {
            contentDatas.addAll(list);
        }
        xListView.stopLoadMore();
        updateAdapter();
    }

    public void showFooterView() {
        if (hasMore)
            xListView.setPullLoadEnable(true);
        else
            xListView.setPullLoadEnable(false);
    }

    public void updateAdapter() {
        if (contentDatas != null && contentDatas.size() > 0) {
            activity_bg.setVisibility(View.GONE);
        } else {
            activity_bg.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            adapter = new TrainAdapter(this, contentDatas);
            xListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;
        if (position >= 0) {
            HomeWorkClass infoitem = contentDatas.get(position);
            workId = infoitem.getHomeworkId();
            Intent intent = new Intent(this, WorkScoreActivity.class).putExtra("homework_id", workId);
            startActivity(intent);
        }
    }


    @Override
    public void getData() {
        if (user != null)
            gardenId = user.getGardenId();
        initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnTitleLeft:
                this.finish();
                break;
            case R.id.tv_right:
                Intent intent = new Intent(this, SetClassWorkActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "homework");
        super.finish();
    }
}
