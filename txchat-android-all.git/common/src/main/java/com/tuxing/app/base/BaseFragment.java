package com.tuxing.app.base;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.tuxing.app.LoginActivity;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.view.ProgressHUD;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.facade.CoreService;
import com.umeng.analytics.MobclickAgent;
import com.tuxing.sdk.utils.Constants;

public abstract class BaseFragment extends Fragment implements OnClickListener{

	protected NotificationManager notificationManager;
	private static final int notifiId = 11;
	public InputMethodManager manager;
	public TextView tv_title;
	public LinearLayout ll_left;//左边layout
	public TextView tv_left_text;//左边按钮
	public ProgressBar pullProgressBar;
	public TextView tv_left;//左标题

	public TextView tv_right;//右标题
	public ImageView iv_right;//右图标
	public ImageView iv_left;//左图标
	public LinearLayout ll_left_img;
	public LinearLayout ll_right;

	public LinearLayout llContent;
	public View contentView;
	public User currentUser;
	private ProgressHUD progressDialog;
	//qzq
	private RelativeLayout rl_title_bar;
	private  String muteGroupIds = "";//禁言群组ids
	private String[] btnNams;
	private Dialog bottomDialog;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public void initView() {
		tv_title = (TextView)getActivity().findViewById(R.id.tv_title);//标题
		ll_left = (LinearLayout)getActivity().findViewById(R.id.ll_left);
		tv_left = (TextView)getActivity().findViewById(R.id.tv_left);
		tv_left_text = (TextView ) getActivity().findViewById(R.id.tv_left_text);//返回图标
		tv_right = (TextView) getActivity().findViewById(R.id.tv_right);//右边文字,如下一步 确定
		iv_right = (ImageView)getActivity().findViewById(R.id.iv_right);//右边图标
		ll_right = (LinearLayout)getActivity().findViewById(R.id.ll_right);
		iv_right = (ImageView)getActivity().findViewById(R.id.iv_right);
		ll_left_img = (LinearLayout)getActivity().findViewById(R.id.ll_left_img);
		iv_left = (ImageView)getActivity().findViewById(R.id.iv_left);
		//qzq
		rl_title_bar = (RelativeLayout)getActivity().findViewById(R.id.rl_title_bar);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		initView();
		notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		currentUser = getService().getLoginManager().getCurrentUser();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public CoreService getService(){
		return CoreService.getInstance();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onResume() {
		if(getService().getLoginManager().getCurrentUser() == null){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			getActivity().finish();
		}

		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(getActivity());
		MobclickAgent.onPageEnd("我的界面");
	}

	public void setTitle(String title){
		if(tv_title != null) {
			tv_title.setText(title);
		}
	}
	public void setLeftBack(String text,boolean isBack){
		ll_left_img.setVisibility(View.GONE);
		if(isBack){
			if (null != tv_left_text) {
				tv_left_text.setVisibility(View.GONE);
			}
			if (null != tv_left) {
				tv_left.setVisibility(View.VISIBLE);
			}
			ll_left.setVisibility(View.VISIBLE);
			ll_left.setOnClickListener(this);
			ll_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().onBackPressed();//返回
				}
			});
		}else{//取消
			if("".equals(text)){
				tv_left.setVisibility(View.GONE);
				tv_left_text.setVisibility(View.GONE);
			}else{
				if (null != tv_left) {
					tv_left.setVisibility(View.GONE);
				}
				if(null!=tv_left_text){
					tv_left_text.setVisibility(View.VISIBLE);
					tv_left_text.setText(text);
					ll_left.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							getActivity().onBackPressed();//返回
						}
					});
				}
			}
		}
	}
	public void setLeftBack(int resId){
		tv_left_text.setVisibility(View.GONE);
		tv_left.setVisibility(View.GONE);
		ll_left_img.setVisibility(View.VISIBLE);
		iv_left.setImageResource(resId);
		ll_left_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onclickLeftImg();
			}
		});
	}
	public void setRightNext(boolean isText,String text,int resId){
//		tv_left.setTextColor(getResources().getColor(R.color.black));
//		rl_title_bar.setBackgroundColor(Color.argb(0,255,255,255));
////		rl_title_bar.setBackground(getResources().getDrawable(R.drawable.qzq_title_bar_bg));
//		tv_title.setTextColor(getResources().getColor(R.color.black));
		if(isText){//例如 下一步
			if("".equals(text)){
				ll_right.setVisibility(View.GONE);
				iv_right.setVisibility(View.GONE);
				tv_right.setVisibility(View.GONE);
			}else{
				if (null != iv_right) {
					ll_right.setVisibility(View.GONE);
					iv_right.setVisibility(View.GONE);
				}
				if (null != tv_right) {
					tv_right.setVisibility(View.VISIBLE);
					tv_right.setText(text);
					tv_right.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onclickRightNext();
						}
					});
				}
			}
		}else{//icon
			if(resId==0){
				if (null != iv_right) {
					ll_right.setVisibility(View.GONE);
					iv_right.setVisibility(View.GONE);
				}
			}else{
				if (null != tv_right) {
					tv_right.setVisibility(View.GONE);
				}
				if (null != iv_right) {
					ll_right.setVisibility(View.VISIBLE);
					iv_right.setVisibility(View.VISIBLE);
					iv_right.setImageResource(resId);
					ll_right.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onclickRightImg();
						}
					});
				}
			}
		}
	}

//    /** Fragment当前状态是否可见 */
//    protected boolean isVisible;
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        if(getUserVisibleHint()) {
//            isVisible = true;
//            onVisible();
//        } else {
//            isVisible = false;
//            onInvisible();
//        }
//    }


//    /**
//     * 可见
//     */
//    protected void onVisible() {
//        lazyLoad();
//    }

//
//    /**
//     * 不可见
//     */
//    protected void onInvisible() {
//
//
//    }
	/**
	 * 跳转到主工程界面
	 * @param action
	 */
	public void openActivity(String action){
		Intent intent = new Intent(action);
		startActivity(intent);
	}
	public void onclickLeftImg() {
	}
	public void onclickRightImg() {
	}
	public void onclickRightNext() {
	}
	public  void showToast(String msg){
		Toast.makeText(getActivity(),msg+"",Toast.LENGTH_SHORT).show();
	}

	/**
	 */
	public  void getData(){}

	/**
	 * 等待加载框
	 * @param context
	 * @param message
	 * @param cancelable
	 * @param cancelListener
	 */
	public void showProgressDialog(Context context, CharSequence message, boolean cancelable,
								   DialogInterface.OnCancelListener cancelListener){
		if(progressDialog == null||(progressDialog!=null && !progressDialog.isShowing())){
			progressDialog = ProgressHUD.show(context, message, cancelable, cancelListener);
		}
	}

	/**
	 * 取消等待框
	 */
	public void disProgressDialog(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	/**
	 * 如果被禁言
	 * @return
	 */
	public   boolean checkFeedMute(){
		String tempString = getService().getUserManager().getUserProfile(Constants.SETTING_FIELD.FEED_MUTE,"");
		if(!TextUtils.isEmpty(tempString)&&"true".equals(tempString)){//禁言
			showToast("亲子圈暂不可用");
			return true;
		}
		return false;
	}


}
