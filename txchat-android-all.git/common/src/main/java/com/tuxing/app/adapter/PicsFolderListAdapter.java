package com.tuxing.app.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.activity.PicListActivity;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;

import java.util.List;

public class PicsFolderListAdapter extends BaseAdapter {
	
	public static class PicsFolder{
		public String folder;
		public int counts;
		public String lastPicFile;
	}
	public PicListActivity activity;
	private Context mContext;
	private List<PicsFolder> mDatas;
	public PicsFolderListAdapter(Context context, List<PicsFolder> folders,PicListActivity activity){
		mContext = context;
		mDatas = folders;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHoler;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.select_pics_list_item, null);
			viewHoler = new ViewHolder();
			viewHoler.head = (ImageView) convertView
					.findViewById(R.id.image);
			viewHoler.title = (TextView) convertView
					.findViewById(R.id.name);
			viewHoler.count = (TextView) convertView
					.findViewById(R.id.count);
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolder) convertView.getTag();
		}
		String imgUrl = "file://" + mDatas.get(position).lastPicFile;
		ImageLoader.getInstance().displayImage(imgUrl, viewHoler.head, ImageUtils.DIO_DOWN);
//		viewHoler.head.setImageBitmap(Utils.revitionImageSize(mDatas.get(position).lastPicFile,SysConstants.IMAGEIMPLESIZE_256));
		viewHoler.title.setText(mDatas.get(position).folder.substring(mDatas.get(position).folder.lastIndexOf("/")+1));
		viewHoler.count.setText("("+mDatas.get(position).counts+")");
		return convertView;
	}

	public class ViewHolder {
		public ImageView head;
		public TextView title;
		public TextView count;
	}
}
