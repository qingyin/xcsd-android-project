package com.tuxing.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.tuxing.app.R;
import com.tuxing.app.qzq.ParentCircleActivity;
import com.tuxing.sdk.db.entity.Feed;
import java.util.List;


public class CircleNoDataAdapter extends ArrayAdapter<Feed> {
    private Context mContext;
    private ParentCircleActivity activity;

    public CircleNoDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CircleNoDataAdapter(Context _context, List<Feed> objects,ParentCircleActivity activity) {
        super(_context, 0, objects);
        mContext = _context;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_no_data_item, parent, false);
        ImageView qzq_bg = (ImageView)convertView.findViewById(R.id.qzq_bg);
        qzq_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onclickRightImg();
            }
        });
        return convertView;
    }
}