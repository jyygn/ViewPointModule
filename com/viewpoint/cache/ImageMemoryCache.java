package com.viewpoint.cache;

import com.letv.mobile.core.log.Logger;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

@SuppressLint("NewApi")
public class ImageMemoryCache {

    /**
     * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。
     * 强引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
     */
    private static final String TAG = "ImageMemoryCache";

    private static LruCache<Integer, Bitmap> mLruCache; // 强引用缓存

    private static LinkedHashMap<Integer, SoftReference<Bitmap>> mSoftCache; // 软引用缓存

    private static int sLruCacheSize = 4 * 1024 * 1024; // 强引用缓存容量：4MB

    private static int sSoftCacheNum = 20; // 软引用缓存个数

    public static void init(int lruCacheSize, int softCacheNum) {
        sLruCacheSize = lruCacheSize;
        sSoftCacheNum = softCacheNum;
        Logger.i(TAG, "lruCacheSize " + lruCacheSize + " softCacheNum " + softCacheNum);
    }

    // 在这里分别初始化强引用缓存和弱引用缓存
    public ImageMemoryCache() {
        mLruCache = new LruCache<Integer, Bitmap>(sLruCacheSize) {
            @Override
            // sizeOf返回为单个hashmap value的大小
            protected int sizeOf(Integer key, Bitmap value) {
                if (value != null)
                    return value.getRowBytes() * value.getHeight();
                else
                    return 0;
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue,
                    Bitmap newValue) {
                if (oldValue != null) {
                    // 强引用缓存容量满的时候或当图片位置发生改变时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存
                    Log.d(TAG,
                            "LruCache is full or when change cache position ,move to SoftRefernceCache");
                    mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
                }
            }
        };
        mSoftCache = new LinkedHashMap<Integer, SoftReference<Bitmap>>(sSoftCacheNum, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            /**
             * 当软引用数量大于20的时候，最旧的软引用将会被从链式哈希表中移出
             */
            @Override
            protected boolean removeEldestEntry(Entry<Integer, SoftReference<Bitmap>> eldest) {
                if (this.size() > sSoftCacheNum) {
                    Log.d(TAG, "should remove the eldest from SoftReference");
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 从缓存中获取图片
     */
    public Bitmap getBitmapFromMemory(Integer url) {
        Bitmap bitmap;

        // 先从强引用缓存中获取
        synchronized (mLruCache) {
            bitmap = mLruCache.get(url);
            if (bitmap != null) {
                // 如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
                mLruCache.remove(url);
                mLruCache.put(url, bitmap);
                Log.d(TAG, "get bmp from LruCache,url=" + url);
                return bitmap;
            }
        }

        // 如果强引用缓存中找不到，到软引用缓存中找，找到后就把它从软引用中移到强引用缓存中
        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    // 将图片移回LruCache
                    mLruCache.put(url, bitmap);
                    mSoftCache.remove(url);
                    Log.d(TAG, "get bmp from SoftReferenceCache, url=" + url);
                    return bitmap;
                } else {
                    mSoftCache.remove(url);
                }
            }
        }
        return null;
    }

    /**
     * 添加图片到缓存
     */
    public void addBitmapToMemory(Integer url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mLruCache) {
                mLruCache.put(url, bitmap);
            }
        }
    }

    public void clearCache() {
        mSoftCache.clear();
    }
}
