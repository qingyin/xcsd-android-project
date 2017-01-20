package com.xcsd.app.teacher.tea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.modle.NoticeDepartmentReceiver;

import java.util.List;

public class NoticeOutboxInfoClassAdapter extends BaseAdapter {
	private List<NoticeDepartmentReceiver> list = null;
	private Context mContext;
	private List<Department> departments;
	
	public NoticeOutboxInfoClassAdapter(Context mContext, List<NoticeDepartmentReceiver> list) {
		this.mContext = mContext;
		this.list = list;
		departments = CoreService.getInstance().getContactManager().getAllDepartment();
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void setTeacherList(List<NoticeDepartmentReceiver> list){
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
		final NoticeDepartmentReceiver info = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.tea_send_notice_info_item_layout, null);
			viewHolder.icon = (RoundImageView) view.findViewById(R.id.send_notice_info_item_icon);
			viewHolder.className = (TextView) view.findViewById(R.id.send_notice_info_item_name);
			viewHolder.readNum = (TextView) view.findViewById(R.id.no_read);
			for(Department department:departments){
				if(department.getDepartmentId()==list.get(position).getDepartmentId()){
					viewHolder.icon.setImageUrl(department.getAvatar()+ SysConstants.Imgurlsuffix90, R.drawable.ic_default_class);
				}
			}
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.className.setText(info.getDepartmentName());
		viewHolder.readNum.setText(String.valueOf(info.getMemberCount()-info.getReadCount()));
		return view;

	}
	


	final static class ViewHolder {
		RoundImageView icon;
		TextView className;
		TextView readNum;
	}



	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


}