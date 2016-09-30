package com.example.mosaicimageview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * 
 * @author Administrator
 * 
 */
public class ImageUtil {

	/**
	 * ʹʾ
	 * 
	 * @param bitmap
	 *            ԭʼ
	 * @param orientation
	 *            ת
	 * @return
	 */
	public static Bitmap getTotateBitmap(Bitmap bitmap, int orientation) {
		// ת
		Matrix matrix = new Matrix();

		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90: {// ת90
			matrix.setRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
			break;
		case ExifInterface.ORIENTATION_ROTATE_180: {// ת180
			matrix.setRotate(180);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
			break;
		case ExifInterface.ORIENTATION_ROTATE_270: {// ת270
			matrix.setRotate(270);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
			break;
		default:
			break;
		}
		return bitmap;
	}

	/**  **/
	public static boolean saveMyBitmap(File f, Bitmap mBitmap)
			throws IOException {
		boolean saveComplete = true;
		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();
			// ŵı
			int finalWidth = 800;
			int finalHeight = (int) (finalWidth * 1.0 * (height * 1.0 / width * 1.0));
			double x = width * finalHeight;
			double y = height * finalWidth;

			if (x > y) {
				finalHeight = (int) (y / (double) width);
			} else if (x < y) {
				finalWidth = (int) (x / (double) height);
			}

			if (finalWidth > width && finalHeight > height) {
				finalWidth = width;
				finalHeight = height;
			}
			Matrix matrix = new Matrix();
			matrix.reset();
			//
			float scaleWidth = ((float) finalWidth) / (float) width;
			float scaleHeight = ((float) finalHeight) / (float) height;
			//
			matrix.postScale(scaleWidth, scaleHeight);
			mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, (int) width,
					(int) height, matrix, true);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
			fOut.flush();
			fOut.close();
			// ڴռ
			mBitmap.recycle();
			System.gc();
		} catch (FileNotFoundException e) {
			saveComplete = false;
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			saveComplete = false;
		}
		return saveComplete;
	}

	/**
	 * ر
	 * 
	 * @param path
	 *            ·
	 * @return Bitmap ʱnull
	 */
	public static Bitmap getLoacalBitmap(Context context, String file) {
		String buff = file.replace("file://", "");
		// һжļǷ
		File check = new File(buff);
		// ·ڣnull
		if (!check.exists()) {
			return null;
		}
		// ȡ
		try {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// ʼʱoptions.inJustDecodeBounds true
			newOpts.inJustDecodeBounds = false;
			// ʾ16λλ,565Ӧԭɫռλ
			newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
			newOpts.inInputShareable = true;
			newOpts.inPurgeable = true;// Ա
			return BitmapFactory.decodeFile(buff, newOpts);
		} catch (Exception e) {
			e.printStackTrace();
			// ȡʱnull
			return null;
		}
	}

	public static void write(Context context, String srcPathName, String  newPathName) {
		InputStream inputStream;
		try {
			inputStream = context.getResources().getAssets().open(srcPathName);
			File file = null;
			file = new File(GlobalData.CameraFile);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(GlobalData.CameraFile + "/" + newPathName);
			if (file.exists()) {
				file.delete();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(GlobalData.CameraFile + "/" + newPathName);
			byte[] buffer = new byte[512];
			int count = 0;
			while ((count = inputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, count);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
