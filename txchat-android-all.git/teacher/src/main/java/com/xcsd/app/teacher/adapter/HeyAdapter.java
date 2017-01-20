package com.xcsd.app.teacher.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.FeedMedicineTask;

import java.util.ArrayList;
import java.util.List;

public class HeyAdapter extends ArrayAdapter<FeedMedicineTask> {

	private Context mContext;
	private List<FeedMedicineTask> feeds = new ArrayList<FeedMedicineTask>();
	
	
	


	public HeyAdapter(Context context, List<FeedMedicineTask> feeds) {
		super(context, 0, feeds);
		mContext = context;
		this.feeds = feeds;
	}
	
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView =  LayoutInflater.from(mContext).inflate(R.layout.listview_hey_item_layout,null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (RoundImageView) convertView.findViewById(R.id.ivUserhead);
			viewHolder.content = (TextView) convertView.findViewById(R.id.tvChatcontent);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
			viewHolder.time = (TextView) convertView.findViewById(R.id.tvSendtime);
			viewHolder.isNew = (ImageView)convertView.findViewById(R.id.tvHintNum);
			viewHolder.line = convertView.findViewById(R.id.item_line);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(feeds.size() > 0){
			final FeedMedicineTask feed = feeds.get(position);
		if(feed != null){
			viewHolder.icon.setImageUrl(feed.getSenderAvatar(), R.drawable.default_image);
		viewHolder.content.setText(feed.getDescription());
		viewHolder.name.setText(feed.getSenderName());
		if (feed.getSendTime() != null) {
			viewHolder.time.setText(Utils.getDateTime(mContext, feed.getSendTime()));
		}
		if(feed.getUpdated() != null && feed.getUpdated() )
			viewHolder.isNew.setVisibility(View.VISIBLE);
		else
			viewHolder.isNew.setVisibility(View.GONE);
		
		if(position + 1 == feeds.size()){
			viewHolder.line.setVisibility(View.GONE);
		}else{
			viewHolder.line.setVisibility(View.VISIBLE);
		}
		}}
		return convertView;
	}

	class ViewHolder {
		public RoundImageView icon;
		public TextView content;
		public TextView name;
		public TextView time;
		public ImageView isNew;
		View line ;
	}

}
