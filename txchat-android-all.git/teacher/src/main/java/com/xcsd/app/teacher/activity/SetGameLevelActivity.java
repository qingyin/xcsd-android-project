package com.xcsd.app.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.ToastUtil;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.xcsd.app.teacher.R;
import com.xcsd.app.teacher.adapter.HomeWorkDetailsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SetGameLevelActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String LOG_TAG = SetGameLevelActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private TextView tvTitle;
    private TextView tv_right;
    private Button bt_setgame_submit;
    private SwipeListView xListView;
    private GameLevelAdapter adapter;
    private long gamelevel;
    private long gardenId;
    private int position;
    private long gameId;
    private int status;
    private boolean hasMore = false;
    private RelativeLayout activity_bg;
    private ArrayList<GameLevel> choiceGame;
    private List<GameLevel> gameLevels;
    ArrayList<String> Stringlist;
    public static SetGameLevelActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_game_level_layout);
        instance = this;

        init();
        getData();
    }

    private void init() {
        gameLevels = new ArrayList<GameLevel>();
        choiceGame = new ArrayList<GameLevel>();
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
//        bt_setgame_submit = (Button) findViewById(R.id.bt_setgame_submit);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tv_right = (TextView) findViewById(R.id.tv_right);
        xListView = (SwipeListView) findViewById(R.id.home_work_list_dateils);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);
        xListView.setOnItemClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        tv_right.setOnClickListener(this);
//        bt_setgame_submit.setOnClickListener(this);

        adapter = new GameLevelAdapter(this, gameLevels, false);
        xListView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
    }

    public void initData() {
        gamelevel = getIntent().getLongExtra("gamelevel", 0);
        position = getIntent().getIntExtra("position", 0);
        gameId = getIntent().getLongExtra("gameId", 0);
        long level = gamelevel;
        for (int i = 0; i < level; i++) {
            GameLevel levels = new GameLevel();
            levels.setLevel(i + 1);
            levels.setStars(0);
            levels.setGameId(gameId);
//            levels.setStars(1);
            gameLevels.add(levels);
        }
        if (!SysConstants.game_Level.isEmpty() && SysConstants.game_Level.size() > 0) {
            for (int i = 0; i < level; i++) {
                for (int j = 0; j < SysConstants.game_Level.size(); j++) {
                    if (SysConstants.game_Level.get(j).getLevel() == gameLevels.get(i).getLevel() && SysConstants.game_Level.get(j).getGameId() == gameLevels.get(i).getGameId()) {
                        gameLevels.get(i).setStars(1);
                    }
                }
            }
        }
        status = SysConstants.GAMELEVEL;
        adapter.notifyDataSetChanged();
//        showProgressDialog(this, "", true, null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;
        if (position >= 0) {
//            GameLevel infoitem = gameLevels.get(position);
//            if (infoitem.getStars()==1){
//                infoitem.setStars(0);
//                for (int i=0;i<choiceGame.size();i++){
//                    if(choiceGame.get(i).getLevel()==infoitem.getLevel()){
//                        choiceGame.remove(i);
//                    }
//                }
//            }else if (infoitem.getStars()==0){
//                infoitem.setStars(1);
//                choiceGame.add(infoitem);
//            }
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
        switch (v.getId()) {
            case R.id.tv_right:
//            case R.id.bt_setgame_submit:
                String choice = "";
                for (int i = 0; i < gameLevels.size(); i++) {
                    if (gameLevels.get(i).getStars() == 1) {
//                        gameLevels.get(i).setGameId(gameId);
                        choiceGame.add(gameLevels.get(i));
                        if(i==gameLevels.size()-1){
                            choice = choice + gameLevels.get(i).getLevel() + " ";
                        }else{
                            choice = choice + gameLevels.get(i).getLevel() + ",";
                        }
                    }
                }
//                SysConstants.GAMELEVEL = SysConstants.GAMELEVEL + choiceGame.size();
                if (SysConstants.GAMELEVEL > SysConstants.GAMEmax) {
                    showToast("自主作业每天不超过"+SysConstants.GAMEmax+"关");
                    return;
                } else {
                    Intent intent = new Intent();
//                    循环删除这个游戏ID的数组
                    if (choiceGame.size() > 0) {
                        for (int i = 0; i < SysConstants.game_Level.size(); i++) {
                            if (SysConstants.game_Level.get(i).getGameId() == choiceGame.get(0).getGameId()) {
                                SysConstants.game_Level.remove(i);
                            }
                        }
//                    循环重新添加这个游戏关卡
                        for (int i = 0; i < choiceGame.size(); i++) {
                            SysConstants.game_Level.add(choiceGame.get(i));
                        }
//                    }else{
//
//                    }
                    }else{
                        for (int i = 0; i < SysConstants.game_Level.size(); i++) {
                            if (SysConstants.game_Level.get(i).getGameId() == gameId) {
                                SysConstants.game_Level.remove(i);
                            }
                        }
                        choice="0";
                    }
                    intent.putExtra("position", position);
                    intent.putExtra("choice", choice);
                    setResult(RESULT_OK, intent);
                }
                this.finish();
                break;
            case R.id.btnTitleLeft:
                SysConstants.GAMELEVEL = status;
                this.finish();
//                Intent intent = new Intent(this, SetClassWorkActivity.class);
//                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SysConstants.GAMELEVEL = status;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    class GameLevelAdapter extends BaseAdapter {

        private static final String TAG = "HomeWorkDetailsAdapter";
        private LayoutInflater inflater;
        public Context mContext;
        public List<GameLevel> contentDatas;
        public boolean bl = false;
        int i = 0;

        public GameLevelAdapter(Context mContext, List<GameLevel> contentDatas, boolean b) {
            this.mContext = mContext;
            this.contentDatas = contentDatas;
            this.bl = b;
        }

        @Override
        public int getCount() {
            return contentDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return contentDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HashMap<Integer, View> cuoluan = new HashMap<Integer, View>();
            ViewHolder holder = null;
            if (cuoluan.get(position) != null) {
                convertView = cuoluan.get(position);
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_game_level_layout, parent, false);
//                holder = new ViewHolder();
                holder.xing_ll = (RelativeLayout) convertView.findViewById(R.id.xing_ll);
                holder.xing_ll_show = (RelativeLayout) convertView.findViewById(R.id.xing_ll_show);
                holder.comment_dialog_xing_1 = (RadioButton) convertView.findViewById(R.id.comment_dialog_xing_1);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                cuoluan.put(position, convertView);
                convertView.setTag(holder);
            }

            if (contentDatas.size() > 0) {
                GameLevel data = contentDatas.get(position);
                if (data != null) {
                    holder.tvTitle.setText("第" + data.getLevel() + "关");
                    holder.comment_dialog_xing_1.setClickable(true);
                    if (data.getLevel() % 10 == 0) {
                        holder.xing_ll_show.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (contentDatas.get(position).getStars() == 1) {
                holder.comment_dialog_xing_1.setChecked(true);
            }
            final ViewHolder finalHolder = holder;
            finalHolder.comment_dialog_xing_1.setClickable(false);
            holder.xing_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalHolder.comment_dialog_xing_1.isChecked()) {
                        finalHolder.comment_dialog_xing_1.setChecked(false);
                        SysConstants.GAMELEVEL = SysConstants.GAMELEVEL - 1;
                        contentDatas.get(position).setStars(0);
                    } else {
                        if (SysConstants.GAMELEVEL < SysConstants.GAMEmax) {
                            finalHolder.comment_dialog_xing_1.setChecked(true);
                            contentDatas.get(position).setStars(1);
                            SysConstants.GAMELEVEL = SysConstants.GAMELEVEL + 1;
                        } else {
//                                finalHolder.xing_ll.setClickable(false);
                            showToast("自主作业每天不超过"+SysConstants.GAMEmax+"关");
                            return;
                        }
                    }
//                    SysConstants.GAMELEVEL = i;
                }
            });
            return convertView;
        }

        private class ViewHolder {

            /**
             * 班级名称，作业类型，时间，完成度，完成人数
             */
            TextView tvTitle;
            RelativeLayout xing_ll;
            RelativeLayout xing_ll_show;
            RadioButton comment_dialog_xing_1;

        }
    }

}
