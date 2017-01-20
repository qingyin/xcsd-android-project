package com.tuxing.app.adapter;

import java.util.List;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.bean.TestSecondBean;
import com.tuxing.app.util.ShapeUtil;
import com.tuxing.app.view.RotateTextView;

public class TestSecondAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	public List<TestSecondBean> listItem;

	private Context context;

	private int itemWidth;

	public TestSecondAdapter(Context context, List<TestSecondBean> listItem) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.listItem = listItem;
	}
	
	public void setListItem(List<TestSecondBean> listItem){
		this.listItem = listItem;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.test_second_item, null);
			holder = new ViewHolder();
			holder.test_second_name = (TextView) convertView
					.findViewById(R.id.test_second_name);
			holder.school_age = (RotateTextView) convertView
					.findViewById(R.id.school_age);
			holder.test_second_state = (ImageView) convertView
					.findViewById(R.id.test_second_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TestSecondBean test = listItem.get(position);
		holder.test_second_name.setBackgroundDrawable(ShapeUtil
				.getGradientTestDrawable(context, "#" + test.colorValue));
		holder.test_second_name.setText(test.name);
//		移植默认注掉
//		holder.school_age.setText(GuideHelper
//				.getCurrentBabySchoolAgeStr(context));
		holder.test_second_name.setLayoutParams(new RelativeLayout.LayoutParams(
				itemWidth, itemWidth));
		if(test.status==1){
			//已完成
			holder.test_second_state.setVisibility(View.VISIBLE);
		}else{
			holder.test_second_state.setVisibility(View.GONE);
		}

		return convertView;
	}

	final class ViewHolder {
		public TextView test_second_name;
		public RotateTextView school_age;
		public ImageView test_second_state;
	}

}
