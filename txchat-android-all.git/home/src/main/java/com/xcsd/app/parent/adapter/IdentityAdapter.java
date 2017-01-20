package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.xcsd.app.parent.R;
import com.tuxing.sdk.modle.Relative;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityAdapter extends BaseAdapter{
	private List<Relative> list = null;
	private Context mContext;
	private HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	private Map<Integer,String> nameMap;
	private String name;
	 private int index = 100;
	public IdentityAdapter(Context mContext, List<Relative> list,Map<Integer,String> nameMap,String name) {
		this.mContext = mContext;
		this.list = list;
		this.nameMap = nameMap;
		this.name = name;
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
			viewHolder.select = (TextView) view.findViewById(R.id.identity_select);
			viewHolder.btn = (RadioButton) view.findViewById(R.id.identity_btn);
			viewHolder.line = view.findViewById(R.id.iden_line);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		Relative data = list.get(position);
		viewHolder.name.setText(nameMap.get(data.getRelativeType()));
		if(data.getUser() != null){
			if(name.equals(nameMap.get(data.getRelativeType()))){
				viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.skin_text1));
			}else{
				viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.gray));
			}
			viewHolder.btn.setVisibility(View.GONE);
			viewHolder.select.setVisibility(View.VISIBLE);
		}else{
			viewHolder.select.setVisibility(View.GONE);
			viewHolder.btn.setVisibility(View.VISIBLE);
			viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.black));
			
		}
		
		if(position == list.size() - 1){
			viewHolder.line.setVisibility(View.GONE);
		}else{
			viewHolder.line.setVisibility(View.VISIBLE);
			
		}
		
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
		TextView select;
		RadioButton btn;
		View line;
		
	}
	
	public int selectIndex(){
		return index;
		}
	

}