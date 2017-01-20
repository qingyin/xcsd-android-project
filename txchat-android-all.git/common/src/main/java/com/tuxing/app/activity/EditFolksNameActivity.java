package com.tuxing.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.view.ClearEditText;

import java.util.Timer;
import java.util.TimerTask;

public class EditFolksNameActivity extends BaseActivity {

    private String name;
    private ClearEditText et_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_edit_folks_name);
        name = getIntent().getStringExtra("name");
        initView();
        setTitle("监护人");
        setLeftBack("取消", false, false);
        setRightNext(true, "确定", 0);
    }

    private void initView() {
        et_username = (ClearEditText)findViewById(R.id.et_username);
        et_username.setText(name);
        showInput(et_username);
    }
    public void onclickRightNext() {
        if(!android.text.TextUtils.isEmpty(et_username.getText().toString())){
            hiddenInput(EditFolksNameActivity.this);
            Intent intent = new Intent(EditFolksNameActivity.this,SelectFolksActivity.class);
            intent.putExtra("name",et_username.getText().toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }else{
            showToast("监护人姓名不能为空");
        }
    }
    /**
     * 显示键盘
     *
     * @param edit
     */
    public void showInput(final EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edit, 0);
            }
        }, 300);
    }

    /**
     * 隐藏键盘
     */
    public void hiddenInput(Activity activity) {
        manager.hideSoftInputFromWindow(activity.getCurrentFocus()
                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
