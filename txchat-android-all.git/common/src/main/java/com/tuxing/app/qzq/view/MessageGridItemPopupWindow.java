package com.tuxing.app.qzq.view;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.PopupWindow;
import com.tuxing.app.R;

public class MessageGridItemPopupWindow extends PopupWindow {

	public MessageGridItemPopupWindow(View view){
		setContentView(view);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setAnimationStyle(R.style.msg_list_grid_animation);
		setBackgroundDrawable(new ColorDrawable());
	}
}
