package com.tuxing.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.adapter.CommonAdapter;
import com.tuxing.app.adapter.TestTopAdapter;
import com.tuxing.app.adapter.ViewHolder;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.bean.TestListBean;
import com.tuxing.app.helper.NetManager;
import com.tuxing.app.helper.TestHelper;
import com.tuxing.app.util.PreferenceUtil;
import com.tuxing.app.util.ShapeUtil;
import com.tuxing.app.util.StringUtil;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.TestList;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.DataReportEvent;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class TestActivity extends BaseActivity implements TestTopAdapter.MyItemClickListener {
    public static TestActivity instance = null;
    private static final String TAG = TestActivity.class.getName();
    private TextView test_back_txt;
    private TextView tvTitle;

    private ListView test_list;
    private CommonAdapter<TestListBean> test_adapter;
    private List<TestListBean> test_data = new ArrayList<TestListBean>();

    private RelativeLayout test_activity_empty;
    private Button empty_page_btn;
    private DisplayImageOptions mOption;
    private ImageLoader mImageLoader;

    private LinearLayout ll_test_one;
    private LinearLayout test_header;

    private LinearLayout test_content_empty;
    private TextView content_empty_txt;
    private int childID;
    private String childname = "";
    private boolean isnet = false;

    private RecyclerView mRecyclerView;
    private List<DepartmentMember> mDatas;
    private TestTopAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.test_activity);

        mOption = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        mImageLoader = ImageLoader.getInstance();

        test_back_txt = (TextView) findViewById(R.id.test_back_txt);
        test_back_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "themeTest");
                finish();
            }
        });
//		ll_test_one = (LinearLayout) findViewById(R.id.ll_test_one);
//		ll_test_one.setOnClickListener(this);

        mDatas = new ArrayList<>();

        mAdapter = new TestTopAdapter(this, mDatas);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_horizontal_show);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(this);
        //垂直方向
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
//		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setVisibility(View.GONE);


        test_header = (LinearLayout) findViewById(R.id.test_header);
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//			ll_test_one.setVisibility(View.GONE);
            User loginUser = getService().getUserManager().getUserInfo(user.getUserId());
            childID = Integer.parseInt(user.getUserId() + "");

            childname = user.getRealname() + "";

            if (childname.equals("")) {
                childname = user.getNickname() + "";
            }
            SysConstants.childID = childID + "";
            SysConstants.childname = childname + "";

//			test_header.setBackgroundColor(getResources().getColor(R.color.teacher_pre_title));
        } else if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            User loginUser = getService().getUserManager().getUserInfo(user.getUserId());
            User childData = getService().getUserManager().getUserInfo(loginUser.getChildUserId());
            childID = Integer.parseInt(loginUser.getChildUserId() + "");
            childname = childData.getNickname();
            SysConstants.childID = childID + "";
            SysConstants.childname = childname + "";
        }

        test_list = (ListView) findViewById(R.id.test_list);
        test_adapter = new CommonAdapter<TestListBean>(TestActivity.this,
                test_data, R.layout.test_item) {
            @SuppressWarnings("deprecation")
            @Override
            public void convert(ViewHolder helper, TestListBean item,
                                int position) {
                ImageView test_item_img = (ImageView) helper
                        .getView(R.id.test_item_img);
                TextView test_item_name = (TextView) helper
                        .getView(R.id.test_item_name);
                ImageView test_item_state = (ImageView) helper
                        .getView(R.id.test_item_state);
                String colorValue = null;
                if (StringUtil.isNullOrEmpty(item.getColorValue())) {
                    colorValue = "#58d68d";
                } else {
                    colorValue = "#" + item.getColorValue();
                }
                test_item_img.setBackgroundDrawable(ShapeUtil
                        .getGradientTestListDrawable(TestActivity.this,
                                colorValue));
                if (!StringUtil.isNullOrEmpty(item.getAnimalPic())) {
                    mImageLoader.displayImage(item.getAnimalPic(),
                            test_item_img, mOption);
                }
                test_item_name.setText(item.getName());
                if (item.getStatus() == 1) {
                    // 已完成
                    test_item_state.setVisibility(View.VISIBLE);
                } else {
                    test_item_state.setVisibility(View.GONE);
                }
            }
        };
        test_list.setAdapter(test_adapter);
        test_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final TestListBean test = test_data.get(arg2);
                PreferenceUtil.putDefStr(TestActivity.this,
                        TestSecondActivity.CURRENT_TEST_NAME, test.getName());
                if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                    if (arg2 == 0) {
                        Intent intent = new Intent(TuxingApp.packageName + SysConstants.StartTest);
                        startActivity(intent);
                    } else if (isnet) {
                        if (test.getStatus() == 1) {
                            // 已完成 跳至答题结果
                            boolean isparent = true;
                            int childID = 1;
                            childID = Integer.parseInt(SysConstants.childID + "");
                            TestHelper.getEvaluation(TestActivity.this, test.getId(),
                                    childID, new NetManager.MonitorInterface() {
                                        @Override
                                        public void getContentFromNet(Object content) {
                                            Intent intent = new Intent(
                                                    TestActivity.this,
                                                    TestEvaluationActivity.class);
                                            intent.putExtra(TestSecondActivity.TEST_ID,
                                                    test.getId());
                                            startActivity(intent);
                                        }
                                    });
                        } else {
                            // 未完成 跳至做题
                            Intent intent = new Intent(TestActivity.this,
                                    TestDespActivity.class);
                            intent.putExtra(TestSecondActivity.TEST_ID, test.getId());
                            startActivity(intent);
                        }
                    } else {
                        showToast("网络连接失败");
                    }
                } else if (isnet) {
                    if (test.getStatus() == 1) {
                        // 已完成 跳至答题结果
//					int childID = GuideHelper
//							.getCurrentBabyId(TestActivity.this);
                        boolean isparent = true;
                        int childID = 1;
//					if (isparent){
//						LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
//						if (loginUser != null) {
//							childID = Integer.parseInt(loginUser.getUserId()+"");
//							SysConstants.childID=childID+"";
//						}
//					}else {
//						childID = Integer.parseInt(SysConstants.childID+"");
//					}
                        childID = Integer.parseInt(SysConstants.childID + "");
                        TestHelper.getEvaluation(TestActivity.this, test.getId(),
                                childID, new NetManager.MonitorInterface() {
                                    @Override
                                    public void getContentFromNet(Object content) {
                                        Intent intent = new Intent(
                                                TestActivity.this,
                                                TestEvaluationActivity.class);
                                        intent.putExtra(TestSecondActivity.TEST_ID,
                                                test.getId());
//									intent.putExtra(
//											RelatedTaskActivity.ASSOCIATE_TAG,
//											test.getAssociateTag());
                                        startActivity(intent);
                                    }
                                });
                    } else {
                        // 未完成 跳至做题
                        Intent intent = new Intent(TestActivity.this,
                                TestDespActivity.class);
                        intent.putExtra(TestSecondActivity.TEST_ID, test.getId());
//					intent.putExtra(RelatedTaskActivity.ASSOCIATE_TAG,
//							test.getAssociateTag());
                        startActivity(intent);
                    }
                } else {
                    showToast("网络连接失败");
                }
            }
        });

        test_activity_empty = (RelativeLayout) findViewById(R.id.test_activity_empty);
        empty_page_btn = (Button) findViewById(R.id.empty_page_btn);
        empty_page_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        test_content_empty = (LinearLayout) findViewById(R.id.test_content_empty);
        content_empty_txt = (TextView) findViewById(R.id.content_empty_txt);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        String titlename = "";
//		titlename = getIntent().getStringExtra("name");
        if (!SysConstants.TestTitle.equals("")) {
            tvTitle.setText(SysConstants.TestTitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        List<TestList> testLists = UserDbHelper.getInstance().getLatestTestList();
        if (SysConstants.test_data_location.size() > 0) {
            SysConstants.test_data_location.clear();
        }
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            TestListBean onetest = new TestListBean();
            onetest.setName("学能作业测试");
            onetest.setAnimalPic("drawable://" + R.drawable.lc_icon_learning);
            SysConstants.test_data_location.add(onetest);
        }
        if (testLists.size() > 0) {
            for (int i = 0; i < testLists.size(); i++) {
                TestList bean = testLists.get(i);
                TestListBean testList = new TestListBean();
                testList.setAnimalPic(bean.getAnimalPic());
                testList.setName(bean.getName());
                testList.setStatus(bean.getStatus());
                testList.setColor(bean.getColor());
                testList.setColorValue(bean.getColorValue());
                testList.setAssociateTag(bean.getAssociateTag());
                testList.setDescription(bean.getDescription());
                SysConstants.test_data_location.add(testList);
            }
        }
        if (SysConstants.test_data_location.size() > 0) {
            test_data = SysConstants.test_data_location;
            test_adapter.setItems(test_data);
        }
        TestHelper.getTestList(TestActivity.this, new NetManager.MonitorInterface() {
            @Override
            public void getContentFromNet(Object content) {
                if (content != null) {
                    isnet = true;
                    test_activity_empty.setVisibility(View.GONE);
                    test_list.setVisibility(View.VISIBLE);

                    List<TestListBean> _list = (List<TestListBean>) content;
                    if (_list.size() > 0) {
                        test_data.clear();
                    }
                    if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                        TestListBean onetest = new TestListBean();
                        onetest.setName("学能作业测试");
                        onetest.setAnimalPic("drawable://" + R.drawable.lc_icon_learning);
                        List<TestListBean> list = new ArrayList<TestListBean>();
                        list.add(onetest);
                        for (int i = 0; i < _list.size(); i++) {
                            TestListBean test = _list.get(i);
                            list.add(test);
                        }
                        test_data = list;
                    } else {
                        test_data = _list;
                    }
//					SysConstants.test_data_location=test_data;
                    test_adapter.setItems(test_data);

                    if (test_data.size() == 0) {
                        test_list.setVisibility(View.GONE);
                        test_content_empty.setVisibility(View.VISIBLE);

                    } else {
                        test_content_empty.setVisibility(View.GONE);
                    }
                } else {
                    if (test_data.size() == 0) {
                        test_list.setVisibility(View.GONE);
                        test_activity_empty.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_test_one) {
//			Intent intent = new Intent(TuxingApp.packageName + SysConstants.StartTest);
//			startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, int postion) {
        showToast("" + mDatas.get(postion));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "themeTest");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
