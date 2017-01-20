package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tuxing.app.activity.NewPicActivity;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.CheckInRecord;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 刷卡列表
 *
 */
public class CardAdapter extends ArrayAdapter<CheckInRecord> {
	
	private Context context;
	private List<CheckInRecord> list;
	private String TAG = CardAdapter.class.getSimpleName();
	
	public CardAdapter(Context context, List<CheckInRecord> msgs){
		super(context, 0, msgs);
		this.context = context;
		this.list = msgs;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_card_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.card_item = (LinearLayout) convertView.findViewById(R.id.card_item);
			viewHolder.card_current_item = (LinearLayout) convertView.findViewById(R.id.card_current_item);
			viewHolder.yearMonthDay = (TextView) convertView.findViewById(R.id.home_card_year_month_day);
			viewHolder.time = (TextView) convertView.findViewById(R.id.home_card_time);
			viewHolder.week = (TextView) convertView.findViewById(R.id.home_card_week);
			viewHolder.head = (MyImageView) convertView.findViewById(R.id.home_card_head);
			viewHolder.cardName = (TextView) convertView.findViewById(R.id.home_card_name);
			viewHolder.cardNumber = (TextView) convertView.findViewById(R.id.card_number);
			viewHolder.cardClassName = (TextView) convertView.findViewById(R.id.home_card_class);
			
			viewHolder.current_yearMonthDay = (TextView) convertView.findViewById(R.id.home_card_current_year_month_day);
			viewHolder.current_time = (TextView) convertView.findViewById(R.id.home_current_card_time);
			viewHolder.current_week = (TextView) convertView.findViewById(R.id.home_current_card_week);
			viewHolder.current_head = (MyImageView) convertView.findViewById(R.id.home_current_card_head);
			viewHolder.current_cardName = (TextView) convertView.findViewById(R.id.home_current_card_name);
			viewHolder.current_cardNumber = (TextView) convertView.findViewById(R.id.card_current_number);
			viewHolder.current_cardClassName = (TextView) convertView.findViewById(R.id.home_current_card_class);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		CheckInRecord info =  list.get(position);
		String timeStame = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(Long.valueOf(info.getCheckInTime())));
		String currentStame = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(System.currentTimeMillis()));
		if(currentStame.split(" ")[0].equals(timeStame.split(" ")[0])){
		viewHolder.card_current_item.setVisibility(View.VISIBLE);
		viewHolder.card_item.setVisibility(View.GONE);
		try {
			viewHolder.current_time.setText(timeStame.split(" ")[1]);
			viewHolder.current_yearMonthDay.setText(timeStame.split(" ")[0]);
			viewHolder.current_week.setText(Utils.getWeekOfDate(timeStame.split(" ")[0], "yyyy年MM月dd日"));
			String json = info.getSnapshots();
			JSONArray array = new JSONArray(json);
			if(array.length() > 0){
				final String url = array.getJSONObject(0).getString("url");
				viewHolder.current_head.setImageUrl(url + SysConstants.Imgurlsuffix134, R.drawable.defal_down_lym_proress,true);
				viewHolder.current_head.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						NewPicActivity.invoke(context, url, true);
					}
				});
			}
			viewHolder.current_cardName.setText(info.getParentName());
			viewHolder.current_cardClassName.setText(info.getClassName());
			viewHolder.current_cardNumber.setText(info.getCardNum());
		} catch (JSONException e) {
			MyLog.getLogger(TAG).d("显示刷卡信息出错 msg = " + e.toString());
			e.printStackTrace();
		}catch (Exception e) {
			MyLog.getLogger(TAG).d("显示刷卡信息出错 msg = " + e.toString());
			e.printStackTrace();
		}
		}else{
			viewHolder.card_current_item.setVisibility(View.GONE);
			viewHolder.card_item.setVisibility(View.VISIBLE);
			try {
				viewHolder.time.setText(timeStame.split(" ")[1]);
				viewHolder.yearMonthDay.setText(timeStame.split(" ")[0]);
				viewHolder.week.setText(Utils.getWeekOfDate(timeStame.split(" ")[0], "yyyy年MM月dd日"));
					String json = info.getSnapshots();
					JSONArray array = new JSONArray(json);
					if(array.length() > 0){
						final String url =  array.getJSONObject(0).getString("url");
						viewHolder.head.setImageUrl( url + SysConstants.Imgurlsuffix134, R.drawable.defal_down_lym_proress,true);
						viewHolder.head.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								NewPicActivity.invoke(context, url, true);
							}
						});
					}
					viewHolder.cardName.setText(info.getParentName());
					viewHolder.cardClassName.setText(info.getClassName());
					viewHolder.cardNumber.setText(info.getCardNum());
				} catch (JSONException e) {
					MyLog.getLogger(TAG).d("显示刷卡信息出错 msg = " + e.toString());
					e.printStackTrace();
				}catch (Exception e) {
					MyLog.getLogger(TAG).d("显示刷卡信息出错 msg = " + e.toString());
					e.printStackTrace();
				}
			
			
		}
		
		
		return convertView;
	}
	
	public class ViewHolder {
		public MyImageView head;
		public TextView cardName;
		public TextView cardNumber;
		public TextView cardClassName;
		public TextView yearMonthDay;
		public TextView time;
		public TextView week;
		public MyImageView current_head;
		public TextView current_cardName;
		public TextView current_cardNumber;
		public TextView current_cardClassName;
		public TextView current_yearMonthDay;
		public TextView current_time;
		public TextView current_week;
		public LinearLayout card_item;
		public LinearLayout card_current_item;
	}
	
	
	
	
	
	

}
