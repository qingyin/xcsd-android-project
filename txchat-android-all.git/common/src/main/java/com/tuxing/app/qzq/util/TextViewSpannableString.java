package com.tuxing.app.qzq.util;

import android.content.Context;
import android.content.Intent;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.qzq.MyCircleListActivity;
import com.tuxing.app.util.ExpressionUtil;

public class TextViewSpannableString extends TextView {
	private Context mContext;
	private boolean mDontConsumeNonUrlClicks = true;
	private boolean mLinkHit;

	public TextViewSpannableString(Context context) {
		super(context);
		init(context);
	}

	public TextViewSpannableString(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TextViewSpannableString(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
	}

	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		if (mDontConsumeNonUrlClicks) {
			return super.performClick();
		}
		return false;
	}

	@Override
	public boolean hasFocusable() {
		return false;
	}

	public void updateUI(String authorUser, long userId,String replyUser,long replyUserId, String text) {

		setText("");
		setTextViewSpan(authorUser,userId);
		if (replyUser != null) {
			append("回复");
			setTextViewSpan(replyUser,replyUserId);
		}
		append(":");
		setContent(text);
	}
	public void updateUITwo(String authorUser,long userId, String replyUser,long replyUserId, String text) {

		setText("");
//		setTextViewSpan(authorUser);
		if (replyUser != null) {
			append("回复");
			setTextViewSpan(replyUser,replyUserId);
		}
		setContent(text);
	}
	private void setContent(String text) {
		String faceCode = "\\[[\u4E00-\u9FFF]+\\]";
		int size = mContext.getResources().getDimensionPixelSize(R.dimen.dp_18);
		final SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, text, faceCode, size);
		append(spannableString);

//		Spannable span = SmileUtils.getSmiledText(mContext, text);
		// 设置内容
//		setText(span, BufferType.SPANNABLE);
//		append(span);
	}

	private void setTextViewSpan(String user,long userid) {
		SpannableString spStr = new SpannableString(user);
		LocalClickSpan clickSpan = new LocalClickSpan(user,userid);
		spStr.setSpan(clickSpan, 0, user.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		append(spStr);
		setMovementMethod(LocalLinkMovementMethod.getInstance());
	}

	public class LocalClickSpan extends ClickableSpan {

		private boolean isShowUnderline = false;
		private String mUser;
		private long mUserid;

		public LocalClickSpan(String user,long userid) {
			this.mUser = user;
			this.mUserid = userid;
		}

		public LocalClickSpan() {

		}

		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
			if (mUser == null) {
			} else {
				////跳到个人详情页
//				Intent intent = new Intent(mContext, MessageFriendsListActivity.class);
//				intent.putExtra("id", mUser.getUserId());
//				mContext.startActivity(intent);
				Intent intent = new Intent(mContext, MyCircleListActivity.class);
				intent.putExtra("userId", mUserid);
				intent.putExtra("userName", mUser);
				mContext.startActivity(intent);
			}
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			// TODO Auto-generated method stub
			if (mUser != null) {
				ds.setColor(mContext.getResources().getColor(R.color.skin_text1));//Color.parseColor("#CDCF8D")
			} else {
			}
			ds.setUnderlineText(isShowUnderline); // 去掉下划线
		}

	}

	public static class LocalLinkMovementMethod extends LinkMovementMethod {
		static LocalLinkMovementMethod sInstance;

		public static LocalLinkMovementMethod getInstance() {
			if (sInstance == null)
				sInstance = new LocalLinkMovementMethod();

			return sInstance;
		}

		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
					}

					if (widget instanceof TextViewSpannableString) {
						((TextViewSpannableString) widget).mDontConsumeNonUrlClicks = false;
						((TextViewSpannableString) widget).mLinkHit = true;
					}
					Selection.removeSelection(buffer);// 去除选择背景
					return true;
				} else {
					if (widget instanceof TextViewSpannableString) {
						((TextViewSpannableString) widget).mDontConsumeNonUrlClicks = true;
					}

					Selection.removeSelection(buffer);
					return Touch.onTouchEvent(widget, buffer, event);
				}
			}
			return Touch.onTouchEvent(widget, buffer, event);
		}
	}

}
