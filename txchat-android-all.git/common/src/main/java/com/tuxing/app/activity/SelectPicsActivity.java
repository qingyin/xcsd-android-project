package com.tuxing.app.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.view.CheckableGridViewItem;
import com.tuxing.sdk.db.entity.UploadFile;
import com.tuxing.sdk.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 亲子圈相册,勾选照片
 */
public class SelectPicsActivity extends BaseActivity implements
        OnScrollListener, OnItemClickListener {

    private GridView gridView;
    private Context mContext;
    private List<UploadFile> listItems;
    // 标志gridviewitem是否被选中
    private Map<Integer, Boolean> mSelectMap;
    private Button sendBtn;
    private Button previewBtn;
    private GridAdapter adapter;
    private int max = 20;
    private String folder;
    private boolean mBusy = false;
    private ImageSize imageSize;
    private boolean isSync = false;


    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new GridAdapter(mContext);
            gridView.setAdapter(adapter);
            disProgressDialog();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();

        max = this.getIntent().getIntExtra("max", max);
        folder = this.getIntent().getStringExtra("folder");
        isSync = getIntent().getBooleanExtra("isSync", false);
        setContentLayout(R.layout.select_pics_layout);

        listItems = new ArrayList<>();
        mSelectMap = new HashMap<>();
        imageSize = new ImageSize(160, 160);
        initViews();
        findPics();
    }

    private void initViews() {
        setTitle(folder.substring(folder.lastIndexOf("/") + 1));
        setLeftBack("", true, false);
        setRightNext(true, "", 0);

        sendBtn = (Button) findViewById(R.id.btnSend);
        LinearLayout tabslayout = (LinearLayout) findViewById(R.id.tabs);
        previewBtn = (Button) findViewById(R.id.btnPreview);
        tabslayout.setBackgroundResource(getResources().getIdentifier("tab_bg", "drawable", SelectPicsActivity.this.getPackageName()));
        sendBtn.setOnClickListener(this);
        previewBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btnPreview) {//预览
            ArrayList<String> fileUris = getSelectedPics();
            if (fileUris.size() > 0) {
                NewPicActivity.invoke(this, fileUris.get(0), false, getSelectedPics());
            } else {
                showToast("选择照片后就可以浏览啦");
            }
        } else if (id == R.id.btnSend) {//发送
            if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion()) && isSync) {//非家长
                showDialog("", "照片同步到班级相册?", "取消", "确定", false);
            } else {
                if (getCount() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("file_next_uris", getSeleData());
                    this.setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToast("还没有选择一张照片呢~");
                }
            }
        }
    }

    @Override
    public void onConfirm() {
        super.onConfirm();
        Intent intent = new Intent();
        intent.putExtra("file_next_uris", getSeleData());
        intent.putExtra("isSync", true);
        this.setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCancel() {
        super.onCancel();
        Intent intent = new Intent();
        intent.putExtra("file_next_uris", getSeleData());
        intent.putExtra("isSync", false);
        this.setResult(RESULT_OK, intent);
        finish();
    }

    private ArrayList<String> getSelectedPics() {
        ArrayList<String> fileUris = new ArrayList<>();
        for (int i = 0; i < listItems.size(); i++) {
            if (mSelectMap.get(i) != null && mSelectMap.get(i)) {
                String path = listItems.get(i).url;
                if (path != null) {
                    fileUris.add(path);
                }
            }
        }
        return fileUris;
    }

    private ArrayList<UploadFile> getSeleData() {
        ArrayList<UploadFile> fileUris = new ArrayList<>();
        for (int i = 0; i < listItems.size(); i++) {
            if (mSelectMap.get(i) != null && mSelectMap.get(i)) {
                fileUris.add(listItems.get(i));
            }
        }
        return fileUris;
    }

    private void findPics() {
        gridView = (GridView) findViewById(R.id.grid);
        showProgressDialog(SelectPicsActivity.this, "", true, null);
        new Thread(new ScanAlbumThread()).start();
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(this);
    }

    private class ScanAlbumThread implements Runnable {
        @Override
        public void run() {
            getNoThumbnailImages();
        }
    }


    private void getNoThumbnailImages() {
        String[] projection = {Media._ID, Media.DATA, MediaStore.Images.Media.DATE_ADDED};
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
                    String img_date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                    if (!TextUtils.isEmpty(path) && path.substring(0, path.lastIndexOf("/")).equals(folder)) {
                        /* 取得扩展名 */
                        String end = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
                        if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
                            if (new File(path).exists()) {
                                // Do something with the values.
                                UploadFile mClassPicture = new UploadFile(path, Long.valueOf(img_date + "000"));
                                listItems.add(mClassPicture);
                            }
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            mHandler.sendEmptyMessage(0);
        }
    }


	/*----------------------------------------------GridAdapter------------------------------------------------*/

    class GridAdapter extends BaseAdapter {

        public GridAdapter(Context ctx) {
            mContext = ctx;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listItems.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            CheckableGridViewItem item;
            if (convertView == null) {
                item = new CheckableGridViewItem(mContext);
                item.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
            } else {
                item = (CheckableGridViewItem) convertView;
            }
            item.setChecked(mSelectMap.get(position) == null ? false
                    : mSelectMap.get(position));

            String path = listItems.get(position).url;
            String imageUri = "file://" + path; //SD卡图片
//            ImageLoader.getInstance().displayImage(imageUri, new ImageViewAware(item.getImageView()), ImageUtils.DIO_DOWN, imageSize, null, null);
            ImageLoader.getInstance().displayImage(imageUri, new ImageViewAware(item.getImageView()), ImageUtils.DIO_DOWN);
            return item;
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                mBusy = false;
                adapter.notifyDataSetChanged();
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mBusy = true;
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
                mBusy = true;
                break;
        }
    }

    private void setBtnChanged() {
        sendBtn.setText(formatString(getCount()));
    }

    private String formatString(int count) {
        if (count == 0) {
            return "确定";
        }
        return String.format(getString(R.string.btn_sends), count);
    }

    private int getCount() {
        int count = 0;
        for (int i = 0; i < listItems.size(); i++) {
            if (mSelectMap.get(i) != null && mSelectMap.get(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        CheckableGridViewItem item = (CheckableGridViewItem) view;
        if (!item.isChecked() && getCount() == max) {
            Toast.makeText(mContext, getString(R.string.max_send_pics, max), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        item.toggle();
        mSelectMap.put(position, item.isChecked());

        setBtnChanged();
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, SelectPicsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
