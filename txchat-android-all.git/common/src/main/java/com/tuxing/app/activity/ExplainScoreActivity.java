package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.adapter.ExplainAdapter;
import com.tuxing.app.adapter.GameScoreAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.db.entity.Game_Score;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.event.LearningAbilityEvent;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.Ability;
import com.yixia.camera.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;


public class ExplainScoreActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String LOG_TAG = ExplainScoreActivity.class.getSimpleName();

    private RelativeLayout rlTitle;
    private Button tv_right;
    private TextView tv_explain_how;
    private long gardenId;
    private long workId;
    private boolean hasMore = false;
    private RelativeLayout activity_bg;
    ArrayList<String> list;
    public static ExplainScoreActivity instance = null;
    private List<Game_Score> contentDatas;
    private SwipeListView xListView;
    private GameScoreAdapter adapter;
    int type = 0;
    Long childid =null;
    private TextView tvTitle1;
    private TextView tvTitle;

    private RelativeLayout rl_back;
    private TextView test_back_txt;
    private ImageView test_back_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_score);
        instance = this;

        init();
        getData();
    }

    private void init() {
//        setTitle("学能作业");
//        setLeftBack("", true, false);
//        setRightNext(true, "布置",0);
        TAG = ExplainActivity.class.getSimpleName();
        contentDatas = new ArrayList<Game_Score>();
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        tv_right = (Button) findViewById(R.id.tv_right);
        xListView = (SwipeListView) findViewById(R.id.home_acticity_list);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        tvTitle1 = (TextView) findViewById(R.id.tvTitle1);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        test_back_txt = (TextView) findViewById(R.id.test_back_txt);
        test_back_img = (ImageView) findViewById(R.id.test_back_img);

        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            test_back_txt.setTextColor(getResources().getColor(R.color.text_teacher));
            test_back_img.setImageResource(R.drawable.arrow_left_t);
        }else{
        }
        tv_right.setOnClickListener(this);
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
        type= getIntent().getIntExtra("type",0);
        childid= getIntent().getLongExtra("childid",0);
        Ability ability = null;
        if (type == 0) {
            //            空间思维
            ability = Ability.SpatialThinking;
            tvTitle.setText("空间思维");
        } else if (type == 1) {
            //反应力
            ability = Ability.Reaction;
            tvTitle.setText("反应力");
        } else if (type == 2) {
            //            记忆力
            ability = Ability.Memory;
            tvTitle.setText("记忆力");
        } else if (type == 3) {
//            注意力
            ability = Ability.Attention;
            tvTitle.setText("注意力");
        } else if (type == 4) {
//            逻辑力
            ability = Ability.Reasoning;
            tvTitle.setText("逻辑力");
        }
        User loginUser = getService().getUserManager().getUserInfo(user.getUserId());
        getService().getLearningAbilityManager().GameScoreRequest(childid, ability);
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(LearningAbilityEvent event) {
        if (isActivity) {
            disProgressDialog();
            List<Game_Score> tempDatas = new ArrayList<Game_Score>();
            switch (event.getEvent()) {
                case GAME_SCORE_SUCCESS:
                    tempDatas = event.getGameLevel();
                    if (tempDatas != null && tempDatas.size() > 0) {
                        contentDatas.clear();
                        contentDatas.addAll(tempDatas);
                        updateAdapter();
                    }
                    tvTitle1.setText("学能总积分: "+event.getTotalScore()+"分");
//                    getService().getHomeWorkManager().getHomeWorkSendListLatest();
                    showAndSaveLog(TAG, "获取本地活动数据成功 size = " + contentDatas.size(),
                            false);
                    break;
                case GAME_SCORE_FAILED:
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
        getService().getHomeWorkManager().getHomeWorkSendListLatest();
        showProgressDialog(this, "", true, null);
    }

    private void getResresh(List<Game_Score> refreshList) {
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
//            long lastId = contentDatas.get(contentDatas.size() - 1).getHomeworkId();
////            加载更多，提交参数不明
//            getService().getHomeWorkManager().getHomeWorkSendListHistory(lastId);
        } else {
            xListView.stopLoadMore();
        }
        xListView.stopLoadMore();
    }

    public void getLoadMore(List<Game_Score> list) {
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
            activity_bg.setVisibility(View.GONE);
        }
        if (adapter == null) {
            adapter = new GameScoreAdapter(this, contentDatas);
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
//            HomeWorkClass infoitem = contentDatas.get(position);
//            workId = infoitem.getHomeworkId();
//            Intent intent = new Intent(this, WorkScoreActivity.class).putExtra("homework_id", workId);
//            startActivity(intent);
        }
    }


    @Override
    public void getData() {
        if (user != null)
            gardenId = user.getGardenId();
        initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_right) {
            Intent intent = new Intent(this, ExplainHowActivity.class);
            startActivity(intent);
        }
    }

}
