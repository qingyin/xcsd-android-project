package com.tuxing.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.ClassRoomListActivity;
import com.tuxing.app.activity.ClassRoomPlayerActivity;
import com.tuxing.app.activity.LyceumActivity;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.base.BaseFragment;
import com.tuxing.app.qzq.ParentCircleActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import java.util.Map;

public class DiscoveryFragment extends BaseFragment implements View.OnClickListener {
    private  View rootView;
    private ImageView qzq_hintnum;
    private ImageView activity_hintnum;
    private ImageView integral_hintnum;
    private ImageView wxy_hintnum;
    private boolean isteacher=false;
    private boolean isparent=false;


    private BroadcastReceiver counterChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == SysConstants.UPDATENEWEXPLORE){
                showCounter();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_discovery, null);

        setRightNext(true,"",0);
        ll_left_img.setVisibility(View.GONE);
        rootView.findViewById(R.id.rl_son_circle).setOnClickListener(this);
        RelativeLayout wxy = (RelativeLayout)rootView.findViewById(R.id.rl_wxy);
        RelativeLayout integral_shop = (RelativeLayout)rootView.findViewById(R.id.rl_integral_shop);
        RelativeLayout activity = (RelativeLayout)rootView.findViewById(R.id.rl_activity);
        RelativeLayout wkt = (RelativeLayout) rootView.findViewById(R.id.rl_wkt);
        wkt.setOnClickListener(this);

        // add by mey
//        RelativeLayout game = (RelativeLayout)rootView.findViewById(R.id.rl_integral_game);
//
//        game.setOnClickListener(this);
        isteacher = TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion());
        isparent = TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion());

        wxy.setOnClickListener(this);
        integral_shop.setOnClickListener(this);
        activity.setOnClickListener(this);
        qzq_hintnum = (ImageView)rootView.findViewById(R.id.iv_qzq_HintNum);
        activity_hintnum = (ImageView)rootView.findViewById(R.id.iv_activity_HintNum);
        integral_hintnum = (ImageView)rootView.findViewById(R.id.iv_integral_HintNum);
        wxy_hintnum = (ImageView)rootView.findViewById(R.id.iv_wxy_HintNum);

        if(isteacher){//教师版
            wkt.setVisibility(View.GONE);
//            wxy.setVisibility(View.GONE);
            integral_shop.setVisibility(View.GONE);
            activity.setVisibility(View.GONE);
        }else if(isparent){//家长版
            wkt.setVisibility(View.GONE);
            integral_shop.setVisibility(View.GONE);
            activity.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MobclickAgent.onEvent(getActivity(), "click_qzq", UmengData.click_qzq);
        setTitle(getResources().getString(R.string.tab_find));
        getActivity().registerReceiver(counterChangedReceiver, new IntentFilter(SysConstants.UPDATENEWEXPLORE));
    }

    @Override
    public void onResume() {
        super.onResume();
        showCounter();
        MobclickAgent.onResume(getActivity());
        MobclickAgent.onPageStart("发现界面");

    }

    @Override
    public void onPause() {
        super.onPause();

        MobclickAgent.onPause(getActivity());
        MobclickAgent.onPageEnd("发现界面");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_son_circle) {
            //亲子圈
            getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "parentcircle");
            startActivity(new Intent(getActivity(), ParentCircleActivity.class));

        }else if (id == R.id.rl_integral_shop) {
            // 积分商城
            WebSubUrlActivity.invoke(getActivity(),SysConstants.SHOP,getResources().getString(R.string.integral_shop));
        }else if(id == R.id.rl_activity){
            //活动专区
            WebSubUrlActivity.invoke(getActivity(),SysConstants.HUODONGZQ,getResources().getString(R.string.find_activity));
        }else if(id == R.id.rl_wxy){
            //理解孩子
            getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "understandchild");
            startActivity(new Intent(getActivity(), LyceumActivity.class));
//        }else if(id == R.id.rl_integral_game){
            //game bey mey
//            WebSubUrlActivity.invoke(getActivity(),"http://www.baidu.com","game");
        }else if(id==R.id.rl_wkt){
            //微课堂
//            startActivity(new Intent(getActivity(), ClassRoomPlayerActivity.class));
            startActivity(new Intent(getActivity(), ClassRoomListActivity.class));

        }
    }

    private void showCounter(){
        Map<String, Integer> counters = getService().getCounterManager().getCounters();
        Integer feedCounter = counters.get(Constants.COUNTER.FEED);
        Integer commentCounter = counters.get(Constants.COUNTER.COMMENT);
        Integer learnGardenCounter = counters.get(Constants.COUNTER.LEARN_GARDEN);

        if(feedCounter != null && feedCounter > 0 ||
                commentCounter !=null && commentCounter > 0){
            qzq_hintnum.setVisibility(View.VISIBLE);
        }else{
            qzq_hintnum.setVisibility(View.GONE);
        }

        if(learnGardenCounter != null && learnGardenCounter > 0) {
            wxy_hintnum.setVisibility(View.VISIBLE);
        }else{
            wxy_hintnum.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(counterChangedReceiver!=null){
            getActivity().unregisterReceiver(counterChangedReceiver);
        }
    }
}