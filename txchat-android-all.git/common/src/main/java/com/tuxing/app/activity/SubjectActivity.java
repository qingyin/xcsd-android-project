package com.tuxing.app.activity;

import java.util.List;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.bean.OptionBean;
import com.tuxing.app.bean.SubjectBean;
import com.tuxing.app.bean.TestBean;
import com.tuxing.app.helper.NetManager;
import com.tuxing.app.helper.TestHelper;
import com.tuxing.app.util.ShapeUtil;
import com.tuxing.app.util.StringUtil;
import com.tuxing.app.util.ToastUtil;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

public class SubjectActivity extends BaseActivity {

    private static final String TAG = SubjectActivity.class.getName();
    public static final String TEST_ID = "Test_Id";
    private TextView subject_img;
    private TextView subject_name;
    private TextView subject_back_txt;
    private TextView subject_title;
    private RadioGroup submit_group;
    private Button last_subject, next_subject;
    private TestBean test;
    private List<SubjectBean> subjects;
    private int currentIndex = 0;
    private boolean is_clickable = true;
    private String associateTag;

    private LinearLayout subject_header;

    private static int WHAT = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == WHAT) {
                currentIndex++;
                setCurrentSubject();
                is_clickable = true;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_activity);

        subject_header = (LinearLayout) findViewById(R.id.subject_header);
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//            subject_header.setBackgroundColor(getResources().getColor(R.color.teacher_pre_title));
        }

//		associateTag = getIntent().getStringExtra(RelatedTaskActivity.ASSOCIATE_TAG);
        test = (TestBean) getIntent().getSerializableExtra(
                TestDespActivity.TEST);
        subjects = test.subjests;

        subject_back_txt = (TextView) findViewById(R.id.subject_back_txt);
        subject_back_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        subject_title = (TextView) findViewById(R.id.subject_title);
        subject_title.setText(R.string.test_txt);

        subject_img = (TextView) findViewById(R.id.subject_img);
        subject_img.setBackgroundDrawable(ShapeUtil.getGradientTopDrawable(
                SubjectActivity.this, R.color.edu_color_2));
        subject_name = (TextView) findViewById(R.id.subject_name);

        submit_group = (RadioGroup) findViewById(R.id.subject_group);
        submit_group
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId != -1) {
                            TestHelper.saveAnswer(SubjectActivity.this,
                                    test.id, subjects.get(currentIndex).id,
                                    checkedId);
                        }
                    }
                });

        last_subject = (Button) findViewById(R.id.last_subject);
        last_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 上一题
                last_subject();
            }
        });
        next_subject = (Button) findViewById(R.id.next_subject);
        next_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_clickable) {
                    if (getCurrentAnswer() != 0) {
                        if (currentIndex != subjects.size() - 1) {
                            // 下一题
                            mHandler.sendEmptyMessageDelayed(WHAT, 300);
                            is_clickable = false;
                        } else {
                            // 提交
                            submit();
                        }
                    } else {
                        ToastUtil.show(SubjectActivity.this, R.string.answer_empty);
                    }
                }
            }
        });

        setCurrentSubject();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void submit() {
        String all_answerw = getAllAnswers();
        if (StringUtil.isNullOrEmpty(all_answerw)) {
            ToastUtil.show(SubjectActivity.this, R.string.answer_empty);
        } else {
            TestHelper.addAnswers(SubjectActivity.this, test.id, all_answerw,
                    new NetManager.MonitorInterface() {
                        @Override
                        public void getContentFromNet(Object content) {
                            /*GuideHelper.setGuideFresh(SubjectActivity.this,
                                    true);*/
                            Intent intent = new Intent(SubjectActivity.this,
                                    TestEvaluationActivity.class);
                            intent.putExtra(TEST_ID, test.id);
                            getService().getDataReportManager().reportEventBid(EventType.COMPLETED_TEST,test.id+"");
//							intent.putExtra(RelatedTaskActivity.ASSOCIATE_TAG, associateTag);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    private int getCurrentAnswer() {
        int _subject_id = subjects.get(currentIndex).id;
        int _option_id = TestHelper.getAnswer(SubjectActivity.this, test.id,
                _subject_id);
        return _option_id;
    }

    private String getAllAnswers() {
        String result = "";
        for (int i = 0; i < subjects.size(); i++) {
            int _subjectID = subjects.get(i).id;
            int _optionID = TestHelper.getAnswer(SubjectActivity.this, test.id,
                    _subjectID);
            result += "&answers[" + i + "].subjectID=" + _subjectID
                    + "&answers[" + i + "].option=" + _optionID;
        }
        return result;
    }

    private void last_subject() {
        if (currentIndex != 0) {
            currentIndex--;
            setCurrentSubject();
        }
    }

    private void setCurrentSubject() {
        if (subjects != null) {
            SubjectBean subject = subjects.get(currentIndex);
            subject_name.setText(subject.num + "." + subject.subject);
            List<OptionBean> options = subject.options;
            if (options != null) {
                submit_group.removeAllViews();
                submit_group.clearCheck();

                for (int i = 0; i < options.size(); i++) {
                    final RadioButton button = new RadioButton(
                            SubjectActivity.this);
                    LayoutParams params = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);//PixUtil.convertDpToPixel(40, SubjectActivity.this)
                    params.gravity = Gravity.CENTER;
                    button.setLayoutParams(params);
                    button.setBackgroundResource(R.drawable.white_blue_bg_selector);
                    button.setButtonDrawable(R.drawable.radio_button_selector);
                    // Log.i(TAG, PixUtil.convertDpToPixel(10,
                    // SubjectActivity.this)+"");
                    button.setPadding(35, 0, 0, 0);
                    button.setText(options.get(i).optionName);
                    button.setTextColor(getResources().getColorStateList(
                            R.drawable.btn_radio_txt_font));
                    button.setId(options.get(i).id);
                    if (TestHelper.getAnswer(SubjectActivity.this, test.id,
                            subject.id) == options.get(i).id) {
                        button.setChecked(true);
                        button.setButtonDrawable(R.drawable.radio_button_check_selector);// check
                    }
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (is_clickable
                                    && (currentIndex != subjects.size() - 1)) {
                                mHandler.sendEmptyMessageDelayed(WHAT, 300);
                                is_clickable = false;
                            }
                        }
                    });

                    submit_group.addView(button);
                }
            }

            if (currentIndex == subjects.size() - 1) {
                next_subject.setText(R.string.submit);
            } else {
                next_subject.setText(R.string.next_subject);
            }
            if (currentIndex == 0) {
                last_subject.setEnabled(false);
                last_subject.setBackgroundColor(getResources().getColor(
                        R.color.grey18));
            } else {
                last_subject.setEnabled(true);
                last_subject.setBackgroundColor(getResources().getColor(
                        R.color.edu_color_5));
            }
        }
    }

}
