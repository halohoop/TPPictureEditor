package com.tplink.sumuptheverticalscrollablewidgets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View v) {
        View scrollableView = findScrollableView((ViewGroup) getWindow().getDecorView());
        Log.i("asd", "can scroll vertically drag up: "+ scrollableView);
        Log.i("asd", "can scroll vertically drag up: "+ scrollableView
                .canScrollVertically(1));
    }

    private View findScrollableView(ViewGroup viewGroup) {
        View scrollableView = null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.canScrollVertically(1)) {
                scrollableView = childAt;
                break;
            }
            if (childAt instanceof ViewGroup) {
                scrollableView = findScrollableView(((ViewGroup) childAt));
                if (scrollableView != null) {
                    break;
                }
            }
        }
        return scrollableView;
    }
}
