package com.viewpoint.bean;

import android.annotation.SuppressLint;
import android.view.View;

@SuppressLint("NewApi")
public class ViewWrapper {

    private View mTarget;

    public ViewWrapper(View target) {
        this.mTarget = target;
    }

    public int getWidth() {
        return this.mTarget.getLayoutParams().width;
    }

    public void setWidth(int width) {
        this.mTarget.getLayoutParams().width = width;
        this.mTarget.requestLayout();
    }

    public int getHeight() {
        return this.mTarget.getLayoutParams().height;
    }

    public void setHeight(int height) {
        this.mTarget.getLayoutParams().height = height;
        this.mTarget.requestLayout();
    }

    public void setPivotX(float pivotX) {
        this.mTarget.setPivotX(pivotX);
    }

    public void getPivotX() {
        this.mTarget.getPivotX();
    }

    public void setPivotY(float pivotY) {
        this.mTarget.setPivotX(pivotY);
    }

    public void getPivotY() {
        this.mTarget.getPivotY();
    }

    public float getAlpha() {
        return this.mTarget.getAlpha();
    }

    public void setAlpha(float alpha) {
        this.mTarget.setAlpha(alpha);
    }
}
