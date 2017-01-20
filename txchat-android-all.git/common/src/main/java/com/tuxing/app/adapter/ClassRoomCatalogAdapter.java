package com.tuxing.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.rpc.proto.CourseLesson;

import java.util.List;


public class ClassRoomCatalogAdapter extends BaseAdapter {

	private Context mContext;
	private List<CourseLesson> mDatas;
//	private List<HomeWorkRecord> mDatas;
	View view = null;
    private long playId;

	public ClassRoomCatalogAdapter(Context mContext, List<CourseLesson> mDatas, long playId) {
		this.mContext = mContext;
		this.mDatas = mDatas;
        this.playId = playId;
	}

    public void setData(long playId){
        this.playId = playId;
        notifyDataSetChanged();
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
			contentView = LayoutInflater.from(mContext).inflate(R.layout.classroom_catalog_item_layout, null);
			viewHolter.title = (TextView) contentView.findViewById(R.id.classroom_catalog_item_title);
			viewHolter.time = (TextView) contentView.findViewById(R.id.classroom_catalog_item_time);
			viewHolter.num = (TextView) contentView.findViewById(R.id.classroom_catalog_item_num);
			viewHolter.view =  contentView.findViewById(R.id.view_1);
			contentView.setTag(viewHolter);
		} else {
			viewHolter = (ViewHoler) contentView.getTag();
		}
        if(position ==0){
            viewHolter.view.setVisibility(View.VISIBLE);
        }else{
            viewHolter.view.setVisibility(View.GONE);
        }
       CourseLesson data = mDatas.get(position);
//		HomeWorkRecord data = mDatas.get(position);
        if(playId == data.id){
            viewHolter.title.setTextColor(mContext.getResources().getColor(R.color.login_text_blue));
        }else{
            viewHolter.title.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        if(data.duration!= null && data.duration < 60){
            viewHolter.time.setText("时长: " + String.valueOf(1) + "分钟");
        }else{
            viewHolter.time.setText("时长: " + String.valueOf((int)(data.duration / 60)) + "分钟");
        }
        if((position + 1) > 9 ){
            viewHolter.num.setText(String.valueOf(position + 1));
        }else{
            viewHolter.num.setText("0" + String.valueOf(position + 1));
        }

//        viewHolter.title.setText(data.getTargetName());
		viewHolter.title.setText(data.title);


		return contentView;
	}

	public class ViewHoler {
		TextView title;
		TextView time;
        TextView num;
        View view;
	}
	
	
	
}
