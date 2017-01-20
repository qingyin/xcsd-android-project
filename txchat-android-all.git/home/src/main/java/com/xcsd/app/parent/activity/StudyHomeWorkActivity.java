package com.xcsd.app.parent.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.manager.CounterManager;
import com.tuxing.sdk.manager.impl.CounterManagerImpl;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.StudyHomeWorkAdapter;
import com.tuxing.app.ui.dialog.CommonDialog;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.HomeworkEvent;
import com.xcsd.rpc.proto.EventType;
import com.yixia.camera.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

//import com.tuxing.app.activity.HomeActivityActivity;


/**
 * Created by shan on 2016/4/6.
 */
public class StudyHomeWorkActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static StudyHomeWorkActivity instance = null;

    private SwipeListView mListView;
    private List<HomeWorkRecord> contentDatas = new ArrayList<HomeWorkRecord>();

    private String TAG = NoticeInboxActivity.class.getSimpleName();
    private boolean hasMore = false;
    private RelativeLayout notice_in_bg;

    private StudyHomeWorkAdapter adapter;
    private long gardenId;
    private RelativeLayout activity_bg;
    private Button bt_Delete;
    private CounterManager counterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.home_study_homework_layout);
        init();
        //getData();
        instance = this;

//        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "homework");
    }

    private void init() {
        setTitle(getString(com.tuxing.app.R.string.study_homework));
        String titlename="";
//        titlename = getIntent().getStringExtra("name");
        if (!SysConstants.HomeWorkTitle.equals("")){
            setTitle(SysConstants.HomeWorkTitle);
        }
        setLeftBack("", true, false);
        //  setRightNext(true, "", 0);
        iv_right.setImageResource(R.drawable.reportcard);
        iv_right.setVisibility(View.VISIBLE);
        //  TAG = HomeActivityActivity.class.getSimpleName();
        contentDatas = new ArrayList<HomeWorkRecord>();
        mListView = (SwipeListView) findViewById(R.id.swip_home_study_homework);
         activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        // mListView.setOnItemLongClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showContextMenu(position);
                return true;
            }
        });
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(StudyHomeWorkActivity.this, StudyRecordActivity.class));
            }
        });

        counterManager = CounterManagerImpl.getInstance();

        //mListView.setOnItemClickListener(this);
        updateAdapter();
        //mListView.startRefresh();
    }

    public void updateAdapter() {
        if (contentDatas != null && contentDatas.size() > 0) {
              activity_bg.setVisibility(View.GONE);
        } else {
              activity_bg.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            adapter = new StudyHomeWorkAdapter(this, contentDatas);
            // adapter=new HomeActicityAdapter(this,contentDatas);
            mListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {

            //List<ContentItem> tempDatas = new ArrayList<ContentItem>();
            List<HomeWorkRecord> tempDatas = new ArrayList<>();
            switch (event.getEvent()) {

                case HOMEWORK_LIST_FROM_LOCAL:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkRecords();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        contentDatas.addAll(tempDatas);
                        updateAdapter();
                    }

                    getService().getHomeWorkManager().getLatestHomeWorkList();
                    showAndSaveLog(TAG, "获取本地活动数据成功 size = " + contentDatas.size(),
                            false);
                    break;


                case HOMEWORK_LIST_HISTORY_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkRecords();
                    getLoadMore(tempDatas);
                    showAndSaveLog(TAG, "获取历史活动成功 size = " + contentDatas.size(),
                            false);
                    break;

                case HOMEWORK_LIST_HISTORY_FAILED:
                    mListView.stopLoadMore();
                    updateAdapter();
                    showAndSaveLog(TAG, "获取历史活动失败" + event.getMsg(), false);
                    break;


                case HOMEWORK_LIST_LATEST_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    mListView.stopRefresh();
                    //  showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;

                case HOMEWORK_LIST_LATEST_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkRecords();

                    getResresh(tempDatas);
                    showAndSaveLog(TAG, "获取作業列表数据成功 size = " + contentDatas.size(),
                            false);


                    break;
//                case HOMEWORK_DELETE_NOTICE_SUCCESS:
//                    dialog.dismiss();
//                    contentDatas.remove(deletenum - 1);
//                    adapter.notifyDataSetChanged();
//                    break;
            }
        }
    }

    public void getLoadMore(List<HomeWorkRecord> list) {
        if (list != null && list.size() > 0) {
            contentDatas.addAll(list);
        }
        mListView.stopLoadMore();
        updateAdapter();
    }

    private void getResresh(List<HomeWorkRecord> refreshList) {
        // TODO 加载数据
        if (refreshList != null && refreshList.size() > 0) {
            contentDatas.clear();
            contentDatas.addAll(0, refreshList);
        }
        updateAdapter();
        mListView.stopRefresh();
    }

    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
        getData();
    }

    private void initData() {
        // TODO 获取服务器数据
        mListView.startRefresh();

        getService().getHomeWorkManager().getHomeWorkListFromLocal();

    }

    @Override
    public void onRefresh() {


        getService().getHomeWorkManager().getLatestHomeWorkList();

    }

    @Override
    public void onLoadMore() {
        if (contentDatas.size() > 0) {

            long lastId = contentDatas.get(contentDatas.size() - 1).getHwRecordId();

            // getService().getHomeWorkManager().HomeworkListRequest(null, lastId);
            getService().getHomeWorkManager().getHistoryHomeWorkList(lastId);

        } else {
            mListView.stopLoadMore();
        }
//        mListView.stopLoadMore();
    }

    @Override
    public void getData() {
        if (user != null)
            gardenId = user.getGardenId();
        initData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;
        if (position >= 0) {
            HomeWorkRecord contentItem = contentDatas.get(position);
//            contentItem.setHasRead(true);
//            updateAdapter();
            if(!contentItem.getHasRead()){
                getService().getHomeWorkManager().ReadHomeworkNoticeRequest(contentItem.getHwRecordId());
            }

            //发送已读的请求
            int status =1;
            if (contentItem.getStatus() == Constants.HOMEWORK_STATUS.FINISHED){
                status =0;
            };
            Intent intent  = new Intent(this,HomeWorkDetailsActivity.class).putExtra("MemberId",contentItem.getMemberId()).putExtra("status",status);
            startActivity(intent);

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        //请求服务器成功
        showContextMenu(position);
        return true;
    }

    public void showFooterView() {
        if (hasMore)
            mListView.setPullLoadEnable(true);
        else
            mListView.setPullLoadEnable(false);
    }

    public void showContextMenu(final int index) {
        final CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(StudyHomeWorkActivity.this);
//        dialog.setTitle("选择操作");
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //请求服务器成功
                HomeWorkRecord contentItem = contentDatas.get(index-1);
                if(!contentItem.getHasRead()){
//                    getService().getHomeWorkManager().ReadHomeworkNoticeRequest(contentItem.getHwRecordId());
                    counterManager.decCounter(Constants.COUNTER.HOMEWORK);
                }

                getService().getHomeWorkManager().DeleteHomeworkNoticeRequest(contentDatas.get(index - 1).getHwRecordId());

                dialog.dismiss();
                contentDatas.remove(index - 1);

                adapter.notifyDataSetChanged();
            }
        });
        dialog.setMessage("确认删除吗?");
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "homework");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setLeftBack(String text,boolean isBack,final boolean isAlert){
        super.setLeftBack(text,isBack,isAlert);
        if(isBack){
            ll_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "homework");
                    finish();//返回
                }
            });
        }
    }
}
