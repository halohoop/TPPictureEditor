/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ColorPicker.java
 *
 * 颜色选择器
 *
 * Author huanghaiqi, Created at 2016-09-26
 *
 * Ver 1.0, 2016-09-26, huanghaiqi, Create file.
 */

package com.android.systemui.screenshot.editutils.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.systemui.screenshot.editutils.pages.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends View {
    private Context mContext;
    private List<Integer> mColors = new ArrayList<>();
    private List<Rect> mRects = new ArrayList<>();
    private int mMeasuredWidth;
    private int mColorSelected;
    private int mMeasuredHeight;
    private int mEveryColorWidth;
    private Paint mPaint;
    private Bitmap mTickBitmap;

    public ColorPicker(Context context) {
        this(context, null);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        //init the default colors
        setColors();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTickBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tick);

    }

    public void setColors(String... colorHexs) {
        if (colorHexs == null || colorHexs.length <= 0) {
            mColors.add(Color.RED);
            mColors.add(Color.GREEN);
            mColors.add(Color.BLUE);
            mColors.add(Color.CYAN);
            mColors.add(Color.BLACK);
            mColors.add(Color.GRAY);
        } else {
            mColors.clear();
            for (int i = 0; i < colorHexs.length; i++) {
                int color = parseColor(colorHexs[i]);
                mColors.add(color);
            }
        }
        mColorSelected = mColors.get(0);
        getEveryColorRects();
        invalidate();
    }

    public int parseColor(String colorHex) {
        try {
            return Color.parseColor(colorHex);
        } catch (IllegalArgumentException iae) {
            return Color.BLACK;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        getEveryColorRects();
    }

    private void getEveryColorRects() {
        //get every color rect width
        mEveryColorWidth = mMeasuredWidth / mColors.size();
        //get every color rects
        mRects.clear();
        for (int i = 0; i < mColors.size(); i++) {
            int thisLeft = mEveryColorWidth * i;
            int thisRight = thisLeft + mEveryColorWidth;
            mRects.add(new Rect(thisLeft, 0, thisRight, mMeasuredHeight));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mRects.size(); i++) {
            Rect rect = mRects.get(i);
            Integer color = mColors.get(i);
            mPaint.setColor(color);
            canvas.drawRect(rect, mPaint);
            if (color == mColorSelected) {
                int left = rect.left + Math.abs(rect.left - rect.right) / 2 - mTickBitmap
                        .getWidth() / 2;
                int right = mMeasuredHeight / 2 - mTickBitmap.getHeight() / 2;
                canvas.drawBitmap(mTickBitmap, left, right, mPaint);
            }
        }
    }

    private ColorPickCallBack mColorPickCallBack;

    public void setColorPickCallBack(ColorPickCallBack colorPickCallBack) {
        this.mColorPickCallBack = colorPickCallBack;
    }

    interface ColorPickCallBack {
        void onColorPicked(int color);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                for (int i = 0; i < mRects.size(); i++) {
                    Rect rect = mRects.get(i);
                    if (rect.contains(x, y)) {
                        mColorSelected = mColors.get(i);
                        if (mColorPickCallBack != null) {
                            mColorPickCallBack.onColorPicked(mColorSelected);
                        }
                        invalidate();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
        }
        return true;
    }

    public int getColorSelected() {
        return mColorSelected;
    }
}