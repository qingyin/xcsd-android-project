package com.xcsd.app.teacher.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.TextUtils;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.Department;

import java.util.ArrayList;
import java.util.List;

public class ContactParentPopupwindow extends PopupWindow {

    private Context mContext;
    private View root;
    private ListView gv;
    private OnItemClickWallpaperPopupWindowListener listener;
    private MyAdapter adapter = null;
    private int selectedPopup;
    private List<Department> info = null;

    public ContactParentPopupwindow(Context context, View view, List<Department> info0, int selectedPopup0) {
        super(context);
        info = info0;
        mContext = context;
        selectedPopup = selectedPopup0;
        if (info == null)
            info = new ArrayList<Department>();
        root = LayoutInflater.from(context).inflate(R.layout.contact_popup_list, null);
        gv = (ListView) root.findViewById(R.id.lv_popup);
        adapter = new MyAdapter();
        gv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                selectedPopup = position;
                adapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.OnItemClickPopupWindow(info.get(position), position);
                }
                dismiss();
            }
        });

		setBackgroundDrawable(new BitmapDrawable());
//        setWidth(TextUtils.Dp2Px(mContext, 150));
        setWidth(Utils.getDisplayWidth(mContext));
//        setHeight(TextUtils.Dp2Px(mContext, 250));
        setHeight(TextUtils.Dp2Px(mContext, 126));
        setTouchable(true);
        setOutsideTouchable(true);
        setFocusable(true);
        setContentView(root);
//		showAsDropDown(view);
        showAsDropDown(view, TextUtils.Dp2Px(mContext, -50), 0);
    }

    public void setOnItemClickPopupWindowListener(OnItemClickWallpaperPopupWindowListener listener0) {
        listener = listener0;
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return info.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_popup_list_item, parent, false);
            }
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_popup_name);
            tv_name.setText(info.get(position).getName());
            Resources resource = (Resources) mContext.getResources();
            ColorStateList white = (ColorStateList) resource.getColorStateList(R.color.white);
            ColorStateList black = (ColorStateList) resource.getColorStateList(R.color.black);

            if (position == selectedPopup) {
                tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.btn_blue_normal));
                if(white!=null){
                    tv_name.setTextColor(white);
                }
            } else {
                tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                if(black!=null){
                    tv_name.setTextColor(black);
                }
            }
            return convertView;
        }
    }

    public interface OnItemClickWallpaperPopupWindowListener {
        void OnItemClickPopupWindow(Department itemInfo, int selected);
    }
}
