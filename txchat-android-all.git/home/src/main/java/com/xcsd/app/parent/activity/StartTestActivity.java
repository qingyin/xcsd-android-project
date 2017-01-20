package com.xcsd.app.parent.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.activity.CocosJSActivity;
import com.tuxing.app.activity.RadaActivity;
import com.tuxing.app.activity.TestActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.event.LearningAbilityEvent;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.HomeWorkDetailsAdapter;
import com.xcsd.rpc.proto.EventType;
import com.yixia.camera.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.maxwin.view.XListView;

public class StartTestActivity extends BaseActivity implements View.OnClickListener {

    public static final String LOG_TAG = StartTestActivity.class.getSimpleName();

    private Button bt_start_work;
    private long gardenId;
    private long testid;
    private boolean isfirsttest = false;
    private String gamelist = "";
    private List<GameLevel> GameLevels;


    private RelativeLayout activity_bg;
    private LinearLayout btnTitleLeft;
    public static StartTestActivity instance = null;

    private boolean isRadaActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);
        instance = this;

        init();
        getData();

        CocosJSActivity.loadNativeLibraries();
    }

    private void init() {
        bt_start_work = (Button) findViewById(R.id.bt_start_work);
        tv_right = (Button) findViewById(R.id.tv_right);
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        bt_start_work.setBackgroundColor(getResources().getColor(com.tuxing.app.R.color.text_parent));
        bt_start_work.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        GameLevels = new ArrayList<>();
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
        isRadaActivity = getIntent().getBooleanExtra("isRadaActivity",false);
        User loginUser = getService().getUserManager().getUserInfo(user.getUserId());
        getService().getLearningAbilityManager().GameTestRequest(loginUser.getChildUserId());
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(LearningAbilityEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case GAME_TEST_SUCCESS:
                    GameLevels = event.getGameLevels();
                    gamelist="";
                    for (int i = 0; i < event.getGameLevels().size(); i++) {
                        gamelist=gamelist+event.getGameLevels().get(i).getGameId()+"#"+event.getGameLevels().get(i).getLevel()+"$"+event.getGameLevels().get(i).getChoicelevel()+"_"+event.getGameLevels().get(i).getHasGuide()+";";
                    }
                    isfirsttest = event.getIsfirst();
                    testid = event.getTestid();
                    break;
                case GAME_TEST_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获取数据失败" + event.getMsg(), false);
                    break;

            }
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
            case R.id.btnTitleLeft:
                this.finish();
                break;
            case R.id.tv_right:
//                Intent intent = new Intent(this, SetClassWorkActivity.class);
//                startActivity(intent);
                break;
            case R.id.bt_start_work:
                if (!gamelist.isEmpty()){

//                    getService().getDataReportManager().reportEvent(EventType.GAME_TEST);
                    if (isRadaActivity){
                        CocosJSActivity.setClassLoaderFrom(RadaActivity.instance);
                    }else{
                        CocosJSActivity.setClassLoaderFrom(TestActivity.instance);
                    }
                    CocosJSActivity.goToGameTest(gamelist, isfirsttest, (int) testid);
                    if (isRadaActivity){
                        RadaActivity.instance.finish();
                    }else{
                        TestActivity.instance.finish();
                    }
                    this.finish();
                }else{
                    for (int i = 0; i < GameLevels.size(); i++) {
                        gamelist=gamelist+GameLevels.get(i).getGameId()+"#"+GameLevels.get(i).getLevel()+"$"+GameLevels.get(i).getChoicelevel()+"_"+GameLevels.get(i).getHasGuide()+";";
                    }
                    showToast("网络连接失败，请重新点击");
                }

                break;
        }
    }


}
