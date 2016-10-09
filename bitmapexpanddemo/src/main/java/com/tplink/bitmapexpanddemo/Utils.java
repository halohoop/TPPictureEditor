/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Utils.java
 *
 * util to shrink the bitmap
 *
 * Author huanghaiqi, Created at 2016-10-05
 *
 * Ver 1.0, 2016-10-05, huanghaiqi, Create file.
 */

package com.tplink.bitmapexpanddemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class Utils {
    public static Bitmap getBitmap(String path, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        Point screenSize = getScreenSize(context);
        options.inSampleSize = getSampleSize(
                screenSize.x - 160, screenSize.y,
                options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int getSampleSize(int screenWidth, int screenHeight, int outWidth, int
            outHeight) {
        int widthRatio = (int) Math.ceil((float) outWidth / (float) screenWidth);
        int heightRatio = (int) Math.ceil((float) outHeight / (float) screenHeight);
        return Math.max(widthRatio, heightRatio);
    }

    public static int screenSizeWidth = -1;
    public static int screenSizeHeight = -1;

    public static Point getScreenSize(Context context) {
        if (screenSizeWidth != -1 && screenSizeHeight != -1) {
            Point out = new Point(screenSizeWidth, screenSizeHeight);
            return out;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        //Build.VERSION_CODES.HONEYCOMB_MR2 → 13
        if (Build.VERSION.SDK_INT >= 13) {
            display.getSize(out);
        } else {
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }
        screenSizeWidth = out.x;
        screenSizeHeight = out.y;
        return out;
    }

    public static float getExpandScaleRatio(int originBitmapWidth, int targetWidth) {
        return getScaleRatio(originBitmapWidth, targetWidth);
    }

    public static float getDoneRatio(int originBitmapHeight, int targetHeight) {
        return getScaleRatio(originBitmapHeight, targetHeight);
    }

    private static float getScaleRatio(int originBitmapWidth, int targetWidth) {
        float ratio = ((float) targetWidth) / ((float) originBitmapWidth);
        return ratio;
    }

    public static Rect getViewLocationOnScreen(View scrollableView) {
        int[] locations = new int[2];
        if (scrollableView == null || scrollableView.getVisibility() != View.VISIBLE) {
            return null;
        }
        Rect rect = new Rect();
        scrollableView.getLocationOnScreen(locations);
        rect.left = locations[0];
        rect.top = locations[1];
        rect.right = locations[0] + scrollableView.getMeasuredWidth();
        rect.bottom = locations[1] + scrollableView.getMeasuredHeight();
        return rect;
    }
}
