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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmaps = new ArrayList<>();
        lv = (ListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter();
        lv.setAdapter(myAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Bitmap> objects = new ArrayList<>();
                for (int i = 0; i < paths.length; i++) {
                    String path = paths[i];
                    final Bitmap bitmap = getBitmap(path);
                    objects.add(bitmap);
                }
                bitmaps = objects;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "hahaha" + bitmaps.size(), Toast.LENGTH_SHORT).show();
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private Bitmap getBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        Point screenSize = getScreenSize(this);
        Log.i("getBitmap: ", "getBitmap: "+"size:"+screenSize.x+" "+screenSize.y);
        Log.i("getBitmap: ", "getBitmap1: "+"size:"+options.outWidth+" "+options.outHeight);
        options.inSampleSize = getSampleSize(
                screenSize.x, screenSize.y,
                options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int getSampleSize(int screenWidth, int screenHeight, int outWidth, int outHeight) {
        int widthRatio = Math.round((float) outWidth / (float) screenWidth);
        int heightRatio = Math.round((float) outHeight / (float) screenHeight);
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
