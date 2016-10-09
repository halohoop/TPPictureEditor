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
import android.widget.ListView;

public class ListWithHeaderActivity extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_with_header_and_footer);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Cheeses
                .CHEESES));
    }
}
