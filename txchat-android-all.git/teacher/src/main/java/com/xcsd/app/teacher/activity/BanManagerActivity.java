package com.xcsd.app.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.tea.ChooseUserActivity;
import com.xcsd.app.teacher.tea.NoticeReleaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.MuteEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangst on 15-7-26.
 */
public class BanManagerActivity extends BaseActivity {

    private final int CHOOSEUSER_REQUESTCODE = 0x100;
    private ScrollView svContact;
    private MyGridView gv_ban;
    private boolean deleteFlag;
    private GroupAdapter adapter;
    private LinearLayout other;
    //禁言用户集合
    public List<User> muteUsers = new ArrayList<User>();
    // 解除禁言的用户id
    private List<Long> reduceList = new ArrayList<Long>();
    // 禁言用户的id
    private List<Long> plusList = new ArrayList<Long>();
    private Long departmentId;
    private Map<Long, User> selectMap = new HashMap<Long, User>();
    private int flag = 0;//初始状态 1 删除
    private RadioGroup rg;
    private RadioButton rb_chat;
    private RadioButton rb_circle;
    private int currentType = Constants.MUTE_TYPE.MUTE_CHAT;
    private TextView tv_ban_hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.ban_manager_layout);
        setTitle("禁言设置");
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        departmentId = getIntent().getLongExtra("departmentId", -1);
        initView();
        //获取禁言列表
        getMuteUsers();
        MobclickAgent.onEvent(mContext, "banned", UmengData.banned);
    }

    private void getMuteUsers() {
        showProgressDialog(mContext, "", true, null);
        getService().getDepartmentManager().getMutedUserList(departmentId, currentType);
    }

    private void initView() {
        svContact = (ScrollView) findViewById(R.id.svContact);
        gv_ban = (MyGridView) findViewById(R.id.gv_ban);
        other = (LinearLayout) findViewById(R.id.other);
        rb_chat = (RadioButton)findViewById(R.id.rb_chat);
        rb_circle = (RadioButton)findViewById(R.id.rb_circle);
        rb_chat.setOnClickListener(this);
        rb_circle.setOnClickListener(this);
        tv_ban_hint = (TextView)findViewById(R.id.tv_ban_hint);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.rb_chat) {//聊天
            tv_ban_hint.setText(getResources().getString(R.string.hint_ban_chat));
            currentType = Constants.MUTE_TYPE.MUTE_CHAT;
            getMuteUsers();
        } else if (v.getId() == R.id.rb_circle) {//亲子圈
            tv_ban_hint.setText(getResources().getString(R.string.hint_ban_circle));
            currentType = Constants.MUTE_TYPE.MUTE_FEED;
            getMuteUsers();
        }
    }

    void fillData() {
        deleteFlag = false;
        adapter = new GroupAdapter(mContext);
        gv_ban.setAdapter(adapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                deleteFlag();
            }
        },500);
        gv_ban.setOnTouchInvalidPositionListener(new MyGridView.OnTouchInvalidPositionListener() {

            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                deleteFlag();
                return false;
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFlag();
            }
        });
        gv_ban.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int userSize = muteUsers.size();
                if (position + 1 > userSize) {
                    switch (position - userSize) {
                        case 0:
                            if (!deleteFlag) {
                                Intent intent = new Intent(mContext,
                                        ChooseUserActivity.class);
                                intent.putExtra("departmentId", departmentId);
                                intent.putExtra("muteUsers", (Serializable) muteUsers);//已被禁言的
                                intent.putExtra("muteType", currentType);//已被禁言的
                                startActivityForResult(intent, CHOOSEUSER_REQUESTCODE);
                                deleteFlag();
                                MobclickAgent.onEvent(mContext, "banned_operate", UmengData.banned_operate);
                                break;
                            } else {
                                // 使加号消失
                                adapter.addMemoryList(muteUsers);
                                setFlag();
                                break;
                            }

                        case 1:
                            // 使加号消失
                            adapter.deletePlus(muteUsers);
                            setFlag();
                            break;
                        default:
                            break;
                    }
                } else {
                    if (deleteFlag) {
                        // 添加要解禁用户的id
                        showProgressDialog(mContext, "", true, null);
                        Long userId = muteUsers.get(position).getUserId();
                        getService().getDepartmentManager().removeUsersFromMutedList(departmentId,
                                userId, currentType);
                        plusList.remove(userId);
                    }
                }
            }
        });
    }

    public void onEventMainThread(MuteEvent event) {
        disProgressDialog();
        if (isActivity) {
            //todo
//            event.getMuteType()
            switch (event.getEvent()) {
                case FETCH_MUTED_LIST_SUCCESS:
                    muteUsers.clear();
                    muteUsers = event.getUserList();
                    fillData();
                    if(!CollectionUtils.isEmpty(muteUsers)){
                        adapter.addMemoryList(muteUsers);
                    }else{
                        adapter.initMemoryList(muteUsers);
                    }
                    break;
                case FETCH_MUTED_LIST_FAILED:
                    break;
                case REMOVE_FROM_MUTED_LIST_SUCCESS:
                    sendTouChuan(SysConstants.TOUCHUAN_GAGUSER);
                    flag = 1;
                    showToast("修改成功");
                    getMuteUsers();
                    break;
                case REMOVE_FROM_MUTED_LIST_FAILED:
                    showToast(event.getMsg());
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CHOOSEUSER_REQUESTCODE:
                    getMuteUsers();
                    break;
            }
        }
    }
    private void setFlag() {
        deleteFlag = deleteFlag ? false : true;
        int childCount = gv_ban.getChildCount();
        for (int i = 0; i < childCount - 2; i++) {
            View childAt = gv_ban.getChildAt(i);
            ImageView delete = (ImageView) childAt
                    .findViewById(R.id.iv_unbanded);
            delete.setVisibility(deleteFlag ? View.VISIBLE : View.GONE);
        }
    }

    private void deleteFlag() {
        int childCount = gv_ban.getChildCount();
        if (deleteFlag)
            for (int i = 0; i < childCount - 2; i++) {
                View childAt = gv_ban.getChildAt(i);
                ImageView delete = (ImageView) childAt
                        .findViewById(R.id.iv_unbanded);
                delete.setVisibility(View.GONE);
            }
        deleteFlag = false;
        adapter.addMemoryList(muteUsers);
    }


    class GroupAdapter extends BaseAdapter {

        List<User> memoryList = new ArrayList<User>();
        List<Bitmap> imageList = new ArrayList<Bitmap>();
        private Bitmap addButton;
        private Bitmap banned;
        private Bitmap unbannded;
        private boolean showDeleteFlag;

        public GroupAdapter(Context context) {
            banned = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.btn_add);
            unbannded = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.btn_delete);
            imageList.add(banned);
            imageList.add(unbannded);
        }

        public int getImageListSize() {
            return imageList.size();
        }

        public int getUserListSize() {
            return memoryList.size();
        }

        // 添加数据
        public void addMemoryList(List<User> memories) {
            memoryList.clear();
            memoryList.addAll(memories);
            imageList.clear();
            imageList.add(banned);
            imageList.add(unbannded);
            this.notifyDataSetChanged();
        }

        public void initMemoryList(List<User> memories) {
            memoryList.clear();
            imageList.clear();
            imageList.add(banned);
            this.notifyDataSetChanged();
        }

        public void deletePlus(List<User> memories) {
            imageList.clear();
            imageList.add(unbannded);
            memoryList.clear();
            memoryList.addAll(memories);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return memoryList.size() + imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHodler hodler;
            hodler = new ViewHodler();
            convertView = View.inflate(mContext, R.layout.group_manager_item,
                    null);
            hodler.head = (RoundImageView) convertView.findViewById(R.id.head);
            hodler.name = (TextView) convertView.findViewById(R.id.name);
            hodler.iv_unbanded = (ImageView) convertView
                    .findViewById(R.id.iv_unbanded);
            convertView.setTag(hodler);
            int count = getCount();
            // 填充被禁言的用户
            if (position <= memoryList.size() - 1 && memoryList.size() > 0) {
                User memory = memoryList.get(position);
                // 禁言的用户大于0 是才能进入
                hodler.name.setText(memoryList.get(position).getNickname());
                hodler.iv_unbanded.setVisibility(deleteFlag ? View.VISIBLE
                        : View.GONE);
                String url = memory.getAvatar();
                hodler.head.setTag(url);
                hodler.head.setImageUrl(url + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            } else {
                if (position == memoryList.size() + imageList.size() - 1
                        && memoryList.size() == 0 && deleteFlag == false) {
                    hodler.head.setImageBitmap(imageList.get(position
                            - memoryList.size()));
                    hodler.name.setText(" ");
                    hodler.iv_unbanded.setVisibility(View.GONE);
                    hodler.head.setVisibility(View.GONE);
                }
                hodler.head.setImageBitmap(imageList.get(position
                        - memoryList.size()));
                hodler.name.setText(" ");
                hodler.iv_unbanded.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class ViewHodler {
        ImageView iv_unbanded;
        RoundImageView head;
        TextView name;
    }
}
