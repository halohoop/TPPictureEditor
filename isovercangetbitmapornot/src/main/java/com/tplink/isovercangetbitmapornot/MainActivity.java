package com.tplink.isovercangetbitmapornot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private LinearLayout ll;
    private Bitmap mDrawingCacheBitmap;
    private ImageView iv1;
    private ImageView iv2;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rl = (RelativeLayout) findViewById(R.id.rl);
        ll = (LinearLayout) findViewById(R.id.ll);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
    }

    public void click(View view) {
        mDrawingCacheBitmap = convertViewToBitmap(ll);
        iv1.setImageBitmap(mDrawingCacheBitmap);
        Log.i("hahahahha", "click: " + ll.getWidth() + " " + ll.getHeight());
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Log.i("hahahahha", "click: bitmap:" + bitmap.getWidth() + " " + bitmap.getHeight());
        return bitmap;
    }
}
