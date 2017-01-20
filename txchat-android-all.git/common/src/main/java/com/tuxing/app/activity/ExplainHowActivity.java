package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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


public class ExplainHowActivity extends BaseActivity implements  View.OnClickListener {

    public static final String LOG_TAG = ExplainHowActivity.class.getSimpleName();

    private LinearLayout btnTitleLeft;
    private RelativeLayout rlTitle;
    private Button tv_right;
    private TextView tv_explain_how;
    private RelativeLayout rl_back;
    private TextView test_back_txt;
    private ImageView test_back_img;
    private TextView tvTitle;
    private long gardenId;
    private long workId;
    private boolean hasMore = false;
    private RelativeLayout activity_bg;
    ArrayList<String> list;
    public static ExplainHowActivity instance = null;
    int type =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_how);
        instance = this;

        init();
        getData();
    }

    private void init() {
//        setTitle("学能作业");
//        setLeftBack("", true, false);
//        setRightNext(true, "布置",0);
        type =getIntent().getIntExtra("type",0);
        TAG = ExplainHowActivity.class.getSimpleName();
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        tv_right = (Button) findViewById(R.id.tv_right);
        tv_explain_how = (TextView) findViewById(R.id.tv_explain_how);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        test_back_txt = (TextView) findViewById(R.id.test_back_txt);
        test_back_img = (ImageView) findViewById(R.id.test_back_img);
        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_right.setOnClickListener(this);

        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            test_back_txt.setTextColor(getResources().getColor(R.color.text_teacher));
            test_back_img.setImageResource(R.drawable.arrow_left_t);
        }else{
        }

        if (type==0){
            tvTitle.setText("学习能力");
            tv_explain_how.setText("乐学堂致力于通过游戏训练的方式提升孩子的学习能力，学习能力是指人们在正式学习环境下使用到的各项认知能力。即当一个孩子的学习能力提高了，相应的他的学习成绩也会有显著提高。学习能力存在显著的关键发展期，在幼儿园和小学阶段，学习能力的提升速度最快，美国哈弗大学研究表明，后天的体系化训练，能够帮助孩子提高学习能力，并且随着学习能力的提高，孩子的学习方法也会相应得到改善。");
        }
        else  if (type==1){
            tvTitle.setText("科学依据");
            tv_explain_how.setText("该项目依托中国儿童积极行为塑造模型（Positive Behavior Cultivation for China Infants）简称PBCCI，从小学生认知发展核心任务为切入点，将认知能力提升训练以手机游戏的形式延伸至学生课后生活，实现知识和能力的螺旋式提升。\n" +"\n"+
                    "PBCCI依托美国PBS（Positive Behavior Support）积极行为支持理论，以心理学为基础，结合生理学、社会学、应用行为分析等科学，从中国教育文化特征、家庭特征、儿童智能发展及身体发育特征及需求出发，形成的具有中国特色的“中国儿童积极行为塑造模型”。旨在从能力和意愿两方面对儿童的积极行为进行塑造。");
        }
        else  if (type==2){
            tvTitle.setText("五大学习能力");
//            tv_explain_how.setText("在学习的各项能力中，以下五种能力是国际认可的最基础的五项：\n" +
//                    "注意力\n" +
//                    "注意力是心理活动和各种感觉器官对客观事物的关注能力。在学生听课和学习过程中最基础的能力就是注意力。俄罗斯教育家乌申斯基曾精辟地指出：“‘注意’是我们心灵的惟一门户，意识中的一切，必然都要经过它才能进来。”学生上课是否溜号，能够同时加工几件事情，能够瞬间注意到多少数字等都与注意力紧密相关。 \n" +
//                    "记忆力\n" +
//                    "记忆力也就是人脑对外界输入的信息进行编码、存储和提取的过程。是学生学习和生活的基本技能。记忆力分为短时记忆和长时记忆。\n" +
//                    "短期记忆力的实质是大脑的即时生理生化反应的重复，而长时记忆则是大脑细胞内发生了结构改变，建立了固定联系。比如怎么骑自行车就是长时记忆，即使已多年不骑了，仍能骑上车就跑。短时记忆是数量最多又最不牢固的记忆。一个人每天只将1%的记忆保留下来。\n" +
//                    "反应力\n" +
//                    "反应力是大脑受到体内或体外的刺激引起的相应活动。提高快速反应能力，有助于提高大脑的灵活性和协调性。很多游戏都能够训练孩子的反应能力，例如常玩的手机游戏“别踩白块”等。\n" +
//                    "逻辑力\n" +
//                    "逻辑思维是五大能力中的高级能力，是人们在认识过程中借助于概念、判断、推理等思维形式能动地反映客观现实的理性认识过程。\n" +
//                    "逻辑能力强的人，在分析问题、看待问题时善于抓住问题的本质。因为他们的脑回路能在较短时间内快速厘清事情的来龙去脉，他们对于各种逻辑关系非常娴熟，既能从A推导到B再到C，也能从C反推回A，这一点是”思路清晰“这点的本质。\n" +
//                    "空间思维\n" +
//                    "空间思维也是五大学习能力中的高级能力，是指跳出点、线、面的限制，能从上下左右，四面八方去思考问题的思维方式，也就是要“立起来思考”。\n" +
//                    "一位心理学家曾经出过这样一个测验题：在一块土地上种植四棵树，使得每两棵树之间的距离都相等。受试的学生在纸上画了一个又一个的几何图形：正方形、菱形、梯形、平行四边形，然而，无论什么四边形都不行。这时，心理学家公布出了答案，其中一棵树可以种在山顶上！这样，只要其余三棵树与之构成正四面体的话，就能符合题意要求了。");
            tv_explain_how.setText(Html.fromHtml("   在学习的各项能力中，以下五种能力是国际认可的最基础的五项：<br>" +
                    "<b></b><br>" +
                    "<b>注意力</b><br>" +
                    "   注意力是心理活动和各种感觉器官对客观事物的关注能力。在学生听课和学习过程中最基础的能力就是注意力。俄罗斯教育家乌申斯基曾精辟地指出：“‘注意’是我们心灵的惟一门户，意识中的一切，必然都要经过它才能进来。”学生上课是否溜号，能够同时加工几件事情，能够瞬间注意到多少数字等都与注意力紧密相关。 <br>" +
                    "<b></b><br>" +
                    "<b>记忆力</b><br>" +
                    "   记忆力也就是人脑对外界输入的信息进行编码、存储和提取的过程。是学生学习和生活的基本技能。记忆力分为短时记忆和长时记忆。<br>" +
                    "   短期记忆力的实质是大脑的即时生理生化反应的重复，而长时记忆则是大脑细胞内发生了结构改变，建立了固定联系。比如怎么骑自行车就是长时记忆，即使已多年不骑了，仍能骑上车就跑。短时记忆是数量最多又最不牢固的记忆。一个人每天只将1%的记忆保留下来。<br>" +
                    "<b></b><br>" +
                    "<b>反应力</b><br>" +
                    "   反应力是大脑受到体内或体外的刺激引起的相应活动。提高快速反应能力，有助于提高大脑的灵活性和协调性。很多游戏都能够训练孩子的反应能力，例如常玩的手机游戏“别踩白块”等。<br>" +
                    "<b></b><br>" +
                    "<b>逻辑力</b><br>" +
                    "   逻辑思维是五大能力中的高级能力，是人们在认识过程中借助于概念、判断、推理等思维形式能动地反映客观现实的理性认识过程。<br>" +
                    "   逻辑能力强的人，在分析问题、看待问题时善于抓住问题的本质。因为他们的脑回路能在较短时间内快速厘清事情的来龙去脉，他们对于各种逻辑关系非常娴熟，既能从A推导到B再到C，也能从C反推回A，这一点是”思路清晰“这点的本质。<br>" +
                    "<b></b><br>" +
                    "<b>空间思维</b><br>" +
                    "   空间思维也是五大学习能力中的高级能力，是指跳出点、线、面的限制，能从上下左右，四面八方去思考问题的思维方式，也就是要“立起来思考”。<br>" +
                    "   一位心理学家曾经出过这样一个测验题：在一块土地上种植四棵树，使得每两棵树之间的距离都相等。受试的学生在纸上画了一个又一个的几何图形：正方形、菱形、梯形、平行四边形，然而，无论什么四边形都不行。这时，心理学家公布出了答案，其中一棵树可以种在山顶上！这样，只要其余三棵树与之构成正四面体的话，就能符合题意要求了。"));
        }
        else  if (type==3){
            tvTitle.setText("我们的团队");
            tv_explain_how.setText("PBCCI（Positive Behavior Cultivation for China Infants）——中国儿童积极行为塑造模型，是由携成尚德教育创始人王辉带领其研究团队，联合中国科学院心理研究所重点实验室郑希耕教授、苏州大学教育学院刘雅颖教授共同研发形成的。\n" +
                    "\n"+
                    "王辉：中科院心理研究所硕士，北京携成尚德教育创始人。中国PBCCI模型创建者；“减法教育”创始人；北京教委特聘“游戏化教学”项目负责人；土星教育研究院特聘心理学专家；国内青少年网络成瘾治疗（北京军区总院）发起人。");
        }
        else  if (type==4){
            tvTitle.setText("学能成绩指标解释");
            tv_explain_how.setText(Html.fromHtml("<b>学能等级</b><br>" +
                    "   根据PBCCI理论，我们将学习能力提炼为五大能力，他们分别为：注意力、记忆力、反应力、逻辑力、空间思维。我们将每项学习能力的等级分为1-12，等级越高，学习能力越强。同时乐学堂还提供了每个年级的学生的平均等级水平，帮助学生和老师对比该学生的学习能力高低。<br>" +
                    "<b></b><br>" +
                    "   学能等级根据老师布置的学能作业和家长端测试版块中的学习能力测试中的综合表现得出，例如学生通过了老师布置的序列方阵游戏的第五关，而序列方阵第五关对应的是注意力等级3；而在学能测试中学生通过了注意力等级为4的测试，则最终该学生注意力获得的学能等级为4。<br>" +
                    "<b></b><br>" +
                    "<b>学能总成绩</b><br>" +
                    "   学能总成绩为五大学习能力的等级总分，所以学能总成绩的最高分为60分，乐学堂会根据学生的学能总成绩来布置相应的作业，来帮助学生定制化的提高自己的学习能力水平。 <br>" +
                    "<b></b><br>" +
                    "<b>学能商数</b><br>" +
                    "   学生可以在家长端的测试版块做学习能力测试，学习能力测试通过游戏的方式测量学生在五大能力上的水平，从而得出总体的学能商数得分，为了使学生能够取得进步，乐学堂的学习能力测试会根据学生目前的学习能力来定制化的发布不同的测试题，以帮助学生逐步的提高自己的学习能力。例如学生目前的注意力等级为3，则测试中会给学生发送注意力等级为4的试题，以判断学生是否能够通过该水平。"));
        }
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
//        getService().getHomeWorkManager().getHomeWorkSendListFromLocal();
//        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            List<HomeWorkClass> tempDatas = new ArrayList<HomeWorkClass>();
            switch (event.getEvent()) {
                case HOMEWORK_SENT_LIST_FROM_LOCAL:
                    tv_explain_how.setText("");
                    break;
                case HOMEWORK_SENT_LIST_LATEST_SUCCESS:
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
        if(v.getId()==R.id.tv_right){
//            Intent intent = new Intent(this, ExplainScoreActivity.class);
//            startActivity(intent);
        }
    }

}
