package com.tuxing.app.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.tuxing.app.R;
import com.tuxing.app.adapter.TestTopAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.util.PingYinUtil;
import com.tuxing.app.util.PinyinComparator;
import com.tuxing.app.util.SideBar;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.CharacterParser;
import com.tuxing.app.view.CustomDialog;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.event.GetShareDepartmentMemberEvent;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ShareChooseClassActivity extends BaseActivity implements OnItemClickListener, TestTopAdapter.MyItemClickListener {
    private ListView classListView;
    private SelectReceiverClassAdapter classAdapter;
    public String TAG = ShareChooseClassActivity.class.getSimpleName();
    private final int REQUESTCODE = 0x100;
    private final int GROUP = 120;

    private List<DepartmentMember> departmentall;//全部
    private List<DepartmentMember> unchangedep;//全部
    private int person_num_all = 0;//全部的人
    private List<DepartmentMember> personMap;
    private List<DepartmentMember> findperson;
    private int count = 1;

    private EditText et_input;
    private EditText et_input_select;
    private Dialog forshareDialog;
    private Dialog sharefinishDialog;
    private String url = "";
    private String leavemessage = "";

    private EMConversation conversation;
    private boolean stopThread = false;
    private SideBar sideBar;

    private RecyclerView mRecyclerView;
    private static List<DepartmentMember> mDatas;
    private TestTopAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int countcheck = 1;
    private int countcheckother = 1;
    private boolean isdelete = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.share_choose_class_layout);

        showProgressDialog(this, "请稍等……", true, null);
        initView();
        initData();
    }

    private void initView() {
        setTitle(getString(R.string.select_receive));
        setLeftBack("取消", false, false);
        setRightNext(false, "", 0);
        classListView = (ListView) findViewById(R.id.select_receiver_class_listview);
        classListView.setOnItemClickListener(this);

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = classAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    classListView.setSelection(position);
                }
            }
        });

        mDatas = new ArrayList<>();
        mAdapter = new TestTopAdapter(this, mDatas);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_horizontal_show);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(this);
        //垂直方向
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
//		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        et_input_select = (EditText) findViewById(R.id.et_input_select);
        et_input_select.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
//                                              事件
                    KeyDelete(et_input_select.getText().toString(), 1);
                    return false;
                }
                return false;
            }
        });

        et_input_select.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InputTextChange(et_input_select.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateAdapter(departmentall);
            }
        });


        et_input = (EditText) findViewById(R.id.et_input);
        et_input.setOnKeyListener(new View.OnKeyListener() {
                                      @Override
                                      public boolean onKey(View v, int keyCode, KeyEvent event) {
                                          if (keyCode == KeyEvent.KEYCODE_DEL
                                                  && event.getAction() == KeyEvent.ACTION_DOWN) {
//                                              事件
                                              KeyDelete(et_input.getText().toString(), 0);
                                              return false;
                                          }
                                          return false;
                                      }
                                  }

        );
        et_input.addTextChangedListener(new

                                                TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                    }

                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                showToast("你输入了啊");
                                                        InputTextChange(et_input.getText().toString());

                                                    }

                                                    @Override
                                                    public void afterTextChanged(Editable s) {
                                                        updateAdapter(departmentall);
                                                        if (!et_input.getText().toString().equals("")) {
                                                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                                            imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
                                                        }
                                                    }
                                                }

        );
        departmentall = new ArrayList<>();
        //updateAdapter(departmentall);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        url = getIntent().getStringExtra("url");

        personMap = new ArrayList<>();
//        departmentall = new ArrayList<>();
        findperson = new ArrayList<>();
        unchangedep = new ArrayList<>();
        getService().getContactManager().getDepartmentforshare();

//        if(SysConstants.members.size()>0&&SysConstants.memberteacher.size()>0){
//            departmentall.addAll(SysConstants.memberteacher);
//            departmentall.addAll(SysConstants.members);
//
//            unchangedep.addAll(departmentall);
//
//            updateAdapter(departmentall);
//            disProgressDialog();
//        }else{
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        sleep(50);
//                        getService().getContactManager().getDepartmentforshare();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
////                getService().getContactManager().getDepartmentforshare();
//                }
//            }).start();
//        }
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
        if (personMap.size() == 0) {
            showToast("请选择联系人");
        } else {
            showshareDialog();
        }
    }

    public void Editviewchange(int i) {
        if (i <= 6) {
            et_input.setVisibility(View.VISIBLE);
            et_input_select.setVisibility(View.INVISIBLE);
            if (i > 0) {
                et_input.setGravity(Gravity.CENTER_VERTICAL);
                et_input.setPadding(i * 130 + 20, 0, 0, 0);
            } else {
                et_input.setGravity(Gravity.CENTER);
                et_input.setPadding(0, 0, 0, 0);
            }
        } else {
            et_input.setVisibility(View.GONE);
            et_input_select.setVisibility(View.VISIBLE);
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }


    public void KeyDelete(String input, int inputtype) {
        boolean type = false;
        if (inputtype == 0) {
            type = countcheck % 2 == 0;
        } else {
            type = countcheckother % 2 == 0;
        }
        if (input.equals("")) {
//            240
            if (type) {
                if (personMap.size() > 0) {
                    int i = personMap.size() - 1;
                    personMap.remove(i);
                    mDatas.remove(i);

                    person_num_all = person_num_all - 1;
                    setRightNext(true, "确定(" + personMap.size() + ")", 0);

                    classAdapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                    Editviewchange(personMap.size());
                }
//                showToast("删除被监听");
            } else {
                if (personMap.size() > 0) {
                    isdelete = true;
                    mDatas.get(mDatas.size() - 1).getUser().setGuarder("delete");
                    mAdapter.notifyDataSetChanged();
                }
            }
            if (inputtype == 0) {
                countcheck = countcheck + 1;
            } else {
                countcheckother = countcheckother + 1;
            }
        }
    }

    public void InputTextChange(String input) {

        hiddenInput(ShareChooseClassActivity.this);

        List<DepartmentMember> find = new ArrayList<DepartmentMember>();
        if (input.equals("")) {
            if (findperson.size() > 0) {
                departmentall.clear();
                departmentall.addAll(unchangedep);
                updateAdapter(departmentall);
            }
        } else {
            if (input.matches("^[A-Za-z]+$") || input.matches("[\\u4e00-\\u9fa5]+")) {
                String inputall = input;
                String inputs = "";
                String inputtwo = "";
                if (inputall.length() > 1) {
                    inputtwo = classAdapter.setZmutwo(input);
                } else {
                    inputs = classAdapter.setZmu(input);
                }
//
                if (inputs.matches("^[A-Za-z]+$") || inputtwo.matches("^[A-Za-z]+$")) {
                    for (int i = 0; i < unchangedep.size(); i++) {
                        if (inputall.length() >= 2) {
                            if (classAdapter.setZmutwo(unchangedep.get(i).getUser().getNickname()).equalsIgnoreCase(inputtwo)) {
                                find.add(unchangedep.get(i));
                            }
                        } else if (inputall.length() == 1) {
                            if (classAdapter.setZmu(unchangedep.get(i).getUser().getNickname()).equalsIgnoreCase(inputs)) {
                                find.add(unchangedep.get(i));
                            }
                        }
                    }
                    findperson.clear();
                    findperson.addAll(find);
                    departmentall.clear();
                    departmentall.addAll(findperson);
//                            updateAdapter(departmentall);
                } else {
                    showToast("请输入中英文");
                }
            } else {
                showToast("请输入中英文");
            }
        }
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    /**
     * 显示数据
     */
    public void updateAdapter(List<DepartmentMember> list) {
        if (classAdapter == null) {
            classAdapter = new SelectReceiverClassAdapter(mContext, list);
            classListView.setAdapter(classAdapter);
        } else {
            classAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == REQUESTCODE) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
//
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopThread = true;//退出线程
    }

    @Override
    public void onItemClick(View view, int postion) {

    }

    class SelectReceiverClassAdapter extends BaseAdapter {

        private Context mContext;
        private List<DepartmentMember> classes;
        private CharacterParser characterParser;
        private String ABC = "";
        List<DepartmentMember> person;
        //定义hashMap 用来存放之前创建的每一项item

        public SelectReceiverClassAdapter(Context context, List<DepartmentMember> classList) {
            this.mContext = context;
            this.classes = classList;
            characterParser = CharacterParser.getInstance();
        }


        @Override
        public int getCount() {
            return classes.size();
        }

        @Override
        public Object getItem(int position) {
            return classes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HashMap<Integer, View> lmap = new HashMap<Integer, View>();
            final ViewHolder viewHolder;
            if (lmap.get(position) == null) {
//            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.share_choose_class_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.className = (TextView) convertView.findViewById(R.id.select_receiver_class_item_name);
                viewHolder.icon = (RoundImageView) convertView.findViewById(R.id.select_receiver_class_item_icon);
                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.select_receiver_class_item_cb);
                viewHolder.select_receiver_class_count = (TextView) convertView.findViewById(R.id.select_receiver_class_count);
                viewHolder.rl_check = (RelativeLayout) convertView.findViewById(R.id.rl_check);
                viewHolder.zimu = (TextView) convertView.findViewById(R.id.select_receiver_item_zimu);
                viewHolder.select_receiver_class_count.setVisibility(View.VISIBLE);
                viewHolder.title = (TextView) convertView.findViewById(R.id.select_receiver_item_title);
                viewHolder.title.setVisibility(View.GONE);

                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final DepartmentMember department = classes.get(position);
            viewHolder.select_receiver_class_count.setVisibility(View.GONE);
            viewHolder.icon.setImageUrl(department.getUser().getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            viewHolder.className.setText(department.getUser().getNickname());

            viewHolder.zimu.setVisibility(View.GONE);

//            int section = getSectionForPosition(position);
//            if (department.getUser().getType() == GROUP) {
//                viewHolder.zimu.setVisibility(View.GONE);
//            } else {
//                //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
//                if (position == getPositionForSection(section)) {
//                    viewHolder.zimu.setVisibility(View.VISIBLE);
//                    viewHolder.zimu.setText(setABC(department.getUser().getNickname()).toUpperCase());
//                } else {
//                    viewHolder.zimu.setVisibility(View.GONE);
//                }
//            }

            if (department.getUser().getType() == GROUP) {
                if (department.getUser().getNickname().equals("老师")) {
                    viewHolder.zimu.setVisibility(View.VISIBLE);
                    viewHolder.zimu.setText("群");
                } else {
                    viewHolder.zimu.setVisibility(View.GONE);
                }
            } else {
                String sortStr = setZmu(classes.get(position).getUser().getNickname());
                char firstChar = sortStr.toUpperCase().charAt(0);

                char previewChar = position > 0 && classes.get(position - 1).getUser().getType() != GROUP ? setZmu(classes.get(position - 1).getUser().getNickname()).charAt(0) : ' ';  //前一个字符
                char currentChar = firstChar;                //当前字符

                if (currentChar != previewChar) {//不相等时
                    viewHolder.zimu.setVisibility(View.VISIBLE);
                    viewHolder.zimu.setText(sortStr);
                } else {
                    viewHolder.zimu.setVisibility(View.GONE);
                }

                if (position > 0 && classes.get(position - 1).getUser().getType() == GROUP) {
                    viewHolder.title.setVisibility(View.VISIBLE);
                } else if (position == 0 && classes.get(position).getUser().getType() != GROUP) {
                    viewHolder.title.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.title.setVisibility(View.GONE);
                }

            }



            if (personMap.contains(department)) {
                viewHolder.cb.setChecked(true);
            }else{
                viewHolder.cb.setChecked(false);
            }
            viewHolder.cb.setClickable(false);
//

            viewHolder.rl_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DepartmentMember departmentcheck = classes.get(position);
                    if (personMap.size() < 9) {
                        if (personMap.contains(departmentcheck)) {
                            personMap.remove(departmentcheck);
                            setRightNext(true, "确定(" + personMap.size() + ")", 0);

//                            选择联动取消
                            if (isdelete) {
                                isdelete = false;
                                mDatas.get(mDatas.size() - 1).getUser().setGuarder("nodelete");
                            }
                            mDatas.remove(departmentcheck);
                            mAdapter.notifyDataSetChanged();
                            Editviewchange(personMap.size());
                        }else{
                            personMap.add(departmentcheck);
                            setRightNext(true, "确定(" + personMap.size() + ")", 0);

//                            选择联动添加
                            if (isdelete) {
                                isdelete = false;
                                if (mDatas.size() > 0) {
                                    mDatas.get(mDatas.size() - 1).getUser().setGuarder("nodelete");
                                }
                            }

                            mDatas.add(departmentcheck);
                            mDatas.get(mDatas.size() - 1).getUser().setGuarder("nodelete");
                            mAdapter.notifyDataSetChanged();
                            Editviewchange(personMap.size());

                            if (!et_input.getText().toString().equals("") || !et_input_select.getText().equals("")) {
                                if (personMap.size() > 6) {
                                    et_input_select.setText("");
                                    et_input.setText("");
                                } else {
                                    et_input.setText("");
                                }
                            }
                        }
                    }else{
                        if (personMap.contains(departmentcheck)) {
                            personMap.remove(departmentcheck);
                            setRightNext(true, "确定(" + personMap.size() + ")", 0);

//                            选择联动取消
                            if (isdelete) {
                                isdelete = false;
                                mDatas.get(mDatas.size() - 1).getUser().setGuarder("nodelete");
                            }
                            mDatas.remove(departmentcheck);
                            mAdapter.notifyDataSetChanged();
                            Editviewchange(personMap.size());
                        }else{
                            showToast("数量不能超过9");
                        }
                    }
                    notifyDataSetChanged();
                }
            });

            return convertView;


        }

        class ViewHolder {
            public RoundImageView icon;
            public TextView className;
            public CheckBox cb;
            public TextView select_receiver_class_count;
            public RelativeLayout rl_check;
            TextView zimu;
            TextView title;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
//		return list.get(position).getUser().getUsername().charAt(0);
            return setZmuChoice(classes.get(position).getUser().getNickname()).charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = setZmuChoice(classes.get(i).getUser().getNickname());
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        }

        public String setZmuChoice(String name) {
            String zimu;

            // 正则表达式，判断首字母是否是英文字母
            if ("".equals(name) || name == null) {
                zimu = "#";
                return zimu;
            } else {
                String pinyin = characterParser.getSelling(name);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    zimu = sortString.toUpperCase();
                } else {
                    zimu = "#";
                }
            }
            return zimu;
        }

        public String setZmu(String name) {
            String zimu;

            // 正则表达式，判断首字母是否是英文字母
            if ("".equals(name) || name == null) {
                zimu = "#";
                return zimu;
            } else {
//                String pinyin = converterToPinYin(name);
                String pinyin = PingYinUtil.getPingYin(name);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    zimu = sortString.toUpperCase();
                } else {
                    zimu = "#";
                }
            }
            return zimu;
        }

        public String setZmutwo(String name) {
            String zimu = "";

            // 正则表达式，判断首字母是否是英文字母
            if ("".equals(name) || name == null) {
                zimu = "#";
                return zimu;
            } else if (name.matches("^[A-Za-z]+$")) {
                if (name.length() >= 2) {
                    zimu = name.substring(0, 2);
                } else {
                    zimu = name;
                }
                return zimu;
            } else {
                String pin = "";
                if (name.length() >= 2) {
                    pin = name.substring(1, 2);

                    String pinyin = PingYinUtil.getPingYin(name);
                    String sortString = pinyin.substring(0, 1).toUpperCase();

                    String pinyintwo = PingYinUtil.getPingYin(pin);
                    String sortStringtwo = pinyintwo.substring(0, 1).toUpperCase();

                    if (sortString.matches("[A-Z]") && sortStringtwo.matches("[A-Z]")) {
                        zimu = sortString.toUpperCase() + sortStringtwo.toUpperCase();
                    } else {
                        zimu = "#";
                    }
                }
            }
            return zimu;
        }


    }

    /**
     * 汉语拼音转换工具
     *
     * @param chinese
     * @return
     */
    public String converterToPinYin(String chinese) {
        String pinyinString = "";
        char[] charArray = chinese.toCharArray();
        // 根据需要定制输出格式，我用默认的即可
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        try {
            // 遍历数组，ASC码大于128进行转换
            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] > 128) {
                    // charAt(0)取出首字母
                    if (charArray[i] >= 0x4e00 && charArray[i] <= 0x9fa5) {    //判断是否中文
                        pinyinString += PinyinHelper.toHanyuPinyinStringArray(
                                charArray[i], defaultFormat)[0].charAt(0);
                    } else {                          //不是中文的打上未知，所以无法处理韩文日本等等其他文字
                        pinyinString += "?";
                    }
                } else {
                    pinyinString += charArray[i];
                }
            }
            return pinyinString;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            return null;
        }
    }


    public void showshareDialog() {
        if (forshareDialog != null && forshareDialog.isShowing()) {
            return;
        } else {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_dialog_forshare, null);
            forshareDialog = new CustomDialog(this, R.style.dialog_alert_style, 0);

            RoundImageView iv_forshare_one = (RoundImageView) view.findViewById(R.id.iv_forshare_one);
            RoundImageView iv_forshare_two = (RoundImageView) view.findViewById(R.id.iv_forshare_two);
            RoundImageView iv_forshare_three = (RoundImageView) view.findViewById(R.id.iv_forshare_three);
            RoundImageView iv_forshare_four = (RoundImageView) view.findViewById(R.id.iv_forshare_four);
            RoundImageView iv_forshare_five = (RoundImageView) view.findViewById(R.id.iv_forshare_five);

            RoundImageView iv_forshare_six = (RoundImageView) view.findViewById(R.id.iv_forshare_six);
            RoundImageView iv_forshare_seven = (RoundImageView) view.findViewById(R.id.iv_forshare_seven);
            RoundImageView iv_forshare_eight = (RoundImageView) view.findViewById(R.id.iv_forshare_eight);
            RoundImageView iv_forshare_nine = (RoundImageView) view.findViewById(R.id.iv_forshare_nine);

            final EditText et_forshare_message = (EditText) view.findViewById(R.id.et_forshare_message);

            TextView text_forshare_title = (TextView) view.findViewById(R.id.text_forshare_title);
            text_forshare_title.setText("[链接]" + SysConstants.titlename);


            if (personMap.size() == 1) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_two.setVisibility(View.GONE);
                iv_forshare_three.setVisibility(View.GONE);
                iv_forshare_four.setVisibility(View.GONE);
                iv_forshare_five.setVisibility(View.GONE);
                iv_forshare_six.setVisibility(View.GONE);
                iv_forshare_seven.setVisibility(View.GONE);
                iv_forshare_eight.setVisibility(View.GONE);
                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 2) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_three.setVisibility(View.GONE);
                iv_forshare_four.setVisibility(View.GONE);
                iv_forshare_five.setVisibility(View.GONE);
                iv_forshare_six.setVisibility(View.GONE);
                iv_forshare_seven.setVisibility(View.GONE);
                iv_forshare_eight.setVisibility(View.GONE);
                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 3) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_three.setImageUrl(personMap.get(2).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_four.setVisibility(View.GONE);
                iv_forshare_five.setVisibility(View.GONE);
                iv_forshare_six.setVisibility(View.GONE);
                iv_forshare_seven.setVisibility(View.GONE);
                iv_forshare_eight.setVisibility(View.GONE);
                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 4) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_three.setImageUrl(personMap.get(2).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_four.setImageUrl(personMap.get(3).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_five.setVisibility(View.GONE);
                iv_forshare_six.setVisibility(View.GONE);
                iv_forshare_seven.setVisibility(View.GONE);
                iv_forshare_eight.setVisibility(View.GONE);
                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 5) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_three.setImageUrl(personMap.get(2).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_four.setImageUrl(personMap.get(3).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_five.setImageUrl(personMap.get(4).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_six.setVisibility(View.GONE);
                iv_forshare_seven.setVisibility(View.GONE);
                iv_forshare_eight.setVisibility(View.GONE);
                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 6) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_three.setImageUrl(personMap.get(2).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_four.setImageUrl(personMap.get(3).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_five.setImageUrl(personMap.get(4).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_six.setImageUrl(personMap.get(5).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_seven.setVisibility(View.GONE);
                iv_forshare_eight.setVisibility(View.GONE);
                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 7) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_three.setImageUrl(personMap.get(2).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_four.setImageUrl(personMap.get(3).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_five.setImageUrl(personMap.get(4).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_six.setImageUrl(personMap.get(5).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_seven.setImageUrl(personMap.get(6).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_eight.setVisibility(View.GONE);
                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 8) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_three.setImageUrl(personMap.get(2).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_four.setImageUrl(personMap.get(3).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_five.setImageUrl(personMap.get(4).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_six.setImageUrl(personMap.get(5).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_seven.setImageUrl(personMap.get(6).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_eight.setImageUrl(personMap.get(7).getUser().getAvatar(), R.drawable.default_avatar);

                iv_forshare_nine.setVisibility(View.GONE);
            } else if (personMap.size() == 9) {
                iv_forshare_one.setImageUrl(personMap.get(0).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_two.setImageUrl(personMap.get(1).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_three.setImageUrl(personMap.get(2).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_four.setImageUrl(personMap.get(3).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_five.setImageUrl(personMap.get(4).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_six.setImageUrl(personMap.get(5).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_seven.setImageUrl(personMap.get(6).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_eight.setImageUrl(personMap.get(7).getUser().getAvatar(), R.drawable.default_avatar);
                iv_forshare_eight.setImageUrl(personMap.get(8).getUser().getAvatar(), R.drawable.default_avatar);
            }

            TextView sendAll = (TextView) view.findViewById(R.id.tv_forshare_sent);
            TextView sendCancle = (TextView) view.findViewById(R.id.tv_forshare_cancel);
            sendCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("取消");
                    forshareDialog.dismiss();
                }
            });
            sendAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog(ShareChooseClassActivity.this, "", true, null);
                    leavemessage = et_forshare_message.getText().toString() + "";
                    for (int i = 0; i < personMap.size(); i++) {
                        String chatid = personMap.get(i).getUser().getUserId() + "";
//                      发送消息
                        final int finalI = i;
                        count = i;

                        if (personMap.get(finalI).getUser().getType() != GROUP) {
                            if (personMap.get(finalI).getUser().getType() != 3){
                                for (int j = 0; j < personMap.get(i).getRelatives().size(); j++) {
                                    chatid = personMap.get(i).getRelatives().get(j).getUserId() + "";
                                    sendMessage(SysConstants.titlename, 1, chatid);
                                }
                            }else{
                                sendMessage(SysConstants.titlename, 1, chatid);
                            }
                        } else {
                            sendMessage(SysConstants.titlename, 2, chatid);
                        }
                    }

                    showToast("发送完成");
                    forshareDialog.dismiss();
                    sharefinishDialog();
                }
            });


            forshareDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            forshareDialog.setCanceledOnTouchOutside(false);
            forshareDialog.show();
        }
    }


    public void sharefinishDialog() {
        disProgressDialog();
        if (sharefinishDialog != null && sharefinishDialog.isShowing()) {
            return;
        } else {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_dialog_forshare_over, null);
            sharefinishDialog = new CustomDialog(this, R.style.dialog_alert_style, 0);


            TextView text_forshare_stay = (TextView) view.findViewById(R.id.tv_forshare_set_finish_continue);
            TextView text_forshare_message = (TextView) view.findViewById(R.id.tv_forshare_set_finish_message);


            text_forshare_stay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    sharefinishDialog.dismiss();
                }
            });
            text_forshare_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    forshareDialog.dismiss();
                    showToast("跳转中…");
                    if (!SysConstants.messagein) {
                        LyceumActivity.instance.finish();
                    } else {
                        ChatActivity.activityInstance.finish();
                        SysConstants.messagein = false;
                    }
                    WebSubDataActivity.instance.finish();

                    finish();
                }
            });


            forshareDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            forshareDialog.setCanceledOnTouchOutside(false);
            forshareDialog.show();
        }
    }

    private void sendMessage(String content, int type, String id) {

        if (content.length() > 0) {
            if (content.length() > 500) {
                content = StringUtils.substring(content, 0, 500);
            }
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            final EMMessage messageleave = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            if (type == 2) {
                message.setChatType(EMMessage.ChatType.GroupChat);
                messageleave.setChatType(EMMessage.ChatType.GroupChat);
                conversation = EMChatManager.getInstance().getConversationByType(id, EMConversation.EMConversationType.GroupChat);
            } else {
                conversation = EMChatManager.getInstance().getConversationByType(id, EMConversation.EMConversationType.Chat);
            }


            message.setAttribute("url", url);
            message.setAttribute("articleTitle", SysConstants.titlename);
            message.setAttribute("coverImageUrl", SysConstants.shareimage);

            TextMessageBody txtBody = new TextMessageBody("" + SysConstants.titlename);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(id);
            if (user != null && user.getNickname() != null)
                message.setAttribute("name", user.getNickname());
            else
                message.setAttribute("name", "");


            if (!leavemessage.equals("")) {
                TextMessageBody txtBodyleave = new TextMessageBody(leavemessage + "");
                // 设置消息body
                messageleave.addBody(txtBodyleave);
                messageleave.setReceipt(id);
                if (user != null && user.getNickname() != null)
                    messageleave.setAttribute("name", user.getNickname());
                else
                    messageleave.setAttribute("name", "");
            }

            if (conversation != null) {
                // 把messgage加到conversation中
//                conversation.addMessage(message);
                new Thread() {
                    public void run() {
                        try {
                            sleep(count * 50);
//                            conversation.addMessage(message);
                            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(int i, String s) {

                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });

                            if (!leavemessage.equals("")) {
                                EMChatManager.getInstance().sendMessage(messageleave, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start(); /* 开始运行运行线程 */


            }

        }
    }


    public void onEventMainThread(GetShareDepartmentMemberEvent event) {
        //班级成员
        if (isActivity) {
            if (event.getEvent() == GetShareDepartmentMemberEvent.EventType.GET_FOR_SHARE) {

                List<DepartmentMember> departmentperson =Collections.synchronizedList(new ArrayList<DepartmentMember>());//全部
                departmentperson.addAll(event.getDepartmentMembers());
//                departmentperson =event.getDepartmentMembers();
                if (!CollectionUtils.isEmpty(departmentperson)) {
                    Collections.sort(departmentperson, new PinyinComparator());
                }

                departmentall.addAll(event.departments());
                departmentall.addAll(departmentperson);

//                SysConstants.members.addAll(departmentperson);
//                SysConstants.memberteacher.addAll(event.departments());

                unchangedep.addAll(departmentall);
                updateAdapter(departmentall);
                disProgressDialog();
            }

        }
    }

}
