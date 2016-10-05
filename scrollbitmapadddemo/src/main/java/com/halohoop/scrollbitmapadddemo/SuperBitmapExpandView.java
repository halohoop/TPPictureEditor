/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * SuperBitmapExpandView.java
 *
 * a custom view which can be show lots of bitmap
 *
 * Author huanghaiqi, Created at 2016-10-05
 *
 * Ver 1.0, 2016-10-05, huanghaiqi, Create file.
 */

package com.halohoop.scrollbitmapadddemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuperBitmapExpandView extends View {

    private int mDefaultModeChangeThreadhold = 100;
    private int mModeChangeThreadhold = mDefaultModeChangeThreadhold;
    private List<Bitmap> mBitmaps;
    private float mDownY;
    private float mScrollYdistance;

    public SuperBitmapExpandView(Context context) {
        this(context, null);
    }

    public SuperBitmapExpandView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperBitmapExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmaps = Collections.synchronizedList(new ArrayList<Bitmap>());
    }

    public void addBitmap(Bitmap bitmap) {
        this.mBitmaps.add(bitmap);
        mCurrentTotalBitmapsHeight += bitmap.getHeight();
        postInvalidate();
    }

    float mCurrentTotalBitmapsHeight = 0;

    enum SCROLLMODE {
        FIX_JUST_CAN_DRAG_UP, REACHING_TO_BOTTOM, SCROLL
    }

    private SCROLLMODE mScrollMode = SCROLLMODE.FIX_JUST_CAN_DRAG_UP;

    public int getCurrentBitmapCount() {
        return mBitmaps.size();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                float distance = moveY - mDownY;

                mModeChangeThreadhold += distance;

                if (mModeChangeThreadhold <= 0) {
                    mModeChangeThreadhold = 0;
                    mScrollMode = SCROLLMODE.SCROLL;
                } else if (mModeChangeThreadhold >= mDefaultModeChangeThreadhold
                        && mScrollYdistance == 0) {
                    mModeChangeThreadhold = mDefaultModeChangeThreadhold;
                    mScrollMode = SCROLLMODE.FIX_JUST_CAN_DRAG_UP;
                } else if (mModeChangeThreadhold > 0
                        && mModeChangeThreadhold < mDefaultModeChangeThreadhold
                        && mScrollYdistance == 0) {
                    mScrollMode = SCROLLMODE.REACHING_TO_BOTTOM;
                }
                if (mScrollMode == SCROLLMODE.SCROLL) {
                    mScrollYdistance += distance;
                    mModeChangeThreadhold = 0;
                    if (mScrollYdistance > 0) {
                        mScrollYdistance = 0;
                        mScrollMode = SCROLLMODE.REACHING_TO_BOTTOM;
                    }
                }
                float currentTotalHeight = Math.abs(mScrollYdistance) + getMeasuredHeight();
                if (!mCanScrollUpOrNot && currentTotalHeight > mCurrentTotalBitmapsHeight) {
                    mScrollYdistance = -(mCurrentTotalBitmapsHeight - getMeasuredHeight());
                    Log.i("dispatchTouchEvent", "dispatchTouchEvent: mScrollYdistance:" +
                            mScrollYdistance);
                }

                mDownY = moveY;

                justLoadMoreOrNot();

                postInvalidate();

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    public interface BitmapDataHelper {
        boolean hasMore();

        Bitmap getMore();
    }

    private BitmapDataHelper mBitmapDataHelper;

    public void setBitmapDataHelper(BitmapDataHelper bitmapDataHelper) {
        this.mBitmapDataHelper = bitmapDataHelper;
    }

    private int mHowManyDistanceToLoadMore = 0;

    private void justLoadMoreOrNot() {
        if (mBitmapDataHelper.hasMore()) {
            if (mScrollYdistance != 0) {
                int currentShownHeight = (int) (Math.abs(mScrollYdistance) + getMeasuredHeight());
                int currentTotalBitmapsHeight = 0;
                for (int i = 0; i < mBitmaps.size(); i++) {
                    currentTotalBitmapsHeight += mBitmaps.get(i).getHeight();
                }
                int howManyDistanceToLoadMore = currentTotalBitmapsHeight - currentShownHeight;
                if (mHowManyDistanceToLoadMore >= howManyDistanceToLoadMore) {
                    addBitmap(mBitmapDataHelper.getMore());
                }
            }
        } else {
            mCanScrollUpOrNot = false;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHowManyDistanceToLoadMore = getMeasuredHeight() / 3 * 2;
    }

    private boolean mCanScrollUpOrNot = true;

    @Override
    protected void onDraw(Canvas canvas) {
        //fix the draw rect
        canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - mModeChangeThreadhold,
                Region.Op.INTERSECT);//获取两个交集部分
        canvas.save();
        canvas.translate(0, mScrollYdistance);
        //draw bitmaps
        for (int i = 0; i < mBitmaps.size(); i++) {
            Bitmap bitmap = mBitmaps.get(i);
            float drawTopOffset = 0;
            if (i > 0) {
                drawTopOffset = getCurrentDrawTopOffset(i);
            }
            int leftAndRightOffset = getMeasuredWidth() - bitmap.getWidth();
            canvas.drawBitmap(bitmap, leftAndRightOffset >> 1, drawTopOffset, null);
        }
        canvas.restore();
    }

    private float getCurrentDrawTopOffset(int currentDrawBitmapIndex) {//index must > 0
        return getAllBeforeCurrentIndexBitmapHeight(currentDrawBitmapIndex);
    }

    private float getAllBeforeCurrentIndexBitmapHeight(int currentIndex) {
        int allBitmapsBeforeHeight = 0;
        for (int i = 0; i < currentIndex; i++) {
            Bitmap bitmap = mBitmaps.get(i);
            allBitmapsBeforeHeight += bitmap.getHeight();
        }
        return allBitmapsBeforeHeight;
    }
}
