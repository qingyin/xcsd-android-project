package com.tuxing.app.qzq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.tuxing.app.R;
import com.tuxing.app.activity.ClassNewPicActivity;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.adapter.ClassPictureListAdapter;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.ClassPicture;

import java.util.ArrayList;
import java.util.List;

public class ClassPicGridViewListAdapter extends ArrayAdapter<ClassPicture> {

	Context mContext;
	ArrayList<ClassPicture> list = new ArrayList<ClassPicture>();
	ArrayList<String> urlList = new ArrayList<String>();
	private MyGridView gridView;
	private int flag;
	ClassPictureListAdapter adapter;

	public ClassPicGridViewListAdapter(Context context, List<ClassPicture> objects, MyGridView myGridView0, int mFlag,ClassPictureListAdapter adapter) {
		super(context, 0, objects);
		flag = mFlag;
		mContext = context;
		this.adapter = adapter;
		gridView = myGridView0;
		if (objects == null)
			return;
		for (ClassPicture str : objects) {
			list.add(str);
			urlList.add(str.getPicUrl());
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_list_grid_item2, parent, false);
			holder.tiv = (MyImageView) convertView.findViewById(R.id.iv);
			int width = Utils.getDisplayWidth(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width/3, width/3);
			lp.setMargins(0, 0, 0, 0);
			holder.tiv.setLayoutParams(lp);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if (flag == 3) {
			// 视频
			holder.startRl.setVisibility(View.VISIBLE);
			holder.tiv.setImageUrl(list.get(position) + SysConstants.VIDEOSUFFIX306, R.drawable.defal_down_lym_proress, true);
		}else{
			holder.tiv.setScaleType(ScaleType.CENTER_CROP);
			holder.tiv.setImageUrl(list.get(position).getPicUrl() + SysConstants.Imgurlsuffix134, R.drawable.defal_down_proress,false);

		}
		holder.tiv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<ClassPicture> fileUris = list;
				if (fileUris.size() > 0) {
					ClassNewPicActivity.invoke(mContext, fileUris.get(position).getPicUrl(), true, adapter.getAllUrl(),adapter.getTotal(),adapter.getLastId());
				}
			}
		});
		return convertView;
	}

	class Holder {
		public MyImageView tiv;
		public LinearLayout startRl;
	}


}
