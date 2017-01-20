package com.tuxing.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuxing.app.R;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by alan on 15/12/8.
 */
public class ImageUtils {


    /**
     * 图片最大宽度.
     */
    public static final int MAX_WIDTH = 4096 / 2;
    /**
     * 图片最大高度.
     */
    public static final int MAX_HEIGHT = 4096 / 2;

    public final static DisplayImageOptions DIO_ROUND_DOWN = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defal_down_proress)
            .showImageForEmptyUri(R.drawable.defal_down_fail)
            .showImageOnFail(com.tuxing.app.R.drawable.defal_down_fail)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(10))
            .build();

    public final static DisplayImageOptions DIO_DOWN = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defal_down_proress)
            .showImageForEmptyUri(R.drawable.defal_down_fail)
            .showImageOnFail(com.tuxing.app.R.drawable.defal_down_fail)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public final static DisplayImageOptions DIO_RESOURCE_SQUARE = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defal_down_proress)
            .showImageForEmptyUri(R.drawable.resource_detail_icon)
            .showImageOnFail(com.tuxing.app.R.drawable.resource_detail_icon)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();


    public static DisplayImageOptions getDioRoundAngleOptions(int loadIcon, int failedIcon, int cornerPx) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loadIcon)
                .showImageForEmptyUri(failedIcon)
                .showImageOnFail(failedIcon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(cornerPx))
                .build();
        return options;
    }

    public final static DisplayImageOptions DIO_USER_ICON = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_avatar)
            .showImageForEmptyUri(R.drawable.default_avatar)
            .showImageOnFail(com.tuxing.app.R.drawable.default_avatar)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(10))
            .build();

    public final static DisplayImageOptions DIO_USER_ICON_round_4 = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_avatar)
            .showImageForEmptyUri(R.drawable.default_avatar)
            .showImageOnFail(com.tuxing.app.R.drawable.default_avatar)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(4))
            .build();


    public final static DisplayImageOptions DIO_BIG_SQUARE_AVATAR = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_avatar)
            .showImageForEmptyUri(com.tuxing.app.R.drawable.default_avatar)
            .showImageOnFail(com.tuxing.app.R.drawable.default_avatar)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public final static DisplayImageOptions DIO_EXPERT_AVATAR = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.expert_avatar)
            .showImageForEmptyUri(R.drawable.expert_avatar)
            .showImageOnFail(R.drawable.expert_avatar)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(10))
            .build();

    public final static DisplayImageOptions DIO_MEDIA_ICON = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.resource_detail_icon)
            .showImageForEmptyUri(com.tuxing.app.R.drawable.resource_detail_icon)
            .showImageOnFail(com.tuxing.app.R.drawable.resource_detail_icon)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
//找不到资源
//    public final static DisplayImageOptions DIO_BUBBLE = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.defal_bubble_icon)
//            .showImageForEmptyUri(com.tuxing.app.R.drawable.defal_bubble_icon)
//            .showImageOnFail(com.tuxing.app.R.drawable.defal_bubble_icon)
//            .cacheInMemory(true)
//            .cacheOnDisk(true)
//            .considerExifParams(true)
//            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//            .bitmapConfig(Bitmap.Config.RGB_565)
//            .displayer(new CircleBitmapDisplayer())
//            .build();

    public final static DisplayImageOptions DIO_BUBBLE = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defal_bubble_icon)
            .showImageForEmptyUri(com.tuxing.app.R.drawable.defal_bubble_icon)
            .showImageOnFail(com.tuxing.app.R.drawable.defal_bubble_icon)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new CircleBitmapDisplayer())
            .build();

    public final static DisplayImageOptions DIO_SPLASH = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.splash)
            .showImageForEmptyUri(R.drawable.splash)
            .showImageOnFail(R.drawable.splash)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public final static DisplayImageOptions DIO_DOWN_LYM = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defal_down_lym_proress)
            .showImageForEmptyUri(R.drawable.defal_down_lym_fail)
            .showImageOnFail(com.tuxing.app.R.drawable.defal_down_lym_fail)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public final static DisplayImageOptions DIO_UPLOAD = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defal_down_proress)
            .showImageForEmptyUri(R.drawable.add_icon)
            .showImageOnFail(com.tuxing.app.R.drawable.defal_down_fail)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public final static DisplayImageOptions DIO_LIGHT_APP = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_lightapp_avatar)
            .showImageForEmptyUri(R.drawable.default_lightapp_avatar)
            .showImageOnFail(R.drawable.default_lightapp_avatar)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(24))
            .build();

    /**
     * 高斯模糊，一般方向模糊10，迭代度7
     *
     * @param bmp        图片
     * @param hRadius    水平方向模糊度
     * @param vRadius    竖直方向模糊度
     * @param iterations 模糊迭代度
     * @return
     */
    public static Bitmap BoxBlurFilterBitmap(Bitmap bmp, int hRadius, int vRadius, int iterations) {
//        return bmp;
        try {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] inPixels = new int[width * height];
            int[] outPixels = new int[width * height];
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
            for (int i = 0; i < iterations; i++) {
                blur(inPixels, outPixels, width, height, hRadius);
                blur(outPixels, inPixels, height, width, vRadius);
            }
            blurFractional(inPixels, outPixels, width, height, hRadius);
            blurFractional(outPixels, inPixels, height, width, vRadius);
            if (bitmap == null)
                return bmp;
            bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            Logger.getLogger("BoxBlurFilter 高斯模糊").e("change date");
            return bmp;
        } catch (Error e) {
            return bmp;
        }
    }

    private static void blur(int[] in, int[] out, int width, int height,
                             int radius) {
        int widthMinus1 = width - 1;
        int r = radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int inIndexUp = (i < 0) ? 0 : (i > (width - 1)) ? (width - 1) : i;
                int rgb = in[inIndex + inIndexUp];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    private static void blurFractional(int[] in, int[] out, int width,
                                       int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            //莫名的会数组溢出
            try {
                out[outIndex] = in[width - 1];
            } catch (Error error) {
            }
            inIndex += width;
        }
    }

    /**
     * 从互联网上获取原始大小图片.
     *
     * @param url     要下载文件的网络地址
//     * @param desired Width
     *                新图片的宽
//     * @param desired Height
     *                新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        URLConnection con;
        InputStream is = null;
        try {
            URL imageURL = new URL(url);
            con = imageURL.openConnection();
            con.setDoInput(true);
            con.connect();
            is = con.getInputStream();
            // 获取资源图片
            bitmap = BitmapFactory.decodeStream(is, null, null);
        } catch (Exception e) {
            Logger.getLogger("imageUtils getBitmap").d(e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 从互联网上获取指定大小的图片.
     *
     * @param url           要下载文件的网络地址
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmap(String url, int desiredWidth, int desiredHeight) {
        Bitmap bitmap = null;
        URLConnection con;
        InputStream is = null;
        try {
            URL imageURL = new URL(url);
            con = imageURL.openConnection();
            con.setDoInput(true);
            con.connect();
            is = con.getInputStream();
            bitmap = getBitmap(is, desiredWidth, desiredHeight);
            //超出的裁掉
            if (bitmap.getWidth() > desiredWidth || bitmap.getHeight() > desiredHeight) {
                bitmap = getCutBitmap(bitmap, desiredWidth, desiredHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger("imageUtils getBitmap").d(e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 描述：裁剪图片.
     *
     * @param bitmap        the bitmap
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getCutBitmap(Bitmap bitmap, int desiredWidth, int desiredHeight) {

        if (!checkBitmap(bitmap)) {
            return null;
        }

        if (!checkSize(desiredWidth, desiredHeight)) {
            return null;
        }

        Bitmap resizeBmp = null;

        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int offsetX = 0;
            int offsetY = 0;

            if (width > desiredWidth) {
                offsetX = (width - desiredWidth) / 2;
            } else {
                desiredWidth = width;
            }

            if (height > desiredHeight) {
                offsetY = (height - desiredHeight) / 2;
            } else {
                desiredHeight = height;
            }

            resizeBmp = Bitmap.createBitmap(bitmap, offsetX, offsetY, desiredWidth, desiredHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resizeBmp != bitmap) {
                bitmap.recycle();
            }
        }
        return resizeBmp;
    }

    /**
     * 从流中获取指定大小的图片.
     *
     * @param inputStream
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap getBitmap(InputStream inputStream, int desiredWidth, int desiredHeight) {
        Bitmap bitmap = null;
        try {
            byte[] data = StreamUtil.stream2bytes(inputStream);
            bitmap = getBitmap(data, desiredWidth, desiredHeight);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(StreamUtil.class).d(e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 从流中获取指定大小的图片.
     *
     * @param data
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap getBitmap(byte[] data, int desiredWidth, int desiredHeight) {
        Bitmap resizeBmp = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            // 获取图片的原始宽度高度
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
            desiredWidth = size[0];
            desiredHeight = size[1];

            // 默认为ARGB_8888.
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            // 以下两个字段需一起使用：
            // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
            opts.inPurgeable = true;
            // 位图可以共享一个参考输入数据(inputstream、阵列等)
            opts.inInputShareable = true;
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            int sampleSize = findBestSampleSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
            opts.inSampleSize = sampleSize;

            // 创建内存
            opts.inJustDecodeBounds = false;
            // 使图片不抖动
            opts.inDither = false;
            resizeBmp = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            if (resizeBmp != null) {
                resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger("AbImageUtil class").d(e.getMessage());
        }
        return resizeBmp;
    }

    /**
     * 找到最合适的SampleSize
     *
     * @param width
     * @param height
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private static int findBestSampleSize(int width, int height, int desiredWidth, int desiredHeight) {
        double wr = (double) width / desiredWidth;
        double hr = (double) height / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    private static int[] resizeToMaxSize(int srcWidth, int srcHeight,
                                         int desiredWidth, int desiredHeight) {
        int[] size = new int[2];
        if (desiredWidth <= 0) {
            desiredWidth = srcWidth;
        }
        if (desiredHeight <= 0) {
            desiredHeight = srcHeight;
        }
        if (desiredWidth > MAX_WIDTH) {
            // 重新计算大小
            desiredWidth = MAX_WIDTH;
            float scaleWidth = (float) desiredWidth / srcWidth;
            desiredHeight = (int) (desiredHeight * scaleWidth);
        }

        if (desiredHeight > MAX_HEIGHT) {
            // 重新计算大小
            desiredHeight = MAX_HEIGHT;
            float scaleHeight = (float) desiredHeight / srcHeight;
            desiredWidth = (int) (desiredWidth * scaleHeight);
        }
        size[0] = desiredWidth;
        size[1] = desiredHeight;
        return size;
    }

    private static boolean checkBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Logger.getLogger("imageUtils getBitmap").d("原图Bitmap为空了");
            return false;
        }

        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            Logger.getLogger("imageUtils getBitmap").d("原图Bitmap大小为0");
            return false;
        }
        return true;
    }

    private static boolean checkSize(int desiredWidth, int desiredHeight) {
        if (desiredWidth <= 0 || desiredHeight <= 0) {
            Logger.getLogger("imageUtils getBitmap").d("请求Bitmap的宽高参数必须大于0");
            return false;
        }
        return true;
    }

    public static void changeImgAlpha(ImageView imageView, int alpha) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(new float[] { 1, 0, 0, 0, alpha, 0, 1, 0, 0,
                alpha, 0, 0, 1, 0, alpha, 0, 0, 0, 1, 0 });
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));

    }
}
