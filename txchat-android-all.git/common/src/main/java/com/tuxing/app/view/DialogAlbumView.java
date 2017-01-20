package com.tuxing.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuxing.app.R;


/**
 * DialogAlbumView
 *
 * @author liangyanqiao
 */
public class DialogAlbumView extends Dialog implements View.OnClickListener {

    private final View view;
    private OnDialogItemClick listener;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;

    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll3;
    private LinearLayout ll4;

    public DialogAlbumView(Context context) {
        this(context, R.style.transparentDialogViewStyle);
    }

    private DialogAlbumView(Context context, int defStyle) {
        super(context, defStyle);
        view = getLayoutInflater().inflate(R.layout.dialog_album_view, null);
        view.findViewById(R.id.ly_share_weichat_circle).setOnClickListener(this);
        view.findViewById(R.id.ly_share_weichat).setOnClickListener(this);
        view.findViewById(R.id.ly_share_qq).setOnClickListener(this);
        view.findViewById(R.id.ly_share_sina_weibo).setOnClickListener(this);
        view.findViewById(R.id.ly_share_copy_link).setOnClickListener(this);

        ll1 = (LinearLayout) view.findViewById(R.id.ll1);
        ll2 = (LinearLayout) view.findViewById(R.id.ll2);
        ll3 = (LinearLayout) view.findViewById(R.id.ll3);
        ll4 = (LinearLayout) view.findViewById(R.id.ll4);

        iv1 = (ImageView)view.findViewById(R.id.iv1);
        iv2 = (ImageView)view.findViewById(R.id.iv2);
        iv3 = (ImageView)view.findViewById(R.id.iv3);
        iv4 = (ImageView)view.findViewById(R.id.iv4);

        tv1 = (TextView)view.findViewById(R.id.tv1);
        tv2 = (TextView)view.findViewById(R.id.tv2);
        tv3 = (TextView)view.findViewById(R.id.tv3);
        tv4 = (TextView)view.findViewById(R.id.tv4);

        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll4.setOnClickListener(this);
        view.findViewById(R.id.cancel_tv).setOnClickListener(this);

        setContentView(view);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = d.getWidth();
        getWindow().setAttributes(params);
    }


    public void setOnDialogItemClick(OnDialogItemClick click) {
        listener = click;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.OnDialogItemClick(v.getId());
        }
    }

    public interface OnDialogItemClick {
        void OnDialogItemClick(int id);
    }

    public void setFunctionMenuVisible(String[] names,int[] iconIds) {
        switch (names.length){
            case 4:
                if(!TextUtils.isEmpty(names[3])) {
                    ll4.setVisibility(View.VISIBLE);
                    tv4.setText(names[3]);
                    iv4.setImageResource(iconIds[3]);
                }

            case 3:
                if(!TextUtils.isEmpty(names[2])) {
                    ll3.setVisibility(View.VISIBLE);
                    tv3.setText(names[2]);
                    iv3.setImageResource(iconIds[2]);
                }

            case 2:
                if(!TextUtils.isEmpty(names[1])) {
                    ll2.setVisibility(View.VISIBLE);
                    tv2.setText(names[1]);
                    iv2.setImageResource(iconIds[1]);
                }
            case 1:
                if(!TextUtils.isEmpty(names[0])){
                    ll1.setVisibility(View.VISIBLE);
                    tv1.setText(names[0]);
                    iv1.setImageResource(iconIds[0]);
                }
                break;

        }

//
//        view.findViewById(R.id.edit_ll).setVisibility(isEdit ? View.VISIBLE : View.GONE);
//        view.findViewById(R.id.save_img_ll).setVisibility(isSaveImg ? View.VISIBLE : View.GONE);
//        view.findViewById(R.id.refresh_ll).setVisibility(isReLoad ? View.VISIBLE : View.GONE);
//        view.findViewById(R.id.delete_img_ll).setVisibility(isDelete ? View.VISIBLE : View.GONE);
//        view.findViewById(R.id.use_safari_ll).setVisibility(isUseSafari ? View.VISIBLE : View.GONE);
    }

    /**
     * 是否显示 share 布局
     */
    public void setShareViewVisible(boolean shareViewVisible) {
        if (!shareViewVisible) {
            view.findViewById(R.id.share_ll).setVisibility(View.GONE);
            view.findViewById(R.id.line_view).setVisibility(View.GONE);
        }
    }
}
