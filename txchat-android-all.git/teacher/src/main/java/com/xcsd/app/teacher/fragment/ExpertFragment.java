package com.xcsd.app.teacher.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tuxing.app.base.BaseFragment;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.app.teacher.activity.ExpertDetailedActivity;
import com.xcsd.app.teacher.activity.QuestionAskSearchActivity;
import com.xcsd.app.teacher.adapter.ExpertAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.event.quora.ExpertEvent;
import com.tuxing.sdk.modle.Expert;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.StringUtils;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;


public class ExpertFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, XListView.IXListViewListener {

    private View mContentView;
    private TextView mTitle;
    private XListView mListView;
    private ExpertAdapter mAdapter;
    private List<Banner> mBanners = new ArrayList<>();
    private List<Expert> mExperts = new ArrayList<>();
    private int mCurrentPage = 1;
    private boolean isActivity = false;
    private boolean hashMore = false;
    DisplayImageOptions mBannerCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExperts = new ArrayList<>();
        mAdapter = new ExpertAdapter(getActivity(), mExperts);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mBannerCache = new DisplayImageOptions.Builder()
                .showImageOnLoading(com.tuxing.app.R.drawable.defal_down_lym_proress)
                .showImageForEmptyUri(com.tuxing.app.R.drawable.defal_down_lym_fail)
                .showImageOnFail(com.tuxing.app.R.drawable.defal_down_lym_fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(mContentView==null) {
            mContentView = inflater.inflate(R.layout.teacher_help_expert, null);
            mContentView.findViewById(R.id.ll_left).setOnClickListener(this);
            mContentView.findViewById(R.id.tv_right).setOnClickListener(this);
            mTitle = (TextView) mContentView.findViewById(R.id.tv_title);
            mListView = (XListView) mContentView.findViewById(R.id.expert_list);
            mTitle.setText(R.string.teacher_help_expert);
            mListView.setOnItemClickListener(this);
            mListView.setXListViewListener(this);
            mListView.setPullRefreshEnable(false);
            mListView.setPullLoadEnable(false);
            mListView.startRefresh();
            mListView.setAdapter(mAdapter);
            mCurrentPage = 1;
        }
        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }
        return mContentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getService().getQuoraManager().getTopHotExperts();
        getService().getQuoraManager().getExperts(mCurrentPage);
    }

    public void onEventMainThread(ExpertEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_TOP_HOT_EXPERT_SUCCESS:
                    hashMore = event.isHasMore();
                    if (!CollectionUtils.isEmpty(event.getExperts())) {
                        mBanners.clear();
                        for (Expert expert : event.getExperts()) {
                            if (!StringUtils.isBlank(expert.getBanner())) {
                                Banner banner = new Banner(expert.getBanner(), expert.getExpertId());
                                mBanners.add(banner);
                            }
                        }
                    } else {
                        mBanners.clear();
                        Banner defaultBanner = new Banner("drawable://" + com.tuxing.app.R.drawable.banner, null);
                        defaultBanner.showDefault = true;
                        mBanners.add(defaultBanner);
                    }
                    break;
                case GET_TOP_HOT_EXPERT_FAILED:
                    Banner banner = new Banner(null, null);
                    banner.showDefaultBanner();
                    mBanners.clear();
                    mBanners.add(banner);
                    showToast(event.getMsg());
                    break;
                case GET_EXPERT_SUCCESS:
                    hashMore = event.isHasMore();
                    if (mCurrentPage == 1) {
                        mExperts.clear();
                    }
                    mExperts.addAll(event.getExperts());
                    mListView.setPullLoadEnable(event.isHasMore());
                    mListView.stopLoadMore();
                    mAdapter.notifyDataSetChanged();
                    break;
                case GET_EXPERT_FAILED:
                    mListView.stopLoadMore();

                    showToast(event.getMsg());
                    break;
            }
            showFooterView();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_left) {
            getActivity().finish();
        } else if (view.getId() == R.id.tv_right) {
            Intent intent = new Intent(getActivity(), QuestionAskSearchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;
        if (position >= 0 ) {
            int bg = position%3;

            Expert expert = mExperts.get(position);
            Intent intent = new Intent(getActivity(), ExpertDetailedActivity.class);
            intent.putExtra("expert_id", expert.getExpertId());
            intent.putExtra("expert",mExperts.get(position));
            if(bg == 0){
                intent.putExtra("backColorid", R.color.expert_item_bg1);
            }else if(bg == 1){
                intent.putExtra("backColorid", R.color.expert_item_bg2);
            }else if(bg == 2){
                intent.putExtra("backColorid", R.color.expert_item_bg3);
            }
            startActivity(intent);
        }
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        getService().getQuoraManager().getExperts(++mCurrentPage);
    }

    @Override
    public void onResume() {
        super.onResume();
        isActivity = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivity = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void showFooterView() {
        if (hashMore) {
            mListView.setPullLoadEnable(true);
        } else {
            mListView.setPullLoadEnable(false);
        }
    }

    public class Banner {
        String imageUrl;
        Long expertId;
        boolean showDefault;

        public Banner(String imageUrl, Long expertId) {
            this.imageUrl = imageUrl;
            this.expertId = expertId;
            this.showDefault = false;
        }

        public void showDefaultBanner() {
            this.showDefault = true;
        }
    }


}
