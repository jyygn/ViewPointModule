package com.viewpoint.bean;

import android.graphics.Bitmap;

import com.letv.mobile.player.highlight.viewpoint.cache.ImageMemoryCache;

/**
 * TODO 需优化
 * 缩略图组（大图信息)描述类
 * @author yangn
 */
public class ThumImgGroup /* extends WeakHashMap<Integer, Bitmap> */{

    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    private static ImageMemoryCache mImageMemoryCache = new ImageMemoryCache();
    public final int Step;
    // from item
    public final int[] PointTimes;
    public final int Start;
    public final int End;
    public final int Count;

    public ThumImgGroup(int step, int[] pointTimes, int start, int end, int count) {
        super();
        this.Step = step;
        this.PointTimes = pointTimes;
        this.Start = start;
        this.End = end;
        this.Count = count;
    }

    private String getThumCacheName(String videoId, int bigImgListIndex, int pointTime) {
        return videoId + "_" + bigImgListIndex + "_" + pointTime;
    }

    public String getThumCacheName(int pointTime) {
        return this.getThumCacheName(this.mVideoId, this.mBigImgListIndex, pointTime);
    }

    String mVideoId = "";

    public void setVideoId(String videoId) {
        this.mVideoId = videoId;
    }

    int mBigImgListIndex = 0;

    public void setBigImgListIndex(int bigImgListIndex) {
        this.mBigImgListIndex = bigImgListIndex;
    }

    public int getBigImgListIndex() {
        return this.mBigImgListIndex;
    }

    public void put(Integer key, Bitmap bitmap) {
        mImageMemoryCache.addBitmapToMemory(key, bitmap);
    }

    public Bitmap get(Integer key) {
        return mImageMemoryCache.getBitmapFromMemory(key);
    }

    public boolean has(Integer key) {
        return null != get(key);
    }

    public void clearCache() {
        mImageMemoryCache.clearCache();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ThumImgGroup that = (ThumImgGroup) o;

        if (Start != that.Start)
            return false;
        return End == that.End;

    }

}
