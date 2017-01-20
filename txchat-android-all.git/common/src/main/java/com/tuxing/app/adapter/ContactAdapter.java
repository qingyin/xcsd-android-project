package com.tuxing.app.adapter;


import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Department;

import java.util.List;


/**
 * 联系人班级列表Adapter
 */
public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<Department> classList;

    public ContactAdapter(Context context, List<Department> classList) {
        this.context = context;
        this.classList = classList;
    }
    public void setData(List<Department> list){
        this.classList = list;
        notifyDataSetChanged();
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            try {
                super.unregisterDataSetObserver(observer);
            }catch (Exception e){
            }
        }
    }

    @Override
    public int getCount() {
        return classList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return classList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoler = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_item_layout, null);
            viewHoler = new ViewHolder();
            viewHoler.classIcon = (RoundImageView) convertView.findViewById(R.id.contact_item_icon);
            viewHoler.className = (TextView) convertView.findViewById(R.id.contact_item_name);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHolder) convertView.getTag();
        }

        viewHoler.classIcon.setImageUrl(classList.get(position).getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
        viewHoler.className.setText(classList.get(position).getName());
        return convertView;
    }


    public class ViewHolder {
        public RoundImageView classIcon;
        public TextView className;
    }
}
