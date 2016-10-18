package com.example.mosaicimageview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 **/

public class DrawPhotoActivity extends Activity {
	public static final String FILEPATH = "filepath";
	public static final String ACTION = "action";
	public static final String ACTION_INIT = "action_init";


	public static final String FROM = "from";

	public static final String TAKEPHOTO = "takephoto";

	public static final String REDRAW = "ReDraw";

	public LinearLayout imageContent;
	private String filePath = "";
	private MosaicImageView touchView;
	public TextView overBt;
	public ImageButton backIB = null;
	public Button finishBtn = null;
	public TextView cancelText;
	private GetImage handler;
	private ProgressDialog progressDialog = null;
	public boolean isReDraw = false;
	Intent intent = null;
	public Context context;
	public BroadcastReceiver broadcastReceiver = null;

	private SeekBar seekBar;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw_photo);
		initView();
		context = this;
		intent = getIntent();
		broadcastReceiver = new BroadcastReceiver(){
			public void onReceive(Context context, Intent intent) {
				if(progressDialog != null && progressDialog.isShowing()){
					progressDialog.dismiss();
				}
			};
		};
		
		registerReceiver(broadcastReceiver, new IntentFilter(ACTION_INIT));
		String action = intent.getExtras().getString(ACTION,"");
		if(!TextUtils.isEmpty(action)&&action.equals(TAKEPHOTO)){
			Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File f = new File(GlobalData.CameraFile);
			if (!f.exists())
				f.mkdir();
			takephoto.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(GlobalData.CameraPhoto)));//
			startActivityForResult(takephoto, 1);
		}else {
			System.out.println("645646564456456546");
			filePath = intent.getExtras().getString(FILEPATH);
			if (!TextUtils.isEmpty(filePath)) {
				ImageThread thread = new ImageThread();
				thread.start();
			}
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0)
			finish();
		if (requestCode != 1)
			finish();
		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			// Bitmap bitmap = setImage(uri);
			try {
				BitmapFactory.Options newOpts = new BitmapFactory.Options();
				// ʼʱoptions.inJustDecodeBounds true
				newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
				newOpts.inInputShareable = true;
				newOpts.inPurgeable = true;
				newOpts.inJustDecodeBounds = false;
				newOpts.inSampleSize = 2;
				Bitmap bitmap = BitmapFactory.decodeFile(
						GlobalData.CameraPhoto, newOpts);
				String ddr = GlobalData.tempImagePaht;
				File ddrfile = new File(ddr);
				if (!ddrfile.exists()) {
					ddrfile.mkdirs();
				}
				String fileName = GlobalData.tempImagePaht + "/"
						+ System.currentTimeMillis() + ".jpg";
				File file = new File(fileName);
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				ExifInterface exif = new ExifInterface(GlobalData.CameraPhoto);
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, 0);
				// ʹʾ
				bitmap = ImageUtil.getTotateBitmap(bitmap, orientation);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
				bos.flush();
				bos.close();
				bitmap.recycle();
				bitmap = null;
				System.gc();
				filePath = fileName;
				intent.putExtra("action", filePath);
				if (filePath != null && !filePath.equals("")) {
					ImageThread thread = new ImageThread();
					thread.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initView() {
		imageContent = (LinearLayout) findViewById(R.id.draw_photo_view);
		handler = new GetImage();
		backIB = (ImageButton) findViewById(R.id.title_bar_left_btn);
		backIB.setVisibility(View.VISIBLE);
		finishBtn = (Button) findViewById(R.id.title_bar_right_btn);
		finishBtn.setBackgroundResource(R.drawable.selector_round_green_btn);
		finishBtn.setVisibility(View.GONE);
		overBt = (TextView) findViewById(R.id.draw_ok_text);
		cancelText = (TextView) findViewById(R.id.draw_photo_cancel);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progress = 0;			
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if(progress > 0 && progress < 12.5){
					seekBar.setProgress(0);
				}else if(progress > 12.5 && progress < 25){
					seekBar.setProgress(25);
				}else if(progress > 25 && progress < 37.5){
					seekBar.setProgress(25);
				}else if(progress > 37.5 && progress < 50){
					seekBar.setProgress(50);
				}else if(progress > 50 && progress < 62.5){
					seekBar.setProgress(50);
				}else if(progress > 62.5 && progress < 75){
					seekBar.setProgress(75);
				}else if(progress > 75 && progress < 87.5){
					seekBar.setProgress(75);
				}else if(progress > 87.5 && progress < 100){
					seekBar.setProgress(100);
				}
				touchView.setStrokeMultiples(1 + (float)(progress / 100.0));
				touchView.removeStrokeView();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				this.progress = progress;
//				System.out.println(progress);
				touchView.setStrokeMultiples(1 + (float)(progress / 100.0));
			}
		});
		backIB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                try {
                    touchView.sourceBitmap.recycle();
                    touchView.sourceBitmapCopy.recycle();
                    touchView.destroyDrawingCache();
                } catch (Exception e) {
                    e.printStackTrace();
                }
				finish();
			}
		});
		finishBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				touchView.sourceBitmap.recycle();
				touchView.sourceBitmapCopy.recycle();
				touchView.destroyDrawingCache();
				finish();
			}
		});
		overBt.setOnClickListener(new View.OnClickListener() {// ɱ༭ť
			@Override
			public void onClick(View v) {
				overBt.setEnabled(false);
				File f = new File(GlobalData.tempImagePaht + "/"
						+ System.currentTimeMillis() + ".jpg");
				if (!f.exists()) {
					try {
						if (!new File(GlobalData.tempImagePaht).exists()) {
							new File(GlobalData.tempImagePaht).mkdirs();
						}
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					Bitmap saveBitmap = touchView.combineBitmap(touchView.sourceBitmapCopy, touchView.sourceBitmap);
					ImageUtil.saveMyBitmap(f, saveBitmap);
					if (touchView.sourceBitmapCopy != null) {
						touchView.sourceBitmapCopy.recycle();
					}
					touchView.sourceBitmap.recycle();
					saveBitmap.recycle();
					touchView.destroyDrawingCache();
					if (!TextUtils.isEmpty(filePath)
							&& filePath.contains(GlobalData.tempImagePaht)) {
						new File(filePath).delete();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				Toast.makeText(DrawPhotoActivity.this, "SDMosaicImageView/Temp", Toast.LENGTH_LONG).show();
				finish();
			}
		});

		cancelText.setOnClickListener(new View.OnClickListener() {// ť
					@Override
					public void onClick(View v) {
						cancelDrawImage();
					}
				});
	}

	/**  **/
	@SuppressWarnings("deprecation")
	@SuppressLint("HandlerLeak")
	public void cancelDrawImage() {
			touchView.destroyDrawingCache();
			WindowManager manager = DrawPhotoActivity.this.getWindowManager();
			int ww = manager.getDefaultDisplay().getWidth();
			int hh = manager.getDefaultDisplay().getHeight();
			touchView.revocation(filePath, ww, hh);
			if(imageContent.getChildCount() == 0){
				imageContent.addView(touchView);
			}
	}

	@SuppressLint("HandlerLeak")
	private class GetImage extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0: {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(DrawPhotoActivity.this,
						context.getString(R.string.drawPhoto_actionName),
						context.getString(R.string.drawPhoto_actioning));
			}
				break;
			case 1: {
				if (touchView != null) {
					imageContent.removeView(touchView);
				}
				touchView = (MosaicImageView) msg.obj;
				touchView.destroyDrawingCache();
				imageContent.addView(touchView);
			}
				break;
			case 2: {
				filePath = (String) msg.obj;
				ImageThread thread = new ImageThread();
				thread.start();
			}
				break;
			case 3: {
				if (progressDialog != null)
					progressDialog.dismiss();
			}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	private class ImageThread extends Thread {
		@SuppressWarnings("deprecation")
		public void run() {
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);
			WindowManager manager = DrawPhotoActivity.this.getWindowManager();
			int ww = manager.getDefaultDisplay().getWidth();
			int hh = manager.getDefaultDisplay().getHeight();
			touchView = new MosaicImageView(DrawPhotoActivity.this, null, filePath, ww, hh);
			Message msg1 = new Message();
			msg1.what = 1;
			msg1.obj = touchView;
			handler.sendMessage(msg1);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(broadcastReceiver != null){
			unregisterReceiver(broadcastReceiver);
		}
	}
}

