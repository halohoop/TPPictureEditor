package com.halohoop.openeditordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "halohoop";
    private String path = "/mnt/sdcard/Pictures/Screenshots/Screenshot_2016-01-01-20-31-49.png";
    private String dirPath = "/mnt/sdcard/Pictures/Screenshots";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        String path = files[0].getAbsolutePath();
        Log.i(TAG, "click: "+path);
        Intent intent = new Intent("android.intent.action.TPEIDITOR");
        intent.putExtra("EDITOR_TARGET", path);
        startActivity(intent);
    }
}
