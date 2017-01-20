package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tuxing.app.easemob.util.SmileUtils;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Comment;

import java.util.List;

public class HeyInfoCommentAdapter extends BaseAdapter {
	private List<Comment> list = null;
	private Context mContext;
	
	public HeyInfoCommentAdapter(Context mContext, List<Comment> list) {
		this.mContext = mContext;
		this.list = list;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void setTeacherList(List<Comment> list){
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final Comment comment = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.hey_info_item_layout, null);
			viewHolder.icon = (RoundImageView) view.findViewById(R.id.comment_icon);
			viewHolder.name = (TextView) view.findViewById(R.id.comment_name);
			viewHolder.content = (TextView) view.findViewById(R.id.comment_content);
			viewHolder.time = (TextView) view.findViewById(R.id.comment_time);
			viewHolder.line = view.findViewById(R.id.comment_line);
			viewHolder.isNew = (ImageView) view.findViewById(R.id.comment_isnew);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.name.setText(comment.getSenderName());
		viewHolder.content.setText(SmileUtils.getSmiledText(mContext, comment.getContent()));
		if(comment.getSendTime() != null){
			viewHolder.time.setText(Utils.getDateTime(mContext, comment.getSendTime()));
		}
		
		
		
		if(position + 1 == list.size()){
			viewHolder.line.setVisibility(View.GONE);
		}else{
			viewHolder.line.setVisibility(View.VISIBLE);
		}
		
		return view;

	}
	


	final static class ViewHolder {
		RoundImageView icon;
		TextView name;
		TextView time;
		TextView content;
		View line;
		ImageView isNew;
	}





}