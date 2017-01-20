package com.xcsd.app.teacher.tea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.util.SideBar;
import com.xcsd.app.teacher.util.SideBar.OnTouchingLetterChangedListener;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.PinyinComparator;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.CharacterParser;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseReceiverUserActivity extends BaseActivity {
    private ListView teaListView;
    private CharacterParser characterParser;
    public SelectReceiverAdapter adapter;
    private Department department;
    private List<DepartmentMember> departmentMembers;//全部人员
    private String TAG = ChooseReceiverUserActivity.class.getSimpleName();
    private boolean isCheckAll;
    public static Button titleRight;
    private static int selectNumber = 0;
    private List<Long> orgDepartmentMember;
    private SideBar sideBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.tea_select_receiver_layout);
        init();
        initData();
    }

    private void init() {
        setLeftBack("取消", false, false);
        isCheckAll = getIntent().getBooleanExtra("isCheckAll", false);
        orgDepartmentMember = (List<Long>) getIntent().getSerializableExtra("memberUserIds");
        department = (Department) getIntent().getSerializableExtra("department");
        setTitle(department.getName());
        characterParser = CharacterParser.getInstance();
        teaListView = (ListView) findViewById(R.id.select_receiver_listview);
    	sideBar = (SideBar) findViewById(R.id.sidrbar);
    	//设置右侧触摸监听
    			sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

                    @Override
                    public void onTouchingLetterChanged(String s) {
                        // 该字母首次出现的位置
                        int position = adapter.getPositionForSection(s.charAt(0));
                        if (position != -1) {
                            teaListView.setSelection(position);
                        }
                    }
                });
    			
    }

    private void initData() {
        if (department.getType() == Constants.DEPARTMENT_TYPE.SCHOOL) {//如果是教师组
//        if (department.getType() == Constants.DEPARTMENT_TYPE.GARDEN) {//如果是教师组
            getService().getContactManager().getDepartmentMemberByUserType(department.getDepartmentId(), Constants.USER_TYPE.TEACHER);
        }else if (department.getType() == Constants.DEPARTMENT_TYPE.GARDEN){//幼儿园版老师type为1
            getService().getContactManager().getDepartmentMemberByUserType(department.getDepartmentId(), Constants.USER_TYPE.TEACHER);
        } else if (department.getType() == Constants.DEPARTMENT_TYPE.CLASS) {//班级组
            getService().getContactManager().getDepartmentMemberByUserType(department.getDepartmentId(), Constants.USER_TYPE.CHILD);
        }
    }

    public void onEventMainThread(GetDepartmentMemberEvent event) {
        //班级成员
        if (isActivity) {
            departmentMembers = event.getDepartmentMembers();
            removeMyself(user.getUserId(),departmentMembers);
            if (!CollectionUtils.isEmpty(departmentMembers)) {
                updateAdapter(departmentMembers);
            }
            showAndSaveLog(TAG, "获取本地的班级成员", false);
        }
    }

    private List<DepartmentMember> removeMyself(long my,List<DepartmentMember> department) {
        DepartmentMember dep = null;
        for(DepartmentMember depart:department){
            if(depart.getUser().getUserId()==user.getUserId()){
                dep = depart;
            }
        }
        if(dep!=null)
            department.remove(dep);
        return department;
    }

    /**
     * 显示数据
     *
     * @param list
     */
    public void updateAdapter(List<DepartmentMember> list) {
        Collections.sort(departmentMembers, new PinyinComparator());
        if (adapter == null) {
            adapter = new SelectReceiverAdapter(mContext, list, isCheckAll, orgDepartmentMember);
            teaListView.setAdapter(adapter);
        } else {
            adapter.setTeacherList(list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        Intent intent = new Intent(mContext, ChooseReceiverClassActivity.class);
        if (!CollectionUtils.isEmpty(adapter.getList())) {
            List<DepartmentMember> departmentMembers = new ArrayList<DepartmentMember>();
            for(Long id:adapter.getList()){
                DepartmentMember member = new DepartmentMember();
                User user = new User();
                user.setUserId(id);
                member.setUser(user);
                departmentMembers.add(member);
            }
            intent.putExtra("selectList", (Serializable)departmentMembers);
        } else
            intent.putExtra("selectList", new ArrayList<DepartmentMember>());
        if (adapter.getList() != null && departmentMembers.size() == adapter.getList().size()) {//如果是全选
            intent.putExtra("isSelectAll", true);
        } else
            intent.putExtra("isSelectAll", false);
        intent.putExtra("departmentId", department.getDepartmentId());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    class SelectReceiverAdapter extends BaseAdapter {
        private List<DepartmentMember> list = null;
        private Context mContext;
        private boolean isCheckAll;
        private CharacterParser characterParser;
        private List<Long> orgDepartList;
        private List<Long> selectIdList;

        public SelectReceiverAdapter(Context mContext, List<DepartmentMember> list, boolean isChecket, List<Long> orgList) {
            this.mContext = mContext;
            this.list = list;
            this.orgDepartList = orgList;
            this.isCheckAll = isChecket;
            characterParser = CharacterParser.getInstance();
            selectIdList = new ArrayList<Long>();
            addMap();
        }

        public void addMap() {
            if (isCheckAll) {
                for (int i = 0; i < list.size(); i++) {
                    selectIdList.add(list.get(i).getUser().getUserId());
                }
                setRightNext(true, "确定(" + list.size() + ")", 0);
            } else {
                if (!CollectionUtils.isEmpty(orgDepartList)) {
                    for (int i = 0; i < orgDepartList.size(); i++) {
                        selectIdList.add(orgDepartList.get(i));
                    }
                    setRightNext(true, "确定(" + orgDepartList.size() + ")", 0);
                }
            }
        }

        /**
         * 当ListView数据发生变化时,调用此方法来更新ListView
         *
         * @param list
         */
        public void setTeacherList(List<DepartmentMember> list) {
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
            final DepartmentMember info = list.get(position);
//		if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.tea_select_receiver_sort_item_layout, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.select_receiver_item_name);
            viewHolder.zimu = (TextView) view.findViewById(R.id.select_receiver_item_zimu);
            viewHolder.icon = (RoundImageView) view.findViewById(R.id.select_receiver_item_icon);
            viewHolder.cb = (CheckBox) view.findViewById(R.id.select_receiver_item_cb);
            view.setTag(info);
//			view.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) view.getTag();
//		}
            if(info.getUser().getUserId() == user.getUserId()){
            	 viewHolder.tvTitle.setVisibility(View.GONE);
            	 viewHolder.icon.setVisibility(View.GONE);
            	 viewHolder.cb.setVisibility(View.GONE);
            }
            viewHolder.icon.setImageUrl(info.getUser().getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            int section = getSectionForPosition(position);
            if (department.getType() == Constants.DEPARTMENT_TYPE.GARDEN) {
                viewHolder.zimu.setVisibility(View.GONE);
            } else {
                //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                if (position == getPositionForSection(section)) {
                    viewHolder.zimu.setVisibility(View.VISIBLE);
                    viewHolder.zimu.setText(setZmu(info.getUser().getNickname()));
                } else {
                    viewHolder.zimu.setVisibility(View.GONE);
                }
            }
            viewHolder.tvTitle.setText(info.getUser().getNickname());
            if (selectIdList.contains(info.getUser().getUserId()))
                viewHolder.cb.setChecked(true);
            else
                viewHolder.cb.setChecked(false);
            viewHolder.cb.setTag(info);
            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean ischeck) {
                    DepartmentMember in = (DepartmentMember) compoundButton.getTag();
                    if (ischeck) {
                        selectIdList.add(in.getUser().getUserId());
                        setRightNext(true, "确定(" + selectIdList.size() + ")", 0);
                    } else {
                        selectIdList.remove(in.getUser().getUserId());
                        setRightNext(true, "确定(" + selectIdList.size() + ")", 0);
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DepartmentMember in = (DepartmentMember) v.getTag();
                    if (!selectIdList.contains(in.getUser().getUserId())) {
                        selectIdList.add(in.getUser().getUserId());
                        setRightNext(true, "确定(" + selectIdList.size() + ")", 0);
                    } else {
                        selectIdList.remove(in.getUser().getUserId());
                        setRightNext(true, "确定(" + selectIdList.size() + ")", 0);
                    }
                    notifyDataSetChanged();
                }
            });
            return view;

        }

        public List<Long> getList() {
            return selectIdList;
        }


        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
//		return list.get(position).getUser().getUsername().charAt(0);
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

        public String setZmu(String name) {
            String zimu;

            // 正则表达式，判断首字母是否是英文字母
            if ("".equals(name) || name == null) {
                zimu = "#";
                return zimu;
            } else {
                String pinyin = characterParser.getSelling(name);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    zimu = sortString.toUpperCase();
                } else {
                    zimu = "#";
                }
            }
            return zimu;
        }
    }

    static class ViewHolder {
        RoundImageView icon;
        TextView zimu;
        TextView tvTitle;
        CheckBox cb;
    }

}
