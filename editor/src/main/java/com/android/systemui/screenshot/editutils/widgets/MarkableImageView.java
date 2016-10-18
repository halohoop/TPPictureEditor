/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * MarkableImageView.java
 *
 * Description
 *
 * Author huanghaiqi
 *
 * Ver 1.0, 2016-09-08, huanghaiqi, Create file
 */

package com.android.systemui.screenshot.editutils.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.systemui.screenshot.editutils.shape.MosaicPathBean;
import com.android.systemui.screenshot.editutils.shape.PathBean;
import com.android.systemui.screenshot.editutils.shape.Shape;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkableImageView extends View {

    final String TAG = "huanghaiqi";
    /**
     * paint to draw arrow rect circle .etc;
     */
    private Paint mShapePaint;
    private PointF mStartPointF;
    private PointF mEndPointF;
    /**
     * mark whether is Editing mode
     */
    private boolean mIsEditing = false;
    /**
     * the angle of arrow to rotate
     */
    private double mAngle;
    private float heightOfArrow = 40.0f;
    private float widthOfArrow = 10.0f;
    private float radiusCornor = 5.0f;

    public final static int MODE_FREE_DRAW_PEN = 100;
    public final static int MODE_FREE_DRAW_RUBBER = MODE_FREE_DRAW_PEN + 1;
    public final static int MODE_TEXT = MODE_FREE_DRAW_PEN + 2;
    public final static int MODE_SHAPE = MODE_FREE_DRAW_PEN + 3;
    public final static int MODE_MOSAIC_DRAW = MODE_FREE_DRAW_PEN + 4;

    /**
     * 标识当前正在那种编辑模式
     */
    private int mInWhichMode = MODE_FREE_DRAW_PEN;
    /**
     * 标识当前正在添加哪一种类型的图形（箭头，圆，方）
     */
    private Shape.ShapeType mNowAddingWhatForShapeMode = Shape.ShapeType.CIRCLE;

    //drawing data list
    private List<Action> mFreeDrawAndShapeActions = new ArrayList<>();
    private List<Shape> mShapes = new ArrayList<>();
    private List<PathBean> mPathBeans = new ArrayList<>();
    private List<MosaicPathBean> mMosaicPathBeans = new ArrayList<>();

    private float mDisX;
    private float mDisY;
    private float mShapePenStrokeWidth = 3;
    private Paint mFreePaint;
    private Paint mMosaicPaint;
    private Paint mRubberPaint;

    public MarkableImageView(Context context) {
        this(context, null);
    }

    public MarkableImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public MarkableImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        initShapePaint();

        initFreePaint();

        initMosaicPaint();

        initRubberPaint();

//        mShapePaint.setTextSize(100.0f);
        mStartPointF = new PointF();
        mEndPointF = new PointF();
    }

    private void initMosaicPaint() {
        mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMosaicPaint.setDither(true);
        mMosaicPaint.setAlpha(255);
        mMosaicPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        mMosaicPaint.setStyle(Paint.Style.STROKE);
        mMosaicPaint.setStrokeCap(Paint.Cap.ROUND);
        mMosaicPaint.setStrokeJoin(Paint.Join.ROUND);
        mMosaicPaint.setStrokeWidth(25);
    }

    private void initRubberPaint() {
        mRubberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRubberPaint.setDither(true);
        mRubberPaint.setAlpha(255);
        mRubberPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        mRubberPaint.setStyle(Paint.Style.STROKE);
        mRubberPaint.setStrokeCap(Paint.Cap.ROUND);
        mRubberPaint.setStrokeJoin(Paint.Join.ROUND);
        mRubberPaint.setStrokeWidth(25);
    }

    private void initFreePaint() {
        mFreePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFreePaint.setDither(true);
        mFreePaint.setStrokeJoin(Paint.Join.ROUND);
        mFreePaint.setColor(Color.BLACK);
        mFreePaint.setStrokeCap(Paint.Cap.ROUND);
        mFreePaint.setStyle(Paint.Style.STROKE);
        mFreePaint.setStrokeJoin(Paint.Join.ROUND);
        mFreePaint.setStrokeWidth(1);
    }

    private void initShapePaint() {
        mShapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShapePaint.setColor(Color.BLACK);
        mShapePaint.setStrokeCap(Paint.Cap.ROUND);
        mShapePaint.setStyle(Paint.Style.FILL);
        mShapePaint.setStrokeWidth(mShapePenStrokeWidth);
    }

    public void setNowAddingShapeType(Shape.ShapeType mNowAddingWhat) {
        this.mNowAddingWhatForShapeMode = mNowAddingWhat;
    }

    public void changePaintColor(int color) {
        int alpha = mFreePaint.getAlpha();
        mFreePaint.setColor(color);
        mFreePaint.setAlpha(alpha);
        mShapePaint.setColor(color);
    }

    private Bitmap mBitmap;
    private Bitmap mMosaicBitmap;

    public void setMosaicBitmap(Bitmap mosaicBitmap) {
        if (!mosaicBitmap.isMutable()) {
            this.mMosaicBitmap = Bitmap.createBitmap(mosaicBitmap.getWidth(), mosaicBitmap.getHeight(), Bitmap.Config
                    .ARGB_8888);
            Canvas canvas = new Canvas(mMosaicBitmap);
            canvas.drawBitmap(mosaicBitmap, 0, 0, null);
            mosaicBitmap.recycle();
        } else {
            this.mBitmap = mosaicBitmap;
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (!bitmap.isMutable()) {
            this.mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config
                    .ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            bitmap.recycle();
        } else {
            this.mBitmap = bitmap;
        }
        invalidate();
    }

    public void changeFreeDrawPaintThickness(int thickness) {
        mFreePaint.setStrokeWidth(thickness);
    }

    public void changeFreeDrawPaintAlpha(int alpha) {
        mFreePaint.setAlpha(alpha);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mIsEditing) {
            int action = event.getAction();
            if (mInWhichMode == MODE_FREE_DRAW_PEN) {
                handleFreeDrawModeTouch(event, action);
            } else if (mInWhichMode == MODE_SHAPE) {
                handleShapeModeTouch(event, action);
            } else if (mInWhichMode == MODE_MOSAIC_DRAW) {
                handleMosaicDrawModeTouch(event, action);
            }
            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    public int getInWhichMode() {
        return mInWhichMode;
    }

    public void setInWhichMode(int inWhichMode) {
        this.mInWhichMode = inWhichMode;
    }

    private void handleFreeDrawModeTouch(MotionEvent event, int action) {
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                PathBean pathBean = new PathBean();
                Path newPath = new Path();
                newPath.moveTo(x, y);
                pathBean.setPath(newPath);
                pathBean.setColor(mFreePaint.getColor());
                pathBean.setStrokeWidth(mFreePaint.getStrokeWidth());
                pathBean.setAlpha(mFreePaint.getAlpha());
                mPathBeans.add(pathBean);
                Action action1 = new Action(MODE_FREE_DRAW_PEN, pathBean);
                mFreeDrawAndShapeActions.add(action1);
                break;
            case MotionEvent.ACTION_MOVE:
                PathBean pathBeanMove = mPathBeans.get(mPathBeans.size() - 1);
                if (pathBeanMove != null) {
                    pathBeanMove.setIsAvailable(true);
                    Path path = pathBeanMove.getPath();
                    path.quadTo(x, y, x, y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                PathBean pathBeanUp = mPathBeans.get(mPathBeans.size() - 1);
                if (pathBeanUp != null) {
                    Path path = pathBeanUp.getPath();
                    path.quadTo(x, y, x, y);
                }
                boolean isAvailable = pathBeanUp.isIsAvailable();
                if (!isAvailable) {
                    mPathBeans.remove(pathBeanUp);
                    mFreeDrawAndShapeActions.remove(mFreeDrawAndShapeActions.size() - 1);
                }
                invalidate();
                break;
        }
    }

    private void handleShapeModeTouch(MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // This is the arrow start point
                mStartPointF.x = event.getX();
                mStartPointF.y = event.getY();

                initShapeType();
                Shape shape1 = mShapes.get(mShapes.size() - 1);
                Action nowAddingWhichAction = new Action(MODE_SHAPE, shape1);
                mFreeDrawAndShapeActions.add(nowAddingWhichAction);

                break;
            case MotionEvent.ACTION_MOVE:
                mEndPointF.x = event.getX();
                mEndPointF.y = event.getY();

                updateDistanceXY();

                updateShapeState();

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mEndPointF.x = event.getX();
                mEndPointF.y = event.getY();

                saveFinalState();
                Shape shape = mShapes.get(mShapes.size() - 1);
                boolean isAvailable = shape.isAvailable();
                if (!isAvailable) {
                    mPathBeans.remove(shape);
                    mFreeDrawAndShapeActions.remove(mFreeDrawAndShapeActions.size() - 1);
                }
                invalidate();
                break;
        }
    }

    private void handleMosaicDrawModeTouch(MotionEvent event, int action) {
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                MosaicPathBean mosaicPathBean = new MosaicPathBean();
                Path newPath = new Path();
                newPath.moveTo(x, y);
                mosaicPathBean.setPath(newPath);
                mosaicPathBean.setStrokeWidth(mMosaicPaint.getStrokeWidth());
                mMosaicPathBeans.add(mosaicPathBean);
                Action action1 = new Action(MODE_MOSAIC_DRAW, mosaicPathBean);
                mFreeDrawAndShapeActions.add(action1);
                break;
            case MotionEvent.ACTION_MOVE:
                MosaicPathBean mosaicPathBeanMove = mMosaicPathBeans.get(mMosaicPathBeans.size()
                        - 1);
                if (mosaicPathBeanMove != null) {
                    mosaicPathBeanMove.setIsAvailable(true);
                    Path path = mosaicPathBeanMove.getPath();
                    path.quadTo(x, y, x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                MosaicPathBean mosaicPathBeanUp = mMosaicPathBeans.get(mMosaicPathBeans.size() - 1);
                if (mosaicPathBeanUp != null) {
                    Path path = mosaicPathBeanUp.getPath();
                    path.quadTo(x, y, x, y);
                }
                boolean isAvailable = mosaicPathBeanUp.isIsAvailable();
                if (!isAvailable) {
                    mMosaicPathBeans.remove(mosaicPathBeanUp);
                    mFreeDrawAndShapeActions.remove(mFreeDrawAndShapeActions.size() - 1);
                }
                break;
        }
        invalidate();
    }

    private void updateDistanceXY() {
        mDisX = mEndPointF.x - mStartPointF.x;
        mDisY = mEndPointF.y - mStartPointF.y;
    }

    private void saveFinalState() {
        Shape shape = mShapes.get(mShapes.size() - 1);
        switch (shape.getShapeType()) {
            case LINE:
                PointF[] linePoints = shape.getPoints();
                linePoints[1].x = mEndPointF.x;
                linePoints[1].y = mEndPointF.y;
                break;
            case ARROW:
                PointF[] arrowPoints = shape.getPoints();
                arrowPoints[0].x = mEndPointF.x;
                arrowPoints[0].y = mEndPointF.y;
                break;
            case RECT:
                PointF[] rectPoints = shape.getPoints();
                rectPoints[1].x = mEndPointF.x;
                rectPoints[1].y = mEndPointF.y;
                break;
            case CIRCLE:
                PointF[] circlePoints = shape.getPoints();
                circlePoints[1].x = mEndPointF.x;
                circlePoints[1].y = mEndPointF.y;
                break;
            case ROUNDRECT:
                PointF[] roundRectPoints = shape.getPoints();
                roundRectPoints[1].x = mEndPointF.x;
                roundRectPoints[1].y = mEndPointF.y;
                break;
        }
    }

    private void updateShapeState() {
        Shape shape = mShapes.get(mShapes.size() - 1);
        shape.setIsAvailable(true);
        switch (mNowAddingWhatForShapeMode) {
            case LINE:
                updateLinePointFs();
                break;
            case ARROW:
                // angle of the arrow
                updateAngle();
                updateTrianglePointFs();
                break;
            case RECT:
                updateRectanglePointFs();
                break;
            case CIRCLE:
                updateCirclePointFsAndRadius();
                break;
            case ROUNDRECT:
                updateRectanglePointFs();
                break;
        }
    }

    private void updateLinePointFs() {
        Shape shape = mShapes.get(mShapes.size() - 1);
        PointF[] circlePointFs = shape.getPoints();
        circlePointFs[1].x = mEndPointF.x;
        circlePointFs[1].y = mEndPointF.y;
    }

    private void updateCirclePointFsAndRadius() {
        Shape shape = mShapes.get(mShapes.size() - 1);
        PointF[] circlePointFs = shape.getPoints();
        circlePointFs[1].x = mEndPointF.x;
        circlePointFs[1].y = mEndPointF.y;
        float radius = (float) Math.sqrt(mDisX * mDisX + mDisY * mDisY);
        shape.setRadius(radius);
    }

    private void updateRectanglePointFs() {
        Shape shape = mShapes.get(mShapes.size() - 1);
        PointF[] rectanglePointFs = shape.getPoints();
        //----------------------
        //left top to right bottom
        if (mStartPointF.x <= mEndPointF.x && mStartPointF.y <= mEndPointF.y) {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y + Math.abs(mDisY);
            rectanglePointFs[2].x = mEndPointF.x;
            rectanglePointFs[2].y = mEndPointF.y;
            rectanglePointFs[3].x = mEndPointF.x;
            rectanglePointFs[3].y = mEndPointF.y - Math.abs(mDisY);
        }
        //right bottom to left top
        else if (mStartPointF.x >= mEndPointF.x && mStartPointF.y >= mEndPointF.y) {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y - Math.abs(mDisY);
            rectanglePointFs[2].x = mEndPointF.x;
            rectanglePointFs[2].y = mEndPointF.y;
            rectanglePointFs[3].x = mEndPointF.x;
            rectanglePointFs[3].y = mEndPointF.y + Math.abs(mDisY);
        }
        //left bottom to right top
        else if (mStartPointF.x < mEndPointF.x && mStartPointF.y > mEndPointF.y) {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y - Math.abs(mDisY);
            rectanglePointFs[2].x = mEndPointF.x;
            rectanglePointFs[2].y = mEndPointF.y;
            rectanglePointFs[3].x = mEndPointF.x;
            rectanglePointFs[3].y = mEndPointF.y + Math.abs(mDisY);
        }
        //right top to left bottom
        else {
            rectanglePointFs[1].x = rectanglePointFs[0].x;
            rectanglePointFs[1].y = rectanglePointFs[0].y + Math.abs(mDisY);
            rectanglePointFs[2].x = mEndPointF.x;
            rectanglePointFs[2].y = mEndPointF.y;
            rectanglePointFs[3].x = mEndPointF.x;
            rectanglePointFs[3].y = mEndPointF.y - Math.abs(mDisY);
        }
    }

    private void initShapeType() {
        Shape shape = null;
        switch (mNowAddingWhatForShapeMode) {
            case LINE:
                Shape line = new Shape(Shape.ShapeType.LINE);
                shape = line;
                PointF[] linePoints = line.getPoints();
                linePoints[0].x = mStartPointF.x;
                linePoints[0].y = mStartPointF.y;
                break;
            case ARROW:
                Shape triangle = new Shape(Shape.ShapeType.ARROW);
                shape = triangle;
                PointF[] arrowPoints = triangle.getPoints();
                arrowPoints[4].x = mStartPointF.x;
                arrowPoints[4].y = mStartPointF.y;
                break;
            case RECT:
                Shape rectangle = new Shape(Shape.ShapeType.RECT);
                shape = rectangle;
                PointF[] rectanglePoints = rectangle.getPoints();
                rectanglePoints[0].x = mStartPointF.x;
                rectanglePoints[0].y = mStartPointF.y;
                break;
            case CIRCLE:
                Shape circle = new Shape(Shape.ShapeType.CIRCLE);
                shape = circle;
                PointF[] circlePoints = circle.getPoints();
                circlePoints[0].x = mStartPointF.x;
                circlePoints[0].y = mStartPointF.y;
                break;
            case ROUNDRECT:
                Shape roundRectangle = new Shape(Shape.ShapeType.ROUNDRECT);
                shape = roundRectangle;
                PointF[] roundRectanglePoints = roundRectangle.getPoints();
                roundRectanglePoints[0].x = mStartPointF.x;
                roundRectanglePoints[0].y = mStartPointF.y;
                break;
        }
        shape.setColor(mShapePaint.getColor());
        mShapes.add(shape);
    }

    private void updateAngle() {
//        float disX = Math.abs(mEndPointF.x - mStartPointF.x);
//        float disY = Math.abs(mEndPointF.y - mStartPointF.y);
        mAngle = Math.atan(mDisY / mDisX);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null || mMosaicBitmap == null) {
            return;
        }
        //draw picture
        canvas.drawBitmap(mMosaicBitmap, 0, 0, null);
        int i = canvas.saveLayer(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), null);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        //马赛克只操作图片，所以画在最下面
        drawMosaicAndShapeActions(canvas);
        canvas.restoreToCount(i);

        i = canvas.saveLayer(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), null);
        drawFreeDrawActions(canvas);
        canvas.restoreToCount(i);
        drawShapeActions(canvas);
    }

    private void drawFreeDrawActions(Canvas canvas) {
        int currentFree = 0;
        for (int i = 0; i < mFreeDrawAndShapeActions.size(); i++) {
            Action action = mFreeDrawAndShapeActions.get(i);
            switch (action.mThisActionBelongWhichMode) {
                case MODE_FREE_DRAW_PEN:
                    //draw our lovely free draws
                    drawFree(canvas, currentFree++);
                    break;
            }
        }
    }

    private void drawShapeActions(Canvas canvas) {
        int currentShape = 0;
        for (int i = 0; i < mFreeDrawAndShapeActions.size(); i++) {
            Action action = mFreeDrawAndShapeActions.get(i);
            switch (action.mThisActionBelongWhichMode) {
                case MODE_SHAPE:
                    //draw our lovely mShapes
                    drawShape(canvas, currentShape++);
                    break;
            }
        }
    }

    private void drawMosaicAndShapeActions(Canvas canvas) {
        int currentMosaicShape = 0;
        for (int i = 0; i < mFreeDrawAndShapeActions.size(); i++) {
            Action action = mFreeDrawAndShapeActions.get(i);
            switch (action.mThisActionBelongWhichMode) {
                case MODE_MOSAIC_DRAW:
                    //draw our lovely mosaic path
                    drawMosaic(canvas, currentMosaicShape++);
                    break;
            }
        }
    }

    public void undo() {
        if (mFreeDrawAndShapeActions.size() > 0) {
            Action action = mFreeDrawAndShapeActions.remove(mFreeDrawAndShapeActions.size() - 1);
            pushIntoUndoRedo(action);
            if (action.mThisActionBelongWhichMode == MODE_FREE_DRAW_PEN) {
                mPathBeans.remove(action.mPointer);
            } else if (action.mThisActionBelongWhichMode == MODE_SHAPE) {
                mShapes.remove(action.mPointer);
            } else if (action.mThisActionBelongWhichMode == MODE_MOSAIC_DRAW) {
                mMosaicPathBeans.remove(action.mPointer);
            }
            invalidate();
        }
    }

    public void redo() {
        Action action = popFromUndoRedo();
        if (action != null) {
            mFreeDrawAndShapeActions.add(action);
            if (action.mThisActionBelongWhichMode == MODE_FREE_DRAW_PEN) {
                mPathBeans.add((PathBean) action.mPointer);
            } else if (action.mThisActionBelongWhichMode == MODE_SHAPE) {
                mShapes.add((Shape) action.mPointer);
            } else if (action.mThisActionBelongWhichMode == MODE_MOSAIC_DRAW) {
                mMosaicPathBeans.add((MosaicPathBean) action.mPointer);
            }
            invalidate();
        }
    }

    private List<Action> mUndoRedoPools = new ArrayList<>();
    private int mUndoRedoPoolMaxSize = 20;

    private int pushIntoUndoRedo(Action action) {
        if (mUndoRedoPools.size() >= mUndoRedoPoolMaxSize) {
            mUndoRedoPools.remove(0);
        }
        mUndoRedoPools.add(action);
        return mUndoRedoPools.size();
    }

    private Action popFromUndoRedo() {
        if (mUndoRedoPools.size() > 0) {
            Action action = mUndoRedoPools.remove(mUndoRedoPools.size() - 1);
            return action;
        } else {
            return null;
        }
    }

    private void drawFree(Canvas canvas, int currentDrawIndex) {
        try {
            PathBean pathBean = mPathBeans.get(currentDrawIndex);
            Path path = pathBean.getPath();
            mFreePaint.setColor(pathBean.getColor());
            mFreePaint.setStrokeWidth(pathBean.getStrokeWidth());
            canvas.drawPath(path, mFreePaint);
        } catch (IndexOutOfBoundsException ex) {
            if (mAllowLog) {
                Log.i(TAG, "drawFree: IndexOutOfBoundsException appear");
            }
        }
    }

    private void drawMosaic(Canvas canvas, int currentDrawIndex) {
        try {
            MosaicPathBean mosaicPathBean = mMosaicPathBeans.get(currentDrawIndex);
            Path path = mosaicPathBean.getPath();
            mMosaicPaint.setStrokeWidth(mosaicPathBean.getStrokeWidth());
            canvas.drawPath(path, mMosaicPaint);
        } catch (IndexOutOfBoundsException ex) {
            if (mAllowLog) {
                Log.i(TAG, "drawFree: IndexOutOfBoundsException appear");
            }
        }
    }

    private boolean mAllowLog = true;

    private void drawShape(Canvas canvas, int currentDrawIndex) {
        try {
            Shape shape = mShapes.get(currentDrawIndex);
            mShapePaint.setColor(shape.getColor());
            PointF[] pointFs = shape.getPoints();
            switch (shape.getShapeType()) {
                case LINE:
                    canvas.drawLine(pointFs[0].x, pointFs[0].y,
                            pointFs[1].x, pointFs[1].y, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
                case ARROW:
                    //draw arrow
                    //draw triangle
                    Path triangle = new Path();
                    triangle.moveTo(pointFs[0].x, pointFs[0].y);
                    triangle.lineTo(pointFs[2].x, pointFs[2].y);
                    triangle.lineTo(pointFs[3].x, pointFs[3].y);
                    triangle.close();
                    canvas.drawPath(triangle, mShapePaint);

                    canvas.drawLine(pointFs[4].x, pointFs[4].y, pointFs[1].x,
                            pointFs[1].y, mShapePaint);

                    //draw arrow
                    break;
                case RECT:
                    mShapePaint.setStyle(Paint.Style.STROKE);
                    float minLeftTopX = Math.min(pointFs[0].x, pointFs[2].x);
                    float minLeftTopY = Math.min(pointFs[0].y, pointFs[2].y);
                    float maxRightBottomX = Math.max(pointFs[0].x, pointFs[2].x);
                    float maxRightBottomY = Math.max(pointFs[0].y, pointFs[2].y);
                    canvas.drawRect(minLeftTopX, minLeftTopY,
                            maxRightBottomX, maxRightBottomY, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
                case CIRCLE:
                    float radius = shape.getRadius();
                    mShapePaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(pointFs[0].x, pointFs[0].y, radius, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
                case ROUNDRECT:
                    mShapePaint.setStyle(Paint.Style.STROKE);
                    float minRoundLeftTopX = Math.min(pointFs[0].x, pointFs[2].x);
                    float minRoundLeftTopY = Math.min(pointFs[0].y, pointFs[2].y);
                    float maxRoundRightBottomX = Math.max(pointFs[0].x, pointFs[2].x);
                    float maxRoundRightBottomY = Math.max(pointFs[0].y, pointFs[2].y);
                    canvas.drawRoundRect(minRoundLeftTopX, minRoundLeftTopY,
                            maxRoundRightBottomX, maxRoundRightBottomY,
                            radiusCornor, radiusCornor * 2, mShapePaint);
                    mShapePaint.setStyle(Paint.Style.FILL);
                    break;
            }
        } catch (IndexOutOfBoundsException ex) {
            if (mAllowLog) {
                Log.i(TAG, "drawShape: IndexOutOfBoundsException appear");
            }
        }
    }

    public void updateTrianglePointFs() {
        Shape shape = mShapes.get(mShapes.size() - 1);
        PointF[] trianglePointFs = shape.getPoints();

        trianglePointFs[0].x = mEndPointF.x;
        trianglePointFs[0].y = mEndPointF.y;

        double sH = Math.abs(Math.sin(mAngle) * heightOfArrow);
        double cH = Math.abs(Math.cos(mAngle) * heightOfArrow);

        double sW = Math.abs(Math.sin(mAngle) * widthOfArrow);
        double cW = Math.abs(Math.cos(mAngle) * widthOfArrow);


        //left top to right bottom
        if (mStartPointF.x <= mEndPointF.x && mStartPointF.y <= mEndPointF.y) {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y - sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x - cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x + sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x - sW);
        }
        //right bottom to left top
        else if (mStartPointF.x >= mEndPointF.x && mStartPointF.y >= mEndPointF.y) {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y + sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x + cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x - sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x + sW);
        }
        //left bottom to right top
        else if (mStartPointF.x < mEndPointF.x && mStartPointF.y > mEndPointF.y) {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y + sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x - cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x - sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x + sW);
        }
        //right top to left bottom
        else {
            trianglePointFs[1].y =
                    (float) (trianglePointFs[0].y - sH);
            trianglePointFs[1].x =
                    (float) (trianglePointFs[0].x + cH);

            trianglePointFs[2].y = (float) (trianglePointFs[1].y + cW);
            trianglePointFs[2].x = (float) (trianglePointFs[1].x + sW);

            trianglePointFs[3].y = (float) (trianglePointFs[1].y - cW);
            trianglePointFs[3].x = (float) (trianglePointFs[1].x - sW);
        }
    }

    public void enterEditMode() {
        mIsEditing = true;
    }

    public void exitEditMode() {
        mIsEditing = false;
    }

    public void saveImageToFile(String oldFilePath) {
//        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//
////        Bitmap finalBitmap = bitmap.copy(bitmap.getConfig(), true);
//        Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, this.getWidth(), this.getHeight());
//        Canvas canvas = new Canvas(finalBitmap);
//        drawFreeDrawActions(canvas);
//
//        try {
//            saveFile(finalBitmap, oldFilePath);
//        } catch (IOException e) {
//            Log.e("huanghaiqi", "huanghaiqi 保存文件失败!");
//            e.printStackTrace();
//        } finally {
////            if (finalBitmap != null && !finalBitmap.isRecycled()) {
////                finalBitmap.recycle();
////            }
//        }
    }

    /**
     * 保存文件
     *
     * @param bm
     * @param oldFilePath
     * @throws IOException
     */
    private void saveFile(Bitmap bm, String oldFilePath) throws IOException {
        File oldFile = new File(oldFilePath);
        File dir = oldFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = getFileNameFromOldName(oldFile.getName());
        File myCaptureFile = new File(dir, fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        Toast.makeText(getContext(), "Save file to " + myCaptureFile.getAbsolutePath(),
                Toast.LENGTH_SHORT).show();
    }

    private String getFileNameFromOldName(String oldName) {
        String substring = oldName.substring(0, oldName.length() - 4);
        String finalName = substring + System.currentTimeMillis() + ".png";
        return finalName;
    }

    public boolean isEdited() {
        boolean isEdited = true;
        if (mFreeDrawAndShapeActions.size() <= 0) {
            isEdited = false;
        } else {
            isEdited = true;
        }
        //TODO else
        return isEdited;
    }

    public void changeRubberDrawPaintAlpha(int progress) {
        //TODO
    }

    public void changeMosaicDrawPaintAlpha(int progress) {
        mMosaicPaint.setStrokeWidth(progress);
    }

    private class Action {
        public int mThisActionBelongWhichMode = -1;
        public Object mPointer;//指向Shape or PathBean or others

        public Action(int modeFreeDraw, Object obj) {
            this.mThisActionBelongWhichMode = modeFreeDraw;
            this.mPointer = obj;
        }
    }
}