package com.tuxing.app.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.tuxing.app.R;
import com.tuxing.app.adapter.PicsFolderListAdapter;
import com.tuxing.app.adapter.PicsFolderListAdapter.PicsFolder;
import com.tuxing.app.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * 照片浏览界面
 * 
 * 
 */
public class PicListActivity extends BaseActivity implements OnItemClickListener {
    private String TAG = PicListActivity.class.getSimpleName();
    private ArrayList<PicsFolder> mDatas = new ArrayList<>();
    private HashMap<String, Integer> mFolders = new HashMap<>();
    public static final int REQUEST_CODE = 105;
    private PicsFolderListAdapter mAdapter;
    private ListView listView;
    private boolean isSync;
    private boolean isVideo;
    private int max = 20;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            disProgressDialog();
            if (mAdapter == null) {
                mAdapter = new PicsFolderListAdapter(PicListActivity.this, mDatas, PicListActivity.this);
                listView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        max = this.getIntent().getIntExtra("max", max);
        isSync = getIntent().getBooleanExtra("isSync", false);
        isVideo = getIntent().getBooleanExtra("isVideo", false);
        setContentLayout(R.layout.select_pics_list_layout);
        setTitle("选择照片");
        setLeftBack("", true, false);
        setRightNext(true, "", 0);
        initView();
        if (isVideo) {
            getVideoFolder();
        } else {
            bindData();
        }

    }

    private void initView() {

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
    }

    private void bindData() {
        showProgressDialog(mContext, "", false, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] projection = {Media._ID, Media.DATA};
                // 条件
                String selection = Media.MIME_TYPE + "=?";
                // 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)
                String[] selectionArgs = {"image/jpeg"};
                // 排序
                String sortOrder = Media.DATE_MODIFIED + " desc";

                Cursor cursor = mContext.getContentResolver().query(
                        Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, sortOrder);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            long id = cursor.getLong(cursor.getColumnIndex(Media._ID));
                            String path = cursor.getString(cursor.getColumnIndex(Media.DATA));
                    /* 取得扩展名 */
                            if (!TextUtils.isEmpty(path)) {
                                String end = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
                                if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
                                    if (new File(path).exists()) {
                                        // Do something with the values.
                                        String parent = new File(path).getParent();
                                        if (mFolders.containsKey(parent)) {
                                            mDatas.get(mFolders.get(parent)).counts++;
                                        } else {
                                            mFolders.put(parent, mDatas.size());
                                            PicsFolder item = new PicsFolder();
                                            item.folder = parent;
                                            item.counts++;
                                            item.lastPicFile = path;
                                            mDatas.add(item);
                                        }
                                    }
                                }
                            }

                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    Collections.sort(mDatas, new Comparator<PicsFolder>() {

                        @Override
                        public int compare(PicsFolder lhs, PicsFolder rhs) {
                            return rhs.counts - lhs.counts;
                        }
                    });
                    handler.sendEmptyMessage(0);
                }


            }
        }).start();

    }

    /**
     * 获取手机相册图片
     */
    private void getVideoFolder() {
        showProgressDialog(mContext, "", false, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] projection = {Media._ID, Media.DATA};
                // 排序
                String sortOrder = MediaStore.Images.Media.DATE_ADDED + " desc";

                Cursor cursor = getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, sortOrder);
                if (cursor != null)

                {
                    if (cursor.moveToFirst()) {
                        do {

                            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                            if (!TextUtils.isEmpty(path)) {
                                /** 取得扩展名 */
                                String end = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
                                if (end.equals("mp4")) {

                                    if (new File(path).exists()) {
                                        String parent = new File(path).getParent();
                                        if (mFolders.containsKey(parent)) {
                                            mDatas.get(mFolders.get(parent)).counts++;
                                        } else {
                                            mFolders.put(parent, mDatas.size());
                                            PicsFolder item = new PicsFolder();
                                            item.folder = parent;
                                            item.counts++;
                                            item.lastPicFile = path;
                                            mDatas.add(item);
                                        }
                                    }
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    Collections.sort(mDatas, new Comparator<PicsFolder>() {

                        @Override
                        public int compare(PicsFolder lhs, PicsFolder rhs) {
                            return rhs.counts - lhs.counts;
                        }
                    });
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (isVideo) {
//            Intent intent = new Intent(this, SelectAllVideoActivity.class);
//            intent.putExtra("folder", mDatas.get(position).folder);
//            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, SelectPicsActivity.class);
            intent.putExtra("folder", mDatas.get(position).folder);
            intent.putExtra("isSync", isSync);
            this.startActivityForResult(intent.putExtra("max", max), REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            this.setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        mDatas.clear();
    }
}
