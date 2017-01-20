package com.tuxing.app.ui.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.sdk.utils.Constants;

public class PopDialogAdapter extends BaseAdapter {

	private CharSequence[] _items;
	private int select;
	private boolean showChk = true;
	private Context mContext;

	public PopDialogAdapter(Context context, CharSequence[] items, int selectIdx) {
		_items = items;
		this.select = selectIdx;
		this.mContext = context;
	}

	public PopDialogAdapter(Context context, CharSequence[] items) {
		_items = items;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return _items.length;
	}

	@Override
	public String getItem(int i) {
		return _items[i].toString();// super.getItem(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewgroup) {
		DialogHolder vh = null;
		if (view == null) {
			vh = new DialogHolder();
			view = LayoutInflater.from(viewgroup.getContext()).inflate(
					R.layout.list_cell_dialog_pop, null, false);
			vh.titleTv = (TextView) view.findViewById(R.id.title_tv);
			vh.divider = view.findViewById(R.id.list_divider);
			vh.checkIv = (RadioButton) view.findViewById(R.id.rb_select);
			view.setTag(vh);
		} else {
			vh = (DialogHolder) view.getTag();
		}
		vh.titleTv.setText(getItem(i));
		
		if (i == -1 + getCount()) {
			vh.divider.setVisibility(View.GONE);
		} else {
			vh.divider.setVisibility(View.VISIBLE);
		}
		
		if(showChk) {
			vh.checkIv.setVisibility(View.VISIBLE);
			if (select == i) {
				vh.checkIv.setChecked(true);
			} else {
				vh.checkIv.setChecked(false);
			}
		} else{
			vh.checkIv.setVisibility(View.GONE);
		}

		Resources resource = (Resources) mContext.getResources();
		ColorStateList white = (ColorStateList) resource.getColorStateList(R.color.white);
		ColorStateList black = (ColorStateList) resource.getColorStateList(R.color.black);

		if (i == select) {
			if(i==0){
				if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
					view.setBackgroundResource(R.drawable.shape_pop_top_on);
				}else if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
					view.setBackgroundResource(R.drawable.shape_pop_top_on_p);
				}
			}else if(i == -1 + getCount()){
				if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
					view.setBackgroundResource(R.drawable.shape_pop_bottom_on);
				}else if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
					view.setBackgroundResource(R.drawable.shape_pop_bottom_on_p);
				}
			}else{
				if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
					view.setBackgroundColor(mContext.getResources().getColor(R.color.text_teacher));
				}else if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
					view.setBackgroundColor(mContext.getResources().getColor(R.color.text_parent));
				}
			}
			if(white!=null){
				vh.titleTv.setTextColor(white);
			}
		} else {
			if(i==0){
				view.setBackgroundResource(R.drawable.shape_pop_top);
			}else if(i == -1 + getCount()){
				view.setBackgroundResource(R.drawable.shape_pop_bottom);
			}else{
				view.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			}
			if(black!=null){
				vh.titleTv.setTextColor(black);
			}
		}
		return view;
	}

	public boolean isShowChk() {
		return showChk;
	}

	public void setShowChk(boolean showChk) {
		this.showChk = showChk;
	}
	private class DialogHolder {
		public View divider;
		public TextView titleTv;
		public RadioButton checkIv;
	}
}
