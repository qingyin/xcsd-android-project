package com.tuxing.app.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.R;
import com.tuxing.app.adapter.HomeActicityAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.event.DataReportEvent;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.Constants.CONTENT_TYPE;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView.IXListViewListener;

public class HomeActivityActivity extends BaseActivity implements
        IXListViewListener, OnItemClickListener {
    public static final String LOG_TAG = HomeActivityActivity.class
            .getSimpleName();

    private SwipeListView mListView;
    private HomeActicityAdapter adapter;
    private List<ContentItem> contentDatas;
    private String TAG;
    private long gardenId;
    private boolean hasMore = false;
    private RelativeLayout activity_bg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.home_acticity_layout);
        init();
        getData();

        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "activity");
    }

    private void init() {
        setTitle(getString(R.string.activity));
        setLeftBack("", true, false);
        setRightNext(true, "", 0);
        TAG = HomeActivityActivity.class.getSimpleName();
        contentDatas = new ArrayList<ContentItem>();
        mListView = (SwipeListView) findViewById(R.id.home_acticity_list);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        updateAdapter();
    }

    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
    }

    private void initData() {
        // TODO 获取服务器数据
        mListView.startRefresh();
        getService().getContentManager().getItemsFromLocal(
                CONTENT_TYPE.ACTIVITY);
    }

    public void onEventMainThread(ContentItemEvent event) {
        if (isActivity) {
            List<ContentItem> tempDatas = new ArrayList<ContentItem>();
            switch (event.getEvent()) {
                case FETCH_ITEM_FROM_LOCAL:
                    hasMore = event.isHasMore();
                    tempDatas = event.getItems();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        contentDatas.addAll(tempDatas);
                        updateAdapter();
                    }
                    getService().getContentManager().getLatestItems(gardenId,
                            CONTENT_TYPE.ACTIVITY);
                    showAndSaveLog(TAG, "获取本地活动数据成功 size = " + contentDatas.size(),
                            false);
                    break;
                case FETCH_LATEST_ITEM_SUCCESS:
                    hasMore = event.isHasMore();
                    tempDatas = event.getItems();
                    getResresh(tempDatas);
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);
                    getService().getCounterManager().resetCounter(
                            Constants.COUNTER.ACTIVITY);
                    break;
                case FETCH_LATEST_ITEM_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    mListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;
                case FETCH_HISTORY_ITEM_SUCCESS:
                    hasMore = event.isHasMore();
                    tempDatas = event.getItems();
                    getLoadMore(tempDatas);
                    showAndSaveLog(TAG, "获取历史活动成功 size = " + contentDatas.size(),
                            false);
                    break;
                case FETCH_HISTORY_ITEM_FAILED:
                    mListView.stopLoadMore();
                    updateAdapter();
                    showAndSaveLog(TAG, "获取历史活动失败" + event.getMsg(), false);
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        getService().getContentManager().getLatestItems(gardenId,
                CONTENT_TYPE.ACTIVITY);
    }

    private void getResresh(List<ContentItem> refreshList) {
        // TODO 加载数据
        if (refreshList != null && refreshList.size() > 0) {
            contentDatas.clear();
            contentDatas.addAll(0, refreshList);
        }
        updateAdapter();
        mListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        if (contentDatas.size() > 0) {
            long lastId = contentDatas.get(contentDatas.size() - 1).getItemId();
            getService().getContentManager().getHistoryItems(gardenId,
                    CONTENT_TYPE.ACTIVITY, lastId);
        } else {
            mListView.stopLoadMore();
        }
        mListView.stopLoadMore();
    }

    public void getLoadMore(List<ContentItem> list) {
        if (list != null && list.size() > 0) {
            contentDatas.addAll(list);
        }
        mListView.stopLoadMore();
        updateAdapter();
    }

    public void showFooterView() {
        if (hasMore)
            mListView.setPullLoadEnable(true);
        else
            mListView.setPullLoadEnable(false);
    }

    public void updateAdapter() {
        if (contentDatas != null && contentDatas.size() > 0) {
            activity_bg.setVisibility(View.GONE);
        } else {
            activity_bg.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            adapter = new HomeActicityAdapter(this, contentDatas);
            mListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        position = position - 1;
        if (position >= 0) {
            ContentItem contentItem = contentDatas.get(position);
            WebSubDataActivity.invoke(mContext, contentItem.getPostUrl(),
                    getString(R.string.activity));
        }
    }

    @Override
    public void getData() {
        if (user != null)
            gardenId = user.getGardenId();
        initData();
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
        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "activity");
        super.finish();
    }
}
