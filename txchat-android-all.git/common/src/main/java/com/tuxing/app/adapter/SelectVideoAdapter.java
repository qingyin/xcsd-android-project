package com.tuxing.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.VideoInfo;

import java.util.List;

public class SelectVideoAdapter extends RecyclerView.Adapter<SelectVideoAdapter.ViewHolder> {


	private Context mContext;
	private List<VideoInfo> mDatas;
	public int clickPositon = 1;
	private MyItemClickListener onClickListener;

	public SelectVideoAdapter(Context context, List<VideoInfo> folders){
		mContext = context;
		mDatas = folders;
	}

	public void setOnClickListener(MyItemClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHoler, int position) {
		if(position == clickPositon){
			viewHoler.head_bg.setVisibility(View.VISIBLE);
			viewHoler.tv_bg.setVisibility(View.GONE);
			viewHoler.time.setTextColor(mContext.getResources().getColor(R.color.white));
		}else{
			viewHoler.head_bg.setVisibility(View.GONE);
			viewHoler.tv_bg.setVisibility(View.VISIBLE);
			viewHoler.time.setTextColor(mContext.getResources().getColor(R.color.select_video_time));
		}

		if(position == 0){
			viewHoler.head.setImageResource(R.drawable.select_video_pic);
			viewHoler.rl_desc.setVisibility(View.GONE);
			viewHoler.tv_bg.setVisibility(View.GONE);
		}else{
			String imgUrl = "file://" + mDatas.get(position).videoUrl;
			ImageLoader.getInstance().displayImage(imgUrl, viewHoler.head, ImageUtils.DIO_ROUND_DOWN);
			viewHoler.time.setText(Utils.getDuration(mDatas.get(position).duration));
			viewHoler.rl_desc.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
		// 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
		View view = View.inflate(viewGroup.getContext(), R.layout.select_video_item, null);
		// 创建一个ViewHolder
		ViewHolder holder = new ViewHolder(view, onClickListener);

		return holder;
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public ImageView head;
		public ImageView head_bg;
		public TextView time;
		public TextView tv_bg;
		private RelativeLayout rl_desc;
		private MyItemClickListener listener;

		public ViewHolder(View itemView, MyItemClickListener listener) {
			super(itemView);
			this.listener = listener;
			head = (ImageView) itemView.findViewById(R.id.image_iv);
			head_bg = (ImageView) itemView.findViewById(R.id.image_iv_bg);
			time = (TextView) itemView.findViewById(R.id.tv_time);
			tv_bg = (TextView) itemView.findViewById(R.id.iv_alpha);
			rl_desc = (RelativeLayout)itemView.findViewById(R.id.rl_desc);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				listener.onItemClick(itemView, getPosition());
			}
		}
	}

	public interface MyItemClickListener {
		public void onItemClick(View view, int position);
	}

}