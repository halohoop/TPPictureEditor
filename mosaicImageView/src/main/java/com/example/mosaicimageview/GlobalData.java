package com.example.mosaicimageview;

import java.io.File;

import android.os.Environment;


/**
 * ȫֻ
 * 
 */
public class GlobalData {
	/** SD· **/
	public static String SDcardPaht = getSDcardPath();

	public static final String CameraFile = SDcardPaht + "/MosaicImageView";
	public static final String CameraPhoto = SDcardPaht + "/MosaicImageView/temp.jpg";
	public static final String tempImagePaht = SDcardPaht + "/MosaicImageView/Temp";

	/** ȡSD· **/
	public static String getSDcardPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // жsdǷ
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// ȡĿ¼
		}
		if(sdDir ==null){
			return "/mnt/sdcard";
		}
		return sdDir.toString();
	}
	
	
}