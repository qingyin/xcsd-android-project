package com.xcsd.app.teacher.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.adapter.HomeActicityAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.xcsd.app.teacher.adapter.TrainAdapter;
import com.xcsd.app.teacher.adapter.WorkSocreAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.db.entity.HomeWorkMember;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.utils.Constants;
import com.yixia.camera.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class WorkScoreActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener,View.OnClickListener {

    public  static final String LOG_TAG = WorkScoreActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private SwipeListView xListView;
    private WorkSocreAdapter adapter;
    private List<HomeWorkMember> contentDatas;
    private long gardenId;
    private boolean hasMore = false;
    private String TAG;
    private RelativeLayout activity_bg;
    long id=0;
//学能作业详情界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_score);

        initView();
        initData();
    }

    private void initView(){
        TAG = WorkScoreActivity.class.getSimpleName();
        btnTitleLeft =  (LinearLayout)findViewById(R.id.btnTitleLeft);
        xListView = (SwipeListView) findViewById(R.id.home_acticity_list);
        contentDatas = new ArrayList<HomeWorkMember>();
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        btnTitleLeft.setOnClickListener(this);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
        id = getIntent().getLongExtra("homework_id", 0);
        updateAdapter();
    }

    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
    }

    private void initData() {
        // TODO 获取服务器数据
//        xListView.startRefresh();
        getService().getHomeWorkManager().getHomeworkMemberListFromLacal(id);
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            xListView.stopRefresh();
            List<HomeWorkMember> tempDatas = new ArrayList<HomeWorkMember>();
            switch (event.getEvent()) {
                case HOMEWORK_MEMBERS_LIST_FROM_LOCAL:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkMemberList();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        contentDatas.addAll(tempDatas);
                        updateAdapter();
                    }
                    getService().getHomeWorkManager().getLatestHomeworkMemberList(id);
                    showAndSaveLog(TAG, "获取本地活动数据成功 size = " + contentDatas.size(),
                            false);
                    break;
                case HOMEWORK_MEMBERS_LIST_LATEST_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkMemberList();
                    getResresh(tempDatas);
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);
//                    getService().getCounterManager().resetCounter(
//                            Constants.COUNTER.ACTIVITY);
                    break;
                case HOMEWORK_MEMBERS_LIST_LATEST_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    xListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;
                case HOMEWORK_MEMBERS_LIST_HISTORY_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkMemberList();
                    getLoadMore(tempDatas);
                    showAndSaveLog(TAG, "获取历史活动成功 size = " + contentDatas.size(),
                            false);
                    break;
                case HOMEWORK_MEMBERS_LIST_HISTORY_FAILED:
                    xListView.stopLoadMore();
                    updateAdapter();
                    showAndSaveLog(TAG, "获取历史活动失败" + event.getMsg(), false);
                    break;
            }
        }
    }


    @Override
    public void onRefresh() {
        getService().getHomeWorkManager().getLatestHomeworkMemberList(id);
        showProgressDialog(this,"",true,null);
    }

    private void getResresh(List<HomeWorkMember> refreshList) {
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
//            long lastId = contentDatas.get(contentDatas.size() - 1).getItemId();
//            getService().getContentManager().getHistoryItems(gardenId,
//                    Constants.CONTENT_TYPE.ACTIVITY, lastId);
            getService().getHomeWorkManager().getHistoryHomeworkMemberList(id);
        } else {
            xListView.stopLoadMore();
        }
        xListView.stopLoadMore();
    }

    public void getLoadMore(List<HomeWorkMember> list) {
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
            adapter = new WorkSocreAdapter(this, contentDatas);
            xListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        position = position - 1;
        if(position >= 0) {
            HomeWorkMember contentItem = contentDatas.get(position);
            if (contentItem.getStatus()!=0){

                String token = null;
                LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                if (loginUser != null) {
                    token = loginUser.getToken();

                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(SysConstants.H5_GAME_HOST_URL);
                stringBuilder.append("?");
                stringBuilder.append("action=10002&");
            stringBuilder.append("memberId=" + contentItem.getMemberId() + "&");
//                stringBuilder.append("memberId=" + contentItem.getHomeworkId()、 + "&");
                stringBuilder.append("token=" + token + "&");
                stringBuilder.append("isTaskState=" + "1");
//            showAndSaveLog(TAG, "TOKEN数据=======================+" + stringBuilder.toString(), false);
//                WebSubUrlActivity.invoke(mContext, stringBuilder.toString(),
//                        getString(com.tuxing.app.R.string.homeworkscore));
                Intent intent = new Intent(this,WorkDoShowActivity.class);
                intent.putExtra("MemberId",contentItem.getMemberId());
                startActivity(intent);
            }


        }}

    @Override
    public void getData() {
        if(user != null)
            gardenId = user.getGardenId();
        initData();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTitleLeft:
//            点击退出界面
                this.finish();
                break;
//            case R.id.tv_right:
////            点击跳转布置作业
//                Intent intent = new Intent(this,SetWorkActivity.class);
//                break;
        }
    }


}
