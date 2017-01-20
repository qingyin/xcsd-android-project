package com.tuxing.app.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.Resource;

import java.util.List;


public class EducInfoAdapter extends BaseAdapter {

    private Context mContext;
    private List<Resource> datas;
    private long playId;

    public EducInfoAdapter(Context context, List<Resource> mDatas, long playId) {
        super();
        this.mContext = context;
        this.datas = mDatas;
        this.playId = playId;
    }

    public void setData(long playId){
       this.playId = playId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.educ_info_list_item, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.educ_info_icon);
            viewHolder.play_icon = (ImageView) convertView.findViewById(R.id.educ_video_info_play_icon);
            viewHolder.time = (TextView) convertView.findViewById(R.id.educ_info_time);
            viewHolder.title = (TextView) convertView.findViewById(R.id.educ_info_title);
            viewHolder.play_num = (TextView) convertView.findViewById(R.id.educ_info_play_num);
            viewHolder.zan_num = (TextView) convertView.findViewById(R.id.educ_info_zan_num);
            viewHolder.info_num = (TextView) convertView.findViewById(R.id.educ_info_num);
            viewHolder.by = (TextView) convertView.findViewById(R.id.educ_info_by);
            viewHolder.playing_rl = (RelativeLayout) convertView.findViewById(R.id.educ_video_info_playing_rl);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Resource data = datas.get(position);
        viewHolder.info_num.setVisibility(View.GONE);
        ImageLoader.getInstance().displayImage(data.coverImage + SysConstants.Imgurlsuffix80, viewHolder.icon, ImageUtils.DIO_MEDIA_ICON);
        viewHolder.play_num.setText(Utils.roundOne(data.viewedCount));
        viewHolder.zan_num.setText(Utils.roundOne(data.likedCount));
        viewHolder.by.setText("by " + data.providerName);
        viewHolder.title.setText(data.name);

        if (data.createdOn != null) {
            viewHolder.time.setText(Utils.getDateTime(mContext, data.createdOn));
        }
        if (data.type.getValue() == 1) {//audio
            if (data.id.equals(playId) ) {
                viewHolder.playing_rl.setVisibility(View.VISIBLE);
                viewHolder.play_icon.setImageResource(R.drawable.educ_play_audio);
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.login_text_blue));
            } else {
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.qzq_comment_content));
                viewHolder.playing_rl.setVisibility(View.GONE);
            }

        } else if (data.type.getValue() == 2) {//video
            if (data.id.equals(playId)) {
                viewHolder.playing_rl.setVisibility(View.VISIBLE);
                viewHolder.play_icon.setImageResource(R.drawable.educ_play_video);
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.login_text_blue));
            } else {
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.qzq_comment_content));
                viewHolder.playing_rl.setVisibility(View.GONE);
            }
        } else if(data.type.getValue() == 4){
            viewHolder.playing_rl.setVisibility(View.GONE);
            if (data.id.equals(playId)) {
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.login_text_blue));
            } else {
                viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.qzq_comment_content));
            }
        }else{
            viewHolder.playing_rl.setVisibility(View.GONE);
        }
        Drawable leftDrawable;
        if(data.liked){
            leftDrawable = mContext.getResources().getDrawable(R.drawable.educ_zan_press_icon);
        }else{
            leftDrawable = mContext.getResources().getDrawable(R.drawable.educ_zan_icon);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        viewHolder.zan_num.setCompoundDrawables(leftDrawable, null, null, null);


        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        ImageView play_icon;
        TextView time;
        TextView title;
        TextView play_num;
        TextView zan_num;
        TextView info_num;
        TextView by;
        RelativeLayout playing_rl;

    }
}
