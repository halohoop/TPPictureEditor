/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ModelView.java
 *
 * 
 *
 * Author huanghaiqi, Created at 2016-10-19
 *
 * Ver 1.0, 2016-10-19, huanghaiqi, Create file.
 */

package com.tplink.penandrubberdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ModelView extends ImageView {

    private Paint mPaint;

    public ModelView(Context context) {
        this(context, null);
    }

    public ModelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ModelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
