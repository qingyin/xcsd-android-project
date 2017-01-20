package com.tuxing.app.qzq.view;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.tuxing.app.R;
import com.tuxing.app.easemob.util.SmileUtils;

public class MessageCollapsibleTextView extends LinearLayout implements OnClickListener {

	private  Context mContext;
	/** default text show max lines */
	private static final int DEFAULT_MAX_LINE_COUNT = 3;

	private static final int COLLAPSIBLE_STATE_NONE = 0;
	private static final int COLLAPSIBLE_STATE_SHRINKUP = 1;
	private static final int COLLAPSIBLE_STATE_SPREAD = 2;

	private TextView desc;
	private TextView descOp;

	private String shrinkup;
	private String spread;
	private int mState;
	private boolean flag;

	public MessageCollapsibleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		shrinkup = context.getString(R.string.collapse_more);
		spread = context.getString(R.string.show_all);
		View view = inflate(context, R.layout.qinzi_msg_list_collapsible_textview, this);
		view.setPadding(0, -1, 0, 0);
		desc = (TextView) view.findViewById(R.id.desc_tv);
		descOp = (TextView) view.findViewById(R.id.desc_op_tv);
		descOp.setOnClickListener(this);
	}

	public MessageCollapsibleTextView(Context context) {
		this(context, null);
	}

	public final void setDesc(CharSequence charSequence, BufferType bufferType) {
//		desc.setText(charSequence, bufferType);
		Spannable span = SmileUtils.getSmiledText(mContext, charSequence);
		// 设置内容
		desc.setText(span, BufferType.SPANNABLE);
		mState = COLLAPSIBLE_STATE_SPREAD;
		flag = false;
		requestLayout();
	}

	@Override
	public void onClick(View v) {
		flag = false;
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!flag) {
			flag = true;
			if (desc.getLineCount() <= DEFAULT_MAX_LINE_COUNT) {
				mState = COLLAPSIBLE_STATE_NONE;
				descOp.setVisibility(View.GONE);
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT + 1);
			} else {
				post(new InnerRunnable());
			}
		}
	}

	class InnerRunnable implements Runnable {
		@Override
		public void run() {
			if (mState == COLLAPSIBLE_STATE_SPREAD) {
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(spread);
				mState = COLLAPSIBLE_STATE_SHRINKUP;
			} else if (mState == COLLAPSIBLE_STATE_SHRINKUP) {
				desc.setMaxLines(Integer.MAX_VALUE);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(shrinkup);
				mState = COLLAPSIBLE_STATE_SPREAD;
			}
		}
	}
}
