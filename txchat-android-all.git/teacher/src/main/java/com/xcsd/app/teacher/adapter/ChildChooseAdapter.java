package com.xcsd.app.teacher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xcsd.app.teacher.R;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildChooseAdapter extends ArrayAdapter<User> {

	private Context context;
	private GridView teacherGridView;
	private MyImageLoadingListener imageLoadingListener;
	private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

	public ChildChooseAdapter(Context context, List<User> objects,
			GridView gvChild) {
		super(context, 0, objects);
		this.context = context;
		MyImageLoadingListener imageLoadingListener = new MyImageLoadingListener(
				this);
		teacherGridView = gvChild;
	}

	public ChildChooseAdapter(Context context, List<User> objects,
			GridView gvChild,
			Map<Integer, Boolean> mSelectMap) {
		this(context,objects,gvChild);
		this.mSelectMap = mSelectMap;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHoler;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.choose_user_item, null);
			viewHoler = new ViewHolder();
			viewHoler.head = (RoundImageView) convertView
					.findViewById(R.id.ivContactListItem);
			viewHoler.user = (TextView) convertView
					.findViewById(R.id.tvContactListItem);
			viewHoler.select = (RoundImageView) convertView.findViewById(R.id.select);
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolder) convertView.getTag();
		}
		
		Boolean selectFlag = mSelectMap.get(position);
		if(selectFlag==null){
			viewHoler.select.setVisibility(View.GONE);
		}else {
			viewHoler.select.setVisibility(View.VISIBLE);
		}
		
		User memory = getItem(position);
		String url = memory.getAvatar();

		viewHoler.head.setTag(url);
		viewHoler.head.setImageUrl(url, R.drawable.default_avatar);
		viewHoler.user.setText(memory.getNickname());
		return convertView;
	}

	public class ViewHolder {
		public RoundImageView head;
		public TextView user;
		public RoundImageView select;
	}

	private class MyImageLoadingListener extends SimpleImageLoadingListener {
		private ChildChooseAdapter mAdapter;

		public MyImageLoadingListener(ChildChooseAdapter adapter) {
			mAdapter = adapter;
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			mAdapter.notifyDataSetChanged();
		}

	}

}
