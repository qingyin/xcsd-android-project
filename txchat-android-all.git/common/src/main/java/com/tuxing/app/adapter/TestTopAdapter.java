package com.tuxing.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.CourseLessonBean;
import com.tuxing.sdk.modle.DepartmentMember;

import java.util.List;


public class TestTopAdapter extends RecyclerView.Adapter<TestTopAdapter.MyViewHolder> {

    private Context mContext;
    private List<DepartmentMember> mDatas;

    private MyItemClickListener mListener;

    public TestTopAdapter(Context mContext, List<DepartmentMember> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_top_item, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
//			MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
//					TestActivity.this).inflate(R.layout.test_top_item, parent,
//					false));
        return holder;
    }

    //		@Override
//		public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//			return null;
//		}
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (mDatas.get(position).getUser().getGuarder().equals("delete")){
            holder.home_item_isdelete.setVisibility(View.VISIBLE);
        }else{
            holder.home_item_isdelete.setVisibility(View.GONE);
        }
        holder.tv.setText(mDatas.get(position).getUser().getNickname() + "");
        holder.iv.setImageUrl(mDatas.get(position).getUser().getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        RoundImageView iv;
        RoundImageView home_item_isdelete;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.test_top_item_name);
            iv = (RoundImageView) view.findViewById(R.id.test_top_item_icon);
            home_item_isdelete = (RoundImageView) view.findViewById(R.id.home_item_isdelete);

        }
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

}
