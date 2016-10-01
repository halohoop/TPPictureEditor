package com.android.systemui.screenshot.editutils.pages;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class EditorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

    }
}
