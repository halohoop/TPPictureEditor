package com.tplink.forscrollshotdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void listview(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    public void gridview(View view) {
        Intent intent = new Intent(this, GridActivity.class);
        startActivity(intent);
    }

    public void scrollview(View view) {
        Intent intent = new Intent(this, ScrollActivity.class);
        startActivity(intent);
    }

    public void listviewWithHeader(View view) {
        Intent intent = new Intent(this, ListWithHeaderActivity.class);
        startActivity(intent);
    }

    public void gridviewWithHeader(View view) {
        Intent intent = new Intent(this, GridWithHeaderActivity.class);
        startActivity(intent);
    }

    public void scrollviewWithHeader(View view) {
        Intent intent = new Intent(this, ScrollWithHeaderActivity.class);
        startActivity(intent);
    }
}
