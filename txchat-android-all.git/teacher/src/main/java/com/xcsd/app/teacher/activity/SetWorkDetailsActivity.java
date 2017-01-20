package com.xcsd.app.teacher.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.activity.GameWebSubUrlActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.HomeworkEvent;
import com.xcsd.app.teacher.R;
import com.xcsd.app.teacher.adapter.HomeWorkDetailsAdapter;
import com.yixia.camera.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class SetWorkDetailsActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String LOG_TAG = SetWorkDetailsActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private Button bt_unite_work_submit;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tvTitle;
    private Button tv_right;
    private SwipeListView xListView;
    private HomeWorkDetailsAdapter adapter;
    private long gardenId;
    private long MemberId;
    private long classid;
    private int status;
    private boolean hasMore = false;
    private RelativeLayout activity_bg;
    private List<HomeWorkDetail> contentDatas;
    private List<GameLevel> gameLevels;
    ArrayList<String> list;
    public static SetWorkDetailsActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_details);
        instance = this;

        init();
        getData();
    }

    private void init() {
//        setTitle("学能作业");
//        setLeftBack("", true, false);
//        setRightNext(true, "布置",0);
        gameLevels = new ArrayList<GameLevel>();
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tv_right = (Button) findViewById(R.id.tv_right);
        xListView = (SwipeListView) findViewById(R.id.home_work_list_dateils);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);
//        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        tv_right.setOnClickListener(this);
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
        MemberId = getIntent().getLongExtra("MemberId", 0);
        classid = getIntent().getLongExtra("classid", 0);
        getService().getHomeWorkManager().generate_detail(MemberId, classid);
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            List<GameLevel> tempDatas = new ArrayList<GameLevel>();
            switch (event.getEvent()) {
                case HOMEWORK_GAME_LIST_SUCCESS:
                    tempDatas=event.getGlList();
                    getResresh(tempDatas);
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);
//                    getService().getHomeWorkManager().getHistoryHomeWorkList();
                    break;
                case HOMEWORK_GAME_LIST_FAILED:
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
            adapter = new HomeWorkDetailsAdapter(this, gameLevels,false,false);
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
//            HomeWorkDetail infoitem = contentDatas.get(position);
//            workId = infoitem.getHomeworkId();
//            Intent intent = new Intent(this, WorkScoreActivity.class).putExtra("homework_id", workId);
//            startActivity(intent);
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
//                Intent intent = new Intent(this, SetClassWorkActivity.class);
//                startActivity(intent);
                break;
            case R.id.bt_unite_work_submit:
//                Intent intent = new Intent(this, SetClassWorkActivity.class);
//                startActivity(intent);
                break;
        }
    }


}
