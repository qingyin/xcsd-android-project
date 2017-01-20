package com.xcsd.app.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.CardNumberAdapter;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.event.CheckInCardEvent;
import com.tuxing.sdk.modle.CheckInCard;

import java.util.ArrayList;
import java.util.List;


public class MyCloudCardActivity extends BaseActivity {

	private ListView cloudView;
	private CardNumberAdapter adapter;
	private String TAG = MyCloudCardActivity.class.getSimpleName();
	private List<CheckInCard> cards;
	private List<CheckInCard> otherCards;
	private ArrayList<String> cardnNums = new ArrayList<String>();
	private long userId;
	private int index = 0;
	private View headVIew;
	private TextView name;
	private TextView cardNumber;
	private ImageView arrow;
	private RoundImageView icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.my_card_number_layout);
		init();
		
	}
	
	private void init() {
		setTitle(getString(R.string.cloudcard_title));
		setLeftBack("", true, false);
		setRightNext(false, "", 0);
		headVIew = LayoutInflater.from(mContext).inflate(R.layout.listview_item4_layout, null);

		name = (TextView) headVIew.findViewById(R.id.item4_name);
		cardNumber = (TextView) headVIew.findViewById(R.id.item4_card_number);
		arrow = (ImageView) headVIew.findViewById(R.id.item4_arrow);
		icon = (RoundImageView) headVIew.findViewById(R.id.item4_icon);
		cards = new ArrayList<CheckInCard>();
		otherCards = new ArrayList<CheckInCard>();
		if(user != null)
		userId = user.getUserId();
		cloudView = (ListView) findViewById(R.id.my_cardnumber_list);
		headVIew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AddCloudCardrActivity.class);
				intent.putExtra("name", name.getText().toString());
				intent.putExtra("cardnNums", cardnNums);
				openActivityOrFragment(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		isActivity = true;
		initData();
		super.onResume();
	}
	private void initData() {
		//TODO 获取卡号信息
		showProgressDialog(mContext, "", true, null);
		getService().getUserManager().getBindCard();
	}
	
	 public void onEventMainThread(CheckInCardEvent event){
		 if(isActivity){
			 
	
		 switch (event.getEvent()) {
		 case CARD_REQUEST_SUCCESS:
				disProgressDialog();
			cards  = event.getCards();
			if(cards != null){
				updateAdapter();
				showAndSaveLog(TAG, "获取绑定卡号成功  --size = " + cards .size(), false);
			}
				break;
		 case CARD_REQUEST_FAILED:
			 disProgressDialog();
			 showToast(event.getMsg());
			 showAndSaveLog(TAG, "获取绑定卡号失败   --" + event.getMsg(), false);
			 break;
		 }
	 }	 }
	 
	public void updateAdapter(){
		upadateView();
		if(adapter == null){
			cloudView.addHeaderView(headVIew);
			adapter = new CardNumberAdapter(mContext,otherCards,userId);
			cloudView.setAdapter(adapter);
		}else{
			adapter.setList(otherCards);
			adapter.notifyDataSetChanged();
		}
	}
	

	private void upadateView() {
		cardnNums.clear();
		otherCards.clear();
		for(int i = 0; i < cards.size(); i++){
			if(cards.get(i).getUserId() == userId){
				name.setText(cards.get(i).getUserName());
				if(!TextUtils.isEmpty(cards.get(i).getCardNum())){
					cardNumber.setText(mContext.getString(R.string.has_bind));
					cardnNums.add(cards.get(i).getCardNum());
				}else{
					cardNumber.setText(mContext.getString(R.string.no_bind));
				}
				arrow.setVisibility(View.VISIBLE);
				icon.setImageUrl(cards.get(i).getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_info_image);
			}else{
				otherCards.add(cards.get(i));
			}
		}
	}

}
