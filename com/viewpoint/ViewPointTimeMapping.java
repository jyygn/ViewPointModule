package com.viewpoint;

import com.letv.mobile.common.ICallback;
import com.letv.mobile.core.log.Logger;
import com.letv.mobile.player.highlight.viewpoint.file.ViewPointsFile;

/**
 * 进度条时间节点与视点图时间节点映射
 * @author yangn
 */
public class ViewPointTimeMapping {

    final String TAG = "ViewPointTimeMapping";

    public ViewPointTimeMapping(ViewPointsFile viewPointsFile) {
        this.mViewPointsFile = viewPointsFile;
    }

    ViewPointsFile mViewPointsFile = null;

    public int mPrePointIndex;
    public int mNextPointIndex = -1;// 初始值第一次加载时保证是从0开始因为迭代每次会先加1再迭代
    public int mPreBigIndex;
    public int mNextBigIndex;
    public int mCurrentBigIndex = -1, mCurrentPointTimeIndex = -1;

    float previouseProgress = 0;
    /**
     * 没有进度对应视点图结果
     */
    byte NO_RESULT = -1;

    /**
     * 从未查找过
     * @return
     */
    boolean isNeverFind() {
        return this.mCurrentBigIndex == -1 && this.mCurrentPointTimeIndex == -1;
    }

    // 验证是否安全bigIndex
    boolean isSurelyBigIndex(int bigIndex) {
        if (null == this.mViewPointsFile.getBigImgs()
                || this.mViewPointsFile.getBigImgs().length == 0) {
            return false;
        }
        return bigIndex >= 0 && bigIndex < this.mViewPointsFile.getBigImgs().length;

    }

    /**
     * 通过进度条进度查找视图点对应毫秒点
     * @param progress
     *            进度
     * @param offset
     *            偏移
     * @param isCheck
     *            是否检查已经加载到最后
     * @return
     */
    public int searchPointTimeByProgress(float progress, int offset, boolean isCheck) {

        if (isCheck && this.isLast() && progress > this.previouseProgress) {
            return this.NO_RESULT;
        }
        this.previouseProgress = progress;
        int pointTime = this.NO_RESULT;
        boolean isHaveRet = false;
        int pointTimeIndexOffset = 0;

        boolean isNoAnyRange = false;
        int index = 0;

        // TODO modif to binary search
        int centerIndex = this.isNeverFind() ? this.mViewPointsFile.getBigImgBrief().length / 2
                : this.mCurrentPointTimeIndex;
        for (int i = 0; i < this.mViewPointsFile.getBigImgBrief().length; i++) {
            // 通过每张大图开始、结束时间查找progress是否在该张大图范围内
            if (this.mViewPointsFile.getBigImgBrief()[i][0] < progress
                    && progress < this.mViewPointsFile.getBigImgBrief()[i][1]) {
                index = i;
                isNoAnyRange = false;
                break;

            } else if (progress >= this.mViewPointsFile.getBigImgBrief()[i][1]) {
                // 在两没个数组范围外而不在某个数组范围内的值取上一个数组最大数
                index = i;
                pointTimeIndexOffset = this.mViewPointsFile.getBigImgs()[index].PointTimes.length - 1;
                isNoAnyRange = true;
            }

        }

        if (isNoAnyRange) {
            // bigimg某个item数据为空情况 错误数据
            if (pointTimeIndexOffset < 0) {
                Logger.e(this.TAG, "find point time  not result");
                return this.NO_RESULT;
            }
            this.mCurrentPointTimeIndex = pointTimeIndexOffset;
            this.mCurrentBigIndex = index;
            pointTime = this.mViewPointsFile.getBigImgs()[this.mCurrentBigIndex].PointTimes[this.mCurrentPointTimeIndex];
        } else {
            // 遍历所在大图查找最接近查找进度的视点图时间节点
            for (int i = 0; i < this.mViewPointsFile.getBigImgs()[index].PointTimes.length - 1; i++) {

                if (isHaveRet = (progress < this.mViewPointsFile.getBigImgs()[index].PointTimes[i + 1] && progress >= this.mViewPointsFile
                        .getBigImgs()[index].PointTimes[i])) {
                    this.mCurrentPointTimeIndex = i;
                    this.mCurrentBigIndex = index;
                    pointTime = this.mViewPointsFile.getBigImgs()[this.mCurrentBigIndex].PointTimes[this.mCurrentPointTimeIndex];

                    // pointTimeIndex += offset;
                    break;
                }

            }
        }

        // 前后偏移默认与当前索引一致 处理被选中项目是第一项或最后一项时 不重新计算前后值的情况

        this.mPrePointIndex = this.mCurrentPointTimeIndex;
        this.mPreBigIndex = this.mCurrentBigIndex;
        this.mNextPointIndex = this.mCurrentPointTimeIndex;
        this.mNextBigIndex = this.mCurrentBigIndex;

        if (!isHaveRet) {
            Logger.d(this.TAG, "search no  result!!!!!!!!");
        }
        this.notifyLastItemListener();
        return pointTime;
    }

    int mPreviousBigIndex = 0, mPreviousePointTimeIndex;

    void notifyLastItemListener() {

        if (this.isLast()) {
            this.mPreviousBigIndex = this.mNextBigIndex;
            this.mPreviousePointTimeIndex = this.mNextPointIndex;
            if (null != this.mOnLastListener) {
                this.mOnLastListener.hand(null);
            }

        }

    }

    boolean isLast = false;

    public boolean isLast() {
        return this.isLast = (this.mNextBigIndex == this.mViewPointsFile.getBigImgs().length - 1 && this.mNextPointIndex == this.mViewPointsFile
                .getBigImgs()[this.mNextBigIndex].Count - 1);
    }

    ICallback<Boolean> mOnLastListener = null;

    public void setOnLastListener(ICallback<Boolean> callback) {
        this.mOnLastListener = callback;
    }
}
