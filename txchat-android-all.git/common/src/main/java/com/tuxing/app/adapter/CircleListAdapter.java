package com.tuxing.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.qzq.MessageUnreadDetailActivity;
import com.tuxing.app.qzq.MyCircleListActivity;
import com.tuxing.app.qzq.ParentCircleActivity;
import com.tuxing.app.qzq.adapter.MessageGridViewListAdapter;
import com.tuxing.app.qzq.util.ParentNoNullUtil;
import com.tuxing.app.qzq.util.TextUnderlineClickSpan;
import com.tuxing.app.qzq.util.TextViewSpannableString.LocalLinkMovementMethod;
import com.tuxing.app.qzq.view.MessageCollapsibleTextView;
import com.tuxing.app.ui.dialog.CommonDialog;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.util.DateTimeUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangst on 15-7-2.
 */
public class CircleListAdapter extends ArrayAdapter<Feed> {

    private Context mContext;
    private List<Feed> list;
    private String uri;
    private Feed selectedFeed;
    private  User currentUser;
    private PopupWindow popupWindow;
    private ParentCircleActivity activity;
    private CoreService service;
    private String muteGroupIds = "";//禁言群组ids

    public CircleListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CircleListAdapter(Context _context, List<Feed> objects, ParentCircleActivity activity, CoreService txservice) {
        super(_context, 0, objects);
        mContext = _context;
        this.list = objects;
        this.activity = activity;
        service = txservice;
        currentUser = service.getLoginManager().getCurrentUser();
    }

    public void setData(List<Feed> mlist) {
        list = mlist;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

            if (getCount() == 1 && list.get(0).getFeedId() == -1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.listview_no_data_item, parent, false);
            ImageView qzq_bg = (ImageView) view.findViewById(R.id.qzq_bg);
            qzq_bg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onclickRightImg();
                }
            });
            return view;
        }

        // TODO Auto-generated method stub
        Holder holder = null;
        if (!CollectionUtils.isEmpty(list) && list.get(position) != null) {
            final Feed feed = list.get(position);
            List<String> iconList = new ArrayList<String>();
            String json = feed.getAttachments();
            JSONArray array = null;
            int flag = -1;
            try {
                array = new JSONArray(json);
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        iconList.add(array.getJSONObject(i).optString("url"));
                        flag = array.getJSONObject(i).optInt("type");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder = new Holder();
            if (feed.getFeedType() != null && feed.getFeedType() == Constants.FEED_TYPE.ACTIVITY_FEED) {//活动类型
//        if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_list_item_activity, parent, false);
                holder.ctv = (MessageCollapsibleTextView) convertView.findViewById(R.id.ctv);
                holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.iv_portrait = (RoundImageView) convertView.findViewById(R.id.iv_portrait);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.more = (TextView) convertView.findViewById(R.id.tv_more);
                holder.view_line_leaveMsg = (View) convertView.findViewById(R.id.view_line_leaveMsg);
                holder.iv_friend_arrow = (ImageView) convertView.findViewById(R.id.iv_friend_arrow);
                holder.imageView = (MyImageView) convertView.findViewById(R.id.iv_myImageView);

                convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
                holder.iv_portrait.setImageUrl(feed.getUserAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
                holder.tv_name.setText(feed.getUserName() + "");
                holder.ctv.setDesc(feed.getContent(), null);
                if (TextUtils.isEmpty(feed.getContent())) {
                    holder.ctv.setVisibility(View.GONE);
                } else
                    holder.ctv.setVisibility(View.VISIBLE);
                if (!CollectionUtils.isEmpty(iconList)) {
                    holder.imageView.setImageUrl(iconList.get(0) + SysConstants.Imgurlsuffix320, R.drawable.defal_down_lym_proress, true);
                    holder.imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            WebSubUrlActivity.invoke(mContext, String.format(SysConstants.ACTIVITY_URL,
                                    feed.getActivityId()), mContext.getResources().getString(R.string.find_activity));
                        }
                    });
                }
                //删除
                holder.tv_delete.setTag(feed);
                holder.tv_delete.setVisibility(View.VISIBLE);
                holder.tv_delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        service.getFeedManager().deleteActivity(feed.getActivityId());
                    }
                });
            } else {
//        if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_list_item, parent, false);
                holder.ctv = (MessageCollapsibleTextView) convertView.findViewById(R.id.ctv);
                holder.gv_img = (MyGridView) convertView.findViewById(R.id.gv_img);
                holder.ll_pop = (LinearLayout) convertView.findViewById(R.id.ll_pop);
                holder.iv_more = (ImageView) convertView.findViewById(R.id.iv_more);
                holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_praiseName = (TextView) convertView.findViewById(R.id.tv_praiseName);
                holder.ll_leaveMsg = (LinearLayout) convertView.findViewById(R.id.ll_leaveMsg);
                holder.ll_piarse = (LinearLayout) convertView.findViewById(R.id.piarse_ll);
                holder.iv_portrait = (RoundImageView) convertView.findViewById(R.id.iv_portrait);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.more = (TextView) convertView.findViewById(R.id.tv_more);
                holder.view_line_leaveMsg = (View) convertView.findViewById(R.id.view_line_leaveMsg);
                holder.iv_friend_arrow = (ImageView) convertView.findViewById(R.id.iv_friend_arrow);
                holder.line_more_comment = (View) convertView.findViewById(R.id.line_more_comment);
                convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }

                holder.tv_name.setText(feed.getUserName() + "");
                holder.tv_name.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, MyCircleListActivity.class);
                        intent.putExtra("userId", feed.getUserId());
                        intent.putExtra("userName", feed.getUserName());
                        mContext.startActivity(intent);
                    }
                });
                holder.ctv.setDesc(feed.getContent(), null);
                if (TextUtils.isEmpty(feed.getContent())) {
                    holder.ctv.setVisibility(View.GONE);
                } else
                    holder.ctv.setVisibility(View.VISIBLE);
                holder.ctv.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });
                holder.iv_portrait.setImageUrl(feed.getUserAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
                holder.iv_portrait.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //详细页面
                        Intent intent = new Intent(mContext, MyCircleListActivity.class);
                        intent.putExtra("userId", feed.getUserId());
                        intent.putExtra("userName", feed.getUserName());
                        mContext.startActivity(intent);
                    }
                });
                holder.gv_img.setSelector(new ColorDrawable(android.R.color.transparent));

                if (iconList.size() > 0) {
                    holder.gv_img.setVisibility(View.VISIBLE);
                } else
                    holder.gv_img.setVisibility(View.GONE);
                if (iconList.size() == 4) {// 如果图片个数为4个，则排列方式为上下各两个
                    holder.gv_img.setNumColumns(2);
                    holder.gv_img.setVerticalSpacing(dip2px(4));
                    holder.gv_img.setHorizontalSpacing(dip2px(4));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dip2px(160), LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, dip2px(10), 0, 0);
                    holder.gv_img.setLayoutParams(lp);
                } else if (iconList.size() == 1) {
                    holder.gv_img.setNumColumns(1);
                    holder.gv_img.setVerticalSpacing(dip2px(0));
                    holder.gv_img.setHorizontalSpacing(dip2px(0));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, dip2px(10), 0, 0);
                    holder.gv_img.setLayoutParams(lp);
                } else {
                    holder.gv_img.setNumColumns(3);
                    holder.gv_img.setVerticalSpacing(dip2px(4));
                    holder.gv_img.setHorizontalSpacing(dip2px(4));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dip2px(240), LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, dip2px(10), 0, 0);
                    holder.gv_img.setLayoutParams(lp);
                }
                MessageGridViewListAdapter adapter = new MessageGridViewListAdapter(mContext, iconList, holder.gv_img, flag);
                holder.gv_img.setAdapter(adapter);
                //赞

                holder.ll_pop.setTag(feed);
                holder.ll_pop.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        Feed snspFeed = (Feed) v.getTag();

                        popupWindow = new PopupWindow(getPopupWindowView(snspFeed, position), dip2px(160), dip2px(40), false);
                        popupWindow.setFocusable(true);
                        popupWindow.setTouchable(true);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setAnimationStyle(R.style.msg_list_grid_animation);
                        popupWindow.setBackgroundDrawable(new ColorDrawable());
                        int[] location = new int[2];
                        v.getLocationInWindow(location);
                        //                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, dip2px(100), location[1] - dip2px(3)); // 设置layout在PopupWindow中显示的位置
                        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - popupWindow.getWidth() - 5, location[1] - popupWindow.getHeight() / 3);
                    }// - dip2px(10)
                });

                //获取点赞的人
                List<User> praiseList = new ArrayList<User>();
                if (feed.getLikes() != null && feed.getLikes().size() > 0) {
                    for (int i = 0; i < feed.getLikes().size(); i++) {
                        if (feed.getLikes().get(i).getCommentType() == 1) {
                            User user = new User();
                            user.setUserId(feed.getLikes().get(i).getSenderId());
                            user.setUsername(feed.getLikes().get(i).getSenderName());
                            user.setNickname(feed.getLikes().get(i).getSenderName());
                            user.setAvatar(feed.getLikes().get(i).getSenderAvatar());
                            praiseList.add(user);
                        }
                    }
                }
                //时间
                if (DateTimeUtils.getDateTime((Long) (feed.getPublishTime())) == 0) {
                    holder.tv_time.setText("刚刚");
                } else {
                    String value = DateTimeUtils.formatRelativeDate(DateTimeUtils.getDateTime((Long) (feed.getPublishTime())));
                    holder.tv_time.setText(value);
                }

                //删除
                holder.tv_delete.setTag(feed);
                if ((currentUser != null && feed.getUserId() == currentUser.getUserId()) ||
                        (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion()) &&
                                feed.getUserType() == Constants.USER_TYPE.PARENT)) {
                    //2.1加下删除家长feed的功能
                    holder.tv_delete.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_delete.setVisibility(View.INVISIBLE);
                }


                holder.tv_delete.setOnClickListener(delete_ClickListener);
                //评论
                holder.ll_leaveMsg.removeAllViews();
//            holder.ll_piarse.removeAllViews();
                if ((feed.getLikes() != null && feed.getLikes().size() > 0) &&
                        (feed.getComments() != null && feed.getComments().size() > 0)) {
                    holder.view_line_leaveMsg.setVisibility(View.VISIBLE);
                } else {
                    holder.view_line_leaveMsg.setVisibility(View.GONE);
                }

                if ((feed.getLikes() != null && feed.getLikes().size() > 0) ||
                        (feed.getComments() != null && feed.getComments().size() > 0)) {
                    holder.iv_friend_arrow.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_friend_arrow.setVisibility(View.GONE);
                }

                if (feed.getLikes() != null && feed.getLikes().size() > 0) {
                    holder.ll_piarse.setVisibility(View.VISIBLE);
                } else {
                    holder.ll_piarse.setVisibility(View.GONE);

                }

                if (feed.getComments() != null && feed.getComments().size() > 0) {
                    holder.ll_leaveMsg.setVisibility(View.VISIBLE);
                } else {
                    holder.ll_leaveMsg.setVisibility(View.GONE);
                }


                if (praiseList.size() > 0) {
                    holder.tv_praiseName.setText("");
                    for (int i = 0; i < praiseList.size(); i++) {
                        User user = praiseList.get(i);
                        SpannableString spStr = null;
                        if (praiseList.size() == 1 || i == praiseList.size() - 1) {
//                            spStr = new SpannableString(user.getNickname());
//                            spStr = new SpannableString(ParentNoNullUtil.getNickName(user));
                            if (ParentNoNullUtil.getNickName(user)==null||ParentNoNullUtil.getNickName(user).equals("null")||ParentNoNullUtil.getNickName(user).equals("")){
                                User currentuser = service.getLoginManager().getCurrentUser();
                                spStr = new SpannableString(ParentNoNullUtil.getNickName(currentuser));
                                user =currentuser;
                            }else{
                                spStr = new SpannableString(ParentNoNullUtil.getNickName(user));
                            }
                        } else {
//                            spStr = new SpannableString(user.getNickname() + ",");
//                            spStr = new SpannableString(ParentNoNullUtil.getNickName(user) + ",");
                            if (ParentNoNullUtil.getNickName(user)==null||ParentNoNullUtil.getNickName(user).equals("null")){
                                User currentuser = service.getLoginManager().getCurrentUser();
                                spStr = new SpannableString(ParentNoNullUtil.getNickName(currentuser) + ",");
                                user =currentuser;
                            }else{
                                spStr = new SpannableString(ParentNoNullUtil.getNickName(user) + ",");
                            }
                        }
                        TextUnderlineClickSpan clickSpan = new TextUnderlineClickSpan(user, mContext);
//                        spStr.setSpan(clickSpan, 0, user.getNickname().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spStr.setSpan(clickSpan, 0, ParentNoNullUtil.getNickName(user).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        holder.tv_praiseName.append(spStr);
                        holder.tv_praiseName.setMovementMethod(LocalLinkMovementMethod.getInstance());
                    }

                }

                final List<Comment> comments = feed.getComments();
                if (comments != null && comments.size() > 0) {
                    for (int i = 0; i < comments.size(); i++) {
                        View commentView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_comment_item, null);
                        RelativeLayout commentll = (RelativeLayout) commentView.findViewById(R.id.comment_rl);
                        TextView name = (TextView) commentView.findViewById(R.id.comment_name);
                        TextView content = (TextView) commentView.findViewById(R.id.comment_content);
                        TextView nextContent = (TextView) commentView.findViewById(R.id.comment_next_content);
                        TextView huifu = (TextView) commentView.findViewById(R.id.comment_huifu);
                        TextView huifuName = (TextView) commentView.findViewById(R.id.comment_huifu_name);
                        ImageView comentIcon = (ImageView) commentView.findViewById(R.id.paraise_icon);

                        name.setTag(i);
                        huifuName.setTag(i);
                        content.setTag(i);
                        nextContent.setTag(i);
                        if (TextUtils.isEmpty(comments.get(i).getReplayToUserName())) {
                            if (comments.get(i).getContent().length() > 12) {
//            			nextContent.setText(comments.get(i).getContent());
                                Spannable span = SmileUtils.getSmiledText(mContext, comments.get(i).getContent());
                                nextContent.setText(span, TextView.BufferType.SPANNABLE);
                                content.setVisibility(View.GONE);
                                nextContent.setVisibility(View.VISIBLE);
                            } else {
//            			content.setText(comments.get(i).getContent());
                                Spannable span = SmileUtils.getSmiledText(mContext, comments.get(i).getContent());
                                content.setText(span, TextView.BufferType.SPANNABLE);
                                content.setVisibility(View.VISIBLE);
                                nextContent.setVisibility(View.GONE);
                            }
                            if (comments.get(i).getSenderName()==null||comments.get(i).getSenderName().equals("null")||comments.get(i).getSenderName().equals("")){
                                User currentuser = service.getLoginManager().getCurrentUser();
                                name.setText(ParentNoNullUtil.getNickName(currentuser) + ":");
                            }else{
                                name.setText(comments.get(i).getSenderName() + ":");
                            }
                            huifu.setVisibility(View.GONE);
                            huifuName.setVisibility(View.GONE);
                        } else {
                            huifu.setVisibility(View.VISIBLE);
                            huifuName.setVisibility(View.VISIBLE);
//                            name.setText(comments.get(i).getSenderName());
                            if (comments.get(i).getSenderName()==null||comments.get(i).getSenderName().equals("null")||comments.get(i).getSenderName().equals("")){
                                User currentuser = service.getLoginManager().getCurrentUser();
                                name.setText(ParentNoNullUtil.getNickName(currentuser) + ":");
                            }else{
                                name.setText(comments.get(i).getSenderName() + ":");
                            }
                            huifuName.setText(comments.get(i).getReplayToUserName() + ":");
                            if (comments.get(i).getContent().length() > 6) {
                                Spannable span = SmileUtils.getSmiledText(mContext, comments.get(i).getContent());
                                nextContent.setText(span, TextView.BufferType.SPANNABLE);
                                content.setVisibility(View.GONE);
                                nextContent.setVisibility(View.VISIBLE);
                            } else {
                                content.setVisibility(View.VISIBLE);
                                Spannable span = SmileUtils.getSmiledText(mContext, comments.get(i).getContent());
                                content.setText(span, TextView.BufferType.SPANNABLE);
                                nextContent.setVisibility(View.GONE);
                            }
                        }
                        if (i == 0) {
                            comentIcon.setVisibility(View.VISIBLE);
                        } else {
                            comentIcon.setVisibility(View.INVISIBLE);
                        }


                        name.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int index = (Integer) v.getTag();
                                Intent intent = new Intent(mContext, MyCircleListActivity.class);
                                intent.putExtra("userId", comments.get(index).getSenderId());
                                intent.putExtra("userName", comments.get(index).getSenderName());
                                mContext.startActivity(intent);
                            }
                        });
                        huifuName.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int index = (Integer) v.getTag();
                                Intent intent = new Intent(mContext, MyCircleListActivity.class);
                                intent.putExtra("userId", comments.get(index).getReplayToUserId());
                                intent.putExtra("userName", comments.get(index).getReplayToUserName());
                                mContext.startActivity(intent);
                            }
                        });
                        content.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = (Integer) v.getTag();
                                if (currentUser != null) {
                                    if (comments.get(index).getSenderId() != currentUser.getUserId()) {
                                        activity.replyComment(feed, SysConstants.commentType_2, "", comments.get(index).getSenderId(), comments.get(index).getSenderName(), comments.get(index), comments);
                                    }
                                }
                            }
                        });
                        content.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                int index = (Integer) v.getTag();
                                User user = CoreService.getInstance().getUserManager().getUserInfo(comments.get(index).getSenderId());
                                if ((comments.get(index).getSenderId() == currentUser.getUserId()) ||
                                        (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion()) &&
                                                user.getType() == Constants.USER_TYPE.PARENT)) {
                                    ///
                                    showContextMenu(feed, comments.get(index));
                                }
                                return true;
                            }
                        });
                        nextContent.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int index = (Integer) v.getTag();
                                if (comments.get(index).getSenderId() != currentUser.getUserId()) {
                                    activity.replyComment(feed, SysConstants.commentType_2, "", comments.get(index).getSenderId(), comments.get(index).getSenderName(), comments.get(index), comments);
                                }
                            }
                        });
                        nextContent.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                int index = (Integer) v.getTag();
                                User user = CoreService.getInstance().getUserManager().getUserInfo(comments.get(index).getSenderId());
                                if ((comments.get(index).getSenderId() == currentUser.getUserId()) ||
                                        (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion()) &&
                                                user.getType() == Constants.USER_TYPE.PARENT)) {
                                    showContextMenu(feed, comments.get(index));
                                }
                                return true;
                            }
                        });
                        holder.ll_leaveMsg.addView(commentView);
                    }
                    if (feed.getComments().size() > 19) {
                        holder.more.setVisibility(View.VISIBLE);
                        holder.line_more_comment.setVisibility(View.VISIBLE);
                    } else {
                        holder.more.setVisibility(View.GONE);
                        holder.line_more_comment.setVisibility(View.GONE);
                    }

                }
                holder.more.setTag(feed);
                holder.more.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Feed feed = (Feed) v.getTag();
                        Intent intent = new Intent(mContext, MessageUnreadDetailActivity.class);
                        intent.putExtra("feed", feed);
                        mContext.startActivity(intent);

                    }
                });
            }
        }
        return convertView;
    }

    public OnClickListener delete_ClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            selectedFeed = (Feed) v.getTag();
            showContextMenu(selectedFeed);
            MobclickAgent.onEvent(mContext, "qzq_del", UmengData.qzq_del);
        }
    };


    class Holder {
        public MessageCollapsibleTextView ctv;
        public MyGridView gv_img; // 如果是多张图片，用gridview展示
        public LinearLayout ll_pop;
        public ImageView iv_more; // 就是那个有两个点点的图片
        public TextView tv_delete; // 删除
        public TextView tv_time; // 距离现在的发表时间
        public TextView tv_praiseName; // 点赞的人
        public LinearLayout ll_leaveMsg; // 留言信息
        public LinearLayout ll_piarse; //赞
        public RoundImageView iv_portrait; // 发消息人头像
        public TextView tv_name; // 发消息人
        public TextView more; // 发消息人
        public ImageView iv_friend_arrow;
        public View view_line_leaveMsg;
        public View line_more_comment;
        public MyImageView imageView;
    }

    public View getPopupWindowView(final Feed sFeed, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_list_grid_item_popup, null);
        final CheckBox cb_praise = (CheckBox) view.findViewById(R.id.cb_praise);
        CheckBox cb_comment = (CheckBox) view.findViewById(R.id.cb_comment);

        for (Comment comment : sFeed.getLikes()) {
            if (currentUser != null && comment.getSenderId() == currentUser.getUserId()) {
                cb_praise.setChecked(true);
                cb_praise.setText("取消");
                break;
            }
        }

        cb_praise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                MobclickAgent.onEvent(mContext, "qzq_praise", UmengData.qzq_praise);
                if (isChecked) {
                    if (currentUser != null) {
                        activity.selectedFeed = sFeed;
                        service.getFeedManager().publishComment(sFeed.getFeedId(), "",
                                currentUser.getUserId(), currentUser.getUsername(), 1); // 攒
                    }
                } else {
                    Comment selectedComment = null;
                    for (Comment comment : sFeed.getLikes()) {
                        if (currentUser != null && comment.getSenderId() == currentUser.getUserId()) {
                            selectedComment = comment;
                            break;
                        }
                    }

                    if (selectedComment != null) {
                        activity.selectedFeed = sFeed;
                        activity.deletedComment = selectedComment;
                        service.getFeedManager().deleteFeedComment(selectedComment.getCommentId()); // 攒
                    }
                /*for(Comment comment:selectedFeed.getLikes()){
                    if(comment.getSenderId()==currentUser.getUserId()){
                        selectedFeed.getLikes().remove(comment);
                        break;
                    }
                }*/
                }
                if (activity.ll_send.getVisibility() == View.VISIBLE) {
                    activity.hideKeyboard();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                //notifyDataSetChanged();
                activity.showProgressDialog(mContext, "", true, null);
                popupWindow.dismiss();
            }
        });

        cb_comment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.replyComment(sFeed, SysConstants.commentType_2, "", -1, null, new Comment(), list.get(position).getComments());
                MobclickAgent.onEvent(mContext, "qzq_comment", UmengData.qzq_comment);
                popupWindow.dismiss();
            }
        });
        return view;
    }

    //    // 获取点赞的人
    public String getPraiseName(List<User> simpleUsers, CheckBox cb) {
        cb.setText("");
        for (int i = 0; i < simpleUsers.size(); i++) {
            User user = simpleUsers.get(i);
            SpannableString spStr = null;
            if (simpleUsers.size() == 1 || i == simpleUsers.size() - 1) {
//                spStr = new SpannableString(user.getNickname());
//                spStr = new SpannableString(ParentNoNullUtil.getNickName(user));
                if (ParentNoNullUtil.getNickName(user)==null||ParentNoNullUtil.getNickName(user).equals("null")||ParentNoNullUtil.getNickName(user).equals("")){
                    User currentuser = service.getLoginManager().getCurrentUser();
                    spStr = new SpannableString(ParentNoNullUtil.getNickName(currentuser));
                    user =currentuser;
                }else{
                    spStr = new SpannableString(ParentNoNullUtil.getNickName(user));
                }
            } else {
//                spStr = new SpannableString(user.getNickname() + ",");
//                spStr = new SpannableString(ParentNoNullUtil.getNickName(user) + ",");
                if (ParentNoNullUtil.getNickName(user)==null||ParentNoNullUtil.getNickName(user).equals("null")||ParentNoNullUtil.getNickName(user).equals("")){
                    User currentuser = service.getLoginManager().getCurrentUser();
                    spStr = new SpannableString(ParentNoNullUtil.getNickName(currentuser) + ",");
                    user =currentuser;
                }else{
                    spStr = new SpannableString(ParentNoNullUtil.getNickName(user) + ",");
                }
            }
            TextUnderlineClickSpan clickSpan = new TextUnderlineClickSpan(user, mContext);
//            spStr.setSpan(clickSpan, 0, user.getNickname().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spStr.setSpan(clickSpan, 0, ParentNoNullUtil.getNickName(user).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            cb.append(spStr);
            cb.setMovementMethod(LocalLinkMovementMethod.getInstance());
        }
        return null;
    }

    private void showToast(String value) {
        Toast toast = Toast.makeText(mContext, value, Toast.LENGTH_SHORT);
        toast.show();
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void showContextMenu(final Feed feed) {
        final CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(mContext);
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                service.getFeedManager().deleteFeed(feed.getFeedId());
                dialog.dismiss();
            }
        });
        dialog.setMessage("确认删除吗?");
        dialog.show();
    }

    public void showContextMenu(final Feed feed, final Comment comment) {
        final CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(mContext);
//        dialog.setTitle("选择操作");
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MobclickAgent.onEvent(mContext, UmengData.qzq_del_comment);
                activity.selectedFeed = feed;
                activity.deletedComment = comment;
                service.getFeedManager().deleteFeedComment(comment.getCommentId()); //删除评论
                dialog.dismiss();
            }
        });
        dialog.setMessage("确认删除吗?");
        dialog.show();
    }


    public void showTextandSmile(Context context, TextView tv, String msg) {
        Spannable span = SmileUtils.getSmiledText(context, msg);
        // 设置内容
        tv.setText(span, TextView.BufferType.SPANNABLE);
    }
}
