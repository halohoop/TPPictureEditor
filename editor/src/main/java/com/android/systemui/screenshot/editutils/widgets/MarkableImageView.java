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
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.systemui.screenshot.editutils.shape.PathBean;
import com.android.systemui.screenshot.editutils.shape.Shape;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkableImageView extends ImageView {

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

    public final static int MODE_FREE_DRAW = 100;
    public final static int MODE_TEXT = MODE_FREE_DRAW + 1;
    public final static int MODE_SHAPE = MODE_FREE_DRAW + 2;
    public final static int MODE_MOSAIC_DRAW = MODE_FREE_DRAW + 3;

    /**
     * 标识当前正在那种编辑模式
     */
    private int mInWhichMode = MODE_FREE_DRAW;
    /**
     * 标识当前正在添加哪一种类型的图形（箭头，圆，方）
     */
    private Shape.ShapeType mNowAddingWhatForShapeMode = Shape.ShapeType.CIRCLE;

    //drawing data list
    private List<Shape> mShapes = new ArrayList<>();
    private List<PathBean> mFreeDrawsPath = new ArrayList<>();

    private float mDisX;
    private float mDisY;
    private float mShapePenStrokeWidth = 3;
    private Paint mFreePaint;

    public MarkableImageView(Context context) {
        this(context, null);
    }

    public MarkableImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public MarkableImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        mShapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFreePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mShapePaint.setTextSize(100.0f);
        mShapePaint.setColor(Color.BLACK);
        mFreePaint.setColor(Color.BLACK);
        mShapePaint.setStrokeCap(Paint.Cap.ROUND);
        mFreePaint.setStrokeCap(Paint.Cap.ROUND);
        mFreePaint.setStyle(Paint.Style.STROKE);
        mShapePaint.setStyle(Paint.Style.FILL);
        mShapePaint.setStrokeWidth(mShapePenStrokeWidth);
        mFreePaint.setStrokeWidth(1);
        mStartPointF = new PointF();
        mEndPointF = new PointF();
    }

    public void setNowAddingShapeType(Shape.ShapeType mNowAddingWhat) {
        this.mNowAddingWhatForShapeMode = mNowAddingWhat;
    }

    public void changePaintColor(int color) {
        mFreePaint.setColor(color);
        mShapePaint.setColor(color);
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
            if (mInWhichMode == MODE_FREE_DRAW) {
                handleFreeDrawModeTouch(event, action);
            } else if (mInWhichMode == MODE_SHAPE) {
                handleShapeModeTouch(event, action);
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
                mFreeDrawsPath.add(pathBean);
                break;
            case MotionEvent.ACTION_MOVE:
                PathBean pathBeanMove = mFreeDrawsPath.get(mFreeDrawsPath.size() - 1);
                if (pathBeanMove != null) {
                    pathBeanMove.setIsAvailable(true);
                    Path path = pathBeanMove.getPath();
                    path.quadTo(x, y, x, y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                PathBean pathBeanUp = mFreeDrawsPath.get(mFreeDrawsPath.size() - 1);
                if (pathBeanUp != null) {
                    Path path = pathBeanUp.getPath();
                    path.quadTo(x, y, x, y);
                }
                boolean isAvailable = pathBeanUp.isIsAvailable();
                if (!isAvailable) {
                    mFreeDrawsPath.remove(pathBeanUp);
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

                invalidate();
                break;
        }
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
        //draw picture
        super.onDraw(canvas);
        //draw our lovely free draws
        drawFree(canvas);
        //draw our lovely mShapes
        drawShape(canvas);
        //TODO draw our lovely text
        //TODO draw our lovely mosaic
    }

    private void drawFree(Canvas canvas) {
        for (int i = 0; i < mFreeDrawsPath.size(); i++) {
            PathBean pathBean = mFreeDrawsPath.get(i);
            Path path = pathBean.getPath();
            mFreePaint.setColor(pathBean.getColor());
            mFreePaint.setStrokeWidth(pathBean.getStrokeWidth());
            canvas.drawPath(path, mFreePaint);
        }
    }

    private void drawShape(Canvas canvas) {
        for (int i = 0; i < mShapes.size(); i++) {
            Shape shape = mShapes.get(i);
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
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
        Bitmap bitmap = drawable.getBitmap();

//        Bitmap finalBitmap = bitmap.copy(bitmap.getConfig(), true);
        Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, this.getWidth(), this.getHeight());
        Canvas canvas = new Canvas(finalBitmap);
        drawShape(canvas);

        try {
            saveFile(finalBitmap, oldFilePath);
        } catch (IOException e) {
            Log.e("huanghaiqi", "huanghaiqi 保存文件失败!");
            e.printStackTrace();
        } finally {
//            if (finalBitmap != null && !finalBitmap.isRecycled()) {
//                finalBitmap.recycle();
//            }
        }
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
        if (mFreeDrawsPath.size() <= 0 && mShapes.size() <= 0) {
            isEdited = false;
        } else {
            isEdited = true;
        }
        //TODO else
        return isEdited;
    }
}