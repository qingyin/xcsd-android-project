package com.tuxing.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import java.util.HashMap;
import java.util.List;

/**
 * 圆角图片视图
 * 
 * @author zhaomeng
 * 
 */
public class RoundImageView extends ImageView implements DownloadTaskListener {
	public static int ANGLE = 5;

	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	private Context mContext;
	private String filePath;
	public static LruCache<String, Bitmap> lruCache;
	private Bitmap bitmap = null;
	public static List<String> fileList = new ArrayList<String>();
	static {
		creatLrucash();
	}
	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private static final int COLORDRAWABLE_DIMENSION = 1;

	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;

	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();

	private final Matrix mShaderMatrix = new Matrix();
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	private int mBorderColor = DEFAULT_BORDER_COLOR;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapWidth;
	private int mBitmapHeight;

	private float mDrawableRadius;
	private float mBorderRadius;

	private boolean mReady;
	private boolean mSetupPending;

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

	public RoundImageView(Context context) {
		super(context);
		this.mContext  = context;
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext  = context;
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(SCALE_TYPE);
		this.mContext  = context;

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CircleImageView, defStyle, 0);

		mBorderWidth = a.getDimensionPixelSize(
				R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
		mBorderColor = a.getColor(R.styleable.CircleImageView_border_color,
				DEFAULT_BORDER_COLOR);

		a.recycle();

		mReady = true;

		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	@Override
	public ScaleType getScaleType() {
		return SCALE_TYPE;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (scaleType != SCALE_TYPE) {
			throw new IllegalArgumentException(String.format(
					"ScaleType %s not supported.", scaleType));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}

		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius,
				mBitmapPaint);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius,
				mBorderPaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup();
	}

	public int getBorderColor() {
		return mBorderColor;
	}

	public void setBorderColor(int borderColor) {
		if (borderColor == mBorderColor) {
			return;
		}

		mBorderColor = borderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		if (borderWidth == mBorderWidth) {
			return;
		}

		mBorderWidth = borderWidth;
		setup();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
						COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	private void setup() {
		if (!mReady) {
			mSetupPending = true;
			return;
		}

		if (mBitmap == null) {
			return;
		}

		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();

		mBorderRect.set(0, 0, getWidth(), getHeight());
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
				(mBorderRect.width() - mBorderWidth) / 2);

		mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width()
				- mBorderWidth, mBorderRect.height() - mBorderWidth);
		mDrawableRadius = Math.min(mDrawableRect.height() / 2,
				mDrawableRect.width() / 2);

		updateShaderMatrix();
		invalidate();
	}

	private void updateShaderMatrix() {
		float scale;
		float dx = 0;
		float dy = 0;

		mShaderMatrix.set(null);

		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
				* mBitmapHeight) {
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			scale = mDrawableRect.width() / (float) mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}

		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
				(int) (dy + 0.5f) + mBorderWidth);

		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

	/**
	 * 加载图片
	 * 
	 *            图片地址
	 *            图片缓存
	 */

	public void setImageUrl(final String key, int resId) {
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

			filePath = SysConstants.FILE_DIR_ROOT + Utils.getImageToken(key) + "_min";
			final File cacheFile = new File(filePath);
			if (lruCache.get(filePath) != null) {
				bitmap = lruCache.get(filePath);
				if (bitmap != null) {
					setImageBitmap(lruCache.get(filePath));
				}else {
					if (!fileList.contains(filePath)) {
						fileList.add(filePath);
						beginDownload(key);
					}
				}
			} else if (imageCache.containsKey(filePath)) {
				bitmap = imageCache.get(filePath).get();
				if (bitmap != null) {
					setImageBitmap(imageCache.get(filePath).get());
				}else {
					if (!fileList.contains(filePath)) {
						fileList.add(filePath);
						beginDownload(key);
					}
				}
			}else if (cacheFile.exists()) {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgress(long current, long total) {
		// TODO Auto-generated method stub

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
//					if (bitmap != null) {
//						setImageBitmap(bitmap);
//							synchronized (lruCache) {
//								lruCache.put(filePath, bitmap);
//								fileList.remove(filePath);
//							}
//					}
				}
			}else{
				fileList.remove(filePath);
			}
	}

}
