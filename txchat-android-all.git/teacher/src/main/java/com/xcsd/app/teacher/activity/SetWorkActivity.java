package com.xcsd.app.teacher.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.xcsd.app.teacher.adapter.SetWorkAdapter;
import com.xcsd.app.teacher.adapter.TrainAdapter;
import com.xcsd.app.teacher.adapter.WorkSocreAdapter;
import com.xcsd.app.teacher.view.ContactParentPopupwindow;
import com.xcsd.app.teacher.R;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.ui.dialog.PopupWindowDialog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.HomeWorkGenerate;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.jar.Attributes;

import me.maxwin.view.XListView;

public class SetWorkActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener,  View.OnClickListener {

    private LinearLayout btnTitleLeft;
    private TextView tv_right;
    private SwipeListView xListView;
    private SetWorkAdapter adapter;
    private TextView tv_setwork_type;
    private List<HomeWorkGenerate> contentDatas;
    private long gardenId;
    private boolean hasMore = false;
    private String TAG;
    private RelativeLayout activity_bg;
    private ContactParentPopupwindow Popupwindow;
    ArrayList<String> list;

    private List<Department> departmentList;
    int selectedPopup = 0;
    int slectedtype = 0;
    int type = 1;


    private List<Department> info = null;
    Department test;
    private long classid;
    private List<HomeWorkGenerate> tempDatas;
    private List<HomeWorkGenerate> tempDatas_all;
    private List<HomeWorkGenerate> tempDatas_normal;
    private List<HomeWorkGenerate> tempDatas_special;

    public static SetWorkActivity instance = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_work);
        instance = this;
        initView();
//        initData();
        getData();
    }

    private void initView() {
        TAG = SetWorkActivity.class.getSimpleName();
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        tv_right = (TextView) findViewById(R.id.tv_right);

        contentDatas = new ArrayList<HomeWorkGenerate>();
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);

        xListView = (SwipeListView) findViewById(R.id.home_acticity_list);
        tv_setwork_type = (TextView) findViewById(R.id.tv_setwork_type);

        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);

        tempDatas_all = new ArrayList<HomeWorkGenerate>();
        tempDatas_normal = new ArrayList<HomeWorkGenerate>();
        tempDatas_special = new ArrayList<HomeWorkGenerate>();
//        默认选择全部类型
        slectedtype = Constants.STUDENT_SCOPE.ALL;

        classid = getIntent().getLongExtra("classid", 0);


        Department test = new Department();
        Department test2 = new Department();
        Department test3 = new Department();
        test.setName("全部学生");
        test2.setName("普通学生");
        test3.setName("特别关注学生");
        info = new ArrayList<Department>();
        info.add(test);
        info.add(test2);
        info.add(test3);
        departmentList = info;

        tv_setwork_type.setOnClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        tv_right.setOnClickListener(this);

        updateAdapter();
//
    }

    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
        getPopWindow();
    }

    private void initData() {
        // TODO 获取服务器数据
        getService().getHomeWorkManager().GenerateHomeworkRequest(classid);
        showProgressDialog(this, "", true, null);
//        getService().getHomeWorkManager().SendHomeworkRequest(classid, Constants.STUDENT_SCOPE.ALL);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            tempDatas = new ArrayList<HomeWorkGenerate>();
            switch (event.getEvent()) {
                case HOMEWORK_GENERATE_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkGenerateList();
                    fortype();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        contentDatas.addAll(tempDatas);
                        updateAdapter();
                    }
//                    getResresh(tempDatas);
//                    showFooterView();
//                    xListView.stopLoadMore();
//                    xListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);
//                    getService().getCounterManager().resetCounter(
//                            Constants.COUNTER.ACTIVITY);
                    break;
                case HOMEWORK_GENERATE_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    xListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;

                case HOMEWORK_SEND_SUCCESS:
                    showToast("发送成功");
                    getService().getDataReportManager().reportEventBid(EventType.CUSTOMIZED_HOMEWORK, classid+"");
                    SetClassWorkActivity.instance.finish();
                    getService().getHomeWorkManager().getHomeWorkSendListLatest();
//                    TrainActivity.instance.initData();
                    this.finish();
                    break;
                case HOMEWORK_SEND_FAILED:
                    showToast(event.getMsg());
                    break;
            }
        }
    }


    @Override
    public void onRefresh() {
//        getService().getContentManager().getLatestItems(gardenId,
//                Constants.CONTENT_TYPE.ACTIVITY);
    }

    private void getResresh(List<HomeWorkGenerate> refreshList) {
        // TODO 加载数据
        if (refreshList != null && refreshList.size() > 0) {
            contentDatas.clear();
            contentDatas.addAll(0, refreshList);
        }
        updateAdapter();
        xListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        if (contentDatas.size() > 0) {
//            long lastId = contentDatas.get(contentDatas.size() - 1).getItemId();
//            getService().getContentManager().getHistoryItems(gardenId,
//                    Constants.CONTENT_TYPE.ACTIVITY, lastId);
        } else {
            xListView.stopLoadMore();
        }
        xListView.stopLoadMore();
    }

    public void getLoadMore(List<HomeWorkGenerate> list) {
        if (list != null && list.size() > 0) {
            contentDatas.addAll(list);
        }
        xListView.stopLoadMore();
        updateAdapter();
    }

    public void showFooterView() {
        if (hasMore)
            xListView.setPullLoadEnable(true);
        else
            xListView.setPullLoadEnable(false);
    }

    public void updateAdapter() {
        if (contentDatas != null && contentDatas.size() > 0) {
            activity_bg.setVisibility(View.GONE);
        } else {
            activity_bg.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            adapter = new SetWorkAdapter(this, contentDatas);
            xListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        position = position - 1;
        if (position >= 0) {
            HomeWorkGenerate contentItem = contentDatas.get(position);
//            判断作业是否已满
            if(contentItem.getRemainMaxCount()!=0){

                String token = null;
                LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                if (loginUser != null) {
                    token = loginUser.getToken();

                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(SysConstants.H5_GAME_HOST_URL);
                stringBuilder.append("?");
                stringBuilder.append("action=10002&");
                stringBuilder.append("memberId=" + contentItem.getChildUserId() + "&");
                stringBuilder.append("classId=" + classid + "&");
                stringBuilder.append("token=" + token + "&");
                stringBuilder.append("isTaskState=" + "0");
                showAndSaveLog(TAG, "TOKEN数据=======================+" + stringBuilder.toString(), false);
//                WebSubUrlActivity.invoke(mContext, stringBuilder.toString(),
//                        getString(com.tuxing.app.R.string.homeworkdetail));
                Intent intent = new Intent(this,SetWorkDetailsActivity.class).putExtra("MemberId",contentItem.getChildUserId()).putExtra("classid",classid);
                startActivity(intent);
            }

        }
    }

    @Override
    public void getData() {
//        if (user != null)
//            classid = user.getClassId();
        initData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTitleLeft:
//            点击退出界面
                this.finish();
                break;
//            case R.id.tv_setwork_type:
//                int state = 2;
//                if (tv_setwork_type.getText().toString().equals("普通学生")) {
//                    state = 1;
//                } else if (tv_setwork_type.getText().toString().equals("特别关注学生")) {
//                    state = 2;
//                }else if (tv_setwork_type.getText().toString().equals("全部学生")) {
//                    state = 0;
//                }
//                Popupwindow = new ContactParentPopupwindow(this, v, info, state);
//                Popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        arrow_show(false);
//                    }
//                });
//
//                Popupwindow.setOnItemClickPopupWindowListener(new ContactParentPopupwindow.OnItemClickWallpaperPopupWindowListener() {
//                    @Override
//                    public void OnItemClickPopupWindow(Department itemInfo, int selected) {
//
//                        tv_setwork_type.setText(info.get(selected).getName());
//                        Toast.makeText(SetWorkActivity.this, info.get(selected).getName(), Toast.LENGTH_SHORT).show();
//                        Popupwindow.dismiss();
//                    }
//                });
//                Popupwindow.showAsDropDown(tv_setwork_type);
//                arrow_show(true);
//                break;
            case R.id.tv_right:
//            点击发送作业
                showProgressDialog(this, "", true, null);
                if (slectedtype==Constants.STUDENT_SCOPE.SPECIAL){
                    if (tempDatas_special.size()==0||tempDatas_special==null){
                        disProgressDialog();
                        showToast("未选择学生");
                    }else{
                        getService().getHomeWorkManager().SendHomeworkRequest(classid, slectedtype);
                    }
                }else if (slectedtype==Constants.STUDENT_SCOPE.NORMAL){
                    if (tempDatas_normal.size()==0||tempDatas_normal==null){
                        disProgressDialog();
                        showToast("未选择学生");
                    }else{
                        getService().getHomeWorkManager().SendHomeworkRequest(classid, slectedtype);
                    }
                }else if(slectedtype==Constants.STUDENT_SCOPE.ALL){
                    if (tempDatas_all.size()==0||tempDatas_all==null){
                        disProgressDialog();
                        showToast("未选择学生");
                    }else{
                        getService().getHomeWorkManager().SendHomeworkRequest(classid, slectedtype);
                    }
                }
                break;
        }
    }

    private  void  fortype(){
//        tempDatas_all = tempDatas;
        for (int i =0;i<tempDatas.size();i++){
            HomeWorkGenerate type = tempDatas.get(i);
            if (type.getSpecialAttention()){
                tempDatas_special.add(type);
            }else {
                tempDatas_normal.add(type);
            }
        }
        tempDatas.clear();
        for (int i =0;i<tempDatas_special.size();i++){
            HomeWorkGenerate type = tempDatas_special.get(i);
                tempDatas.add(type);
        }
        for (int i =0;i<tempDatas_normal.size();i++){
            HomeWorkGenerate type = tempDatas_normal.get(i);
                tempDatas.add(type);
        }
        tempDatas_all = tempDatas;

    }

    private void arrow_show(boolean isUp) {
        Drawable drawable = null;
        if (isUp) {
            drawable = getResources().getDrawable(com.tuxing.app.R.drawable.ic_arrow_up);
        } else {
            drawable = getResources().getDrawable(com.tuxing.app.R.drawable.ic_arrow_down);
        }
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth() + 5, drawable.getMinimumHeight());
        tv_setwork_type.setCompoundDrawables(null, null, drawable, null);

    }

    private void getPopWindow() {
        arrow_show(false);
        tv_setwork_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> departs = new ArrayList<String>();
                for (Department department : departmentList) {
                    departs.add(department.getName());
                }
                arrow_show(true);
                showContextMenu(departs.toArray(new CharSequence[departs.size()]));
            }
        });
    }

    public void showContextMenu(final CharSequence[] departs) {
        final PopupWindowDialog dialog = DialogHelper.getPopDialogCancelable(this);
        dialog.setItemsWithoutChk(departs,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        dialog.dismiss();
                        tv_setwork_type.setText(departs[position]);
                        arrow_show(false);
                        if (!CollectionUtils.isEmpty(departmentList)) {
                            Department department = departmentList.get(position);
                            if (department != null) {
                                if (position==0){
                                    type= 1;
                                    tempDatas = tempDatas_all;
                                    slectedtype = Constants.STUDENT_SCOPE.ALL;
                                }else if (position ==1){
                                    type= 2;
                                    slectedtype = Constants.STUDENT_SCOPE.NORMAL;
                                    tempDatas = tempDatas_normal;
                                }else{
                                    type= 3;
                                    slectedtype = Constants.STUDENT_SCOPE.SPECIAL;
                                    tempDatas = tempDatas_special;
                                }
                                if (tempDatas.size()==0){
                                    contentDatas.clear();
                                    updateAdapter();
                                }else {
                                    getResresh(tempDatas);
                                }
//                                showProgressDialog(mContext, "", true, null);
                                showToast("已选择"+department.getName());
                            }
                        }
                        dialog.setSelectIndex(selectedPopup);
                        selectedPopup = position;
                    }
                }, selectedPopup);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        lp.y = PhoneUtils.dip2px(this, 48); // 新位置Y坐标
        lp.width = PhoneUtils.dip2px(this, 200); // 宽度
        if (departs.length < 3) {
            lp.height = PhoneUtils.dip2px(mContext, 85);
            ; // 动态高度
        } else if (departs.length >= 5) {
            lp.height = PhoneUtils.dip2px(mContext, 250);
            ; // 动态高度
        } else {
            lp.height = PhoneUtils.dip2px(mContext, departs.length * 45) - PhoneUtils.dip2px(mContext, 8);
            ; // 动态高度
        }

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                arrow_show(false);
            }
        });
    }



}
