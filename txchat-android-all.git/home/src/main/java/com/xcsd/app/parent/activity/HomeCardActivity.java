package com.xcsd.app.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.CardAdapter;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.CheckInRecord;
import com.tuxing.sdk.event.CheckInEvent;
import com.tuxing.sdk.utils.Constants;
import me.maxwin.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

public class HomeCardActivity extends BaseActivity implements IXListViewListener {
	
	
	private SwipeListView swipeListView;
	private CardAdapter adapter;
	private List<CheckInRecord> checkInRecords;
	private String TAG;
	private boolean hasMore = false;
	private RelativeLayout card_bg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.home_card_layout);
		init();
		initData();
	}

	private void initData() {
		swipeListView.startRefresh();
		getService().getCheckInManager().getLocalCachedCheckInRecords();
	}



	private void init() {
		
		//注册广播
		setTitle(getString(R.string.card_cloudcard));
		setLeftBack("", true,false);
		setRightNext(false, "", R.drawable.ic_circle_more);
		TAG = HomeCardActivity.class.getSimpleName();
		checkInRecords = new ArrayList<CheckInRecord>();
		swipeListView = (SwipeListView) findViewById(R.id.home_card_list);
		card_bg = (RelativeLayout) findViewById(R.id.card_bg);
		swipeListView.setXListViewListener(this);
		updateAdapter();
		
	}
	
	 public void onEventMainThread(CheckInEvent event){
		 if(isActivity){
			 List<CheckInRecord> tempList = new ArrayList<CheckInRecord>();
		 switch (event.getEvent()) {
		case CHECKIN_LOAD_FROM_CACHE:
			tempList = event.getCheckInRecords();
			hasMore = false;
			if(tempList != null && tempList.size() > 0){
				checkInRecords.clear();
				checkInRecords.addAll(0, tempList);
				updateAdapter();
			}
			getService().getCheckInManager().getLatestCheckInRecords();
			 showAndSaveLog(TAG, "获取本地刷卡成功   siz = " + checkInRecords.size(), false);
			break;
		case CHECKIN_LATEST_RECORDS_SUCCESS:
			tempList = event.getCheckInRecords();
			hasMore  = event.isHasMore();
				 getResresh(tempList);
			showAndSaveLog(TAG, "获取网络刷卡记录成功 size = " + checkInRecords.size(), false);
			getService().getCounterManager().resetCounter(Constants.COUNTER.CHECK_IN);
			break;
		case CHECKIN_REQUEST_SUCCESS:
			 tempList = event.getCheckInRecords();
			 hasMore  = event.isHasMore();
				 getLoadMore(tempList);
			 showAndSaveLog(TAG, "上拉加载刷卡记录成功 size = " + checkInRecords.size(), false);
			break;
		case CHECKIN_REQUEST_FAILED:
			 swipeListView.stopLoadMore();
			 swipeListView.stopRefresh();
			 showToast(event.getMsg());
			 updateAdapter();
			showAndSaveLog(TAG, "请获取刷卡信息失败   --" + event.getMsg(), false);
			break;
		case CHECKIN_CLEAR_SUCCESS:
			checkInRecords.clear();
			hasMore  = event.isHasMore();
			 updateAdapter();
			disProgressDialog();
			Intent intent = new Intent(TuxingApp.packageName + SysConstants.DELREFRESHACTION);
			intent.putExtra("action","card");
			sendBroadcast(intent);
			showAndSaveLog(TAG, "清除刷卡信息成功", false);
			break;
		case CHECKIN_CLEAR_FAILED:
			disProgressDialog();
			showToast(event.getMsg());
			showAndSaveLog(TAG, "清除刷卡信息失败 ", false);
			break;
		}
	 }}
	
	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	/**
	 * 下拉刷新服务器数据
	 */
	@Override
	public void onRefresh() {
		getService().getCheckInManager().getLatestCheckInRecords();
	}
	
	private void getResresh(List<CheckInRecord> list) {
		//TODO 加载数据
		if(list != null && list.size() > 0){
			checkInRecords.clear();
			checkInRecords.addAll(0,list);
		}
		swipeListView.stopRefresh();
		updateAdapter();
	}

	@Override
	public void onLoadMore() {
		if(checkInRecords.size() > 0){
			long lastId = checkInRecords.get(checkInRecords.size() - 1).getCheckInRecordId();
			getService().getCheckInManager().getHistoryCheckInRecords(Integer.valueOf(String.valueOf(lastId)));
		}else{
			swipeListView.stopLoadMore();
		}
	}

	public void getLoadMore(List<CheckInRecord> list) {
		if(list != null && list.size() > 0){
			checkInRecords.addAll(list);
		}
		updateAdapter();
		swipeListView.stopLoadMore();

	}
	
	public void showFooterView(){
		if(hasMore){
			swipeListView.setPullLoadEnable(true);
		}
		else{
			swipeListView.setPullLoadEnable(false);
		}
	}
	
	
	
	public void updateAdapter(){
		if(checkInRecords != null && checkInRecords.size() > 0 ){
			card_bg.setVisibility(View.GONE);
		}else{
			card_bg.setVisibility(View.VISIBLE);
		}
		if(adapter == null){
			adapter  = new CardAdapter(mContext, checkInRecords);
			swipeListView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		 showFooterView();
	}

	@Override
	public void onclickRightImg() {
		showBtnDialog(new String[]{getString(R.string.btn_clear), getString(R.string.btn_cancel)});
	}

	@Override
	public void onclickBtn1() {
		super.onclickBtn1();
		// TODO 清空
		if(checkInRecords != null && checkInRecords.size() > 0){
			showProgressDialog(mContext, "清除中...", false, null);
			getService().getCheckInManager().clearCheckInRecord(checkInRecords.get(0).getCheckInRecordId());
		} 
	}

}
