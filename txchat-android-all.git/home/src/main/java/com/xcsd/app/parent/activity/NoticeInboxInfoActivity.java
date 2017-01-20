package com.xcsd.app.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.Notice;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class NoticeInboxInfoActivity extends BaseActivity implements
		OnItemClickListener {
	private ArrayList<String> iconList;
	private IconAdapter adapter;
	private MyGridView iconView;
	private TextView sendName;
	private TextView sendTime;
	private TextView content;
	private String TAG = NoticeInboxActivity.class.getSimpleName();
	private Notice notice;
	
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.home_notice_info_layout);
		init();
		initData();
	}

	private void init() {
		setTitle(getString(R.string.notice));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		sendName = (TextView) findViewById(R.id.receive_notice_info_send_name);
		content = (TextView) findViewById(R.id.receive_notice_info_content);
		iconView = (MyGridView) findViewById(R.id.receive_notice_info_grid);
		sendTime = (TextView) findViewById(R.id.receive_notice_info_tiem);
		iconView.setOnItemClickListener(this);
		sendName.setOnClickListener(this);
		iconList = new ArrayList<String>();
	}

	private void initData() {
		notice = (Notice) getIntent().getSerializableExtra("notice");
		showData(notice);
	}

//	public void onEventMainThread(NoticeEvent event){
//			if(isActivity){
//			 switch (event.getEvent()) {
//			 case NOTICE_QUERY_BY_ID:
//				  notices = event.getNotices();
//				  if(notices != null && notices.size() > 0){
//					  showData(notices.get(0));
//					  showAndSaveLog(TAG, "获取通知详情成功   id = " + notices.get(0).getNoticeId(), false);
//				  }
//				 break;
//			 case NOTICE_INBOX_REQUEST_FAILED:
//				 showAndSaveLog(TAG, "获取通知详情失败   msg = " + event.getMsg(), false);
//				 break;
//			}
//		    }}
	
	public void showData(Notice mNotice){
		try {
			String json = mNotice.getAttachments();
			JSONArray array = new JSONArray(json);
			if(array.length() > 0){
				for(int i = 0; i < array.length(); i++){
					iconList.add(array.getJSONObject(i).getString("url"));
				}
				updateAdapter();
			}
			if(notice.getSendTime() != null){
				sendTime.setText(Utils.getDateTime(mContext, notice.getSendTime()));
			}
		sendName.setText(mNotice.getSenderName());
		
		content.setText(mNotice.getContent());
		} catch (Exception e) {
			sendName.setText(mNotice.getSenderName());
			content.setText(mNotice.getContent());
			e.printStackTrace();
		}
	}

	public void updateAdapter() {
		if (adapter == null) {
			adapter = new IconAdapter(mContext, iconList);
			iconView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	public class IconAdapter extends BaseAdapter {
		private Context context;
		private List<String> list;

		public IconAdapter(Context context, List<String> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.grideview_item1_layout, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (MyImageView) convertView
						.findViewById(R.id.grid_item1_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			 viewHolder.icon.setImageUrl(iconList.get(position) + SysConstants.Imgurlsuffix134, R.drawable.defal_down_proress,false);
			return convertView;
		}

	}

	public class ViewHolder {
		MyImageView icon;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NewPicActivity.invoke(NoticeInboxInfoActivity.this, iconList.get(position), true, iconList, false);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.receive_notice_info_send_name:
			if(notice != null){
				Intent intent = new Intent(mContext,ChatUserInfoParentActivity.class);
				intent.putExtra("userId", notice.getSenderUserId());
				startActivity(intent);
			}
			break;

		default:
			break;
		}
		super.onClick(v);
	}
}
