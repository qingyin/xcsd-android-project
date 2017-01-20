package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tuxing.app.easemob.util.SmileUtils;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Comment;

import java.util.List;

public class LeaderMailboxInfoCommentAdapter extends BaseAdapter {
	private List<Comment> list = null;
	private Context mContext;
	private boolean isAnonymous;
	public LeaderMailboxInfoCommentAdapter(Context mContext, List<Comment> list,boolean isisAnonymous) {
		this.mContext = mContext;
		this.list = list;
		this.isAnonymous = isisAnonymous;
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

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final Comment comment = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.leader_maibox_info_item_layout, null);
			viewHolder.icon = (RoundImageView) view.findViewById(R.id.leader_mailbox_icon);
			viewHolder.name = (TextView) view.findViewById(R.id.leader_mailbox_name);
			viewHolder.content = (TextView) view.findViewById(R.id.leader_mailbox_content);
			viewHolder.time = (TextView) view.findViewById(R.id.leader_mailbox_time);
			viewHolder.line = view.findViewById(R.id.leader_mailbox_line);
			viewHolder.isNew = (ImageView) view.findViewById(R.id.leader_mailbox_isnew);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.content.setText(SmileUtils.getSmiledText(mContext, comment.getContent()));
		viewHolder.icon.setImageUrl(comment.getSenderAvatar()+ SysConstants.Imgurlsuffix90, R.drawable.default_avatar);
		if(comment.getSendTime() != null){
			viewHolder.time.setText(Utils.getDateTime(mContext, comment.getSendTime()));
		}
//		if(isAnonymous)
//		viewHolder.name.setText("匿名反馈");
//		else
			viewHolder.name.setText(comment.getSenderName());
			
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



	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


}