package com.tuxing.app.adapter;

import java.util.List;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tuxing.app.R;
import com.tuxing.app.bean.TestFirstBean;
import com.tuxing.app.util.ShapeUtil;
import com.tuxing.app.view.RotateTextView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestFirstAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	public List<TestFirstBean> listItem;

	private Context context;

	private int itemWidth;
	
	private DisplayImageOptions mOption;
	
	private ImageLoader mImageLoader;

	public TestFirstAdapter(Context context, List<TestFirstBean> listItem) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.listItem = listItem;
		mOption = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888).
				displayer(new FadeInBitmapDisplayer(300)).build();
		mImageLoader = ImageLoader.getInstance();
	}

	public void setListItem(List<TestFirstBean> listItem) {
		this.listItem = listItem;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.test_first_item, null);
			holder = new ViewHolder();
			holder.test_first_item_bg = (RelativeLayout) convertView
					.findViewById(R.id.test_first_item_bg);
			holder.test_first_item_img = (ImageView) convertView
					.findViewById(R.id.test_first_item_img);
			holder.test_first_name = (TextView) convertView
					.findViewById(R.id.test_first_name);
			holder.school_age = (RotateTextView) convertView
					.findViewById(R.id.school_age);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TestFirstBean test = listItem.get(position);
		holder.test_first_item_bg.setBackgroundDrawable(ShapeUtil
				.getGradientTestDrawable(context, "#" + test.colorValue));
		mImageLoader.displayImage(test.animalPic, holder.test_first_item_img, mOption);
		//holder.test_first_item_img.setBackgroundResource(R.drawable.test_ani_img);
		holder.test_first_name.setText(test.name);
//		holder.school_age.setText(GuideHelper
//				.getCurrentBabySchoolAgeStr(context));
		holder.test_first_item_bg
				.setLayoutParams(new RelativeLayout.LayoutParams(itemWidth,
						itemWidth));

		return convertView;
	}

	final class ViewHolder {
		public RelativeLayout test_first_item_bg;
		public ImageView test_first_item_img;
		public TextView test_first_name;
		public RotateTextView school_age;
	}

}
