package com.viewpoint.reader;

import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroup;

import java.util.Iterator;

/**
 * Created by yanguannan on 16/9/7.
 */
public class ThumImgGroupReader implements Iterator<ThumImgGroup> {

    public ThumImgGroupReader(ThumImgGroup[] thumImgGroups) {
        this.mThumImgGroup = thumImgGroups;
    }

    private ThumImgGroup[] mThumImgGroup;
    private int mPointer = -1;

    @Override
    public boolean hasNext() {
        return ++mPointer < mThumImgGroup.length;
    }

    @Override
    public ThumImgGroup next() {
        return mThumImgGroup[mPointer];
    }

    @Override
    public void remove() {

    }

    public void moveToThumImgGroup(ThumImgGroup thumImgGroup) {
        if (null == thumImgGroup) {
            throw new NullPointerException("moveToThumImgGroup thumImgGroup is null ");
        }
        for (int i = 0; i < this.mThumImgGroup.length; ++i) {
            if (this.mThumImgGroup[i].equals(thumImgGroup)) {
                this.mPointer = i - 1;
                break;
            }
        }

    }
}
