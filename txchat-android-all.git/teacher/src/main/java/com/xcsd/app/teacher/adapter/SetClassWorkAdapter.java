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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryant on 16/4/7.
 */
public class SetClassWorkAdapter extends BaseAdapter {

    private static final String TAG = "SetClassWorkAdapter";
    private LayoutInflater inflater;
    public Context mContext;
    public List<ContentItem> list;

    public SetClassWorkAdapter(Context mContext, List<ContentItem> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.listview_classwork_item,parent,false);

        }
        ViewHolder holder = (ViewHolder)convertView.getTag();
        if (holder == null){
            holder = new ViewHolder();
            holder.rl_set = (RelativeLayout) convertView.findViewById(R.id.rl_set);
            holder.rl_done = (RelativeLayout) convertView.findViewById(R.id.rl_done);
            holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
            holder.item_times = (TextView) convertView.findViewById(R.id.item_times);
            holder.item_name_done = (TextView) convertView.findViewById(R.id.item_name_done);
            holder.name_icon = (RoundImageView) convertView.findViewById(R.id.name_icon);
            holder.name_heart = (RoundImageView) convertView.findViewById(R.id.name_heart);
            holder.name_icon_done = (RoundImageView) convertView.findViewById(R.id.name_icon_done);
            holder.name_heart_done = (RoundImageView) convertView.findViewById(R.id.name_heart_down);

            convertView.setTag(holder);
        }else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        }
        if (true){
//            未设置完成显示
            holder.rl_set.setVisibility(View.VISIBLE);
        }else {
//            设置完成显示
            holder.rl_done.setVisibility(View.INVISIBLE);
        }
//        holder.name.setText(list.get(position).);
//        数据及联网逻辑未完成
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
