package com.halohoop.scrollableviewscrolltestdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "huanghaiqi";
    private ListView lv;
    private ScrollView sv;
    private RecyclerView rv;
    private static String[] names;

    static {
        names = new String[400];
        for (int i = 0; i < 400; i++) {
            names[i] = "halo" + i;
        }
    }

    private View childAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        sv = (ScrollView) findViewById(R.id.sv);
        rv = (RecyclerView) findViewById(R.id.rv);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MyAdapater());
    }

    class MyAdapater extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(MainActivity.this, android.R.layout.simple_list_item_1, null);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(names[position]);
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    public void click(View view) {
        if (childAt == null) {
            childAt = lv.getChildAt(lv.getChildCount()-1);
        }
        //TODO 如何判断最后
        Log.i(TAG, "click: "+" "+childAt.getBottom());
    }

    public void click1(View view) {
        Log.i(TAG, "click hh1: " + lv.canScrollVertically(1));
        //以下两者不一样，scrollBy直接移动的是绘制的区域，并不是滑动列表
        lv.smoothScrollBy(100, 0);
//        lv.scrollBy(0, 100);
        View childAt = lv.getChildAt(lv.getChildCount() - 1);
        Log.i(TAG, "getBottom: " + childAt.getBottom());
    }

    public void click2(View view) {
        Log.i(TAG, "click hh2: " + sv.canScrollVertically(1));
        //以下两者一样,都是滑动列表，区别在于smooth有过度效果，后者没有
//        sv.smoothScrollBy(0, 100);
        sv.scrollBy(0, 100);
    }

    public void click3(View view) {
        Log.i(TAG, "click hh3: " + rv.canScrollVertically(1));
        //以下两者一样,都是滑动列表，区别在于smooth有过度效果，后者没有
//        rv.smoothScrollBy(0, 100);
        rv.scrollBy(0, 100);
    }
}
