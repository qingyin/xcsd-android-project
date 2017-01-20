package com.xcsd.app.teacher.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.GardenMail;

import java.util.ArrayList;
import java.util.List;

public class LeaderMailBoxAdapter extends ArrayAdapter<GardenMail> {

	private Context mContext;
	private List<GardenMail> heys = new ArrayList<GardenMail>();

	public LeaderMailBoxAdapter(Context context, List<GardenMail> heys) {
		super(context, 0, heys);
		mContext = context;
		this.heys = heys;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView =  LayoutInflater.from(mContext).inflate(R.layout.listview_leader_mailbox_item_layout,null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (RoundImageView) convertView.findViewById(R.id.mailbox_icon);
			viewHolder.content = (TextView) convertView.findViewById(R.id.mailbox_content);
			viewHolder.time = (TextView) convertView.findViewById(R.id.mailbox_time);
			viewHolder.name = (TextView) convertView.findViewById(R.id.mailbox_name);
			viewHolder.isNew = (ImageView)convertView.findViewById(R.id.mailbox_isNew);
			viewHolder.line = convertView.findViewById(R.id.mailbox_line);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(heys.size() > 0){
			final GardenMail mail = heys.get(position);
			if(mail != null){
		
		viewHolder.content.setText(mail.getContent()==null?"":mail.getContent());
		viewHolder.name.setText(mail.getSenderName());
		if(mail.getSendTime() != null){
			viewHolder.time.setText(Utils.getDateTime(mContext, mail.getSendTime()));
		}
		if(mail.getAnonymous()){
			viewHolder.icon.setImageUrl("", R.drawable.defal_mail_icon);
		}else{
			viewHolder.icon.setImageUrl(mail.getSenderAvatar()+ SysConstants.Imgurlsuffix80, R.drawable.defal_mail_icon);
			
		}
		
		if(mail.getUpdated() != null && mail.getUpdated())
			viewHolder.isNew.setVisibility(View.VISIBLE);
		else
			viewHolder.isNew.setVisibility(View.GONE);
		
		if(position + 1 == heys.size()){
			viewHolder.line.setVisibility(View.GONE);
		}else{
			viewHolder.line.setVisibility(View.VISIBLE);
		}
		
			}
		}
		return convertView;
	}

	class ViewHolder {
		public RoundImageView icon;
		public TextView content;
		public TextView time;
		public TextView name;
		public ImageView isNew;
		View line ;
	}
	
	

}
