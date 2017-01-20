package com.xcsd.app.teacher.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.tuxing.app.base.BaseFragment;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.app.teacher.activity.QuestionAskSearchActivity;
import com.xcsd.app.teacher.activity.QuestionInfoActivity;
import com.xcsd.app.teacher.adapter.QuestionAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.quora.QuestionEvent;
import com.tuxing.sdk.modle.Question;
import com.tuxing.sdk.utils.CollectionUtils;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class QuestionFragment extends BaseFragment implements IXListViewListener,OnItemClickListener {
    View rootView;
    private XListView swipListView;
    private TextView rb_hot;
    private TextView rb_new;
    private int currentType = 0;//0 最新 1 热门
    private int currentPage = 1;
    private boolean hostHasMore = false;
    private boolean newHasMore = false;
    private Map<Integer, List<Question>> questionMap;
    private boolean isActivity = false;
    private QuestionAdapter adapter;
    private PersonalQAAdapter mAdapter;
    private String TAG = QuestionFragment.class.getSimpleName();
    private NewQuestionReceiver questionReceiver;
    private boolean refreshHot = false;
    private boolean refreshNew = false;
    private String mTabs[] = new String[]{"最新","热门"};
    private ViewPager mViewPager;
    private List<XListView> mViews = new ArrayList<>();
    private List<QuestionAdapter> adapterList = new ArrayList<>();
    private boolean isQuesFragement = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if(rootView==null){
            rootView = inflater.inflate(R.layout.teacher_help_question, null);
            rootView.findViewById(R.id.ll_left).setOnClickListener(this);
            rootView.findViewById(R.id.tv_right).setOnClickListener(this);
            rb_hot = (TextView)rootView.findViewById(R.id.rb_question_hot);
            rb_new = (TextView)rootView.findViewById(R.id.rb_question_new);
            mViewPager = (ViewPager) rootView.findViewById(R.id.question_viewpager);
            rb_hot.setSelected(false);
            rb_new.setSelected(true);
            rb_hot.setOnClickListener(this);
            rb_new.setOnClickListener(this);
            mViewPager.setOnPageChangeListener(new PageChangeListener());
            mViewPager.setAdapter(mAdapter);

        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(refreshHot){
            getService().getQuoraManager().getLatestQuestions(null);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        for (int i = 0; i < mTabs.length; i++) {
            swipListView = (XListView) LayoutInflater.from(getActivity()).inflate(R.layout.answer_question_listview, null);
            swipListView.setId(i);
            swipListView.setFootBack(getResources().getColor(R.color.bg));
            adapter = new QuestionAdapter(getActivity(), new ArrayList<Question>());
            swipListView.setXListViewListener(this);
            swipListView.setOnItemClickListener(this);
            swipListView.setAdapter(adapter);
            adapterList.add(adapter);
            mViews.add(swipListView);
        }

        mAdapter = new PersonalQAAdapter(mViews);
        questionMap = new ConcurrentHashMap<>();
        questionMap.put(0, new ArrayList<Question>());
        questionMap.put(1, new ArrayList<Question>());

        swipListView = mViews.get(currentType);
        adapter = adapterList.get(currentType);
        swipListView.startRefresh();
        showFooterView();
        getService().getQuoraManager().getLatestQuestions(null);
        questionReceiver = new NewQuestionReceiver();
        getActivity().registerReceiver(questionReceiver, new IntentFilter(SysConstants.UPDATEQUESTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivity = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isActivity = true;
        if(isQuesFragement && currentType == 1){
            isQuesFragement = false;
            currentType = 0;
            mViewPager.setCurrentItem(currentType);
        }else{
            if(currentType == 1 && refreshHot){
                refreshHot = false;
                currentPage = 1;
                getService().getQuoraManager().getTopHotQuestions(currentPage);
            }else if(currentType == 0 && refreshNew){
                refreshNew = false;
                getService().getQuoraManager().getLatestQuestions(null);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(questionReceiver != null){
            getActivity().unregisterReceiver(questionReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_left) {
            getActivity().finish();
        } else if (view.getId() == R.id.tv_right) {
            Intent intent = new Intent(getActivity(), QuestionAskSearchActivity.class);
            intent.putExtra("isQuesFragement",true);
            startActivity(intent);
        }else if(view.getId() == R.id.rb_question_hot){
            currentType = 1;
            mViewPager.setCurrentItem(currentType);
        }else if(view.getId() == R.id.rb_question_new){
            currentType = 0;
            mViewPager.setCurrentItem(currentType);
        }
    }


    private void updateAdapter(){
            if(adapter == null) {
                adapter = new QuestionAdapter(getActivity(), questionMap.get(currentType));
                swipListView.setAdapter(adapter);
            }else{
                adapter.setData(questionMap.get(currentType));
            }
        showFooterView();
    }

    @Override
    public void onRefresh() {
      if(currentType == 1){
          currentPage = 1;
          getService().getQuoraManager().getTopHotQuestions(currentPage);
      }else if(currentType == 0){
          getService().getQuoraManager().getLatestQuestions(null);
      }
    }

    @Override
    public void onLoadMore() {
        if(currentType == 1){
            currentPage += 1;
            getService().getQuoraManager().getTopHotQuestions(currentPage);
        }else if(currentType == 0){
            List<Question> latestQuestions = questionMap.get(currentType);
            long maxId = latestQuestions.get(latestQuestions.size() - 1).getQuestionId();
            getService().getQuoraManager().getHistoryQuestions(null, maxId);
        }

    }


    private void getRefresh(List<Question> refreshList) {
        swipListView = mViews.get(currentType);
        if(!CollectionUtils.isEmpty(refreshList)){
            questionMap.get(currentType).clear();
            questionMap.get(currentType).addAll(refreshList);
        }
        swipListView.stopRefresh();
        updateAdapter();

    }

    public void getLoadMore(List<Question> list) {
        swipListView = mViews.get(currentType);
        if(!CollectionUtils.isEmpty(list)){
            questionMap.get(currentType).addAll(list);
        }
        updateAdapter();
        swipListView.stopLoadMore();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        position = position - 1;
        if (position >= 0) {
            Intent intent = new Intent(getActivity(), QuestionInfoActivity.class);
            intent.putExtra("question", questionMap.get(currentType).get(position));
            startActivity(intent);
        }

    }

    public void showFooterView() {
        if ((currentType == 1 && hostHasMore) || (currentType == 0 && newHasMore)) {
            mViews.get(currentType).setPullLoadEnable(true);
        } else {
            mViews.get(currentType).setPullLoadEnable(false);
        }
    }

    class NewQuestionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SysConstants.UPDATEQUESTION) && intent.getBooleanExtra("isReplase",false)){
                isQuesFragement =  intent.getBooleanExtra("isQuesFragement",false);
                refreshHot = true;
                refreshNew = true;
            }else{
                    refreshHot = true;
                    refreshNew = true;
            }
        }
    }

    public void onEventMainThread(QuestionEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_TOP_HOT_QUESTION_SUCCESS:
                    hostHasMore =  event.isHasMore();
                    if(currentPage == 1){
                        getRefresh(event.getQuestions());
                    }else{
                        getLoadMore(event.getQuestions());
                    }
                    MyLog.getLogger(TAG).d("获取最热的问题列表成功 size = " + event.getQuestions().size());
                    break;
                case GET_TOP_HOT_QUESTION_FAILED:
                    mViews.get(currentType).stopRefresh();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("获取最热的问题列表失败 msg = " + event.getMsg() );
                    break;
                case GET_LATEST_QUESTION_SUCCESS:
                    newHasMore =  event.isHasMore();
                    getRefresh(event.getQuestions());
                    MyLog.getLogger(TAG).d("获取最新的问题列表成功 size = " + event.getQuestions().size());
                    break;
                case GET_LATEST_QUESTION_FAILED:
                    mViews.get(currentType).stopRefresh();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("获取最新的问题列表失败 msg = " + event.getMsg() );
                    break;
                case GET_HISTORY_QUESTION_SUCCESS:
                    if(currentType == 1){
                        hostHasMore =  event.isHasMore();
                    }else if(currentType == 0){
                        newHasMore =  event.isHasMore();
                    }
                    getLoadMore(event.getQuestions());
                    MyLog.getLogger(TAG).d("获取历史的问题列表成功 size = " + event.getQuestions().size());
                    break;
                case GET_HISTORY_QUESTION_FAILED:
                    mViews.get(currentType).stopLoadMore();
                    showToast(event.getMsg());
                    MyLog.getLogger(TAG).d("获取历史的问题列表失败 msg = " + event.getMsg() );
                    break;
            }
        }
    }


    class PersonalQAAdapter extends PagerAdapter {

        List<XListView> mViews;

        public PersonalQAAdapter(List<XListView> views) {
            this.mViews = views;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViews.get(position));

            return mViews.get(position);
        }
    }

    class PageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if(i == 1){
                rb_hot.setSelected(true);
                rb_new.setSelected(false);
                currentType = 1;
                swipListView = mViews.get(currentType);
                adapter = adapterList.get(currentType);
                if(questionMap.get(currentType).size() > 0){
                    if(refreshHot){
                        currentPage = 1;
                        refreshHot = false;
                        getService().getQuoraManager().getTopHotQuestions(currentPage);
                    }else{
                        updateAdapter();
                    }
                }else{
                    currentPage = 1;
                    getService().getQuoraManager().getTopHotQuestions(currentPage);
                }
            }else if(i == 0){
                rb_hot.setSelected(false);
                rb_new.setSelected(true);
                currentType = 0;
                swipListView = mViews.get(currentType);
                adapter = adapterList.get(currentType);
                if(questionMap.get(currentType).size() > 0){
                    if(refreshNew){
                        refreshNew = false;
                        getService().getQuoraManager().getLatestQuestions(null);
                    }else{
                        updateAdapter();
                    }
                }else{
                    mViews.get(currentType).startRefresh();
                    newHasMore = false;
                    showFooterView();
                    updateAdapter();
                    getService().getQuoraManager().getLatestQuestions(null);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

}
