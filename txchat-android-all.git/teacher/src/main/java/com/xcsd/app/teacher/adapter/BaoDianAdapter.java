package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.rpc.proto.KnowledegeContentType;
import com.tuxing.sdk.modle.Knowledge;

import java.util.List;


public class BaoDianAdapter extends BaseAdapter {

    private Context mContext;
    private List<Knowledge> mList;

    public BaoDianAdapter(Context context, List<Knowledge> list) {
        super();
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<Knowledge> listData) {
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
        Knowledge mKnowledge = mList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.baodian_list_item, null);
            viewHolder.vItemLl = (RelativeLayout) convertView.findViewById(R.id.bao_dian_item_ll);
            viewHolder.vItemImage = (ImageView) convertView.findViewById(R.id.bao_dian_item_img);
            viewHolder.vItemVideoImage = (ImageView) convertView.findViewById(R.id.bao_dian_video_img);
            viewHolder.vItemTitle = (TextView) convertView.findViewById(R.id.bao_dian_item_title);
            viewHolder.vSupportNum = (TextView) convertView.findViewById(R.id.bao_dian_support);
            viewHolder.vItemContent = (TextView) convertView.findViewById(R.id.bao_dian_item_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mKnowledge.getType() == KnowledegeContentType.K_PLAIN) {//text
            viewHolder.vItemLl.setVisibility(View.GONE);
        } else if (mKnowledge.getType() == KnowledegeContentType.K_VIDEO) {//video
            viewHolder.vItemVideoImage.setVisibility(View.VISIBLE);
            String avatarUrl = mKnowledge.getCoverImageUrl() + SysConstants.Imgurlsuffix120;
            ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.vItemImage, ImageUtils.DIO_BIG_SQUARE_AVATAR);
        } else {//normal
            viewHolder.vItemImage.setVisibility(View.VISIBLE);
            viewHolder.vItemVideoImage.setVisibility(View.GONE);
            String avatarUrl = mKnowledge.getCoverImageUrl() + SysConstants.Imgurlsuffix120;
            ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.vItemImage, ImageUtils.DIO_BIG_SQUARE_AVATAR);
        }

        viewHolder.vSupportNum.setSelected(mKnowledge.isThanked());
        viewHolder.vItemTitle.setText(mKnowledge.getTitle());
        viewHolder.vSupportNum.setText(String.valueOf(mKnowledge.getThankCount()));//赞的数目
        viewHolder.vItemContent.setText(mKnowledge.getDescription());

        return convertView;
    }

    class ViewHolder {
        RelativeLayout vItemLl;
        ImageView vItemImage;
        ImageView vItemVideoImage;
        TextView vItemTitle;
        TextView vSupportNum;
        TextView vItemContent;
    }
}
