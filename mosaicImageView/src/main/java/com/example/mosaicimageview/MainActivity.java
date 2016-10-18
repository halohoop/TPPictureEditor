package com.example.mosaicimageview;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	protected String srcPath = "src.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageUtil.write(this, "aaa.jpg", srcPath);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				running();
			}
		});
	}

	private void running() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), DrawPhotoActivity.class);
		String path = GlobalData.CameraFile + "/" + srcPath;
		intent.putExtra(DrawPhotoActivity.FILEPATH, path);
		startActivity(intent);
	}

}
