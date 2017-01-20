package com.xcsd.app.teacher.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.adapter.ExpressionAdapter;
import com.tuxing.app.easemob.adapter.ExpressionPagerAdapter;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.easemob.widget.ExpandGridView;
import com.xcsd.app.teacher.adapter.HeyInfoCommentAdapter;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.MyListView;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.FeedMedicineTask;
import com.tuxing.sdk.event.CommentEvent;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeyInfoActivity extends BaseActivity implements
		OnItemClickListener {
	private ArrayList<String> iconList;
	private List<Comment> comments;
	private List<Comment> tempList;
	private IconAdapter adapter;
	private HeyInfoCommentAdapter commentAdapter;
	private MyGridView iconView;
	private TextView mAndD;
	private TextView week;
	private TextView content;
	private MyListView commentView;
	private ImageView faceBtn;
	private EditText msgEt;
	private ViewPager facePager;
	private LinearLayout faceLl;
	private RelativeLayout allVIew;
	private LinearLayout page_select;
	private LinearLayout scro_ll;
	private Button send;
	private List<String> reslist;//表情
	private InputMethodManager manager;
	private String TAG = HeyInfoActivity.class.getSimpleName();
	private FeedMedicineTask feedData;
	private ScrollView scroll;
	 public static boolean isUpdate = false;
	 private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				scroll.fullScroll(ScrollView.FOCUS_DOWN);
				msgEt.requestFocus();
			};
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentLayout(R.layout.home_hey_info_layout);
		init();
		initData();
	}

	private void init() {
		setTitle(getString(R.string.eat_drug));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		mAndD = (TextView) findViewById(R.id.hey_info_moth_day);
		week = (TextView) findViewById(R.id.hey_info_week);
		commentView = (MyListView) findViewById(R.id.hey_info_list);
		content = (TextView) findViewById(R.id.hey_info_content);
		iconView = (MyGridView) findViewById(R.id.hey_info_grid);
		scroll = (ScrollView) findViewById(R.id.hey_scroll);
		faceBtn = (ImageView) findViewById(R.id.mailbox_emoticons);
		msgEt = (EditText) findViewById(R.id.mailbox_info_et);
		facePager = (ViewPager) findViewById(R.id.mailbox_Pager);
		faceLl = (LinearLayout) findViewById(R.id.ll_face_container);
		allVIew = (RelativeLayout) findViewById(R.id.ll);
		page_select = (LinearLayout) findViewById(R.id.mailbox_select);
		send = (Button) findViewById(R.id.mailbox_send);
		findViewById(R.id.scro_ll).setOnClickListener(this);
		iconView.setOnItemClickListener(this);
		content.setFocusable(true);
		 iconList = new ArrayList<String>();
		comments = new ArrayList<Comment>();
		tempList = new ArrayList<Comment>();
		faceBtn.setOnClickListener(this);
		send.setOnClickListener(this);
		msgEt.setOnClickListener(this);
		msgEt.requestFocus();
		msgEt.addTextChangedListener(new MaxLengthWatcher(200, msgEt));
		// 表情list
		reslist = getExpressionRes(SmileUtils.emoticons.size());
		initViewPager();
		msgEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s) && s.toString().trim().length() > 0) {
					send.setVisibility(View.VISIBLE);
				} else {
					send.setVisibility(View.GONE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		allVIew.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
		    @Override
		    public void onGlobalLayout(){
		       //比较Activity根布局与当前布局的大小
		        int heightDiff = allVIew.getRootView().getHeight()- allVIew.getHeight();
		        if(heightDiff >200){
		        	isUpdate = false;
		       }
		     }
		});
		commentView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				hideKeyboard();
			}
		});
		
	}

	private void initData() {
		// TODO 获取喂药详情
		 feedData = (FeedMedicineTask) getIntent().getSerializableExtra("feed");
		try {
		JSONArray array = new JSONArray(feedData.getAttachments());
		for(int i = 0; i < array.length(); i++){
			iconList.add(array.getJSONObject(i).getString("url"));
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		updateAdapter();
		content.setText(feedData.getDescription());
		String beginDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(feedData.getBeginDate()));
		mAndD.setText(beginDate.split("-")[1] + "-" + beginDate.split("-")[2]);
		week.setText(Utils.getWeekOfDate(beginDate, "yyyy-MM-dd"));
		
		getService().getFeedManager().getCommentsForFeedMedicineTask(feedData.getTaskId());
		
	}
	
	 public void onEventMainThread(CommentEvent event){
		 if(isActivity){
			 switch (event.getEvent()){
			 case GET_FEED_MEDICINE_TASK_COMMENTS_SUCCESS:
				 tempList = event.getComments();
				if(tempList != null){
					comments.clear();
					comments.addAll(tempList);
					updateCommentAdapter();
				}
				 disProgressDialog();
				 showAndSaveLog(TAG, "获取喂药评论列表成功 size = " + comments.size(), false);
				 break;
			 case GET_FEED_MEDICINE_TASK_COMMENTS_FAILED:
				 disProgressDialog();
				 showAndSaveLog(TAG, "获取喂药评论列表失败"+ event.getMsg(), false);
				 break;
			 case REPLAY_FEED_MEDICINE_TASK_SUCCESS:
				 getService().getFeedManager().getCommentsForFeedMedicineTask(feedData.getTaskId());
					 showAndSaveLog(TAG, "回复喂药成功 " , false);
					 break;
				 case REPLAY_FEED_MEDICINE_TASK_FAILED:
					 disProgressDialog();
					 showAndSaveLog(TAG, "回复喂药失败"+ event.getMsg(), false);
					 break;
			 }
		 } }

	

	public void updateAdapter() {
		isUpdate = true;
		if (adapter == null) {
			adapter = new IconAdapter(mContext, iconList);
			iconView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}
	public void updateCommentAdapter() {
		isUpdate = true;
		if (commentAdapter == null) {
			commentAdapter = new HeyInfoCommentAdapter(this, comments);
			commentView.setAdapter(commentAdapter);
		} else {
			commentAdapter.notifyDataSetChanged();
		}
		handler.sendEmptyMessage(0);
	}

	public class IconAdapter extends BaseAdapter {
		private Context context;
		private List<String> list;

		public IconAdapter(Context context, List<String> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.grideview_item1_layout, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (MyImageView) convertView
						.findViewById(R.id.grid_item1_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(isUpdate){
				viewHolder.icon.setImageUrl(iconList.get(position) + SysConstants.Imgurlsuffix90, R.drawable.defal_down_proress,false);
			}
			return convertView;
		}

	}

	public class ViewHolder {
		MyImageView icon;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO
		NewPicActivity.invoke(HeyInfoActivity.this, iconList.get(position), true, iconList, false);
	}
	
	
	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}
	

		@Override
		public void onClick(View v) {
			super.onClick(v);
			switch (v.getId()) {
			case R.id.scro_ll:
				hiddenInput(this);
				break;
			case R.id.mailbox_send:
				hideKeyboard();
				faceLl.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
				SendContent();
				MobclickAgent.onEvent(mContext,"wy_common",UmengData.wy_common);
				break;
			case R.id.mailbox_info_et:
				if(faceLl.getVisibility() == View.VISIBLE){
					faceLl.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
				}
				break;
			case R.id.mailbox_emoticons:
				//
				msgEt.setFocusable(true);
				hideKeyboard();
				if(faceLl.getVisibility() == View.VISIBLE){
					faceLl.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
				}else{
					faceLl.setVisibility(View.VISIBLE);
					page_select.setVisibility(View.VISIBLE);
				}
				break;

			}
	}
	
	
	public void initViewPager(){
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		int count = SmileUtils.emoticons.size()%20;
		if(count!=0)
			count = SmileUtils.emoticons.size()/20+1;
		else
			count = SmileUtils.emoticons.size()/20;
		for(int i=0;i<count;i++){
			View gv = getGridChildView(i,count);
			views.add(gv);
		}
		facePager.setAdapter(new ExpressionPagerAdapter(views));
		facePager.setOnPageChangeListener(new GuidePageChangeListener());
		for(int i =0;i<views.size();i++){
			ImageView image = new ImageView(mContext);
			if(i == 0){
				image.setImageResource(R.drawable.page_focused);
			}
			else{
				image.setImageResource(R.drawable.page_unfocused);
			}
			image.setPadding(Utils.dip2px(mContext, 10), 0, 0, 40);
			image.setId(i);
			page_select.addView(image);
		}
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i,int max) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if(max==i+1){
			List<String> list1 = reslist.subList(i*20,reslist.size());
			list.addAll(list1);
		}else{
			List<String> list1 = reslist.subList(i*20,(i+1)*20);
			list.addAll(list1);
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.tuxing.app.easemob.util.SmileUtils");
							Field field = clz.getField(filename);
							msgEt.append(SmileUtils.getSmiledText(HeyInfoActivity.this,
                                    (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(msgEt.getText())) {

								int selectionStart = msgEt.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = msgEt.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											msgEt.getEditableText().delete(i, selectionStart);
										else
											msgEt.getEditableText().delete(selectionStart - 1,
													selectionStart);
									} else {
										msgEt.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}
	
	// ** 指引页面改监听器 */
		class GuidePageChangeListener implements OnPageChangeListener {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				int count = page_select.getChildCount();
				for(int i=0; i< count; i++){
					ImageView view = (ImageView) page_select.getChildAt(i);
					if(i == arg0 )
						view.setImageResource(R.drawable.page_focused);
					else{
						view.setImageResource(R.drawable.page_unfocused);
					}
				}
			}
		}
		
		
		
		/**
		 * 隐藏软键盘
		 */
		private void hideKeyboard() {
			if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
				if (getCurrentFocus() != null){
					InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}}
		}
		
		public void SendContent(){
			if(msgEt.getText().toString().trim().length() > 0){
				
				showProgressDialog(mContext, "", true, null);
				getService().getFeedManager().replayFeedMedicineTask(feedData.getTaskId(), msgEt.getText().toString().trim());
				msgEt.setText("");
			}else{
				showToast("发送的内容不能为空");
			}
		}
		
		public class MaxLengthWatcher implements TextWatcher {  
		    private int maxLen = 0;  
		    private EditText editText = null;  
		    public MaxLengthWatcher(int maxLen, EditText editText) {  
		        this.maxLen = maxLen;  
		        this.editText = editText;  
		    }  
		  
		    public void afterTextChanged(Editable arg0) {  
		    }  
		  
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,  
		            int arg3) {  
		    }  
		  
		    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {  
		        Editable editable = editText.getText();  
		        int len = editable.length();  
		          
		        if(len >= maxLen){  
		        	showToast(getResources().getString(R.string.edit_number_count));
		        }  
		    }  
		  
		}
	
}
