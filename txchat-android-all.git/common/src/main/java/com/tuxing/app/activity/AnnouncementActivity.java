package com.tuxing.app.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.R;
import com.tuxing.app.adapter.AnnouncementAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.event.DataReportEvent;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.Constants.CONTENT_TYPE;
import com.xcsd.rpc.proto.EventType;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends BaseActivity implements IXListViewListener, OnItemClickListener {
	public static final String LOG_TAG = AnnouncementActivity.class.getSimpleName();
	private SwipeListView mListView;
	private AnnouncementAdapter adapter;
	private List<ContentItem> contentDatas;
	private String TAG = AnnouncementActivity.class.getSimpleName();
	private long gardenId;
	private RelativeLayout announ_bg;

	private boolean hasMore = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.announcement_layout);
		init();
		initData();
		updateAdapter();

		getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "announcement");
	}
	
	private void init() {
		setTitle(getString(R.string.call_boark));
		setLeftBack("", true,false);
		setRightNext(true, "", 0);
		//TODO 幼儿园id
		 contentDatas = new ArrayList<ContentItem>();
		 if(user != null)
		 gardenId = user.getGardenId();
		mListView = (SwipeListView) findViewById(R.id.announcement_list);
		mListView.startRefresh();
		announ_bg = (RelativeLayout) findViewById(R.id.announ_bg);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
	}
	@Override
	protected void onResume() {
		isActivity = true;
		super.onResume();
	}
	
	private void initData() {
		getService().getContentManager().getItemsFromLocal(CONTENT_TYPE.ANNOUNCEMENT);
	}
	 public void onEventMainThread(ContentItemEvent event){
		 if(isActivity){
			 List<ContentItem> tempDatas = new ArrayList<ContentItem>();
		 switch (event.getEvent()){
		 case FETCH_ITEM_FROM_LOCAL:
			 tempDatas = event.getItems();
			 hasMore  = event.isHasMore();
			 if(tempDatas != null && tempDatas.size() > 0){
				 contentDatas.clear();
				 contentDatas.addAll(tempDatas);
				 updateAdapter();
			 }
			 getService().getContentManager().getLatestItems(gardenId, CONTENT_TYPE.ANNOUNCEMENT);
			 showAndSaveLog(TAG, "获取本地公告数据成功 size = " + contentDatas.size(), false);
			 break;
		 case FETCH_LATEST_ITEM_SUCCESS:
			 hasMore  = event.isHasMore();
			 tempDatas = event.getItems();
			 getResresh(tempDatas);
			 showAndSaveLog(TAG, "获取最新公告数据成功 size = " + contentDatas.size(), false);
		 	 getService().getCounterManager().resetCounter(Constants.COUNTER.ANNOUNCEMENT);
			 break;
			 case FETCH_LATEST_ITEM_FAILED:
			 hasMore = false;
				 showToast(event.getMsg());
			 updateAdapter();
			 mListView.stopRefresh();
			 showAndSaveLog(TAG, "获取最新的数据失败"+ event.getMsg(), false);
			 break;
		 case FETCH_HISTORY_ITEM_SUCCESS:
			 hasMore  = event.isHasMore();
			 tempDatas = event.getItems();
			 getLoadMore(tempDatas);
			 showAndSaveLog(TAG, "获取历史公告成功 size = " + contentDatas.size(), false);
			 break;
		 case FETCH_HISTORY_ITEM_FAILED:
			 hasMore = false;
			 mListView.stopLoadMore();
			 updateAdapter();
			 showAndSaveLog(TAG, "获取历史公告失败"+ event.getMsg(), false);
			 break;
		 } }
	 } 
	


	
	@Override
	public void onRefresh() {
		getService().getContentManager().getLatestItems(gardenId, CONTENT_TYPE.ANNOUNCEMENT);
	}

	private void getResresh(List<ContentItem> refreshList) {
		//TODO 加载数据
		if(refreshList != null){
			contentDatas.clear();
			contentDatas.addAll(0,refreshList);
		}
		updateAdapter();
		mListView.stopRefresh();
	}
	
	@Override
	public void onLoadMore() {
		if(contentDatas.size() > 0){
			long lastId = contentDatas.get(contentDatas.size() - 1).getItemId();
			getService().getContentManager().getHistoryItems(gardenId, CONTENT_TYPE.ANNOUNCEMENT, lastId);
		}else{
			mListView.stopLoadMore();
		}
		mListView.stopLoadMore();
	}
	
	public void getLoadMore(List<ContentItem> list) {
		if(list != null && list.size() > 0){
		contentDatas.addAll(list);
	}
		mListView.stopLoadMore();
		updateAdapter();
	}
	
	public void showFooterView(){
		if(hasMore)
			mListView.setPullLoadEnable(true);
		else
			mListView.setPullLoadEnable(false);
	}

	
	public void updateAdapter(){
		if(contentDatas != null && contentDatas.size() > 0){
			announ_bg.setVisibility(View.GONE);
		}else{
			announ_bg.setVisibility(View.VISIBLE);
		}
		if(adapter == null){
			adapter = new AnnouncementAdapter(this, contentDatas);
			mListView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		showFooterView();
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position = position - 1;
		if(position >= 0) {
		ContentItem contentItem = contentDatas.get(position);
		WebSubDataActivity.invoke(mContext, contentItem.getPostUrl(), getString(R.string.call_boark));
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
		getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "announcement");
		super.finish();
	}
}
