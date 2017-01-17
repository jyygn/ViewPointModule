package com.viewpoint;

import com.letv.mobile.core.log.Logger;
import com.letv.mobile.player.highlight.viewpoint.bean.ViewPointBitmapDrawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/**
 * Created by yanguannan on 16/9/7.
 */
public class ViewPointAnim {

    public static int sTransitionDurationMillis = 50;
    private final String TAG = ViewPointAnim.class.getSimpleName();
    private Drawable[] mDrawableLayer = new Drawable[2];
    private TransitionDrawable mTransitionDrawable;
    private ViewPointBitmapDrawable mPreviouseBitmapDrawable;
    private ViewPointBitmapDrawable mCurrentTimeBitmapDrawable;

    public static void init(int transitionDurationMillis) {
        sTransitionDurationMillis = transitionDurationMillis;
    }

    /**
     * 两张图片切换过渡动画
     * @param imageView
     * @param bitmap
     * @param pointTime
     */
    public void animToPointTime(ImageView imageView, Bitmap bitmap, long pointTime) {
        if (null == mPreviouseBitmapDrawable) {
            this.mPreviouseBitmapDrawable = new ViewPointBitmapDrawable(bitmap, pointTime);
            imageView.setImageBitmap(bitmap);
            return;
        }
        this.mCurrentTimeBitmapDrawable = new ViewPointBitmapDrawable(bitmap, pointTime);
        if (null != this.mPreviouseBitmapDrawable && null != this.mCurrentTimeBitmapDrawable) {
            if (this.mPreviouseBitmapDrawable.equals(this.mCurrentTimeBitmapDrawable)) {
                return;
            }
        }
        this.mDrawableLayer[0] = this.mPreviouseBitmapDrawable;
        this.mDrawableLayer[1] = this.mCurrentTimeBitmapDrawable;
        this.mTransitionDrawable = new TransitionDrawable(this.mDrawableLayer);
        imageView.setImageDrawable(this.mTransitionDrawable);
        this.mTransitionDrawable.startTransition(sTransitionDurationMillis);
        Logger.i(TAG, "previousTime " + this.mPreviouseBitmapDrawable.getPointTime()
                + "  currnetTime " + this.mCurrentTimeBitmapDrawable.getPointTime());
        this.mPreviouseBitmapDrawable = this.mCurrentTimeBitmapDrawable;
    }

    public void clear() {
        this.mDrawableLayer = null;
        this.mTransitionDrawable = null;
        this.mPreviouseBitmapDrawable = null;
        this.mCurrentTimeBitmapDrawable = null;
    }

}
