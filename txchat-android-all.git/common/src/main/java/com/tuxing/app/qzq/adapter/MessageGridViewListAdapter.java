package com.tuxing.app.qzq.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.tuxing.app.R;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.activity.VideoPlayerActivity;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import org.jivesoftware.smack.util.ReaderListener;

import java.util.ArrayList;
import java.util.List;

public class MessageGridViewListAdapter extends ArrayAdapter<String> {
    Context mContext;
    ArrayList<String> list = new ArrayList<String>();
    private MyGridView gridView;
    private int flag;

    public MessageGridViewListAdapter(Context context, List<String> objects, MyGridView myGridView0, int mFlag) {
        super(context, 0, objects);
        flag = mFlag;
        mContext = context;
        gridView = myGridView0;
        if (objects == null)
            return;
        for (String str : objects) {
            list.add(str);
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
            if (list.size() == 1 || flag == 3) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_list_grid_item3, parent, false);
                holder.startRl = (LinearLayout) convertView.findViewById(R.id.ll_status_btn);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_msg_list_grid_item, parent, false);
            }
            holder.tiv = (MyImageView) convertView.findViewById(R.id.iv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (flag == 3) {
            // 视频
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dip2px(mContext, 160), dip2px(mContext, 120));
            lp.setMargins(0, 0, 0, 0);
            holder.startRl.setLayoutParams(lp);
            holder.tiv.setLayoutParams(lp);
            holder.tiv.setImageUrl(list.get(position) + SysConstants.VIDEOSUFFIX306, 0, true);
            holder.startRl.setVisibility(View.VISIBLE);
        } else {
            holder.tiv.setScaleType(ScaleType.CENTER_CROP);
            if (list.size() == 1) {
                holder.startRl.setVisibility(View.GONE);
                holder.tiv.setImageUrl(list.get(position) + SysConstants.Imgurlsuffix306, R.drawable.defal_down_lym_proress, true);
            } else {
                holder.tiv.setImageUrl(list.get(position) + SysConstants.Imgurlsuffix134, R.drawable.defal_down_proress, false);
            }
        }
        holder.tiv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ArrayList<String> fileUris = list;
                if (fileUris.size() > 0 && flag != 3) {
                    NewPicActivity.invoke(mContext, fileUris.get(position), true, list);
                } else {
                    Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                    intent.putExtra("flag", 1);
                    intent.putExtra("remotepath", fileUris.get(position));
                    mContext.startActivity(intent);
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
