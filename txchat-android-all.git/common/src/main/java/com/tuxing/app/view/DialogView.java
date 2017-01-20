package com.tuxing.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.tuxing.app.R;
import com.tuxing.app.util.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * dialog
 * @author liangyanqiao
 */
public class DialogView extends Dialog implements View.OnClickListener {

    private static String[] btnNames;
    private final Button btn1;
    private final Button btn2;
    private final Button btn3;
    private final Button btn4;
    private final Button btn5;
    private final Button btn6;
    private final Context context;
    private OnDialogItemClick listener;

    public void setOnDialogItemClick(OnDialogItemClick click) {
        listener = click;
    }

    public DialogView(Context context) {
        this(context, R.style.transparentDialogViewStyle);
    }

    private DialogView(Context context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View view = getLayoutInflater().inflate(
                R.layout.dialog_view, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        view.setLayoutParams(params);
        List<Button> btnList = new ArrayList<>();
        btnList.clear();
        btn1 = (Button) view.findViewById(R.id.btn_dialog1);
        btn2 = (Button) view.findViewById(R.id.btn_dialog2);
        btn3 = (Button) view.findViewById(R.id.btn_dialog3);
        btn4 = (Button) view.findViewById(R.id.btn_dialog4);
        btn5 = (Button) view.findViewById(R.id.btn_dialog5);
        btn6 = (Button) view.findViewById(R.id.btn_dialog6);
        btnNames = getBtnNames();
        if (btnNames != null) {
            switch (btnNames.length) {
                case 6:
                    btn6.setVisibility(View.VISIBLE);
                    btn6.setText(btnNames[5]);
                    btn6.setOnClickListener(this);
                    btnList.add(btn6);
                case 5:
                    btn5.setVisibility(View.VISIBLE);
                    btn5.setText(btnNames[4]);
                    btn5.setOnClickListener(this);
                    btnList.add(btn5);
                case 4:
                    btn4.setVisibility(View.VISIBLE);
                    btn4.setText(btnNames[3]);
                    btn4.setOnClickListener(this);
                    btnList.add(btn4);
                case 3:
                    btn3.setVisibility(View.VISIBLE);
                    btn3.setText(btnNames[2]);
                    btn3.setOnClickListener(this);
                    btnList.add(btn3);
                case 2:
                    btn2.setVisibility(View.VISIBLE);
                    btn2.setText(btnNames[1]);
                    btn2.setOnClickListener(this);
                    btnList.add(btn2);
                case 1:
                    btn1.setVisibility(View.VISIBLE);
                    btn1.setText(btnNames[0]);
                    btn1.setOnClickListener(this);
                    btnList.add(btn1);
                default:
                    break;
            }
        }
        btnList.get(0).setTextColor(context.getResources().getColor(R.color.text_gray));
        setContentView(view);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = d.getWidth() - Utils.dip2px(context, 40f);
        getWindow().setAttributes(params);
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

    public static void setBtnNames(String[] names) {
        btnNames = null;
        btnNames = names;
    }

    public String[] getBtnNames() {
        return btnNames;
    }
}
