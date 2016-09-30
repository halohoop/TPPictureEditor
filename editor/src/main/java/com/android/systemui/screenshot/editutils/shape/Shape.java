/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * BaseShapeHolder.java
 *
 * Description
 *
 * Author huanghaiqi
 *
 * Ver 1.0, 2016-09-09, huanghaiqi, Create file
 */

package com.android.systemui.screenshot.editutils.shape;

import android.graphics.PointF;

public class Shape {

    protected float mRadius = 0;//just for circle

    protected int mOrder;
    protected Mode mMode = Mode.EDITING;
    protected TouchableArea mTouchableArea;
    protected PointF[] mPoints;
    protected ShapeType mShapeType = ShapeType.ARROW;

    public Shape(ShapeType shapeType) {
        switch (shapeType) {
            case ARROW:
                this.mPoints = new PointF[5];
                this.mShapeType = ShapeType.ARROW;
                break;
            case CIRCLE:
                this.mPoints = new PointF[2];
                this.mShapeType = ShapeType.CIRCLE;
                break;
            case RECTANGLE:
                this.mPoints = new PointF[4];
                this.mShapeType = ShapeType.RECTANGLE;
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
        ARROW, CIRCLE, RECTANGLE
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

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    @Override
    public String toString() {
        return "ShapeType{" +
                "mOrder=" + mOrder +
                ", mMode=" + mMode +
                ", mTouchableArea=" + mTouchableArea +
                '}';
    }
}
