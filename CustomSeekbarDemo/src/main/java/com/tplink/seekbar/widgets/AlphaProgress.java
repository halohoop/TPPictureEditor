/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * AlphaProgress.java
 *
 * 渐变色
 *
 * Author huanghaiqi, Created at 2016-09-30
 *
 * Ver 1.0, 2016-09-30, huanghaiqi, Create file.
 */

package com.tplink.seekbar.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AlphaProgress extends View {

    private static final String TAG = "AlphaProgress";
    private Paint mPaint;
    private int mMeasuredHeight;
    private int mMeasuredWidth;

    public AlphaProgress(Context context) {
        this(context, null);
    }

    public AlphaProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(10, 10, 5, mPaint);
        for (int i = 0; i < mMeasuredWidth; i++) {
            int alpha = getAlpha(i);
            mPaint.setAlpha(alpha);
            canvas.drawLine(i, 0, i, mMeasuredHeight, mPaint);
            Log.i(TAG, "onDraw: " + " alpha:" + alpha);
        }
    }

    private int getAlpha(int i) {
        float tmpI = i;
        return Math.round(255f * (tmpI / mMeasuredWidth));
    }
}
