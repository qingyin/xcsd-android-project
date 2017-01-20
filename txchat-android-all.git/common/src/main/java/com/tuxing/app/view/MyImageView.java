package com.tuxing.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import com.tuxing.app.R;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 方角角图片视图
 * 
 * 
 */
public class MyImageView extends ImageView implements DownloadTaskListener {
public static  int ANGLE = 5;
	
	public static final String SMALL_KEY = "?imageView/1/w/128/h/128/q/100";

	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	private Context mContext;
	private String filePath;
	public static LruCache<String, Bitmap> lruCache;
	public static List<String> fileList = Collections.synchronizedList(new ArrayList<String>());
	private Bitmap bitmap = null;
    private boolean mIsLym = false;
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
    static{
    	creatLrucash();
    }

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public MyImageView(Context context) {
		super(context);
		mContext = context;
	}


	/**
	 * 加载图片
	 * 
	 */

	public void setImageUrl(final String key, int resId,boolean isLym) {
		mIsLym = isLym;
			if (key == null || key.length() == 0 || !key.contains("http://")) {
				if (lruCache.get(String.valueOf(resId)) != null) {
					bitmap = lruCache.get(String.valueOf(resId));
					if (bitmap != null) {
						setImageBitmap(lruCache.get(String.valueOf(resId)));
					}
				} else {
					bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
					if(bitmap != null){
						setImageBitmap(bitmap);
						lruCache.put(String.valueOf(resId),bitmap);
					}

				}
				return;
			}

			filePath = SysConstants.FILE_DIR_ROOT + Utils.getImageToken(key)+ "_min";
			final File cacheFile = new File(filePath);
			if (lruCache.get(filePath) != null) {
				bitmap = lruCache.get(filePath);
				if (bitmap != null) {
					setImageBitmap(lruCache.get(filePath));
				}else{
					if(!fileList.contains(filePath)){
						fileList.add(filePath);
						beginDownload(key);
					}
				}
			} else if (imageCache.containsKey(filePath)) {
				bitmap = imageCache.get(filePath).get();
				if (bitmap != null){
					setImageBitmap(imageCache.get(filePath).get());
				}else{
					if(!fileList.contains(filePath)){
						fileList.add(filePath);
						beginDownload(key);
					}
				}
			}
			else if (cacheFile.exists()) {

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

			} else {
				if(!fileList.contains(filePath)){
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

	@Override
	public void onStartDownload() {
	}

	@Override
	public void onProgress(long current, long total) {
	}

	@Override
	public void onFinished(int resultCode, final String filePath) {
		if (resultCode == 1) {
			fileList.remove(filePath);
			final File file = new File(filePath);
			if (file.exists()) {
				final long total = Runtime.getRuntime().totalMemory();
				final long max = Runtime.getRuntime().maxMemory();
				new Thread(new Runnable() {
					@Override
					public void run() {
						bitmap = Utils.revitionImageSize(filePath,SysConstants.IMAGEIMPLESIZE_256);
						Message msg = handler.obtainMessage();
						msg.arg1 = LOADFINISH;
						msg.obj = bitmap;
						handler.sendMessage(msg);
					}
				}).start();



			}else{
				if(mIsLym){
					setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defal_down_lym_fail));
				} else {
					setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defal_down_fail));
				}
				fileList.remove(filePath);
			}
	}}


	 public static  void creatLrucash(){
		 int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		    final int cacheSize = maxMemory / 10;
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
			            	imageCache.put(key, new SoftReference<Bitmap>(oldValue));
			                super.entryRemoved(evicted, key, oldValue, newValue);  
			            }  
			        };  
		}

}

