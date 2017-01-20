package com.tuxing.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.LyceumActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.ContentItemGroup;
import com.tuxing.sdk.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class LyceumAdapter extends BaseAdapter {

	private Context mContext;
	private List<ContentItemGroup> datas;
	View view = null;
	private int index = 0;
	private String itemUrl = "";
	List<View> viewList = new ArrayList<View>();
	private ClickListener clickListener;

	public LyceumAdapter(Context mContext, List<ContentItemGroup> datas, ClickListener clickListener) {
		this.mContext = mContext;
		this.datas = datas;
		this.clickListener = clickListener;
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
	public View getView(final int position, View contentView, ViewGroup mParent) {
		ViewHoler viewHolter = null;
		String timeStame = "";
		if (contentView == null) {
			viewHolter = new ViewHoler();
			contentView = LayoutInflater.from(mContext).inflate(R.layout.lyceum_layout_item, null);
			viewHolter.new_top_pic = (MyImageView) contentView.findViewById(R.id.lyceum_top_pic);
			viewHolter.new_top_pic_title = (TextView) contentView.findViewById(R.id.lyceum_tope_pic_title);
			viewHolter.lyceum_top_title = (TextView) contentView.findViewById(R.id.lyceum_top_title);
			viewHolter.articlUrl = (TextView) contentView.findViewById(R.id.lyceum_top_url);
			viewHolter.new_content_item = (LinearLayout) contentView.findViewById(R.id.lyceum_item_layout);
			viewHolter.new_top_pic_time = (TextView) contentView.findViewById(R.id.lyceum_time);
			viewHolter.new_summary = (TextView) contentView.findViewById(R.id.lyceum_summary);
			viewHolter.summaryLine= (TextView) contentView.findViewById(R.id.lyceum_summary_line);
			viewHolter.read_homedata = (RelativeLayout) contentView.findViewById(R.id.rl_read_homedata);
			viewHolter.artil_bg = (RelativeLayout) contentView.findViewById(R.id.rl_artil_bg);
			viewHolter.iv_lyceum_arrow = (ImageView) contentView.findViewById(R.id.iv_lyceum_arrow);
			contentView.setTag(viewHolter);
		} else {
			viewHolter = (ViewHoler) contentView.getTag();
		}
//		if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
//			viewHolter.iv_lyceum_arrow.setImageResource(R.drawable.arrow_right_t);
//		}else if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
//			viewHolter.iv_lyceum_arrow.setImageResource(R.drawable.arrow_right_p);
//		}

		 if(datas.size() > 0){
			 ContentItemGroup data = datas.get(position);
			if(data != null){
			
		if(data.getItems().get(0).getPublishTime() != null){
			timeStame = Utils.getDateTime(mContext, data.getItems().get(0).getPublishTime());
		}
		viewHolter.new_top_pic_time.setText(timeStame);
		final List<ContentItem> dataItems = data.getItems();
		viewList.clear();
		if(dataItems != null && dataItems.size() >= 1){
			for(int i = 0; i < dataItems.size() ; i++){
				if(i == 0){
					
					viewHolter.lyceum_top_title.setVisibility(View.VISIBLE);
					viewHolter.new_summary.setVisibility(View.VISIBLE);
					viewHolter.summaryLine.setVisibility(View.VISIBLE);
					viewHolter.read_homedata.setVisibility(View.VISIBLE);
					viewHolter.new_top_pic_title.setVisibility(View.GONE);
					viewHolter.new_content_item.setVisibility(View.GONE);
					viewHolter.new_top_pic.setImageUrl(dataItems.get(i).getCoverImageUrl(), R.drawable.defal_down_lym_proress,true);
					viewHolter.new_summary.setText(dataItems.get(0).getSummary());
					viewHolter.lyceum_top_title.setText(dataItems.get(0).getTitle());
				}else{
					viewHolter.new_content_item.removeAllViews();
					viewHolter.new_content_item.setVisibility(View.VISIBLE);
					viewHolter.new_top_pic_title.setVisibility(View.VISIBLE);
					viewHolter.new_summary.setVisibility(View.GONE);
					viewHolter.read_homedata.setVisibility(View.GONE);
					viewHolter.summaryLine.setVisibility(View.GONE);
					viewHolter.lyceum_top_title.setVisibility(View.GONE);
					viewHolter.new_top_pic_title.setText(dataItems.get(0).getTitle());
					view = (View) LayoutInflater.from(mContext).inflate(R.layout.lyceum_layout_item_item, null);
					viewHolter.contentTitle = (TextView) view.findViewById(R.id.lyceum_content_title);
					viewHolter.contentId = (TextView) view.findViewById(R.id.lyceum_content_id);
					viewHolter.contnetImageView = (MyImageView) view.findViewById(R.id.lyceum_content_pic);
					viewHolter.contentLine = (TextView) view.findViewById(R.id.lyceum_content_line);
					viewHolter.contentTitle.setText(dataItems.get(i).getTitle());
					viewHolter.contentId.setText(String.valueOf(dataItems.get(i).getPostUrl()));
					if(LyceumActivity.isScroll){
						viewHolter.contnetImageView.setImageUrl(dataItems.get(i).getCoverImageUrl(), R.drawable.defal_down_proress,false);
					}
					index = i;
					if(index != dataItems.size() - 1){
						viewHolter.contentLine.setVisibility(View.VISIBLE);
					}
					viewHolter.contentTitle.setOnClickListener(new ItemClickListener(clickListener,dataItems.get(i).getCoverImageUrl()));
					viewHolter.contnetImageView.setOnClickListener(new ItemClickListener(clickListener,dataItems.get(i).getCoverImageUrl()));
					view.setId(index);
					viewList.add(view);
				}
				}
		
			for(View view: viewList){
				viewHolter.new_content_item.addView(view);
				
			}
			viewHolter.new_top_pic.setOnClickListener(new TopClickListener(dataItems.get(0),clickListener));
			viewHolter.read_homedata.setOnClickListener(new TopClickListener(dataItems.get(0),clickListener));
			viewHolter.new_summary.setOnClickListener(new TopClickListener(dataItems.get(0),clickListener));
			viewHolter.lyceum_top_title.setOnClickListener(new TopClickListener(dataItems.get(0),clickListener));
				}
			}

		}
		return contentView;
	}

	public class ViewHoler {
		ImageView iv_lyceum_arrow;
		MyImageView contnetImageView;
		MyImageView new_top_pic;
		TextView new_top_pic_title;
		TextView lyceum_top_title;
		TextView new_top_pic_time;
		TextView new_summary;
		TextView summaryLine;
		TextView articlUrl;
		RelativeLayout read_homedata;
		RelativeLayout artil_bg;
		
		TextView contentTitle;
		TextView contentLine;
		TextView contentId;

		LinearLayout new_content_item;
	}
	
	public class ItemClickListener implements OnClickListener{
		private ClickListener clickListener;
		private String stringurl;
		public ItemClickListener(ClickListener clickListener,String stringurl) {
			this.clickListener = clickListener;
			this.stringurl = stringurl;
		}
		@Override
		public void onClick(View v) {
			View parent = (View) v.getParent();
			TextView titlename = (TextView) parent.findViewById(R.id.lyceum_content_title);
			SysConstants.titlename = titlename.getText().toString();
			SysConstants.shareimage = stringurl+"";
			TextView tvId = (TextView) parent.findViewById(R.id.lyceum_content_id);
			itemUrl = tvId.getText().toString();
			clickListener.itemClick();
		}
		
	}
	public class TopClickListener implements OnClickListener{
		private ContentItem item;
		private ClickListener clickListener;
		public TopClickListener(ContentItem item,ClickListener clickListener) {
			this.item = item;
			this.clickListener = clickListener;
		}
		@Override
		public void onClick(View v) {
			SysConstants.titlename = item.getTitle();
			SysConstants.shareimage = item.getCoverImageUrl();
			itemUrl = item.getPostUrl();
			clickListener.itemClick();
		}
	}
	
	public String getClickId(){
		return itemUrl;
	}

	public interface ClickListener{
		public  void itemClick();
	}
	
}
