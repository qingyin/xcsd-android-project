package com.tuxing.app.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.*;
import android.widget.*;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.TextUtils;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.utils.Constants.CONTENT_TYPE;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends BaseActivity {
	
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private List<View> listViews; // Tab页面列表
	int currPos = -1;
	WebViewClient viewClient;
	WebView webView;
	private int index;
	List<ContentItem> datas;
	private String TAG = AnnouncementActivity.class.getSimpleName();
	private long gardenId;
	private RelativeLayout recipe_bg;
	private ImageView left;
	private ImageView right;
	int leftId = 0;
	int rightId = 200;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.activity_recipe);
		if(user != null)
		gardenId = user.getGardenId();
		setTitle(getString(R.string.recipe));
		setLeftBack("", true,false);
		setRightNext(true, "", 0);
		recipe_bg = (RelativeLayout) findViewById(R.id.recipe_bg);
		listViews = new ArrayList<View>();
		datas = new ArrayList<ContentItem>();
		initData();
		
	}
	
	private void initData() {
		//TODO 获取服务器数据
		showProgressDialog(mContext, "", true, null);
		getService().getContentManager().getLatestItems(gardenId, CONTENT_TYPE.RECIPES);
	}
	
	public void getHtml(){
			for(int i = 0; i < datas.size(); i++){
					getService().getContentManager().getContentHtml(datas.get(i).getItemId(), CONTENT_TYPE.RECIPES);
		}
	}
	
	public void onEventMainThread(ContentItemEvent event){
		 if(isActivity){
			 disProgressDialog();
		 switch (event.getEvent()){
		 case FETCH_LATEST_ITEM_SUCCESS:
			 if(event.getItems() != null && event.getItems().size() > 0){
				 datas.addAll(event.getItems());
				 recipe_bg.setVisibility(View.GONE);
				 init();
			 }else{
				 recipe_bg.setVisibility(View.VISIBLE);
			 }
			 showAndSaveLog(TAG, "获取食谱数据成功 size = " , false);
			 break;
		 case FETCH_LATEST_ITEM_FAILED:
				 showToast(event.getMsg());
			 recipe_bg.setVisibility(View.VISIBLE);
			 showAndSaveLog(TAG, "获取食谱的数据失败"+ event.getMsg(), false);
			 break;
		 } }
	 } 
	
	
	
	private void init() {
		pager = (ViewPager) findViewById(R.id.recipe_pager);
		
		viewClient = new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
				// 重写此方法可以让webview处理https请求
				handler.proceed();
			}
			 @Override
             public void onPageFinished(WebView view, String url) {
			 }
			 @Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		};
		for (int i = 0; i < datas.size(); i++) {
			LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			LinearLayout lLayout = new LinearLayout(mContext);
			lLayout.setOrientation(LinearLayout.VERTICAL);
			lLayout.setBackgroundColor(R.color.white);
			lLayout.setLayoutParams(relativeParams);
			lLayout.setId(i+ 10);
			listViews.add(lLayout);
		}

		if (currPos == -1) {
			currPos = 0;
		}
		pager.setOffscreenPageLimit(0);
		pager.setAdapter(new MyPagerAdapter(listViews));
		pager.setCurrentItem(currPos);

		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				index = arg0;
					if(arg0 == datas.size() - 1){
					left.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_colour));
					right.setImageDrawable(getResources().getDrawable(R.drawable.arrow_gray));
					webView.refreshDrawableState();
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}

	private void initWebView(WebView webView) {

		webView.getSettings().setPluginState(WebSettings.PluginState.ON);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);

	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			final ProgressBar  progressBar = new ProgressBar(mContext,null,android.R.attr.progressBarStyleHorizontal);
			progressBar.setId(arg1+20);
			progressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, TextUtils.Dp2Px(mContext, 3)));
			progressBar.setMax(100);
			((LinearLayout)(mListViews.get(arg1))).addView(progressBar);
			final View titleVIew = LayoutInflater.from(mContext).inflate(R.layout.recipe_item_title, null);
			
			titleVIew.findViewById(R.id.left).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(v.getTag() != null)
					leftId = (Integer) v.getTag();
					if(leftId > 100){
						pager.setCurrentItem(leftId - 100 - 1);
					}
				}
			});
			titleVIew.findViewById(R.id.right).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(v.getTag() != null)
					rightId = (Integer) v.getTag();
					if(rightId < 100 + datas.size()){
						pager.setCurrentItem(rightId - 100 + 1);
					}
				}
			});
			
			final TextView  webTitle = (TextView) titleVIew.findViewById(R.id.tecipe_item_tite);
			((LinearLayout)(mListViews.get(arg1))).addView(titleVIew);
			
			
			webView = new WebView(getApplicationContext());
			webView.setLayoutParams(relativeParams);
			webView.setId(arg1 + 100);
			webView.setWebViewClient(viewClient);
			webView.setWebChromeClient(new WebChromeClient() {  
	             public void onProgressChanged(WebView view, int progress) {  
	                 // Activity和Webview根据加载程度决定进度条的进度大小  
	                 // 当加载到100%的时候 进度条自动消失  
	            	 progressBar.setProgress(progress);
	            	 if (progress==100) {
	            		 titleVIew.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
					}
	             }  
	             @Override
	            public void onReceivedTitle(WebView view, String title) {
	            	super.onReceivedTitle(view, title);
	            	if(!android.text.TextUtils.isEmpty(title)){
	            		left = (ImageView) titleVIew.findViewById(R.id.left);
	            		right = (ImageView) titleVIew.findViewById(R.id.right);
	            		left.setTag(view.getId());
	            		right.setTag(view.getId());
	            		webTitle.setText(title);
	            		if(view.getId() == 100){
	    					left.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_gray));
	    					right.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_colour));
	    				}else if(view.getId() == datas.size() - 1){
	    					left.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_colour));
	    					right.setImageDrawable(getResources().getDrawable(R.drawable.arrow_gray));
	    				}else{
	    					left.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_colour));
	    					right.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_colour));
	    				}
	            	}
	            	
	            }
	             
	         });  
			initWebView(webView);
			webView.loadUrl(datas.get(arg1).getPostUrl());
			((LinearLayout)(mListViews.get(arg1))).addView(webView);
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			
			
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}

	}
	

}
