package com.viewpoint.bean;

/**
 * Created by yanguannan on 16/9/7.
 */
public class ThumImgGroupInfo {
    public final int RowLen;
    public final int ColumnLen;
    public final int ThumWidth;
    public final int ThumHeigh;
    public final ThumImgGroup ThumImgGroup;

    public ThumImgGroupInfo(int rowLen, int columnLen, int thumWidth, int thumHeigh,
            ThumImgGroup thumImgGroup) {
        RowLen = rowLen;
        ColumnLen = columnLen;
        ThumWidth = thumWidth;
        ThumHeigh = thumHeigh;
        ThumImgGroup = thumImgGroup;
    }
}
