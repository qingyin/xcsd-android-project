
package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.ui.dialog.CommonDialog;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.RelativeEvent;
import com.tuxing.sdk.modle.Relative;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class InviteFolksListActivity extends BaseActivity {

    private ListView listView;
    private List<Relative> relatives;
    private int masterType = -1;
	private int[] itemsTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_invite_folks_list);
        relatives = new ArrayList<Relative>();
//        if (EventBus.getDefault() != null && !EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().registerSticky(this);
//        }
        initView();
        setTitle("邀请家人");
        setLeftBack("", true, false);
        setRightNext(true, "", 0);
        iniData();
    }
    
	@Override
	protected void onResume() {
		 showProgressDialog(mContext,"",true,null);
		 getData();
		super.onResume();
	}

    @Override
    public void getData() {
        super.getData();
        getService().getRelativeManager().getRelativeList();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setDivider(null);
    }

    private void iniData() {
        String[] items = getResources().getStringArray(R.array.folks_menu_option);
        itemsTypes = getResources().getIntArray(R.array.folks_menu_option_value);
        ArrayListAdapter adapter = new ArrayListAdapter(items);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            	if(getService() != null){
                	Relative relative = relatives.get(position);
                	if(user != null && user.getRelativeType() == masterType && !relative.isMaster()){
                		showContextMenu(position);
                	}
            	}
                return false;
            }
        });
    }

    class ArrayListAdapter extends BaseAdapter {
        String[] items;
        public ArrayListAdapter(String[] _items) {
            this.items = _items;
        }


        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int postion, View view, ViewGroup viewGroup) {
            if(view == null){
                view = getLayoutInflater().inflate(R.layout.layout_folks_item,null);
            }
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phonenum = (TextView) view.findViewById(R.id.tv_phonenum);//邀请家人 1
            ImageView iv_user_star = (ImageView)view.findViewById(R.id.iv_user_star);//如果是主帐号,点亮图标  1
            RoundImageView icon = (RoundImageView)view.findViewById(R.id.tem_icon);//如果是主帐号,点亮图标  1
            Button cb_check = (Button)view.findViewById(R.id.btn_check);// 0 2 激活和设置替换
            LinearLayout ll_get_verification = (LinearLayout)view.findViewById(R.id.ll_get_verification);//邀请家人 1
            View view_line = (View)view.findViewById(R.id.view_line);
            cb_check.setVisibility(View.GONE);

            tv_name.setText(items[postion]);
            if(relatives.size()>postion&&relatives.get(postion).getUser()!=null){
                User user = relatives.get(postion).getUser();
                if(relatives.get(postion).isMaster()){
                	masterType = relatives.get(postion).getRelativeType();
                    iv_user_star.setVisibility(View.VISIBLE);
                }else
                    iv_user_star.setVisibility(View.GONE);
                ll_get_verification.setVisibility(View.GONE);
                tv_phonenum.setVisibility(View.VISIBLE);
                icon.setImageUrl(relatives.get(postion).getUser().getAvatar()+ SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
                tv_name.setTextColor(getResources().getColor(R.color.skin_text1));
                tv_phonenum.setText(user.getMobile());
            } else {
                iv_user_star.setVisibility(View.GONE);
                ll_get_verification.setVisibility(View.VISIBLE);
                tv_phonenum.setVisibility(View.GONE);
            }

            ll_get_verification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                	//TODO
                    Intent intent = new Intent(mContext, InviteVerificationActivity.class);
                    intent.putExtra("title", "邀请家人");
                    intent.putExtra("relationType", itemsTypes[postion]);
                    startActivity(intent);
                    MobclickAgent.onEvent(mContext,"my_invite",UmengData.my_invite);
                }
            });

            if(postion == items.length-1)
                view_line.setVisibility(View.GONE);
            return view;
        }
    }

    public void onEventMainThread(RelativeEvent event) {
       
        switch (event.getEvent()) {
            case GET_RELATIVE_SUCCESS:
                relatives = event.getRelatives();
                iniData();
                disProgressDialog();
                break;
            case GET_RELATIVE_FAILED:
            	 showToast(event.getMsg());
            	 disProgressDialog();
                break;
            case REMOVE_RELATIVE_SUCCESS:
                getService().getRelativeManager().getRelativeList();
                showToast("解除关系成功");
                sendTouChuan(SysConstants.TOUCHUAN_UNBINDUSER);
                break;
            case REMOVE_RELATIVE_FAILED:
                showToast(event.getMsg());
                disProgressDialog();
                break;
        }
    }
    public void showContextMenu(final int index) {
        final CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(mContext);
        dialog.setTitle("选择操作");
        dialog.setNegativeButton("取消", null);
        dialog.setItemsWithoutChk(
                getResources().getStringArray(R.array.remove_menu_option),
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    	 showProgressDialog(mContext,"",true,null);
                            getService().getRelativeManager().removeRelative(relatives.get(index).getUser().getUserId());
                        MobclickAgent.onEvent(mContext,"my_remove", UmengData.my_remove);
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

}
