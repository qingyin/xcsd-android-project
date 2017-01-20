package com.tuxing.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.polites.android.GestureImageView;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.*;
import com.tuxing.app.view.MyViewPager.MyImageAdapter;
import com.tuxing.sdk.utils.IOUtils;

import java.io.*;
import java.util.*;

/**
 * 照片浏览界面
 */
public class NewPicActivity extends BaseActivity implements
		OnPageChangeListener, View.OnClickListener, OnTouchListener {
	private GestureDetector mLongPressGesture;
	// 网络图片视图
	private String fileNextUri;
	private boolean isNet;
	private boolean isSetBItmap;
	private ArrayList<String> fileUris;
	private TextView countText;
	private String TAG = NewPicActivity.class.getSimpleName();
	private int curIndex;
	private ViewPager mPager;
	private ImageAdapter mAdapter;
	private String filePath;
	private DownloadTask downToask;
	Bitmap currentBitmap = null;
	public static List<String> fileList = Collections
			.synchronizedList(new ArrayList<String>());
	public static LruCache<String, Bitmap> lruCache;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_view_layout);
		mPager = (ViewPager) findViewById(R.id.pager);

		mLongPressGesture = new GestureDetector(this,
				new SimpleOnGestureListener() {
					public boolean onSingleTapConfirmed(MotionEvent e) {
						finish();
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						showBtnDialog(new String[] {
								getString(R.string.btn_save_phone),
								getString(R.string.btn_cancel) });
					}
				});
		mLongPressGesture.setIsLongpressEnabled(true);
		creatLrucash();
		init();
	}

	/**
	 *
	 * 加载图片
	 *
	 */

	private void init() {
		fileNextUri = getIntent().getStringExtra("file_next_uri");
		isNet = getIntent().getBooleanExtra("is_net", false);
		isSetBItmap = getIntent().getBooleanExtra("is_setbitmap", false);

		fileUris = getIntent().getStringArrayListExtra("file_next_uris");
		countText = (TextView) findViewById(R.id.textCount);
		if (fileUris != null && fileUris.size() > 1) {
			curIndex = fileUris.indexOf(fileNextUri);
			countText.setText((curIndex + 1) + "/" + fileUris.size());
		} else {
			countText.setVisibility(View.GONE);
		}

		mAdapter = new ImageAdapter();
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(this);

		if (curIndex > 0) {
			mPager.setCurrentItem(curIndex);
		}
	}

	private void showPic(final View pagerView, final ImageView image, String key) {
		image.setOnTouchListener(this);
		filePath = SysConstants.FILE_DIR_ROOT + Utils.getImageToken(key)
				+ "_min";
		if (key.startsWith("http")) {
			if (isNet) {
				final File file = new File(filePath);
				if (file.exists()) {
					setImageBitmap(image, filePath);
					// wivPic.setImageBitmap(BitmapFactory.decodeFile(f));
					// pagerView.findViewById(R.id.llLoading).setVisibility(View.GONE);
				} else {
					pagerView.findViewById(R.id.llLoading).setVisibility(
							View.VISIBLE);
					if (isSetBItmap) {
						if (!fileList.contains(key)) {
							fileList.add(key);
							Map<String, String> maps = new HashMap<String, String>();
							downloadImage(pagerView, image, key, maps);
						}
					} else {
						if (!fileList.contains(filePath)) {
							fileList.add(filePath);
							beginDownload(pagerView, image, filePath, key);
						}
					}
				}
			} else {
				pagerView.findViewById(R.id.llLoading).setVisibility(View.GONE);
				setImageBitmap(image, filePath);
			}
		} else {
			setImageBitmap(image, key);
		}

	}

	private void beginDownload(final View pagerView, final ImageView image,
							   final String filePath, final String key) {
		// TODO 去网络加载图片
		downToask = (DownloadTask) new DownloadTask(key, filePath,
				new DownloadTaskListener() {

					@Override
					public void onStartDownload() {
						// TODO Auto-generated method stub
						pagerView.findViewById(R.id.llLoading).setVisibility(
								View.VISIBLE);
					}

					@Override
					public void onProgress(long current, long total) {
						TextView tvProgress = (TextView) pagerView
								.findViewById(R.id.tvProgress);
						tvProgress.setText((current * 100 / total) + "%");

						TextView tvDownloadSize = (TextView) pagerView
								.findViewById(R.id.tvDownloadSize);
						tvDownloadSize.setText((current / 1024) + "kb/"
								+ (total / 1024) + "kb");
					}

					@Override
					public void onFinished(int resultCode, String filePath) {
						fileList.remove(filePath);
						if (resultCode == 1) {
							final File file = new File(filePath);
							if (file.exists()) {
								setImageBitmap(image, filePath);
							}
							MyLog.getLogger(TAG).d(
									"下载成功  filePath= " + filePath);
							pagerView.findViewById(R.id.llLoading)
									.setVisibility(View.GONE);
						} else {
							Toast.makeText(NewPicActivity.this,
									R.string.net_slowly, Toast.LENGTH_SHORT)
									.show();
							pagerView.findViewById(R.id.llLoading)
									.setVisibility(View.GONE);
							MyLog.getLogger(TAG).d(
									"下载失败  filePath= " + filePath);
							setImageBitmap(image, "");

						}

					}
				}).execute();
	}

	private void downloadImage(final View pagerView, final ImageView image,
							   final String remote, final Map<String, String> headers) {
		final String localPath = SysConstants.FILE_DIR_ROOT
				+ Utils.getImageToken(remote) + "_min";
		final String tempPath = SysConstants.FILE_DIR_TEMP
				+ Utils.getImageToken(remote) + "_min";
		pagerView.findViewById(R.id.llLoading).setVisibility(View.VISIBLE);
		final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {
				fileList.remove(localPath);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final File file = new File(tempPath);
						if (file.exists()) {
							Utils.copyFile(tempPath, localPath);
							setImageBitmap(image, localPath);
							delete(new File(tempPath));
						} else {
							setImageBitmap(image, "");
							delete(new File(tempPath));
						}
						pagerView.findViewById(R.id.llLoading).setVisibility(
								View.GONE);
					}
				});
			}

			public void onError(int error, String msg) {
				fileList.remove(localPath);
				runOnUiThread(new Runnable() {
					public void run() {
						setImageBitmap(image, "");
						Toast.makeText(NewPicActivity.this,
								R.string.net_slowly, Toast.LENGTH_SHORT).show();
						pagerView.findViewById(R.id.llLoading).setVisibility(
								View.GONE);
						delete(new File(tempPath));
					}
				});
			}

			public void onProgress(final int progress, String status) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TextView tvProgress = (TextView) pagerView
								.findViewById(R.id.tvProgress);
						tvProgress.setText(progress + "%");
					}
				});
			}
		};

		EMChatManager.getInstance().downloadFile(remote, tempPath, headers,
				callback);

	}

	private void setImageBitmap(ImageView image, String filePath) {

		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();
		if (!TextUtils.isEmpty(filePath)) {
			Double fileSize = Utils.getFileSize(new File(filePath));
			if (lruCache.get(filePath) != null) {
				currentBitmap = lruCache.get(filePath);
				image.setImageBitmap(currentBitmap);
			}else{
//				if (fileSize < 0.4 && (max - total)/1024 > 3000) {
					currentBitmap = Utils.revitionImageSize(filePath,Utils.getDisplayWidth(mContext),Utils.getDisplayHeight(mContext));
//				} else {
//					currentBitmap = Utils.revitionImageSize(filePath,SysConstants.IMAGEIMPLESIZE_1024);
//				}
				if (currentBitmap != null) {
					image.setImageBitmap(currentBitmap);
					lruCache.put(filePath, currentBitmap);
				} else {
					image.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defal_down_lym_fail));
				}
			}

		} else {
			image.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defal_down_lym_fail));
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mLongPressGesture.onTouchEvent(event);
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if (id == R.id.wivPhoto) {
			finish();
		} else {
		}
		super.onClick(arg0);

	}

	@Override
	public void onclickBtn1() {
		fileNextUri = fileUris.get(curIndex);
		String currentFilePath = SysConstants.FILE_DIR_ROOT + Utils.getImageToken(fileNextUri) + "_min";
		if (new File(SysConstants.DCIM_DIR_ROOT).exists() == false) {
			try {
				new File(SysConstants.DCIM_DIR_ROOT).mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (fileNextUri.startsWith("http")) {
			try {
				copyFile(currentFilePath,SysConstants.DCIM_DIR_ROOT+ Utils.getImageToken(fileNextUri));
				Toast.makeText(mContext,String.format(
								mContext.getString(R.string.save_pic),
								SysConstants.DCIM_DIR_ROOT+ Utils.getImageToken(fileNextUri)),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String destFile = SysConstants.DCIM_DIR_ROOT+ fileNextUri.split("/")[fileNextUri.split("/").length - 1];
			if (!destFile.endsWith(".jpg")) {
				destFile = destFile + ".jpg";
			}
			copyFile(fileNextUri, destFile);
			Toast.makeText(
					mContext,
					String.format(mContext.getString(R.string.save_pic),
							destFile), Toast.LENGTH_LONG).show();
		}
	}

	private void copyFile(String srcFile, String destFile) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024 * 64];
			int size = 0;
			while ((size = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, size);
			}
			fos.flush();
			Utils.scanOneFile(mContext, destFile);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			IOUtils.safeClose(fis);
			IOUtils.safeClose(fos);
		}
	}

	@Override
	public void onclickBtn2() {
		super.onclickBtn2();

	}

	public static void invoke(Context context, String fileNextUri, boolean isNet) {
		Intent intent = new Intent(context, NewPicActivity.class);
		intent.putExtra("file_next_uri", fileNextUri);
		intent.putExtra("is_net", isNet);
		ArrayList<String> fileUris = new ArrayList<String>();
		fileUris.add(fileNextUri);
		intent.putStringArrayListExtra("file_next_uris", fileUris);
		context.startActivity(intent);

	}

	public static void invoke(Context context, String fileNextUri,
							  boolean isNet, ArrayList<String> picUris, boolean isSetBItmap) {
		Intent intent = new Intent(context, NewPicActivity.class);
		intent.putExtra("file_next_uri", fileNextUri);
		intent.putExtra("is_net", isNet);
		intent.putExtra("is_setbitmap", isSetBItmap);
		intent.putStringArrayListExtra("file_next_uris", picUris);
		context.startActivity(intent);
	}

	public static void invoke(Context context, String fileNextUri,
							  boolean isNet, ArrayList<String> picUris) {
		Intent intent = new Intent(context, NewPicActivity.class);
		intent.putExtra("file_next_uri", fileNextUri);
		intent.putExtra("is_net", isNet);
		intent.putStringArrayListExtra("file_next_uris", picUris);
		context.startActivity(intent);
	}

	public class ImageAdapter extends MyImageAdapter {

		public GestureImageView getItem(ViewPager pager, int position) {
			return (GestureImageView) pager.findViewWithTag("image" + position);
		}

		@Override
		public int getCount() {
			return fileUris.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			GestureImageView imgDisplay;

			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View viewLayout = inflater.inflate(R.layout.pic_layout, container,
					false);
			imgDisplay = (GestureImageView) viewLayout
					.findViewById(R.id.wivPhoto);
			imgDisplay.setTag("image" + position);

			((ViewPager) container).addView(viewLayout);
			if (!TextUtils.isEmpty(fileUris.get(position))) {
				showPic(viewLayout, imgDisplay, fileUris.get(position));
			}

			return viewLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((RelativeLayout) object);

		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int pos) {
		if (curIndex != pos) {
			GestureImageView imageView = (GestureImageView) mAdapter.getItem(
					mPager, curIndex);

			if(imageView != null) {
				imageView.reset();
			}
		}
		curIndex = pos;
		if (countText.getVisibility() == View.VISIBLE) {
			countText.setText((pos + 1) + "/" + fileUris.size());
		}
	}

	public static  void creatLrucash(){
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 6;
		//内存缓存
		lruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return (bitmap.getRowBytes() * bitmap.getHeight())/ 1024;

			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
										Bitmap oldValue, Bitmap newValue) {
				// 不要在这里强制回收oldValue，因为从LruCache清掉的对象可能在屏幕上显示着，
				// 这样就会出现空白现象
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};
	}


	@Override
	protected void onPause() {

		if (downToask != null) {
			downToask.cancel(true);
		}
		super.onPause();
	}

	public void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		fileList.clear();
		if (currentBitmap != null && !currentBitmap.isRecycled()) {
			// 回收并且置为null
			currentBitmap.recycle();
			currentBitmap = null;
		}
		System.gc();
		super.onDestroy();
	}

}
