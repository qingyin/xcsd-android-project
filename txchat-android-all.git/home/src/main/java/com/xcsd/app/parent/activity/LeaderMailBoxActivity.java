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
import com.xcsd.app.parent.adapter.LeaderMailBoxAdapter;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.db.entity.GardenMail;
import com.tuxing.sdk.event.GardenMailEvent;
import com.umeng.analytics.MobclickAgent;
import me.maxwin.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;


public class LeaderMailBoxActivity extends BaseActivity implements IXListViewListener,OnItemClickListener {

	private SwipeListView swipListView;
	private List<GardenMail> mails;
	private LeaderMailBoxAdapter adapter;
	private String TAG ;
	private RelativeLayout mailbosBg;
	private boolean hasMore = false;
	private RelativeLayout contentRl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.home_mailbox_layout);
		init();
		initData();
		}
	
	@Override
	protected void onResume() {
		isActivity = true;
		updataAdapter();
		super.onResume();
	}

		private void init() {
			setTitle(getString(R.string.mailbox));
			setLeftBack("取消", true,false);
			setRightNext(false, "", R.drawable.title_edit_icon);
			TAG = LeaderMailBoxActivity.class.getSimpleName();
			mails = new ArrayList<GardenMail>();
			swipListView = (SwipeListView) findViewById(R.id.mailbox_list);
			mailbosBg = (RelativeLayout) findViewById(R.id.mailbox_bg);
			contentRl = (RelativeLayout) findViewById(R.id.content_rl);
			swipListView.setXListViewListener(this);
			swipListView.setOnItemClickListener(this);
			swipListView.startRefresh();
			mailbosBg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, LeaderMailboxReleaseActivity.class);
					startActivityForResult(intent, 100);
				}
			});
			
		}
		
		private void initData() {
			getService().getFeedManager().getGardenMailsFromLocal();
		}
		
		/**
		 * 服务器返回
		 * @param event
		 */
		 public void onEventMainThread(GardenMailEvent event){
			 if(isActivity){
				 List<GardenMail> tempList = new ArrayList<GardenMail>();
			 switch (event.getEvent()) {
			 case FETCH_HISTORY_MAIL_FROM_LOCAL:
				 tempList = event.getMails();
				 hasMore = event.isHasMore();
				 if(tempList != null && tempList.size() > 0){
					 mails.clear();
					 mails.addAll(tempList);
					 updataAdapter();
				 }
				 getService().getFeedManager().getLatestGardenMails();
				 showAndSaveLog(TAG, "获取本地邮件成功 size = " + mails.size(), false);
				 break;
			 case FETCH_LATEST_MAIL_SUCCESS:
				 tempList = event.getMails();
				 hasMore = event.isHasMore();
				 getResresh(tempList);
				 showAndSaveLog(TAG, "获取邮件成功 size = " + mails.size(), false);
				 break;
			 case FETCH_LATEST_MAIL_FAILED:
					 showToast(event.getMsg());
				 updataAdapter();
				 swipListView.stopRefresh();
				 showAndSaveLog(TAG, "获取邮件失败 msg = " + event.getMsg(), false);
				 break;
			 case FETCH_HISTORY_MAIL_SUCCESS:
				 tempList = event.getMails();
				 hasMore = event.isHasMore();
				 getLoadMore(tempList);
					disProgressDialog();
				 showAndSaveLog(TAG, "请求历史邮件成功 size = " + mails.size(), false);
				 break;
			 case FETCH_HISTORY_MAIL_FAILED:
				 swipListView.stopLoadMore();
				 swipListView.stopLoadMore();
				 updataAdapter();
				disProgressDialog();
				 showAndSaveLog(TAG, "请求历史邮件失败" + event.getMsg(), false);
				 break;
				 
			}
		    }
		 }
		

		@Override
		public void onRefresh() {
			getService().getFeedManager().getLatestGardenMails();
		}
		
		private void getResresh(List<GardenMail> refreshList) {
			//TODO 加载数据
			if(refreshList != null && refreshList.size() > 0){
				mails.clear();
				mails.addAll(0,refreshList);
			}else{
			}
			updataAdapter();
			swipListView.stopRefresh();
		}
		
		@Override
		public void onLoadMore() {
			if(mails.size() > 0){
				long lastId = mails.get(mails.size() - 1).getMailId();
				getService().getFeedManager().getHistoryGardenMails(lastId);
			}else{
				swipListView.stopLoadMore();
			}
		}
		
		public void getLoadMore(List<GardenMail> list) {
			if(list != null){
				mails.addAll(list);
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
			if(mails != null && mails.size() > 0){
				mailbosBg.setVisibility(View.GONE);
			}else{
				mailbosBg.setVisibility(View.VISIBLE);
			}
			if(adapter  == null){
				adapter = new LeaderMailBoxAdapter(this, mails);
				swipListView.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
			showFooterView();
		}

	@Override
	public void onclickRightImg() {
		super.onclickRightImg();
		Intent intent = new Intent(mContext, LeaderMailboxReleaseActivity.class);
		startActivityForResult(intent, 100);
		MobclickAgent.onEvent(mContext,"mail_new",UmengData.mail_new);
	}
	
	@Override
	public void onCancelAlert() {
		finish();
		super.onCancelAlert();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position = position - 1;
		if(position >= 0){
			readMil(mails.get(position));
			mails.get(position).setUpdated(false);
			updataAdapter();
			Intent intent = new Intent(mContext, leaderMailboxInfoActivity.class);
			intent.putExtra("mail", mails.get(position));
			startActivityForResult(intent, 100);
		}

	}
	
	private void readMil(GardenMail mail) {
		getService().getFeedManager().markGardenMailAsRead(mail.getMailId());
}
	

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1 == RESULT_OK){
			initData();
		}
	}



	
}
