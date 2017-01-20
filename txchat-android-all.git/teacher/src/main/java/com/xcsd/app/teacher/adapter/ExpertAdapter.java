package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.modle.Expert;

import java.util.List;

/**
 * Created by Mingwei on 2015/12/1.
 */
public class ExpertAdapter extends BaseAdapter {

    Context mContext;
    private List<Expert> mList;
    String format;
    int width;
    int hight;
    int screenWidth;

    public ExpertAdapter(Context context, List<Expert> list) {
        super();
        this.mContext = context;
        screenWidth = Utils.getDisplayWidth(mContext);
        width = (screenWidth - Utils.dip2px(mContext, 20))/2;
        hight = width*566/621;
        format = "?imageView2/1/w/" + width + "/h/" +hight + "/format/png";
        this.mList = list;
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
        if(position < 0 || position >= mList.size()){
            return convertView;
        }

        Expert expert = mList.get(position);

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expert_list_layout, null);
            viewHolder.vImage = (ImageView) convertView.findViewById(R.id.expert_list_item_image);
            viewHolder.vName = (TextView) convertView.findViewById(R.id.expert_list_item_name);
            viewHolder.vLevel = (TextView) convertView.findViewById(R.id.expert_list_item_level);
            viewHolder.view = (TextView) convertView.findViewById(R.id.bottom_view);
            viewHolder.ll = (LinearLayout)convertView.findViewById(R.id.expert_ll);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,hight);
        viewHolder.ll.setLayoutParams(lp);
        int bg = position%3;
        if(bg == 0){
            viewHolder.ll.setBackgroundResource(R.drawable.expert_item_bg1_shape);
        }else if(bg == 1){
            viewHolder.ll.setBackgroundResource(R.drawable.expert_item_bg2_shape);
        }else if(bg == 2){
            viewHolder.ll.setBackgroundResource(R.drawable.expert_item_bg3_shape);
        }
        String avatarUrl = mList.get(position).getAvatar() + format;
        ImageLoader.getInstance().displayImage(avatarUrl ,viewHolder.vImage, ImageUtils.DIO_EXPERT_AVATAR);
        viewHolder.vName.setText(expert.getName());
        viewHolder.vLevel.setText(expert.getTitle());
        if(position == mList.size() - 1){
            viewHolder.view.setVisibility(View.VISIBLE);
        }else{
            viewHolder.view.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView vImage;
        TextView vName;
        TextView view;
        TextView vLevel;
        LinearLayout ll;
    }
}
