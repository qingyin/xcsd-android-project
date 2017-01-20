package com.xcsd.app.parent.activity;

import android.content.Context;
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
import com.xcsd.app.parent.adapter.HeyInfoCommentAdapter;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.MyListView;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.FeedMedicineTask;
import com.tuxing.sdk.event.CommentEvent;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeyInfoActivity extends BaseActivity implements
		OnItemClickListener {
	private ArrayList<String> iconList;
	private List<Comment> comments;
	private List<Comment> tempList;
	private IconAdapter adapter;
	private HeyInfoCommentAdapter commentAdapter;
	private MyGridView iconView;
	private TextView mAndD;
	private TextView week;
	private TextView content;
	private TextView hey_msg;
	private MyListView commentView;
	private String TAG = HeyInfoActivity.class.getSimpleName();
	private FeedMedicineTask feedData;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.home_hey_info_layout);
		init();
		initData();
	}

	private void init() {
		setTitle(getString(R.string.eat_drug));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		mAndD = (TextView) findViewById(R.id.hey_info_moth_day);
		week = (TextView) findViewById(R.id.hey_info_week);
		commentView = (MyListView) findViewById(R.id.hey_info_list);
		content = (TextView) findViewById(R.id.hey_info_content);
		hey_msg = (TextView) findViewById(R.id.hey_msg);
		iconView = (MyGridView) findViewById(R.id.hey_info_grid);
		iconView.setOnItemClickListener(this);
		content.setFocusable(true);
		 iconList = new ArrayList<String>();
		comments = new ArrayList<Comment>();
		tempList = new ArrayList<Comment>();
	}

	private void initData() {
		// TODO 获取喂药详情
		FeedMedicineTask feedData = (FeedMedicineTask) getIntent().getSerializableExtra("feed");
		try {
		JSONArray array = new JSONArray(feedData.getAttachments());
		for(int i = 0; i < array.length(); i++){
			iconList.add(array.getJSONObject(i).getString("url"));
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		updateAdapter();
		content.setText(feedData.getDescription());
		String beginDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(feedData.getBeginDate()));
		mAndD.setText(beginDate.split("-")[1] + "-" + beginDate.split("-")[2]);
		week.setText(Utils.getWeekOfDate(beginDate, "yyyy-MM-dd"));
		
		getService().getFeedManager().getCommentsForFeedMedicineTask(feedData.getTaskId());
		
	}
	
	 public void onEventMainThread(CommentEvent event){
		 if(isActivity){
			 switch (event.getEvent()){
			 case GET_FEED_MEDICINE_TASK_COMMENTS_SUCCESS:
				 tempList = event.getComments();
				if(tempList != null && tempList.size() > 0){
					comments.addAll(tempList);
					updateCommentAdapter();
					hey_msg.setVisibility(View.GONE);
				}else{
					hey_msg.setVisibility(View.VISIBLE);
				}
				 showAndSaveLog(TAG, "获取喂药评论列表成功 size = " + comments.size(), false);
				 break;
			 case GET_FEED_MEDICINE_TASK_COMMENTS_FAILED:
				 showAndSaveLog(TAG, "获取喂药评论列表失败"+ event.getMsg(), false);
				 break;
			 }
		 } }

	

	public void updateAdapter() {
		if (adapter == null) {
			adapter = new IconAdapter(mContext, iconList);
			iconView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}
	public void updateCommentAdapter() {
		if (commentAdapter == null) {
			commentAdapter = new HeyInfoCommentAdapter(mContext, comments);
			commentView.setAdapter(commentAdapter);
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
			// TODO Auto-generated method stub
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
		// TODO
		NewPicActivity.invoke(HeyInfoActivity.this, iconList.get(position), true, iconList, false);
	}
	
	
}
