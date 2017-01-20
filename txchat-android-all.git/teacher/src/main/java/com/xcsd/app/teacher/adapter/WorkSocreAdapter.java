package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcsd.app.teacher.R;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.HomeWorkMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bryant on 16/4/7.
 */
public class WorkSocreAdapter extends BaseAdapter{

    private static final String TAG = "WorkSocreAdapter";
    private LayoutInflater inflater;
    public Context mContext;
    public List<HomeWorkMember> listdata;

    public WorkSocreAdapter(Context mContext, List<HomeWorkMember> listdata) {
        this.mContext = mContext;
        this.listdata = listdata;
    }

    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<Integer, View> cuoluan = new HashMap<Integer, View>();

//

        ViewHolder holder = null;
            if (cuoluan.get(position)!=null){
                convertView = cuoluan.get(position);
                holder = (ViewHolder) convertView.getTag();
            }else {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_worksocre_item, parent, false);
                holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
                holder.sorrt_item_dones = (TextView) convertView.findViewById(R.id.sorrt_item_dones);
                holder.item_times = (TextView) convertView.findViewById(R.id.item_times);
                holder.name_icon = (RoundImageView) convertView.findViewById(R.id.name_icon);
                holder.name_heart = (RoundImageView) convertView.findViewById(R.id.name_heart);
                holder.item_arrow = (ImageView) convertView.findViewById(R.id.item_arrow);

                cuoluan.put(position,convertView);
                convertView.setTag(holder);
            }


        if (listdata.size()>0){
            HomeWorkMember data = listdata.get(position);
            holder.item_name.setText(data.getName());
            holder.item_times.setText(data.getScore()+"分");
            String imageUrl = data.getAvatar();
            holder.name_icon.setImageUrl(imageUrl, com.tuxing.app.R.drawable.default_image);
            if (!data.getSpecialAttention()){
                holder.name_heart.setVisibility(View.GONE);
            }
            if (data.getStatus()==0){
                holder.item_times.setVisibility(View.GONE);
                holder.item_arrow.setVisibility(View.GONE);
                holder.sorrt_item_dones.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private static class ViewHolder{

        /** 姓名，分数,未完成 */
        TextView item_name,item_times,sorrt_item_dones;

        /** 头像，桃心 */
        RoundImageView name_icon,name_heart;

        /** 图标是否显示 */
        ImageView item_arrow;

    }

}
