package com.xcsd.app.parent.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tuxing.app.base.BaseFragment;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.StudyHomeWorkRankFrgAdapter;
import com.xcsd.app.parent.util.SysUtil;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;
import com.tuxing.sdk.event.HomeworkEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by shan on 2016/4/11.
 */
public class StudyHomeworkRankFragment extends BaseFragment {
    View rootView;
    private ListView mListView;
    private boolean isActivity = false;
    private List<HomeWorkUserRank> userRankList;
    private StudyHomeWorkRankFrgAdapter adapter;
    private boolean first = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        if (rootView == null) {

            rootView = inflater.inflate(R.layout.home_study_homework_frg_rank_layout, null);
        }
        intView();

        //  initData();

        return rootView;
    }

    private void show() {
        adapter = new StudyHomeWorkRankFrgAdapter(getActivity(), userRankList);
        mListView.setAdapter(adapter);
    }

    private void initData() {
        Bundle data = getArguments();
        userRankList = (List<HomeWorkUserRank>) data.get("homeranklist");

    }

    public void onEventMainThread(HomeworkEvent event) {
        userRankList = new ArrayList<>();

        switch (event.getEvent()) {

            case HOMEWORK_RANKING_SUCCESS:
                disProgressDialog();
                userRankList = event.getHomeWorkUserRankList();
                show();
                break;
            case HOMEWORK_RANKING_FAILED:

                break;
        }


    }


    private void intView() {
        mListView = (ListView) rootView.findViewById(R.id.lv_home_study_frg_rank);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (first) {

            getService().getHomeWorkManager().getHomeworkRankingFromParent(SysUtil.getUserId());
            showProgressDialog(getActivity(), "", true, null);
            first = false;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置数据
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
