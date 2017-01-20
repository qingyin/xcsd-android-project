package com.tuxing.app.activity;

import android.content.Intent;
import android.graphics.*;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.ClipView;
import com.tuxing.sdk.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;


/**
 * @author Administrator 整体思想是：截取屏幕的截图，然后截取矩形框里面的图片
 */
public class ClipPictureActivity extends BaseActivity implements OnTouchListener,
		OnClickListener {
	ImageView srcPic;
	Button sure;
	Button cancel;
	ClipView clipview;
	 public static String picAction = "com.tuxing.mobile.cutPicPath";

	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	private static final String TAG = "ClipPictureActivity";
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	private String path = "";
	public static int statusBarHeight = 0;
	public static int titleBarHeight = 0;
	public static int x = 500;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clip_picture);
		Intent intent = getIntent();
		path = intent.getStringExtra("path");
//		type = intent.getStringExtra("type");
		if (TextUtils.isEmpty(path)){
			Toast.makeText(ClipPictureActivity.this, "图片不存在", 0).show();
			finish();
			return;
		}
		MyLog.getLogger(getClass()).d("上传头像==>ClipPictureActivity:path:"+path);
		srcPic = (ImageView) this.findViewById(R.id.src_pic);
		srcPic.setOnTouchListener(this);
		initDisplayParams();
		if(width>0){
			x = width/2;
			System.out.println("剪切框宽度："+x);
		}
		MyLog.getLogger(getClass()).d("上传头像==>ClipPictureActivity:width"+width+",height:"+height);
		Bitmap bitmap = convertToBitmap(path,width,height);
		if(bitmap==null){
			Toast.makeText(ClipPictureActivity.this, "图片不存在", 0).show();
			finish();
			return;
		}
		if(bitmap.getWidth()<width){
			matrix.setScale((float)width/bitmap.getWidth(), (float)width/bitmap.getWidth());
		}
		srcPic.setImageMatrix(matrix);
		srcPic.setScaleType(ScaleType.MATRIX);
		srcPic.setImageBitmap(bitmap);

		sure = (Button) this.findViewById(R.id.sure);
		sure.setOnClickListener(this);
		cancel = (Button)this.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/* 这里实现了多点触摸放大缩小，和单点移动图片的功能 */
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			// 設置初始點位置
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true; // indicate event was handled
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/* 点击进入预览 */
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.sure) {
			String picPath = "";
			Bitmap fianBitmap = getBitmap();
			if (fianBitmap == null){
				picPath = path;
//				return;f
			}else{
				picPath = saveBitmap(fianBitmap);
				if(TextUtils.isEmpty(picPath))
					picPath = path;
			}
			MyLog.getLogger(getClass()).d("上传头像==>ClipPictureActivity:剪切图片保存路径："+picPath);
			//TODO MyInfoActivity.picAction
			Intent intent = new Intent(picAction);
			intent.putExtra("picPath", picPath);
			setResult(RESULT_OK, intent);
			ClipPictureActivity.this.sendBroadcast(intent);
			finish();
		} else if (id == R.id.cancel) {
			finish();
		} else {
		}
	}

	/* 获取矩形区域内的截图 */
	private Bitmap getBitmap() {
		MyLog.getLogger(getClass()).d("上传头像==>ClipPictureActivity:获取截图。。");
		getBarHeight();
		Bitmap screenShoot = takeScreenShot();
	
		clipview = (ClipView)this.findViewById(R.id.clipview);
		int width = clipview.getWidth();
		int height = clipview.getHeight();
		return Bitmap.createBitmap(screenShoot,
				(width-x)/2,(height - x )/ 2 + titleBarHeight + statusBarHeight, x, x);
	}

	private void getBarHeight() {
		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;

		int contenttop = this.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// statusBarHeight是上面所求的状态栏的高度
		titleBarHeight = contenttop - statusBarHeight;

		Log.v(TAG, "statusBarHeight = " + statusBarHeight
				+ ", titleBarHeight = " + titleBarHeight);
	}

	// 获取Activity的截屏
	private Bitmap takeScreenShot() {
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

	public Bitmap convertToBitmap(String path, int w, int h) {
		MyLog.getLogger(getClass()).d("上传头像==>ClipPictureActivity:压缩图片。。");
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		
		if (width > w || height > h) {
			// 缩放
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile(path, opts));
		return rotaingImageView(readPictureDegree(path), BitmapFactory.decodeFile(path, opts));
//		return BitmapFactory.decodeFile(path, opts);
//		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	public String saveBitmap(Bitmap mBitmap) {
		MyLog.getLogger(getClass()).d("上传头像==>ClipPictureActivity:保存图片。。");
		//TODO MyInfoActivity.IMAGE_FILE_NAME_BAT
		File f = new File(SysConstants.FILE_DIR_ROOT+ "testcut.jpg");
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			IOUtils.safeClose(fOut);
		}
		if (f.length() > 0)
			return f.getAbsolutePath();
		else
			return "";
	}
	
	/**
	 * 
	 * 旋转图片
	 * 
	 */

	public static int readPictureDegree(String imagePath) {
		int imageDegree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(imagePath);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				imageDegree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				imageDegree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				imageDegree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageDegree;
	}
	// 然后将图片旋转回去

	public static Bitmap rotaingImageView(int angle, Bitmap mBitmap) {
		if (angle > 0) {
			Matrix matrix = new Matrix();
			matrix.setRotate(angle);
			Bitmap rotateBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
					mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			if (rotateBitmap != null) {
				mBitmap.recycle();
				mBitmap = rotateBitmap;
			}
		}
		return mBitmap;
	}
	public static int height = 0;
	public static int width = 0;
	Display display;

	private void initDisplayParams() {
		display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();

		System.out.println("width:"+width +"   height:"+height);
/*		if (display.getWidth() > 1080) {
			// 800 * 1280
			width = 1556;
			height = 2550;// 450
		}else if (display.getWidth() > 800 && display.getWidth() <= 1080) {
			// 960*540
			width = 1080; 
			height = 1920;// 320
		} else if (display.getWidth() > 480 && display.getWidth() <= 800) {
			// 960*540
			width = 960;
			height = 1080;// 320
		} else if (display.getWidth() > 320 && display.getWidth() <= 480) {
			// 800 * 480
			width = 480;
			height = 800;// 200
		} else if (display.getWidth() <= 320) {
			// 320*480
			width = 320;
			height = 480;// 160
		}*/
	}
}