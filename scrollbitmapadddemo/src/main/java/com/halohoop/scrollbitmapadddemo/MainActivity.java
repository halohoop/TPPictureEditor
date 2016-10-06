package com.halohoop.scrollbitmapadddemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity
        implements View.OnLongClickListener,SuperBitmapExpandView.BitmapDataHelper {

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
    private ListView lv;
    private View ll;
    private WindowManager mWindowManager;
    private SuperBitmapExpandView superview;

    public void click(View view) {
    }

    static String[] names;

    static {
        names = new String[500];
        for (int i = 0; i < names.length; i++) {
            names[i] = "huanghaiqi" + i;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        lv = (ListView) findViewById(R.id.lv);
        ll = findViewById(R.id.ll);
        ll.setOnLongClickListener(this);

        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
    }
    @Override
    public boolean onLongClick(View v) {
        if (Settings.canDrawOverlays(this)) {
            showFloatView();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
        return true;
    }

    private void showFloatView() {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);

        View view = View.inflate(this, R.layout.screenshot, null);
        superview = (SuperBitmapExpandView) view.findViewById(R.id.superview);
        superview.setBitmapDataHelper(this);
        superview.addBitmap(Utils.getBitmap(paths[0],this));
        superview.addBitmap(Utils.getBitmap(paths[1],this));

        mWindowManager.addView(view, windowLayoutParams);
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public Bitmap getMore() {
        String path = paths[superview.getCurrentBitmapCount()];
        Bitmap bitmap = Utils.getBitmap(path, MainActivity.this);
        return bitmap;
    }
}
