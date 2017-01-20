package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.adapter.ContactAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.rpc.proto.EventType;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

import java.util.ArrayList;
import java.util.List;


public class ContactActivity extends BaseActivity implements OnItemClickListener,XListView.IXListViewListener {

    private XListView classListView;
    private List<Department> departmentList;
    private ContactAdapter classAdapter;
    public String TAG = ContactActivity.class.getSimpleName();
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.contact_layout);
        departmentList = new ArrayList<Department>();
        init();
        setHeader();
        initData();

        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "addressList");
    }

    private void init() {
        setTitle(getString(R.string.address_book));
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        classListView = (XListView) findViewById(R.id.contact_listview);
        classListView.setOnItemClickListener(this);
        classListView.setXListViewListener(this);
    }

    private void initData() {
        departmentList = getService().getContactManager().getAllDepartment();
        updateAdapter();
    }
    private void setHeader() {
        headView = getLayoutInflater().inflate(R.layout.contact_header_layout, null);
        RelativeLayout rl_contact_teacher = (RelativeLayout)headView.findViewById(R.id.rl_contact_teacher);
        final TextView gardenName = (TextView) headView.findViewById(R.id.contact_item_name_1);
        final TextView parentName = (TextView) headView.findViewById(R.id.contact_item_name_2);
        rl_contact_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TuxingApp.packageName+ SysConstants.CONTACTALLTEACHER);
                intent.putExtra("departmentId", user.getGardenId());
                intent.putExtra("title", gardenName.getText().toString());
                startActivity(intent);
            }
        });
        RelativeLayout rl_contact_parent = (RelativeLayout)headView.findViewById(R.id.rl_contact_parent);
        rl_contact_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TuxingApp.packageName + SysConstants.CONTACTALLPARENT);
                intent.putExtra("title", parentName.getText().toString());
                startActivity(intent);
            }
        });
    }
    private void  updateAdapter(){
        if(classAdapter == null) {
            classListView.addHeaderView(headView);
            classAdapter = new ContactAdapter(mContext, departmentList);
            classListView.setAdapter(classAdapter);
        }else{
            classAdapter.setData(departmentList);
        }
        classListView.setPullLoadEnable(false);
        classListView.setPullRefreshEnable(false);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if(position>=2){
            Department department = departmentList.get(position-2);
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
            intent.putExtra("userName", department.getName());
            intent.putExtra("groupId", Long.valueOf(department.getChatGroupId()));
            startActivity(intent);
        }

    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT,"addressList");
        super.finish();
    }
}
