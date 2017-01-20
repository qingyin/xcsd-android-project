package com.tuxing.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.polites.android.Animation;
import com.polites.android.GestureImageView;
import com.tuxing.app.R;
import com.tuxing.app.Radar.BaseRoundCornerProgressBar;
import com.tuxing.app.Radar.LineView;
import com.tuxing.app.Radar.Rada;
import com.tuxing.app.Radar.RadarView;
import com.tuxing.app.Radar.RoundCornerProgressBar;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.CustomDialog;
import com.tuxing.app.view.NewScrollView;
import com.tuxing.sdk.db.entity.AbilityDetailList;
import com.tuxing.sdk.db.entity.AbilityPoint;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.LearningAbilityEvent;
import com.tuxing.sdk.event.UpgradeEvent;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.Ability;
import com.xcsd.rpc.proto.EventType;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class RadaActivity extends BaseActivity implements View.OnClickListener {

    public static RadaActivity instance = null;
    private com.tuxing.app.Radar.Rada rada;
    private com.tuxing.app.Radar.RadarView radarview;
    private RoundCornerProgressBar progressTwo;
    private RelativeLayout rl_back;
    private TextView tv_right;
    private TextView bt_right;
    private lecho.lib.hellocharts.view.LineChartView lineChart;

    private com.tuxing.app.view.NewScrollView NewScrollView;

    private LineChartData lineData;
    public final static String[] days = new String[]{"", "1", "2", "3", "4", "5", "6",};

    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private List<AbilityPoint> point = new ArrayList<AbilityPoint>();

    private double[] data_Average = {50, 50, 50, 50, 50, 40}; //各维度分值
    private static final int MSG_DATA_CHANGE = 0x11;


    private LineView mLineView;
    private Handler mHandler;
    private int mX = 0;
    private CustomDialog dialog;

    private com.tuxing.app.Radar.RoundCornerProgressBar progress_space;
    private com.tuxing.app.Radar.RoundCornerProgressBar progress_space_score;
    private TextView tv_space_right_score;
    private TextView tv_space_right;
    private TextView tv_space;

    private com.tuxing.app.Radar.RoundCornerProgressBar progress_response;
    private com.tuxing.app.Radar.RoundCornerProgressBar progress_response_score;
    private TextView tv_response_right_score;
    private TextView tv_response_right;
    private TextView tv_response;

    private com.tuxing.app.Radar.RoundCornerProgressBar progress_memory;
    private com.tuxing.app.Radar.RoundCornerProgressBar progress_memory_score;
    private TextView tv_memory_right_score;
    private TextView tv_memory_right;
    private TextView tv_memory_name;

    private com.tuxing.app.Radar.RoundCornerProgressBar progress_attention;
    private com.tuxing.app.Radar.RoundCornerProgressBar progress_attention_score;
    private TextView tv_attention_right_score;
    private TextView tv_attention_right;
    private TextView tv_attention_name;

    private com.tuxing.app.Radar.RoundCornerProgressBar progress_logic;
    private com.tuxing.app.Radar.RoundCornerProgressBar progress_logic_score;
    private TextView tv_logic_right_score;
    private TextView tv_logic_right;
    private TextView tv_logic;

    private TextView tv_your_score;
    private TextView tvTitle_number;
    private TextView tvTitle_number_all;
    private TextView tvTitle_number_tip;

    private TextView tvTitle;

    private RelativeLayout rlTitle;

    private RelativeLayout tvTitle1;
    private RelativeLayout tvTitle3;
    private RelativeLayout tvTitle5;

    private RelativeLayout ll_space_score;
    private RelativeLayout ll_response_score;
    private RelativeLayout ll_memory_score;
    private RelativeLayout ll_attention_score;
    private RelativeLayout ll_logic_score;

    private TextView tv_space_right_score_arraw;
    private TextView tv_response_right_score_arraw;
    private TextView tv_memory_right_score_arraw;
    private TextView tv_attention_right_score_arraw;
    private TextView tv_logic_right_score_arraw;

    private TextView tv_tip;
    private TextView tv_tip_name;

    private TextView test_back_txt;
    private ImageView test_back_img;

    private long childid;
    float progress_all;
    float progress_float_space;
    float progress_float_response;
    float progress_float_memory;
    float progress_float_attention;
    float progress_float_logic;
    float progress_float_space_num;
    float progress_float_response_num;
    float progress_float_memory_num;
    float progress_float_attention_num;
    float progress_float_logic_num;
    private Handler handler;

    private RelativeLayout rl_bottom;
    private Button bt_start_work;

    int max = 0;
    int cur = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rada);
        instance = this;
        initview();
    }

    private void initview() {
//        rada = new Rada(this,null);
        radarview = (RadarView) findViewById(R.id.radarview);
        progressTwo = (RoundCornerProgressBar) findViewById(R.id.progress_two);

        tvTitle1 = (RelativeLayout) findViewById(R.id.tvTitle1);
        tvTitle1.setOnClickListener(this);
        tvTitle3 = (RelativeLayout) findViewById(R.id.tvTitle3);
        tvTitle3.setOnClickListener(this);
        tvTitle5 = (RelativeLayout) findViewById(R.id.tvTitle5);
        tvTitle5.setOnClickListener(this);

        tv_tip = (TextView) findViewById(R.id.tv_tip);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        test_back_img = (ImageView) findViewById(R.id.test_back_img);

        tv_space_right_score_arraw = (TextView) findViewById(R.id.tv_space_right_score_arraw);
        tv_space_right_score_arraw.setOnClickListener(this);
        tv_response_right_score_arraw = (TextView) findViewById(R.id.tv_response_right_score_arraw);
        tv_response_right_score_arraw.setOnClickListener(this);
        tv_memory_right_score_arraw = (TextView) findViewById(R.id.tv_memory_right_score_arraw);
        tv_memory_right_score_arraw.setOnClickListener(this);
        tv_attention_right_score_arraw = (TextView) findViewById(R.id.tv_attention_right_score_arraw);
        tv_attention_right_score_arraw.setOnClickListener(this);
        tv_logic_right_score_arraw = (TextView) findViewById(R.id.tv_logic_right_score_arraw);
        tv_logic_right_score_arraw.setOnClickListener(this);

        progress_space = (RoundCornerProgressBar) findViewById(R.id.progress_space);
        progress_space_score = (RoundCornerProgressBar) findViewById(R.id.progress_space_score);
        tv_space_right_score = (TextView) findViewById(R.id.tv_space_right_score);
        tv_space_right = (TextView) findViewById(R.id.tv_space_right);
        tv_space = (TextView) findViewById(R.id.tv_space);

        progress_response = (RoundCornerProgressBar) findViewById(R.id.progress_response);
        progress_response_score = (RoundCornerProgressBar) findViewById(R.id.progress_response_score);
        tv_response_right_score = (TextView) findViewById(R.id.tv_response_right_score);
        tv_response_right = (TextView) findViewById(R.id.tv_response_right);
        tv_response = (TextView) findViewById(R.id.tv_response);

        progress_memory = (RoundCornerProgressBar) findViewById(R.id.progress_memory);
        progress_memory_score = (RoundCornerProgressBar) findViewById(R.id.progress_memory_score);
        tv_memory_right_score = (TextView) findViewById(R.id.tv_memory_right_score);
        tv_memory_right = (TextView) findViewById(R.id.tv_memory_right);
        tv_memory_name = (TextView) findViewById(R.id.tv_memory_name);

        progress_attention = (RoundCornerProgressBar) findViewById(R.id.progress_attention);
        progress_attention_score = (RoundCornerProgressBar) findViewById(R.id.progress_attention_score);
        tv_attention_right_score = (TextView) findViewById(R.id.tv_attention_right_score);
        tv_attention_right = (TextView) findViewById(R.id.tv_attention_right);
        tv_attention_name = (TextView) findViewById(R.id.tv_attention_name);

        tv_your_score = (TextView) findViewById(R.id.tv_your_score);

        progress_logic = (RoundCornerProgressBar) findViewById(R.id.progress_logic);
        progress_logic_score = (RoundCornerProgressBar) findViewById(R.id.progress_logic_score);
        tv_logic_right_score = (TextView) findViewById(R.id.tv_logic_right_score);
        tv_logic_right = (TextView) findViewById(R.id.tv_logic_right);
        tv_logic = (TextView) findViewById(R.id.tv_logic);

        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tvTitle_number = (TextView) findViewById(R.id.tvTitle_number);
        tvTitle_number_all = (TextView) findViewById(R.id.tvTitle_number_all);
        tvTitle_number_tip = (TextView) findViewById(R.id.tvTitle_number_tip);

        ll_space_score = (RelativeLayout) findViewById(R.id.ll_space_score);
        ll_space_score.setOnClickListener(this);
        ll_response_score = (RelativeLayout) findViewById(R.id.ll_response_score);
        ll_response_score.setOnClickListener(this);
        ll_memory_score = (RelativeLayout) findViewById(R.id.ll_memory_score);
        ll_memory_score.setOnClickListener(this);
        ll_attention_score = (RelativeLayout) findViewById(R.id.ll_attention_score);
        ll_attention_score.setOnClickListener(this);
        ll_logic_score = (RelativeLayout) findViewById(R.id.ll_logic_score);
        ll_logic_score.setOnClickListener(this);

        test_back_txt = (TextView) findViewById(R.id.test_back_txt);
        bt_right = (TextView) findViewById(R.id.bt_right);
        bt_right.setOnClickListener(this);

        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            bt_right.setVisibility(View.GONE);
            tv_tip.setText("学生等级");
            String name = getIntent().getStringExtra("name");
            tvTitle.setText(name+"的学能成绩");
            test_back_txt.setTextColor(getResources().getColor(R.color.text_teacher));
            test_back_img.setImageResource(R.drawable.arrow_left_t);
        } else{
            rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
            bt_start_work = (Button) findViewById(R.id.bt_start_work);
            bt_start_work.setBackgroundColor(getResources().getColor(R.color.text_parent));
            bt_start_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.StartTest).putExtra("isRadaActivity",true);
                    startActivity(intent);
                }
            });
        }
        progressTwo.setProgressColor(Color.parseColor("#db93f3"));
        progressTwo.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
//        progressTwo.setSecondaryProgress(progressTwo.getProgress());

//        radarview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressTwo.setProgress(progressTwo.getProgress() - 1);
//                tv_right.setText(progressTwo.getProgress() + "%");
//            }
//        });
//        ll_chick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressTwo.setProgress(progressTwo.getProgress() + 1);
//                tv_right.setText(progressTwo.getProgress() + "%");
//            }
//        });

        //handle接收消息
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        progressTwo.setProgress(msg.getData().getInt("size"));
                        int score_all = Math.round(progressTwo.getProgress());
                        tv_right.setText(score_all + "%");
                        break;

                    case 2:
                        progress_space.setProgress(msg.getData().getInt("size"));
//                        progress_space_score.setProgress(msg.getData().getInt("size"));
                        int score_space = Math.round(progress_space.getProgress());
                        tv_space_right.setText(score_space + "%");
                        break;
                    case 3:
                        progress_response.setProgress(msg.getData().getInt("size"));
//                        progress_response_score.setProgress(msg.getData().getInt("size"));
                        int score_response = Math.round(progress_response.getProgress());
                        tv_response_right.setText(score_response + "%");
                        break;
                    case 4:
                        progress_memory.setProgress(msg.getData().getInt("size"));
//                        progress_memory_score.setProgress(msg.getData().getInt("size"));
                        int score_memory = Math.round(progress_memory.getProgress());
                        tv_memory_right.setText(score_memory + "%");
                        break;
                    case 5:
                        progress_attention.setProgress(msg.getData().getInt("size"));
//                        progress_attention_score.setProgress(msg.getData().getInt("size"));
                        int score_attention = Math.round(progress_attention.getProgress());
                        tv_attention_right.setText(score_attention + "%");
                        break;
                    case 6:
                        progress_logic.setProgress(msg.getData().getInt("size"));
//                        progress_logic_score.setProgress(msg.getData().getInt("size"));
                        int score_logic = Math.round(progress_logic.getProgress());
                        tv_logic_right.setText(score_logic + "%");
                        break;
                    case 7:
                        progress_space_score.setProgress(msg.getData().getInt("size"));
                        break;
                    case 8:
                        progress_response_score.setProgress(msg.getData().getInt("size"));
                        break;
                    case 9:
                        progress_memory_score.setProgress(msg.getData().getInt("size"));
                        break;
                    case 10:
                        progress_attention_score.setProgress(msg.getData().getInt("size"));
                        break;
                    case 11:
                        progress_logic_score.setProgress(msg.getData().getInt("size"));
                        break;
                }
            }
        };


        lineChart = (LineChartView) findViewById(R.id.line_chart);
        generateInitialLineData();
//        generateLineData(Color.parseColor("#fda220"), 100);

        initData();

    }

    private void generateInitialLineData() {
        int numValues = 7;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, 0));
            if (point.size()>1&&i<point.size()){
                    axisValues.add(new AxisValue(i).setLabel(point.get(i).getNumber()+""));
            }else{
                axisValues.add(new AxisValue(i).setLabel(days[i]));
            }
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_ORANGE);
        line.setHasLabels(false);//曲线的数据坐标是否加上备注
        line.setHasPoints(true);
        line.setHasLines(true);
        line.setCubic(false);//曲线是否平滑


        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(false));
//        List<AxisValue> valuey = new ArrayList<>();
//        for(int i = 0; i < 1000; i+= 100){
//            AxisValue valuesy = new AxisValue(i);
//            String label = i+"";
//            valuesy.setLabel(label);
//            valuey.add(valuesy);
//        }
//        lineData.setAxisYLeft(new Axis().setHasLines(false).setMaxLabelChars(4).setValues(valuey));
        lineData.setAxisYLeft(new Axis().setHasLines(false).setMaxLabelChars(4));

        lineChart.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
//        lineChart.setViewportCalculationEnabled(false);
//        Path path = new Path();
//        float x = values.get(0).getX();
//        canvas.drawLine(x, 30+20, x-6, 30+14+20, linePaint);//Y轴箭头。

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport viewport = new Viewport(0, 200, 7, 0);
        lineChart.setMaximumViewport(viewport);
        lineChart.setCurrentViewport(viewport);
        lineChart.setInteractive(false);
        lineChart.setScrollEnabled(false);
//        lineChart.setValueTouchEnabled(false);
        lineChart.setFocusableInTouchMode(false);


        lineChart.setZoomType(ZoomType.HORIZONTAL);
//        lineChart.setContainerScrollEnabled(false, ContainerScrollType.HORIZONTAL);
    }

    private void generateLineData(int color, float range) {
        // Cancel last animation if not finished.
        if (point.size()>1){
            if (point.size()==2){
                if (point.get(1).getScore()==0){
                    return;
                }
            }else if (point.size()==3){
                if (point.get(1).getScore()==0&&point.get(2).getScore()==0){
                    return;
                }
            }else if (point.size()==4){
                if (point.get(1).getScore()==0&&point.get(2).getScore()==0&&point.get(3).getScore()==0){
                    return;
                }
            }else if (point.size()==5){
                if (point.get(1).getScore()==0&&point.get(2).getScore()==0&&point.get(3).getScore()==0&&point.get(4).getScore()==0){
                    return;
                }
            }else if (point.size()==6){
                if (point.get(1).getScore()==0&&point.get(2).getScore()==0&&point.get(3).getScore()==0&&point.get(4).getScore()==0&&point.get(5).getScore()==0){
                    return;
                }
            }else if (point.size()==7){
                if (point.get(1).getScore()==0&&point.get(2).getScore()==0&&point.get(3).getScore()==0&&point.get(4).getScore()==0&&point.get(5).getScore()==0&&point.get(6).getScore()==0){
                    return;
                }
            }
        }

        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        if (point.size()>1){
            for (int i = 1; i < line.getValues().size(); i++) {
                PointValue values = line.getValues().get(i);
                if (i<point.size()){
//                    if(point.get(i).getScore()==0){
//                        line.getValues().get(i).setTarget(values.getX(), (float) -1);
//                        return;
//                    }else{
                        line.getValues().get(i).setTarget(values.getX(), (float) point.get(i).getScore());
//                    }
                }else{
                    line.getValues().get(i).setTarget(values.getX(), (float) -1);
                }
//                line.getValues().get(i).setTarget(values.getX(),  (float) Math.random() * range);
            }
            lineChart.startDataAnimation(300);
        }else{
            invalidateOptionsMenu();
        }
//        PointValue values = line.getValues().get(7);
//        line.getValues().get(7).setTarget(values.getX(), (float)max+200 );
//        for (PointValue value : line.getValues()) {
////            // Change target only for Y value.
//            value.setTarget(value.getX(), (float) Math.random() * range);
//        }
//        Viewport viewport = new Viewport(0, max + 200, 7, 0);
//        lineChart.setMaximumViewport(viewport);
//        lineChart.setCurrentViewport(viewport);
//        lineChart.startDataAnimation(300);
    }


    private void initData() {
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            childid = getIntent().getLongExtra("childid", 0);
        } else {
            User loginUser = getService().getUserManager().getUserInfo(user.getUserId());
            childid = loginUser.getChildUserId();
            tvTitle.setText("成绩");
        }
//        List<AbilityDetailList> detailLists=UserDbHelper.getInstance().getLatestAbilityDetailList();
//        getService().getLearningAbilityManager().AbilityEvaluationRequest(childid);

        getService().getLearningAbilityManager().AbilityEvaluationRequestlocation();
        showProgressDialog(this, "", true, null);
    }

    public void onEventMainThread(LearningAbilityEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case ABILITY_SUCCESS_LOCATION:
                    SharedPreferences preferences = getSharedPreferences("xcsdtxt", Context.MODE_PRIVATE);
                    int totalAbilityLevel_txt = preferences.getInt("totalAbilityLevel_txt", 0);
                    String totalAbilityPercentage_txt = preferences.getString("totalAbilityPercentage_txt", "");
                    int abilityQuotient_txt = preferences.getInt("abilityQuotient_txt", 0);
                    int maxAbilityQuotient_txt = preferences.getInt("maxAbilityQuotient_txt", 0);

                    if (event.getAbilityDetailslist().size()==0&&event.getAbilityPoint().size()==0){
                        getService().getLearningAbilityManager().AbilityEvaluationRequest(childid);
                        return;
                    }
//                    if (event.getAbilityDetailslist().size()==0&&event.getAbilityPoint().size()==0&&totalAbilityPercentage_txt.equals("")&&totalAbilityLevel_txt==0&&abilityQuotient_txt==0&&maxAbilityQuotient_txt==0){
//                        getService().getLearningAbilityManager().AbilityEvaluationRequest(childid);
//                        return;
//                    }
                    double[] data = {Double.parseDouble(event.getAbilityDetailslist().get(2).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(1).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(0).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(3).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(4).getLevel() * 10 + "")
                    };
                    double[] data_Average = {Double.parseDouble(event.getAbilityDetailslist().get(2).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(1).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(0).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(3).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(4).getAvgLevel() * 10 + "")
                    };


                    for (int i = 0; i < event.getAbilityDetailslist().size(); i++) {
                        AbilityDetailList detailList = event.getAbilityDetailslist().get(i);
                        if (detailList.getAbility() == 5) {
                            progress_float_space = Float.parseFloat(detailList.getPercentage() + "");
                            progress_space.setProgressColor(Color.parseColor("#ff6f84"));
                            progress_space.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_space_right.setText(score + "%");
                            tv_space.setText(event.getAbilityDetailslist().get(4).getLevel() + "");

                            progress_space_score.setProgressColor(Color.parseColor("#ff6f84"));
                            tv_space_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_space_num = Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 3) {
                            progress_float_response = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_response.setProgress(progress * 100);
                            progress_response.setProgressColor(Color.parseColor("#ffc24b"));
                            progress_response.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_response_right.setText(score + "%");
                            tv_response.setText(event.getAbilityDetailslist().get(2).getLevel() + "");

//                            progress_response_score.setProgress(progress * 100);
                            progress_response_score.setProgressColor(Color.parseColor("#ffc24b"));
                            tv_response_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_response_num =  Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 2) {
                            progress_float_memory = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_memory.setProgress(progress * 100);
                            progress_memory.setProgressColor(Color.parseColor("#82d04d"));
                            progress_memory.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_memory_right.setText(score + "%");
                            tv_memory_name.setText(event.getAbilityDetailslist().get(1).getLevel() + "");

//                            progress_memory_score.setProgress(progress * 100);
                            progress_memory_score.setProgressColor(Color.parseColor("#82d04d"));
                            tv_memory_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_memory_num = Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 1) {
                            progress_float_attention = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_attention.setProgress(progress * 100);
                            progress_attention.setProgressColor(Color.parseColor("#84bef0"));
                            progress_attention.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_attention_right.setText(score + "%");
                            tv_attention_name.setText(event.getAbilityDetailslist().get(0).getLevel() + "");

//                            progress_attention_score.setProgress(progress * 100);
                            progress_attention_score.setProgressColor(Color.parseColor("#84bef0"));
                            tv_attention_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_attention_num = Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 4) {
                            progress_float_logic = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_logic.setProgress(progress * 100);
                            progress_logic.setProgressColor(Color.parseColor("#9a93f3"));
                            progress_logic.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_logic_right.setText(score + "%");
                            tv_logic.setText(event.getAbilityDetailslist().get(3).getLevel() + "");

//                            progress_logic_score.setProgress(progress * 100);
                            progress_logic_score.setProgressColor(Color.parseColor("#9a93f3"));
                            tv_logic_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_logic_num = Float.parseFloat(more+"");
                        }
                    }
                    progress_all = Float.parseFloat(totalAbilityPercentage_txt + "");
                    if (event.getAbilityDetailslist().get(0).getLevel()==0&&
                            event.getAbilityDetailslist().get(1).getLevel()==0&&
                            event.getAbilityDetailslist().get(2).getLevel()==0&&
                            event.getAbilityDetailslist().get(3).getLevel()==0&&
                            event.getAbilityDetailslist().get(4).getLevel()==0
                            ){
                        double[] data_average = {0.0,0,0,0,0};
                        tv_your_score.setText("还没有做作业和测试！");
                        radarview.setData(data);
                        radarview.setdata_Average(data_average);
                    }else{
                        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
                            tv_your_score.setText("学能总成绩为：" + totalAbilityLevel_txt);
                        }else{
                            tv_your_score.setText("你的学能总成绩为：" + totalAbilityLevel_txt);
                        }
                        radarview.setData(data);
                        radarview.setdata_Average(data_Average);
                    }

                    tvTitle_number.setText(abilityQuotient_txt+ "");
                    tvTitle_number_all.setText(maxAbilityQuotient_txt + "");
                    if (event.getAbilityQuotient()==0){
                        tvTitle_number_tip.setText("快去测试版块做学习能力测试吧！");
                    }else{
                        tvTitle_number_tip.setText("你的学能商数！");
                    }

                    AbilityPoint abilityPointnull=new AbilityPoint();
                    abilityPointnull.setNumber(0);
                    abilityPointnull.setScore(0);
                    abilityPointnull.setId(null);
                    point.add(abilityPointnull);

//                    setProgress();
                    if (event.getAbilityPoint().size() > 0) {
                        for (int i = 0; i < event.getAbilityPoint().size(); i++) {
                            AbilityPoint abilityPoint = event.getAbilityPoint().get(i);
                            point.add(abilityPoint);
                            cur = abilityPoint.getScore();
                            if (max < cur) {
                                max = cur;
                            }
                        }
//                        generateInitialLineData();
//                        generateLineData(Color.parseColor("#fda220"), 100);
                    }else{
//                        generateInitialLineData();
//                        generateLineData(Color.parseColor("#fda220"), 100);
                    }



                    getService().getLearningAbilityManager().AbilityEvaluationRequest(childid);
                case ABILITY_SUCCESS:
                    double[] data_last = {Double.parseDouble(event.getAbilityDetailslist().get(2).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(1).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(0).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(3).getLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(4).getLevel() * 10 + "")
                    };
                    double[] data_Average_last = {Double.parseDouble(event.getAbilityDetailslist().get(2).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(1).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(0).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(3).getAvgLevel() * 10 + ""),
                            Double.parseDouble(event.getAbilityDetailslist().get(4).getAvgLevel() * 10 + "")
                    };


//                    Attention(1),
//                        Memory(2),
//                        Reaction(3),
//                        Reasoning(4),
//                        SpatialThinking(5);
//                    event.getEvent();
                    for (int i = 0; i < event.getAbilityDetailslist().size(); i++) {
                        AbilityDetailList detailList = event.getAbilityDetailslist().get(i);
//                        data[i] =Double.parseDouble(detailList.getLevel()+"");
//                        data_Average[i] =Double.parseDouble(detailList.getUniversalLevel()+"");
                        if (detailList.getAbility() == 5) {
                            progress_float_space = Float.parseFloat(detailList.getPercentage() + "");
                            progress_space.setProgressColor(Color.parseColor("#ff6f84"));
                            progress_space.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_space_right.setText(score + "%");
                            tv_space.setText(event.getAbilityDetailslist().get(4).getLevel() + "");

                            progress_space_score.setProgressColor(Color.parseColor("#ff6f84"));
                            tv_space_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_space_num = Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 3) {
                            progress_float_response = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_response.setProgress(progress * 100);
                            progress_response.setProgressColor(Color.parseColor("#ffc24b"));
                            progress_response.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_response_right.setText(score + "%");
                            tv_response.setText(event.getAbilityDetailslist().get(2).getLevel() + "");

//                            progress_response_score.setProgress(progress * 100);
                            progress_response_score.setProgressColor(Color.parseColor("#ffc24b"));
                            tv_response_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_response_num =  Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 2) {
                            progress_float_memory = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_memory.setProgress(progress * 100);
                            progress_memory.setProgressColor(Color.parseColor("#82d04d"));
                            progress_memory.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_memory_right.setText(score + "%");
                            tv_memory_name.setText(event.getAbilityDetailslist().get(1).getLevel() + "");

//                            progress_memory_score.setProgress(progress * 100);
                            progress_memory_score.setProgressColor(Color.parseColor("#82d04d"));
                            tv_memory_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_memory_num = Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 1) {
                            progress_float_attention = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_attention.setProgress(progress * 100);
                            progress_attention.setProgressColor(Color.parseColor("#84bef0"));
                            progress_attention.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_attention_right.setText(score + "%");
                            tv_attention_name.setText(event.getAbilityDetailslist().get(0).getLevel() + "");

//                            progress_attention_score.setProgress(progress * 100);
                            progress_attention_score.setProgressColor(Color.parseColor("#84bef0"));
                            tv_attention_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_attention_num = Float.parseFloat(more+"");
                        } else if (detailList.getAbility() == 4) {
                            progress_float_logic = Float.parseFloat(detailList.getPercentage() + "");
//                            progress_logic.setProgress(progress * 100);
                            progress_logic.setProgressColor(Color.parseColor("#9a93f3"));
                            progress_logic.setProgressBackgroundColor(getResources().getColor(R.color.gray_line));
                            Long score = Math.round(detailList.getPercentage() * 100);
                            tv_logic_right.setText(score + "%");
                            tv_logic.setText(event.getAbilityDetailslist().get(3).getLevel() + "");

//                            progress_logic_score.setProgress(progress * 100);
                            progress_logic_score.setProgressColor(Color.parseColor("#9a93f3"));
                            tv_logic_right_score.setText(detailList.getScore() + "/" + detailList.getMaxScore());
                            double scoreup= detailList.getScore();
                            double scoredown= detailList.getMaxScore();
                            double more = scoreup/scoredown*0.86;
                            progress_float_logic_num = Float.parseFloat(more+"");
                        }
                    }
                    progress_all = Float.parseFloat(event.getAvgAbilityPercentage() + "");
//                    progressTwo.setProgress(progress_all * 100);
//                    Long score_all = Math.round(event.getAvgAbilityPercentage() * 100);
//                    tv_right.setText(score_all + "%");
                    if (event.getAbilityDetailslist().get(0).getLevel()==0&&
                            event.getAbilityDetailslist().get(1).getLevel()==0&&
                            event.getAbilityDetailslist().get(2).getLevel()==0&&
                            event.getAbilityDetailslist().get(3).getLevel()==0&&
                            event.getAbilityDetailslist().get(4).getLevel()==0
                            ){
                        double[] data_average = {0.0,0,0,0,0};
                        tv_your_score.setText("还没有做作业和测试！");
                        radarview.setData(data_last);
                        radarview.setdata_Average(data_average);
                    }else{
                        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
                            tv_your_score.setText("学能总成绩为：" + event.getAvgAbilityLevel());
                        }else{
                            tv_your_score.setText("你的学能总成绩为：" + event.getAvgAbilityLevel());
                        }
                        radarview.setData(data_last);
                        radarview.setdata_Average(data_Average_last);
                    }

                    tvTitle_number.setText(event.getAbilityQuotient() + "");
                    tvTitle_number_all.setText(event.getMaxAbilityQuotient() + "");
                    if (event.getAbilityQuotient()==0){
                        tvTitle_number_tip.setText("快去测试版块做学习能力测试吧！");
                    }else{
                        tvTitle_number_tip.setText("你的学能商数！");
                    }
//                    AbilityPoint abilityp=null;
//                    abilityp.setNumber(0);
//                    abilityp.setScore(0);
//                    point.add(abilityp);

                    point.clear();
                    AbilityPoint abilityPoint_null=new AbilityPoint();
                    abilityPoint_null.setNumber(0);
                    abilityPoint_null.setScore(0);
                    abilityPoint_null.setId(null);
                    point.add(abilityPoint_null);

                    setProgress();
//                    Viewport viewport = new Viewport(0, max + 200, 8, 0);
//                    lineChart.setMaximumViewport(viewport);
//                    lineChart.setCurrentViewport(viewport);
                    if (event.getAbilityPoint().size() > 0) {
                        for (int i = 0; i < event.getAbilityPoint().size(); i++) {
                            AbilityPoint abilityPoint = event.getAbilityPoint().get(i);
                            point.add(abilityPoint);
                            cur = abilityPoint.getScore();
                            if (max < cur) {
                                max = cur;
                            }
                        }
                        generateInitialLineData();
                        generateLineData(Color.parseColor("#fda220"), 100);
                    }else{
                        generateInitialLineData();
                        generateLineData(Color.parseColor("#fda220"), 100);
                    }

//                    radarview.setData(data);
//                    radarview.setdata_Average(data_Average);
//                    radarview = new RadarView(this);
//                    radarview.setRotation(15.0f);
//                    radarview.setRotationX(15.0f);
//                    radarview.setRotationY(15.0f);
//                    radarview.setData(data);
//                    radarview.setdata_Average(data_Average);
                    String totalAbilityLevels = event.getAvgAbilityLevel()+"";
                    String totalAbilityPercentages = event.getAvgAbilityPercentage()+"";
                    String abilityQuotients = event.getAbilityQuotient()+"";
                    String maxAbilityQuotients = event.getMaxAbilityQuotient()+"";
                    SharedPreferences pref = getSharedPreferences("xcsdtxt",
                            Context.MODE_WORLD_READABLE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("totalAbilityLevel_txt", new Integer(totalAbilityLevels));
                    editor.putString("totalAbilityPercentage_txt", totalAbilityPercentages);
//                    editor.putInt("totalAbilityPercentage_txt", new Integer(totalAbilityPercentages));
                    editor.putInt("abilityQuotient_txt", new Integer(abilityQuotients));
                    editor.putInt("maxAbilityQuotient_txt", new Integer(maxAbilityQuotients));
                    editor.commit();

                    disProgressDialog();
                    break;
                case ABILITY_FAILED:
                    disProgressDialog();
                    setProgress();

                    generateInitialLineData();
                    generateLineData(Color.parseColor("#fda220"), 100);

                    showToast(event.getMsg());
                    break;
            }
        }
    }

    public void setProgress() {
        new Thread() {
            public void run() {
                try {
                    sleep(300);
                    for (int i = 0; i <= progress_all * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 1;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }

                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */

        new Thread() {
            public void run() {
                try {
                    sleep(300);
                    for (int i = 0; i <= progress_float_space * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 2;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */

        new Thread() {
            public void run() {
                try {
                    sleep(300);
                    for (int i = 0; i <= progress_float_response * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 3;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */


    }

    public void setProgressNext() {
        new Thread() {
            public void run() {
                try {
                    sleep(300);
                    for (int i = 0; i <= progress_float_memory * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 4;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */

        new Thread() {
            public void run() {
                try {
                    sleep(300);
                    for (int i = 0; i <= progress_float_attention * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 5;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */

        new Thread() {
            public void run() {
                try {
                    sleep(300);
                    for (int i = 0; i <= progress_float_logic * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 6;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */
    }

    public void setProgressNexts() {
        new Thread() {
            public void run() {
                try {
                    sleep(300);

                    for (int i = 0; i <= progress_float_space_num * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 7;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }

                    for (int i = 0; i <= progress_float_response_num * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 8;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }

                    for (int i = 0; i <= progress_float_memory_num * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 9;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }

                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */
    }

    public void setProgressEnd() {
        new Thread() {
            public void run() {
                try {
                    sleep(300);

                    for (int i = 0; i <= progress_float_attention_num * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 10;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }
                    for (int i = 0; i <= progress_float_logic_num * 100; ) {
                         /* 在这里写上运行的进度条 */
                        Message msg = new Message();
                        msg.what = 11;
                        msg.getData().putInt("size", i);
                        handler.sendMessage(msg);//handle发送消息
                         /* 为了明显看见效果，以暂停1秒 */
                        sleep(30);
                        i += 1;
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start(); /* 开始运行运行线程 */
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_right) {
            Intent intent = new Intent(this, ExplainActivity.class);
////            Intent intent = new Intent(this, LineColumnDependencyActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.rl_back) {
            finish();
        }
        if (v.getId() == R.id.tvTitle1) {
            showDialog("学能等级是什么？", "学能等级是通过日常作业和学能测试的综合表现得出你孩子在各项学习能力的高低，每个孩子的学习成绩好坏，其实和他的学习能力直接相关，根据大脑学习加工的认知功能，将学习能力分成五大能力，分别是注意力、记忆力、反应力、逻辑力、空间思维。每种能力分为12个等级，从1到12.\n\r五种能力分别对应着不同的训练游戏，我们会根据学生的学能等级水平去适配学生的训练计划，所以赶紧让你的孩子点击去做作业和测试吧。");
        }
        if (v.getId() == R.id.tvTitle3) {
            showDialog("学能通关积分是什么？", "学能通关积分与根据孩子做游戏时获得的成绩相关，如果通关的游戏关卡越多，星数越多，则孩子的学能训练积分越高。");
        }
        if (v.getId() == R.id.tvTitle5) {
            showDialog("学能商数是什么？", "学能商数与孩子的学能测试成绩相关，在测试板块做学习能力测试，就可以测出学生的学习能力，并且通过多次测试展示学生学习能力的变化曲线，孩子可以根据学习能力表现针对性的训练自己相应的能力。");
        }
        if (v.getId() == R.id.ll_space_score) {
            Intent intent_score = new Intent(this, ExplainScoreActivity.class).putExtra("type", 0).putExtra("childid", childid);
            startActivity(intent_score);
        }
        if (v.getId() == R.id.ll_response_score) {
            Intent intent_response = new Intent(this, ExplainScoreActivity.class).putExtra("type", 1).putExtra("childid", childid);
            startActivity(intent_response);
        }
        if (v.getId() == R.id.ll_memory_score) {
            Intent intent_memory = new Intent(this, ExplainScoreActivity.class).putExtra("type", 2).putExtra("childid", childid);
            startActivity(intent_memory);
        }
        if (v.getId() == R.id.ll_attention_score) {
            Intent intent_attention = new Intent(this, ExplainScoreActivity.class).putExtra("type", 3).putExtra("childid", childid);
            startActivity(intent_attention);
        }
        if (v.getId() == R.id.ll_logic_score) {
            Intent intent_logic = new Intent(this, ExplainScoreActivity.class).putExtra("type", 4).putExtra("childid", childid);
            startActivity(intent_logic);
        }
    }

    public void showDialog(String title, String content) {
        if (dialog != null && dialog.isShowing()) {
            return;
        } else {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.radadialog, null);
            dialog = new CustomDialog(this, R.style.dialog_alert_style, 0);

            // 根据id在布局中找到控件对象
            TextView tv_dialog_title = (TextView) view.findViewById(R.id.tv_dialog_title);
            RelativeLayout rl_all = (RelativeLayout) view.findViewById(R.id.rl_all);
            TextView tv_dialog_content = (TextView) view.findViewById(R.id.tv_dialog_content);
            TextView confirmBtn = (TextView) view.findViewById(R.id.confirm_btn);
//            if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//                confirmBtn.setBackgroundColor(getResources().getColor(R.color.text_teacher));
//            }
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            rl_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setCanceledOnTouchOutside(false);
            tv_dialog_title.setText(content);
            tv_dialog_content.setText(title);
            dialog.show();
        }
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
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "achievement");
        }
        super.finish();
    }
}
