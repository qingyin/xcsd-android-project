package com.xcsd.app.teacher.tea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.activity.BanManagerActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.CharacterParser;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.event.MuteEvent;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;

import java.io.Serializable;
import java.text.Collator;
import java.util.*;

/**
 * 选择要禁止的用户
 *
 * @author Administrator
 */
public class ChooseUserActivity extends BaseActivity {

    private ListView lv_choose_user;
    private Map<Long, User> mSelectMap = new HashMap<Long, User>();
    private SelectReceiverAdapter adapterChild;
    private long departmentId;
    private List<User> muteUsers = new ArrayList<User>();
    private List<Long> muteUserIds;
    private List<Long> orgmuteUserIds;
    int muteTpye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.choose_user_layout);
        setTitle("选择人员");
        setLeftBack("", true, false);
        departmentId = getIntent().getLongExtra("departmentId", -1);
        muteUsers = (List<User>) getIntent().getSerializableExtra("muteUsers");
        muteTpye = (int)getIntent().getIntExtra("muteType",0);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
        setRightNext(true, "", 0);
        muteUserIds = new ArrayList<Long>();
        orgmuteUserIds = new ArrayList<Long>();
        if (!CollectionUtils.isEmpty(muteUsers)) {
//            setRightNext(true, "确定(" + muteUsers.size() + ")", 0);
            for (User u : muteUsers) {
                orgmuteUserIds.add(u.getUserId());
//                mSelectMap.put(u.getUserId(), u);
//                muteUserIds.add(u.getUserId());
            }
        }
        initView();
        initData();

    }

    private void initData() {
        getService().getContactManager().getDepartmentMemberByUserType(departmentId, Constants.USER_TYPE.CHILD);
    }

    public void onEventMainThread(GetDepartmentMemberEvent event) {
        disProgressDialog();
        switch (event.getEvent()) {
            case QUERY_BY_TYPE:
                if (event.getUserType() == Constants.USER_TYPE.CHILD) {
                    final Comparator comparator = Collator.getInstance(Locale.SIMPLIFIED_CHINESE);
                    Collections.sort(event.getDepartmentMembers(), new Comparator<DepartmentMember>() {
                        @Override
                        public int compare(DepartmentMember departmentMember, DepartmentMember t1) {
                            return comparator.compare(departmentMember.getUser().getNickname().toLowerCase(), t1.getUser().getNickname().toLowerCase());
                        }
                    });
                    List<User> users = new ArrayList<User>();
                    for (DepartmentMember member : event.getDepartmentMembers()) {
                        //排除已被禁言的用户
                        if(!orgmuteUserIds.contains(member.getUser().getUserId())){
                            users.add(member.getUser());
                        }
                    }
                    showData(users);
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        lv_choose_user = (ListView) findViewById(R.id.lv_choose_user);
    }

    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
        switch (arg0.getId()) {
            case R.id.btnTitleLeft:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        mSelectMap = adapterChild.getMap();
        if(mSelectMap.size()>0){
            List<Long> plusList = new ArrayList<Long>();
            for (Map.Entry<Long, User> entry : mSelectMap.entrySet()) {
                muteUsers.add(entry.getValue());
                plusList.add(entry.getValue().getUserId());
            }
            showProgressDialog(mContext, "", true, null);
            getService().getDepartmentManager().addUsersToMutedList(departmentId, plusList,
                    muteTpye);
        }
    }

    public void onEventMainThread(MuteEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case ADD_TO_MUTED_LIST_SUCCESS:
                    sendTouChuan(SysConstants.TOUCHUAN_GAGUSER);
                    showToast("添加成功");
                    Intent intent = new Intent(mContext, BanManagerActivity.class);
                    intent.putExtra("selectMap", (Serializable) mSelectMap);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                case ADD_TO_MUTED_LIST_FAILED:
                    showToast(event.getMsg());
                    break;
            }
        }

    }


    private void showData(List<User> objects) {
        if (adapterChild == null) {
            adapterChild = new SelectReceiverAdapter(mContext, objects, mSelectMap);
            lv_choose_user.setAdapter(adapterChild);
        } else {
            adapterChild.notifyDataSetChanged();
        }
    }

    class SelectReceiverAdapter extends BaseAdapter implements SectionIndexer {
        private List<User> list = null;
        private Context mContext;
        private Map<Long, User> selectCb = new HashMap<Long, User>();
        private CharacterParser characterParser;

        public SelectReceiverAdapter(Context mContext, List<User> list, Map<Long, User> mSelectCb) {
            this.mContext = mContext;
            this.list = list;
            characterParser = CharacterParser.getInstance();
            selectCb = mSelectCb;
        }

        public Map<Long, User> getMap() {
            return selectCb;
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
            final User info = list.get(position);
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.tea_select_receiver_sort_item_layout, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.select_receiver_item_name);
            viewHolder.zimu = (TextView) view.findViewById(R.id.select_receiver_item_zimu);
            viewHolder.icon = (RoundImageView) view.findViewById(R.id.select_receiver_item_icon);
            viewHolder.cb = (CheckBox) view.findViewById(R.id.select_receiver_item_cb);
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                viewHolder.zimu.setVisibility(View.VISIBLE);
                viewHolder.zimu.setText(setZmu(info.getNickname()));
            } else {
                viewHolder.zimu.setVisibility(View.GONE);
            }

            view.setTag(info);
            viewHolder.tvTitle.setText(info.getNickname());

            viewHolder.icon.setImageUrl(info.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            viewHolder.cb.setTag(info);
            if (selectCb.containsKey(info.getUserId())) {
                viewHolder.cb.setChecked(true);
            } else {
                viewHolder.cb.setChecked(false);
            }

            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean ischeck) {
                    User in = (User) compoundButton.getTag();
                    if (ischeck) {
                        selectCb.put(in.getUserId(), in);
                        setRightNext(true, "确定(" + selectCb.size() + ")", 0);
                    } else {
                        selectCb.remove(in.getUserId());
                        if(selectCb.size()<=0){
                            setRightNext(true, "", 0);
                        }else {
                            setRightNext(true, "确定(" + selectCb.size() + ")", 0);
                        }
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User in = (User) v.getTag();
                    if (!selectCb.containsKey(in.getUserId())) {
                        selectCb.put(in.getUserId(), in);
                        setRightNext(true, "确定(" + selectCb.size() + ")", 0);
                    } else {
                        selectCb.remove(in.getUserId());
                        if(selectCb.size()>0){
                            setRightNext(true, "确定(" + selectCb.size() + ")", 0);
                        }else
                            setRightNext(true,"",0);
                    }
                    notifyDataSetChanged();
                }
            });
            return view;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
            return setZmu(list.get(position).getNickname()).charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = setZmu(list.get(i).getNickname());
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
