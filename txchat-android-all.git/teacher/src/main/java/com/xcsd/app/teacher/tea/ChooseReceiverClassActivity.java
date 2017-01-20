package com.xcsd.app.teacher.tea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.modle.NoticeDepartmentReceiver;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseReceiverClassActivity extends BaseActivity implements OnItemClickListener {
    private ListView classListView;
    private SelectReceiverClassAdapter classAdapter;
    public String TAG = ChooseReceiverClassActivity.class.getSimpleName();
    private List<Department> departments;//全部的班级组织
    private Map<Long, Integer> departmentsCount;//全部的班级组织
    private final int REQUESTCODE = 0x100;
    private final int COUNTMSG = 0x101;
    private Map<Long, NoticeDepartmentReceiver> receiversMap;
    List<NoticeDepartmentReceiver> initReceivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.tea_select_receiver_class_layout);
        departments = getService().getContactManager().getAllDepartment();
        initReceivers = (List<NoticeDepartmentReceiver>) getIntent().getSerializableExtra("receivers");
        receiversMap = new HashMap<Long, NoticeDepartmentReceiver>();
        departmentsCount = new HashMap<Long, Integer>();
        if (!CollectionUtils.isEmpty(initReceivers)) {
            for (NoticeDepartmentReceiver receiver : initReceivers) {
                receiversMap.put(receiver.getDepartmentId(), receiver);
            }
        }
        checkNum(receiversMap);
        initView();
        initData();
    }

    private void initView() {
        setTitle(getString(R.string.select_receive));
        setLeftBack("取消", false, false);
        setRightNext(false, "", 0);
        classListView = (ListView) findViewById(R.id.select_receiver_class_listview);
        classListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        updateAdapter(departments);
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        if (checkNum(receiversMap) <= 0) {
            showToast("请选择接收人");
            return;
        }
        List<NoticeDepartmentReceiver> receivers = new ArrayList<NoticeDepartmentReceiver>();
        for (Map.Entry<Long, NoticeDepartmentReceiver> rece : receiversMap.entrySet()) {
            receivers.add(rece.getValue());
        }
        Intent intent = new Intent(mContext, NoticeReleaseActivity.class);
        intent.putExtra("receivers", (Serializable) receivers);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 显示数据
     */
    public void updateAdapter(List<Department> list) {
        if (classAdapter == null) {
            classAdapter = new SelectReceiverClassAdapter(mContext, list);
            classListView.setAdapter(classAdapter);
        } else {
            classAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(mContext, ChooseReceiverUserActivity.class);
        Department department = departments.get(position);
        if (receiversMap != null && receiversMap.size() > 0) {
            NoticeDepartmentReceiver receiver = receiversMap.get(department.getDepartmentId());
            if (receiver != null && (receiver.isAll()||receiver.getMemberUserIds().size()==receiver.getMemberCount())) {
                intent.putExtra("isCheckAll", true);
            } else {
                intent.putExtra("isCheckAll", false);
                if (receiversMap != null && receiversMap.get(department.getDepartmentId()) != null
                        && !CollectionUtils.isEmpty(receiversMap.get(department.getDepartmentId()).getMemberUserIds())) {
                    intent.putExtra("memberUserIds", (Serializable) receiversMap.get(department.getDepartmentId()).getMemberUserIds());
                }
            }
        }
        intent.putExtra("department", department);
        startActivityForResult(intent, REQUESTCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == REQUESTCODE) {
            List<DepartmentMember> classUsers = (List<DepartmentMember>) data.getSerializableExtra("selectList");
            Long departmentId = data.getLongExtra("departmentId", -1);
            boolean isSelectAll = data.getBooleanExtra("isSelectAll", false);//全选
            NoticeDepartmentReceiver receiver = new NoticeDepartmentReceiver();
            if (!isSelectAll) {//如果没有全选
                List<Long> userIds = new ArrayList<Long>();
                for (DepartmentMember member : classUsers) {
                    userIds.add(member.getUser().getUserId());
                }
                receiver.setMemberUserIds(userIds);
            }
            receiver.setAll(isSelectAll);
            receiver.setDepartmentId(departmentId);

            //添加到全部Map集合
            receiversMap.put(departmentId, receiver);
            checkNum(receiversMap);
            classAdapter.notifyDataSetChanged();
            setRightNext(true, "确定(" + checkNum(receiversMap) + ")", 0);
        }
    }

    class SelectReceiverClassAdapter extends BaseAdapter {

        private Context mContext;
        private List<Department> classes;

        public SelectReceiverClassAdapter(Context context, List<Department> classList) {
            this.mContext = context;
            this.classes = classList;
        }


        @Override
        public int getCount() {
            return classes.size();
        }

        @Override
        public Object getItem(int position) {
            return classes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.tea_select_receiver_class_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.className = (TextView) convertView.findViewById(R.id.select_receiver_class_item_name);
                viewHolder.icon = (RoundImageView) convertView.findViewById(R.id.select_receiver_class_item_icon);
                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.select_receiver_class_item_cb);
                viewHolder.select_receiver_class_count = (TextView) convertView.findViewById(R.id.select_receiver_class_count);
                viewHolder.select_receiver_class_count.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Department department = classes.get(position);
            if (receiversMap.get(department.getDepartmentId()) != null && receiversMap.get(department.getDepartmentId()).isAll()) {//全选
                viewHolder.select_receiver_class_count.setText(receiversMap.get(department.getDepartmentId()).getMemberCount() + "/" + departmentsCount.get(department.getDepartmentId()));
            } else if (receiversMap.get(department.getDepartmentId()) != null && !receiversMap.get(department.getDepartmentId()).isAll()) {
                viewHolder.select_receiver_class_count.setText(receiversMap.get(department.getDepartmentId()).getMemberUserIds().size() + "/" + departmentsCount.get(department.getDepartmentId()));
            } else {
                viewHolder.select_receiver_class_count.setText("0/" + departmentsCount.get(department.getDepartmentId()));
            }
            viewHolder.icon.setImageUrl(department.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            viewHolder.className.setText(department.getName());
            viewHolder.cb.setTag(department);
            boolean isAll = false;
            for (Map.Entry<Long, NoticeDepartmentReceiver> entry : receiversMap.entrySet()) {
                if (department.getDepartmentId() == entry.getKey()) {
                    if (entry.getValue().isAll()||!CollectionUtils.isEmpty(entry.getValue().getMemberUserIds())){
                        isAll = true;
                    }
                }
            }
            if (isAll) {
                viewHolder.cb.setChecked(true);
            }else{
                viewHolder.cb.setChecked(false);
            }

            viewHolder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Department depart = (Department) v.getTag();
                    NoticeDepartmentReceiver receiver = receiversMap.get(depart.getDepartmentId());
                    if(receiver !=null&&(receiver.isAll()||!CollectionUtils.isEmpty(receiver.getMemberUserIds()))){
                        if(viewHolder.cb.isChecked()){//取消
                            receiver = new NoticeDepartmentReceiver();
                            receiver.setDepartmentId(depart.getDepartmentId());
                            receiver.setAll(true);
                            receiver.setMemberCount(departmentsCount.get(department.getDepartmentId()));
                            receiversMap.put(depart.getDepartmentId(), receiver);
                            setRightNext(true, "确定(" + checkNum(receiversMap) + ")", 0);
                            viewHolder.select_receiver_class_count.setText(departmentsCount.get(department.getDepartmentId())
                                    + "/" + departmentsCount.get(department.getDepartmentId()));
                            viewHolder.cb.setChecked(true);
                        }else{
                            if (((NoticeDepartmentReceiver) receiversMap.get(depart.getDepartmentId())) != null) {
                                receiversMap.remove(depart.getDepartmentId());
                                viewHolder.select_receiver_class_count.setText("0/" + departmentsCount.get(department.getDepartmentId()));
                                setRightNext(true, "确定(" + checkNum(receiversMap) + ")", 0);
                            }
                            viewHolder.cb.setChecked(false);
                        }
                    }else{
                        receiver = new NoticeDepartmentReceiver();
                        receiver.setDepartmentId(depart.getDepartmentId());
                        receiver.setAll(true);
                        receiver.setMemberCount(departmentsCount.get(department.getDepartmentId()));
                        receiversMap.put(depart.getDepartmentId(), receiver);
                        setRightNext(true, "确定(" + checkNum(receiversMap) + ")", 0);
                        viewHolder.select_receiver_class_count.setText(departmentsCount.get(department.getDepartmentId())
                                + "/" + departmentsCount.get(department.getDepartmentId()));
                        viewHolder.cb.setChecked(true);
                    }
                }
            });
//            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean ischeck) {
//                    Department depart = (Department) compoundButton.getTag();
//                    NoticeDepartmentReceiver receiver = receiversMap.get(depart.getDepartmentId());
//                    if (ischeck) {//选中
////                        if(receiver!=null){
////                        }else{
//                            receiver = new NoticeDepartmentReceiver();
//                            receiver.setDepartmentId(depart.getDepartmentId());
//                            receiver.setAll(true);
//                            receiver.setMemberCount(departmentsCount.get(department.getDepartmentId()));
//                            receiversMap.put(depart.getDepartmentId(), receiver);
//                            setRightNext(true, "确定(" + checkNum(receiversMap) + ")", 0);
//                            viewHolder.select_receiver_class_count.setText(departmentsCount.get(department.getDepartmentId())
//                                    + "/" + departmentsCount.get(department.getDepartmentId()));
////                        }
//                    } else {
//                        if (((NoticeDepartmentReceiver) receiversMap.get(depart.getDepartmentId())) != null) {
//                            receiversMap.remove(depart.getDepartmentId());
//                            viewHolder.select_receiver_class_count.setText("0/" + departmentsCount.get(department.getDepartmentId()));
//                            setRightNext(true, "确定(" + checkNum(receiversMap) + ")", 0);
//                        }
//                    }
//                }
//            });
            return convertView;
        }

        class ViewHolder {
            public RoundImageView icon;
            public TextView className;
            public CheckBox cb;
            public TextView select_receiver_class_count;
        }

    }

    /**
     * 计算数量
     *
     * @param map
     * @return
     */
    private int checkNum(Map<Long, NoticeDepartmentReceiver> map) {
        int count = 0;
        for (Department depart : departments) {
            int num = 0;
            if (depart.getType() == Constants.DEPARTMENT_TYPE.GARDEN) {//幼儿园
//            if (depart.getType() == 11) {//老师班级
                Long numl = getService().getContactManager().getDepartmentMemberCountByUserType(depart.getDepartmentId(), Constants.USER_TYPE.TEACHER);
                num = (int) numl.longValue()-1;
                departmentsCount.put(depart.getDepartmentId(),num);
            } else if (depart.getType() == Constants.DEPARTMENT_TYPE.CLASS) {//幼儿班级
                Long numl = getService().getContactManager().getDepartmentMemberCountByUserType(depart.getDepartmentId(), Constants.USER_TYPE.CHILD);
                num = (int) numl.longValue();
                departmentsCount.put(depart.getDepartmentId(), num);
            }else if (depart.getType() == Constants.DEPARTMENT_TYPE.GRADE){//年级
//                Long numl = getService().getContactManager().getDepartmentMemberCountByUserType(depart.getDepartmentId(), Constants.USER_TYPE.GRADE);
//                num = (int) numl.longValue();
//                departmentsCount.put(depart.getDepartmentId(), num);
            }else if (depart.getType() == Constants.DEPARTMENT_TYPE.SCHOOL){//学校
                Long numl = getService().getContactManager().getDepartmentMemberCountByUserType(depart.getDepartmentId(), Constants.USER_TYPE.TEACHER);
                num = (int) numl.longValue();
                departmentsCount.put(depart.getDepartmentId(), num);
            }
            for (Map.Entry<Long, NoticeDepartmentReceiver> noticeDepartmentReceiver : map.entrySet()) {
                if (noticeDepartmentReceiver.getKey() == depart.getDepartmentId()) {
                    receiversMap.get(depart.getDepartmentId()).setMemberCount(num);
                    if (noticeDepartmentReceiver.getValue().isAll()) {//班级全选
                        count = count + num;
                    } else {
                        count = count + noticeDepartmentReceiver.getValue().getMemberUserIds().size();
                    }
                }
            }
        }
        return count;
    }
}
