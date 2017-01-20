package com.xcsd.app.teacher.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.activity.ExplainScoreActivity;
import com.tuxing.app.activity.RadaActivity;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.base.BaseFragment;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.xcsd.app.teacher.adapter.WorkSocreShowAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

/**
 * Created by bryant on 16/4/8.
 *
 */
public class ScoreShowFragment extends BaseFragment implements XListView.IXListViewListener,AdapterView.OnItemClickListener,View.OnClickListener {

    private SwipeListView xListView;
    private WorkSocreShowAdapter adapter;
    private List<HomeWorkUserRank> contentDatas;
    private long classid;
    private boolean hasMore = false;
    private String TAG;
    private RelativeLayout activity_bg;
    public boolean isActivity = false;
    List<HomeWorkUserRank> tempDatas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_workshow, null);
//        仿MyProfileFragment模式
        initView(view);
        return view;
    }

    private void initView(View view) {
        xListView = (SwipeListView) view.findViewById(com.tuxing.app.R.id.home_acticity_list);
        activity_bg = (RelativeLayout) view.findViewById(R.id.activity_bg);
        contentDatas = new ArrayList<HomeWorkUserRank>();
        tempDatas = new ArrayList<HomeWorkUserRank>();
        xListView.setPullRefreshEnable(false);
        xListView.setPullRefreshEnable(false);
        xListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        MobclickAgent.onEvent(getActivity(), "click_my", UmengData.click_my);
//        setTitle(getResources().getString(com.tuxing.app.R.string.tab_my));
        updateAdapter();
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        isActivity = true;
        super.onResume();
    }
    public void getclassid(long class_id){
        classid = class_id;
    }

    public void initData() {
        // TODO 获取服务器数据
        List<HomeWorkUserRank> data = UserDbHelper.getInstance().getLatestHomeWorkUserRank();
        if (data.size()>0){
            for (int i=data.size()-1;i>-1;i--){
                HomeWorkUserRank homeWorkUserRank = data.get(i);
                contentDatas.add(homeWorkUserRank);
            }
            updateAdapter();
        }
        getService().getHomeWorkManager().getHomeworkRankingFromTeacherLocation();
        getService().getHomeWorkManager().getHomeworkRankingFromTeacher(classid);
        showProgressDialog(getActivity(), "", true, null);
    }
    public void ReData(long id) {
        // TODO 获取服务器数据
        getService().getHomeWorkManager().getHomeworkRankingFromTeacher(id);
        showProgressDialog(getActivity(), "", true, null);
    }

    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
//            List<HomeWorkUserRank> tempDatas = new ArrayList<HomeWorkUserRank>();
            switch (event.getEvent()) {
                case HOMEWORK_RANKING_LOCTION_SUCCESS:
                    hasMore = false;
                    tempDatas = event.getHomeWorkUserRankList();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        for (int i = 0; i < tempDatas.size(); i++) {
                            HomeWorkUserRank homeWorkUserRank = tempDatas.get(i);
                            contentDatas.add(homeWorkUserRank);
                        }
                        updateAdapter();
                    }
                    getService().getHomeWorkManager().getHomeworkRankingFromTeacher(classid);
                    break;
                case HOMEWORK_RANKING_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas = event.getHomeWorkUserRankList();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        contentDatas.addAll(tempDatas);
                        updateAdapter();
                    }else{
                        contentDatas.clear();
                        updateAdapter();
                    }
                    getResresh(tempDatas);
//                    showToast("联网请求成功返回");
                    break;
                case HOMEWORK_RANKING_FAILED:
                    showToast(event.getMsg());

                    updateAdapter();
                    xListView.stopRefresh();
                    break;
            }
        }
    }


    @Override
    public void onRefresh() {
//        getService().getContentManager().getLatestItems(gardenId,
//                Constants.CONTENT_TYPE.ACTIVITY);
    }

    private void getResresh(List<HomeWorkUserRank> refreshList) {
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

    public void getLoadMore(List<HomeWorkUserRank> list) {
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
            adapter = new WorkSocreShowAdapter(getActivity(), contentDatas);
            xListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }



    @Override
    public void getData() {
//        if(user != null)
//            gardenId = user.getGardenId();
//        initData();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;
        if(position >= 0) {
            HomeWorkUserRank contentItem = contentDatas.get(position);


            String token = null;
            LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
            if (loginUser != null) {
                token = loginUser.getToken();

            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(SysConstants.H5_GAME_HOST_URL);
            stringBuilder.append("?");
            stringBuilder.append("action=10003&");
            stringBuilder.append("memberId=" + contentItem.getUserId() + "&");
            stringBuilder.append("token=" + token + "&");
            stringBuilder.append("isChildState=" + "1");
//            WebSubUrlActivity.invoke(getActivity(), stringBuilder.toString(),
//                    contentItem.getName() + "学能成绩");
            Intent intent_logic= new Intent(getActivity(),RadaActivity.class).putExtra("childid", contentItem.getUserId()).putExtra("name",contentItem.getName());
            startActivity(intent_logic);
        }

    }
}
