package com.xcsd.app.parent.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.activity.CocosJSActivity;
import com.tuxing.app.activity.GameWebSubUrlActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.HomeworkEvent;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.HomeWorkDetailsAdapter;
import com.yixia.camera.util.Log;

import java.util.ArrayList;
import java.util.List;
import me.maxwin.view.XListView;

public class HomeWorkDetailsActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String LOG_TAG = HomeWorkDetailsActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private Button bt_start_work;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tvTitle;
    private TextView tv_listview_title;
    private TextView tv_score_number;
    private TextView tv_right_tips;
    private Button tv_right;
    private SwipeListView xListView;
    private HomeWorkDetailsAdapter adapter;
    private long childUserId;
    private long MemberId;
    private int status;
    private boolean hasMore = false;
    private RelativeLayout activity_bg;
    private RelativeLayout rl_score_show;
    private RelativeLayout rl_bottom;
    private List<HomeWorkDetail> contentDatas;
    private List<GameLevel> gameLevels;
    ArrayList<String> list;
    public static HomeWorkDetailsActivity instance = null;
    List<GameLevel> tempDatas;
    String gamelist = "";
    String game = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home_work_details);
            instance = this;

            init();
            getData();
            CocosJSActivity.loadNativeLibraries();
        }

    private void init() {
//        setTitle("学能作业");
//        setLeftBack("", true, false);
//        setRightNext(true, "布置",0);
        gameLevels = new ArrayList<GameLevel>();
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        bt_start_work = (Button) findViewById(R.id.bt_start_work);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_right = (Button) findViewById(R.id.tv_right);
        tv_listview_title = (TextView) findViewById(R.id.tv_listview_title);
        tv_score_number = (TextView) findViewById(R.id.tv_score_number);
        tv_right_tips = (TextView) findViewById(R.id.tv_right_tips);
        rl_score_show = (RelativeLayout) findViewById(R.id.rl_score_show);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        xListView = (SwipeListView) findViewById(R.id.home_work_list_dateils);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);
//        xListView.setXListViewListener(this);
//        xListView.setOnItemClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        bt_start_work.setBackgroundColor(getResources().getColor(com.tuxing.app.R.color.text_parent));
        bt_start_work.setOnClickListener(this);
        tv_right.setOnClickListener(this);

        MemberId = getIntent().getLongExtra("MemberId", 0);
        status = getIntent().getIntExtra("status", 0);
        if (status==0){
            tv_listview_title.setVisibility(View.GONE);
            bt_start_work.setVisibility(View.GONE);
            rl_score_show.setVisibility(View.VISIBLE);
            rl_bottom.setVisibility(View.GONE);
        }

        updateAdapter();
    }


    @Override
    protected void onResume() {
        isActivity = true;
        initData();
        super.onResume();
    }

    public void initData() {
//        获取服务器数据
//        xListView.startRefresh();
        getService().getHomeWorkManager().getHomeWorkDateils(MemberId);
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            tempDatas = new ArrayList<GameLevel>();
            switch (event.getEvent()) {
                case HOMEWORK_DATEILS_LIST_HISTORY_SUCCESS:
                    hasMore = event.getHasMore();
                    tempDatas=event.getGlList();
                    childUserId = event.getHwDetails().getChildUserId();
                    tvTitle.setText(event.getHwDetails().getTitle());
                    tv_name.setText(event.getHwDetails().getSenderName());
                    String datatime = event.getHwDetails().getSendTime() + "";
                if (datatime != null && datatime != "") {
                    String Time = Utils.getDateTime(mContext, event.getHwDetails().getSendTime());
                    tv_time.setText(Time);
                }
                    if (status==0){
                        tv_score_number.setText(event.getHwDetails().getTotalScore()+"分");
                        tv_right_tips.setText("作业满分为"+event.getHwDetails().getMaxScore()+"分");
                    }
                    if (gamelist.equals("")) {
                        for (int i = 0; i < tempDatas.size(); i++) {
                            game = tempDatas.get(i).getGameId() + "#" + tempDatas.get(i).getLevel() + "$" + 1 + "_" + tempDatas.get(i).getHasGuide() + ";";
                            gamelist = gamelist + game;
                        }
                    }
                    getResresh(tempDatas);
//                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
//                            false);
//                    getService().getHomeWorkManager().getHistoryHomeWorkList();
                    break;
                case HOMEWORK_DATEILS_LIST_HISTORY_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    xListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;

            }
        }
    }

    @Override
    public void onRefresh() {
//        getService().getHomeWorkManager().getHomeWorkSendListLatest();
//        showProgressDialog(this,"",true,null);
    }

    private void getResresh(List<GameLevel> refreshlist) {
        // TODO 加载数据
        if (refreshlist != null && refreshlist.size() > 0) {
            gameLevels.clear();
            gameLevels.addAll(0, refreshlist);
        }
        updateAdapter();
        xListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
//        if (contentDatas.size() > 0) {
//            long lastId = contentDatas.get(contentDatas.size() - 1).getHomeworkId();
////            加载更多，提交参数不明
//            getService().getHomeWorkManager().getHomeWorkSendListHistory(lastId);
//        } else {
//            xListView.stopLoadMore();
//        }
        xListView.stopLoadMore();
    }

    public void getLoadMore(List<HomeWorkDetail> list) {
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
        if (gameLevels != null && gameLevels.size() > 0) {
            activity_bg.setVisibility(View.GONE);
        } else {
            activity_bg.setVisibility(View.GONE);
        }
        if (adapter == null) {
            if (status==0){
                adapter = new HomeWorkDetailsAdapter(this, gameLevels,true);
            }else{
                adapter = new HomeWorkDetailsAdapter(this, gameLevels,false);

            }
            xListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        showFooterView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;
        if (position >= 0) {
//            HomeWorkDetail infoitem = contentDatas.get(position);
//            workId = infoitem.getHomeworkId();
//            Intent intent = new Intent(this, WorkScoreActivity.class).putExtra("homework_id", workId);
//            startActivity(intent);
        }
    }


    @Override
    public void getData() {
        if (user != null)
//            gardenId = user.getGardenId();
        initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnTitleLeft:
                this.finish();
                break;
            case R.id.tv_right:
//                Intent intent = new Intent(this, SetClassWorkActivity.class);
//                startActivity(intent);
                break;
            case R.id.bt_start_work:
//                跳转开始游戏
//                String token = null;
//                LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
//                if (loginUser != null) {
//                    token = loginUser.getToken();
//                }
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(SysConstants.H5_GAME_HOST_URL);
//                stringBuilder.append("?");
//                stringBuilder.append("action=10002&");
//                stringBuilder.append("memberId=" + MemberId + "&");
//                stringBuilder.append("token=" + token + "&");
//                stringBuilder.append("isTaskState=" + "3");
//                Log.i("--------shan", stringBuilder.toString());
////            H5原生
//            GameWebSubUrlActivity.invoke(mContext, stringBuilder.toString(),
//                    getString(com.tuxing.app.R.string.activity));

//                String gamelist = "";
//                String game = null;
//                for (int i = 0; i < tempDatas.size(); i++) {
//                    game = tempDatas.get(i).getGameId() + "#" + tempDatas.get(i).getLevel() + "$" + 1 + "_" + tempDatas.get(i).getHasGuide() + ";";
//                    gamelist = gamelist + game;
//                }
                if (!gamelist.isEmpty()&&!gamelist.equals("")) {

                    CocosJSActivity.setClassLoaderFrom(StudyHomeWorkActivity.instance);
                    CocosJSActivity.goToGameHomeWork(gamelist,MemberId,childUserId);
                    StudyHomeWorkActivity.instance.finish();
                    this.finish();
                } else {
                    gamelist="";
                    for (int i = 0; i < tempDatas.size(); i++) {
                        game = tempDatas.get(i).getGameId() + "#" + tempDatas.get(i).getLevel() + "$" + 1 + "_" + tempDatas.get(i).getHasGuide() + ";";
                        gamelist = gamelist + game;
                    }
                    showToast("网络连接失败，请重新点击");
                }

                break;
        }
    }


}
