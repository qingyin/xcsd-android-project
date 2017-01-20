package com.xcsd.app.parent.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.event.HomeworkEvent;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.HomeWorkDetailsAdapter;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class WorkDoShowActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String LOG_TAG = WorkDoShowActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private TextView tv_score_number;
    private TextView tvTitle;
    private TextView tv_right_tips;
    private Button tv_right;
    private SwipeListView xListView;
    private HomeWorkDetailsAdapter adapter;
    private long gardenId;
    private long MemberId;
    private long classid;
    private int status;
    private int choice_position;
    boolean hasMore = false;
    int type = 0;
    private RelativeLayout activity_bg;
    private List<HomeWorkDetail> contentDatas;
    private List<GameLevel> gameLevels;
    ArrayList<String> list;
    public static WorkDoShowActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_do_show);
        instance = this;
        init();
        getData();
    }

    private void init() {
//        setTitle("学能作业");
//        setLeftBack("", true, false);
//        setRightNext(true, "布置",0);
        gameLevels = new ArrayList<GameLevel>();
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tv_right_tips = (TextView) findViewById(R.id.tv_right_tips);
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        tv_score_number = (TextView) findViewById(R.id.tv_score_number);
        xListView = (SwipeListView) findViewById(R.id.home_work_list_dateils);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);
//        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        updateAdapter();
    }


    @Override
    protected void onResume() {
        isActivity = true;
//        initData();
        super.onResume();
    }

    public void initData() {
//        获取服务器数据
//        xListView.startRefresh();
        MemberId = getIntent().getLongExtra("MemberId", 0);
//        classid = getIntent().getLongExtra("classid", 0);
        getService().getHomeWorkManager().getHomeWorkDateils(MemberId);
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            List<GameLevel> tempDatas = new ArrayList<GameLevel>();
            switch (event.getEvent()) {
                case HOMEWORK_DATEILS_LIST_HISTORY_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas=event.getGlList();
                    String socre=event.getHwDetails().getTotalScore()+"分";
                    String MaxScore="作业满分为"+event.getHwDetails().getMaxScore()+"分";
                    tv_score_number.setText(socre);
                    tv_right_tips.setText(MaxScore);
                    getResresh(tempDatas);
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);
//                    getService().getHomeWorkManager().getHistoryHomeWorkList();
                    break;
                case HOMEWORK_DATEILS_LIST_HISTORY_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    xListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;

            }
        }
    }

    @Override
    public void onRefresh() {
//        getService().getHomeWorkManager().getHomeWorkSendListLatest();
//        showProgressDialog(this,"",true,null);
    }

    private void getResresh(List<GameLevel> refreshlist) {
        // TODO 加载数据
        if (refreshlist != null && refreshlist.size() > 0) {
            gameLevels.clear();
            gameLevels.addAll(0, refreshlist);
        }
        updateAdapter();
        xListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
//        if (contentDatas.size() > 0) {
//            long lastId = contentDatas.get(contentDatas.size() - 1).getHomeworkId();
////            加载更多，提交参数不明
//            getService().getHomeWorkManager().getHomeWorkSendListHistory(lastId);
//        } else {
//            xListView.stopLoadMore();
//        }
        xListView.stopLoadMore();
    }

    public void getLoadMore(List<HomeWorkDetail> list) {
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
        if (gameLevels != null && gameLevels.size() > 0) {
            activity_bg.setVisibility(View.GONE);
        } else {
            activity_bg.setVisibility(View.GONE);
        }
        if (adapter == null) {
            adapter = new HomeWorkDetailsAdapter(this, gameLevels,true);
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
        }
    }


    @Override
    public void getData() {
        if (user != null)
            gardenId = user.getGardenId();
        initData();
    }

    public void setType(boolean b) {
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnTitleLeft:
                this.finish();
                break;
        }
    }

}
