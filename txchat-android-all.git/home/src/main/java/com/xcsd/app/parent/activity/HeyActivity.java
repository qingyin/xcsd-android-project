package com.xcsd.app.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.HeyAdapter;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.db.entity.FeedMedicineTask;
import com.tuxing.sdk.event.FeedMedicineTaskEvent;
import com.umeng.analytics.MobclickAgent;
import me.maxwin.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;


public class HeyActivity extends BaseActivity implements IXListViewListener,OnItemClickListener {

	private SwipeListView swipListView;
	private List<FeedMedicineTask> feedList;
	private HeyAdapter adapter;
	private String TAG ;
	private RelativeLayout heyBg;
	private boolean hasMore = false;
	private RelativeLayout contentRl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.home_hey_layout);
		init();
		initData();
		updataAdapter();
		}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

		private void init() {
			setTitle(getString(R.string.eat_drug));
			setLeftBack("", true,false);
			setRightNext(false, "", R.drawable.title_edit_icon);
			feedList = new ArrayList<FeedMedicineTask>();
			TAG = HeyActivity.class.getSimpleName();
			swipListView = (SwipeListView) findViewById(R.id.hey_list);
			swipListView.startRefresh();
			heyBg = (RelativeLayout) findViewById(R.id.hey_bg);
			contentRl = (RelativeLayout) findViewById(R.id.content_rl);
			swipListView.setXListViewListener(this);
			swipListView.setOnItemClickListener(this);
			swipListView.startRefresh();
			heyBg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, HeyReleaseActivity.class);
					startActivityForResult(intent, 100);
				}
			});
			
		}
		
		private void initData() {
			// TODO 获取服务器
			getService().getFeedManager().getFeedMedicineTaskFromLocal();
		
		}
		
		/**
		 * 服务器返回
		 * @param event
		 */
		 public void onEventMainThread(FeedMedicineTaskEvent event){
			 if(isActivity){
				 List<FeedMedicineTask> tempList = new ArrayList<FeedMedicineTask>();
			 switch (event.getEvent()) {
			 case FEED_MEDICINE_FROM_LOCAL:
				 tempList = event.getFeedMedicineTasks();
				 if(tempList != null && tempList.size() > 0){
					 feedList.clear();
					 feedList.addAll(tempList);
				 }
				 hasMore = event.isHasMore();
				 updataAdapter();
					getService().getFeedManager().getLatestFeedMedicineTask();
				 showAndSaveLog(TAG, "获取本地喂药列表成功 size = " + feedList.size(), false);
				 break;
			 case FEED_MEDICINE_LATEST_SUCCESS:
				 tempList = event.getFeedMedicineTasks();
				 hasMore = event.isHasMore();
					 getResresh(tempList);
				 showAndSaveLog(TAG, "获取喂药列表成功 size = " + feedList.size(), false);
				 break;
			 case FEED_MEDICINE_LATEST_FAILED:
					 showToast(event.getMsg());
				 updataAdapter();
				 swipListView.stopRefresh();
				 showAndSaveLog(TAG, "获取喂药列表失败 msg = " + event.getMsg(), false);
				 break;
			 case FEED_MEDICINE_HISTORY_SUCCESS:
				 tempList = event.getFeedMedicineTasks();
				 hasMore = event.isHasMore();
				 getLoadMore(tempList);
				 showAndSaveLog(TAG, "获取历史喂药列表成功 size = " + feedList.size(), false);
				 break;
			 case FEED_MEDICINE_HISTORY_FAILED:
				 swipListView.stopLoadMore();
				 updataAdapter();
				 showAndSaveLog(TAG, "请求历史喂药列表失败  msg = " + event.getMsg(), false);
				 break;
				 
			}
		    }
		 }
		

		@Override
		public void onRefresh() {
			getService().getFeedManager().getLatestFeedMedicineTask();
		}
		
		private void getResresh(List<FeedMedicineTask> refreshList) {
			//TODO 加载数据
			if(refreshList != null && refreshList.size() > 0){
				feedList.clear();
				feedList.addAll(0,refreshList);
			}
			updataAdapter();
			swipListView.stopRefresh();
		}
		
		@Override
		public void onLoadMore() {
			if(feedList.size() > 0){
				long lastId = feedList.get(feedList.size() - 1).getTaskId();
				getService().getFeedManager().getHistoryFeedMedicineTask(lastId);
			}else{
				swipListView.stopLoadMore();
			}
		}
		
		public void getLoadMore(List<FeedMedicineTask> list) {
			if(list != null && list.size() > 0){
				feedList.addAll(list);
			}
			swipListView.stopLoadMore();
			updataAdapter();
		}
		
		public void showFooterView(){
			if(hasMore){
				swipListView.setPullLoadEnable(true);
			}else{
				swipListView.setPullLoadEnable(false);
			}
		}

		
		public void updataAdapter(){
			if(feedList != null && feedList.size() > 0){
				heyBg.setVisibility(View.GONE);
			}else{
				heyBg.setVisibility(View.VISIBLE);
			}
			if(adapter  == null){
				adapter = new HeyAdapter(this, feedList);
				swipListView.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
			 showFooterView();
		}

	@Override
	public void onclickRightImg() {
		super.onclickRightImg();
		Intent intent = new Intent(mContext, HeyReleaseActivity.class);
		startActivityForResult(intent, 100);
		MobclickAgent.onEvent(mContext,"wy_new",UmengData.wy_new);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position = position - 1;
		if(position >= 0){
		FeedMedicineTask feedMedicineTask = feedList.get(position);
		readHey(feedMedicineTask);
		feedMedicineTask.setUpdated(false);
		updataAdapter();
		Intent intent = new Intent(mContext, HeyInfoActivity.class);
	     intent.putExtra("feed", feedMedicineTask);
		openActivityOrFragment(intent);
		}
	}
	
	private void readHey(FeedMedicineTask feed) {
		getService().getFeedManager().markFeedMedicineTaskAsRead(feed.getTaskId());
}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1 == RESULT_OK){
			initData();
		}
	}

}
