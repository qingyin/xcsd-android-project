package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.modle.CheckInCard;

import java.util.List;

public class CardNumberAdapter extends BaseAdapter{
	private List<CheckInCard> list = null;
	private Context mContext;
	private long userId;
	public CardNumberAdapter(Context mContext, List<CheckInCard> list,long userId) {
		this.mContext = mContext;
		this.list = list;
		this.userId = userId;
	}
	
	public void setList(List<CheckInCard> list){
		this.list = list;
	}
	
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void setTeacherList(List<CheckInCard> list){
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		CheckInCard card = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.listview_item4_layout, null);
			viewHolder.name = (TextView) view.findViewById(R.id.item4_name);
			viewHolder.cardNumber = (TextView) view.findViewById(R.id.item4_card_number);
			viewHolder.arrow = (ImageView) view.findViewById(R.id.item4_arrow);
			viewHolder.icon = (RoundImageView) view.findViewById(R.id.item4_icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.name.setText(card.getUserName());
		viewHolder.icon.setImageUrl(card.getAvatar()+ SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
		if(card.getUserId() != userId) {
			viewHolder.arrow.setVisibility(view.GONE);
		}else{
			viewHolder.arrow.setVisibility(view.VISIBLE);
		}
		if(!TextUtils.isEmpty(card.getCardNum())){
				viewHolder.cardNumber.setText(card.getCardNum());
			}else{
				viewHolder.cardNumber.setText(mContext.getString(R.string.no_bind));
			}
		return view;

	}
	


	public class ViewHolder {
		TextView name;
		TextView cardNumber;
		ImageView arrow;
		RoundImageView icon;
	}
	

}