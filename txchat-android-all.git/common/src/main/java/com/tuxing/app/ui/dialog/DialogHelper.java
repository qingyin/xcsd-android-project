package com.tuxing.app.ui.dialog;

import android.content.Context;
import com.tuxing.app.R;

public class DialogHelper {
	
	public static CommonDialog getPinterestDialog(Context context) {
		return new CommonDialog(context, R.style.dialog_common);
	}

	public static CommonDialog getPinterestDialogCancelable(Context context) {
		CommonDialog dialog = new CommonDialog(context,
				R.style.dialog_common);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
	public static PopupWindowDialog getPopDialogCancelable(Context context) {
		PopupWindowDialog dialog = new PopupWindowDialog(context,
				R.style.dialog_common_pop);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	public static CommonDialogReward getPinterestDialogCancelableReward(Context context) {
		CommonDialogReward dialog = new CommonDialogReward(context,
				R.style.dialog_common_reward);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
}
