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
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.android.systemui.screenshot.editutils.shape.Shape;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkableImageView extends ImageView {

    final String TAG = "huanghaiqi";
    final boolean mIsNeedDebug = true;
    /**
     * paint to draw arrow rect circle .etc;
     */
    private Paint mPaint;
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

    /**
     * 标识当前正在添加哪一种类型的图形（箭头，圆，方）
     */
    private Shape.ShapeType mNowAddingWhat = Shape.ShapeType.CIRCLE;

    private List<Shape> shapes = new ArrayList<>();
    private float mDisX;
    private float mDisY;


    public MarkableImageView(Context context) {
        this(context, null);
    }

    public MarkableImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public MarkableImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setTextSize(100.0f);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3);
        mStartPointF = new PointF();
        mEndPointF = new PointF();
    }

    public void setNowAddingShapeType(Shape.ShapeType mNowAddingWhat) {
        this.mNowAddingWhat = mNowAddingWhat;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mIsEditing) {
            int action = event.getAction();
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
            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    private void updateDistanceXY() {
        mDisX = mEndPointF.x - mStartPointF.x;
        mDisY = mEndPointF.y - mStartPointF.y;
    }

    private void saveFinalState() {
        Shape shape = shapes.get(shapes.size() - 1);
        switch (shape.getShapeType()) {
            case ARROW:
                PointF[] arrowPoints = shape.getPoints();
                arrowPoints[0].x = mEndPointF.x;
                arrowPoints[0].y = mEndPointF.y;
                break;
            case CIRCLE:
                PointF[] circlePoints = shape.getPoints();
                circlePoints[1].x = mEndPointF.x;
                circlePoints[1].y = mEndPointF.y;
                break;
            case RECTANGLE:
                break;
        }
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    private void updateShapeState() {
        switch (mNowAddingWhat) {
            case ARROW:
                // angle of the arrow
                updateAngle();
                updateTrianglePointFs();
                break;
            case CIRCLE:
                updateCirclePointFsAndRadius();
                break;
            case RECTANGLE:
                updateRectanglePointFs();
                break;
        }
    }

    private void updateCirclePointFsAndRadius() {
        Shape shape = shapes.get(shapes.size() - 1);
        PointF[] circlePointFs = shape.getPoints();
        circlePointFs[1].x = mEndPointF.x;
        circlePointFs[1].y = mEndPointF.y;
        float radius = (float) Math.sqrt(mDisX * mDisX + mDisY * mDisY);
        shape.setRadius(radius);
    }

    private void updateRectanglePointFs() {
        Shape shape = shapes.get(shapes.size() - 1);
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
        switch (mNowAddingWhat) {
            case ARROW:
                Shape triangle = new Shape(Shape.ShapeType.ARROW);
                shape = triangle;
                PointF[] arrowPoints = triangle.getPoints();
                arrowPoints[4].x = mStartPointF.x;
                arrowPoints[4].y = mStartPointF.y;
                break;
            case CIRCLE:
                Shape circle = new Shape(Shape.ShapeType.CIRCLE);
                shape = circle;
                PointF[] circlePoints = circle.getPoints();
                circlePoints[0].x = mStartPointF.x;
                circlePoints[0].y = mStartPointF.y;
                break;
            case RECTANGLE:
                Shape rectangle = new Shape(Shape.ShapeType.RECTANGLE);
                shape = rectangle;
                PointF[] rectanglePoints = rectangle.getPoints();
                rectanglePoints[0].x = mStartPointF.x;
                rectanglePoints[0].y = mStartPointF.y;
                break;
        }
        shape.setColor(mPaint.getColor());
        shapes.add(shape);
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
        //draw our lovely shapes
        drawShape(canvas);
    }

    private void drawShape(Canvas canvas) {
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            PointF[] pointFs = shape.getPoints();
            switch (shape.getShapeType()) {
                case ARROW:
                    //draw arrow
                    //draw triangle
                    Path triangle = new Path();
                    triangle.moveTo(pointFs[0].x, pointFs[0].y);
                    triangle.lineTo(pointFs[2].x, pointFs[2].y);
                    triangle.lineTo(pointFs[3].x, pointFs[3].y);
                    triangle.close();
                    canvas.drawPath(triangle, mPaint);

                    canvas.drawLine(pointFs[4].x, pointFs[4].y, pointFs[1].x,
                            pointFs[1].y, mPaint);

                    //just for debug
                    if (mIsNeedDebug) {
                        mPaint.setColor(Color.RED);
                        canvas.drawCircle(pointFs[0].x, pointFs[0].y, 5, mPaint);
                        mPaint.setColor(Color.YELLOW);
                        canvas.drawCircle(pointFs[1].x, pointFs[1].y, 5, mPaint);
                        mPaint.setColor(Color.GREEN);
                        canvas.drawCircle(pointFs[2].x, pointFs[2].y, 5, mPaint);
                        mPaint.setColor(Color.BLUE);
                        canvas.drawCircle(pointFs[3].x, pointFs[3].y, 5, mPaint);
                        mPaint.setColor(Color.BLACK);
                    }
                    //just for debug
                    //draw arrow
                    break;
                case CIRCLE:
                    float radius = shape.getRadius();
                    mPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(pointFs[0].x, pointFs[0].y, radius, mPaint);
                    mPaint.setStyle(Paint.Style.FILL);
                    break;
                case RECTANGLE:
                    mPaint.setStyle(Paint.Style.STROKE);
                    float minLeftTopX = Math.min(pointFs[0].x, pointFs[2].x);
                    float minLeftTopY = Math.min(pointFs[0].y, pointFs[2].y);
                    float maxRightBottomX = Math.max(pointFs[0].x, pointFs[2].x);
                    float maxRightBottomY = Math.max(pointFs[0].y, pointFs[2].y);
                    canvas.drawRoundRect(minLeftTopX, minLeftTopY,
                            maxRightBottomX, maxRightBottomY,
                            radiusCornor, radiusCornor * 2, mPaint);
                    mPaint.setStyle(Paint.Style.FILL);
                    break;
            }
        }
    }

    public void updateTrianglePointFs() {
        Shape shape = shapes.get(shapes.size() - 1);
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

    public void saveImageToFile(ImageView mIv) {
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
        Bitmap bitmap = drawable.getBitmap();


//        Bitmap finalBitmap = bitmap.copy(bitmap.getConfig(), true);
        Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, this.getWidth(), this.getHeight());
        Canvas canvas = new Canvas(finalBitmap);
        drawShape(canvas);

        mIv.setImageBitmap(finalBitmap);

        try {
            saveFile(finalBitmap, "huanghaiqi" + System.currentTimeMillis());
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
     * @param fileName
     * @throws IOException
     */
    private void saveFile(Bitmap bm, String fileName) throws IOException {
        String dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File
                .separator + "huanghaiqi"
                + File.separator;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(dirPath + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }
}
