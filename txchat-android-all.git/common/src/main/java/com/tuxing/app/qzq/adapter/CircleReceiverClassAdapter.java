package com.tuxing.app.qzq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Department;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CircleReceiverClassAdapter extends BaseAdapter {

	private Context mContext;
	private List<Department> classes = new ArrayList<Department>();
	private Map<Integer,Boolean> selectCb;
	private ShowNumber showNumbr;
	private  int num = 0;
	public CircleReceiverClassAdapter(Context context,List<Department> classList,ShowNumber showNumbr,Map<Integer,Boolean> select){
		this.mContext = context;
		this.classes = classList;
		this.showNumbr = showNumbr;
		this.selectCb = select;
		if(selectCb.size()>0){
			for(Map.Entry<Integer,Boolean> map:selectCb.entrySet()){
				if(map.getValue()){
					num++;
				}
			}
		}
		showNumbr.refeshNumber(num);
	}
	public  void setData(Map<Integer,Boolean> map){
		this.selectCb = map;
		notifyDataSetChanged();
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return classes.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return classes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView className;
		RoundImageView icon;
		CheckBox cb;
		convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_circle_receiver_class_item,null);
		className = (TextView) convertView.findViewById(R.id.select_receiver_class_item_name);
		icon = (RoundImageView) convertView.findViewById(R.id.select_receiver_class_item_icon);
		cb = (CheckBox) convertView.findViewById(R.id.select_receiver_class_item_cb);
		className.setText(classes.get(position).getName());
		icon.setImageUrl(classes.get(position).getAvatar(), R.drawable.default_avatar);
		if (selectCb.get(position))
			cb.setChecked(true);
		else
			cb.setChecked(false);

		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if(isChecked){
					selectCb.put(position, true);
					showNumbr.refeshNumber(++num);
				}else{
					selectCb.put(position, false);
					showNumbr.refeshNumber(--num);
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}
	public  Map<Integer, Boolean> getMap(){
		return selectCb;
	}

	public void setMap(Map<Integer,Boolean> map){
		this.selectCb = map;

	}


	public interface ShowNumber{
		public void refeshNumber(int number);
	}

}
