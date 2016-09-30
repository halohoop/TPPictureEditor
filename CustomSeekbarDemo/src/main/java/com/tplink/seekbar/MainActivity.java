package com.tplink.seekbar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tplink.seekbar.widgets.AlphaSeekBar;
import com.tplink.seekbar.widgets.ThicknessSeekBar;

public class MainActivity extends AppCompatActivity {

    private AlphaSeekBar asb;
    private ThicknessSeekBar tsb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tsb = (ThicknessSeekBar) findViewById(R.id.tsb);
        asb = (AlphaSeekBar) findViewById(R.id.asb);
    }

    public void click1(View view) {
        tsb.setProgressColor(Color.GREEN);
    }

    public void click2(View view) {
        asb.setProgressColor(Color.GREEN);
    }
}
