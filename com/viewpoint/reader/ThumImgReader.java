package com.viewpoint.reader;

import java.util.Iterator;

import com.letv.mobile.core.log.Logger;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroupInfo;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImg;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroup;

/**
 * 某个Group内小图迭代器
 * Created by yanguannan on 16/9/1.
 */
public class ThumImgReader implements Iterator<ThumImg> {

    public ThumImgReader(ThumImgGroupInfo thumImgGroupInfo) {
        this.mThumImgGroupInfo = thumImgGroupInfo;
        mThumImgGroup = mThumImgGroupInfo.ThumImgGroup;
        restartPointer();
    }

    public ThumImgReader(ThumImgGroupInfo thumImgGroupInfo, ThumImg thumImg) {
        this(thumImgGroupInfo);
        restartPointer(thumImg);
    }

    private ThumImgGroupInfo mThumImgGroupInfo;
    // private ViewPointsFile mViewPointsFile;
    private int mPointTimeIndex = -1;
    // json描述小图数量
    private int mSmallImgCount = -1;
    private int mRowPointer;
    private int mColumnPointer;
    private ThumImgGroup mThumImgGroup;

    public boolean moveToSmallRect() {
        restartPointer();
        return false;
    }

    public boolean moveToThumImgGroup(ThumImgGroup thumImgGroup) {
        mThumImgGroup = thumImgGroup;
        restartPointer();
        return true;
    }

    public boolean moveToThumImgGroup(ThumImgGroup thumImgGroup, ThumImg thumImg) {
        mThumImgGroup = thumImgGroup;
        restartPointer(thumImg);
        return true;
    }

    private void restartPointer() {
        mPointTimeIndex = -1;
        mSmallImgCount = -1;
        mRowPointer = 1;
        mColumnPointer = 0;
    }

    private void restartPointer(ThumImg thumImg) {
        mPointTimeIndex = thumImg.PointTime;
        mSmallImgCount = thumImg.Row * thumImg.Column - 1;
        mRowPointer = thumImg.Row;
        mColumnPointer = thumImg.Column;
    }

    @Override
    public boolean hasNext() {
        ++mColumnPointer;

        if (++mSmallImgCount >= mThumImgGroup.Count) {
            return false;
        }

        if (hasNextColumn()) {
            return true;
        } else {
            nextRow();
            return hasNextRow();
        }
    }

    private boolean hasNextColumn() {
        return mColumnPointer <= this.mThumImgGroupInfo.ColumnLen;
    }

    private boolean hasNextRow() {
        return mRowPointer <= this.mThumImgGroupInfo.RowLen;
    }

    @Override
    public ThumImg next() {
        return new ThumImg(mThumImgGroup.PointTimes[mSmallImgCount],
                this.mThumImgGroupInfo.ThumWidth, this.mThumImgGroupInfo.ThumHeigh, mRowPointer,
                mColumnPointer);

    }

    private void nextRow() {
        this.mColumnPointer = 1;
        ++mRowPointer;
    }

    @Deprecated
    // 没有实现该功能
    @Override
    public void remove() {

    }

    public int getPointer() {
        return this.mSmallImgCount;
    }
}
