package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.CardNumberAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.event.CheckInCardEvent;
import com.tuxing.sdk.modle.CheckInCard;

import java.util.ArrayList;
import java.util.List;


public class MyCloudCardActivity extends BaseActivity  implements AdapterView.OnItemClickListener{

	private ListView cloudView;
	private CardNumberAdapter adapter;
	private String TAG = MyCloudCardActivity.class.getSimpleName();
	private List<CheckInCard> cards;
	private List<CheckInCard> tempCards = new ArrayList<CheckInCard>();
	private long userId = 0;
	private int index = 0;

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

		cards = new ArrayList<CheckInCard>();
		if(user != null)
			userId = user.getUserId();
		cloudView = (ListView) findViewById(R.id.my_cardnumber_list);
		cloudView.setOnItemClickListener(this);
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
					tempCards  = event.getCards();
					if(tempCards != null && tempCards.size() > 0){
						cards.clear();
						for(int i = 0; i < tempCards.size(); i++){
							if(tempCards.get(i).getUserId() == userId){
								cards.add(0,tempCards.get(i));
							}else{
								cards.add(tempCards.get(i));
							}
						}
						updateAdapter(cards);
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

	public void updateAdapter(List<CheckInCard> datas){
		if(adapter == null){
			adapter = new CardNumberAdapter(mContext,datas,userId);
			cloudView.setAdapter(adapter);
		}else{
			adapter.setList(datas);
			adapter.notifyDataSetChanged();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		if(cards.get(i) != null && cards.get(i).getUserId() == userId){
			Intent intent = new Intent(mContext,AddCloudCardrActivity.class);
			intent.putExtra("name", cards.get(i).getUserName());
			if(!TextUtils.isEmpty(cards.get(i).getCardNum())){
				intent.putExtra("cardNumber", cards.get(i).getCardNum());
			}else{
				intent.putExtra("cardNumber", "");
			}
			openActivityOrFragment(intent);
		}}
}
