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
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;


public class ExplainActivity extends BaseActivity implements View.OnClickListener {

    public static final String LOG_TAG = ExplainActivity.class.getSimpleName();

    private RelativeLayout rlTitle;
    private RelativeLayout rl_learn_explain;
    private RelativeLayout rl_learn_ab;
    private RelativeLayout rl_learn_pbsc;
    private RelativeLayout rl_learn_five;
    private RelativeLayout rl_learn_our_team;
    private RelativeLayout rl_back;
    private TextView test_back_txt;
    private ImageView test_back_img;
    private Button tv_right;
    ArrayList<String> list;
    public static ExplainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        instance = this;

        init();
        getData();
    }

    private void init() {
        TAG = ExplainActivity.class.getSimpleName();
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        tv_right = (Button) findViewById(R.id.tv_right);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_right.setOnClickListener(this);

        rl_learn_explain = (RelativeLayout) findViewById(R.id.rl_learn_explain);
        rl_learn_explain.setOnClickListener(this);
        rl_learn_ab = (RelativeLayout) findViewById(R.id.rl_learn_ab);
        rl_learn_ab.setOnClickListener(this);
        rl_learn_pbsc = (RelativeLayout) findViewById(R.id.rl_learn_pbsc);
        rl_learn_pbsc.setOnClickListener(this);
        rl_learn_five = (RelativeLayout) findViewById(R.id.rl_learn_five);
        rl_learn_five.setOnClickListener(this);
        rl_learn_our_team = (RelativeLayout) findViewById(R.id.rl_learn_our_team);
        rl_learn_our_team.setOnClickListener(this);

        test_back_txt = (TextView) findViewById(R.id.test_back_txt);
        test_back_img = (ImageView) findViewById(R.id.test_back_img);
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);

        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            test_back_txt.setTextColor(getResources().getColor(R.color.text_teacher));
            test_back_img.setImageResource(R.drawable.arrow_left_t);
        }else{
//            rl_learn_explain.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume() {
        isActivity = true;
        initData();
        super.onResume();
    }

    public void initData() {
    }




    @Override
    public void getData() {
        if (user != null)
        initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId()==R.id.rl_learn_explain){
            Intent intent = new Intent(this, ExplainHowActivity.class).putExtra("type",4);
            startActivity(intent);
        }
        if(v.getId()==R.id.rl_learn_ab){
            Intent intent = new Intent(this, ExplainHowActivity.class).putExtra("type",0);
            startActivity(intent);
        }
        if(v.getId()==R.id.rl_learn_pbsc){
            Intent intent = new Intent(this, ExplainHowActivity.class).putExtra("type",1);
            startActivity(intent);
        }
        if(v.getId()==R.id.rl_learn_five){
            Intent intent = new Intent(this, ExplainHowActivity.class).putExtra("type",2);
            startActivity(intent);
        }
        if(v.getId()==R.id.rl_learn_our_team){
            Intent intent = new Intent(this, ExplainHowActivity.class).putExtra("type",3);
            startActivity(intent);
        }
    }
}
