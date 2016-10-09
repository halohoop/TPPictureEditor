package com.halohoop.openeditordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private String path = "/mnt/sdcard/Pictures/Screenshots/Screenshot_2016-10-08-03-11-38.png";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        Intent intent = new Intent("android.intent.action.TPEIDITOR");
        intent.putExtra("EDITOR_TARGET", path);
        startActivity(intent);
    }
}
