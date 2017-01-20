package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.Comment;

import java.util.List;


public class BaoDianInfoAdapter extends BaseAdapter {

    private Context mContext;
    private List<Comment> mList;

    public BaoDianInfoAdapter(Context context, List<Comment> list) {
        super();
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<Comment> listData){
        this.mList.clear();
        this.mList.addAll(listData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.baodian_info_list_item, null);
            viewHolder.vHeadIcon = (ImageView) convertView.findViewById(R.id.head_icon);
            viewHolder.vDiscussUserName = (TextView) convertView.findViewById(R.id.discuss_user_name);
            viewHolder.vJobName = (TextView) convertView.findViewById(R.id.job_name);
            viewHolder.vDiscussTime = (TextView) convertView.findViewById(R.id.discuss_time);
            viewHolder.vDiscussContent = (TextView) convertView.findViewById(R.id.discuss_content);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Comment comment = mList.get(position);
            ImageLoader.getInstance().displayImage(comment.getSenderAvatar() + SysConstants.Imgurlsuffix80, viewHolder.vHeadIcon, ImageUtils.DIO_BIG_SQUARE_AVATAR);
        viewHolder.vDiscussUserName.setText(comment.getSenderName());
        if(comment.getUserType() != null){
            String userType = comment.getUserType().name();
            if(!android.text.TextUtils.isEmpty(userType) && !userType.equals("TEACHER")){
                viewHolder.vJobName.setText(comment.getUserTitle().toString());
            }
        }
        viewHolder.vDiscussTime.setText(Utils.getDateTime(mContext, comment.getSendTime()));
        viewHolder.vDiscussContent.setText(comment.getContent());

        return convertView;
    }

    class ViewHolder {
        ImageView vHeadIcon;
        TextView vDiscussUserName;
        TextView vJobName;
        TextView vDiscussTime;
        TextView vDiscussContent;
    }
}
