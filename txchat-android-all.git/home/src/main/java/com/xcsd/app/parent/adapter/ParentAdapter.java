package com.xcsd.app.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.CharacterParser;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.modle.DepartmentMember;

import java.util.List;

public class ParentAdapter extends BaseExpandableListAdapter{
	private Context mContext;
	private int mGroupPosition = 0;
	private int mChildPosition = 0;
	private List<DepartmentMember> DepartmentMembers;
	private CharacterParser characterParser;

	public ParentAdapter(Context context, List<DepartmentMember> categoryList) {
		super();
		this.mContext = context;
		DepartmentMembers = categoryList;
		characterParser = CharacterParser.getInstance();
	}

	public void setSelcected(int groupPosition, int childPosition) {
		mGroupPosition = groupPosition;
		mChildPosition = childPosition;
	}

	public int getCurrentGroup() {
		return mGroupPosition;
	}

	public int getCurrentChild() {
		return mChildPosition;
	}

	@Override
	public int getGroupCount() {
		return DepartmentMembers.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return DepartmentMembers.get(groupPosition).getRelatives().size();
	}

	@Override
	public DepartmentMember getGroup(int groupPosition) {
		return DepartmentMembers.get(groupPosition);
	}

	@Override
	public User getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder viewHoler;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_patent_group_item_layout, null);
			viewHoler = new ViewHolder();
			viewHoler.user = (TextView) convertView.findViewById(R.id.parent_item_name);
			viewHoler.noUser = (TextView) convertView.findViewById(R.id.parent_no_item_name);
			viewHoler.childRl = (RelativeLayout) convertView.findViewById(R.id.have_child_rl);
			viewHoler.noChildRl = (RelativeLayout) convertView.findViewById(R.id.no_child_rl);
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolder) convertView.getTag();
		}
		
		DepartmentMember classes = DepartmentMembers.get(groupPosition);
		int section = getSectionForPosition(groupPosition);
		if(classes.getRelatives() != null && classes.getRelatives().size() > 0){
			viewHoler.childRl.setVisibility(View.VISIBLE);
			viewHoler.noChildRl.setVisibility(View.GONE);
			viewHoler.user.setText(classes.getUser().getNickname());
		}else{
			viewHoler.childRl.setVisibility(View.GONE);
			viewHoler.noChildRl.setVisibility(View.VISIBLE);
			viewHoler.noUser.setText(classes.getUser().getNickname() + "未绑定家长");
			
		}
		
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder viewHoler;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_parent_item_layout, null);
			viewHoler = new ViewHolder();
			viewHoler.head = (RoundImageView) convertView.findViewById(R.id.parent_item_icon);
			viewHoler.user = (TextView) convertView.findViewById(R.id.parent_item_name);
			viewHoler.no_active = (TextView) convertView.findViewById(R.id.parent_item_noative);
			convertView.setTag(viewHoler);
		}
		viewHoler = (ViewHolder) convertView.getTag();
		if (DepartmentMembers.get(groupPosition).getRelatives() != null) {
			User user = DepartmentMembers.get(groupPosition).getRelatives().get(childPosition);
			viewHoler.user.setText(user.getNickname());
			viewHoler.head.setImageUrl(user.getAvatar()+ SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
			if(user.getActivated() != null && user.getActivated()){
				viewHoler.no_active.setVisibility(View.GONE);
			}else{
				viewHoler.no_active.setVisibility(View.VISIBLE);
			}
		} 
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public class ViewHolder {
		public RoundImageView head;
		public TextView user;
		public TextView no_active;
		public TextView noUser;
		public TextView zimu;
		public RelativeLayout childRl;
		public RelativeLayout noChildRl;
	}

	public void setDepartmentMemberList(List<DepartmentMember> DepartmentMemberList) {
		DepartmentMembers = DepartmentMemberList;
	}
	
	
	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return setZmu(DepartmentMembers.get(position).getUser().getRealname()).charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getGroupCount(); i++) {
			String sortStr = setZmu(DepartmentMembers.get(i).getUser().getRealname());
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
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
