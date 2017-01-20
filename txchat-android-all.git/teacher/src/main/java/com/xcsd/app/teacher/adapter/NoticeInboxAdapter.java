package com.xcsd.app.teacher.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * 刷卡列表
 */
public class NoticeInboxAdapter extends ArrayAdapter<Notice> {

    private Context mContext;
    private List<Notice> notices;
    private int noticeType;

    public NoticeInboxAdapter(Context context, List<Notice> notices, int type) {
        super(context, 0, notices);
        mContext = context;
        this.notices = notices;
        this.noticeType = type;
    }

    public void setData(List<Notice> mNotices, int type) {
        this.notices = mNotices;
        this.noticeType = type;
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_notice_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.tvSendTimeL = (TextView) convertView.findViewById(R.id.tvSendtime);
            viewHolder.wivHeadL = (RoundImageView) convertView.findViewById(R.id.ivUserhead);
            viewHolder.tvContentL = (TextView) convertView.findViewById(R.id.tvChatcontent);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.isNew = (ImageView) convertView.findViewById(R.id.tvHintNum);
            viewHolder.llNameContent = (LinearLayout) convertView.findViewById(R.id.llNameContent);
            viewHolder.line = convertView.findViewById(R.id.item_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!CollectionUtils.isEmpty(notices) && position < notices.size()) {
            final Notice notice = notices.get(position);

            if (notice != null) {
                viewHolder.tvName.setText(notice.getSenderName());

                if (notice.getSendTime() != null) {
                    viewHolder.tvSendTimeL.setText(Utils.getDateTime(mContext, notice.getSendTime()));
                }

                String json = notice.getAttachments();
                if (!notice.getContent().equals("") && notice.getContent().length() > 0) {
                    viewHolder.tvContentL.setText(notice.getContent());
                } else {
                    try {
                        JSONArray array = new JSONArray(json);
                        if (array.length() > 0) {
                            viewHolder.tvContentL.setText("[图片]");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                User sender = CoreService.getInstance().getUserManager().getUserInfo(notice.getSenderUserId());
                if (sender != null) {
                    viewHolder.wivHeadL.setImageUrl(sender.getAvatar()+ SysConstants.Imgurlsuffix90, R.drawable.default_avatar);
                }
                if (noticeType == Constants.MAILBOX_OUTBOX) {
                    viewHolder.isNew.setVisibility(View.GONE);
                } else {
                    if (notice.getUnread() != null && notice.getUnread())
                        viewHolder.isNew.setVisibility(View.VISIBLE);
                    else
                        viewHolder.isNew.setVisibility(View.GONE);
                }

                if (position + 1 == notices.size()) {
                    viewHolder.line.setVisibility(View.GONE);
                } else {
                    viewHolder.line.setVisibility(View.VISIBLE);
                }
            }
        }

        return convertView;
    }

    class ViewHolder {
        public TextView tvSendTimeL;
        public RoundImageView wivHeadL;
        public TextView tvContentL;
        public ImageView ivState;
        public TextView tvName;
        public ImageView isNew;
        public LinearLayout llNameContent;
        View line;
    }


}
