package com.tuxing.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.event.RelativeEvent;
import com.tuxing.sdk.modle.Relative;

import java.util.ArrayList;
import java.util.List;

//这个页面共用的  激活和设置家属列表
public class FolksListActivity extends BaseActivity {

    private ListView listView;
    private int type = -1;//0 激活选择身份 1 邀请家人选择身份 2 设置页面选择身份
    List<Relative> relatives;
    ArrayListAdapter adapter;
    int[] folks_value;
    private int relativeTypeIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_folks_list);
        folks_value = getResources().getIntArray(R.array.folks_menu_option_value);
        relatives = new ArrayList<Relative>();
        type = getIntent().getIntExtra("type",-1);
        if(type==0){
            relativeTypeIndex = getIntent().getIntExtra("relativeType",-1);
        }
        initView();
        setTitle("我是孩子的");
        setLeftBack("", true, false);
        setRightNext(true, "", 0);
        showProgressDialog(mContext,"",true,null);
        getService().getRelativeManager().getRelativeList();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setDivider(null);
    }

    private void iniData() {
        String[] items = getResources().getStringArray(R.array.folks_menu_option);
        adapter = new ArrayListAdapter(items);
        listView.setAdapter(adapter);
    }

    public void onEventMainThread(RelativeEvent event) {
        disProgressDialog();
        switch (event.getEvent()) {
            case GET_RELATIVE_SUCCESS:
                relatives = event.getRelatives();
                iniData();
                break;
            case GET_RELATIVE_FAILED:
                showToast(event.getMsg());
                iniData();
                break;
            default:
                break;
        }
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
            RelativeLayout arl_select_folks = (RelativeLayout)view.findViewById(R.id.rl_select_folks);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phonenum = (TextView) view.findViewById(R.id.tv_phonenum);//邀请家人 1
            RoundImageView tem_icon = (RoundImageView)view.findViewById(R.id.tem_icon);
            ImageView iv_user_star = (ImageView)view.findViewById(R.id.iv_user_star);//如果是主帐号,点亮图标  1
            final Button btn_check = (Button)view.findViewById(R.id.btn_check);// 0 2 激活和设置替换
            LinearLayout ll_get_verification = (LinearLayout)view.findViewById(R.id.ll_get_verification);//邀请家人 1
            View view_line = (View)view.findViewById(R.id.view_line);

            tv_name.setText(items[postion]);
            iv_user_star.setVisibility(View.GONE);
            ll_get_verification.setVisibility(View.GONE);
            tv_phonenum.setVisibility(View.GONE);
            if(relativeTypeIndex!=-1&&relativeTypeIndex==folks_value[postion]){
                btn_check.setVisibility(View.GONE);
            }else
                btn_check.setVisibility(View.VISIBLE);
            btn_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FolksListActivity.this, SelectFolksActivity.class);
                    intent.putExtra("name", items[postion]);
                    intent.putExtra("relativeType", folks_value[postion]);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
            arl_select_folks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btn_check.getVisibility()==View.VISIBLE){
                        Intent intent = new Intent(FolksListActivity.this, SelectFolksActivity.class);
                        intent.putExtra("name", items[postion]);
                        intent.putExtra("relativeType", folks_value[postion]);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            });

            if(postion == items.length-1)
                view_line.setVisibility(View.GONE);
            return view;
        }
    }


}
