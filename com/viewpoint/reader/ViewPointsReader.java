package com.viewpoint.reader;

import com.letv.mobile.player.highlight.viewpoint.bean.ThumImg;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroup;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroupInfo;
import com.letv.mobile.player.highlight.viewpoint.file.ViewPointsFile;

import java.util.Iterator;

/**
 * 小图阅读迭代器 通过 hasNext() next() 迭代所有大图中的小图信息 可通过moveToThumImgGroup切换大图
 * Created by yanguannan on 16/9/7.
 */
public class ViewPointsReader implements Iterator<ThumImg> {

    public ViewPointsReader(ViewPointsFile viewPointsFile) {
        this.mThumImgGroupReader = new ThumImgGroupReader(viewPointsFile.getBigImgs());
        this.mThumImgReader = new ThumImgReader(new ThumImgGroupInfo(
                viewPointsFile.getThumRowLen(), viewPointsFile.getThumColumnLen(),
                viewPointsFile.getThumWidth(), viewPointsFile.getThumHeigh(),
                mThumImgGroupReader.hasNext() ? mThumImgGroupReader.next() : null));
    }

    private ThumImgReader mThumImgReader;
    private ThumImgGroupReader mThumImgGroupReader;

    public boolean moveToThumImgGroup(ThumImgGroup thumImgGroup) {
        mThumImgGroupReader.moveToThumImgGroup(thumImgGroup);
        mThumImgReader.moveToThumImgGroup(thumImgGroup);
        return true;
    }

    public boolean moveToThumImg(ThumImgGroup thumImgGroup, ThumImg thumImg) {
        mThumImgGroupReader.moveToThumImgGroup(thumImgGroup);
        mThumImgReader.moveToThumImgGroup(thumImgGroup, thumImg);
        return true;
    }

    public boolean moveToThumImg(ThumImg thumImg) {
        throw new RuntimeException("Not implemented ");
    }

    public boolean moveToProgress(long progress) {
        throw new RuntimeException("Not implemented ");
    }

    @Override
    public boolean hasNext() {
        boolean hasThumImg = mThumImgReader.hasNext();
        if (hasThumImg) {
            return true;
        } else {
            if (mThumImgGroupReader.hasNext()) {
                mThumImgReader.moveToThumImgGroup(mThumImgGroupReader.next());
                return mThumImgReader.hasNext();
            } else {
                return false;
            }

        }

    }

    @Override
    public ThumImg next() {
        return mThumImgReader.next();
    }

    @Override
    public void remove() {

    }

    public int getPointer() {
        return mThumImgReader.getPointer();
    }

}
