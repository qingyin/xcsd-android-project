package com.tuxing.app.qzq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import me.maxwin.view.XListView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class MessageUnreadListActivity extends BaseActivity implements XListView.IXListViewListener,AdapterView.OnItemClickListener{

    private int mFlag = -1; // 0表示正常下载，1表示下拉刷新，2表示加载更多,3表示评论，4表示回复
    private XListView xListView;
    private MessageListAdapter adapter;
//    private LinearLayout rootView;
    List<Comment> list = new ArrayList<Comment>();//列表
    UpdateReceiver receiver;
//    private TextView xlistview_footer_hint_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        rootView = (LinearLayout) getLayoutInflater().inflate(R.layout.message_unread_circle, null);
        setContentLayout(R.layout.message_unread_circle);
        setTitle("消息列表");
        setRightNext(true, "", 0);
        setLeftBack("", true, false);
        initView();
        getData();
    }

    public void getData() {
        list.clear();
        getnetlData();
        mFlag = 0;
        adapter = new MessageListAdapter(mContext, list);
        xListView.setAdapter(adapter);
        xListView.setPullRefreshEnable(false);
        xListView.setPullLoadEnable(false);
        receiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
        mContext.registerReceiver(receiver, intentFilter);
    }
    public void initView() {
        xListView = (XListView)findViewById(R.id.xListView);
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);

//        xlistview_footer_hint_textview = (TextView)findViewById(R.id.xlistview_footer_hint_textview);
    }

    //从net获取最近20条
    private  void getnetlData(){
    	showProgressDialog(mContext, "", true, null);
        getService().getFeedManager().getLatestMyConcernedComment();
    }

    //从net获取之前20条
    private  void getnetlHistoryData(){
        if(list.size()>0){
            getService().getFeedManager().getHistoryMyConcernedComment(list.get(list.size()-1).getCommentId());
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    @Override
    public void onclickRightImg() {
        super.onclickRightImg();
        if(checkFeedMute()){
        }else{
            //发布新内容
            Intent intent = new Intent(mContext, CircleReleaseActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onRefresh() {
        mFlag = 1;
        //getnetlData();
    }

    @Override
    public void onLoadMore() {
//        xlistview_footer_hint_textview.setVisibility(View.GONE);
        mFlag = 2;
        getnetlHistoryData();
    }

    /**
     * 服务器返回
     * @param event
     */
    public void onEventMainThread(CommentEvent event){
    	if(isActivity){
    	disProgressDialog();
        switch (event.getEvent()){
            case GET_LATEST_CONCERNED_COMMENT_SUCCESS:
                if(!CollectionUtils.isEmpty(event.getComments())){
                    list.clear();
                    list.addAll(event.getComments());
                    adapter.setData(list);
                    adapter.notifyDataSetChanged();
                    if(!event.isHasMore()){
                        xListView.setPullLoadEnable(false);
                    }else
                        xListView.setPullLoadEnable(true);
                    getService().getCounterManager().resetCounter(Constants.COUNTER.COMMENT);
                }

                break;
            case GET_LATEST_CONCERNED_COMMENT_FAILED:
                showToast(event.getMsg());
                break;

            case GET_HISTORY_CONCERNED_COMMENT_SUCCESS:
                if(!CollectionUtils.isEmpty(event.getComments())){
                    list.addAll(event.getComments());
                    adapter.setData(list);
                    adapter.notifyDataSetChanged();
                }
                xListView.stopLoadMore();
                if(!event.isHasMore()){
                    xListView.setPullLoadEnable(false);
                }else
                    xListView.setPullLoadEnable(true);
                break;
            case GET_HISTORY_CONCERNED_COMMENT_FAILED:
                showToast(event.getMsg());
                xListView.stopLoadMore();
                break;
            default:
                break;
        }
    }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        if(receiver != null){
            mContext.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(!CollectionUtils.isEmpty(list)&&position>=1){
            Intent intent = new Intent();
            intent.setClass(mContext,MessageUnreadDetailActivity.class);
            intent.putExtra("feed", list.get(position - 1).getFeed());
            startActivity(intent);
        }
    }

    class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mFlag = 1;
            Intent dataIntent = new Intent(MessageUnreadListActivity.this,MyCircleListActivity.class);
            MessageUnreadListActivity.this.setResult(RESULT_OK, dataIntent);
//            getnetlData();
            getService().getFeedManager().getLatestMyConcernedComment();
        }
    }

    class MessageListAdapter extends BaseAdapter{

        private List<Comment> list;
        private String uri;
        public MessageListAdapter(Context context,List<Comment> object) {
            list = object;
        }

       private void  setData(List<Comment> _list){
           this.list = _list;
           notifyDataSetChanged();
       }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Comment item;
            String content = "";
            long time = 0;
            String praiseName = "";
            String name = "";
            String iv_portrait = "";
            String attachmentFileKey = "";
            String feedContent = "";
            Holder holder = null;
            int flag = -1;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_unreadmsg_list_item, parent, false);
                holder.ctv = (TextView) convertView.findViewById(R.id.ctv);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.cb_praiseName = (CheckBox) convertView.findViewById(R.id.cb_praiseName);
                holder.iv_portrait = (RoundImageView) convertView.findViewById(R.id.iv_portrait);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_pic_unread = (MyImageView) convertView.findViewById(R.id.iv_pic_unread);
                holder.ll_unread_layout = (LinearLayout)convertView.findViewById(R.id.ll_unread_layout);
                holder.tv_content_right = (TextView)convertView.findViewById(R.id.tv_content_right);
                holder.status_btn = (LinearLayout)convertView.findViewById(R.id.ll_status_btn);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            item = list.get(position);
            holder.ll_unread_layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(mContext, MessageUnreadDetailActivity.class);
                    intent.putExtra("feed", list.get(position).getFeed());
                    startActivity(intent);
                }
            });

            name = item.getSenderName();
            iv_portrait = item.getSenderAvatar();
            if(item.getFeed().getAttachments().length()>5){
                List<String> iconList = new ArrayList<String>();

                String json = item.getFeed().getAttachments();
                JSONArray array = null;
                try {
                    array = new JSONArray(json);
                    if(array.length() > 0){
//                            for(int i = 0; i < array.length(); i++){
//                                iconList.add(array.getJSONObject(i).optString("url"));
//                            }
                        attachmentFileKey = array.getJSONObject(0).optString("url");
                        flag = array.getJSONObject(0).optInt("type");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            time =  item.getSendTime();
            feedContent = item.getFeed().getContent();
            if(item.getCommentType()== Constants.COMMENT_TYPE.REPLY){
                content = item.getContent();
                holder.cb_praiseName.setVisibility(View.GONE);
                holder.ctv.setVisibility(View.VISIBLE);
            }else if(item.getCommentType()== Constants.COMMENT_TYPE.LIKE){
//                praiseName  = item.getSenderName();
                content = "";
                holder.cb_praiseName.setVisibility(View.VISIBLE);
                holder.ctv.setVisibility(View.GONE);
            }
            if(!"".equalsIgnoreCase(attachmentFileKey)){
                holder.tv_content_right.setText("");
                holder.tv_content_right.setVisibility(View.GONE);
                holder.iv_pic_unread.setVisibility(View.VISIBLE);
                if(flag != 3){
                    holder.status_btn.setVisibility(View.GONE);
                    holder.iv_pic_unread.setImageUrl(attachmentFileKey+SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
                }else{
                    holder.status_btn.setVisibility(View.VISIBLE);
                    holder.iv_pic_unread.setImageUrl(attachmentFileKey+SysConstants.VIDEOSUFFIX80, R.drawable.defal_down_proress,false);

                }
            }else{
                holder.status_btn.setVisibility(View.GONE);
                holder.tv_content_right.setVisibility(View.VISIBLE);
//                holder.tv_content_right.setText(decodeUtf8(feedContent));
                holder.tv_content_right.setText(SmileUtils.getSmiledText(mContext,
                        feedContent), TextView.BufferType.SPANNABLE);
                holder.iv_pic_unread.setVisibility(View.GONE);
            }
            String value = Utils.getDateTime(mContext,item.getSendTime());
            holder.tv_time.setText(value);
            if(TextUtils.isEmpty(content)){
                holder.ctv.setVisibility(View.GONE);
            }else{
                holder.ctv.setVisibility(View.VISIBLE);
                holder.ctv.setText(SmileUtils.getSmiledText(mContext,
                        content), TextView.BufferType.SPANNABLE);
            }
            holder.cb_praiseName.setText(praiseName);
            holder.iv_portrait.setImageUrl(iv_portrait + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            holder.tv_name.setText(name);
            return convertView;
        }
        // 获取点赞的人
        public String getPraiseName(List<User> simpleUsers) {
            StringBuilder sb = new StringBuilder();
            for (User user : simpleUsers) {
                sb.append(user.getNickname() + ",");
            }
            String nick_name = sb.toString();
            if (TextUtils.isEmpty(nick_name)) {
                return "";
            } else {
                nick_name = nick_name.substring(0, nick_name.length() - 1);
                return nick_name;
            }
        }
        class Holder {
            public LinearLayout ll_unread_layout;
            public TextView ctv;
            public TextView tv_time; // 距离现在的发表时间
            public CheckBox cb_praiseName; // 点赞的人
            public RoundImageView iv_portrait;
            public TextView tv_name;
            public MyImageView iv_pic_unread;
            public TextView tv_content_right;
            public LinearLayout status_btn;
        }
        public String encode(String str) {
            String value = "";
            try {
                value = new String(str.getBytes(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return value;
        }

        public String decodeUtf8(String str) {
            String value = "";
            try {
                value = new String(URLDecoder.decode(str, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return value;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}

