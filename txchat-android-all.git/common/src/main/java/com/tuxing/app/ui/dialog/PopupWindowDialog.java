package com.tuxing.app.ui.dialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.tuxing.app.R;

public class PopupWindowDialog extends Dialog {
	public OnClickListener listener;
	protected FrameLayout container;
	protected View content;
	private final int contentPadding = 0;
	private ListView listview;
	private Context mContext;

	protected OnClickListener dismissClick = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};

	public PopupWindowDialog(Context context) {
		this(context, R.style.dialog_common);
		this.mContext = context;
	}

	public PopupWindowDialog(Context context, int defStyle) {
		super(context, defStyle);
//		contentPadding = (int) getContext().getResources().getDimension(
//				R.dimen.global_dialog_padding);
		init(context);
		this.mContext = context;
	}

	protected PopupWindowDialog(Context context, boolean flag,
								OnCancelListener listener) {
		super(context, flag, listener);
//		contentPadding = (int) getContext().getResources().getDimension(
//				R.dimen.global_dialog_padding);
		init(context);
		this.mContext = context;
	}

	@SuppressLint("InflateParams")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void init(final Context context) {
		setCancelable(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		content = LayoutInflater.from(context).inflate(
				R.layout.dialog_common_pop, null);
		container = (FrameLayout) content.findViewById(R.id.content_container);
		super.setContentView(content);
	}
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.dismiss();
	}

	public void setContent(View view) {
		setContent(view, contentPadding);
	}

	public void setContent(View view, int padding) {
		container.removeAllViews();
		container.setPadding(padding, padding, padding, padding);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		container.addView(view, lp);
	}

	@Override
	public void setContentView(int i) {
		setContent(null);
	}

	@Override
	public void setContentView(View view) {
		setContentView(null, null);
	}

	@Override
	public void setContentView(View view,
			LayoutParams layoutparams) {
		throw new Error("Dialog: User setContent (View view) instead!");
	}

	public void setItemsWithoutChk(CharSequence[] items,
			AdapterView.OnItemClickListener onItemClickListener,int selectIndex) {
		listview = new ListView(content.getContext());
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(0,0,0,0);
		listview.setLayoutParams(lp);
		PopDialogAdapter adapter = new PopDialogAdapter(mContext,items,selectIndex);
		adapter.setShowChk(false);
		listview.setDivider(null);
		listview.setVerticalScrollBarEnabled(false);
		listview.setAdapter(adapter);
//		listview.setBackgroundDrawable(content.getContext().getResources().getDrawable(R.drawable.contact_pop_bg));
		listview.setOnItemClickListener(onItemClickListener);
		setContent(listview, 0);
	}
	@Override
	public void setTitle(int title) {
		setTitle((getContext().getResources().getString(title)));
	}
	public void setSelectIndex(int index){
		if(listview.getAdapter().getCount()>=index){
			listview.setSelection(index);
		}else
			listview.setSelection(0);
	}


}
