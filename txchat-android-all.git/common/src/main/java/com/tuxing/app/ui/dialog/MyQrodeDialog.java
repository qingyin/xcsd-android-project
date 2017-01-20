package com.tuxing.app.ui.dialog;

/**
 * Created by wangst on 15-10-12.
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.WriterException;
import com.tuxing.app.R;
import com.tuxing.app.util.QrCodeUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.modle.CheckInCard;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MyQrodeDialog extends Dialog {

    private ImageView mIvCode;
    private Bitmap bitmap;
    private Bitmap bitmapAdd;
    private TextView tv_text;
    private TextView tv_text1;

    private MyQrodeDialog(Context context, boolean flag,
                          OnCancelListener listener) {
        super(context, flag, listener);
    }

    @SuppressLint("InflateParams")
    private MyQrodeDialog(Context context, int defStyle,String type) {
        super(context, defStyle);
        View contentView = getLayoutInflater().inflate(
                R.layout.dialog_my_qr_code, null);
        mIvCode = (ImageView) contentView.findViewById(R.id.iv_qr_code);
        tv_text = (TextView)contentView.findViewById(R.id.tv_text);
        tv_text1 = (TextView)contentView.findViewById(R.id.tv_text1);
        if(CoreService.getInstance().getLoginManager().getCurrentUser() != null){
            User currentUser = CoreService.getInstance().getUserManager().getUserInfo(CoreService.getInstance().getLoginManager().getCurrentUser().getUserId());
//            User childData = CoreService.getInstance().getUserManager().getUserInfo(currentUser.getChildUserId());
//            if(childData!=null){
//                tv_text.setText(childData.getNickname());
//            }else
//                tv_text.setText("");
            tv_text.setText(type);
            tv_text1.setText(currentUser.getNickname());

            try {
                //wjyteacher://check_in_with_user_id?user_id=%@&user_name=%@&user_type=%@&user_cardnumber=%@
                String url_checkin = String.format(
                        SysConstants.CHECK_IN_QD, String.valueOf(currentUser.getUserId()), URLEncoder.encode(currentUser.getNickname(), "UTF-8"));
                bitmap = QrCodeUtils.Create2DCode(url_checkin);
                bitmapAdd = QrCodeUtils.addLogo(bitmap, BitmapFactory.decodeResource(context.getResources(), R.drawable.logo));
                mIvCode.setImageBitmap(bitmapAdd);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mIvCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dismiss();
                return false;
            }
        });

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyQrodeDialog.this.dismiss();
                return false;
            }
        });
        super.setContentView(contentView);
    }

    public MyQrodeDialog(Context context,String type) {
        this(context, R.style.quick_option_dialog,type);
    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(p);
    }
}
