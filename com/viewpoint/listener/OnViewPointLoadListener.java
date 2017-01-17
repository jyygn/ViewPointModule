package com.viewpoint.listener;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by yanguannan on 16/9/9.
 */
public interface OnViewPointLoadListener {
    /**
     * @param pointTime
     * @param view
     */
    void onLoadingFailed(ImageView view, long pointTimeIndex);

    /**
     * @param pointTime
     * @param view
     */
    void onLoadingComplete(ImageView view, long pointTimeIndex);
}
