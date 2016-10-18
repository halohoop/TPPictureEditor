package com.tplink.mymosaicdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);

    }

    public void click(View view) {
        String dirPath = "/mnt/sdcard/Pictures/Screenshots";
        File file = new File(dirPath);
        File[] files = file.listFiles();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        Bitmap bitmap = BitmapFactory.decodeFile(files[0].getAbsolutePath());
        Bitmap bitmap1 = BitmapUtils.mosaicIt(bitmap, 10);
        iv.setImageBitmap(bitmap1);
    }
}
