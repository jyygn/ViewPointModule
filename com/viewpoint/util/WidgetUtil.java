package com.viewpoint.util;

import android.view.View;

public class WidgetUtil {

    public static boolean isPointInViewHorizontal(float x, View view) {
        if (null == view) {
            throw new IllegalArgumentException("param view is null!");
        }
        int[] location = new int[2];
        view.getLocationInWindow /* getLocationOnScreen */(location);
        int left = location[0];
        int right = left + view.getWidth();

        return x >= left && x <= right;
    }

    /**
     * 判断x是否在view水平位置内
     * @param x
     * @param view
     * @return
     */
    public static boolean[] isPointInViewHorizontalForTag(float x, View view) {
        if (null == view) {
            throw new IllegalArgumentException("param view is null!");
        }

        boolean isIn = false;

        // int top = view.getTop();
        // int left = view.getLeft();
        // int right = view.getRight();
        // int bottom = view.getBottom();
        // int width = view.getWidth();
        // int height = view.getHeight();
        if (null == mLocation) {
            mLocation = new int[2];
        }
        mLocation[0] = 0;
        mLocation[1] = 0;
        view.getLocationInWindow/* getLocationOnScreen */(mLocation);
        int left = mLocation[0];
        int right = left + view.getWidth();
        boolean isLeftPart = false;// 是否在视图左半部分
        if (x >= left && x <= right) {
            isIn = true;

            // 计算水平中心位置
            int centerHorizontal = left + view.getWidth() / 2;
            int letfOrRight = (int) (x - centerHorizontal);
            isLeftPart = x < centerHorizontal;
            // 计算动画率
            float animPro = (x - left) / ((float) right - left);
            // float animPro = ((float) left - x) / ((float) right - left);
            // float animPro = Math.abs(centerHorizontal - x)
            // / ((float) (right - left) / 2);
            // float animProRet = Float.parseFloat(decimalFormat
            // .format((isLeftPart ? (1 - animPro) : animPro)));

            view.setTag(new float[] { letfOrRight, animPro /* animProRet */});

        }
        // Log.i("View", ", x: " + x + ", left: " + left + ", right: " + right
        // + ", y: " + y + ", top: " + top + ", bottom: " + bottom
        // + ", width: " + width + ", height: " + height + ", result: "
        // + isIn);

        return new boolean[] { isIn, isLeftPart };

    }

    static int[] mLocation = null;

    /**
     * 获取一个View所在窗口xy
     * @param view
     * @return 获取视图x坐标（视图左边距在屏幕位置）
     */
    public static int getViewOfX(View view) {
        if (null == view) {
            throw new IllegalArgumentException("param view is null!");
        }
        if (null == mLocation) {
            mLocation = new int[2];
        }
        mLocation[0] = 0;
        mLocation[1] = 0;
        view.getLocationInWindow/* getLocationOnScreen */(mLocation);
        int left = mLocation[0];
        return left;
    }

    /**
     * 获取一个View所在窗口y
     * @param view
     * @return 获取视图x坐标（视图左边距在屏幕位置）
     */
    public static int getViewOfY(View view) {
        if (null == view) {
            throw new IllegalArgumentException("param view is null!");
        }
        if (null == mLocation) {
            mLocation = new int[2];
        }
        mLocation[0] = 0;
        mLocation[1] = 0;
        view.getLocationInWindow/* getLocationOnScreen */(mLocation);
        int left = mLocation[1];
        return left;
    }

    /**
     * 获取一个View所在窗口xy
     * @param view
     * @return
     */
    public static int[] getViewOfXY(View view) {
        if (null == view) {
            throw new IllegalArgumentException("param view is null!");
        }
        if (null == mLocation) {
            mLocation = new int[2];
        }
        mLocation[0] = 0;
        mLocation[1] = 0;
        view.getLocationInWindow(mLocation);
        return mLocation;
    }

    /**
     * 获取视图中心x坐标（视图水平中心在屏幕位置）
     * @param view
     * @return
     */
    public static int getViewOfMiddleX(View view) {
        int middleX = WidgetUtil.getViewOfX(view) + view.getWidth() / 2;
        return middleX < 0 ? 0 : middleX;
    }

    /**
     * 测量View宽高
     * @param view
     * @return 测量完成View的measureWidth 与measureHeight将会有值
     */
    public static void measureView(View view) {

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }
}
