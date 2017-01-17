package com.viewpoint.listener;

import android.graphics.Bitmap;

/**
 * 切割视图点监听
 * @author yangn
 */
public interface OnCropVideoPointListener {

    void onProgressChanged(long pointTime, Bitmap bitmap);
    // void onStartCropImg();
    // void onEndCropImg();
}
