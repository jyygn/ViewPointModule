/**
 *
 */
package com.viewpoint.core;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.letv.mobile.common.ICallback;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroup;

/**
 * 视点图业务处理 从网络获取图片 切图 获取视点图
 * @author yangn
 */
public abstract class AbstractViewPointsModel {


    /**
     * 设置pointTime对应的视图点 若pointTime对应图不存在则到网络下载并切割返回
     * @param vwVideoPoint
     *            显示该视图点的视图
     * @param pointTime
     *            被显示视图点时间
     */
    public abstract void setPointImg(final ImageView ivVideoPoint,
            final int pointTime);

    /**
     * 将一张大图按宽高切割成小图
     * @param fromBitmap
     */
    public abstract void startCropBigImg(Bitmap fromBitmap, ThumImgGroup imgInfo);

    /**
     * 根据图片索引获取大图URL http://i0.letvimg.com/yunzhuanma/201410/27/6
     * c80bb7a5dda44c59537510d1865ebc5/viewpoint/
     * @param bigImgListIndex
     * @return
     */
    public abstract String getBigImgOfUrlStr(int bigImgListIndex);

    /**
     * 根据小图组信息获取bitmap
     * @param thumImgGroup
     * @param pointTime
     * @param a
     * @param callback
     */
    public abstract void getNetBitmap(final ThumImgGroup thumImgGroup,
            final int pointTime, final ICallback<Bitmap> callback);

}
