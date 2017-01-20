package com.tuxing.app.qzq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.qzq.adapter.MessageFriendsListAdapter;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.FeedEvent;
import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangst on 15-6-30.
 */
public class MyCircleListActivity extends BaseActivity implements XListView.IXListViewListener, View.OnClickListener {

    private XListView xListView;
    public static ArrayList<String> filePaths2;
    private MessageFriendsListAdapter adapter;
    private long currentID;
    private String currentUserName;
    private RoundImageView iv_portrait;
    private TextView tv_name;
    private LinearLayout btnMessageTitleRight;
    private ImageView ivFriendSend;

    private int mFlag = 0; // 0表示正常下载，1表示加载更多
    MyImageView iv_top_bg;
    public final int LISTSENDTEXT = 0x0005;
    public final int LISTSENDPIC = 0x0006;
    private List<Feed> list = new ArrayList<Feed>();
    List<Feed>  localList = new ArrayList<Feed>();//本地缓存
    List<Feed> netList = new ArrayList<Feed>();//网络获取
    UpdateReceiver receiver;
    boolean isMyself = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_my_circle);
        currentID = getIntent().getLongExtra("userId", 0);
        currentUserName = getIntent().getStringExtra("userName");
        if(user!=null&&currentID == user.getUserId()){
        	isMyself = true;
            setRightNext(false,"", R.drawable.ic_circle_more);
//            setQzqTitleBarBg(true);
        }else{
            setRightNext(false, "", 0);
//            setQzqTitleBarBg(false);
        }
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        setTitle(currentUserName);
        setLeftBack("", true, false);
        mFlag = 0;
        //获取自己的timeline
//        getlocalData();//本地
        getnetlData();
        initView();
        listViewInit();
        xListView.setXListViewListener(this);
        xListView.setHeaderbgColor(getResources().getColor(R.color.white));
        adapter = new MessageFriendsListAdapter(mContext, list,isMyself);
        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(false);
        receiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
        registerReceiver(receiver, intentFilter);
    }

    private void initView() {
        xListView = (XListView) findViewById(R.id.xListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onclickRightImg() {
        super.onclickRightImg();
        // 跳入到消息列表
        startActivityForResult(new Intent(mContext, MessageUnreadListActivity.class),100);
    }

    // listview的初始化
    public void listViewInit() {
        View view = getLayoutInflater().inflate(R.layout.qinzi_msg_list_header, null);
        // add send camera
        btnMessageTitleRight = (LinearLayout) view.findViewById(R.id.btnMessageTitleRight);
        iv_top_bg = (MyImageView) view.findViewById(R.id.iv_top_bg);
        iv_top_bg.setOnClickListener(this);
        ivFriendSend = (ImageView) view.findViewById(R.id.ivFriendSend);
        ivFriendSend.setOnClickListener(this);
        iv_portrait = (RoundImageView) view.findViewById(R.id.iv_portrait);
        iv_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (user != null && (currentID == user.getUserId())) {//表示自己
            btnMessageTitleRight.setVisibility(View.VISIBLE);
            iv_portrait.setImageUrl(user.getAvatar(), R.drawable.default_avatar);
        }else
            btnMessageTitleRight.setVisibility(View.GONE);

        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(currentUserName);
        xListView.addHeaderView(view);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 2) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, MessageUnreadDetailActivity.class);
                    intent.putExtra("feed", list.get(position - 2));
                    startActivity(intent);
                }
            }
        });
        User otherUser = getService().getUserManager().getUserInfo(currentID);
        if (otherUser != null) {// 个人头像判断性别
            iv_portrait.setImageUrl(otherUser.getAvatar(), R.drawable.default_avatar);
        }
    }

    //从net获取最近20条
    private  void getnetlData(){
        getService().getFeedManager().getLatestTimeLineByUser(currentID);
        showProgressDialog(this, "", true, null);
    }

    //从net获取之前20条
    private  void getnetlHistoryData(){
        if(list.size()>0){
            getService().getFeedManager().getHistoryTimeLineByUser(currentID, list.get(list.size() - 1).getFeedId());
        }
    }


    /**
     * 服务器返回
     * @param event
     */
    public void onEventMainThread(FeedEvent event){
        disProgressDialog();
        if(isActivity){
        switch (event.getEvent()){
            case GET_TIME_LINE_FROM_LOCAL_CACHE:
                if(event.getFeeds()!=null&&event.getFeeds().size()>0){
                    localList.clear();
                    localList = event.getFeeds();
                    if(netList.size()<=0){//如果网网络还没数据 刷新列表
                        list.addAll(localList);
                        //刷新列表
                        adapter.setData(list);
                    }
                }
                break;
            case REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_SUCCESS://userid
                xListView.stopRefresh();
                if(event.getFeeds()!=null&&event.getFeeds().size()>0){
                    netList.clear();
                    netList = event.getFeeds();
                    list.clear();
                    list.addAll(netList);
                    //刷新列表
                    adapter.setData(list);
                }else{
                    list.clear();
                    //刷新列表
                    adapter.setData(list);
                }
                if (event.isHasMore()) {
                    xListView.setPullLoadEnable(true);
                }else
                    xListView.setPullLoadEnable(false);

                break;
            case REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_FAILED://userid
                showToast("获取数据失败");
                break;
            case REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_SUCCESS://历史
                xListView.stopLoadMore();
                if(event.getFeeds()!=null&&event.getFeeds().size()>0){
                    list.addAll(event.getFeeds());
                    adapter.setData(list);
                }
                if (event.isHasMore()) {
                    xListView.setPullLoadEnable(true);
                }else
                    xListView.setPullLoadEnable(false);
                break;
            case REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_FAILED://历史
                xListView.stopLoadMore();
                break;
            default:
                break;
        }
    }}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null)
            unregisterReceiver(receiver);
    }

    @Override
    public void onRefresh() {
        mFlag = 0;
        getnetlData();
    }

    @Override
    public void onLoadMore() {
        mFlag = 1;
        getnetlHistoryData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.ivFriendSend){
            if(checkFeedMute()){
            }else{
                //发布新内容
                showBtnDialog(new String[]{getString(R.string.take_video),
                        getString(R.string.btn_info_text),
                        getString(R.string.btn_cancel)});

            }
        }
    }

    class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST)){
                int count = intent.getIntExtra("scoreCount",0);
                String isFrom = intent.getStringExtra("isFrom");
                if("myCircle".equals(isFrom)&&count>0){
                    showContextMenuScore(count);
                }
                getnetlData();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
    	super.onActivityResult(arg0, arg1, arg2);
    	if(arg1 == RESULT_OK){
    		 getnetlData();
    	}
    }

    @Override
    public void onclickBtn1() {
        Intent intent1 = new Intent(mContext, CircleViedeoReleaseActivity.class);
        intent1.putExtra("isFrom", "myCircle");//是从亲子圈进去的还是从我的亲子圈主页进去的?
        startActivity(intent1);
        super.onclickBtn1();
    }

    @Override
    public void onclickBtn2() {
        Intent intent2 = new Intent(mContext, CircleReleaseActivity.class);
        intent2.putExtra("isFrom","myCircle");//是从亲子圈进去的还是从我的亲子圈主页进去的?
        startActivity(intent2);
        super.onclickBtn2();
    }

}
