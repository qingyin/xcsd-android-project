package com.tuxing.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.view.RoundAngleImageView;
import com.tuxing.sdk.db.entity.ContentItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class HomeActicityAdapter extends BaseAdapter {

	private Context mContext;
	private List<ContentItem> contentDatas;
	View view = null;

	public HomeActicityAdapter(Context mContext, List<ContentItem> contentDatas) {
		this.mContext = mContext;
		this.contentDatas = contentDatas;
	}

	@Override
	public int getCount() {
		return contentDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return contentDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup mParent) {
		ViewHoler viewHolter = null;
		if (contentView == null) {
			viewHolter = new ViewHoler();
			contentView = LayoutInflater.from(mContext).inflate(R.layout.home_acticity_item, null);
			viewHolter.icon = (RoundAngleImageView) contentView.findViewById(R.id.activity_icon);
			viewHolter.isiconRl = (RelativeLayout) contentView.findViewById(R.id.activity_isicon_rl);
			viewHolter.isincoTitle = (TextView) contentView.findViewById(R.id.activity_isicon_title);
			viewHolter.isiconSum = (TextView) contentView.findViewById(R.id.activity_isicon_sum);
			viewHolter.isiconTime = (TextView) contentView.findViewById(R.id.activity_isicon_time);
			
			viewHolter.line = contentView.findViewById(R.id.activity_line);
			viewHolter.noiconLl = (LinearLayout) contentView.findViewById(R.id.activity_noicon_ll);
			viewHolter.noincoTitle = (TextView) contentView.findViewById(R.id.activity_title);
			viewHolter.noiconSum = (TextView) contentView.findViewById(R.id.activity_sum);
			viewHolter.noiconTime = (TextView) contentView.findViewById(R.id.activity_time);
			
			contentView.setTag(viewHolter);
		} else {
			viewHolter = (ViewHoler) contentView.getTag();
		}
		if(contentDatas.size() > 0){
		ContentItem data = contentDatas.get(position);
		if(data != null){
		
		String imageUrl = data.getCoverImageUrl();
		String dataTime = "";
		if(data.getPublishTime() != null){
			 dataTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getPublishTime()));
		}
		if(TextUtils.isEmpty(imageUrl)){
			//无图
		viewHolter.isiconRl.setVisibility(View.GONE);
		viewHolter.noiconLl.setVisibility(View.VISIBLE);
		viewHolter.noincoTitle.setText(data.getTitle());
		viewHolter.noiconSum.setText(data.getSummary());
		viewHolter.noiconTime.setText(dataTime);
			
		}else{
			// 有图
			viewHolter.isiconRl.setVisibility(View.VISIBLE);
			viewHolter.noiconLl.setVisibility(View.GONE);
			
			viewHolter.icon.setImageUrl(imageUrl, R.drawable.default_image);
			viewHolter.isincoTitle.setText(data.getTitle());
			viewHolter.isiconSum.setText(data.getSummary());
			viewHolter.isiconTime.setText(dataTime);
		}
		
		if((position + 1) == contentDatas.size())
			viewHolter.line.setVisibility(View.GONE);
		else
			viewHolter.line.setVisibility(View.VISIBLE);
		}
		}
		return contentView;
	}

	public class ViewHoler {
		RoundAngleImageView icon;
		TextView noincoTitle;
		TextView noiconSum;
		TextView noiconTime;
		TextView isincoTitle;
		TextView isiconSum;
		TextView isiconTime;
		LinearLayout noiconLl;
		RelativeLayout isiconRl;
		View line;
	}
	
	
	
}
