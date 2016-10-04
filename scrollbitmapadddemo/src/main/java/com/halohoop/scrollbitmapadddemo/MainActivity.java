package com.halohoop.scrollbitmapadddemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
        implements AbsListView.OnScrollListener {

    private ListView lv;
    private List<Bitmap> bitmaps;
    private String[] paths = {
            "/mnt/sdcard/Download/pic1.png",
            "/mnt/sdcard/Download/pic2.png",
            "/mnt/sdcard/Download/pic3.png",
            "/mnt/sdcard/Download/pic4.png",
            "/mnt/sdcard/Download/pic5.png",
            "/mnt/sdcard/Download/pic6.png",
            "/mnt/sdcard/Download/pic7.png",
            "/mnt/sdcard/Download/pic8.png",
            "/mnt/sdcard/Download/pic9.png",
            "/mnt/sdcard/Download/pic10.png",
            "/mnt/sdcard/Download/pic11.png",
            "/mnt/sdcard/Download/pic12.png",
            "/mnt/sdcard/Download/pic13.png"
    };
    //    private String[] paths = {
//            "/mnt/sdcard/Pictures/Screenshots/Screenshot_2016-10-04-11-30-08.png",
//            "/mnt/sdcard/Pictures/Screenshots/Screenshot_2016-10-04-11-30-17.png",
//            "/mnt/sdcard/Pictures/Screenshots/Screenshot_2016-10-04-11-30-24.png",
//            "/mnt/sdcard/Pictures/Screenshots/Screenshot_2016-10-04-11-30-28.png"
//    };
    private MyAdapter myAdapter;
    private ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmaps = new ArrayList<>();
        lv = (ListView) findViewById(R.id.lv);
        mExecutorService = Executors.newFixedThreadPool(1);
        myAdapter = new MyAdapter();
        lv.setAdapter(myAdapter);
        lv.setOnScrollListener(this);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<Bitmap> objects = new ArrayList<>();
//                for (int i = 0; i < paths.length; i++) {
//                    String path = paths[i];
//                    final Bitmap bitmap = getBitmap(path);
//                    objects.add(bitmap);
//                }
//                bitmaps = objects;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "hahaha" + bitmaps.size(), Toast
//                                .LENGTH_SHORT).show();
//                        myAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }).start();
    }


    class MyRunnable implements Runnable {

        @Override
        public void run() {
            indexToLoad++;
            if (indexToLoad >= paths.length) {
                return;
            }
            Log.i("run: ", "run: " + "now loading:" + indexToLoad);
            String path = paths[indexToLoad];
            final Bitmap bitmap = getBitmap(path);
            bitmaps.add(bitmap);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private Bitmap getBitmap(String path) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lv
                .getLayoutParams();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        Point screenSize = getScreenSize(this);
        Log.i("getBitmap: ", "getBitmap: " + "size:" + screenSize.x + " " + screenSize.y);
        Log.i("getBitmap: ", "getBitmap1: " + "size:" + options.outWidth + " " + options.outHeight);
        options.inSampleSize = getSampleSize(
                screenSize.x - layoutParams.leftMargin - layoutParams.rightMargin, screenSize.y,
                options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int getSampleSize(int screenWidth, int screenHeight, int outWidth, int outHeight) {
        int widthRatio = (int) Math.ceil((float) outWidth / (float) screenWidth);
        int heightRatio = (int) Math.ceil((float) outHeight / (float) screenHeight);
        return Math.max(widthRatio, heightRatio);
    }

    @SuppressLint("NewApi")//getSize(方法)需要 api13才能使用
/**
 * 通过返回值point拿宽高
 * point.x 屏幕的宽
 * point.y 屏幕的高
 */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        //Build.VERSION_CODES.HONEYCOMB_MR2 → 13
        if (Build.VERSION.SDK_INT >= 13) {
            display.getSize(out);
        } else {
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }
        return out;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        int lastVisiableItem = firstVisibleItem + visibleItemCount - 1;
        if (lastVisiableItem == bitmaps.size() - 1) {
            mExecutorService.execute(new MyRunnable());
        }
    }

    private int indexToLoad = -1;


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bitmaps.size();
        }

        @Override
        public Object getItem(int position) {
            return bitmaps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView != null) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                View view = View.inflate(MainActivity.this, R.layout.item, null);
                ImageView iv = (ImageView) view.findViewById(R.id.iv);
                viewHolder = new ViewHolder();
                viewHolder.iv = iv;
                view.setTag(viewHolder);
                convertView = view;
            }
            viewHolder.iv.setImageBitmap(bitmaps.get(position));
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView iv;
    }
}
