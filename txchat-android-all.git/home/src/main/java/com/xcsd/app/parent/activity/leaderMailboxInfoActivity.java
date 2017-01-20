package com.xcsd.app.parent.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.adapter.ExpressionAdapter;
import com.tuxing.app.easemob.adapter.ExpressionPagerAdapter;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.easemob.widget.ExpandGridView;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.R.id;
import com.xcsd.app.parent.adapter.LeaderMailboxInfoCommentAdapter;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyListView;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.GardenMail;
import com.tuxing.sdk.event.CommentEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class leaderMailboxInfoActivity extends BaseActivity {
	private List<Comment> comments;
	private LeaderMailboxInfoCommentAdapter adapter;
	private TextView time;
	private TextView content;
	private TextView mail_msg;
	private MyListView commentView;
	private String TAG = leaderMailboxInfoActivity.class.getSimpleName();
	private ImageView faceBtn;
	private EditText msgEt;
	private ViewPager facePager;
	private LinearLayout faceLl;
	private LinearLayout page_select;
	private Button send;
	private List<String> reslist;//表情
	private InputMethodManager manager;
	private GardenMail mail;
	private long loginUserId;
	private ScrollView scroll;
	public boolean isUpdate = true;
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

		setContentLayout(R.layout.home_leader_mailbox_info_layout);
		init();
		initData();
	}

	private void init() {
		setTitle(getString(R.string.mailbox));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		time = (TextView) findViewById(R.id.maibox_info_time);
		commentView = (MyListView) findViewById(R.id.maibox_info_list);
		content = (TextView) findViewById(R.id.maibox_info_content);
		mail_msg = (TextView) findViewById(R.id.mail_msg);
		faceBtn = (ImageView) findViewById(R.id.mailbox_emoticons);
		msgEt = (EditText) findViewById(R.id.mailbox_info_et);
		facePager = (ViewPager) findViewById(R.id.mailbox_Pager);
		faceLl = (LinearLayout) findViewById(R.id.ll_face_container);
		page_select = (LinearLayout) findViewById(R.id.mailbox_select);
		scroll = (ScrollView) findViewById(id.mailbox_scroll);
		send = (Button) findViewById(R.id.mailbox_send);
		comments = new ArrayList<Comment>();
		findViewById(R.id.ll).setOnClickListener(this);
		faceBtn.setOnClickListener(this);
		send.setOnClickListener(this);
		msgEt.setOnClickListener(this);
		msgEt.setFocusable(true);
		// 表情list
		reslist = getExpressionRes(SmileUtils.emoticons.size());
		initViewPager();
		msgEt.addTextChangedListener(new MaxLengthWatcher(200, msgEt));
		msgEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s) && s.toString().trim().length() > 0) {
					send.setVisibility(View.VISIBLE);
				} else {
					send.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

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

	private void initData() {
		if(user != null)
		loginUserId = user.getUserId();
		 mail = (GardenMail) getIntent().getSerializableExtra("mail");
		content.setText(mail.getContent());
		time.setText(Utils.getDateTime(mContext, mail.getSendTime()));
		showProgressDialog(mContext, "", true, null);
		getService().getFeedManager().getCommentsForGardenMail(mail.getMailId());

	}

	 public void onEventMainThread(CommentEvent event){
		 if(isActivity){
			 List<Comment> tempConents = new ArrayList<Comment>();
			 switch (event.getEvent()){
			 case GET_GARDEN_MAIL_COMMENTS_SUCCESS:
				 tempConents = event.getComments();
				if(tempConents != null && tempConents.size() > 0){
					comments.clear();
					comments.addAll(tempConents);
					commentView.setSelection(comments.size() - 1);
					updateCommentAdapter();
					mail_msg.setVisibility(View.GONE);
				}else{
					mail_msg.setVisibility(View.VISIBLE);
				}
				disProgressDialog();
				 showAndSaveLog(TAG, "获信箱评论列表成功 size = " + comments.size(), false);
				 break;
			 case GET_GARDEN_MAIL_COMMENTS_FAILED:
				 disProgressDialog();
				 showAndSaveLog(TAG, "获取信箱评论列表失败"+ event.getMsg(), false);
				 break;
			 case REPLAY_GARDEN_MAIL_SUCCESS:
				getService().getFeedManager().getCommentsForGardenMail(mail.getMailId());
				msgEt.setText("");
				 showAndSaveLog(TAG, "回复邮件成功 " , false);
				 break;
			 case REPLAY_GARDEN_MAIL_FAILED:
				 showDialog("", getString(R.string.replay_msg), "", getResources().getString(R.string.btn_ok));
				 disProgressDialog();
				 showAndSaveLog(TAG, "回复邮件失败"+ event.getMsg(), false);
				 break;
			 }
		 } }




	public void updateCommentAdapter() {
		isUpdate = true;
		if (adapter == null) {
			adapter = new LeaderMailboxInfoCommentAdapter(leaderMailboxInfoActivity.this, comments,mail.getAnonymous());
			commentView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		handler.sendEmptyMessage(0);

	}



	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.ll:
			hiddenInput(this);
			break;
		case R.id.mailbox_send:
			hideKeyboard();
			faceLl.setVisibility(View.GONE);
			page_select.setVisibility(View.GONE);
			SendContent();

			break;
		case R.id.mailbox_info_et:
			isUpdate = false;
			faceLl.setVisibility(View.GONE);
			page_select.setVisibility(View.GONE);
			break;
		case R.id.mailbox_emoticons:
			//
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

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

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
							msgEt.append(SmileUtils.getSmiledText(leaderMailboxInfoActivity.this,
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
				if (getCurrentFocus() != null)
					manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		
		public void SendContent(){
			if(msgEt.getText().toString().trim().length() > 0){
				showProgressDialog(mContext, "", true, null);
				getService().getFeedManager().replayGardenMail(mail.getMailId(), msgEt.getText().toString().trim());
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
