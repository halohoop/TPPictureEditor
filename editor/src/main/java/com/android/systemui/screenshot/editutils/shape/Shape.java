/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Shape.java
 *
 * Description
 *
 * Author huanghaiqi
 *
 * Ver 1.0, 2016-09-09, huanghaiqi, Create file
 */

package com.android.systemui.screenshot.editutils.shape;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.Arrays;

/**
 * @hide
 */
public class Shape extends PathBean {

    protected float mRadius = 0;//just for circle

    protected int mOrder;
    protected int mColor;
    protected Mode mMode = Mode.EDITING;
    protected TouchableArea mTouchableArea;
    protected PointF[] mPoints;
    protected ShapeType mShapeType = ShapeType.LINE;
    protected String mText;
    protected static final int NORMAL_TEXT_SZIE = 12;
    protected int mTextSize = NORMAL_TEXT_SZIE;
    /**
     * 防止用户点一下这种down事件和up事件的空路径绘制
     */
    private boolean mIsAvailable = false;

    public Shape(ShapeType shapeType) {
        switch (shapeType) {
            case LINE:
                this.mPoints = new PointF[2];
                this.mShapeType = ShapeType.LINE;
                break;
            case ARROW:
                this.mPoints = new PointF[5];
                this.mShapeType = ShapeType.ARROW;
                break;
            case RECT:
                this.mPoints = new PointF[4];
                this.mShapeType = ShapeType.RECT;
                break;
            case CIRCLE:
                this.mPoints = new PointF[2];
                this.mShapeType = ShapeType.CIRCLE;
                break;
            case ROUNDRECT:
                this.mPoints = new PointF[4];
                this.mShapeType = ShapeType.ROUNDRECT;
                break;
            default:
                throw new IllegalArgumentException("please choose a right enum value from the " +
                        "enum ShapeType");
        }
        for (int i = 0; i < mPoints.length; i++) {
            mPoints[i] = new PointF();
        }
    }

    protected enum Mode {
        NORMAL, EDITING
    }

    public enum ShapeType {
        LINE, ARROW, RECT, CIRCLE, ROUNDRECT
    }

    public ShapeType getShapeType() {
        return mShapeType;
    }

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(PointF[] mPoints) {
        this.mPoints = mPoints;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public int getOder() {
        return mOrder;
    }

    public void setOder(int oder) {
        this.mOrder = oder;
    }

    public TouchableArea getTouchableArea() {
        return mTouchableArea;
    }

    public void setTouchableArea(TouchableArea touchableArea) {
        this.mTouchableArea = touchableArea;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    public boolean isAvailable() {
        return mIsAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.mIsAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "Shape{" +
                "mRadius=" + mRadius +
                ", mOrder=" + mOrder +
                ", mColor=" + mColor +
                ", mMode=" + mMode +
                ", mTouchableArea=" + mTouchableArea +
                ", mPoints=" + Arrays.toString(mPoints) +
                ", mShapeType=" + mShapeType +
                ", mText='" + mText + '\'' +
                ", mTextSize=" + mTextSize +
                '}';
    }
}