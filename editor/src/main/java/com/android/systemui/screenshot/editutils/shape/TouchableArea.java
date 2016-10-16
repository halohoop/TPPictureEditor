/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * TouchableArea.java
 *
 * Description
 *
 * Author huanghaiqi
 *
 * Ver 1.0, 2016-09-09, huanghaiqi, Create file
 */

package com.android.systemui.screenshot.editutils.shape;

import android.graphics.PointF;

/**
 * @hide
 */
public class TouchableArea {
    private PointF mLeftTop;
    private PointF mRightBottom;

    public PointF getLeftTop() {
        return mLeftTop;
    }

    public void setLeftTop(PointF leftTop) {
        this.mLeftTop = leftTop;
    }

    public PointF getRightBottom() {
        return mRightBottom;
    }

    public void setRightBottom(PointF rightBottom) {
        this.mRightBottom = rightBottom;
    }

    @Override
    public String toString() {
        return "TouchableArea{" +
                "mLeftTop=" + mLeftTop +
                ", mRightBottom=" + mRightBottom +
                '}';
    }
}