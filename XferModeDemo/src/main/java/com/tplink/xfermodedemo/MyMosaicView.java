/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * MyMosaicView.java
 *
 * 
 *
 * Author huanghaiqi, Created at 2016-10-18
 *
 * Ver 1.0, 2016-10-18, huanghaiqi, Create file.
 */

package com.tplink.xfermodedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyMosaicView extends View {

    private Paint mEraserPaint;
    private float mDownX;
    private float mDownY;
    private Path path;
    private Canvas mCanvas;

    public MyMosaicView(Context context) {
        this(context, null);
    }

    public MyMosaicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyMosaicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeWidth(30);
        path = new Path();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                path.moveTo(mDownX, mDownY);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                path.quadTo(moveX, moveY, moveX, moveY);
                mDownX = moveX;
                mDownY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                path.quadTo(upX, upY, upX, upY);
                break;
        }
        mCanvas.drawPath(path, mEraserPaint);
        invalidate();
        return true;
    }

    private Bitmap mBitmap;

    public void setBitmap(Bitmap bitmap) {
        mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config
                .ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }
}
