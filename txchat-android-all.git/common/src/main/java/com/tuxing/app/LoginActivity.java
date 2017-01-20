package com.tuxing.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatConfig;
import com.tuxing.app.activity.CocosJSActivity;
import com.tuxing.app.activity.GetVerificationActivity;
import com.tuxing.app.activity.SelectFolksActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import de.greenrobot.event.EventBus;

/**
 */
public class LoginActivity extends BaseActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    View view;
    private TextView tv_active_account;
    private TextView tv_forget_password;
    private TextView tv_change_environment;
    private ImageView iv_logon_logo;
    private Button btn_login;
    private RelativeLayout rl_active;
    private LinearLayout ll_show;
    private EditText et_username;
    private EditText et_password;
    private String password;
    FinishReceiver receiver;
    boolean isConflict;
    String msg = "登录状态已失效，请重新登录";
    boolean isRemove;

    public static String VersionType = "";
    public static String packageName = "";
    public static String VersionName = "";
    private HttpClient httpClient = HttpClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.layout_login, null);
        setContentView(view);
        try {
            initView();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        receiver = new FinishReceiver();
        IntentFilter filter = new IntentFilter(SysConstants.FINISH_LOGIN);
        registerReceiver(receiver, filter);

    }

    private void initView() throws PackageManager.NameNotFoundException {
        tv_change_environment = (TextView) findViewById(R.id.tv_change_environment);
//        切换环境，测试服，开发服，正式服
        if (!SysConstants.isTest) {
            tv_change_environment.setVisibility(View.GONE);
        }
        tv_change_environment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show_change_environment();
            }
        });
        tv_active_account = (TextView) findViewById(R.id.tv_active_account);
        ll_show = (LinearLayout) findViewById(R.id.ll_show);
        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
        iv_logon_logo = (ImageView) findViewById(R.id.iv_login_logo);
        tv_forget_password.setOnClickListener(this);
        et_username = (EditText) findViewById(R.id.et_username);
        String name = PreferenceUtils.getPrefString(mContext, SysConstants.userName, "");
        et_username.setText(name);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);//btn_login.setOnClickListener(this);
        PackageInfo info = LoginActivity.this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        String packageName = info.packageName;
        if ("com.xcsd.app.parent".equals(packageName)) {
            iv_logon_logo.setImageDrawable(getResources().getDrawable(R.drawable.logo_p));
            btn_login.setBackground(getResources().getDrawable(R.drawable.btn_bg_selector_p));
            ll_show.setBackgroundColor(getResources().getColor(R.color.text_parent));
            tv_active_account.setTextColor(getResources().getColor(R.color.text_parent));
        } else {
            iv_logon_logo.setImageDrawable(getResources().getDrawable(R.drawable.logo_teacher));
        }
//        } else if ("com.tuxing.app.teacher".equals(packageName)) {
//            iv_logon_logo.setImageDrawable(getResources().getDrawable(R.drawable.logo_teacher));
//        }
        rl_active = (RelativeLayout) findViewById(R.id.rl_active);
        if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.ISACTIVITYACTIVITY, false)) {
            rl_active.setVisibility(View.GONE);
        } else {
            rl_active.setVisibility(View.VISIBLE);
            rl_active.setOnClickListener(this);
        }
        if (!TextUtils.isEmpty(et_password.getText().toString()) && !TextUtils.isEmpty(et_username.getText().toString())) {
            btn_login.setClickable(true);
            btn_login.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn_login.setTextColor(getResources().getColor(R.color.white));
            btn_login.setClickable(false);
        }
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(et_password.getText().toString()) && !TextUtils.isEmpty(et_username.getText().toString())) {
                    btn_login.setClickable(true);
                    btn_login.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btn_login.setTextColor(getResources().getColor(R.color.white));
                    btn_login.setClickable(false);
                }
            }
        });
        et_password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
//        et_password.setLongClickable(false);
        et_password.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        et_password.setTextIsSelectable(false);
        et_password.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(et_password.getText().toString()) && !TextUtils.isEmpty(et_username.getText().toString())) {
                    btn_login.setClickable(true);
                    btn_login.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btn_login.setTextColor(getResources().getColor(R.color.white));
                    btn_login.setClickable(false);
                }
            }
        });

        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                String userName = et_username.getText().toString();
                password = et_password.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    showToast(getResources().getString(R.string.name_null));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    showToast(getResources().getString(R.string.pwd_null));
                    return;
                }
//                if (password.length() < 6 || password.length() > 16) {
//                    showToast(getResources().getString(R.string.pwd_length));
//                    return;
//                }
                showProgressDialog(mContext, "", true, null);
                //登录
                getService().getLoginManager().login(et_username.getText().toString(), et_password.getText().toString());
            }
        });

        tv_active_account.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActiveActivity.class));
                hiddenInput(LoginActivity.this);

            }
        });

        tv_forget_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GetVerificationActivity.class);
                intent.putExtra("type", SysConstants.VERIFICATION_CODE_RESET_PASSWORD);
                intent.putExtra("title", "重置密码");
                startActivity(intent);
                hiddenInput(LoginActivity.this);
            }
        });

        rl_active.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActiveActivity.class));
                hiddenInput(LoginActivity.this);
            }
        });

        Intent intent = getIntent();
        isConflict = intent.getBooleanExtra("isConflict", false);
        msg = intent.getStringExtra("msg");
        isRemove = intent.getBooleanExtra("isRemove", false);

        if (isConflict) {
            showDialog(null, msg, "退出", "重新登录");
            sendBroadcast(new Intent(SysConstants.FINISH_MAIN));
        }
        if (isRemove) {
            if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {//家长版
                showDialog(null, getResources().getString(R.string.user_remove), "", "确定");
            } else {
                showDialog(null, getResources().getString(R.string.user_kickoff), "", "确定");
            }
            sendBroadcast(new Intent(SysConstants.FINISH_MAIN));
        }

    }

    @Override
    public void removeActivity(Activity a) {
        super.removeActivity(a);
    }

    //切换开发环境选择
    private void show_change_environment() {
        new AlertDialog.Builder(this).setTitle("选择环境").setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                new String[]{"开发环境", "测试环境","正式环境"}, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int choice) {
                        CoreService.getInstance().stop();
                        if (choice==0){
                            //开发服
                            SysConstants.H5_GAME_HOST_URL = SysConstants.H5_GAME_HOST_URL_DEV_TEST;
                            EMChatConfig.getInstance().APPKEY = SysConstants.HX_APPKEY_DEV;
                            SysConstants.AGREEMENTURL_XCSD = SysConstants.AGREEMENTURL_XCSD_DEV_TEST;
                            SysConstants.KURL_FEEDBACK = SysConstants.KURL_FEEDBACK_DEV_TEST;
                            SysConstants.KURL_THEME_TEST = SysConstants.KURL_THEME_TEST_DEV_TEST;
                            SysConstants.JSHostUrl = SysConstants.JSHostUrlDev;
                            Constants.QI_NIU_DOMAIN = Constants.QI_NIU_DOMAIN_DEV_TEST;


                            httpClient.setHostAddress("121.40.16.212", 8080);
//                            CoreService.getInstance().start(TuxingApp.getInstance().getBaseContext(), VersionType + "_" + VersionName, "121.40.16.212",8080);
                            showToast("开发服环境切换成功");
                        }else if(choice==1){
                            //测试服
                            SysConstants.H5_GAME_HOST_URL = SysConstants.H5_GAME_HOST_URL_DEV_TEST;
                            EMChatConfig.getInstance().APPKEY = SysConstants.HX_APPKEY_TEST;
                            SysConstants.AGREEMENTURL_XCSD = SysConstants.AGREEMENTURL_XCSD_DEV_TEST;
                            SysConstants.KURL_FEEDBACK = SysConstants.KURL_FEEDBACK_DEV_TEST;
                            SysConstants.KURL_THEME_TEST = SysConstants.KURL_THEME_TEST_DEV_TEST;
                            SysConstants.JSHostUrl = SysConstants.JSHostUrlTest;
                            Constants.QI_NIU_DOMAIN = Constants.QI_NIU_DOMAIN_DEV_TEST;

                            httpClient.setHostAddress("121.41.101.14", 8080);
//                            CoreService.getInstance().start(TuxingApp.getInstance().getBaseContext(), VersionType + "_" + VersionName, "121.41.101.14", 8080);
                            showToast("测试服环境切换成功");
                        }else if(choice==2){
                            //正式服
                            SysConstants.H5_GAME_HOST_URL = SysConstants.H5_GAME_HOST_URL_DIS;
                            EMChatConfig.getInstance().APPKEY = SysConstants.HX_APPKEY_DIS;
                            SysConstants.AGREEMENTURL_XCSD = SysConstants.AGREEMENTURL_XCSD_DIS;
                            SysConstants.KURL_FEEDBACK = SysConstants.KURL_FEEDBACK_DIS;
                            SysConstants.KURL_THEME_TEST = SysConstants.KURL_THEME_TEST_DIS;
                            SysConstants.JSHostUrl = SysConstants.JSHostUrlDis;
                            Constants.QI_NIU_DOMAIN = Constants.QI_NIU_DOMAIN_DIS;

                            httpClient.setHostAddress("service.xcsdedu.com", 80);
//                            CoreService.getInstance().start(TuxingApp.getInstance().getBaseContext(), VersionType+"_"+VersionName,"service.xcsdedu.com",80);
                            showToast("正式服环境切换成功");
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
    }

    @Override
    public void onConfirm() {
        super.onConfirm();
        if (!isRemove) {
            //登录
            String userMob = PreferenceUtils.getPrefString(mContext, SysConstants.userName, "");
            if (!TextUtils.isEmpty(userMob)) {
                showProgressDialog(mContext, "", true, null);
                String userPwd = getService().getLoginManager().getPassword(userMob);
                if (!TextUtils.isEmpty(userPwd)) {
                    getService().getLoginManager().login(userMob, userPwd);
                } else {
                    showToast("登录已失效,请手动登录");
                }
            }
        }
    }

    @Override
    public void onCancel() {
        super.onCancel();
    }

    public void onEventMainThread(LoginEvent event) {
        disProgressDialog();
        //if(event.getToken().equals("LoginActivity")){
        if (isActivity) {

            switch (event.getEvent()) {
                case LOGIN_SUCCESS:
                    PreferenceUtils.setPrefString(mContext, SysConstants.userName, et_username.getText().toString());
                    getService().getContactManager().syncContact();
                    user = getService().getLoginManager().getCurrentUser();
                    if (user != null && user.isActive()) {//2.0
                        //cUser.type  1 小孩 2 家长 3 教师
                        if ((user.getType() == Constants.USER_TYPE.CHILD || user.getType() == Constants.USER_TYPE.PARENT) && user.getRelativeType() == null) {//需要绑定小孩
                            Intent intent = new Intent(mContext, SelectFolksActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else if (user != null) {//1.0流程
                        //cUser.type  1 小孩 2 家长 3 教师
//                    if ((user.getType() == Constants.USER_TYPE.CHILD || user.getType() == Constants.USER_TYPE.PARENT) && user.getChildUserId() == 0) {//需要绑定小孩
                        Intent intent = new Intent(mContext, GetVerificationActivity.class);
                        intent.putExtra("type", SysConstants.VERIFICATION_CODE_ACTIVATE_USER);
                        intent.putExtra("title", "绑定手机号");
                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
                    }
                    CocosJSActivity.resetData();
                    break;
                case LOGIN_NO_SUCH_USER:
                case LOGIN_FAILED_UNKNOWN_REASON:
                    showToast(event.getMsg());
                    break;
                case LOGOUT:
//                Toast.makeText(getApplicationContext(), event.getMsg(), Toast.LENGTH_LONG).show();
                    break;
                case LOGIN_AUTH_FAILED:
                    showToast(event.getMsg());
                    break;
                case KICK_OFF://这两个要处理下
                    showToast(event.getMsg());
                    break;
                default:
                    break;
            }
            //}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class FinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SysConstants.FINISH_LOGIN)) {
                showAndSaveLog(TAG, "FinishReceiver", false);
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity();
//            finish();
            return false;
//            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
