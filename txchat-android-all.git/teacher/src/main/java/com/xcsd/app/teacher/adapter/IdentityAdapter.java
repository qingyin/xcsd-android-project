package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.modle.Relative;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityAdapter extends BaseAdapter{
	private List<Relative> list = null;
	private Context mContext;
	private HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	private Map<Integer,String> nameMap;
	 private int index = 100;
	public IdentityAdapter(Context mContext, List<Relative> list,Map<Integer,String> nameMap) {
		this.mContext = mContext;
		this.list = list;
		this.nameMap = nameMap;
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
		final Relative LoginUser = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.identity_item, null);
			viewHolder.name = (TextView) view.findViewById(R.id.identity_name);
			viewHolder.btn = (RadioButton) view.findViewById(R.id.identity_btn);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		Relative data = list.get(position);
		viewHolder.name.setText(nameMap.get(data.getRelativeType()));
		if(data.getUser() != null)
			viewHolder.btn.setVisibility(View.GONE);
		else
			viewHolder.btn.setVisibility(View.VISIBLE);
		
		final RadioButton radio=(RadioButton) view.findViewById(R.id.identity_btn);
		viewHolder.btn = radio;  
		
		viewHolder.btn.setOnClickListener(new View.OnClickListener() {
		      public void onClick(View v) {
		        for (String key : states.keySet()) {
		          states.put(key, false);
		        }
		        states.put(String.valueOf(position), radio.isChecked());
		        index = position;
		        IdentityAdapter.this.notifyDataSetChanged();
		      }
		    });

		    boolean res = false;
		    if (states.get(String.valueOf(position)) == null
		        || states.get(String.valueOf(position)) == false) {
		      res = false;
		      states.put(String.valueOf(position), false);
		    } else
		      res = true;

		    viewHolder.btn.setChecked(res);
		
		return view;

	}
	

	public class ViewHolder {
		TextView name;
		RadioButton btn;
	}
	
	public int selectIndex(){
		return index;
		}
	

}