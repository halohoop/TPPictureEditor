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

package com.halohoop.scrollbitmapadddemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
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

    public static float getScaleRatio(int originBitmapWidth, int targetWidth) {
        float ratio = ((float) targetWidth) / ((float) originBitmapWidth);
        return ratio;
    }
}
