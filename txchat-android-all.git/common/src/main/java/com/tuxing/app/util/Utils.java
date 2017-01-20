package com.tuxing.app.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.tuxing.app.activity.PicListActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    private static final SimpleDateFormat TIME_FORMAT_SS = new SimpleDateFormat(
            "MM月dd日HH:mm");
    private static final SimpleDateFormat TIME_FORMAT_DAY = new SimpleDateFormat(
            "MM,dd");

    private static final SimpleDateFormat TIME_DAY_TIME = new SimpleDateFormat(
            "dd日 HH:mm");

    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat(
            "yyyy");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat(
            "MM");
    private static final SimpleDateFormat TODAY_FORMAT = new SimpleDateFormat(
            "dd");
    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat(
            "HH:mm");
    private static final SimpleDateFormat LONG_FORMAT = new SimpleDateFormat(
            "yyyy年MM月dd日 HH:mm");
    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat(
            "EEE HH:mm", java.util.Locale.CHINA);
    private static final SimpleDateFormat YEAR_LONG_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd  HH:mm");

    /**
     * 复制textview
     */

    public static void copy(Context context, String str) {
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(str);
    }

    public static boolean isAppOnForeground(Context mContext) {
        ActivityManager activityManager = (ActivityManager) mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = mContext.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保留一位小数
     * 转换单位（万）
     */
    public static String roundOne(long count) {
        String strCount;
        if (String.valueOf(count).length() > 4) {
            String str = String.valueOf((float) count / 10000);
            BigDecimal bg = new BigDecimal(str);
            double f1 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            strCount = String.valueOf(f1 + "万");
        } else {
            strCount = String.valueOf(count);
        }
        return strCount;
    }


    /**
     * 获取缩率图
     */

    public static Bitmap getBitmap(String photo, int size) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo, options);
        options.inSampleSize = calculateInSampleSize(options, size);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inInputShareable = true;
        return BitmapFactory.decodeFile(photo, options);
    }

    /**
     * 获取缩放比例
     */

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int maxSize) {
        // 图像原始高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > maxSize || width > maxSize) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) maxSize);
            } else {
                inSampleSize = Math.round((float) width / (float) maxSize);
            }
        }
        return inSampleSize;
    }


    public static Bitmap getSizeBitmap(String photo, int width) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap decodeFile = null;
        BitmapFactory.decodeFile(photo, options);
        options.inSampleSize = InSampleSize(options, width);
        if (options.inSampleSize == 1) {
            decodeFile = scaleImg(BitmapFactory.decodeFile(photo), 120);
        } else {
            options.inJustDecodeBounds = false;
            options.inInputShareable = true;
            decodeFile = BitmapFactory.decodeFile(photo, options);

        }
        return decodeFile;
    }

    public static int InSampleSize(BitmapFactory.Options options,
                                   int maxSize) {
        // 图像原始高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > maxSize) {
            inSampleSize = Math.round((float) width / (float) maxSize);
        } else {
            inSampleSize = Math.round((float) maxSize / (float) width);
        }
        return inSampleSize;
    }

    public static Bitmap scaleImg(Bitmap bm, int newWidth) {
        if (bm != null) {
            // 获得图片的宽高
            int width = bm.getWidth();
            int height = bm.getHeight();
            // 设置想要的大小
            int newWidth1 = newWidth;
            // 计算缩放比例
            float scaleWidth = ((float) newWidth1) / width;
            int newHeight1 = (int) (height * scaleWidth);
            float scaleHeight = ((float) newHeight1) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            return newbm;
        } else
            return null;
    }


    /**
     * 获取缩率图路径
     */

    public static String getSmallPath(String filePath) {
        String str = filePath.replace(".jpg", "_small.jpg");
        return str;
    }

    /**
     * 获取声音路径
     */

    public static String getAudioPath(String audio) {
        makeDir(SysConstants.FILE_DIR_ROOT);
        return SysConstants.FILE_DIR_ROOT + audio;
    }

    /**
     * 获取缩率图
     */


    public static Bitmap revitionImageSize(String imagePath, float width, float hight) {
        Bitmap bitmap = null;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了,表示只返回宽高
        newOpts.inPreferredConfig = Config.RGB_565;
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, newOpts);//此时返回bm为空


        //当前图片宽高
        float w = newOpts.outWidth;
        float h = newOpts.outHeight;
        float hh = hight;
        float ww = width;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
            //有时会出现be=3.2或5.2现象，如果不做处理压缩还会失败
            if ((newOpts.outWidth / ww) > be) {

                be += 1;
            }
            //be = Math.round((float) newOpts.outWidth / (float) ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
            if ((newOpts.outHeight / hh) > be) {

                be += 1;
            }
        }
        if (be <= 0) {

            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imagePath, newOpts);
        int degree = readPictureDegree(imagePath);
        if (bitmap != null && degree > 0) {
            bitmap = rotaingImageView(degree, bitmap);
        }
        return bitmap;
    }

    /**
     * 获取缩小图片
     */

    public static Bitmap revitionImageSize(String path, int size) {
        FileInputStream temp = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }

            // 取得图片
            temp = new FileInputStream(file);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.RGB_565;
            // 这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
            options.inJustDecodeBounds = true;
            // 通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
            BitmapFactory.decodeStream(temp, null, options);
            // 生成压缩的图片
            int i = 0;
            Bitmap bitmap = null;
            while (true) {
                // 这一步是根据要设置的大小，使宽和高都能满足
                if ((options.outWidth >> i <= size)
                        && (options.outHeight >> i <= size)) {
                    // 重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
                    temp = new FileInputStream(file);
                    // 这个参数表示 新生成的图片为原始图片的几分之一。
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    // Log.v("bitmap","options.outWidth="+options.outWidth+"options.outHeight="+options.outHeight+"inSampleSize="+options.inSampleSize);
                    // 这里之前设置为了true，所以要改为false，否则就创建不出图片
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(temp, null, options);
                    break;
                }
                i += 1;
            }
            // Log.v("bitmap",
            // "bitmap==" + bitmap.getWidth() + "  h ="
            // + bitmap.getHeight());
            int degree = readPictureDegree(path);
            if (bitmap != null && degree > 0)
                return rotaingImageView(degree, bitmap);
            else
                return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.getLogger(Utils.class.getSimpleName()).d("图片转换失败 erroy = " + e.toString());
        } finally {
            IOUtils.safeClose(temp);
        }
        return null;
    }


    /**
     * 旋转图片
     */

    public static int readPictureDegree(String imagePath) {
        int imageDegree = 0;
        if (!TextUtils.isEmpty(imagePath)) {

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
        }
        return imageDegree;
    }

    // 然后将图片旋转回去

    public static Bitmap rotaingImageView(int angle, Bitmap mBitmap) {
        if (angle > 0 && mBitmap != null) {
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

    // 调用本地相机拍照并保存图片
    public static String getPhoto(Activity act, String fileName, int p) {
        String filepath = SysConstants.FILE_DIR_ROOT + fileName;
        final File file = new File(filepath);
        final Uri imageuri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        act.startActivityForResult(intent, p);
        return filepath;
    }

    /**
     * 获取本地相册
     *
     * @param mContext
     * @param photo
     * @param maxPhoto
     */
    public static void getMultiPhoto(BaseActivity mContext, int photo, int maxPhoto) {
        //SelectPicsActivity.invoke(mContext);
        mContext.startActivityForResult(new Intent(mContext, PicListActivity.class).putExtra("max", maxPhoto), photo);
    }

    /**
     * 根据路径获取图片
     *
     * @param activity
     * @param uri
     * @return
     */
    public static String getPath(Activity activity, Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(column_index);
                }
                cursor.close();

            }
        }

        return path;
    }

    /**
     * 设置路径
     */

    public static boolean makeDir(String dir) {
        String dirAbsolut = dir;
        File file = new File(dirAbsolut);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.exists();
    }


    /**
     * 获取时间
     */

    public static String getDateTime(Context con, long createDate) {

        int currentDay = Integer.parseInt(TODAY_FORMAT.format(new Date()));
        int currentMonth = Integer.parseInt(MONTH_FORMAT.format(new Date()));
        int day = Integer.parseInt(TODAY_FORMAT.format(new Date(createDate)));
        int month = Integer.parseInt(MONTH_FORMAT.format(new Date(createDate)));


        int currentYear = Integer.parseInt(YEAR_FORMAT.format(new Date()));
        int year = Integer.parseInt(YEAR_FORMAT.format(new Date(createDate)));

        if (currentMonth == month) {//本月
            if (currentDay == day) {
                return "今天" + SIMPLE_FORMAT.format(new Date(createDate));
            } else if (currentDay - day == 1) {
                return "昨天" + SIMPLE_FORMAT.format(new Date(createDate));
            } else if (currentDay - day == 2) {
                return "前天" + SIMPLE_FORMAT.format(new Date(createDate));
            } else {
                return TIME_DAY_TIME.format(new Date(createDate));
            }
        } else {//非本月

            if (currentYear == year) {
                return TIME_FORMAT_SS.format(new Date(createDate));
            } else {
                return LONG_FORMAT.format(new Date(createDate));
            }

        }

    }
    /**
     * 获取学能作业时间
     */

    public static String getStudyDateTime(Context con, long createDate) {

        int currentDay = Integer.parseInt(TODAY_FORMAT.format(new Date()));
        int currentMonth = Integer.parseInt(MONTH_FORMAT.format(new Date()));
        int day = Integer.parseInt(TODAY_FORMAT.format(new Date(createDate)));
        int month = Integer.parseInt(MONTH_FORMAT.format(new Date(createDate)));


        int currentYear = Integer.parseInt(YEAR_FORMAT.format(new Date()));
        int year = Integer.parseInt(YEAR_FORMAT.format(new Date(createDate)));

        if (currentMonth == month) {//本月
            if (currentDay == day) {
                return "今天" + SIMPLE_FORMAT.format(new Date(createDate));
            } else if (currentDay - day == 1) {
                return "昨天" + SIMPLE_FORMAT.format(new Date(createDate));
            } else if (currentDay - day == 2) {
                return "前天" + SIMPLE_FORMAT.format(new Date(createDate));
            } else {
                return YEAR_LONG_FORMAT.format(new Date(createDate));
            }
        } else {//非本月

            if (currentYear == year) {
                return TIME_FORMAT_SS.format(new Date(createDate));
            } else {
                return LONG_FORMAT.format(new Date(createDate));
            }

        }

    }

    public static int getMonthDay(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if ((year % 100 == 0 && year % 400 == 0) || year % 4 == 0) {
                    return 29;
                } else {
                    return 28;
                }
        }
        return 30;
    }


    /**
     * 获取版本name
     */

    public static String getVersionName(Context activity) {
        PackageManager manager = activity.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    activity.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
        }
        return null;
    }


    public static void cancelNotice(Context context, int noticeId) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(noticeId);
    }

    // 放大缩小图片
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / (float) width);
        float scaleHeight = ((float) h / (float) height);
        float scale = Math.max(scaleWidht, scaleHeight);
        matrix.postScale(scale, scale);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }

    /**
     * 圆角图片
     */

    public static Bitmap roundCorners(Bitmap bitmap, ImageView imageView,
                                      int roundPixels) {
        Bitmap roundBitmap;

        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();

        int vw = imageView.getWidth();
        int vh = imageView.getHeight();
        if (vw <= 0)
            vw = bw;
        if (vh <= 0)
            vh = bh;

        int width, height;
        Rect srcRect;
        Rect destRect;
        switch (imageView.getScaleType()) {
            case CENTER_INSIDE:
                float vRation = (float) vw / vh;
                float bRation = (float) bw / bh;
                int destWidth;
                int destHeight;
                if (vRation > bRation) {
                    destHeight = Math.min(vh, bh);
                    destWidth = (int) (bw / ((float) bh / destHeight));
                } else {
                    destWidth = Math.min(vw, bw);
                    destHeight = (int) (bh / ((float) bw / destWidth));
                }
                int x = (vw - destWidth) / 2;
                int y = (vh - destHeight) / 2;
                srcRect = new Rect(0, 0, bw, bh);
                destRect = new Rect(x, y, x + destWidth, y + destHeight);
                width = vw;
                height = vh;
                break;
            case FIT_CENTER:
            case FIT_START:
            case FIT_END:
            default:
                vRation = (float) vw / vh;
                bRation = (float) bw / bh;
                if (vRation > bRation) {
                    width = (int) (bw / ((float) bh / vh));
                    height = vh;
                } else {
                    width = vw;
                    height = (int) (bh / ((float) bw / vw));
                }
                srcRect = new Rect(0, 0, bw, bh);
                destRect = new Rect(0, 0, width, height);
                break;
            case CENTER_CROP:
                vRation = (float) vw / vh;
                bRation = (float) bw / bh;
                int srcWidth;
                int srcHeight;
                if (vRation > bRation) {
                    srcWidth = bw;
                    srcHeight = (int) (vh * ((float) bw / vw));
                    x = 0;
                    y = (bh - srcHeight) / 2;
                } else {
                    srcWidth = (int) (vw * ((float) bh / vh));
                    srcHeight = bh;
                    x = (bw - srcWidth) / 2;
                    y = 0;
                }
                width = srcWidth;// Math.min(vw, bw);
                height = srcHeight;// Math.min(vh, bh);
                srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
                destRect = new Rect(0, 0, width, height);
                break;
            case FIT_XY:
                width = vw;
                height = vh;
                srcRect = new Rect(0, 0, bw, bh);
                destRect = new Rect(0, 0, width, height);
                break;
            case CENTER:
            case MATRIX:
                width = Math.min(vw, bw);
                height = Math.min(vh, bh);
                x = (bw - width) / 2;
                y = (bh - height) / 2;
                srcRect = new Rect(x, y, x + width, y + height);
                destRect = new Rect(0, 0, width, height);
                break;
        }

        try {
            roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect,
                    destRect, width, height);
        } catch (OutOfMemoryError e) {
            roundBitmap = bitmap;
        }

        return roundBitmap;
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap,
                                                 int roundPixels, Rect srcRect, Rect destRect, int width, int height) {
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final RectF destRectF = new RectF(destRect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFF000000);
        canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, destRectF, paint);
        return output;
    }

    /**
     * 获取该语音的时长
     *
     * @param voice_path
     * @return
     */
    public static int getVoiceTime(String voice_path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(voice_path);
            mediaPlayer.prepare();
            int duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            mediaPlayer = null;
            return duration;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public static String toTime(int time) {
        time /= 1000;
        int min = time / 60;
        int second = time % 60;
        if (min == 0)
            if (second == 0) {
                return 1 + "\"";
            } else {
                return second + "\"";
            }
        return min + "\'" + second + "\"";
    }

    public static String getNoticeTime(int time) {
        time /= 1000;
        int min = time / 60;
        int second = time % 60;
        if (min == 0)
            if (second == 0) {
                return 1 + "\"";
            } else {
                return second + "\"";
            }
        return min + "\'" + second + "\"";
    }

    /**
     * 删除文件
     */

    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File f : file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".csv")) {
                    return false;
                }
                return true;
            }
        })) {
            if (f.isFile()) {
                f.delete();
            } else {
                deleteFile(f);
            }
        }
    }


    public static void saveObject(Context context, Object contact, String fileName) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(
                    new File(context.getCacheDir(), fileName)));
            oos.writeObject(contact);
            oos.flush();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            IOUtils.safeClose(oos);
        }
    }

    /**
     * 获取联系人
     */

    public static Object getObject(Context context, String fileName) {
        Object object = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(
                    new File(context.getCacheDir(), fileName)));
            object = ois.readObject();
        } catch (StreamCorruptedException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        } finally {
            IOUtils.safeClose(ois);
        }

        return object;
    }

    /*
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static void scanOneFile(final Context context, final String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Video.Media.DATA, filePath);
        // Add a new record (identified by uri)
        final Uri uri = context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath));
        context.sendBroadcast(intent);
    }


    public static boolean copyFile(String sourceFile, String targetFile) {
        // Log.i("","copy from "+ sourceFile +" to "+ targetFile);
        boolean res = false;
        BufferedInputStream inBuff = null;
        FileOutputStream output = null;
        BufferedOutputStream outBuff = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourceFile);
            inBuff = new BufferedInputStream(fis);
            output = new FileOutputStream(targetFile);
            outBuff = new BufferedOutputStream(output);

            byte[] b = new byte[1024 * 2];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }

            outBuff.flush();
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.safeClose(inBuff);
            IOUtils.safeClose(outBuff);
            IOUtils.safeClose(output);
            IOUtils.safeClose(fis);
        }

        return res;
    }

    /**
     * 日志存储目录
     */
    public static String savePath() {
        File sdRoot;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            sdRoot = Environment.getExternalStorageDirectory();
        } else {
            sdRoot = Environment.getDataDirectory();
        }
        return sdRoot.getAbsolutePath();
    }

    public static String getWeekOfDate(String dateStr, String format) {
        DateFormat dd = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = dd.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String[] weekDaysCode = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * msg 提醒方式
     * 0  静音
     * 1 	 声音  震动
     * 2	声音
     * 3	震动
     */
    public static int getMsgRemind(Context mContext) {
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int RingerMode = audio.getRingerMode();
        int disturb = PreferenceUtils.getPrefInt(mContext, SysConstants.remind_disturb, 0);
        int indMode = 0;

        switch (disturb) {// 免打扰
            case 0:
                //0 关闭
                if (RingerMode == AudioManager.RINGER_MODE_SILENT) {
                    //静音
                    return indMode;
                } else if (RingerMode == AudioManager.RINGER_MODE_NORMAL) {
                    //铃声
                    if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.ISCHATACTIVITY, false)) {
                        // ischatActivity
                        indMode = 3;
                    } else if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.voice_remind, true) &&
                            PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true)) {
                        //铃声  震动
                        indMode = 1;
                    } else if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.voice_remind, true)) {
                        // 铃声
                        indMode = 2;
                    } else if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true)) {
                        //震动
                        indMode = 3;
                    }
                    return indMode;
                } else if (RingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                    //震动
                    if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true)) {
                        indMode = 3;
                    }
                    return indMode;
                }
                break;
            case 1:
                //1开启
                indMode = 0;
                break;
            case 2:
                //2夜间
                Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
                String strTime = SIMPLE_FORMAT.format(curDates);
                int hour = Integer.valueOf(strTime.split(":")[0]);
                try {
//		        long curTime = SIMPLE_FORMAT.parse(strTime).getTime();
//		        long startLong = SIMPLE_FORMAT.parse("08:00").getTime();
//		        long endLong = SIMPLE_FORMAT.parse("20:00").getTime();
                    if (hour >= 22 || 8 >= hour) {
                        indMode = 0;
                    } else {

                        if (RingerMode == AudioManager.RINGER_MODE_SILENT) {
                            //静音
                            return indMode;
                        } else if (RingerMode == AudioManager.RINGER_MODE_NORMAL) {
                            //铃声
                            if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.ISCHATACTIVITY, false)) {
                                // ischatActivity
                                indMode = 3;
                            } else if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.voice_remind, true) &&
                                    PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true)) {
                                //铃声  震动
                                indMode = 1;
                            } else if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.voice_remind, true)) {
                                // 铃声
                                indMode = 2;
                            } else if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true)) {
                                //震动
                                indMode = 3;
                            }
                            return indMode;
                        } else if (RingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                            //震动
                            if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true)) {
                                indMode = 3;
                            }
                            return indMode;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return indMode;
    }

    /**
     * 保存图片的token
     *
     * @param imageUrl
     * @return
     */
    public static String getImageToken(String imageUrl) {
        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = imageUrl.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str) + ".jpg";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 保存视频的token
     *
     * @param imageUrl
     * @return
     */
    public static String getVideoToken(String imageUrl) {
        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = imageUrl.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str) + ".mp4";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * fileSize
     *
     * @param f
     * @return
     */
    public static Double getFileSize(File f) {
        double size = 0;
        try {
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                size = ((double) fis.available() / 1048576);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public static int getDisplayWidth(Context mContext) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return width;
    }

    public static int getDisplayHeight(Context mContext) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    public static int dipToPX(final Context ctx, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.getResources().getDisplayMetrics());
    }

    // 拍照保存的路径
    public static String getTakePhonePath(){
        File file = new File(SysConstants.FILE_DCIM);
        File filePic = new File(SysConstants.FILE_PICTURES);
        String path;
        if(file.exists() && file.isDirectory()){
            path = SysConstants.FILE_DCIM;
        }else{
            if(filePic.exists()){
                if(filePic.isDirectory()){
                    path =  SysConstants.FILE_PICTURES;
                }else{
                    IOUtils.deleteFile(filePic);
                    filePic.mkdirs();
                    path =  SysConstants.FILE_PICTURES;
                }
            }else{
                filePic.mkdirs();
                path =  SysConstants.FILE_PICTURES;
            }
        }
        return path;
    }

    public static String getDuration(long duration) {

        StringBuffer sb = new StringBuffer();
        int minu = (int) (duration / 1000 / 60);
        if (minu > 9) {
            sb.append(minu + ":");
        } else {
            sb.append("0" + minu + ":");
        }

        int secen = (int) (duration / 1000 % 60);
        if (secen > 9) {
            sb.append(secen);
        } else {
            sb.append("0" + secen);
        }
        return sb.toString();

    }


//    /**
//     * 根据图片路径进行压缩图片
//     *
//     * @return
//     */
//    public static void saveBitmap(String imagePath, String bitName, String savePath, int size) {
//        Bitmap bitmap;
//        Double fileSize = getFileSize(new File(imagePath));
//        if (fileSize < 0.4) {
//            bitmap = BitmapFactory.decodeFile(imagePath);
//        } else {
//
//            BitmapFactory.Options newOpts = new BitmapFactory.Options();
//            //开始读入图片，此时把options.inJustDecodeBounds 设回true了,表示只返回宽高
//            newOpts.inPreferredConfig = Config.RGB_565;
//            newOpts.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(imagePath, newOpts);//此时返回bm为空
//
//
//            //当前图片宽高
//            float w = newOpts.outWidth;
//            float h = newOpts.outHeight;
//            float hh = 1280f;
//            float ww = 720f;
//            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//            int be = 1;//be=1表示不缩放
//            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//                be = (int) (newOpts.outWidth / ww);
//                //有时会出现be=3.2或5.2现象，如果不做处理压缩还会失败
//                if ((newOpts.outWidth / ww) > be) {
//
//                    be += 1;
//                }
//                //be = Math.round((float) newOpts.outWidth / (float) ww);
//            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//                be = (int) (newOpts.outHeight / hh);
//                if ((newOpts.outHeight / hh) > be) {
//
//                    be += 1;
//                }
//            }
//            if (be <= 0) {
//
//                be = 1;
//            }
//            newOpts.inSampleSize = be;//设置缩放比例
//            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//            newOpts.inJustDecodeBounds = false;
//            bitmap = BitmapFactory.decodeFile(imagePath, newOpts);
//            int degree = readPictureDegree(imagePath);
//            if (bitmap != null && degree > 0) {
//                bitmap = rotaingImageView(degree, bitmap);
//            }
//        }
//        compressImage(bitmap, bitName, savePath, size);//压缩好比例大小后再进行质量压缩
//
//    }

    /**
     * 根据图片路径进行压缩图片
     *
     * @return
     */
    public static boolean saveBitmap(String imagePath, String bitName, String savePath, int size) {
        Bitmap bitmap;
        boolean isSuccess = true;
        Double fileSize = getFileSize(new File(imagePath));
        try {
            if (fileSize < 0.2) {
                bitmap = BitmapFactory.decodeFile(imagePath);
            } else {
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                //开始读入图片，此时把options.inJustDecodeBounds 设回true了,表示只返回宽高
                newOpts.inPreferredConfig = Config.RGB_565;
                newOpts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, newOpts);//此时返回bm为空


                //当前图片宽高
                float w = newOpts.outWidth;
                float h = newOpts.outHeight;
                float hh = 1280f;
                float ww = 720f;
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) (newOpts.outWidth / ww);
                    //有时会出现be=3.2或5.2现象，如果不做处理压缩还会失败
                    if ((newOpts.outWidth / ww) > be) {

                        be += 1;
                    }
                    //be = Math.round((float) newOpts.outWidth / (float) ww);
                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) (newOpts.outHeight / hh);
                    if ((newOpts.outHeight / hh) > be) {

                        be += 1;
                    }
                }
                if (be <= 0) {

                    be = 1;
                }
                newOpts.inSampleSize = be;//设置缩放比例
                //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                newOpts.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(imagePath, newOpts);
                int degree = readPictureDegree(imagePath);
                if (bitmap != null && degree > 0) {
                    bitmap = rotaingImageView(degree, bitmap);
                }
            }
            isSuccess = compressImage(bitmap, bitName, savePath, size);//压缩好比例大小后再进行质量压缩
        }catch (Exception e) {
            MyLog.getLogger("Utils").d("压缩图片失败 e= " + e.toString());
        } catch (OutOfMemoryError e) {
            MyLog.getLogger("Utils").d("压缩图片失败OOM e= " + e.toString());
        }
        return isSuccess;
    }


//    /**
//     * 压缩图片
//     *
//     * @param image
//     * @param size
//     * @return
//     */
//    private static void compressImage(Bitmap image, String bitName, String savePath, int size) {
//        try {
//            if (image != null) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//                int options = 100;
//
//                while ((baos.toByteArray().length / 1024) > size) {  //循环判断如果压缩后图片是否大于等于size,大于等于继续压缩
//                    baos.reset();//重置baos即清空baos
//                    options -= 5;//每次都减少5
//                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//                }
//
//
//                File foder = new File(savePath);
//                if (!foder.exists()) {
//                    foder.mkdirs();
//                }
//
//                File myCaptureFile = new File(savePath, bitName);
//                if (!myCaptureFile.exists()) {
//                    myCaptureFile.createNewFile();
//                }
//
//                FileOutputStream fos = new FileOutputStream(myCaptureFile);
//                fos.write(baos.toByteArray());
//                fos.flush();
//                fos.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 压缩图片
     *
     * @param image
     * @param size
     * @return
     */
    private static boolean compressImage(Bitmap image, String bitName, String savePath, int size) {
        boolean isSuccess = true;
        try {
            if (image != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 80, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                int options = 100;

                while ((baos.toByteArray().length / 1024) > size) {  //循环判断如果压缩后图片是否大于等于size,大于等于继续压缩
                    baos.reset();//重置baos即清空baos
                    options -= 5;//每次都减少5
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                }

                if (baos.toByteArray().length > 0) {
                    File foder = new File(savePath);
                    if (!foder.exists()) {
                        foder.mkdirs();
                    }

                    File myCaptureFile = new File(savePath, bitName);
                    if (!myCaptureFile.exists()) {
                        myCaptureFile.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(myCaptureFile);
                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                } else {
                    isSuccess = false;
                    MyLog.getLogger("Utils").d("压缩图片出错 length = " + baos.toByteArray().length);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
            MyLog.getLogger("utils").d("压缩图片出错 msg = " + e.toString());
        }
        return isSuccess;
    }

    public static SpannableString SearchKeye(String text, String keyword, int color) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * 含html标签的字符串
     *
     * @param inputString
     * @return
     */

    public static String Html2Text(String inputString) {
        String htmlStr = inputString;
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        java.util.regex.Pattern p_html1;
        java.util.regex.Matcher m_html1;

        try {
            String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
            String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            String regEx_html1 = "<[^>]+";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
            m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll(""); // 过滤html标签

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }


    /**
     * >100 -- 99+
     */
    public static String commentCount(long count) {
        String strCount;
        if (count > 100) {
            strCount = "99+";
        } else {
            strCount = String.valueOf(count);
        }
        return strCount;
    }


}
