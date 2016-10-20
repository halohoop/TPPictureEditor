/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * MyDrawView.java
 *
 * 
 *
 * Author huanghaiqi, Created at 2016-10-19
 *
 * Ver 1.0, 2016-10-19, huanghaiqi, Create file.
 */

package com.tplink.penandrubberdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyDrawView extends View {

    private Paint mPaint;
    private int mMode = 0;//0 for pen 1 for rubber
    private Bitmap mPenAndRubberBitmap;
    private Canvas mPenAndRubberCanvas;
    private Bitmap mPicBitmap;

    public MyDrawView(Context context) {
        this(context, null);
    }

    public MyDrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MyDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(25);
    }

    public void setImageBitmap(Bitmap bm) {
        this.mPicBitmap = bm;
        mPenAndRubberBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config
                .ARGB_8888);
        mPenAndRubberCanvas = new Canvas(mPenAndRubberBitmap);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    //reset
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
        //draw picture
        canvas.drawBitmap(mPicBitmap, 0, 0, null);
    }
}
