package com.tuxing.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.tuxing.app.easemob.task.LoadLocalBigImgTask;
import com.tuxing.app.util.DownloadTask;
import com.tuxing.app.util.DownloadTaskListener;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 圆角图片视图
 * 
 * @author zhaomeng
 * 
 */
public class RoundAngleImageView extends ImageView implements
        DownloadTaskListener {
	public static int ANGLE = 5;

	private final static String LOG_TAG = RoundAngleImageView.class.getName();
	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	private Context mContext;
	private String filePath;
	public static LruCache<String, Bitmap> lruCache;
	private Bitmap bitmap = null;
	private Bitmap drawable = null;
	public static List<String> fileList = new ArrayList<String>();
	public final int LOADLOCALBITMAP = 1;
	public final int LOADFINISH = 2;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1){
				case LOADLOCALBITMAP:
					String bitmapKey = msg.getData().getString("key");
					bitmap = (Bitmap)msg.obj;
					if (bitmap != null) {
						setImageBitmap(bitmap);
						lruCache.put(filePath, bitmap);
					} else {
						if (!fileList.contains(filePath)) {
							fileList.add(filePath);
							beginDownload(bitmapKey);
						}
					}
					break;
				case LOADFINISH:
					bitmap = (Bitmap)msg.obj;
					if (bitmap != null) {
						setImageBitmap(bitmap);
						synchronized (lruCache) {
							lruCache.put(filePath, bitmap);
							fileList.remove(filePath);
						}
					}
					break;
			}


		}
	};

	static {
		creatLrucash();
	}

	public RoundAngleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public RoundAngleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public RoundAngleImageView(Context context) {
		super(context);
		mContext = context;
	}

	// }

	/**
	 * 加载图片
	 * 
	 */

	public void setImageUrl(final String key, int resId) {
			if (key == null || key.length() == 0
					|| (!key.startsWith("http://") && !key.startsWith("https://"))) {
				if (lruCache.get(String.valueOf(resId)) != null) {
					bitmap = lruCache.get(String.valueOf(resId));
					if (bitmap != null) {
						setImageBitmap(Utils.roundCorners(
								lruCache.get(String.valueOf(resId)), this, ANGLE));
					}
				} else if (resId != 0) {
					setImageBitmap(Utils.roundCorners(BitmapFactory.decodeResource(
							mContext.getResources(), resId), this, ANGLE));
					lruCache.put(String.valueOf(resId), BitmapFactory
							.decodeResource(mContext.getResources(), resId));
				}
				return;
			}
			filePath = SysConstants.FILE_DIR_ROOT + Utils.getImageToken(key);
			final File cacheFile = new File(filePath);
			if (lruCache.get(filePath) != null) {
				bitmap = lruCache.get(filePath);
				if (bitmap != null) {
					setImageBitmap(Utils.roundCorners(lruCache.get(filePath), this,
							ANGLE));
				} else {
					if (!fileList.contains(filePath)) {
						fileList.add(filePath);
						beginDownload(key);
					}
				}
			} else if (imageCache.containsKey(filePath)) {
				bitmap = imageCache.get(filePath).get();
				if (bitmap != null) {
					setImageBitmap(Utils.roundCorners(imageCache.get(filePath)
							.get(), this, ANGLE));
				} else {
					if (!fileList.contains(filePath)) {
						fileList.add(filePath);
						beginDownload(key);
					}
				}
			} else if (cacheFile.exists()) {

				final long total = Runtime.getRuntime().totalMemory();
				final long max = Runtime.getRuntime().maxMemory();
				new Thread(new Runnable() {
					@Override
					public void run() {
							bitmap = Utils.revitionImageSize(cacheFile.getAbsolutePath(),SysConstants.IMAGEIMPLESIZE_256);
						Message msg = handler.obtainMessage();
						Bundle b = new Bundle();
						b.putString("key", key);
						msg.arg1 = LOADLOCALBITMAP;
						msg.obj = bitmap;
						msg.setData(b);
						handler.sendMessage(msg);
					}
				}).start();

//				if(Utils.getFileSize(cacheFile) > 0.4){
//					bitmap = Utils.revitionImageSize(cacheFile.getAbsolutePath());
//				}else{
//					bitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
//				}
//				if (bitmap != null)
//					bitmap = Utils.roundCorners(bitmap, this,
//							Utils.dip2px(mContext, ANGLE));
//				if (bitmap != null) {
//					setImageBitmap(bitmap);
//					lruCache.put(filePath, bitmap);
//				} else {
//					if (!fileList.contains(filePath)) {
//						fileList.add(filePath);
//						beginDownload(key);
//					}
//				}
			} else {
				if (!fileList.contains(filePath)) {
					fileList.add(filePath);
					beginDownload(key);
				}
			}

	}

	private void beginDownload(String url) {

		try {
			if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 12){
				new DownloadTask(url, filePath, this).executeOnExecutor(DownloadTask.DOWNLOAD_THREAD_POOL_EXECUTOR);
			}else{
				new DownloadTask(url, filePath, this).execute();
			}
		}catch (Exception e){
			//FIXME: the thread pool may be full and discard the request
		}

	}

	public static void creatLrucash() {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 10;
		// 内存缓存
		lruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;

			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				// 不要在这里强制回收oldValue，因为从LruCache清掉的对象可能在屏幕上显示着，
				// 这样就会出现空白现象
				imageCache.put(key, new SoftReference<Bitmap>(oldValue));
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};
	}

	@Override
	public void onStartDownload() {

	}

	@Override
	public void onProgress(long current, long total) {

	}

	@Override
	public void onFinished(int resultCode, final String filePath) {
		if (resultCode == 1) {
			final File file = new File(filePath);
			if (file.exists()) {

				final long total = Runtime.getRuntime().totalMemory();
				final long max = Runtime.getRuntime().maxMemory();
				new Thread(new Runnable() {
					@Override
					public void run() {
							bitmap = Utils.revitionImageSize(file.getAbsolutePath(),SysConstants.IMAGEIMPLESIZE_256);
						Message msg = handler.obtainMessage();
						msg.arg1 = LOADFINISH;
						msg.obj = bitmap;
						handler.sendMessage(msg);
					}
				}).start();


//				if(Utils.getFileSize(file) > 0.4){
//					bitmap = Utils.revitionImageSize(file.getAbsolutePath());
//					}else{
//					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//					}
//				if (bitmap != null) {
//					drawable = Utils.roundCorners(bitmap,
//							RoundAngleImageView.this, ANGLE);
//					if (drawable != null) {
//						setImageBitmap(drawable);
//							synchronized (lruCache) {
//								lruCache.put(filePath, drawable);
//								fileList.remove(filePath);
//							}
//					}
//				}
			}
		} else {
			fileList.remove(filePath);
		}
	}


}
