package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.event.HomeworkEvent;
import com.xcsd.app.teacher.R;
import com.xcsd.app.teacher.adapter.HomeWorkDetailsAdapter;
import com.xcsd.rpc.proto.EventType;


import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class SetUniteWorkActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String LOG_TAG = SetUniteWorkActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private Button bt_unite_work_submit;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tvTitle;
    private TextView tv_right;
    private SwipeListView xListView;
    private HomeWorkDetailsAdapter adapter;
    private HomeWorkDetailsAdapter adapter_change;
    private long gardenId;
    private long MemberId;
    private long classid;
    private int status;
    private int choice_position;
    boolean hasMore = false;
    int type = 0;
    private RelativeLayout activity_bg;
    private List<HomeWorkDetail> contentDatas;
    private List<GameLevel> gameLevels;
    private List<GameLevel> gameLevels_choice;
    ArrayList<String> list;
    public static SetUniteWorkActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_unite_work_layout);
        instance = this;
        init();
        getData();
    }

    private void init() {
//        setTitle("学能作业");
//        setLeftBack("", true, false);
//        setRightNext(true, "布置",0);
        gameLevels = new ArrayList<GameLevel>();
        gameLevels_choice = new ArrayList<GameLevel>();
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tv_right = (TextView) findViewById(R.id.tv_right);
        xListView = (SwipeListView) findViewById(R.id.home_work_list_dateils);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
//        bt_unite_work_submit = (Button) findViewById(R.id.bt_unite_work_submit);
//        bt_unite_work_submit.setOnClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        updateAdapter();
        SysConstants.game_Level.clear();
        SysConstants.GAMELEVEL = 0;
        adapter_change = new HomeWorkDetailsAdapter(this, gameLevels_choice, true,false);
    }


    @Override
    protected void onResume() {
        isActivity = true;
//        initData();
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    public void initData() {
//        获取服务器数据
//        xListView.startRefresh();
        MemberId = getIntent().getLongExtra("MemberId", 0);
        classid = getIntent().getLongExtra("classid", 0);
        getService().getHomeWorkManager().get_game_list(MemberId, classid);
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            List<GameLevel> tempDatas = new ArrayList<GameLevel>();
            switch (event.getEvent()) {
                case HOMEWORK_GAME_SUCCESS:
                    tempDatas = event.getGlList();
                    getResresh(tempDatas);
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);
//                    getService().getHomeWorkManager().getHistoryHomeWorkList();
                    break;
                case HOMEWORK_GAME_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    xListView.stopRefresh();
                    showAndSaveLog(TAG, "获取最新的数据失败" + event.getMsg(), false);
                    break;
                case HOMEWORK_SEND_GAME_SUCCESS:
                    getService().getDataReportManager().reportEventBid(EventType.UNIFIED_HOMEWORK, classid+"");
                    SetClassWorkActivity.instance.finish();
                    showToast("作业发送成功");
                    this.finish();
                    showAndSaveLog(TAG, "获取最新活动数据成功 size = " + contentDatas.size(),
                            false);
//                    getService().getHomeWorkManager().getHistoryHomeWorkList();
                    break;
                case HOMEWORK_SEND_GAME_FAILED:
                    showToast(event.getMsg());
                    updateAdapter();
                    xListView.stopRefresh();
                    showToast("作业发送失败");
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
            adapter = new HomeWorkDetailsAdapter(this, gameLevels, true,false);
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
            GameLevel infoitem = gameLevels.get(position);
            long gamelevel = infoitem.getLevel();
            long gameId = infoitem.getGameId();
            if (SysConstants.GAMELEVEL < SysConstants.GAMEmax) {
                Intent intent = null;
                intent = new Intent(this, SetGameLevelActivity.class).putExtra("gamelevel", gamelevel).putExtra("position", position).putExtra("gameId", gameId);
                startActivityForResult(intent, 0);
            } else {
                if (!infoitem.getChoicelevel().equals("")) {
                    Intent intent = new Intent(this, SetGameLevelActivity.class).putExtra("gamelevel", gamelevel).putExtra("position", position).putExtra("gameId", gameId);
                    startActivityForResult(intent, 0);
                } else {
//                    默认写五关
//                    showToast("自主作业每天不超过"+SysConstants.GAMEmax+"关");
                    showToast("自主作业每天不超过"+"5关");
                }
            }
        }
    }


    @Override
    public void getData() {
        if (user != null)
            gardenId = user.getGardenId();
        initData();
    }

    public void setType(boolean b) {
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnTitleLeft:
                this.finish();
                break;
            case R.id.tv_right:
                if (!SysConstants.game_Level.isEmpty()&&SysConstants.game_Level.size()>0) {
                    List<com.xcsd.rpc.proto.GameLevel> gameLevelsList = new ArrayList<>();
                    for (int i = 0; i < SysConstants.game_Level.size(); i++) {
                        com.xcsd.rpc.proto.GameLevel cell = new com.xcsd.rpc.proto.GameLevel.Builder()
                                .gameId(SysConstants.game_Level.get(i).getGameId())
                                .level(SysConstants.game_Level.get(i).getLevel())
                                .build();
                        gameLevelsList.add(cell);
                    }
                    getService().getHomeWorkManager().send_unified(MemberId, classid, gameLevelsList);
                    showProgressDialog(this, "", true, null);
                }else{
                    showToast("请选择关卡");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，回传的是RESULT_OK
            case RESULT_OK:
                int i = data.getIntExtra("position", 0);
//                showToast(i + "");
                if (!data.getStringExtra("choice").equals("")) {
                    if (data.getStringExtra("choice").equals("0")){
                        gameLevels.get(i).setChoicelevel("");
                    }else{
                        gameLevels.get(i).setChoicelevel(data.getStringExtra("choice"));
                    }
//                    if (gameLevels.get(0).getChoicelevel().equals("")) {
//                        GameLevel gameLevelchange = gameLevels.get(i);
//                        GameLevel gameLevelone = gameLevels.get(0);
//                        gameLevels.add(gameLevelchange);
//                        gameLevels.remove(i+1);
//                    } else if (gameLevels.get(1).getChoicelevel().equals("")) {
//                        GameLevel gameLevelchange = gameLevels.get(i);
//                        GameLevel gameLevelone = gameLevels.get(1);
//                        gameLevels.set(1, gameLevelchange);
//                        gameLevels.set(i, gameLevelone);
//                    }
                }
                if (gameLevels_choice.size()>0){
                    gameLevels_choice.clear();
                }else{

                }
                for (int j=0;j<gameLevels.size();j++){
                    if (!gameLevels.get(j).getChoicelevel().equals("")){
                        gameLevels_choice.add(gameLevels.get(j));
                    }
                }
//                gameLevels_choice.add(gameLevels.get(i));
                for (int k=0;k<gameLevels.size();k++){
                    if (gameLevels.get(k).getChoicelevel().equals("")){
                        gameLevels_choice.add(gameLevels.get(k));
                    }
                }
                gameLevels.clear();
                gameLevels.addAll(gameLevels_choice);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
