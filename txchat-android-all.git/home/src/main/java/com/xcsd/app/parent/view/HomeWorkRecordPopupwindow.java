package com.xcsd.app.parent.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xcsd.app.parent.R;
import com.tuxing.app.util.TextUtils;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
//import com.xcsd.rpc.proto.HomeworkProto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shan on 2016/4/7.
 */
public class HomeWorkRecordPopupwindow extends PopupWindow {

    private Context mContext;
    private View root;
    private ListView gv;
    private OnItemClickWallpaperPopupWindowListener listener;
    private MyAdapter adapter = null;
    private int selectedPopup;

    private List<HomeWorkRecord> info = null;


    public HomeWorkRecordPopupwindow(Context context, View view, List<HomeWorkRecord> info0, int selectedPopup0) {
        super(context);
        info = info0;
        mContext = context;
        selectedPopup = selectedPopup0;
        if (info == null) {

            info = new ArrayList<HomeWorkRecord>();

        }
        root = LayoutInflater.from(context).inflate(R.layout.home_study_popupwindow_layout, null);
        gv = (ListView) root.findViewById(R.id.lv_homework_popup);
        adapter = new MyAdapter();
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                selectedPopup = position;
                adapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.OnItemClickPopupWindow(info.get(position), position);
                }
            }
        });
//		setBackgroundDrawable(new BitmapDrawable());
      //  setWidth(TextUtils.Dp2Px(mContext, 150));
        setWidth(Utils.getDisplayWidth(mContext));
        setHeight(TextUtils.Dp2Px(mContext, 250));
        setTouchable(true);
        setOutsideTouchable(true);
        setFocusable(true);
        setContentView(root);
		showAsDropDown(view);
       // showAsDropDown(view, TextUtils.Dp2Px(mContext, -50), 0);

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_study_popupwindow_layout_item, parent, false);
            }
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_popup_name);
            tv_name.setText(info.get(position).getTargetName());
            Resources resource = (Resources) mContext.getResources();
            ColorStateList white = (ColorStateList) resource.getColorStateList(R.color.white);
            ColorStateList black = (ColorStateList) resource.getColorStateList(R.color.black);

            if (position == selectedPopup) {
                tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.skin_text1));
                if (white != null) {
                    tv_name.setTextColor(white);
                }
            } else {
                tv_name.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                if (black != null) {
                    tv_name.setTextColor(black);
                }
            }
            return convertView;
        }
    }

    public interface OnItemClickWallpaperPopupWindowListener {
        void OnItemClickPopupWindow(HomeWorkRecord itemInfo, int selected);
    }
}
