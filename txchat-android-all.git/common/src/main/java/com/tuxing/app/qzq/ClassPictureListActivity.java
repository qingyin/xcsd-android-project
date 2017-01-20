package com.tuxing.app.qzq;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.adapter.ClassPictureListAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.ui.dialog.PopupWindowDialog;
import com.tuxing.app.util.DateTimeUtils;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.sdk.db.entity.ClassPicture;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.event.ClassPictureEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

import java.util.*;

/**
 */
public class ClassPictureListActivity extends BaseActivity implements XListView.IXListViewListener, View.OnClickListener {

    private XListView xListView;
    private ClassPictureListAdapter adapter;
    private ImageView qzq_bg;
    private int mFlag = 0; // 0表示正常下载，1表示加载更多
    boolean isMyself = false;
    List<ClassPicture> localList = new ArrayList<ClassPicture>();//local
    List<ClassPicture> netList = new ArrayList<ClassPicture>();//net
    List<ClassPicture> list = new ArrayList<ClassPicture>();
    int selectedPopup = 0;
    private List<Department> departmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_my_circle);
        setRightNext(false, "", 0);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        setLeftBack("", true, false);
        int index = getIntent().getIntExtra("index",0);
        if(index>0){
            selectedPopup = index -1;
        }
        departmentList = getService().getContactManager().getAllDepartment();
        mFlag = 0;
        getlocalData();
        getnetlData();
        initView();
        listViewInit();
        xListView.setXListViewListener(this);
        xListView.setHeaderbgColor(getResources().getColor(R.color.white));
        xListView.setPullLoadEnable(false);
    }

    private void initView() {
        xListView = (XListView) findViewById(R.id.xListView);
        qzq_bg = (ImageView)findViewById(R.id.qzq_bg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPopWindow();
    }

    @Override
    public void onclickRightImg() {
        super.onclickRightImg();
        // 跳入到消息列表
        startActivityForResult(new Intent(mContext, MessageUnreadListActivity.class),100);
    }

    // listview的初始化
    public void listViewInit() {
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 2) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, MessageUnreadDetailActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    //从net获取最近20条
    private  void getlocalData(){
        if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            if(user!=null){
                getService().getFeedManager().getClassPicturesFromLocalCache(user.getClassId());
                showProgressDialog(this, "", true, null);
            }
        }else{
            if(!CollectionUtils.isEmpty(departmentList)){
                Department department = departmentList.get(selectedPopup);
                getService().getFeedManager().getClassPicturesFromLocalCache(department.getDepartmentId());
                showProgressDialog(this, "", true, null);
            }else{
                if(user!=null){
                    getService().getFeedManager().getClassPicturesFromLocalCache(user.getClassId());
                    showProgressDialog(this, "", true, null);
                }
            }
        }
    }
    //从net获取最近20条
    private  void getnetlData(){
        if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            if(user!=null){
                getService().getFeedManager().getLatestClassPictures(user.getClassId());
                showProgressDialog(this, "", true, null);
            }
        }else{
            if(!CollectionUtils.isEmpty(departmentList)){
                Department department = departmentList.get(selectedPopup);
                getService().getFeedManager().getLatestClassPictures(department.getDepartmentId());
                showProgressDialog(this, "", true, null);
            }else{
                if(user!=null){
                    getService().getFeedManager().getLatestClassPictures(user.getClassId());
                    showProgressDialog(this, "", true, null);
                }
            }
        }
    }

    //从net获取之前20条
    private  void getnetlHistoryData(){
        if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            if(user!=null){
                if(list.size()!=0){
                    getService().getFeedManager().getHistoryClassPictures(user.getClassId(),list.get(list.size()-1).getPicId());
                }
            }
        }else{
            if(!CollectionUtils.isEmpty(departmentList)){
                Department department = departmentList.get(selectedPopup);
                if(list.size()!=0){
                    getService().getFeedManager().getHistoryClassPictures(department.getDepartmentId(),list.get(list.size()-1).getPicId());
                }
            }else{
                if(user!=null){
                    getService().getFeedManager().getHistoryClassPictures(user.getClassId(),list.get(list.size()-1).getPicId());
                }
            }
        }
    }
    private void getPopWindow() {
        if (!TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {//教师版
            Department department = departmentList.get(selectedPopup);
            if(department!=null){
                setTitle(department.getName()+getString(R.string.class_picture));
            }else
                setTitle("班级"+getString(R.string.class_picture));
            arrow_show(false);
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> departs = new ArrayList<String>();
                    for(Department department:departmentList){
                        departs.add(department.getName());
                    }
                    arrow_show(true);
                    showContextMenu(departs.toArray(new CharSequence[departs.size()]));
                }
            });
        }else {
            if(user!=null)
                setTitle(user.getClassName()+getString(R.string.class_picture));
            else
                setTitle("班级"+getString(R.string.class_picture));
        }
    }

    /**
     * 服务器返回
     * @param event
     */
    public void onEventMainThread(ClassPictureEvent event){
        if(isActivity){
            disProgressDialog();
            xListView.stopRefresh();
            switch (event.getEvent()){
                case GET_LOCAL_PICTURE:
                    if (!CollectionUtils.isEmpty(event.getPictures())) {
                        localList.clear();
                        localList = event.getPictures();
                        list.clear();
                        list.addAll(event.getPictures());
                        updateAdapter(list,event.getTotal());
                    }
                    break;
                case GET_LATEST_PICTURE_SUCCESS:
                    if (event.getPictures() != null) {
                        netList.clear();
                        netList = event.getPictures();
                        list.clear();
                        list.addAll(netList);
                        if(list.size() > 0){
                            qzq_bg.setVisibility(View.GONE);
                        }else{
                            qzq_bg.setVisibility(View.VISIBLE);
                        }
                        updateAdapter(list,event.getTotal());
                        if (event.isHasMore()) {
                            xListView.setPullLoadEnable(true);
                        }else
                            xListView.setPullLoadEnable(false);
                    }
                    break;
                case GET_LATEST_PICTURE_FAILED:
                    showToast(event.getMsg());
                    break;
                case GET_HISTORY_PICTURE_SUCCESS:
                    if (event.getPictures() != null && event.getPictures().size() > 0) {
                        list.addAll(event.getPictures());
                        updateAdapter(list,event.getTotal());
                    }
                    if (event.isHasMore()) {
                        xListView.setPullLoadEnable(true);
                    }else
                        xListView.setPullLoadEnable(false);
                    xListView.stopLoadMore();
                    break;
                case GET_HISTORY_PICTURE_FAILED:
                    xListView.stopLoadMore();
                    showToast(event.getMsg());
                    break;
                default:
                    break;
            }
    }}
    public void updateAdapter(List<ClassPicture> list,long picTotal){
        Map<String,List<ClassPicture>> map = groupByDate(list);
        List<List<ClassPicture>> lists = new ArrayList<List<ClassPicture>>(map.values());
        if(adapter == null){
            adapter = new ClassPictureListAdapter(mContext, lists, getService(),picTotal);
            xListView.setAdapter(adapter);
//            adapter.setData(lists);
        }else{
            adapter.setData(lists,picTotal);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mFlag = 0;
        getnetlData();
    }

    @Override
    public void onLoadMore() {
        mFlag = 1;
        getnetlHistoryData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    /**
     * @param list
     * @return
     */
    public Map<String,List<ClassPicture>> groupByDate(List<ClassPicture> list){
        Map<String,List<ClassPicture>> maps = new LinkedHashMap<String,List<ClassPicture>>();
        if(!CollectionUtils.isEmpty(list)){
            for(ClassPicture classPicture:list) {
                String dateKey = DateTimeUtils.Date2YYYYMMDD_C(classPicture.getCreatedOn());
                if(maps.containsKey(dateKey)){
                    maps.get(dateKey).add(classPicture);
                }else {
                    List<ClassPicture> classList = new ArrayList<ClassPicture>();
                    classList.add(classPicture);
                    maps.put(dateKey,classList);
                }
            }
        }
        return maps;
    }

    public void showContextMenu(final CharSequence[] departs) {
        final PopupWindowDialog dialog = DialogHelper.getPopDialogCancelable(this);
        dialog.setItemsWithoutChk(departs,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        dialog.dismiss();
                        tv_title.setText(departs[position]);
                        arrow_show(false);
                        if(!CollectionUtils.isEmpty(departmentList)){
                            Department department = departmentList.get(position);
                            if (department!=null) {
                                showProgressDialog(mContext,"",true,null);
                                getService().getFeedManager().getLatestClassPictures(department.getDepartmentId());
                            }
                        }
                        dialog.setSelectIndex(selectedPopup);
                        selectedPopup = position;
                    }
                },selectedPopup);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        lp.y =  PhoneUtils.dip2px(this, 48); // 新位置Y坐标
        lp.width = PhoneUtils.dip2px(this, 200); // 宽度
        if(departs.length<3){
            lp.height = PhoneUtils.dip2px(mContext, 85); ; // 动态高度
        }else if(departs.length>=5){
            lp.height = PhoneUtils.dip2px(mContext, 208); ; // 动态高度
        }else{
            lp.height = PhoneUtils.dip2px(mContext, departs.length*45)-PhoneUtils.dip2px(mContext, 8); ; // 动态高度
        }

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void arrow_show(boolean isUp){
        Drawable drawable = null;
        if(isUp){
            drawable= getResources().getDrawable(R.drawable.ic_arrow_up);
        }else{
            drawable= getResources().getDrawable(R.drawable.ic_arrow_down);
        }
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth() + 5, drawable.getMinimumHeight());
        tv_title.setCompoundDrawables(null, null, null, drawable);

    }
}
