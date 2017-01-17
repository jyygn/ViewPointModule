package com.viewpoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;

import com.letv.mobile.common.ICallback;
import com.letv.mobile.core.config.LeTVConfig;
import com.letv.mobile.core.log.Logger;
import com.letv.mobile.player.highlight.viewpoint.bean.ImgData;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImg;
import com.letv.mobile.player.highlight.viewpoint.bean.ThumImgGroup;
import com.letv.mobile.player.highlight.viewpoint.cache.ImageMemoryCache;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConfiguration;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;
import com.letv.mobile.player.highlight.viewpoint.core.AbstractViewPointsModel;
import com.letv.mobile.player.highlight.viewpoint.file.ViewPointsFile;
import com.letv.mobile.player.highlight.viewpoint.listener.OnCropVideoPointListener;
import com.letv.mobile.player.highlight.viewpoint.listener.OnSearchThumImgListener;
import com.letv.mobile.player.highlight.viewpoint.listener.OnViewPointLoadListener;
import com.letv.mobile.player.highlight.viewpoint.reader.ViewPointsReader;
import com.letv.mobile.player.highlight.viewpoint.util.BitmapDownloader;
import com.letv.mobile.player.highlight.viewpoint.util.CropImgUtil;
import com.letv.mobile.player.highlight.viewpoint.util.FileUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Handler;
import android.widget.ImageView;

/**
 * 视点图业务操作类
 * @author yangn
 */
public class ViewPointsModule extends AbstractViewPointsModel implements Observer {

    private final String TAG = "ViewPointsModel";
    private final String CACHE_PATH_TAG = "CachePathTag";
    private final BitmapFactory.Options mBitmapFactoryOptions = new BitmapFactory.Options();
    private BitmapDownloader mBitmapDownloader = null;
    private String mUrlPrefix = "";
    private ViewPointsFile mViewPointsFile = null;
    private ViewPointTimeMapping mViewPointTimeMapping = null;
    private ViewPointsReader mViewPointsReader;
    private ViewPointAnim mViewPointAnim = new ViewPointAnim();

    private int mPreviouseSelectPointTime = 0;
    private ArrayList<OnCropVideoPointListener> mVideoPointListeners = new ArrayList<OnCropVideoPointListener>();
    private final String DESC_MP = "desc_mp.json";
    private ArrayList<OnSearchThumImgListener> mOnSearchThumImgListener = new ArrayList<OnSearchThumImgListener>();
    private OnViewPointIndexStateListener mOnViewPointIndexStateListener = null;
    private HashMap<String, Integer> mUrlAndThumImgGroupIndexMapping = new HashMap<String, Integer>();
    private boolean mDirectionLeft = false;

    public ViewPointsFile getViewPointInfo() {
        return this.mViewPointsFile;
    }

    /**
     * @param viewPointsFile
     * @throws JSONException
     */
    public ViewPointsModule(ViewPointsFile viewPointsFile) throws JSONException {

        this.mViewPointsFile = viewPointsFile;
        int lastIndex = viewPointsFile.getFromUrl().indexOf(DESC_MP);
        this.mUrlPrefix = viewPointsFile.getFromUrl().substring(0, lastIndex);

        this.pullVideoPoints();
        this.initIterator();
        this.mBitmapFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    /**
     * 获取网络视图点
     */
    public void pullVideoPoints() {
        this.mBitmapDownloader = new BitmapDownloader();
        this.mBitmapDownloader.addObserver(this);

        String[] urls = new String[this.mViewPointsFile.getBigImgs().length];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = this.getBigImgOfUrlStr(i);
        }

        this.mBitmapDownloader.pullNetImgListByUrls(urls);
    }

    void initIterator() {
        this.mViewPointTimeMapping = new ViewPointTimeMapping(this.mViewPointsFile);
        this.mViewPointTimeMapping.setOnLastListener(new ICallback<Boolean>() {

            @Override
            public void hand(Boolean result) {
                if (null != ViewPointsModule.this.mOnViewPointIndexStateListener) {
                    ViewPointsModule.this.mOnViewPointIndexStateListener.onLastItemListener();
                }

            }
        });
        mViewPointsReader = new ViewPointsReader(this.mViewPointsFile);
    }

    public void setOnSearchThumImgListener(OnSearchThumImgListener videoPointListener) {
        this.mOnSearchThumImgListener.add(videoPointListener);
    }

    private int getIndex(float index, int offset) {
        return this.iterator().searchPointTimeByProgress(index, offset, false);
    }

    /**
     * 设置pointTime对应的视图点 若pointTime对应图不存在则到网络下载并切割返回
     * @param ivVideoPoint
     *            显示该视图点的视图
     * @param ivVideoPoint
     *            被显示视图点时间(播放进度条时间 拿该时间查找离该时间最接近视点图数据)
     */
    @Override
    public void setPointImg(final ImageView ivVideoPoint, final int pointTimeIndex) {
        this.setPointImg(ivVideoPoint, pointTimeIndex, null);
    }

    public void setPointImg(final ImageView ivVideoPoint, final int pointTimeIndex,
            final OnViewPointLoadListener onViewPointLoadListener) {
        final int pointTime = getIndex(pointTimeIndex, 0);
        if (pointTime < 0) {
            return;
        }
        // TODO queue asyn
        if (null == ivVideoPoint) {
            return;
        }
        Logger.d("slidingDirect", "mDirectionLeft " + mDirectionLeft);
        if (this.mPreviouseSelectPointTime == pointTime) {
            Logger.e(TAG, "same point time");
            return;
        }
        mDirectionLeft = pointTimeIndex < mPreviouseSelectPointTime;
        mPreviouseSelectPointTime = pointTimeIndex;
        ThumImgGroup thumImgGroup = this.mViewPointsFile.searchThumImgGroupByPointTime(pointTime);
        // 获取大图 会在 内存 本地文件 网络 中检查
        this.getNetBitmap(thumImgGroup, pointTime, new ICallback<Bitmap>() {

            @Override
            public void hand(final Bitmap result) {
                if (null != onViewPointLoadListener) {
                    ViewPointsModule.this.notifyViewPointLoadListener(onViewPointLoadListener,
                            result, ivVideoPoint, pointTime);
                } else {
                    ViewPointsModule.this.setViewAnim(result, ivVideoPoint, pointTime);
                }

            }
        });
    }

    protected void setViewAnim(Bitmap result, ImageView ivVideoPoint, long pointTime) {
        if (null != result) {
            mViewPointAnim.animToPointTime(ivVideoPoint, result, pointTime);
            return;
        } else {
            Logger.d(TAG, "pointTime fail " + pointTime);
        }
    }

    private void notifyViewPointLoadListener(OnViewPointLoadListener onViewPointLoadListener,
            Bitmap result, ImageView ivVideoPoint, long pointTime) {
        if (null != result) {
            onViewPointLoadListener.onLoadingComplete(ivVideoPoint, pointTime);
            return;
        } else {
            onViewPointLoadListener.onLoadingFailed(ivVideoPoint, pointTime);
            Logger.d(TAG, "pointTime fail " + pointTime);
        }
    }

    /**
     * 根据小图组信息获取bitmap
     * @param thumImgGroup
     * @param pointTime
     * @param callback
     */
    @Override
    public void getNetBitmap(final ThumImgGroup thumImgGroup, final int pointTime,
            final ICallback<Bitmap> callback) {
        if (null == thumImgGroup) {
            Logger.e(TAG, "thumImgGroup==   null  ");
            return;
        }
        // 内存检查时候存在小图
        Bitmap result = thumImgGroup.get(pointTime);
        Logger.d(CACHE_PATH_TAG, "thumImgGroup.get(" + pointTime + ") from memory null is  "
                + (null == result));
        if (null != result) {
            callback.hand(result);
            return;
        }

        OnSearchThumImgListener onSearchThumImgListener = this.getSearchThumImgListener(
                thumImgGroup, pointTime, callback);

        final String url = this.getBigImgOfUrlStr(thumImgGroup.getBigImgListIndex());
        String localPath = ImgData.getPath(url);

        File file = new File(localPath);
        if (file.exists()) {
            Logger.d(CACHE_PATH_TAG, "bigmap local file  exists  ");
            searchSmallRect(localPath, thumImgGroup, onSearchThumImgListener);
            return;
        }
        Logger.d(CACHE_PATH_TAG, "get network bigmap file");

        this.setOnSearchThumImgListener(onSearchThumImgListener);

        this.mBitmapDownloader.moveFristNetImgByUrl(url);
    }

    /**
     * 获取缩略图监听
     * @param thumImgGroup
     * @param pointTime
     * @param callback
     * @return
     */
    protected OnSearchThumImgListener getSearchThumImgListener(final ThumImgGroup thumImgGroup,
            final int pointTime, final ICallback<Bitmap> callback) {
        OnSearchThumImgListener onSearchThumImgListener = new OnSearchThumImgListener() {
            @Override
            public boolean onSearch(String bigBitmapPath, ThumImg thumImg) {
                boolean searchResult = pointTime == thumImg.PointTime;
                if (searchResult) {
                    try {
                        final Bitmap bitmapResult = getSmallBitmapFromBigmap(bigBitmapPath, thumImg);
                        Logger.d(CACHE_PATH_TAG,
                                thumImg.PointTime + "thumImgGroup.has(smallRect.PointTime) = "
                                        + thumImgGroup.has(thumImg.PointTime) + "searchResult "
                                        + searchResult + " row " + thumImg.Row + " col "
                                        + thumImg.Column + " pointTime " + thumImg.PointTime);
                        if (!thumImgGroup.has(thumImg.PointTime)) {
                            thumImgGroup.put(thumImg.PointTime, bitmapResult);
                        }
                        // TODO 根据方向添加预加载 目前decoder图片较小在手机上加载较快 无loading 的情况 所以先不加该功能
                        // 加入此功能可使用ViewPointReader读取数据
                        ViewPointsModule.this.mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                callback.hand(bitmapResult);

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return searchResult;
            }
        };
        return onSearchThumImgListener;
    }

    /**
     * 获取来自bigBitmapPath大图中的其中thumImg描述的小图
     * @param bigBitmapPath 大图路径
     * @param thumImg 大图中的小图描述
     * @return
     * @throws IOException
     */
    protected Bitmap getSmallBitmapFromBigmap(String bigBitmapPath, ThumImg thumImg)
            throws IOException {
        BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(bigBitmapPath,
                false);
        // 目前bitmapRegionDecoder平均速度在2 3毫秒最长情况不会超过5毫秒 故没有做异步加载与预加载处理 若TV端使用或其他逻辑修改 decoder 图片较大时
        // 可再次进行测试 依据测试结果做相应优化调整
        final Bitmap bitmapResult = bitmapRegionDecoder.decodeRegion(thumImg.getRect(),
                mBitmapFactoryOptions);
        return bitmapResult;
    }

    Handler mHandler = new Handler() {
    };

    /**
     * 根据图片索引获取大图URL http://i0.letvimg.com/yunzhuanma/201410/27/6
     * c80bb7a5dda44c59537510d1865ebc5/viewpoint/
     * @param bigImgListIndex
     * @return
     */
    @Override
    public String getBigImgOfUrlStr(int bigImgListIndex) {
        StringBuilder url = new StringBuilder(this.mUrlPrefix);

        url.append((bigImgListIndex + 1));
        url.append("_");
        url.append(this.mViewPointsFile.getThumWidth());
        url.append("x");
        url.append(this.mViewPointsFile.getThumHeigh());
        url.append(".jpg");
        String urlStr = url.toString();
        this.mUrlAndThumImgGroupIndexMapping.put(urlStr, bigImgListIndex);

        return urlStr;
    }

    /**
     * 将一张大图按宽高切割成小图
     * @param fromBitmap
     */
    @Override
    public void startCropBigImg(Bitmap fromBitmap, ThumImgGroup thumImgGroup) {
        // imgInfo = mViewPointInfo.getBigImgs()[0];

        int pointTimeIndex = -1;
        // json描述小图数量
        int smallImgCount = 0;
        for (int r = 1; r <= this.mViewPointsFile.getThumRowLen(); r++) {
            for (int c = 1; c <= this.mViewPointsFile.getThumColumnLen()
                    && smallImgCount < thumImgGroup.Count; c++) {

                if (++smallImgCount > thumImgGroup.Count) {
                    break;
                }
                Bitmap bitmap = getThumbnail(fromBitmap, this.mViewPointsFile.getThumWidth(),
                        this.mViewPointsFile.getThumHeigh(), r, c);
                thumImgGroup.put(thumImgGroup.PointTimes[++pointTimeIndex], bitmap);
                // TODO add md5
                /*
                 * String key = videoId + "_" + bigImgListIndex
                 * +"_"+imgInfo.PointTimes[pointTimeIndex];
                 */
                String key = thumImgGroup.getThumCacheName(thumImgGroup.PointTimes[pointTimeIndex]);
                FileUtil.saveBitmap(bitmap, key);

                // 通知监听切割进度
                if (null != this.mVideoPointListeners) {
                    for (int i = 0; i < this.mVideoPointListeners.size(); i++) {
                        this.mVideoPointListeners.get(i).onProgressChanged(
                                thumImgGroup.PointTimes[pointTimeIndex], bitmap);
                    }

                }
            }
        }
        fromBitmap.recycle();
        fromBitmap = null;
        System.gc();
    }

    private void searchSmallRect(String path, ThumImgGroup imgInfo) {
        this.searchSmallRect(path, imgInfo, null);
    }

    /**
     * 搜索小图
     * @param path
     * @param thumImgGroup
     * @param onSearchThumImgListener
     */
    private void searchSmallRect(String path, ThumImgGroup thumImgGroup,
            OnSearchThumImgListener onSearchThumImgListener) {
        if (null != onSearchThumImgListener) {
            this.mOnSearchThumImgListener.add(onSearchThumImgListener);
        }
        ThumImg thumImg;
        this.mViewPointsReader.moveToThumImgGroup(thumImgGroup);

        while (mViewPointsReader.hasNext()) {
            thumImg = mViewPointsReader.next();
            if (this.notifySearchThumImgChange(path, thumImg)) {
                break;
            }
        }

        if (null != onSearchThumImgListener) {
            mOnSearchThumImgListener.remove(onSearchThumImgListener);
        }

    }

    /**
     * 通知搜索迭代项
     * @param path
     * @param thumImg
     * @return 迭代项符合搜索结果 返回true 否则返回false
     */
    private boolean notifySearchThumImgChange(String path, ThumImg thumImg) {
        ArrayList<OnSearchThumImgListener> onSearchThumImgListeners = (ArrayList<OnSearchThumImgListener>) mOnSearchThumImgListener
                .clone();
        boolean searchResult = false;
        for (int i = 0; i < onSearchThumImgListeners.size(); i++) {
            searchResult = onSearchThumImgListeners.get(i).onSearch(path, thumImg);
            if (searchResult) {
                break;
            }
        }
        return searchResult;
    }

    /**
     * 检查progress是否大小不超出 target 的20%范围内
     * @param progress
     * @param target
     * @return
     */
    static boolean isPointTimeRange(float progress, int target) {
        // float percentage = target * 0.2f;
        float percentage = 3000;
        return Math.abs(progress - target) <= percentage;
    }

    public int findPointTimeByProgress(float progress, int offset) {
        return this.mViewPointTimeMapping.searchPointTimeByProgress(progress, offset, true);
    }

    public ViewPointTimeMapping iterator() {
        return this.mViewPointTimeMapping;
    }

    /**
     * 根据索引从大图中获取一张小图
     * @param fromBitmap
     *            小图所在的大图
     * @param w
     *            小图宽
     * @param h
     *            小图高
     * @param c
     *            小图所在大图行数
     * @param r
     *            小图所在大图列数
     * @return
     */
    public static Bitmap getThumbnail(Bitmap fromBitmap, int w, int h, int r, int c) {
        int left = c * w - w;
        int right = c * w;
        int top = r * h - h;
        int bottom = r * h;
        return CropImgUtil.crop(fromBitmap, w, h, new Rect(left, top, right, bottom));

    }

    @Override
    public void update(Observable arg0, Object obj) {
        if (null == obj) {
            return;
        }
        final ImgData imgData = (ImgData) obj;
        // Log.e(TAG, "crop  url" + imgData.getUrl());

        int thumImgGroupIndex = this.mUrlAndThumImgGroupIndexMapping.get(imgData.getUrl());
        if (thumImgGroupIndex < 0 || thumImgGroupIndex > this.mViewPointsFile.getBigImgs().length) {
            return;
        }
        imgData.outputFile();
        imgData.close();
        searchSmallRect(imgData.getPath(), this.mViewPointsFile.getBigImgs()[thumImgGroupIndex]);

    }

    public void setOnViewPointIndexStateListener(
            OnViewPointIndexStateListener onViewPointIndexStateListener) {
        this.mOnViewPointIndexStateListener = onViewPointIndexStateListener;
    }

    public OnViewPointIndexStateListener getOnViewPointIndexStateListener() {
        return this.mOnViewPointIndexStateListener;
    }

    public interface OnViewPointIndexStateListener {
        public void onLastItemListener();

        public void onFristItemListener();
    }

    /**
     * 初始化视点图文件夹
     */
    public static void initViewPointDir() {
        File file = new File(LeTVConfig.getAppRoot());
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(ViewPointConstants.CACHE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }

    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        for (int i = 0; i < mViewPointsFile.getBigImgs().length; ++i) {
            mViewPointsFile.getBigImgs()[i].clearCache();
        }
        mViewPointAnim.clear();
    }

    public static void init(ViewPointConfiguration viewPointConfiguration) {
        ViewPointConstants.CACHE_PATH = viewPointConfiguration.getCachePath();
        ImageMemoryCache.init(viewPointConfiguration.getLruCacheSize(),
                viewPointConfiguration.getSoftCacheNum());
        CleanViewPointCacheModule.init(viewPointConfiguration.getMaxCacheSize());
        ViewPointsModule.initViewPointDir();
        DisplayThumImageOptions displayThumImageOptions = viewPointConfiguration
                .getDisplayThumImageOptions();
        if (null != displayThumImageOptions) {
            ViewPointAnim.init(displayThumImageOptions.getTransitionDurationMillis());
        }

    }
}