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

package com.tplink.bitmapexpanddemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BitmapAutoExpandView extends View
        implements Animator.AnimatorListener, GestureDetector.OnDoubleTapListener {

    private Bitmap mBitmap;
    private Bitmap mFooterBitmap;
    private Paint mPaint;
    private static final int FIX_902_WIDTH = 518;
    private float mRatio = -1;
    private float mDoneRatio = -1;
    private float mScrollYdistance = 0;
    private float mDoneScrollYdistance = 0;
    private int offsetScroll = 100;//175
    private float mVelocity = 0.5f;
    private List<TaskAnimatorData> mTaskAnimatorDatas = new ArrayList<>();
    private float mDefaultModeChangeThreadhold = 66;
    private float mModeChangeThreadhold = mDefaultModeChangeThreadhold;
    private boolean mIsExpandDone = false;
    private boolean mIsEditMode = false;
    private boolean isAllowDebug = true;
    private GestureDetector mGestureDetector;

    public BitmapAutoExpandView(Context context) {
        this(context, null);
    }

    public BitmapAutoExpandView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapAutoExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        mGestureDetector.setOnDoubleTapListener(this);
    }

    public void setFooterBitmap(Bitmap footerBitmap) {
        if (footerBitmap != null) {
            this.mFooterBitmap = footerBitmap;
            this.mModeChangeThreadhold = mFooterBitmap.getHeight();
        }
    }

    private class TaskAnimatorData {
        float shrinkBitmapHeight;

        public TaskAnimatorData(float shrinkBitmapHeight) {
            this.shrinkBitmapHeight = shrinkBitmapHeight;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void expandBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int originBitmapWidth = bitmap.getWidth();
            if (mRatio == -1) {
                mRatio = Utils.getExpandScaleRatio(originBitmapWidth, FIX_902_WIDTH);
            }
            expandBitmapOnExistOne(bitmap);
            float shrinkBitmapHeight = ((float) bitmap.getHeight()) * mRatio;
            if (mTaskAnimatorDatas.size() == 0) {
                ValueAnimator scrollAnimation = createNextScrollAnimator(shrinkBitmapHeight);
                scrollAnimation.start();
            }
            mTaskAnimatorDatas.add(new TaskAnimatorData(shrinkBitmapHeight));
        }
    }

    private void expandBitmapOnExistOne(Bitmap bitmap) {
        Bitmap coalecence = null;
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config
                    .ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);
            Bitmap rest = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(rest, 0, 0, null);
        } else {
            //new a canvas of last main bitmap state
            coalecence = Bitmap.createBitmap(mBitmap.getWidth(),
                    mBitmap.getHeight() + bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(coalecence);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.drawBitmap(bitmap, 0, mBitmap.getHeight(), null);
            mBitmap.recycle();
            mBitmap = null;
            mBitmap = coalecence;
        }
    }

    private ValueAnimator createNextScrollAnimator(float shrinkBitmapHeight) {
        ValueAnimator scrollAnimation = ValueAnimator.ofFloat(mScrollYdistance,
                mScrollYdistance - shrinkBitmapHeight);
        scrollAnimation.setDuration((long) (shrinkBitmapHeight / mVelocity));
        scrollAnimation.addListener(this);
        scrollAnimation.setInterpolator(new LinearInterpolator());
        scrollAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScrollYdistance = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        return scrollAnimation;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            if (!mIsExpandDone) {//scroll
                if (mFooterBitmap == null) {
                    mModeChangeThreadhold = mModeChangeThreadhold - Math.abs(mScrollYdistance);
                    if (mModeChangeThreadhold < 0) {
                        mModeChangeThreadhold = 0;
                    }
                    canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    canvas.clipRect(0, 0, getMeasuredWidth(),
                            getMeasuredHeight() - mModeChangeThreadhold,
                            Region.Op.INTERSECT);//get two rects intersect parts
                }
                canvas.save();
                canvas.translate(0, offsetScroll + mScrollYdistance);
                canvas.scale(mRatio, mRatio, getMeasuredWidth() >> 1, 0);
                int leftAndRightOffset = getMeasuredWidth() - mBitmap.getWidth();
                canvas.drawBitmap(mBitmap, leftAndRightOffset >> 1, 0, null);
                if (mFooterBitmap != null) {
                    canvas.drawBitmap(mFooterBitmap, leftAndRightOffset >> 1, mBitmap.getHeight()
                            , null);
                }
                canvas.restore();
            } else {//scroll done
                //fix the draw rect
                canvas.save();
                canvas.translate(0, offsetScroll + mDoneScrollYdistance);
                canvas.scale(mDoneRatio, mDoneRatio, getMeasuredWidth() >> 1, 0);
                int leftAndRightOffset = getMeasuredWidth() - mBitmap.getWidth();
                canvas.drawBitmap(mBitmap, leftAndRightOffset >> 1, 0, null);
                if (mFooterBitmap != null) {
                    canvas.drawBitmap(mFooterBitmap, leftAndRightOffset >> 1, mBitmap.getHeight()
                            , null);
                }
                canvas.restore();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mIsEditMode) {
//            if (event.getPointerCount() > 1) {
            return mGestureDetector.onTouchEvent(event);
//            }
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (mTaskAnimatorDatas.size() > 0) {
            mTaskAnimatorDatas.remove(0);
            if (mTaskAnimatorDatas.size() > 0) {
                TaskAnimatorData nowThisTurn = mTaskAnimatorDatas.get(0);
                ValueAnimator nextScrollAnimator = createNextScrollAnimator(nowThisTurn
                        .shrinkBitmapHeight);
                nextScrollAnimator.start();
            } else {
                //the last is done
                mIsExpandDone = true;
                animatorToCropMode();
            }

        }
    }

    private void animatorToCropMode() {
        AnimatorSet as = new AnimatorSet();
        ValueAnimator doneShrinkSCrollAnimation = ValueAnimator.ofFloat(mScrollYdistance,
                0);
        doneShrinkSCrollAnimation.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        doneShrinkSCrollAnimation.setDuration(750);
        doneShrinkSCrollAnimation.addUpdateListener(new ValueAnimator
                .AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDoneScrollYdistance = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        float endRatio = 1;
        if (mFooterBitmap == null) {
            endRatio = Utils.getDoneRatio(mBitmap.getHeight(),
                    getMeasuredHeight() - mDefaultModeChangeThreadhold - offsetScroll);
        } else {
            endRatio = Utils.getDoneRatio(mBitmap.getHeight(),
                    getMeasuredHeight() - mFooterBitmap.getHeight() - offsetScroll);
        }
        ValueAnimator doneShrinkScaleAnimation = ValueAnimator.ofFloat(mRatio,
                endRatio);
        doneShrinkScaleAnimation.setDuration(750);
        doneShrinkScaleAnimation.setInterpolator(new AnticipateOvershootInterpolator(0.8f));
        doneShrinkScaleAnimation.addUpdateListener(new ValueAnimator
                .AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDoneRatio = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        as.playTogether(doneShrinkSCrollAnimation, doneShrinkScaleAnimation);
        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(getContext(), "Done To enter Crop Mode", Toast.LENGTH_SHORT)
                        .show();
                mIsEditMode = true;
            }
        });
        as.start();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isAllowDebug) {
            Log.i("onSingleTapConfirmed", "onSingleTapConfirmed");
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (isAllowDebug) {
            Log.i("onDoubleTap", "Gesture onDoubleTap");
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        if (isAllowDebug) {
            Log.i("onDoubleTapEvent", "Gesture onDoubleTapEvent");
        }
        return true;
    }

    //OnGestureListener监听
    private class GestureListener implements GestureDetector.OnGestureListener {

        public boolean onDown(MotionEvent e) {
            if (isAllowDebug) {
                Log.i("onDown", "Gesture onDown");
            }
            return true;
        }

        public void onShowPress(MotionEvent e) {
            if (isAllowDebug) {
                Log.i("onShowPress", "Gesture onShowPress");
            }
        }

        public boolean onSingleTapUp(MotionEvent e) {
            if (isAllowDebug) {
                Log.i("onSingleTapUp", "Gesture onSingleTapUp");
            }
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (isAllowDebug) {
                Log.i("onScroll", "Gesture onScroll:" + (e2.getX() - e1.getX()) + "   " + distanceX);
            }

            return true;
        }

        public void onLongPress(MotionEvent e) {
            if (isAllowDebug) {
                Log.i("onLongPress", "Gesture onLongPress");
            }
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (isAllowDebug) {
                Log.i("onFling", "Gesture onFling velocityY:" + velocityY);
            }
            return true;
        }
    }
}
