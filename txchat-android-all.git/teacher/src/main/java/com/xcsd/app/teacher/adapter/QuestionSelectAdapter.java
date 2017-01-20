package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xcsd.app.teacher.activity.QuestionSelectActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.modle.QuestionTag;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionSelectAdapter extends BaseAdapter{
	private List<QuestionTag> datas = new ArrayList<>();
	private Context mContext;
	private int index = 100;
	private QuestionSelectActivity activity;
	private HashMap<Integer, Boolean> states = new HashMap<Integer, Boolean>();
	public QuestionSelectAdapter(Context mContext, List<QuestionTag> datas,QuestionSelectActivity activity,int selectIndex) {
		this.mContext = mContext;
		this.activity = activity;
        if(!CollectionUtils.isEmpty(datas)) {
            this.datas = datas;
        }else {
            this.datas = new ArrayList<>();
        }
		this.index = selectIndex;
		if(index != 100){
			states.put(index,true);
		}
	}



	public int getCount() {
		return datas.size();
	}

	public Object getItem(int position) {
		return datas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View contentView, ViewGroup arg2) {
		 ViewHolder viewHolder = null;
		final QuestionTag data = datas.get(position);
		if (contentView == null) {
			viewHolder = new ViewHolder();
			contentView = LayoutInflater.from(mContext).inflate(R.layout.quesrion_select_item, null);
			viewHolder.btn = (RadioButton) contentView.findViewById(R.id.select_name);
            viewHolder.icon = (TextView)contentView.findViewById(R.id.select_icon);
			contentView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) contentView.getTag();
		}
        viewHolder.btn.setTag(position);
        viewHolder.icon.setTag(position);

		viewHolder.btn.setText(data.getName());
		final RadioButton radio=(RadioButton) contentView.findViewById(R.id.select_name);
		viewHolder.btn = radio;
		viewHolder.btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for (Integer key : states.keySet()) {
					states.put(key, false);
				}
				states.put(position, radio.isChecked());
				index = position;
				notifyDataSetChanged();
				activity.onclickSelect();
			}
		});

		if (states.get(position) == null
				|| states.get(position) == false) {
            viewHolder.icon.setVisibility(View.GONE);
			states.put((position), false);
		} else{
            viewHolder.icon.setVisibility(View.VISIBLE);
        }

//		viewHolder.btn.setChecked(res);

		return contentView;

	}
	

	public class ViewHolder {
		RadioButton btn;
        TextView icon;
	}
	
public int getIndex(){
	return index;
}

}