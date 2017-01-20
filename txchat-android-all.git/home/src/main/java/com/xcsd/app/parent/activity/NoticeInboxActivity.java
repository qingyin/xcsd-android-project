package com.xcsd.app.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.NoticeInboxAdapter;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.event.NoticeEvent;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView.IXListViewListener;


public class NoticeInboxActivity extends BaseActivity implements IXListViewListener,OnItemClickListener {

	private SwipeListView swipListView;
	private List<Notice> noticeList = new ArrayList<Notice>();
	private NoticeInboxAdapter adapter;
	private String TAG = NoticeInboxActivity.class.getSimpleName();
	private boolean hasMore = false;
	private RelativeLayout notice_in_bg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.home_notice_layout);
		//家长版用
		setContentLayout(R.layout.home_notice_layout);
		init();
		getData();

//		getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "notice");

		}
	
	@Override
	protected void onResume() {
		super.onResume();
		isActivity = true;
		if(noticeList.size() == 0){
//			initData();
		}
	}

		private void init() {
			setTitle(getString(R.string.tab_child_marking));
			setLeftBack("", true, false);
		setRightNext(false, "", R.drawable.ic_circle_more_p);
//		setRightNext(false, "", R.drawable.ic_back);
			swipListView = (SwipeListView) findViewById(R.id.notice_list);
			notice_in_bg = (RelativeLayout) findViewById(R.id.notice_in_bg);
			swipListView.setXListViewListener(this);
			swipListView.setOnItemClickListener(this);
			swipListView.startRefresh();
		}
		
		private void initData() {
			// TODO   本地数据
			getService().getNoticeManager().getLocalCachedNotice(Constants.MAILBOX_INBOX);
		}
		
		/**
		 * 服务器返回
		 * @param event
		 */
		 public void onEventMainThread(NoticeEvent event){
			 if(isActivity){
				 List<Notice> tempList = new ArrayList<Notice>();
			 switch (event.getEvent()) {
			 case NOTICE_INBOX_FROM_CACHE:
				 tempList = event.getNotices();
				 if(tempList != null && tempList.size() > 0){
					 noticeList.addAll(0,tempList);
				 }
				 hasMore = event.isHasMore();
				 updataAdapter();
				 getService().getNoticeManager().getLatestNotice(Constants.MAILBOX_INBOX);
				 showAndSaveLog(TAG, "获取本地最新收件通知成功 size = " + noticeList.size(), false);
				 break;
			 case NOTICE_INBOX_LATEST_NOTICE_SUCCESS:
				 tempList = event.getNotices();
				 hasMore  = event.isHasMore();
					 getResresh(tempList);
				 showAndSaveLog(TAG, "获取服务器最新收件通知成功 size = " + noticeList.size(), false);
				 break;
			 case NOTICE_INBOX_REQUEST_SUCCESS:
				 tempList = event.getNotices();
				 hasMore  = event.isHasMore();
					 getLoadMore(tempList);
				 showAndSaveLog(TAG, "请求历史收件通知成功 size = " + noticeList.size(), false);
				 break;
			 case NOTICE_INBOX_REQUEST_FAILED:
					 showToast(event.getMsg());
				 updataAdapter();
				 swipListView.stopLoadMore();
				 swipListView.stopRefresh();
				 showAndSaveLog(TAG, "请求收件通知失败" + event.getMsg(), false);
					 break;
			 case NOTICE_CLEAR_SUCCESS:

				 noticeList.clear();;
				 hasMore  = event.isHasMore();
				 updataAdapter();
				 disProgressDialog();
				 Intent intent = new Intent(TuxingApp.packageName + SysConstants.DELREFRESHACTION);
				 intent.putExtra("action","notice");
				 sendBroadcast(intent);
				 showAndSaveLog(TAG, "清空收件通知成功", false);
				 break;
			 case NOTICE_CLEAR_FAILED:
				 disProgressDialog();
				 showToast(event.getMsg());
				 showAndSaveLog(TAG, "清空收件通知失败", false);
				 break;

			}
		    }
		 }
		

		@Override
		public void onRefresh() {
			getService().getNoticeManager().getLatestNotice(Constants.MAILBOX_INBOX);
		}
		
		private void getResresh(List<Notice> refreshList) {
			 if(refreshList != null && refreshList.size() > 0){
				 noticeList.clear();
				 noticeList.addAll(0,refreshList);
			 }
			updataAdapter();
			swipListView.stopRefresh();
		}
		
		@Override
		public void onLoadMore() {
			if(noticeList.size() >= 0){
				long lastId = noticeList.get(noticeList.size() - 1).getNoticeId();
				getService().getNoticeManager().getHistoryNotice(lastId, Constants.MAILBOX_INBOX);
			}else{
				swipListView.stopLoadMore();
			}
		}
		
		public void getLoadMore(List<Notice> list) {
			 if(list != null && list.size() > 0){
				 noticeList.addAll(list);
			 }
			 swipListView.stopLoadMore();
			 updataAdapter();
		}
		
		public void showFooterView(){
			if(hasMore){
				swipListView.setPullLoadEnable(true);
			}
			else{
				swipListView.setPullLoadEnable(false);
			}
		}

		
		public void updataAdapter(){
			
			if(noticeList != null && noticeList.size() > 0){
				notice_in_bg.setVisibility(View.GONE);
			}else{
				notice_in_bg.setVisibility(View.VISIBLE);
			}
			if(adapter  == null){
				adapter = new NoticeInboxAdapter(this, noticeList);
				swipListView.setAdapter(adapter);
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
			if (noticeList.get(position).getUnread()) {
				noticeList.get(position).setUnread(false);
				readNotice(noticeList.get(position));
				getService().getDataReportManager().reportEventBid(EventType.READ_NOTICE,noticeList.get(position).getNoticeId()+"");
			}
			updataAdapter();
//			readNotice(noticeList.get(position));
			Intent intent = new Intent(mContext, NoticeInboxInfoActivity.class);
			intent.putExtra("notice", noticeList.get(position));
			openActivityOrFragment(intent);
		}

	}
	@Override
	public void getData() {
		initData();
	}

	private void readNotice(Notice notice) {
				getService().getNoticeManager().markAsRead(notice.getNoticeId());
	}

	@Override
	public void onclickRightImg() {
		showBtnDialog(new String[]{getString(R.string.btn_clear), getString(R.string.btn_cancel)});
	}

	@Override
	public void onclickBtn1() {
		super.onclickBtn1();
		// TODO 清空
		if(noticeList != null && noticeList.size() > 0){
			showProgressDialog(mContext, "清除中...", false, null);
			getService().getNoticeManager().clearNotice(noticeList.get(0).getNoticeId(), Constants.MAILBOX_INBOX);
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
		getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "notice");
		super.finish();
	}
}
