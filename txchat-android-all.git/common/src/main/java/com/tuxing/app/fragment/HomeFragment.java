package com.tuxing.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.picCarousel.AutoScrollViewPager;
import com.tuxing.app.MainActivity;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.ClassRoomListActivity;
import com.tuxing.app.activity.CocosJSActivity;
import com.tuxing.app.activity.ContactActivity;
import com.tuxing.app.activity.GameWebSubUrlActivity;
import com.tuxing.app.activity.GetVerificationActivity;
import com.tuxing.app.activity.RadaActivity;
import com.tuxing.app.activity.SelectFolksActivity;
import com.tuxing.app.activity.TestActivity;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.activity.WebSubDataActivityNoShare;
import com.tuxing.app.base.BaseFragment;
import com.tuxing.app.domain.HomeMenuItem;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.view.MyGridView;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class HomeFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
    private static final String BANNER_IMAGE_FORMAT = "?imageMogr2/thumbnail/" + 640 + "x" + 390 + "/quality/100";
    private View view;
    private MyGridView grieView;
    private ImageView banner;
    private IconAdapter adapter;
    private Context mContext;
    private TuxingApp app;
    private boolean isActivity = false;
    private List<HomeMenuItem> menuItems;
    private DisplayImageOptions options;
    private User user;
    private List<Banner> banners = new ArrayList<Banner>();
    private MyPagerAdapter myPagerAdapter;
    String name[]=null;
    String token = null;
    public static HomeFragment instance = null;

    public LinearLayout ll_allview;
    public ScrollView sc_all;
    // private CirclePageIndicator indicator;
    private BroadcastReceiver counterChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == SysConstants.HOMEREFRESHACTION) {
                updateAdapter();
            }
        }
    };


    private String TAG = HomeFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_home, null);
        mContext = getActivity();

        instance = this;

        isActivity = true;
        app = (TuxingApp) getActivity().getApplication();
        grieView = (MyGridView) view.findViewById(R.id.home_gridview);
        // indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
//        banner = (ImageView) view.findViewById(R.id.iv_home_banner);
//        banner.setOnClickListener(this);
        setRightNext(false, "", 0);
        ll_left_img.setVisibility(View.GONE);
        grieView.setOnItemClickListener(this);

        //******
        sc_all = (ScrollView) view.findViewById(R.id.sc_all);

        sc_all.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.instance.detector.onTouchEvent(event);
                return false;
            }
        });
        sc_all.setLongClickable(true);

        ll_allview = (LinearLayout) view.findViewById(R.id.ll_allview);
        ll_allview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.instance.detector.onTouchEvent(event);
                return false;
            }
        });
        ll_allview.setLongClickable(true);

        grieView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.instance.detector.onTouchEvent(event);
                return false;
            }
        });
        grieView.setLongClickable(true);
//   *************

        initMenu();
        initBanners();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.banner_bg)
                .showImageForEmptyUri(R.drawable.banner_bg)
                .showImageOnFail(R.drawable.banner_bg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        updateAdapter();
        return view;
    }

    public void initMenu() {
        try {
            String strHomeMenu ="";
            String strHomeMenu_NAME ="";
            if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
                strHomeMenu = getService().getUserManager().getUserProfile(
                        Constants.SETTING_FIELD.HOME_MENU, SysConstants.DEFAULT_HOME_MENU_ITEMS_P);
                strHomeMenu_NAME = getService().getUserManager().getUserProfile(
                        Constants.SETTING_FIELD.HOME_NAME, "");
            }else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
                strHomeMenu = getService().getUserManager().getUserProfile(
                        Constants.SETTING_FIELD.HOME_MENU, SysConstants.DEFAULT_HOME_MENU_ITEMS);
                strHomeMenu_NAME = getService().getUserManager().getUserProfile(
                        Constants.SETTING_FIELD.HOME_NAME, "");
            }

            //by mey
//			String strHomeMenu = "announcement,activity,recipes,checkIn,notice,medicine,mail,insurance,game";
//            String strHomeMenu = "safety,teachclub,communication,homeWork,game,achievement,announcement,notice,activity";


//            String strHomeMenu = "";
//            String sHomeMenu = "studententsefaty,homework,game,achievement,announcement,notice,activity";
//            String sTeacherMenu = "teachercommunity,addressList,homework,game,achievement,announcement,notice,activity";
//
//            menuItems = new ArrayList<HomeMenuItem>();
//            if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
//                strHomeMenu = sHomeMenu;
//                for (String item : strHomeMenu.split(",")) {
//                    if (SysConstants.HOME_MENU_ITEMS.containsKey(item)) {
//                        menuItems.add(SysConstants.HOME_MENU_ITEMS.get(item));
//                    }
//                }
//            } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//                strHomeMenu = sTeacherMenu;
//                for (String item : strHomeMenu.split(",")) {
//                    if (SysConstants.HOME_MENU_ITEMS.containsKey(item)) {
//                        menuItems.add(SysConstants.HOME_MENU_ITEMS.get(item));
//                    }
//                }
//            }
            menuItems = new ArrayList<HomeMenuItem>();
            for (String item : strHomeMenu.split(",")) {
                if (SysConstants.HOME_MENU_ITEMS.containsKey(item)) {
                    menuItems.add(SysConstants.HOME_MENU_ITEMS.get(item));
                }
            }
            int i = 0;
            for (String items : strHomeMenu_NAME.split(",")) {
                menuItems.get(i).setText_name(items);
                i++;
            }
//            if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
//                menuItems.remove(SysConstants.HOME_MENU_ITEMS.get("teachclub"));
//                menuItems.remove(SysConstants.HOME_MENU_ITEMS.get("communication"));
//            }else if(TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
//                menuItems.remove(SysConstants.HOME_MENU_ITEMS.get("safety"));
//            }
            updateAdapter();
        } catch (Exception e) {
            MyLog.getLogger(TAG).d("家园获取菜单失败!" + e.getMessage().toString());
        }
    }

    private void initBanners() {
        if (getService().getLoginManager().getCurrentUser() != null) {
            try {
//                Thread.sleep(500);
                banners.clear();
                String bannerJson = getService().getUserManager().getUserProfile(
                        Constants.SETTING_FIELD.HOME_BANNERS, "[]");
                JSONArray array = new JSONArray(bannerJson);
                Banner banner;
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        banner = new Banner(array.getJSONObject(i).getString("imgUrl") + BANNER_IMAGE_FORMAT,
                                array.getJSONObject(i).getString("url"));
                        banners.add(banner);
                    }
                }

            } catch (JSONException e) {
                MyLog.getLogger(TAG).d("解析bannerJson 失败 msg = " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                MyLog.getLogger(TAG).d("显示banner事失败 msg = " + e.toString());
                e.printStackTrace();
            }

            if (banners.size() == 0) {
                Banner defaultBanner = new Banner("drawable://" + R.drawable.banner_bg, null);
                banners.add(defaultBanner);
            }
        }
    }

    public void showBnners() {
        if (banners.size() == 1) {
            //   indicator.setVisibility(View.GONE);
        } else {
            //  indicator.setVisibility(View.VISIBLE);
        }

        AutoScrollViewPager pager = (AutoScrollViewPager) view.findViewById(R.id.scroll_pager);

        RelativeLayout viewPagerRl = (RelativeLayout) view.findViewById(R.id.viewPagerRl);

        int width = DeviceUtils.getScreenWidth(mContext);

        int height = width * 390 / 640;

        pager.getLayoutParams().height = height;
        myPagerAdapter = new MyPagerAdapter();
        pager.setAdapter(myPagerAdapter);
//        pager.setAdapter(new MyPagerAdapter());

        //  indicator.setViewPager(pager);
        // indicator.setSnap(true);

        pager.startAutoScroll(2000);
        pager.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(AutoScrollViewPager autoScrollPager, int position) {
                //图片点击事件
                if (!android.text.TextUtils.isEmpty(banners.get(position).url)) {
                    WebSubDataActivity.invoke(getActivity(), true, banners.get(position).url, "详情");

                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MobclickAgent.onEvent(getActivity(), "click_home", UmengData.click_home);
        String title = getResources().getString(R.string.tab_home);
//        if (getService().getLoginManager().getCurrentUser() != null) {
//            if (!StringUtils.isBlank(getService().getLoginManager().getCurrentUser().getGardenName())) {
//                title = getService().getLoginManager().getCurrentUser().getGardenName();
//            }
//        }
        setTitle(title);
        showBnners();
        user = CoreService.getInstance().getLoginManager().getCurrentUser();
//原来的显示广告的逻辑

//        if (getService().getLoginManager().getCurrentUser() != null) {
//            String gardenBanner = getService().getUserManager().getUserProfile(
//                    Constants.SETTING_FIELD.GARDEN_IMAGE, "");
//
//            if (!StringUtils.isBlank(gardenBanner)) {
//                ImageLoader.getInstance().displayImage(gardenBanner + "?imageMogr2/thumbnail/640x360/quality/100"
//                        , banner, options);
//            } else {
//                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.banner, banner, options);
//            }
//        }

        updateAdapter();
        getActivity().registerReceiver(counterChangedReceiver, new IntentFilter(SysConstants.HOMEREFRESHACTION));

        CocosJSActivity.loadNativeLibraries();
    }

    @Override
    public void onResume() {
        isActivity = true;
        super.onResume();
        updateAdapter();
        MobclickAgent.onResume(getActivity());
        MobclickAgent.onPageStart("家园界面");
    }


    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.iv_home_banner) {
//            WebSubDataActivity.invoke(getActivity(), SysConstants.GARDENURL + getService().getLoginManager().getCurrentUser().getGardenId(), "学校介绍");
//        }


    }

    @Override
    public void onclickRightImg() {
        WebSubDataActivity.invoke(getActivity(), SysConstants.GARDENURL + getService().getLoginManager().getCurrentUser().getGardenId(), "学校介绍");
        super.onclickRightImg();
    }

    public void updateAdapter() {
        if (adapter == null) {
            adapter = new IconAdapter(mContext, menuItems);
            grieView.setAdapter(adapter);
        } else {
            grieView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


    public class IconAdapter extends BaseAdapter {
        private Context context;
        private List<HomeMenuItem> menuItems;

        public IconAdapter(Context context, List<HomeMenuItem> menuItems) {
            this.context = context;
            this.menuItems = menuItems;
        }

        @Override
        public int getCount() {
            return menuItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.home_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.home_item_icon);
//                viewHolder.title = (TextView) convertView.findViewById(R.id.home_item_name);
                viewHolder.isNew = (ImageView) convertView.findViewById(R.id.home_item_isnew);
                viewHolder.unreanNum = (TextView) convertView.findViewById(R.id.home_item_unread_num);
                viewHolder.horizontalLine = convertView.findViewById(R.id.horizontal_line);
                viewHolder.verticalLine = convertView.findViewById(R.id.vertical_line);
                viewHolder.home_item_name = (TextView) convertView.findViewById(R.id.home_item_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position < menuItems.size()) {
                HomeMenuItem menuItem = menuItems.get(position);
                viewHolder.icon.setImageDrawable(getResources().getDrawable(menuItem.icon));
                String item_name = null;
                item_name=menuItem.getText_name();
                viewHolder.home_item_name.setText(item_name);
                if (menuItem.name.equals(getResources().getString(R.string.xn_homeWork))){
                    SysConstants.HomeWorkTitle=item_name;
                }
                if (menuItem.name.equals("themeTest")){
                    SysConstants.TestTitle=item_name;
                }
//                viewHolder.title.setText(menuItem.name);

                Map<String, Integer> counters = getService().getCounterManager().getCounters();
                if (!StringUtils.isBlank(menuItem.counter)) {
                    Integer counter = counters.get(menuItem.counter);
                    if (counter != null && counter > 0) {
                        if (menuItem.showCount) {
                            viewHolder.unreanNum.setVisibility(View.VISIBLE);
                            if (counter >= 10) {
                                viewHolder.unreanNum.setTextSize(8);
                            } else {
                                viewHolder.unreanNum.setTextSize(10);
                            }
                            viewHolder.unreanNum.setText(counter > 99 ? "···" : String.valueOf(counter));
                            viewHolder.isNew.setVisibility(View.GONE);
                        } else {
                            viewHolder.isNew.setVisibility(View.VISIBLE);
                            viewHolder.unreanNum.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.unreanNum.setVisibility(View.GONE);
                        viewHolder.isNew.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.unreanNum.setVisibility(View.GONE);
                    viewHolder.isNew.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

    }

    public class ViewHolder {
        ImageView icon;
        ImageView isNew;
        //        TextView title;
        View verticalLine;
        View horizontalLine;
        TextView unreanNum;
        TextView home_item_name;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        isActivity = false;

        if (position < menuItems.size()) {
            String activity = menuItems.get(position).activityClass;

            if (menuItems.get(position).name.equals(getResources().getString(R.string.zywyou))) {
                WebSubDataActivity.invoke(getActivity(), true, SysConstants.wuyouhome, getResources().getString(R.string.zywyou));

            } else if (menuItems.get(position).name.equals(getResources().getString(R.string.xn_game))) {
                if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {

                    CocosJSActivity.setClassLoaderFrom(this.getActivity());
                    CocosJSActivity.goToGameLobby();
                    MobclickAgent.onEvent(getActivity(), "click_game", UmengData.click_game);
                } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {

                    CocosJSActivity.setClassLoaderFrom(this.getActivity());
                    CocosJSActivity.goToGameLobby();
                }

            } else if (menuItems.get(position).name.equals(getResources().getString(R.string.xn_achievement))) {
                if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                    getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "achievement");

                    Intent intent1 = new Intent(getActivity(), RadaActivity.class).putExtra("name",menuItems.get(position).text_name);;
                    startActivity(intent1);
                    MobclickAgent.onEvent(getActivity(), "click_achievement", UmengData.click_achievement);
                } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//                    教师端判断
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.SCORESHOW);
                    startActivity(intent);
                }
            } else if (menuItems.get(position).name.equals(getResources().getString(R.string.xn_homeWork))) {
                getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "homework");
                //HomeWorkManagerImpl.getInstance().HomeworkSentListRequest(null,null);
                if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                    MobclickAgent.onEvent(getActivity(), "click_studyhomework", UmengData.click_studyhomework);
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.STUDYHOMEWORKACTION).putExtra("name",menuItems.get(position).text_name);
                    startActivity(intent);

                } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//                    教师端判断
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.TRAIN).putExtra("name",menuItems.get(position).text_name);
                    startActivity(intent);
                }

            } else if (menuItems.get(position).name.equals(getResources().getString(R.string.xn_studententsefaty))) {
                if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                    LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                    if (loginUser != null) {
                        token = loginUser.getToken();

                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    String url = "http://mm.tx2010.com/safe/m.do";
                    stringBuilder.append(url);
                    stringBuilder.append("?");
                    stringBuilder.append("token=" + token);

                    Log.i("--------shan", stringBuilder.toString());

                    WebSubDataActivityNoShare.invoke(mContext, stringBuilder.toString(),
                            getString(R.string.studentsafety));
                    MobclickAgent.onEvent(getActivity(), "click_studententsefaty", UmengData.click_studententsefaty);
                } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//                    教师端判断
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.TRAIN);
                    startActivity(intent);
                }

            } else if (menuItems.get(position).name.equals(getResources().getString(R.string.xn_teachercommunity))) {
                if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.STUDYHOMEWORKACTION);
                    startActivity(intent);

                } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//                    教师端判断
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.TEACHERCLUBACTION);
                    startActivity(intent);
                }

            } else if (menuItems.get(position).name.equals(getResources().getString(R.string.xn_addressList))) {
                if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.STUDYHOMEWORKACTION);
                    startActivity(intent);

                } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//                    教师端判断
                    Intent intent = new Intent(getActivity(), ContactActivity.class);
                    startActivity(intent);
                }

            }else if (menuItems.get(position).name.equals("themeTest")){
                getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "themeTest");
                Intent intent = new Intent(getActivity(), TestActivity.class).putExtra("name",menuItems.get(position).text_name);
                startActivity(intent);
            }
            else if (menuItems.get(position).name.equals("course")){
                Intent intent = new Intent(getActivity(), ClassRoomListActivity.class).putExtra("name",menuItems.get(position).text_name);
                startActivity(intent);
            }
            else if (menuItems.get(position).name.equals("notice")){
                getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "notice");
                Intent intent = new Intent(TuxingApp.packageName + SysConstants.NOTICEACTION);
                startActivity(intent);
            }
            else {
                if (!StringUtils.isBlank(activity)) {
                    openActivity(activity);
                }
            }
        }
    }

    @Override
    public void onPause() {
        isActivity = false;
        MobclickAgent.onPause(getActivity());
        MobclickAgent.onPageEnd("家园界面");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (counterChangedReceiver != null) {
            getActivity().unregisterReceiver(counterChangedReceiver);
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(banners.get(position).imageUrl, view, options);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class Banner {
        String imageUrl;
        String url;

        public Banner(String imageUrl, String url) {
            this.imageUrl = imageUrl;
            this.url = url;
        }

    }


}
