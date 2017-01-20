package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xcsd.app.parent.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.CharacterParser;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.modle.DepartmentMember;

import java.util.List;

//import com.xcsd.rpc.proto.HomeworkProto;

public class TeacherAdapter extends BaseAdapter implements SectionIndexer{
	private List<DepartmentMember> list = null;
	private Context mContext;
	private CharacterParser characterParser;

	private HttpClient client = HttpClient.getInstance();
	
	public TeacherAdapter(Context mContext, List<DepartmentMember> list) {
		this.mContext = mContext;
		this.list = list;
		characterParser = CharacterParser.getInstance();
//       HomeworkProto.HomeworkListRequest.Builder requst=HomeworkProto.HomeworkListRequest.newBuilder();
//
//       requst.setMaxId(1).setSinceId(2).build();
		//client.sendRequest("", HomeworkProto.HomeworkListRequest)
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void setTeacherList(List<DepartmentMember> list){
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
		final DepartmentMember teacherInfo = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.listview_sort_item_layout, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.sorrt_item_name);
			viewHolder.zimu = (TextView) view.findViewById(R.id.sorrt_zimu);
			viewHolder.no_activity = (TextView) view.findViewById(R.id.sorrt_item_no_active);
			viewHolder.icon = (RoundImageView) view.findViewById(R.id.sorrt_icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
//		if(!TextUtils.isEmpty(teacherInfo.getUser().getNickname())){
		//根据position获取分类的首字母的Char ascii值
//		int section = getSectionForPosition(position);
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
//			if(position == getPositionForSection(section)){
//				viewHolder.zimu.setVisibility(View.VISIBLE);
//				viewHolder.zimu.setText(setZmu(teacherInfo.getUser().getNickname()));
//			}else{
//				viewHolder.zimu.setVisibility(View.GONE);
//			}
//		}
		viewHolder.zimu.setVisibility(View.GONE);
	
		viewHolder.icon.setImageUrl(list.get(position).getUser().getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
		viewHolder.tvTitle.setText(this.list.get(position).getUser().getNickname());
        boolean activated = list.get(position).getUser().getActivated() != null ?
                list.get(position).getUser().getActivated() : false;
		if(activated){
			viewHolder.no_activity.setVisibility(View.GONE);
		}else{
			viewHolder.no_activity.setVisibility(View.VISIBLE);
		}
		
		return view;

	}
	


	final static class ViewHolder {
		TextView zimu;
		TextView no_activity;
		TextView tvTitle;
		RoundImageView icon;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return setZmu(list.get(position).getUser().getNickname()).charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = setZmu(list.get(i).getUser().getNickname());
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	

	@Override
	public Object[] getSections() {
		return null;
	}
	
	public String setZmu(String name){
		String zimu;
		
		// 正则表达式，判断首字母是否是英文字母
		if("".equals(name) || name == null){
			zimu = "#";
			return zimu;
		}else{
			String pinyin = characterParser.getSelling(name);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if(sortString.matches("[A-Z]")){
				zimu = sortString.toUpperCase();
			}else{
				zimu = "#";
			}
		}
		return zimu;
	}
	
}