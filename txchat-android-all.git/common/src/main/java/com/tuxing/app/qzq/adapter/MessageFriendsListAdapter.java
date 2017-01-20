package com.tuxing.app.qzq.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.qzq.MyCircleListActivity;
import com.tuxing.app.util.DateTimeUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.Feed;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageFriendsListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Feed> list;
	boolean isMyself;

	DateFormat format = new SimpleDateFormat("MM月dd日");

	public MessageFriendsListAdapter(Context context, List<Feed> mlist,boolean isMyself) {
		mContext = context;
		this.isMyself = isMyself;

		if (mlist == null)
			mlist = new ArrayList<Feed>();
		list = mlist;
	}

	public void setData(List<Feed> mlist) {
		list = mlist;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_friends_list_item, parent, false);
			holder = new Holder();
			holder.rl_img1 = (RelativeLayout) convertView.findViewById(R.id.rl_img1);
			holder.rl_img2 = (RelativeLayout) convertView.findViewById(R.id.rl_img2);
			holder.rl_img3 = (RelativeLayout) convertView.findViewById(R.id.rl_img3);
			holder.rl_img4 = (RelativeLayout)convertView.findViewById(R.id.rl_img4);

			holder.iv_img1_1 = (MyImageView) convertView.findViewById(R.id.iv_img1_1);

			holder.iv_img2_1 = (MyImageView) convertView.findViewById(R.id.iv_img2_1);
			holder.iv_img2_2 = (MyImageView) convertView.findViewById(R.id.iv_img2_2);

			holder.iv_img1 = (MyImageView) convertView.findViewById(R.id.iv_img1);
			holder.iv_img2 = (MyImageView) convertView.findViewById(R.id.iv_img2);
			holder.iv_img3 = (MyImageView) convertView.findViewById(R.id.iv_img3);
			holder.iv_img4 = (MyImageView) convertView.findViewById(R.id.iv_img4);

			holder.iv_img4_1 = (MyImageView) convertView.findViewById(R.id.iv_img4_1);
			holder.iv_img4_2 = (MyImageView) convertView.findViewById(R.id.iv_img4_2);
			holder.iv_img4_3 = (MyImageView) convertView.findViewById(R.id.iv_img4_3);

			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_date2 = (TextView) convertView.findViewById(R.id.tv_date2);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_content1 = (TextView) convertView.findViewById(R.id.tv_content_1);
			holder.tv_img_count = (TextView) convertView.findViewById(R.id.tv_img_count);
			holder.view_blank = (View)convertView.findViewById(R.id.view_blank);
			holder.view_blank2 = (View)convertView.findViewById(R.id.view_blank2);
			holder.status_btn = (LinearLayout)convertView.findViewById(R.id.ll_status_btn);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		int flag = -1;
		if(position==list.size()-1){
			holder.view_blank2.setVisibility(View.VISIBLE);
		}else{
			holder.view_blank2.setVisibility(View.GONE);
		}
		Feed feed = list.get(position);
		holder.tv_content.setText(SmileUtils.getSmiledText(mContext,
                list.get(position).getContent()), TextView.BufferType.SPANNABLE);
		holder.tv_content1.setText(SmileUtils.getSmiledText(mContext,
                list.get(position).getContent()), TextView.BufferType.SPANNABLE);
//		holder.tv_content.setText(decodeUtf8(list.get(position).getContent()));
		List<String> fileKeyList = null;
		if(DateTimeUtils.getDateTime(list.get(position).getPublishTime())==0){
			fileKeyList = MyCircleListActivity.filePaths2;
		}else{
			fileKeyList = new ArrayList<String>();
			String json = feed.getAttachments();
			JSONArray array = null;

			try {
				array = new JSONArray(json);
				if(array.length() > 0){
					for(int i = 0; i < array.length(); i++){
						fileKeyList.add(array.getJSONObject(i).optString("url"));
						flag = array.getJSONObject(i).optInt("type");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}



		int image_count = 0;
		if(fileKeyList!=null)
			image_count = fileKeyList.size();
		if (image_count == 0) {
			holder.tv_content1.setVisibility(View.VISIBLE);
			holder.tv_content1.setBackgroundResource(R.drawable.shape_text_border);
			holder.tv_content.setVisibility(View.GONE);
			holder.tv_img_count.setVisibility(View.GONE);
			holder.rl_img1.setVisibility(View.GONE);
			holder.rl_img2.setVisibility(View.GONE);
			holder.rl_img3.setVisibility(View.GONE);
			holder.rl_img4.setVisibility(View.GONE);
		} else if (image_count > 4) {
			holder.tv_img_count.setVisibility(View.VISIBLE);
			holder.tv_img_count.setText("共" + image_count + "张");
			holder.tv_content.setVisibility(View.VISIBLE);
			holder.tv_content.setBackgroundColor(Color.TRANSPARENT);
			holder.tv_content1.setVisibility(View.GONE);
		} else {
			holder.tv_content.setVisibility(View.VISIBLE);
			holder.tv_content.setBackgroundColor(Color.TRANSPARENT);
			holder.tv_content1.setVisibility(View.GONE);
			holder.tv_img_count.setText("");
			holder.tv_img_count.setVisibility(View.GONE);
		}

		if (image_count == 1) {
			holder.tv_img_count.setVisibility(View.VISIBLE);
			holder.rl_img1.setVisibility(View.VISIBLE);
			holder.rl_img2.setVisibility(View.GONE);
			holder.rl_img3.setVisibility(View.GONE);
			holder.rl_img4.setVisibility(View.GONE);
			if( flag == 3){
				holder.status_btn.setVisibility(View.VISIBLE);
				holder.iv_img1_1.setImageUrl(fileKeyList.get(0)+ SysConstants.VIDEOSUFFIX306, R.drawable.defal_down_proress,false);
			}else{
				holder.status_btn.setVisibility(View.GONE);
				holder.iv_img1_1.setImageUrl(fileKeyList.get(0)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);

			}
		} else if (image_count == 2) {
			holder.tv_img_count.setVisibility(View.VISIBLE);
			holder.rl_img1.setVisibility(View.GONE);
			holder.rl_img2.setVisibility(View.VISIBLE);
			holder.rl_img3.setVisibility(View.GONE);
			holder.rl_img4.setVisibility(View.GONE);
			holder.iv_img2_1.setImageUrl(fileKeyList.get(0)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
			holder.iv_img2_2.setImageUrl(fileKeyList.get(1)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
		} else if (image_count == 3) {
			holder.tv_img_count.setVisibility(View.VISIBLE);
			holder.rl_img1.setVisibility(View.GONE);
			holder.rl_img2.setVisibility(View.GONE);
			holder.rl_img3.setVisibility(View.GONE);
			holder.rl_img4.setVisibility(View.VISIBLE);
			holder.iv_img4_1.setImageUrl(fileKeyList.get(0) + SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
			holder.iv_img4_2.setImageUrl(fileKeyList.get(1)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
			holder.iv_img4_3.setImageUrl(fileKeyList.get(2)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
		} else if (image_count >= 4) {
			holder.tv_img_count.setVisibility(View.VISIBLE);
			holder.rl_img1.setVisibility(View.GONE);
			holder.rl_img2.setVisibility(View.GONE);
			holder.rl_img3.setVisibility(View.VISIBLE);
			holder.rl_img4.setVisibility(View.GONE);
			holder.iv_img1.setImageUrl(fileKeyList.get(0)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
			holder.iv_img2.setImageUrl(fileKeyList.get(1)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
			holder.iv_img3.setImageUrl(fileKeyList.get(2)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
			holder.iv_img4.setImageUrl(fileKeyList.get(3)+ SysConstants.Imgurlsuffix80, R.drawable.defal_down_proress,false);
		}
		holder.view_blank.setVisibility(View.GONE);
		if(DateTimeUtils.getDateTime(list.get(position).getPublishTime())==0){
			holder.tv_date.setText("刚刚");
			holder.tv_date2.setText("");
			try {
				if(format.parse(format.format(new Date(System.currentTimeMillis()))).getTime() == 
						format.parse(format.format(new Date(list.get(position).getPublishTime()))).getTime()
						&& isMyself){
					holder.tv_date.setVisibility(View.INVISIBLE);
					holder.tv_date2.setVisibility(View.INVISIBLE);
				}else{
					holder.tv_date.setVisibility(View.VISIBLE);
					holder.tv_date2.setVisibility(View.VISIBLE);
//					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//					lp.topMargin = dip2px(10);
//					convertView.setLayoutParams(lp);
					holder.view_blank.setVisibility(View.VISIBLE);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			String value = format.format(DateTimeUtils.getDate(list.get(position).getPublishTime()));
			holder.tv_date.setText(DateTimeUtils.convertMonth(value.substring(0, 3)));
			holder.tv_date2.setText(value.substring(3, 5));
			
			try {
				if(format.parse(format.format(new Date(System.currentTimeMillis()))).getTime() == 
						format.parse(format.format(new Date(list.get(position).getPublishTime()))).getTime()
						&& isMyself){
					holder.tv_date.setVisibility(View.INVISIBLE);
					holder.tv_date2.setVisibility(View.INVISIBLE);
				}else{
					if(position != 0){
						if(list.size()==1){
							holder.tv_date.setVisibility(View.VISIBLE);
							holder.tv_date2.setVisibility(View.VISIBLE);
//							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//							lp.topMargin = dip2px(10);
//							convertView.setLayoutParams(lp);
							holder.view_blank.setVisibility(View.VISIBLE);
						}else{
							String current = format.format(DateTimeUtils.getDate(list.get(position).getPublishTime()));
							String previous = format.format(DateTimeUtils.getDate(list.get(position-1).getPublishTime()));
							if(current.equalsIgnoreCase(previous)){
								holder.tv_date.setVisibility(View.INVISIBLE);
								holder.tv_date2.setVisibility(View.INVISIBLE);
							}else{
//								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//								lp.topMargin = dip2px(10);
//								convertView.setLayoutParams(lp);
								holder.tv_date.setVisibility(View.VISIBLE);
								holder.tv_date2.setVisibility(View.VISIBLE);
								holder.view_blank.setVisibility(View.VISIBLE);
							}
						}
					}else{
//						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//						lp.topMargin = dip2px(10);
//						convertView.setLayoutParams(lp);
						holder.tv_date.setVisibility(View.VISIBLE);
						holder.tv_date2.setVisibility(View.VISIBLE);
						holder.view_blank.setVisibility(View.VISIBLE);
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return convertView;
	}

	class Holder {
		private TextView tv_content1;
		private TextView tv_content;
		private TextView tv_img_count;
		private RelativeLayout rl_img1;
		private RelativeLayout rl_img2;
		private RelativeLayout rl_img3;
		private RelativeLayout rl_img4;

		private MyImageView iv_img1_1;

		private MyImageView iv_img2_1;
		private MyImageView iv_img2_2;

		private MyImageView iv_img1;
		private MyImageView iv_img2;
		private MyImageView iv_img3;
		private MyImageView iv_img4;

		private MyImageView iv_img4_1;
		private MyImageView iv_img4_2;
		private MyImageView iv_img4_3;
		private TextView tv_date;
		private TextView tv_date2;
		private View view_blank;
		private View view_blank2;
		private LinearLayout status_btn;
	}

	public String decodeUtf8(String str) {
		String value = "";
		try {
			value = new String(URLDecoder.decode(str, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public int dip2px(float dpValue) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}