package com.viewpoint.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.letv.mobile.common.ICallback;
import com.letv.mobile.player.highlight.viewpoint.bean.ImgData;
import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConstants;

/**
 * 图片下载(支持多下载)
 * @author yangn
 */
public class BitmapDownloader extends Observable {

    LinkedList<String> mQueue = new LinkedList<String>();

    volatile String mTargetUrl = null;
    volatile boolean isRun = false;
    protected ExecutorService mBitmapDownloaderThreadPool = Executors
            .newSingleThreadExecutor(new MinPriorityThreadFactory());

    void checkExists() {
        synchronized (this.mQueue) {
            for (int i = this.mQueue.size() - 1; i >= 0; --i) {
                if (ViewPointConstants.isExistsUrlImgOfCache(this.mQueue.get(i))) {
                    this.mQueue.remove(i);
                    continue;
                }

            }
        }
    }

    /**
     * 到网络上拉取大图片
     * @param url
     */
    public void pullNetImgListByUrls(final String[] urls) {
        /*
         * for(int i=0;i<urls.length;i++){ mQueue.add(new DownItem(urls[i])) }
         */
        this.mQueue.addAll(Arrays.asList(urls));
        // int size=mQueue.size();
        this.checkExists();
        synchronized (BitmapDownloader.this.mQueue) {
            if (!this.isRun) {
                BitmapDownloader.this.isRun = true;
                this.mBitmapDownloaderThreadPool.submit(this.mRunnable);
            }
        }

    }

    Runnable mRunnable = new Runnable() {

        ICallback<ImgData> mImgDataCallback = null;

        @Override
        public void run() {
            BitmapDownloader.this.checkExists();

            this.handDownloadQueue();

            synchronized (BitmapDownloader.this.mQueue) {
                BitmapDownloader.this.isRun = false;

            }

        }

        /**
         * 初始化图片回调
         */
        void initImgDataCallback() {
            if (null == this.mImgDataCallback) {

            }
            this.mImgDataCallback = new ICallback<ImgData>() {

                @Override
                public void hand(ImgData result) {

                    BitmapDownloader.this.setChanged();
                    BitmapDownloader.this.notifyObservers(result);
                }
            };
        }

        void handDownloadQueue() {
            this.initImgDataCallback();
            while (!BitmapDownloader.this.isQueueEmpty()) {

                BitmapDownloader.this.mTargetUrl = BitmapDownloader.this.mQueue.remove();

                if (null == BitmapDownloader.this.mTargetUrl
                        || "".equals(BitmapDownloader.this.mTargetUrl)) {
                    continue;
                }
                NetImg.getNetImg(BitmapDownloader.this.mTargetUrl, this.mImgDataCallback);
            }
        }
    };

    private boolean isQueueEmpty() {
        synchronized (BitmapDownloader.this.mQueue) {
            return BitmapDownloader.this.mQueue.isEmpty();
        }
    }

    /**
     * 将url移动到下载队列最前边
     * @param url
     * @param observer
     */
    public void moveFristNetImgByUrl(String url) {
        if (null == url || "".equals(url)) {
            return;
        }
        synchronized (this.mQueue) {
            if (!url.equals(this.mTargetUrl)) {
                this.mQueue.remove(url);
                this.mQueue.add(0, url);
                if (!this.isRun) {
                    this.mBitmapDownloaderThreadPool.submit(this.mRunnable);
                }
            }
        }

    }

    /**
     * 停止所有下载
     */
    public void shutdown() {
        this.mQueue.clear();
    }

}
