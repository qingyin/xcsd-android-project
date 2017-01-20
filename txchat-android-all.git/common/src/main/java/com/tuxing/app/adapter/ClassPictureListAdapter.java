package com.tuxing.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.qzq.adapter.ClassPicGridViewListAdapter;
import com.tuxing.app.util.DateTimeUtils;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.sdk.db.entity.ClassPicture;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ClassPictureListAdapter extends ArrayAdapter<List<ClassPicture>> {

    private Context mContext;
    private List<List<ClassPicture>> list;
    private String uri;
    private User currentUser;
    private CoreService service;
    private long total;
    int totalInt = 0;
    public long lastPicId = 0;
    ArrayList<String> allUrlList = new ArrayList<String>();

    public ClassPictureListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ClassPictureListAdapter(Context _context, List<List<ClassPicture>> objects, CoreService txservice,long total) {
        super(_context, 0, objects);
        list = objects;
        mContext = _context;
        service = txservice;
        this.total = total;
        currentUser = service.getLoginManager().getCurrentUser();
    }

    public void setData(List<List<ClassPicture>> mlist,long total) {
        list.clear();
        allUrlList.clear();
        list.addAll(mlist);
        this.total = total;
        notifyDataSetChanged();
    }

    public ArrayList<String> getAllUrl(){
        if(list != null && list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                List<ClassPicture> pictureList = list.get(i);
                for(int j = 0; j < pictureList.size(); j++){
                    if(!allUrlList.contains(pictureList.get(j).getPicUrl())){
                        allUrlList.add(pictureList.get(j).getPicUrl());
                        lastPicId = pictureList.get(j).getPicId();
                    }
                }
            }
        }
        return allUrlList;
    }

    public long getLastId(){
        return lastPicId;
    }


    public int getTotal(){
        totalInt = Integer.valueOf(String.valueOf(total));
        return totalInt;
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.qinzi_class_picture_item, parent, false);
            holder.gv_img = (MyGridView) convertView.findViewById(R.id.gv_img);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ClassPicture classPicture;
        if(position < list.size()){
            List<ClassPicture> classPictureList = list.get(position);
            if(!CollectionUtils.isEmpty(classPictureList)){
                classPicture = classPictureList.get(0);
                if(classPicture!=null){
                    holder.tv_name.setText(DateTimeUtils.Date2YYYYMMDD_C(classPicture.getCreatedOn()));
                }
                holder.gv_img.setNumColumns(3);
                holder.gv_img.setVerticalSpacing(Utils.dip2px(mContext, 4));
                holder.gv_img.setHorizontalSpacing(Utils.dip2px(mContext, 4));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, Utils.dip2px(mContext, 10), 0, 0);
                holder.gv_img.setLayoutParams(lp);
                holder.gv_img.setAdapter(new ClassPicGridViewListAdapter(mContext, classPictureList, holder.gv_img, 0,ClassPictureListAdapter.this));
            }
        }
        return convertView;
    }

    class Holder {
        public MyGridView gv_img; // 如果是多张图片，用gridview展示
        public TextView tv_name; // 发消息人
    }
}
