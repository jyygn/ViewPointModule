package com.viewpoint.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.android.camera_my.Util;

public class CropImgUtil {

    static final String TAG = "CropImgUtil";

    // static Bitmap mBitmap = null;

    /**
     * 按矩形位置裁剪图片
     * @param mBitmap
     *            裁剪自该图
     * @param outputX
     *            裁剪输出宽
     * @param outputY
     *            裁剪输出高
     * @param r
     *            裁剪矩形位置描述
     * @return 裁剪图
     */
    public static Bitmap crop(Bitmap fromBitmap, int outputX, int outputY,
            Rect r) {
        int width = r.width();
        int height = r.height();

        Bitmap croppedImage = Bitmap.createBitmap(width, height,
        /* false ? Bitmap.Config.ARGB_8888 : */Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(croppedImage);
        Rect dstRect = new Rect(0, 0, width, height);
        canvas.drawBitmap(fromBitmap, r, dstRect, null);

        // Release bitmap memory as soon as possible

        // fromBitmap.recycle();

        if (outputX != 0 && outputY != 0 /* && true */) {
            croppedImage = Util.transform(new Matrix(), croppedImage, outputX,
                    outputY, true, Util.RECYCLE_INPUT);
        }
        return croppedImage;
    }

    /**
     * 计算imSampleSize
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 返回请求尺寸大小图片
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
            int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}
