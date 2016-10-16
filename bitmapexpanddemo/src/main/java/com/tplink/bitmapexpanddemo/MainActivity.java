package com.tplink.bitmapexpanddemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BitmapAutoExpandView bae;
    private String path = "/mnt/sdcard/Pictures/Screenshots";
    private String footerPath = "/mnt/sdcard/Pictures/Screenshots/footer.png";
    private File[] files;
    private List<Bitmap> bitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bae = (BitmapAutoExpandView) findViewById(R.id.bae);
        this.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 1);
        File file = new File(path);
        files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                bitmaps.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
            }
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
    }

    private int index = 0;

    public void expand(View view) {
        if (index < bitmaps.size() - 1) {
            bae.expandBitmap(bitmaps.get(index++));
        } else {
            Toast.makeText(MainActivity.this, "end", Toast.LENGTH_SHORT).show();
        }
    }
    public void addfooter(View view) {
        Bitmap bitmap = BitmapFactory.decodeFile(footerPath);
        bae.setFooterBitmap(bitmap);
    }
}
