package com.tuxing.app.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.R;
import com.tuxing.app.adapter.LyceumAdapter;
import com.tuxing.app.adapter.LyceumAdapter.ClickListener;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.db.entity.ContentItemGroup;
import com.tuxing.sdk.event.ContentItemGroupEvent;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import me.maxwin.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

public class LyceumActivity extends BaseActivity implements IXListViewListener {
	public static final String LOG_TAG = LyceumActivity.class.getSimpleName();
	

	private SwipeListView mListView;
	private LyceumAdapter adapter;
	private List<ContentItemGroup> contentDatas;
	private String TAG = LyceumActivity.class.getSimpleName();
	private boolean hasMore = false;
	public static boolean isScroll = true;
	private RelativeLayout lym_bg;

	public static LyceumActivity instance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.lyceum_home_layout);

		instance = this;

		init();
//		mListView.setPullLoadEnable(false);
	}
	
	private void init() {
		setTitle(getString(R.string.school_home));
		setLeftBack("", true,false);
		setRightNext(true, "", 0);
		contentDatas = new ArrayList<ContentItemGroup>();
		mListView = (SwipeListView) findViewById(R.id.lyceum_list);
		lym_bg = (RelativeLayout) findViewById(R.id.lym_bg);
		mListView.setXListViewListener(this);
		mListView.setOnScrollListener(new OnScrollListener() {
			 
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
                	isScroll = true;
                	updateAdapter();
                    break;
 
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滑动
                	isScroll = false;
                	break;
                	
                }
            }
 
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });
	}
	@Override
	protected void onResume() {
		super.onResume();
		isActivity = true;
		if(contentDatas != null && contentDatas.size() == 0){
			initData();
		}
	}
	
	private void initData() {
//		mListView.startRefresh();
		getService().getContentManager().getGroupItemsFromLocal();
//		getService().getContentManager().getLatestGroupItems();
	}
	
	 public void onEventMainThread(ContentItemGroupEvent event){
		 if(isActivity){
			 List<ContentItemGroup> tempDatas = new ArrayList<ContentItemGroup>();
		 switch (event.getEvent()){
		 case FETCH_ITEM_GROUP_FROM_LOCAL:
			 hasMore  = event.isHasMore();
			 tempDatas = event.getItemGroups();
			 if(tempDatas != null && tempDatas.size() > 0){
				 contentDatas.clear();
				 contentDatas.addAll(tempDatas);
				 updateAdapter();
				 mListView.setSelection(contentDatas.size());
			 }
				 getService().getContentManager().getLatestGroupItems();
			 showAndSaveLog(TAG, "获取最新微学园数据成功 size = " + contentDatas.size(), false);
			 break;
		 case FETCH_LATEST_ITEM_GROUP_SUCCESS:
			 hasMore  = event.isHasMore();
			 tempDatas = event.getItemGroups();
			 getLast(tempDatas);
			 showAndSaveLog(TAG, "获取最新微学园数据成功 size = " + contentDatas.size(), false);
			 getService().getCounterManager().resetCounter(Constants.COUNTER.LEARN_GARDEN);
			 break;
		 case FETCH_LATEST_ITEM_GROUP_FAILED:
				 showToast(event.getMsg());
			 updateAdapter();
			 hasMore = false;
			 getLast(tempDatas);
			 showAndSaveLog(TAG, "获取最新的数据失败"+ event.getMsg(), false);
			 break;
		 case FETCH_HISTORY_ITEM_GROUP_SUCCESS:
			 hasMore  = event.isHasMore();
			 tempDatas = event.getItemGroups();
			 getResresh(tempDatas);
			 showAndSaveLog(TAG, "获取历史微学园成功 size = "+ contentDatas.size(), false);
			 break;
		 case FETCH_HISTORY_ITEM_GROUP_FAILED:
			 getResresh(tempDatas);
			 showAndSaveLog(TAG, "获取历史微学园失败  msg = "+ event.getMsg(), false);
			 break;
		
		 } }
	 } 
	 
	 @Override
		public void onRefresh() {
		 if(contentDatas != null && contentDatas.size() > 0){
			 //TODO
			 long firstId = contentDatas.get(0).getGroupId();
			 getService().getContentManager().getHistoryGroupItems(firstId);
		 }else{
			 mListView.stopRefresh();
		 }
		}

		private void getResresh(List<ContentItemGroup> refreshList) {
			if(refreshList != null && refreshList.size() > 0){
				contentDatas.addAll(0,refreshList);
			}
			mListView.stopRefresh();
			updateAdapter();
		}
		private void getLast(List<ContentItemGroup> refreshList) {
			if(refreshList != null && refreshList.size() > 0){
				contentDatas.clear();
				contentDatas.addAll(refreshList);
			}
			updateAdapter();
			mListView.stopRefresh();
			 mListView.setSelection(contentDatas.size());
		}
		
		@Override
		public void onLoadMore() {
		}
		
		
	public void updateAdapter(){
		
		if(contentDatas != null && contentDatas.size() > 0){
			lym_bg.setVisibility(View.GONE);
		}else{
			lym_bg.setVisibility(View.VISIBLE);
		}
		if(adapter == null){
			adapter = new LyceumAdapter(this, contentDatas,new MyClickListener());
			mListView.setAdapter(adapter);
			mListView.setPullLoadEnable(false);
		}else{
			adapter.notifyDataSetChanged();
		}
	}
	
	
	
	/**
	 * 显示选择的item
	 * @param num
	 */
	public class MyClickListener implements ClickListener{

		@Override
		public void itemClick() {
			String clickUrl = adapter.getClickId();
			if(!TextUtils.isEmpty(clickUrl)){
				WebSubDataActivity.invoke(mContext,clickUrl,getString(R.string.school_home));
			}
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
		getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "understandchild");
		super.finish();
	}
}
