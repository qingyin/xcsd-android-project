package com.tuxing.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.CourseComment;

import java.util.ArrayList;
import java.util.List;


public class ClassRoomCommentAdapter extends BaseAdapter {

	private Context mContext;
	private List<CourseComment> mDatas;
//	private List<Comment> mDatas;
	View view = null;

	public ClassRoomCommentAdapter(Context mContext, List<CourseComment> mDatas) {
		this.mContext = mContext;
		this.mDatas = mDatas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup mParent) {
		ViewHoler viewHolter = null;
		if (contentView == null) {
			viewHolter = new ViewHoler();
			contentView = LayoutInflater.from(mContext).inflate(R.layout.classroom_comment_item_layout, null);
			viewHolter.icon = (ImageView) contentView.findViewById(R.id.classroom_comment_item_icon);
			viewHolter.xing_1 = (ImageView) contentView.findViewById(R.id.comment_item_xing_1);
			viewHolter.xing_2 = (ImageView) contentView.findViewById(R.id.comment_item_xing_2);
			viewHolter.xing_3 = (ImageView) contentView.findViewById(R.id.comment_item_xing_3);
			viewHolter.xing_4 = (ImageView) contentView.findViewById(R.id.comment_item_xing_4);
			viewHolter.xing_5 = (ImageView) contentView.findViewById(R.id.comment_item_xing_5);
			viewHolter.count = (TextView) contentView.findViewById(R.id.classroom_comment_item_content);
			viewHolter.author = (TextView) contentView.findViewById(R.id.classroom_comment_item_austor);
			viewHolter.time = (TextView) contentView.findViewById(R.id.classroom_comment_item_time);
			contentView.setTag(viewHolter);
		} else {
			viewHolter = (ViewHoler) contentView.getTag();
		}
        List<ImageView> xingList = new ArrayList<>();
        xingList.clear();
        xingList.add(viewHolter.xing_1);
        xingList.add(viewHolter.xing_2);
        xingList.add(viewHolter.xing_3);
        xingList.add(viewHolter.xing_4);
        xingList.add(viewHolter.xing_5);
       CourseComment data = mDatas.get(position);
//		Comment data = mDatas.get(position);
        if(!TextUtils.isEmpty(data.content)){
            viewHolter.count.setText(SmileUtils.getSmiledText(mContext, data.content));
            viewHolter.count.setVisibility(View.VISIBLE);
        }else{
            viewHolter.count.setVisibility(View.GONE);
        }

       viewHolter.author.setText(data.userName);
//		viewHolter.author.setText(data.userTitle);
        viewHolter.time.setText(Utils.getDateTime(mContext, data.createOn));
        ImageLoader.getInstance().displayImage(data.userAvatar + SysConstants.Imgurlsuffix80 ,viewHolter.icon, ImageUtils.DIO_USER_ICON);
        int sorce = data.score;
        for(int i = 0; i < xingList.size(); i++){
            if(i < sorce){
                xingList.get(i).setSelected(true);
            }else{
                xingList.get(i).setSelected(false);
            }
        }
		return contentView;
	}

	public class ViewHoler {
		ImageView xing_1;
		ImageView xing_2;
		ImageView xing_3;
		ImageView xing_4;
		ImageView xing_5;
		ImageView icon;
		TextView count;
		TextView author;
		TextView time;
	}
	
	
	
}
