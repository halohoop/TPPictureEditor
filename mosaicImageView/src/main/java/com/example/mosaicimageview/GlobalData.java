package com.example.mosaicimageview;

import java.io.File;

import android.os.Environment;


/**
 * ȫֻ
 * 
 */
public class GlobalData {
	public static String SDcardPaht = getSDcardPath();

	public static final String CameraFile = SDcardPaht + "/MosaicImageView";
	public static final String CameraPhoto = SDcardPaht + "/MosaicImageView/temp.jpg";
	public static final String tempImagePaht = SDcardPaht + "/MosaicImageView/Temp";

	public static String getSDcardPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		if(sdDir ==null){
			return "/mnt/sdcard";
		}
		return sdDir.toString();
	}
	
	
}