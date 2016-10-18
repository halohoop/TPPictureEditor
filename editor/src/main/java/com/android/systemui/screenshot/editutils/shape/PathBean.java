/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * PathBean.java
 *
 * Save free draw path and relivant data;
 *
 * Author huanghaiqi, Created at 2016-10-18
 *
 * Ver 1.0, 2016-10-18, huanghaiqi, Create file.
 */

package com.android.systemui.screenshot.editutils.shape;

import android.graphics.Path;

public class PathBean {
    private Path mPath;
    private int mColor;
    private float mStrokeWidth;
    private int mAlpha;//0--255
    /**
     * 防止用户点一下这种down事件和up事件的空路径绘制
     */
    private boolean mIsAvailable = false;

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        this.mPath = path;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
    }

    public boolean isIsAvailable() {
        return mIsAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.mIsAvailable = isAvailable;
    }
}
