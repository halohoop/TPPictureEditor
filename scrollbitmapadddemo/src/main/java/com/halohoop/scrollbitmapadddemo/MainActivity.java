package com.halohoop.scrollbitmapadddemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity
        implements View.OnLongClickListener, SuperBitmapExpandView.BitmapDataHelper
        , View.OnClickListener,View.OnKeyListener {

    private static final String TAG = "huanghaiqi";
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
    private ImageView ivSave;
    private ImageView ivCancel;
    private View view;
    private int CAN_DRAG_UP = 1;
    private int CAN_DRAG_DOWN = -1;
    private View scrollableView;

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
            analyseTheViewTreeStructure();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
        return true;
    }

    private void analyseTheViewTreeStructure() {
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
//        scrollableView = findScrollableView2(decorView);
        scrollableView = decorView;
        Log.i(TAG, "analyseTheViewTreeStructure: "+ scrollableView);
        scrollableView.setDrawingCacheEnabled(true);
        scrollableView.buildDrawingCache();
        final Bitmap drawingCache = scrollableView.getDrawingCache();
        superview.postDelayed(new Runnable() {
            @Override
            public void run() {
                superview.addBitmap(drawingCache);
            }
        },500);
    }

    private View findScrollableView(ViewGroup viewGroup) {
        View scrollableView = null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.canScrollVertically(CAN_DRAG_UP)) {
                scrollableView = childAt;
                break;
            }
            if (childAt instanceof ViewGroup) {
                scrollableView = findScrollableView(((ViewGroup) childAt));
                if (scrollableView != null) {
                    if (scrollableView.canScrollVertically(CAN_DRAG_UP)) {
                        break;
                    }
                }
            }
        }
        return scrollableView;
    }

    private View findScrollableView2(ViewGroup viewGroup){
        View scrollableView = findScrollableView(viewGroup);
        if (scrollableView instanceof ScrollView) {
            scrollableView = ((ScrollView) scrollableView).getChildAt(0);
        }
        return scrollableView;
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

        view = View.inflate(this, R.layout.screenshot, null);
        view.setOnKeyListener(this);
        superview = (SuperBitmapExpandView) view.findViewById(R.id.superview);
        ivSave = (ImageView) view.findViewById(R.id.iv_save);
        ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);
        ivSave.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        superview.setBitmapDataHelper(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                mWindowManager.removeView(view);
                scrollableView.destroyDrawingCache();
                break;
            case R.id.iv_save:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mWindowManager.removeView(view);
        }
        return true;
    }
}
