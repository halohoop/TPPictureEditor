/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * PenceilAndRubberView.java
 *
 * 铅笔和橡皮擦自定义view
 *
 * Author huanghaiqi, Created at 2016-09-30
 *
 * Ver 1.0, 2016-09-30, huanghaiqi, Create file.
 */

package com.halohoop.penceilandrubberview.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.halohoop.penceilandrubberview.R;

public class PenceilAndRubberView extends View {

    private Drawable mDrawableRubberOn;
    private Drawable mDrawablePenceilOn;
    private Drawable mDrawableRubberOff;
    private Drawable mDrawablePenceilOff;
    private MODE mMode = MODE.PENCEILON;
    private int mWidth;
    private int mHeight;
    private int mDeltaX;
    private int mHalfHeight;
    private int mTwoPicturePadding;
    private Rect mTopRect;
    private Rect mBottomRect;
    private int mPositionOffset;
    private int mAnimationOffset;

    public PenceilAndRubberView(Context context) {
        this(context, null);
    }

    public PenceilAndRubberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PenceilAndRubberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.PenceilAndRubberView);
        mDrawablePenceilOn = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResPenceilOn);
        mDrawableRubberOn = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResRubberOn);
        mDrawablePenceilOff = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResPenceilOff);
        mDrawableRubberOff = typedArray.getDrawable(R.styleable.PenceilAndRubberView_ResRubberOff);
        mDeltaX = Math.abs(mDrawablePenceilOn.getIntrinsicWidth()
                - mDrawableRubberOff.getIntrinsicWidth());
        mMode = MODE.PENCEILON;
        mTwoPicturePadding = 0;
        mPositionOffset = 40;
        mAnimationOffset = 30;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int intrinsicWidthPenceilOn = mDrawablePenceilOn.getIntrinsicWidth();
        int intrinsicHeightPenceilOn = mDrawablePenceilOn.getIntrinsicHeight();
        int intrinsicWidthPenceilOff = mDrawablePenceilOff.getIntrinsicWidth();
        int intrinsicHeightPenceilOff = mDrawablePenceilOff.getIntrinsicHeight();
        int intrinsicWidthRubberOn = mDrawablePenceilOn.getIntrinsicWidth();
        int intrinsicHeightRubberOn = mDrawablePenceilOn.getIntrinsicHeight();
        int intrinsicWidthRubberOff = mDrawableRubberOff.getIntrinsicWidth();
        int intrinsicHeightRubberOff = mDrawableRubberOff.getIntrinsicHeight();
        mWidth = getTheMaxOne(intrinsicWidthPenceilOn, intrinsicWidthPenceilOff,
                intrinsicWidthRubberOn, intrinsicWidthRubberOff);
        mHeight = getTheMaxOne(intrinsicHeightPenceilOn, intrinsicHeightPenceilOff,
                intrinsicHeightRubberOn, intrinsicHeightRubberOff) * 2 + 10;
        mHalfHeight = mHeight / 2;
        setMeasuredDimension(mWidth, mHeight);
        initRects();
    }

    private void initRects() {
        if (mMode == MODE.PENCEILON) {
            mTopRect = new Rect(mPositionOffset, 0, mWidth + mPositionOffset,
                    mHalfHeight - mTwoPicturePadding / 2);
            mBottomRect = new Rect(mPositionOffset + mAnimationOffset, mHalfHeight + mTwoPicturePadding / 2,
                    mWidth + mPositionOffset + mAnimationOffset, mHeight);
        } else if (mMode == MODE.RUBBERON) {
            mTopRect = new Rect(mPositionOffset + mAnimationOffset, 0,
                    mWidth + mPositionOffset + mAnimationOffset,
                    mHalfHeight - mTwoPicturePadding / 2);
            mBottomRect = new Rect(mPositionOffset, mHalfHeight + mTwoPicturePadding / 2,
                    mWidth + mPositionOffset, mHeight);
        }
    }

    public int getTheMaxOne(int... widths) {
        int max = widths[0];
        for (int i = 1; i < widths.length; i++) {
            max = Math.max(max, widths[i]);
        }
        return max;
    }

    public enum MODE {
        PENCEILON, RUBBERON
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMode == MODE.PENCEILON) {
            mDrawablePenceilOn.setBounds(mTopRect);
            mDrawableRubberOff.setBounds(mBottomRect);
            mDrawablePenceilOn.draw(canvas);
            mDrawableRubberOff.draw(canvas);
        } else {
            mDrawablePenceilOff.setBounds(mTopRect);
            mDrawableRubberOn.setBounds(mBottomRect);
            mDrawablePenceilOff.draw(canvas);
            mDrawableRubberOn.draw(canvas);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (mTopRect.contains(x, y)) {
                    //start animation
                    if (mMode != MODE.PENCEILON && mIsAnimationEnd) {
                        changeMode(MODE.PENCEILON);
                    }
                } else if (mBottomRect.contains(x, y)) {
                    //start animation
                    if (mMode != MODE.RUBBERON && mIsAnimationEnd) {
                        changeMode(MODE.RUBBERON);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public interface PenceilOrRubberModeCallBack {
        void onModeSelected(MODE mode);
    }

    private PenceilOrRubberModeCallBack mPenceilOrRubberModeCallBack;

    public void setPenceilOrRubberModeCallBack(
            PenceilOrRubberModeCallBack penceilOrRubberModeCallBack) {
        this.mPenceilOrRubberModeCallBack = penceilOrRubberModeCallBack;
    }

    private boolean mIsAnimationEnd = true;

    private void changeMode(MODE modeToChangeTo) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,
                mAnimationOffset)
                .setDuration(500);
        //mark current value
        final int currentTopLeft = mTopRect.left;
        final int currentTopRight = mTopRect.right;
        final int currentBottomLeft = mBottomRect.left;
        final int currentBottomRight = mBottomRect.right;
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        if (modeToChangeTo == MODE.PENCEILON) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    mTopRect.set(currentTopLeft - animatedValue, mTopRect.top,
                            currentTopRight - animatedValue, mTopRect.bottom);
                    mBottomRect.set(currentBottomLeft + animatedValue, mBottomRect.top,
                            currentBottomRight + animatedValue, mBottomRect.bottom);
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsAnimationEnd = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimationEnd = true;
                    mMode = MODE.PENCEILON;
                    invalidate();//final state update
                }
            });
        } else if (modeToChangeTo == MODE.RUBBERON) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    mTopRect.set(currentTopLeft + animatedValue, mTopRect.top,
                            currentTopRight + animatedValue, mTopRect.bottom);
                    mBottomRect.set(currentBottomLeft - animatedValue, mBottomRect.top,
                            currentBottomRight - animatedValue, mBottomRect.bottom);
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsAnimationEnd = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimationEnd = true;
                    mMode = MODE.RUBBERON;
                    invalidate();//final state update
                    if (mPenceilOrRubberModeCallBack != null) {
                        mPenceilOrRubberModeCallBack.onModeSelected(mMode);
                    }
                }
            });
        }
        valueAnimator.start();
    }
}
