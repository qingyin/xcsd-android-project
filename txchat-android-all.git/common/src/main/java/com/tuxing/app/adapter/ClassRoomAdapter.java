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
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.CourseLesson;
import com.tuxing.sdk.db.entity.CourseLessonBean;

import java.util.List;


public class ClassRoomAdapter extends BaseAdapter {

	private Context mContext;
	private List<CourseLessonBean> mDatas;
	View view = null;
    String Imgurlsuffix = "";
//    String Imgurlsuffix80 = "?imageView2/1/w/160/h/160/format/jpg";

	public ClassRoomAdapter(Context mContext, List<CourseLessonBean> mDatas) {
		this.mContext = mContext;
		this.mDatas = mDatas;
        Imgurlsuffix = "?imageView2/1/w/" + Utils.getDisplayWidth(mContext) + "/h/" + Utils.dip2px(mContext, 150) + "/format/jpg";
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
			contentView = LayoutInflater.from(mContext).inflate(R.layout.classroom_item, null);
			viewHolter.iconType = (ImageView) contentView.findViewById(R.id.classrooom_type);
			viewHolter.icon = (ImageView) contentView.findViewById(R.id.classrooom_icon);
			viewHolter.headIcon = (ImageView) contentView.findViewById(R.id.head_icon);
			viewHolter.title = (TextView) contentView.findViewById(R.id.classroom_title);
			viewHolter.author = (TextView) contentView.findViewById(R.id.classroom_austor);
			viewHolter.time = (TextView) contentView.findViewById(R.id.classroom_time);
			contentView.setTag(viewHolter);
		} else {
			viewHolter = (ViewHoler) contentView.getTag();
		}
		CourseLessonBean data = mDatas.get(position);

//data.duration
		if(data.getDuration()!= null && data.getDuration() < 60){
			viewHolter.time.setText("时长: " + String.valueOf(1) + "分钟");
		}else{
			viewHolter.time.setText("时长: " + String.valueOf((int)(data.getDuration() / 60)) + "分钟");
		}


        viewHolter.title.setText(data.getTitle());
//        viewHolter.author.setText(data.course.teacherName);
        viewHolter.author.setText(data.getTeacherName()+"");

		ImageLoader.getInstance().displayImage(data.getPic() + Imgurlsuffix, viewHolter.icon, ImageUtils.DIO_DOWN_LYM);
        ImageLoader.getInstance().displayImage(data.getTeacherAvatar()+"" + SysConstants.Imgurlsuffix80,viewHolter.headIcon, ImageUtils.DIO_USER_ICON);

		return contentView;
	}

	public class ViewHoler {
		ImageView icon;
		ImageView iconType;
		ImageView headIcon;
		TextView title;
		TextView author;
		TextView time;
	}
	
	
	
}
