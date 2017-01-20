package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.IdentityAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.event.RelativeEvent;
import com.tuxing.sdk.modle.Relative;
import com.tuxing.sdk.utils.Constants.RELATIVE_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectIdentityActivity extends BaseActivity {

	private ListView identityView;
	private IdentityAdapter adapter;
	private String TAG = SelectIdentityActivity.class.getSimpleName();
	private Map<Integer,String> nameMap;
	private List<Relative> relatives;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.identity_layout);
		showProgressDialog(mContext, "", true, null);
		getService().getRelativeManager().getRelativeList();
		init();
	}
	
	private void init() {
		setTitle(getString(R.string.me_baby));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		nameMap = new HashMap<Integer,String>();
		relatives = new ArrayList<Relative>();
		nameMap.put(RELATIVE_TYPE.FATHER, "爸爸");
		nameMap.put(RELATIVE_TYPE.MOTHER, "妈妈");
		nameMap.put(RELATIVE_TYPE.PATERNAL_GRANDFATHER, "爷爷");
		nameMap.put(RELATIVE_TYPE.PATERNAL_GRANDMOTHER, "奶奶");
		nameMap.put(RELATIVE_TYPE.MATERNAL_GRANDFATHER, "姥爷");
		nameMap.put(RELATIVE_TYPE.MATERNAL_GRANDMOTHER, "姥姥");
		nameMap.put(RELATIVE_TYPE.OTHER, "亲属");
		identityView = (ListView) findViewById(R.id.identity_lv);
	}
	
	 public void onEventMainThread(RelativeEvent event) {
	        disProgressDialog();
	        switch (event.getEvent()) {
	            case GET_RELATIVE_SUCCESS:
	                relatives = event.getRelatives();
	                if(relatives != null){
	                	updateAdapter();
	                }
	                disProgressDialog();
	                showAndSaveLog(TAG, "获取亲属列表成功 " , false);
	                break;
	            case GET_RELATIVE_FAILED:
	            	disProgressDialog();
	                showAndSaveLog(TAG, "获取亲属列表失败 msg = " + event.getMsg(), false);
	                break;
	        }
	    }
	
	
	 
	public void updateAdapter(){
		if(adapter == null){
			adapter = new IdentityAdapter(mContext,relatives,nameMap);
			identityView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void finish() {
		if(relatives != null && relatives.size() > 0 ){
		int index = adapter.selectIndex();
		if(index != 100){
			Intent mIntent = new Intent();  
			mIntent.putExtra("selectName", nameMap.get(relatives.get(index).getRelativeType()));  
			mIntent.putExtra("selectNameId", relatives.get(index).getRelativeType());  
			this.setResult(RESULT_OK, mIntent);  
		}}
	       super.finish();
	}
	
}
