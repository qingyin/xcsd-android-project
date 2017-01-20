package com.tuxing.app.qzq.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.tuxing.app.R;
import com.tuxing.app.qzq.MyCircleListActivity;
import com.tuxing.sdk.db.entity.User;

public class TextUnderlineClickSpan extends ClickableSpan {

	private boolean isShowUnderline = false;
	private int mTextHexColor = 0xff7f93bd;
	private User mUser;
	private Context mContext;

	public TextUnderlineClickSpan(User user, Context context) {
		this.mUser = user;
		this.mContext = context;
	}

	public TextUnderlineClickSpan() {

	}

	@Override
	public void onClick(View widget) {
		// TODO Auto-generated method stub
		if (mUser == null) {

		} else {
			Intent intent = new Intent(mContext, MyCircleListActivity.class);
			intent.putExtra("userId", mUser.getUserId());
			intent.putExtra("userName", mUser.getNickname());
			mContext.startActivity(intent);
		}
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		// TODO Auto-generated method stub
		if (mUser != null) {
			ds.setColor(mContext.getResources().getColor(R.color.qzq_name));//Color.parseColor("#C6C654")
		} else {
		}
		ds.setUnderlineText(isShowUnderline); // 去掉下划线
	}

}
