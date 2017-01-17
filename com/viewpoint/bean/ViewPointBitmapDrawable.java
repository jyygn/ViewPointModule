package com.viewpoint.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by yanguannan on 16/9/6.
 */
public class ViewPointBitmapDrawable extends BitmapDrawable {
    public ViewPointBitmapDrawable(Bitmap bitmap, long pointTime) {
        super(bitmap);
        this.pointTime = pointTime;
    }

    public long getPointTime() {
        return pointTime;
    }

    public void setPointTime(long pointTime) {
        this.pointTime = pointTime;
    }

    private long pointTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewPointBitmapDrawable that = (ViewPointBitmapDrawable) o;

        return pointTime == that.pointTime;

    }

    @Override
    public int hashCode() {
        return (int) (pointTime ^ (pointTime >>> 32));
    }
}
