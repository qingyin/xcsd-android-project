package com.xcsd.app.parent.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.event.CheckInCardEvent;
import com.tuxing.sdk.modle.CheckInCard;
import com.tuxing.sdk.modle.Relative;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class AddCloudCardrActivity extends BaseActivity {

	private EditText editCard;
	private TextView add_card_name;
	private TextView textNum0;
	private TextView textNum1;
	private TextView textNum2;
	private TextView textCard;
	private Long userId = 0l;
	private Long childUserId = 0l;
	private Button bindCard;
	private Button btn0;
	private Button btn1;
	private Button btn2;
	private RelativeLayout editRel;
	private RelativeLayout textRel0;
	private RelativeLayout textRel1;
	private RelativeLayout textRel2;
	private ArrayList<String> cardnNums;
	private List<CheckInCard> cards;
	private String TAG = AddCloudCardrActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.my_card_number_add_layout);
		init();
		initData();
	}

	private void initData() {
			if(cardnNums != null){
				editTextIsEnable(cardnNums);
			}else{
				editRel.setVisibility(View.VISIBLE);
				textCard.setVisibility(View.GONE);
				textRel0.setVisibility(View.GONE);
				textRel1.setVisibility(View.GONE);
				textRel2.setVisibility(View.GONE);
			}
	}

	private void init() {
		setTitle("绑定卡号");
		setLeftBack(getString(R.string.btn_cancel), false, false);
		setRightNext(true, "", 0);
		editCard = (EditText) findViewById(R.id.et_card_number);
		add_card_name = (TextView) findViewById(R.id.add_card_name);
		textCard = (TextView) findViewById(R.id.tv_bind_card);
		textNum0 = (TextView) findViewById(R.id.tv_card_number_0);
		textNum1 = (TextView) findViewById(R.id.tv_card_number_1);
		textNum2 = (TextView) findViewById(R.id.tv_card_number_2);
		bindCard = (Button) findViewById(R.id.btn_cloud);
		btn0 = (Button) findViewById(R.id.btn_cloud_0);
		btn1 = (Button) findViewById(R.id.btn_cloud_1);
		btn2 = (Button) findViewById(R.id.btn_cloud_2);
		editRel = (RelativeLayout)findViewById(R.id.rl_edit);
		textRel0 = (RelativeLayout)findViewById(R.id.rl_card_0);
		textRel1 = (RelativeLayout)findViewById(R.id.rl_card_1);
		textRel2 = (RelativeLayout)findViewById(R.id.rl_card_2);
		bindCard.setOnClickListener(this);
		btn0.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		if(user != null){
			childUserId = user.getChildUserId();
			userId = user.getUserId();
		}
		cards = new ArrayList<CheckInCard>();
		cardnNums =  (ArrayList<String>)getIntent().getSerializableExtra("cardnNums");
	}

	public void onEventMainThread(CheckInCardEvent event) {

		switch (event.getEvent()) {
		case CARD_BIND_SUCCESS:
			getService().getUserManager().getBindCard();
			showAndSaveLog(TAG, "绑定卡号成功  --", false);
			break;
		case CARD_BIND_FAILED:
			disProgressDialog();
			showAndSaveLog(TAG, "绑定卡号失败   --msg = " + event.getMsg(), false);
			showToast(event.getMsg());
			break;
		case CARD_UNBIND_SUCCESS:
			getService().getUserManager().getBindCard();
			showAndSaveLog(TAG, "解绑成功     --msg = " + event.getMsg(), false);
			showToast("解绑成功");
			break;
		case CARD_UNBIND_FAILED:
			disProgressDialog();
			showAndSaveLog(TAG, "解绑失败   --msg = " + event.getMsg(), false);
			showToast(event.getMsg());
			break;
		case CARD_REQUEST_SUCCESS:
			disProgressDialog();
			cardnNums.clear();
			cards  = event.getCards();
			if(cards != null){
				for(int i = 0; i < cards.size(); i++){
					if(cards.get(i).getUserId().longValue() == userId.longValue() && !TextUtils.isEmpty(cards.get(i).getCardNum())){
						cardnNums.add(cards.get(i).getCardNum());

					}
				}
				initData();
				showAndSaveLog(TAG, "获取绑定卡号成功  --size = " + cards .size(), false);
			}
			break;
		case CARD_REQUEST_FAILED:
			disProgressDialog();
			showToast(event.getMsg());
			showAndSaveLog(TAG, "获取绑定卡号失败   --" + event.getMsg(), false);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_cloud:
			btnAddOrLoss(true,textNum0);
			break;
		case R.id.btn_cloud_0:
			btnAddOrLoss(false,textNum0);
			break;
		case R.id.btn_cloud_1:
			btnAddOrLoss(false,textNum1);
			break;
		case R.id.btn_cloud_2:
			btnAddOrLoss(false,textNum2);
			break;
		}
		super.onClick(v);
	}


	public void btnAddOrLoss(boolean isSave,TextView textCard) {
		if (isSave) {
			String bindData = editCard.getText().toString().trim();
			editCard.setText("");
			if (!TextUtils.isEmpty(bindData) || bindData.length() == 8) {
				showProgressDialog(mContext, "", true, null);
				getService().getCheckInManager().bindCheckInCard(bindData, childUserId);
			} else {
				showToast("请输入8位卡号");
			}
			MobclickAgent.onEvent(mContext, "my_card_bound", UmengData.my_card_bound);
		} else {
			String unbindData = textCard.getText().toString();
			showProgressDialog(mContext, "", true, null);
			getService().getUserManager().unbindCheckInCard(unbindData);
			MobclickAgent.onEvent(mContext,"my_card_remove",UmengData.my_card_remove);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (editNum0.getText().toString().trim().length() > 0) {
//				showDialog(null, "您确定要放弃此编辑吗？", getString(R.string.cancel),
//						getString(R.string.ok));
//			} else {
//				finish();
//			}
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}

	public void editTextIsEnable(List<String> cardNums) {
			if (cardNums.size() == 3) {
				textCard.setVisibility(View.VISIBLE);
				editRel.setVisibility(View.GONE);
				add_card_name.setVisibility(View.GONE);
				textRel0.setVisibility(View.VISIBLE);
				textRel1.setVisibility(View.VISIBLE);
				textRel2.setVisibility(View.VISIBLE);
				textNum0.setText(cardNums.get(0));
				textNum1.setText(cardNums.get(1));
				textNum2.setText(cardNums.get(2));
			} else if (cardNums.size() == 2) {
				textCard.setVisibility(View.VISIBLE);
				editRel.setVisibility(View.VISIBLE);
				add_card_name.setVisibility(View.VISIBLE);
				textRel0.setVisibility(View.VISIBLE);
				textRel1.setVisibility(View.VISIBLE);
				textRel2.setVisibility(View.GONE);
				textNum0.setText(cardNums.get(0));
				textNum1.setText(cardNums.get(1));
			} else if (cardNums.size() == 1) {
				textCard.setVisibility(View.VISIBLE);
				editRel.setVisibility(View.VISIBLE);
				add_card_name.setVisibility(View.VISIBLE);
				textRel0.setVisibility(View.VISIBLE);
				textRel1.setVisibility(View.GONE);
				textRel2.setVisibility(View.GONE);
				textNum0.setText(cardNums.get(0));
			} else {
				editRel.setVisibility(View.VISIBLE);
				add_card_name.setVisibility(View.VISIBLE);
				textCard.setVisibility(View.GONE);
				textRel0.setVisibility(View.GONE);
				textRel1.setVisibility(View.GONE);
				textRel2.setVisibility(View.GONE);
			}

	}
}
