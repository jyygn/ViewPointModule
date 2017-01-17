package com.viewpoint.common;

import com.letv.mobile.player.highlight.viewpoint.DisplayThumImageOptions;

import android.graphics.drawable.Drawable;

/**
 * Created by yanguannan on 16/9/8.
 */
public class ViewPointConfiguration {

    public ViewPointConfiguration(Builder builder) {
        this.builder = builder;
    }

    private Builder builder;

    public String getCachePath() {
        return this.builder.cachePath;
    }

    public int getMaxCacheSize() {
        return this.builder.maxCacheSize;
    }

    public int getSoftCacheNum() {
        return this.builder.softCacheNum;
    }

    public int getLruCacheSize() {
        return this.builder.lruCacheSize;
    }

    public DisplayThumImageOptions getDisplayThumImageOptions() {
        return this.builder.displayThumImageOptions;
    }

    public static class Builder {

        private String cachePath;
        private int maxCacheSize;
        private int softCacheNum;
        private int lruCacheSize;
        private DisplayThumImageOptions displayThumImageOptions;

        /**
         * 设置视点图缓存路径
         * @param cachePath
         * @return
         */
        public Builder setCachePath(String cachePath) {
            this.cachePath = cachePath;
            return this;
        }

        /**
         * 设置最大缓存数
         * @param maxCacheSize
         * @return
         */
        public Builder setMaxCacheSize(int maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
            return this;
        }

        /**
         * 设置最大软引用数
         * @param softCacheNum
         * @return
         */
        public Builder setSoftCacheNum(int softCacheNum) {
            this.softCacheNum = softCacheNum;
            return this;
        }

        /**
         * 设置强引用缓存容量
         * @param lruCacheSize
         * @return
         */
        public Builder setLruCacheSize(int lruCacheSize) {
            this.lruCacheSize = lruCacheSize;
            return this;
        }

        /**
         * 设置显示缩略图选项
         * @param displayThumImageOptions
         * @return
         */
        public Builder setDisplayThumImageOptions(DisplayThumImageOptions displayThumImageOptions) {
            this.displayThumImageOptions = displayThumImageOptions;
            return this;
        }

        public ViewPointConfiguration build() {
            return new ViewPointConfiguration(this);
        }

    }
}
