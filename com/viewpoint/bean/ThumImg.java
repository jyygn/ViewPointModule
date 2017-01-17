package com.viewpoint.bean;

import android.graphics.Rect;

/**
 * 小图矩形
 * Created by yanguannan on 16/8/26.
 */
public class ThumImg {

    public final int Width;
    public final int Height;
    public final int Row;
    public final int Column;
    public final int PointTime;

    public ThumImg(int pointTime, int width, int height, int row, int column) {
        PointTime = pointTime;
        Width = width;
        Height = height;
        Row = row;
        Column = column;
    }

    public Rect getRect() {
        int left = Column * Width - Width;
        int right = Column * Width;
        int top = Row * Height - Height;
        int bottom = Row * Height;
        return new Rect(left, top, right, bottom);
    }
}
