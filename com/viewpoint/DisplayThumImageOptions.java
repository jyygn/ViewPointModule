package com.viewpoint;

import com.letv.mobile.player.highlight.viewpoint.common.ViewPointConfiguration;

import android.graphics.drawable.Drawable;

/**
 * Created by yanguannan on 16/9/8.
 */
public class DisplayThumImageOptions {

    public DisplayThumImageOptions(Builder builder) {
        this.mBuilder = builder;
    }

    private Builder mBuilder;

    public Drawable showImageOnLoading() {
        return mBuilder.showImageOnLoading;
    }

    public Drawable getDefaultDisplayImage() {
        return mBuilder.defaultDisplayImage;
    }

    public int getTransitionDurationMillis() {
        return this.mBuilder.durationMillis;
    }

    public class Builder {
        private Drawable defaultDisplayImage;
        private Drawable showImageOnLoading;
        private int durationMillis;

        public Builder setDefaultDisplayImage(Drawable defaultDisplayImage) {
            this.defaultDisplayImage = defaultDisplayImage;
            return this;
        }

        public Builder showImageOnLoading(Drawable showImageOnLoading) {
            this.showImageOnLoading = showImageOnLoading;
            return this;
        }

        public Builder setTransitionDurationMillis(int durationMillis) {
            this.durationMillis = durationMillis;
            return this;
        }

        public DisplayThumImageOptions build() {
            return new DisplayThumImageOptions(this);
        }
    }
}
