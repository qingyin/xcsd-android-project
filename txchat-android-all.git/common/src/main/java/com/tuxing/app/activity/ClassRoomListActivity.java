package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.tuxing.app.R;
import com.tuxing.app.adapter.ClassRoomAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.rpc.proto.CourseLesson;
import com.tuxing.rpc.proto.LessonLiveStatus;
import com.tuxing.sdk.db.entity.CourseLessonBean;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.DataReportEvent;
import com.tuxing.sdk.event.course.CourseLessonEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.utils.CollectionUtils;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

/**
 * ΢微课堂
 */
public class ClassRoomListActivity extends BaseActivity implements
        IXListViewListener, OnItemClickListener {
    public static final String TAG = ClassRoomListActivity.class.getSimpleName();
    private XListView mListView;
    private ClassRoomAdapter adapter;
//    private List<CourseLesson> mDatas = new ArrayList<>();
    private List<CourseLessonBean> mDatas = new ArrayList<>();
    private boolean hasMore = false;
    private RelativeLayout activity_bg;
    private int currentPage = 1;

    String token = "";
    private HttpClient httpClient = HttpClient.getInstance();
    List<CourseLesson> lessons = new ArrayList<>();
    List<CourseLessonBean> courseLessonBeans;

    private boolean isonline = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.classroom_layout);
        init();

        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "course");
    }

    private void init() {
        setTitle(getString(R.string.tab_classroom));
        String titlename = "";
        titlename = getIntent().getStringExtra("name");
        if (!titlename.equals("")) {
            setTitle(titlename);
        }
        setLeftBack("", true, false);
        setRightNext(true, "", 0);
        mListView = (XListView) findViewById(R.id.classroom_list);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
//        mListView.startRefresh();
        updateAdapter();
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
        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "course");
        super.finish();
    }

    @Override
    protected void onResume() {
        isActivity = true;
        initData();
//        getRefresh(mDatas);
        super.onResume();
    }

    private void initData() {
//        LoginUser user = GlobalDbHelper.getInstance().getLoginUser();
//        if(user != null){
//            token = user.getToken();
////            user.setToken("cd070eff111e4e40862746b52067734e");
//            httpClient.setHostAddress("api.tx2010.com", 80);
//        }

        courseLessonBeans = UserDbHelper.getInstance().getCourseLesson();
//        lessons = new A、rrayList<CourseLesson>();
//        if (courseLessonBeans.size() > 0) {
//            for (int i = 0; i < courseLessonBeans.size(); i++) {
//                LessonLiveStatus lessonLiveStatus = LessonLiveStatus.T_NOT_START;
//                CourseLessonBean courseLesson = courseLessonBeans.get(i);
//                if (courseLesson.getLiveStatus() == 0) {
//                    lessonLiveStatus = LessonLiveStatus.T_NOT_START;
//                } else if (courseLesson.getLiveStatus() == 1) {
//                    lessonLiveStatus = LessonLiveStatus.T_START;
//                } else if (courseLesson.getLiveStatus() == 2) {
//                    lessonLiveStatus = LessonLiveStatus.T_STOP;
//                }
//                CourseLesson bean = new CourseLesson(courseLesson.getId(),
//                        courseLesson.getCreateOn(), courseLesson.getUpdateOn(), courseLesson.getCourseId(),
//                        courseLesson.getTitle(), courseLesson.getStartOn(), courseLesson.getEndOn(), courseLesson.getVideoUrl(),
//                        courseLesson.getHits(), courseLesson.getLiveHits(), lessonLiveStatus, null, courseLesson.getPic(),
//                        courseLesson.getDuration(), courseLesson.getResourceType());
////            bean.setId(courseLesson.id);
//                lessons.add(bean);
//            }
            getRefresh(courseLessonBeans);
//        }
        showProgressDialog(this, "", true, null);
        getService().getCourseManager().getLatestCourseLessonList(currentPage);

    }


    @Override
    public void onRefresh() {
        currentPage = 1;
        getService().getCourseManager().getLatestCourseLessonList(currentPage);
    }

    @Override
    public void onLoadMore() {
        currentPage += 1;
        getService().getCourseManager().getLatestCourseLessonList(currentPage);
    }


    private void getRefresh(List<CourseLessonBean> refreshList) {
        if (currentPage == 1) {
            mDatas.clear();
        }
        if (!CollectionUtils.isEmpty(refreshList)) {
            mDatas.addAll(refreshList);
        }
        updateAdapter();
        mListView.stopRefresh();
        mListView.stopLoadMore();

    }

    public void getLoadMore(List<CourseLessonBean> list) {
        if (!CollectionUtils.isEmpty(list)) {
            mDatas.addAll(list);
        }
        updateAdapter();
        mListView.stopLoadMore();
    }

    public void showFooterView() {
        if (hasMore)
            mListView.setPullLoadEnable(true);
        else
            mListView.setPullLoadEnable(false);
    }


    public void updateAdapter() {
        if (mDatas != null && mDatas.size() > 0) {
            activity_bg.setVisibility(View.GONE);
        } else {
            activity_bg.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            adapter = new ClassRoomAdapter(this, mDatas);
            mListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        position = position - 1;
        if (position >= 0 && isonline) {
            //资源类型 1音频 2视频
//            if (mDatas.get(position).resourceType==1){
//                Intent intent = new Intent(mContext, EducAudioPlayerActivity.class);
//                intent.putExtra("courseLesson", mDatas.get(position));
//                startActivity(intent);
//            }else{
            Intent intent = new Intent(mContext, ClassRoomPlayerActivity.class);
//            intent.putExtra("courseLesson", mDatas.get(position));
            intent.putExtra("courseLesson", lessons.get(position));
            startActivity(intent);
//            }
        }else{
            showToast("网络连接失败");
        }
    }


    public void onEventMainThread(CourseLessonEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case GET_COURSELESSON_LIST_SUCCESS:
                    hasMore = event.hasMore();
//                    getRefresh(event.getCourseLesson());
                    if (event.getCourseLesson().size()>0){
                        lessons.clear();
                        lessons.addAll(event.getCourseLesson());
                    }
                    getRefresh(event.getmCourseLessonBean());
                    showAndSaveLog(TAG, "获得课程视频列表成功 ", false);
                    break;
                case GET_COURSELESSON_LIST_FAILED:
                    showToast(event.getMsg());
                    showToast("网络连接失败");
                    hasMore = false;
//                    getRefresh(lessons);
                    isonline = false;
                    mListView.stopLoadMore();
                    mListView.stopRefresh();
                    showFooterView();
                    showAndSaveLog(TAG, "获得课程视频列表失败", false);
                    break;
            }
        }
    }

}
