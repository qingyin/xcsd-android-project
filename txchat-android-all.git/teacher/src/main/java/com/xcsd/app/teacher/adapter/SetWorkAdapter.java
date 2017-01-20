package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xcsd.app.teacher.R;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.HomeWorkGenerate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bryant on 16/4/7.
 */
public class SetWorkAdapter extends BaseAdapter{

    private static final String TAG = "SetWorkAdapter";
    private LayoutInflater inflater;
    public Context mContext;
    public List<HomeWorkGenerate> listdata;

    public SetWorkAdapter(Context mContext, List<HomeWorkGenerate> listdata) {
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
        ViewHolder holder = null;
        if (cuoluan.get(position)!=null){
            convertView = cuoluan.get(position);
            holder = (ViewHolder) convertView.getTag();
        }else{
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_setwork_item, parent,false);
            holder.rl_set = (RelativeLayout) convertView.findViewById(R.id.rl_set);
            holder.rl_done = (RelativeLayout) convertView.findViewById(R.id.rl_done);
            holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
            holder.item_times = (TextView) convertView.findViewById(R.id.item_times);
            holder.item_name_done = (TextView) convertView.findViewById(R.id.item_name_done);
            holder.name_icon = (RoundImageView) convertView.findViewById(R.id.name_icon);
            holder.name_heart = (RoundImageView) convertView.findViewById(R.id.name_heart);
            holder.name_icon_done = (RoundImageView) convertView.findViewById(R.id.name_icon_done);
            holder.name_heart_done = (RoundImageView) convertView.findViewById(R.id.name_heart_down);

            cuoluan.put(position,convertView);
            convertView.setTag(holder);
        }

//
        if (listdata.size()>0){
            HomeWorkGenerate homworkedata = listdata.get(position);
            if (homworkedata != null){

                if (homworkedata.getRemainMaxCount()!=0){
                    if (!homworkedata.getSpecialAttention()){
                        holder.name_heart.setVisibility(View.GONE);
                    }
                    holder.rl_done.setVisibility(View.GONE);
                    holder.item_name.setText(homworkedata.getName());
                    holder.item_times.setText(homworkedata.getGenerateCount() + "关");
                    holder.name_icon.setImageUrl(homworkedata.getAvatar(),R.drawable.default_image);
                }else {
                    if (!homworkedata.getSpecialAttention()){
                        holder.name_heart_done.setVisibility(View.GONE);
                    }
                    holder.rl_set.setVisibility(View.GONE);
                    holder.item_name_done.setText(homworkedata.getName());
                    holder.name_icon_done.setImageUrl(homworkedata.getAvatar(), R.drawable.default_image);
                }
            }
        }
////        数据及联网逻辑未完成
        return convertView;
    }

    private static class ViewHolder{

        /** 完成类型布局 */
        RelativeLayout rl_set,rl_done;

        /** 姓名，完成次数 完成者姓名*/
        TextView item_name,item_times,item_name_done;

        /** 头像 ，是否有桃心*/
        RoundImageView name_icon,name_heart,name_icon_done,name_heart_done;

    }
}


