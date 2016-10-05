package com.halohoop.scrollbitmapadddemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.halohoop.scrollbitmapadddemo.SuperBitmapExpandView.BitmapDataHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity3 extends AppCompatActivity
        implements BitmapDataHelper {

    private String[] paths = {
            "/mnt/sdcard/bluetooth/pic1.png",
            "/mnt/sdcard/bluetooth/pic2.png",
            "/mnt/sdcard/bluetooth/pic3.png",
            "/mnt/sdcard/bluetooth/pic4.png",
            "/mnt/sdcard/bluetooth/pic5.png",
            "/mnt/sdcard/bluetooth/pic6.png",
            "/mnt/sdcard/bluetooth/pic7.png",
            "/mnt/sdcard/bluetooth/pic8.png",
            "/mnt/sdcard/bluetooth/pic9.png",
            "/mnt/sdcard/bluetooth/pic10.png",
            "/mnt/sdcard/bluetooth/pic11.png",
            "/mnt/sdcard/bluetooth/pic12.png",
            "/mnt/sdcard/bluetooth/pic13.png"
    };

    private SuperBitmapExpandView mSuperBitmapExpandView;
    private ExecutorService mExecutorService;

    public void click(View view) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                String path2 = paths[2];
                String path3 = paths[3];
                String path4 = paths[4];
                Bitmap bitmap2 = Utils.getBitmap(path2, MainActivity3.this);
                Bitmap bitmap3 = Utils.getBitmap(path3, MainActivity3.this);
                Bitmap bitmap4 = Utils.getBitmap(path4, MainActivity3.this);
                mSuperBitmapExpandView.addBitmap(bitmap2);
                mSuperBitmapExpandView.addBitmap(bitmap3);
                mSuperBitmapExpandView.addBitmap(bitmap4);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mSuperBitmapExpandView = (SuperBitmapExpandView) findViewById(R.id
                .super_bitmap_expand_view);
        mSuperBitmapExpandView.setBitmapDataHelper(this);
        mExecutorService = Executors.newFixedThreadPool(1);
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                String path0 = paths[0];
                String path1 = paths[1];
                Bitmap bitmap0 = Utils.getBitmap(path0, MainActivity3.this);
                Bitmap bitmap1 = Utils.getBitmap(path1, MainActivity3.this);
                mSuperBitmapExpandView.addBitmap(bitmap0);
                mSuperBitmapExpandView.addBitmap(bitmap1);
            }
        });
    }

    @Override
    public boolean hasMore() {
        if (mSuperBitmapExpandView.getCurrentBitmapCount() < paths.length) {
            return true;
        }
        return false;
    }

    @Override
    public Bitmap getMore() {
        String path = paths[mSuperBitmapExpandView.getCurrentBitmapCount()];
        Bitmap bitmap = Utils.getBitmap(path, MainActivity3.this);
        return bitmap;
    }
}
