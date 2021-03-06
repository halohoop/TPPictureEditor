/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * AlphaSeekBar.java
 *
 * alpha seek bar
 *
 * Author huanghaiqi, Created at 2016-09-30
 *
 * Ver 1.0, 2016-09-30, huanghaiqi, Create file.
 */

package com.android.systemui.screenshot.editutils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.android.systemui.screenshot.editutils.pages.R;

public class AlphaSeekBar extends SeekBar {

    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private Path mPath;
    private float mRadius;
    private Paint mPaint;
    private int mProgressColor;

    public AlphaSeekBar(Context context) {
        this(context, null);
    }

    public AlphaSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBar);
        //bouncing line stroke width and color
        mProgressColor = typedArray.getColor(R.styleable.SeekBar_ProgressColor, Color.RED);
        mPath = new Path();
        mRadius = 0;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mProgressColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        mRadius = mMeasuredHeight / 2;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        mPath.reset();
        mPath.moveTo(mMeasuredWidth + mRadius, mMeasuredHeight);
        mPath.arcTo(0, 0, mRadius * 2, mMeasuredHeight, 90, 180, false);
        mPath.lineTo(mMeasuredWidth - mRadius, 0);
        mPath.lineTo(mMeasuredWidth - mRadius, mMeasuredHeight);
        mPath.arcTo(mMeasuredWidth - mRadius * 2, 0, mMeasuredWidth, mMeasuredHeight,
                -90, 180, false);
        mPath.close();
        canvas.clipPath(mPath);
        canvas.clipRect(0, 0, mMeasuredWidth, mMeasuredHeight, Region.Op.INTERSECT);
        drawFinalShape(canvas);
        canvas.restore();
        super.onDraw(canvas);
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        mPaint.setColor(mProgressColor);
        invalidate();
    }

    public void setProgressColor(String progressColorHex) {
        int color = Color.parseColor(progressColorHex);
        setProgressColor(color);
    }

    private void drawFinalShape(Canvas canvas) {
        for (int i = 0; i < mMeasuredWidth; i++) {
            int alpha = getAlpha(i);
            mPaint.setAlpha(alpha);
            canvas.drawLine(i, 0, i, mMeasuredHeight, mPaint);
        }
    }

    private int getAlpha(int i) {
        float tmpI = i;
        return Math.round(255f * (tmpI / mMeasuredWidth));
    }

}