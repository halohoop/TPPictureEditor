/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ListActivity.java
 *
 * asd
 *
 * Author huanghaiqi, Created at 2016-10-09
 *
 * Ver 1.0, 2016-10-09, huanghaiqi, Create file.
 */

package com.tplink.forscrollshotdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class GridWithHeaderActivity extends AppCompatActivity {

    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_with_header_and_footer);
        gv = (GridView) findViewById(R.id.gv);
        gv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Cheeses
                .CHEESES));
    }
}
