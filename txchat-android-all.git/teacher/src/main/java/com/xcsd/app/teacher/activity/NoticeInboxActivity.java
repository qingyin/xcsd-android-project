package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.app.teacher.adapter.NoticeInboxAdapter;
import com.xcsd.app.teacher.tea.NoticeOutboxInfoActivity;
import com.xcsd.app.teacher.tea.NoticeReleaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.event.NoticeEvent;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NoticeInboxActivity extends BaseActivity implements IXListViewListener, OnItemClickListener {

    private XListView swipListView;
    //	private List<Notice> noticeList = new ArrayList<Notice>();
    private String TAG = NoticeInboxActivity.class.getSimpleName();
    private boolean hasMore = false;
    private RadioGroup rg;
    private RadioButton rb_inbox;
    private RadioButton rb_outbox;
    private Map<Integer, List<Notice>> noticeMap;
    private int currentType = Constants.MAILBOX_INBOX;
    private int currentPosition = 0;
    private RelativeLayout notice_bg;
    private ImageView hey_icon;
    private final  int requestDetaliCode = 0x110;
    private final  int requestRelease = 0x111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noticeMap = new HashMap<Integer, List<Notice>>();
        setContentLayout(R.layout.home_notice_layout);
        init();
        showProgressDialog(mContext, "", true, null);
        getData();

//        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "notice");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivity = true;
        if(currentType == Constants.MAILBOX_OUTBOX){
            getService().getNoticeManager().getLocalCachedNotice(currentType);//从本地和网络获取发件箱
        }
        swipListView.setSelection(currentPosition);
    }

    private void init() {
        setTitle(getString(R.string.tab_child_marking));
        setLeftBack("", true, false);
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
            setRightNext(false, "", R.drawable.ic_circle_more_t);
            setSecondRight(true, R.drawable.title_edit_icon);
        }else if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            setRightNext(false, "", R.drawable.ic_circle_more_p);
        }
        swipListView = (XListView) findViewById(R.id.notice_list);
        swipListView.setXListViewListener(this);
        swipListView.setOnItemClickListener(this);
        swipListView.startRefresh();
        rg = (RadioGroup) findViewById(R.id.rg);
        rb_inbox = (RadioButton) findViewById(R.id.rb_inbox);
        rb_outbox = (RadioButton) findViewById(R.id.rb_outbox);
        notice_bg = (RelativeLayout) findViewById(R.id.notice_bg);
        hey_icon = (ImageView) findViewById(R.id.hey_icon);
        rb_inbox.setOnClickListener(this);
        rb_outbox.setOnClickListener(this);
        notice_bg.setOnClickListener(this);
        hey_icon.setOnClickListener(this);
        swipListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * 滚动状态改变时调用
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 不滚动时保存当前滚动到的位置
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    currentPosition = swipListView.getFirstVisiblePosition();
                }
            }

            /**
             * 滚动时调用
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void initData() {
        // TODO   本地数据
        getService().getNoticeManager().getLocalCachedNotice(Constants.MAILBOX_INBOX);
    }

    /**
     * 服务器返回
     *
     * @param event
     */
    public void onEventMainThread(NoticeEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case NOTICE_OUTBOX_FROM_CACHE:
                case NOTICE_INBOX_FROM_CACHE:
                    noticeMap.put(currentType, event.getNotices());
                    hasMore = event.isHasMore();
                    getService().getNoticeManager().getLatestNotice(currentType);
                    showAndSaveLog(TAG, "获取本地最新收件通知成功 size = " + event.getNotices().size(), false);
                    updataAdapter();
                    break;
                case NOTICE_OUTBOX_LATEST_NOTICE_SUCCESS:
                case NOTICE_INBOX_LATEST_NOTICE_SUCCESS:
                    hasMore = event.isHasMore();
                    getResresh(event.getNotices());
                    showAndSaveLog(TAG, "获取服务器最新收件通知成功 size = " + event.getNotices().size(), false);
                    break;
                case NOTICE_OUTBOX_REQUEST_SUCCESS:
                case NOTICE_INBOX_REQUEST_SUCCESS:
                    hasMore = event.isHasMore();
                    getLoadMore(event.getNotices());
                    showAndSaveLog(TAG, "请求历史收件通知成功 size = " + event.getNotices().size(), false);
                    break;
                case NOTICE_OUTBOX_REQUEST_FAILED:
                case NOTICE_INBOX_REQUEST_FAILED:
                    showToast(event.getMsg());
                    updataAdapter();
                    swipListView.stopLoadMore();
                    swipListView.stopRefresh();
                    showAndSaveLog(TAG, "请求收件通知失败" + event.getMsg(), false);
                    break;
                case NOTICE_CLEAR_SUCCESS:
                    hasMore = event.isHasMore();
                    if(currentType == Constants.MAILBOX_INBOX &&  noticeMap.get(Constants.MAILBOX_INBOX) != null){
                        noticeMap.get(Constants.MAILBOX_INBOX).clear();
                        showAndSaveLog(TAG, "清除收件箱成功", false);
                    }else if(currentType == Constants.MAILBOX_OUTBOX && noticeMap.get(Constants.MAILBOX_OUTBOX) != null){
                        noticeMap.get(Constants.MAILBOX_OUTBOX).clear();
                        showAndSaveLog(TAG, "清除发件箱成功" , false);
                    }
                    Intent intent = new Intent(TuxingApp.packageName + SysConstants.DELREFRESHACTION);
                    intent.putExtra("action","notice");
                    sendBroadcast(intent);
                    updataAdapter();
                    disProgressDialog();
                    break;
                case NOTICE_CLEAR_FAILED:
                    showToast(event.getMsg());
                    disProgressDialog();
                    showAndSaveLog(TAG, "清除通知失败" + event.getMsg(), false);
                    break;

            }
        }
    }


    @Override
    public void onRefresh() {
        getService().getNoticeManager().getLatestNotice(currentType);
    }

    private void getResresh(List<Notice> refreshList) {
        noticeMap.put(currentType, refreshList);
        updataAdapter();
        swipListView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        if (noticeMap.get(currentType).size() >= 0) {
            long lastId = noticeMap.get(currentType).get(noticeMap.get(currentType).size() - 1).getNoticeId();
            getService().getNoticeManager().getHistoryNotice(lastId, currentType);
        } else {
            swipListView.stopLoadMore();
        }
    }

    public void getLoadMore(List<Notice> list) {
        if (list != null && list.size() > 0) {
            List<Notice> tmpList = noticeMap.get(currentType);
            tmpList.addAll(list);
            noticeMap.put(currentType, tmpList);
        }
        swipListView.stopLoadMore();
        updataAdapter();
    }

    public void showFooterView() {
        if (hasMore) {
            swipListView.setPullLoadEnable(true);
        } else {
            swipListView.setPullLoadEnable(false);
        }
    }


    public void updataAdapter() {
        if(noticeMap.get(currentType) != null && noticeMap.get(currentType).size() > 0){
            notice_bg.setVisibility(View.GONE);
        }else{
            notice_bg.setVisibility(View.VISIBLE);
        }
        NoticeInboxAdapter adapter = new NoticeInboxAdapter(this, noticeMap.get(currentType),currentType);
        swipListView.setAdapter(adapter);
        showFooterView();
    }

    // 读取通知
    private void readNotice(Notice notice) {
        getService().getNoticeManager().markAsRead(notice.getNoticeId());
        if (notice.getUnread()) {
            sendReceiver();
        }
    }

    /**
     * 发送读取通知的广播
     */
    private void sendReceiver() {
        Intent intent = new Intent();
        intent.setAction(SysConstants.READNOTICEACTION);
        sendBroadcast(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        position = position - 1;
        if (position >= 0) {
            if(currentType == Constants.MAILBOX_INBOX){//收件箱
                if (noticeMap.get(currentType).get(position).getUnread()) {
                    readNotice(noticeMap.get(currentType).get(position));
                    getService().getDataReportManager().reportEventBid(EventType.READ_NOTICE,noticeMap.get(currentType).get(position).getNoticeId()+"");
                }
                noticeMap.get(currentType).get(position).setUnread(false);
                Intent intent = new Intent(mContext, NoticeInboxInfoActivity.class);
                intent.putExtra("notice", noticeMap.get(currentType).get(position));
                startActivityForResult(intent, requestDetaliCode);
            }else if(currentType == Constants.MAILBOX_OUTBOX){//发件箱
                Intent intent = new Intent(mContext, NoticeOutboxInfoActivity.class);
                intent.putExtra("notice", noticeMap.get(currentType).get(position));
                startActivity(intent);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==requestDetaliCode){
            updataAdapter();
            swipListView.setSelection(currentPosition);
        }else if(requestCode==requestRelease){
            if(data!=null){
                int count = data.getIntExtra("scoreCount",0);
                if(count>0)
                    showContextMenuScore(count);
                //跳到通知发件箱列表
                rb_outbox.setChecked(true);
                rb_inbox.setChecked(false);
                currentType = Constants.MAILBOX_OUTBOX;
                getService().getNoticeManager().getLocalCachedNotice(currentType);
            }
        }
    }

    @Override
    public void getData() {
        initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.rb_inbox) {//收件箱
            currentType = Constants.MAILBOX_INBOX;
            getService().getNoticeManager().getLocalCachedNotice(currentType);
        } else if (v.getId() == R.id.rb_outbox) {//发件箱
            currentType = Constants.MAILBOX_OUTBOX;
            getService().getNoticeManager().getLocalCachedNotice(currentType);
        } else if (v.getId() == R.id.hey_icon) {
            Intent intent = new Intent(mContext, NoticeReleaseActivity.class);
            startActivityForResult(intent,requestRelease);
        }
    }

//    @Override
//    public void onclickRightNext() {
//        super.onclickRightNext();
//        //发通知
//        Intent intent = new Intent(mContext, NoticeReleaseActivity.class);
//        startActivityForResult(intent, requestRelease);
//    }

    @Override
    public void onclickRightImg() {
        super.onclickRightImg();
        if(currentType == Constants.MAILBOX_INBOX){
            showBtnDialog(new String[]{getString(R.string.btn_clear_inbox_notice), getString(R.string.btn_cancel)});
        }else if(currentType == Constants.MAILBOX_OUTBOX){
            showBtnDialog(new String[]{getString(R.string.btn_clear_outbox_notice), getString(R.string.btn_cancel)});

        }
    }

    @Override
    public void onclickSecondRightImg() {
        super.onclickSecondRightImg();
        //发通知
        Intent intent = new Intent(mContext, NoticeReleaseActivity.class);
        startActivityForResult(intent, requestRelease);
    }

    @Override
    public void onclickBtn1() {
        super.onclickBtn1();
        //清空收件箱
        if(currentType == Constants.MAILBOX_INBOX){
            if (noticeMap.get(Constants.MAILBOX_INBOX) != null && noticeMap.get(Constants.MAILBOX_INBOX).size() > 0) {
                showProgressDialog(mContext, "清除中...", false, null);
                getService().getNoticeManager().clearNotice(noticeMap.get(Constants.MAILBOX_INBOX).get(0).getNoticeId(),Constants.MAILBOX_INBOX);
            }
        }else if(currentType == Constants.MAILBOX_OUTBOX){
            if (noticeMap.get(Constants.MAILBOX_OUTBOX) != null && noticeMap.get(Constants.MAILBOX_OUTBOX).size() > 0) {
                showProgressDialog(mContext, "清除中...", false, null);
                getService().getNoticeManager().clearNotice(noticeMap.get(Constants.MAILBOX_OUTBOX).get(0).getNoticeId(),Constants.MAILBOX_OUTBOX);
            }
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
        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "notice");
        super.finish();
    }
}
