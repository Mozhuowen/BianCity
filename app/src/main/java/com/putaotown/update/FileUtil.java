package com.putaotown.update;

import android.content.Context;

import java.io.File;
import java.io.IOException;


public class FileUtil {
	
	public static File updateDir = null;
	public static File updateFile = null;
	public static final String KonkaApplication = "biancityUpdateApplication";
	
	public static boolean isCreateFileSucess;

	public static void createFile(Context context,String app_name) {
		
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			isCreateFileSucess = true;
			
			updateDir = new File(context.getExternalCacheDir()+ "/");
			updateFile = new File(updateDir + "/" + app_name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					isCreateFileSucess = false;
					e.printStackTrace();
				}
			}

		}else{
			isCreateFileSucess = false;
		}
	}
}